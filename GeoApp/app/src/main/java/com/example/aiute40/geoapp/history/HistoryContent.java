package com.example.aiute40.geoapp.history;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryContent {
    public static final List<HistoryItem> ITEMS = new ArrayList<>();

    public static void addItem(HistoryItem item) {
        ITEMS.add(item);
    }

    public static class HistoryItem {
        public final String origLat;
        public final String origLng;
        public final String destLat;
        public final String destLng;
        public final DateTime timestamp;

        public HistoryItem(
                String origLat,
                String origLng,
                String destLat,
                String destLng,
                DateTime timestamp
        ) {
            this.origLat = origLat;
            this.origLng = origLng;
            this.destLat = destLat;
            this.destLng = destLng;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "(" + this.origLat + "," + this.origLat + ")";
        }
    }
}