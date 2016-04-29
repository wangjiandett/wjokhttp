/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.ui;

import android.app.Application;

import com.mainaer.wjokhttp.model.BaseResponse;
import com.mainaer.wjoklib.okhttp.OKHttpConfig;
import com.mainaer.wjoklib.okhttp.OKHttpManager;

import okhttp3.Cache;

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

        Cache cache = new Cache(getCacheDir(), 10 * 1024 * 1024);

        // 程序初始化时，初始okhttp配置
        OKHttpConfig OKHttpConfig = new OKHttpConfig.Builder().setBaseResponseClass(BaseResponse.class)
            .setConnectTimeout(10).setReadTimeout(10).setWriteTimeout(10).setCache(cache).build();
        OKHttpManager.init(this, OKHttpConfig);
    }
}
