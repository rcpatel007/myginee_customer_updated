package com.myginee.customer;

import android.app.Application;
import android.content.Context;

import com.myginee.customer.payumoney.AppEnvironment;
import com.google.firebase.FirebaseApp;

public class GineeAppApplication extends Application {
    public static Context getApplicationContext;
    AppEnvironment appEnvironment;
    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());
        getApplicationContext = this;
        appEnvironment = AppEnvironment.SANDBOX;
        FirebaseApp.initializeApp(this);

    }
    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }
}