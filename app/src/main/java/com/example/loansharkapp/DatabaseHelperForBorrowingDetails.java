package com.example.loansharkapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.File;

public class DatabaseHelperForBorrowingDetails extends SQLiteOpenHelper {

    public static String databaseName;
    public static Context context;
    public static final String TABLE_NAME = "profile_details_table";
    public static final String COL_1 = "Id";
    public static final String COL_2 = "No";
    public static final String COL_3 = "Item";
    public static final String COL_4 = "Date";
    public static final String COL_5 = "Amount";

    public DatabaseHelperForBorrowingDetails(@Nullable Context _context, @Nullable String name) {
        super(_context, name, null, 1);
        databaseName = name;
        context = _context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NO TEXT, ITEM TEXT, DATE TEXT, AMOUNT INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData (String no, String item, String date, String amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, no);
        contentValues.put(COL_3, item);
        contentValues.put(COL_4, date);
        contentValues.put(COL_5, amount);
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

    public boolean updateData(String oriNo, String newNo, String item, String date, String amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, newNo);
        contentValues.put(COL_3, item);
        contentValues.put(COL_4, date);
        contentValues.put(COL_5, amount);
        db.update(TABLE_NAME, contentValues, "NO = ?", new String[] {oriNo});
        return true;
    }

    public Integer deleteData(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "NO = ?", new String[] {number});
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public void deleteDatabase(String database) {
        context.deleteDatabase(database);
    }
}
