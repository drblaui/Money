package me.drblau.money.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="expenses")
public class Expense {

    @PrimaryKey(autoGenerate=true)
    public long id;
    public String reason;
    public String description;
    public double amount;
    public int day;
    public int month;
    public int year;

    public Expense(String reason, String description, double amount, int day, int month, int year) {
        this.id = 0;
        this.reason = reason;
        this.description = description;
        this.amount = amount;
        this.day = day;
        this.month = month;
        this.year = year;
    }
}
