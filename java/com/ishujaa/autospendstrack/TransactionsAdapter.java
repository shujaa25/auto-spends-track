package com.ishujaa.autospendstrack;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.ViewTransition;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private ArrayList<Transaction> transactions;
    private Listener listener;

    interface Listener{
        public void onClick(int pos);
    }

    public TransactionsAdapter(ArrayList<Transaction> transactions){
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_txn_card_view, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        TextView textViewAccount = (TextView) cardView.findViewById(R.id.text_view_card_acc_name);
        TextView textViewAmount = (TextView) cardView.findViewById(R.id.text_view_card_amount);
        TextView textViewNote = (TextView) cardView.findViewById(R.id.text_view_card_note);
        TextView textViewDate = (TextView) cardView.findViewById(R.id.text_view_card_date);

        String accName = null;
        try {
            accName = new DBAccess(holder.itemView.getContext())
                    .getAccountName(transactions.get(position).getAccountId());
        } catch (Exception e) {
            Toast.makeText(holder.itemView.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        textViewAccount.setText(String.valueOf(accName));
        textViewAmount.setText(String.valueOf(transactions.get(position).getAmount()));
        textViewNote.setText(transactions.get(position).getNote());
        textViewDate.setText(transactions.get(position).getDate());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null)
                    listener.onClick(position);
            }
        });
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            this.cardView = v;
        }
    }
}
