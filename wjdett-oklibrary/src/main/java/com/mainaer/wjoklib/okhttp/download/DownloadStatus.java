package com.mainaer.wjoklib.okhttp.download;

/**
 * description the download status
 * <p/>
 * Created by wangjian on 15/11/21.
 */
public class DownloadStatus {
    /**
     * init download
     */
    public static final int DOWNLOAD_STATUS_INIT = 1;
    /**
     * downloading
     */
    public static final int DOWNLOAD_STATUS_DOWNLOADING = 2;
    /**
     * download cancel
     */
    public static final int DOWNLOAD_STATUS_CANCEL = 3;
    /**
     * download error
     */
    public static final int DOWNLOAD_STATUS_ERROR = 4;
    /**
     * download finish
     */
    public static final int DOWNLOAD_STATUS_COMPLETED = 5;
    /**
     * download pause
     */
    public static final int DOWNLOAD_STATUS_PAUSE = 6;
    /**
     * download error file not found
     */
    public static final int DOWNLOAD_ERROR_FILE_NOT_FOUND = 7;
    /**
     * download error io exception
     */
    public static final int DOWNLOAD_ERROR_IO_ERROR = 8;

}
