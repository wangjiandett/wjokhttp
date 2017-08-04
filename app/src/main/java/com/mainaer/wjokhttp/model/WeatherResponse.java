/*
 *
 *  Copyright 2014-2016 wjokhttp.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.mainaer.wjokhttp.model;

import java.util.List;

/**
 * 类或文件描述
 * <p>
 * Created by：wangjian on 2017/8/2 18:30
 */
public class WeatherResponse {
    
    public YesterdayBean yesterday;
    public String city;
    public String aqi;
    public String ganmao;
    public String wendu;
    public List<ForecastBean> forecast;
    
    public static class YesterdayBean {
        /**
         * date : 1日星期二
         * high : 高温 31℃
         * fx : 东南风
         * low : 低温 24℃
         * fl : 微风
         * type : 阵雨
         */
        
        public String date;
        public String high;
        public String fx;
        public String low;
        public String fl;
        public String type;
    }
    
    public static class ForecastBean {
        /**
         * date : 2日星期三
         * high : 高温 29℃
         * fengli : 微风级
         * low : 低温 24℃
         * fengxiang : 东风
         * type : 阵雨
         */
        
        public String date;
        public String high;
        public String fengli;
        public String low;
        public String fengxiang;
        public String type;
    }
    
}
