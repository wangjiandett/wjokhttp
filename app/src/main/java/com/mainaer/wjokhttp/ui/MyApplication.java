/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.ui;

import android.app.Application;

import com.mainaer.wjokhttp.model.BaseResponse;
import com.mainaer.wjoklib.okhttp.OKHttpConfig;
import com.mainaer.wjoklib.okhttp.OKHttpManager;
import com.mainaer.wjoklib.okhttp.download.DownloadManager;

import java.io.File;

import okhttp3.Cache;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/28.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 自定义缓存目录和大小
        File cacheFile = new File(getCacheDir(), "okcache");
        Cache cache = new Cache(cacheFile, 100 * 1024 * 1024);// 100mb

        // 程序初始化时，初始okhttp配置
        OKHttpConfig OKHttpConfig = new OKHttpConfig.Builder()
            .setBaseResponseClass(BaseResponse.class)
            .setLogLevel(HttpLoggingInterceptor.Level.BODY)// log level
            .setConnectTimeout(10) // connect time out
            .setReadTimeout(10) // read time out
            .setWriteTimeout(10) // write time out
            .setCacheTime(1000) // cache time
            .setCache(cache) // cache
            .build();
        OKHttpManager.init(this, OKHttpConfig);

        DownloadManager.init(this);
    }
}
