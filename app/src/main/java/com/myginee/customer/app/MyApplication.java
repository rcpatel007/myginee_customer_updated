package com.myginee.customer.app;

import android.app.Application;
import android.os.StrictMode;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;

    @Override
    public void onCreate() {

     /*   StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());*/
        super.onCreate();
        mInstance = this;
        FirebaseApp.initializeApp(this);

    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
}