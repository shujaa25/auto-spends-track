package com.ishujaa.autospendstrack;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ManageAccActivity extends AppCompatActivity {

    private DBAccess dbAccess;
    private ListView listViewAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_acc);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        dbAccess = new DBAccess(this);
        listViewAccounts = findViewById(R.id.list_view_acc);
        updateListView();
        listViewAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), AccViewActivity.class);
                intent.putExtra(AccViewActivity.ACC_ID_EXTRA, (int)id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }

    private void updateListView(){

        try{
            SimpleCursorAdapter cursorAdapter = dbAccess.getAccountsAdapter();
            listViewAccounts.setAdapter(cursorAdapter);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    public void btnAddNewAccClick(View view){
        EditText editTextNewAccName = findViewById(R.id.edit_text_new_acc_name);
        String newAccName = editTextNewAccName.getText().toString();

        if(newAccName.equals("") || newAccName.equals(" "))
            Toast.makeText(this, "Invalid Account Name.", Toast.LENGTH_SHORT).show();
        else{
            try{
                dbAccess.insertNewAcc(editTextNewAccName.getText().toString());
                Toast.makeText(this, "Successfully Inserted.", Toast.LENGTH_SHORT).show();
                editTextNewAccName.setText("");
                updateListView();
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}