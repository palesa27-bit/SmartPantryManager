package com.richfield.smartpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.richfield.smartpantry.db.DatabaseHelper;
import com.richfield.smartpantry.model.PantryItem;

import java.util.Calendar;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private TextInputLayout tilName, tilQuantity, tilUnit, tilExpiry;
    private TextInputEditText etName, etQuantity, etExpiry;
    private AutoCompleteTextView etUnit;
    private MaterialButton btnSave, btnDelete;

    private DatabaseHelper db;
    private long itemId = -1;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        db = DatabaseHelper.getInstance(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);

        tilName = findViewById(R.id.til_name);
        tilQuantity = findViewById(R.id.til_quantity);
        tilUnit = findViewById(R.id.til_unit);
        tilExpiry = findViewById(R.id.til_expiry);
        etName = findViewById(R.id.et_name);
        etQuantity = findViewById(R.id.et_quantity);
        etUnit = findViewById(R.id.et_unit);
        etExpiry = findViewById(R.id.et_expiry);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);

        String[] units = {"pcs", "g", "kg", "ml", "l", "cup", "tbsp", "tsp", "pinch", "cloves", "slices", "head"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, units);
        etUnit.setAdapter(unitAdapter);

        etExpiry.setOnClickListener(v -> showDatePicker());

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        isEdit = itemId != -1;

        if (isEdit) {
            toolbar.setTitle(R.string.edit_ingredient);
            btnDelete.setVisibility(View.VISIBLE);
            loadItem();
        } else {
            toolbar.setTitle(R.string.add_ingredient);
        }

        btnSave.setOnClickListener(v -> saveItem());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadItem() {
        PantryItem item = db.getPantryItem(itemId);
        if (item != null) {
            etName.setText(item.getName());
            if (item.getQuantity() == (long) item.getQuantity()) {
                etQuantity.setText(String.valueOf((long) item.getQuantity()));
            } else {
                etQuantity.setText(String.valueOf(item.getQuantity()));
            }
            etUnit.setText(item.getUnit(), false);
            if (item.getExpiryDate() != null) {
                etExpiry.setText(item.getExpiryDate());
            }
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            etExpiry.setText(date);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveItem() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String qtyStr = etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
        String unit = etUnit.getText() != null ? etUnit.getText().toString().trim() : "";
        String expiry = etExpiry.getText() != null ? etExpiry.getText().toString().trim() : null;
        if (expiry != null && expiry.isEmpty()) expiry = null;

        // Validation
        boolean valid = true;
        if (name.isEmpty()) {
            tilName.setError(getString(R.string.validation_name));
            valid = false;
        } else {
            tilName.setError(null);
        }

        double quantity = 0;
        try {
            quantity = Double.parseDouble(qtyStr);
            if (quantity <= 0) {
                tilQuantity.setError(getString(R.string.validation_quantity));
                valid = false;
            } else {
                tilQuantity.setError(null);
            }
        } catch (NumberFormatException e) {
            tilQuantity.setError(getString(R.string.validation_quantity));
            valid = false;
        }

        if (unit.isEmpty()) {
            unit = "pcs";
        }

        if (!valid) return;

        PantryItem item = new PantryItem(name, quantity, unit, expiry);
        if (isEdit) {
            item.setId(itemId);
            db.updatePantryItem(item);
        } else {
            db.insertPantryItem(item);
        }

        Toast.makeText(this, R.string.item_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    db.deletePantryItem(itemId);
                    Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
