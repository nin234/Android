package com.rekhaninan.common;

import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.graphics.Point;
import static com.rekhaninan.common.Constants.*;

import java.util.ListIterator;
import java.util.Stack;

/**
 * Created by nin234 on 8/21/16.
 */

public class ArrayAdapterMainVw extends ArrayAdapter<Item> {


    private RowView rowView;



    private List<Item> arryElems;
    final int INVALID_ID = -1;
    HashMap<Item, Integer> mIdMap = new HashMap<Item, Integer>();
    private int nSize;
    private Stack<Integer> undo;
    private Stack<Integer> redo;
    private List<Item> checkList;


    //Need an undo redo stack
    //Undo should undo the previous action, upon undo move the action from undo to redo
    //redo should undo the undo and remove the item from redo stack


    public List<Item> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<Item> checkList) {
        this.checkList = checkList;
    }

    public ArrayAdapterMainVw (Context context, int resource, List<Item> objects)
   {
        super(context, resource, objects);
        arryElems = objects;
       checkList = new ArrayList<>();

   }

    public Item getNewContact()
    {
        Item newContact = new Item();
        int i =0;
        Item nameItem = arryElems.get(CONTACTS_NAME_ROW);
        if (nameItem.getName() != null && nameItem.getName().length() > 0)
        {
            newContact.setName(nameItem.getName());
        }
        Item shareItem = arryElems.get(CONTACTS_SHARE_ID_ROW);
        newContact.setShare_id(shareItem.getShare_id());
        return newContact;
    }


    public void resetSelected (int i)
    {
        for (Item itmElem : arryElems)
        {
            if (itmElem.getRowno() == i)
                continue;
            itmElem.setSelected(false);
            itmElem.resetChkBox();
        }
        return;
    }

    public void undoPush(int indx)
    {
        undo.push(indx);
        return;
    }

    public int undoPop()
    {
        return undo.pop();
    }

    public void redoPush (int indx)
    {
        redo.push(indx);
        return;
    }

    public int redoPop()
    {
        return redo.pop();
    }

    public ArrayList<Item> getSelectedList()
    {
        ArrayList<Item> selctdList = new ArrayList<Item>();
        for (Item itm : arryElems)
        {
            if (itm.isSelected() == true)
            {
                selctdList.add(itm);
            }
        }

        return selctdList;
    }

    public Item getSelectedItem()
    {

        for (Item itm : arryElems)
        {
            if (itm.isSelected() == true)
            {
                return itm;
            }
        }

        return null;
    }

    public List<Item> getArryElems() {
        return arryElems;
    }

    public void setArryElems(List<Item> arryElems) {
        this.arryElems = arryElems;
    }

    public void moveElements(int originalPosn, int newPosn)
    {
        if (originalPosn == 0)
            return;
        if (newPosn ==0)
            return;

        if (originalPosn == newPosn)
        {
            return;
        }
        else if (originalPosn < newPosn)
        {
            // 0 1 2 3 4 5 6 7 8 9
            // op=2  np=6
            for (int i= originalPosn+1; i < newPosn; ++i )
            {
                Item itm = arryElems.get(i);
                itm.setRowno(itm.getRowno()-1);
            }
            Item itm = arryElems.get(originalPosn);
            itm.setRowno(newPosn);
            arryElems.add(newPosn, itm);
            arryElems.remove(originalPosn);
        }
        else
        {
            // 0 1 2 3 4 5 6 7 8 9
            // op=6  np=2
            for (int i= originalPosn; i < newPosn; ++i )
            {
                Item itm = arryElems.get(i);
                itm.setRowno(itm.getRowno()+1);
            }
            Item itm = arryElems.get(originalPosn);
            itm.setRowno(newPosn);
            arryElems.remove(originalPosn);
            arryElems.add(newPosn, itm);

        }
        return;
    }

    public void addItem(Item itm)
    {
        int rowNo = itm.getRowno();
        for (Item itmElem : arryElems)
        {
            if (itmElem.getRowno() >= rowNo)
            {
                itmElem.setRowno(itmElem.getRowno()+1);
            }
        }
        arryElems.add(itm.getRowno(), itm);
        ++nSize;
        mIdMap.put(itm, nSize);
        return;
    }

    public void getKeyVals(Item itm)
    {
        rowView.getKeyVals(itm);
        return;
    }

    public void removeItem(Item itm)
    {
        int rowNo = itm.getRowno();
        mIdMap.remove(itm);
        arryElems.remove(itm);
        for (Item itmElem : arryElems)
        {
            if (itmElem.getRowno() > rowNo)
            {
                itmElem.setRowno(itmElem.getRowno()-1);
            }
        }
    }

    public int getCount()
    {
        if (rowView.getVwType() == EASYGROC_DISPLAY_ITEM)
        {
            int cnt = 0;
            for (Item itm : arryElems)
            {
                if (itm.isHidden() == false)
                    ++cnt;
            }
            return cnt;
        }
       return super.getCount();
    }

    public Item getItem(int position)
    {
        if (rowView.getVwType() == EASYGROC_DISPLAY_ITEM)
        {
            int cnt = 0;
            for (Item itm : arryElems)
            {
                if (itm.isHidden() == false) {
                    if (cnt == position)
                    {
                        return itm;
                    }
                    ++cnt;
                }
            }
        }
        return  super.getItem(position);
    }

    @Override
    public long getItemId(int position) {

        switch (rowView.getVwType())
        {
            case EASYGROC_ADD_ITEM:
            case EASYGROC_EDIT_ITEM:
            case EASYGROC_TEMPL_ADD_ITEM:
            case EASYGROC_TEMPL_EDIT_ITEM:
            {
                if (position < 0 || position >= mIdMap.size()) {
                    return INVALID_ID;
                }
                Item item = getItem(position);
                return mIdMap.get(item);
            }

            default:
                break;
        }

        return super.getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    public void setParams (String appTyp, int viewType)
    {


        switch (appTyp)
        {
            case OPENHOUSES:
                    rowView = new OpenHousesRow();
                break;

            case AUTOSPREE:
                    rowView = new AutoSpreeRow();
                break;

            case EASYGROC:
                    rowView = new EasyGrocRow();
                break;
            default:
                break;
        }
        rowView.setVwType(viewType);
        rowView.setCtxt(getContext());
        rowView.setAdapter(this);
        rowView.setApp_name(appTyp);

        switch (viewType)
        {
            case EASYGROC_ADD_ITEM:
            case EASYGROC_EDIT_ITEM:
            case EASYGROC_TEMPL_ADD_ITEM:
            case EASYGROC_TEMPL_EDIT_ITEM:
            {
                 nSize = arryElems.size();
                for (int i=0; i < nSize; ++i)
                {
                    mIdMap.put(arryElems.get(i), i);
                }
            }
            break;

            default:
                break;
        }
    }

    public View getView (int position,
                  View convertView,
                  ViewGroup parent)
    {

        return rowView.getView(getItem(position), position, parent);
    }


}
