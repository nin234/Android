package com.rekhaninan.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.rekhaninan.common.Constants.ADD_CONTACT_ITEM_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.CONTACTS_ITEM_ADD;
import static com.rekhaninan.common.Constants.CONTACTS_MAINVW;
import static com.rekhaninan.common.Constants.DELETE_CONTACT_ITEM_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM_REQUEST;
import static com.rekhaninan.common.Constants.MAINVW;

public class ContactsVwTabbed extends Fragment {

    public String app_name;
    private ListView mListView;
    private final String TAG = "ContactsVwTabbed";
    private ArrayAdapterMainVw adapter;

    public static final String ARG_OBJECT = "ContactsVwTabbed";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main_vw, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        Log.d(TAG, "Inflating menu add_single_item in ContactsVwTabbed");
        inflater.inflate(R.menu.add_single_item, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.add_new_item) {
            Log.d(TAG, "Showing add new contact screen");
            Intent intent = new Intent(getActivity(), ShareActivity.class);
            intent.putExtra("app_name", app_name);
            intent.putExtra("ViewType", CONTACTS_ITEM_ADD);
            startActivityForResult(intent, ADD_CONTACT_ITEM_ACTIVITY_REQUEST);
        }
        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(CONTACTS_MAINVW);
        mListView = (ListView) view.findViewById(R.id.recipe_list_view);
        adapter = new ArrayAdapterMainVw(getContext(), R.layout.simple_list_1, mainLst);
        adapter.setParams(app_name, CONTACTS_MAINVW);
        mListView.setAdapter(adapter);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Processing Activity result for request Code=" + requestCode);
        switch (requestCode)
        {
            case ADD_CONTACT_ITEM_ACTIVITY_REQUEST:
                if (resultCode == Activity.RESULT_OK)
                {
                    String refreshNeeded = data.getStringExtra("refresh");
                    if (refreshNeeded.equals("Needed"))
                    {
                        refresh();
                    }
                }
                break;

            default:
                break;
        }

    }

    public void refresh()
    {
        Log.d(TAG, "Refreshing ContactsVwTabbed");
        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(CONTACTS_MAINVW);
        adapter.setArryElems(mainLst);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "In onResume of ContactsVwTabbed");
        refresh();
    }

    }
