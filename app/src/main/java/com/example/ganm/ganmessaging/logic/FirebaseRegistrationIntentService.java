package com.example.ganm.ganmessaging.logic;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.liveperson.messaging.sdk.api.LivePerson;

public class FirebaseRegistrationIntentService extends IntentService {

    public static final String TAG = FirebaseRegistrationIntentService.class.getSimpleName();

    public FirebaseRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: registering the token to pusher");
        String token = FirebaseInstanceId.getInstance().getToken();
        // Register to Liveperson Pusher
        String account = "44142597";
        String appID = "com.example.ganm.ganmessaging";
        LivePerson.registerLPPusher(account, appID, token);
    }
}