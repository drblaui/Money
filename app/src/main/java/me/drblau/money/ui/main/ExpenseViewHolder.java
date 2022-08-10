package me.drblau.money.ui.main;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import me.drblau.money.R;

public class ExpenseViewHolder extends RecyclerView.ViewHolder {
    private final TextView reason;
    private final TextView amount;
    private final TextView date;

    private ExpenseViewHolder(View expenseView) {
        super(expenseView);
        reason = expenseView.findViewById(R.id.reason);
        amount = expenseView.findViewById(R.id.amount);
        date = expenseView.findViewById(R.id.date);
    }

    public void bind(String reason, double amount, String date) {
        this.reason.setText(reason);
        if(amount < 0) this.amount.setBackgroundColor(Color.RED);
        else this.amount.setBackgroundColor(Color.GREEN);
        this.amount.setText(String.format(Locale.getDefault(), "%s%.2f€", (amount >= 0 ? "+" : ""), amount));
        this.date.setText(date);
    }

    static ExpenseViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new ExpenseViewHolder(view);
    }
}
