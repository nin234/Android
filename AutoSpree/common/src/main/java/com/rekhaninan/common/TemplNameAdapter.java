package com.rekhaninan.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.rekhaninan.common.Constants.CONTACTS_ITEM_DISPLAY;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_DISPLAY_ITEM;

/**
 * Created by ninanthomas on 4/2/17.
 */

public class TemplNameAdapter extends BaseExpandableListAdapter {

    private List<Item> groupItem;
    private Context ctxt;

    public TemplNameAdapter (List<Item> gitems, Context ctx)
    {
        super();
        groupItem = gitems;
        ctxt = ctx;
    }

    public List<Item> getGroupItem() {
        return groupItem;
    }

    public void setGroupItem(List<Item> groupItem) {
        this.groupItem = groupItem;
    }

    public View getGroupView (int groupPosition,
                              boolean isExpanded,
                              View convertView,
                              ViewGroup parent)
    {
        Item grpItem = groupItem.get(groupPosition);
        String listTitle = grpItem.getName();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.ctxt.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.templ_name_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.templNameGrp);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;

    }

    public Object getGroup (int groupPosition)
    {
        return null;
    }

    public long getChildId (int groupPosition,
                            int childPosition)

    {
        return childPosition;
    }

    @Override
   public View getChildView (int groupPosition,
                       int childPosition,
                       boolean isLastChild,
                       View convertView,
                       ViewGroup parent)
    {
         String expandedListText;
        Item itm = groupItem.get(groupPosition);
        String name = itm.getName();
        switch (childPosition)
        {
            case 0:
                expandedListText = "Recurring List";
            break;

            case 1:
                expandedListText = "Inventory List";
                name += ":INV";
             break;

            case 2:
                expandedListText = "Scratch Pad";
                name += ":SCRTCH";
            break;

            default:
                expandedListText = "Invalid List";
                break;

        }
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.ctxt
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.templ_name_item, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.templNameItem);
        expandedListTextView.setText(expandedListText);

        itm.setName(name);
        convertView.setTag(itm);
        convertView.setOnClickListener(new View.OnClickListener()
                                       {
                                           @Override
                                           public void onClick(View view) {

                                               TextView tv = (TextView) view;

                                               Item itm = (Item) tv.getTag();
                                               Log.d(getClass().getName(), "Clicked row " + tv.getText());

                                               Intent intent = new Intent(ctxt, SingleItemActivity.class);
                                               java.util.List<Item> list = DBOperations.getInstance().getTemplList(itm.getName());
                                               if (list.size() ==0) {
                                                   intent.putExtra("ViewType", EASYGROC_TEMPL_ADD_ITEM);
                                               }
                                               else
                                               {
                                                   intent.putExtra("ViewType", EASYGROC_TEMPL_DISPLAY_ITEM);
                                               }
                                               intent.putExtra("item", itm);

                                               ctxt.startActivity(intent);
                                           }
                                       }
        );
        return convertView;

    }

     public boolean isChildSelectable (int groupPosition,
                               int childPosition)
    {

        return true;
    }

    public int getGroupCount ()
    {
        return groupItem.size();
    }


    public int getChildrenCount (int groupPosition)
    {

        return 3;
    }

    public boolean hasStableIds ()
    {
        return false;
    }

   public Object getChild (int groupPosition,
                     int childPosition)
   {

       return null;
   }

    public long getGroupId (int groupPosition)
    {
        return groupPosition;
    }
}
