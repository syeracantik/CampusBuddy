package com.groupprojek.campusbuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class LostFoundDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "lostfound.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "items";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESC = "description";
    private static final String COL_IMAGE = "image";

    public LostFoundDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + " TEXT,"
                + COL_DESC + " TEXT,"
                + COL_IMAGE + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addItem(String title, String desc, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_DESC, desc);
        cv.put(COL_IMAGE, image);
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public ArrayList<LostFoundModel> getAllItems() {
        ArrayList<LostFoundModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new LostFoundModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}