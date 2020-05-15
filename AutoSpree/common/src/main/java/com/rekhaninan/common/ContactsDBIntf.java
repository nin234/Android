package com.rekhaninan.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.rekhaninan.common.Constants.CONTACTS_ITEM_ADD;
import static com.rekhaninan.common.Constants.CONTACTS_ITEM_ADD_NOVWTYP;
import static com.rekhaninan.common.Constants.FRIENDLISTITEMSEPERATOR;
import static com.rekhaninan.common.Constants.FRIENDLISTTOKENSEPERATOR;

/**
 * Created by ninanthomas on 2/9/17.
 */

public class ContactsDBIntf {


    private SQLiteDatabase contactsDB;
    private ContactsDbHelper contactsDbHelper;
    private Context ctxt;
    private HashMap<Long, Item> contactsMp;

    private final String TAG = "ContactsDBIntf";

    public ContactsDBIntf() {

            Log.i (TAG, "Constructing ContactsDBIntf");

    }

    public void initDb(Context ctx) {
        ctxt = ctx;
        contactsDbHelper = new ContactsDbHelper(ctxt);
        contactsDB = contactsDbHelper.getWritableDatabase();
        contactsMp = new HashMap<>();

        return;
    }

    private void populateContactsMp()
    {
        contactsMp.clear();
        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_MULTI_PROCESS);
        String frndList = sharing.getString("FriendList", "NoName");
        Log.i(TAG, "Populating contactsMp with friendList=" + frndList);
        if (!frndList.equals("NoName")) {
            String[] listcomps = frndList.split(FRIENDLISTITEMSEPERATOR);
            int comps = listcomps.length;
            long share_id = 0;
            for (int j = 0; j < comps; ++j) {
                if (listcomps[j] == null || listcomps[j].length() < 1)
                    continue;

                String[] shareIdArr = listcomps[j].split(FRIENDLISTTOKENSEPERATOR);
                Item contact = new Item();

                share_id = Long.parseLong(shareIdArr[0]);
                contact.setShare_id(share_id);
                contact.setName(shareIdArr[1]);
                contactsMp.put(share_id, contact);
            }
        }
    }

    public List<Item> getMainViewLst()
    {
        try {
            populateContactsMp();
            List<Item> mainVwLst =  new ArrayList<Item>();
            for (HashMap.Entry<Long, Item> entry : contactsMp.entrySet()) {
                if (entry.getValue().getName().equals("ME")) {
                    mainVwLst.add(entry.getValue());
                }

            }

            for (HashMap.Entry<Long, Item> entry : contactsMp.entrySet()) {
                if (!entry.getValue().getName().equals("ME")) {
                    mainVwLst.add(entry.getValue());
                }

            }

            return mainVwLst;
        }
        catch(Exception e)
        {
            Log.e("Error", "Error" + e.getMessage(), e);
        }

        return  null;
    }

    public  boolean insertDb (Item itm, int vwType)
    {
        if (vwType == CONTACTS_ITEM_ADD || vwType == CONTACTS_ITEM_ADD_NOVWTYP) {
            populateContactsMp();
            if (contactsMp.containsKey(itm.getShare_id()))
            {
                Log.i(TAG, "Share Id=" + itm.getShare_id() + " exists in contacts failed to insert");
                return false;
            }
            if (itm.getName().equals("ME") && vwType == CONTACTS_ITEM_ADD)
            {
                Log.d(TAG, "Invalid name ME for contacts");
                return  false;
            }
            String name = Long.toString(itm.getShare_id());
            if (itm.getName() != null && itm.getName().length() > 0) {
                name = itm.getName();
            }
                SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
                String frndList = sharing.getString("FriendList", "NoName");
                if (frndList.equals("NoName"))
                {
                    frndList = Long.toString(itm.getShare_id());
                }
                else
                {
                    frndList += Long.toString(itm.getShare_id());
                }
                frndList += FRIENDLISTTOKENSEPERATOR;
                frndList += name;
                frndList += FRIENDLISTITEMSEPERATOR;
                storeFriendList("com.rekhaninan.autospree" , frndList);
                storeFriendList("com.rekhaninan.openhouses" , frndList);
                storeFriendList("com.rekhaninan.easygroclist" , frndList);
                 Item contact = new Item();
                contact.setShare_id(itm.getShare_id());
                contact.setName(name);
                contactsMp.put(itm.getShare_id(), contact);


        }
        return true;
    }

    public  boolean deleteDb (Item itm, int vwType)
    {
        populateContactsMp();
        contactsMp.remove(itm.getShare_id());
        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        String frndList = sharing.getString("FriendList", "NoName");
        boolean bFirst = true;
        if (frndList.equals("NoName"))
        {
            return true;
        }
        else
        {
            String[] listcomps = frndList.split(FRIENDLISTITEMSEPERATOR);
            int comps = listcomps.length;
            long share_id =0;
            for (int j=0; j < comps; ++j)
            {
                if (listcomps[j] == null || listcomps[j].length() < 1)
                    continue;
                Log.i(TAG, "decoding comp j=" + j);

                    String[] shareIdArr = listcomps[j].split(FRIENDLISTTOKENSEPERATOR);
                    share_id = Long.parseLong(shareIdArr[0]);
                if (share_id == itm.getShare_id())
                    continue;
                if (bFirst)
                {
                    bFirst = false;
                    frndList = shareIdArr[0];
                }
                else
                {
                    frndList += shareIdArr[0];
                }
                frndList += FRIENDLISTTOKENSEPERATOR;
                frndList += shareIdArr[1];
                frndList += FRIENDLISTITEMSEPERATOR;

            }
            storeFriendList("com.rekhaninan.autospree" , frndList);
            storeFriendList("com.rekhaninan.openhouses" , frndList);
            storeFriendList("com.rekhaninan.easygroclist" , frndList);
        }
        return true;
    }

    private void storeFriendList(String pkgname, String friendList)
    {
        try
        {
            Context con = ctxt.createPackageContext(pkgname, 0);//first app package name is "com.sharedpref1"
            if (con == null)
            {
                Log.i(TAG, "Cannot obtain context pkgname " + pkgname + " not installed?");
                return ;
            }
            SharedPreferences pref = con.getSharedPreferences(
                    "Sharing", Context.MODE_PRIVATE);
            if (pref == null)
            {
                Log.i(TAG, "Cannot obtain SharedPreferences  " + pkgname + " not installed?");
                return;
            }

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("FriendList", friendList);
            editor.apply();
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e("Not data shared", e.toString(), e);
        }
    }

    public class ContactsDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Contacts.db";
        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE Contacts (name TEXT, share_id INTEGER PRIMARY KEY, nickname TEXT, email TEXT, phoneno INTEGER)";
        private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS Contacts";

        public ContactsDbHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(SQL_CREATE_ENTRIES);

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);

            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
