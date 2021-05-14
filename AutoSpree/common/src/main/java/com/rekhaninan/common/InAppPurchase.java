package com.rekhaninan.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
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

import static com.android.billingclient.api.BillingClient.SkuType.SUBS;
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
    private boolean bQuerying;
    private String productId;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

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
                delta = 27;
            }
                break;

            case NSHARELIST:
            {
                productId = "com.rekhaninan.nsharelist_yearly";
                delta = 3600*24*30;
                delta = 27;
            }
            break;

            case OPENHOUSES:
            {
                productId = "com.rekhaninan.openhouses_yearly";
                delta = 3600*24*7;
                delta = 27;
            }
            break;

            case AUTOSPREE:
            {
                productId = "com.rekhaninan.autospree_yearly";
                delta = 3600*24*7;
                delta = 27;
            }
            break;

            default:
                break;
        }

    }

    public InAppPurchase(Context ctx)
    {
        bPurchasing = false;
        bQuerying = false;
      ctxt = ctx;
      activity = (Activity) ctxt;
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
        setupPurchaseHandler();
    }

    public void queryPurchases()
    {

        if (bPurchased == true || bPurchasing == true || bQuerying == true)
        {
            return;
        }

        long  now =  System.currentTimeMillis()/1000;


        bQuerying = true;
        queryPurchaseSetup();

    }


    private void updatePrefAndAlert()
    {
        Log.d(getClass().getSimpleName(), "Purchase finished alerting user");
        bPurchased = true;
        bQuerying = false;

        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharing.edit();
        editor.putBoolean("Purchased", bPurchased);
        editor.commit();

        AlertDialog alertDialog = new AlertDialog.Builder(ctxt).create();
        alertDialog.setTitle("Success");
        String purchaseMsg = "Subscribed to Nshare Apps Unlimited";
        alertDialog.setMessage(purchaseMsg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> {
                    dialog.dismiss();
                    return;
                });
        alertDialog.show();
    }

    private void ackPurchase(Purchase purchase)
    {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            Log.d(getClass().getSimpleName(), "Purchased product=" + productId);
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                Log.d(getClass().getSimpleName(), "Acking purchases");
            }
            else
            {
                updatePrefAndAlert();
            }
        }
    }

    private void setupPurchaseHandler()
    {
        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                // To be implemented in a later section.
                Log.d(getClass().getSimpleName(), "Purchase updated callback");
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                        if (purchase.getSku().equals(productId)) {

                            ackPurchase(purchase);
                        }
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                    bPurchasing  = false;
                    bQuerying = false;
                    Log.d(getClass().getSimpleName(), "User cancelled purchased");
                } else {
                    Log.d(getClass().getSimpleName(), "Failed to purchase error="
                            + billingResult.getResponseCode());
                            // Handle any other error codes
                    if (bQuerying ==  true)
                    {
                        bQuerying = false;
                        return;
                    }
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
        };

        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        acknowledgePurchaseResponseListener = billingResult -> {

            Log.d(getClass().getSimpleName(), "Purchase acked callback");
            bPurchasing = false;
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
            {
                updatePrefAndAlert();
            }
            else
            {
                bQuerying = false;
            }
        };
    }

    public boolean  canContinue()
    {
        if (bPurchased == true || bPurchasing == true)
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

    private void queryPurchaseSetup()
    {
        Log.d(getClass().getSimpleName(), "Querying purchases");

        billingClient.startConnection(new BillingClientStateListener() {

            // Log.d(getClass().getSimpleName(), "Querying sku details ");

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    boolean bAck = false;
                    Log.d(getClass().getSimpleName(), "Billing set up finished for querying " +
                            "purchases");
                   List<Purchase>  purchases = billingClient.queryPurchases(SUBS).getPurchasesList();
                    for (Purchase purchase : purchases) {
                        if (purchase.getSku().equals(productId)) {
                            Log.d(getClass().getSimpleName(), "Acking product found in querying productId=" +
                                    productId);
                            bAck = true;
                            ackPurchase(purchase);
                        }
                    }
                    if (bAck == false)
                    {
                        bQuerying = false;
                        Log.d(getClass().getSimpleName(), "Could not find productId=" +
                                productId);
                    }

                }
                else
                {
                    bQuerying = false;
                    Log.d(getClass().getSimpleName(), "Error in billing setup response=" +
                            billingResult.getResponseCode());
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.

                Log.d(getClass().getSimpleName(), "Billing service disconnected ");
                bQuerying = false;
            }
        });
    }

    private void onPurchaseConfirm()
    {
        Log.d(getClass().getSimpleName(), "Starting inapp purchase");
        bPurchasing = true;

        billingClient.startConnection(new BillingClientStateListener() {

          // Log.d(getClass().getSimpleName(), "Querying sku details ");

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                Log.d(getClass().getSimpleName(), "Billing set up finished ");
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d(getClass().getSimpleName(), "Querying sku details productId="
                            + productId);
                            List<String> skuList = new ArrayList<>();
                    skuList.add(productId);
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(SUBS);
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
                else
                {
                    bPurchasing = false;
                    Log.d(getClass().getSimpleName(), "Error in billing setup response=" +
                            billingResult.getResponseCode());
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.

                Log.d(getClass().getSimpleName(), "Billing service disconnected ");

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

