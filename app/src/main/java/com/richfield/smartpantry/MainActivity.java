package com.richfield.smartpantry;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.richfield.smartpantry.fragment.PantryFragment;
import com.richfield.smartpantry.fragment.RecipesFragment;
import com.richfield.smartpantry.fragment.SettingsFragment;
import com.richfield.smartpantry.fragment.SuggestedFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected;
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                selected = new PantryFragment();
                setTitle("My Pantry");
            } else if (id == R.id.nav_suggested) {
                selected = new SuggestedFragment();
                setTitle("Suggested Recipes");
            } else if (id == R.id.nav_recipes) {
                selected = new RecipesFragment();
                setTitle("All Recipes");
            } else if (id == R.id.nav_settings) {
                selected = new SettingsFragment();
                setTitle("Settings");
            } else {
                return false;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selected)
                    .commit();
            return true;
        });

        // Default tab or restore title
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_pantry);
        } else {
            updateTitle(bottomNav.getSelectedItemId());
        }
    }

    private void updateTitle(int itemId) {
        if (itemId == R.id.nav_pantry) {
            setTitle("My Pantry");
        } else if (itemId == R.id.nav_suggested) {
            setTitle("Suggested Recipes");
        } else if (itemId == R.id.nav_recipes) {
            setTitle("All Recipes");
        } else if (itemId == R.id.nav_settings) {
            setTitle("Settings");
        }
    }
}
