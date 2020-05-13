package com.rekhaninan.common;

import android.os.Bundle;

import java.util.StringJoiner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabbedCollectionAdapter extends FragmentStateAdapter {
    public TabbedCollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = new ShareVwTabbed();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(ShareVwTabbed.ARG_OBJECT, position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 100;
    }
}
