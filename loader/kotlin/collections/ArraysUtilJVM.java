/*
 * Decompiled with CFR 0.150.
 */
package kotlin.collections;

import java.util.Arrays;
import java.util.List;

class ArraysUtilJVM {
    ArraysUtilJVM() {
    }

    static <T> List<T> asList(T[] array) {
        return Arrays.asList(array);
    }
}

