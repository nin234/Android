package com.rekhaninan.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.rekhaninan.common.Constants.ADD_CONTACT_ITEM_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.CONTACTITEMSEPARATOR;
import static com.rekhaninan.common.Constants.CONTACTS_ITEM_ADD;
import static com.rekhaninan.common.Constants.CONTACTS_VW;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.GET_CONTACTS_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.ITEMSEPARATOR;
import static com.rekhaninan.common.Constants.KEYVALSEPARATOR;
import static com.rekhaninan.common.Constants.MAINVW;
import static com.rekhaninan.common.Constants.OPENHOUSES;
import static com.rekhaninan.common.Constants.RESULT_NO_CONTACT_SELECTED;
import static com.rekhaninan.common.Constants.SHARE_MAINVW;
import static com.rekhaninan.common.Constants.SHARE_PICTURE_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.SHARE_PICTURE_VW;

public class ShareVwTabbed extends Fragment {

    public static final String ARG_OBJECT = "ShareVwTabbed";
    public String app_name;
    private ListView mListView;
    private final String TAG = "ShareVwTabbed";
    private Item selectedItem;
    private  ArrayAdapterMainVw adapter;
    private ArrayList<String> selectedImages;
    private boolean dontRefresh;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dontRefresh = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main_vw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(SHARE_MAINVW);
        if (mainLst == null) {
            Log.d(TAG, "NULL main list");
            return;
        }


        mListView = (ListView) view.findViewById(R.id.recipe_list_view);
        adapter = new ArrayAdapterMainVw(getContext(), R.layout.simple_list_1, mainLst);
        adapter.setParams(app_name, SHARE_MAINVW);
        mListView.setAdapter(adapter);
        adapter.setFragment(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        Log.d(TAG, "Inflating menu add_single_item in ContactsVwTabbed");
        inflater.inflate(R.menu.share_item, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.select_items) {
            Log.d(TAG, "Showing select contacts screen");
            ArrayAdapterMainVw  adapter = (ArrayAdapterMainVw)mListView.getAdapter();
            selectedItem = adapter.getSelectedItem();
            if (selectedItem == null)
            {
                return true;
            }

            Intent intent = new Intent(getActivity(), ShareActivity.class);
            intent.putExtra("app_name", app_name);
            intent.putExtra("ViewType", CONTACTS_VW);
            startActivityForResult(intent, GET_CONTACTS_ACTIVITY_REQUEST);
            return true;
        }
        return true;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Processing Activity result for request Code=" + requestCode);
        switch (requestCode) {
           case GET_CONTACTS_ACTIVITY_REQUEST: {
               Log.d(TAG, "GET_CONTACTS_ACTIVITY_REQUEST result code=" + resultCode);
               String msg;
               boolean bShowAlert = true;
               if (resultCode == RESULT_OK) {
                   java.util.ArrayList<Item> contactsLst = data.getParcelableArrayListExtra("contactslist");
                   if (app_name.equals(EASYGROC))
                   {
                       shareEasyGrocItem(contactsLst);
                   }
                   else if (app_name.equals(AUTOSPREE))
                   {
                       shareAutoSpreeItem(contactsLst);
                   }
                   else if (app_name.equals(OPENHOUSES))
                   {
                       shareOpenHousesItem(contactsLst);
                   }
                   msg = "Send item="+ selectedItem.getName() + " to=";
                   for (Item contact : contactsLst)
                   {
                       msg += contact.getName() + ", ";
                   }

               } else if (resultCode == RESULT_NO_CONTACT_SELECTED){
                    msg = "Failed to share item, no contacts selected";

               }
               else
               {
                   msg="cxled";
                   bShowAlert = false;
               }
               adapter.notifyDataSetChanged();
               if (bShowAlert) {
                   AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                   alertDialog.setTitle("Share Items");
                   alertDialog.setMessage(msg);
                   alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                           new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                                   return;
                               }
                           });
                   alertDialog.show();
               }
           }
            case SHARE_PICTURE_ACTIVITY_REQUEST:
           {
               dontRefresh = true;
               if(resultCode == RESULT_OK)
               {
                   selectedImages = data.getStringArrayListExtra("image_items");
               }
           }
               break;

        }
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
            Log.d(TAG, "No contact to share to");
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
                Log.d(TAG, "No elements in the sharing list");
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
            Log.d(TAG, "No contact to share to");
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


    private void startPictureSharePictureActivity()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Attach Pictures?");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getActivity(), PhotoRoll.class);
                        intent.putExtra("app_name", app_name);
                        intent.putExtra("ViewType", SHARE_PICTURE_VW);
                        intent.putExtra("album_name", selectedItem.getAlbum_name());
                        startActivityForResult(intent, SHARE_PICTURE_ACTIVITY_REQUEST);
                        dialog.cancel();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        alertDialog.show();
    }

    public void attachPictures()
    {
        Log.d(TAG, "Showing select contacts screen");
        ArrayAdapterMainVw  adapter = (ArrayAdapterMainVw)mListView.getAdapter();
        selectedItem = adapter.getSelectedItem();
        if (selectedItem == null)
        {
            return;
        }



        File dir = getContext().getFilesDir();
        String thumbDir = selectedItem.getAlbum_name() + File.separator + "thumbnails";
        File thumbNailsDir = new File(dir, thumbDir);
        boolean startSharePictures = true;
        if (!thumbNailsDir.exists())
        {
            Log.i(TAG, "Starting contacts activity in sharing as no thumbnail directory found");
            startSharePictures = false;
        }

        if (startSharePictures) {
            File[] files = thumbNailsDir.listFiles();
            if (files.length == 0) {
                Log.i(TAG, "Starting contacts activity in sharing as no thumbnail images found");
                startSharePictures = false;
            }
        }
        if (startSharePictures)
        {
            startPictureSharePictureActivity();
        }


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
            Log.d(TAG, "No contact to share to");
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

    public void refresh()
    {
        Log.d(TAG, "Refreshing Share View");
        if (dontRefresh)
        {
            Log.d(TAG, "Not refreshing Share View now");
            dontRefresh = false;
            return;
        }
        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(SHARE_MAINVW);
        adapter.setArryElems(mainLst);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "In onResume of ShareVwTabbed");
        refresh();
    }
}
