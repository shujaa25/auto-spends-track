package com.ishujaa.autospendstracker.helpers;

import android.content.Context;

import com.ishujaa.autospendstracker.DBAccess;

import java.util.HashMap;

public class RestoreHelper {

    Context context;

    public RestoreHelper(Context context){
        this.context = context;
    }

    void restore(String csv){

        String[] txns = csv.split("\n");
        HashMap<String, Integer> map = new HashMap<>();

        for(int i=1;i<txns.length;i++){
            String[] txn = txns[i].split(",");

            DBAccess dbAccess = new DBAccess(context);
            int id = -1;
            if(!map.containsKey(txn[1])){
                id = (int)dbAccess.insertNewAcc(txn[1]);
                map.put(txn[1], id);
            }else{
                id = map.get(txn[1]);
            }

            dbAccess.insertNewTxn(Double.parseDouble(txn[2]), id, txn[3], txn[4]);
        }

    }

}
