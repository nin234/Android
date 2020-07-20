package com.rekhaninan.common;



import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    private static final String helpText = "Create Planner lists. Click planner icon in the bottom tab bar to " +
            "go to the Planner section of the App.\n\nCreate a new planner by clicking the + button on the top right " +
            "hand corner. Enter the name of the store in the text box. Planners can be created for multiple stores.\n\nTo add " +
            "items to planner, select the newly created planner item on the screen.\nThe planner list of a store is made up " +
            "of three different sections (One-time, Replenish and Always). To switch between the sections click the buttons " +
            "on the top navigation bar. Click the Edit button on top right corner to make changes to planner list and " +
            "click the Save button to save the changes\n\nReplenish list keeps track of items that needs to be bought when " +
            "they run out. The switch in the off position (red color) indicates that particular item has run out. When a list " +
            "is created from the Home screen this item will be added to the list.\nAlways list are the items that are needed " +
            "on every store visit. \n\nOne-time items are infrequently needed items. The items in this list are used the next " +
            "time a new list is created from the Home screen. The items in the list are deleted after a new list is created " +
            "and cannot be used again.\n\n After creating planner lists click the + button on the top right corner of the Home" +
            " screen. Create a new list by selecting the appropriate Planner list. A new list created  from the planner list " +
            "will merge items from these 3 components (Always, Replenish and One-time).\n\nClicking the brand new list on this" +
            " screen will create an empty blank list. This can be used for one time lists.\n\n Alexa Integration. EasyGrocList" +
            " iPhone App is integrated with EasyGrocList Alexa skill. Enable EasyGrocList Alexa skill. Refer to the skill" +
            " documentation on how to use the skill. To link the iPhone App and Alexa skill, press the A button on the top " +
            "right corner and enter the numerical code spoken by Alexa when the skill is invoked the first time. Invoke th" +
            "e Alexa skill again to start adding items. It is important to note that the Alexa added items are downloaded to " +
            "the iPhone App only at start up. If Alexa added items are not seen in the list restart the App\n\nThe list can be " +
            "shared with friends. Notifications should be enabled for the app for sharing. Notifications can be enabled during " +
            "intial start up or later in the Settings app.\n\n The first step to share is to add Contacts to share the list " +
            "with. Click the Contacts icon in the bottom tab bar to bring up the Contacts screen. There will be a ME line. " +
            "Selecting the ME line, shows the share Id of the EasyGrocList on this iPhone. This number uniquely identifies " +
            "the App for sharing purposes. Now navigate back to Contacts screen by clicking the Contacts button on top left" +
            " corner. Click the + button on top right corner to add a new contact. Enter the share Id and a name to identify " +
            "the contact.The Share Id is the number in the ME row of your friend's EasyGrocList app. \n\nClick the Share icon " +
            "in the bottom tab bar. This will bring up the Share screen. Select the List to share and click the Recipients " +
            "icon on the top right corner. This will bring up the Contacts screen. Select the contacts to share the item. Once" +
            " the contacts are selected click the Send button. This will sent the list to the selected Contacts";

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
            inflater.inflate(R.menu.add_single_item, menu);
        }
        else {
            Log.d(TAG, "Inflating main_menu");
            inflater.inflate(R.menu.main_menu, menu);
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
                    intent.putExtra("item", itm);
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
                }
                break;

                case EASYGROC:
                    intent.putExtra("ViewType", EASYGROC_ADD_ITEM_OPTIONS);
                    Item itm = new Item();
                    intent.putExtra("item", itm);
                    startActivityForResult(intent, EASYGROC_ADD_ITEM_REQUEST);
                    return true;

                default:

                    break;
            }
            startActivity(intent);

        }
        else if (item.getItemId() == R.id.help_screen) {
            Log.d(getClass().getName(), "Creating new Item for app=" + app_name);
            Intent intent = new Intent(getActivity(), SingleItemActivity.class);
            intent.putExtra("ViewType", HELP_SCREEN_VIEW);
            Item itm = new Item();
            intent.putExtra("item", itm);
            intent.putExtra("HelpText", helpText);
            startActivity(intent);
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Processing Activity result for request Code=" + requestCode);
        switch (requestCode)
        {
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



