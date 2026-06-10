package com.example.ex11042;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
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
 * Activity for editing an existing expense in the database.
 */
public class EditActivity extends AppCompatActivity {

    EditText etDesc, etAmount;
    Spinner spCategory;
    TextView tvDate;
    Button btnUpdate;

    HelperDB helper;
    SQLiteDatabase db;
    int expenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Connect variables to XML views
        etDesc = findViewById(R.id.etDesc);
        etAmount = findViewById(R.id.etAmount);
        spCategory = findViewById(R.id.spCategory);
        tvDate = findViewById(R.id.tvDate);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Get the ID passed from MainActivity
        expenseId = getIntent().getIntExtra("id", -1);

        helper = new HelperDB(this);
        db = helper.getWritableDatabase();

        // Load current data for this expense to show in the fields
        String[] args = {String.valueOf(expenseId)};
        Cursor crsr = db.query(Constant.TABLE_NAME, null, Constant.KEY_ID + "=?", args, null, null, null);

        if (crsr.moveToFirst()) {
            int colDesc = crsr.getColumnIndex(Constant.KEY_DESC);
            int colAmount = crsr.getColumnIndex(Constant.KEY_AMOUNT);
            int colDate = crsr.getColumnIndex(Constant.KEY_DATE);

            etDesc.setText(crsr.getString(colDesc));
            etAmount.setText(String.valueOf(crsr.getDouble(colAmount)));
            tvDate.setText(crsr.getString(colDate));
        }
        crsr.close();

        // Setup DatePickerDialog for choosing a new date
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String sMonth = String.valueOf(month + 1);
                        if (month + 1 < 10) {
                            sMonth = "0" + sMonth;
                        }

                        String sDay = String.valueOf(day);
                        if (day < 10) {
                            sDay = "0" + sDay;
                        }

                        tvDate.setText(year + "-" + sMonth + "-" + sDay);
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        // Update button click listener
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = etDesc.getText().toString();
                String amountStr = etAmount.getText().toString();
                String category = spCategory.getSelectedItem().toString();
                String date = tvDate.getText().toString();

                if (desc.isEmpty() || amountStr.isEmpty()) {
                    Toast.makeText(EditActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountStr);

                // Create ContentValues to hold the updated data
                ContentValues cv = new ContentValues();
                cv.put(Constant.KEY_DESC, desc);
                cv.put(Constant.KEY_AMOUNT, amount);
                cv.put(Constant.KEY_CATEGORY, category);
                cv.put(Constant.KEY_DATE, date);

                // Update the database row
                String[] updateArgs = {String.valueOf(expenseId)};
                db.update(Constant.TABLE_NAME, cv, Constant.KEY_ID + "=?", updateArgs);

                db.close();

                Toast.makeText(EditActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}