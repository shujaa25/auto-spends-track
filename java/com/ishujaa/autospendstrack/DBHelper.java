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

    public void insertRecord(SQLiteDatabase db, String name){
        ContentValues targetValues = new ContentValues();
        targetValues.put("name", name);
        db.insert(TABLE_ACCOUNTS, null, targetValues);
    }

    public void insertTxn(SQLiteDatabase db, String amount, long acc_id, String cmt){
        ContentValues contentValues = new ContentValues();
        contentValues.put("amount", amount);
        contentValues.put("acc_id", acc_id);
        contentValues.put("comment", cmt);
        db.insert(TABLE_TXNS, null, contentValues);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE table_accounts " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL);");

        db.execSQL("CREATE TABLE table_txns " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount TEXT NOT NULL," +
                "acc_id INTEGER NOT NULL," +
                "comment TEXT," +
                "date DATETIME DEFAULT (datetime('now', 'localtime')) NOT NULL," +
                "FOREIGN KEY(acc_id) REFERENCES table_accounts(_id));");

        insertRecord(db, "Default");
        insertTxn(db, "123123", 1, "test");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public SimpleCursorAdapter getTxnsAdapter(){
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TXNS, new String[]{"_id", "comment"},
                null, null, null, null, "_id");
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(context,
                android.R.layout.simple_list_item_1, cursor,
                new String[]{"comment"}, new int[]{android.R.id.text1}, 0);
        return cursorAdapter;
    }

    public SimpleCursorAdapter getAccountsAdapter(){
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_ACCOUNTS, new String[]{"_id", "name"},
                null, null, null, null, "_id");
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(context,
                android.R.layout.simple_list_item_1, cursor,
                new String[]{"name"}, new int[]{android.R.id.text1}, 0);
        return cursorAdapter;
    }
}
