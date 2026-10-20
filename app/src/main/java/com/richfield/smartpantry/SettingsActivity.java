package com.richfield.smartpantry;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_UNITS = "units_preference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        SwitchMaterial switchAlerts = findViewById(R.id.switch_expiry_alerts);
        Spinner spinnerUnits = findViewById(R.id.spinner_units);

        switchAlerts.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERTS, true));
        switchAlerts.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply());

        String[] units = {"Metric (g, ml, pcs)", "Imperial (oz, cups, pcs)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, units);
        spinnerUnits.setAdapter(adapter);

        int selected = prefs.getInt(KEY_UNITS, 0);
        spinnerUnits.setSelection(selected);
        spinnerUnits.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                prefs.edit().putInt(KEY_UNITS, position).apply();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }
}
