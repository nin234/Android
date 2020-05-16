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
import android.widget.ListView;
import android.widget.TextView;
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
            ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(getContext(), R.layout.simple_list_1, mainLst);
            //ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_list_1, mainLst);
            adapter.setParams(app_name, MAINVW);
            mListView.setAdapter(adapter);
           //mListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
          //  mListView.setClickable(true);


    }
}



