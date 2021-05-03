package com.rekhaninan.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.NSHARELIST;
import static com.rekhaninan.common.Constants.OPENHOUSES;

public class InAppPurchase {

    private Activity activity;
    private int delta;
    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
        }
    };

    private BillingClient billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build();


    private String getProductId()
    {
        String app_name = DBOperations.getInstance().getApp_name();
        String productId = "INVALID";

        switch (app_name)
        {

            case EASYGROC: {
                productId = "com.rekhaninan.easygroclist_yearly";
                delta = 3600 * 24 * 30;
            }
                break;

            case NSHARELIST:
            {
                productId = "com.rekhaninan.nsharelist_yearly";
                delta = 3600*24*30;
            }
            break;

            case OPENHOUSES:
            {
                productId = "com.rekhaninan.openhouses_yearly";
                delta = 3600*24*7;
            }
            break;

            case AUTOSPREE:
            {
                productId = "com.rekhaninan.autospree_yearly";
                delta = 3600*24*7;
            }
            break;

            default:
                break;
        }

        return productId;
    }
    public InAppPurchase(Activity act, Context ctxt)
    {
      activity = act;

        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        long firstUseTime = sharing.getLong("FirstUseTime", 0);
        if (firstUseTime == 0)
        {
            firstUseTime =  System.currentTimeMillis();
            SharedPreferences.Editor editor = sharing.edit();
            editor.putLong("FirstUseTime", firstUseTime);
            editor.commit();

        }
    }
}

