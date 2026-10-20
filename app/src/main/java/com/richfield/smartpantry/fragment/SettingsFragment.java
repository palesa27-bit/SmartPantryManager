package com.richfield.smartpantry.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.richfield.smartpantry.R;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_UNITS = "units_preference";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        SwitchMaterial switchAlerts = view.findViewById(R.id.switch_expiry_alerts);
        Spinner spinnerUnits = view.findViewById(R.id.spinner_units);

        switchAlerts.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERTS, true));
        switchAlerts.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply());

        String[] units = {"Metric (g, ml, pcs)", "Imperial (oz, cups, pcs)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, units);
        spinnerUnits.setAdapter(adapter);

        int selected = prefs.getInt(KEY_UNITS, 0);
        spinnerUnits.setSelection(selected);
        spinnerUnits.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt(KEY_UNITS, position).apply();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }
}
