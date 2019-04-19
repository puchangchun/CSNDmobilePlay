package com.android.puccmobileplay.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.puccmobileplay.model.bean.UserInfo;
import com.android.puccmobileplay.model.db.UserAccountDB;


/**
 * Created by 99653 on 2017/11/4.
 * UserAccount数据库的操作类
 */

public class UserAccountDao {
    private final UserAccountDB mHelper;
    public UserAccountDao(Context context) {
         mHelper = new UserAccountDB(context);
    }

    /**
     * 添加用户到数据库
     * @param userInfo
     */
    public void addAccount(UserInfo userInfo){
        //获取数据库对象
        SQLiteDatabase sqLiteDatabase = mHelper.getReadableDatabase();

        // 执行添加操作
        ContentValues values = new ContentValues();
        values.put(UserAccountTable.COL_HXID, userInfo.getHxid());
        values.put(UserAccountTable.COL_NAME, userInfo.getName());
        values.put(UserAccountTable.COL_NICK, userInfo.getNick());
        values.put(UserAccountTable.COL_PHOTO, userInfo.getPhoto());

        sqLiteDatabase.replace(UserAccountTable.TAB_NAME,null,values);

    }

    /**
     * 根据HXID取得用户
     * @param hxId
     * @return
     */
    public UserInfo getAccountByHxId(String hxId){
        //获取数据库
        SQLiteDatabase sqLiteDatabase = mHelper.getReadableDatabase();
        //查询
        String sql = "select * from "
                + UserAccountTable.TAB_NAME + " where "
                + UserAccountTable.COL_HXID + " =?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{hxId});
        UserInfo userInfo = null;
        if (cursor.moveToNext()){
            userInfo = new UserInfo();
            // 封装对象
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));

        }
        //关闭
        cursor.close();
        //返回
        return userInfo;
    }
}
