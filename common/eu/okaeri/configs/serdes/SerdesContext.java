/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.serdes;

import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.schema.FieldDeclaration;
import eu.okaeri.configs.serdes.SerdesContextAttachment;
import eu.okaeri.configs.serdes.SerdesContextAttachments;
import java.lang.annotation.Annotation;
import java.util.Optional;
import lombok.NonNull;

public class SerdesContext {
    @NonNull
    private final Configurer configurer;
    private final FieldDeclaration field;
    private final SerdesContextAttachments attachments;

    public static SerdesContext of(@NonNull Configurer configurer) {
        if (configurer == null) {
            throw new NullPointerException("configurer is marked non-null but is null");
        }
        return SerdesContext.of(configurer, null, new SerdesContextAttachments());
    }

    public static SerdesContext of(@NonNull Configurer configurer, FieldDeclaration field) {
        if (configurer == null) {
            throw new NullPointerException("configurer is marked non-null but is null");
        }
        return SerdesContext.of(configurer, field, field == null ? new SerdesContextAttachments() : field.readStaticAnnotations(configurer));
    }

    public static SerdesContext of(@NonNull Configurer configurer, FieldDeclaration field, @NonNull SerdesContextAttachments attachments) {
        if (configurer == null) {
            throw new NullPointerException("configurer is marked non-null but is null");
        }
        if (attachments == null) {
            throw new NullPointerException("attachments is marked non-null but is null");
        }
        return new SerdesContext(configurer, field, attachments);
    }

    public static Builder builder() {
        return new Builder();
    }

    public <T extends Annotation> Optional<T> getConfigAnnotation(@NonNull Class<T> type) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        return this.getConfigurer().getParent() == null ? Optional.empty() : Optional.ofNullable(this.getConfigurer().getParent().getClass().getAnnotation(type));
    }

    public <T extends Annotation> Optional<T> getFieldAnnotation(@NonNull Class<T> type) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        return this.getField() == null ? Optional.empty() : this.getField().getAnnotation(type);
    }

    public <T extends SerdesContextAttachment> Optional<T> getAttachment(Class<T> type) {
        SerdesContextAttachment attachment = (SerdesContextAttachment)this.attachments.get(type);
        return Optional.ofNullable(attachment);
    }

    public <T extends SerdesContextAttachment> T getAttachment(Class<T> type, T defaultValue) {
        return (T)((SerdesContextAttachment)this.getAttachment(type).orElse(defaultValue));
    }

    @NonNull
    public Configurer getConfigurer() {
        return this.configurer;
    }

    public FieldDeclaration getField() {
        return this.field;
    }

    public SerdesContextAttachments getAttachments() {
        return this.attachments;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SerdesContext)) {
            return false;
        }
        SerdesContext other = (SerdesContext)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Configurer this$configurer = this.getConfigurer();
        Configurer other$configurer = other.getConfigurer();
        if (this$configurer == null ? other$configurer != null : !this$configurer.equals(other$configurer)) {
            return false;
        }
        FieldDeclaration this$field = this.getField();
        FieldDeclaration other$field = other.getField();
        if (this$field == null ? other$field != null : !((Object)this$field).equals(other$field)) {
            return false;
        }
        SerdesContextAttachments this$attachments = this.getAttachments();
        SerdesContextAttachments other$attachments = other.getAttachments();
        return !(this$attachments == null ? other$attachments != null : !((Object)this$attachments).equals(other$attachments));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SerdesContext;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Configurer $configurer = this.getConfigurer();
        result = result * 59 + ($configurer == null ? 43 : $configurer.hashCode());
        FieldDeclaration $field = this.getField();
        result = result * 59 + ($field == null ? 43 : ((Object)$field).hashCode());
        SerdesContextAttachments $attachments = this.getAttachments();
        result = result * 59 + ($attachments == null ? 43 : ((Object)$attachments).hashCode());
        return result;
    }

    public String toString() {
        return "SerdesContext(configurer=" + this.getConfigurer() + ", field=" + this.getField() + ", attachments=" + this.getAttachments() + ")";
    }

    private SerdesContext(@NonNull Configurer configurer, FieldDeclaration field, SerdesContextAttachments attachments) {
        if (configurer == null) {
            throw new NullPointerException("configurer is marked non-null but is null");
        }
        this.configurer = configurer;
        this.field = field;
        this.attachments = attachments;
    }

    private static class Builder {
        private final SerdesContextAttachments attachments = new SerdesContextAttachments();
        private Configurer configurer;
        private FieldDeclaration field;

        private Builder() {
        }

        public void configurer(Configurer configurer) {
            this.configurer = configurer;
        }

        public void field(FieldDeclaration field) {
            this.field = field;
        }

        public <A extends SerdesContextAttachment> Builder attach(Class<A> type, A attachment) {
            if (this.attachments.containsKey(type)) {
                throw new IllegalArgumentException("cannot override SerdesContext attachment of type " + type);
            }
            this.attachments.put(type, attachment);
            return this;
        }

        public SerdesContext create() {
            return SerdesContext.of(this.configurer, this.field, this.attachments);
        }
    }
}

