package com.example.ganm.ganmessaging.UI;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ganm.ganmessaging.UserInfo;
import com.example.ganm.ganmessaging.logic.IntentsHandler;
import com.example.ganm.ganmessaging.R;
import com.example.ganm.ganmessaging.logic.FirebaseRegistrationIntentService;
import com.example.ganm.ganmessaging.push.NotificationUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.liveperson.api.LivePersonCallback;
import com.liveperson.api.ams.cm.types.CloseReason;
import com.liveperson.api.sdk.LPConversationData;
import com.liveperson.api.sdk.PermissionType;
import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.LPAuthenticationParams;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.messaging.TaskType;
import com.liveperson.messaging.model.AgentData;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.messaging.sdk.api.callbacks.LogoutLivePersonCallback;
import com.liveperson.messaging.sdk.api.model.ConsumerProfile;

import static com.example.ganm.ganmessaging.CONSTANTS.ACCOUNT_ID;
import static com.example.ganm.ganmessaging.CONSTANTS.APP_ID;

public class MainActivity extends AppCompatActivity implements LivePersonCallback {


    private EditText fnameInput;
    private EditText lnameInput;
    private EditText phoneInput;
    private EditText avatarInput;
    private EditText nickInput;
    private Button startConvBtn,LoadT;
    private CheckBox saveMyDetails;
    private Button loadData;
    private Switch authSwitch;
    private Button logOutBtn;
    // Intent Handler
    private IntentsHandler mIntentsHandler;
    private String name,lastName,phone,nickName,avatarURL;

    private static final String TAG = "Main Activity";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("dialog"));
        setContentView(R.layout.activity_main);
        // IntentsHandler is the object we introduced in the previous section of this tutorial
        mIntentsHandler = new IntentsHandler(this);

        // Consumer name and other inputs
        fnameInput = (EditText) findViewById(R.id.firstnamefield);
        lnameInput = (EditText) findViewById(R.id.lastnamefield);

        avatarInput = (EditText) findViewById(R.id.avatarURL);
        phoneInput = (EditText) findViewById(R.id.phoneNumber);
        nickInput = (EditText) findViewById(R.id.nickName);
        saveMyDetails = (CheckBox) findViewById(R.id.saveD);

        initLiveperson(); // Move Init to a different function

        logOutBtn = (Button)findViewById(R.id.logoutBtn);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LivePerson.logOut(MainActivity.this, ACCOUNT_ID, APP_ID, new LogoutLivePersonCallback() {
                    @Override
                    public void onLogoutSucceed() {
                        Log.d(TAG, "onLogoutSucceed: ");
                        Toast.makeText(getApplicationContext(), "onLogoutSucceed", Toast.LENGTH_SHORT).show();
                        initLiveperson();
                    }

                    @Override
                    public void onLogoutFailed() {
                        Log.d(TAG, "onLogoutFailed: ");
                        Toast.makeText(getApplicationContext(), "onLogoutFailed", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        authSwitch = (Switch)findViewById(R.id.authSwitch);
        //If Checkbox is checked load the last saved details
        isChecked();
        loadFromPref();

// init basic UI views

        //initViews();

// Init the button listener
        initOpenConversationButton();
        handlePush(getIntent());

        // Tests
        UserInfo v1 = new UserInfo();
        v1.setFirst("Gan");
        Log.v(TAG, "PRINT" + v1.getFirst());

         LoadT = (Button) findViewById(R.id.loadD);
        LoadT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogLoadDetails dialog = new DialogLoadDetails();
                dialog.show(getSupportFragmentManager(), "dialog_load_layout");

            }
        });
    }

    private void initLiveperson() {
        LivePerson.initialize(MainActivity.this, new InitLivePersonProperties(ACCOUNT_ID, APP_ID, new InitLivePersonCallBack() {
            @Override
            public void onInitSucceed() {
                // you can't register pusher before initialization
                handleGCMRegistration(MainActivity.this);
                LivePerson.setCallback(MainActivity.this);
                runOnUiThread(new Runnable() {


                    @Override
                    public void run() {
                      //  openActivity();
                    }
                });

            }
            @Override
            public void onInitFailed(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Init Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }));
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        checkAuthenticationState();

    }

    // If check box is back save the input
    public void isChecked(){
        saveMyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveMyDetails.isChecked()) {
                    saveDetails();
                  //  saveToDB();

                } else {

                    System.out.println("Not saving any details");
                         }
                }
            });
    }

// Load the saved data in the preference files
protected void loadFromPref(){
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        fnameInput.setText(sharedPref.getString("firstName",""));
        lnameInput.setText(sharedPref.getString("lastName",""));
        avatarInput.setText(sharedPref.getString("avatarUrl",""));
        phoneInput.setText(sharedPref.getString("phone",""));
        nickInput.setText(sharedPref.getString("nickName",""));
        saveMyDetails.setChecked(sharedPref.getBoolean("saveLogin", false));

        System.out.println("Pref Loaded");
    }


//Save the current data in the inputs in shared preferences
public void saveDetails(){
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("saveLogin", true);
        editor.putString("firstName",fnameInput.getText().toString());
        editor.putString("lastName",lnameInput.getText().toString());
        editor.putString("avatarUrl",avatarInput.getText().toString());
        editor.putString("phone",phoneInput.getText().toString());
        editor.putString("nickName",nickInput.getText().toString());
        editor.apply();

        Toast.makeText(this, "Details Saved", Toast.LENGTH_LONG).show();
    }

// Save to FireBase Database

//    public void saveToDB(){
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("User").push();
//
//        myRef.child("first").setValue(fnameInput.getText().toString());
//        myRef.child("last").setValue(lnameInput.getText().toString());
//        myRef.child("avatar").setValue(avatarInput.getText().toString());
//        myRef.child("phone").setValue(phoneInput.getText().toString());
//        myRef.child("nick").setValue(nickInput.getText().toString());
//
//    }

    // Credentials load activity

    public void loadCredsActivity(View view) {
        Intent startCredsActivity = new Intent(this, DialogLoadDetails.class);

        startActivity(startCredsActivity);
    }

    private void clearPushNotifications() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NotificationUI.NOTIFICATION_ID);
    }

    private void handlePush(Intent intent) {
        boolean isFromPush = intent.getBooleanExtra(NotificationUI.PUSH_NOTIFICATION, false);
        //Check if we came from Push Notification
        if (isFromPush) {
            clearPushNotifications();
            if (LivePerson.isValidState()){
                openActivity();
            }
            else
                initActivityConversation();
        }
    }

    private void initOpenConversationButton() {
        startConvBtn = (Button) findViewById(R.id.startchtbtn2);
        startConvBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
// This will check if we already in a conversation
                    if (LivePerson.isValidState()) {


                            Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser()
                                    .getUid());

                            UserInfo user = new UserInfo();
                            user.setFirst(fnameInput.getText().toString());
                            user.setAvatarurl(avatarInput.getText().toString());
                            user.setEmail("nonea");
                            user.setLast(lnameInput.getText().toString());
                            user.setNick(nickInput.getText().toString());
                            user.setPhone(phoneInput.getText().toString());
                            user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            FirebaseDatabase.getInstance().getReference()
                                    .child("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "onComplete: Task: " + task);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Error: " + e);

                                }
                            });

                            openActivity();
                    }
                    else {
// Push - later in this tutorial
                    removeNotification();
                    initActivityConversation(); // The conversation activity
                }
            }
        });
    }

    public void  initActivityConversation() {

        initLiveperson();

    }

    public void openActivity() {
 if(authSwitch.isChecked()) {
     LivePerson.showConversation(MainActivity.this, new LPAuthenticationParams().setHostAppJWT("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2xpdmVwZXJzb24uY29tIiwic3ViIjoiZ2FubSIsImlhdCI6MTUyNzU5NDc3NywiZXhwIjo2MTUyNzU5NDc3N30.mIi1yf5fzFkdknSDwEqfrG2sXjwljWops3k3cnWaDKsRCsXIsDZJBtGzD0KGRyBWhSCvhEuF72QBl_7AtWT3T5JNVMQrDlS26nXimMLtl_sqBCqI5XDzxY1ENif-gpivWu-pcGcaKN1APjd_csUa76rm0Yc_JLjgsjTp8uz0yXTrQWub-8oOikmGvBnBnt75t4Ht-2tDGI3RBFN1o6xN_YkQrHwRJ1xx2bhzKq2anYQjFVcmVVDTQ0WdgbOv2GZu5bLHvgXHWYwdKRdqVwWwMwFgyUbjNixMFL1IVPHU6_wBKccjwsrC0dfSl0B8g4oH8cw9PeEPYP4cOZ2elbd3NQ"), new ConversationViewParams(false));
 } else {
     LivePerson.showConversation(MainActivity.this, new LPAuthenticationParams().setHostAppJWT(""), new ConversationViewParams(false));
 }
        ConsumerProfile consumerProfile = new ConsumerProfile.Builder()
                .setFirstName(fnameInput.getText().toString())
                .setLastName(lnameInput.getText().toString())
                .setPhoneNumber(phoneInput.getText().toString())
                .setNickname(nickInput.getText().toString())
                .setAvatarUrl(avatarInput.getText().toString())
                .build();
        LivePerson.setUserProfile(consumerProfile);

    }



    public void handleGCMRegistration(Context ctx) {
        Intent intent = new Intent(ctx, FirebaseRegistrationIntentService.class);
        ctx.startService(intent);
    }


    public void removeNotification() {
        NotificationUI.hideNotification(this);
    }

    private void getUserDetails(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            String properties =
                    "uid: " + uid + "\n" +
                    "name: " + name + "\n" +
                    "email: " + email + "\n" +
                    "photoUrl: " + photoUrl ;

            Log.d(TAG, "Get user properties: " + properties);

        }


    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state.");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
            Log.d(TAG, "checkAuthenticationState: user is authenticated.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.optionSignOut:
                signOut();
                return true;
            case R.id.smile:
                Toast.makeText(MainActivity.this,"Run, you fool!",Toast.LENGTH_LONG).show();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut()
    {
        Log.d(TAG, "Sign out: User has clicked to sign out");
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
// Get extra data included in the Intent
            name = intent.getStringExtra("name");
            lastName = intent.getStringExtra("last");
            phone = intent.getStringExtra("phone");
            avatarURL = intent.getStringExtra("avatar");
            nickName = intent.getStringExtra("nick");

            Log.d(TAG, "onReceive: "+ name);
            fnameInput.setText(name);
            lnameInput.setText(lastName);
            phoneInput.setText(phone);
            nickInput.setText(nickName);
            avatarInput.setText(avatarURL);





        }
    };

    @Override
    public void onError(TaskType taskType, String s) {

    }

    @Override
    public void onTokenExpired() {
        Log.d(TAG, "onTokenExpired: ");
        Toast.makeText(getApplicationContext(), "onTokenExpired: ", Toast.LENGTH_SHORT).show();

        LivePerson.reconnect(new LPAuthenticationParams().setHostAppJWT("NEWJWT"));

    }

    @Override
    public void onConversationStarted(LPConversationData lpConversationData) {
        Log.d(TAG, "onConversationStarted: ");
        Toast.makeText(getApplicationContext(), "onConversationStarted: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onConversationStarted() {
        Log.d(TAG, "onConversationStarted: ");
        Toast.makeText(getApplicationContext(), "onConversationStarted: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onConversationResolved(LPConversationData lpConversationData) {
        Log.d(TAG, "onConversationResolved: ");
        Toast.makeText(getApplicationContext(), "onConversationResolved: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onConversationResolved() {
        Log.d(TAG, "onConversationResolved: ");
        Toast.makeText(getApplicationContext(), "onConversationResolved: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onConversationResolved(CloseReason closeReason) {
        Log.d(TAG, "onConversationResolved: ");
        Toast.makeText(getApplicationContext(), "onConversationResolved: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onConnectionChanged(boolean b) {
        Log.d(TAG, "onConnectionChanged: ");
        Toast.makeText(getApplicationContext(), "onConnectionChanged: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onAgentTyping(boolean b) {
        Log.d(TAG, "onAgentTyping: ");
        Toast.makeText(getApplicationContext(), "onAgentTyping: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onAgentDetailsChanged(AgentData agentData) {
        Log.d(TAG, "onAgentDetailsChanged: ");
        Toast.makeText(getApplicationContext(), "onAgentDetailsChanged: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onCsatLaunched() {
        Log.d(TAG, "onCsatLaunched: ");
        Toast.makeText(getApplicationContext(), "onCsatLaunched: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onCsatDismissed() {
        Log.d(TAG, "onCsatDismissed: ");
        Toast.makeText(getApplicationContext(), "onCsatDismissed: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onCsatSubmitted(String s) {
        Log.d(TAG, "onCsatSubmitted: ");
        Toast.makeText(getApplicationContext(), "onCsatSubmitted: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onCsatSkipped() {
        Log.d(TAG, "onCsatSkipped: ");
        Toast.makeText(getApplicationContext(), "onCsatSkipped: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onConversationMarkedAsUrgent() {
        Log.d(TAG, "onConversationMarkedAsUrgent: ");
        Toast.makeText(getApplicationContext(), "onConversationMarkedAsUrgent: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onConversationMarkedAsNormal() {
        Log.d(TAG, "onConversationMarkedAsNormal: ");
        Toast.makeText(getApplicationContext(), "onConversationMarkedAsNormal: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onOfflineHoursChanges(boolean b) {
        Log.d(TAG, "onOfflineHoursChanges: ");
        Toast.makeText(getApplicationContext(), "onOfflineHoursChanges: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onAgentAvatarTapped(AgentData agentData) {
        Log.d(TAG, "onAgentAvatarTapped: ");
        Toast.makeText(getApplicationContext(), "onAgentAvatarTapped: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onUserDeniedPermission(PermissionType permissionType, boolean b) {
        Log.d(TAG, "onUserDeniedPermission: ");
        Toast.makeText(getApplicationContext(), "onUserDeniedPermission: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onUserActionOnPreventedPermission(PermissionType permissionType) {
        Log.d(TAG, "onUserActionOnPreventedPermission: ");
        Toast.makeText(getApplicationContext(), "onUserActionOnPreventedPermission: ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onStructuredContentLinkClicked(String s) {
        Log.d(TAG, "onStructuredContentLinkClicked: ");
        Toast.makeText(getApplicationContext(), "onStructuredContentLinkClicked: ", Toast.LENGTH_SHORT).show();


    }
}
