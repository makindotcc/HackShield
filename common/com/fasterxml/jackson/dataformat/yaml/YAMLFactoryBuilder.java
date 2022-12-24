/*
 * Decompiled with CFR 0.150.
 */
package com.fasterxml.jackson.dataformat.yaml;

import com.fasterxml.jackson.core.TSFBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public class YAMLFactoryBuilder
extends TSFBuilder<YAMLFactory, YAMLFactoryBuilder> {
    protected int _formatGeneratorFeatures;

    protected YAMLFactoryBuilder() {
        this._formatGeneratorFeatures = YAMLFactory.DEFAULT_YAML_GENERATOR_FEATURE_FLAGS;
    }

    public YAMLFactoryBuilder(YAMLFactory base) {
        super(base);
        this._formatGeneratorFeatures = base._yamlGeneratorFeatures;
    }

    public YAMLFactoryBuilder enable(YAMLGenerator.Feature f) {
        this._formatGeneratorFeatures |= f.getMask();
        return (YAMLFactoryBuilder)this._this();
    }

    public YAMLFactoryBuilder enable(YAMLGenerator.Feature first, YAMLGenerator.Feature ... other) {
        this._formatGeneratorFeatures |= first.getMask();
        for (YAMLGenerator.Feature f : other) {
            this._formatGeneratorFeatures |= f.getMask();
        }
        return (YAMLFactoryBuilder)this._this();
    }

    public YAMLFactoryBuilder disable(YAMLGenerator.Feature f) {
        this._formatGeneratorFeatures &= ~f.getMask();
        return (YAMLFactoryBuilder)this._this();
    }

    public YAMLFactoryBuilder disable(YAMLGenerator.Feature first, YAMLGenerator.Feature ... other) {
        this._formatGeneratorFeatures &= ~first.getMask();
        for (YAMLGenerator.Feature f : other) {
            this._formatGeneratorFeatures &= ~f.getMask();
        }
        return (YAMLFactoryBuilder)this._this();
    }

    public YAMLFactoryBuilder configure(YAMLGenerator.Feature f, boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }

    public int formatGeneratorFeaturesMask() {
        return this._formatGeneratorFeatures;
    }

    @Override
    public YAMLFactory build() {
        return new YAMLFactory(this);
    }
}

