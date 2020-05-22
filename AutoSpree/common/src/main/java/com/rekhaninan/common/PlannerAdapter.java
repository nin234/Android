package com.rekhaninan.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import static com.rekhaninan.common.Constants.*;

public class PlannerAdapter extends FragmentStateAdapter {

    private static final int PLANNER_TABS = 3;

    private Item item;





    public PlannerAdapter(FragmentActivity frg)
    {

        super(frg);
    }
    public void setItem(Item itm) {item = itm;}


    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Log.d(getClass().getSimpleName(), "Creating fragment at=" + position);
        switch (position) {
            case REPLENISH_POSN: {
                ReplenishVw fragment = new ReplenishVw();
                fragment.setItem(item);
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(ReplenishVw.ARG_OBJECT, position + 1);
                fragment.setArguments(args);
                return fragment;
            }

            case ONETIME_POSN: {
                OneTimeVw fragment = new OneTimeVw();
                fragment.setItem(item);
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(OneTimeVw.ARG_OBJECT, position + 1);
                fragment.setArguments(args);
                return fragment;
            }

            case ALWAYS_POSN: {
                AlwaysVw fragment = new AlwaysVw();
                fragment.setItem(item);
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(AlwaysVw.ARG_OBJECT, position + 1);
                fragment.setArguments(args);
                return fragment;
            }

            default:
                break;
        }

        ReplenishVw fragment = new ReplenishVw();
        return fragment;

    }

    @Override
    public int getItemCount() {

        return PLANNER_TABS;
    }

}
