package com.rekhaninan.easygroclist;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;

public class EasyGrocAmplifyApp  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.configure(getApplicationContext());

            Log.i("EasyGrocAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("EasyGrocAmplifyApp", "Could not initialize Amplify", e);
        }
    }
}

