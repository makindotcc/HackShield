/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.dataformat.yaml;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.dataformat.yaml.JacksonYAMLParseException;
import com.fasterxml.jackson.dataformat.yaml.PackageVersion;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.MarkedYAMLException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;
import pl.hackshield.shaded.org.yaml.snakeyaml.error.Mark;
import pl.hackshield.shaded.org.yaml.snakeyaml.error.YAMLException;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.AliasEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.CollectionStartEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.Event;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.MappingStartEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.NodeEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.ScalarEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.NodeId;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.Tag;
import pl.hackshield.shaded.org.yaml.snakeyaml.parser.ParserImpl;
import pl.hackshield.shaded.org.yaml.snakeyaml.reader.StreamReader;
import pl.hackshield.shaded.org.yaml.snakeyaml.resolver.Resolver;

public class YAMLParser
extends ParserBase {
    private static final Pattern PATTERN_FLOAT = Pattern.compile("[-+]?([0-9][0-9_]*)?\\.[0-9]*([eE][-+][0-9]+)?");
    protected ObjectCodec _objectCodec;
    protected int _formatFeatures;
    protected final Reader _reader;
    protected final ParserImpl _yamlParser;
    protected final Resolver _yamlResolver = new Resolver();
    protected Event _lastEvent;
    protected String _textValue;
    protected String _cleanedTextValue;
    protected String _currentFieldName;
    protected boolean _currentIsAlias;
    protected String _currentAnchor;

    public YAMLParser(IOContext ctxt, BufferRecycler br, int parserFeatures, int formatFeatures, ObjectCodec codec, Reader reader) {
        super(ctxt, parserFeatures);
        this._objectCodec = codec;
        this._formatFeatures = formatFeatures;
        this._reader = reader;
        this._yamlParser = new ParserImpl(new StreamReader(reader));
    }

    @Override
    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    @Override
    public void setCodec(ObjectCodec c) {
        this._objectCodec = c;
    }

    public boolean isCurrentAlias() {
        return this._currentIsAlias;
    }

    @Deprecated
    public String getCurrentAnchor() {
        return this._currentAnchor;
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    protected void _closeInput() throws IOException {
        if (this._ioContext.isResourceManaged() || this.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
            this._reader.close();
        }
    }

    @Override
    public int getFormatFeatures() {
        return this._formatFeatures;
    }

    @Override
    public JsonParser overrideFormatFeatures(int values, int mask) {
        this._formatFeatures = this._formatFeatures & ~mask | values & mask;
        return this;
    }

    public JsonParser enable(Feature f) {
        this._formatFeatures |= f.getMask();
        return this;
    }

    public JsonParser disable(Feature f) {
        this._formatFeatures &= ~f.getMask();
        return this;
    }

    public JsonParser configure(Feature f, boolean state) {
        if (state) {
            this.enable(f);
        } else {
            this.disable(f);
        }
        return this;
    }

    public boolean isEnabled(Feature f) {
        return (this._formatFeatures & f.getMask()) != 0;
    }

    @Override
    public JsonLocation getTokenLocation() {
        if (this._lastEvent == null) {
            return JsonLocation.NA;
        }
        return this._locationFor(this._lastEvent.getStartMark());
    }

    @Override
    public JsonLocation getCurrentLocation() {
        if (this._lastEvent == null) {
            return JsonLocation.NA;
        }
        return this._locationFor(this._lastEvent.getEndMark());
    }

    protected JsonLocation _locationFor(Mark m) {
        if (m == null) {
            return new JsonLocation(this._ioContext.getSourceReference(), -1L, -1, -1);
        }
        return new JsonLocation(this._ioContext.getSourceReference(), m.getIndex(), m.getLine() + 1, m.getColumn() + 1);
    }

    @Override
    public JsonToken nextToken() throws IOException {
        this._currentIsAlias = false;
        this._binaryValue = null;
        if (this._closed) {
            return null;
        }
        while (true) {
            Event evt;
            try {
                evt = this._yamlParser.getEvent();
            }
            catch (YAMLException e) {
                if (e instanceof pl.hackshield.shaded.org.yaml.snakeyaml.error.MarkedYAMLException) {
                    throw MarkedYAMLException.from((JsonParser)this, (pl.hackshield.shaded.org.yaml.snakeyaml.error.MarkedYAMLException)e);
                }
                throw new JacksonYAMLParseException((JsonParser)this, e.getMessage(), e);
            }
            if (evt == null) {
                this._currentAnchor = null;
                this._currToken = null;
                return null;
            }
            this._lastEvent = evt;
            if (this._parsingContext.inObject()) {
                if (this._currToken != JsonToken.FIELD_NAME) {
                    String name;
                    ScalarEvent scalar;
                    String newAnchor;
                    if (!evt.is(Event.ID.Scalar)) {
                        this._currentAnchor = null;
                        if (evt.is(Event.ID.MappingEnd)) {
                            if (!this._parsingContext.inObject()) {
                                this._reportMismatchedEndMarker(125, ']');
                            }
                            this._parsingContext = this._parsingContext.getParent();
                            this._currToken = JsonToken.END_OBJECT;
                            return this._currToken;
                        }
                        this._reportError("Expected a field name (Scalar value in YAML), got this instead: " + evt);
                    }
                    if ((newAnchor = (scalar = (ScalarEvent)evt).getAnchor()) != null || this._currToken != JsonToken.START_OBJECT) {
                        this._currentAnchor = scalar.getAnchor();
                    }
                    this._currentFieldName = name = scalar.getValue();
                    this._parsingContext.setCurrentName(name);
                    this._currToken = JsonToken.FIELD_NAME;
                    return this._currToken;
                }
            } else if (this._parsingContext.inArray()) {
                this._parsingContext.expectComma();
            }
            this._currentAnchor = null;
            if (evt.is(Event.ID.Scalar)) {
                JsonToken t;
                this._currToken = t = this._decodeScalar((ScalarEvent)evt);
                return t;
            }
            if (evt.is(Event.ID.MappingStart)) {
                Mark m = evt.getStartMark();
                MappingStartEvent map = (MappingStartEvent)evt;
                this._currentAnchor = map.getAnchor();
                this._parsingContext = this._parsingContext.createChildObjectContext(m.getLine(), m.getColumn());
                this._currToken = JsonToken.START_OBJECT;
                return this._currToken;
            }
            if (evt.is(Event.ID.MappingEnd)) {
                this._reportError("Not expecting END_OBJECT but a value");
            }
            if (evt.is(Event.ID.SequenceStart)) {
                Mark m = evt.getStartMark();
                this._currentAnchor = ((NodeEvent)evt).getAnchor();
                this._parsingContext = this._parsingContext.createChildArrayContext(m.getLine(), m.getColumn());
                this._currToken = JsonToken.START_ARRAY;
                return this._currToken;
            }
            if (evt.is(Event.ID.SequenceEnd)) {
                if (!this._parsingContext.inArray()) {
                    this._reportMismatchedEndMarker(93, '}');
                }
                this._parsingContext = this._parsingContext.getParent();
                this._currToken = JsonToken.END_ARRAY;
                return this._currToken;
            }
            if (evt.is(Event.ID.DocumentEnd) || evt.is(Event.ID.DocumentStart)) continue;
            if (evt.is(Event.ID.Alias)) {
                AliasEvent alias = (AliasEvent)evt;
                this._currentIsAlias = true;
                this._textValue = alias.getAnchor();
                this._cleanedTextValue = null;
                this._currToken = JsonToken.VALUE_STRING;
                return this._currToken;
            }
            if (evt.is(Event.ID.StreamEnd)) {
                this.close();
                this._currToken = null;
                return null;
            }
            if (!evt.is(Event.ID.StreamStart)) continue;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected JsonToken _decodeScalar(ScalarEvent scalar) throws IOException {
        String value;
        this._textValue = value = scalar.getValue();
        this._cleanedTextValue = null;
        String typeTag = scalar.getTag();
        int len = value.length();
        if (typeTag == null || typeTag.equals("!")) {
            Tag nodeTag = this._yamlResolver.resolve(NodeId.scalar, value, scalar.getImplicit().canOmitTagInPlainScalar());
            if (nodeTag == Tag.STR) {
                return JsonToken.VALUE_STRING;
            }
            if (nodeTag == Tag.INT) {
                return this._decodeNumberScalar(value, len);
            }
            if (nodeTag == Tag.FLOAT) {
                this._numTypesValid = 0;
                return this._cleanYamlFloat(value);
            }
            if (nodeTag == Tag.BOOL) {
                Boolean B = this._matchYAMLBoolean(value, len);
                if (B == null) return JsonToken.VALUE_STRING;
                return B != false ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
            }
            if (nodeTag != Tag.NULL) return JsonToken.VALUE_STRING;
            return JsonToken.VALUE_NULL;
        }
        if (typeTag.startsWith("tag:yaml.org,2002:") && (typeTag = typeTag.substring("tag:yaml.org,2002:".length())).contains(",")) {
            typeTag = typeTag.split(",")[0];
        }
        if ("binary".equals(typeTag)) {
            value = value.trim();
            try {
                this._binaryValue = Base64Variants.MIME.decode(value);
                return JsonToken.VALUE_EMBEDDED_OBJECT;
            }
            catch (IllegalArgumentException e) {
                this._reportError(e.getMessage());
            }
            return JsonToken.VALUE_EMBEDDED_OBJECT;
        }
        if ("bool".equals(typeTag)) {
            Boolean B = this._matchYAMLBoolean(value, len);
            if (B == null) return JsonToken.VALUE_STRING;
            return B != false ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
        }
        if ("int".equals(typeTag)) {
            return this._decodeNumberScalar(value, len);
        }
        if ("float".equals(typeTag)) {
            this._numTypesValid = 0;
            return this._cleanYamlFloat(value);
        }
        if (!"null".equals(typeTag)) return JsonToken.VALUE_STRING;
        return JsonToken.VALUE_NULL;
    }

    protected Boolean _matchYAMLBoolean(String value, int len) {
        switch (len) {
            case 1: {
                switch (value.charAt(0)) {
                    case 'Y': 
                    case 'y': {
                        return Boolean.TRUE;
                    }
                    case 'N': 
                    case 'n': {
                        return Boolean.FALSE;
                    }
                }
                break;
            }
            case 2: {
                if ("no".equalsIgnoreCase(value)) {
                    return Boolean.FALSE;
                }
                if (!"on".equalsIgnoreCase(value)) break;
                return Boolean.TRUE;
            }
            case 3: {
                if ("yes".equalsIgnoreCase(value)) {
                    return Boolean.TRUE;
                }
                if (!"off".equalsIgnoreCase(value)) break;
                return Boolean.FALSE;
            }
            case 4: {
                if (!"true".equalsIgnoreCase(value)) break;
                return Boolean.TRUE;
            }
            case 5: {
                if (!"false".equalsIgnoreCase(value)) break;
                return Boolean.FALSE;
            }
        }
        return null;
    }

    protected JsonToken _decodeNumberScalar(String value, int len) {
        block10: {
            int i;
            if ("0".equals(value)) {
                this._numberNegative = false;
                this._numberInt = 0;
                this._numTypesValid = 1;
                return JsonToken.VALUE_NUMBER_INT;
            }
            char sign = value.charAt(0);
            if (sign == '-') {
                this._numberNegative = true;
                if (len == 1) {
                    return null;
                }
                i = 1;
            } else if (sign == '+') {
                this._numberNegative = false;
                if (len == 1) {
                    return null;
                }
                i = 1;
            } else {
                this._numberNegative = false;
                i = 0;
            }
            int underscores = 0;
            do {
                char c;
                if ((c = value.charAt(i)) <= '9' && c >= '0') continue;
                if (c != '_') break block10;
                ++underscores;
            } while (++i != len);
            this._numTypesValid = 0;
            if (underscores > 0) {
                return this._cleanYamlInt(this._textValue);
            }
            this._cleanedTextValue = this._textValue;
            return JsonToken.VALUE_NUMBER_INT;
        }
        if (PATTERN_FLOAT.matcher(value).matches()) {
            this._numTypesValid = 0;
            return this._cleanYamlFloat(this._textValue);
        }
        return JsonToken.VALUE_STRING;
    }

    protected JsonToken _decodeIntWithUnderscores(String value, int len) {
        return JsonToken.VALUE_NUMBER_INT;
    }

    @Override
    public boolean hasTextCharacters() {
        return false;
    }

    @Override
    public String getText() throws IOException {
        if (this._currToken == JsonToken.VALUE_STRING) {
            return this._textValue;
        }
        if (this._currToken == JsonToken.FIELD_NAME) {
            return this._currentFieldName;
        }
        if (this._currToken != null) {
            if (this._currToken.isScalarValue()) {
                return this._textValue;
            }
            return this._currToken.asString();
        }
        return null;
    }

    @Override
    public String getCurrentName() throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            return this._currentFieldName;
        }
        return super.getCurrentName();
    }

    @Override
    public char[] getTextCharacters() throws IOException {
        String text = this.getText();
        return text == null ? null : text.toCharArray();
    }

    @Override
    public int getTextLength() throws IOException {
        String text = this.getText();
        return text == null ? 0 : text.length();
    }

    @Override
    public int getTextOffset() throws IOException {
        return 0;
    }

    @Override
    public int getText(Writer writer) throws IOException {
        String str = this.getText();
        if (str == null) {
            return 0;
        }
        writer.write(str);
        return str.length();
    }

    @Override
    public Object getEmbeddedObject() throws IOException {
        if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
            return this._binaryValue;
        }
        return null;
    }

    @Override
    public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
        byte[] b = this.getBinaryValue(b64variant);
        out.write(b);
        return b.length;
    }

    @Override
    protected void _parseNumericValue(int expType) throws IOException {
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            int len = this._cleanedTextValue.length();
            if (this._numberNegative) {
                --len;
            }
            if (len <= 9) {
                this._numberInt = Integer.parseInt(this._cleanedTextValue);
                this._numTypesValid = 1;
                return;
            }
            if (len <= 18) {
                long l = Long.parseLong(this._cleanedTextValue);
                if (len == 10) {
                    if (this._numberNegative) {
                        if (l >= Integer.MIN_VALUE) {
                            this._numberInt = (int)l;
                            this._numTypesValid = 1;
                            return;
                        }
                    } else if (l <= Integer.MAX_VALUE) {
                        this._numberInt = (int)l;
                        this._numTypesValid = 1;
                        return;
                    }
                }
                this._numberLong = l;
                this._numTypesValid = 2;
                return;
            }
            try {
                BigInteger n = new BigInteger(this._cleanedTextValue);
                if (len == 19 && n.bitLength() <= 63) {
                    this._numberLong = n.longValue();
                    this._numTypesValid = 2;
                    return;
                }
                this._numberBigInt = n;
                this._numTypesValid = 4;
                return;
            }
            catch (NumberFormatException nex) {
                this._wrapError("Malformed numeric value '" + this._textValue + "'", nex);
            }
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            String str = this._cleanedTextValue;
            try {
                if (expType == 16) {
                    this._numberBigDecimal = new BigDecimal(str);
                    this._numTypesValid = 16;
                } else {
                    this._numberDouble = Double.parseDouble(str);
                    this._numTypesValid = 8;
                }
            }
            catch (NumberFormatException nex) {
                this._wrapError("Malformed numeric value '" + this._textValue + "'", nex);
            }
            return;
        }
        this._reportError("Current token (" + (Object)((Object)this._currToken) + ") not numeric, can not use numeric value accessors");
    }

    @Override
    protected int _parseIntValue() throws IOException {
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            int len = this._cleanedTextValue.length();
            if (this._numberNegative) {
                --len;
            }
            if (len <= 9) {
                this._numTypesValid = 1;
                this._numberInt = Integer.parseInt(this._cleanedTextValue);
                return this._numberInt;
            }
        }
        this._parseNumericValue(1);
        if ((this._numTypesValid & 1) == 0) {
            this.convertNumberToInt();
        }
        return this._numberInt;
    }

    @Override
    public boolean canReadObjectId() {
        return true;
    }

    @Override
    public boolean canReadTypeId() {
        return true;
    }

    @Override
    public String getObjectId() throws IOException {
        return this._currentAnchor;
    }

    @Override
    public String getTypeId() throws IOException {
        String tag;
        if (this._lastEvent instanceof CollectionStartEvent) {
            tag = ((CollectionStartEvent)this._lastEvent).getTag();
        } else if (this._lastEvent instanceof ScalarEvent) {
            tag = ((ScalarEvent)this._lastEvent).getTag();
        } else {
            return null;
        }
        if (tag != null) {
            while (tag.startsWith("!")) {
                tag = tag.substring(1);
            }
            return tag;
        }
        return null;
    }

    private JsonToken _cleanYamlInt(String str) {
        int i;
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        int n = i = str.charAt(0) == '+' ? 1 : 0;
        while (i < len) {
            char c = str.charAt(i);
            if (c != '_') {
                sb.append(c);
            }
            ++i;
        }
        this._cleanedTextValue = sb.toString();
        return JsonToken.VALUE_NUMBER_INT;
    }

    private JsonToken _cleanYamlFloat(String str) {
        int i;
        int len = str.length();
        int ix = str.indexOf(95);
        if (ix < 0 || len == 0) {
            this._cleanedTextValue = str;
            return JsonToken.VALUE_NUMBER_FLOAT;
        }
        StringBuilder sb = new StringBuilder(len);
        int n = i = str.charAt(0) == '+' ? 1 : 0;
        while (i < len) {
            char c = str.charAt(i);
            if (c != '_') {
                sb.append(c);
            }
            ++i;
        }
        this._cleanedTextValue = sb.toString();
        return JsonToken.VALUE_NUMBER_FLOAT;
    }

    public static enum Feature implements FormatFeature
    {

        final boolean _defaultState;
        final int _mask;

        public static int collectDefaults() {
            int flags = 0;
            for (Feature f : Feature.values()) {
                if (!f.enabledByDefault()) continue;
                flags |= f.getMask();
            }
            return flags;
        }

        private Feature(boolean defaultState) {
            this._defaultState = defaultState;
            this._mask = 1 << this.ordinal();
        }

        @Override
        public boolean enabledByDefault() {
            return this._defaultState;
        }

        @Override
        public boolean enabledIn(int flags) {
            return (flags & this._mask) != 0;
        }

        @Override
        public int getMask() {
            return this._mask;
        }
    }
}

