/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.controller;

import com.mainaer.wjokhttp.model.UploadResponse;
import com.mainaer.wjoklib.okhttp.IUrl;
import com.mainaer.wjoklib.okhttp.controller.OKUploadController;
import com.mainaer.wjoklib.okhttp.controller.ProgressListener;
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
        HashMap<String ,Object> map = new HashMap<>();
        map.put("file", file);
        task.load(map, UploadResponse.class);
    }

    private class UpLoadTask extends BaseUpLoadTask<UploadResponse> {
        @Override
        protected IUrl getUrl() {
            return null;
        }

        @Override
        protected void onSuccess(UploadResponse baseResponse) {
            mListener.onSuccess(baseResponse);
        }

        @Override
        protected void onError(OkHttpError error) {
            mListener.onFailure(error);
        }
    }
    
    public interface UploadListener extends ProgressListener<UploadResponse, OkHttpError> {
        
    }
    
}
