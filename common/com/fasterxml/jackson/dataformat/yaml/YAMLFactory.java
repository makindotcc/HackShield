/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.dataformat.yaml;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.dataformat.yaml.PackageVersion;
import com.fasterxml.jackson.dataformat.yaml.UTF8Reader;
import com.fasterxml.jackson.dataformat.yaml.UTF8Writer;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import pl.hackshield.shaded.org.yaml.snakeyaml.DumperOptions;

public class YAMLFactory
extends JsonFactory {
    private static final long serialVersionUID = 1L;
    protected static final Charset UTF8 = Charset.forName("UTF-8");
    public static final String FORMAT_NAME_YAML = "YAML";
    protected static final int DEFAULT_YAML_PARSER_FEATURE_FLAGS = YAMLParser.Feature.collectDefaults();
    protected static final int DEFAULT_YAML_GENERATOR_FEATURE_FLAGS = YAMLGenerator.Feature.collectDefaults();
    private static final byte UTF8_BOM_1 = -17;
    private static final byte UTF8_BOM_2 = -69;
    private static final byte UTF8_BOM_3 = -65;
    protected int _yamlParserFeatures = DEFAULT_YAML_PARSER_FEATURE_FLAGS;
    protected int _yamlGeneratorFeatures = DEFAULT_YAML_GENERATOR_FEATURE_FLAGS;
    protected DumperOptions.Version _version;

    public YAMLFactory() {
        this((ObjectCodec)null);
    }

    public YAMLFactory(ObjectCodec oc) {
        super(oc);
        this._yamlParserFeatures = DEFAULT_YAML_PARSER_FEATURE_FLAGS;
        this._yamlGeneratorFeatures = DEFAULT_YAML_GENERATOR_FEATURE_FLAGS;
        this._version = null;
    }

    public YAMLFactory(YAMLFactory src, ObjectCodec oc) {
        super(src, oc);
        this._version = src._version;
        this._yamlParserFeatures = src._yamlParserFeatures;
        this._yamlGeneratorFeatures = src._yamlGeneratorFeatures;
    }

    protected YAMLFactory(YAMLFactoryBuilder b) {
        super(b, false);
        this._yamlGeneratorFeatures = b.formatGeneratorFeaturesMask();
    }

    public YAMLFactoryBuilder rebuild() {
        return new YAMLFactoryBuilder(this);
    }

    public static YAMLFactoryBuilder builder() {
        return new YAMLFactoryBuilder();
    }

    @Override
    public YAMLFactory copy() {
        this._checkInvalidCopy(YAMLFactory.class);
        return new YAMLFactory(this, null);
    }

    @Override
    protected Object readResolve() {
        return new YAMLFactory(this, this._objectCodec);
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public boolean canUseCharArrays() {
        return false;
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME_YAML;
    }

    @Override
    public MatchStrength hasFormat(InputAccessor acc) throws IOException {
        if (!acc.hasMoreBytes()) {
            return MatchStrength.INCONCLUSIVE;
        }
        byte b = acc.nextByte();
        if (b == -17) {
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            if (acc.nextByte() != -69) {
                return MatchStrength.NO_MATCH;
            }
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            if (acc.nextByte() != -65) {
                return MatchStrength.NO_MATCH;
            }
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            b = acc.nextByte();
        }
        if (b == 45 && acc.hasMoreBytes() && acc.nextByte() == 45 && acc.hasMoreBytes() && acc.nextByte() == 45) {
            return MatchStrength.FULL_MATCH;
        }
        return MatchStrength.INCONCLUSIVE;
    }

    public final YAMLFactory configure(YAMLParser.Feature f, boolean state) {
        if (state) {
            this.enable(f);
        } else {
            this.disable(f);
        }
        return this;
    }

    public YAMLFactory enable(YAMLParser.Feature f) {
        this._yamlParserFeatures |= f.getMask();
        return this;
    }

    public YAMLFactory disable(YAMLParser.Feature f) {
        this._yamlParserFeatures &= ~f.getMask();
        return this;
    }

    public final boolean isEnabled(YAMLParser.Feature f) {
        return (this._yamlParserFeatures & f.getMask()) != 0;
    }

    @Override
    public int getFormatParserFeatures() {
        return this._yamlParserFeatures;
    }

    public final YAMLFactory configure(YAMLGenerator.Feature f, boolean state) {
        if (state) {
            this.enable(f);
        } else {
            this.disable(f);
        }
        return this;
    }

    public YAMLFactory enable(YAMLGenerator.Feature f) {
        this._yamlGeneratorFeatures |= f.getMask();
        return this;
    }

    public YAMLFactory disable(YAMLGenerator.Feature f) {
        this._yamlGeneratorFeatures &= ~f.getMask();
        return this;
    }

    public final boolean isEnabled(YAMLGenerator.Feature f) {
        return (this._yamlGeneratorFeatures & f.getMask()) != 0;
    }

    @Override
    public int getFormatGeneratorFeatures() {
        return this._yamlGeneratorFeatures;
    }

    @Override
    public YAMLParser createParser(String content) throws IOException {
        return this.createParser(new StringReader(content));
    }

    @Override
    public YAMLParser createParser(File f) throws IOException {
        IOContext ctxt = this._createContext(f, true);
        return this._createParser(this._decorate(new FileInputStream(f), ctxt), ctxt);
    }

    @Override
    public YAMLParser createParser(URL url) throws IOException {
        IOContext ctxt = this._createContext(url, true);
        return this._createParser(this._decorate(this._optimizedStreamFromURL(url), ctxt), ctxt);
    }

    @Override
    public YAMLParser createParser(InputStream in) throws IOException {
        IOContext ctxt = this._createContext(in, false);
        return this._createParser(this._decorate(in, ctxt), ctxt);
    }

    @Override
    public YAMLParser createParser(Reader r) throws IOException {
        IOContext ctxt = this._createContext(r, false);
        return this._createParser(this._decorate(r, ctxt), ctxt);
    }

    @Override
    public YAMLParser createParser(char[] data) throws IOException {
        return this.createParser(new CharArrayReader(data, 0, data.length));
    }

    @Override
    public YAMLParser createParser(char[] data, int offset, int len) throws IOException {
        return this.createParser(new CharArrayReader(data, offset, len));
    }

    @Override
    public YAMLParser createParser(byte[] data) throws IOException {
        InputStream in;
        IOContext ctxt = this._createContext(data, true);
        if (this._inputDecorator != null && (in = this._inputDecorator.decorate(ctxt, data, 0, data.length)) != null) {
            return this._createParser(in, ctxt);
        }
        return this._createParser(data, 0, data.length, ctxt);
    }

    @Override
    public YAMLParser createParser(byte[] data, int offset, int len) throws IOException {
        InputStream in;
        IOContext ctxt = this._createContext(data, true);
        if (this._inputDecorator != null && (in = this._inputDecorator.decorate(ctxt, data, offset, len)) != null) {
            return this._createParser(in, ctxt);
        }
        return this._createParser(data, offset, len, ctxt);
    }

    @Override
    public YAMLGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
        IOContext ctxt = this._createContext(out, false);
        ctxt.setEncoding(enc);
        return this._createGenerator(this._createWriter(this._decorate(out, ctxt), enc, ctxt), ctxt);
    }

    @Override
    public YAMLGenerator createGenerator(OutputStream out) throws IOException {
        IOContext ctxt = this._createContext(out, false);
        return this._createGenerator(this._createWriter(this._decorate(out, ctxt), JsonEncoding.UTF8, ctxt), ctxt);
    }

    @Override
    public YAMLGenerator createGenerator(Writer out) throws IOException {
        IOContext ctxt = this._createContext(out, false);
        return this._createGenerator(this._decorate(out, ctxt), ctxt);
    }

    @Override
    public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
        FileOutputStream out = new FileOutputStream(f);
        IOContext ctxt = this._createContext(f, true);
        ctxt.setEncoding(enc);
        return this._createGenerator(this._createWriter(this._decorate(out, ctxt), enc, ctxt), ctxt);
    }

    @Override
    protected YAMLParser _createParser(InputStream in, IOContext ctxt) throws IOException {
        return new YAMLParser(ctxt, this._getBufferRecycler(), this._parserFeatures, this._yamlParserFeatures, this._objectCodec, this._createReader(in, null, ctxt));
    }

    @Override
    protected YAMLParser _createParser(Reader r, IOContext ctxt) throws IOException {
        return new YAMLParser(ctxt, this._getBufferRecycler(), this._parserFeatures, this._yamlParserFeatures, this._objectCodec, r);
    }

    @Override
    protected YAMLParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
        return new YAMLParser(ctxt, this._getBufferRecycler(), this._parserFeatures, this._yamlParserFeatures, this._objectCodec, new CharArrayReader(data, offset, len));
    }

    @Override
    protected YAMLParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
        return new YAMLParser(ctxt, this._getBufferRecycler(), this._parserFeatures, this._yamlParserFeatures, this._objectCodec, this._createReader(data, offset, len, null, ctxt));
    }

    @Override
    protected YAMLGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
        int feats = this._yamlGeneratorFeatures;
        YAMLGenerator gen = new YAMLGenerator(ctxt, this._generatorFeatures, feats, this._objectCodec, out, this._version);
        return gen;
    }

    @Override
    protected YAMLGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
        throw new IllegalStateException();
    }

    @Override
    protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
        if (enc == JsonEncoding.UTF8) {
            return new UTF8Writer(out);
        }
        return new OutputStreamWriter(out, enc.getJavaName());
    }

    protected Reader _createReader(InputStream in, JsonEncoding enc, IOContext ctxt) throws IOException {
        if (enc == null) {
            enc = JsonEncoding.UTF8;
        }
        if (enc == JsonEncoding.UTF8) {
            boolean autoClose = ctxt.isResourceManaged() || this.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE);
            return new UTF8Reader(in, autoClose);
        }
        return new InputStreamReader(in, enc.getJavaName());
    }

    protected Reader _createReader(byte[] data, int offset, int len, JsonEncoding enc, IOContext ctxt) throws IOException {
        if (enc == null) {
            enc = JsonEncoding.UTF8;
        }
        if (enc == null || enc == JsonEncoding.UTF8) {
            return new UTF8Reader(data, offset, len, true);
        }
        ByteArrayInputStream in = new ByteArrayInputStream(data, offset, len);
        return new InputStreamReader((InputStream)in, enc.getJavaName());
    }
}

