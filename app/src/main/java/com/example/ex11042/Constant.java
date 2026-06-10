package com.example.ex11042;

/**
 * Constants class for the application database.
 * Keeps all table and column names to prevent typing mistakes.
 */
public class Constant {

    /** Database file name */
    public static final String DB_NAME = "expenses.db";

    /** Expenses table name */
    public static final String TABLE_NAME = "expenses";

    /** Primary key column */
    public static final String KEY_ID = "_id";

    /** Expense description column */
    public static final String KEY_DESC = "description";

    /** Expense amount column */
    public static final String KEY_AMOUNT = "amount";

    /** Expense category column */
    public static final String KEY_CATEGORY = "category";

    /** Expense date column */
    public static final String KEY_DATE = "date";
}