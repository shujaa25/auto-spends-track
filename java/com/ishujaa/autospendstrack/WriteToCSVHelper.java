package com.ishujaa.autospendstrack;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class WriteToCSVHelper {
    private DBAccess dbAccess;
    private Context context;
    WriteToCSVHelper(Context context, ArrayList<Transaction> transactions) throws Exception{
        this.context = context;
        dbAccess = new DBAccess(context);
        StringBuilder res = new StringBuilder();
        res.append("No.,Account,Amount,Note,Date\n");

        int ctr=1;
        for(Transaction transaction: transactions){
            StringBuilder line = new StringBuilder();
            line.append(ctr++);line.append(",");
            line.append(dbAccess.getAccountName(transaction.getAccountId()));line.append(",");
            line.append(transaction.getAmount());line.append(",");
            line.append(transaction.getNote().replaceAll(","," "));line.append(",");
            line.append(transaction.getDate());line.append("\n");
            res.append(line);
        }

        performWrite(res.toString());

    }

    private void performWrite(String s) throws Exception{
        OutputStream outputStream;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentValues values = new ContentValues();

            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "export" + ".csv");   // file name
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/AutoSpendsTracker");

            Uri extVolumeUri = MediaStore.Files.getContentUri("external");
            Uri fileUri = context.getContentResolver().insert(extVolumeUri, values);

            outputStream = context.getContentResolver().openOutputStream(fileUri);
        }
        else {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() +
                    "Documents/AutoSpendsTracker";
            File file = new File(path, "export" + ".csv");
            outputStream = new FileOutputStream(file);
        }

        byte[] bytes = s.getBytes();
        outputStream.write(bytes);
        outputStream.close();
    }
}
