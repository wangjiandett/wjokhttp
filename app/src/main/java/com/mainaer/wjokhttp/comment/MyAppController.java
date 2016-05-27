/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.comment;


import com.mainaer.wjokhttp.model.BaseResponse;
import com.mainaer.wjoklib.okhttp.OKBaseResponse;
import com.mainaer.wjoklib.okhttp.controller.OKHttpController;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/25.
 */
public abstract class MyAppController<Listener> extends OKHttpController<Listener> {


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
                if (!"000000".equals(resp.getStatus())) {//101表示成功
                    // 主线程中调用onError,不能直接调用onError
                    sendMessage(resp.getMessage(), ERROR_CODE);
                    return true;
                }
            }
            return false;
        }
    }
}
