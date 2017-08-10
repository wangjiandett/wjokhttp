/*
 *
 *  Copyright 2014-2016 wjokhttp.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.mainaer.wjoklib.okhttp.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.mainaer.wjoklib.okhttp.download.dao.DownloadDao;
import com.mainaer.wjoklib.okhttp.download.dao.DownloadEntity;
import com.mainaer.wjoklib.okhttp.utils.FileUtil;
import com.mainaer.wjoklib.okhttp.utils.WLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import static com.mainaer.wjoklib.okhttp.utils.FileUtil.close;
import static com.mainaer.wjoklib.okhttp.utils.OkStringUtils.getFileNameFromUrl;
import okhttp3.Call;
import okhttp3.Callback;
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
public class DownLoadTask implements Runnable {
    
    private static String FILE_MODE = "rwd";
    private OkHttpClient mClient;
    private Call call;
    
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
    private String fileName; // File name when saving
    private int downloadStatus;
    
    private int percent;
    private int errorCode;
    
    private DownLoadTask(Builder builder) {
        this.mBuilder = builder;
        this.id = mBuilder.id;
        this.url = mBuilder.url;
        this.saveDirPath = mBuilder.saveDirPath;
        this.fileName = mBuilder.fileName;
        this.downloadStatus = mBuilder.downloadStatus;
        this.mListener = mBuilder.listener;
    }
    
    @Override
    public void run() {
        try {
            // 数据库中加载数据
            dbEntity = mDownloadDao.load(id);
            if (dbEntity != null) {
                completedSize = dbEntity.getCompletedSize();
                totalSize = dbEntity.getTotalSize();
            }
            
            // 获得文件路径
            String filepath = getDownLoadFilePath().getAbsolutePath();
            // 获得下载保存文件
            mDownLoadFile = new RandomAccessFile(filepath, FILE_MODE);
            
            // 获得本地下载的文件大小
            long fileLength = mDownLoadFile.length();
            // 本地文件中已经下载完成
            if (fileLength > 0 && totalSize == fileLength) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
                completedSize = fileLength;
                totalSize = fileLength;
                percent = 100;
                WLog.d("totalSize=" + totalSize + " ,completedSize=" + completedSize + " ,downloadStatus="
                    + downloadStatus);
                // 执行回调
                onCallBack();
                return;
            }
            
            // ============开始下载==================
            Request request = new Request.Builder().url(url)//
                .header("RANGE", "bytes=" + completedSize + "-") // Http value set breakpoints RANGE
                .build();
            // 文件跳转到指定位置开始写入
            mDownLoadFile.seek(completedSize);
            
            call = mClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    error();
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody body = response.body();
                    if (body != null) {
                        // 更新回调下载状态
                        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING;
                        onCallBack();
                        // 开始下载
                        readAndSave2File(body.byteStream(), body.contentLength());
                    }
                }
            });
        } catch (FileNotFoundException e) {
            // file not found
            WLog.e(e.getMessage());
            error();
        } catch (IOException e) {
            // io exception
            WLog.e(e.getMessage());
            error();
        }
    }
    
    private void readAndSave2File(InputStream inputStream, final long contentLength) throws IOException {
        byte[] buf = new byte[2048];
        int length = 0;
        try {
            long sum = completedSize;
            float times = 0;
            
            // contentLength 在暂停后再次启动会从断点后开始计算，会小于原始的值
            // 所以只能在第一次加载的时候保存
            if (totalSize <= 0) {
                totalSize = contentLength;
            }
            
            while ((length = inputStream.read(buf)) != -1) {
                // 如果不是正在下载状态，则停止任务
                if (downloadStatus != DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
                    return;
                }
                
                sum += length;
                mDownLoadFile.write(buf, 0, length);
                
                completedSize = sum;
                float prgress = (float) (completedSize * 100 / totalSize);
                // WLog.d("completedSize=" + completedSize + " ,prgress=" + prgress);
                // 更新进度，回调100次
                if (prgress - times >= 1) {
                    times = prgress;
                    // 更新状态
                    downloadStatus = DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING;
                    // 更新百分比
                    percent = (int) prgress;
                    onCallBack();
                }
            }
        } catch (Exception e) {
            WLog.e(e.toString());
        } finally {
            WLog.d("completedSize=" + completedSize);
            // 回收资源
            close(inputStream);
            close(mDownLoadFile);
            
            // 判断是否下载完成，更新下载状态
            isDownloadFinish();
            // 最终都会执行，数据库更新
            insertOrUpdateDB();
            // 回调一次
            onCallBack();
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
     * 更新数据库操作
     */
    private void insertOrUpdateDB() {
        if (dbEntity == null) {
            dbEntity = new DownloadEntity(id, totalSize, completedSize, url, saveDirPath, fileName, downloadStatus);
        }
        else {
            dbEntity.setCompletedSize(completedSize);
            dbEntity.setDownloadStatus(downloadStatus);
        }
        
        mDownloadDao.insertOrReplace(dbEntity);
        
        WLog.d("totalSize=" + dbEntity.getTotalSize() + " ,completedSize=" + dbEntity.getCompletedSize()
            + " ,downloadStatus=" + dbEntity.getDownloadStatus());
    }
    
    /**
     * error 事件处理
     */
    private void error() {
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
        errorCode = DownloadStatus.DOWNLOAD_ERROR_IO_ERROR;
        onCallBack();
    }
    
    /**
     * 取消下载
     */
    public void cancel() {
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_CANCEL;
        doCancelClear();
        cancelRequest();
        onCallBack();
    }
    
    /**
     * 暂停下载
     */
    public void pause() {
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_PAUSE;
        onCallBack();
    }
    
    /**
     * 取消下载请求
     */
    private void cancelRequest() {
        if (call != null) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
    }
    
    /**
     * 删除本地文件和数据库数据
     */
    private void doCancelClear() {
        if (dbEntity != null) {
            mDownloadDao.delete(dbEntity);
            File temp = getDownLoadFilePath();
            if (temp.exists()) {
                temp.delete();
            }
        }
    }
    
    /**
     * 分发回调事件到ui层
     */
    private void onCallBack() {
        // 发送当前下载状态
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
                    mListener.onError(DownLoadTask.this, errorCode);
                    break;
                // 正在下载
                case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                    mListener.onDownloading(DownLoadTask.this, completedSize, totalSize, percent);
                    break;
                // 取消
                case DownloadStatus.DOWNLOAD_STATUS_CANCEL:
                    mListener.onCancel(DownLoadTask.this);
                    break;
                // 完成
                case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                    mListener.onDownloadSuccess(DownLoadTask.this, getDownLoadFilePath());
                    break;
                // 停止
                case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                    mListener.onPause(DownLoadTask.this, completedSize, totalSize, percent);
                    break;
            }
        }
    };
    
    private File getDownLoadFilePath() {
        File file = FileUtil.getDownLoadDir();
        String parentDir = file.getAbsolutePath();
        // 获得文件名
        String filename = !TextUtils.isEmpty(fileName) ? fileName : getFileNameFromUrl(url);
        File downFile = new File(parentDir, filename);
        return downFile;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DownLoadTask){
            DownLoadTask task = (DownLoadTask) obj;
            return task.getId().equals(this.getId());
        }
        return super.equals(obj);
    }
    
    //=============================================================
    
    public void setDownloadDao(DownloadDao mDownloadDao) {
        this.mDownloadDao = mDownloadDao;
    }
    
    public Builder getBuilder() {
        return mBuilder;
    }
    
    public void setBuilder(Builder builder) {
        this.mBuilder = builder;
    }
    
    public String getId() {
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
    
    public void setClient(OkHttpClient mClient) {
        this.mClient = mClient;
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
         * 作为下载task开始、删除、停止的key值（not null）
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
        
        public DownLoadTask build() {
            return new DownLoadTask(this);
        }
    }
    
}
