package com.rekhaninan.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.Fragment;

import static com.rekhaninan.common.Constants.*;


/**
 * Created by nin234 on 8/27/16.
 */
public abstract class RowView implements AdapterView.OnItemSelectedListener{

    protected int vwType;
    protected Context ctxt;
    protected View notes_row, pictures_row, check_row;
    protected String album_name;
    protected View camera_row, map_row;
    protected ArrayAdapterMainVw adapter;
    private String app_name;
    private int ratings;
    private Spinner ratingsSpnr;
    protected Fragment fragment;
    private InAppPurchase inApp;

    public void setFragment(Fragment frg) {fragment = frg;}

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }

    public ArrayAdapterMainVw getAdapter() {
        return adapter;
    }

    public void setAdapter(ArrayAdapterMainVw adapter) {
        this.adapter = adapter;
    }


    public abstract String getItemName(Item itm);

    private final String TAG = "RowView";


    public void  setVwType(int vtype)
    {
        vwType = vtype;
        return;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public int getVwType()
    {
        return vwType;
    }

    protected boolean checkEntitlement()
    {
        if (inApp == null) {
            inApp = new InAppPurchase(ctxt);
        }
        return inApp.canContinue();
    }

    public void setCtxt(Context ctxt1)
    {
        ctxt = ctxt1;
        return;
    }
       public  View getView(final Item itm, int position, ViewGroup parent)
       {

           WindowManager wm = (WindowManager) ctxt.getSystemService(Context.WINDOW_SERVICE);
           Display display = wm.getDefaultDisplay();
           Point size = new Point();
           display.getSize(size);
           int width = size.x;
           int height = size.y;
           int txtHeight = height / 12;
          // Log.d(getClass().getName(), "In Super getView position=" + position);
           switch (vwType) {

               case CONTACTS_ITEM_ADD:
               {
                   switch (position)
                   {
                       case CONTACTS_ROW_THREE:
                       case CONTACTS_ROW_FIVE:
                       case CONTACTS_ROW_ONE:
                           return getNoLabelView(parent, txtHeight, width, "  ");


                       case CONTACTS_NAME_ROW:
                          return getContactNameView(parent, txtHeight, width, itm, true);
                       case CONTACTS_SHARE_ID_ROW:
                           return getContactShareId(parent, txtHeight, width, itm, true);

                       default:
                           break;
                   }
               }
               break;

               case CONTACTS_ITEM_DISPLAY:
               {
                    switch (position)
                    {
                        case CONTACTS_ROW_THREE:
                        case CONTACTS_ROW_FIVE:
                        case CONTACTS_ROW_ONE:
                            return getNoLabelView(parent, txtHeight, width, "  ");
                        case CONTACTS_NAME_ROW:
                            return getContactNameView(parent, txtHeight, width, itm, false);
                        case CONTACTS_SHARE_ID_ROW:
                            return getContactShareId(parent, txtHeight, width, itm, false);

                        default:
                            break;

                    }

               }
               break;

               case CONTACTS_MAINVW:
               {
                    return getContactMainVwRow(parent, txtHeight, width, itm);
               }

               case CONTACTS_VW:
               {
                   LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                   View vw = inflater.inflate(R.layout.share_main, parent, false);
                   TextView tv = (TextView) vw.findViewById(R.id.share_main_item);


                   tv.setHeight(txtHeight);
                   String itemname = itm.getName();
                    if (itemname == null || itemname.length() <=0)
                    {
                        itemname = Long.toString(itm.getShare_id());
                    }
                   Log.d(TAG, "Setting item name=" + itemname);
                   tv.setText(itemname);
                   tv.setTag(itm);

                   //tv.setText(Item, TextView.BufferType.EDITABLE);
                   tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.35f );

                   tv.setOnClickListener(new View.OnClickListener() {

                       @Override
                       public void onClick(View view) {

                           TextView tv = (TextView) view;
                           Item itm = (Item) tv.getTag();
                           CheckBox cbxi = itm.getCbx();
                           cbxi.setChecked(!cbxi.isChecked());
                           Log.d(TAG, "Toggling checkbok " + itm.isSelected() + " cbx=" + cbxi.isChecked());
                           itm.setSelected(cbxi.isChecked());
                       }
                   }
                   );

                   final CheckBox cbx = (CheckBox) vw.findViewById(R.id.share_main_chkbox);
                   cbx.setTag(itm);
                   itm.setCbx(cbx);
                   itm.setRowno(position);

                   cbx.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {

                                                  CheckBox cbxi = (CheckBox) view;
                                                  Item itm = (Item) cbxi.getTag();
                                                  Log.d(TAG, "Toggling checkbok " + itm.isSelected());
                                                  itm.setSelected(cbxi.isChecked());
                                              }
                                          }

                   );

                   return vw;
               }

               case SHARE_TEMPL_MAINVW:
               case SHARE_MAINVW:
               {
                   return  getCheckBoxedMainLstVw(parent, txtHeight, itm, position);
               }
               case AUTOSPREE_ADD_ITEM:
               case AUTOSPREE_DISPLAY_ITEM:
               case AUTOSPREE_EDIT_ITEM:
               case OPENHOUSES_ADD_ITEM:
               case OPENHOUSES_DISPLAY_ITEM:
               case OPENHOUSES_EDIT_ITEM: {
                   Log.d(getClass().getName(), "In Super ADD/display/edit ITEM position=" + position);
                   switch (position) {

                       case ROW_FIVE:
                       {
                           if (vwType == OPENHOUSES_DISPLAY_ITEM || vwType == AUTOSPREE_DISPLAY_ITEM)
                           {
                               if (check_row != null)
                                   return check_row;
                               check_row = getChildPointerView(parent, txtHeight, width, "Check List");
                               checkListRowSetOnClick(itm, true);
                               return check_row;
                           }
                           else
                           {
                               if (camera_row != null)
                                   return camera_row;

                               camera_row = getChildPointerView(parent, txtHeight, width, "Camera");

                               camera_row.setOnClickListener(new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View view) {
                                                                     Log.d(getClass().getName(), "Clicked camera row starting camera");
                                                                     if (!PermissionsManager.getInstance().hasCameraPermission(ctxt))
                                                                     {
                                                                         return;
                                                                     }
                                                                     Intent intent = new Intent(ctxt, CameraActivity.class);
                                                                     intent.putExtra("album_name", itm.getAlbum_name());
                                                                     ctxt.startActivity(intent);
                                                                 }
                                                             }
                               );
                               return camera_row;
                           }
                       }

                       case ROW_SIX:
                       {
                           if (vwType == OPENHOUSES_DISPLAY_ITEM || vwType == AUTOSPREE_DISPLAY_ITEM)
                           {
                               if (notes_row != null)
                                   return notes_row;
                               notes_row = getChildPointerView(parent, txtHeight, width, "Notes");
                               notesRowSetOnClick(itm, true);
                               return notes_row;
                           }
                           else
                           {
                               if (getAdapter().isbCheckListChg()) {
                                   checkListRowSetOnClick(itm, false);
                                   getAdapter().setbCheckListChg(false);
                               }
                               if (check_row != null) {

                                   return check_row;
                               }
                               check_row = getChildPointerView(parent, txtHeight, width, "Check List");
                               checkListRowSetOnClick(itm, false);
                               return check_row;
                           }
                       }

                       case ROW_SEVEN:
                       {
                           if (vwType == OPENHOUSES_DISPLAY_ITEM || vwType == AUTOSPREE_DISPLAY_ITEM)
                           {
                               if (pictures_row != null)
                                   return pictures_row;
                               pictures_row = getChildPointerView(parent, txtHeight, width, "Pictures");
                               picturesRowSetOnClick(itm);
                               return pictures_row;
                           }
                           else
                           {
                               if (notes_row != null)
                                   return notes_row;
                               notes_row = getChildPointerView(parent, txtHeight, width, "Notes");
                               notesRowSetOnClick(itm, false);
                               return notes_row;
                           }
                       }


                       case ROW_EIGHT:
                       {
                           if (vwType == OPENHOUSES_DISPLAY_ITEM || vwType == AUTOSPREE_DISPLAY_ITEM)
                           {
                               map_row = getMapView(parent, txtHeight, width);
                               mapRowSetOnClick(itm);
                               return map_row;
                           }
                           else
                           {
                               if (pictures_row != null)
                                   return pictures_row;
                               pictures_row = getChildPointerView(parent, txtHeight, width, "Pictures");
                               picturesRowSetOnClick(itm);
                               return pictures_row;
                           }
                       }

                       case ROW_NINE:
                       {
                           if (vwType == OPENHOUSES_DISPLAY_ITEM || vwType == AUTOSPREE_DISPLAY_ITEM)
                           {
                               return getRatingsView(parent, txtHeight, width, itm);
                           }
                           else
                           {
                               map_row = getMapView(parent, txtHeight, width);
                               mapRowSetOnClick(itm);
                               return map_row;
                           }

                       }

                       case ROW_TEN:
                       {
                           if (vwType == OPENHOUSES_DISPLAY_ITEM || vwType == AUTOSPREE_DISPLAY_ITEM)
                           {
                               return getAddrRowView(parent, txtHeight, width, "Street:", itm.getStreet());
                           }
                           else
                           {
                               return getRatingsView(parent, txtHeight, width, null);

                           }

                       }

                       case ROW_ELEVEN:
                       {
                            if (vwType == OPENHOUSES_DISPLAY_ITEM || vwType == AUTOSPREE_DISPLAY_ITEM)
                            {
                                return getAddrRowView(parent, txtHeight, width, "City:", itm.getCity());
                            }
                            else
                            {
                                Log.d(TAG, "Street=" + itm.getStreet());
                                return getAddrRowView(parent, txtHeight, width, "Street:", itm.getStreet());
                            }

                       }

                       case ROW_TWELVE:
                       {
                           if (vwType == OPENHOUSES_DISPLAY_ITEM || vwType == AUTOSPREE_DISPLAY_ITEM)
                           {
                               return getAddrRowView(parent, txtHeight, width, "State:", itm.getState());
                           }
                           else
                           {
                               Log.d(TAG, "City=" + itm.getCity());
                               return getAddrRowView(parent, txtHeight, width, "City:", itm.getCity());
                           }

                       }


                       case ROW_THIRTEEN:
                       {
                           if (vwType == OPENHOUSES_DISPLAY_ITEM || vwType == AUTOSPREE_DISPLAY_ITEM)
                           {
                               return getAddrRowView(parent, txtHeight, width, "Zip:", itm.getZip());
                           }
                           else
                           {
                               Log.d(TAG, "State=" + itm.getState());
                               return getAddrRowView(parent, txtHeight, width, "State:", itm.getState());
                           }
                       }


                       case ROW_FOURTEEN:
                       {
                           if (!(vwType == OPENHOUSES_DISPLAY_ITEM || vwType == AUTOSPREE_DISPLAY_ITEM))
                           {
                               Log.d(TAG, "Zip=" + itm.getZip());
                               return getAddrRowView(parent, txtHeight, width, "Zip:", itm.getZip());
                           }
                          
                       }
                       break;


                       default:
                           break;
                   }
               }
               break;
               default:
                   break;
           }

           return null;
       }

      private void displayContactItem(View view, String contactname)
      {
          Item itm = (Item) view.getTag();
          Log.d(getClass().getName(), "Clicked row " + contactname);
          Intent intent = new Intent(ctxt, ShareActivity.class);
          intent.putExtra("ViewType", CONTACTS_ITEM_DISPLAY);
          intent.putExtra("app_name", app_name);
          intent.putExtra("item", itm);
          Activity itemAct = (Activity) ctxt;
          itemAct.startActivityForResult(intent, DELETE_CONTACT_ITEM_ACTIVITY_REQUEST);
      }

    public View getContactMainVwRow (ViewGroup parent , int txtHeight,  int width, Item itm)
    {
        final LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label, parent, false);


        ImageView disclosure = (ImageView) vw.findViewById(R.id.label_image_icon);
        disclosure.setMaxHeight(txtHeight);
        disclosure.setMaxWidth(width / 10);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins((width / 10) * 8, 5, 5, 5);
        disclosure.setLayoutParams(lp);
        String itemname = itm.getName();
        if (itemname == null || itemname.length() <=0)
        {
            itemname = Long.toString(itm.getShare_id());
        }

        final String contactname = itemname;
        Log.d(TAG, "Setting item name=" + itemname + " in CONTACTS_MAINVW");
        TextView tv = (TextView) vw.findViewById(R.id.name);
        tv.setHeight(txtHeight);
        tv.setWidth((width / 10) * 8);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.35f);
        tv.setText(itemname);
        tv.setTag(itm);
        disclosure.setTag(itm);
        tv.setOnClickListener(view -> displayContactItem(view, contactname)
        );
        disclosure.setOnClickListener(view -> displayContactItem(view, contactname)
        );
        return vw;
    }

    private void mapRowSetOnClick(final Item itm)
    {
        map_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(getClass().getName(), "Clicked Maps row displaying location map");
                Intent intent = new Intent(ctxt, MapsActivity.class);
                intent.putExtra("latitude", itm.getLatitude());
                intent.putExtra("longitude", itm.getLongitude());
                ctxt.startActivity(intent);
            }
        });
    }

    public View getCheckBoxedMainLstVw (ViewGroup parent , int txtHeight,  Item itm, int position)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.share_main, parent, false);
        TextView tv = (TextView) vw.findViewById(R.id.share_main_item);


        tv.setHeight(txtHeight);
        String itemname = getItemName(itm);

        Log.d(TAG, "Setting item name=" + itemname);
        tv.setText(itemname);
        tv.setTag(itm);

        //tv.setText(Item, TextView.BufferType.EDITABLE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.35f);
        tv.setOnClickListener(new View.OnClickListener() {

                                  @Override
                                  public void onClick(View view) {

                                      TextView tv = (TextView) view;
                                      Item itm = (Item) tv.getTag();
                                      CheckBox cbxi = itm.getCbx();
                                      cbxi.setChecked(!cbxi.isChecked());
                                      Log.d(TAG, "Toggling checkbox of shareItem " + itm.isSelected() + " cbx=" + cbxi.isChecked());
                                      itm.setSelected(cbxi.isChecked());
                                      if (cbxi.isChecked())
                                      {
                                          adapter.resetSelected(itm.getRowno());
                                          ShareVwTabbed  shareVwTabbed= (ShareVwTabbed) fragment;
                                          shareVwTabbed.attachPictures();
                                      }
                                  }
                              }

        );

        final CheckBox cbx = (CheckBox) vw.findViewById(R.id.share_main_chkbox);
        cbx.setTag(itm);
        itm.setCbx(cbx);
        itm.setRowno(position);

        cbx.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {

                                       CheckBox cbxi = (CheckBox) view;
                                       Item itm = (Item) cbxi.getTag();
                                       Log.d(TAG, "Toggling checkbox " + itm.isSelected() + " cbx=" + cbxi.isChecked());
                                       itm.setSelected(cbxi.isChecked());
                                       if (cbxi.isChecked())
                                       {
                                           adapter.resetSelected(itm.getRowno());
                                           ShareVwTabbed  shareVwTabbed= (ShareVwTabbed) fragment;
                                           shareVwTabbed.attachPictures();
                                       }

                                   }
                               }

        );

        return vw;

    }

       public  abstract void getKeyVals(Item itm);

    public View getRatingsView(ViewGroup parent, int txtHeight, int width, Item itm)
    {
        if (itm != null)
        {
            LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vw = inflater.inflate(R.layout.label_text, parent,false);
            TextView name = (TextView) vw.findViewById(R.id.name);
            name.setText("Ratings:    ");
            name.setHeight(txtHeight);
            name.setWidth(width/4);
            name.setTypeface(name.getTypeface(), Typeface.BOLD);
            name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);
            EditText name_value = (EditText) vw.findViewById(R.id.value);
            //itm.getRating()
            name_value.setText(Integer.toString(itm.getRating()));
            name_value.setKeyListener(null);
            name_value.setHeight(txtHeight);
            name_value.setWidth(width/3);
            name_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
            return vw;
        }
        else {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.ratings, parent,false);
        TextView label = (TextView) vw.findViewById(R.id.ohaspree_ratings_text);
        label.setText("Ratings: ");
        label.setHeight(txtHeight);
        label.setTypeface(label.getTypeface(), Typeface.BOLD);
        label.setWidth(width/4);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);

            ratingsSpnr = (Spinner) vw.findViewById(R.id.ohaspree_ratings_spnr);
            ArrayAdapter<CharSequence> ratingsAdapter = ArrayAdapter.createFromResource(ctxt,
                    R.array.ratings_value, android.R.layout.simple_spinner_item);

            ratingsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ratingsSpnr.setAdapter(ratingsAdapter);
            ratingsSpnr.setOnItemSelectedListener(this);
            ratingsSpnr.setSelection(ratings);
            ratingsSpnr.setMinimumHeight(txtHeight);

            return vw;
        }

    }



    private View getChildPointerView(ViewGroup parent, int txtHeight, int width, String lblTxt)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label, parent,false);
        TextView label = (TextView) vw.findViewById(R.id.name);
        label.setText(lblTxt);
        label.setHeight(txtHeight);
        label.setWidth((width/10)*8);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.37f);
        ImageView disclosure = (ImageView) vw.findViewById(R.id.label_image_icon);
        disclosure.setMaxHeight(txtHeight);
        disclosure.setMaxWidth(width/10);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins((width/10)*8, 5, 5, 5);
        disclosure.setLayoutParams(lp);

        return vw;
    }

    public View getMapView(ViewGroup parent, int txtHeight, int width)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label, parent,false);
        TextView label = (TextView) vw.findViewById(R.id.name);
        label.setText("Map");
        label.setHeight(txtHeight);
        label.setWidth((width/10)*8);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.37f);
        ImageView disclosure = (ImageView) vw.findViewById(R.id.label_image_icon);
        disclosure.setMaxHeight(txtHeight);
        disclosure.setMaxWidth(width/10);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins((width/10)*8, 5, 5, 5);
        disclosure.setLayoutParams(lp);

        return vw;
    }

    public View getAddrRowView(ViewGroup parent, int txtHeight, int width, String item, String value)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label_text, parent,false);
        TextView name = (TextView) vw.findViewById(R.id.name);
        name.setText(item);
        name.setHeight(txtHeight);
        name.setWidth(width/4);
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);
        name.setTypeface(name.getTypeface(), Typeface.BOLD);
        EditText name_value = (EditText) vw.findViewById(R.id.value);
        name_value.setText(value);
        name_value.setKeyListener(null);
        name_value.setHeight(txtHeight);
        name_value.setWidth((width/4)*3);
        name_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
        return vw;
    }

    protected void notesRowSetOnClick(final Item itm, final boolean displ)
    {
        notes_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(getClass().getName(), "Clicked Notes row displaying item notes");
                Intent intent = new Intent(ctxt, NotesActivity.class);
                intent.putExtra("display", displ);
                intent.putExtra("notes", itm.getNotes());
                Activity itemAct = (Activity) ctxt;
                itemAct.startActivityForResult(intent, NOTES_ACTIVITY_REQUEST);

            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id)
    {
        ratings = pos+1;
        return;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void checkListRowSetOnClick(final Item item, final boolean displ)
    {
        List<Item> chklist = adapter.getCheckList();
        if (displ)
        {
            if (chklist.size() ==0)
            {
                return;
            }
            else {
                check_row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ctxt, SingleItemActivity.class);
                        intent.putExtra("ViewType", CHECK_LIST_DISPLAY);
                        intent.putExtra("item", item);
                        intent.putParcelableArrayListExtra("check_list", (ArrayList<Item>) adapter.getCheckList());
                        Activity itemAct = (Activity) ctxt;
                        itemAct.startActivity(intent);
                    }
                });

            }
        }
        else
        {
            if (chklist.size() ==0)
            {
                check_row.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(ctxt, SingleItemActivity.class);
                        intent.putExtra("ViewType", CHECK_LIST_TEMPL_SELECTOR);
                        intent.putExtra("item", item);
                        intent.putExtra("check_list", true);
                        Activity itemAct = (Activity) ctxt;
                        itemAct.startActivityForResult(intent, ADD_CHECK_LIST_ACTIVITY_REQUEST);
                    }
                });

            }
            else
            {
                check_row.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                         Intent intent = new Intent(ctxt, SingleItemActivity.class);
                         intent.putExtra("ViewType", CHECK_LIST_EDIT);
                         intent.putExtra("item", item);
                         Activity itemAct = (Activity) ctxt;
                        intent.putParcelableArrayListExtra("check_list", (ArrayList<Item>) adapter.getCheckList());
                        itemAct.startActivityForResult(intent, EDIT_CHECK_LIST_ACTIVITY_REQUEST);
                    }
                });
            }

        }
    }

    protected void picturesRowSetOnClick(final Item itm)
    {
        album_name = itm.getAlbum_name();
        pictures_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(getClass().getName(), "Clicked picture row displaying image gallery");
                Intent intent = new Intent(ctxt, PhotoRoll.class);
                intent.putExtra("album_name", itm.getAlbum_name());
                intent.putExtra("app_name", app_name);
                intent.putExtra("ViewType", PICTURE_VW);
                ctxt.startActivity(intent);
            }
        });
    }



    public View getNoLabelView(ViewGroup parent, int txtHeight, int width, String txt)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label, parent,false);
        TextView label = (TextView) vw.findViewById(R.id.name);
        label.setText(txt);
        label.setHeight(txtHeight);
        label.setWidth(width);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.4f);
        return vw;
    }

       public View getContactNameView (ViewGroup parent, int txtHeight, int width, final Item itm, boolean edit)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label_text, parent,false);
        TextView name = (TextView) vw.findViewById(R.id.name);
        name.setText("Name: ");
        name.setHeight(txtHeight);
        name.setWidth(width/4);
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 3);
        EditText name_value = (EditText) vw.findViewById(R.id.value);
        if (edit && (itm.getName() != null && itm.getName().length() > 0))
        {
            name_value.setText(itm.getName(), TextView.BufferType.EDITABLE);
        }
        else if (!edit)
        {
            if (itm.getName() != null && itm.getName().length() > 0)
                name_value.setText(itm.getName());
            name_value.setKeyListener(null);
            name_value.setBackgroundResource(android.R.color.transparent);
        }

        if (edit) {
            name_value.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    itm.setName(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                }
            });
        }
        name_value.setHeight(txtHeight);
        name_value.setWidth((width/4)*3);
        name_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 3);
        return vw;
    }

    public View getContactShareId (ViewGroup parent, int txtHeight, int width, final Item itm, boolean edit)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label_text_ints, parent,false);
        TextView name = (TextView) vw.findViewById(R.id.name_ints);
        name.setText("Share Id: ");
        name.setHeight(txtHeight);
        name.setWidth(width/4);
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 3);
        EditText name_value = (EditText) vw.findViewById(R.id.value_ints);

        if (edit && (itm.getShare_id() != 0))
        {
            name_value.setText(Long.toString(itm.getShare_id()), TextView.BufferType.EDITABLE);
        }
        else if (!edit)
        {
            if (itm.getShare_id() != 0)
                name_value.setText(Long.toString(itm.getShare_id()));
            name_value.setKeyListener(null);
            name_value.setBackgroundResource(android.R.color.transparent);
        }
        if (edit) {
            name_value.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    String shId = s.toString();
                    if (shId.equals(""))
                    {
                        itm.setShare_id(0);
                    }
                    else {
                        itm.setShare_id(Integer.parseInt(shId));
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                }
            });
        }
        name_value.setHeight(txtHeight);
        name_value.setWidth((width/4)*3);
        name_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 3);

        return vw;
    }

}
