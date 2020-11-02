package com.rekhaninan.common;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
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



    public List<Item> getMainViewLst()
    {
        try {
            String column_names[] = {"name",  "share_id"};
            Cursor c =  contactsDB.query(contactsDbHelper.DATABASE_NAME, column_names, null, null, null, null, null);
            boolean suceed = c.moveToFirst();
            List<Item> mainVwLst =  new ArrayList<Item>();
            while (suceed)
            {
                Item list = new Item();
                list.setName(c.getString(c.getColumnIndexOrThrow("name")));
                list.setShare_id(c.getLong(c.getColumnIndexOrThrow("share_id")));
                if (list.getName().equals("ME"))
                {
                    mainVwLst.add(0, list);
                }
                else {
                    mainVwLst.add(list);
                }
                suceed = c.moveToNext();
            }

            c.close();

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
        try
        {
            if (vwType == CONTACTS_ITEM_ADD || vwType == CONTACTS_ITEM_ADD_NOVWTYP) {
            if (itm.getName().equals("ME") && vwType == CONTACTS_ITEM_ADD)
            {
                Log.d(TAG, "Invalid name ME for contacts");
                return  false;
            }
            String name = Long.toString(itm.getShare_id());
            if (itm.getName() != null && itm.getName().length() > 0) {
                name = itm.getName();
            }

            ContentValues values  = new ContentValues();;

            values.put("name", name);
            values.put("share_id", itm.getShare_id());

            contactsDB.insert(contactsDbHelper.DATABASE_NAME, null, values);
            }

        }
        catch (NumberFormatException e)
        {
           Log.i(TAG, "Caught NumberFormatException exception" + e.getMessage());
        }
        catch (Exception e)
        {
        Log.i(TAG, "Caught Exception exception" + e.getMessage());
        }

        return true;
    }

    public boolean deleteAll()
    {
        contactsDB.delete(contactsDbHelper.DATABASE_NAME, null , null);
        return true;
    }

    public  boolean deleteDb (Item itm, int vwType)
    {

        contactsDB.delete(contactsDbHelper.DATABASE_NAME,  "share_id = ?" , new String[]{Long.toString(itm.getShare_id())});
        return true;
    }



    public class ContactsDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Contacts.db";
        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE Contacts (name TEXT, share_id INTEGER PRIMARY KEY(share_id))";
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
