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
import android.widget.Toast;

import com.mainaer.wjokhttp.R;
import com.mainaer.wjokhttp.comment.OkUtils;
import com.mainaer.wjokhttp.controller.DownController;
import com.mainaer.wjoklib.okhttp.OkException;

import java.io.File;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/28.
 */
public class DownLoadActivity extends AppCompatActivity implements View.OnClickListener,
    DownController.DownLoadListener {
    private ProgressBar mProgressBar;
    private Button mButton;

    DownController mDownController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initView();
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(this);

        mDownController = new DownController(this);

    }

    @Override
    public void onClick(View v) {
        if (mButton == v) {
            mProgressBar.setProgress(0);
            mDownController.downLoad();
        }
    }

    @Override
    public void onDownLoadProgress(float progress, long total) {
        int pro = (int) (progress * 100);
        mProgressBar.setProgress(pro);
    }

    @Override
    public void onDownLoadSuccess(File file) {
        Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownLoadFailure(OkException error) {
        OkUtils.toastError(this,error);
    }

    public static void go(Context context) {
        Intent intent = new Intent(context, DownLoadActivity.class);
        context.startActivity(intent);
    }
}
