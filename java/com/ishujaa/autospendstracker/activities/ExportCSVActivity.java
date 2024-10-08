package com.ishujaa.autospendstracker.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ishujaa.autospendstracker.DBAccess;
import com.ishujaa.autospendstracker.R;
import com.ishujaa.autospendstracker.adapters.TransactionsAdapter;
import com.ishujaa.autospendstracker.helpers.WriteToCSVHelper;
import com.ishujaa.autospendstracker.pojo.Transaction;

import java.util.ArrayList;
import java.util.Calendar;

public class ExportCSVActivity extends AppCompatActivity {

    private DBAccess dbAccess;
    private Spinner spinnerAccount, spinnerOrderBy;
    private CheckBox checkBoxAccount, checkBoxAmount, checkBoxDates;
    private EditText editTextAmountMin, editTextAmountMax, editTextDateLow, editTextDateHigh;
    private ArrayList<Transaction> transactions;
    private Button buttonExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_csv);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //CRASHING IF NO DATA

        dbAccess = new DBAccess(this);
        spinnerAccount = findViewById(R.id.spinner_acc_name_view2);
        SimpleCursorAdapter cursorAdapter = dbAccess.getAccountsAdapter();
        spinnerAccount.setAdapter(cursorAdapter);
        spinnerAccount.setEnabled(false);

        spinnerOrderBy = findViewById(R.id.spinner_order_by);

        checkBoxAccount = findViewById(R.id.check_box_account);
        checkBoxAmount = findViewById(R.id.check_box_amount);
        checkBoxDates = findViewById(R.id.check_box_dates);

        checkBoxAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    spinnerAccount.setEnabled(true);
                }else spinnerAccount.setEnabled(false);
            }
        });

        editTextAmountMin = findViewById(R.id.edit_text_amt_min);
        editTextAmountMax = findViewById(R.id.edit_text_amt_max);
        editTextDateLow = findViewById(R.id.edit_text_date_low);
        editTextDateHigh = findViewById(R.id.edit_text_date_high);

        checkBoxAmount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    editTextAmountMin.setEnabled(true);
                    editTextAmountMax.setEnabled(true);
                }else{
                    editTextAmountMin.setEnabled(false);
                    editTextAmountMax.setEnabled(false);
                }
            }
        });

        checkBoxDates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    editTextDateLow.setEnabled(true);
                    editTextDateHigh.setEnabled(true);
                }else{
                    editTextDateLow.setEnabled(false);
                    editTextDateHigh.setEnabled(false);
                }
            }
        });

        buttonExport = findViewById(R.id.btnExport);
        double[] amts = dbAccess.getMinMaxAmount();
        editTextAmountMin.setText(String.valueOf(amts[0]));
        editTextAmountMax.setText(String.valueOf(amts[1]));

        String[] dates = dbAccess.getMinMaxDates();
        editTextDateLow.setText(dates[0].substring(0, 10));
        editTextDateHigh.setText(dates[1].substring(0, 10));

        ImageButton buttonSelectDateLow = findViewById(R.id.btnSelectDateLow);
        ImageButton buttonSelectDateHigh = findViewById(R.id.btnSelectDateHigh);

        buttonSelectDateLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(editTextDateLow);
            }
        });

        buttonSelectDateHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(editTextDateHigh);
            }
        });

    }

    private void setDate(EditText editText){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ExportCSVActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String monthString = "";
                        int month = monthOfYear+1;
                        if(month < 10){
                            monthString = "0"+month;
                        }else monthString+=month;

                        String date = year+"-"+monthString+"-"+dayOfMonth;
                        editText.setText(date);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void setRecView() {
        transactions = null;
        try {
            boolean isAccountEnabled = checkBoxAccount.isChecked();
            boolean isAmountEnabled = checkBoxAmount.isChecked();
            boolean isDatesEnabled = checkBoxDates.isChecked();

            int currentAccId = (int) spinnerAccount.getSelectedItemId();

            double amtLow = Double.parseDouble(editTextAmountMin.getText().toString());
            double amtHigh = Double.parseDouble(editTextAmountMax.getText().toString());

            String dateLow = editTextDateLow.getText().toString();
            String dateHigh = editTextDateHigh.getText().toString();

            transactions = dbAccess.getTransactions(isAccountEnabled, isAmountEnabled, isDatesEnabled,
                    currentAccId, amtLow, amtHigh, dateLow,dateHigh, spinnerOrderBy.getSelectedItem().toString());
            if (transactions != null && transactions.size() >= 1) {
                buttonExport.setEnabled(true);
                TransactionsAdapter transactionsAdapter = new TransactionsAdapter(transactions);
                RecyclerView recyclerView = findViewById(R.id.rec_view_txns2);
                recyclerView.setAdapter(transactionsAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));


                ArrayList<Transaction> finalTransactions = transactions;
                transactionsAdapter.setListener(new TransactionsAdapter.Listener() {
                    @Override
                    public void onClick(int pos) {
                        try {
                            Intent intent = new Intent(getApplicationContext(), TxnViewActivity.class);
                            intent.putExtra(TxnViewActivity.TXN_ID_EXTRA, finalTransactions.get(pos).getTxnId());
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                double sum = 0;
                for(int i=0;i<finalTransactions.size();i++){
                    sum+=finalTransactions.get(i).getAmount();
                }
                //Toast.makeText(this, "Sum: "+sum, Toast.LENGTH_SHORT).show();
                ((TextView)findViewById(R.id.text_view_sum_display))
                        .setText("Sum: "+Math.round(sum));
            } else{
                transactions = null;
                Toast.makeText(this, "No transaction found for selected criteria.",
                        Toast.LENGTH_SHORT).show();
                if(buttonExport.isEnabled()){
                    buttonExport.setEnabled(false);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setRecView();
    }

    public void btnFetchTxnClick(View view) {
        setRecView();
    }

    public void btnExportClick(View view) {
        if(transactions!= null){
            try{
                new WriteToCSVHelper(this, transactions);
                Toast.makeText(this, "Saved to Documents/AutoSpendsTracker folder.",
                        Toast.LENGTH_LONG).show();
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}