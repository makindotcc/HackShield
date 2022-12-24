/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.schema;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Comments;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.configs.annotation.Variable;
import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.SerdesAnnotationResolver;
import eu.okaeri.configs.serdes.SerdesContextAttachment;
import eu.okaeri.configs.serdes.SerdesContextAttachments;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import lombok.NonNull;

public class FieldDeclaration {
    private static final Logger LOGGER = Logger.getLogger(FieldDeclaration.class.getSimpleName());
    private static final Map<CacheEntry, FieldDeclaration> DECLARATION_CACHE = new ConcurrentHashMap<CacheEntry, FieldDeclaration>();
    private static final Set<String> FINAL_WARNS = new HashSet<String>();
    private Object startingValue;
    private String name;
    private String[] comment;
    private GenericsDeclaration type;
    private Variable variable;
    private boolean variableHide;
    private Field field;
    private Object object;

    public static FieldDeclaration of(@NonNull ConfigDeclaration config, @NonNull Field field, Object object) {
        if (config == null) {
            throw new NullPointerException("config is marked non-null but is null");
        }
        if (field == null) {
            throw new NullPointerException("field is marked non-null but is null");
        }
        CacheEntry cache = new CacheEntry(config.getType(), field.getName());
        FieldDeclaration template = DECLARATION_CACHE.computeIfAbsent(cache, entry -> {
            FieldDeclaration declaration = new FieldDeclaration();
            field.setAccessible(true);
            if (field.getAnnotation(Exclude.class) != null) {
                return null;
            }
            CustomKey customKey = field.getAnnotation(CustomKey.class);
            if (customKey != null) {
                declaration.setName("".equals(customKey.value()) ? field.getName() : customKey.value());
            } else if (config.getNameStrategy() != null) {
                Names nameStrategy = config.getNameStrategy();
                NameStrategy strategy = nameStrategy.strategy();
                NameModifier modifier = nameStrategy.modifier();
                String name = strategy.getRegex().matcher(field.getName()).replaceAll(strategy.getReplacement());
                if (modifier == NameModifier.TO_UPPER_CASE) {
                    name = name.toUpperCase(Locale.ROOT);
                } else if (modifier == NameModifier.TO_LOWER_CASE) {
                    name = name.toLowerCase(Locale.ROOT);
                }
                declaration.setName(name);
            } else {
                declaration.setName(field.getName());
            }
            Variable variable = field.getAnnotation(Variable.class);
            declaration.setVariable(variable);
            declaration.setComment(FieldDeclaration.readComments(field));
            declaration.setType(GenericsDeclaration.of(field.getGenericType()));
            declaration.setField(field);
            return declaration;
        });
        if (template == null) {
            return null;
        }
        FieldDeclaration declaration = new FieldDeclaration();
        Object startingValue = object == null ? null : template.getField().get(object);
        declaration.setStartingValue(startingValue);
        declaration.setName(template.getName());
        declaration.setComment(template.getComment());
        declaration.setType(template.getType());
        declaration.setVariable(template.getVariable());
        declaration.setField(template.getField());
        declaration.setObject(object);
        return declaration;
    }

    private static String[] readComments(Field field) {
        Comments comments = field.getAnnotation(Comments.class);
        if (comments != null) {
            ArrayList<String> commentList = new ArrayList<String>();
            for (Comment comment : comments.value()) {
                commentList.addAll(Arrays.asList(comment.value()));
            }
            return commentList.toArray(new String[0]);
        }
        Comment comment = field.getAnnotation(Comment.class);
        if (comment != null) {
            return comment.value();
        }
        return null;
    }

    public void updateValue(Object value) throws OkaeriException {
        try {
            if (Modifier.isFinal(this.getField().getModifiers()) && FINAL_WARNS.add(this.getField().toString())) {
                LOGGER.warning(this.getField() + ": final fields (especially with default value) may prevent loading of the data. Removal of the final modifier is strongly advised.");
            }
            this.getField().setAccessible(true);
            this.getField().set(this.getObject(), value);
        }
        catch (IllegalAccessException exception) {
            throw new OkaeriException("failed to #updateValue", exception);
        }
    }

    public Object getValue() throws OkaeriException {
        if (this.isVariableHide()) {
            return this.getStartingValue();
        }
        try {
            this.getField().setAccessible(true);
            return this.getField().get(this.getObject());
        }
        catch (IllegalAccessException exception) {
            throw new OkaeriException("failed to #getValue", exception);
        }
    }

    public <T extends Annotation> Optional<T> getAnnotation(@NonNull Class<T> type) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        return Optional.ofNullable(this.getField().getAnnotation(type));
    }

    public SerdesContextAttachments readStaticAnnotations(@NonNull Configurer configurer) {
        if (configurer == null) {
            throw new NullPointerException("configurer is marked non-null but is null");
        }
        SerdesContextAttachments attachments = new SerdesContextAttachments();
        for (Annotation annotation : this.getField().getAnnotations()) {
            Optional<SerdesContextAttachment> attachmentOptional;
            SerdesAnnotationResolver<Annotation, SerdesContextAttachment> annotationResolver = configurer.getRegistry().getAnnotationResolver(annotation);
            if (annotationResolver == null || !(attachmentOptional = annotationResolver.resolveAttachment(this.getField(), annotation)).isPresent()) continue;
            SerdesContextAttachment attachment = attachmentOptional.get();
            Class<?> attachmentType = attachment.getClass();
            attachments.put(attachmentType, attachment);
        }
        return attachments;
    }

    public Object getStartingValue() {
        return this.startingValue;
    }

    public String getName() {
        return this.name;
    }

    public String[] getComment() {
        return this.comment;
    }

    public GenericsDeclaration getType() {
        return this.type;
    }

    public Variable getVariable() {
        return this.variable;
    }

    public boolean isVariableHide() {
        return this.variableHide;
    }

    public Field getField() {
        return this.field;
    }

    public Object getObject() {
        return this.object;
    }

    public void setStartingValue(Object startingValue) {
        this.startingValue = startingValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComment(String[] comment) {
        this.comment = comment;
    }

    public void setType(GenericsDeclaration type) {
        this.type = type;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public void setVariableHide(boolean variableHide) {
        this.variableHide = variableHide;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FieldDeclaration)) {
            return false;
        }
        FieldDeclaration other = (FieldDeclaration)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isVariableHide() != other.isVariableHide()) {
            return false;
        }
        Object this$startingValue = this.getStartingValue();
        Object other$startingValue = other.getStartingValue();
        if (this$startingValue == null ? other$startingValue != null : !this$startingValue.equals(other$startingValue)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        if (!Arrays.deepEquals(this.getComment(), other.getComment())) {
            return false;
        }
        GenericsDeclaration this$type = this.getType();
        GenericsDeclaration other$type = other.getType();
        if (this$type == null ? other$type != null : !((Object)this$type).equals(other$type)) {
            return false;
        }
        Variable this$variable = this.getVariable();
        Variable other$variable = other.getVariable();
        if (this$variable == null ? other$variable != null : !this$variable.equals(other$variable)) {
            return false;
        }
        Field this$field = this.getField();
        Field other$field = other.getField();
        if (this$field == null ? other$field != null : !((Object)this$field).equals(other$field)) {
            return false;
        }
        Object this$object = this.getObject();
        Object other$object = other.getObject();
        return !(this$object == null ? other$object != null : !this$object.equals(other$object));
    }

    protected boolean canEqual(Object other) {
        return other instanceof FieldDeclaration;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isVariableHide() ? 79 : 97);
        Object $startingValue = this.getStartingValue();
        result = result * 59 + ($startingValue == null ? 43 : $startingValue.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getComment());
        GenericsDeclaration $type = this.getType();
        result = result * 59 + ($type == null ? 43 : ((Object)$type).hashCode());
        Variable $variable = this.getVariable();
        result = result * 59 + ($variable == null ? 43 : $variable.hashCode());
        Field $field = this.getField();
        result = result * 59 + ($field == null ? 43 : ((Object)$field).hashCode());
        Object $object = this.getObject();
        result = result * 59 + ($object == null ? 43 : $object.hashCode());
        return result;
    }

    public String toString() {
        return "FieldDeclaration(startingValue=" + this.getStartingValue() + ", name=" + this.getName() + ", comment=" + Arrays.deepToString(this.getComment()) + ", type=" + this.getType() + ", variable=" + this.getVariable() + ", variableHide=" + this.isVariableHide() + ", field=" + this.getField() + ", object=" + this.getObject() + ")";
    }

    private static class CacheEntry {
        private final Class<?> type;
        private final String fieldName;

        public Class<?> getType() {
            return this.type;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof CacheEntry)) {
                return false;
            }
            CacheEntry other = (CacheEntry)o;
            if (!other.canEqual(this)) {
                return false;
            }
            Class<?> this$type = this.getType();
            Class<?> other$type = other.getType();
            if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
                return false;
            }
            String this$fieldName = this.getFieldName();
            String other$fieldName = other.getFieldName();
            return !(this$fieldName == null ? other$fieldName != null : !this$fieldName.equals(other$fieldName));
        }

        protected boolean canEqual(Object other) {
            return other instanceof CacheEntry;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Class<?> $type = this.getType();
            result = result * 59 + ($type == null ? 43 : $type.hashCode());
            String $fieldName = this.getFieldName();
            result = result * 59 + ($fieldName == null ? 43 : $fieldName.hashCode());
            return result;
        }

        public String toString() {
            return "FieldDeclaration.CacheEntry(type=" + this.getType() + ", fieldName=" + this.getFieldName() + ")";
        }

        public CacheEntry(Class<?> type, String fieldName) {
            this.type = type;
            this.fieldName = fieldName;
        }
    }
}

