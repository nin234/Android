package com.rekhaninan.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.HashMap;


import static com.rekhaninan.common.Constants.ADD_CHECK_LIST_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.ADD_CHECK_LIST_ACTIVITY_REQUEST_2;
import static com.rekhaninan.common.Constants.AUTOSPREE_ADD_ITEM;
import static com.rekhaninan.common.Constants.AUTOSPREE_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.AUTOSPREE_EDIT_ITEM;
import static com.rekhaninan.common.Constants.CHECK_LIST_ADD;
import static com.rekhaninan.common.Constants.CHECK_LIST_DISPLAY;
import static com.rekhaninan.common.Constants.CHECK_LIST_EDIT;
import static com.rekhaninan.common.Constants.CHECK_LIST_TEMPL_SELECTOR;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM_OPTIONS;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_NEW_LIST;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_PIC_LIST;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_ROW_FOUR;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_ROW_TWO;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_TEMPL_LIST;
import static com.rekhaninan.common.Constants.EASYGROC_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_EDIT_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_NAME_ROW;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_EDIT_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_LISTS;
import static com.rekhaninan.common.Constants.MAINVW;
import static com.rekhaninan.common.Constants.NOTES_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.PICTURE_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.SHARE_MAINVW;

/**
 * Created by nin234 on 8/28/1
 */
public class EasyGrocRow extends RowView implements AdapterView.OnItemSelectedListener
{
    private final String TAG = "EasyGrocRow";
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        Spinner spinner = (Spinner) parent;
        Item seasonChgItem = (Item)spinner.getTag(1);
        if (seasonChgItem != null)
        {
            seasonChgItem.setStart_month(pos);
        }
        else
        {
            seasonChgItem = (Item)spinner.getTag(2);
            if (seasonChgItem != null)
            {
                seasonChgItem.setEnd_month(pos);
            }

        }
        parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    @Override
    public  String getItemName(Item itm)
    {
        return itm.getName();
    }

    public View getView(Item itm, int position, ViewGroup parent)
    {
        WindowManager wm = (WindowManager) ctxt.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        View svw = super.getView(itm, position, parent);
        if (svw != null)
        {
            Log.d(TAG, "svw not null");
            return svw;
        }
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int txtHeight = height / 12;


        switch (vwType)
        {
            case EASYGROC_TEMPL_LISTS:
            case MAINVW: {
                TextView tv = new TextView(ctxt);
                tv.setHeight(txtHeight);
                tv.setText(itm.getName());
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
                tv.setTag(itm);
                tv.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {

                                              TextView tv = (TextView) view;
                                              Item itm = (Item) tv.getTag();
                                              Log.d(getClass().getName(), "Clicked row " + tv.getText());
                                              Intent intent = new Intent(ctxt, SingleItemActivity.class);
                                              if (vwType == MAINVW) {
                                                  intent.putExtra("ViewType", EASYGROC_DISPLAY_ITEM);
                                              }
                                              else if (vwType == EASYGROC_TEMPL_LISTS)
                                              {
                                                  intent.putExtra("ViewType", EASYGROC_TEMPL_DISPLAY_ITEM);
                                              }
                                              intent.putExtra("item", itm);
                                              ctxt.startActivity(intent);
                                          }
                                      }

                );
                return tv;
            }


            case CHECK_LIST_ADD:
            {
                return getCheckedLabelView(parent, txtHeight, width, itm);
            }

            case CHECK_LIST_DISPLAY:
            {
                return getCheckedLabelViewNoSel(parent, txtHeight, width, itm);
            }

            case CHECK_LIST_EDIT:
            {
                return getCheckedLabelView(parent, txtHeight, width, itm);
            }

            case CHECK_LIST_TEMPL_SELECTOR:
            {
                switch (position)
                {
                    case EASYGROC_NAME_ROW:
                    {
                        return getLabelView(parent, txtHeight, width, "Create from Check Lists");
                    }

                    case EASYGROC_ADD_ROW_TWO:
                    {
                        return getNoLabelView(parent, txtHeight, width, "  ");
                    }

                    default:
                    {
                        View vw = getTemplView(parent, txtHeight, width, itm.getName());

                        final String name = itm.getName();

                        vw.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View view) {
                                                      Log.d(getClass().getName(), "Clicked row Template item");
                                                      Item itmInt = new Item();
                                                      itmInt.setName(name);
                                                      Intent intent = new Intent(ctxt, SingleItemActivity.class);
                                                      intent.putExtra("ViewType", CHECK_LIST_ADD);
                                                      intent.putExtra("item", itmInt);
                                                      Activity itemAct = (Activity) ctxt;
                                                      itemAct.startActivityForResult(intent, ADD_CHECK_LIST_ACTIVITY_REQUEST_2);
                                                  }
                                              }

                        );
                        return vw;
                    }

                }
            }

            case EASYGROC_ADD_ITEM_OPTIONS:
            {
                switch (position)
                {
                    case EASYGROC_ADD_NEW_LIST:
                    {
                        View vw = getLabelView(parent, txtHeight, width, "Brand New List");
                        vw.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View view) {
                                                      Log.d(getClass().getName(), "Clicked row Brand New List");
                                                      Item itm = new Item();
                                                      Intent intent = new Intent(ctxt, SingleItemActivity.class);
                                                      intent.putExtra("ViewType", EASYGROC_ADD_ITEM);
                                                      intent.putExtra("item", itm);
                                                      ctxt.startActivity(intent);
                                                  }
                                              }

                        );
                        return vw;
                    }

                    case EASYGROC_ADD_ROW_TWO:
                    case EASYGROC_ADD_ROW_FOUR:
                    {
                        return getNoLabelView(parent, txtHeight, width, "  ");
                    }

                    case EASYGROC_ADD_PIC_LIST:
                    {
                       View vw = getCameraView(parent, txtHeight, width, "Picture List");
                        vw.setOnClickListener(new View.OnClickListener() {
                                                          @Override
                                                          public void onClick(View view) {
                                                              Log.d(getClass().getName(), "Clicked Picture List starting camera");
                                                              if (!PermissionsManager.getInstance().hasCameraPermission(ctxt))
                                                              {
                                                                  return;
                                                              }
                                                              Intent intent = new Intent(ctxt, CameraActivity.class);
                                                              intent.putExtra("album_name", EASYGROC);
                                                              Activity itemAct = (Activity) ctxt;
                                                              itemAct.startActivityForResult(intent, PICTURE_ACTIVITY_REQUEST);
                                                          }
                                                      }
                        );
                        return vw;

                    }

                    case EASYGROC_ADD_TEMPL_LIST:
                    {
                        return getLabelView(parent, txtHeight, width, "Create from Template Lists");
                    }

                    default:
                    {
                        View vw = getTemplView(parent, txtHeight, width, itm.getName());

                        final String name = itm.getName();

                        vw.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View view) {
                                                      Log.d(getClass().getName(), "Clicked row Template item");
                                                      Item itmInt = new Item();
                                                      itmInt.setName(name);
                                                      Intent intent = new Intent(ctxt, SingleItemActivity.class);
                                                      intent.putExtra("ViewType", EASYGROC_ADD_ITEM);
                                                      intent.putExtra("item", itmInt);
                                                      ctxt.startActivity(intent);
                                                  }
                                              }

                        );
                        return vw;
                    }

                }

            }

            case EASYGROC_DISPLAY_ITEM:
            {
                switch (position)
                {

                    case EASYGROC_NAME_ROW:
                        return getNoLabelView(parent, txtHeight, width, itm.getName());


                    default:
                        return getLabelSwitchView(parent, txtHeight, width, itm);

                }
            }

            case EASYGROC_TEMPL_DISPLAY_ITEM:
            {
                switch (position) {
                    case EASYGROC_NAME_ROW:
                        return getNoLabelView(parent, txtHeight, width, itm.getName());

                    default: {
                        String app_name = DBOperations.getInstance().getApp_name();
                        if (app_name.equals("EASYGROC"))
                        {
                            return getLabelSwitchInventoryView(parent, txtHeight, width, itm, EASYGROC_TEMPL_DISPLAY_ITEM);
                        }
                        else {
                            return getNoLabelView(parent, txtHeight, width, itm.getItem());
                        }
                    }
                }
            }


            case EASYGROC_TEMPL_ADD_ITEM:
            {
                switch (position)
                {
                    case EASYGROC_NAME_ROW:
                        return getNameRowView(parent, txtHeight, width, itm);

                    default: {
                        String app_name = DBOperations.getInstance().getApp_name();
                        if (app_name.equals("EASYGROC"))
                        {
                            return getAddEditViewWithSeasonPicker(parent, txtHeight, width, itm);
                        }
                        else {
                            return getAddEditView(parent, txtHeight, width, itm);
                        }
                    }

                }
            }

            case EASYGROC_ADD_ITEM:
            {
                switch (position)
                {
                    case EASYGROC_NAME_ROW:
                        return getName1RowView(parent, txtHeight, width, itm);

                    default:
                        return getAddEditView(parent, txtHeight, width, itm);
                }
            }

            case EASYGROC_EDIT_ITEM:
            {
                switch (position)
                {
                    case EASYGROC_NAME_ROW:
                        return getName2RowView(parent, txtHeight, width, itm);

                    default:
                        String app_name = DBOperations.getInstance().getApp_name();
                        if (app_name.equals("EASYGROC"))
                        {
                            return getAddEditViewWithSeasonPicker(parent, txtHeight, width, itm);
                        }
                        else {
                            return getAddEditView(parent, txtHeight, width, itm);
                        }

                }
            }

            case EASYGROC_TEMPL_EDIT_ITEM:
            {
                switch (position)
                {
                    case EASYGROC_NAME_ROW:
                        return getName2RowView(parent, txtHeight, width, itm);

                    default:
                        return getAddEditView(parent, txtHeight, width, itm);
                }
            }

            default:
                break;
        }

        return null;
    }

    private View getAddEditViewWithSeasonPicker(final ViewGroup parent, int txtHeight, int width, final Item itm)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.easygroc_main_season, parent,false);
        final EditText item = (EditText) vw.findViewById(R.id.list_item_string_season);
        item.setHeight(txtHeight);
        item.setWidth((width/10)*6);
        item.setTag(itm);

        if (itm.getItem() != null && itm.getItem().length() > 0)
        {
            item.setText(itm.getItem(), TextView.BufferType.EDITABLE);
        }

        item.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                itm.setItem(s.toString());
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
        Button delet = (Button) vw.findViewById(R.id.delete_btn_season);
        delet.setHeight(txtHeight);
        delet.setWidth(width/10);
        delet.setTag(itm);
        delet.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {

                                         Button tv = (Button) view;
                                         Item itm = (Item) tv.getTag();
                                         Log.d(getClass().getName(), "Clicked row " + itm.getRowno());
                                         adapter.removeItem(itm);
                                         adapter.notifyDataSetChanged();

                                     }
                                 }

        );

        Button add = (Button) vw.findViewById(R.id.add_btn_season);
        add.setHeight(txtHeight);
        add.setWidth(width/10);
        add.setTag(itm);
        add.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {

                                       Log.d(getClass().getName(), "Clicked row " + itm.getRowno());
                                       Item newItm = new Item();
                                       newItm.setRowno(itm.getRowno()+1);
                                       adapter.addItem(newItm);
                                       adapter.notifyDataSetChanged();

                                   }
                               }

        );

        final Button spicker = (Button) vw.findViewById(R.id.season_picker_btn_season);
        spicker.setHeight(txtHeight);
        spicker.setWidth(width/10);

        final EasyGrocRow pEasyGrocRow = this;

        spicker.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {


                                       LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View seasonPickerView = inflater.inflate(R.layout.season_picker, parent, false);
                                       Spinner spinner1 = (Spinner) seasonPickerView.findViewById(R.id.spinner1);
                                       ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ctxt,
                                               R.array.months, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                                       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                                       spinner1.setAdapter(adapter);
                                      spinner1.setOnItemSelectedListener(pEasyGrocRow);
                                       spinner1.setTag(1, itm);
                                       spinner1.setSelection(itm.getStart_month());
                                       Spinner spinner2 = (Spinner) seasonPickerView.findViewById(R.id.spinner2);
                                       spinner2.setAdapter(adapter);
                                       spinner2.setOnItemSelectedListener(pEasyGrocRow);
                                       spinner2.setTag(2, itm);
                                       spinner2.setSelection(itm.getEnd_month());

                                       final PopupWindow popupWindow = new PopupWindow(
                                               seasonPickerView,
                                               ViewGroup.LayoutParams.WRAP_CONTENT,
                                               ViewGroup.LayoutParams.WRAP_CONTENT);

                                       Button btnDismiss = (Button)seasonPickerView.findViewById(R.id.btnSubmit);
                                       btnDismiss.setOnClickListener(new Button.OnClickListener(){

                                           @Override
                                           public void onClick(View v) {
                                               // TODO Auto-generated method stub
                                               popupWindow.dismiss();
                                           }});

                                       popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                                   }
                               }

        );

        return vw;

    }

    private View getAddEditView(ViewGroup parent, int txtHeight, int width, final Item itm)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.easygroc_main, parent,false);
        final EditText item = (EditText) vw.findViewById(R.id.list_item_string);
        item.setHeight(txtHeight);
        item.setWidth((width/10)*7);
        item.setTag(itm);

        if (itm.getItem() != null && itm.getItem().length() > 0)
        {
            item.setText(itm.getItem(), TextView.BufferType.EDITABLE);
        }

        item.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                itm.setItem(s.toString());
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
        Button delet = (Button) vw.findViewById(R.id.delete_btn);
        delet.setHeight(txtHeight);
        delet.setWidth(width/10);
        delet.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {

                                         TextView tv = (TextView) view;
                                         Item itm = (Item) tv.getTag();
                                         Log.d(getClass().getName(), "Clicked row " + itm.getRowno());
                                         adapter.removeItem(itm);
                                         adapter.notifyDataSetChanged();

                                     }
                                 }

        );

        Button add = (Button) vw.findViewById(R.id.add_btn);
        add.setHeight(txtHeight);
        add.setWidth(width/10);
        add.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {

                                         TextView tv = (TextView) view;
                                         Item itm = (Item) tv.getTag();
                                         Log.d(getClass().getName(), "Clicked row " + itm.getRowno());
                                         Item newItm = new Item();
                                         newItm.setRowno(itm.getRowno()+1);
                                         adapter.addItem(newItm);
                                         adapter.notifyDataSetChanged();

                                     }
                                 }

        );

        return vw;

    }

    private View getName2RowView(ViewGroup parent, int txtHeight, int width, final Item itm)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.just_text, parent,false);

        EditText name_value = (EditText) vw.findViewById(R.id.name);
        if (itm.getName() != null && itm.getName().length() > 0)
        {
            name_value.setText(itm.getName());
        }
        name_value.setHeight(txtHeight);
        name_value.setWidth(width);
        name_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        return vw;
    }
    private View getName1RowView(ViewGroup parent, int txtHeight, int width, final Item itm)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.just_text, parent,false);

        EditText name_value = (EditText) vw.findViewById(R.id.name);
        if (itm.getName() != null && itm.getName().length() > 0)
        {
            name_value.setText(itm.getName(), TextView.BufferType.EDITABLE);
        }
        name_value.setHeight(txtHeight);
        name_value.setWidth(width);
        name_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        return vw;
    }

    private View getNameRowView(ViewGroup parent, int txtHeight, int width, final Item itm)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label_text, parent,false);
        TextView name = (TextView) vw.findViewById(R.id.name);
        name.setText("Name:");
        name.setHeight(txtHeight);
        name.setWidth(width/4);
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        EditText name_value = (EditText) vw.findViewById(R.id.value);
        if (itm.getName() != null && itm.getName().length() > 0)
        {
            name_value.setText(itm.getName(), TextView.BufferType.EDITABLE);
        }
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
        name_value.setHeight(txtHeight);
        name_value.setWidth((width/4)*3);
        name_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        return vw;
    }

    private View getLabelSwitchInventoryView(ViewGroup parent, int txtHeight, int width, final Item itm, final int vwType)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label_switch, parent, false);
        TextView label = (TextView) vw.findViewById(R.id.listItem_inv);
        if (itm != null && itm.getItem() != null) {
            label.setText(itm.getItem());
        }
        else
        {
            label.setText(" ");
        }
        label.setHeight(txtHeight);
        label.setWidth((width / 10) * 8);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        Switch onoff = (Switch) vw.findViewById(R.id.toggle_inv);
        if (itm.getInventory() > 0)
        {
            onoff.setChecked(true);
        }
        else
        {
            onoff.setChecked(false);
        }

        onoff.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // do something, the isChecked will be
                        // true if the switch is in the On position
                        Log.d(TAG, "Toggled inventory in " + itm.getItem() + " inv=" + itm.getInventory());
                        if (isChecked)
                        {
                            if (itm.getInventory() > 0)
                            {
                                return;
                            }
                            else
                            {
                                itm.setInventory(10);
                                DBOperations.getInstance().updateDb(itm, vwType);
                            }
                        }
                        else
                        {
                            if (itm.getInventory() == 0)
                            {
                                return;
                            }
                            else
                            {
                                itm.setInventory(0);
                                DBOperations.getInstance().updateDb(itm, vwType);
                            }
                        }

                    }
                }
        );

        onoff.setMaxHeight(txtHeight);
        onoff.setMaxWidth((width / 10)*2);

        return vw;
    }

    private View getLabelSwitchView(ViewGroup parent, int txtHeight, int width, final Item itm)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label_switch, parent, false);
        TextView label = (TextView) vw.findViewById(R.id.listItem);
        if (itm != null && itm.getItem() != null) {
            label.setText(itm.getItem());
        }
        else
        {
            label.setText(" ");
        }
        label.setHeight(txtHeight);
        label.setWidth((width / 10) * 8);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        Switch onoff = (Switch) vw.findViewById(R.id.done);
        onoff.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // do something, the isChecked will be
                        // true if the switch is in the On position
                        if (isChecked)
                        {
                             itm.setHidden(false);
                              adapter.notifyDataSetChanged();
                            adapter.undoPush(itm.getRowno());
                            Log.d(TAG, "Toggled tag to false in " + itm.getItem());
                        }
                        else
                        {
                            itm.setHidden(true);
                            adapter.undoPush(itm.getRowno());
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Toggled tag to true in " + itm.getItem());
                        }

                    }
                }
        );

                    onoff.setMaxHeight(txtHeight);
        onoff.setMaxWidth((width / 10)*2);

        return vw;
    }

    private View getTemplView(ViewGroup parent, int txtHeight, int width, String labelTxt)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label, parent, false);
        TextView label = (TextView) vw.findViewById(R.id.name);
        label.setText(labelTxt);
        label.setHeight(txtHeight);
        label.setWidth((width / 10) * 8);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        ImageView disclosure = (ImageView) vw.findViewById(R.id.label_image_icon);
        disclosure.setMaxHeight(txtHeight);
        disclosure.setMaxWidth(width / 10);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins((width / 10) * 8, 5, 5, 5);
        disclosure.setLayoutParams(lp);

        return vw;

    }

    private View getCheckedLabelViewNoSel (ViewGroup parent, int txtHeight, int width, final Item itm)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label_checked, parent, false);

        final CheckedTextView label = (CheckedTextView) vw.findViewById(R.id.name_checked);
        label.setText(itm.getItem());
        label.setHeight(txtHeight);
        label.setWidth(width);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        if (itm.isSelected())
        {
            label.setCheckMarkDrawable(R.drawable.ic_done_black_48dp);
            label.setChecked(true);
        }
        else
        {
            label.setCheckMarkDrawable(0);
            label.setChecked(false);
        }

        return vw;
    }



    private View getCheckedLabelView (ViewGroup parent, int txtHeight, int width, final Item itm)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label_checked, parent, false);

        final CheckedTextView label = (CheckedTextView) vw.findViewById(R.id.name_checked);
        label.setText(itm.getItem());
        label.setHeight(txtHeight);
        label.setWidth(width);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (label.isChecked()) {
                    label.setCheckMarkDrawable(0);
                    label.setChecked(false);
                    itm.setSelected(false);
                } else {
                    label.setCheckMarkDrawable(R.drawable.ic_done_black_48dp);
                    label.setChecked(true);
                    itm.setSelected(true);
                }
            }
        });
        if (itm.isSelected())
        {
            label.setCheckMarkDrawable(R.drawable.ic_done_black_48dp);
            label.setChecked(true);
        }
        else
        {
            label.setCheckMarkDrawable(0);
            label.setChecked(false);
        }

        return vw;
    }

    private View getLabelView(ViewGroup parent, int txtHeight, int width, String labelTxt)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label, parent, false);

        TextView label = (TextView) vw.findViewById(R.id.name);
        label.setText(labelTxt);
        label.setHeight(txtHeight);
        label.setWidth((width / 10) * 8);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        ImageView disclosure = (ImageView) vw.findViewById(R.id.label_image_icon);
        disclosure.setMaxHeight(txtHeight);
        disclosure.setMaxWidth(width / 10);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins((width / 10) * 8, 5, 5, 5);
        disclosure.setLayoutParams(lp);

        return vw;

    }



    public void getKeyVals(Item itm)
    {
        return;
    }
}
