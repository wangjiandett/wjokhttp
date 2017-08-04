package com.mainaer.wjokhttp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mainaer.wjokhttp.R;
import com.mainaer.wjokhttp.comment.OkUtils;
import com.mainaer.wjokhttp.controller.LoadController;
import com.mainaer.wjokhttp.controller.UploadController;
import com.mainaer.wjokhttp.model.LoadResponse;
import com.mainaer.wjokhttp.model.UploadResponse;
import com.mainaer.wjokhttp.model.WeatherRequest;
import com.mainaer.wjokhttp.model.WeatherResponse;
import com.mainaer.wjokhttp.ui.view.H5Activity;
import com.mainaer.wjoklib.okhttp.exception.OkHttpError;

import java.io.File;

/**
 * github地址项目地址 https://github.com/wangjiandett/wjokhttp
 */
public class MainActivity extends AppCompatActivity implements LoadController.LoadListener, View.OnClickListener,
    UploadController.UploadListener{

    ListView mListview;
    LoadController mLoadController;
    UploadController mUploadController;
    MyAdapter mMyAdapter;
    View btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        initData();
    }

    private void initView() {
        mListview = (ListView) findViewById(R.id.listview);
        btnDownload = findViewById(R.id.download);
        btnDownload.setOnClickListener(this);

        // init controller
        mLoadController = new LoadController(this);
        mUploadController = new UploadController(this);

        // init adapter
        mMyAdapter = new MyAdapter(this);
        mListview.setAdapter(mMyAdapter);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LoadResponse response = (LoadResponse) parent.getAdapter().getItem(position);
                H5Activity.go(MainActivity.this, response);
            }
        });

    }

    // 加载列表
    private void initData() {
        // create request and load data
        WeatherRequest request = new WeatherRequest();
        request.city = "北京";
        mLoadController.load(request);
    }

    @Override
    public void onLoadSuccess(WeatherResponse loadResponse) {
        // set data to the ui
        mMyAdapter.setList(loadResponse.forecast);
        mMyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadFail(OkHttpError e) {
        // toast error info
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
    
    
    @Override
    public void onSuccess(UploadResponse response) {
        
    }
    
    @Override
    public void onFailure(OkHttpError error) {
        
    }
    
    @Override
    public void onProgress(long total, int progress) {
        
    }
}
