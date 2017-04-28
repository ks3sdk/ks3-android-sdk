package com.ks3.demo.main;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.Part;
import com.ksyun.ks3.model.PartETag;
import com.ksyun.ks3.model.result.CompleteMultipartUploadResult;
import com.ksyun.ks3.model.result.InitiateMultipartUploadResult;
import com.ksyun.ks3.model.result.ListPartsResult;
import com.ksyun.ks3.services.Ks3Client;
import com.ksyun.ks3.services.handler.CompleteMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.InitiateMultipartUploadResponceHandler;
import com.ksyun.ks3.services.handler.ListPartsResponseHandler;
import com.ksyun.ks3.services.handler.UploadPartResponceHandler;
import com.ksyun.ks3.services.request.AbortMultipartUploadRequest;
import com.ksyun.ks3.services.request.InitiateMultipartUploadRequest;
import com.ksyun.ks3.services.request.ListPartsRequest;
import com.ksyun.ks3.services.request.UploadPartRequest;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhaotao on 4/27/17.
 */

public class MultiUploader {
    final private String TAG = "MultiUploader";

    private String bucketName;
    private String key;
    private File file;
    private long partSize = 5*1024*1024; // 5MB
    private String uploadId;
    private Ks3Client client;
    final static private int INIT_DONE = 0;
    final static private int PARTS_DONE = 1;
    final static private int COMPLETE_DONE = 2;
    final static private int GET_UPLOADED_DONE=3;

    final List<PartETag> doneParts = Collections.synchronizedList(new ArrayList());

    private void create(Ks3Client client, String bucketName, String key, File file, String uploadId, long partSize){
        this.client = client;
        this.bucketName = bucketName;
        this.key = key;
        this.file = file;
        this.uploadId = uploadId;
        this.partSize = partSize;
    }

    public MultiUploader(Ks3Client client, String bucketName, String key, File file, String uploadId, long partSize){
        create(client, bucketName,key, file, uploadId, partSize);
    }

    public MultiUploader(Ks3Client client, String bucketName, String key, File file, String uploadId){
        create(client, bucketName, key, file, uploadId, partSize);
    }
    public MultiUploader(Ks3Client client, String bucketName, String key, File file){
        create(client, bucketName, key, file, null, partSize);
    }
    public MultiUploader(Ks3Client client, String bucketName, String key, File file, long partSize){
        create(client, bucketName, key, file, null, partSize);
    }

    public String getKey(){
        return key;
    }
    public String getUploadId(){
        return uploadId;
    }

    abstract class MyUploadPartResponceHandler extends UploadPartResponceHandler{
        private String key;
        private int partNo;
        private String uploadId;
        public abstract void onSuccess(int statesCode, Header[] responceHeaders, PartETag result, String key, int partNo, String uploadId);
        public abstract void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable throwable
                , String key, int partNo, String uploadId);
        public abstract void onTaskProgress(double progress, String key, int partNo, String uploadId);

        public MyUploadPartResponceHandler(String key, int partNo, String uploadId){
            this.key = key;
            this.partNo = partNo;
            this.uploadId = uploadId;
        }
        @Override
        public void onTaskProgress(double progress) {
            onTaskProgress(progress, key, partNo, uploadId);
        }
        @Override
        public void onSuccess(int statesCode, Header[] responceHeaders, PartETag result) {
            onSuccess(statesCode, responceHeaders, result, key, partNo, uploadId);
        }
        @Override
        public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable throwable) {
            onFailure(statesCode,error,responceHeaders,response,throwable, key, partNo, uploadId);
        }
    }

    private final MyHandler mHandler = new MyHandler();

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_DONE:
                    uploadParts();
                    break;
                case PARTS_DONE:
                    completeUpload();
                    break;
                case GET_UPLOADED_DONE:
                    List<PartETag> res = (List<PartETag>)msg.obj;
                    reUpload(res);
                    break;
                default:
                    break;
            }
        }
    }

    /*
      return false, if uploadId hasbeen got
     */
    public boolean upload(){
        if(uploadId != null) return false;
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);
        client.initiateMultipartUpload(request, new InitiateMultipartUploadResponceHandler(){
            @Override
            public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
                Log.w(TAG, "init multiupload fail, statesCode="+statesCode, paramThrowable);
            }
            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, InitiateMultipartUploadResult result) {
                uploadId = result.getUploadId();
                Log.d(TAG, "init multiupload success, uploadId="+uploadId + ",key="+key);
                mHandler.sendEmptyMessage(INIT_DONE);
            }
        });
        return true;
    }

    private List<PartETag> convertPart(List<Part> list){
        List<PartETag> res = new ArrayList<PartETag>();
        for(Part p : list) {
            res.add(new PartETag(p.getPartNumber(), p.getETag()));
        }
        return res;
    }
    /*
    Get from ks3 server. call list parts API.
     */
    public void getUploadedParts(){
        final List<PartETag> res = new ArrayList<PartETag>();
        final ListPartsResponseHandler listPartsResponseHandler = new ListPartsResponseHandler() {
            @Override
            public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
                Log.w(TAG, "list parts, statesCode="+statesCode, paramThrowable);
            }
            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, ListPartsResult listPartsResult) {
                res.addAll(convertPart(listPartsResult.getParts()));
                if(! listPartsResult.isTruncated())
                    mHandler.sendMessage(Message.obtain(mHandler, GET_UPLOADED_DONE, res));
                else
                    Log.e(TAG, "File size too largs. You may not use phone to upload");
            }
        };
        client.listParts(bucketName, key, uploadId, listPartsResponseHandler);
    }

    public List<Integer> getLeftParts(List<PartETag> uploadedParts){
        List<Integer> res = new ArrayList<Integer>();
        long start = 0L;
        int partNumber = 1;
        Set<Integer> set = new HashSet<Integer>();
        for(PartETag p : uploadedParts){
            set.add(p.getPartNumber());
        }
        while(start<file.length()){
            if(! set.contains(partNumber))
                res.add(partNumber);
            partNumber++;
            start += partSize;
        }
        return res;
    }

    public void reUpload(){
        if(uploadId == null) {
            Log.i(TAG, "no upload id, cannot reupload");
            return;
        }
        getUploadedParts();
    }

    public void reUpload(final List<PartETag> uploadedParts){
        final List<Integer> leftParts = getLeftParts(uploadedParts);
        final int N = leftParts.size() + uploadedParts.size();
        doneParts.addAll(uploadedParts);
        if(leftParts.isEmpty())
            mHandler.sendEmptyMessage(PARTS_DONE);
        for(Integer partNumber : leftParts){
            long offset = partNumber * partSize - partSize;
            UploadPartRequest uploadPartRequest = new UploadPartRequest(bucketName, key,uploadId, file,
                    offset, partNumber, Math.min(file.length()-offset, partSize));
            client.uploadPart(uploadPartRequest, new MyUploadPartResponceHandler(key, partNumber, uploadId){
                @Override
                public void onSuccess(int statesCode, Header[] responceHeaders, PartETag result, String key, int partNo, String uploadId) {
                    result.setPartNumber(partNo);
                    doneParts.add(result);
                    if(doneParts.size() == N)
                        mHandler.sendEmptyMessage(PARTS_DONE);
                }
                @Override
                public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable throwable,
                                      String key, int partNo, String uploadId) {
                    // you may record this failure info in file, database, or backend
                    Log.w(TAG, "upload part fail, uploadId="+uploadId+",key="+key+",partNo="+partNo, throwable);
                }
                @Override
                public void onTaskProgress(double progress, String key, int partNo, String uploadId) {
                    if(progress>=99)
                        Log.i(TAG, "progress:"+progress+",key="+key+",partNo"+partNo);
                }
            });
        }
    }

    private void uploadParts(){
        reUpload(new ArrayList<PartETag>());
    }

    private void completeUpload(){
        client.completeMultipartUpload(bucketName,key,uploadId,doneParts,new CompleteMultipartUploadResponseHandler(){
            @Override
            public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
                Log.w(TAG, "complete upload fail, statusCode="+statesCode, paramThrowable);
            }
            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, CompleteMultipartUploadResult result) {
                Log.i(TAG, "complete upload, key="+key);
            }
        });
    }
}
