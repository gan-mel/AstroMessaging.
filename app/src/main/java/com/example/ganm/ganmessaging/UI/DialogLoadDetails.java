package com.example.ganm.ganmessaging.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ganm.ganmessaging.R;
import com.example.ganm.ganmessaging.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;


public class DialogLoadDetails extends DialogFragment {

    private static final String TAG = "ResendVerificationDialo";

    //widgets
    private TextView  userOne,userTwo,userThree;

    //vars
    private Context mContext;

    private ArrayList<UserInfo> user =new ArrayList<>();



    @Nullable

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_load_layout, container, false);

        userOne = (TextView) view.findViewById(R.id.user1);
        userTwo = (TextView) view.findViewById(R.id.user2);
        userThree = (TextView) view.findViewById(R.id.user3);


        mContext = getActivity();

        //Call query
        getUserAccountsData();


        /* 3 Confirm diaglogs to choose a user */

        TextView confirmDialog = (TextView) view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to resend verification email.");

                if(!isEmpty(userOne.getText().toString())){

                    Log.d("sender", "Broadcast from dialog to mainactivity");
                    Intent intent = new Intent("dialog");
                    intent.putExtra("name", user.get(0).getFirst());
                    intent.putExtra("last", user.get(0).getLast());
                    intent.putExtra("avatar", user.get(0).getAvatarurl());
                    intent.putExtra("nick", user.get(0).getNick());
                    intent.putExtra("phone", user.get(0).getPhone());


                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                    getDialog().dismiss();

                }


                else{
                    Toast.makeText(mContext, "There was an error loading user from DB or no previous login attempts exist", Toast.LENGTH_SHORT).show();
                }

            }
        });

        TextView confirmDialog2 = (TextView) view.findViewById(R.id.dialogConfirm2);
        confirmDialog2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to resend verification email.");

                if(!isEmpty(userTwo.getText().toString())){

                    Log.d("sender", "Broadcast from dialog to mainactivity");
                    Intent intent = new Intent("dialog");
                    intent.putExtra("name", user.get(1).getFirst());
                    intent.putExtra("last", user.get(1).getLast());
                    intent.putExtra("avatar", user.get(1).getAvatarurl());
                    intent.putExtra("nick", user.get(1).getNick());
                    intent.putExtra("phone", user.get(1).getPhone());


                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                    getDialog().dismiss();

                }


                else{
                    Toast.makeText(mContext, "There was an error loading user from DB or no previous login attempts exist", Toast.LENGTH_SHORT).show();
                }

            }
        });

        TextView confirmDialog3 = (TextView) view.findViewById(R.id.dialogConfirm3);
        confirmDialog3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to resend verification email.");

                if(!isEmpty(userThree.getText().toString())){

                    Log.d("sender", "Broadcast from dialog to mainactivity");
                    Intent intent = new Intent("dialog");
                    intent.putExtra("name", user.get(2).getFirst());
                    intent.putExtra("last", user.get(2).getLast());
                    intent.putExtra("avatar", user.get(2).getAvatarurl());
                    intent.putExtra("nick", user.get(2).getNick());
                    intent.putExtra("phone", user.get(2).getPhone());


                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                    getDialog().dismiss();

                }


                else{
                    Toast.makeText(mContext, "There was an error loading user from DB or no previous login attempts exist", Toast.LENGTH_SHORT).show();
                }

            }
        });



        // Cancel button for closing the dialog
        TextView cancelDialog = (TextView) view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialog().dismiss();

            }
        });

        return view;
    }



    /**
     * Return true if the @param is null
     * @param string
     * @return
     */
    private boolean isEmpty(String string){
        return string.equals("");
    }


    private void getUserAccountsData(){

        Log.d(TAG,"getUserAccountsData: getting the users account information");

/* First getting the tier 1 level which is all the users
 *
  * Then selecting the currently logged in user and getting his children, but in order to display them
  * you must go through each one using an iterator and catch each one's values using UserInfo.class which expects
  * how each object would look like and Push that in to the user array*/

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User");

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(uid);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                    if (singleSnapshot.getKey().equals(uid)) {
                        Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                        while (itr.hasNext()) {
                            user.add(itr.next().getValue(UserInfo.class));
  // Reverse the arraylist to get the latest logins as Firebase adds items from the bottom
                            Collections.reverse(user);

                        }
//                    }
//                }

            //    for (int i=0; i<user.size(); i++ ) {

                   // Log.d("Array: "+i, user.get(i).toString());

                if (  user.size() >= 1   )  {
                    Log.d(TAG, "SIZE MORE THAN 00   " + user.size());

                    userOne.setText(user.get(0).getFirst());

                    if (user.size() >= 2) {
                        Log.d(TAG, "SIZE MORE THAN 0   " + user.size());

                        userTwo.setText(user.get(1).getFirst());

                        if (user.size() >= 3) {
                            Log.d(TAG, "SIZE MORE THAN 1   " + user.size());
                            userThree.setText(user.get(2).getFirst());
                        }
                    }

                }
              }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onDataChange:  ERROR" );

            }
        });

    }


}