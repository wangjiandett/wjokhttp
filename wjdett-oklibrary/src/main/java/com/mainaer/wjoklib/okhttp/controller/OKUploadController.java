/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjoklib.okhttp.controller;

import com.mainaer.wjoklib.okhttp.utils.WLog;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * 文件图片上传基类
 *
 * @author wangjian
 * @date 2016/3/25.
 */
public abstract class OKUploadController<Listener> extends OKHttpController<Listener> {
    
    
    private static final String TAG = "OKUploadController";
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/octet-stream");
    
    public OKUploadController() {
        super();
    }
    
    public OKUploadController(Listener l) {
        super();
        setListener(l);
    }
    
    protected abstract class BaseUpLoadTask<Output> extends LoadTask<HashMap<String, Object>, Output> {
        
        @Override
        protected RequestBody postBody(HashMap<String, Object> params) {
            // 设置请求体
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            if (params != null && params.size() > 0) {
                for (String key : params.keySet()) {
                    Object object = params.get(key);
                    if (object != null) {
                        if (!(object instanceof File)) {
                            // add params
                            builder.addFormDataPart(key, object.toString());
                        }
                        else {
                            // add file
                            File file = (File) object;
                            // need progress callback
                            if (mListener instanceof ProgressListener) {
                                ProgressListener callback = (ProgressListener) mListener;
                                builder.addFormDataPart(key, file.getName(),
                                    createProgressRequestBody(MEDIA_TYPE, file, callback));
                            }
                            else {
                                // do not need progress callback
                                builder.addFormDataPart(key, file.getName(), RequestBody.create(MEDIA_TYPE, file));
                            }
                        }
                    }
                }
            }
            
            return builder.build();
        }
    }
    
    /**
     * 根据文件名获取类型
     *
     * @param filename
     * @return
     */
    private String guessMimeType(String filename) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(filename);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }
    
    /**
     * 创建带进度的RequestBody
     *
     * @param contentType MediaType
     * @param file        准备上传的文件
     * @param callBack    回调
     * @return
     */
    private RequestBody createProgressRequestBody(final MediaType contentType, final File file,
                                                  final ProgressListener callBack) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }
            
            @Override
            public long contentLength() {
                return file.length();
            }
            
            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buf = new Buffer();
                    long remaining = contentLength();
                    
                    float downloadProgress = 0;
                    float last = 0;
                    long current = 0;
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        current += readCount;
                        
                        downloadProgress = (float) (current * 100 / remaining);
                        WLog.d(TAG, "current------>" + current + " ,progress------>" + downloadProgress);
                        // 控制更新进度100次
                        if (downloadProgress - last >= 1) {
                            callBack.onProgress(remaining, (int) downloadProgress);
                            last = downloadProgress;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
