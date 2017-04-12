package com.rekhaninan.common;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.util.Log;
import android.content.Context;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import static com.rekhaninan.common.Constants.*;
import android.widget.AdapterView;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.support.v7.widget.Toolbar;

public class MainVwActivity extends AppCompatActivity {
    private static final String TAG="MainVwActivity";
    private ListView mListView;
    private String app_name;
    private String message;
    private int no_items;
    private String dbClassName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            Log.d(getClass().getSimpleName(), "Starting onCreate of MainVwActivity");
            super.onCreate(savedInstanceState);
            Intent intent = getIntent();
            String APP_NAME = "APP_NAME";
            message = intent.getStringExtra(APP_NAME);
            app_name = message;
            setContentView(R.layout.activity_main_vw);


            ShareMgr.getInstance().start_thr(this, app_name);

            PermissionsManager.getInstance().requestPermissionIfReqd(getApplicationContext(), this);
            dbClassName = "com.rekhaninan.common.";
            dbClassName +=   message;
            dbClassName += "DBIntf";
            Log.d(getClass().getSimpleName(), dbClassName);
            DBOperations.getInstance().initDb(dbClassName, this);
            java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(MAINVW);
            if (mainLst == null)
            {
                Log.d(getClass().getSimpleName(), "NULL main list");
                return;
            }
            DBOperations.getInstance().setApp_name(app_name);

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


        }

        catch (Exception e)
        {
            Log.d(getClass().getName(), "Cannot find reflection class name");
            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (app_name.equals(EASYGROC))
        {
            Log.d(TAG, "Inflating main_easygroc_menu");
            inflater.inflate(R.menu.main_easygroc_menu, menu);
        }
        else {
            Log.d(TAG, "Inflating main_menu");
            inflater.inflate(R.menu.main_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
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
        else if (item.getItemId() == R.id.templ_list_view)
        {
            Log.d (TAG, "Launching check lists");
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
        else
        {
                return super.onOptionsItemSelected(item);
        }
    }

    public void refreshVw()
    {
        Log.d(getClass().getSimpleName(), "Refreshing MainVw");
        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(MAINVW);
        if (mainLst == null)
        {
            Log.d(getClass().getSimpleName(), "NULL main list");
            return;
        }

        no_items = mainLst.size();

        ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);

        adapter.setParams(message, MAINVW);
        mListView.setAdapter(adapter);
        return;
    }

    @Override
    protected void onResume()
    {
        Log.d(getClass().getSimpleName(), "In onResume of MainVwActivity");
       refreshVw();
        super.onResume();
    }
}
