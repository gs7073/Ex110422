package com.example.ex11042;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

/**
 * Main Activity to display all expenses.
 * Includes long-click to delete or update.
 */
public class MainActivity extends AppCompatActivity {

    ListView lvExpenses;
    Button btnAdd;
    TextView tvTotal;

    HelperDB helper;
    SQLiteDatabase db;
    Cursor crsr;

    ArrayList<String> arr;
    ArrayList<Integer> arrIds; // Keeps the IDs for delete and update
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect variables to XML views
        lvExpenses = findViewById(R.id.lvExpenses);
        btnAdd = findViewById(R.id.btnAdd);
        tvTotal = findViewById(R.id.tvTotal);

        // Button to open AddActivity
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        // Long click listener for Update or Delete
        lvExpenses.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the ID of the clicked expense from our list
                final int expenseId = arrIds.get(position);

                // Create an alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose Action");
                builder.setMessage("Do you want to update or delete this expense?");

                // Delete Button - Exact same logic as teacher's slides
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helper = new HelperDB(MainActivity.this);
                        db = helper.getWritableDatabase();

                        // The delete query
                        String[] args = {String.valueOf(expenseId)};
                        db.delete(Constant.TABLE_NAME, Constant.KEY_ID + "=?", args);

                        db.close();
                        Toast.makeText(MainActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();

                        // Refresh the list after deleting
                        onResume();
                    }
                });

                // Update Button
                builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "We will do update next!", Toast.LENGTH_SHORT).show();
                        // Next step: Open EditActivity
                    }
                });

                builder.show();

                return true; // Tells Android we handled the long click
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        arr = new ArrayList<>();
        arrIds = new ArrayList<>(); // Initialize the IDs list
        double totalSum = 0;

        helper = new HelperDB(this);
        db = helper.getReadableDatabase();

        // Query to get all expenses
        crsr = db.query(Constant.TABLE_NAME, null, null, null, null, null, Constant.KEY_ID + " DESC");

        int colId = crsr.getColumnIndex(Constant.KEY_ID);
        int colDesc = crsr.getColumnIndex(Constant.KEY_DESC);
        int colAmount = crsr.getColumnIndex(Constant.KEY_AMOUNT);
        int colCategory = crsr.getColumnIndex(Constant.KEY_CATEGORY);
        int colDate = crsr.getColumnIndex(Constant.KEY_DATE);

        crsr.moveToFirst();

        while (!crsr.isAfterLast()) {
            int id = crsr.getInt(colId);
            String desc = crsr.getString(colDesc);
            double amount = crsr.getDouble(colAmount);
            String category = crsr.getString(colCategory);
            String date = crsr.getString(colDate);

            // Add the ID to our hidden list
            arrIds.add(id);

            totalSum += amount;

            String row = "ID: " + id + " | " + date + "\n" + category + " - " + desc + "\nAmount: " + amount;
            arr.add(row);

            crsr.moveToNext();
        }

        crsr.close();
        db.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
        lvExpenses.setAdapter(adapter);

        tvTotal.setText("Total: " + totalSum + " ₪");
    }
}