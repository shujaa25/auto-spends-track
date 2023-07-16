package com.ishujaa.autospendstrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AccountView extends AppCompatActivity {

    static String ACC_ID_EXTRA = "acc_id";
    private DBHelper dbHelper;
    private long accId;

    private EditText editTextAccName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);

        Intent intent = getIntent();
        accId = intent.getLongExtra(ACC_ID_EXTRA, -1);

        dbHelper = new DBHelper(this);
        editTextAccName = findViewById(R.id.edit_text_view_acc_name);

        if(accId != -1){
            try{
                SQLiteDatabase database = dbHelper.getReadableDatabase();
                Cursor cursor = database.query(DBHelper.TABLE_ACCOUNTS, new String[]{"name"},
                        "_id=?", new String[]{String.valueOf(accId)},
                        null, null, null);
                cursor.moveToFirst();
                editTextAccName.setText(cursor.getString(0));
                cursor.close();
                database.close();
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Invalid Account", Toast.LENGTH_LONG).show();
        }
    }

    public void btnDeleteClick(View view){

        //show confirmation dialog

        try{
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            database.delete(DBHelper.TABLE_ACCOUNTS, "_id=?",
                    new String[]{String.valueOf(accId)});
            database.close();
            Toast.makeText(this, "Deleted.", Toast.LENGTH_SHORT).show();
            finish();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnUpdateNameClick(View view){
        try{
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", editTextAccName.getText().toString());
            database.update(DBHelper.TABLE_ACCOUNTS, contentValues,"_id=?",
                    new String[]{String.valueOf(accId)});
            database.close();
            Toast.makeText(this, "Updated.", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}