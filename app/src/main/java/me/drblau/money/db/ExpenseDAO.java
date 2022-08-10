package me.drblau.money.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExpenseDAO {
    /**
     *  Returns whole database
     * @return List of all expenses
     */
    @Query("SELECT * FROM expenses ORDER BY year DESC, month DESC, day DESC")
    LiveData<List<Expense>> getAll();

    /**
     * Returns all elements from specific month
     * @param month the month as int (1 = January, 12 = December)
     * @param year the year it happened in YYYY format
     * @return List of expenses in the specific month
     */
    @Query("SELECT * FROM expenses WHERE month = :month AND year = :year ORDER BY year DESC, month DESC, day DESC")
    LiveData<List<Expense>> getFromMonth(int month, int year);

    @Query("SELECT * FROM expenses WHERE year = :year ORDER BY year DESC, month DESC, day DESC")
    LiveData<List<Expense>> getFromYear(int year);

    @Query("SELECT * FROM expenses WHERE year = :year AND month = :month AND day = :day")
    LiveData<List<Expense>> getForDay(int day, int month, int year);

    @Query("SELECT * FROM expenses WHERE id = :id")
    List<Expense> getFromId(long id);

    @Insert
    void insert(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("DELETE FROM expenses WHERE 1")
    void clear();

}
