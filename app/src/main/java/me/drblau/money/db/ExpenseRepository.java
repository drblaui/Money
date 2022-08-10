package me.drblau.money.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.List;

public class ExpenseRepository {
    private final ExpenseDAO expenseDAO;
    private final LiveData<List<Expense>> allExpenses;

    public ExpenseRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);

        expenseDAO = db.expenseDAO();
        //generateTestData(expenseDAO);
        allExpenses = expenseDAO.getAll();
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    public LiveData<List<Expense>> getMonthExpenses(int month, int year) {
        return expenseDAO.getFromMonth(month, year);
    }

    public LiveData<List<Expense>> getYearExpenses(int year) {
        return expenseDAO.getFromYear(year);
    }

    public void insert(Expense expense) {
        AppDatabase.dbWriteExecutor.execute(() -> expenseDAO.insert(expense));
    }

    public void delete(Expense expense) {
        AppDatabase.dbWriteExecutor.execute(() -> expenseDAO.delete(expense));
    }

    private static void generateTestData(ExpenseDAO dao) {
        AppDatabase.dbWriteExecutor.execute(() -> {
            dao.clear();

            Expense expense = new Expense("Very old Test Data", "For testing purposes. Inserted itself at the begin of time", -100, 1, 1, 1970);
            dao.insert(expense);
            Calendar cal = Calendar.getInstance();
            expense = new Expense("Fresh Test Data", "For testing purposes. Inserted itself at the current day", 100, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
            dao.insert(expense);
        });
    }
}
