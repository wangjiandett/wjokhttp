/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.model;

import java.io.Serializable;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/23.
 */
public class LoadResponse implements Serializable {
    public String title;

    public String source;

    public String article_url;

    public long publish_time;

    public long behot_time;

    public long create_time;

    public int digg_count;

    public int bury_count;

    public int repin_count;

    public String group_id;
}

