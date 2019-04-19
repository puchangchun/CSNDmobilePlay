package com.android.puccmobileplay.model;

import android.content.Context;
import android.support.annotation.NonNull;


import com.android.puccmobileplay.model.bean.UserInfo;
import com.android.puccmobileplay.model.dao.UserAccountDao;
import com.android.puccmobileplay.model.db.DBManager;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 99653 on 2017/11/4.
 */

public class ModelController {
    private Context mContext;

    private static ModelController sModelController;

    /**
     * 线程池
     */
    private ThreadPoolExecutor executor ;
    /**
     * 用户数据库操作类
     */
    private UserAccountDao mUserAccountDao;
    private DBManager dbManager;

    private ModelController() {
    }

    /**
     * 单例
     * @return
     */
    public static ModelController getInstance() {
        if (sModelController == null) {
            synchronized (ModelController.class) {
                if (sModelController == null) {
                    sModelController = new ModelController();
                }
            }
        }
        return sModelController;
    }

    /**
     * 在应用启动时的初始化
     * @param context
     */
    public void init(Context context) {
        mContext = context;
    }

    public ThreadPoolExecutor getGlobalThreadPool() {
        if (executor == null){
            executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(5), new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    Thread t = new Thread(r);
                    t.setName(r.getClass().getSimpleName());
                    return t;
                }
            });
        }
        return executor;
    }

    public UserAccountDao getUserAccountDao() {
        if (mUserAccountDao == null) {
            mUserAccountDao = new UserAccountDao(mContext);
        }
        return mUserAccountDao;
    }

    /**
     * 用户登陆成功后的处理
     */
    public void loginSuccess(UserInfo account) {

        // 校验
        if(account == null) {
            return;
        }

        if(dbManager != null) {
            dbManager.close();
        }

        dbManager = new DBManager(mContext, account.getName());
    }

    public Map<String, EaseUser> getContactList() {
        return null;
    }

    public DBManager getDbManager() {
        return dbManager;
    }
}
