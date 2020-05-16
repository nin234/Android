package com.rekhaninan.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.rekhaninan.common.Constants.CONTACTS_MAINVW;

public class ContactsVwTabbed extends Fragment {

    public String app_name;
    private ListView mListView;
    private final String TAG = "ContactsVwTabbed";

    public static final String ARG_OBJECT = "ContactsVwTabbed";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main_vw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(CONTACTS_MAINVW);
        mListView = (ListView) view.findViewById(R.id.recipe_list_view);
        ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(getContext(), R.layout.simple_list_1, mainLst);
        adapter.setParams(app_name, CONTACTS_MAINVW);
        mListView.setAdapter(adapter);
    }
    }
