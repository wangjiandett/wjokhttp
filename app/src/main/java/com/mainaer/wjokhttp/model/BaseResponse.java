/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.model;

import com.google.gson.Gson;
import com.mainaer.wjoklib.okhttp.OKBaseResponse;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/1/15.
 */
public class BaseResponse implements OKBaseResponse {

    /**
     * 状态码
     */
    public String status;

    /**
     * 消息
     */
    public String message;

    /**
     * 数据
     */
    public Object data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String desc) {
        this.message = desc;
    }

    public void setData(Object detail) {
        this.data = detail;
    }

    @Override
    public String getData() {
        if (data instanceof String) {
            return (String) data;
        }
        return new Gson().toJson(data);
    }

    @Override
    public String toString() {
        return "{status:" + this.status + ", message:" + this.message + ", data:" + this.data + "}";
    }
}
