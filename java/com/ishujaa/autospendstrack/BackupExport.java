package com.ishujaa.autospendstrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class BackupExport extends AppCompatActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_export);

        dbHelper = new DBHelper(this);
    }

    public void btnExportCSVClick(View view){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "SELECT txn._id, amount, comment, date, name FROM table_txns AS txn, " +
                "table_accounts AS acc WHERE acc_id == acc._id ORDER BY txn._id;";

        Cursor cursor = null;
        StringBuilder res = new StringBuilder("Serial No.,Transaction ID,Amount (INR),Account Name,Comment,DateTime\n");;
        try{
            cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            int ctr= 1;
            while (cursor.moveToNext()){
                StringBuilder line = new StringBuilder();
                line.append(String.valueOf(ctr++)); line.append(",");
                line.append(cursor.getString(0)); line.append(",");
                line.append(cursor.getString(1)); line.append(",");
                line.append(cursor.getString(4)); line.append(",");
                line.append(cursor.getString(2)); line.append(",");
                line.append(cursor.getString(3)); line.append("\n");

                res.append(line);
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }finally {
            if(cursor != null) cursor.close();
            database.close();
        }
        EditText editTextFileName = findViewById(R.id.edit_text_file_name_input);
        try {
            ContentValues values = new ContentValues();

            values.put(MediaStore.MediaColumns.DISPLAY_NAME, editTextFileName.getText().toString()); //file name
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");//file extension, will automatically add to file
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/AutoSpendsTrack/");//end "/" is not mandatory

            Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);//important!
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            outputStream.write(res.toString().getBytes());
            outputStream.close();
            Toast.makeText(view.getContext(), "File exported to Documents/AutoSpendsTrack directory.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(view.getContext(), "Fail to create file", Toast.LENGTH_SHORT).show();
        }
    }

}