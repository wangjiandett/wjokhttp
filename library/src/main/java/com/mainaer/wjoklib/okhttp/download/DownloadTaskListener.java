package com.mainaer.wjoklib.okhttp.download;

import java.io.File;

/**
 * Created by wangjian on 15/11/21.
 */
public interface DownloadTaskListener {

    /**
     * 下载中
     *
     * @param completedSize
     * @param totalSize
     * @param percent
     * @param downloadTask
     */
    void onDownloading(DownLoadTask downloadTask, long completedSize, long totalSize, int percent);

    /**
     * 下载暂停
     *
     * @param downloadTask
     * @param completedSize
     * @param totalSize
     * @param percent
     */
    void onPause(DownLoadTask downloadTask, long completedSize, long totalSize, int percent);

    /**
     * 下载取消
     *
     * @param downloadTask
     */
    void onCancel(DownLoadTask downloadTask);

    /**
     * 下载成功
     *
     * @param file
     * @param downloadTask
     */
    void onDownloadSuccess(DownLoadTask downloadTask, File file);

    /**
     * 下载失败
     *
     * @param downloadTask
     * @param errorCode    {@link DownloadStatus}
     */
    void onError(DownLoadTask downloadTask, int errorCode);

}
