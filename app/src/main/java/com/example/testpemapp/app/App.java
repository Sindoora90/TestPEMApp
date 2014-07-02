package com.example.testpemapp.app;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

/**
 * Created by Sindoora on 02.07.14.
 */
public class App extends Application {




    @Override public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "MHHSAa8eQ6gpV4GnGO8TJBVjQ7f4bN8EuqKego9l", "DUhSOqqpyz677Zaz1TuA0jthlRINYTN9u4LYxQdL");
        PushService.setDefaultPushCallback(this, MainActivity.class);
        //TODO testing push channels here
        //PushService.subscribe(this, "TestChannel", MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }


}
