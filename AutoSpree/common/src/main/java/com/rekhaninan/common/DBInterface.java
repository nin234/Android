package com.rekhaninan.common;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;

import java.util.HashMap;
import java.util.List;

/**
 * Created by nin234 on 7/30/16.
 */
public abstract class DBInterface {

    private String sortString;

    public String getSortString() {
        return sortString;
    }

    public void setSortString(String sortString) {
        this.sortString = sortString;
    }

    public abstract void initDb(Context ctxt);

    public abstract  List<Item> getMainViewLst();

    public abstract boolean insertDb (Item itm, int vwType);

    public abstract boolean updateDb (Item itm, int vwType);

    public abstract boolean deleteDb (Item itm, int vwType);

    public abstract boolean itemExists (Item itm, int vwType);

    public abstract Item shareItemExists (Item itm, int vwType);

}
