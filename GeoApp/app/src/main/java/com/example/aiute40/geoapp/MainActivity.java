package com.example.aiute40.geoapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.location.Location;

import com.example.aiute40.geoapp.history.HistoryContent;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    public static final int SETTINGS_SELECTION = 1;
    public static final int HISTORY_RESULT = 2;
    public static final int LOCATION_SEARCH_RESULT = 3;

    private final static String BEARING_BASE_STR = "Bearing: ";
    private final static String DISTANCE_BASE_STR = "Distance: ";

    private String bearingUnits = "Degrees";
    private String distanceUnits = "Kilometers";

    private int bearingUnitsIndex = 0;
    private int distanceUnitsIndex = 0;

    public static final String DISTANCE_UNITS_INTENT = "distanceUnits";
    public static final String DISTANCE_UNITS_INTENT_INDEX = "distanceUnitsIndex";
    public static final String BEARING_UNITS_INTENT = "bearingUnits";
    public static final String BEARING_UNITS_INTENT_INDEX = "bearingUnitsIndex";

    @BindView(R.id.lon1)
    public TextView longitude1;

    @BindView(R.id.lon2)
    public TextView longitude2;

    @BindView(R.id.lat1)
    public TextView latitude1;

    @BindView(R.id.lat2)
    public TextView latitude2;

    @BindView(R.id.bearingText)
    public TextView bearingText;

    @BindView(R.id.distanceText)
    public TextView distanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //This is our calculate button definition and action
        Button calculateButton = (Button) findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener((v) -> {
            hideSoftKeyBoard();

            if(calculate()) {
                HistoryContent.HistoryItem item = new HistoryContent.HistoryItem(
                        latitude1.getText().toString(),
                        longitude1.getText().toString(),
                        latitude2.getText().toString(),
                        longitude2.getText().toString(),
                        DateTime.now()
                );
                HistoryContent.addItem(item);
            }
        });

        Button clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener((v) -> {
            hideSoftKeyBoard();

            longitude1.setText("");
            longitude2.setText("");
            latitude1.setText("");
            latitude2.setText("");

            bearingText.setText(BEARING_BASE_STR);
            distanceText.setText(DISTANCE_BASE_STR);
        });
    }

    @OnClick(R.id.searchButton)
    public void onSearchButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, LocationSearchActivity.class);
        startActivityForResult(intent, LOCATION_SEARCH_RESULT);
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private boolean calculate() {
        if(longitude1.getText() != null && longitude1.getText().length() > 0 &&
                latitude1.getText() != null && latitude1.getText().length() > 0 &&
                longitude2.getText() != null && longitude2.getText().length() > 0 &&
                latitude2.getText() != null && latitude2.getText().length() > 0
                ) {
            Double lat1Value = Double.parseDouble(latitude1.getText().toString());
            Double lon1Value = Double.parseDouble(longitude1.getText().toString());
            Double lat2Value = Double.parseDouble(latitude2.getText().toString());
            Double lon2Value = Double.parseDouble(longitude2.getText().toString());


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

            if(distanceUnits.equals("Kilometers")) {
                finalDistance = distanceInMeters / 1000;
            } else {
                finalDistance = distanceInMeters / 1609.34;
            }

            if(bearingUnits.equals("Mils")) {
                finalBearing *= 17.777777777778;
            }

            // Round to 2 decimal places
            finalDistance = Math.round(finalDistance * 100.0) / 100.0;
            finalBearing = Math.round(finalBearing * 100.0) / 100.0;

            TextView bearingText = (TextView) findViewById(R.id.bearingText);
            TextView distanceText = (TextView) findViewById(R.id.distanceText);

            bearingText.setText(BEARING_BASE_STR + finalBearing + " " + bearingUnits);
            distanceText.setText(DISTANCE_BASE_STR + finalDistance + " " + distanceUnits);

            // Did calculate a valid set of points
            return true;
        } else {
            // Did not calculate a valid set of points
            return false;
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
        } else if(item.getItemId() == R.id.action_history) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivityForResult(intent, HISTORY_RESULT );
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
        } else if (resultCode == HISTORY_RESULT) {
            String[] vals = data.getStringArrayExtra("item");
            this.latitude1.setText(vals[0]);
            this.longitude1.setText(vals[1]);
            this.latitude2.setText(vals[2]);
            this.longitude2.setText(vals[3]);
            calculate();
        }
    }
}

