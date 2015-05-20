package com.loopj.android.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import android.text.TextUtils;
import android.util.Log;

class SimpleMultipartEntity
		implements HttpEntity
{

	private static final String LOG_TAG = "SimpleMultipartEntity";
	private static final String STR_CR_LF = "\r\n";
	private static final byte[] CR_LF = "\r\n".getBytes();
	private static final byte[] TRANSFER_ENCODING_BINARY = "Content-Transfer-Encoding: binary\r\n".getBytes();

	private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private final String boundary;
	private final byte[] boundaryLine;
	private final byte[] boundaryEnd;
	private boolean isRepeatable;
	private final List<FilePart> fileParts = new ArrayList();

	private final ByteArrayOutputStream out = new ByteArrayOutputStream();
	private final ResponseHandlerInterface progressHandler;
	private int bytesWritten;
	private int totalSize;

	public SimpleMultipartEntity(ResponseHandlerInterface progressHandler)
	{

		StringBuilder buf = new StringBuilder();
		Random rand = new Random();
		for (int i = 0; i < 30; i++) {
			buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
		}

		this.boundary = buf.toString();
		this.boundaryLine = new StringBuilder().append("--").append(this.boundary).append("\r\n").toString().getBytes();
		this.boundaryEnd = new StringBuilder().append("--").append(this.boundary).append("--").append("\r\n").toString().getBytes();

		this.progressHandler = progressHandler;
	}

	public void addPart(String key, String value, String contentType) {

		try {
			this.out.write(this.boundaryLine);
			this.out.write(createContentDisposition(key));
			this.out.write(createContentType(contentType));
			this.out.write(CR_LF);
			this.out.write(value.getBytes());
			this.out.write(CR_LF);
		} catch (IOException e) {
			Log.e("SimpleMultipartEntity", "addPart ByteArrayOutputStream exception", e);
		}
	}

	public void addPartWithCharset(String key, String value, String charset) {

		if (charset == null)
			charset = "UTF-8";
		addPart(key, value, new StringBuilder().append("text/plain; charset=").append(charset).toString());
	}

	public void addPart(String key, String value) {

		addPartWithCharset(key, value, null);
	}

	public void addPart(String key, File file) {

		addPart(key, file, null);
	}

	public void addPart(String key, File file, String type) {

		this.fileParts.add(new FilePart(key, file, normalizeContentType(type)));
	}

	public void addPart(String key, File file, String type, String customFileName) {

		this.fileParts.add(new FilePart(key, file, normalizeContentType(type), customFileName));
	}

	public void addPart(String key, String streamName, InputStream inputStream, String type)
			throws IOException
	{

		this.out.write(this.boundaryLine);

		this.out.write(createContentDisposition(key, streamName));
		this.out.write(createContentType(type));
		this.out.write(TRANSFER_ENCODING_BINARY);
		this.out.write(CR_LF);

		byte[] tmp = new byte[4096];
		int l;
		while ((l = inputStream.read(tmp)) != -1) {
			this.out.write(tmp, 0, l);
		}

		this.out.write(CR_LF);
		this.out.flush();

		AsyncHttpClient.silentCloseOutputStream(this.out);
	}

	private String normalizeContentType(String type) {

		return type == null ? "application/octet-stream" : type;
	}

	private byte[] createContentType(String type) {

		String result = new StringBuilder().append("Content-Type: ").append(normalizeContentType(type)).append("\r\n").toString();
		return result.getBytes();
	}

	private byte[] createContentDisposition(String key) {

		return new StringBuilder().append("Content-Disposition: form-data; name=\"").append(key).append("\"").append("\r\n").toString().getBytes();
	}

	private byte[] createContentDisposition(String key, String fileName)
	{

		return new StringBuilder().append("Content-Disposition: form-data; name=\"").append(key).append("\"").append("; filename=\"").append(fileName).append("\"").append("\r\n").toString().getBytes();
	}

	private void updateProgress(int count)
	{

		this.bytesWritten += count;
		this.progressHandler.sendProgressMessage(this.bytesWritten, this.totalSize);
	}

	@Override
	public long getContentLength()
	{

		long contentLen = this.out.size();
		for (FilePart filePart : this.fileParts) {
			long len = filePart.getTotalLength();
			if (len < 0L) {
				return -1L;
			}
			contentLen += len;
		}
		contentLen += this.boundaryEnd.length;
		return contentLen;
	}

	@Override
	public Header getContentType()
	{

		return new BasicHeader("Content-Type", new StringBuilder().append("multipart/form-data; boundary=").append(this.boundary).toString());
	}

	@Override
	public boolean isChunked()
	{

		return false;
	}

	public void setIsRepeatable(boolean isRepeatable) {

		this.isRepeatable = isRepeatable;
	}

	@Override
	public boolean isRepeatable()
	{

		return this.isRepeatable;
	}

	@Override
	public boolean isStreaming()
	{

		return false;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException
	{

		this.bytesWritten = 0;
		this.totalSize = (int) getContentLength();
		this.out.writeTo(outstream);
		updateProgress(this.out.size());

		for (FilePart filePart : this.fileParts) {
			filePart.writeTo(outstream);
		}
		outstream.write(this.boundaryEnd);
		updateProgress(this.boundaryEnd.length);
	}

	@Override
	public Header getContentEncoding()
	{

		return null;
	}

	@Override
	public void consumeContent() throws IOException, UnsupportedOperationException
	{

		if (isStreaming())
			throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
	}

	@Override
	public InputStream getContent()
			throws IOException, UnsupportedOperationException
	{

		throw new UnsupportedOperationException("getContent() is not supported. Use writeTo() instead.");
	}

	private class FilePart
	{

		public File file;
		public byte[] header;

		public FilePart(String key, File file, String type, String customFileName)
		{

			this.header = createHeader(key, TextUtils.isEmpty(customFileName) ? file.getName() : customFileName, type);
			this.file = file;
		}

		public FilePart(String key, File file, String type) {

			this.header = createHeader(key, file.getName(), type);
			this.file = file;
		}

		private byte[] createHeader(String key, String filename, String type) {

			ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
			try {
				headerStream.write(SimpleMultipartEntity.this.boundaryLine);

				headerStream.write(SimpleMultipartEntity.this.createContentDisposition(key, filename));
				headerStream.write(SimpleMultipartEntity.this.createContentType(type));
				headerStream.write(SimpleMultipartEntity.TRANSFER_ENCODING_BINARY);
				headerStream.write(SimpleMultipartEntity.CR_LF);
			} catch (IOException e) {
				Log.e("SimpleMultipartEntity", "createHeader ByteArrayOutputStream exception", e);
			}
			return headerStream.toByteArray();
		}

		public long getTotalLength() {

			long streamLength = this.file.length() + SimpleMultipartEntity.CR_LF.length;
			return this.header.length + streamLength;
		}

		public void writeTo(OutputStream out) throws IOException {

			out.write(this.header);
			SimpleMultipartEntity.this.updateProgress(this.header.length);

			FileInputStream inputStream = new FileInputStream(this.file);
			byte[] tmp = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(tmp)) != -1) {
				out.write(tmp, 0, bytesRead);
				SimpleMultipartEntity.this.updateProgress(bytesRead);
			}
			out.write(SimpleMultipartEntity.CR_LF);
			SimpleMultipartEntity.this.updateProgress(SimpleMultipartEntity.CR_LF.length);
			out.flush();
			AsyncHttpClient.silentCloseInputStream(inputStream);
		}
	}
}
