package com.groupprojek.campusbuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AssignmentDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CampusBuddy.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "assignments";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DUEDATE = "duedate";
    private static final String COLUMN_UID = "uid";

    public AssignmentDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DUEDATE + " TEXT,"
                + COLUMN_UID + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_UID + " TEXT");
        }
    }

    // ➕ Create
    public void addAssignment(String title, String dueDate, String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DUEDATE, dueDate);
        values.put(COLUMN_UID, uid);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // 🔍 Read by uid
    public ArrayList<AssignmentModel> getAssignmentsByUid(String uid) {
        ArrayList<AssignmentModel> assignmentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_UID + "=?",
                new String[]{uid}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                AssignmentModel assignment = new AssignmentModel();
                assignment.setId(cursor.getInt(0));
                assignment.setTitle(cursor.getString(1));
                assignment.setDueDate(cursor.getString(2));
                assignmentList.add(assignment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return assignmentList;
    }

    // ✏️ Update
    public void updateAssignment(int id, String title, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DUEDATE, dueDate);

        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ❌ Delete
    public void deleteAssignment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}