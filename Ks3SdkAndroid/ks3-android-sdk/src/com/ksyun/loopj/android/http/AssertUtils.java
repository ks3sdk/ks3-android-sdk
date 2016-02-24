package com.ksyun.loopj.android.http;

class AssertUtils
{

	public static void asserts(boolean expression, String failedMessage)
	{

		if (!expression)
			throw new AssertionError(failedMessage);
	}
}
