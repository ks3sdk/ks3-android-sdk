package com.loopj.android.http;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Base64OutputStream extends FilterOutputStream
{

	private final Base64.Coder coder;
	private final int flags;
	private byte[] buffer = null;
	private int bpos = 0;

	private static byte[] EMPTY = new byte[0];

	public Base64OutputStream(OutputStream out, int flags)
	{

		this(out, flags, true);
	}

	public Base64OutputStream(OutputStream out, int flags, boolean encode)
	{

		super(out);
		this.flags = flags;
		if (encode)
			this.coder = new Base64.Encoder(flags, null);
		else
			this.coder = new Base64.Decoder(flags, null);
	}

	@Override
	public void write(int b)
			throws IOException
	{

		if (this.buffer == null) {
			this.buffer = new byte[1024];
		}
		if (this.bpos >= this.buffer.length)
		{
			internalWrite(this.buffer, 0, this.bpos, false);
			this.bpos = 0;
		}
		this.buffer[(this.bpos++)] = (byte) b;
	}

	private void flushBuffer()
			throws IOException
	{

		if (this.bpos > 0) {
			internalWrite(this.buffer, 0, this.bpos, false);
			this.bpos = 0;
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{

		if (len <= 0)
			return;
		flushBuffer();
		internalWrite(b, off, len, false);
	}

	@Override
	public void close() throws IOException
	{

		IOException thrown = null;
		try {
			flushBuffer();
			internalWrite(EMPTY, 0, 0, true);
		} catch (IOException e) {
			thrown = e;
		}
		try
		{
			if ((this.flags & 0x10) == 0)
				this.out.close();
			else
				this.out.flush();
		} catch (IOException e) {
			if (thrown != null) {
				thrown = e;
			}
		}

		if (thrown != null)
			throw thrown;
	}

	private void internalWrite(byte[] b, int off, int len, boolean finish)
			throws IOException
	{

		this.coder.output = embiggen(this.coder.output, this.coder.maxOutputSize(len));
		if (!this.coder.process(b, off, len, finish)) {
			throw new Base64DataException("bad base-64");
		}
		this.out.write(this.coder.output, 0, this.coder.op);
	}

	private byte[] embiggen(byte[] b, int len)
	{

		if ((b == null) || (b.length < len)) {
			return new byte[len];
		}
		return b;
	}
}
