package com.rekhaninan.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by ninanthomas on 5/4/17.
 */

public class ShareTokenMgr extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
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
