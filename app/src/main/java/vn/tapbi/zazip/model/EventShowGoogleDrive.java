package vn.tapbi.zazip.model;

import android.content.Intent;

public class EventShowGoogleDrive {
    private Intent intent;

    public EventShowGoogleDrive(Intent intent) {
        this.intent = intent;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
