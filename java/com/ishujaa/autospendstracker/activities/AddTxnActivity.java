package com.ishujaa.autospendstracker.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ishujaa.autospendstracker.DBAccess;
import com.ishujaa.autospendstracker.R;

public class AddTxnActivity extends AppCompatActivity {

    public static final String MSG_EXTRA = "MSGEXTRA_AST_AddTxn";
    public static final String SENDER_EXTRA = "SENDER_EXTRA";
    public static final String EXPLICIT_EXTRA = "EXPLICIT_EXTRA";

    private EditText editTextAmount, editTextNote;
    private DBAccess dbAccess;
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
        editTextNote = findViewById(R.id.edit_text_note_input);
        Intent intent = getIntent();
        boolean isExplicit = intent.getBooleanExtra(EXPLICIT_EXTRA, false);
        if(!isExplicit){
            String msg = intent.getStringExtra(MSG_EXTRA);
            String sender = intent.getStringExtra(SENDER_EXTRA);
            TextView textViewSender = findViewById(R.id.text_view_sender);
            textViewSender.setVisibility(View.VISIBLE);
            textViewSender.setText("Sender: "+sender);
            TextView textViewMsg = findViewById(R.id.text_view_msg);
            textViewMsg.setVisibility(View.VISIBLE);
            textViewMsg.setText("Message: "+msg);
            String amountVal = fetchAmount(msg);
            editTextAmount.setText(amountVal);
            if(amountVal.isEmpty()){
                editTextAmount.requestFocus();
            }else editTextNote.requestFocus();
        }else editTextAmount.requestFocus();

        dbAccess = new DBAccess(this);
        spinner = findViewById(R.id.spinner_acc_view);
        SimpleCursorAdapter cursorAdapter = dbAccess.getAccountsAdapter();
        spinner.setAdapter(cursorAdapter);



        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    /*private void processMsg(String msg){
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
                if(msg.charAt(i)!=','){
                    amountVal.append(msg.charAt(i++));
                }else i++;

            editTextAmount.setText(amountVal.toString());
        }
    }*/

    private String fetchAmount(String m){
        m = m.toLowerCase();
        int i = m.indexOf("rs");
        int len = m.length();

        if(i == -1) i = 0;

        boolean confidence = false;
        StringBuilder amount = new StringBuilder();
        while(i < len && !confidence){

            while(i < len && !(m.charAt(i) >= '0' && m.charAt(i) <= '9')) i++;

            StringBuilder val = new StringBuilder();
            while(i < len && ((m.charAt(i) == '.') || (m.charAt(i) == ',') || (m.charAt(i) >= '0' && m.charAt(i) <= '9'))){
                if(m.charAt(i) == ','){
                    i++;
                    continue;
                }
                if(m.charAt(i) == '.') confidence = true;
                val.append(m.charAt(i++));
            }
            amount=val;
        }

        return amount.toString();
    }

    public void btnInsertTxnClick(View view){
        long accId = spinner.getSelectedItemId();
        try{
            dbAccess.insertNewTxn(Double.parseDouble(editTextAmount.getText().toString()),
                    (int)accId, editTextNote.getText().toString());
            Toast.makeText(this, "Inserted.", Toast.LENGTH_SHORT).show();
            finish();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}