package com.rekhaninan.common;




import android.content.Context;
import android.content.SharedPreferences;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;



/**
 * Created by ninanthomas on 5/15/17.
 */

public class FirebaseMsgRcv extends FirebaseMessagingService {
    private static final String TAG="FirebaseMsgRcv";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

            Log.i("FirebaseMsgRcv", " received message from Firebase , retrieving items");
            ShareMgr.getInstance().getItems();
    }

    @Override
    public void onNewToken(String refreshedToken) {
        Log.e("NEW_TOKEN", refreshedToken);


        Log.i(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        // sendRegistrationToServer(refreshedToken);
        SharedPreferences sharing = getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        String devTkn = sharing.getString("token", "None");
        Log.i(TAG, "Current token=" + devTkn);
        if (!refreshedToken.equals(devTkn)) {
            Log.i(TAG, "Setting update token to true");
            SharedPreferences.Editor editor = sharing.edit();
            editor.putString("token", refreshedToken);
            editor.putBoolean("update", true);
            editor.commit();
            ShareMgr.getInstance().setbUpdateTkn(true);
        }
    }

}
