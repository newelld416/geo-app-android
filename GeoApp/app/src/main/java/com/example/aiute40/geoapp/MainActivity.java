package com.example.aiute40.geoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import webservice.WeatherService;

import static com.example.aiute40.geoapp.R.id.lat1;
import static com.example.aiute40.geoapp.R.id.lat2;
import static com.example.aiute40.geoapp.R.id.lon1;
import static com.example.aiute40.geoapp.R.id.lon2;
import static webservice.WeatherService.BROADCAST_WEATHER;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static final int SETTINGS_SELECTION = 1;
    public static final int HISTORY_RESULT = 2;
    public static final int LOCATION_SEARCH_RESULT = 3;
    public static final String INTENT_LOCATION_RESULT = "INTENT_LOCATION_RESULT";

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

    @BindView(lon1)
    public TextView longitude1;

    @BindView(lon2)
    public TextView longitude2;

    @BindView(lat1)
    public TextView latitude1;

    @BindView(lat2)
    public TextView latitude2;

    @BindView(R.id.bearingText)
    public TextView bearingText;

    @BindView(R.id.distanceText)
    public TextView distanceText;

    @BindView(R.id.p1Icon)
    public ImageView p1Icon;

    @BindView(R.id.p2Icon)
    public ImageView p2Icon;

    @BindView(R.id.p1Summary)
    public TextView p1Summary;

    @BindView(R.id.p2Summary)
    public TextView p2Summary;

    @BindView(R.id.p1Temp)
    public TextView p1Temp;

    @BindView(R.id.p2Temp)
    public TextView p2Temp;

    DatabaseReference topRef;
    public static List<LocationLookup> allHistory;

    private BroadcastReceiver weatherReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //Log.d(TAG, "onReceive: " + intent);

            Bundle bundle = intent.getExtras();
            double temp = bundle.getDouble("TEMPERATURE");
            String summary = bundle.getString("SUMMARY");
            String icon = bundle.getString("ICON").replaceAll("-", "_");
            String key = bundle.getString("KEY");
            int resID = getResources().getIdentifier(icon , "drawable", getPackageName());

            setWeatherViews(View.VISIBLE);

            if (key.equals("p1")) {
                p1Summary.setText(summary);
                p1Temp.setText(Double.toString(temp));
                p1Icon.setImageResource(resID);
                p1Icon.setVisibility(View.INVISIBLE);
            } else {
                p2Summary.setText(summary);
                p2Temp.setText(Double.toString(temp));
                p2Icon.setImageResource(resID);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        allHistory = new ArrayList<>();
    }

    @OnClick(R.id.calculateButton)
    public void onClickCalculateButton(View view) {
        hideSoftKeyBoard();
        if(calculate()) {
            saveItem();
        }
    }

    @OnClick(R.id.clearButton)
    public void onClickClearButton(View view) {
        setWeatherViews(View.INVISIBLE);

        hideSoftKeyBoard();

        longitude1.setText("");
        longitude2.setText("");
        latitude1.setText("");
        latitude2.setText("");

        bearingText.setText(BEARING_BASE_STR);
        distanceText.setText(DISTANCE_BASE_STR);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("oops");
    }

    @OnClick(R.id.searchButton)
    public void onSearchButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, LocationSearchActivity.class);
        startActivityForResult(intent, LOCATION_SEARCH_RESULT);
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private boolean calculate() {
        if (longitude1.getText() != null && longitude1.getText().length() > 0 &&
                latitude1.getText() != null && latitude1.getText().length() > 0 &&
                longitude2.getText() != null && longitude2.getText().length() > 0 &&
                latitude2.getText() != null && latitude2.getText().length() > 0
                ) {
            Double lat1Value = Double.parseDouble(latitude1.getText().toString());
            Double lon1Value = Double.parseDouble(longitude1.getText().toString());
            Double lat2Value = Double.parseDouble(latitude2.getText().toString());
            Double lon2Value = Double.parseDouble(longitude2.getText().toString());

            WeatherService.startGetWeather(this, Double.toString(lat1Value), Double.toString(lon1Value), "p1");
            WeatherService.startGetWeather(this, Double.toString(lat2Value), Double.toString(lon2Value), "p2");

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

            if (distanceUnits.equals("Kilometers")) {
                finalDistance = distanceInMeters / 1000;
            } else {
                finalDistance = distanceInMeters / 1609.34;
            }

            if (bearingUnits.equals("Mils")) {
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

    private void saveItem() {
        LocationLookup entry = new LocationLookup();
        entry.setOrigLat(Double.valueOf(latitude1.getText().toString()));
        entry.setOrigLng(Double.valueOf(longitude1.getText().toString()));
        entry.setDestLat(Double.valueOf(latitude2.getText().toString()));
        entry.setDestLng(Double.valueOf(longitude2.getText().toString()));
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        entry.setTimestamp(fmt.print(DateTime.now()));
        topRef.push().setValue(entry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settingsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settingsMenuItem) {
            Intent intent = new Intent(MainActivity.this, MySettingsActivity.class);
            intent.putExtra(DISTANCE_UNITS_INTENT_INDEX, this.distanceUnitsIndex);
            intent.putExtra(BEARING_UNITS_INTENT_INDEX, this.bearingUnitsIndex);
            startActivityForResult(intent, SETTINGS_SELECTION);
            return true;
        } else if (item.getItemId() == R.id.action_history) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivityForResult(intent, HISTORY_RESULT);
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
            Parcelable parcel = data.getParcelableExtra("item");
            LocationLookup locationLookup = Parcels.unwrap(parcel);
            this.latitude1.setText(String.valueOf(locationLookup.getOrigLat()));
            this.longitude1.setText(String.valueOf(locationLookup.getOrigLng()));
            this.latitude2.setText(String.valueOf(locationLookup.getDestLat()));
            this.longitude2.setText(String.valueOf(locationLookup.getDestLng()));
            calculate();
        } else if (resultCode == LOCATION_SEARCH_RESULT) {
            if (data != null && data.hasExtra(INTENT_LOCATION_RESULT)) {
                Parcelable parcel = data.getParcelableExtra(INTENT_LOCATION_RESULT);
                LocationLookup locationLookup = Parcels.unwrap(parcel);
                longitude1.setText(String.valueOf(locationLookup.getOrigLng()));
                latitude1.setText(String.valueOf(locationLookup.getOrigLat()));
                longitude2.setText(String.valueOf(locationLookup.getDestLng()));
                latitude2.setText(String.valueOf(locationLookup.getDestLat()));
                if(calculate()) {
                    saveItem();
                }
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        // Firebase
        allHistory.clear();
        topRef = FirebaseDatabase.getInstance().getReference("history");
        topRef.addChildEventListener(chEvListener);

        IntentFilter weatherFilter = new IntentFilter(BROADCAST_WEATHER);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(weatherReceiver, weatherFilter);

        // View
        setWeatherViews(View.INVISIBLE);
    }

    private void setWeatherViews(int visible) {
        p1Icon.setVisibility(visible);
        p2Icon.setVisibility(visible);
        p1Summary.setVisibility(visible);
        p2Summary.setVisibility(visible);
        p1Temp.setVisibility(visible);
        p2Temp.setVisibility(visible);
    }

    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherReceiver);
        topRef.removeEventListener(chEvListener);
    }

    private ChildEventListener chEvListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            LocationLookup entry = dataSnapshot.getValue(LocationLookup.class);
            entry._key = dataSnapshot.getKey();
            allHistory.add(entry);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            LocationLookup entry = dataSnapshot.getValue(LocationLookup.class);
            List<LocationLookup> newHistory = new ArrayList<>();
            for (LocationLookup t : allHistory) {
                if (!t._key.equals(dataSnapshot.getKey())) {
                    newHistory.add(t);
                }
            }
            allHistory = newHistory;
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };
}

