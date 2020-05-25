package com.rekhaninan.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import static com.rekhaninan.common.Constants.ADD_TEMPL_CHECKLIST_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.AUTOSPREE_ADD_ITEM;
import static com.rekhaninan.common.Constants.CONTACTS_MAINVW;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM_OPTIONS;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_LISTS;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_NAME_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_NAME_LISTS;
import static com.rekhaninan.common.Constants.OPENHOUSES;
import static com.rekhaninan.common.Constants.OPENHOUSES_ADD_ITEM;

public class PlannerVwTabbed extends Fragment {
    public String app_name;
    private ListView mListView;
    private final String TAG = "PlannerVwTabbed";

    public static final String ARG_OBJECT = "PlannerVwTabbed";
    private ExpandableListView mTemplNameView;
    private TemplNameAdapter templNameAdapter;
    private ArrayAdapterMainVw adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main_vw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        List<Item> mainLst = DBOperations.getInstance().getTemplNameLst();
        Log.i(TAG, "No of elements in Templ name list=" + mainLst.size());

        mListView = (ListView) view.findViewById(R.id.recipe_list_view);
        adapter = new ArrayAdapterMainVw(getActivity(), R.layout.simple_list_1, mainLst);
        adapter.setParams(app_name, EASYGROC_TEMPL_NAME_LISTS);
        adapter.setFragment(this);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (app_name.equals(EASYGROC))
        {
            Log.d(TAG, "Inflating main_easygro_menu in PlannerVwTabbed");
            inflater.inflate(R.menu.add_single_item, menu);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.add_new_item) {
            Log.d(getClass().getName(), "Creating new Item");


            switch (app_name) {


                case EASYGROC:
                    addTemplListName();
                    break;

                default:

                    break;
            }


        }
        return true;
    }

    private void addTemplListName()
    {


        switch (app_name) {

            case EASYGROC: {
                Log.d(TAG, "Adding template list item");
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("New Planner");

                String delMsg = "Please enter name of store ";
                alertDialog.setMessage(delMsg);
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                alertDialog.setView(input);
// Set up the buttons
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String name = input.getText().toString();
                                Item nameItem = new Item();
                                nameItem.setName(name);
                                nameItem.setShare_id(ShareMgr.getInstance().getShare_id());

                                if (DBOperations.getInstance().itemExists(nameItem, EASYGROC_TEMPL_ADD_ITEM)) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                    alertDialog.setTitle("Error");
                                    String err = "Store " + nameItem.getName() + " exists. Choose different name";
                                    alertDialog.setMessage(err);
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    return;
                                                }
                                            });
                                    alertDialog.show();
                                    return;
                                }

                                String templateName = input.getText().toString();
                                dialog.cancel();
                                Item templNameItm = new Item();
                                templNameItm.setName(templateName);
                                DBOperations.getInstance().insertDb(templNameItm, EASYGROC_TEMPL_NAME_ADD_ITEM);
                                List<Item>  mainLst = DBOperations.getInstance().getTemplNameLst();
                                adapter.setArryElems(mainLst);
                                adapter.notifyDataSetChanged();


                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }
            break;



            default:
                Log.d(TAG, "Invalid app_name " + app_name);
                break;

        }
        return;
    }

    }
