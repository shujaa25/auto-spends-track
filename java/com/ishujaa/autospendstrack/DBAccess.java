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

    public double[] getMinMaxAmount(){
        double[] res = new double[2];
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TXNS,
                new String[]{"MIN(amount), MAX(amount)"}, null,
                null, null, null, null);
        cursor.moveToFirst();
        if(cursor.getCount() != 0){
            res[0] = cursor.getDouble(0);
            res[1] = cursor.getDouble(1);
        }

        cursor.close();
        database.close();
        return res;
    }

    public String[] getMinMaxDates(){
        String[] res = new String[2];
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TXNS,
                new String[]{"MIN(date), MAX(date)"}, null,
                null, null, null, null);
        cursor.moveToFirst();
        if(cursor.getCount() != 0){
            res[0] = cursor.getString(0);
            res[1] = cursor.getString(1);
        }

        cursor.close();
        database.close();
        return res;
    }

    public ArrayList<Transaction>getTransactions(boolean acc, boolean amt, boolean date,
            int acc_id, double amtLow, double amtHigh, String date1, String date2) throws Exception{
        String sql = "";
        if(acc && !amt && !date)
            sql ="SELECT _id, acc_id, amount, note, date FROM table_txns " +
                    "WHERE acc_id = "+acc_id;
        else if(acc && amt && !date)
            sql = "SELECT _id, acc_id, amount, note, date FROM table_txns " +
                    "WHERE acc_id = "+acc_id+" AND amount BETWEEN "+amtLow+" AND "+amtHigh+";";
        else if(acc && !amt && date)
            sql = "SELECT _id, acc_id, amount, note, date FROM table_txns " +
                    "WHERE acc_id = "+acc_id+" AND date BETWEEN '"+date1+"' AND '"+date2+"';";
        else if(acc && amt && date)
            sql = "SELECT _id, acc_id, amount, note, date FROM table_txns " +
                    "WHERE acc_id = "+acc_id+" AND date BETWEEN '"+date1+"' AND '"+date2+
                    "' AND amount BETWEEN "+amtLow+" AND "+amtHigh+";";
        else if (!acc && amt && !date)
            sql ="SELECT _id, acc_id, amount, note, date FROM table_txns " +
                    "WHERE amount BETWEEN "+amtLow+" AND "+amtHigh+";";
        else if (!acc && !amt && date)
            sql ="SELECT _id, acc_id, amount, note, date FROM table_txns " +
                    "WHERE date BETWEEN '"+date1+"' AND '"+date2+"';";
        else if (!acc && amt && date)
            sql = "SELECT _id, acc_id, amount, note, date FROM table_txns " +
                    "WHERE date BETWEEN '"+date1+"' AND '"+date2+
                    "' AND amount BETWEEN "+amtLow+" AND "+amtHigh+";";
        else
            sql = "SELECT _id, acc_id, amount, note, date FROM table_txns;";
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, null);

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
