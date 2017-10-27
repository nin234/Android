package com.rekhaninan.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.rekhaninan.common.Constants.CONTACTS_ITEM_ADD;

/**
 * Created by ninanthomas on 10/26/17.
 */

public class ShareDBIntf {

    private SQLiteDatabase shareDB;
    private ShareDbHelper shareDbHelper;
    private final String TAG = "ShareDBIntf";


    public boolean insertItem(ByteBuffer item)
    {
        ContentValues values = new ContentValues();
        values.put("item", item.array());
        values.put("time", System.currentTimeMillis());
        shareDB.insert("Items", null, values);
        return true;
    }

    public  boolean deleteItem() {
        Log.d(TAG, "deleteItem");
        try {

            String column_names[] = {"item", "time"};
            Cursor c = shareDB.query("Items", column_names, null, null, null, null, "time DESC");
            boolean suceed = c.moveToFirst();

            if (suceed) {

                long time = c.getLong(c.getColumnIndexOrThrow("time"));
                c.close();
                Log.i(TAG, "Deleting db row from Items time=" + time);
                shareDB.delete("Items",  "time = ?" , new String[]{Long.toString(time)});
            }
            else {
                c.close();
            }


        }
        catch(Exception e)
        {
            Log.e("Error", "Exception deleteItem ", e);
        }
        return true;
    }

    public  boolean insertImagesMetaData (String metaData)
    {
            ContentValues values = new ContentValues();

            if (metaData != null && metaData.length() > 0) {
                values.put("item", metaData);
                values.put("time", System.currentTimeMillis());
                shareDB.insert("ImagesMetaData", null, values);

            }

        return true;
    }

    public  boolean deleteImagesMetaData() {
        Log.d(getClass().getSimpleName(), "deleteImagesMetaData");
        try {

            String column_names[] = {"item", "time"};
            Cursor c = shareDB.query("ImagesMetaData", column_names, null, null, null, null, "time DESC");
            boolean suceed = c.moveToFirst();

            if (suceed) {

                long time = c.getLong(c.getColumnIndexOrThrow("time"));
                c.close();
                Log.i(TAG, "Deleting db row from ImagesMetaData time=" + time);
                shareDB.delete("ImagesMetaData",  "time = ?" , new String[]{Long.toString(time)});
            }
            else {
                c.close();
            }


        }
        catch(Exception e)
        {
            Log.e("Error", "Exception deleteImagesMetaData ", e);
        }
        return true;
    }

    public  boolean insertImage (String image)
    {
        ContentValues values = new ContentValues();

        if (image != null && image.length() > 0) {
            values.put("item", image);
            values.put("time", System.currentTimeMillis());
            shareDB.insert("Images", null, values);

        }

        return true;
    }

    public  boolean deleteImage() {
        Log.d(TAG, "deleteImage");
        try {

            String column_names[] = {"item", "time"};
            Cursor c = shareDB.query("Images", column_names, null, null, null, null, "time DESC");
            boolean suceed = c.moveToFirst();

            if (suceed) {

                long time = c.getLong(c.getColumnIndexOrThrow("time"));
                c.close();
                Log.i(TAG, "Deleting db row from Images time=" + time);
                shareDB.delete("Images",  "time = ?" , new String[]{Long.toString(time)});
            }
            else {
                c.close();
            }


        }
        catch(Exception e)
        {
            Log.e("Error", "Exception deleteImagesMetaData ", e);
        }
        return true;
    }

    public class ShareDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Share.db";
        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE Images (item TEXT, time INTEGER PRIMARY KEY)";
        private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS Images";
        private static final String SQL_CREATE_ENTRIES_1 = "CREATE TABLE ImagesMetaData (item TEXT, time INTEGER PRIMARY KEY)";
        private static final String SQL_DELETE_ENTRIES_1 = "DROP TABLE IF EXISTS ImagesMetaData";
        private static final String SQL_CREATE_ENTRIES_2 = "CREATE TABLE Items (item BLOB, time INTEGER PRIMARY KEY)";
        private static final String SQL_DELETE_ENTRIES_2 = "DROP TABLE IF EXISTS Items";

        public ShareDbHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(SQL_CREATE_ENTRIES);
            db.execSQL(SQL_CREATE_ENTRIES_1);
            db.execSQL(SQL_CREATE_ENTRIES_2);

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            db.execSQL(SQL_DELETE_ENTRIES_1);
            db.execSQL(SQL_DELETE_ENTRIES_2);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}
