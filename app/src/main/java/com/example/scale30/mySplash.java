package com.example.scale30;

import android.app.Application;
import android.os.SystemClock;

public class mySplash extends Application {
// class to delay the splashscreen for 2500 ms
    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(2500);
    }
}
