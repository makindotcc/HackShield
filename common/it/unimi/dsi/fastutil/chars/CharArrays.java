/*
 * Decompiled with CFR 0.150.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public final class CharArrays {
    public static final char[] EMPTY_ARRAY = new char[0];
    public static final char[] DEFAULT_EMPTY_ARRAY = new char[0];
    private static final int QUICKSORT_NO_REC = 16;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int QUICKSORT_MEDIAN_OF_9 = 128;
    private static final int MERGESORT_NO_REC = 16;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 2;
    private static final int RADIXSORT_NO_REC = 1024;
    private static final int RADIXSORT_NO_REC_SMALL = 64;
    private static final int PARALLEL_RADIXSORT_NO_FORK = 1024;
    static final int RADIX_SORT_MIN_THRESHOLD = 2000;
    protected static final Segment POISON_PILL = new Segment(-1, -1, -1);
    public static final Hash.Strategy<char[]> HASH_STRATEGY = new ArrayHashStrategy();

    private CharArrays() {
    }

    public static char[] forceCapacity(char[] array, int length, int preserve) {
        char[] t = new char[length];
        System.arraycopy(array, 0, t, 0, preserve);
        return t;
    }

    public static char[] ensureCapacity(char[] array, int length) {
        return CharArrays.ensureCapacity(array, length, array.length);
    }

    public static char[] ensureCapacity(char[] array, int length, int preserve) {
        return length > array.length ? CharArrays.forceCapacity(array, length, preserve) : array;
    }

    public static char[] grow(char[] array, int length) {
        return CharArrays.grow(array, length, array.length);
    }

    public static char[] grow(char[] array, int length, int preserve) {
        if (length > array.length) {
            int newLength = (int)Math.max(Math.min((long)array.length + (long)(array.length >> 1), 0x7FFFFFF7L), (long)length);
            char[] t = new char[newLength];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }

    public static char[] trim(char[] array, int length) {
        if (length >= array.length) {
            return array;
        }
        char[] t = length == 0 ? EMPTY_ARRAY : new char[length];
        System.arraycopy(array, 0, t, 0, length);
        return t;
    }

    public static char[] setLength(char[] array, int length) {
        if (length == array.length) {
            return array;
        }
        if (length < array.length) {
            return CharArrays.trim(array, length);
        }
        return CharArrays.ensureCapacity(array, length);
    }

    public static char[] copy(char[] array, int offset, int length) {
        CharArrays.ensureOffsetLength(array, offset, length);
        char[] a = length == 0 ? EMPTY_ARRAY : new char[length];
        System.arraycopy(array, offset, a, 0, length);
        return a;
    }

    public static char[] copy(char[] array) {
        return (char[])array.clone();
    }

    @Deprecated
    public static void fill(char[] array, char value) {
        int i = array.length;
        while (i-- != 0) {
            array[i] = value;
        }
    }

    @Deprecated
    public static void fill(char[] array, int from, int to, char value) {
        CharArrays.ensureFromTo(array, from, to);
        if (from == 0) {
            while (to-- != 0) {
                array[to] = value;
            }
        } else {
            for (int i = from; i < to; ++i) {
                array[i] = value;
            }
        }
    }

    @Deprecated
    public static boolean equals(char[] a1, char[] a2) {
        int i = a1.length;
        if (i != a2.length) {
            return false;
        }
        while (i-- != 0) {
            if (a1[i] == a2[i]) continue;
            return false;
        }
        return true;
    }

    public static void ensureFromTo(char[] a, int from, int to) {
        Arrays.ensureFromTo(a.length, from, to);
    }

    public static void ensureOffsetLength(char[] a, int offset, int length) {
        Arrays.ensureOffsetLength(a.length, offset, length);
    }

    public static void ensureSameLength(char[] a, char[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch: " + a.length + " != " + b.length);
        }
    }

    private static ForkJoinPool getPool() {
        ForkJoinPool current = ForkJoinTask.getPool();
        return current == null ? ForkJoinPool.commonPool() : current;
    }

    public static void swap(char[] x, int a, int b) {
        char t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    public static void swap(char[] x, int a, int b, int n) {
        int i = 0;
        while (i < n) {
            CharArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static int med3(char[] x, int a, int b, int c, CharComparator comp) {
        int ab = comp.compare(x[a], x[b]);
        int ac = comp.compare(x[a], x[c]);
        int bc = comp.compare(x[b], x[c]);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(char[] a, int from, int to, CharComparator comp) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (comp.compare(a[j], a[m]) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            char u = a[i];
            a[i] = a[m];
            a[m] = u;
        }
    }

    private static void insertionSort(char[] a, int from, int to, CharComparator comp) {
        int i = from;
        while (++i < to) {
            char t = a[i];
            int j = i;
            char u = a[j - 1];
            while (comp.compare(t, u) < 0) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = a[--j - 1];
            }
            a[j] = t;
        }
    }

    public static void quickSort(char[] x, int from, int to, CharComparator comp) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            CharArrays.selectionSort(x, from, to, comp);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = CharArrays.med3(x, l, l + s, l + 2 * s, comp);
            m = CharArrays.med3(x, m - s, m, m + s, comp);
            n = CharArrays.med3(x, n - 2 * s, n - s, n, comp);
        }
        m = CharArrays.med3(x, l, m, n, comp);
        char v = x[m];
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = comp.compare(x[b], v)) <= 0) {
                if (comparison == 0) {
                    CharArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(x[c], v)) >= 0) {
                if (comparison == 0) {
                    CharArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            CharArrays.swap(x, b++, c--);
        }
        int s = Math.min(a - from, b - a);
        CharArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        CharArrays.swap(x, b, to - s, s);
        s = b - a;
        if (s > 1) {
            CharArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1) {
            CharArrays.quickSort(x, to - s, to, comp);
        }
    }

    public static void quickSort(char[] x, CharComparator comp) {
        CharArrays.quickSort(x, 0, x.length, comp);
    }

    public static void parallelQuickSort(char[] x, int from, int to, CharComparator comp) {
        ForkJoinPool pool = CharArrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            CharArrays.quickSort(x, from, to, comp);
        } else {
            pool.invoke(new ForkJoinQuickSortComp(x, from, to, comp));
        }
    }

    public static void parallelQuickSort(char[] x, CharComparator comp) {
        CharArrays.parallelQuickSort(x, 0, x.length, comp);
    }

    private static int med3(char[] x, int a, int b, int c) {
        int ab = Character.compare(x[a], x[b]);
        int ac = Character.compare(x[a], x[c]);
        int bc = Character.compare(x[b], x[c]);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(char[] a, int from, int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (a[j] >= a[m]) continue;
                m = j;
            }
            if (m == i) continue;
            char u = a[i];
            a[i] = a[m];
            a[m] = u;
        }
    }

    private static void insertionSort(char[] a, int from, int to) {
        int i = from;
        while (++i < to) {
            char t = a[i];
            int j = i;
            char u = a[j - 1];
            while (t < u) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = a[--j - 1];
            }
            a[j] = t;
        }
    }

    public static void quickSort(char[] x, int from, int to) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            CharArrays.selectionSort(x, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = CharArrays.med3(x, l, l + s, l + 2 * s);
            m = CharArrays.med3(x, m - s, m, m + s);
            n = CharArrays.med3(x, n - 2 * s, n - s, n);
        }
        m = CharArrays.med3(x, l, m, n);
        char v = x[m];
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = Character.compare(x[b], v)) <= 0) {
                if (comparison == 0) {
                    CharArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Character.compare(x[c], v)) >= 0) {
                if (comparison == 0) {
                    CharArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            CharArrays.swap(x, b++, c--);
        }
        int s = Math.min(a - from, b - a);
        CharArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        CharArrays.swap(x, b, to - s, s);
        s = b - a;
        if (s > 1) {
            CharArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1) {
            CharArrays.quickSort(x, to - s, to);
        }
    }

    public static void quickSort(char[] x) {
        CharArrays.quickSort(x, 0, x.length);
    }

    public static void parallelQuickSort(char[] x, int from, int to) {
        ForkJoinPool pool = CharArrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            CharArrays.quickSort(x, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSort(x, from, to));
        }
    }

    public static void parallelQuickSort(char[] x) {
        CharArrays.parallelQuickSort(x, 0, x.length);
    }

    private static int med3Indirect(int[] perm, char[] x, int a, int b, int c) {
        char aa = x[perm[a]];
        char bb = x[perm[b]];
        char cc = x[perm[c]];
        int ab = Character.compare(aa, bb);
        int ac = Character.compare(aa, cc);
        int bc = Character.compare(bb, cc);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void insertionSortIndirect(int[] perm, char[] a, int from, int to) {
        int i = from;
        while (++i < to) {
            int t = perm[i];
            int j = i;
            int u = perm[j - 1];
            while (a[t] < a[u]) {
                perm[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = perm[--j - 1];
            }
            perm[j] = t;
        }
    }

    public static void quickSortIndirect(int[] perm, char[] x, int from, int to) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            CharArrays.insertionSortIndirect(perm, x, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = CharArrays.med3Indirect(perm, x, l, l + s, l + 2 * s);
            m = CharArrays.med3Indirect(perm, x, m - s, m, m + s);
            n = CharArrays.med3Indirect(perm, x, n - 2 * s, n - s, n);
        }
        m = CharArrays.med3Indirect(perm, x, l, m, n);
        char v = x[perm[m]];
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = Character.compare(x[perm[b]], v)) <= 0) {
                if (comparison == 0) {
                    IntArrays.swap(perm, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Character.compare(x[perm[c]], v)) >= 0) {
                if (comparison == 0) {
                    IntArrays.swap(perm, c, d--);
                }
                --c;
            }
            if (b > c) break;
            IntArrays.swap(perm, b++, c--);
        }
        int s = Math.min(a - from, b - a);
        IntArrays.swap(perm, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        IntArrays.swap(perm, b, to - s, s);
        s = b - a;
        if (s > 1) {
            CharArrays.quickSortIndirect(perm, x, from, from + s);
        }
        if ((s = d - c) > 1) {
            CharArrays.quickSortIndirect(perm, x, to - s, to);
        }
    }

    public static void quickSortIndirect(int[] perm, char[] x) {
        CharArrays.quickSortIndirect(perm, x, 0, x.length);
    }

    public static void parallelQuickSortIndirect(int[] perm, char[] x, int from, int to) {
        ForkJoinPool pool = CharArrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            CharArrays.quickSortIndirect(perm, x, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSortIndirect(perm, x, from, to));
        }
    }

    public static void parallelQuickSortIndirect(int[] perm, char[] x) {
        CharArrays.parallelQuickSortIndirect(perm, x, 0, x.length);
    }

    public static void stabilize(int[] perm, char[] x, int from, int to) {
        int curr = from;
        for (int i = from + 1; i < to; ++i) {
            if (x[perm[i]] == x[perm[curr]]) continue;
            if (i - curr > 1) {
                IntArrays.parallelQuickSort(perm, curr, i);
            }
            curr = i;
        }
        if (to - curr > 1) {
            IntArrays.parallelQuickSort(perm, curr, to);
        }
    }

    public static void stabilize(int[] perm, char[] x) {
        CharArrays.stabilize(perm, x, 0, perm.length);
    }

    private static int med3(char[] x, char[] y, int a, int b, int c) {
        int bc;
        int t = Character.compare(x[a], x[b]);
        int ab = t == 0 ? Character.compare(y[a], y[b]) : t;
        t = Character.compare(x[a], x[c]);
        int ac = t == 0 ? Character.compare(y[a], y[c]) : t;
        t = Character.compare(x[b], x[c]);
        int n = bc = t == 0 ? Character.compare(y[b], y[c]) : t;
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void swap(char[] x, char[] y, int a, int b) {
        char t = x[a];
        char u = y[a];
        x[a] = x[b];
        y[a] = y[b];
        x[b] = t;
        y[b] = u;
    }

    private static void swap(char[] x, char[] y, int a, int b, int n) {
        int i = 0;
        while (i < n) {
            CharArrays.swap(x, y, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static void selectionSort(char[] a, char[] b, int from, int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                int u = Character.compare(a[j], a[m]);
                if (u >= 0 && (u != 0 || b[j] >= b[m])) continue;
                m = j;
            }
            if (m == i) continue;
            char t = a[i];
            a[i] = a[m];
            a[m] = t;
            t = b[i];
            b[i] = b[m];
            b[m] = t;
        }
    }

    public static void quickSort(char[] x, char[] y, int from, int to) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            CharArrays.selectionSort(x, y, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = CharArrays.med3(x, y, l, l + s, l + 2 * s);
            m = CharArrays.med3(x, y, m - s, m, m + s);
            n = CharArrays.med3(x, y, n - 2 * s, n - s, n);
        }
        m = CharArrays.med3(x, y, l, m, n);
        char v = x[m];
        char w = y[m];
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int t;
            int comparison;
            if (b <= c && (comparison = (t = Character.compare(x[b], v)) == 0 ? Character.compare(y[b], w) : t) <= 0) {
                if (comparison == 0) {
                    CharArrays.swap(x, y, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = (t = Character.compare(x[c], v)) == 0 ? Character.compare(y[c], w) : t) >= 0) {
                if (comparison == 0) {
                    CharArrays.swap(x, y, c, d--);
                }
                --c;
            }
            if (b > c) break;
            CharArrays.swap(x, y, b++, c--);
        }
        int s = Math.min(a - from, b - a);
        CharArrays.swap(x, y, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        CharArrays.swap(x, y, b, to - s, s);
        s = b - a;
        if (s > 1) {
            CharArrays.quickSort(x, y, from, from + s);
        }
        if ((s = d - c) > 1) {
            CharArrays.quickSort(x, y, to - s, to);
        }
    }

    public static void quickSort(char[] x, char[] y) {
        CharArrays.ensureSameLength(x, y);
        CharArrays.quickSort(x, y, 0, x.length);
    }

    public static void parallelQuickSort(char[] x, char[] y, int from, int to) {
        ForkJoinPool pool = CharArrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            CharArrays.quickSort(x, y, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSort2(x, y, from, to));
        }
    }

    public static void parallelQuickSort(char[] x, char[] y) {
        CharArrays.ensureSameLength(x, y);
        CharArrays.parallelQuickSort(x, y, 0, x.length);
    }

    public static void unstableSort(char[] a, int from, int to) {
        if (to - from >= 2000) {
            CharArrays.radixSort(a, from, to);
        } else {
            CharArrays.quickSort(a, from, to);
        }
    }

    public static void unstableSort(char[] a) {
        CharArrays.unstableSort(a, 0, a.length);
    }

    public static void unstableSort(char[] a, int from, int to, CharComparator comp) {
        CharArrays.quickSort(a, from, to, comp);
    }

    public static void unstableSort(char[] a, CharComparator comp) {
        CharArrays.unstableSort(a, 0, a.length, comp);
    }

    public static void mergeSort(char[] a, int from, int to, char[] supp) {
        int len = to - from;
        if (len < 16) {
            CharArrays.insertionSort(a, from, to);
            return;
        }
        if (supp == null) {
            supp = java.util.Arrays.copyOf(a, to);
        }
        int mid = from + to >>> 1;
        CharArrays.mergeSort(supp, from, mid, a);
        CharArrays.mergeSort(supp, mid, to, a);
        if (supp[mid - 1] <= supp[mid]) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int p = from;
        int q = mid;
        for (int i = from; i < to; ++i) {
            a[i] = q >= to || p < mid && supp[p] <= supp[q] ? supp[p++] : supp[q++];
        }
    }

    public static void mergeSort(char[] a, int from, int to) {
        CharArrays.mergeSort(a, from, to, (char[])null);
    }

    public static void mergeSort(char[] a) {
        CharArrays.mergeSort(a, 0, a.length);
    }

    public static void mergeSort(char[] a, int from, int to, CharComparator comp, char[] supp) {
        int len = to - from;
        if (len < 16) {
            CharArrays.insertionSort(a, from, to, comp);
            return;
        }
        if (supp == null) {
            supp = java.util.Arrays.copyOf(a, to);
        }
        int mid = from + to >>> 1;
        CharArrays.mergeSort(supp, from, mid, comp, a);
        CharArrays.mergeSort(supp, mid, to, comp, a);
        if (comp.compare(supp[mid - 1], supp[mid]) <= 0) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int p = from;
        int q = mid;
        for (int i = from; i < to; ++i) {
            a[i] = q >= to || p < mid && comp.compare(supp[p], supp[q]) <= 0 ? supp[p++] : supp[q++];
        }
    }

    public static void mergeSort(char[] a, int from, int to, CharComparator comp) {
        CharArrays.mergeSort(a, from, to, comp, null);
    }

    public static void mergeSort(char[] a, CharComparator comp) {
        CharArrays.mergeSort(a, 0, a.length, comp);
    }

    public static void stableSort(char[] a, int from, int to) {
        CharArrays.unstableSort(a, from, to);
    }

    public static void stableSort(char[] a) {
        CharArrays.stableSort(a, 0, a.length);
    }

    public static void stableSort(char[] a, int from, int to, CharComparator comp) {
        CharArrays.mergeSort(a, from, to, comp);
    }

    public static void stableSort(char[] a, CharComparator comp) {
        CharArrays.stableSort(a, 0, a.length, comp);
    }

    public static int binarySearch(char[] a, int from, int to, char key) {
        --to;
        while (from <= to) {
            int mid = from + to >>> 1;
            char midVal = a[mid];
            if (midVal < key) {
                from = mid + 1;
                continue;
            }
            if (midVal > key) {
                to = mid - 1;
                continue;
            }
            return mid;
        }
        return -(from + 1);
    }

    public static int binarySearch(char[] a, char key) {
        return CharArrays.binarySearch(a, 0, a.length, key);
    }

    public static int binarySearch(char[] a, int from, int to, char key, CharComparator c) {
        --to;
        while (from <= to) {
            int mid = from + to >>> 1;
            char midVal = a[mid];
            int cmp = c.compare(midVal, key);
            if (cmp < 0) {
                from = mid + 1;
                continue;
            }
            if (cmp > 0) {
                to = mid - 1;
                continue;
            }
            return mid;
        }
        return -(from + 1);
    }

    public static int binarySearch(char[] a, char key, CharComparator c) {
        return CharArrays.binarySearch(a, 0, a.length, key, c);
    }

    public static void radixSort(char[] a) {
        CharArrays.radixSort(a, 0, a.length);
    }

    public static void radixSort(char[] a, int from, int to) {
        if (to - from < 1024) {
            CharArrays.quickSort(a, from, to);
            return;
        }
        boolean maxLevel = true;
        int stackSize = 256;
        int stackPos = 0;
        int[] offsetStack = new int[256];
        int[] lengthStack = new int[256];
        int[] levelStack = new int[256];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            boolean signMask = false;
            int shift = (1 - level % 2) * 8;
            int i = first + length;
            while (i-- != first) {
                int n = a[i] >>> shift & 0xFF ^ 0;
                count[n] = count[n] + 1;
            }
            int lastUsed = -1;
            int p = first;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0) {
                    lastUsed = i2;
                }
                pos[i2] = p += count[i2];
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                char t = a[i3];
                c = t >>> shift & 0xFF ^ 0;
                if (i3 < end) {
                    while (true) {
                        int n = c;
                        int n2 = pos[n] - 1;
                        pos[n] = n2;
                        int d = n2;
                        if (n2 <= i3) break;
                        char z = t;
                        t = a[d];
                        a[d] = z;
                        c = t >>> shift & 0xFF ^ 0;
                    }
                    a[i3] = t;
                }
                if (level < 1 && count[c] > 1) {
                    if (count[c] < 1024) {
                        CharArrays.quickSort(a, i3, i3 + count[c]);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    public static void parallelRadixSort(char[] a, int from, int to) {
        ForkJoinPool pool = CharArrays.getPool();
        if (to - from < 1024 || pool.getParallelism() == 1) {
            CharArrays.quickSort(a, from, to);
            return;
        }
        boolean maxLevel = true;
        LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<Segment>();
        queue.add(new Segment(from, to - from, 0));
        AtomicInteger queueSize = new AtomicInteger(1);
        int numberOfThreads = pool.getParallelism();
        ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<Void>(pool);
        int j = numberOfThreads;
        while (j-- != 0) {
            executorCompletionService.submit(() -> {
                int[] count = new int[256];
                int[] pos = new int[256];
                while (true) {
                    Segment segment;
                    if (queueSize.get() == 0) {
                        int i = numberOfThreads;
                        while (i-- != 0) {
                            queue.add(POISON_PILL);
                        }
                    }
                    if ((segment = (Segment)queue.take()) == POISON_PILL) {
                        return null;
                    }
                    int first = segment.offset;
                    int length = segment.length;
                    int level = segment.level;
                    boolean signMask = false;
                    int shift = (1 - level % 2) * 8;
                    int i = first + length;
                    while (i-- != first) {
                        int n = a[i] >>> shift & 0xFF ^ 0;
                        count[n] = count[n] + 1;
                    }
                    int lastUsed = -1;
                    int p = first;
                    for (int i2 = 0; i2 < 256; ++i2) {
                        if (count[i2] != 0) {
                            lastUsed = i2;
                        }
                        pos[i2] = p += count[i2];
                    }
                    int end = first + length - count[lastUsed];
                    int c = -1;
                    for (int i3 = first; i3 <= end; i3 += count[c]) {
                        char t = a[i3];
                        c = t >>> shift & 0xFF ^ 0;
                        if (i3 < end) {
                            while (true) {
                                int n = c;
                                int n2 = pos[n] - 1;
                                pos[n] = n2;
                                int d = n2;
                                if (n2 <= i3) break;
                                char z = t;
                                t = a[d];
                                a[d] = z;
                                c = t >>> shift & 0xFF ^ 0;
                            }
                            a[i3] = t;
                        }
                        if (level < 1 && count[c] > 1) {
                            if (count[c] < 1024) {
                                CharArrays.quickSort(a, i3, i3 + count[c]);
                            } else {
                                queueSize.incrementAndGet();
                                queue.add(new Segment(i3, count[c], level + 1));
                            }
                        }
                        count[c] = 0;
                    }
                    queueSize.decrementAndGet();
                }
            });
        }
        Throwable problem = null;
        int i = numberOfThreads;
        while (i-- != 0) {
            try {
                executorCompletionService.take().get();
            }
            catch (Exception e) {
                problem = e.getCause();
            }
        }
        if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
        }
    }

    public static void parallelRadixSort(char[] a) {
        CharArrays.parallelRadixSort(a, 0, a.length);
    }

    public static void radixSortIndirect(int[] perm, char[] a, boolean stable) {
        CharArrays.radixSortIndirect(perm, a, 0, perm.length, stable);
    }

    public static void radixSortIndirect(int[] perm, char[] a, int from, int to, boolean stable) {
        int[] support;
        if (to - from < 1024) {
            CharArrays.quickSortIndirect(perm, a, from, to);
            if (stable) {
                CharArrays.stabilize(perm, a, from, to);
            }
            return;
        }
        boolean maxLevel = true;
        int stackSize = 256;
        int stackPos = 0;
        int[] offsetStack = new int[256];
        int[] lengthStack = new int[256];
        int[] levelStack = new int[256];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        int[] arrn = support = stable ? new int[perm.length] : null;
        while (stackPos > 0) {
            int i;
            int p;
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            boolean signMask = false;
            int shift = (1 - level % 2) * 8;
            int i2 = first + length;
            while (i2-- != first) {
                int n = a[perm[i2]] >>> shift & 0xFF ^ 0;
                count[n] = count[n] + 1;
            }
            int lastUsed = -1;
            int n = p = stable ? 0 : first;
            for (i = 0; i < 256; ++i) {
                if (count[i] != 0) {
                    lastUsed = i;
                }
                pos[i] = p += count[i];
            }
            if (stable) {
                i = first + length;
                while (i-- != first) {
                    int n2 = a[perm[i]] >>> shift & 0xFF ^ 0;
                    int n3 = pos[n2] - 1;
                    pos[n2] = n3;
                    support[n3] = perm[i];
                }
                System.arraycopy(support, 0, perm, first, length);
                p = first;
                for (i = 0; i <= lastUsed; ++i) {
                    if (level < 1 && count[i] > 1) {
                        if (count[i] < 1024) {
                            CharArrays.quickSortIndirect(perm, a, p, p + count[i]);
                            if (stable) {
                                CharArrays.stabilize(perm, a, p, p + count[i]);
                            }
                        } else {
                            offsetStack[stackPos] = p;
                            lengthStack[stackPos] = count[i];
                            levelStack[stackPos++] = level + 1;
                        }
                    }
                    p += count[i];
                }
                java.util.Arrays.fill(count, 0);
                continue;
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                int t = perm[i3];
                c = a[t] >>> shift & 0xFF ^ 0;
                if (i3 < end) {
                    while (true) {
                        int n4 = c;
                        int n5 = pos[n4] - 1;
                        pos[n4] = n5;
                        int d = n5;
                        if (n5 <= i3) break;
                        int z = t;
                        t = perm[d];
                        perm[d] = z;
                        c = a[t] >>> shift & 0xFF ^ 0;
                    }
                    perm[i3] = t;
                }
                if (level < 1 && count[c] > 1) {
                    if (count[c] < 1024) {
                        CharArrays.quickSortIndirect(perm, a, i3, i3 + count[c]);
                        if (stable) {
                            CharArrays.stabilize(perm, a, i3, i3 + count[c]);
                        }
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    public static void parallelRadixSortIndirect(int[] perm, char[] a, int from, int to, boolean stable) {
        ForkJoinPool pool = CharArrays.getPool();
        if (to - from < 1024 || pool.getParallelism() == 1) {
            CharArrays.radixSortIndirect(perm, a, from, to, stable);
            return;
        }
        boolean maxLevel = true;
        LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<Segment>();
        queue.add(new Segment(from, to - from, 0));
        AtomicInteger queueSize = new AtomicInteger(1);
        int numberOfThreads = pool.getParallelism();
        ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<Void>(pool);
        int[] support = stable ? new int[perm.length] : null;
        int j = numberOfThreads;
        while (j-- != 0) {
            executorCompletionService.submit(() -> {
                int[] count = new int[256];
                int[] pos = new int[256];
                while (true) {
                    int i;
                    Segment segment;
                    if (queueSize.get() == 0) {
                        int i2 = numberOfThreads;
                        while (i2-- != 0) {
                            queue.add(POISON_PILL);
                        }
                    }
                    if ((segment = (Segment)queue.take()) == POISON_PILL) {
                        return null;
                    }
                    int first = segment.offset;
                    int length = segment.length;
                    int level = segment.level;
                    boolean signMask = false;
                    int shift = (1 - level % 2) * 8;
                    int i3 = first + length;
                    while (i3-- != first) {
                        int n = a[perm[i3]] >>> shift & 0xFF ^ 0;
                        count[n] = count[n] + 1;
                    }
                    int lastUsed = -1;
                    int p = first;
                    for (i = 0; i < 256; ++i) {
                        if (count[i] != 0) {
                            lastUsed = i;
                        }
                        pos[i] = p += count[i];
                    }
                    if (stable) {
                        i = first + length;
                        while (i-- != first) {
                            int n = a[perm[i]] >>> shift & 0xFF ^ 0;
                            int n2 = pos[n] - 1;
                            pos[n] = n2;
                            support[n2] = perm[i];
                        }
                        System.arraycopy(support, first, perm, first, length);
                        p = first;
                        for (i = 0; i <= lastUsed; ++i) {
                            if (level < 1 && count[i] > 1) {
                                if (count[i] < 1024) {
                                    CharArrays.radixSortIndirect(perm, a, p, p + count[i], stable);
                                } else {
                                    queueSize.incrementAndGet();
                                    queue.add(new Segment(p, count[i], level + 1));
                                }
                            }
                            p += count[i];
                        }
                        java.util.Arrays.fill(count, 0);
                    } else {
                        int end = first + length - count[lastUsed];
                        int c = -1;
                        for (int i4 = first; i4 <= end; i4 += count[c]) {
                            int t = perm[i4];
                            c = a[t] >>> shift & 0xFF ^ 0;
                            if (i4 < end) {
                                while (true) {
                                    int n = c;
                                    int n3 = pos[n] - 1;
                                    pos[n] = n3;
                                    int d = n3;
                                    if (n3 <= i4) break;
                                    int z = t;
                                    t = perm[d];
                                    perm[d] = z;
                                    c = a[t] >>> shift & 0xFF ^ 0;
                                }
                                perm[i4] = t;
                            }
                            if (level < 1 && count[c] > 1) {
                                if (count[c] < 1024) {
                                    CharArrays.radixSortIndirect(perm, a, i4, i4 + count[c], stable);
                                } else {
                                    queueSize.incrementAndGet();
                                    queue.add(new Segment(i4, count[c], level + 1));
                                }
                            }
                            count[c] = 0;
                        }
                    }
                    queueSize.decrementAndGet();
                }
            });
        }
        Throwable problem = null;
        int i = numberOfThreads;
        while (i-- != 0) {
            try {
                executorCompletionService.take().get();
            }
            catch (Exception e) {
                problem = e.getCause();
            }
        }
        if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
        }
    }

    public static void parallelRadixSortIndirect(int[] perm, char[] a, boolean stable) {
        CharArrays.parallelRadixSortIndirect(perm, a, 0, a.length, stable);
    }

    public static void radixSort(char[] a, char[] b) {
        CharArrays.ensureSameLength(a, b);
        CharArrays.radixSort(a, b, 0, a.length);
    }

    public static void radixSort(char[] a, char[] b, int from, int to) {
        if (to - from < 1024) {
            CharArrays.quickSort(a, b, from, to);
            return;
        }
        int layers = 2;
        int maxLevel = 3;
        int stackSize = 766;
        int stackPos = 0;
        int[] offsetStack = new int[766];
        int[] lengthStack = new int[766];
        int[] levelStack = new int[766];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            boolean signMask = false;
            char[] k = level < 2 ? a : b;
            int shift = (1 - level % 2) * 8;
            int i = first + length;
            while (i-- != first) {
                int n = k[i] >>> shift & 0xFF ^ 0;
                count[n] = count[n] + 1;
            }
            int lastUsed = -1;
            int p = first;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0) {
                    lastUsed = i2;
                }
                pos[i2] = p += count[i2];
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                char t = a[i3];
                char u = b[i3];
                c = k[i3] >>> shift & 0xFF ^ 0;
                if (i3 < end) {
                    while (true) {
                        int n = c;
                        int n2 = pos[n] - 1;
                        pos[n] = n2;
                        int d = n2;
                        if (n2 <= i3) break;
                        c = k[d] >>> shift & 0xFF ^ 0;
                        char z = t;
                        t = a[d];
                        a[d] = z;
                        z = u;
                        u = b[d];
                        b[d] = z;
                    }
                    a[i3] = t;
                    b[i3] = u;
                }
                if (level < 3 && count[c] > 1) {
                    if (count[c] < 1024) {
                        CharArrays.quickSort(a, b, i3, i3 + count[c]);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    public static void parallelRadixSort(char[] a, char[] b, int from, int to) {
        ForkJoinPool pool = CharArrays.getPool();
        if (to - from < 1024 || pool.getParallelism() == 1) {
            CharArrays.quickSort(a, b, from, to);
            return;
        }
        int layers = 2;
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        int maxLevel = 3;
        LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<Segment>();
        queue.add(new Segment(from, to - from, 0));
        AtomicInteger queueSize = new AtomicInteger(1);
        int numberOfThreads = pool.getParallelism();
        ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<Void>(pool);
        int j = numberOfThreads;
        while (j-- != 0) {
            executorCompletionService.submit(() -> {
                int[] count = new int[256];
                int[] pos = new int[256];
                while (true) {
                    Segment segment;
                    if (queueSize.get() == 0) {
                        int i = numberOfThreads;
                        while (i-- != 0) {
                            queue.add(POISON_PILL);
                        }
                    }
                    if ((segment = (Segment)queue.take()) == POISON_PILL) {
                        return null;
                    }
                    int first = segment.offset;
                    int length = segment.length;
                    int level = segment.level;
                    int signMask = level % 2 == 0 ? 128 : 0;
                    char[] k = level < 2 ? a : b;
                    int shift = (1 - level % 2) * 8;
                    int i = first + length;
                    while (i-- != first) {
                        int n = k[i] >>> shift & 0xFF ^ signMask;
                        count[n] = count[n] + 1;
                    }
                    int lastUsed = -1;
                    int p = first;
                    for (int i2 = 0; i2 < 256; ++i2) {
                        if (count[i2] != 0) {
                            lastUsed = i2;
                        }
                        pos[i2] = p += count[i2];
                    }
                    int end = first + length - count[lastUsed];
                    int c = -1;
                    for (int i3 = first; i3 <= end; i3 += count[c]) {
                        char t = a[i3];
                        char u = b[i3];
                        c = k[i3] >>> shift & 0xFF ^ signMask;
                        if (i3 < end) {
                            while (true) {
                                int n = c;
                                int n2 = pos[n] - 1;
                                pos[n] = n2;
                                int d = n2;
                                if (n2 <= i3) break;
                                c = k[d] >>> shift & 0xFF ^ signMask;
                                char z = t;
                                char w = u;
                                t = a[d];
                                u = b[d];
                                a[d] = z;
                                b[d] = w;
                            }
                            a[i3] = t;
                            b[i3] = u;
                        }
                        if (level < 3 && count[c] > 1) {
                            if (count[c] < 1024) {
                                CharArrays.quickSort(a, b, i3, i3 + count[c]);
                            } else {
                                queueSize.incrementAndGet();
                                queue.add(new Segment(i3, count[c], level + 1));
                            }
                        }
                        count[c] = 0;
                    }
                    queueSize.decrementAndGet();
                }
            });
        }
        Throwable problem = null;
        int i = numberOfThreads;
        while (i-- != 0) {
            try {
                executorCompletionService.take().get();
            }
            catch (Exception e) {
                problem = e.getCause();
            }
        }
        if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
        }
    }

    public static void parallelRadixSort(char[] a, char[] b) {
        CharArrays.ensureSameLength(a, b);
        CharArrays.parallelRadixSort(a, b, 0, a.length);
    }

    private static void insertionSortIndirect(int[] perm, char[] a, char[] b, int from, int to) {
        int i = from;
        while (++i < to) {
            int t = perm[i];
            int j = i;
            int u = perm[j - 1];
            while (a[t] < a[u] || a[t] == a[u] && b[t] < b[u]) {
                perm[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = perm[--j - 1];
            }
            perm[j] = t;
        }
    }

    public static void radixSortIndirect(int[] perm, char[] a, char[] b, boolean stable) {
        CharArrays.ensureSameLength(a, b);
        CharArrays.radixSortIndirect(perm, a, b, 0, a.length, stable);
    }

    public static void radixSortIndirect(int[] perm, char[] a, char[] b, int from, int to, boolean stable) {
        int[] support;
        if (to - from < 64) {
            CharArrays.insertionSortIndirect(perm, a, b, from, to);
            return;
        }
        int layers = 2;
        int maxLevel = 3;
        int stackSize = 766;
        int stackPos = 0;
        int[] offsetStack = new int[766];
        int[] lengthStack = new int[766];
        int[] levelStack = new int[766];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        int[] arrn = support = stable ? new int[perm.length] : null;
        while (stackPos > 0) {
            int i;
            int p;
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            boolean signMask = false;
            char[] k = level < 2 ? a : b;
            int shift = (1 - level % 2) * 8;
            int i2 = first + length;
            while (i2-- != first) {
                int n = k[perm[i2]] >>> shift & 0xFF ^ 0;
                count[n] = count[n] + 1;
            }
            int lastUsed = -1;
            int n = p = stable ? 0 : first;
            for (i = 0; i < 256; ++i) {
                if (count[i] != 0) {
                    lastUsed = i;
                }
                pos[i] = p += count[i];
            }
            if (stable) {
                i = first + length;
                while (i-- != first) {
                    int n2 = k[perm[i]] >>> shift & 0xFF ^ 0;
                    int n3 = pos[n2] - 1;
                    pos[n2] = n3;
                    support[n3] = perm[i];
                }
                System.arraycopy(support, 0, perm, first, length);
                p = first;
                for (i = 0; i < 256; ++i) {
                    if (level < 3 && count[i] > 1) {
                        if (count[i] < 64) {
                            CharArrays.insertionSortIndirect(perm, a, b, p, p + count[i]);
                        } else {
                            offsetStack[stackPos] = p;
                            lengthStack[stackPos] = count[i];
                            levelStack[stackPos++] = level + 1;
                        }
                    }
                    p += count[i];
                }
                java.util.Arrays.fill(count, 0);
                continue;
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                int t = perm[i3];
                c = k[t] >>> shift & 0xFF ^ 0;
                if (i3 < end) {
                    while (true) {
                        int n4 = c;
                        int n5 = pos[n4] - 1;
                        pos[n4] = n5;
                        int d = n5;
                        if (n5 <= i3) break;
                        int z = t;
                        t = perm[d];
                        perm[d] = z;
                        c = k[t] >>> shift & 0xFF ^ 0;
                    }
                    perm[i3] = t;
                }
                if (level < 3 && count[c] > 1) {
                    if (count[c] < 64) {
                        CharArrays.insertionSortIndirect(perm, a, b, i3, i3 + count[c]);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    private static void selectionSort(char[][] a, int from, int to, int level) {
        int layers = a.length;
        int firstLayer = level / 2;
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            block1: for (int j = i + 1; j < to; ++j) {
                for (int p = firstLayer; p < layers; ++p) {
                    if (a[p][j] < a[p][m]) {
                        m = j;
                        continue block1;
                    }
                    if (a[p][j] > a[p][m]) continue block1;
                }
            }
            if (m == i) continue;
            int p = layers;
            while (p-- != 0) {
                char u = a[p][i];
                a[p][i] = a[p][m];
                a[p][m] = u;
            }
        }
    }

    public static void radixSort(char[][] a) {
        CharArrays.radixSort(a, 0, a[0].length);
    }

    public static void radixSort(char[][] a, int from, int to) {
        if (to - from < 64) {
            CharArrays.selectionSort(a, from, to, 0);
            return;
        }
        int layers = a.length;
        int maxLevel = 2 * layers - 1;
        int p = layers;
        int l = a[0].length;
        while (p-- != 0) {
            if (a[p].length == l) continue;
            throw new IllegalArgumentException("The array of index " + p + " has not the same length of the array of index 0.");
        }
        int stackSize = 255 * (layers * 2 - 1) + 1;
        int stackPos = 0;
        int[] offsetStack = new int[stackSize];
        int[] lengthStack = new int[stackSize];
        int[] levelStack = new int[stackSize];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        char[] t = new char[layers];
        while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            boolean signMask = false;
            char[] k = a[level / 2];
            int shift = (1 - level % 2) * 8;
            int i = first + length;
            while (i-- != first) {
                int n = k[i] >>> shift & 0xFF ^ 0;
                count[n] = count[n] + 1;
            }
            int lastUsed = -1;
            int p2 = first;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0) {
                    lastUsed = i2;
                }
                pos[i2] = p2 += count[i2];
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                int p3 = layers;
                while (p3-- != 0) {
                    t[p3] = a[p3][i3];
                }
                c = k[i3] >>> shift & 0xFF ^ 0;
                if (i3 < end) {
                    block6: while (true) {
                        int n = c;
                        int n2 = pos[n] - 1;
                        pos[n] = n2;
                        int d = n2;
                        if (n2 <= i3) break;
                        c = k[d] >>> shift & 0xFF ^ 0;
                        p3 = layers;
                        while (true) {
                            if (p3-- == 0) continue block6;
                            char u = t[p3];
                            t[p3] = a[p3][d];
                            a[p3][d] = u;
                        }
                        break;
                    }
                    p3 = layers;
                    while (p3-- != 0) {
                        a[p3][i3] = t[p3];
                    }
                }
                if (level < maxLevel && count[c] > 1) {
                    if (count[c] < 64) {
                        CharArrays.selectionSort(a, i3, i3 + count[c], level + 1);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    public static char[] shuffle(char[] a, int from, int to, Random random) {
        int i = to - from;
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            char t = a[from + i];
            a[from + i] = a[from + p];
            a[from + p] = t;
        }
        return a;
    }

    public static char[] shuffle(char[] a, Random random) {
        int i = a.length;
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            char t = a[i];
            a[i] = a[p];
            a[p] = t;
        }
        return a;
    }

    public static char[] reverse(char[] a) {
        int length = a.length;
        int i = length / 2;
        while (i-- != 0) {
            char t = a[length - i - 1];
            a[length - i - 1] = a[i];
            a[i] = t;
        }
        return a;
    }

    public static char[] reverse(char[] a, int from, int to) {
        int length = to - from;
        int i = length / 2;
        while (i-- != 0) {
            char t = a[from + length - i - 1];
            a[from + length - i - 1] = a[from + i];
            a[from + i] = t;
        }
        return a;
    }

    protected static class ForkJoinQuickSortComp
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final char[] x;
        private final CharComparator comp;

        public ForkJoinQuickSortComp(char[] x, int from, int to, CharComparator comp) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.comp = comp;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            char[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                CharArrays.quickSort(x, this.from, this.to, this.comp);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = CharArrays.med3(x, l, l + s, l + 2 * s, this.comp);
            m = CharArrays.med3(x, m - s, m, m + s, this.comp);
            n = CharArrays.med3(x, n - 2 * s, n - s, n, this.comp);
            m = CharArrays.med3(x, l, m, n, this.comp);
            char v = x[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int comparison;
                if (b <= c && (comparison = this.comp.compare(x[b], v)) <= 0) {
                    if (comparison == 0) {
                        CharArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = this.comp.compare(x[c], v)) >= 0) {
                    if (comparison == 0) {
                        CharArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                CharArrays.swap(x, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            CharArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            CharArrays.swap(x, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp), new ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp));
            } else if (s > 1) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp));
            } else {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp));
            }
        }
    }

    protected static class ForkJoinQuickSort
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final char[] x;

        public ForkJoinQuickSort(char[] x, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            char[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                CharArrays.quickSort(x, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = CharArrays.med3(x, l, l + s, l + 2 * s);
            m = CharArrays.med3(x, m - s, m, m + s);
            n = CharArrays.med3(x, n - 2 * s, n - s, n);
            m = CharArrays.med3(x, l, m, n);
            char v = x[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int comparison;
                if (b <= c && (comparison = Character.compare(x[b], v)) <= 0) {
                    if (comparison == 0) {
                        CharArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = Character.compare(x[c], v)) >= 0) {
                    if (comparison == 0) {
                        CharArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                CharArrays.swap(x, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            CharArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            CharArrays.swap(x, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x, this.from, this.from + s), new ForkJoinQuickSort(x, this.to - t, this.to));
            } else if (s > 1) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x, this.from, this.from + s));
            } else {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x, this.to - t, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSortIndirect
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final int[] perm;
        private final char[] x;

        public ForkJoinQuickSortIndirect(int[] perm, char[] x, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.perm = perm;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            char[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                CharArrays.quickSortIndirect(this.perm, x, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = CharArrays.med3Indirect(this.perm, x, l, l + s, l + 2 * s);
            m = CharArrays.med3Indirect(this.perm, x, m - s, m, m + s);
            n = CharArrays.med3Indirect(this.perm, x, n - 2 * s, n - s, n);
            m = CharArrays.med3Indirect(this.perm, x, l, m, n);
            char v = x[this.perm[m]];
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int comparison;
                if (b <= c && (comparison = Character.compare(x[this.perm[b]], v)) <= 0) {
                    if (comparison == 0) {
                        IntArrays.swap(this.perm, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = Character.compare(x[this.perm[c]], v)) >= 0) {
                    if (comparison == 0) {
                        IntArrays.swap(this.perm, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                IntArrays.swap(this.perm, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            IntArrays.swap(this.perm, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            IntArrays.swap(this.perm, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s), new ForkJoinQuickSortIndirect(this.perm, x, this.to - t, this.to));
            } else if (s > 1) {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s));
            } else {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect(this.perm, x, this.to - t, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSort2
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final char[] x;
        private final char[] y;

        public ForkJoinQuickSort2(char[] x, char[] y, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.y = y;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            char[] x = this.x;
            char[] y = this.y;
            int len = this.to - this.from;
            if (len < 8192) {
                CharArrays.quickSort(x, y, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = CharArrays.med3(x, y, l, l + s, l + 2 * s);
            m = CharArrays.med3(x, y, m - s, m, m + s);
            n = CharArrays.med3(x, y, n - 2 * s, n - s, n);
            m = CharArrays.med3(x, y, l, m, n);
            char v = x[m];
            char w = y[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int t;
                int comparison;
                if (b <= c && (comparison = (t = Character.compare(x[b], v)) == 0 ? Character.compare(y[b], w) : t) <= 0) {
                    if (comparison == 0) {
                        CharArrays.swap(x, y, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = (t = Character.compare(x[c], v)) == 0 ? Character.compare(y[c], w) : t) >= 0) {
                    if (comparison == 0) {
                        CharArrays.swap(x, y, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                CharArrays.swap(x, y, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            CharArrays.swap(x, y, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            CharArrays.swap(x, y, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2(x, y, this.from, this.from + s), new ForkJoinQuickSort2(x, y, this.to - t, this.to));
            } else if (s > 1) {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2(x, y, this.from, this.from + s));
            } else {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2(x, y, this.to - t, this.to));
            }
        }
    }

    protected static final class Segment {
        protected final int offset;
        protected final int length;
        protected final int level;

        protected Segment(int offset, int length, int level) {
            this.offset = offset;
            this.length = length;
            this.level = level;
        }

        public String toString() {
            return "Segment [offset=" + this.offset + ", length=" + this.length + ", level=" + this.level + "]";
        }
    }

    private static final class ArrayHashStrategy
    implements Hash.Strategy<char[]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private ArrayHashStrategy() {
        }

        @Override
        public int hashCode(char[] o) {
            return java.util.Arrays.hashCode(o);
        }

        @Override
        public boolean equals(char[] a, char[] b) {
            return java.util.Arrays.equals(a, b);
        }
    }
}

