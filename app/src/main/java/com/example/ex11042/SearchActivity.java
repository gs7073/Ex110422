package com.example.ex11042;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

/**
 * Activity for searching specific expenses and filtering by amount.
 */
public class SearchActivity extends AppCompatActivity {

    EditText etSearchDesc, etMinAmount;
    Button btnSearchDesc, btnFilterAmount;
    ListView lvSearchResults;

    HelperDB helper;
    SQLiteDatabase db;

    ArrayList<String> arr;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Connect variables to XML views
        etSearchDesc = findViewById(R.id.etSearchDesc);
        etMinAmount = findViewById(R.id.etMinAmount);
        btnSearchDesc = findViewById(R.id.btnSearchDesc);
        btnFilterAmount = findViewById(R.id.btnFilterAmount);
        lvSearchResults = findViewById(R.id.lvSearchResults);

        helper = new HelperDB(this);

        // Button to search by exact description
        btnSearchDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = etSearchDesc.getText().toString();
                if (desc.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Enter description", Toast.LENGTH_SHORT).show();
                    return;
                }

                db = helper.getReadableDatabase();

                // Advanced query with selection and selectionArgs
                String selection = Constant.KEY_DESC + "=?";
                String[] selectionArgs = {desc};

                Cursor crsr = db.query(Constant.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                showResults(crsr);
            }
        });

        // Button to filter by minimum amount
        btnFilterAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = etMinAmount.getText().toString();
                if (amountStr.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Enter amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                db = helper.getReadableDatabase();

                // Advanced query using the greater than (>) operator
                String selection = Constant.KEY_AMOUNT + ">?";
                String[] selectionArgs = {amountStr};

                // Order results by amount descending (highest to lowest)
                Cursor crsr = db.query(Constant.TABLE_NAME, null, selection, selectionArgs, null, null, Constant.KEY_AMOUNT + " DESC");
                showResults(crsr);
            }
        });
    }

    /**
     * Reads the cursor data and updates the ListView.
     * @param crsr The cursor with the query results
     */
    private void showResults(Cursor crsr) {
        arr = new ArrayList<>();

        int colId = crsr.getColumnIndex(Constant.KEY_ID);
        int colDesc = crsr.getColumnIndex(Constant.KEY_DESC);
        int colAmount = crsr.getColumnIndex(Constant.KEY_AMOUNT);
        int colCategory = crsr.getColumnIndex(Constant.KEY_CATEGORY);
        int colDate = crsr.getColumnIndex(Constant.KEY_DATE);

        crsr.moveToFirst();

        // Loop through the cursor exactly like the teacher's example
        while (!crsr.isAfterLast()) {
            int id = crsr.getInt(colId);
            String desc = crsr.getString(colDesc);
            double amount = crsr.getDouble(colAmount);
            String category = crsr.getString(colCategory);
            String date = crsr.getString(colDate);

            // Format the string for the ListView
            String row = "ID: " + id + " | " + date + "\n" + category + " - " + desc + "\nAmount: " + amount;
            arr.add(row);

            crsr.moveToNext();
        }

        crsr.close();
        db.close();

        // Update the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
        lvSearchResults.setAdapter(adapter);

        if (arr.isEmpty()) {
            Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
        }
    }
}