/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.comment;


import com.mainaer.wjokhttp.model.BaseResponse;
import com.mainaer.wjoklib.okhttp.OKBaseResponse;
import com.mainaer.wjoklib.okhttp.controller.OKHttpController;
import com.mainaer.wjoklib.okhttp.exception.OkHttpError;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/25.
 */
public abstract class MyAppController<Listener> extends OKHttpController<Listener> {

    public static final String SUCCESS_CODE = "200";
    
    public MyAppController() {
        super();
    }

    public MyAppController(Listener l) {
        super();
        setListener(l);
    }

    protected abstract class AppBaseTask<Input, Output> extends LoadTask<Input, Output> {

        @Override
        public boolean onInterceptor(OKBaseResponse response) {
            if (response instanceof BaseResponse) {
                BaseResponse resp = (BaseResponse) response;
                // 此处需要自定义成功的code,也可不用重写onInterceptor，默认返回false
                // 此处使用200表示成功
                if (!SUCCESS_CODE.equals(resp.getStatus())) {
                    // 主线程中调用onError
                    sendMessage(new OkHttpError(resp.getMessage()), ERROR_CODE);
                    return true;
                }
            }
            return false;
        }
    }
}
