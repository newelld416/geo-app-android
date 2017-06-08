package com.example.aiute40.geoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.parceler.Parcels;

public class HistoryActivity extends AppCompatActivity implements HistoryFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onListFragmentInteraction(LocationLookup locationLookup) {
        Intent intent = new Intent();
        Parcelable parcelable = Parcels.wrap(locationLookup);
        intent.putExtra("item", parcelable);
        setResult(MainActivity.HISTORY_RESULT,intent);
        finish();
    }
}
