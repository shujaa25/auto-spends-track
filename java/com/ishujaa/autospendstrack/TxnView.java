package com.ishujaa.autospendstrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TxnView extends AppCompatActivity {

    static String TXN_ID_EXTRA = "TXN_ID_EXTRA";
    private DBHelper dbHelper;
    private TextView textViewSender, textViewMsg, textViewAmt, textViewAccName, textViewCmt,
    textViewDateTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txn_view);

        Intent intent = getIntent();
        long id = intent.getLongExtra(TXN_ID_EXTRA, -1);

        textViewSender = findViewById(R.id.text_view_sender_val);
        textViewMsg = findViewById(R.id.text_view_msg_val);
        textViewAccName = findViewById(R.id.text_view_acc_name_val);
        textViewAmt = findViewById(R.id.text_view_amt_val);
        textViewCmt = findViewById(R.id.text_view_cmt_val);
        textViewDateTime = findViewById(R.id.text_view_datetime_val);

        dbHelper = new DBHelper(this);

        if(id != -1){
            try{
                SQLiteDatabase database = dbHelper.getReadableDatabase();
                Cursor cursor = database.query(DBHelper.TABLE_TXNS, new String[]{"amount, acc_id," +
                                "comment, date"},
                        "_id=?", new String[]{String.valueOf(id)},
                        null, null, null);
                cursor.moveToFirst();

                textViewAmt.setText("Amount: "+cursor.getString(0));
                long accId = Long.parseLong(cursor.getString(1));
                textViewCmt.setText("Comment: "+cursor.getString(2));
                textViewDateTime.setText("Date Time: "+cursor.getString(3));

                textViewSender.setText("Sender: "); textViewMsg.setText("Message: ");

                cursor.close();

                cursor = database.query(DBHelper.TABLE_ACCOUNTS, new String[]{"name"},
                        "_id=?", new String[]{String.valueOf(accId)},
                        null, null, null);
                cursor.moveToFirst();

                textViewAccName.setText("Account: "+cursor.getString(0));
                cursor.close();
                database.close();
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Invalid ID supplied.", Toast.LENGTH_LONG).show();
        }
    }
}