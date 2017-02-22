package com.bandaids.meatme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shaheedebrahim on 2017-02-22.
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SausageFest";
    private static final int DATABASE_VERSION = 2;

    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
