package com.rekhaninan.common;

import android.os.Bundle;

import java.util.StringJoiner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import static com.rekhaninan.common.Constants.*;


public class TabbedCollectionAdapter extends FragmentStateAdapter {
    public String appName;
    private PlannerVwTabbed plannerVwTabbed;
    public TabbedCollectionAdapter(FragmentActivity frg)
    {

        super(frg);
    }



    private static final int MAIN_PAGE_TABS = 4;
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Log.d(getClass().getSimpleName(), "Creating fragment at=" + position);
        switch (position) {
            case  HOME_POSN: {
                MainVwTabbed fragment = new MainVwTabbed();
                fragment.app_name = appName;
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(MainVwTabbed.ARG_OBJECT, position + 1);
                fragment.setArguments(args);
                return fragment;
            }

            case PLANNER_POSN: {
                plannerVwTabbed  = new PlannerVwTabbed();
                plannerVwTabbed.app_name = appName;
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(PlannerVwTabbed.ARG_OBJECT, position + 1);
                plannerVwTabbed.setArguments(args);
                return plannerVwTabbed;
            }

            case CONTACTS_POSN: {
                ContactsVwTabbed fragment = new ContactsVwTabbed();
                fragment.app_name = appName;
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(ContactsVwTabbed.ARG_OBJECT, position + 1);
                fragment.setArguments(args);
                return fragment;
            }

            case SHARE_POSN: {
                ShareVwTabbed fragment = new ShareVwTabbed();
                fragment.app_name = appName;
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(ShareVwTabbed.ARG_OBJECT, position + 1);
                fragment.setArguments(args);
                return fragment;
            }

            default:
                break;

        }
        Fragment fragment = new ShareVwTabbed();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(ShareVwTabbed.ARG_OBJECT, position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {

        return MAIN_PAGE_TABS;
    }

    public void refresh()
    {
        plannerVwTabbed.refresh();
    }


}
