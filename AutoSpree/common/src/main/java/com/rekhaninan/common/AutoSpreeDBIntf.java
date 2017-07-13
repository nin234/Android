package com.rekhaninan.common;

import android.content.ContentValues;
import android.util.Log;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_EDIT_ITEM;

/**
 * Created by nin234 on 8/1/16.
 */
public class AutoSpreeDBIntf extends DBInterface {

    private SQLiteDatabase aspreeDB;
    private AutoSpreeDbHelper aspreeDbHelper;
    private final String TAG = "AutoSpreeDBIntf";
    private EasyGrocListDBIntf easyGrocListDBIntf;

    public AutoSpreeDBIntf ()
    {

        Log.d(getClass().getSimpleName(), "In AutoSpreeDBIntf constructor");
        easyGrocListDBIntf= new EasyGrocListDBIntf();
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
            String  value = entry.getValue();
            Log.d(getClass().getSimpleName(), "Inserting/Updating into ContentValues key=" + key +" value=" +value);
           try {

               if (value == null) {
                   Log.d(TAG, "Putting null value for key=" + key);
                   values.putNull(key);
                   continue;
               }

               if (key.equals("miles")) {
                   Log.d(TAG, "Inserting miles value");
                   int miles = 0;
                   miles = Integer.parseInt(value);
                   values.put(key, miles);
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
           }
           catch (NumberFormatException e)
           {
               Log.i(TAG, "Caught NumberFormatException exception" + e.getMessage());
               values.putNull(key);
           }
           catch (Exception e)
           {
               Log.i(TAG, "Caught Exception exception" + e.getMessage());
               values.putNull(key);
           }

        }
        return;
    }

    public  boolean deleteDb (Item itm, int vwType)
    {
        switch (vwType) {

            case EASYGROC_TEMPL_DISPLAY_ITEM:
            case EASYGROC_TEMPL_EDIT_ITEM:
                return easyGrocListDBIntf.deleteDb(itm, vwType);

            default:
                break;
        }
        String dbName = "Item";
        aspreeDB.delete(dbName,  "album_name = ?" , new String[]{itm.getAlbum_name()});
        return true;
    }

    private void populateKeyVals(HashMap<String, String> keyvals, Item itm)
    {
        keyvals.put("make", itm.getMake());
        keyvals.put("color", itm.getColor());
        keyvals.put("model", itm.getModel());
        keyvals.put("name", itm.getName());
        keyvals.put("year", String.valueOf(itm.getYear()));
        keyvals.put("ratings", String.valueOf(itm.getRating()));
        keyvals.put("price", String.valueOf(itm.getPrice()));
        keyvals.put("miles", String.valueOf(itm.getMiles()));

        keyvals.put("album_name", itm.getAlbum_name());
        keyvals.put("notes", itm.getNotes());
        keyvals.put("street", itm.getStreet());
        keyvals.put("city", itm.getCity());
        keyvals.put("state", itm.getState());
        keyvals.put("zip", itm.getZip());
        keyvals.put("latitude", Double.toString(itm.getLatitude()));
        keyvals.put("longitude", Double.toString(itm.getLongitude()));
        keyvals.put("ratings", Integer.toString(itm.getRating()));
        return;
    }

    public  boolean insertDb (Item itm, int vwType)
    {
        switch (vwType)
        {
            case EASYGROC_ADD_ITEM:
            case EASYGROC_TEMPL_ADD_ITEM:
                return easyGrocListDBIntf.insertDb(itm, vwType);

            default:
                break;
        }
        HashMap<String, String> keyvals = new HashMap<>();
        populateKeyVals(keyvals, itm);
        ContentValues values = new ContentValues();

        getContentValues(keyvals, values);

        Log.d(getClass().getSimpleName(),"Inserting values into Item");
        aspreeDB.insert("Item", null, values);
        return true;
    }

    public  boolean itemExists (Item itm, int vwType)
    {

        return true;
    }

    public  Item shareItemExists (Item itm, int vwType)
    {

        try {

            String column_names[] = {"name" , "color", "model", "make", "year", "price",
                    "miles", "album_name", "notes", "latitude", "longitude",
                    "street", "city", "state", "zip", "share_name", "share_id", "ratings" };
            Cursor c = aspreeDB.query("Item", column_names, "share_name = ? and share_id = ?",
                    new String[]{itm.getShare_name(), Long.toString(itm.getShare_id())},
                    null, null, null);
            boolean suceed = c.moveToFirst();
            while (suceed)
            {
                String share_name = c.getString(c.getColumnIndexOrThrow("share_name"));
                long share_id = c.getLong(c.getColumnIndexOrThrow("share_id"));
                if (share_id == itm.getShare_id() && share_name.equals(itm.getShare_name())) {
                    Item car = new Item();
                    car.setName(c.getString(c.getColumnIndexOrThrow("name")));
                    car.setColor(c.getString(c.getColumnIndexOrThrow("color")));
                    car.setModel(c.getString(c.getColumnIndexOrThrow("model")));
                    car.setMake(c.getString(c.getColumnIndexOrThrow("make")));
                    car.setYear(c.getInt(c.getColumnIndexOrThrow("year")));
                    car.setPrice(c.getDouble(c.getColumnIndexOrThrow("price")));
                    car.setMiles(c.getInt(c.getColumnIndexOrThrow("miles")));
                    car.setAlbum_name(c.getString(c.getColumnIndexOrThrow("album_name")));
                    car.setNotes(c.getString(c.getColumnIndexOrThrow("notes")));
                    car.setLatitude(c.getDouble(c.getColumnIndexOrThrow("latitude")));
                    car.setLongitude(c.getDouble(c.getColumnIndexOrThrow("longitude")));
                    car.setStreet(c.getString(c.getColumnIndexOrThrow("street")));
                    car.setCity(c.getString(c.getColumnIndexOrThrow("city")));
                    car.setState(c.getString(c.getColumnIndexOrThrow("state")));
                    car.setZip(c.getString(c.getColumnIndexOrThrow("zip")));
                    car.setShare_name(c.getString(c.getColumnIndexOrThrow("share_name")));
                    car.setShare_id(c.getLong(c.getColumnIndexOrThrow("share_id")));
                    car.setRating(c.getInt(c.getColumnIndexOrThrow("ratings")));
                    c.close();
                    return car;
                }


            }
            c.close();
        }
        catch(Exception e)
        {
            Log.e("Error", "Exception getting MainVwLst", e);
        }

        return null;
    }



    public boolean updateDb (Item itm, int vwType)
    {
        HashMap<String, String> keyvals = new HashMap<>();
        populateKeyVals(keyvals, itm);
        ContentValues values = new ContentValues();
        getContentValues(keyvals, values);
        String album_name = keyvals.get("album_name");
        Log.d(TAG, "Updating db album_name=" + album_name);
        aspreeDB.update("Item", values, "album_name = ?" , new String[]{album_name});
        return true;
    }

   public void initDb(Context ctxt)
    {
        Log.d(getClass().getSimpleName(), "initDb");
        aspreeDbHelper = new AutoSpreeDbHelper(ctxt);
        aspreeDB = aspreeDbHelper.getWritableDatabase();
        easyGrocListDBIntf.initDb(ctxt);
    }

   public List<Item> getMainViewLst()
    {
        Log.d(getClass().getSimpleName(), "getMainViewLst");
        try {

            String column_names[] = {"name" , "color", "model", "make", "year", "price",
                    "miles", "album_name", "notes", "latitude", "longitude",
                    "street", "city", "state", "zip", "share_name", "share_id" , "ratings"};
           Cursor c =  aspreeDB.query("Item", column_names, null, null, null, null, getSortString());
           boolean suceed = c.moveToFirst();
            List<Item> mainVwLst =  new ArrayList<Item>();
            while (suceed)
            {
                Item car = new Item();
                car.setName(c.getString(c.getColumnIndexOrThrow("name")));
                car.setColor(c.getString(c.getColumnIndexOrThrow("color")));
                car.setModel(c.getString(c.getColumnIndexOrThrow("model")));
                car.setMake(c.getString(c.getColumnIndexOrThrow("make")));
                car.setYear(c.getInt(c.getColumnIndexOrThrow("year")));
                car.setPrice(c.getDouble(c.getColumnIndexOrThrow("price")));
                car.setMiles(c.getInt(c.getColumnIndexOrThrow("miles")));
                car.setAlbum_name(c.getString(c.getColumnIndexOrThrow("album_name")));
                car.setNotes(c.getString(c.getColumnIndexOrThrow("notes")));
                car.setLatitude(c.getDouble(c.getColumnIndexOrThrow("latitude")));
                car.setLongitude(c.getDouble(c.getColumnIndexOrThrow("longitude")));
                car.setStreet(c.getString(c.getColumnIndexOrThrow("street")));
                car.setCity(c.getString(c.getColumnIndexOrThrow("city")));
                car.setState(c.getString(c.getColumnIndexOrThrow("state")));
                car.setZip(c.getString(c.getColumnIndexOrThrow("zip")));
                car.setShare_name(c.getString(c.getColumnIndexOrThrow("share_name")));
                car.setShare_id(c.getLong(c.getColumnIndexOrThrow("share_id")));
                car.setRating(c.getInt(c.getColumnIndexOrThrow("ratings")));

                mainVwLst.add(car);
                suceed = c.moveToNext();
            }
            c.close();
            return mainVwLst;

        }
        catch(Exception e)
        {
            Log.e("Error", "Exception getting MainVwLst", e);
        }
        return null;
    }

    public class AutoSpreeDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "AutoSpree.db";
        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE Item (album_name TEXT PRIMARY KEY, color TEXT, model TEXT, make TEXT," +
                "city TEXT, country TEXT, latitude REAL, longitude REAL, name TEXT, notes TEXT, pic_cnt INTEGER, price REAL," +
                "state TEXT, str1 TEXT, str2 TEXT, str3 TEXT, street TEXT, val1 REAL, val2 REAL, year INTEGER, miles INTEGER, " +
                " share_id INTEGER, share_name TEXT, zip TEXT, ratings INTEGER) ";
        private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS Item";

        public AutoSpreeDbHelper(Context context) {
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
