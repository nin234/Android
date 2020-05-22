package com.rekhaninan.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import static com.rekhaninan.common.Constants.ALWAYS_POSN;
import static com.rekhaninan.common.Constants.CHECK_LIST_ADD;
import static com.rekhaninan.common.Constants.CHECK_LIST_EDIT;
import static com.rekhaninan.common.Constants.ONETIME_POSN;
import static com.rekhaninan.common.Constants.REPLENISH_POSN;

public class PlannerActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    TabLayout tabLayout;
    private Item itm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            Log.d(getClass().getSimpleName(), "Starting onCreate of PlannerActivity");
            super.onCreate(savedInstanceState);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            setContentView(R.layout.tabbed_main);

            viewPager = findViewById(R.id.pager);

            tabLayout = findViewById(R.id.tab_layout);
            PlannerAdapter adapter = new PlannerAdapter(this);
            Intent intent = getIntent();
            itm =  intent.getParcelableExtra("item");
            adapter.setItem(itm);
            viewPager.setAdapter(adapter);
            new TabLayoutMediator(tabLayout, viewPager,
                    new TabLayoutMediator.TabConfigurationStrategy() {
                        @Override public void onConfigureTab(TabLayout.Tab tab, int position) {
                            Log.d(getClass().getSimpleName(), "Configuring tab at=" + position);
                            switch (position)
                            {
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
                    }).attach();

            viewPager.setCurrentItem(REPLENISH_POSN);


        }
             catch (Exception e)
            {
                Log.e(getClass().getName(), "Exception2 in onCreate of PlannerActivity " + e.getMessage(), e);
                return;
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
        return  true;

    }

}
