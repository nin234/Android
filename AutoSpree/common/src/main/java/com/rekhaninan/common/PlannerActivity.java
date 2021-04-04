package com.rekhaninan.common;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import static com.rekhaninan.common.Constants.ALWAYS_POSN;
import static com.rekhaninan.common.Constants.CHECK_LIST_ADD;
import static com.rekhaninan.common.Constants.CHECK_LIST_EDIT;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.EASYGROC_EDIT_VIEW;
import static com.rekhaninan.common.Constants.EASYGROC_SAVE_VIEW;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_EDIT_ITEM;
import static com.rekhaninan.common.Constants.NSHARELIST;
import static com.rekhaninan.common.Constants.ONETIME_POSN;
import static com.rekhaninan.common.Constants.REPLENISH_POSN;

public class PlannerActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    TabLayout tabLayout;
    private Item itm;
    int viewType;
    private PlannerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            String app_name = DBOperations.getInstance().getApp_name();
            Log.d(getClass().getSimpleName(), "Starting onCreate of PlannerActivity");
            super.onCreate(savedInstanceState);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            setContentView(R.layout.tabbed_main);

            viewPager = findViewById(R.id.pager);

            tabLayout = findViewById(R.id.tab_layout);
            adapter = new PlannerAdapter(this);
            Intent intent = getIntent();
            itm =  intent.getParcelableExtra("item");
            int view = intent.getIntExtra("View", 0);
            adapter.setItem(itm);
            Log.d(getClass().getSimpleName(), "Starting planner view=" + view);
            if (view == EASYGROC_EDIT_VIEW) {
                setViewType();
            }
            else
            {
                viewType = EASYGROC_TEMPL_EDIT_ITEM;
            }
            adapter.setViewType(viewType);
            viewPager.setAdapter(adapter);
            new TabLayoutMediator(tabLayout, viewPager,
                    new TabLayoutMediator.TabConfigurationStrategy() {
                        @Override public void onConfigureTab(TabLayout.Tab tab, int position) {
                            Log.d(getClass().getSimpleName(), "Configuring tab at=" + position);
                            if (app_name.equals(EASYGROC))
                            {
                                switch (position) {
                                    case REPLENISH_POSN:
                                        tab.setText("Replenish");
                                        break;

                                    case ONETIME_POSN:
                                        tab.setText("One-time");
                                        break;

                                    case ALWAYS_POSN:
                                        tab.setText("Always");
                                        break;

                                    default:
                                        tab.setText("Invalid Posn");
                                        break;
                                }
                            }

                        }
                    }).attach();

            if (app_name.equals(EASYGROC)) {
                viewPager.setCurrentItem(REPLENISH_POSN);
            }


        }
             catch (Exception e)
            {
                Log.e(getClass().getName(), "Exception2 in onCreate of PlannerActivity " + e.getMessage(), e);
                return;
            }

        }

        private void setViewType()
        {
            boolean add = true;
            java.util.List<Item> list = DBOperations.getInstance().getTemplList(itm.getName(), itm.getShare_id());
            if (list.size() != 0)
            {
                add = false;
            }

            Item itemInv = new Item();
            String name = itm.getName();
            name += ":INV";
            itemInv.setName(name);
            itemInv.setShare_id(itm.getShare_id());
            list = DBOperations.getInstance().getTemplList(itemInv.getName(), itemInv.getShare_id());
            if (list.size() != 0)
            {
                add = false;
            }

            Item itemScrtch = new Item();
            name = itm.getName();
            name += ":SCRTCH";
            Log.d(getClass().getName(), "Setting name="+name);
            itemScrtch.setName(name);
            itemScrtch.setShare_id(itm.getShare_id());
            list = DBOperations.getInstance().getTemplList(itemScrtch.getName(), itemScrtch.getShare_id());
            if (list.size() != 0)
            {
                add = false;
            }

            if (add)
            {
                viewType = EASYGROC_TEMPL_EDIT_ITEM;
            }
            else
            {
                viewType = EASYGROC_TEMPL_DISPLAY_ITEM;
            }
        }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Log.d(getClass().getName(), "Menu item selected");
        if (item.getItemId() == android.R.id.home)
        {
            Log.d(getClass().getName(), "Back button pressed");

            finish();
            return  true;
        }
        else if (item.getItemId() == R.id.edit_item)
        {
            finish();
            Intent intent = new Intent(this, PlannerActivity.class);
            intent.putExtra("item", itm);
            intent.putExtra("View", EASYGROC_SAVE_VIEW);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.save_templ_item)
        {
            finish();
            adapter.save();
            Intent intent = new Intent(this, PlannerActivity.class);
            intent.putExtra("item", itm);
            intent.putExtra("View", EASYGROC_EDIT_VIEW);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.delete_templ_item)
        {
            Log.d(getClass().getName(), "Showing delete confirm menu");
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Delete");

            String delMsg = "Delete "  + itm.getName() + " ?";
            alertDialog.setMessage(delMsg);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.delete();
                            Intent intent = new Intent();
                            intent.putExtra("refresh", "Needed");
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
            alertDialog.show();
        }
        return  true;

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        switch (viewType) {
            case EASYGROC_TEMPL_DISPLAY_ITEM:
                inflater.inflate(R.menu.easygroc_templ_edit, menu);
                break;

            case EASYGROC_TEMPL_EDIT_ITEM:
                inflater.inflate(R.menu.easygroc_templ_main, menu);
                break;
            default:
                break;
        }

        return true;
    }
}
