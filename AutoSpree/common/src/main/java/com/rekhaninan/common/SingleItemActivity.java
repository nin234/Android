package com.rekhaninan.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.File;
import java.util.List;

import android.os.ResultReceiver;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import static com.rekhaninan.common.Constants.*;

public class SingleItemActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener {

    private ListView mListView;
    private ExpandableListView mTemplNameView;
    private DynamicListView mListViewDynmic;
    private ArrayAdapterMainVw adapter;
    private TemplNameAdapter templNameAdapter;
    private long rightNow;

    int viewType;
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    protected GoogleApiClient mGoogleApiClient;
    private final String TAG = "SingleItemActivity";
    private ArrayList<Item> checkLst;
    private String m_TemplateName = "";

    private Item itm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkLst = new ArrayList<>();

        Intent intent = getIntent();
        viewType = intent.getIntExtra("ViewType", 0);

        Log.d(getClass().getName(), "in onCreate");
        mResultReceiver = new AddressResultReceiver(new Handler());
        java.util.List<Item> mainLst = new ArrayList<Item>();
        itm =  intent.getParcelableExtra("item");


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        rightNow = System.currentTimeMillis();


        Log.i(getClass().getName(), "Added name to mainLst " + itm.getName());
        switch (viewType) {
            case HELP_SCREEN_VIEW:
                setUpHelpScreen(intent);
                break;
            case AUTOSPREE_ADD_ITEM:
            case OPENHOUSES_ADD_ITEM: {
                setContentView(R.layout.activity_single_item);
                itm.setAlbum_name(Long.toString(rightNow));
                itm.setNotes(" ");

                for (int i=0; i < 14; ++i)
                    mainLst.add(itm);

                mListView = (ListView) findViewById(R.id.add_item_view);

                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
               // adapter.setNotifyOnChange(false);

                if (viewType == OPENHOUSES_ADD_ITEM) {
                    adapter.setParams(OPENHOUSES, OPENHOUSES_ADD_ITEM);
                } else {
                    adapter.setParams(AUTOSPREE, AUTOSPREE_ADD_ITEM);
                }
                mListView.setAdapter(adapter);
                buildGoogleApiClient();
            }
            break;

            case OPENHOUSES_DISPLAY_ITEM:
            case OPENHOUSES_EDIT_ITEM: {

                setContentView(R.layout.activity_single_item);
                for (int i=0; i < 13; ++i)
                    mainLst.add(itm);
                if (viewType == OPENHOUSES_EDIT_ITEM)
                    mainLst.add(itm);
                LayoutInflater inflater = (LayoutInflater) this.getApplicationContext().getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                mListView = (ListView) findViewById(R.id.add_item_view);
                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
               // adapter.setNotifyOnChange(false);
                 checkLst = intent.getParcelableArrayListExtra("check_list");
                 adapter.setCheckList(checkLst);
                if (viewType == OPENHOUSES_DISPLAY_ITEM)
                    adapter.setParams(OPENHOUSES, OPENHOUSES_DISPLAY_ITEM);
                else if (viewType == OPENHOUSES_EDIT_ITEM)
                    adapter.setParams(OPENHOUSES, OPENHOUSES_EDIT_ITEM);
                mListView.setAdapter(adapter);

            }
            break;

            case AUTOSPREE_DISPLAY_ITEM:
            case AUTOSPREE_EDIT_ITEM: {
                setContentView(R.layout.activity_single_item);
                for (int i=0; i < 13; ++i)
                    mainLst.add(itm);
                if (viewType == AUTOSPREE_EDIT_ITEM)
                    mainLst.add(itm);

                LayoutInflater inflater = (LayoutInflater) this.getApplicationContext().getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                mListView = (ListView) findViewById(R.id.add_item_view);
                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                checkLst = intent.getParcelableArrayListExtra("check_list");
                adapter.setCheckList(checkLst);
              //  adapter.setNotifyOnChange(false);
                if (viewType == AUTOSPREE_DISPLAY_ITEM)
                    adapter.setParams(AUTOSPREE, AUTOSPREE_DISPLAY_ITEM);
                else if (viewType == AUTOSPREE_EDIT_ITEM)
                    adapter.setParams(AUTOSPREE, AUTOSPREE_EDIT_ITEM);
                mListView.setAdapter(adapter);

            }
            break;

            case CHECK_LIST_ADD:
            {
                setContentView(R.layout.activity_single_item);
                if (itm.getName() != null && itm.getName().length() > 0)
                {
                    java.util.List<Item> templList = DBOperations.getInstance().getTemplList(itm.getName(), itm.getShare_id());
                    if (templList != null)
                        mainLst.addAll(templList);

                }

                mListView = (ListView) findViewById(R.id.add_item_view);
                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(EASYGROC, CHECK_LIST_ADD);
                mListView.setAdapter(adapter);

            }
            break;

            case CHECK_LIST_EDIT:
            {
                setContentView(R.layout.activity_single_item);
                checkLst = intent.getParcelableArrayListExtra("check_list");
                mainLst = checkLst;
                mListView = (ListView) findViewById(R.id.add_item_view);
                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(EASYGROC, CHECK_LIST_EDIT);
                mListView.setAdapter(adapter);

            }
            break;

            case EASYGROC_TEMPL_DELETE_ITEM:
            {
                createTemplDeleteAdapter();
            }
            break;

            case EASYGROC_ADD_ITEM:
            {
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                Calendar c = new GregorianCalendar();
                String formattedDate = df.format(c.getTime());

                setContentView(R.layout.activity_single_item);
                String app_name = DBOperations.getInstance().getApp_name();
                int rowno=0;
                if (itm.getName() != null && itm.getName().length() > 0)
                {
                    java.util.List<Item> templList = DBOperations.getInstance().getTemplList(itm.getName(), itm.getShare_id());
                    Item titleItem = new Item();
                    titleItem.setName(itm.getName() + " " + formattedDate);
                    titleItem.setRowno(rowno++);
                    mainLst.add(titleItem);
                    int month = c.get(Calendar.MONTH);
                    for (Item litm : templList)
                    {
                        boolean bContinue = false;
                        if (app_name.equals(EASYGROC)) {
                            if (litm.getEnd_month() > litm.getStart_month()) {
                                if (month > litm.getEnd_month() || month < litm.getStart_month())
                                    bContinue = true;

                            } else if (litm.getEnd_month() == litm.getStart_month()) {
                                if (month != litm.getEnd_month())
                                    bContinue = true;
                            } else {
                                if (month < litm.getStart_month() && month > litm.getEnd_month())
                                    bContinue = true;
                            }

                            if (bContinue)
                                continue;
                        }
                        litm.setRowno(rowno++);
                        mainLst.add(litm);

                    }

                    if (app_name.equals(EASYGROC)) {

                        java.util.List<Item> templInvList = DBOperations.getInstance().getTemplList(itm.getName() + ":INV", itm.getShare_id());
                        Log.i(TAG, "No of elements in inventory list for " +itm.getName() + ":INV for=" + templInvList.size() );
                        for (Item invItem : templInvList) {
                            if (invItem.getInventory() > 0)
                                continue;
                            invItem.setRowno(rowno++);
                            mainLst.add(invItem);
                        }
                        java.util.List<Item> templScrList = DBOperations.getInstance().getTemplList(itm.getName() + ":SCRTCH", itm.getShare_id());
                        for (Item scrItem : templScrList) {
                            scrItem.setRowno(rowno++);
                            mainLst.add(scrItem);
                        }
                    }

                    mListView = (ListView) findViewById(R.id.add_item_view);
                    adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                    adapter.setParams(EASYGROC, CHECK_LIST_TEMPL_SELECTOR);
                    mListView.setAdapter(adapter);
                }
                else {
                    for (int i = 0; i < 12; ++i) {
                        Item templItm = new Item();
                        if (i==0)
                        {
                            templItm.setName("List " + formattedDate);
                        }
                        templItm.setRowno(i);
                        mainLst.add(templItm);
                    }
                }
                mListView = (ListView) findViewById(R.id.add_item_view);
                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(EASYGROC, EASYGROC_ADD_ITEM);
                mListView.setAdapter(adapter);
            }
            break;

            case CHECK_LIST_TEMPL_SELECTOR:
            {
                setContentView(R.layout.activity_single_item);
                for (int i=0; i < 2; ++i)
                {
                    mainLst.add(itm);
                }

                java.util.List<Item> templNameLst = DBOperations.getInstance().getTemplNameLst();
                mainLst.addAll(templNameLst);
                mListView = (ListView) findViewById(R.id.add_item_view);
                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(EASYGROC, CHECK_LIST_TEMPL_SELECTOR);
                mListView.setAdapter(adapter);
            }
            break;

            case EASYGROC_ADD_ITEM_OPTIONS:
            {
                setContentView(R.layout.activity_single_item);
                for (int i=0; i < 3; ++i)
                {
                    mainLst.add(itm);
                }

                java.util.List<Item> templNameLst = DBOperations.getInstance().getTemplNameLst();
                mainLst.addAll(templNameLst);
                mListView = (ListView) findViewById(R.id.add_item_view);
                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(EASYGROC, EASYGROC_ADD_ITEM_OPTIONS);
                mListView.setAdapter(adapter);
            }
            break;

            case CHECK_LIST_DISPLAY:
            {
                setContentView(R.layout.activity_single_item);
                checkLst = intent.getParcelableArrayListExtra("check_list");
                mainLst.addAll(checkLst);
                mListView = (ListView) findViewById(R.id.add_item_view);
                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(EASYGROC, CHECK_LIST_DISPLAY);
                mListView.setAdapter(adapter);
            }
            break;

            case EASYGROC_DISPLAY_ITEM:
            {
                if (itm.getPicurl() != null && itm.getPicurl().length() > 0)
                {
                    ArrayList<String>  images = new ArrayList<>();
                    images.add(itm.getPicurl());
                    Intent intnt = new Intent(this, ImageSwipeActivity.class);
                    intnt.putExtra("images", images);
                    intnt.putExtra("position", 0);
                    intnt.putExtra("App", EASYGROC);
                    Activity itemAct = (Activity) this;
                    itemAct.startActivityForResult(intnt, IMAGE_SWIPE_ACTIVITY_REQUEST);
                }
                else {
                    setContentView(R.layout.activity_single_item);
                    mainLst.add(itm);
                    java.util.List<Item> list = DBOperations.getInstance().getList(itm.getName(), itm.getShare_id());
                    mainLst.addAll(list);
                    mListView = (ListView) findViewById(R.id.add_item_view);
                    adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                    adapter.setParams(EASYGROC, EASYGROC_DISPLAY_ITEM);
                    mListView.setAdapter(adapter);
                }
            }
            break;

            case EASYGROC_TEMPL_LISTS:
            {
               setTemplNameLists();
            }
            break;

            case EASYGROC_TEMPL_DISPLAY_ITEM:
            {
                setContentView(R.layout.activity_single_item);
                mainLst.add(itm);
                java.util.List<Item> list = DBOperations.getInstance().getTemplList(itm.getName(), itm.getShare_id());
                mainLst.addAll(list);
                mListView = (ListView) findViewById(R.id.add_item_view);
                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(EASYGROC, EASYGROC_TEMPL_DISPLAY_ITEM);
                if (!itm.getName().endsWith(":INV") && !itm.getName().endsWith(":SCRTCH"))
                    adapter.setRecrLst(true);
                mListView.setAdapter(adapter);
            }
            break;

            case EASYGROC_TEMPL_EDIT_ITEM:
            {
                setContentView(R.layout.activity_single_item);
                mainLst.add(itm);
                java.util.List<Item> list = DBOperations.getInstance().getTemplList(itm.getName(), itm.getShare_id());
                mainLst.addAll(list);
                mListView = (ListView) findViewById(R.id.add_item_view);
                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(EASYGROC, EASYGROC_TEMPL_EDIT_ITEM);
                if (!itm.getName().endsWith(":INV") && !itm.getName().endsWith(":SCRTCH"))
                    adapter.setRecrLst(true);
                mListView.setAdapter(adapter);

            }
            break;

            case EASYGROC_EDIT_ITEM:
            {
                Log.i(TAG, "In EASYGROC_EDIT_ITEM");
                setContentView(R.layout.activity_single_item);
                mainLst.add(itm);
                java.util.List<Item> list = DBOperations.getInstance().getList(itm.getName(), itm.getShare_id());
                mainLst.addAll(list);
                mListView = (ListView) findViewById(R.id.add_item_view);
                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(EASYGROC, EASYGROC_EDIT_ITEM);
                mListView.setAdapter(adapter);


            }
            break;

            case EASYGROC_TEMPL_ADD_ITEM:
            {
                setContentView(R.layout.activity_single_item);
                mainLst.add(itm);
                for (int i=0; i <12; ++i) {
                    Item templItm = new Item();
                    templItm.setRowno(i);
                    mainLst.add(templItm);
                }
                    mListView = (ListView) findViewById(R.id.add_item_view);
                    adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                    adapter.setParams(EASYGROC, EASYGROC_TEMPL_ADD_ITEM);
                  if (itm.getName() != null && !itm.getName().endsWith(":INV") && !itm.getName().endsWith(":SCRTCH"))
                        adapter.setRecrLst(true);
                    mListView.setAdapter(adapter);

            }
            break;

            default:

                break;
        }


    }

    private void setUpHelpScreen(Intent intent)
    {
        setContentView(R.layout.help_screen);
        final TextView helpTextView = (TextView) findViewById(R.id.help_text_id);
        String helpText = intent.getStringExtra("HelpText");
        helpTextView.setText(helpText);
        helpTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    private void createTemplDeleteAdapter()
    {
        setContentView(R.layout.activity_main_vw);
        java.util.List<Item> mainLst = DBOperations.getInstance().getTemplNameLst();
        if (mainLst == null) {
            Log.d(TAG, "NULL main list");
            return;
        }

        mListView = (ListView) findViewById(R.id.recipe_list_view);
        ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
        adapter.setParams(EASYGROC, EASYGROC_TEMPL_DELETE_ITEM);
        mListView.setAdapter(adapter);
    }

    private void setTemplNameLists()
    {
        String app_name = DBOperations.getInstance().getApp_name();

        switch (app_name) {

            case EASYGROC: {
                setContentView(R.layout.templ_name_layout);
               List<Item>  mainLst = DBOperations.getInstance().getTemplNameLst();
                Log.i(TAG, "No of elements in Templ name list=" + mainLst.size());
                mTemplNameView = (ExpandableListView) findViewById(R.id.templName);
                templNameAdapter = new TemplNameAdapter(mainLst, this);
                mTemplNameView.setAdapter(templNameAdapter);

            }
            break;

            default: {
                setContentView(R.layout.activity_single_item);
                java.util.List<Item> mainLst = DBOperations.getInstance().getTemplNameLst();
                mListView = (ListView) findViewById(R.id.add_item_view);
                adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(EASYGROC, EASYGROC_TEMPL_LISTS);
                mListView.setAdapter(adapter);
            }
            break;

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void startIntentService() {
        Intent loc_intent = new Intent(this, FetchAddressIntentService.class);
        loc_intent.putExtra(Constants.RECEIVER, mResultReceiver);
        loc_intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(loc_intent);
        return;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient != null &&  mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case EASYGROC_BRAND_NEW_ADD_REQUEST:
            {
                if (resultCode == RESULT_OK) {
                    String refreshNeeded = data.getStringExtra("refresh");
                    Intent intent = new Intent();
                    intent.putExtra("refresh", refreshNeeded);
                    setResult(Activity.RESULT_OK, intent);
                    finish();

                }
            }
            break;

            case  NOTES_ACTIVITY_REQUEST:
            {
                if (resultCode == RESULT_OK) {
                    String notes = data.getStringExtra("notes_value");
                    itm.setNotes(notes);
                    Log.d(TAG, "Notes changed to " + notes);
                }
            }
            break;
            case  PICTURE_ACTIVITY_REQUEST:
            {
               processPictureActivityRequest(resultCode, data);
            }
            break;

            case  IMAGE_SWIPE_ACTIVITY_REQUEST:
            {
                if (resultCode == RESULT_OK) {
                    finish();
                }
            }
            break;

            case ADD_CHECK_LIST_ACTIVITY_REQUEST_2:
            {
                processChkListActivityReq2(resultCode, data);
                finish();

            }
            break;

            case EDIT_CHECK_LIST_ACTIVITY_REQUEST:
            case ADD_CHECK_LIST_ACTIVITY_REQUEST:
            {
                processChkListActivityReq (resultCode, data);
              //  finish();
            }
            break;

            case DELETE_TEMPL_CHECKLIST_ACTIVITY_REQUEST:
            case ADD_TEMPL_CHECKLIST_ACTIVITY_REQUEST:
            case DELETE_TEMPL_ACTIVITY_REQUEST:
            {
                finish();
            }
            break;

            default:
                break;
        }
    }

    private void processChkListActivityReq (int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            checkLst = data.getParcelableArrayListExtra("check_list");
            adapter.setCheckList(checkLst);
            adapter.setbCheckListChg(true);
            adapter.notifyDataSetChanged();
        }
    }

    private void processChkListActivityReq2 (int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent();
            ArrayList<Item> chkLst = data.getParcelableArrayListExtra("check_list");

            if (chkLst.size() > 0) {
                intent.putParcelableArrayListExtra("check_list", chkLst);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
            setResult(RESULT_OK, intent);
        }
    }

    private void processPictureActivityRequest(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            itm.setPicurl(data.getStringExtra("pic_url"));
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
            Calendar c = Calendar.getInstance();
            String formattedDate = df.format(c.getTime());
            itm.setName("List " + formattedDate);
            DBOperations.getInstance().insertDb(itm, EASYGROC_ADD_ITEM);

            //5492347934
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, "Geocoder not available ", Toast.LENGTH_LONG).show();
                return;
            }
            // It is possible that the user presses the button to get the address before the
            // GoogleApiClient object successfully connects. In such a case, mAddressRequested
            // is set to true, but no attempt is made to fetch the address (see
            // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
            // user has requested an address, since we now have a connection to GoogleApiClient.
            startIntentService();
        }

    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        switch (viewType) {

            case AUTOSPREE_ADD_ITEM:
            case OPENHOUSES_ADD_ITEM:
            case AUTOSPREE_EDIT_ITEM:
            case OPENHOUSES_EDIT_ITEM:
                inflater.inflate(R.menu.add_item, menu);
                break;

            case OPENHOUSES_DISPLAY_ITEM:
            case AUTOSPREE_DISPLAY_ITEM:
                inflater.inflate(R.menu.edit_item, menu);
                break;

            case EASYGROC_TEMPL_LISTS: {
                String app_name = DBOperations.getInstance().getApp_name();
                if (app_name.equals(EASYGROC))
                {
                    inflater.inflate(R.menu.easygroc_templ_main, menu);
                }
                else {
                    inflater.inflate(R.menu.add_single_item, menu);
                }
            }
                break;

            case EASYGROC_TEMPL_DISPLAY_ITEM:
                inflater.inflate(R.menu.easygroc_templ_edit, menu);
                break;

            case EASYGROC_DISPLAY_ITEM:
                inflater.inflate(R.menu.easygroc_edit, menu);
                break;

            case EASYGROC_TEMPL_DELETE_ITEM:
                inflater.inflate(R.menu.easygroc_templ_delete, menu);
                break;

            case EASYGROC_TEMPL_ADD_ITEM:
            case EASYGROC_TEMPL_EDIT_ITEM:
            case EASYGROC_ADD_ITEM:
            case EASYGROC_EDIT_ITEM:
                inflater.inflate(R.menu.add_item, menu);
                break;
        }
        return true;
    }


    private void ASpreeOHAddItemDone()
    {
        Log.d(TAG, "Inserting into Db ");
        itm.setShare_id(ShareMgr.getInstance().getShare_id());
        DBOperations.getInstance().insertDb(itm, viewType);
        SharedPreferences settings = getSharedPreferences("OHAutoSpree", Context.MODE_PRIVATE);
        int item_no = settings.getInt("item_no", 1);
        ++item_no;
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("item_no", item_no);
        editor.commit();
        int i=0;
        for (Item item : checkLst)
        {
            item.setName(itm.getName());
            item.setRowno(i);
            item.setShare_id(ShareMgr.getInstance().getShare_id());
            DBOperations.getInstance().insertDb(item, EASYGROC_ADD_ITEM);
            ++i;
        }
        Intent intent = new Intent();
        intent.putExtra("refresh", "Needed");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void
    ASpreeOHEditItemDone()
    {
        Log.d(TAG, "Updating Db ");
        DBOperations.getInstance().updateDb(itm, viewType);
        int i=0;
        DBOperations.getInstance().deleteDb(itm, EASYGROC_EDIT_ITEM);
        for (Item item : checkLst)
        {
            item.setName(itm.getName());
            item.setShare_id(itm.getShare_id());
            DBOperations.getInstance().insertDb(item, EASYGROC_EDIT_ITEM);

            ++i;
        }
        startDisplayActivity();

    }

    private boolean EasyGrocAddItemDone()
    {
        ArrayAdapterMainVw adapterMainVw = (ArrayAdapterMainVw) mListView.getAdapter();
        List<Item> arryEl = adapterMainVw.getArryElems();
        Item nameItem = arryEl.get(0);

        if (nameItem.getName() == null || nameItem.getName().length() == 0 ) {
            AlertDialog alertDialog = new AlertDialog.Builder(SingleItemActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Name needed for list.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            return;
                        }
                    });
            alertDialog.show();
            return true;
        }

        if (DBOperations.getInstance().itemExists(nameItem, EASYGROC_ADD_ITEM)) {
            AlertDialog alertDialog = new AlertDialog.Builder(SingleItemActivity.this).create();
            alertDialog.setTitle("Error");
            String err = "List " + nameItem.getName() + " exists. Choose different name";
            alertDialog.setMessage(err);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            return;
                        }
                    });
            alertDialog.show();
            return true;
        }

        String name = nameItem.getName();
        int i=0;

        for (Item itm : arryEl)
        {
            if (i == 0) {
                ++i;
                continue;
            }
            if (itm.getItem() == null || itm.getItem().length() <= 0)
            {
                ++i;
                continue;
            }
            itm.setName(name);
            itm.setRowno(i);
            itm.setShare_id(ShareMgr.getInstance().getShare_id());
            DBOperations.getInstance().insertDb(itm, viewType);

            ++i;
        }

        Intent intent = new Intent();
        intent.putExtra("refresh", "Needed");
        setResult(Activity.RESULT_OK, intent);
        finish();
        return true;

    }

    private boolean easyGrocTemplAddItemDone()
    {
        ArrayAdapterMainVw adapterMainVw = (ArrayAdapterMainVw) mListView.getAdapter();
        List<Item> arryEl = adapterMainVw.getArryElems();
        Item nameItem = arryEl.get(0);

        if (nameItem.getName() == null || nameItem.getName().length() == 0 ) {
            AlertDialog alertDialog = new AlertDialog.Builder(SingleItemActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Name needed for list.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            return;
                        }
                    });
            alertDialog.show();
            return true;
        }



        String name = nameItem.getName();
        int i=0;

        for (Item itm : arryEl)
        {
            if (i == 0) {
                ++i;
                continue;
            }
            itm.setName(name);
            if (itm.getItem() == null || itm.getItem().length() <= 0)
            {
                ++i;
                continue;
            }
            itm.setRowno(i);

            DBOperations.getInstance().insertDb(itm, viewType);

            ++i;
        }
        String app_name = DBOperations.getInstance().getApp_name();
        if (app_name.equals(EASYGROC)) {
            startEasyGrocTemplDisplayActivity(nameItem);
        }
        else
        {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }

        return true;

    }

    private void easyGrocEditItemDone()
    {
        ArrayAdapterMainVw adapterMainVw = (ArrayAdapterMainVw) mListView.getAdapter();
        List<Item> arryEl = adapterMainVw.getArryElems();
        Item nameItem = arryEl.get(0);
        String name = nameItem.getName();
        long share_id = nameItem.getShare_id();
        Log.d(TAG, "ShareId of edited list="+ share_id);
        int i=0;
        DBOperations.getInstance().deleteDb(nameItem, viewType);
        for (Item itm : arryEl)
        {
            if (i == 0) {
                ++i;
                continue;
            }
            if (itm.getItem() == null || itm.getItem().length() <= 0)
            {
                ++i;
                continue;
            }
            itm.setName(name);
            itm.setShare_id(share_id);

            DBOperations.getInstance().insertDb(itm, viewType);

            ++i;
        }
        if (viewType != EASYGROC_EDIT_ITEM)
            startEasyGrocTemplDisplayActivity(nameItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Log.d(TAG, "Menu item selected");

        if (item.getItemId() == R.id.add_item_done) {
            adapter.getKeyVals(itm);


            switch (viewType) {
                case AUTOSPREE_ADD_ITEM:
                case OPENHOUSES_ADD_ITEM: {
                   ASpreeOHAddItemDone();
                }
                break;

                case AUTOSPREE_EDIT_ITEM:
                case OPENHOUSES_EDIT_ITEM: {
                    ASpreeOHEditItemDone();
                }
                break;

                case EASYGROC_ADD_ITEM:
                {
                   return EasyGrocAddItemDone();
                }

                case EASYGROC_TEMPL_ADD_ITEM:
                    return easyGrocTemplAddItemDone();

                case EASYGROC_EDIT_ITEM:
                case EASYGROC_TEMPL_EDIT_ITEM:
                  easyGrocEditItemDone();
                break;

                default:
                    break;
            }
            finish();

        }
        else if (item.getItemId() == R.id.delete_templ_item)
        {
            startEasyGrocTemplDeleteActivity();
        }
        else if (item.getItemId() == R.id.delete_templ_item_confirm)
        {
            easyGrocTemplDeleteItem();
        }
        else if (item.getItemId() == R.id.edit_item) {
            Log.d(getClass().getName(), "Selected edit Item menu");
            switch (viewType) {
                case AUTOSPREE_DISPLAY_ITEM:
                    startAutoSpreeEditActivity();
                    break;

                case OPENHOUSES_DISPLAY_ITEM:
                    startOpenHousesEditActivity();

                    break;
                case EASYGROC_DISPLAY_ITEM:
                    startEasyGrocEditActivity();

                    break;

                case EASYGROC_TEMPL_DISPLAY_ITEM:
                    startEasyGrocTemplEditActivity();
                    break;

                default:

                    break;
            }

            finish();
        }
        else if ( item.getItemId() == R.id.add_new_item)
        {
            switch (viewType)
            {
                case EASYGROC_TEMPL_LISTS:
                {
                    startEasyGrocTemplListAddActivity();
                    return true;
                }
                default:
                    break;
            }
            finish();
        }
        else if (item.getItemId() == R.id.delete_item)
        {
            switch (viewType)
            {
                case EASYGROC_TEMPL_DISPLAY_ITEM:
                case EASYGROC_DISPLAY_ITEM:
                {
                  easyGrocDeleteItem();
                }
                break;

                case OPENHOUSES_DISPLAY_ITEM:
                {
                    openHousesDeleteItem();
                }
                break;

                case AUTOSPREE_DISPLAY_ITEM:
                {
                    autoSpreeDeleteItem();
                }
                break;

                default:
                    break;
            }
        }
        else if (item.getItemId() == R.id.showall)
        {
            switch (viewType)
            {
                case EASYGROC_DISPLAY_ITEM:
                {
                    easyGrocShowAll();
                }
                break;

                default:
                    break;
            }
        }
        else if (item.getItemId() == R.id.undo)
        {
            switch (viewType)
            {
                case EASYGROC_DISPLAY_ITEM:
                {
                    easyGrocUndo();
                }
                break;

                default:
                    break;
            }
        }
        else if (item.getItemId() == R.id.redo)
        {
            switch (viewType)
            {
                case EASYGROC_DISPLAY_ITEM:
                {
                    easyGrocRedo();
                }
                break;

                default:
                    break;
            }
        }
        else if (item.getItemId() == android.R.id.home)
        {
            Log.d(getClass().getName(), "Back button pressed");
            switch (viewType)
            {
                case CHECK_LIST_EDIT:
                case CHECK_LIST_ADD:
                {
                   sendCheckListDisplayResult();
                }
                break;

                case EASYGROC_ADD_ITEM:
                {
                    Intent intent = new Intent();
                    intent.putExtra("refresh", "NotNeeded");
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                break;


                default:
                    break;
            }
            finish();
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void startEasyGrocTemplShareActivity()
    {
        Log.d (TAG, "Starting share activity");
        String app_name = DBOperations.getInstance().getApp_name();
        Intent intent = new Intent(this, ShareActivity.class);
        intent.putExtra("app_name", app_name);
        intent.putExtra("ViewType", SHARE_TEMPL_MAINVW);
        startActivity(intent);

    }

    private void sendCheckListDisplayResult()
    {
        Intent intent = new Intent();
        ArrayList<Item> chckLst = (ArrayList<Item>)adapter.getArryElems();

        if (chckLst.size() > 0) {
            intent.putParcelableArrayListExtra("check_list", chckLst);
            setResult(RESULT_OK, intent);
        }
        else
        {
            setResult(RESULT_CANCELED, intent);
        }
        setResult(RESULT_OK, intent);
    }

    private void easyGrocShowAll()
    {
        ArrayAdapterMainVw adapterMainVw = (ArrayAdapterMainVw) mListView.getAdapter();
        List<Item> arryEl = adapterMainVw.getArryElems();
        for (Item showItm : arryEl)
        {
            showItm.setHidden(false);
            adapterMainVw.notifyDataSetChanged();
        }
        return;
    }

    private void easyGrocUndo()
    {
        ArrayAdapterMainVw adapterMainVw = (ArrayAdapterMainVw) mListView.getAdapter();
        int indx = adapterMainVw.undoPop();

        List<Item> arryEl = adapterMainVw.getArryElems();
        if (indx == -1 || indx >= arryEl.size())
        {
            return;
        }
        Item itm = arryEl.get(indx);
        itm.setHidden(!itm.isHidden());
        adapterMainVw.redoPush(indx);
        adapterMainVw.notifyDataSetChanged();
        return;
    }

    private void easyGrocRedo()
    {
        ArrayAdapterMainVw adapterMainVw = (ArrayAdapterMainVw) mListView.getAdapter();
        int indx = adapterMainVw.redoPop();
        List<Item> arryEl = adapterMainVw.getArryElems();
        if (indx == -1 || indx >= arryEl.size())
        {
            return;
        }
        Item itm = arryEl.get(indx);
        itm.setHidden(!itm.isHidden());
        adapterMainVw.notifyDataSetChanged();
        return;
    }

    private void openHousesDeleteItem()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(SingleItemActivity.this).create();
        alertDialog.setTitle("Delete");
        String delMsg = "Delete "  + itm.getName();
        alertDialog.setMessage(delMsg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (itm.getAlbum_name() != null && itm.getAlbum_name().length() > 0)
                        {
                            File dir = getFilesDir();
                            File albumDir = new File(dir, itm.getAlbum_name());
                            deleteRecursive(albumDir);
                        }
                        DBOperations.getInstance().deleteDb(itm, viewType);
                        DBOperations.getInstance().deleteDb(itm, EASYGROC_EDIT_ITEM);
                        Intent intent = new Intent();
                        intent.putExtra("refresh", "Needed");
                        setResult(RESULT_OK, intent);
                        Log.d(TAG, "Setting activity result of refresh Needed in  item delete");
                        finish();
                        return;
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        return;
                    }
                });
        alertDialog.show();
        return;
    }

    private void  deleteRecursive (File albumDir)
    {
        if (albumDir.isDirectory())
        {
            for (File child : albumDir.listFiles())
            {
                deleteRecursive(child);
            }
        }
        albumDir.delete();
    }

    private void autoSpreeDeleteItem()
    {
        openHousesDeleteItem();
        return;
    }

    private void easyGrocTemplDeleteItem()
    {
        ArrayAdapterMainVw  adapter = (ArrayAdapterMainVw)mListView.getAdapter();
        Item selectedItem = adapter.getSelectedItem();
        if (selectedItem == null)
        {
            Log.i(TAG, "No item selected for deleting");
            return;
        }
        DBOperations.getInstance().deleteDb(selectedItem, viewType);
        String name = selectedItem.getName();
        selectedItem.setName(name + ":INV");
        DBOperations.getInstance().deleteDb(selectedItem, viewType);
        selectedItem.setName(name + ":SCRTCH");
        DBOperations.getInstance().deleteDb(selectedItem, viewType);
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void easyGrocDeleteItem()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(SingleItemActivity.this).create();
        alertDialog.setTitle("Delete");

        String delMsg = "Delete "  + itm.getName();
        alertDialog.setMessage(delMsg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        String app_name = DBOperations.getInstance().getApp_name();
                        DBOperations.getInstance().deleteDb(itm, viewType);
                    if (app_name.equals(EASYGROC)) {
                        Intent intent = new Intent();
                        intent.putExtra("refresh", "Needed");
                        setResult(RESULT_OK, intent);
                        Log.d(TAG, "Setting activity result of refresh Needed in EASYGROC item delete");
                        finish();
                    }
                    else
                    {
                        if (viewType == EASYGROC_TEMPL_DISPLAY_ITEM)
                        {
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else
                        {
                            finish();
                        }
                    }


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

    private void startDisplayActivity() {

        Intent intent = new Intent(this, SingleItemActivity.class);
        intent.putExtra("item", itm);
        intent.putParcelableArrayListExtra("check_list", checkLst);
        switch (viewType)
        {
            case OPENHOUSES_ADD_ITEM:
            case OPENHOUSES_EDIT_ITEM:
                intent.putExtra("ViewType", OPENHOUSES_DISPLAY_ITEM);
                startActivity(intent);
                break;
            case AUTOSPREE_ADD_ITEM:
            case AUTOSPREE_EDIT_ITEM:
                intent.putExtra("ViewType", AUTOSPREE_DISPLAY_ITEM);
                startActivityForResult(intent, AUTOSPREE_DISPLAY_ITEM_REQUEST);
                break;
            default:
                break;
        }
        return;
    }

    private void startAutoSpreeEditActivity() {

        // DBOperations.getInstance().updateDb(editItem);
        Intent intent = new Intent(this, SingleItemActivity.class);
        intent.putExtra("ViewType", AUTOSPREE_EDIT_ITEM);
        intent.putExtra("item", itm);
        intent.putParcelableArrayListExtra("check_list", checkLst);
        startActivity(intent);
        return;
    }

    private void startEasyGrocTemplListAddActivity()
    {
        String app_name = DBOperations.getInstance().getApp_name();

        switch (app_name) {

            case EASYGROC: {
                Log.d(TAG, "Adding template list item");
                AlertDialog alertDialog = new AlertDialog.Builder(SingleItemActivity.this).create();
                alertDialog.setTitle("Template List Create");

                String delMsg = "Enter the name of the new Template List ";
                alertDialog.setMessage(delMsg);
                final EditText input = new EditText(this);
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
                                    AlertDialog alertDialog = new AlertDialog.Builder(SingleItemActivity.this).create();
                                    alertDialog.setTitle("Error");
                                    String err = "Template List " + nameItem.getName() + " exists. Choose different name";
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

                                m_TemplateName = input.getText().toString();
                                dialog.cancel();
                                Item templNameItm = new Item();
                                templNameItm.setName(m_TemplateName);
                                DBOperations.getInstance().insertDb(templNameItm, EASYGROC_TEMPL_NAME_ADD_ITEM);
                                List<Item>  mainLst = DBOperations.getInstance().getTemplNameLst();
                                templNameAdapter.setGroupItem(mainLst);
                                templNameAdapter.notifyDataSetChanged();

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

            case OPENHOUSES:
            case AUTOSPREE: {


                Intent intent = new Intent(this, SingleItemActivity.class);
                 intent.putExtra("ViewType", EASYGROC_TEMPL_ADD_ITEM);
                 Item easyItem = new Item();

                List<Item>  mainLst = DBOperations.getInstance().getTemplNameLst();
                String name = "Check List ";
                name += Integer.toString(mainLst.size()+1);
                easyItem.setName(name);
                intent.putExtra("item", easyItem);
                Activity itemAct = (Activity) this;
                itemAct.startActivityForResult(intent, ADD_TEMPL_CHECKLIST_ACTIVITY_REQUEST);

            }
            break;

            default:
                    Log.d(TAG, "Invalid app_name " + app_name);
                break;

        }
        return;
    }
    private void startOpenHousesEditActivity() {
        Intent intent = new Intent(this, SingleItemActivity.class);
        intent.putExtra("ViewType", OPENHOUSES_EDIT_ITEM);
        intent.putExtra("item", itm);
        intent.putParcelableArrayListExtra("check_list", checkLst);
        startActivity(intent);
        return;
    }

    private void startEasyGrocTemplDisplayActivity(Item itm)
    {

        Log.d(getClass().getName(), "Displaying item " + itm.getName());
        Intent intent = new Intent(this, SingleItemActivity.class);
        intent.putExtra("ViewType", EASYGROC_TEMPL_DISPLAY_ITEM);
        intent.putExtra("item", itm);
        startActivity(intent);
        return;
    }

    private void startEasyGrocDisplayActivity(Item itm)
    {

        Log.d(getClass().getName(), "Displaying item " + itm.getName());
        Intent intent = new Intent(this, SingleItemActivity.class);
        intent.putExtra("ViewType", EASYGROC_DISPLAY_ITEM);
        intent.putExtra("item", itm);
        startActivity(intent);
        return;
    }

    private void startEasyGrocEditActivity() {
        Intent intent = new Intent(this, SingleItemActivity.class);
        intent.putExtra("ViewType", EASYGROC_EDIT_ITEM);
        intent.putExtra("item", itm);
        startActivity(intent);
        return;
    }

    private void startEasyGrocTemplEditActivity() {
        Intent intent = new Intent(this, SingleItemActivity.class);
        intent.putExtra("ViewType", EASYGROC_TEMPL_EDIT_ITEM);
        intent.putExtra("item", itm);
        //edit activity
        startActivity(intent);
        return;
    }

    private void startEasyGrocTemplDeleteActivity() {
        Intent intent = new Intent(this, SingleItemActivity.class);
        intent.putExtra("ViewType", EASYGROC_TEMPL_DELETE_ITEM);
        intent.putExtra("item", itm);

        Activity itemAct = (Activity) this;
        itemAct.startActivityForResult(intent, DELETE_TEMPL_ACTIVITY_REQUEST);
        return;
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            //  mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            // displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
                Address address = resultData.getParcelable(Constants.RESULT_DATA_KEY);
                Log.i(TAG, "Received address=" + address.toString());




                String street = address.getFeatureName();
                street += " ";
                street += address.getThoroughfare();

                itm.setStreet(street);
                itm.setCity(address.getLocality());
                itm.setState(address.getAdminArea());
                itm.setZip(address.getPostalCode());
                itm.setLatitude(address.getLatitude());
                itm.setLongitude(address.getLongitude());
                Log.d(TAG, "Setting address " + street + " " +address.getLocality() + " "
                        + address.getAdminArea() + " " + address.getPostalCode());
                 adapter.notifyDataSetChanged();
                // adapter.setNotifyOnChange(false);
            }


        }
    }

}

