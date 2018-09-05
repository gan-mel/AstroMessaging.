package com.example.ganm.ganmessaging.push;

import android.content.Intent;

import com.example.ganm.ganmessaging.logic.FirebaseRegistrationIntentService;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        Intent intent = new Intent(this, FirebaseRegistrationIntentService.class);
        startService(intent);

    }
}