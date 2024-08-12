package com.ishujaa.autospendstracker.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.ishujaa.autospendstracker.DBAccess;
import com.ishujaa.autospendstracker.R;
import com.ishujaa.autospendstracker.pojo.Transaction;

public class TxnViewActivity extends AppCompatActivity {

    static String TXN_ID_EXTRA = "TXN_ID_EXTRA";
    private DBAccess dbAccess;
    private EditText editTextAmt,editTextNote, editTextDateTime;
    private Spinner spinner;
    private int txnId;
    private long accId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txn_view);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        txnId = intent.getIntExtra(TXN_ID_EXTRA, -1);

        editTextAmt = findViewById(R.id.edit_text_amt_val);
        editTextNote= findViewById(R.id.edit_text_note_val);
        editTextDateTime = findViewById(R.id.edit_text_datetime_val);

        dbAccess = new DBAccess(this);

        if(txnId != -1){

            spinner = findViewById(R.id.spinner_acc_name_view);
            SimpleCursorAdapter cursorAdapter = dbAccess.getAccountsAdapter();

            spinner.setAdapter(cursorAdapter);

            try{

                Transaction transaction = dbAccess.getTransaction(txnId);

                editTextAmt.setText(String.valueOf(transaction.getAmount()));
                accId = transaction.getAccountId();
                editTextNote.setText(transaction.getNote());
                editTextDateTime.setText(transaction.getDate());

                for(int i=0;i<cursorAdapter.getCount();i++) {
                    if(accId == cursorAdapter.getItemId(i)){
                        spinner.setSelection(i);
                        break;
                    }
                }

            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Error: Invalid ID supplied.", Toast.LENGTH_LONG).show();
        }
    }

    public void btnUpdateTxnClick(View view) {
        try{
            if(txnId != -1){
                dbAccess.updateTransaction(txnId, (int)spinner.getSelectedItemId(), Double.parseDouble(editTextAmt.getText().toString()),
                        editTextNote.getText().toString());
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnDeleteTxnClick(View view) {
        try{
            if(txnId != -1){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Do you really want to delete?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",(dialog, id) -> {
                    dbAccess.deleteTransaction((int)txnId);
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                });
                builder1.setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}