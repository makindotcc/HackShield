/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.dataformat.yaml;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.dataformat.yaml.PackageVersion;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import pl.hackshield.shaded.org.yaml.snakeyaml.DumperOptions;
import pl.hackshield.shaded.org.yaml.snakeyaml.emitter.Emitter;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.AliasEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.DocumentEndEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.DocumentStartEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.Event;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.ImplicitTuple;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.MappingEndEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.MappingStartEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.ScalarEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.SequenceEndEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.SequenceStartEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.StreamEndEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.events.StreamStartEvent;
import pl.hackshield.shaded.org.yaml.snakeyaml.nodes.Tag;

public class YAMLGenerator
extends GeneratorBase {
    protected static final long MIN_INT_AS_LONG = Integer.MIN_VALUE;
    protected static final long MAX_INT_AS_LONG = Integer.MAX_VALUE;
    protected static final Pattern PLAIN_NUMBER_P = Pattern.compile("-?[0-9]*(\\.[0-9]*)?");
    protected static final String TAG_BINARY = Tag.BINARY.toString();
    private static final Set<String> MUST_QUOTE_NAMES = new HashSet<String>(Arrays.asList("yes", "Yes", "YES", "no", "No", "NO", "true", "True", "TRUE", "false", "False", "FALSE", "on", "On", "ON", "off", "Off", "OFF"));
    private static final Set<String> MUST_QUOTE_VALUES = new HashSet<String>(Arrays.asList("y", "Y", "n", "N", "yes", "Yes", "YES", "no", "No", "NO", "true", "True", "TRUE", "false", "False", "FALSE", "on", "On", "ON", "off", "Off", "OFF", "null", "Null", "NULL"));
    protected final IOContext _ioContext;
    protected int _formatFeatures;
    protected Writer _writer;
    protected DumperOptions _outputOptions;
    protected final DumperOptions.Version _docVersion;
    private static final DumperOptions.ScalarStyle STYLE_UNQUOTED_NAME = DumperOptions.ScalarStyle.PLAIN;
    private static final DumperOptions.ScalarStyle STYLE_SCALAR = DumperOptions.ScalarStyle.PLAIN;
    private static final DumperOptions.ScalarStyle STYLE_QUOTED = DumperOptions.ScalarStyle.DOUBLE_QUOTED;
    private static final DumperOptions.ScalarStyle STYLE_LITERAL;
    private static final DumperOptions.ScalarStyle STYLE_BASE64;
    private static final DumperOptions.ScalarStyle STYLE_PLAIN;
    protected Emitter _emitter;
    protected String _objectId;
    protected String _typeId;
    protected int _rootValueCount;
    private static final ImplicitTuple NO_TAGS;
    private static final ImplicitTuple EXPLICIT_TAGS;

    public YAMLGenerator(IOContext ctxt, int jsonFeatures, int yamlFeatures, ObjectCodec codec, Writer out, DumperOptions.Version version) throws IOException {
        super(jsonFeatures, codec);
        this._ioContext = ctxt;
        this._formatFeatures = yamlFeatures;
        this._writer = out;
        this._docVersion = version;
        this._outputOptions = this.buildDumperOptions(jsonFeatures, yamlFeatures, version);
        this._emitter = new Emitter(this._writer, this._outputOptions);
        this._emit(new StreamStartEvent(null, null));
        this._emitStartDocument();
    }

    protected DumperOptions buildDumperOptions(int jsonFeatures, int yamlFeatures, DumperOptions.Version version) {
        DumperOptions opt = new DumperOptions();
        if (Feature.CANONICAL_OUTPUT.enabledIn(this._formatFeatures)) {
            opt.setCanonical(true);
        } else {
            opt.setCanonical(false);
            opt.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        }
        opt.setSplitLines(Feature.SPLIT_LINES.enabledIn(this._formatFeatures));
        if (Feature.INDENT_ARRAYS.enabledIn(this._formatFeatures)) {
            opt.setIndicatorIndent(1);
            opt.setIndent(2);
        }
        if (Feature.USE_PLATFORM_LINE_BREAKS.enabledIn(this._formatFeatures)) {
            opt.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
        }
        return opt;
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public YAMLGenerator useDefaultPrettyPrinter() {
        return this;
    }

    @Override
    public YAMLGenerator setPrettyPrinter(PrettyPrinter pp) {
        return this;
    }

    @Override
    public Object getOutputTarget() {
        return this._writer;
    }

    @Override
    public int getOutputBuffered() {
        return -1;
    }

    @Override
    public int getFormatFeatures() {
        return this._formatFeatures;
    }

    @Override
    public JsonGenerator overrideFormatFeatures(int values, int mask) {
        this._formatFeatures = this._formatFeatures & ~mask | values & mask;
        return this;
    }

    @Override
    public boolean canUseSchema(FormatSchema schema) {
        return false;
    }

    @Override
    public boolean canWriteFormattedNumbers() {
        return true;
    }

    public YAMLGenerator enable(Feature f) {
        this._formatFeatures |= f.getMask();
        return this;
    }

    public YAMLGenerator disable(Feature f) {
        this._formatFeatures &= ~f.getMask();
        return this;
    }

    public final boolean isEnabled(Feature f) {
        return (this._formatFeatures & f.getMask()) != 0;
    }

    public YAMLGenerator configure(Feature f, boolean state) {
        if (state) {
            this.enable(f);
        } else {
            this.disable(f);
        }
        return this;
    }

    @Override
    public final void writeFieldName(String name) throws IOException {
        if (this._writeContext.writeFieldName(name) == 4) {
            this._reportError("Can not write a field name, expecting a value");
        }
        this._writeFieldName(name);
    }

    @Override
    public final void writeFieldName(SerializableString name) throws IOException {
        if (this._writeContext.writeFieldName(name.getValue()) == 4) {
            this._reportError("Can not write a field name, expecting a value");
        }
        this._writeFieldName(name.getValue());
    }

    @Override
    public void writeFieldId(long id) throws IOException {
        String idStr = Long.valueOf(id).toString();
        if (this._writeContext.writeFieldName(idStr) == 4) {
            this._reportError("Can not write a field id, expecting a value");
        }
        this._writeScalar(idStr, "int", STYLE_SCALAR);
    }

    private final void _writeFieldName(String name) throws IOException {
        this._writeScalar(name, "string", this._nameNeedsQuoting(name) ? STYLE_QUOTED : STYLE_UNQUOTED_NAME);
    }

    @Override
    public final void flush() throws IOException {
        if (this.isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
            this._writer.flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.isClosed()) {
            this._emitEndDocument();
            this._emit(new StreamEndEvent(null, null));
            super.close();
            if (this._writer != null) {
                if (this._ioContext.isResourceManaged() || this.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
                    this._writer.close();
                } else if (this.isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
                    this._writer.flush();
                }
            }
        }
    }

    @Override
    public final void writeStartArray() throws IOException {
        this._verifyValueWrite("start an array");
        this._writeContext = this._writeContext.createChildArrayContext();
        DumperOptions.FlowStyle style = this._outputOptions.getDefaultFlowStyle();
        String yamlTag = this._typeId;
        boolean implicit = yamlTag == null;
        String anchor = this._objectId;
        if (anchor != null) {
            this._objectId = null;
        }
        this._emit(new SequenceStartEvent(anchor, yamlTag, implicit, null, null, style));
    }

    @Override
    public final void writeEndArray() throws IOException {
        if (!this._writeContext.inArray()) {
            this._reportError("Current context not Array but " + this._writeContext.typeDesc());
        }
        this._typeId = null;
        this._writeContext = this._writeContext.getParent();
        this._emit(new SequenceEndEvent(null, null));
    }

    @Override
    public final void writeStartObject() throws IOException {
        this._verifyValueWrite("start an object");
        this._writeContext = this._writeContext.createChildObjectContext();
        DumperOptions.FlowStyle style = this._outputOptions.getDefaultFlowStyle();
        String yamlTag = this._typeId;
        boolean implicit = yamlTag == null;
        String anchor = this._objectId;
        if (anchor != null) {
            this._objectId = null;
        }
        this._emit(new MappingStartEvent(anchor, yamlTag, implicit, null, null, style));
    }

    @Override
    public final void writeEndObject() throws IOException {
        if (!this._writeContext.inObject()) {
            this._reportError("Current context not Object but " + this._writeContext.typeDesc());
        }
        this._typeId = null;
        this._writeContext = this._writeContext.getParent();
        this._emit(new MappingEndEvent(null, null));
    }

    @Override
    public void writeString(String text) throws IOException, JsonGenerationException {
        if (text == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write String value");
        if (text.isEmpty()) {
            this._writeScalar(text, "string", STYLE_QUOTED);
            return;
        }
        DumperOptions.ScalarStyle style = Feature.MINIMIZE_QUOTES.enabledIn(this._formatFeatures) ? (this._valueNeedsQuoting(text) || Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS.enabledIn(this._formatFeatures) && PLAIN_NUMBER_P.matcher(text).matches() ? STYLE_QUOTED : (text.indexOf(10) >= 0 ? STYLE_LITERAL : STYLE_PLAIN)) : (Feature.LITERAL_BLOCK_STYLE.enabledIn(this._formatFeatures) && text.indexOf(10) >= 0 ? STYLE_LITERAL : STYLE_QUOTED);
        this._writeScalar(text, "string", style);
    }

    @Override
    public void writeString(char[] text, int offset, int len) throws IOException {
        this.writeString(new String(text, offset, len));
    }

    @Override
    public final void writeString(SerializableString sstr) throws IOException {
        this.writeString(sstr.toString());
    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int len) throws IOException {
        this._reportUnsupportedOperation();
    }

    @Override
    public final void writeUTF8String(byte[] text, int offset, int len) throws IOException {
        this.writeString(new String(text, offset, len, "UTF-8"));
    }

    @Override
    public void writeRaw(String text) throws IOException {
        this._reportUnsupportedOperation();
    }

    @Override
    public void writeRaw(String text, int offset, int len) throws IOException {
        this._reportUnsupportedOperation();
    }

    @Override
    public void writeRaw(char[] text, int offset, int len) throws IOException {
        this._reportUnsupportedOperation();
    }

    @Override
    public void writeRaw(char c) throws IOException {
        this._reportUnsupportedOperation();
    }

    @Override
    public void writeRawValue(String text) throws IOException {
        this._reportUnsupportedOperation();
    }

    @Override
    public void writeRawValue(String text, int offset, int len) throws IOException {
        this._reportUnsupportedOperation();
    }

    @Override
    public void writeRawValue(char[] text, int offset, int len) throws IOException {
        this._reportUnsupportedOperation();
    }

    @Override
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
        if (data == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write Binary value");
        if (offset > 0 || offset + len != data.length) {
            data = Arrays.copyOfRange(data, offset, offset + len);
        }
        this._writeScalarBinary(b64variant, data);
    }

    @Override
    public void writeBoolean(boolean state) throws IOException {
        this._verifyValueWrite("write boolean value");
        this._writeScalar(state ? "true" : "false", "bool", STYLE_SCALAR);
    }

    @Override
    public void writeNumber(int i) throws IOException {
        this._verifyValueWrite("write number");
        this._writeScalar(String.valueOf(i), "int", STYLE_SCALAR);
    }

    @Override
    public void writeNumber(long l) throws IOException {
        if (l <= Integer.MAX_VALUE && l >= Integer.MIN_VALUE) {
            this.writeNumber((int)l);
            return;
        }
        this._verifyValueWrite("write number");
        this._writeScalar(String.valueOf(l), "long", STYLE_SCALAR);
    }

    @Override
    public void writeNumber(BigInteger v) throws IOException {
        if (v == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write number");
        this._writeScalar(String.valueOf(v.toString()), "java.math.BigInteger", STYLE_SCALAR);
    }

    @Override
    public void writeNumber(double d) throws IOException {
        this._verifyValueWrite("write number");
        this._writeScalar(String.valueOf(d), "double", STYLE_SCALAR);
    }

    @Override
    public void writeNumber(float f) throws IOException {
        this._verifyValueWrite("write number");
        this._writeScalar(String.valueOf(f), "float", STYLE_SCALAR);
    }

    @Override
    public void writeNumber(BigDecimal dec) throws IOException {
        if (dec == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write number");
        String str = this.isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) ? dec.toPlainString() : dec.toString();
        this._writeScalar(str, "java.math.BigDecimal", STYLE_SCALAR);
    }

    @Override
    public void writeNumber(String encodedValue) throws IOException, JsonGenerationException, UnsupportedOperationException {
        if (encodedValue == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write number");
        this._writeScalar(encodedValue, "number", STYLE_SCALAR);
    }

    @Override
    public void writeNull() throws IOException {
        this._verifyValueWrite("write null value");
        this._writeScalar("null", "object", STYLE_SCALAR);
    }

    @Override
    public boolean canWriteObjectId() {
        return Feature.USE_NATIVE_OBJECT_ID.enabledIn(this._formatFeatures);
    }

    @Override
    public boolean canWriteTypeId() {
        return Feature.USE_NATIVE_TYPE_ID.enabledIn(this._formatFeatures);
    }

    @Override
    public void writeTypeId(Object id) throws IOException {
        this._typeId = String.valueOf(id);
    }

    @Override
    public void writeObjectRef(Object id) throws IOException {
        this._verifyValueWrite("write Object reference");
        AliasEvent evt = new AliasEvent(String.valueOf(id), null, null);
        this._emit(evt);
    }

    @Override
    public void writeObjectId(Object id) throws IOException {
        this._objectId = id == null ? null : String.valueOf(id);
    }

    @Override
    protected final void _verifyValueWrite(String typeMsg) throws IOException {
        int status = this._writeContext.writeValue();
        if (status == 5) {
            this._reportError("Can not " + typeMsg + ", expecting field name");
        }
        if (this._writeContext.inRoot() && this._writeContext.getCurrentIndex() > 0) {
            this._emitEndDocument();
            this._emitStartDocument();
        }
    }

    @Override
    protected void _releaseBuffers() {
    }

    protected void _writeScalar(String value, String type, DumperOptions.ScalarStyle style) throws IOException {
        this._emit(this._scalarEvent(value, style));
    }

    private void _writeScalarBinary(Base64Variant b64variant, byte[] data) throws IOException {
        if (b64variant == Base64Variants.getDefaultVariant()) {
            b64variant = Base64Variants.MIME;
        }
        String lf = this._lf();
        String encoded = this._base64encode(b64variant, data, lf);
        this._emit(new ScalarEvent(null, TAG_BINARY, EXPLICIT_TAGS, encoded, null, null, STYLE_BASE64));
    }

    protected ScalarEvent _scalarEvent(String value, DumperOptions.ScalarStyle style) {
        String anchor;
        String yamlTag = this._typeId;
        if (yamlTag != null) {
            this._typeId = null;
        }
        if ((anchor = this._objectId) != null) {
            this._objectId = null;
        }
        return new ScalarEvent(anchor, yamlTag, NO_TAGS, value, null, null, style);
    }

    private String _base64encode(Base64Variant b64v, byte[] input, String linefeed) {
        int inputEnd = input.length;
        StringBuilder sb = new StringBuilder(inputEnd + (inputEnd >> 2) + (inputEnd >> 3));
        int chunksBeforeLF = b64v.getMaxLineLength() >> 2;
        int inputPtr = 0;
        int safeInputEnd = inputEnd - 3;
        while (inputPtr <= safeInputEnd) {
            int b24 = input[inputPtr++] << 8;
            b24 |= input[inputPtr++] & 0xFF;
            b24 = b24 << 8 | input[inputPtr++] & 0xFF;
            b64v.encodeBase64Chunk(sb, b24);
            if (--chunksBeforeLF > 0) continue;
            sb.append(linefeed);
            chunksBeforeLF = b64v.getMaxLineLength() >> 2;
        }
        int inputLeft = inputEnd - inputPtr;
        if (inputLeft > 0) {
            int b24 = input[inputPtr++] << 16;
            if (inputLeft == 2) {
                b24 |= (input[inputPtr++] & 0xFF) << 8;
            }
            b64v.encodeBase64Partial(sb, b24, inputLeft);
        }
        return sb.toString();
    }

    private boolean _nameNeedsQuoting(String name) {
        if (name.length() == 0) {
            return true;
        }
        switch (name.charAt(0)) {
            case 'F': 
            case 'N': 
            case 'O': 
            case 'T': 
            case 'Y': 
            case 'f': 
            case 'n': 
            case 'o': 
            case 't': 
            case 'y': {
                return MUST_QUOTE_NAMES.contains(name);
            }
            case '+': 
            case '-': 
            case '.': 
            case '0': 
            case '1': 
            case '2': 
            case '3': 
            case '4': 
            case '5': 
            case '6': 
            case '7': 
            case '8': 
            case '9': {
                return true;
            }
        }
        return false;
    }

    private boolean _valueNeedsQuoting(String name) {
        switch (name.charAt(0)) {
            case 'F': 
            case 'N': 
            case 'O': 
            case 'T': 
            case 'Y': 
            case 'f': 
            case 'n': 
            case 'o': 
            case 't': 
            case 'y': {
                if (!MUST_QUOTE_VALUES.contains(name)) break;
                return true;
            }
        }
        return YAMLGenerator._valueHasQuotableChar(name);
    }

    private static boolean _valueHasQuotableChar(String inputStr) {
        int end = inputStr.length();
        block5: for (int i = 0; i < end; ++i) {
            switch (inputStr.charAt(i)) {
                case ',': 
                case '[': 
                case ']': 
                case '{': 
                case '}': {
                    return true;
                }
                case '\t': 
                case ' ': {
                    if (i >= end - 1 || '#' != inputStr.charAt(i + 1)) continue block5;
                    return true;
                }
                case ':': {
                    if (i >= end - 1 || ' ' != inputStr.charAt(i + 1) && '\t' != inputStr.charAt(i + 1)) continue block5;
                    return true;
                }
            }
        }
        return false;
    }

    protected String _lf() {
        return this._outputOptions.getLineBreak().getString();
    }

    protected void _emitStartDocument() throws IOException {
        Map<String, String> noTags = Collections.emptyMap();
        boolean startMarker = Feature.WRITE_DOC_START_MARKER.enabledIn(this._formatFeatures);
        this._emit(new DocumentStartEvent(null, null, startMarker, this._docVersion, noTags));
    }

    protected void _emitEndDocument() throws IOException {
        this._emit(new DocumentEndEvent(null, null, false));
    }

    protected final void _emit(Event e) throws IOException {
        this._emitter.emit(e);
    }

    static {
        STYLE_BASE64 = STYLE_LITERAL = DumperOptions.ScalarStyle.LITERAL;
        STYLE_PLAIN = DumperOptions.ScalarStyle.PLAIN;
        NO_TAGS = new ImplicitTuple(true, true);
        EXPLICIT_TAGS = new ImplicitTuple(false, false);
    }

    public static enum Feature implements FormatFeature
    {
        WRITE_DOC_START_MARKER(true),
        USE_NATIVE_OBJECT_ID(true),
        USE_NATIVE_TYPE_ID(true),
        CANONICAL_OUTPUT(false),
        SPLIT_LINES(true),
        MINIMIZE_QUOTES(false),
        ALWAYS_QUOTE_NUMBERS_AS_STRINGS(false),
        LITERAL_BLOCK_STYLE(false),
        INDENT_ARRAYS(false),
        USE_PLATFORM_LINE_BREAKS(false);

        protected final boolean _defaultState;
        protected final int _mask;

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

