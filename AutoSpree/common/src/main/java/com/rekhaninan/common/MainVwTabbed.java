package com.rekhaninan.common;



import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

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
            adapter = new ArrayAdapterMainVw(getContext(), R.layout.simple_list_1, mainLst);
            //ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_list_1, mainLst);
            adapter.setParams(app_name, MAINVW);
            mListView.setAdapter(adapter);
           //mListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
          //  mListView.setClickable(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (app_name.equals(EASYGROC))
        {
            Log.d(TAG, "Inflating main_easygro_menu in MainVwTabbed");
            inflater.inflate(R.menu.add_help, menu);
        }
        else {
            Log.d(TAG, "Inflating main_menu");
            inflater.inflate(R.menu.add, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.add_new_item) {
            Log.d(getClass().getName(), "Creating new Item for app="+ app_name);

            Intent intent = new Intent(getActivity(), SingleItemActivity.class);
            SharedPreferences settings = getContext().getSharedPreferences("OHAutoSpree", Context.MODE_PRIVATE);

            switch (app_name) {
                case AUTOSPREE: {
                    intent.putExtra("ViewType", AUTOSPREE_ADD_ITEM);
                    String item_name = "Car";
                    int item_no = settings.getInt("item_no", 1);
                    item_name += item_no;
                    Item itm = new Item();
                    itm.setName(item_name);
                    itm.setShare_id(ShareMgr.getInstance().getShare_id());
                    intent.putExtra("item", itm);
                    startActivityForResult(intent, AUTOSPREE_ADD_ITEM_REQUEST);
                }
                break;

                case OPENHOUSES: {
                    intent.putExtra("ViewType", OPENHOUSES_ADD_ITEM);
                    String item_name = "House";
                    int item_no = settings.getInt("item_no", 1);
                    item_name += item_no;
                    Item itm = new Item();
                    itm.setName(item_name);
                    itm.setShare_id(ShareMgr.getInstance().getShare_id());
                    intent.putExtra("item", itm);
                    startActivityForResult(intent, OPENHOUSES_ADD_ITEM_REQUEST);
                }
                break;

                case EASYGROC: {
                    intent.putExtra("ViewType", EASYGROC_ADD_ITEM_OPTIONS);
                    Item itm = new Item();
                    intent.putExtra("item", itm);
                    startActivityForResult(intent, EASYGROC_ADD_ITEM_REQUEST);
                }
                break;

                default:

                    break;
            }

        }
        else if (item.getItemId() == R.id.help_screen) {
            showHelpScreen();
        }
        else if (item.getItemId() == R.id.alexa_button)
        {
            showAlexaDialog();
        }
        return true;
    }

    private void showAlexaDialog()
    {
        Log.d(TAG, "Showing Alexa dialog");
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Alexa code");

        String alexaMsg = "Enter the Alexa numerical code to link the App and Alexa skill";
        alertDialog.setMessage(alexaMsg);
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialog.setView(input);
// Set up the buttons
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String codeStr = input.getText().toString();
                        int code = Integer.parseInt(codeStr);

                        dialog.cancel();
                        ShareMgr.getInstance().getAlexaUserID(code);

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

    private void showHelpScreen()
    {
        Log.d(getClass().getName(), "Creating new Item for app=" + app_name);
        Intent intent = new Intent(getActivity(), SingleItemActivity.class);
        intent.putExtra("ViewType", HELP_SCREEN_VIEW);
        Item itm = new Item();
        intent.putExtra("item", itm);
        switch (app_name) {
            case AUTOSPREE: {

                intent.putExtra("HelpText", HelpTxt.AutoSpreeTxt);
            }
            break;

            case OPENHOUSES:
            {

                intent.putExtra("HelpText", HelpTxt.OpenHousesTxt);
            }
            break;

            case EASYGROC:{

                intent.putExtra("HelpText", HelpTxt.EasyGrocTxt);
            }
            break;

            default:

                break;
        }
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Processing Activity result for request Code=" + requestCode);
        switch (requestCode)
        {
            case AUTOSPREE_ADD_ITEM_REQUEST:
            case OPENHOUSES_ADD_ITEM_REQUEST:
            case AUTOSPREE_DISPLAY_ITEM_REQUEST:
            case OPENHOUSES_DISPLAY_ITEM_REQUEST:
            case EASYGROC_ADD_ITEM_REQUEST:

                if (resultCode == Activity.RESULT_OK)
                {
                    String refreshNeeded = data.getStringExtra("refresh");
                    if (refreshNeeded.equals("Needed"))
                    {
                        refresh();
                    }
                }
                break;

            default:
                break;
        }

    }

    public void refresh()
    {
        Log.d(TAG, "Refreshing Home View");
        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(MAINVW);
        adapter.setArryElems(mainLst);
        adapter.notifyDataSetChanged();
    }
}



