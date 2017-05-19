package com.example.aiute40.geoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MySettingsActivity extends AppCompatActivity {

    private String distanceUnitsSelection = "kilometers";
    private String bearingUnitsSelection = "degrees";
    private int distanceUnitsSelectionIndex = 0;
    private int bearingUnitsSelectionIndex = 0;

    private Spinner distanceUnitsSpinner;
    private Spinner bearingUnitsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener((v) -> {
            Intent intent = new Intent();
            intent.putExtra(MainActivity.DISTANCE_UNITS_INTENT, distanceUnitsSelection);
            intent.putExtra(MainActivity.BEARING_UNITS_INTENT, bearingUnitsSelection);
            intent.putExtra(MainActivity.DISTANCE_UNITS_INTENT_INDEX, distanceUnitsSelectionIndex);
            intent.putExtra(MainActivity.BEARING_UNITS_INTENT_INDEX, bearingUnitsSelectionIndex);
            setResult(MainActivity.SETTINGS_SELECTION,intent);
            finish();
        });

        bearingUnitsSpinner = (Spinner) findViewById(R.id.bearingUnitsSpinner);
        distanceUnitsSpinner = (Spinner) findViewById(R.id.distanceUnitsSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> distanceAdapter = ArrayAdapter.createFromResource(this,
                R.array.distance_units, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> bearingAdapter = ArrayAdapter.createFromResource(this,
                R.array.bearing_units, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bearingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        distanceUnitsSpinner.setAdapter(distanceAdapter);
        bearingUnitsSpinner.setAdapter(bearingAdapter);

        distanceUnitsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                distanceUnitsSelection = (String) adapterView.getItemAtPosition(i);
                distanceUnitsSelectionIndex = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        bearingUnitsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bearingUnitsSelection = (String) adapterView.getItemAtPosition(i);
                bearingUnitsSelectionIndex = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Intent intent = getIntent();
        int distanceIndex = intent.getIntExtra(MainActivity.DISTANCE_UNITS_INTENT_INDEX, -1);
        int bearingIndex = intent.getIntExtra(MainActivity.BEARING_UNITS_INTENT_INDEX, -1);
        bearingUnitsSpinner.setSelection(bearingIndex);
        distanceUnitsSpinner.setSelection(distanceIndex);
    }

}
