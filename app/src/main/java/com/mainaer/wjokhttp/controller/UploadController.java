/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.controller;

import com.mainaer.wjokhttp.model.UploadResponse;
import com.mainaer.wjoklib.okhttp.IUrl;
import com.mainaer.wjoklib.okhttp.controller.OKUploadController;
import com.mainaer.wjoklib.okhttp.exception.OkHttpError;

import java.io.File;
import java.util.HashMap;


/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/26.
 */
public class UploadController extends OKUploadController<UploadController.UploadListener> {


    public UploadController(UploadListener l) {
        super(l);
    }

    public void upload(File file) {
        UpLoadTask task = new UpLoadTask();
        task.load(file, UploadResponse.class);
    }

    private class UpLoadTask extends BaseUpLoadTask<UploadResponse> {
        @Override
        protected IUrl getUrl() {
            return null;
        }

        @Override
        protected void onSuccess(UploadResponse baseResponse) {
            mListener.upLoadSuccess(baseResponse);
        }

        @Override
        protected void onError(OkHttpError error) {
            mListener.upLoadFail(error);
        }

        @Override
        protected void addParams(HashMap<String, String> params) {
            params.put("client","Android");
            params.put("uid","1061");
            params.put("token","1911173227afe098143caf4d315a436d");
            params.put("uuid","A000005566DA77");
        }
    }


    public interface UploadListener {
        void upLoadSuccess(UploadResponse uploadResponse);

        void upLoadFail(OkHttpError msg);
    }
}
