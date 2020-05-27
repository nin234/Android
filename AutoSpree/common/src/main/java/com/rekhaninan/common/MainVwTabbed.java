package com.rekhaninan.common;



import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import static com.rekhaninan.common.Constants.*;



public class MainVwTabbed extends Fragment {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.

    public static final String ARG_OBJECT = "MainVwTabbed";
    private String dbClassName;
    private String message;
    private int no_items;
    private ListView mListView;
    private static final String TAG="MainVwTabbed";
    public String app_name;
    private ArrayAdapterMainVw adapter;
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



            java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(MAINVW);
            if (mainLst == null)
            {
                Log.d(getClass().getSimpleName(), "NULL main list");
                return;
            }


            no_items = mainLst.size();

            mListView = (ListView) view.findViewById(R.id.recipe_list_view);
// 1
// 4
            adapter = new ArrayAdapterMainVw(getContext(), R.layout.simple_list_1, mainLst);
            //ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_list_1, mainLst);
            adapter.setParams(app_name, MAINVW);
            mListView.setAdapter(adapter);
           //mListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
          //  mListView.setClickable(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (app_name.equals(EASYGROC))
        {
            Log.d(TAG, "Inflating main_easygro_menu in MainVwTabbed");
            inflater.inflate(R.menu.add_single_item, menu);
        }
        else {
            Log.d(TAG, "Inflating main_menu");
            inflater.inflate(R.menu.main_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.add_new_item) {
            Log.d(getClass().getName(), "Creating new Item for app="+ app_name);

            Intent intent = new Intent(getActivity(), SingleItemActivity.class);
            SharedPreferences settings = getContext().getSharedPreferences("OHAutoSpree", Context.MODE_PRIVATE);

            switch (app_name) {
                case AUTOSPREE: {
                    intent.putExtra("ViewType", AUTOSPREE_ADD_ITEM);
                    String item_name = "Car";
                    int item_no = settings.getInt("item_no", 1);
                    item_name += item_no;
                    Item itm = new Item();
                    itm.setName(item_name);
                    intent.putExtra("item", itm);
                }
                break;

                case OPENHOUSES: {
                    intent.putExtra("ViewType", OPENHOUSES_ADD_ITEM);
                    String item_name = "House";
                    int item_no = settings.getInt("item_no", 1);
                    item_name += item_no;
                    Item itm = new Item();
                    itm.setName(item_name);
                    itm.setShare_id(ShareMgr.getInstance().getShare_id());
                    intent.putExtra("item", itm);
                }
                break;

                case EASYGROC:
                    intent.putExtra("ViewType", EASYGROC_ADD_ITEM_OPTIONS);
                    Item itm = new Item();
                    intent.putExtra("item", itm);
                    startActivityForResult(intent, EASYGROC_ADD_ITEM_REQUEST);
                    return true;

                default:

                    break;
            }
            startActivity(intent);

        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Processing Activity result for request Code=" + requestCode);
        switch (requestCode)
        {
            case EASYGROC_ADD_ITEM_REQUEST:

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

        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(MAINVW);
        adapter.setArryElems(mainLst);
        adapter.notifyDataSetChanged();
    }
}



