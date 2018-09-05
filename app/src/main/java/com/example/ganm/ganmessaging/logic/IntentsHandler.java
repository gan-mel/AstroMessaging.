package com.example.ganm.ganmessaging.logic;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.ganm.ganmessaging.push.NotificationUI;
import com.liveperson.api.LivePersonIntents;
import com.liveperson.messaging.model.AgentData;
import com.example.ganm.ganmessaging.UI.MainActivity;
/**
 * Created by ganm on 1/15/18.
 */

public class IntentsHandler extends MainActivity {

    private static final String TAG = "INTENTS_HANDLER";
    private Context mContext;
    private BroadcastReceiver mLivePersonReceiver;

    public IntentsHandler(Context ctx){
        this.mContext = ctx;
      //  registerToLivePersonEvents();
    }

    public void registerToLivePersonEvents(){
        //createLivePersonReceiver();
        LocalBroadcastManager.getInstance(mContext.getApplicationContext())
                .registerReceiver(mLivePersonReceiver, LivePersonIntents.getIntentFilterForAllEvents());
    }


    private void createLivePersonReceiver() {
        if (mLivePersonReceiver != null){
            return;
        }
        mLivePersonReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "Got LP intent event with action " + intent.getAction());
                switch (intent.getAction()){
                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_AGENT_AVATAR_TAPPED_INTENT_ACTION:
                        onAgentAvatarTapped(LivePersonIntents.getAgentData(intent));
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_AGENT_DETAILS_CHANGED_INTENT_ACTION:
                        AgentData agentData = LivePersonIntents.getAgentData(intent);
                        onAgentDetailsChanged(agentData);
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_CSAT_SKIPPED_INTENT_ACTION:
                    //     LivePersonIntents.sendOnCsatDismissed();
                         AgentData agentData2 = LivePersonIntents.getAgentData(intent);
                         onCsatSkipped(agentData2);
                         break;
                }

            }
        };
    }

//    private void onAgentAvatarTapped(AgentData agentData) {
//        showToast("on Agent Avatar Tapped - " + agentData.mFirstName + " " + agentData.mLastName);
//    }
//
    public void onAgentDetailsChanged(AgentData agentData) {
        showToast("on Agent Details changed - " + agentData.mFirstName + " " + agentData.mLastName);
    }


    private void onCsatSkipped(AgentData agentData){
        showToast("You skipped your CSAT ! " + agentData.mFirstName + " " + agentData.mLastName + " Will be very upset ! ");

    }

    private void showToast(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);

    }




    private void clearPushNotifications() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NotificationUI.NOTIFICATION_ID);
    }

}

