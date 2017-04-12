package com.rekhaninan.common;

import android.content.Context;
import android.provider.Contacts;
import android.util.Log;

import java.util.HashMap;
import java.util.StringTokenizer;

import static com.rekhaninan.common.Constants.CONTACTS_ITEM_ADD;
import static com.rekhaninan.common.Constants.CONTACTS_ITEM_DISPLAY;
import static com.rekhaninan.common.Constants.CONTACTS_MAINVW;
import static com.rekhaninan.common.Constants.CONTACTS_VW;
import static com.rekhaninan.common.Constants.MAINVW;
import static com.rekhaninan.common.Constants.SHARE_MAINVW;

/**
 * Created by nin234 on 9/2/16.
 */
public class DBOperations {

    private static final DBOperations INSTANCE = new DBOperations();
    private Object dbi;
    private ContactsDBIntf contactsDBIntf;
    private String app_name;

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    private DBOperations()
    {


    }

    public static DBOperations getInstance () {

        return INSTANCE;
    }

    public void initDb(String dbClass, Context ctxt) {

        try {
            dbi = Class.forName(dbClass).newInstance();
            java.lang.reflect.Method methd;
            methd = dbi.getClass().getMethod("initDb", Context.class);
            methd.invoke(dbi, ctxt);
            contactsDBIntf = new ContactsDBIntf();
            contactsDBIntf.initDb(ctxt);
        }
        catch (Exception e)
        {
            Log.d(getClass().getName(), "Cannot find reflection class name");
            return;
        }
    }

    public java.util.List<Item>  getMainLst(int vwType) {
        try {

            switch (vwType) {

                case MAINVW:
                case SHARE_MAINVW: {
                    java.lang.reflect.Method methd;
                    methd = dbi.getClass().getMethod("getMainViewLst");
                    java.util.List<Item> mainLst = (java.util.List<Item>) methd.invoke(dbi);
                    return mainLst;
                }

                case CONTACTS_VW:
                case CONTACTS_MAINVW:
                case CONTACTS_ITEM_ADD:
                case CONTACTS_ITEM_DISPLAY:
                {
                    java.util.List<Item> mainLst = contactsDBIntf.getMainViewLst();
                    return mainLst;
                }

                default:
                    return null;

            }
        }
        catch (Exception e)
        {
            Log.d(getClass().getName(), "Cannot find reflection class name");
            return null;
        }

    }

    public java.util.List<Item>  getTemplNameLst() {
        try {

            java.lang.reflect.Method methd;
            methd = dbi.getClass().getMethod("getTemplNameLst");
            java.util.List<Item> mainLst = (java.util.List<Item>) methd.invoke(dbi);
            return mainLst;
        }
        catch (Exception e)
        {
            Log.d(getClass().getName(), "Cannot find reflection class name");
            return null;
        }

    }

    public java.util.List<Item>  getList(String name) {
        try {

            java.lang.reflect.Method methd;
            methd = dbi.getClass().getMethod("getList", String.class);
            java.util.List<Item> mainLst = (java.util.List<Item>) methd.invoke(dbi, name);
            return mainLst;
        }
        catch (Exception e)
        {
            Log.d(getClass().getName(), "Cannot find reflection class name");
            return null;
        }

    }

    public java.util.List<Item>  getTemplList(String name) {
        try {

            java.lang.reflect.Method methd;
            methd = dbi.getClass().getMethod("getTemplList", String.class);
            java.util.List<Item> mainLst = (java.util.List<Item>) methd.invoke(dbi, name);
            return mainLst;
        }
        catch (Exception e)
        {
            Log.d(getClass().getName(), "Cannot find reflection class name");
            return null;
        }

    }

    public boolean updateDb (Item itm, int vwType) {
        try {
            java.lang.reflect.Method methd;
            methd = dbi.getClass().getMethod("updateDb", Item.class, int.class);
            return (boolean) methd.invoke(dbi, itm, vwType);

        }
        catch (Exception e)
        {
            Log.d(getClass().getName(), "Exception in updateDb " + e.getMessage());
            return false;
        }

    }

    public boolean deleteDb (Item itm, int vwType) {
        try {
            switch (vwType) {

                case CONTACTS_ITEM_DISPLAY:
                    return contactsDBIntf.deleteDb(itm, vwType);

                default:
                    java.lang.reflect.Method methd;
                    methd = dbi.getClass().getMethod("deleteDb", Item.class, int.class);
                    return (boolean) methd.invoke(dbi, itm, vwType);
            }

        }
        catch (Exception e)
        {
            Log.d(getClass().getName(), "Exception in deleteDb " + e.getMessage());
            return false;
        }

    }

    public boolean itemExists (Item itm, int vwType)
    {
        try {
            java.lang.reflect.Method methd;
            methd = dbi.getClass().getMethod("itemExists", Item.class, int.class);
            return (boolean) methd.invoke(dbi, itm, vwType);

        }
        catch (Exception e)
        {
            Log.d(getClass().getName(), "Exception in updateDb " + e.getMessage());
            return false;
        }

    }

    public Item shareItemExists (Item itm, int vwType)
    {
        try {
            java.lang.reflect.Method methd;
            methd = dbi.getClass().getMethod("shareItemExists", Item.class, int.class);
            return (Item) methd.invoke(dbi, itm, vwType);

        }
        catch (Exception e)
        {
            Log.d(getClass().getName(), "Exception in updateDb " + e.getMessage());
            return null;
        }

    }


    public boolean insertDb (Item itm, int vwType) {
        try {

            switch (vwType) {

                case CONTACTS_ITEM_ADD:
                    return contactsDBIntf.insertDb(itm, vwType);

                default:
                    java.lang.reflect.Method methd;
                    methd = dbi.getClass().getMethod("insertDb", Item.class, int.class);
                    return (boolean) methd.invoke(dbi, itm, vwType);
            }

        }
        catch (Exception e)
        {
            Log.d(getClass().getName(), "Exception in insertDb " + e.getMessage());
            return false;
        }
    }
}
