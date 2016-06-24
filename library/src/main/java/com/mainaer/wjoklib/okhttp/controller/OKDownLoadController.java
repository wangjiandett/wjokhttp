/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjoklib.okhttp.controller;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.mainaer.wjoklib.okhttp.exception.OkHttpError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 文件下载父类，需要下载请继承后实现
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

        private ResponseBody mBody;

        private File mFile;

        private Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg != null) {
                    if (msg.what == DOWN_PROGRESS && mBody != null) {
                        float progress = (float) msg.obj;
                        inProgress(progress, mBody.contentLength());
                    }
                    else if (msg.what == DOWN_SUCCESS) {
                        File file = (File) msg.obj;
                        onSuccess(file);
                    }
                    else if (msg.what == DOWN_FAILURE) {
                        onError((OkHttpError) msg.obj);
                    }
                }
            }
        };

        private void postMessage(int what, Object obj) {
            Message message = Message.obtain();
            message.what = what;
            message.obj = obj;
            mHandler.sendMessage(message);
        }

        @Override
        protected void convertData(Response response) {
            if (response != null) {
                ResponseBody body = response.body();
                mBody = body;
                String fileDir = getFileDir();
                String fileName = getFileName();
                try {
                    if (!TextUtils.isEmpty(fileDir)) {
                        // 获取文件名
                        String url = getUrl().getUrl().trim();
                        if (!TextUtils.isEmpty(url) && TextUtils.isEmpty(fileName)) {
                            fileName = url.substring(url.lastIndexOf("/"));
                        }

                        mFile = saveFile(body.byteStream(), body.contentLength(), fileDir, fileName);
                        if (mFile != null) {
                            postMessage(DOWN_SUCCESS, mFile);
                        }
                    }
                    else {
                        postMessage(DOWN_FAILURE, "the methord getFileDir() or getFileName() is null");
                    }
                } catch (Exception e) {
                    postMessage(DOWN_FAILURE, new OkHttpError(e));
                }
            }
        }

        @Override
        protected abstract void onSuccess(File file);

        protected abstract void inProgress(float progress, long total);

        protected abstract String getFileDir();

        /**
         * 自定义文件名
         *
         * @return
         */
        protected String getFileName() {
            return null;
        }

        private File saveFile(InputStream inputStream, final long contentLength, String destFileDir,
                              String destFileName) throws IOException {
            byte[] buf = new byte[2048];
            int length = 0;
            FileOutputStream fos = null;
            try {
                long sum = 0;

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
                    float prgress = finalSum * 1.0f / contentLength;
                    postMessage(DOWN_PROGRESS, prgress);
                }
                fos.flush();
                return file;
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                }
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

}