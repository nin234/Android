package com.rekhaninan.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_EDIT_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_EDIT_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_NAME_ADD_ITEM;

/**
 * Created by nin234 on 8/1/16.
 */
public class EasyGrocListDBIntf extends DBInterface {

    private SQLiteDatabase egrocDB;
    private EasyGrocDbHelper egrocDbHelper;

    private final String TAG = "EasyGrocListDBInt";


    public EasyGrocListDBIntf ()
    {
        System.out.println("In EasyGrocListDBIntf constructor");
    }

    public void initDb(Context ctxt)
    {
        egrocDbHelper = new EasyGrocDbHelper(ctxt);
        egrocDB = egrocDbHelper.getWritableDatabase();
        return;
    }




    private void getContentValues(ContentValues values, Item itm)
    {
        try {
            values.put("name", itm.getName());
            values.put("rowno", itm.getRowno());
            values.put ("item", itm.getItem());
            values.put("start_month", itm.getStart_month());
            values.put("end_month", itm.getEnd_month());
            values.put("inventory", itm.getInventory());
        } catch (NumberFormatException e) {
            Log.i(TAG, "Caught NumberFormatException exception" + e.getMessage());

        } catch (Exception e) {
            Log.i(TAG, "Caught Exception exception" + e.getMessage());

        }
        return;
    }

    private void getNameContentValues(ContentValues values, Item itm)
    {
        try {
            values.put("name", itm.getName());
            values.put("share_name", itm.getShare_name());
            values.put("share_id", itm.getShare_id());

        } catch (NumberFormatException e) {
            Log.i(TAG, "Caught NumberFormatException exception" + e.getMessage());

        } catch (Exception e) {
            Log.i(TAG, "Caught Exception exception" + e.getMessage());

        }
        return;
    }

    private void getListNameContentValues(ContentValues values, Item itm)
    {
        try {
            values.put("name", itm.getName());
            values.put("share_name", itm.getShare_name());
            values.put("share_id", itm.getShare_id());
            if (itm.getPicurl() != null && itm.getPicurl().length() > 0)
            {
                values.put("picurl", itm.getPicurl());
            }
            values.put("date", itm.getDate());
            values.put("current", itm.getCurrent());

        } catch (NumberFormatException e) {
            Log.i(TAG, "Caught NumberFormatException exception" + e.getMessage());

        } catch (Exception e) {
            Log.i(TAG, "Caught Exception exception" + e.getMessage());

        }
        return;
    }
    private void getListContentValues(ContentValues values, Item itm)
    {
        try {
            values.put("name", itm.getName());
            values.put("rowno", itm.getRowno());
            values.put ("item", itm.getItem());
            int hidden=0;
            if (itm.isSelected())
                hidden =1;
            values.put("hidden", hidden);
            values.put("date", itm.getDate());

        } catch (NumberFormatException e) {
            Log.i(TAG, "Caught NumberFormatException exception" + e.getMessage());

        } catch (Exception e) {
            Log.i(TAG, "Caught Exception exception" + e.getMessage());

        }
        return;
    }

    public  boolean insertDb (Item itm, int vwType)
    {
        try {
            HashMap<String, String> keyvals = new HashMap<>();
            String dbName = "NoName";
            String dbName1 = "NoName";
            ContentValues nameValue = new ContentValues();
            ContentValues values = new ContentValues();
            switch (vwType) {
                case EASYGROC_TEMPL_ADD_ITEM:
                case EASYGROC_TEMPL_EDIT_ITEM:
                    dbName = "MasterList";
                    dbName1 = "MasterListNames";
                    getContentValues(values, itm);
                    getNameContentValues(nameValue, itm);
                    break;

                case EASYGROC_ADD_ITEM:
                case EASYGROC_EDIT_ITEM:
                    dbName = "List";
                    dbName1 = "ListNames";
                    getListContentValues(values, itm);
                    getListNameContentValues(nameValue, itm);
                    break;
                case EASYGROC_TEMPL_NAME_ADD_ITEM:
                    getListNameContentValues(nameValue, itm);
                    egrocDB.insert("MasterListNames", null, nameValue);
                    return true;

                default:
                    break;
            }



            egrocDB.insert(dbName, null, values);
            if (!itemExists(itm, vwType)) {
                egrocDB.insert(dbName1, null, nameValue);
            }

            Log.i (TAG, "Inserting to dbs " + dbName + " " + dbName1 + " Item name=" + itm.getName() + " item=" + itm.getItem()
                    + " rowno=" + itm.getRowno());
        }
        catch(Exception e)
        {
            Log.e("Error", "Error", e);
        }

        return true;
    }

    public  boolean deleteDb (Item itm, int vwType)
    {
        String dbName = "NoName";
        String dbName1 = "NoName";

        switch (vwType) {
            case EASYGROC_TEMPL_ADD_ITEM:
            case EASYGROC_TEMPL_EDIT_ITEM:
            case EASYGROC_TEMPL_DISPLAY_ITEM:
            case EASYGROC_ADD_ITEM:
                dbName = "MasterList";
                dbName1 = "MasterListNames";
                break;

            case EASYGROC_EDIT_ITEM:
            case EASYGROC_DISPLAY_ITEM:
                dbName = "List";
                dbName1 = "ListNames";
                break;

            default:
                break;
        }
        egrocDB.delete(dbName,  "name = ?" , new String[]{itm.getName()});
        egrocDB.delete(dbName1,  "name = ?" , new String[]{itm.getName()});

        return true;
    }

    public  Item shareItemExists (Item itm, int vwType)
    {
        String column_names[] = {"name", "share_name", "share_id", "pic_url" };

        switch (vwType) {

            case EASYGROC_ADD_ITEM:
            case EASYGROC_EDIT_ITEM: {
                Cursor c = egrocDB.query("ListNames", column_names, "share_name = ? and share_id = ?",
                        new String[]{itm.getShare_name(), Long.toString(itm.getShare_id())},
                        null, null, null);
                boolean suceed = c.moveToFirst();

                while (suceed)
                {
                    String share_name = c.getString(c.getColumnIndexOrThrow("share_name"));
                    long share_id = c.getLong(c.getColumnIndexOrThrow("share_id"));
                    if (share_id == itm.getShare_id() && share_name.equals(itm.getShare_name())) {
                        Item list = new Item();
                        list.setName(c.getString(c.getColumnIndexOrThrow("name")));
                        list.setShare_name(share_name);
                        list.setPicurl(c.getString(c.getColumnIndexOrThrow("picurl")));
                        list.setShare_id(share_id);
                        c.close();
                        return list;
                    }
                    suceed = c.moveToNext();
                }
                c.close();
            }
            break;

            case EASYGROC_TEMPL_ADD_ITEM:
            case EASYGROC_TEMPL_EDIT_ITEM:
            {
                Cursor c = egrocDB.query("MasterListNames", column_names, "share_name = ? and share_id = ?",
                        new String[]{itm.getShare_name(), Long.toString(itm.getShare_id())},
                        null, null, null);
                boolean suceed = c.moveToFirst();

                while (suceed)
                {
                    String share_name = c.getString(c.getColumnIndexOrThrow("share_name"));
                    long share_id = c.getLong(c.getColumnIndexOrThrow("share_id"));
                    if (share_id == itm.getShare_id() && share_name.equals(itm.getShare_name())) {
                        Item list = new Item();
                        list.setName(c.getString(c.getColumnIndexOrThrow("name")));
                        list.setShare_name(share_name);
                        list.setShare_id(share_id);
                        c.close();
                        return list;
                    }
                    suceed = c.moveToNext();
                }
                c.close();
            }
            break;

            default:
                break;
        }
        return null;
    }

    public  boolean itemExists (Item itm, int vwType)
    {
        String column_names[] = {"name"};

        switch (vwType) {
            case EASYGROC_TEMPL_ADD_ITEM:
            case EASYGROC_TEMPL_EDIT_ITEM: {
                Cursor c = egrocDB.query("MasterListNames", column_names, "name = ?", new String[]{itm.getName()}, null, null, null);
                if (c.getCount() == 0)
                    return false;
                else
                    return true;
            }
            case EASYGROC_ADD_ITEM:
            case EASYGROC_EDIT_ITEM: {
                Cursor c = egrocDB.query("ListNames", column_names, "name = ?", new String[]{itm.getName()}, null, null, null);
                if (c.getCount() == 0)
                    return false;
                else
                    return true;
            }

            default:
                break;
        }
        return false;
    }


    public boolean updateDb (Item itm, int vwType)
    {
        ContentValues values = new ContentValues();
        String dbName = "NoName";
        switch (vwType)
        {
            case EASYGROC_TEMPL_DISPLAY_ITEM:
                dbName = "MasterList";
                values.put("inventory", itm.getInventory());
                break;

            default:
                break;
        }
        egrocDB.update(dbName, values, "name = ?", new String[]{itm.getName()});
        return true;
    }

    public List<Item> getMainViewLst()
    {
        try {

            String column_names[] = {"name", "picurl", "share_name", "share_id"};
            Cursor c =  egrocDB.query("ListNames", column_names, null, null, null, null, null);
            boolean suceed = c.moveToFirst();
            List<Item> mainVwLst =  new ArrayList<Item>();
            while (suceed)
            {
                Item list = new Item();
                list.setName(c.getString(c.getColumnIndexOrThrow("name")));
                list.setShare_name(c.getString(c.getColumnIndexOrThrow("share_name")));
                list.setPicurl(c.getString(c.getColumnIndexOrThrow("picurl")));
                list.setShare_id(c.getLong(c.getColumnIndexOrThrow("share_id")));
                mainVwLst.add(list);
                suceed = c.moveToNext();
            }

            c.close();
            Collections.reverse(mainVwLst);
            return mainVwLst;

        }
        catch(Exception e)
        {
            Log.e("Error", "Error", e);
        }

        return  null;
    }

    public List<Item> getTemplNameLst()
    {
        try {

            String column_names[] = {"name", "share_name", "share_id"};
            Cursor c =  egrocDB.query("MasterListNames", column_names, null, null, null, null, null);
            boolean suceed = c.moveToFirst();
            List<Item> templNameLst =  new ArrayList<Item>();
            while (suceed)
            {
                Item list = new Item();
                list.setName(c.getString(c.getColumnIndexOrThrow("name")));
                list.setShare_name(c.getString(c.getColumnIndexOrThrow("share_name")));
                list.setShare_id(c.getLong(c.getColumnIndexOrThrow("share_id")));
                templNameLst.add(list);
                suceed = c.moveToNext();
            }
            c.close();
            return templNameLst;

        }
        catch(Exception e)
        {
            Log.e("Error", "Error", e);
        }

        return null;
    }

    public List<Item> getList(String name)
    {
        try {

            String column_names[] = {"rowno", "item", "hidden", "date"};
            Cursor c =  egrocDB.query("List", column_names, "name = ?" , new String[]{name}, null, null, null);
            boolean suceed = c.moveToFirst();
            List<Item> list =  new ArrayList<Item>();
            while (suceed)
            {
                Item itm = new Item();
                itm.setName(name);
                itm.setRowno(c.getInt(c.getColumnIndexOrThrow("rowno")));
                itm.setItem(c.getString(c.getColumnIndexOrThrow("item")));
                int hidden = c.getInt(c.getColumnIndexOrThrow("hidden"));
                itm.setSelected(hidden != 0);
                itm.setDate(c.getInt(c.getColumnIndexOrThrow("date")));
                list.add(itm);
                suceed = c.moveToNext();
            }
            c.close();
            return list;

        }
        catch(Exception e)
        {
            Log.e("Error", "Error", e);
        }

        return null;
    }


    public List<Item> getTemplList(String name)
    {
        try {

            String column_names[] = {"rowno", "item", "start_month", "end_month", "inventory"};
            String orderBy = "rowno" + " DESC";
            Cursor c =  egrocDB.query("MasterList", column_names, "name = ?" , new String[]{name}, null, null, orderBy);
            boolean suceed = c.moveToFirst();
            List<Item> list =  new ArrayList<Item>();
            while (suceed)
            {
                Item itm = new Item();
                itm.setName(name);
                itm.setRowno(c.getInt(c.getColumnIndexOrThrow("rowno")));
                itm.setItem(c.getString(c.getColumnIndexOrThrow("item")));
                itm.setStart_month(c.getInt(c.getColumnIndexOrThrow("start_month")));
                itm.setEnd_month(c.getInt(c.getColumnIndexOrThrow("end_month")));
                itm.setInventory(c.getInt(c.getColumnIndexOrThrow("inventory")));
                itm.setHidden(false);
                list.add(itm);
                suceed = c.moveToNext();
            }
            c.close();
            return list;

        }
        catch(Exception e)
        {
            Log.e("Error", "Error", e);
        }

        return null;
    }

    public class EasyGrocDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "EasyGroc.db";
        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE ListNames (picurl TEXT, name TEXT PRIMARY KEY, share_name TEXT, share_id INTEGER, date INTEGER, current INTEGER)";
        private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ListNames";
        private static final String SQL_CREATE_ENTRIES_1 = "CREATE TABLE List (name TEXT, rowno INTEGER, item TEXT, hidden INTEGER, date INTEGER)";
        private static final String SQL_DELETE_ENTRIES_1 = "DROP TABLE IF EXISTS List";
        private static final String SQL_CREATE_ENTRIES_2 = "CREATE TABLE MasterListNames (name TEXT PRIMARY KEY, share_name TEXT, share_id INTEGER)";
        private static final String SQL_DELETE_ENTRIES_2 = "DROP TABLE IF EXISTS MasterListNames";
        private static final String SQL_CREATE_ENTRIES_3 = "CREATE TABLE MasterList (name TEXT, rowno INTEGER, item TEXT, start_month INTEGER, end_month INTEGER, inventory INTEGER)";
        private static final String SQL_DELETE_ENTRIES_3 = "DROP TABLE IF EXISTS MasterList";

        public EasyGrocDbHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(SQL_CREATE_ENTRIES);
            db.execSQL(SQL_CREATE_ENTRIES_1);
            db.execSQL(SQL_CREATE_ENTRIES_2);
            db.execSQL(SQL_CREATE_ENTRIES_3);


        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            db.execSQL(SQL_DELETE_ENTRIES_1);
            db.execSQL(SQL_DELETE_ENTRIES_2);
            db.execSQL(SQL_DELETE_ENTRIES_3);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
