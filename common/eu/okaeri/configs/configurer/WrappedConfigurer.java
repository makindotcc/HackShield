/*
 * Decompiled with CFR 0.150.
 */
package eu.okaeri.configs.configurer;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.FieldDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesContext;
import eu.okaeri.configs.serdes.SerdesRegistry;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WrappedConfigurer
extends Configurer {
    private final Configurer wrapped;

    public WrappedConfigurer(Configurer wrapped) {
        this.wrapped = wrapped;
    }

    public Configurer getWrapped() {
        return this.wrapped;
    }

    @Override
    public void register(OkaeriSerdesPack pack) {
        this.getWrapped().register(pack);
    }

    @Override
    public List<String> getExtensions() {
        return this.getWrapped().getExtensions();
    }

    @Override
    public void setValue(String key, Object value, GenericsDeclaration genericType, FieldDeclaration field) {
        this.getWrapped().setValue(key, value, genericType, field);
    }

    @Override
    public void setValueUnsafe(String key, Object value) {
        this.getWrapped().setValueUnsafe(key, value);
    }

    @Override
    public Object getValue(String key) {
        return this.getWrapped().getValue(key);
    }

    @Override
    public Object getValueUnsafe(String key) {
        return this.getWrapped().getValueUnsafe(key);
    }

    @Override
    public Object remove(String key) {
        return this.getWrapped().remove(key);
    }

    @Override
    public boolean isToStringObject(Object object, GenericsDeclaration genericType) {
        return this.getWrapped().isToStringObject(object, genericType);
    }

    @Override
    public Object simplifyCollection(Collection<?> value, GenericsDeclaration genericType, SerdesContext serdesContext, boolean conservative) throws OkaeriException {
        return this.getWrapped().simplifyCollection(value, genericType, serdesContext, conservative);
    }

    @Override
    public Object simplifyMap(Map<Object, Object> value, GenericsDeclaration genericType, SerdesContext serdesContext, boolean conservative) throws OkaeriException {
        return this.getWrapped().simplifyMap(value, genericType, serdesContext, conservative);
    }

    @Override
    public Object simplify(Object value, GenericsDeclaration genericType, SerdesContext serdesContext, boolean conservative) throws OkaeriException {
        return this.getWrapped().simplify(value, genericType, serdesContext, conservative);
    }

    @Override
    public <T> T getValue(String key, Class<T> clazz, GenericsDeclaration genericType, SerdesContext serdesContext) {
        return this.getWrapped().getValue(key, clazz, genericType, serdesContext);
    }

    @Override
    public <T> T resolveType(Object object, GenericsDeclaration genericSource, Class<T> targetClazz, GenericsDeclaration genericTarget, SerdesContext serdesContext) throws OkaeriException {
        return this.getWrapped().resolveType(object, genericSource, targetClazz, genericTarget, serdesContext);
    }

    @Override
    public Class<?> resolveTargetBaseType(SerdesContext serdesContext, GenericsDeclaration target, GenericsDeclaration source) {
        return this.getWrapped().resolveTargetBaseType(serdesContext, target, source);
    }

    @Override
    public Object createInstance(Class<?> clazz) throws OkaeriException {
        return this.getWrapped().createInstance(clazz);
    }

    @Override
    public boolean keyExists(String key) {
        return this.getWrapped().keyExists(key);
    }

    @Override
    public boolean isValid(FieldDeclaration declaration, Object value) {
        return this.getWrapped().isValid(declaration, value);
    }

    @Override
    public List<String> getAllKeys() {
        return this.getWrapped().getAllKeys();
    }

    @Override
    public Set<String> sort(ConfigDeclaration declaration) {
        return this.getWrapped().sort(declaration);
    }

    @Override
    public void write(OutputStream outputStream, ConfigDeclaration declaration) throws Exception {
        this.getWrapped().write(outputStream, declaration);
    }

    @Override
    public void load(InputStream inputStream, ConfigDeclaration declaration) throws Exception {
        this.getWrapped().load(inputStream, declaration);
    }

    @Override
    public OkaeriConfig getParent() {
        return this.getWrapped().getParent();
    }

    @Override
    public void setParent(OkaeriConfig parent) {
        this.getWrapped().setParent(parent);
    }

    @Override
    public void setRegistry(SerdesRegistry registry) {
        this.getWrapped().setRegistry(registry);
    }

    @Override
    public SerdesRegistry getRegistry() {
        return this.getWrapped().getRegistry();
    }
}

