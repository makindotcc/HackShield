/*
 * Decompiled with CFR 0.150.
 */
package okio.internal;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okio.-Base64;
import okio.-Platform;
import okio.-Util;
import okio.Buffer;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 4, 0}, bv={1, 0, 3}, k=2, d1={"\u0000P\n\u0000\n\u0002\u0010\u0019\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\f\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0018\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005H\u0002\u001a\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0007H\u0080\b\u001a\u0010\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000eH\u0002\u001a\r\u0010\u000f\u001a\u00020\u0010*\u00020\nH\u0080\b\u001a\r\u0010\u0011\u001a\u00020\u0010*\u00020\nH\u0080\b\u001a\u0015\u0010\u0012\u001a\u00020\u0005*\u00020\n2\u0006\u0010\u0013\u001a\u00020\nH\u0080\b\u001a\u000f\u0010\u0014\u001a\u0004\u0018\u00010\n*\u00020\u0010H\u0080\b\u001a\r\u0010\u0015\u001a\u00020\n*\u00020\u0010H\u0080\b\u001a\r\u0010\u0016\u001a\u00020\n*\u00020\u0010H\u0080\b\u001a\u0015\u0010\u0017\u001a\u00020\u0018*\u00020\n2\u0006\u0010\u0019\u001a\u00020\u0007H\u0080\b\u001a\u0015\u0010\u0017\u001a\u00020\u0018*\u00020\n2\u0006\u0010\u0019\u001a\u00020\nH\u0080\b\u001a\u0017\u0010\u001a\u001a\u00020\u0018*\u00020\n2\b\u0010\u0013\u001a\u0004\u0018\u00010\u001bH\u0080\b\u001a\u0015\u0010\u001c\u001a\u00020\u001d*\u00020\n2\u0006\u0010\u001e\u001a\u00020\u0005H\u0080\b\u001a\r\u0010\u001f\u001a\u00020\u0005*\u00020\nH\u0080\b\u001a\r\u0010 \u001a\u00020\u0005*\u00020\nH\u0080\b\u001a\r\u0010!\u001a\u00020\u0010*\u00020\nH\u0080\b\u001a\u001d\u0010\"\u001a\u00020\u0005*\u00020\n2\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010#\u001a\u00020\u0005H\u0080\b\u001a\r\u0010$\u001a\u00020\u0007*\u00020\nH\u0080\b\u001a\u001d\u0010%\u001a\u00020\u0005*\u00020\n2\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010#\u001a\u00020\u0005H\u0080\b\u001a\u001d\u0010%\u001a\u00020\u0005*\u00020\n2\u0006\u0010\u0013\u001a\u00020\n2\u0006\u0010#\u001a\u00020\u0005H\u0080\b\u001a-\u0010&\u001a\u00020\u0018*\u00020\n2\u0006\u0010'\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010(\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u0005H\u0080\b\u001a-\u0010&\u001a\u00020\u0018*\u00020\n2\u0006\u0010'\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\n2\u0006\u0010(\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u0005H\u0080\b\u001a\u0015\u0010*\u001a\u00020\u0018*\u00020\n2\u0006\u0010+\u001a\u00020\u0007H\u0080\b\u001a\u0015\u0010*\u001a\u00020\u0018*\u00020\n2\u0006\u0010+\u001a\u00020\nH\u0080\b\u001a\u001d\u0010,\u001a\u00020\n*\u00020\n2\u0006\u0010-\u001a\u00020\u00052\u0006\u0010.\u001a\u00020\u0005H\u0080\b\u001a\r\u0010/\u001a\u00020\n*\u00020\nH\u0080\b\u001a\r\u00100\u001a\u00020\n*\u00020\nH\u0080\b\u001a\r\u00101\u001a\u00020\u0007*\u00020\nH\u0080\b\u001a\u001d\u00102\u001a\u00020\n*\u00020\u00072\u0006\u0010'\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u0005H\u0080\b\u001a\r\u00103\u001a\u00020\u0010*\u00020\nH\u0080\b\u001a\r\u00104\u001a\u00020\u0010*\u00020\nH\u0080\b\u001a$\u00105\u001a\u000206*\u00020\n2\u0006\u00107\u001a\u0002082\u0006\u0010'\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u0005H\u0000\"\u0014\u0010\u0000\u001a\u00020\u0001X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\u00a8\u00069"}, d2={"HEX_DIGIT_CHARS", "", "getHEX_DIGIT_CHARS", "()[C", "codePointIndexToCharIndex", "", "s", "", "codePointCount", "commonOf", "Lokio/ByteString;", "data", "decodeHexDigit", "c", "", "commonBase64", "", "commonBase64Url", "commonCompareTo", "other", "commonDecodeBase64", "commonDecodeHex", "commonEncodeUtf8", "commonEndsWith", "", "suffix", "commonEquals", "", "commonGetByte", "", "pos", "commonGetSize", "commonHashCode", "commonHex", "commonIndexOf", "fromIndex", "commonInternalArray", "commonLastIndexOf", "commonRangeEquals", "offset", "otherOffset", "byteCount", "commonStartsWith", "prefix", "commonSubstring", "beginIndex", "endIndex", "commonToAsciiLowercase", "commonToAsciiUppercase", "commonToByteArray", "commonToByteString", "commonToString", "commonUtf8", "commonWrite", "", "buffer", "Lokio/Buffer;", "okio"})
public final class ByteStringKt {
    @NotNull
    private static final char[] HEX_DIGIT_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    @NotNull
    public static final String commonUtf8(@NotNull ByteString $this$commonUtf8) {
        int $i$f$commonUtf8 = 0;
        Intrinsics.checkNotNullParameter($this$commonUtf8, "$this$commonUtf8");
        String result = $this$commonUtf8.getUtf8$okio();
        if (result == null) {
            result = -Platform.toUtf8String($this$commonUtf8.internalArray$okio());
            $this$commonUtf8.setUtf8$okio(result);
        }
        return result;
    }

    @NotNull
    public static final String commonBase64(@NotNull ByteString $this$commonBase64) {
        int $i$f$commonBase64 = 0;
        Intrinsics.checkNotNullParameter($this$commonBase64, "$this$commonBase64");
        return -Base64.encodeBase64$default($this$commonBase64.getData$okio(), null, 1, null);
    }

    @NotNull
    public static final String commonBase64Url(@NotNull ByteString $this$commonBase64Url) {
        int $i$f$commonBase64Url = 0;
        Intrinsics.checkNotNullParameter($this$commonBase64Url, "$this$commonBase64Url");
        return -Base64.encodeBase64($this$commonBase64Url.getData$okio(), -Base64.getBASE64_URL_SAFE());
    }

    @NotNull
    public static final char[] getHEX_DIGIT_CHARS() {
        return HEX_DIGIT_CHARS;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final String commonHex(@NotNull ByteString $this$commonHex) {
        int $i$f$commonHex = 0;
        Intrinsics.checkNotNullParameter($this$commonHex, "$this$commonHex");
        char[] result = new char[$this$commonHex.getData$okio().length * 2];
        int c = 0;
        for (byte b : $this$commonHex.getData$okio()) {
            void $this$and$iv;
            byte $this$shr$iv;
            int n = c++;
            byte by = b;
            int other$iv = 4;
            boolean $i$f$shr = false;
            result[n] = ByteStringKt.getHEX_DIGIT_CHARS()[$this$shr$iv >> other$iv & 0xF];
            int n2 = c++;
            $this$shr$iv = b;
            other$iv = 15;
            boolean $i$f$and = false;
            result[n2] = ByteStringKt.getHEX_DIGIT_CHARS()[$this$and$iv & other$iv];
        }
        boolean bl = false;
        return new String(result);
    }

    @NotNull
    public static final ByteString commonToAsciiLowercase(@NotNull ByteString $this$commonToAsciiLowercase) {
        int $i$f$commonToAsciiLowercase = 0;
        Intrinsics.checkNotNullParameter($this$commonToAsciiLowercase, "$this$commonToAsciiLowercase");
        for (int i = 0; i < $this$commonToAsciiLowercase.getData$okio().length; ++i) {
            byte c = $this$commonToAsciiLowercase.getData$okio()[i];
            if (c < (byte)65 || c > (byte)90) {
                continue;
            }
            byte[] arrby = $this$commonToAsciiLowercase.getData$okio();
            boolean bl = false;
            byte[] arrby2 = Arrays.copyOf(arrby, arrby.length);
            Intrinsics.checkNotNullExpressionValue(arrby2, "java.util.Arrays.copyOf(this, size)");
            byte[] lowercase = arrby2;
            lowercase[i++] = (byte)(c - -32);
            while (i < lowercase.length) {
                c = lowercase[i];
                if (c < (byte)65 || c > (byte)90) {
                    ++i;
                    continue;
                }
                lowercase[i] = (byte)(c - -32);
                ++i;
            }
            return new ByteString(lowercase);
        }
        return $this$commonToAsciiLowercase;
    }

    @NotNull
    public static final ByteString commonToAsciiUppercase(@NotNull ByteString $this$commonToAsciiUppercase) {
        int $i$f$commonToAsciiUppercase = 0;
        Intrinsics.checkNotNullParameter($this$commonToAsciiUppercase, "$this$commonToAsciiUppercase");
        for (int i = 0; i < $this$commonToAsciiUppercase.getData$okio().length; ++i) {
            byte c = $this$commonToAsciiUppercase.getData$okio()[i];
            if (c < (byte)97 || c > (byte)122) {
                continue;
            }
            byte[] arrby = $this$commonToAsciiUppercase.getData$okio();
            boolean bl = false;
            byte[] arrby2 = Arrays.copyOf(arrby, arrby.length);
            Intrinsics.checkNotNullExpressionValue(arrby2, "java.util.Arrays.copyOf(this, size)");
            byte[] lowercase = arrby2;
            lowercase[i++] = (byte)(c - 32);
            while (i < lowercase.length) {
                c = lowercase[i];
                if (c < (byte)97 || c > (byte)122) {
                    ++i;
                    continue;
                }
                lowercase[i] = (byte)(c - 32);
                ++i;
            }
            return new ByteString(lowercase);
        }
        return $this$commonToAsciiUppercase;
    }

    @NotNull
    public static final ByteString commonSubstring(@NotNull ByteString $this$commonSubstring, int beginIndex, int endIndex) {
        int $i$f$commonSubstring = 0;
        Intrinsics.checkNotNullParameter($this$commonSubstring, "$this$commonSubstring");
        boolean bl = beginIndex >= 0;
        boolean bl2 = false;
        boolean bl3 = false;
        if (!bl) {
            boolean bl4 = false;
            String string = "beginIndex < 0";
            throw (Throwable)new IllegalArgumentException(string.toString());
        }
        bl = endIndex <= $this$commonSubstring.getData$okio().length;
        bl2 = false;
        bl3 = false;
        if (!bl) {
            boolean bl5 = false;
            String string = "endIndex > length(" + $this$commonSubstring.getData$okio().length + ')';
            throw (Throwable)new IllegalArgumentException(string.toString());
        }
        int subLen = endIndex - beginIndex;
        bl2 = subLen >= 0;
        bl3 = false;
        boolean bl6 = false;
        if (!bl2) {
            boolean bl7 = false;
            String string = "endIndex < beginIndex";
            throw (Throwable)new IllegalArgumentException(string.toString());
        }
        if (beginIndex == 0 && endIndex == $this$commonSubstring.getData$okio().length) {
            return $this$commonSubstring;
        }
        byte[] arrby = $this$commonSubstring.getData$okio();
        bl3 = false;
        return new ByteString(ArraysKt.copyOfRange(arrby, beginIndex, endIndex));
    }

    public static final byte commonGetByte(@NotNull ByteString $this$commonGetByte, int pos) {
        int $i$f$commonGetByte = 0;
        Intrinsics.checkNotNullParameter($this$commonGetByte, "$this$commonGetByte");
        return $this$commonGetByte.getData$okio()[pos];
    }

    public static final int commonGetSize(@NotNull ByteString $this$commonGetSize) {
        int $i$f$commonGetSize = 0;
        Intrinsics.checkNotNullParameter($this$commonGetSize, "$this$commonGetSize");
        return $this$commonGetSize.getData$okio().length;
    }

    @NotNull
    public static final byte[] commonToByteArray(@NotNull ByteString $this$commonToByteArray) {
        int $i$f$commonToByteArray = 0;
        Intrinsics.checkNotNullParameter($this$commonToByteArray, "$this$commonToByteArray");
        byte[] arrby = $this$commonToByteArray.getData$okio();
        boolean bl = false;
        byte[] arrby2 = Arrays.copyOf(arrby, arrby.length);
        Intrinsics.checkNotNullExpressionValue(arrby2, "java.util.Arrays.copyOf(this, size)");
        return arrby2;
    }

    @NotNull
    public static final byte[] commonInternalArray(@NotNull ByteString $this$commonInternalArray) {
        int $i$f$commonInternalArray = 0;
        Intrinsics.checkNotNullParameter($this$commonInternalArray, "$this$commonInternalArray");
        return $this$commonInternalArray.getData$okio();
    }

    public static final boolean commonRangeEquals(@NotNull ByteString $this$commonRangeEquals, int offset, @NotNull ByteString other, int otherOffset, int byteCount) {
        int $i$f$commonRangeEquals = 0;
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "$this$commonRangeEquals");
        Intrinsics.checkNotNullParameter(other, "other");
        return other.rangeEquals(otherOffset, $this$commonRangeEquals.getData$okio(), offset, byteCount);
    }

    public static final boolean commonRangeEquals(@NotNull ByteString $this$commonRangeEquals, int offset, @NotNull byte[] other, int otherOffset, int byteCount) {
        int $i$f$commonRangeEquals = 0;
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "$this$commonRangeEquals");
        Intrinsics.checkNotNullParameter(other, "other");
        return offset >= 0 && offset <= $this$commonRangeEquals.getData$okio().length - byteCount && otherOffset >= 0 && otherOffset <= other.length - byteCount && -Util.arrayRangeEquals($this$commonRangeEquals.getData$okio(), offset, other, otherOffset, byteCount);
    }

    public static final boolean commonStartsWith(@NotNull ByteString $this$commonStartsWith, @NotNull ByteString prefix) {
        int $i$f$commonStartsWith = 0;
        Intrinsics.checkNotNullParameter($this$commonStartsWith, "$this$commonStartsWith");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        return $this$commonStartsWith.rangeEquals(0, prefix, 0, prefix.size());
    }

    public static final boolean commonStartsWith(@NotNull ByteString $this$commonStartsWith, @NotNull byte[] prefix) {
        int $i$f$commonStartsWith = 0;
        Intrinsics.checkNotNullParameter($this$commonStartsWith, "$this$commonStartsWith");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        return $this$commonStartsWith.rangeEquals(0, prefix, 0, prefix.length);
    }

    public static final boolean commonEndsWith(@NotNull ByteString $this$commonEndsWith, @NotNull ByteString suffix) {
        int $i$f$commonEndsWith = 0;
        Intrinsics.checkNotNullParameter($this$commonEndsWith, "$this$commonEndsWith");
        Intrinsics.checkNotNullParameter(suffix, "suffix");
        return $this$commonEndsWith.rangeEquals($this$commonEndsWith.size() - suffix.size(), suffix, 0, suffix.size());
    }

    public static final boolean commonEndsWith(@NotNull ByteString $this$commonEndsWith, @NotNull byte[] suffix) {
        int $i$f$commonEndsWith = 0;
        Intrinsics.checkNotNullParameter($this$commonEndsWith, "$this$commonEndsWith");
        Intrinsics.checkNotNullParameter(suffix, "suffix");
        return $this$commonEndsWith.rangeEquals($this$commonEndsWith.size() - suffix.length, suffix, 0, suffix.length);
    }

    /*
     * WARNING - void declaration
     */
    public static final int commonIndexOf(@NotNull ByteString $this$commonIndexOf, @NotNull byte[] other, int fromIndex) {
        int $i$f$commonIndexOf = 0;
        Intrinsics.checkNotNullParameter($this$commonIndexOf, "$this$commonIndexOf");
        Intrinsics.checkNotNullParameter(other, "other");
        int limit = $this$commonIndexOf.getData$okio().length - other.length;
        int n = 0;
        boolean bl = false;
        int n2 = Math.max(fromIndex, n);
        int n3 = limit;
        if (n2 <= n3) {
            void i;
            do {
                if (!-Util.arrayRangeEquals($this$commonIndexOf.getData$okio(), (int)(++i), other, 0, other.length)) continue;
                return (int)i;
            } while (i != n3);
        }
        return -1;
    }

    public static final int commonLastIndexOf(@NotNull ByteString $this$commonLastIndexOf, @NotNull ByteString other, int fromIndex) {
        int $i$f$commonLastIndexOf = 0;
        Intrinsics.checkNotNullParameter($this$commonLastIndexOf, "$this$commonLastIndexOf");
        Intrinsics.checkNotNullParameter(other, "other");
        return $this$commonLastIndexOf.lastIndexOf(other.internalArray$okio(), fromIndex);
    }

    /*
     * WARNING - void declaration
     */
    public static final int commonLastIndexOf(@NotNull ByteString $this$commonLastIndexOf, @NotNull byte[] other, int fromIndex) {
        int $i$f$commonLastIndexOf = 0;
        Intrinsics.checkNotNullParameter($this$commonLastIndexOf, "$this$commonLastIndexOf");
        Intrinsics.checkNotNullParameter(other, "other");
        int limit = $this$commonLastIndexOf.getData$okio().length - other.length;
        boolean bl = false;
        int n = Math.min(fromIndex, limit);
        boolean bl2 = false;
        while (n >= 0) {
            void i;
            if (-Util.arrayRangeEquals($this$commonLastIndexOf.getData$okio(), (int)i, other, 0, other.length)) {
                return (int)i;
            }
            --i;
        }
        return -1;
    }

    public static final boolean commonEquals(@NotNull ByteString $this$commonEquals, @Nullable Object other) {
        int $i$f$commonEquals = 0;
        Intrinsics.checkNotNullParameter($this$commonEquals, "$this$commonEquals");
        return other == $this$commonEquals ? true : (other instanceof ByteString ? ((ByteString)other).size() == $this$commonEquals.getData$okio().length && ((ByteString)other).rangeEquals(0, $this$commonEquals.getData$okio(), 0, $this$commonEquals.getData$okio().length) : false);
    }

    public static final int commonHashCode(@NotNull ByteString $this$commonHashCode) {
        int $i$f$commonHashCode = 0;
        Intrinsics.checkNotNullParameter($this$commonHashCode, "$this$commonHashCode");
        int result = $this$commonHashCode.getHashCode$okio();
        if (result != 0) {
            return result;
        }
        byte[] arrby = $this$commonHashCode.getData$okio();
        boolean bl = false;
        int n = Arrays.hashCode(arrby);
        bl = false;
        boolean bl2 = false;
        int it = n;
        boolean bl3 = false;
        $this$commonHashCode.setHashCode$okio(it);
        return n;
    }

    /*
     * WARNING - void declaration
     */
    public static final int commonCompareTo(@NotNull ByteString $this$commonCompareTo, @NotNull ByteString other) {
        int $i$f$commonCompareTo = 0;
        Intrinsics.checkNotNullParameter($this$commonCompareTo, "$this$commonCompareTo");
        Intrinsics.checkNotNullParameter(other, "other");
        int sizeA = $this$commonCompareTo.size();
        int sizeB = other.size();
        boolean bl = false;
        int size = Math.min(sizeA, sizeB);
        for (int i = 0; i < size; ++i) {
            void $this$and$iv;
            void $this$and$iv2;
            byte by = $this$commonCompareTo.getByte(i);
            int other$iv = 255;
            boolean $i$f$and = false;
            int byteA = $this$and$iv2 & other$iv;
            other$iv = other.getByte(i);
            int other$iv2 = 255;
            boolean $i$f$and2 = false;
            int byteB = $this$and$iv & other$iv2;
            if (byteA == byteB) {
                continue;
            }
            return byteA < byteB ? -1 : 1;
        }
        if (sizeA == sizeB) {
            return 0;
        }
        return sizeA < sizeB ? -1 : 1;
    }

    @NotNull
    public static final ByteString commonOf(@NotNull byte[] data) {
        int $i$f$commonOf = 0;
        Intrinsics.checkNotNullParameter(data, "data");
        byte[] arrby = data;
        boolean bl = false;
        byte[] arrby2 = Arrays.copyOf(arrby, arrby.length);
        Intrinsics.checkNotNullExpressionValue(arrby2, "java.util.Arrays.copyOf(this, size)");
        return new ByteString(arrby2);
    }

    @NotNull
    public static final ByteString commonToByteString(@NotNull byte[] $this$commonToByteString, int offset, int byteCount) {
        int $i$f$commonToByteString = 0;
        Intrinsics.checkNotNullParameter($this$commonToByteString, "$this$commonToByteString");
        -Util.checkOffsetAndCount($this$commonToByteString.length, offset, byteCount);
        byte[] arrby = $this$commonToByteString;
        int n = offset + byteCount;
        boolean bl = false;
        return new ByteString(ArraysKt.copyOfRange(arrby, offset, n));
    }

    @NotNull
    public static final ByteString commonEncodeUtf8(@NotNull String $this$commonEncodeUtf8) {
        int $i$f$commonEncodeUtf8 = 0;
        Intrinsics.checkNotNullParameter($this$commonEncodeUtf8, "$this$commonEncodeUtf8");
        ByteString byteString = new ByteString(-Platform.asUtf8ToByteArray($this$commonEncodeUtf8));
        byteString.setUtf8$okio($this$commonEncodeUtf8);
        return byteString;
    }

    @Nullable
    public static final ByteString commonDecodeBase64(@NotNull String $this$commonDecodeBase64) {
        int $i$f$commonDecodeBase64 = 0;
        Intrinsics.checkNotNullParameter($this$commonDecodeBase64, "$this$commonDecodeBase64");
        byte[] decoded = -Base64.decodeBase64ToArray($this$commonDecodeBase64);
        return decoded != null ? new ByteString(decoded) : null;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final ByteString commonDecodeHex(@NotNull String $this$commonDecodeHex) {
        int $i$f$commonDecodeHex = 0;
        Intrinsics.checkNotNullParameter($this$commonDecodeHex, "$this$commonDecodeHex");
        boolean bl = $this$commonDecodeHex.length() % 2 == 0;
        int n = 0;
        int n2 = 0;
        if (!bl) {
            boolean bl2 = false;
            String string = "Unexpected hex string: " + $this$commonDecodeHex;
            throw (Throwable)new IllegalArgumentException(string.toString());
        }
        byte[] result = new byte[$this$commonDecodeHex.length() / 2];
        n = 0;
        n2 = result.length;
        while (n < n2) {
            void i;
            int d1 = ByteStringKt.decodeHexDigit($this$commonDecodeHex.charAt((int)(i * 2))) << 4;
            int d2 = ByteStringKt.decodeHexDigit($this$commonDecodeHex.charAt((int)(i * 2 + true)));
            result[i] = (byte)(d1 + d2);
            ++i;
        }
        return new ByteString(result);
    }

    public static final void commonWrite(@NotNull ByteString $this$commonWrite, @NotNull Buffer buffer, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "$this$commonWrite");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        buffer.write($this$commonWrite.getData$okio(), offset, byteCount);
    }

    private static final int decodeHexDigit(char c) {
        int n;
        char c2 = c;
        char c3 = c2;
        if ('0' <= c3 && '9' >= c3) {
            n = c - 48;
        } else {
            c3 = c2;
            if ('a' <= c3 && 'f' >= c3) {
                n = c - 97 + 10;
            } else {
                c3 = c2;
                if ('A' <= c3 && 'F' >= c3) {
                    n = c - 65 + 10;
                } else {
                    throw (Throwable)new IllegalArgumentException("Unexpected hex digit: " + c);
                }
            }
        }
        return n;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final String commonToString(@NotNull ByteString $this$commonToString) {
        String text;
        int $i$f$commonToString = 0;
        Intrinsics.checkNotNullParameter($this$commonToString, "$this$commonToString");
        byte[] arrby = $this$commonToString.getData$okio();
        boolean bl = false;
        if (arrby.length == 0) {
            return "[size=0]";
        }
        int i = ByteStringKt.codePointIndexToCharIndex($this$commonToString.getData$okio(), 64);
        if (i == -1) {
            String string;
            if ($this$commonToString.getData$okio().length <= 64) {
                string = "[hex=" + $this$commonToString.hex() + ']';
            } else {
                ByteString byteString;
                void beginIndex$iv;
                void $this$commonSubstring$iv;
                StringBuilder stringBuilder = new StringBuilder().append("[size=").append($this$commonToString.getData$okio().length).append(" hex=");
                ByteString byteString2 = $this$commonToString;
                boolean bl2 = false;
                int endIndex$iv = 64;
                boolean $i$f$commonSubstring = false;
                boolean bl3 = true;
                boolean bl4 = false;
                boolean bl5 = false;
                bl3 = endIndex$iv <= $this$commonSubstring$iv.getData$okio().length;
                bl4 = false;
                bl5 = false;
                if (!bl3) {
                    boolean bl6 = false;
                    String string2 = "endIndex > length(" + $this$commonSubstring$iv.getData$okio().length + ')';
                    throw (Throwable)new IllegalArgumentException(string2.toString());
                }
                int subLen$iv = endIndex$iv - beginIndex$iv;
                bl4 = subLen$iv >= 0;
                bl5 = false;
                boolean bl7 = false;
                if (!bl4) {
                    boolean bl8 = false;
                    String string3 = "endIndex < beginIndex";
                    throw (Throwable)new IllegalArgumentException(string3.toString());
                }
                if (endIndex$iv == $this$commonSubstring$iv.getData$okio().length) {
                    byteString = $this$commonSubstring$iv;
                } else {
                    byte[] arrby2 = $this$commonSubstring$iv.getData$okio();
                    bl5 = false;
                    ByteString byteString3 = new ByteString(ArraysKt.copyOfRange(arrby2, (int)beginIndex$iv, endIndex$iv));
                    byteString = byteString3;
                }
                string = stringBuilder.append(byteString.hex()).append("\u2026]").toString();
            }
            return string;
        }
        String string = text = $this$commonToString.utf8();
        int n = 0;
        boolean bl9 = false;
        String string4 = string;
        if (string4 == null) {
            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
        }
        String string5 = string4.substring(n, i);
        Intrinsics.checkNotNullExpressionValue(string5, "(this as java.lang.Strin\u2026ing(startIndex, endIndex)");
        String safeText = StringsKt.replace$default(StringsKt.replace$default(StringsKt.replace$default(string5, "\\", "\\\\", false, 4, null), "\n", "\\n", false, 4, null), "\r", "\\r", false, 4, null);
        return i < text.length() ? "[size=" + $this$commonToString.getData$okio().length + " text=" + safeText + "\u2026]" : "[text=" + safeText + ']';
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private static final int codePointIndexToCharIndex(byte[] s, int codePointCount) {
        charCount = 0;
        j = 0;
        var4_4 = s;
        var5_5 = false;
        endIndex$iv = s.length;
        $i$f$processUtf8CodePoints = false;
        index$iv = beginIndex$iv;
        block0: while (true) {
            block97: {
                block101: {
                    block105: {
                        block102: {
                            block104: {
                                block103: {
                                    block98: {
                                        block100: {
                                            block99: {
                                                block96: {
                                                    if (index$iv >= endIndex$iv) return charCount;
                                                    b0$iv = $this$processUtf8CodePoints$iv[index$iv];
                                                    if (b0$iv < 0) break block96;
                                                    c = b0$iv;
                                                    $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                                    var12_12 = j;
                                                    j = var12_12 + 1;
                                                    if (var12_12 == codePointCount) {
                                                        return charCount;
                                                    }
                                                    if (c != 10 && c != 13) {
                                                        $i$f$isIsoControl = 0;
                                                        var13_13 = c;
                                                        if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                                    }
                                                    if (c == 65533) {
                                                        return -1;
                                                    }
                                                    charCount += c < 65536 ? 1 : 2;
                                                    ++index$iv;
                                                    break block97;
                                                }
                                                var14_14 = b0$iv;
                                                other$iv$iv = 5;
                                                $i$f$shr = 0;
                                                if ($this$shr$iv$iv >> other$iv$iv == -2) {
                                                    $this$process2Utf8Bytes$iv$iv = $this$processUtf8CodePoints$iv;
                                                    $i$f$process2Utf8Bytes = false;
                                                    if (endIndex$iv <= index$iv + true) {
                                                        $i$f$shr = 65533;
                                                        var17_20 = index$iv;
                                                        $i$a$-process2Utf8Bytes-Utf8$processUtf8CodePoints$1$iv = false;
                                                        c = it$iv;
                                                        $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                                        $i$f$isIsoControl = j;
                                                        j = $i$f$isIsoControl + 1;
                                                        if ($i$f$isIsoControl == codePointCount) {
                                                            return charCount;
                                                        }
                                                        if (c != 10 && c != 13) {
                                                            $i$f$isIsoControl = 0;
                                                            var13_13 = c;
                                                            if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                                        }
                                                        if (c == 65533) {
                                                            return -1;
                                                        }
                                                        charCount += c < 65536 ? 1 : 2;
                                                        var19_22 = Unit.INSTANCE;
                                                        v0 = var17_20;
                                                        v1 = 1;
                                                    } else {
                                                        b0$iv$iv = $this$process2Utf8Bytes$iv$iv[index$iv];
                                                        b1$iv$iv = $this$process2Utf8Bytes$iv$iv[index$iv + true];
                                                        $i$f$isUtf8Continuation = false;
                                                        var23_26 = b1$iv$iv;
                                                        other$iv$iv$iv$iv = 192;
                                                        $i$f$and = false;
                                                        if (!(($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128)) {
                                                            it$iv = 65533;
                                                            $i$a$-process2Utf8Bytes-Utf8$processUtf8CodePoints$1$iv = false;
                                                            c = it$iv;
                                                            $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                                            $i$f$isIsoControl = j;
                                                            j = $i$f$isIsoControl + 1;
                                                            if ($i$f$isIsoControl == codePointCount) {
                                                                return charCount;
                                                            }
                                                            if (c != 10 && c != 13) {
                                                                $i$f$isIsoControl = 0;
                                                                var13_13 = c;
                                                                if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                                            }
                                                            if (c == 65533) {
                                                                return -1;
                                                            }
                                                            charCount += c < 65536 ? 1 : 2;
                                                            var19_22 = Unit.INSTANCE;
                                                            v0 = var17_20;
                                                            v1 = 1;
                                                        } else {
                                                            codePoint$iv$iv = 3968 ^ b1$iv$iv ^ b0$iv$iv << 6;
                                                            if (codePoint$iv$iv < 128) {
                                                                it$iv = 65533;
                                                                $i$a$-process2Utf8Bytes-Utf8$processUtf8CodePoints$1$iv = false;
                                                                c = it$iv;
                                                                $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                                                $i$f$isIsoControl = j;
                                                                j = $i$f$isIsoControl + 1;
                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                    return charCount;
                                                                }
                                                                if (c != 10 && c != 13) {
                                                                    $i$f$isIsoControl = 0;
                                                                    var13_13 = c;
                                                                    if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                                                }
                                                                if (c == 65533) {
                                                                    return -1;
                                                                }
                                                                charCount += c < 65536 ? 1 : 2;
                                                                var19_22 = Unit.INSTANCE;
                                                                v0 = var17_20;
                                                            } else {
                                                                it$iv = codePoint$iv$iv;
                                                                $i$a$-process2Utf8Bytes-Utf8$processUtf8CodePoints$1$iv = false;
                                                                c = it$iv;
                                                                $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                                                $i$f$isIsoControl = j;
                                                                j = $i$f$isIsoControl + 1;
                                                                if ($i$f$isIsoControl == codePointCount) {
                                                                    return charCount;
                                                                }
                                                                if (c != 10 && c != 13) {
                                                                    $i$f$isIsoControl = 0;
                                                                    var13_13 = c;
                                                                    if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                                                }
                                                                if (c == 65533) {
                                                                    return -1;
                                                                }
                                                                charCount += c < 65536 ? 1 : 2;
                                                                var19_22 = Unit.INSTANCE;
                                                                v0 = var17_20;
                                                            }
                                                            v1 = 2;
                                                        }
                                                    }
                                                    index$iv = v0 + v1;
                                                    continue;
                                                }
                                                $this$process2Utf8Bytes$iv$iv = b0$iv;
                                                other$iv$iv = 4;
                                                $i$f$shr = 0;
                                                if ($this$shr$iv$iv >> other$iv$iv != -2) break block98;
                                                $this$process3Utf8Bytes$iv$iv = $this$processUtf8CodePoints$iv;
                                                $i$f$process3Utf8Bytes = false;
                                                if (endIndex$iv > index$iv + 2) break block99;
                                                $i$f$shr = 65533;
                                                var17_20 = index$iv;
                                                $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2$iv = false;
                                                c = it$iv;
                                                $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                                $i$f$isIsoControl = j;
                                                j = $i$f$isIsoControl + 1;
                                                if ($i$f$isIsoControl == codePointCount) {
                                                    return charCount;
                                                }
                                                if (c != 10 && c != 13) {
                                                    $i$f$isIsoControl = 0;
                                                    var13_13 = c;
                                                    if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                                }
                                                if (c == 65533) {
                                                    return -1;
                                                }
                                                charCount += c < 65536 ? 1 : 2;
                                                var19_22 = Unit.INSTANCE;
                                                v2 = var17_20;
                                                if (endIndex$iv <= index$iv + true) ** GOTO lbl-1000
                                                byte$iv$iv$iv = $this$process3Utf8Bytes$iv$iv[index$iv + true];
                                                $i$f$isUtf8Continuation = false;
                                                codePoint$iv$iv = byte$iv$iv$iv;
                                                other$iv$iv$iv$iv = 192;
                                                $i$f$and = false;
                                                if (!(($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128)) lbl-1000:
                                                // 2 sources

                                                {
                                                    v3 = 1;
                                                } else {
                                                    v3 = 2;
                                                }
                                                break block100;
                                            }
                                            b0$iv$iv = $this$process3Utf8Bytes$iv$iv[index$iv];
                                            b1$iv$iv = $this$process3Utf8Bytes$iv$iv[index$iv + true];
                                            $i$f$isUtf8Continuation = false;
                                            other$iv$iv$iv$iv = b1$iv$iv;
                                            other$iv$iv$iv$iv = 192;
                                            $i$f$and = false;
                                            if (!(($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128)) {
                                                it$iv = 65533;
                                                $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2$iv = false;
                                                c = it$iv;
                                                $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                                $i$f$isIsoControl = j;
                                                j = $i$f$isIsoControl + 1;
                                                if ($i$f$isIsoControl == codePointCount) {
                                                    return charCount;
                                                }
                                                if (c != 10 && c != 13) {
                                                    $i$f$isIsoControl = 0;
                                                    var13_13 = c;
                                                    if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                                }
                                                if (c == 65533) {
                                                    return -1;
                                                }
                                                charCount += c < 65536 ? 1 : 2;
                                                var19_22 = Unit.INSTANCE;
                                                v2 = var17_20;
                                                v3 = 1;
                                            } else {
                                                b2$iv$iv = $this$process3Utf8Bytes$iv$iv[index$iv + 2];
                                                $i$f$isUtf8Continuation = false;
                                                other$iv$iv$iv$iv = b2$iv$iv;
                                                other$iv$iv$iv$iv = 192;
                                                $i$f$and = false;
                                                if (!(($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128)) {
                                                    it$iv = 65533;
                                                    $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2$iv = false;
                                                    c = it$iv;
                                                    $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                                    $i$f$isIsoControl = j;
                                                    j = $i$f$isIsoControl + 1;
                                                    if ($i$f$isIsoControl == codePointCount) {
                                                        return charCount;
                                                    }
                                                    if (c != 10 && c != 13) {
                                                        $i$f$isIsoControl = 0;
                                                        var13_13 = c;
                                                        if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                                    }
                                                    if (c == 65533) {
                                                        return -1;
                                                    }
                                                    charCount += c < 65536 ? 1 : 2;
                                                    var19_22 = Unit.INSTANCE;
                                                    v2 = var17_20;
                                                    v3 = 2;
                                                } else {
                                                    codePoint$iv$iv = -123008 ^ b2$iv$iv ^ b1$iv$iv << 6 ^ b0$iv$iv << 12;
                                                    if (codePoint$iv$iv < 2048) {
                                                        it$iv = 65533;
                                                        $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2$iv = false;
                                                        c = it$iv;
                                                        $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                                        $i$f$isIsoControl = j;
                                                        j = $i$f$isIsoControl + 1;
                                                        if ($i$f$isIsoControl == codePointCount) {
                                                            return charCount;
                                                        }
                                                        if (c != 10 && c != 13) {
                                                            $i$f$isIsoControl = 0;
                                                            var13_13 = c;
                                                            if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                                        }
                                                        if (c == 65533) {
                                                            return -1;
                                                        }
                                                        charCount += c < 65536 ? 1 : 2;
                                                        var19_22 = Unit.INSTANCE;
                                                        v2 = var17_20;
                                                    } else {
                                                        $this$and$iv$iv$iv$iv = codePoint$iv$iv;
                                                        if (55296 <= $this$and$iv$iv$iv$iv && 57343 >= $this$and$iv$iv$iv$iv) {
                                                            it$iv = 65533;
                                                            $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2$iv = false;
                                                            c = it$iv;
                                                            $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                                            $i$f$isIsoControl = j;
                                                            j = $i$f$isIsoControl + 1;
                                                            if ($i$f$isIsoControl == codePointCount) {
                                                                return charCount;
                                                            }
                                                            if (c != 10 && c != 13) {
                                                                $i$f$isIsoControl = 0;
                                                                var13_13 = c;
                                                                if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                                            }
                                                            if (c == 65533) {
                                                                return -1;
                                                            }
                                                            charCount += c < 65536 ? 1 : 2;
                                                            var19_22 = Unit.INSTANCE;
                                                            v2 = var17_20;
                                                        } else {
                                                            it$iv = codePoint$iv$iv;
                                                            $i$a$-process3Utf8Bytes-Utf8$processUtf8CodePoints$2$iv = false;
                                                            c = it$iv;
                                                            $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                                            $i$f$isIsoControl = j;
                                                            j = $i$f$isIsoControl + 1;
                                                            if ($i$f$isIsoControl == codePointCount) {
                                                                return charCount;
                                                            }
                                                            if (c != 10 && c != 13) {
                                                                $i$f$isIsoControl = 0;
                                                                var13_13 = c;
                                                                if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                                            }
                                                            if (c == 65533) {
                                                                return -1;
                                                            }
                                                            charCount += c < 65536 ? 1 : 2;
                                                            var19_22 = Unit.INSTANCE;
                                                            v2 = var17_20;
                                                        }
                                                    }
                                                    v3 = 3;
                                                }
                                            }
                                        }
                                        index$iv = v2 + v3;
                                        continue;
                                    }
                                    $this$process3Utf8Bytes$iv$iv = b0$iv;
                                    other$iv$iv = 3;
                                    $i$f$shr = 0;
                                    if ($this$shr$iv$iv >> other$iv$iv != -2) break block101;
                                    $this$process4Utf8Bytes$iv$iv = $this$processUtf8CodePoints$iv;
                                    $i$f$process4Utf8Bytes = false;
                                    if (endIndex$iv > index$iv + 3) break block102;
                                    $i$f$shr = 65533;
                                    var17_20 = index$iv;
                                    $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                    c = it$iv;
                                    $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                    $i$f$isIsoControl = j;
                                    j = $i$f$isIsoControl + 1;
                                    if ($i$f$isIsoControl == codePointCount) {
                                        return charCount;
                                    }
                                    if (c != 10 && c != 13) {
                                        $i$f$isIsoControl = 0;
                                        var13_13 = c;
                                        if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                    }
                                    if (c == 65533) {
                                        return -1;
                                    }
                                    charCount += c < 65536 ? 1 : 2;
                                    var19_22 = Unit.INSTANCE;
                                    v4 = var17_20;
                                    if (endIndex$iv <= index$iv + true) break block103;
                                    byte$iv$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv + true];
                                    $i$f$isUtf8Continuation = false;
                                    b2$iv$iv = byte$iv$iv$iv;
                                    other$iv$iv$iv$iv = 192;
                                    $i$f$and = false;
                                    if (($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128) break block104;
                                }
                                v5 = 1;
                                break block105;
                            }
                            if (endIndex$iv <= index$iv + 2) ** GOTO lbl-1000
                            byte$iv$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv + 2];
                            $i$f$isUtf8Continuation = false;
                            $this$and$iv$iv$iv$iv = byte$iv$iv$iv;
                            other$iv$iv$iv$iv = 192;
                            $i$f$and = false;
                            if (!(($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128)) lbl-1000:
                            // 2 sources

                            {
                                v5 = 2;
                            } else {
                                v5 = 3;
                            }
                            break block105;
                        }
                        b0$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv];
                        b1$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv + true];
                        $i$f$isUtf8Continuation = false;
                        other$iv$iv$iv$iv = b1$iv$iv;
                        other$iv$iv$iv$iv = 192;
                        $i$f$and = false;
                        if (!(($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128)) {
                            it$iv = 65533;
                            $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                            c = it$iv;
                            $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                            $i$f$isIsoControl = j;
                            j = $i$f$isIsoControl + 1;
                            if ($i$f$isIsoControl == codePointCount) {
                                return charCount;
                            }
                            if (c != 10 && c != 13) {
                                $i$f$isIsoControl = 0;
                                var13_13 = c;
                                if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                            }
                            if (c == 65533) {
                                return -1;
                            }
                            charCount += c < 65536 ? 1 : 2;
                            var19_22 = Unit.INSTANCE;
                            v4 = var17_20;
                            v5 = 1;
                        } else {
                            b2$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv + 2];
                            $i$f$isUtf8Continuation = false;
                            other$iv$iv$iv$iv = b2$iv$iv;
                            other$iv$iv$iv$iv = 192;
                            $i$f$and = false;
                            if (!(($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128)) {
                                it$iv = 65533;
                                $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                c = it$iv;
                                $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                $i$f$isIsoControl = j;
                                j = $i$f$isIsoControl + 1;
                                if ($i$f$isIsoControl == codePointCount) {
                                    return charCount;
                                }
                                if (c != 10 && c != 13) {
                                    $i$f$isIsoControl = 0;
                                    var13_13 = c;
                                    if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                }
                                if (c == 65533) {
                                    return -1;
                                }
                                charCount += c < 65536 ? 1 : 2;
                                var19_22 = Unit.INSTANCE;
                                v4 = var17_20;
                                v5 = 2;
                            } else {
                                b3$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv + 3];
                                $i$f$isUtf8Continuation = false;
                                other$iv$iv$iv$iv = b3$iv$iv;
                                other$iv$iv$iv$iv = 192;
                                $i$f$and = false;
                                if (!(($this$and$iv$iv$iv$iv & other$iv$iv$iv$iv) == 128)) {
                                    it$iv = 65533;
                                    $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                    c = it$iv;
                                    $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                    $i$f$isIsoControl = j;
                                    j = $i$f$isIsoControl + 1;
                                    if ($i$f$isIsoControl == codePointCount) {
                                        return charCount;
                                    }
                                    if (c != 10 && c != 13) {
                                        $i$f$isIsoControl = 0;
                                        var13_13 = c;
                                        if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                    }
                                    if (c == 65533) {
                                        return -1;
                                    }
                                    charCount += c < 65536 ? 1 : 2;
                                    var19_22 = Unit.INSTANCE;
                                    v4 = var17_20;
                                    v5 = 3;
                                } else {
                                    codePoint$iv$iv = 3678080 ^ b3$iv$iv ^ b2$iv$iv << 6 ^ b1$iv$iv << 12 ^ b0$iv$iv << 18;
                                    if (codePoint$iv$iv > 0x10FFFF) {
                                        it$iv = 65533;
                                        $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                        c = it$iv;
                                        $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                        $i$f$isIsoControl = j;
                                        j = $i$f$isIsoControl + 1;
                                        if ($i$f$isIsoControl == codePointCount) {
                                            return charCount;
                                        }
                                        if (c != 10 && c != 13) {
                                            $i$f$isIsoControl = 0;
                                            var13_13 = c;
                                            if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                        }
                                        if (c == 65533) {
                                            return -1;
                                        }
                                        charCount += c < 65536 ? 1 : 2;
                                        var19_22 = Unit.INSTANCE;
                                        v4 = var17_20;
                                    } else {
                                        var25_28 = codePoint$iv$iv;
                                        if (55296 <= var25_28 && 57343 >= var25_28) {
                                            it$iv = 65533;
                                            $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                            c = it$iv;
                                            $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                            $i$f$isIsoControl = j;
                                            j = $i$f$isIsoControl + 1;
                                            if ($i$f$isIsoControl == codePointCount) {
                                                return charCount;
                                            }
                                            if (c != 10 && c != 13) {
                                                $i$f$isIsoControl = 0;
                                                var13_13 = c;
                                                if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                            }
                                            if (c == 65533) {
                                                return -1;
                                            }
                                            charCount += c < 65536 ? 1 : 2;
                                            var19_22 = Unit.INSTANCE;
                                            v4 = var17_20;
                                        } else if (codePoint$iv$iv < 65536) {
                                            it$iv = 65533;
                                            $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                            c = it$iv;
                                            $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                            $i$f$isIsoControl = j;
                                            j = $i$f$isIsoControl + 1;
                                            if ($i$f$isIsoControl == codePointCount) {
                                                return charCount;
                                            }
                                            if (c != 10 && c != 13) {
                                                $i$f$isIsoControl = 0;
                                                var13_13 = c;
                                                if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                            }
                                            if (c == 65533) {
                                                return -1;
                                            }
                                            charCount += c < 65536 ? 1 : 2;
                                            var19_22 = Unit.INSTANCE;
                                            v4 = var17_20;
                                        } else {
                                            it$iv = codePoint$iv$iv;
                                            $i$a$-process4Utf8Bytes-Utf8$processUtf8CodePoints$3$iv = false;
                                            c = it$iv;
                                            $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                                            $i$f$isIsoControl = j;
                                            j = $i$f$isIsoControl + 1;
                                            if ($i$f$isIsoControl == codePointCount) {
                                                return charCount;
                                            }
                                            if (c != 10 && c != 13) {
                                                $i$f$isIsoControl = 0;
                                                var13_13 = c;
                                                if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                                            }
                                            if (c == 65533) {
                                                return -1;
                                            }
                                            charCount += c < 65536 ? 1 : 2;
                                            var19_22 = Unit.INSTANCE;
                                            v4 = var17_20;
                                        }
                                    }
                                    v5 = 4;
                                }
                            }
                        }
                    }
                    index$iv = v4 + v5;
                    continue;
                }
                c = 65533;
                $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                $i$f$isIsoControl = j;
                j = $i$f$isIsoControl + 1;
                if ($i$f$isIsoControl == codePointCount) {
                    return charCount;
                }
                if (c != 10 && c != 13) {
                    $i$f$isIsoControl = 0;
                    var13_13 = c;
                    if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                }
                if (c == 65533) {
                    return -1;
                }
                charCount += c < 65536 ? 1 : 2;
                ++index$iv;
                continue;
            }
            while (true) {
                if (index$iv < endIndex$iv && $this$processUtf8CodePoints$iv[index$iv] >= 0) ** break;
                continue block0;
                c = $this$processUtf8CodePoints$iv[index$iv++];
                $i$a$-processUtf8CodePoints-ByteStringKt$codePointIndexToCharIndex$1 = false;
                $i$f$isIsoControl = j;
                j = $i$f$isIsoControl + 1;
                if ($i$f$isIsoControl == codePointCount) {
                    return charCount;
                }
                if (c != 10 && c != 13) {
                    $i$f$isIsoControl = 0;
                    var13_13 = c;
                    if ((0 <= var13_13 && 31 >= var13_13 || 127 <= (var13_13 = c) && 159 >= var13_13) != false) return -1;
                }
                if (c == 65533) {
                    return -1;
                }
                charCount += c < 65536 ? 1 : 2;
            }
            break;
        }
    }
}

