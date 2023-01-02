package com.example.loansharkapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BorrowersProfile.db";
    public static final String TABLE_NAME = "profile_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "PHOTO";
    public static final String COL_4 = "TOTAL_BORROWING_AMOUNT";
    public static final String COL_5 = "PHOTO_BITMAP";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, PHOTO TEXT, TOTAL_BORROWING_AMOUNT INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData (String name, String photo, String totalBorrowingAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, photo);
        contentValues.put(COL_4, totalBorrowingAmount);
        long result = db.insert(TABLE_NAME, null,contentValues);
        if (result == -1) {
            //Return false when the data failed to insert
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public boolean updateData(String name, String photo, String totalBorrowingAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, photo);
        contentValues.put(COL_4, totalBorrowingAmount);
        db.update(TABLE_NAME, contentValues, "NAME = ?", new String[] {name});
        return true;
    }

    public Integer deleteData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "NAME = ?", new String[] {name});
    }

}
