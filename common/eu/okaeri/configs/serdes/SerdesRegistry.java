/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package eu.okaeri.configs.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.ObjectTransformer;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesAnnotationResolver;
import eu.okaeri.configs.serdes.SerdesContext;
import eu.okaeri.configs.serdes.SerdesContextAttachment;
import eu.okaeri.configs.serdes.standard.ObjectToStringTransformer;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.NonNull;

public class SerdesRegistry {
    private final Map<Class<? extends Annotation>, SerdesAnnotationResolver<Annotation, SerdesContextAttachment>> annotationResolverMap = new ConcurrentHashMap<Class<? extends Annotation>, SerdesAnnotationResolver<Annotation, SerdesContextAttachment>>();
    private final Set<ObjectSerializer> serializerSet = ConcurrentHashMap.newKeySet();
    private final Map<GenericsPair, ObjectTransformer> transformerMap = new ConcurrentHashMap<GenericsPair, ObjectTransformer>();

    public void register(@NonNull ObjectTransformer transformer) {
        if (transformer == null) {
            throw new NullPointerException("transformer is marked non-null but is null");
        }
        this.transformerMap.put(transformer.getPair(), transformer);
    }

    public void register(@NonNull OkaeriSerdesPack serdesPack) {
        if (serdesPack == null) {
            throw new NullPointerException("serdesPack is marked non-null but is null");
        }
        serdesPack.register(this);
    }

    public <L, R> void register(final @NonNull BidirectionalTransformer<L, R> transformer) {
        if (transformer == null) {
            throw new NullPointerException("transformer is marked non-null but is null");
        }
        this.register(new ObjectTransformer<L, R>(){

            @Override
            public GenericsPair<L, R> getPair() {
                return transformer.getPair();
            }

            @Override
            public R transform(@NonNull L data, @NonNull SerdesContext serdesContext) {
                if (data == null) {
                    throw new NullPointerException("data is marked non-null but is null");
                }
                if (serdesContext == null) {
                    throw new NullPointerException("serdesContext is marked non-null but is null");
                }
                return transformer.leftToRight(data, serdesContext);
            }
        });
        this.register(new ObjectTransformer<R, L>(){

            @Override
            public GenericsPair<R, L> getPair() {
                return transformer.getPair().reverse();
            }

            @Override
            public L transform(@NonNull R data, @NonNull SerdesContext serdesContext) {
                if (data == null) {
                    throw new NullPointerException("data is marked non-null but is null");
                }
                if (serdesContext == null) {
                    throw new NullPointerException("serdesContext is marked non-null but is null");
                }
                return transformer.rightToLeft(data, serdesContext);
            }
        });
    }

    public void registerWithReversedToString(@NonNull ObjectTransformer transformer) {
        if (transformer == null) {
            throw new NullPointerException("transformer is marked non-null but is null");
        }
        this.transformerMap.put(transformer.getPair(), transformer);
        this.transformerMap.put(transformer.getPair().reverse(), new ObjectToStringTransformer());
    }

    public void register(@NonNull ObjectSerializer serializer) {
        if (serializer == null) {
            throw new NullPointerException("serializer is marked non-null but is null");
        }
        this.serializerSet.add(serializer);
    }

    public void registerExclusive(@NonNull Class<?> type, @NonNull ObjectSerializer serializer) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        if (serializer == null) {
            throw new NullPointerException("serializer is marked non-null but is null");
        }
        this.serializerSet.removeIf(ser -> ser.supports(type));
        this.serializerSet.add(serializer);
    }

    public ObjectTransformer getTransformer(@NonNull GenericsDeclaration from, @NonNull GenericsDeclaration to) {
        if (from == null) {
            throw new NullPointerException("from is marked non-null but is null");
        }
        if (to == null) {
            throw new NullPointerException("to is marked non-null but is null");
        }
        GenericsPair pair = new GenericsPair(from, to);
        return this.transformerMap.get(pair);
    }

    public List<ObjectTransformer> getTransformersFrom(@NonNull GenericsDeclaration from) {
        if (from == null) {
            throw new NullPointerException("from is marked non-null but is null");
        }
        return this.transformerMap.entrySet().stream().filter(entry -> from.equals(((GenericsPair)entry.getKey()).getFrom())).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public List<ObjectTransformer> getTransformersTo(@NonNull GenericsDeclaration to) {
        if (to == null) {
            throw new NullPointerException("to is marked non-null but is null");
        }
        return this.transformerMap.entrySet().stream().filter(entry -> to.equals(((GenericsPair)entry.getKey()).getTo())).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public boolean canTransform(@NonNull GenericsDeclaration from, @NonNull GenericsDeclaration to) {
        if (from == null) {
            throw new NullPointerException("from is marked non-null but is null");
        }
        if (to == null) {
            throw new NullPointerException("to is marked non-null but is null");
        }
        return this.getTransformer(from, to) != null;
    }

    public ObjectSerializer getSerializer(@NonNull Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz is marked non-null but is null");
        }
        return this.serializerSet.stream().filter(serializer -> serializer.supports(clazz)).findFirst().orElse(null);
    }

    public void register(@NonNull SerdesAnnotationResolver<? extends Annotation, ? extends SerdesContextAttachment> annotationResolver) {
        if (annotationResolver == null) {
            throw new NullPointerException("annotationResolver is marked non-null but is null");
        }
        this.annotationResolverMap.put(annotationResolver.getAnnotationType(), annotationResolver);
    }

    public SerdesAnnotationResolver<Annotation, SerdesContextAttachment> getAnnotationResolver(@NonNull Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            throw new NullPointerException("annotationType is marked non-null but is null");
        }
        return this.annotationResolverMap.get(annotationType);
    }

    public SerdesAnnotationResolver<Annotation, SerdesContextAttachment> getAnnotationResolver(@NonNull Annotation annotation) {
        if (annotation == null) {
            throw new NullPointerException("annotation is marked non-null but is null");
        }
        return this.annotationResolverMap.get(annotation.annotationType());
    }

    public OkaeriSerdesPack allSerdes() {
        return registry -> {
            this.transformerMap.values().forEach(registry::register);
            this.serializerSet.forEach(registry::register);
            this.annotationResolverMap.values().forEach(registry::register);
        };
    }
}

