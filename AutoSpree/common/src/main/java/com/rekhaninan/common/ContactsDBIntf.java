package com.rekhaninan.common;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.rekhaninan.common.Constants.CONTACTS_ITEM_ADD;
import static com.rekhaninan.common.Constants.FRIENDLISTITEMSEPERATOR;
import static com.rekhaninan.common.Constants.FRIENDLISTTOKENSEPERATOR;

/**
 * Created by ninanthomas on 2/9/17.
 */

public class ContactsDBIntf {


    private SQLiteDatabase contactsDB;
    private ContactsDbHelper contactsDbHelper;
    private Context ctxt;

    private final String TAG = "ContactsDBIntf";

    public ContactsDBIntf() {

            Log.i (TAG, "Constructing ContactsDBIntf");

    }

    public void initDb(Context ctx) {
        ctxt = ctx;
        contactsDbHelper = new ContactsDbHelper(ctxt);
        contactsDB = contactsDbHelper.getWritableDatabase();
        return;
    }

    public List<Item> getMainViewLst()
    {
        try {
            String column_names[] = {"name" , "share_id", "nickname", "phoneno", "email"};
            Cursor c =  contactsDB.query("Contacts", column_names, null, null, null, null, null);
            boolean suceed = c.moveToFirst();
            List<Item> mainVwLst =  new ArrayList<Item>();
            while (suceed)
            {
                Item contact = new Item();
                contact.setName(c.getString(c.getColumnIndexOrThrow("name")));
                contact.setNickname(c.getString(c.getColumnIndexOrThrow("nickname")));
                contact.setEmail(c.getString(c.getColumnIndexOrThrow("email")));
                contact.setShare_id(c.getInt(c.getColumnIndexOrThrow("share_id")));
                contact.setPhoneno(c.getInt(c.getColumnIndexOrThrow("phoneno")));

                mainVwLst.add(contact);
                suceed = c.moveToNext();
            }
            c.close();
            return mainVwLst;
        }
        catch(Exception e)
        {
            Log.e("Error", "Error", e);
        }

        return  null;
    }

    public  boolean insertDb (Item itm, int vwType)
    {
        if (vwType == CONTACTS_ITEM_ADD) {
            ContentValues values = new ContentValues();
            values.put("share_id", itm.getShare_id());
            if (itm.getName() != null && itm.getName().length() > 0) {
                values.put("name", itm.getName());
                contactsDB.insert("Contacts", null, values);
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
                frndList += itm.getName();
                frndList += FRIENDLISTITEMSEPERATOR;
                SharedPreferences.Editor editor = sharing.edit();
                editor.putString("FriendList", frndList);
                editor.commit();
            }
        }
        return true;
    }

    public  boolean deleteDb (Item itm, int vwType)
    {

        String dbName = "Contacts";
        contactsDB.delete(dbName,  "share_id = ?" , new String[]{Long.toString(itm.getShare_id())});
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
        }
        return true;
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
