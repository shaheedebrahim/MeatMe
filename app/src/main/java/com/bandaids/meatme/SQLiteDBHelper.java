package com.bandaids.meatme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by shaheedebrahim on 2017-02-22.
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SausageFest";
    private static final String TABLE_GROUPS = "groups";
    private static final int DATABASE_VERSION = 2;

    private static final String KEY_GROUP_NAME = "group_name";
    private static final String KEY_GROUP_MEMBERS = "group_members";
    private static final String KEY_GROUP_ID = "group_id";

    private int groupID = 0;

    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS + "("
                + KEY_GROUP_ID + " INTEGER PRIMARY KEY," + KEY_GROUP_NAME + " TEXT,"
                + KEY_GROUP_MEMBERS + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_GROUPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addGroup(String groupName, ArrayList<String> friends) {
        SQLiteDatabase db = this.getWritableDatabase();

        String members = "";
        for (int i = 0; i < friends.size(); i++) {
            if (i == 0) members += friends.get(i);
            else members += " " + friends.get(i);
        }

        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_ID, 0);
        values.put(KEY_GROUP_NAME, groupName);
        values.put(KEY_GROUP_MEMBERS, members);

        groupID += 1;

        db.insert(TABLE_GROUPS, null, values);
        db.close();
    }

    /**
     * public ArrayList<String> getMembers(String groupName){
     * SQLiteDatabase db = this.getReadableDatabase();
     * <p>
     * Cursor cursor = db.query(TABLE_GROUPS, new String[] {KEY_GROUP_NAME, KEY_GROUP_MEMBERS}, KEY_GROUP_NAME +"=?",
     * new String[]{groupName}, null, null, null, null);
     * <p>
     * if (cursor!=null) cursor.moveToFirst();
     * <p>
     * ArrayList<String> members = new ArrayList<>(Arrays.asList(cursor.getString(1).split(" ")));
     * <p>
     * return members;
     * }
     **/

    public ArrayList<String> getAllGroups() {
        ArrayList<String> groups = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_GROUPS;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                groups.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        return groups;
    }
}
