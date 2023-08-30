package com.ishujaa.autospendstrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class DBAccess {
    private final Context context;
    private final SQLiteOpenHelper sqLiteOpenHelper;

    DBAccess(Context context){
        this.context = context;
        sqLiteOpenHelper = new DBHelper(context);
    }

    public void insertNewTxn(double amount, int accId, String note){
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("amount", amount);
        contentValues.put("acc_id", accId);
        contentValues.put("note", note);
        database.insert(DBHelper.TABLE_TXNS, null, contentValues);
    }

    public void insertNewAcc(String accountName){
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", accountName);
        database.insert(DBHelper.TABLE_ACCOUNTS, null, contentValues);
    }

    public String getAccountName(int id) throws Exception{
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_ACCOUNTS,
                new String[]{"name"}, "_id=?",
                new String[]{Integer.toString(id)},
                null, null, null);
        cursor.moveToFirst();
        if(cursor.getCount() != 0){
            return cursor.getString(0);
        }

        cursor.close();
        database.close();
        return null;
    }

    public void updateTransaction(int txnId, int accId, double amount, String note){
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        ContentValues targetValues = new ContentValues();
        targetValues.put("acc_id", accId);
        targetValues.put("amount", amount);
        targetValues.put("note", note);
        database.update(DBHelper.TABLE_TXNS, targetValues, "_id=?",
                new String[]{String.valueOf(txnId)});
        database.close();
    }

    public void deleteTransaction(int txnId){
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.delete(DBHelper.TABLE_TXNS, "_id=?",
                new String[]{String.valueOf(txnId)});
        database.close();
    }
    public void deleteAccount(int accId){
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.delete(DBHelper.TABLE_ACCOUNTS, "_id=?",
                new String[]{String.valueOf(accId)});
        database.close();
    }
    public void updateAccount(int accId, String name){
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        ContentValues targetValues = new ContentValues();
        targetValues.put("name", name);
        database.update(DBHelper.TABLE_ACCOUNTS, targetValues, "_id=?",
                new String[]{String.valueOf(accId)});
        database.close();
    }

    public Transaction getTransaction(int id) throws Exception{
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TXNS,
                new String[]{"_id", "acc_id",
                        "amount", "note", "date"}, "_id=?",
                new String[]{Integer.toString(id)},
                null, null, null);

        Transaction transaction = new Transaction();
        cursor.moveToFirst();
        if(cursor.getCount() == 0) return transaction;

        transaction.setTxnId(Integer.parseInt(cursor.getString(0)));
        transaction.setAccountId(Integer.parseInt(cursor.getString(1)));
        transaction.setAmount(Double.parseDouble(cursor.getString(2)));
        transaction.setNote(cursor.getString(3));
        transaction.setDate(cursor.getString(4));

        cursor.close();
        database.close();
        return transaction;
    }

    public ArrayList<Transaction> getTransactions() throws Exception{
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TXNS, new String[]{"_id", "acc_id",
                        "amount", "note", "date"},
                null, null, null, null, "_id");

        ArrayList<Transaction> arrayList = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount() == 0) return arrayList;
        do{
            Transaction transaction = new Transaction();
            transaction.setTxnId(Integer.parseInt(cursor.getString(0)));
            transaction.setAccountId(Integer.parseInt(cursor.getString(1)));
            transaction.setAmount(Double.parseDouble(cursor.getString(2)));
            transaction.setNote(cursor.getString(3));
            transaction.setDate(cursor.getString(4));
            arrayList.add(transaction);

        }while (cursor.moveToNext());

        cursor.close();
        database.close();;
        return arrayList;
    }

    public SimpleCursorAdapter getTxnsAdapter(){
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TXNS, new String[]{"_id", "note"},
                null, null, null, null, "_id");
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(context,
                android.R.layout.simple_list_item_1, cursor,
                new String[]{"note"}, new int[]{android.R.id.text1}, 0);
        return cursorAdapter;
    }

    public SimpleCursorAdapter getAccountsAdapter(){
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_ACCOUNTS, new String[]{"_id", "name"},
                null, null, null, null, "_id");
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(context,
                android.R.layout.simple_list_item_1, cursor,
                new String[]{"name"}, new int[]{android.R.id.text1}, 0);
        return cursorAdapter;
    }

}
