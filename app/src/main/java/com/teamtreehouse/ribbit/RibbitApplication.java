package com.teamtreehouse.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;
import com.teamtreehouse.ribbit.ui.MainActivity;
import com.teamtreehouse.ribbit.utils.ParseConstants;

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

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void updateParseInstallation(ParseUser user){ //this gets parse installation data for user which is used with notifications
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId()); //can add custom data to installation object
        installation.saveInBackground();
    }
}
