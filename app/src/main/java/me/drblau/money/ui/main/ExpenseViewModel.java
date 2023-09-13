package me.drblau.money.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;
import java.util.List;

import me.drblau.money.db.Expense;
import me.drblau.money.db.ExpenseRepository;

public class ExpenseViewModel extends AndroidViewModel {
    private final ExpenseRepository repo;
    private final LiveData<List<Expense>> allExpenses;
    private final LiveData<List<Expense>> monthExpenses;
    private final LiveData<List<Expense>> yearExpenses;
    private final MutableLiveData<LiveData<List<Expense>>> selected = new MutableLiveData<>();
    public ExpenseViewModel(Application application) {
        super(application);
        repo = new ExpenseRepository(application);
        allExpenses = repo.getAllExpenses();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        yearExpenses = repo.getYearExpenses(year);
        monthExpenses = repo.getMonthExpenses(month, year);
    }

    public void select(int i) {
        switch (i) {
            case 0:
                selected.setValue(monthExpenses);
                break;
            case 1:
                selected.setValue(yearExpenses);
                break;
            case 2:
                selected.setValue(allExpenses);
                break;
        }
    }

    public LiveData<List<Expense>> getSelected() {
        return selected.getValue();
    }

    public void insert(Expense expense) {
        repo.insert(expense);
    }

    void delete(Expense expense) {
        repo.delete(expense);
    }

    void clear() {
        repo.clear();
    }
}
