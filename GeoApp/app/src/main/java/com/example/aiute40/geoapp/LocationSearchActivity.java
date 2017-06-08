package com.example.aiute40.geoapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;
import org.parceler.Parcels;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.aiute40.geoapp.MainActivity.INTENT_LOCATION_RESULT;
import static com.example.aiute40.geoapp.MainActivity.LOCATION_SEARCH_RESULT;

public class LocationSearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "LocationSearchActivity";

    private static final int PLACE_ORIGIN_LOCATION = 1;
    private static final int PLACE_DESTINATION_LOCATION = 2;

    @BindView(R.id.originalLocationTextView)
    public TextView originalLocationTextView;

    @BindView(R.id.destinationLocationTextView)
    public TextView destinationLocationTextView;

    @BindView(R.id.calculationDatePicker)
    public TextView calculationDatePicker;

    private DatePickerDialog datePickerDialog;
    private LatLng origLatLng, endLatLng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        DateTime today = DateTime.now();
        calculationDatePicker.setText(formatted(today));

        datePickerDialog = new DatePickerDialog(
                this, LocationSearchActivity.this,
                today.getYear(),
                today.getMonthOfYear(),
                today.getDayOfMonth()
        );
    }

    @OnClick(R.id.originalLocationTextView)
    public void chooseOriginLocation() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_ORIGIN_LOCATION);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.destinationLocationTextView)
    public void chooseDestinationLocation() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_DESTINATION_LOCATION);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.calculationDatePicker)
    public void onClickCalculationDatePicker(View view) {
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calculationDatePicker.setText("TODO: Set date string");
    }

    @OnClick(R.id.fab)
    public void fabOnClickListener(View view) {
        Intent intent = new Intent();
        LocationLookup locationLookup = new LocationLookup();

        if(origLatLng != null) {
            locationLookup.origLat = origLatLng.latitude;
            locationLookup.origLng = origLatLng.longitude;
        }
        if(endLatLng != null) {
            locationLookup.endLat = endLatLng.latitude;
            locationLookup.endLng = endLatLng.longitude;
        }

        Parcelable parcelable = Parcels.wrap(locationLookup);
        intent.putExtra(INTENT_LOCATION_RESULT, parcelable);
        setResult(LOCATION_SEARCH_RESULT, intent);
        finish();
    }

    private String formatted(DateTime d) {
        return d.monthOfYear().getAsShortText(Locale.getDefault()) + " " +
                d.getDayOfMonth() + ", " + d.getYear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_ORIGIN_LOCATION) {
            if (resultCode == RESULT_OK) {
                Place pl = PlaceAutocomplete.getPlace(this, data);
                originalLocationTextView.setText(pl.getAddress());
                origLatLng = pl.getLatLng();
                Log.i(TAG, "onActivityResult: " + pl.getName() + "/" + pl.getAddress());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status stat = PlaceAutocomplete.getStatus(this, data);
                Log.d(TAG, "onActivityResult: " + stat.getStatus());
            } else if (resultCode == RESULT_CANCELED){
                System.out.println("Cancelled by the user");
            }
        } else if(requestCode == PLACE_DESTINATION_LOCATION) {
            if (resultCode == RESULT_OK) {
                Place pl = PlaceAutocomplete.getPlace(this, data);
                destinationLocationTextView.setText(pl.getAddress());
                endLatLng = pl.getLatLng();
                Log.i(TAG, "onActivityResult: " + pl.getName() + "/" + pl.getAddress());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status stat = PlaceAutocomplete.getStatus(this, data);
                Log.d(TAG, "onActivityResult: " + stat.getStatus());
            } else if (resultCode == RESULT_CANCELED){
                System.out.println("Cancelled by the user");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
