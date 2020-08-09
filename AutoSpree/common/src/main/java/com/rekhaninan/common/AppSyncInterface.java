package com.rekhaninan.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;
import androidx.arch.core.internal.FastSafeIterableMap;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
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
    private Activity activity;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDB client;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    void getUserID(int alexaCode)
    {
        Log.d(TAG, "Getting Alexa userID with code=" + alexaCode);

        Amplify.API.query(
                "EASYGROCLIST",
                ModelQuery.list(AccountLink.class, AccountLink.CODE.eq(new Integer(alexaCode))),
                response-> {
                    if (response.getData() == null)
                    {
                        Log.d(TAG, "Null response for AccountLinK AWS amplify query");
                    }
                    else {
                        for (AccountLink accountLink : response.getData()) {
                            Log.d(TAG, "Added to SharedPreferences alexaUserID=" + accountLink.getUserId() +
                                    " alexaCode=" + accountLink.getCode());
                            SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharing.edit();
                            editor.putString("alexaUserID", accountLink.getUserId());
                            editor.commit();
                            storeShareIdInAWS(accountLink.getUserId(), accountLink);
                            displayLinkedAlert();
                        }
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
        Table userInfo = Table.loadTable(client, "UserInfo-tbezpre3sndnnchllezvfcojdi-esylst");
        Document userInfoItem = new Document();
        userInfoItem.put("verified", true);
        userInfoItem.put("share_id", (int)share_id);
        userInfoItem.put("userID", alexaUserID);
        userInfoItem.put("date", secsSince1970);

        userInfo.putItem(userInfoItem);
        Log.d(TAG, "Stored userInfo in AWS share_id=" + share_id + " date="+ secsSince1970
                + " userID=" + alexaUserID);

    }



    private void displayLinkedAlert()
    {
        activity.runOnUiThread(new Runnable() {
            public void run() {

                AlertDialog alertDialog = new AlertDialog.Builder(ctxt).create();
                alertDialog.setTitle("Alexa linked");
                String linkedMsg = "EasyGrocList Android App and Alexa skill are linked." +
                        " You can start adding items to lists from Alexa compatible devices via voice interface." +
                        "IMPORTANT NOTE: ALWAYS Restart Android app to download alexa added items";
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
        });

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

    private  void deleteAlexaItemInAWS (EasyGrocListItems alexaItem, String userID)
    {
       Table easyGrocListItemsTable = Table.loadTable(client, "EasyGrocListItems-tbezpre3sndnnchllezvfcojdi-esylst");
        Log.d(TAG, "Loaded EasyGrocListItems table");
        easyGrocListItemsTable.deleteItem(new Primitive(alexaItem.getUserId()), new Primitive(alexaItem.getName()+ "#" +
                alexaItem.getMasterList()));
        Log.d(TAG, "Deleted alexaItem=" + alexaItem.getName() + " masterList=" + alexaItem.getMasterList());
        /*
        Amplify.API.mutate (
                ModelMutation.delete(alexaItem ),
                response->Log.d(TAG, "Deleted alexaItem=" + alexaItem.getName() + " masterList=" +
                        alexaItem.getMasterList()+ " from AWS response=" + response),
                error->Log.e(TAG, "Failed to delete alexaIteme=" +alexaItem.getName() + " masterList=" +
                        alexaItem.getMasterList()+ " from AWS", error)
        );
         */
    }
    public void getAlexaItems()
    {
        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        String userID = sharing.getString("alexaUserID", "None");
        if (userID.equals("None"))
        {
            Log.d(TAG, "UserID not set, skipping getAlexaItems");
            return;
        }
        Log.d(TAG, "Getting alexa items for UserID=" + userID);
        mainLst = new ArrayList<Item>();
        mainMasterLst = new ArrayList<Item>();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        Calendar c = new GregorianCalendar();
        String formattedDate = df.format(c.getTime());
        plainListName = "List " + formattedDate;
        rowNo = 1;
        Amplify.API.query(
                ModelQuery.list(EasyGrocListItems.class, EasyGrocListItems.USER_ID.contains(userID)),
                response -> {
                    for (EasyGrocListItems alexaItem : response.getData()) {
                        Log.i("TAG", "Received from alexa name=" + alexaItem.getName() +
                                " masterList=" + alexaItem.getMasterList());
                        cacheAlexaItem(alexaItem);
                    }
                    storeAlexaItems();
                    for (EasyGrocListItems alexaItem : response.getData()) {
                        deleteAlexaItemInAWS(alexaItem, userID);
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

    private String getNormalizedListName(List<Item> templNameLst, String alexaMasterList)
    {
        String alexaNormalizedList = NshareUtil.removeNonAlphanumeric(alexaMasterList);

        for (Item templNameItem : templNameLst)
        {
            String templNameNormalized = NshareUtil.removeNonAlphanumeric(templNameItem.getName());
            if (templNameNormalized.equalsIgnoreCase(alexaNormalizedList))
            {
                return templNameItem.getName();
            }
        }
        String emptyString = new String();
        return emptyString;
    }

    private boolean normalizedNameCompare(String alexaName, String name)
    {
        String alexaNormalizedName = NshareUtil.removeNonAlphanumeric(alexaName);
        String normalizedName = NshareUtil.removeNonAlphanumeric(name);
        if (alexaNormalizedName.equalsIgnoreCase(normalizedName))
        {
            return true;
        }
        return  false;
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

        List<Item> templNameLst = DBOperations.getInstance().getTemplNameLst();
        for (Map.Entry<String, Map<String, Item>> entry : mlistMap.entrySet()) {
            String masterList = getNormalizedListName(templNameLst,entry.getKey()) ;
            if (masterList == null || masterList.isEmpty())
                continue;
            Map<String, Item> itemMap  = entry.getValue();
            // ...

            java.util.List<Item> templList = DBOperations.getInstance().getTemplList(masterList, 0);
            java.util.List<Item> templListInv = DBOperations.getInstance().getTemplList(masterList+ ":INV", 0);
            java.util.List<Item> templListScr = DBOperations.getInstance().getTemplList(masterList+ ":SCRTCH", 0);

            for (Item item : templList)
            {
                Log.d(TAG, "Always item=" + item.getItem() +" masterList=" + item.getName());
            }

            for (Item item : templListInv)
            {
                Log.d(TAG, "Replenish item=" + item.getItem() +" masterList=" + item.getName());
            }

            for (Item item : templListScr)
            {
                Log.d(TAG, "Scratch item=" + item.getItem() +" masterList=" + item.getName());
            }

            for (Item item : itemMap.values())
            {
                Log.d(TAG, "Alexa item=" + item.getItem() + " masterList=" + item.getName());
            }
            boolean bChangeReplenish = false;
            boolean bChangeScrtch = false;
            for (Item item : itemMap.values())
            {
                boolean bFound = false;
                for (Item always : templList)
                {
                    if (always.getItem() == null)
                    {
                        continue;
                    }
                    if (normalizedNameCompare(item.getItem(), always.getItem()))
                    {
                        bFound = true;
                        break;
                    }
                }
                if(bFound)
                    continue;
                for (Item replenish : templListInv)
                {
                    if (replenish.getItem() == null)
                    {
                        continue;
                    }
                    if (normalizedNameCompare(item.getItem(), replenish.getItem()))
                    {
                        bFound = true;
                        Log.d(TAG, "Changing inventory for item=" + item.getItem() + " isAdd="+ item.isAdd());
                        if (item.isAdd())
                        {
                            int inv = replenish.getInventory();
                            if (inv != 0) {
                                bChangeReplenish = true;
                                replenish.setInventory(0);
                            }
                        }
                        else
                        {
                            int inv = replenish.getInventory();
                            if (inv != 10) {
                                bChangeReplenish = true;
                                replenish.setInventory(10);
                            }
                        }
                        break;
                    }
                }
                if(bFound)
                    continue;

                int indx = 0;
                int nullindx = -1;
                for (Item oneTime : templListScr)
                {
                    if (oneTime.getItem() == null)
                    {
                        if (nullindx == -1)
                        {
                            nullindx = indx;
                        }
                        continue;
                    }
                    if (normalizedNameCompare(item.getItem(), oneTime.getItem()))
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
                if (nullindx != -1)
                {
                    Item scrItem = templListScr.get(nullindx);
                    scrItem.setItem(item.getItem());
                }
                else {
                    item.setRowno(indx);
                    templListScr.add(item);
                }
                bChangeScrtch = true;

            }

            if (bChangeScrtch)
            {
                Item nameItem = new Item();
                nameItem.setName(masterList + ":SCRTCH");
                DBOperations.getInstance().deleteDb(nameItem, EASYGROC_TEMPL_EDIT_ITEM);
                for (Item oneTime : templListScr)
                    DBOperations.getInstance().insertDb(oneTime, EASYGROC_TEMPL_EDIT_ITEM);
            }

            if (bChangeReplenish)
            {
                Item nameItem = new Item();
                nameItem.setName(masterList + ":INV");
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
        item.setShare_id(0);
        item.setItem(alexaItem.getName());
        item.setAdd(alexaItem.getAdd());
        item.setMasterList(alexaItem.getMasterList());
        long date = Long.parseLong(alexaItem.getDate()) ;
        item.setDate((int)(date/1000));
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
        long date = Long.parseLong(alexaItem.getDate()) ;
        item.setDate((int)(date/1000));
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

    void initializeDynamoDB()
    {
        try {



            client = new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAYKMMXWGL3P3JM24S",
                    "xKtSe0DV4OE2F8iYZB4UFIsCkvtEcrSuaJcgUtmp"));


        }
        catch (Exception excp)
        {
            Log.d(TAG, "Caught exception=" + excp);
        }
    }

    AppSyncInterface(Context ctx)
    {
        ctxt = ctx;

        initializeAWSAppSync();
        initializeDynamoDB();
    }
}
