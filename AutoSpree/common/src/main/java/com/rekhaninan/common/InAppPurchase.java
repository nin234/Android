package com.rekhaninan.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.NSHARELIST;
import static com.rekhaninan.common.Constants.OPENHOUSES;

public class InAppPurchase {

    private Activity activity;
    private Context ctxt;
    private int delta;
    private  long firstUseTime;
    private boolean bPurchased;
    private boolean bPurchasing;
    private String productId;

    private PurchasesUpdatedListener purchasesUpdatedListener;


    private BillingClient billingClient;


    private void setParams()
    {
        String app_name = DBOperations.getInstance().getApp_name();
        productId = "INVALID";

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

    }

    public InAppPurchase(Activity act, Context ctx)
    {
        bPurchasing = false;
      activity = act;
      ctxt = ctx;
      setParams();
        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
         firstUseTime = sharing.getLong("FirstUseTime", 0);
        if (firstUseTime == 0)
        {
            firstUseTime =  System.currentTimeMillis()/1000;
            SharedPreferences.Editor editor = sharing.edit();
            editor.putLong("FirstUseTime", firstUseTime);
            editor.commit();

        }

        bPurchased = sharing.getBoolean("Purchased", false);
        if (bPurchased == true)
        {
            Log.d(getClass().getSimpleName(), "App is purchased");
        }
        else
        {
            Log.d(getClass().getSimpleName(), "App is not purchased");
        }

        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        setupPurchaseHandler();
    }

    private void setupPurchaseHandler()
    {
        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                bPurchasing = false;
                // To be implemented in a later section.
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                        if (purchase.getSku().equals(productId)) {
                            bPurchased = true;
                            SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharing.edit();
                            editor.putBoolean("Purchased", bPurchased);
                            editor.commit();
                        }
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                    Log.d(getClass().getSimpleName(), "User cancelled purchased");
                } else {
                    // Handle any other error codes.
                    AlertDialog alertDialog = new AlertDialog.Builder(ctxt).create();
                    alertDialog.setTitle("Failed to buy");
                    String purchaseMsg = "Failed to buy subscription for Nshare Apps Unlimited, try again later";
                    alertDialog.setMessage(purchaseMsg);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> {
                                dialog.dismiss();
                                return;
                            });
                    alertDialog.show();

                }
            }
        };
    }

    public boolean  canContinue()
    {
        if (bPurchased == true)
        {
            return true;
        }

       long  now =  System.currentTimeMillis()/1000;

        if ((now - firstUseTime) < delta )
        {
            return true;
        }
        purchase();
        return false;
    }

    private void onPurchaseConfirm()
    {

        bPurchasing = true;

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.

                    List<String> skuList = new ArrayList<>();
                    skuList.add(productId);
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    // Process the result.
                                    for (SkuDetails skuDetail : skuDetailsList)
                                    {
                                        Log.d(getClass().getSimpleName(), "Sku=" + skuDetail.getSku());
                                        if (skuDetail.getSku().equals(productId)) {

                                            Log.d(getClass().getSimpleName(), "Purchasing sku="
                                                    + skuDetail.getSku());
                                            // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                                            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                                    .setSkuDetails(skuDetail)
                                                    .build();

                                            int responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();
                                            if (responseCode != BillingClient.BillingResponseCode.OK)
                                            {
                                                bPurchasing = false;
                                                bPurchasing = false;
                                                AlertDialog alertDialog = new AlertDialog.Builder(ctxt).create();
                                                alertDialog.setTitle("Failed to buy");
                                                String purchaseMsg = "Failed to buy subscription for Nshare Apps Unlimited, try again later";
                                                alertDialog.setMessage(purchaseMsg);
                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                        (dialog, which) -> {
                                                            dialog.dismiss();
                                                            return;
                                                        });
                                                alertDialog.show();
                                            }
                                        }
                                    }
                                }
                            });
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                if (bPurchasing == true) {
                    bPurchasing = false;
                    AlertDialog alertDialog = new AlertDialog.Builder(ctxt).create();
                    alertDialog.setTitle("Failed to buy");
                    String purchaseMsg = "Failed to buy subscription for Nshare Apps Unlimited, try again later";
                    alertDialog.setMessage(purchaseMsg);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> {
                                dialog.dismiss();
                                return;
                            });
                    alertDialog.show();
                }

            }
        });
    }

    private void purchase()
    {
        if (bPurchasing == true)
        {
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(ctxt).create();
        alertDialog.setTitle("Nshare apps unlimited");
        String purchaseMsg = "Purchase subscription to continue using Nshare suite of productivity apps" +
                " - EasyGrocList, nsharelist, OpenHouses and AutoSpree- after the free trial period. " +
                "With one subscription of $0.99 per year you can use all our apps";
        alertDialog.setMessage(purchaseMsg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Buy",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Log.d(getClass().getSimpleName(), "User confirmed purchased");
                        dialog.dismiss();
                        onPurchaseConfirm();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(getClass().getSimpleName(), "User cancelled purchased");
                        dialog.dismiss();

                    }
                });
        alertDialog.show();
    }
}

