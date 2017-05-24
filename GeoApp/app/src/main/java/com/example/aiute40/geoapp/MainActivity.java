package com.example.aiute40.geoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.location.Location;


public class MainActivity extends AppCompatActivity {
    public static final int SETTINGS_SELECTION = 1;
    private final static String BEARING_BASE_STR = "Bearing: ";
    private final static String DISTANCE_BASE_STR = "Distance: ";
    private String bearingUnits = "degrees";
    private String distanceUnits = "kilometers";
    private int bearingUnitsIndex = 0;
    private int distanceUnitsIndex = 0;

    public static final String DISTANCE_UNITS_INTENT = "distanceUnits";
    public static final String DISTANCE_UNITS_INTENT_INDEX = "distanceUnitsIndex";
    public static final String BEARING_UNITS_INTENT = "bearingUnits";
    public static final String BEARING_UNITS_INTENT_INDEX = "bearingUnitsIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //This is our calculate button definition and action
        Button calculateButton = (Button) findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener((v) -> {
            hideSoftKeyBoard();

            calculate();
        });

        Button clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener((v) -> {
            hideSoftKeyBoard();
            TextView lon1 = (TextView) findViewById(R.id.lon1);
            lon1.setText("");
            TextView lon2 = (TextView) findViewById(R.id.lon2);
            lon2.setText("");
            TextView lat1 = (TextView) findViewById(R.id.lat1);
            lat1.setText("");
            TextView lat2 = (TextView) findViewById(R.id.lat2);
            lat2.setText("");

            TextView bearingText = (TextView) findViewById(R.id.bearingText);
            TextView distanceText = (TextView) findViewById(R.id.distanceText);

            bearingText.setText(BEARING_BASE_STR);
            distanceText.setText(DISTANCE_BASE_STR);
        });
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private void calculate() {
        TextView lon1 = (TextView) findViewById(R.id.lon1);
        TextView lon2 = (TextView) findViewById(R.id.lon2);
        TextView lat1 = (TextView) findViewById(R.id.lat1);
        TextView lat2 = (TextView) findViewById(R.id.lat2);

        if(lon1.getText() != null && lon1.getText().length() > 0 &&
                lat1.getText() != null && lat1.getText().length() > 0 &&
                lon2.getText() != null && lon2.getText().length() > 0 &&
                lat2.getText() != null && lat2.getText().length() > 0
                ) {
            Double lat1Value = Double.parseDouble(lat1.getText().toString());
            Double lon1Value = Double.parseDouble(lon1.getText().toString());
            Double lat2Value = Double.parseDouble(lat2.getText().toString());
            Double lon2Value = Double.parseDouble(lon2.getText().toString());


            Location fromLocation = new Location("fromLocation");
            fromLocation.setLatitude(lat1Value);
            fromLocation.setLongitude(lon1Value);
            Location toLocation = new Location("toLocation");
            toLocation.setLatitude(lat2Value);
            toLocation.setLongitude(lon2Value);

            double distanceInMeters = fromLocation.distanceTo(toLocation);
            double bearingInDegrees = fromLocation.bearingTo(toLocation);
            double finalDistance = 0;
            double finalBearing = bearingInDegrees;

            if(distanceUnits.equals("kilometers")) {
                finalDistance = distanceInMeters / 1000;
            } else {
                finalDistance = distanceInMeters / 1609.34;
            }

            if(bearingUnits.equals("mils")) {
                finalBearing *= 17.777777777778;
            }

            // Round to 2 decimal places
            finalDistance = Math.round(finalDistance * 100.0) / 100.0;
            finalBearing = Math.round(finalBearing * 100.0) / 100.0;

            TextView bearingText = (TextView) findViewById(R.id.bearingText);
            TextView distanceText = (TextView) findViewById(R.id.distanceText);

            bearingText.setText(BEARING_BASE_STR + finalBearing + " " + bearingUnits);
            distanceText.setText(DISTANCE_BASE_STR + finalDistance + " " + distanceUnits);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settingsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settingsMenuItem) {
            Intent intent = new Intent(MainActivity.this, MySettingsActivity.class);
            intent.putExtra(DISTANCE_UNITS_INTENT_INDEX, this.distanceUnitsIndex);
            intent.putExtra(BEARING_UNITS_INTENT_INDEX, this.bearingUnitsIndex);
            startActivityForResult(intent, SETTINGS_SELECTION);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == SETTINGS_SELECTION) {
            distanceUnits = data.getStringExtra(DISTANCE_UNITS_INTENT);
            bearingUnits = data.getStringExtra(BEARING_UNITS_INTENT);
            distanceUnitsIndex = data.getIntExtra(DISTANCE_UNITS_INTENT_INDEX, distanceUnitsIndex);
            bearingUnitsIndex = data.getIntExtra(BEARING_UNITS_INTENT_INDEX, bearingUnitsIndex);
            calculate();
        }
    }
}

