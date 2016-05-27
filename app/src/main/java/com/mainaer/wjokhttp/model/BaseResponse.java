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
    public String desc;

    /**
     * 数据
     */
    public Object detail;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return desc;
    }

    public void setMessage(String desc) {
        this.desc = desc;
    }

    public void setData(Object detail) {
        this.detail = detail;
    }

    @Override
    public String getData() {
        if (detail instanceof String) {
            return (String) detail;
        }
        return new Gson().toJson(detail);
    }

    @Override
    public String toString() {
        return "{status:" + this.status + ", desc:" + this.desc + ", detail:" + this.detail + "}";
    }
}
