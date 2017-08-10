/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjoklib.okhttp.controller;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.mainaer.wjoklib.okhttp.exception.OkHttpError;
import com.mainaer.wjoklib.okhttp.utils.WLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 文件下载父类
 *
 * @author wangjian
 * @date 2016/3/28.
 */
public abstract class OKDownLoadController<Listener> extends OKHttpController<Listener> {
    
    private final static int DOWN_PROGRESS = 1;
    private final static int DOWN_SUCCESS = 2;
    private final static int DOWN_FAILURE = 3;
    
    public OKDownLoadController() {
        super();
    }
    
    public OKDownLoadController(Listener l) {
        super();
        setListener(l);
    }
    
    protected abstract class BaseDownLoadTask<Input> extends LoadTask<Input, File> {
        
        private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg != null) {
                    if (msg.what == DOWN_PROGRESS) {
                        long total = (long) msg.obj;
                        int progress = msg.arg1;
                        inProgress(progress, total);
                    }
                    else if (msg.what == DOWN_SUCCESS) {
                        File file = (File) msg.obj;
                        onSuccess(file);
                    }
                    else if (msg.what == DOWN_FAILURE) {
                        onError((OkHttpError) msg.obj);
                    }
                }
                return true;
            }
        });
        
        private void postMessage(int what, Object obj) {
            Message message = Message.obtain();
            message.what = what;
            message.obj = obj;
            mHandler.sendMessage(message);
        }
        
        private void postProgress(int what, long total, int progress) {
            Message message = Message.obtain();
            message.what = what;
            message.obj = total;
            message.arg1 = progress;
            mHandler.sendMessage(message);
        }
        
        @Override
        protected void convertData(Response response) {
            if (response != null) {
                ResponseBody body = response.body();
                String fileDir = getFileDir();
                String fileName = getFileName();
                try {
                    if (!TextUtils.isEmpty(fileDir)) {
                        // 获取文件名
                        String url = getUrl().getUrl().trim();
                        if (!TextUtils.isEmpty(url) && TextUtils.isEmpty(fileName)) {
                            fileName = url.substring(url.lastIndexOf("/"));
                        }
                        
                        readAndSave2File(body.byteStream(), body.contentLength(), fileDir, fileName);
                    }
                    else {
                        postMessage(DOWN_FAILURE, new OkHttpError("the methord getFileDir() is null"));
                    }
                } catch (Exception e) {
                    postMessage(DOWN_FAILURE, new OkHttpError(e));
                }
            }
        }
        
        /**
         * 自定义文件名
         *
         * @return
         */
        protected String getFileName() {
            return null;
        }
        
        private void readAndSave2File(InputStream inputStream, final long contentLength, String destFileDir,
                                      String destFileName) throws IOException {
            byte[] buf = new byte[2048];
            int length = 0;
            FileOutputStream fos = null;
            try {
                long sum = 0;
                float times = 0;
                
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);
                fos = new FileOutputStream(file);
                while ((length = inputStream.read(buf)) != -1) {
                    sum += length;
                    fos.write(buf, 0, length);
                    long finalSum = sum;
                    float prgress = (float) (finalSum * 100 / contentLength);
                    WLog.d("sum=" + sum + " ,prgress=" + prgress);
                    // refresh 100 times
                    if (prgress - times >= 1) {
                        postProgress(DOWN_PROGRESS, contentLength, (int) prgress);
                        times = prgress;
                    }
                }
                fos.flush();
                postMessage(DOWN_SUCCESS, file);
            } catch (Exception e) {
                WLog.e(e.toString());
                postMessage(DOWN_FAILURE, new OkHttpError(e));
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    WLog.d(e.getMessage());
                }
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    WLog.d(e.getMessage());
                }
            }
        }
    
        @Override
        protected abstract void onSuccess(File file);
    
        protected abstract void inProgress(float progress, long total);
    
        protected abstract String getFileDir();
    }
}