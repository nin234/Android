package com.rekhaninan.common;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.widget.ListView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.google.android.material.tabs.TabLayout;

import static com.rekhaninan.common.Constants.*;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainVwActivity extends AppCompatActivity {
    private static final String TAG="MainVwActivity";
    private ListView mListView;
    private String app_name;
    private String message;
    private int no_items;
    private String dbClassName;
    private TabbedCollectionAdapter adapter;

    ViewPager2 viewPager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            Log.d(getClass().getSimpleName(), "Starting onCreate of MainVwActivity");
            super.onCreate(savedInstanceState);
            Intent intent = getIntent();

            Log.d(getClass().getSimpleName(), "Starting onCreate of MainVwActivity1");
            String APP_NAME = "APP_NAME";
            message = intent.getStringExtra(APP_NAME);
            app_name = message;
            if (app_name.equals(EASYGROC)) {
                initializeAmplify();
            }
            PermissionsManager.getInstance().requestPermissionIfReqd(getApplicationContext(), this);
            dbClassName = "com.rekhaninan.common.";
            dbClassName +=   message;
            dbClassName += "DBIntf";
            Log.d(getClass().getSimpleName(), dbClassName);
            DBOperations.getInstance().initDb(dbClassName, this);
            ShareMgr.getInstance().start_thr(this, app_name);
            Log.d(TAG, "Getting main list from DB MAINVW=" + MAINVW);
            DBOperations.getInstance().setApp_name(app_name);
            //setContentView(R.layout.activity_main_vw);

            setContentView(R.layout.tabbed_main);
            viewPager = findViewById(R.id.pager);

            tabLayout = findViewById(R.id.tab_layout);

             adapter = new TabbedCollectionAdapter(this);
            adapter.appName = app_name;
            viewPager.setAdapter(adapter);
            new TabLayoutMediator(tabLayout, viewPager,
                    new TabLayoutMediator.TabConfigurationStrategy() {
                        @Override public void onConfigureTab(TabLayout.Tab tab, int position) {
                            Log.d(getClass().getSimpleName(), "Configuring tab at" + position);
                            switch (position)
                            {
                                case SHARE_POSN:
                                    tab.setText("Share");
                                    break;

                                case CONTACTS_POSN:
                                    tab.setText("Contacts");
                                    break;

                                case PLANNER_POSN:
                                    tab.setText("Planner");
                                    break;
                                case HOME_POSN:
                                    tab.setText("Home");
                                    break;
                                default:
                                    tab.setText("Invalid Posn");
                                    break;
                            }

                        }
                    }).attach();

            viewPager.setCurrentItem(HOME_POSN);



            /*
            java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(MAINVW);
            if (mainLst == null)
            {
                Log.d(getClass().getSimpleName(), "NULL main list");
                return;
            }


            no_items = mainLst.size();

            mListView = (ListView) findViewById(R.id.recipe_list_view);
// 1
// 4
            ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
            //ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_list_1, mainLst);
            adapter.setParams(message, MAINVW);
            mListView.setAdapter(adapter);
           //mListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
          //  mListView.setClickable(true);

    */
        }

        catch (Exception e)
        {
            Log.e(getClass().getName(), "Exception2 in onCreate of MainVwActivity " + e.getMessage(), e);
            return;
        }

    }

    public void refresh()
    {
        adapter.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        MenuInflater inflater = getMenuInflater();
        if (app_name.equals(EASYGROC))
        {
            Log.d(TAG, "Inflating main_easygro_menu");
            inflater.inflate(R.menu.main_easygroc_menu, menu);
        }
        else {
            Log.d(TAG, "Inflating main_menu");
            inflater.inflate(R.menu.main_menu, menu);
        }

         */
        return true;
    }

    private void initializeAmplify()
    {
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.configure(getApplicationContext());

            android.util.Log.i("EasyGrocAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException e) {
            android.util.Log.e("EasyGrocAmplifyApp", "Could not initialize Amplify", e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        /*
        if (item.getItemId() == R.id.add_item)
        {
            Log.d(getClass().getName(), "Creating new Item");

            Intent intent = new Intent(this, SingleItemActivity.class);
            SharedPreferences settings = getSharedPreferences("OHAutoSpree", Context.MODE_PRIVATE);

            switch (app_name)
            {
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
                    break;

                default:

                    break;
            }
            startActivity(intent);
            return true;
        }

        else if (item.getItemId() == R.id.templ_list_view)
        {
            Log.d (TAG, "Launching template lists");
            Intent intent = new Intent(this, SingleItemActivity.class);
            intent.putExtra("ViewType", EASYGROC_TEMPL_LISTS);
            Item itm = new Item();
            intent.putExtra("item", itm);
            startActivity(intent);
            return true;
        }
        */

         if (item.getItemId() == R.id.check_list_view)
        {
            Log.d (TAG, "Launching template lists");
            Intent intent = new Intent(this, SingleItemActivity.class);
            intent.putExtra("ViewType", EASYGROC_TEMPL_LISTS);
            Item itm = new Item();
            intent.putExtra("item", itm);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.share)
        {
            Log.d (TAG, "Starting share activity");
            Intent intent = new Intent(this, ShareActivity.class);
            intent.putExtra("app_name", app_name);
            intent.putExtra("ViewType", SHARE_MAINVW);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.sort)
        {
            Log.d (TAG, "Starting share activity");
            Intent intent = new Intent(this, SortActivity.class);
            intent.putExtra("app_name", app_name);
            intent.putExtra("ViewType", SORT_MAINVW);
            startActivity(intent);
            return true;
        }
        else
        {
                return super.onOptionsItemSelected(item);
        }
    }

    public void refreshVw()
    {
        setContentView(R.layout.activity_main_vw);
        Log.d(getClass().getSimpleName(), "Refreshing MainVw");
        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(MAINVW);
        if (mainLst == null)
        {
            Log.d(getClass().getSimpleName(), "NULL main list");
            return;
        }

        no_items = mainLst.size();

        ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
        mListView = (ListView) findViewById(R.id.recipe_list_view);
        adapter.setParams(message, MAINVW);
        mListView.setAdapter(adapter);
        return;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getClass().getSimpleName(), "Processing activity result with request code=" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case EASYGROC_TEMPL_DISPLAY_ITEM:
                if (resultCode == RESULT_OK)
                {
                    String refreshNeeded = data.getStringExtra("refresh");
                    if (refreshNeeded.equals("Needed"))
                    {
                        refresh();
                    }
                }
                break;
            case EASYGROC_DELETE_ITEM_REQUEST:
                if (resultCode == RESULT_OK)
                {
                    String refreshNeeded = data.getStringExtra("refresh");
                    if (refreshNeeded.equals("Needed"))
                    {
                        adapter.refreshMainVw();
                    }
                }
                break;

            case DELETE_CONTACT_ITEM_ACTIVITY_REQUEST:

                if (resultCode == RESULT_OK)
                {
                    String refreshNeeded = data.getStringExtra("refresh");
                    if (refreshNeeded.equals("Needed"))
                    {
                        adapter.refreshContactVw();
                    }
                }
                break;
            default:
                break;
        }

    }
    @Override
    protected void onResume()
    {
        Log.d(getClass().getSimpleName(), "In onResume of MainVwActivity1");
       //refreshVw();
        /*
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            Log.i(TAG, "Retrieving items from server");
           // ShareMgr.getInstance().getItems();
            for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    Log.d(TAG, String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));
                }

        }
        */
        super.onResume();
    }
}
