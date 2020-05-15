package com.rekhaninan.common;


import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainVwTabbed extends Fragment {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    TabbedCollectionAdapter demoCollectionAdapter;
    ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tabbed_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(demoCollectionAdapter);
    }
}



