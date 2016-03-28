/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/23.
 */
public class LoadResponse implements Serializable {

    public List<Item> list;

    public static class Item implements Serializable{
        public int is_special_power;
        public int is_special_price;
        public String id;
        public String title;
        public String star;
        public String sale_status;
        public String actual_status;
        public String hellname;
        public String point_lng;
        public String point_lat;
        public String thumb_path;
        public String star_label;
        public String price;
        public String area;
        public List<String> taglist;
    }

}
