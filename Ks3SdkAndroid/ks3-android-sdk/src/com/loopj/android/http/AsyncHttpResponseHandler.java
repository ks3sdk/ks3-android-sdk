/*     */ package com.loopj.android.http;
/*     */ 
/*     */ import android.os.Handler;
/*     */ import android.os.Looper;
/*     */ import android.os.Message;
/*     */ import android.util.Log;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URI;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.client.HttpResponseException;
/*     */ import org.apache.http.util.ByteArrayBuffer;
/*     */ 
/*     */ public abstract class AsyncHttpResponseHandler
/*     */   implements ResponseHandlerInterface
/*     */ {
/*     */   private static final String LOG_TAG = "AsyncHttpResponseHandler";
/*     */   protected static final int SUCCESS_MESSAGE = 0;
/*     */   protected static final int FAILURE_MESSAGE = 1;
/*     */   protected static final int START_MESSAGE = 2;
/*     */   protected static final int FINISH_MESSAGE = 3;
/*     */   protected static final int PROGRESS_MESSAGE = 4;
/*     */   protected static final int RETRY_MESSAGE = 5;
/*     */   protected static final int CANCEL_MESSAGE = 6;
/*     */   protected static final int BUFFER_SIZE = 4096;
/*     */   public static final String DEFAULT_CHARSET = "UTF-8";
/*     */   public static final String UTF8_BOM = "﻿";
/*  97 */   private String responseCharset = "UTF-8";
/*     */   private Handler handler;
/*     */   private boolean useSynchronousMode;
/* 101 */   private URI requestURI = null;
/* 102 */   private Header[] requestHeaders = null;
/* 103 */   private Looper looper = null;
/*     */ 
/*     */   public URI getRequestURI()
/*     */   {
/* 107 */     return this.requestURI;
/*     */   }
/*     */ 
/*     */   public Header[] getRequestHeaders()
/*     */   {
/* 112 */     return this.requestHeaders;
/*     */   }
/*     */ 
/*     */   public void setRequestURI(URI requestURI)
/*     */   {
/* 117 */     this.requestURI = requestURI;
/*     */   }
/*     */ 
/*     */   public void setRequestHeaders(Header[] requestHeaders)
/*     */   {
/* 122 */     this.requestHeaders = requestHeaders;
/*     */   }
/*     */ 
/*     */   public boolean getUseSynchronousMode()
/*     */   {
/* 144 */     return this.useSynchronousMode;
/*     */   }
/*     */ 
/*     */   public void setUseSynchronousMode(boolean sync)
/*     */   {
/* 150 */     if ((!sync) && (this.looper == null)) {
/* 151 */       sync = true;
/* 152 */       Log.w("AsyncHttpResponseHandler", "Current thread has not called Looper.prepare(). Forcing synchronous mode.");
/*     */     }
/*     */ 
/* 156 */     if ((!sync) && (this.handler == null))
/*     */     {
/* 158 */       this.handler = new ResponderHandler(this, this.looper);
/* 159 */     } else if ((sync) && (this.handler != null))
/*     */     {
/* 161 */       this.handler = null;
/*     */     }
/*     */ 
/* 164 */     this.useSynchronousMode = sync;
/*     */   }
/*     */ 
/*     */   public void setCharset(String charset)
/*     */   {
/* 174 */     this.responseCharset = charset;
/*     */   }
/*     */ 
/*     */   public String getCharset() {
/* 178 */     return this.responseCharset == null ? "UTF-8" : this.responseCharset;
/*     */   }
/*     */ 
/*     */   public AsyncHttpResponseHandler()
/*     */   {
/* 185 */     this(null);
/*     */   }
/*     */ 
/*     */   public AsyncHttpResponseHandler(Looper looper)
/*     */   {
/* 196 */     this.looper = (looper == null ? Looper.myLooper() : looper);
/*     */ 
/* 198 */     setUseSynchronousMode(false);
/*     */   }
/*     */ 
/*     */   public void onProgress(int bytesWritten, int totalSize)
/*     */   {
/* 208 */     Log.v("AsyncHttpResponseHandler", String.format("Progress %d from %d (%2.0f%%)", new Object[] { Integer.valueOf(bytesWritten), Integer.valueOf(totalSize), Double.valueOf(totalSize > 0 ? bytesWritten * 1.0D / totalSize * 100.0D : -1.0D) }));
/*     */   }
/*     */ 
/*     */   public void onStart()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onFinish()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response)
/*     */   {
/*     */   }
/*     */ 
/*     */   public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader, byte[] paramArrayOfByte);
/*     */ 
/*     */   public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader, byte[] paramArrayOfByte, Throwable paramThrowable);
/*     */ 
/*     */   public void onRetry(int retryNo)
/*     */   {
/* 261 */     Log.d("AsyncHttpResponseHandler", String.format("Request retry no. %d", new Object[] { Integer.valueOf(retryNo) }));
/*     */   }
/*     */ 
/*     */   public void onCancel() {
/* 265 */     Log.d("AsyncHttpResponseHandler", "Request got cancelled");
/*     */   }
/*     */ 
/*     */   public final void sendProgressMessage(int bytesWritten, int bytesTotal)
/*     */   {
/* 270 */     sendMessage(obtainMessage(4, new Object[] { Integer.valueOf(bytesWritten), Integer.valueOf(bytesTotal) }));
/*     */   }
/*     */ 
/*     */   public final void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBytes)
/*     */   {
/* 275 */     sendMessage(obtainMessage(0, new Object[] { Integer.valueOf(statusCode), headers, responseBytes }));
/*     */   }
/*     */ 
/*     */   public final void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable)
/*     */   {
/* 280 */     sendMessage(obtainMessage(1, new Object[] { Integer.valueOf(statusCode), headers, responseBody, throwable }));
/*     */   }
/*     */ 
/*     */   public final void sendStartMessage()
/*     */   {
/* 285 */     sendMessage(obtainMessage(2, null));
/*     */   }
/*     */ 
/*     */   public final void sendFinishMessage()
/*     */   {
/* 290 */     sendMessage(obtainMessage(3, null));
/*     */   }
/*     */ 
/*     */   public final void sendRetryMessage(int retryNo)
/*     */   {
/* 295 */     sendMessage(obtainMessage(5, new Object[] { Integer.valueOf(retryNo) }));
/*     */   }
/*     */ 
/*     */   public final void sendCancelMessage()
/*     */   {
/* 300 */     sendMessage(obtainMessage(6, null));
/*     */   }
/*     */ 
/*     */   protected void handleMessage(Message message)
/*     */   {
/*     */     Object[] response;
/* 307 */     switch (message.what) {
/*     */     case 0:
/* 309 */       response = (Object[])(Object[])message.obj;
/* 310 */       if ((response != null) && (response.length >= 3))
/* 311 */         onSuccess(((Integer)response[0]).intValue(), (Header[])(Header[])response[1], (byte[])(byte[])response[2]);
/*     */       else {
/* 313 */         Log.e("AsyncHttpResponseHandler", "SUCCESS_MESSAGE didn't got enough params");
/*     */       }
/* 315 */       break;
/*     */     case 1:
/* 317 */       response = (Object[])(Object[])message.obj;
/* 318 */       if ((response != null) && (response.length >= 4))
/* 319 */         onFailure(((Integer)response[0]).intValue(), (Header[])(Header[])response[1], (byte[])(byte[])response[2], (Throwable)response[3]);
/*     */       else {
/* 321 */         Log.e("AsyncHttpResponseHandler", "FAILURE_MESSAGE didn't got enough params");
/*     */       }
/* 323 */       break;
/*     */     case 2:
/* 325 */       onStart();
/* 326 */       break;
/*     */     case 3:
/* 328 */       onFinish();
/* 329 */       break;
/*     */     case 4:
/* 331 */       response = (Object[])(Object[])message.obj;
/* 332 */       if ((response != null) && (response.length >= 2))
/*     */         try {
/* 334 */           onProgress(((Integer)response[0]).intValue(), ((Integer)response[1]).intValue());
/*     */         } catch (Throwable t) {
/* 336 */           Log.e("AsyncHttpResponseHandler", "custom onProgress contains an error", t);
/*     */         }
/*     */       else {
/* 339 */         Log.e("AsyncHttpResponseHandler", "PROGRESS_MESSAGE didn't got enough params");
/*     */       }
/* 341 */       break;
/*     */     case 5:
/* 343 */       response = (Object[])(Object[])message.obj;
/* 344 */       if ((response != null) && (response.length == 1))
/* 345 */         onRetry(((Integer)response[0]).intValue());
/*     */       else {
/* 347 */         Log.e("AsyncHttpResponseHandler", "RETRY_MESSAGE didn't get enough params");
/*     */       }
/* 349 */       break;
/*     */     case 6:
/* 351 */       onCancel();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void sendMessage(Message msg)
/*     */   {
/* 357 */     if ((getUseSynchronousMode()) || (this.handler == null)) {
/* 358 */       handleMessage(msg);
/* 359 */     } else if (!Thread.currentThread().isInterrupted()) {
/* 360 */       AssertUtils.asserts(this.handler != null, "handler should not be null!");
/* 361 */       this.handler.sendMessage(msg);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void postRunnable(Runnable runnable)
/*     */   {
/* 371 */     if (runnable != null)
/* 372 */       if ((getUseSynchronousMode()) || (this.handler == null))
/*     */       {
/* 374 */         runnable.run();
/*     */       }
/*     */       else {
/* 377 */         AssertUtils.asserts(this.handler != null, "handler should not be null!");
/* 378 */         this.handler.post(runnable);
/*     */       }
/*     */   }
/*     */ 
/*     */   protected Message obtainMessage(int responseMessageId, Object responseMessageData)
/*     */   {
/* 391 */     return Message.obtain(this.handler, responseMessageId, responseMessageData);
/*     */   }
/*     */ 
/*     */   public void sendResponseMessage(HttpResponse response)
/*     */     throws IOException
/*     */   {
/* 397 */     if (!Thread.currentThread().isInterrupted()) {
/* 398 */       StatusLine status = response.getStatusLine();
/*     */ 
/* 400 */       byte[] responseBody = getResponseData(response.getEntity());
/*     */ 
/* 402 */       if (!Thread.currentThread().isInterrupted())
/* 403 */         if (status.getStatusCode() >= 300)
/* 404 */           sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), responseBody, new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
/*     */         else
/* 406 */           sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), responseBody);
/*     */     }
/*     */   }
/*     */ 
/*     */   byte[] getResponseData(HttpEntity entity)
/*     */     throws IOException
/*     */   {
/* 420 */     byte[] responseBody = null;
/* 421 */     if (entity != null) {
/* 422 */       InputStream instream = entity.getContent();
/* 423 */       if (instream != null) {
/* 424 */         long contentLength = entity.getContentLength();
/* 425 */         if (contentLength > 2147483647L) {
/* 426 */           throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
/*     */         }
/* 428 */         int buffersize = contentLength <= 0L ? 4096 : (int)contentLength;
/*     */         try {
/* 430 */           ByteArrayBuffer buffer = new ByteArrayBuffer(buffersize);
/*     */           try {
/* 432 */             byte[] tmp = new byte[4096];
/* 433 */             int count = 0;
/*     */             int l;
/* 435 */             while (((l = instream.read(tmp)) != -1) && (!Thread.currentThread().isInterrupted())) {
/* 436 */               count += l;
/* 437 */               buffer.append(tmp, 0, l);
/* 438 */               sendProgressMessage(count, (int)(contentLength <= 0L ? 1L : contentLength));
/*     */             }
/*     */           } finally {
/* 441 */             AsyncHttpClient.silentCloseInputStream(instream);
/* 442 */             AsyncHttpClient.endEntityViaReflection(entity);
/*     */           }
/* 444 */           responseBody = buffer.toByteArray();
/*     */         } catch (OutOfMemoryError e) {
/* 446 */           System.gc();
/* 447 */           throw new IOException("File too large to fit into available memory");
/*     */         }
/*     */       }
/*     */     }
/* 451 */     return responseBody;
/*     */   }
/*     */ 
/*     */   private static class ResponderHandler extends Handler
/*     */   {
/*     */     private final AsyncHttpResponseHandler mResponder;
/*     */ 
/*     */     ResponderHandler(AsyncHttpResponseHandler mResponder, Looper looper)
/*     */     {
/* 132 */       super();
/* 133 */       this.mResponder = mResponder;
/*     */     }
/*     */ 
/*     */     public void handleMessage(Message msg)
/*     */     {
/* 138 */       this.mResponder.handleMessage(msg);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tangluo\Desktop\android-async-http-1.4.6.jar
 * Qualified Name:     com.loopj.android.http.AsyncHttpResponseHandler
 * JD-Core Version:    0.6.0
 */