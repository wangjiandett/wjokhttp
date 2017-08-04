package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoCreator {

    public static void main(String[] args){

        Schema schema = new Schema(1,"com.mainaer.wjoklib.okhttp.download.dao");// 包名+问价所在目录

        Entity entity = schema.addEntity("DownloadEntity");
        entity.setClassNameDao("DownloadDao");
        entity.setTableName("download");
        entity.addStringProperty("downloadId").primaryKey();
        entity.addLongProperty("totalSize");
        entity.addLongProperty("completedSize");
        entity.addStringProperty("url");
        entity.addStringProperty("saveDirPath");
        entity.addStringProperty("fileName");
        entity.addIntProperty("downloadStatus");
        try {
            new DaoGenerator().generateAll(schema,"H:\\work\\wjokhttp\\library\\src\\main\\java");//java目录的绝对地址
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
