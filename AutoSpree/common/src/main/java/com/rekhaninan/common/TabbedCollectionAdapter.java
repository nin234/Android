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
    public TabbedCollectionAdapter(FragmentActivity frg)
    {

        super(frg);
    }

    private static final int MAIN_PAGE_TABS = 4;
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        if (position == HOME_POSN)
        {
            MainVwTabbed fragment = new MainVwTabbed();
            fragment.app_name = appName;
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(MainVwTabbed.ARG_OBJECT, position + 1);
            fragment.setArguments(args);
            return fragment;
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
}
