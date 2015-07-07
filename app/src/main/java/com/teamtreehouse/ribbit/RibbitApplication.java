package com.teamtreehouse.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Alex on 6/25/2015.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "hiN9EO2DV0yHWLoBevqB3ROD4g8V25wwFi4h0uXe", "R1UN9xkqW9eUQDBbTRdU6j1JUn3hroWwACem7jjG");

    }
}
