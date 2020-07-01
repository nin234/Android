package com.rekhaninan.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import static com.rekhaninan.common.Constants.ADD_CONTACT_ITEM_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.CONTACTITEMSEPARATOR;
import static com.rekhaninan.common.Constants.CONTACTS_ITEM_ADD;
import static com.rekhaninan.common.Constants.CONTACTS_VW;
import static com.rekhaninan.common.Constants.GET_CONTACTS_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.ITEMSEPARATOR;
import static com.rekhaninan.common.Constants.KEYVALSEPARATOR;
import static com.rekhaninan.common.Constants.MAINVW;
import static com.rekhaninan.common.Constants.SHARE_MAINVW;

public class ShareVwTabbed extends Fragment {

    public static final String ARG_OBJECT = "ShareVwTabbed";
    public String app_name;
    private ListView mListView;
    private final String TAG = "ShareVwTabbed";
    private Item selectedItem;
    private  ArrayAdapterMainVw adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main_vw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(SHARE_MAINVW);
        if (mainLst == null) {
            Log.d(TAG, "NULL main list");
            return;
        }


        mListView = (ListView) view.findViewById(R.id.recipe_list_view);
        adapter = new ArrayAdapterMainVw(getContext(), R.layout.simple_list_1, mainLst);
        adapter.setParams(app_name, SHARE_MAINVW);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        Log.d(TAG, "Inflating menu add_single_item in ContactsVwTabbed");
        inflater.inflate(R.menu.share_item, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.select_items) {
            Log.d(TAG, "Showing select contacts screen");
            ArrayAdapterMainVw  adapter = (ArrayAdapterMainVw)mListView.getAdapter();
            selectedItem = adapter.getSelectedItem();
            if (selectedItem == null)
            {
                return true;
            }
            Intent intent = new Intent(getActivity(), ShareActivity.class);
            intent.putExtra("app_name", app_name);
            intent.putExtra("ViewType", CONTACTS_VW);
            startActivityForResult(intent, GET_CONTACTS_ACTIVITY_REQUEST);
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Processing Activity result for request Code=" + requestCode);
        switch (requestCode) {
           case GET_CONTACTS_ACTIVITY_REQUEST: {
               String msg;
               if (resultCode == Activity.RESULT_OK) {
                   java.util.ArrayList<Item> contactsLst = data.getParcelableArrayListExtra("contactslist");
                   shareEasyGrocItem(contactsLst);
                   msg = "Send item="+ selectedItem.getName() + " to=";
                   for (Item contact : contactsLst)
                   {
                       msg += contact.getName() + ", ";
                   }

               } else {
                    msg = "Failed to share item, no contacts selected";

               }
               adapter.notifyDataSetChanged();
               AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
               alertDialog.setTitle("Share Items");
               alertDialog.setMessage(msg);
               alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                               return;
                           }
                       });
               alertDialog.show();
           }
               break;

        }
    }
    private void shareEasyGrocItem(java.util.ArrayList<Item> contactsLst)
    {
        String shrMsg = new String();
        for (Item contact : contactsLst)
        {
            if (contact.getName().equals("ME"))
                continue;
            shrMsg += Long.toString(contact.getShare_id());
            shrMsg += ";";
        }

        if (shrMsg == null || shrMsg.length() <=0)
        {
            Log.d(TAG, "No contact to share to");
            return;
        }

        if (selectedItem.getPicurl() != null && selectedItem.getPicurl().length() > 0)
        {

            String picMetaStr = shrMsg + selectedItem.getName();
            ShareMgr.getInstance().sharePicture(selectedItem.getPicurl(), picMetaStr, selectedItem.getShare_id());
        }
        else {
            shrMsg += CONTACTITEMSEPARATOR;
            java.util.List<Item> list = DBOperations.getInstance().getList(selectedItem.getName(), selectedItem.getShare_id());
            if (list.size() == 0)
            {
                Log.d(TAG, "No elements in the sharing list");
            }
            Item firstEl = list.get(0);
            shrMsg += Long.toString(firstEl.getShare_id());
            shrMsg += KEYVALSEPARATOR;
            shrMsg += Long.toString(firstEl.getShare_id());
            shrMsg += ITEMSEPARATOR;

            for (Item selItem : list)
            {

                shrMsg += Integer.toString(selItem.getRowno());
                shrMsg += KEYVALSEPARATOR;
                shrMsg += selItem.getItem();
                shrMsg += ITEMSEPARATOR;
            }
            ShareMgr.getInstance().shareItem(shrMsg, selectedItem.getName());
        }

        return;
    }
    public void refresh()
    {
        Log.d(TAG, "Refreshing Share View");
        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(SHARE_MAINVW);
        adapter.setArryElems(mainLst);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}
