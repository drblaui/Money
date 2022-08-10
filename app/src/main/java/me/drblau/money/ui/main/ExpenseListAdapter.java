package me.drblau.money.ui.main;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import me.drblau.money.R;
import me.drblau.money.db.Expense;

public class ExpenseListAdapter extends ListAdapter<Expense, ExpenseViewHolder> {
    private ViewGroup parent;
    private Expense recentlyDeleted;
    private final ExpenseViewModel model;
    protected ExpenseListAdapter(@NonNull DiffUtil.ItemCallback<Expense> diffCallback, ExpenseViewModel model) {
        super(diffCallback);
        this.model = model;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        return ExpenseViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense current = getItem(position);
        String date = String.format(Locale.getDefault(),"%02d.%02d.%d", current.day, current.month, current.year);
        holder.bind(current.reason, current.amount, date);
    }

    static class ExpenseDiff extends DiffUtil.ItemCallback<Expense> {
        @Override
        public boolean areItemsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
            return oldItem.reason.equals(newItem.reason) && oldItem.amount == newItem.amount &&
                    oldItem.day == newItem.day && oldItem.month == newItem.month &&
                    oldItem.year == newItem.year && oldItem.description.equals(newItem.description);
        }
    }

    public void removeItem(int position) {
        recentlyDeleted = getItem(position);
        model.delete(recentlyDeleted);
        showUndoSnackbar(recentlyDeleted.reason);
    }

    private void showUndoSnackbar(String reason) {
        Snackbar snackbar = Snackbar.make(parent, String.format(parent.getResources().getString(R.string.snackbar_title), reason), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snackbar_undo, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        model.insert(recentlyDeleted);
    }
}
