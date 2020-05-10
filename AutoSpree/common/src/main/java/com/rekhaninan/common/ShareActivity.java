package com.rekhaninan.common;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import static com.rekhaninan.common.Constants.ADD_CONTACT_ITEM_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.CONTACTITEMSEPARATOR;
import static com.rekhaninan.common.Constants.CONTACTS_ITEM_ADD;
import static com.rekhaninan.common.Constants.CONTACTS_ITEM_DISPLAY;
import static com.rekhaninan.common.Constants.CONTACTS_MAINVW;
import static com.rekhaninan.common.Constants.CONTACTS_VW;
import static com.rekhaninan.common.Constants.DELETE_CONTACT_ITEM_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.GET_CONTACTS_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.ITEMSEPARATOR;
import static com.rekhaninan.common.Constants.KEYVALSEPARATOR;
import static com.rekhaninan.common.Constants.OPENHOUSES;
import static com.rekhaninan.common.Constants.SHARE_MAINVW;
import static com.rekhaninan.common.Constants.SHARE_PICTURE_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.SHARE_PICTURE_VW;
import static com.rekhaninan.common.Constants.SHARE_TEMPL_MAINVW;
import static com.rekhaninan.common.Constants.TEMPLLISTSEPERATOR;

public class ShareActivity extends AppCompatActivity {

    private ListView mListView;
    private int no_items;
    private String app_name;
    private final String TAG = "ShareActivity";
    private int viewType;
    private Item selectedItem;
    ArrayList<String> selectedImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Intent intent = getIntent();
        app_name = intent.getStringExtra("app_name");
        viewType = intent.getIntExtra("ViewType", 0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        switch (viewType) {

            case SHARE_MAINVW:
            {

                setContentView(R.layout.activity_main_vw);
                java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(SHARE_MAINVW);
                if (mainLst == null) {
                    Log.d(TAG, "NULL main list");
                    return;
                }

                no_items = mainLst.size();

                mListView = (ListView) findViewById(R.id.recipe_list_view);
                ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(app_name, SHARE_MAINVW);
                mListView.setAdapter(adapter);
            }
            break;

            case SHARE_TEMPL_MAINVW:
            {
                setContentView(R.layout.activity_main_vw);
                java.util.List<Item> mainLst = DBOperations.getInstance().getTemplNameLst();
                if (mainLst == null) {
                    Log.d(TAG, "NULL main list");
                    return;
                }
                no_items = mainLst.size();
                mListView = (ListView) findViewById(R.id.recipe_list_view);
                ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(app_name, SHARE_TEMPL_MAINVW);
                mListView.setAdapter(adapter);

            }
            break;

            case CONTACTS_VW:
            {

                setContentView(R.layout.activity_main_vw);
                java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(CONTACTS_VW);
                if (mainLst == null)
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(ShareActivity.this).create();
                    alertDialog.setTitle("Error");
                    String err = "Add Contacts to start sharing";
                    alertDialog.setMessage(err);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                    return;
                                }
                            });
                    alertDialog.show();
                    return;
                }
                mListView = (ListView) findViewById(R.id.recipe_list_view);
                ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(app_name, CONTACTS_VW);
                mListView.setAdapter(adapter);
            }
            break;

            case CONTACTS_MAINVW:
            {

                setContentView(R.layout.activity_main_vw);
                java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(CONTACTS_MAINVW);
                mListView = (ListView) findViewById(R.id.recipe_list_view);
                ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(app_name, CONTACTS_MAINVW);
                mListView.setAdapter(adapter);
            }
            break;

            case CONTACTS_ITEM_ADD:
            {
                setContentView(R.layout.activity_main_vw);
                java.util.List<Item> mainLst = new ArrayList<Item>();
                Item itm = new Item();
                itm.setShare_id(0);
                for (int i=0; i < 5; ++i)
                    mainLst.add(itm);
                mListView = (ListView) findViewById(R.id.recipe_list_view);
                ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(app_name, CONTACTS_ITEM_ADD);
                mListView.setAdapter(adapter);
            }
            break;

            case CONTACTS_ITEM_DISPLAY:
            {
                setContentView(R.layout.activity_main_vw);
                java.util.List<Item> mainLst = new ArrayList<Item>();
                Item itm = intent.getParcelableExtra("item");
                Log.i(TAG, "Displaying contact item name=" + itm.getName() + " share_id=" + itm.getShare_id());
                for (int i=0; i < 5; ++i)
                    mainLst.add(itm);
                mListView = (ListView) findViewById(R.id.recipe_list_view);
                ArrayAdapterMainVw adapter = new ArrayAdapterMainVw(this, R.layout.simple_list_1, mainLst);
                adapter.setParams(app_name, CONTACTS_ITEM_DISPLAY);
                mListView.setAdapter(adapter);
            }
            break;

            default:
                break;
        }

    }





    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        switch (viewType) {
            case SHARE_MAINVW:
            case SHARE_TEMPL_MAINVW:
                inflater.inflate(R.menu.share_item, menu);
                break;

            case CONTACTS_VW:
                inflater.inflate(R.menu.add_item, menu);
                break;

            case CONTACTS_MAINVW:
                inflater.inflate(R.menu.add_single_item, menu);
                break;

            case CONTACTS_ITEM_ADD:
                inflater.inflate(R.menu.add_item, menu);
                break;

            case CONTACTS_ITEM_DISPLAY:
                inflater.inflate(R.menu.delete_item, menu);
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_CONTACTS_ACTIVITY_REQUEST)
        {
            if(resultCode == RESULT_OK)
            {
                java.util.ArrayList<Item> contactsLst = data.getParcelableArrayListExtra("contactslist");
                switch (app_name)
                {
                    case EASYGROC: {
                        switch (viewType ) {

                            case SHARE_MAINVW: {
                                shareEasyGrocItem(contactsLst);
                            }
                            break;

                            case SHARE_TEMPL_MAINVW:
                            {
                                shareEasyGrocTemplItem(contactsLst);
                            }
                            break;
                        }
                    }
                    break;

                    case OPENHOUSES:
                            shareOpenHousesItem(contactsLst);
                        break;

                    case AUTOSPREE:
                            shareAutoSpreeItem(contactsLst);
                        break;

                    default:
                        break;
                }

            }

            finish();
        }
        else if (requestCode == SHARE_PICTURE_ACTIVITY_REQUEST)
        {
            if(resultCode == RESULT_OK)
            {
                selectedImages = data.getStringArrayListExtra("image_items");
                Intent intent = new Intent(this, ShareActivity.class);
                intent.putExtra("app_name", app_name);
                intent.putExtra("ViewType", CONTACTS_VW);
                startActivityForResult(intent, GET_CONTACTS_ACTIVITY_REQUEST);
            }
        }
        else if (requestCode == ADD_CONTACT_ITEM_ACTIVITY_REQUEST)
        {
            finish();
        }
        else if (requestCode == DELETE_CONTACT_ITEM_ACTIVITY_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                finish();
            }
        }

    }

    private void shareOpenHousesItem(java.util.ArrayList<Item> contactsLst)
    {
        String shrMsg = new String();
        for (Item contact : contactsLst)
        {
            if (contact.getName().equals("ME"))
                continue;
            shrMsg += Long.toString(contact.getShare_id());
            shrMsg += ";";
        }

        if (shrMsg == null || shrMsg.length() <=0)
        {
            Log.e(TAG, "No contact to share to");
            return;
        }
        String picMetaCommon = shrMsg;
        shrMsg += ":::";
        shrMsg += getCommonStr();
        shrMsg += "Area:|:";
        shrMsg += Double.toString(selectedItem.getArea());
        shrMsg += "]:;";
        shrMsg += "Beds:|:";
        shrMsg += Double.toString(selectedItem.getBeds());
        shrMsg += "]:;";
        shrMsg += "Baths:|:";
        shrMsg += Double.toString(selectedItem.getBaths());
        shrMsg += "]:;";
        shrMsg += getCheckList(selectedItem);
        ShareMgr.getInstance().shareItem(shrMsg, selectedItem.getName());
        if (selectedImages != null) {
            for (String selectedImage : selectedImages) {
                String picMetaStr = picMetaCommon + selectedItem.getName();
                ShareMgr.getInstance().sharePicture(selectedImage, picMetaStr, selectedItem.getShare_id());

            }
        }
        return;
    }

    private void shareAutoSpreeItem(java.util.ArrayList<Item> contactsLst)
    {
        String shrMsg = new String();
        for (Item contact : contactsLst)
        {
            if (contact.getName().equals("ME"))
                continue;
            shrMsg += Long.toString(contact.getShare_id());
            shrMsg += ";";
        }

        if (shrMsg == null || shrMsg.length() <=0)
        {
            Log.e(TAG, "No contact to share to");
            return;
        }

        String picMetaCommon = shrMsg;
        shrMsg += ":::";
        shrMsg += getCommonStr();
        shrMsg += "Model:|:";
        shrMsg += selectedItem.getModel();
        shrMsg += "]:;";
        shrMsg += "Make:|:";
        shrMsg += selectedItem.getMake();
        shrMsg += "]:;";
        shrMsg += "Color:|:";
        shrMsg += selectedItem.getColor();
        shrMsg += "]:;";
        shrMsg += "Miles:|:";
        shrMsg += Integer.toString(selectedItem.getMiles());
        shrMsg += "]:;";
        shrMsg += getCheckList(selectedItem);

        ShareMgr.getInstance().shareItem(shrMsg, selectedItem.getName());
        if (selectedImages != null) {
            for (String selectedImage : selectedImages) {
                String picMetaStr = picMetaCommon + selectedItem.getName();
                ShareMgr.getInstance().sharePicture(selectedImage, picMetaStr, selectedItem.getShare_id());

            }
        }
        return;
    }

    private String getCommonStr()
    {
        String shrMsg = new String();
        shrMsg += "Name:|:";
        shrMsg += selectedItem.getName();
        shrMsg += "]:;";
        shrMsg += "Price:|:";
        shrMsg += Double.toString(selectedItem.getPrice());
        shrMsg += "]:;";
        shrMsg += "Year:|:";
        shrMsg += Integer.toString(selectedItem.getYear());
        shrMsg += "]:;";
        shrMsg += "Notes:|:";
        shrMsg += selectedItem.getNotes();
        shrMsg += "]:;";
        shrMsg += "Ratings:|:";
        shrMsg += Integer.toString(selectedItem.getRating());
        shrMsg += "]:;";
        shrMsg += "Street:|:";
        shrMsg += selectedItem.getStreet();
        shrMsg += "]:;";
        shrMsg += "City:|:";
        shrMsg += selectedItem.getCity();
        shrMsg += "]:;";
        shrMsg += "State:|:";
        shrMsg += selectedItem.getState();
        shrMsg += "]:;";
        shrMsg += "PostalCode:|:";
        shrMsg += selectedItem.getZip();
        shrMsg += "]:;";
        shrMsg += "latitude:|:";
        shrMsg += Double.toString(selectedItem.getLatitude());
        shrMsg += "]:;";
        shrMsg += "longitude:|:";
        shrMsg += Double.toString(selectedItem.getLongitude());
        shrMsg += "]:;";
        shrMsg += "shareId:|:";
        shrMsg += Long.toString(ShareMgr.getInstance().getShare_id());
        shrMsg += "]:;";

        return shrMsg;
    }

    private String getCheckList(Item itm)
    {
        String chkLstMsg = "::]}]::"; //separator
        java.util.List<Item> list = DBOperations.getInstance().getList(itm.getName(), itm.getShare_id());
        if (list == null || list.size() == 0)
            return chkLstMsg;
        for (Item selItem : list)
        {
            chkLstMsg += Integer.toString(selItem.getRowno());
            chkLstMsg += ":|:";
            if (selItem.isSelected())
            {
                chkLstMsg += Integer.toString(1);
            }
            else
            {
                chkLstMsg += Integer.toString(0);
            }
            chkLstMsg += ":|:";
            chkLstMsg += selItem.getItem();
            chkLstMsg += "]:;";
        }
        return chkLstMsg;
    }

    private void shareEasyGrocTemplItem(java.util.ArrayList<Item> contactsLst)
    {
        String shrMsg = new String();
        for (Item contact : contactsLst)
        {
            if (contact.getName().equals("ME"))
                continue;
            shrMsg += Long.toString(contact.getShare_id());
            shrMsg += ";";
        }

        if (shrMsg == null || shrMsg.length() <=0)
        {
            Log.e(TAG, "No contact to share to");
            return;
        }
        shrMsg += CONTACTITEMSEPARATOR;

        shrMsg += Long.toString(selectedItem.getShare_id());
        shrMsg += KEYVALSEPARATOR;
        shrMsg += Long.toString(selectedItem.getShare_id());

        shrMsg += TEMPLLISTSEPERATOR;
        for (int i=0; i < 3; ++i) {
            String name = selectedItem.getName();
            if (i==1) {
                name += ":INV";
            }
            else if (i==2) {
                name += ":SCRTCH";
            }

            java.util.List<Item> list = DBOperations.getInstance().getTemplList(name, selectedItem.getShare_id());
            for (Item selItem : list) {
                shrMsg += Integer.toString(selItem.getRowno());
                shrMsg += KEYVALSEPARATOR;
                shrMsg += Integer.toString(selItem.getStart_month());
                shrMsg += KEYVALSEPARATOR;
                shrMsg += Integer.toString(selItem.getEnd_month());
                shrMsg += KEYVALSEPARATOR;
                shrMsg += Integer.toString(selItem.getInventory());
                shrMsg += KEYVALSEPARATOR;
                shrMsg += selItem.getItem();
                shrMsg += ITEMSEPARATOR;
            }
            shrMsg += TEMPLLISTSEPERATOR;
        }
        ShareMgr.getInstance().shareTemplItem(shrMsg, selectedItem.getName());

    }

    private void shareEasyGrocItem(java.util.ArrayList<Item> contactsLst)
    {
        String shrMsg = new String();
        for (Item contact : contactsLst)
        {
            if (contact.getName().equals("ME"))
                continue;
            shrMsg += Long.toString(contact.getShare_id());
            shrMsg += ";";
        }

        if (shrMsg == null || shrMsg.length() <=0)
        {
          Log.e(TAG, "No contact to share to");
            return;
        }

        if (selectedItem.getPicurl() != null && selectedItem.getPicurl().length() > 0)
        {

            String picMetaStr = shrMsg + selectedItem.getName();
            ShareMgr.getInstance().sharePicture(selectedItem.getPicurl(), picMetaStr, selectedItem.getShare_id());
        }
        else {
            shrMsg += CONTACTITEMSEPARATOR;
            java.util.List<Item> list = DBOperations.getInstance().getList(selectedItem.getName(), selectedItem.getShare_id());
            if (list.size() == 0)
            {
                Log.e(TAG, "No elements in the sharing list");
            }
            Item firstEl = list.get(0);
            shrMsg += Long.toString(firstEl.getShare_id());
            shrMsg += KEYVALSEPARATOR;
            shrMsg += Long.toString(firstEl.getShare_id());
            shrMsg += ITEMSEPARATOR;

            for (Item selItem : list)
            {

                shrMsg += Integer.toString(selItem.getRowno());
                shrMsg += KEYVALSEPARATOR;
                shrMsg += selItem.getItem();
                shrMsg += ITEMSEPARATOR;
            }
            ShareMgr.getInstance().shareItem(shrMsg, selectedItem.getName());
        }

        return;
    }

    private void OnOHASpreeItemSelected()
    {
        ArrayAdapterMainVw  adapter = (ArrayAdapterMainVw)mListView.getAdapter();
        selectedItem = adapter.getSelectedItem();
        if (selectedItem == null)
        {
            Log.i(TAG, "No item selected for sharing");
            return;
        }
        File dir = getFilesDir();
        String thumbDir = selectedItem.getAlbum_name() + File.separator + "thumbnails";
        File thumbNailsDir = new File(dir, thumbDir);
        boolean startContactActivity = false;
        if (!thumbNailsDir.exists())
        {
            Log.i(TAG, "Starting contacts activity in sharing as no thumbnail directory found");
            startContactActivity = true;
        }

        if (!startContactActivity) {
            File[] files = thumbNailsDir.listFiles();
            if (files.length == 0) {
                Log.i(TAG, "Starting contacts activity in sharing as no thumbnail images found");
                startContactActivity = true;
            }
        }

        if (startContactActivity)
        {
            Intent intent = new Intent(this, ShareActivity.class);
            intent.putExtra("app_name", app_name);
            intent.putExtra("ViewType", CONTACTS_VW);
            startActivityForResult(intent, GET_CONTACTS_ACTIVITY_REQUEST);
            return;
        }

        Intent intent = new Intent(this, PhotoRoll.class);
        intent.putExtra("app_name", app_name);
        intent.putExtra("ViewType", SHARE_PICTURE_VW);
        intent.putExtra("album_name", selectedItem.getAlbum_name());
        startActivityForResult(intent, SHARE_PICTURE_ACTIVITY_REQUEST);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Log.d(TAG, "Menu item selected");

        if (item.getItemId() == R.id.select_items) {

            switch (app_name)
            {
                case EASYGROC: {
                    ArrayAdapterMainVw  adapter = (ArrayAdapterMainVw)mListView.getAdapter();
                    selectedItem = adapter.getSelectedItem();
                    if (selectedItem == null)
                    {
                        return true;
                    }
                    Intent intent = new Intent(this, ShareActivity.class);
                    intent.putExtra("app_name", app_name);
                    intent.putExtra("ViewType", CONTACTS_VW);
                    startActivityForResult(intent, GET_CONTACTS_ACTIVITY_REQUEST);
                }

                    break;

                case OPENHOUSES:
                case AUTOSPREE:
                {
                    OnOHASpreeItemSelected();
                }
                break;

            }

        }
        else if (item.getItemId() == R.id.add_item_done)
        {
            switch (viewType)
            {
                case CONTACTS_VW:
                {
                        ArrayAdapterMainVw  adapter = (ArrayAdapterMainVw)mListView.getAdapter();
                        java.util.ArrayList<Item> contactsLst = adapter.getSelectedList();
                        Intent intent = new Intent();
                        if (contactsLst.size() > 0) {
                            Log.i(TAG, "Setting contacts no=" + contactsLst.size());
                            intent.putParcelableArrayListExtra("contactslist", contactsLst);
                            setResult(RESULT_OK, intent);
                        }
                        else
                        {
                            setResult(RESULT_CANCELED, intent);
                        }

                    finish();
                }
                break;

                case CONTACTS_ITEM_ADD:
                {
                    ArrayAdapterMainVw  adapter = (ArrayAdapterMainVw)mListView.getAdapter();
                    Item newContact = adapter.getNewContact();
                    if (newContact.getShare_id() != 0)
                    {
                        if (newContact.getName() == null || newContact.getName().equals(""))
                        {
                            newContact.setName(Long.toString(newContact.getShare_id()));
                        }
                        DBOperations.getInstance().insertDb(newContact, CONTACTS_ITEM_ADD);
                        updateFriendList();
                    }

                    Intent intent = new Intent();
                    intent.putExtra("contact_name", newContact.getName());
                    setResult(RESULT_OK, intent);
                    finish();

                }
                break;

                default:
                    break;
            }
        }
        else if (item.getItemId() == R.id.manage_contacts)
        {
            Log.i(TAG, "Selected menu item manage contacts");
            Intent intent = new Intent(this, ShareActivity.class);
            intent.putExtra("app_name", app_name);
            intent.putExtra("ViewType", CONTACTS_MAINVW);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.add_new_item)
        {
            Log.i(TAG, "Add contact menu item selected");
            Intent intent = new Intent(this, ShareActivity.class);
            intent.putExtra("app_name", app_name);
            intent.putExtra("ViewType", CONTACTS_ITEM_ADD);
            startActivityForResult(intent, ADD_CONTACT_ITEM_ACTIVITY_REQUEST);
        }
        else if (item.getItemId() == R.id.delete_item_item)
        {
            ArrayAdapterMainVw  adapter = (ArrayAdapterMainVw)mListView.getAdapter();
            final Item newContact = adapter.getNewContact();
            if (newContact.getName().equals("ME"))
            {
                Log.i(TAG, "Cannot delete ME share Id");
                return true;
            }
            AlertDialog alertDialog = new AlertDialog.Builder(ShareActivity.this).create();
            alertDialog.setTitle("Delete");
            String delMsg = "Delete "  + newContact.getShare_id();
            alertDialog.setMessage(delMsg);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                            DBOperations.getInstance().deleteDb(newContact, viewType);
                            updateFriendList();
                            Intent intent = new Intent();
                            intent.putExtra("contact_name", newContact.getName());
                            setResult(RESULT_OK, intent);
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
        }
        else if (item.getItemId() == android.R.id.home)
        {
            Log.d(getClass().getName(), "Back button pressed");
            finish();
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onResume()
    {

        super.onResume();
    }
    void updateFriendList ()
    {
        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(viewType);

        String frndLst = new String();
        for (Item itm : mainLst)
        {
            frndLst += Long.toString(itm.getShare_id());

            if (itm.getName() != null && itm.getName().length() >0)
            {
                frndLst += ":";
                frndLst += itm.getName();
            }
            frndLst += ";";

        }

        ShareMgr.getInstance().updateFriendList(frndLst);
        return;
    }
}
