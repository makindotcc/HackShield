/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.dataformat.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

public class YAMLMapper
extends ObjectMapper {
    private static final long serialVersionUID = 1L;

    public YAMLMapper() {
        this(new YAMLFactory());
    }

    public YAMLMapper(YAMLFactory f) {
        super(f);
    }

    public YAMLMapper(YAMLMapper base) {
        super(base);
    }

    public static Builder builder() {
        return new Builder(new YAMLMapper());
    }

    public static Builder builder(YAMLFactory streamFactory) {
        return new Builder(new YAMLMapper(streamFactory));
    }

    @Override
    public YAMLMapper copy() {
        this._checkInvalidCopy(YAMLMapper.class);
        return new YAMLMapper(this);
    }

    public YAMLMapper configure(YAMLGenerator.Feature f, boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }

    public YAMLMapper configure(YAMLParser.Feature f, boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }

    public YAMLMapper enable(YAMLGenerator.Feature f) {
        ((YAMLFactory)this._jsonFactory).enable(f);
        return this;
    }

    public YAMLMapper enable(YAMLParser.Feature f) {
        ((YAMLFactory)this._jsonFactory).enable(f);
        return this;
    }

    public YAMLMapper disable(YAMLGenerator.Feature f) {
        ((YAMLFactory)this._jsonFactory).disable(f);
        return this;
    }

    public YAMLMapper disable(YAMLParser.Feature f) {
        ((YAMLFactory)this._jsonFactory).disable(f);
        return this;
    }

    @Override
    public final YAMLFactory getFactory() {
        return (YAMLFactory)this._jsonFactory;
    }

    public static class Builder
    extends MapperBuilder<YAMLMapper, Builder> {
        public Builder(YAMLMapper m) {
            super(m);
        }

        public Builder enable(YAMLGenerator.Feature ... features) {
            for (YAMLGenerator.Feature f : features) {
                ((YAMLMapper)this._mapper).enable(f);
            }
            return this;
        }

        public Builder disable(YAMLGenerator.Feature ... features) {
            for (YAMLGenerator.Feature f : features) {
                ((YAMLMapper)this._mapper).disable(f);
            }
            return this;
        }

        public Builder configure(YAMLGenerator.Feature f, boolean state) {
            if (state) {
                ((YAMLMapper)this._mapper).enable(f);
            } else {
                ((YAMLMapper)this._mapper).disable(f);
            }
            return this;
        }
    }
}

