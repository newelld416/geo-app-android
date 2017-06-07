package com.example.aiute40.geoapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationSearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.originalLocationTextView)
    public TextView originalLocationTextView;

    @BindView(R.id.destinationLocationTextView)
    public TextView destinationLocationTextView;

    @BindView(R.id.calculationDatePicker)
    public TextView calculationDatePicker;

    private DatePickerDialog datePickerDialog;

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
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private String formatted(DateTime d) {
        return d.monthOfYear().getAsShortText(Locale.getDefault()) + " " +
                d.getDayOfMonth() + ", " + d.getYear();
    }
}
