package com.rekhaninan.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.AccountLink;

public class AppSyncInterface {

    private Context ctxt;
    void getUserID(int alexaCode)
    {
        Amplify.API.query(
                ModelQuery.get(AccountLink.class, "alexaCode"),
                response -> Log.i("MyAmplifyApp", ((AccountLink) response.getData()).getUserId()),
                error -> Log.e("MyAmplifyApp", error.toString(), error)
        );
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

    AppSyncInterface(Context ctx)
    {
        ctxt = ctx;
        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        String aUserId = sharing.getString("alexUserID", "None");
        initializeAWSAppSync();
    }
}
