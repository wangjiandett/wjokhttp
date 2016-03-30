/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.controller;


import com.mainaer.wjokhttp.comment.MyAppController;
import com.mainaer.wjokhttp.model.LoadRequest;
import com.mainaer.wjokhttp.model.LoadResponse;
import com.mainaer.wjokhttp.url.URLConst;
import com.mainaer.wjoklib.okhttp.IUrl;
import com.mainaer.wjoklib.okhttp.OkException;


/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/23.
 */
public class LoadController extends MyAppController<LoadController.LoadListener> {

    public LoadController(LoadListener l) {
        mListener = l;
    }

    public void load(LoadRequest request) {
        Task task = new Task();
        task.load(request, LoadResponse.class);
    }

    public class Task extends AppBaseTask<LoadRequest, LoadResponse> {

        @Override
        public IUrl getUrl() {
            return URLConst.getListUrl();
        }

        @Override
        public void onSuccess(LoadResponse loadResponse) {
            mListener.onLoadSuccess(loadResponse);
        }

        @Override
        public void onError(OkException e) {
            mListener.onLoadFail(e);
        }
    }

    public interface LoadListener {

        void onLoadSuccess(LoadResponse loadResponse);

        void onLoadFail(OkException e);
    }

}
