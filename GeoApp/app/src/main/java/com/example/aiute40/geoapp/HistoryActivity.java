package com.example.aiute40.geoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.aiute40.geoapp.history.HistoryContent;

public class HistoryActivity extends AppCompatActivity implements HistoryFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onListFragmentInteraction(HistoryContent.HistoryItem item) {
        Intent intent = new Intent();
        String[] vals = {item.origLat, item.origLng, item.destLat, item.destLng};
        intent.putExtra("item", vals);
        setResult(MainActivity.HISTORY_RESULT,intent);
        finish();
    }
}
