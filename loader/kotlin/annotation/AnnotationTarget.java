/*
 * Decompiled with CFR 0.150.
 */
package kotlin.annotation;

import kotlin.Metadata;
import kotlin.SinceKotlin;

@Metadata(mv={1, 4, 0}, bv={1, 0, 3}, k=1, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0011\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011\u00a8\u0006\u0012"}, d2={"Lkotlin/annotation/AnnotationTarget;", "", "(Ljava/lang/String;I)V", "CLASS", "ANNOTATION_CLASS", "TYPE_PARAMETER", "PROPERTY", "FIELD", "LOCAL_VARIABLE", "VALUE_PARAMETER", "CONSTRUCTOR", "FUNCTION", "PROPERTY_GETTER", "PROPERTY_SETTER", "TYPE", "EXPRESSION", "FILE", "TYPEALIAS", "kotlin-stdlib"})
public final class AnnotationTarget
extends Enum<AnnotationTarget> {
    public static final /* enum */ AnnotationTarget CLASS;
    public static final /* enum */ AnnotationTarget ANNOTATION_CLASS;
    public static final /* enum */ AnnotationTarget TYPE_PARAMETER;
    public static final /* enum */ AnnotationTarget PROPERTY;
    public static final /* enum */ AnnotationTarget FIELD;
    public static final /* enum */ AnnotationTarget LOCAL_VARIABLE;
    public static final /* enum */ AnnotationTarget VALUE_PARAMETER;
    public static final /* enum */ AnnotationTarget CONSTRUCTOR;
    public static final /* enum */ AnnotationTarget FUNCTION;
    public static final /* enum */ AnnotationTarget PROPERTY_GETTER;
    public static final /* enum */ AnnotationTarget PROPERTY_SETTER;
    public static final /* enum */ AnnotationTarget TYPE;
    public static final /* enum */ AnnotationTarget EXPRESSION;
    public static final /* enum */ AnnotationTarget FILE;
    @SinceKotlin(version="1.1")
    public static final /* enum */ AnnotationTarget TYPEALIAS;
    private static final /* synthetic */ AnnotationTarget[] $VALUES;

    static {
        AnnotationTarget[] arrannotationTarget = new AnnotationTarget[15];
        AnnotationTarget[] arrannotationTarget2 = arrannotationTarget;
        arrannotationTarget[0] = CLASS = new AnnotationTarget();
        arrannotationTarget[1] = ANNOTATION_CLASS = new AnnotationTarget();
        arrannotationTarget[2] = TYPE_PARAMETER = new AnnotationTarget();
        arrannotationTarget[3] = PROPERTY = new AnnotationTarget();
        arrannotationTarget[4] = FIELD = new AnnotationTarget();
        arrannotationTarget[5] = LOCAL_VARIABLE = new AnnotationTarget();
        arrannotationTarget[6] = VALUE_PARAMETER = new AnnotationTarget();
        arrannotationTarget[7] = CONSTRUCTOR = new AnnotationTarget();
        arrannotationTarget[8] = FUNCTION = new AnnotationTarget();
        arrannotationTarget[9] = PROPERTY_GETTER = new AnnotationTarget();
        arrannotationTarget[10] = PROPERTY_SETTER = new AnnotationTarget();
        arrannotationTarget[11] = TYPE = new AnnotationTarget();
        arrannotationTarget[12] = EXPRESSION = new AnnotationTarget();
        arrannotationTarget[13] = FILE = new AnnotationTarget();
        arrannotationTarget[14] = TYPEALIAS = new AnnotationTarget();
        $VALUES = arrannotationTarget;
    }

    public static AnnotationTarget[] values() {
        return (AnnotationTarget[])$VALUES.clone();
    }

    public static AnnotationTarget valueOf(String string) {
        return Enum.valueOf(AnnotationTarget.class, string);
    }
}

