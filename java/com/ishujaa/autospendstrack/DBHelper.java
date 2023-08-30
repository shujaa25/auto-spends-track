package com.ishujaa.autospendstrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ast_db";
    private static final int DB_VERSION = 1;

    static final String TABLE_ACCOUNTS = "table_accounts";
    static final String TABLE_TXNS = "table_txns";
    Context context;
    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE table_accounts " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE NOT NULL);");

        db.execSQL("INSERT INTO table_accounts VALUES (1, 'Default');");
        db.execSQL("CREATE TABLE table_txns " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount TEXT NOT NULL," +
                "acc_id INTEGER NOT NULL DEFAULT 1 REFERENCES table_accounts(_id) ON DELETE SET DEFAULT," +
                "note TEXT DEFAULT 'none'," +
                "date DATETIME DEFAULT (datetime('now', 'localtime')) NOT NULL" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
