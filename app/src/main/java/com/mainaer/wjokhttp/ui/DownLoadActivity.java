/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mainaer.wjokhttp.R;
import com.mainaer.wjoklib.okhttp.download.DownloadManager;
import com.mainaer.wjoklib.okhttp.download.DownloadTask;
import com.mainaer.wjoklib.okhttp.download.DownloadTaskListener;
import com.mainaer.wjoklib.okhttp.utils.LoggerUtil;

import java.io.File;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/28.
 */
public class DownLoadActivity extends AppCompatActivity implements View.OnClickListener, DownloadTaskListener {
    private ProgressBar mProgressBar;
    private Button mButton;
    private Button mButtonPause;
    private Button mButtonCancel;
    private Button mButtonResume;
    private TextView mTvStatus;

    private ProgressBar mProgressBar1;
    private Button mButton1;
    private Button mButtonPause1;
    private Button mButtonCancel1;
    private Button mButtonResume1;
    private TextView mTvStatus1;

    DownloadManager downloadManager;

    private static String URL_360_ID = "url_360";
    private static String URL_QQ_ID = "url_qq";

    private String url_360 = "http://msoftdl.360.cn/mobilesafe/shouji360/360safesis/360StrongBox_1.0.9.1008.apk";

    private String url_qq = "http://221.228.67.156/dd.myapp.com/16891/62B928C30FE677EDEEA9C504486444E9"
        + ".apk?mkey=5736f6098218f3cf&f=1b58&c=0&fsname=com.tencent.mobileqq_6.3.3_358.apk&p=.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initView();
    }

    private void initView() {
        //----------第一组下载----------------
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(this);

        mButtonPause = (Button) findViewById(R.id.buttonpause);
        mButtonPause.setOnClickListener(this);

        mButtonCancel = (Button) findViewById(R.id.buttoncancel);
        mButtonCancel.setOnClickListener(this);

        mButtonResume = (Button) findViewById(R.id.buttonresume);
        mButtonResume.setOnClickListener(this);

        mTvStatus = (TextView) findViewById(R.id.tv_status);

        //-------------第二组下载--------------

        mProgressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        mButton1 = (Button) findViewById(R.id.button1);
        mButton1.setOnClickListener(this);

        mButtonPause1 = (Button) findViewById(R.id.buttonpause1);
        mButtonPause1.setOnClickListener(this);

        mButtonCancel1 = (Button) findViewById(R.id.buttoncancel1);
        mButtonCancel1.setOnClickListener(this);

        mButtonResume1 = (Button) findViewById(R.id.buttonresume1);
        mButtonResume1.setOnClickListener(this);

        mTvStatus1 = (TextView) findViewById(R.id.tv_status1);

        downloadManager = DownloadManager.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (mButton == v) {
            download360();
        }
        else if (mButtonCancel == v) {
            downloadManager.cancel(URL_360_ID);
        }
        else if (mButtonPause == v) {
            downloadManager.pause(URL_360_ID);
        }
        else if (mButtonResume == v) {
            downloadManager.resume(URL_360_ID);
        }

        //-----------------第二组下载
        if (mButton1 == v) {
            downloadQQ();
        }
        else if (mButtonCancel1 == v) {
            downloadManager.cancel(URL_QQ_ID);
        }
        else if (mButtonPause1 == v) {
            downloadManager.pause(URL_QQ_ID);
        }
        else if (mButtonResume1 == v) {
            downloadManager.resume(URL_QQ_ID);
        }
    }

    private void download360() {
        DownloadTask task = new DownloadTask.Builder().setId(URL_360_ID).setUrl(url_360).setListener(this).build();
        downloadManager.addDownloadTask(task);
    }

    private void downloadQQ() {
        DownloadTask task = new DownloadTask.Builder().setId(URL_QQ_ID).setUrl(url_qq).setListener(this).build();
        downloadManager.addDownloadTask(task);
    }

    public static void go(Context context) {
        Intent intent = new Intent(context, DownLoadActivity.class);
        context.startActivity(intent);
    }

    //=========================================================================
    @Override
    public void onDownloading(DownloadTask downloadTask, long completedSize, long totalSize, final String percent) {
        LoggerUtil.i("onDownloading completedSize=" + completedSize + " ,totalSize=" + totalSize + " ,percent=" +
            percent);
        if (downloadTask.getId().equals(URL_360_ID)) {
            mProgressBar.setProgress(Integer.parseInt(percent));
            mTvStatus.setText("正在下载..." + percent + "%");
        }
        else {
            mProgressBar1.setProgress(Integer.parseInt(percent));
            mTvStatus1.setText("正在下载..." + percent + "%");
        }

    }

    @Override
    public void onPause(DownloadTask downloadTask, long completedSize, long totalSize, String percent) {
        LoggerUtil.i("onPause=" + completedSize + " ,totalSize=" + totalSize + " ,percent=" + percent);
        if (downloadTask.getId().equals(URL_360_ID)) {
            mTvStatus.setText("下载已暂停,已下载：" + percent + "%");
        }
        else {
            mTvStatus1.setText("下载已暂停,已下载：" + percent + "%");
        }
    }

    @Override
    public void onCancel(DownloadTask downloadTask) {
        LoggerUtil.i("onCancel");
        if (downloadTask.getId().equals(URL_360_ID)) {
            mTvStatus.setText("下载已取消");
            mProgressBar.setProgress(0);
        }
        else {
            mTvStatus1.setText("下载已取消");
            mProgressBar1.setProgress(0);
        }
    }

    @Override
    public void onDownloadSuccess(DownloadTask downloadTask, File file) {
        LoggerUtil.i("onDownloadSuccess file=" + file.getAbsolutePath());
        if (downloadTask.getId().equals(URL_360_ID)) {
            mTvStatus.setText("下载完成 path：" + file.getAbsolutePath());
        }
        else {
            mTvStatus1.setText("下载完成 path：" + file.getAbsolutePath());
        }


    }

    @Override
    public void onError(DownloadTask downloadTask, int errorCode) {
        LoggerUtil.i("onError errorCode=" + errorCode);
        if (downloadTask.getId().equals(url_360)) {
            mTvStatus.setText("下载失败errorCode=" + errorCode);
        }else{
            mTvStatus1.setText("下载失败errorCode=" + errorCode);
        }
    }

}
