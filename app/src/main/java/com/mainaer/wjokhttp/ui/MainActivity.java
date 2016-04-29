package com.mainaer.wjokhttp.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.mainaer.wjokhttp.R;
import com.mainaer.wjokhttp.comment.OkUtils;
import com.mainaer.wjokhttp.controller.LoadController;
import com.mainaer.wjokhttp.controller.UploadController;
import com.mainaer.wjokhttp.model.LoadRequest;
import com.mainaer.wjokhttp.model.LoadResponse;
import com.mainaer.wjokhttp.model.UploadResponse;
import com.mainaer.wjoklib.okhttp.exception.OkException;

import java.io.File;

public class MainActivity extends AppCompatActivity implements LoadController.LoadListener, View.OnClickListener,
    UploadController.UploadListener {


    ListView mListview;
    LoadController mLoadController;
    UploadController mUploadController;
    MyAdapter mMyAdapter;
    View btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null)
                    .show();
            }
        });

        initView();

        initData();
    }

    private void initView() {
        mListview = (ListView) findViewById(R.id.listview);
        btnDownload = findViewById(R.id.download);
        btnDownload.setOnClickListener(this);

        mLoadController = new LoadController(this);
        mUploadController = new UploadController(this);

        mMyAdapter = new MyAdapter(this);
        mListview.setAdapter(mMyAdapter);
    }

    // 加载列表
    private void initData() {
        LoadRequest request = new LoadRequest();
        mLoadController.load(request);
    }

    @Override
    public void onLoadSuccess(LoadResponse loadResponse) {
        mMyAdapter.setList(loadResponse.list);
        mMyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadFail(OkException e) {
        OkUtils.toastError(this, e);
    }

    @Override
    public void onClick(View v) {
        if (btnDownload == v) {
            DownLoadActivity.go(this);
        }
        else {
            upLoad();
        }
    }

    //------------------------------------------------------------
    // 文件上传，需要传入文件，并修改UploadController中的getUrl();
    private void upLoad() {
        File file = new File("file_path");
        mUploadController.upload(file);
    }


    // 上传回调
    @Override
    public void upLoadSuccess(UploadResponse uploadResponse) {

    }

    @Override
    public void upLoadFail(OkException e) {
        OkUtils.toastError(this, e);
    }
}
