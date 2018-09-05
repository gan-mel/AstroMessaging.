package com.example.ganm.ganmessaging;

import android.app.Application;
import android.content.Context;

/**
 * Created by ganm on 4/12/18.
 */

public class MainApplication extends Application {

    private static MainApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static MainApplication getInstance(){
        return mInstance;
    }
}
