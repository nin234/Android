package com.rekhaninan.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;
import androidx.arch.core.internal.FastSafeIterableMap;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.AccountLink;
import com.amplifyframework.datastore.generated.model.EasyGrocListItems;
import com.amplifyframework.datastore.generated.model.UserInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_EDIT_ITEM;

public class AppSyncInterface {

    private Context ctxt;
    private static final String TAG="AppSyncInterface";
    private String plainListName;
    private int rowNo;
    private java.util.List<Item> mainLst ;
    private java.util.List<Item> mainMasterLst ;
    void getUserID(int alexaCode)
    {
        Log.d(TAG, "Getting Alexa userID with code=" + alexaCode);
        Amplify.API.query(
                ModelQuery.list(AccountLink.class, AccountLink.CODE.eq(alexaCode)),
                response-> {
                for (AccountLink accountLink : response.getData()) {
                Log.d(TAG, "Added to SharedPreferences alexaUserID=" + accountLink.getUserId() +
                        " alexaCode=" + accountLink.getId());
                    SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharing.edit();
                    editor.putString("alexaUserID", accountLink.getUserId());
                    editor.commit();
                    storeShareIdInAWS(accountLink.getUserId(), accountLink);
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

    private void storeShareIdInAWS(String alexaUserID, AccountLink accountLink)
    {
        long share_id =ShareMgr.getInstance().getShare_id();
        long seconds = System.currentTimeMillis() / 1000l;
        String secsSince1970 = Long.toString(seconds);
        UserInfo userInfo = UserInfo.builder().shareId(new Integer((int)share_id)).date(secsSince1970).
                userId(alexaUserID).verified(true).build();
        Amplify.API.mutate (
        ModelMutation.create(userInfo),
                response -> {
                    Log.d("TAG", "Added share_id alexaUserId link to AWS: ");
                    deleteCodeToUserIDLinkInAWS( accountLink);
                },
                error -> {
                    Log.e("TAG", "Failed to add share_id, alexaUserId link to AWS, trying update", error);

                    Amplify.API.mutate(
                            ModelMutation.update(userInfo),
                            response -> {
                                Log.i("TAG", "Updated share_id alexaUserId link to AWS: ");
                                deleteCodeToUserIDLinkInAWS(accountLink);
                            },
                            errorU->  Log.e("TAG", "Failed to update share_id to alexaUserId link  to AWS", error)
                    );
                }
                );
    }

    private void deleteCodeToUserIDLinkInAWS(AccountLink accountLink)
    {

        Amplify.API.mutate (
            ModelMutation.delete(accountLink),
                response->Log.d(TAG, "Deleted alexaCode=" + accountLink.getCode() + " from AccountLink table"),
                error->Log.e(TAG, "Failed to delete alexaCode=" + accountLink.getCode() + " from AccountLink table", error)
        );
    }
   public void subscribeAlexaItems()
   {
       SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
       String userID = sharing.getString("alexaUserID", "None");
       if (userID.equals("None"))
       {
           return;
       }
    //Can't implement for now as AWS doesnot offer a server level filtering for the data returned
        //Amplify.API.subscribe(ModelSubscription.);
   }

    private  void deleteAlexaItemInAWS (EasyGrocListItems alexaItem)
    {
        Amplify.API.mutate (
                ModelMutation.delete(alexaItem),
                response->Log.d(TAG, "Deleted alexaItem=" + alexaItem.getName() + " masterList=" +
                        alexaItem.getMasterList()+ " from AWS"),
                error->Log.e(TAG, "Failed to delete alexaIteme=" +alexaItem.getName() + " masterList=" +
                        alexaItem.getMasterList()+ " from AWS", error)
        );
    }
    public void getAlexaItems()
    {
        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        String userID = sharing.getString("alexaUserID", "None");
        if (userID.equals("None"))
        {
            return;
        }
        mainLst = new ArrayList<Item>();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        Calendar c = new GregorianCalendar();
        String formattedDate = df.format(c.getTime());
        plainListName = "List " + formattedDate;
        rowNo = 1;
        Amplify.API.query(
                ModelQuery.list(EasyGrocListItems.class, EasyGrocListItems.NAME.contains(userID)),
                response -> {
                    for (EasyGrocListItems alexaItem : response.getData()) {
                        Log.i("TAG", "Received from alexa name=" + alexaItem.getName() +
                                " masterList=" + alexaItem.getMasterList());
                        cacheAlexaItem(alexaItem);
                    }
                    storeAlexaItems();
                    for (EasyGrocListItems alexaItem : response.getData()) {
                        deleteAlexaItemInAWS(alexaItem);
                    }
                },
                error -> Log.e("TAG", "Query failure", error)
        );
    }

    private void storeAlexaItems()
    {
        storePlainListItems();
        storeMasterListItems();
    }

    private void storeMasterListItems()
    {
        Map<String, Map<String, Item>> mlistMap = new HashMap<>();
        for (Item item : mainMasterLst)
        {
            Map<String, Item> itemMap = mlistMap.get(item.getMasterList());
            if (itemMap != null)
            {
                Item mapItem = itemMap.get(item.getItem());
                if (mapItem != null)
                {
                    if (item.getDate() > mapItem.getDate())
                    {
                        itemMap.put(item.getItem(), item);
                    }
                }
                else
                {
                    itemMap.put(item.getItem(), item);
                }
            }
            else
            {
                itemMap = new HashMap<>();
                itemMap.put(item.getItem(), item);
                mlistMap.put(item.getMasterList(), itemMap);
            }
        }
        for (Map.Entry<String, Map<String, Item>> entry : mlistMap.entrySet()) {
            String masterList = entry.getKey();
            Map<String, Item> itemMap  = entry.getValue();
            // ...
            long share_id =ShareMgr.getInstance().getShare_id();
            java.util.List<Item> templList = DBOperations.getInstance().getTemplList(masterList, share_id);
            java.util.List<Item> templListInv = DBOperations.getInstance().getTemplList(masterList+ ":INV", share_id);
            java.util.List<Item> templListScr = DBOperations.getInstance().getTemplList(masterList+ ":SCRTCH", share_id);
            boolean bChangeReplenish = false;
            boolean bChangeScrtch = false;
            for (Item item : itemMap.values())
            {
                boolean bFound = false;
                for (Item always : templList)
                {
                    if (always.getItem().equals(item.getItem()))
                    {
                        bFound = true;
                        break;
                    }
                }
                if(bFound)
                    continue;
                for (Item replenish : templListInv)
                {
                    if (replenish.getItem().equals(item.getItem()))
                    {
                        bFound = true;

                        if (item.isAdd())
                        {
                            int inv = replenish.getInventory();
                            if (inv != 10) {
                                bChangeReplenish = true;
                                replenish.setInventory(10);
                            }
                        }
                        else
                        {
                            int inv = replenish.getInventory();
                            if (inv != 0) {
                                bChangeReplenish = true;
                                replenish.setInventory(0);
                            }
                        }
                        break;
                    }
                }
                if(bFound)
                    continue;

                int indx = 0;
                for (Item oneTime : templListScr)
                {
                    if (oneTime.getItem().equals(item.getItem()))
                    {
                        if (!item.isAdd())
                        {
                            templListScr.remove(indx);
                            bFound = true;
                            bChangeScrtch = true;
                        }
                    }

                    ++indx;
                }
                if(bFound)
                    continue;

                item.setName(masterList + ":SCRTCH");
                templListScr.add(item);
                bChangeScrtch = true;

            }

            if (bChangeScrtch)
            {
                Item nameItem = new Item();
                nameItem.setName(masterList + ":SCRTCH");
                nameItem.setShare_id(share_id);
                DBOperations.getInstance().deleteDb(nameItem, EASYGROC_TEMPL_EDIT_ITEM);
                for (Item oneTime : templListScr)
                    DBOperations.getInstance().insertDb(oneTime, EASYGROC_TEMPL_EDIT_ITEM);
            }

            if (bChangeReplenish)
            {
                Item nameItem = new Item();
                nameItem.setName(masterList + ":INV");
                nameItem.setShare_id(share_id);
                DBOperations.getInstance().deleteDb(nameItem, EASYGROC_TEMPL_EDIT_ITEM);
                for (Item replenish : templListInv)
                    DBOperations.getInstance().insertDb(replenish, EASYGROC_TEMPL_EDIT_ITEM);
            }
        }
    }

    private void   cacheMasterListItem(EasyGrocListItems alexaItem)
    {
        Item item = new Item();
        item.setRowno(rowNo++);
        item.setName(plainListName);
        item.setShare_id(ShareMgr.getInstance().getShare_id());
        item.setItem(alexaItem.getName());
        item.setAdd(alexaItem.getAdd());
        item.setMasterList(alexaItem.getMasterList());
        item.setDate(Integer.parseInt(alexaItem.getDate()));
        mainMasterLst.add(item);
    }

    private void storePlainListItems()
    {
        //Comparator<Item> compareById = (Item o1, Item o2) -> o1.getDate() < o2.getDate() );
        //mainLst.sort(compareById);
        Map<String, Item>   itemMap = new HashMap<>();
        for (Item item : mainLst)
        {
            Item mapItem = itemMap.get(item.getItem());
            if (mapItem != null)
            {
                if (item.getDate() > mapItem.getDate())
                {
                    itemMap.put(item.getItem(), item);
                }
            }
            else
            {
                itemMap.put(item.getItem(), item);
            }
        }

        for (Item item : itemMap.values())
        {
            if (item.isAdd())
            {
                DBOperations.getInstance().insertDb(item, EASYGROC_ADD_ITEM);
            }
        }
    }

    private void cacheAlexaItem(EasyGrocListItems alexaItem)
    {
        if (alexaItem.getMasterList().equals("NOLIST"))
        {
            cachePlainListItem(alexaItem);
        }
        else
        {
            cacheMasterListItem(alexaItem);
        }
    }


  private   void cachePlainListItem(EasyGrocListItems alexaItem)
    {
        Item item = new Item();
        item.setRowno(rowNo++);
        item.setName(plainListName);
        item.setShare_id(ShareMgr.getInstance().getShare_id());
        item.setItem(alexaItem.getName());
        item.setAdd(alexaItem.getAdd());
        item.setDate(Integer.parseInt(alexaItem.getDate()));
        mainLst.add(item);

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
