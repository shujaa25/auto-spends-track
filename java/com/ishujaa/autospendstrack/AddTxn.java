package com.ishujaa.autospendstrack;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddTxn extends AppCompatActivity {

    static final String MSG_EXTRA = "MSGEXTRA_AST_AddTxn";
    static final String SENDER_EXTRA = "SENDER_EXTRA";

    private EditText editTextAmount;
    private DBHelper dbHelper;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_txn);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        editTextAmount = findViewById(R.id.edit_text_amount_input);

        Intent intent = getIntent();
        String msg = intent.getStringExtra(MSG_EXTRA);
        String sender = intent.getStringExtra(SENDER_EXTRA);
        TextView textViewSender = findViewById(R.id.text_view_sender);
        textViewSender.setText("Sender: "+sender);
        TextView textViewMsg = findViewById(R.id.text_view_msg);
        textViewMsg.setText("Message: "+msg);

        dbHelper = new DBHelper(this);
        spinner = findViewById(R.id.spinner_acc_view);
        SimpleCursorAdapter cursorAdapter = dbHelper.getAccountsAdapter();
        spinner.setAdapter(cursorAdapter);

        processMsg(msg);

    }

    private void processMsg(String msg){
        int len = msg.length();
        int i = msg.indexOf("Rs"); //case issue, beginning issue
        if(i == -1)
            i = msg.indexOf("INR"); //USD... //INR may be after the value

        StringBuilder amountVal = new StringBuilder();
        if(i != -1){
            i+=2;
            while(i < len && msg.charAt(i) != ' ') i++;

            i++;
            while(i < len && msg.charAt(i) != ' ') //may not end with space or end with , or .
                amountVal.append(msg.charAt(i++));

            editTextAmount.setText(amountVal.toString());
        }
    }

    public void btnInsertTxnClick(View view){
        EditText editTextComment = findViewById(R.id.edit_text_comment_input);
        long accId = spinner.getSelectedItemId();
        try{
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            dbHelper.insertTxn(database, editTextAmount.getText().toString(),
                    accId, editTextComment.getText().toString());
            Toast.makeText(this, "Inserted.", Toast.LENGTH_SHORT).show();
            finish();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}