package com.rekhaninan.common;

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
import androidx.fragment.app.Fragment;

import static com.rekhaninan.common.Constants.ADD_CONTACT_ITEM_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.CONTACTS_ITEM_ADD;
import static com.rekhaninan.common.Constants.CONTACTS_VW;
import static com.rekhaninan.common.Constants.GET_CONTACTS_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.SHARE_MAINVW;

public class ShareVwTabbed extends Fragment {

    public static final String ARG_OBJECT = "ShareVwTabbed";
    public String app_name;
    private ListView mListView;
    private final String TAG = "ShareVwTabbed";

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
        ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(getContext(), R.layout.simple_list_1, mainLst);
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
            Item selectedItem = adapter.getSelectedItem();
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
}
