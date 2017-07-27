package com.rekhaninan.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_EDIT_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_EDIT_ITEM;

/**
 * Created by nin234 on 8/1/16.
 */
public class OpenHousesDBIntf extends DBInterface {

    private SQLiteDatabase ophousesDB;
    private OpenHousesDbHelper ophousesDbHelper;
    private final String TAG = "OpenHousesDBIntf";
    private EasyGrocListDBIntf easyGrocListDBIntf;

    public OpenHousesDBIntf()
    {
        Log.d(getClass().getSimpleName(), "In OpenHousesDBIntf constructor");
        easyGrocListDBIntf = new EasyGrocListDBIntf();
        setSortString("album_name ASC");

    }

    public List<Item> getTemplNameLst()
    {
        return easyGrocListDBIntf.getTemplNameLst();
    }

    public List<Item> getTemplList(String name)
    {
        return easyGrocListDBIntf.getTemplList(name);
    }

    public List<Item> getList(String name)
    {
        return easyGrocListDBIntf.getList(name);
    }

    private void getContentValues(HashMap<String, String> keyvals, ContentValues values)
    {
        for (HashMap.Entry<String, String> entry :keyvals.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
            if (value == null) {
                values.putNull(key);
                continue;
            }
            if (key.equals("beds")) {
                double beds = Double.parseDouble(value);
                values.put(key, beds);

            } else if (key.equals("baths")) {
                double baths = Double.parseDouble(value);
                values.put(key, baths);

            } else if (key.equals("price")) {
                double price = Double.parseDouble(value);
                values.put(key, price);
            } else if (key.equals("year")) {
                int year = Integer.parseInt(value);
                values.put(key, year);
            } else if (key.equals("latitude")) {
                double latitude = Double.parseDouble(value);
                values.put(key, latitude);
            } else if (key.equals("longitude")) {
                double longitude = Double.parseDouble(value);
                values.put(key, longitude);
            }
            else if (key.equals("ratings")) {
                int ratings = Integer.parseInt(value);
                values.put(key, ratings);
            }
            else {

                values.put(key, value);
            }
        } catch (NumberFormatException e) {
                Log.i(TAG, "Caught NumberFormatException exception" + e.getMessage());
                values.putNull(key);
            }
            catch (Exception e)
            {
                Log.i(TAG, "Caught Exception exception" + e.getMessage());
                values.putNull(key);
            }


            Log.d(getClass().getSimpleName(), "Insert/Update into ContentValues key=" + key +" value=" +value);
            // ...
        }
        return;
    }

    private void populateKeyVals(HashMap<String, String> keyvals, Item itm)
    {
        keyvals.put("name", itm.getName());
        keyvals.put("area", String.valueOf(itm.getArea()));
        keyvals.put("beds", String.valueOf(itm.getBeds()));
        keyvals.put("baths", String.valueOf(itm.getBaths()));
        keyvals.put("year", String.valueOf(itm.getYear()));
        keyvals.put("price", String.valueOf(itm.getPrice()));


        keyvals.put("album_name", itm.getAlbum_name());
        keyvals.put("notes", itm.getNotes());
        keyvals.put("street", itm.getStreet());
        keyvals.put("ratings", String.valueOf(itm.getRating()));
        keyvals.put("city", itm.getCity());
        keyvals.put("state", itm.getState());
        keyvals.put("zip", itm.getZip());
        keyvals.put("latitude", Double.toString(itm.getLatitude()));
        keyvals.put("longitude", Double.toString(itm.getLongitude()));
        keyvals.put("ratings", Integer.toString(itm.getRating()));
        return;
    }

    public void setAppName(String appName)
    {
        setApp_name(appName);
        easyGrocListDBIntf.setApp_name(appName);
    }

    public  boolean insertDb (Item itm, int vwType)
    {
        switch (vwType)
        {
            case EASYGROC_ADD_ITEM:
            case EASYGROC_EDIT_ITEM:
            case EASYGROC_TEMPL_ADD_ITEM:
                    return easyGrocListDBIntf.insertDb(itm, vwType);

            default:
                break;
        }
        HashMap<String, String> keyvals = new HashMap<>();
        populateKeyVals(keyvals, itm);
        ContentValues values = new ContentValues();
        getContentValues(keyvals, values);
        ophousesDB.insert("Item", null, values);
        return true;
    }

    public  boolean deleteDb (Item itm, int vwType)
    {
        switch (vwType) {

            case EASYGROC_TEMPL_DISPLAY_ITEM:
            case EASYGROC_TEMPL_EDIT_ITEM:
            case EASYGROC_EDIT_ITEM:
                return easyGrocListDBIntf.deleteDb(itm, vwType);

            default:
                break;
        }
        String dbName = "Item";
        ophousesDB.delete(dbName,  "album_name = ?" , new String[]{itm.getAlbum_name()});
        return true;
    }

    public  Item shareItemExists (Item itm, int vwType)
    {
        try {

            String column_names[] = {"name" , "street", "price", "area", "year", "beds", "baths",
                    "album_name", "notes", "latitude", "longitude",
                    "street", "city", "state", "zip", "share_name", "share_id", "ratings"
            };
            Cursor c = ophousesDB.query("Item", column_names, "share_name = ? and share_id = ?",
                    new String[]{itm.getShare_name(), Long.toString(itm.getShare_id())},
                    null, null, null);
            boolean suceed = c.moveToFirst();
            List<Item> mainVwLst =  new ArrayList<Item>();
            while (suceed)
            {
                String share_name = c.getString(c.getColumnIndexOrThrow("share_name"));
                long share_id = c.getLong(c.getColumnIndexOrThrow("share_id"));
                if (share_id == itm.getShare_id() && share_name.equals(itm.getShare_name())) {
                    Item house = new Item();
                    house.setName(c.getString(c.getColumnIndexOrThrow("name")));
                    house.setStreet(c.getString(c.getColumnIndexOrThrow("street")));
                    house.setPrice(c.getDouble(c.getColumnIndexOrThrow("price")));
                    house.setArea(c.getDouble(c.getColumnIndexOrThrow("area")));
                    house.setBeds(c.getDouble(c.getColumnIndexOrThrow("beds")));
                    house.setBaths(c.getDouble(c.getColumnIndexOrThrow("baths")));
                    house.setYear(c.getInt(c.getColumnIndexOrThrow("year")));
                    house.setAlbum_name(c.getString(c.getColumnIndexOrThrow("album_name")));
                    house.setNotes(c.getString(c.getColumnIndexOrThrow("notes")));
                    house.setLatitude(c.getDouble(c.getColumnIndexOrThrow("latitude")));
                    house.setLongitude(c.getDouble(c.getColumnIndexOrThrow("longitude")));
                    house.setStreet(c.getString(c.getColumnIndexOrThrow("street")));
                    house.setCity(c.getString(c.getColumnIndexOrThrow("city")));
                    house.setState(c.getString(c.getColumnIndexOrThrow("state")));
                    house.setZip(c.getString(c.getColumnIndexOrThrow("zip")));
                    house.setShare_name(c.getString(c.getColumnIndexOrThrow("share_name")));
                    house.setShare_id(c.getLong(c.getColumnIndexOrThrow("share_id")));
                    house.setRating(c.getInt(c.getColumnIndexOrThrow("ratings")));
                    c.close();
                    return house;
                }

            }
            c.close();


        }
        catch(Exception e)
        {
            Log.e("Error", "Error", e);
        }
        return null;
    }

    public  boolean itemExists (Item itm, int vwType)
    {
        if (vwType == EASYGROC_TEMPL_ADD_ITEM)
        {
            return easyGrocListDBIntf.itemExists(itm, vwType);
        }
        return true;
    }

    public boolean updateDb (Item itm, int vwType)
    {
        HashMap<String, String> keyvals = new HashMap<>();
        populateKeyVals(keyvals, itm);

        ContentValues values = new ContentValues();
        getContentValues(keyvals, values);
        String album_name = keyvals.get("album_name");

        ophousesDB.update("Item", values, "album_name = ?" , new String[]{album_name});
        return true;
    }

    public void initDb(Context ctxt)
    {
        ophousesDbHelper = new OpenHousesDbHelper(ctxt);
        ophousesDB = ophousesDbHelper.getWritableDatabase();
        easyGrocListDBIntf.initDb(ctxt);
        return;
    }

    public List<Item> getMainViewLst()
    {
        try {

            String column_names[] = {"name" , "street", "price", "area", "year", "beds", "baths",
                    "album_name", "notes", "latitude", "longitude",
                    "street", "city", "state", "zip", "share_name", "share_id", "ratings"
                           };
            Cursor c =  ophousesDB.query("Item", column_names, null, null, null, null, getSortString());
            boolean suceed = c.moveToFirst();
            List<Item> mainVwLst =  new ArrayList<Item>();
            while (suceed)
            {
                Item house = new Item();
                house.setName(c.getString(c.getColumnIndexOrThrow("name")));
                house.setStreet(c.getString(c.getColumnIndexOrThrow("street")));
                house.setPrice(c.getDouble(c.getColumnIndexOrThrow("price")));
                house.setArea(c.getDouble(c.getColumnIndexOrThrow("area")));
                house.setBeds(c.getDouble(c.getColumnIndexOrThrow("beds")));
                house.setBaths(c.getDouble(c.getColumnIndexOrThrow("baths")));
                house.setYear(c.getInt(c.getColumnIndexOrThrow("year")));
                house.setAlbum_name(c.getString(c.getColumnIndexOrThrow("album_name")));
                house.setNotes(c.getString(c.getColumnIndexOrThrow("notes")));
                house.setLatitude(c.getDouble(c.getColumnIndexOrThrow("latitude")));
                house.setLongitude(c.getDouble(c.getColumnIndexOrThrow("longitude")));
                house.setStreet(c.getString(c.getColumnIndexOrThrow("street")));
                house.setCity(c.getString(c.getColumnIndexOrThrow("city")));
                house.setState(c.getString(c.getColumnIndexOrThrow("state")));
                house.setZip(c.getString(c.getColumnIndexOrThrow("zip")));
                house.setShare_name(c.getString(c.getColumnIndexOrThrow("share_name")));
                house.setShare_id(c.getLong(c.getColumnIndexOrThrow("share_id")));
                house.setRating(c.getInt(c.getColumnIndexOrThrow("ratings")));

                mainVwLst.add(house);
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

    public class OpenHousesDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "OpenHouses.db";
        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE Item (album_name TEXT PRIMARY KEY, area REAL, baths REAL, beds REAL," +
                "city TEXT, country TEXT, latitude REAL, longitude REAL, name TEXT, notes TEXT, pic_cnt INTEGER, price REAL," +
                "state TEXT, str1 TEXT, str2 TEXT, str3 TEXT, street TEXT, val1 REAL, val2 REAL, year INTEGER," +
                "share_id INTEGER, share_name TEXT,  zip TEXT, ratings INTEGER) ";
        private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS Item";

        public OpenHousesDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
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
