/*
 * Copyright 2014-2016 wjokhttp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mainaer.wjoklib.okhttp.download;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.mainaer.wjoklib.okhttp.download.dao.DownloadDao;
import com.mainaer.wjoklib.okhttp.download.dao.DownloadEntity;
import com.mainaer.wjoklib.okhttp.utils.LoggerUtil;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 下载线程
 *
 * @author wangjian
 * @date 2016/5/13 .
 */
public class DownloadTask implements Runnable {

    private static String FILE_MODE = "rwd";
    private OkHttpClient mClient;

    private RandomAccessFile mDownLoadFile;
    private DownloadEntity dbEntity;
    private DownloadDao mDownloadDao;
    private DownloadTaskListener mListener;

    private Builder mBuilder;
    private String id;// task id
    private long totalSize;// filesize
    private long completedSize; //  Download section has been completed
    private String url;// file url
    private String saveDirPath;// file save path
    private double updateSize; // The database is updated 100 times
    private String fileName; // File name when saving
    private int downloadStatus;

    private int errorCode;

    private DownloadTask(Builder builder) {
        mBuilder = builder;
        mClient = new OkHttpClient();
        this.id = mBuilder.id;
        this.url = mBuilder.url;
        this.saveDirPath = mBuilder.saveDirPath;
        this.fileName = mBuilder.fileName;
        this.downloadStatus = mBuilder.downloadStatus;
        this.mListener = mBuilder.listener;
        // 以kb为计算单位
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        try {
            // 数据库中加载数据
            dbEntity = mDownloadDao.load(id);
            if (dbEntity != null) {
                completedSize = dbEntity.getCompletedSize();
                totalSize = dbEntity.getToolSize();
            }

            // 获得文件路径
            String filepath = getFilePath();
            // 获得下载保存文件
            mDownLoadFile = new RandomAccessFile(filepath, FILE_MODE);

            long fileLength = mDownLoadFile.length();
            if (fileLength < completedSize) {
                completedSize = mDownLoadFile.length();
            }
            // 下载完成，更新数据库数据
            if (fileLength != 0 && totalSize <= fileLength) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
                totalSize = completedSize = fileLength;
                dbEntity = new DownloadEntity(id, totalSize, totalSize, url, saveDirPath, fileName, downloadStatus);
                mDownloadDao.insertOrReplace(dbEntity);
                LoggerUtil.i("=====totalSize===== " + totalSize);
                // 执行finally中的回调
                return;
            }

            // 开始下载
            Request request = new Request.Builder().url(url).header("RANGE",
                "bytes=" + completedSize + "-") // Http value set breakpoints RANGE
                .build();
            // 文件跳转到指定位置开始写入
            mDownLoadFile.seek(completedSize);
            Response response = mClient.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING;
                //onCallBack();
                if (totalSize <= 0) {
                    totalSize = responseBody.contentLength();
                }

                updateSize = totalSize / 100;

                // 获得文件流
                inputStream = responseBody.byteStream();
                bis = new BufferedInputStream(inputStream);
                byte[] buffer = new byte[2 * 1024];
                int length = 0;
                int buffOffset = 0;
                // 开始下载数据库中插入下载信息
                if (dbEntity == null) {
                    dbEntity = new DownloadEntity(id, totalSize, 0L, url, saveDirPath, fileName, downloadStatus);
                    mDownloadDao.insertOrReplace(dbEntity);
                }
                while ((length = bis.read(buffer)) > 0 && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_CANCEL
                    && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                    mDownLoadFile.write(buffer, 0, length);
                    completedSize += length;
                    buffOffset += length;

                    LoggerUtil.i("completedSize=" + completedSize + " ,totalSize=" +
                        totalSize + " ," + "" + "Status=" + downloadStatus);
                    // 下载多少触发一次回调，更新一次数据库
                    // 以kb计算
                    if (buffOffset >= updateSize) {
                        // Update download information database
                        buffOffset = 0;
                        // 支持断点续传时，在往数据库中保存下载信息
                        // 此处会频繁更新数据库
                        // dbEntity.setCompletedSize(completedSize);
                        // mDownloadDao.update(dbEntity);
                        //onDownloading 回调
                        onCallBack();
                    }
                }

                //onDownloading;
                // 防止最后一次不足UPDATE_SIZE，导致percent无法达到100%
                onCallBack();
            }
        } catch (FileNotFoundException e) {
            // file not found
            e.printStackTrace();
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            errorCode = DownloadStatus.DOWNLOAD_ERROR_FILE_NOT_FOUND;
        } catch (IOException e) {
            // io exception
            e.printStackTrace();
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            errorCode = DownloadStatus.DOWNLOAD_ERROR_IO_ERROR;
        } finally {
            if (isDownloadFinish()) {
                onCallBack();
            }

            // 下载后新数据库
            if (dbEntity != null) {
                dbEntity.setCompletedSize(completedSize);
                dbEntity.setDownloadStatus(downloadStatus);
                mDownloadDao.update(dbEntity);
            }

            // 回收资源
            if (bis != null) {
                close(bis);
            }
            if (inputStream != null) {
                close(inputStream);
            }
            if (mDownLoadFile != null) {
                close(mDownLoadFile);
            }
        }
    }

    private boolean isDownloadFinish() {
        boolean finish = false;
        if (totalSize > 0 && completedSize > 0 && totalSize == completedSize) {
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
            finish = true;
        }
        return finish;
    }

    /**
     * 删除数据库文件和已经下载的文件
     */
    public void cancel() {
        mListener.onCancel(DownloadTask.this);
        if (dbEntity != null) {
            mDownloadDao.delete(dbEntity);
            File temp = new File(getFilePath());
            if (temp.exists()) {
                temp.delete();
            }
        }
    }

    /**
     * 分发回调事件到ui层
     */
    private void onCallBack() {
        mHandler.sendEmptyMessage(downloadStatus);
        // 同步manager中的task信息
        DownloadManager.getInstance().updateDownloadTask(this);
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch (code) {
                // 下载失败
                case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                    mListener.onError(DownloadTask.this, errorCode);
                    break;
                // 正在下载
                case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                    mListener.onDownloading(DownloadTask.this, completedSize, totalSize, getDownLoadPercent());
                    break;
                // 取消
                case DownloadStatus.DOWNLOAD_STATUS_CANCEL:
                    cancel();
                    break;
                // 完成
                case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                    mListener.onDownloadSuccess(DownloadTask.this, new File(getFilePath()));
                    break;
                // 停止
                case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                    mListener.onPause(DownloadTask.this, completedSize, totalSize, getDownLoadPercent());
                    break;
            }
        }
    };

    private String getDownLoadPercent() {
        String baifenbi = "0";// 接受百分比的值
        double baiy = completedSize * 1.0;
        double baiz = totalSize * 1.0;
        // 防止分母为0出现NoN
        if (baiz > 0) {
            double fen = (baiy / baiz) * 100;
            //NumberFormat nf = NumberFormat.getPercentInstance();
            //nf.setMinimumFractionDigits(2); //保留到小数点后几位
            // 百分比格式，后面不足2位的用0补齐
            //baifenbi = nf.format(fen);
            //注释掉的也是一种方法
            DecimalFormat df1 = new DecimalFormat("0");//0.00
            baifenbi = df1.format(fen);
        }
        return baifenbi;
    }

    private String getFilePath() {
        // 获得文件名
        if (!TextUtils.isEmpty(fileName)) {
        }
        else {
            fileName = getFileNameFromUrl(url);
        }

        if (!TextUtils.isEmpty(saveDirPath)) {
            if (!saveDirPath.endsWith("/")) {
                saveDirPath = saveDirPath + "/";
            }
        }
        else {
            saveDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wjokhttp/download/";
        }

        File file = new File(saveDirPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String filepath = saveDirPath + fileName;
        return filepath;
    }

    private String getFileNameFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        return System.currentTimeMillis() + "";
    }

    private void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDownloadDao(DownloadDao mDownloadDao) {
        this.mDownloadDao = mDownloadDao;
    }

    public void setClient(OkHttpClient mClient) {
        this.mClient = mClient;
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    public void setBuilder(Builder builder) {
        this.mBuilder = builder;
    }

    public String getId() {
        if (!TextUtils.isEmpty(id)) {
        }
        else {
            id = url;
        }
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getSaveDirPath() {
        return saveDirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public void setCompletedSize(long completedSize) {
        this.completedSize = completedSize;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public static class Builder {
        private String id;// task id
        private String url;// file url
        private String saveDirPath;// file save path
        private String fileName; // File name when saving
        private int downloadStatus = DownloadStatus.DOWNLOAD_STATUS_INIT;

        private DownloadTaskListener listener;

        /**
         * 作为下载task开始、删除、停止的key值，如果为空则默认是url
         *
         * @param id
         * @return
         */
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * 下载url（not null）
         *
         * @param url
         * @return
         */
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        /**
         * 设置保存地址
         *
         * @param saveDirPath
         * @return
         */
        public Builder setSaveDirPath(String saveDirPath) {
            this.saveDirPath = saveDirPath;
            return this;
        }

        /**
         * 设置下载状态
         *
         * @param downloadStatus
         * @return
         */
        public Builder setDownloadStatus(int downloadStatus) {
            this.downloadStatus = downloadStatus;
            return this;
        }

        /**
         * 设置文件名
         *
         * @param fileName
         * @return
         */
        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * 设置下载回调
         *
         * @param listener
         * @return
         */
        public Builder setListener(DownloadTaskListener listener) {
            this.listener = listener;
            return this;
        }

        public DownloadTask build() {
            return new DownloadTask(this);
        }
    }

}
