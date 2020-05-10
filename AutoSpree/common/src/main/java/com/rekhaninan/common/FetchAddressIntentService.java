package com.rekhaninan.common;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ResultReceiver;
import android.text.TextUtils;


import static android.content.ContentValues.TAG;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FetchAddressIntentService extends IntentService {


    protected ResultReceiver mReceiver;
    private static final String TAG = "FetchAddressIS";
    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

            // Check if receiver was properly registered.
            if (mReceiver == null) {
                Log.e(TAG, "No receiver received. There is nowhere to send the results.");
                return;
            }

            // Get the location passed to this service through an extra.
            Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

            // Make sure that the location data was really sent over through an extra. If it wasn't,
            // send an error error message and return.
            if (location == null) {

                Log.e(TAG, "No location data provided");
                deliverResultToReceiver(Constants.FAILURE_RESULT, "No location data provided");
                return;
            }
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String errorMessage = "";



            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        // In this sample, get just a single address.
                        1);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.

                Log.e(TAG, "Location service not available " + ioException.getMessage());
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.

                Log.e(TAG,  "Invalid latitude or longitude used . " +
                        "Latitude = " + location.getLatitude() +
                        ", Longitude = " +
                        location.getLongitude() + " " + illegalArgumentException.getMessage());
            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size()  == 0) {

                    Log.e(TAG, "Address not found");

                deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            } else {
                Address address = addresses.get(0);
              //  ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
               // for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                //    addressFragments.add(address.getAddressLine(i));
               // }
                Log.i(TAG, "Address found");
                deliverResultToReceiver(Constants.SUCCESS_RESULT,
                        address);
                //        TextUtils.join(System.getProperty("line.separator"),
                  //              addressFragments));
            }


        }
    }

    private void deliverResultToReceiver(int resultCode, Address address) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(Constants.RESULT_DATA_KEY, address);
        mReceiver.send(resultCode, bundle);
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
