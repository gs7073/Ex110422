package com.example.ex11042;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper class to create and manage the SQLite database.
 */
public class HelperDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expenses.db";
    private static final int DATABASE_VERSION = 1;
    String strCreate, strDelete;

    /**
     * Constructor for the database helper.
     * @param context The context of the application
     */
    public HelperDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the table.
     * @param db The database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        strCreate="CREATE TABLE "+Constant.TABLE_NAME;
        strCreate+=" ("+Constant.KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,";
        strCreate+=" "+Constant.KEY_DESC+" TEXT,";
        strCreate+=" "+Constant.KEY_AMOUNT+" REAL,";
        strCreate+=" "+Constant.KEY_CATEGORY+" TEXT,";
        strCreate+=" "+Constant.KEY_DATE+" TEXT";
        strCreate+=") ;";

        Log.d("HelperDB", "Query: " + strCreate);

        db.execSQL(strCreate);
    }

    /**
     * Upgrades the database.
     * @param db The database
     * @param oldVer The old version
     * @param newVer The new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        strDelete="DROP TABLE IF EXISTS "+Constant.TABLE_NAME;
        db.execSQL(strDelete);

        onCreate(db);
    }
}