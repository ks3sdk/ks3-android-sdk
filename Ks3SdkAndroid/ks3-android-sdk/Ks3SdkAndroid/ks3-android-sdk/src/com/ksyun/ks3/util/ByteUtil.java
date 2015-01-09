package com.ksyun.ks3.util;

public class ByteUtil {
	// For byte array compare
	public static int compareTo(final byte[] left, final byte[] right) {
		return compareTo(left, 0, left.length, right, 0, right.length);
	}

	public static int compareTo(byte[] buffer1, int offset1, int length1,
			byte[] buffer2, int offset2, int length2) {
		// Bring WritableComparator code local
		int end1 = offset1 + length1;
		int end2 = offset2 + length2;
		for (int i = offset1, j = offset2; i < end1 && j < end2; i++, j++) {
			int a = (buffer1[i] & 0xff);
			int b = (buffer2[j] & 0xff);
			if (a != b) {
				return a - b;
			}
		}
		return length1 - length2;
	}
}
