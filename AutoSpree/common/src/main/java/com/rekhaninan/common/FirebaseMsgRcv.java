package com.rekhaninan.common;




import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by ninanthomas on 5/15/17.
 */

public class FirebaseMsgRcv extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

            Log.i("FirebaseMsgRcv", " received message from Firebase , retrieving items");
            ShareMgr.getInstance().getItems();
    }
}
