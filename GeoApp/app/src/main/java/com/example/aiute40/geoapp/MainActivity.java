package com.example.aiute40.geoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.location.Location;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //This is our calculate button definition and action
        Button calculateButton = (Button) findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener((v) -> {
            hideSoftKeyBoard();
            TextView lon1 = (TextView) findViewById(R.id.lon1);
            Double lon1Value = Double.parseDouble(lon1.getText().toString());
            TextView lon2 = (TextView) findViewById(R.id.lon2);
            Double lon2Value = Double.parseDouble(lon2.getText().toString());
            TextView lat1 = (TextView) findViewById(R.id.lat1);
            Double lat1Value = Double.parseDouble(lat1.getText().toString());
            TextView lat2 = (TextView) findViewById(R.id.lat2);
            Double lat2Value = Double.parseDouble(lat2.getText().toString());

            calculate(lon1Value, lon2Value, lat1Value, lat2Value);
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
        });
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private void calculate(Double lon1, Double lon2, Double lat1, Double lat2) {
        Location fromLocation = new Location("fromLocation");
        fromLocation.setLatitude(lat1);
        fromLocation.setLongitude(lon1);
        Location toLocation = new Location("toLocation");
        toLocation.setLatitude(lat2);
        toLocation.setLongitude(lon2);

        Double distance = Double.valueOf(fromLocation.distanceTo(toLocation) / 1000);
        distance = Math.round(distance * 100.0) / 100.0;
        Double bearing = Double.valueOf(fromLocation.bearingTo(toLocation));
        bearing = Math.round(bearing * 100.0) / 100.0;

        TextView bearingText = (TextView) findViewById(R.id.bearingText);
        TextView distanceText = (TextView) findViewById(R.id.distanceText);

        bearingText.setText("Bearing: " + bearing + " Degrees");
        distanceText.setText("Distance: " + distance + " Kilometers");
    }
}

