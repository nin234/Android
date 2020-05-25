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
    private int viewType;

    private AlwaysVw alwaysVw;
    private ReplenishVw replenishVw;
    private OneTimeVw oneTimeVw;


    public PlannerAdapter(FragmentActivity frg)
    {

        super(frg);
    }
    public void setItem(Item itm) {item = itm;}

    public void setViewType(int type) {viewType = type;}

    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Log.d(getClass().getSimpleName(), "Creating fragment at=" + position);
        switch (position) {
            case REPLENISH_POSN: {
                replenishVw = new ReplenishVw();
                replenishVw.setItem(item);
                replenishVw.setViewType(viewType);
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(ReplenishVw.ARG_OBJECT, position + 1);
                replenishVw.setArguments(args);
                
                return replenishVw;
            }

            case ONETIME_POSN: {
                oneTimeVw = new OneTimeVw();
                oneTimeVw.setItem(item);
                oneTimeVw.setViewType(viewType);
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(OneTimeVw.ARG_OBJECT, position + 1);
                oneTimeVw.setArguments(args);
                return oneTimeVw;
            }

            case ALWAYS_POSN: {
                alwaysVw = new AlwaysVw();
                alwaysVw.setItem(item);
                alwaysVw.setViewType(viewType);
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(AlwaysVw.ARG_OBJECT, position + 1);
                alwaysVw.setArguments(args);
                return alwaysVw;
            }

            default:
                break;
        }

        ReplenishVw fragment = new ReplenishVw();
        return fragment;

    }

    public void delete()
    {
        alwaysVw.delete();
        replenishVw.delete();
        oneTimeVw.delete();
    }

    public void save()
    {
        alwaysVw.save();
        replenishVw.save();
        oneTimeVw.save();
    }


    @Override
    public int getItemCount() {

        return PLANNER_TABS;
    }

}
