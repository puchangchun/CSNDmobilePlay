package com.android.puccmobileplay.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.puccmobileplay.model.dao.UserAccountTable;


/**
 * Created by 99653 on 2017/11/4.
 */

public class UserAccountDB extends SQLiteOpenHelper{
    public UserAccountDB(Context context) {
        super(context, UserAccountTable.TAB_NAME, null,UserAccountTable.TAB_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserAccountTable.CREATE_TAB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
