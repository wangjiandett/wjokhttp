/*
 * Copyright 2014-2016 wjokhttp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mainaer.wjoklib.okhttp.download;

import android.content.Context;

import com.mainaer.wjoklib.okhttp.download.dao.DaoMaster;
import com.mainaer.wjoklib.okhttp.download.dao.DownloadDao;
import com.mainaer.wjoklib.okhttp.download.dao.DownloadEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 下载管理器
 *
 * @author wangjian
 * @date 2016/5/13 .
 */
public class DownloadManager {

    private static Context mContext;

    private DownloadDao mDownloadDao;
    private OkHttpClient mClient;

    private int mPoolSize = 5;
    // 将执行结果保存在future变量中
    private Map<String, Future> mFutureMap;
    private ExecutorService mExecutor;
    private Map<String, DownloadTask> mCurrentTaskList;

    static DownloadManager manager;

    /**
     * 方法加锁，防止多线程操作时出现多个实例
     */
    private static synchronized void init() {
        if (manager == null) {
            manager = new DownloadManager();
        }
    }

    /**
     * 获得当前对象实例
     *
     * @return 当前实例对象
     */
    public final static DownloadManager getInstance() {
        if (manager == null) {
            init();
        }
        return manager;
    }

    /**
     * 管理器初始化，建议在application中调用
     *
     * @param context
     */
    public static void init(Context context) {
        mContext = context;
        getInstance();
    }

    public DownloadManager() {
        initOkhttpClient();

        // 数据库初始化
        DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(mContext, "downloadDB", null);
        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
        mDownloadDao = daoMaster.newSession().getDownloadDao();

        // 初始化线程池
        mExecutor = Executors.newFixedThreadPool(mPoolSize);
        mFutureMap = new HashMap<>();
        mCurrentTaskList = new HashMap<>();
    }

    /**
     * 初始化okhttp
     */
    private void initOkhttpClient() {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.connectTimeout(10, TimeUnit.SECONDS);
        okBuilder.readTimeout(10, TimeUnit.SECONDS);
        okBuilder.writeTimeout(10, TimeUnit.SECONDS);
        mClient = okBuilder.build();
    }

    /**
     * 添加下载任务
     *
     * @param downloadTask
     */
    public void addDownloadTask(DownloadTask downloadTask) {
        if (downloadTask != null && !isDownloading(downloadTask)) {
            downloadTask.setDownloadDao(mDownloadDao);
            downloadTask.setClient(mClient);
            downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_INIT);
            // 保存下载task列表
            mCurrentTaskList.put(downloadTask.getId(), downloadTask);
            Future future = mExecutor.submit(downloadTask);
            mFutureMap.put(downloadTask.getId(), future);
        }
    }

    private boolean isDownloading(DownloadTask task) {
        if (task != null) {
            if (task.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
                return true;
            }
        }
        return false;
    }

    /**
     * 暂停下载任务
     *
     * @param id 任务id
     */
    public void pause(String id) {
        DownloadTask task = getDownloadTask(id);
        if (task != null) {
            task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PAUSE);
        }
    }

    /**
     * 重新开始已经暂停的下载任务
     *
     * @param id 任务id
     */
    public void resume(String id) {
        DownloadTask task = getDownloadTask(id);
        if (task != null) {
            addDownloadTask(task);
        }
    }

    /**
     * 取消下载任务(同时会删除已经下载的文件，和清空数据库缓存)
     *
     * @param id 任务id
     */
    public void cancel(String id) {
        DownloadTask task = getDownloadTask(id);
        if (task != null) {
            mCurrentTaskList.remove(id);
            mFutureMap.remove(id);
            task.cancel();
            task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
        }
    }

    /**
     * 实时更新manager中的task信息
     *
     * @param task
     */
    public void updateDownloadTask(DownloadTask task) {
        if (task != null) {
            DownloadTask currTask = getDownloadTask(task.getId());
            if (currTask != null) {
                mCurrentTaskList.put(task.getId(), task);
            }
        }
    }

    /**
     * 获得指定的task
     *
     * @param id task id
     * @return
     */
    public DownloadTask getDownloadTask(String id) {
        DownloadTask currTask = mCurrentTaskList.get(id);
        if (currTask == null) {
            // 从数据库中取出为完成的task
            DownloadEntity entity = mDownloadDao.load(id);
            if (entity != null) {
                if (entity.getDownloadStatus() != DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                    currTask = parseEntity2Task(new DownloadTask.Builder().build(), entity);
                    // 放入task list中
                    mCurrentTaskList.put(id, currTask);
                }
            }
        }
        return currTask;
    }

    /**
     * 获得所有的task
     *
     * @return
     */
    public Map<String, DownloadTask> getAllDownloadTasks() {
        if (mCurrentTaskList != null && mCurrentTaskList.size() <= 0) {
            List<DownloadEntity> entitys = mDownloadDao.loadAll();
            for (DownloadEntity entity : entitys) {
                DownloadTask currTask = parseEntity2Task(new DownloadTask.Builder().build(), entity);
                mCurrentTaskList.put(entity.getDownloadId(), currTask);
            }
        }

        return mCurrentTaskList;
    }

    private DownloadTask parseEntity2Task(DownloadTask currTask, DownloadEntity entity) {
        if (entity != null && currTask != null) {
            DownloadTask.Builder builder = new DownloadTask.Builder()//
                .setDownloadStatus(entity.getDownloadStatus())//
                .setFileName(entity.getFileName())//
                .setSaveDirPath(entity.getSaveDirPath())//
                .setUrl(entity.getUrl())//
                .setId(entity.getDownloadId());//

            currTask.setBuilder(builder);
            currTask.setCompletedSize(entity.getCompletedSize());//
            currTask.setTotalSize(entity.getToolSize());
        }
        return currTask;
    }
}
