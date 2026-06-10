package com.example.ex11042;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

/**
 * Activity for adding a new expense to the database.
 */
public class AddActivity extends AppCompatActivity {

    EditText etDesc, etAmount;
    Spinner spCategory;
    TextView tvDate;
    Button btnSave;

    HelperDB helper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Connect variables to XML views
        etDesc = findViewById(R.id.etDesc);
        etAmount = findViewById(R.id.etAmount);
        spCategory = findViewById(R.id.spCategory);
        tvDate = findViewById(R.id.tvDate);
        btnSave = findViewById(R.id.btnSave);

        // Open DatePickerDialog when clicking on the date TextView
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        // Format month and day to always have 2 digits (e.g., 05 instead of 5)
                        String sMonth = String.valueOf(month + 1);
                        if (month + 1 < 10) {
                            sMonth = "0" + sMonth;
                        }

                        String sDay = String.valueOf(day);
                        if (day < 10) {
                            sDay = "0" + sDay;
                        }

                        // Set the text in YYYY-MM-DD format as required
                        tvDate.setText(year + "-" + sMonth + "-" + sDay);
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        // Save button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = etDesc.getText().toString();
                String amountStr = etAmount.getText().toString();
                String category = spCategory.getSelectedItem().toString();
                String date = tvDate.getText().toString();

                // Check that the user filled all fields
                if (desc.isEmpty() || amountStr.isEmpty() || date.equals("Click here to choose date")) {
                    Toast.makeText(AddActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountStr);

                // Open database for writing
                helper = new HelperDB(AddActivity.this);
                db = helper.getWritableDatabase();

                // Create ContentValues to hold the row data
                ContentValues cv = new ContentValues();
                cv.put(Constant.KEY_DESC, desc);
                cv.put(Constant.KEY_AMOUNT, amount);
                cv.put(Constant.KEY_CATEGORY, category);
                cv.put(Constant.KEY_DATE, date);

                // Insert the row into the table
                db.insert(Constant.TABLE_NAME, null, cv);

                // Close database connection
                db.close();

                Toast.makeText(AddActivity.this, "Expense Saved!", Toast.LENGTH_SHORT).show();

                // Close this screen and go back
                finish();
            }
        });
    }
}