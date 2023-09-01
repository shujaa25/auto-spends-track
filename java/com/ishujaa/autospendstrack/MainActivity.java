package com.ishujaa.autospendstrack;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private DBAccess dbAccess;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add_new){
            Intent intent = new Intent(this, AddTxnActivity.class);
            intent.putExtra(AddTxnActivity.EXPLICIT_EXTRA, true);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_manage_acc){
            startActivity(new Intent(this, ManageAccActivity.class));
            return true;
        }else if(id == R.id.action_export_csv){
            startActivity(new Intent(this, ExportCSV.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        dbAccess = new DBAccess(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},  1);
        }

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            //replace with snack-bar to open settings directly
            Toast.makeText(this, "Please enable SMS permission from settings.", Toast.LENGTH_LONG).show();
        }*/

        setRecView();
    }

    private void setRecView(){
        ArrayList<Transaction> transactions = null;
        try{
            transactions = dbAccess.getTransactions();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        TransactionsAdapter transactionsAdapter = new TransactionsAdapter(transactions);
        RecyclerView recyclerView = findViewById(R.id.rec_view_txns);
        recyclerView.setAdapter(transactionsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Transaction> finalTransactions = transactions;
        transactionsAdapter.setListener(new TransactionsAdapter.Listener() {
            @Override
            public void onClick(int pos) {
                try{
                    Intent intent = new Intent(getApplicationContext(), TxnViewActivity.class);
                    intent.putExtra(TxnViewActivity.TXN_ID_EXTRA, finalTransactions.get(pos).getTxnId());
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRecView();
    }
}