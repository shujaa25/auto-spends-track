package com.ishujaa.autospendstrack;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView listViewTxn;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        listViewTxn = findViewById(R.id.list_view_txns);
        listViewTxn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), TxnView.class);
                intent.putExtra(TxnView.TXN_ID_EXTRA, id);
                startActivity(intent);
            }
        });
        updateListView();

        Intent intent = new Intent(this, AddTxn.class);
        intent.putExtra(AddTxn.SENDER_EXTRA, "SENDERTEST");
        intent.putExtra(AddTxn.MSG_EXTRA, "Rs. 1212.10 debited from HDFC Bank");
        startActivity(intent);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2);
        }

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            //replace with snack-bar to open settings directly
            Toast.makeText(this, "Please enable SMS permission from settings.", Toast.LENGTH_LONG).show();
        }*/
    }

    private void updateListView(){
        try {
            listViewTxn.setAdapter(dbHelper.getTxnsAdapter());
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }

    public void btnManageAccountsClick(View view){
        Intent intent = new Intent(this, ManageAcc.class);
        startActivity(intent);
    }
}