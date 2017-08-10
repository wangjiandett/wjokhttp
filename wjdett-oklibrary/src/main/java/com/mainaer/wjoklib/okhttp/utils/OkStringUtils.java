package com.mainaer.wjoklib.okhttp.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class OkStringUtils {
    

    public static String getRequestParamValue(Object obj, String charset) {
        if (obj == null) {
            return "";
        }
        String value;
        
        if (obj instanceof List) {
            StringBuilder sb = new StringBuilder();
            if (obj != null) {
                for (Object o : (List<?>) obj) {
                    if (o != null) {
                        sb.append(o.toString());
                        sb.append(',');
                    }
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            value = sb.toString();
        }
        
        else {
            value = obj.toString();
        }
        
        try {
            return URLEncoder.encode(value, charset);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    // TODO fix class extends same fields issue
    public static String getRequestParam(Object object, String encoding) {
        StringBuilder sb = new StringBuilder();
        
        if (null == object) {
        }
        else {
            // 获取此类所有声明的字段
            Field[] field = object.getClass().getFields();
            // 用来拼接所需保存的字符串
            
            // 循环此字段数组，获取属性的值
            for (int i = 0; i < field.length && field.length > 0; i++) {
                // 值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查.
                field[i].setAccessible(true);
                // field[i].getName() 获取此字段的名称
                // field[i].get(object) 获取指定对象上此字段的值
                String name = field[i].getName();
                Object val;
                try {
                    val = field[i].get(object);
                } catch (Exception e) {
                    continue;
                }
                if (val != null) {
                    if (sb.length() > 0) {
                        sb.append("&" + name + "=" + getRequestParamValue(val, encoding));
                    }
                    else {
                        sb.append(name + "=" + getRequestParamValue(val, encoding));
                    }
                    
                }
            }
            return sb.toString();
        }
        
        return sb.toString();
        
    }


    public static HashMap postRequestParam(Object object, String encoding) {
        HashMap map = new HashMap();

        if (null == object) {
        }
        else {
            // 获取此类所有声明的字段
            Field[] field = object.getClass().getFields();
            // 用来拼接所需保存的字符串
            // 循环此字段数组，获取属性的值
            for (int i = 0; i < field.length && field.length > 0; i++) {
                // 值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查.
                field[i].setAccessible(true);
                // field[i].getName() 获取此字段的名称
                // field[i].get(object) 获取指定对象上此字段的值
                String name = field[i].getName();
                Object val;
                try {
                    val = field[i].get(object);
                } catch (Exception e) {
                    continue;
                }
                if (val != null) {
                    map.put(name, getRequestParamValue(val, encoding));
                }
            }
            return map;
        }
        return map;
    }
    
    public static String getDownLoadPercent(long completedSize, long totalSize) {
        String baifenbi = "0";// 接受百分比的值
        double baiy = completedSize * 1.0;
        double baiz = totalSize * 1.0;
        // 防止分母为0出现NoN
        if (baiz > 0) {
            double fen = (baiy / baiz) * 100;
            //NumberFormat nf = NumberFormat.getPercentInstance();
            //nf.setMinimumFractionDigits(2); //保留到小数点后几位
            // 百分比格式，后面不足2位的用0补齐
            //baifenbi = nf.format(fen);
            //注释掉的也是一种方法
            DecimalFormat df1 = new DecimalFormat("0");//0.00
            baifenbi = df1.format(fen);
        }
        return baifenbi;
    }
    
    public static String getFileNameFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        return String.valueOf("ok_down_"+System.currentTimeMillis());
    }
    
}
