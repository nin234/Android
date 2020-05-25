package com.rekhaninan.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_DELETE_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_EDIT_ITEM;

public class AlwaysVw extends Fragment {
    public static final String ARG_OBJECT = "AlwaysVw";
    private Item item;
    private ListView mListView;
    ArrayAdapterMainVw adapter;
    int viewType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_single_item, container, false);
    }

    public void setItem(Item itm)
    {
        item = new Item();
        item.setName(itm.getName());
        item.setShare_id(itm.getShare_id());
    }
    public void setViewType(int type)
    {
        viewType = type;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        java.util.List<Item> mainLst = new ArrayList<Item>();
        java.util.List<Item> list = DBOperations.getInstance().getTemplList(item.getName(), item.getShare_id());
        mainLst.add(item);

        if (list.size() != 0) {
            mainLst.addAll(list);
            mListView = (ListView) view.findViewById(R.id.add_item_view);
            adapter = new ArrayAdapterMainVw(getActivity(), R.layout.simple_list_1, mainLst);
            adapter.setParams(EASYGROC, viewType);
            if (!item.getName().endsWith(":INV") && !item.getName().endsWith(":SCRTCH"))
                adapter.setRecrLst(true);
            mListView.setAdapter(adapter);
        }
        else
        {
            for (int i=0; i <12; ++i) {
                Item templItm = new Item();
                templItm.setRowno(i);
                mainLst.add(templItm);
            }
            mListView = (ListView) view.findViewById(R.id.add_item_view);
            adapter = new ArrayAdapterMainVw(getActivity(), R.layout.simple_list_1, mainLst);
            adapter.setParams(EASYGROC, viewType);
            if (item.getName() != null && !item.getName().endsWith(":INV") && !item.getName().endsWith(":SCRTCH"))
                adapter.setRecrLst(true);
            mListView.setAdapter(adapter);
        }

    }

    public void delete()
    {
        DBOperations.getInstance().deleteDb(item, EASYGROC_TEMPL_DELETE_ITEM);
    }

    public void save()
    {
        if (mListView == null)
        {
            return;
        }
        ArrayAdapterMainVw adapterMainVw = (ArrayAdapterMainVw) mListView.getAdapter();
        List<Item> arryEl = adapterMainVw.getArryElems();
        Item nameItem = arryEl.get(0);
        String name = nameItem.getName();
        int i=0;
        DBOperations.getInstance().deleteDb(nameItem, viewType);
        for (Item itm : arryEl)
        {
            if (i == 0) {
                ++i;
                continue;
            }
            itm.setName(name);

            DBOperations.getInstance().insertDb(itm, viewType);

            ++i;
        }
    }

}
