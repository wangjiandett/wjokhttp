/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjoklib.okhttp;

/**
 * 接口回调基本相应类型需要实现此接口
 *
 * @author wangjian
 * @date 2016/3/23.
 */
public interface OKBaseResponse {
    /**
     * 得到要解析的数据，参考demo BaseResponse
     *
     * @return
     */
    String getData();
}
