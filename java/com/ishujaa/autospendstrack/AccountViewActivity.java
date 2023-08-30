package com.ishujaa.autospendstrack;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AccountViewActivity extends AppCompatActivity {

    static String ACC_ID_EXTRA = "acc_id";
    private DBAccess dbAccess;
    private int accId;

    private EditText editTextAccName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        accId = intent.getIntExtra(ACC_ID_EXTRA, -1);

        dbAccess = new DBAccess(this);
        editTextAccName = findViewById(R.id.edit_text_view_acc_name);

        if(accId != -1){
            try{
                editTextAccName.setText(dbAccess.getAccountName(accId));
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Invalid Account", Toast.LENGTH_LONG).show();
        }
    }

    public void btnDeleteClick(View view){
        try{
            if(accId != -1){
                if(accId != 1){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("Do you really want to delete?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Yes",(dialog, id) -> {
                        dbAccess.deleteAccount(accId);
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    builder1.setNegativeButton("No", (dialog, id) -> dialog.cancel());

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                else
                    Toast.makeText(this, "Error: Cannot delete Default account.",
                            Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnUpdateNameClick(View view){
        try{
            if(accId != -1){
                dbAccess.updateAccount(accId, editTextAccName.getText().toString());
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "Error: Not possible.", Toast.LENGTH_LONG).show();
            finish();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}