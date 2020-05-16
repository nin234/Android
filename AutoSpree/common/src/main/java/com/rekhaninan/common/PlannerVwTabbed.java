package com.rekhaninan.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_LISTS;

public class PlannerVwTabbed extends Fragment {
    public String app_name;
    private ListView mListView;
    private final String TAG = "PlannerVwTabbed";

    public static final String ARG_OBJECT = "PlannerVwTabbed";
    private ExpandableListView mTemplNameView;
    private TemplNameAdapter templNameAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.templ_name_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        List<Item> mainLst = DBOperations.getInstance().getTemplNameLst();
        Log.i(TAG, "No of elements in Templ name list=" + mainLst.size());
        mTemplNameView = (ExpandableListView) view.findViewById(R.id.templName);
        templNameAdapter = new TemplNameAdapter(mainLst, getContext());
        mTemplNameView.setAdapter(templNameAdapter);
    }

    }
