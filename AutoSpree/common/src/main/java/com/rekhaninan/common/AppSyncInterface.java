package com.rekhaninan.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.AccountLink;

import java.util.concurrent.atomic.AtomicBoolean;

public class AppSyncInterface {

    private Context ctxt;
    private static final String TAG="AppSyncInterface";
    void getUserID(int alexaCode)
    {
        Amplify.API.query(
                ModelQuery.list(AccountLink.class, AccountLink.ID.eq(alexaCode)),
                response-> {
                for (AccountLink accountLink : response.getData()) {
                Log.d(TAG, "Added to SharedPreferences alexaUserID=" + accountLink.getUserId() +
                        " alexaCode=" + accountLink.getId());
                    SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharing.edit();
                    editor.putString("alexaUserID", accountLink.getUserId());
                    editor.commit();
                    AlertDialog alertDialog = new AlertDialog.Builder(ctxt).create();
                    alertDialog.setTitle("Alexa linked");
                    String linkedMsg = "EasyGrocList iPhone App and Alexa skill are linked." +
                            " You can start adding items to lists from Alexa compatible devices via voice interface";
                    alertDialog.setMessage(linkedMsg);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    return;
                                }
                            });
                    alertDialog.show();
        }
    },
    error -> Log.e(TAG, "getUserID Query failure", error)
        );
    }

    void getAlexaItems()
    {

    }

    void initializeAWSAppSync()
    {
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(ctxt);

            android.util.Log.i("EasyGrocAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException e) {
            android.util.Log.e("EasyGrocAmplifyApp", "Could not initialize Amplify", e);
        }
    }

    boolean isAccountLinked()
    {
        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        String aUserId = sharing.getString("alexaUserID", "None");
        if (aUserId.equals("None"))
            return false;

        return true;
    }

    AppSyncInterface(Context ctx)
    {
        ctxt = ctx;

        initializeAWSAppSync();
    }
}
