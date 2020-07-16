package com.rekhaninan.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;



import androidx.appcompat.app.AlertDialog;
import androidx.viewpager2.widget.ViewPager2;

import static com.rekhaninan.common.Constants.ADD_CHECK_LIST_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.ADD_CHECK_LIST_ACTIVITY_REQUEST_2;
import static com.rekhaninan.common.Constants.ALWAYS_POSN;
import static com.rekhaninan.common.Constants.AUTOSPREE_ADD_ITEM;
import static com.rekhaninan.common.Constants.AUTOSPREE_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.AUTOSPREE_EDIT_ITEM;
import static com.rekhaninan.common.Constants.CHECK_LIST_ADD;
import static com.rekhaninan.common.Constants.CHECK_LIST_DISPLAY;
import static com.rekhaninan.common.Constants.CHECK_LIST_EDIT;
import static com.rekhaninan.common.Constants.CHECK_LIST_TEMPL_SELECTOR;
import static com.rekhaninan.common.Constants.CONTACTS_POSN;
import static com.rekhaninan.common.Constants.DELETE_TEMPL_CHECKLIST_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM_OPTIONS;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_NEW_LIST;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_ROW_TWO;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_TEMPL_LIST;
import static com.rekhaninan.common.Constants.EASYGROC_BRAND_NEW_ADD_REQUEST;
import static com.rekhaninan.common.Constants.EASYGROC_DELETE_ITEM_REQUEST;
import static com.rekhaninan.common.Constants.EASYGROC_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_EDIT_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_EDIT_VIEW;
import static com.rekhaninan.common.Constants.EASYGROC_NAME_ROW;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_DELETE_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_EDIT_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_LISTS;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_NAME_LISTS;
import static com.rekhaninan.common.Constants.HOME_POSN;
import static com.rekhaninan.common.Constants.MAINVW;
import static com.rekhaninan.common.Constants.NOTES_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.ONETIME_POSN;
import static com.rekhaninan.common.Constants.PICTURE_ACTIVITY_REQUEST;
import static com.rekhaninan.common.Constants.PLANNER_POSN;
import static com.rekhaninan.common.Constants.REPLENISH_POSN;
import static com.rekhaninan.common.Constants.SHARE_MAINVW;
import static com.rekhaninan.common.Constants.SHARE_POSN;

/**
 * Created by nin234 on 8/28/1
 */
public class EasyGrocRow extends RowView implements AdapterView.OnItemSelectedListener
{
    private final String TAG = "EasyGrocRow";
    ViewPager2 viewPager;
    TabLayout tabLayout;

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        Spinner spinner = (Spinner) parent;
        Item seasonChgItem = (Item)spinner.getTag(R.id.START_MONTH_SPINNER);
        if (seasonChgItem != null)
        {
            seasonChgItem.setStart_month(pos);
        }
        else
        {
            seasonChgItem = (Item)spinner.getTag(R.id.END_MONTH_SPINNER);
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

    private View getTemplNameRowView(int txtHeight, final Item itm, final ViewGroup parent, int width)
    {
        final LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label, parent, false);

        TextView label = (TextView) vw.findViewById(R.id.name);
        label.setText(itm.getName());
        label.setHeight(txtHeight);
        label.setWidth((width / 10) * 8);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.35f);
        label.setTag(itm);

        ImageView disclosure = (ImageView) vw.findViewById(R.id.label_image_icon);
        disclosure.setMaxHeight(txtHeight);
        disclosure.setMaxWidth(width / 10);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins((width / 10) * 8, 5, 5, 5);
        disclosure.setLayoutParams(lp);

        vw.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View view) {


                                      Intent intent = new Intent(ctxt, PlannerActivity.class);
                                      intent.putExtra("item", itm);
                                      intent.putExtra("View", EASYGROC_EDIT_VIEW);
                                      Activity plannerAct = (Activity) ctxt;
                                      plannerAct.startActivityForResult(intent, EASYGROC_TEMPL_DISPLAY_ITEM);


                                  }
                              }
        );
        return vw;
    }

    private View getMainRowVw(int txtHeight, final Item itm, ViewGroup parent, int width)
    {
        final LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label, parent, false);

        TextView label = (TextView) vw.findViewById(R.id.name);
        label.setText(itm.getName());
        label.setHeight(txtHeight);
        label.setWidth((width / 10) * 8);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.35f);
        label.setTag(itm);

        ImageView disclosure = (ImageView) vw.findViewById(R.id.label_image_icon);
        disclosure.setMaxHeight(txtHeight);
        disclosure.setMaxWidth(width / 10);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins((width / 10) * 8, 5, 5, 5);
        disclosure.setLayoutParams(lp);

        vw.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View view) {


                                      Log.d(getClass().getName(), "Clicked row " + itm.getName());
                                      Intent intent = new Intent(ctxt, SingleItemActivity.class);
                                      if (vwType == MAINVW) {
                                          intent.putExtra("ViewType", EASYGROC_DISPLAY_ITEM);
                                      }
                                      else if (vwType == EASYGROC_TEMPL_LISTS)
                                      {
                                          intent.putExtra("ViewType", EASYGROC_TEMPL_DISPLAY_ITEM);
                                      }
                                      intent.putExtra("item", itm);

                                      String app_name = DBOperations.getInstance().getApp_name();
                                      if (app_name.equals(EASYGROC) && vwType   == EASYGROC_TEMPL_LISTS)
                                      {
                                          ctxt.startActivity(intent);

                                      }
                                      else if (app_name.equals(EASYGROC) && vwType   == MAINVW)
                                      {
                                          Activity mainVwTabbed = (Activity) ctxt;
                                          mainVwTabbed.startActivityForResult(intent, EASYGROC_DELETE_ITEM_REQUEST);
                                      }
                                      else
                                      {
                                          if (vwType == EASYGROC_TEMPL_LISTS)
                                          {
                                              Activity itemAct = (Activity) ctxt;
                                              itemAct.startActivityForResult(intent, DELETE_TEMPL_CHECKLIST_ACTIVITY_REQUEST);
                                          }
                                          else
                                          {
                                              ctxt.startActivity(intent);
                                          }
                                      }
                                  }
                              }

        );
        return vw;
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
            case EASYGROC_TEMPL_NAME_LISTS:
            {
                return getTemplNameRowView(txtHeight, itm, parent, width);
            }

            case EASYGROC_TEMPL_LISTS:
            case MAINVW: {
                return getMainRowVw(txtHeight, itm, parent, width);
            }

            case EASYGROC_TEMPL_DELETE_ITEM:
            {
                return  getCheckBoxedMainLstVw(parent, txtHeight, itm, position);
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
                                                      //itemAct.finish();
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
                        return getBrandNewListLineView(parent, txtHeight, width);
                    }

                    case EASYGROC_ADD_ROW_TWO:

                    {
                        return getNoLabelView(parent, txtHeight, width, "  ");
                    }


                    case EASYGROC_ADD_TEMPL_LIST:
                    {
                        return getNoLabelView(parent, txtHeight, width, "Create List from Planner");
                    }

                    default:
                    {
                        View vw = getTemplView(parent, txtHeight, width, itm.getName());

                        final String name = itm.getName();

                        vw.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View view) {

                                                      Log.d(getClass().getName(), "Clicked row Template item="+name);
                                                      Item itmInt = new Item();
                                                      itmInt.setName(name);
                                                      addEasyGrocItemFromTemplItem(itmInt);

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

                    case EASYGROC_NAME_ROW: {
                        return getNoLabelView(parent, txtHeight, width, itm.getName());

                    }


                    default:
                        return getLabelSwitchView(parent, txtHeight, width, itm);

                }
            }

            case EASYGROC_TEMPL_DISPLAY_ITEM:
            {
                switch (position) {
                    case EASYGROC_NAME_ROW: {
                        if (itm.getName() != null && itm.getName().length() > 0)
                        {
                            String name_val;
                            if (itm.getName().contains(":"))
                            {
                                name_val   = itm.getName().substring(0, itm.getName().lastIndexOf(':'));
                            }
                            else
                            {
                                name_val = itm.getName();
                            }
                            Log.i(TAG, "Template list name=" + name_val + " app_name=" + DBOperations.getInstance().getApp_name());
                            return getNoLabelView(parent, txtHeight, width, name_val);
                        }
                        String invalid = "invalid";
                        return getNoLabelView(parent, txtHeight, width, invalid);

                    }

                    default: {
                        String app_name = DBOperations.getInstance().getApp_name();
                        if (app_name.equals(EASYGROC))
                        {
                            Item nameitem = getAdapter().getItem(0);
                            if (nameitem.getName().endsWith(":INV")) {
                                return getLabelSwitchInventoryView(parent, txtHeight, width, itm, EASYGROC_TEMPL_DISPLAY_ITEM);
                            }
                            else
                            {
                                return getEasyTemplRow(parent, txtHeight, width, itm.getItem());
                            }

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
                        Log.i(TAG, "Returning view for item name=" + itm.getName() + " item=" + itm.getItem());
                        String app_name = DBOperations.getInstance().getApp_name();
                        if (app_name.equals(EASYGROC) && adapter.isRecrLst())
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
                        return getAddEditView(parent, txtHeight, width, itm);

                }
            }

            case EASYGROC_TEMPL_EDIT_ITEM:
            {
                switch (position)
                {
                    case EASYGROC_NAME_ROW:
                        return getName2RowView(parent, txtHeight, width, itm);
                    default: {
                        String app_name = DBOperations.getInstance().getApp_name();
                        Log.i(TAG, "Returning view for item name=" + itm.getName() + " item=" + itm.getItem());
                        if (app_name.equals(EASYGROC) && adapter.isRecrLst())
                        {
                            return getAddEditViewWithSeasonPicker(parent, txtHeight, width, itm);
                        }
                        else {
                            return getAddEditView(parent, txtHeight, width, itm);
                        }
                    }
                }
            }

            default:
                break;
        }

        return null;
    }

    private View getBrandNewListLineView(ViewGroup parent, int txtHeight, int width)
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
                                      Activity singleItemActivity = (Activity) ctxt;
                                      singleItemActivity.startActivityForResult(intent, EASYGROC_BRAND_NEW_ADD_REQUEST);

                                  }
                              }

        );
        return vw;
    }
    private void addEasyGrocItemFromTemplItem(Item itm)
    {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        Calendar c = new GregorianCalendar();
        String formattedDate = df.format(c.getTime());
        String app_name = DBOperations.getInstance().getApp_name();
        java.util.List<Item> mainLst = new ArrayList<Item>();

        java.util.List<Item> templList = DBOperations.getInstance().getTemplList(itm.getName(), itm.getShare_id());
            Item titleItem = new Item();
            titleItem.setName(itm.getName() + " " + formattedDate);
            mainLst.add(titleItem);
            int month = c.get(Calendar.MONTH);
            for (Item litm : templList) {
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
                mainLst.add(litm);

            }


                java.util.List<Item> templInvList = DBOperations.getInstance().getTemplList(itm.getName() + ":INV", itm.getShare_id());
                Log.i(TAG, "No of elements in inventory list for " + itm.getName() + ":INV for=" + templInvList.size());
                for (Item invItem : templInvList) {
                    if (invItem.getInventory() > 0)
                        continue;
                    mainLst.add(invItem);
                }
                java.util.List<Item> templScrList = DBOperations.getInstance().getTemplList(itm.getName() + ":SCRTCH", itm.getShare_id());
                for (Item scrItem : templScrList) {
                    mainLst.add(scrItem);
                }



        Item nameItem = mainLst.get(0);


        String name = nameItem.getName();
        int i=0;
        Log.d(TAG, "Inserting into db new list="+ name);
        for (Item itmL : mainLst)
        {
            if (i == 0) {
                ++i;
                continue;
            }
            if (itmL.getItem() == null || itmL.getItem().length() <= 0)
            {
                continue;
            }
            itmL.setName(name);
            itmL.setRowno(i);
            itmL.setShare_id(ShareMgr.getInstance().getShare_id());

            Log.d(TAG, "Inserting into db item="+ itmL.getItem() +
                    " share_id="+itmL.getShare_id()+ " rowno=" + itmL.getRowno());
            DBOperations.getInstance().insertDb(itmL, EASYGROC_ADD_ITEM);

            ++i;
        }


            if (itm.getName() != null && itm.getName().length() > 0)
            {

                Item scrtchItem = new Item();
                scrtchItem.setName(itm.getName() + ":SCRTCH");
                DBOperations.getInstance().deleteDb(scrtchItem, EASYGROC_ADD_ITEM);
                templInvList = DBOperations.getInstance().getTemplList(itm.getName() + ":INV", itm.getShare_id());
                for (Item invItem : templInvList) {
                    if (invItem.getInventory() > 0)
                        continue;
                    invItem.setInventory(10);
                    DBOperations.getInstance().updateDb(invItem, EASYGROC_TEMPL_DISPLAY_ITEM);
                }
            }


        Activity activity = (Activity) ctxt;
        Intent intent = new Intent();
        intent.putExtra("refresh", "Needed");
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }


    private View getAddEditViewWithSeasonPicker(final ViewGroup parent, final int txtHeight, final int width, final Item itm)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View vw = inflater.inflate(R.layout.easygroc_main_season, parent,false);
        final EditText item = (EditText) vw.findViewById(R.id.list_item_string_season);
        item.setHeight(txtHeight);
        //item.setWidth((int)(width*0.8));
        item.setTag(itm);

        if (itm.isShowSeasonPicker())
        {

            View seasonPickerView = inflater.inflate(R.layout.season_picker, parent, false);
            seasonPickerView.setMinimumHeight(txtHeight);
            seasonPickerView.setMinimumWidth(width);
            Button doneBtn =  (Button) seasonPickerView.findViewById(R.id.btnSubmit);
            doneBtn.setHeight(txtHeight);
            doneBtn.setWidth(width/5);
            Spinner spinner1 = (Spinner) seasonPickerView.findViewById(R.id.spinner1);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ctxt,
                    R.array.months, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
            spinner1.setAdapter(adapter);
            spinner1.setOnItemSelectedListener(this);

            spinner1.setTag(R.id.START_MONTH_SPINNER, itm);
            spinner1.setSelection(itm.getStart_month());
            spinner1.setMinimumHeight(txtHeight);
           // spinner1.setMinimumWidth((width*2)/5);
            Spinner spinner2 = (Spinner) seasonPickerView.findViewById(R.id.spinner2);
            spinner2.setAdapter(adapter);
            spinner2.setOnItemSelectedListener(this);
            spinner2.setTag(R.id.END_MONTH_SPINNER, itm);
            spinner2.setSelection(itm.getEnd_month());
            Log.i(TAG, "Setting selections spinner1=" + itm.getStart_month() + " spinner2=" +itm.getEnd_month());

            spinner2.setMinimumHeight(txtHeight);
           // spinner2.setMinimumWidth((width*2)/5);
            Button btnDismiss = (Button)seasonPickerView.findViewById(R.id.btnSubmit);
            btnDismiss.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    itm.setShowSeasonPicker(false);
                    getAdapter().notifyDataSetChanged();

                }});
            return seasonPickerView;
        }

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
       // delet.setHeight(txtHeight/2);
       // delet.setWidth(width/50);
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
       // add.setHeight(txtHeight);

      //  add.setWidth(width/10);
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
       // spicker.setHeight(txtHeight);
     //   spicker.setWidth(width/10);

        final EasyGrocRow pEasyGrocRow = this;

        spicker.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {

                                       itm.setShowSeasonPicker(true);
                                        adapter.notifyDataSetChanged();

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

        Button add = (Button) vw.findViewById(R.id.add_btn);
        add.setHeight(txtHeight);
        add.setTag(itm);
        add.setWidth(width/10);
        add.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {

                                         Button tv = (Button) view;
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
            String name_val;
            if (itm.getName().contains(":"))
            {
                name_val   = itm.getName().substring(0, itm.getName().lastIndexOf(':'));
            }
            else
            {
                name_val = itm.getName();
            }
            Log.i(TAG, "Template list name=" + name_val);
            name_value.setText(name_val, TextView.BufferType.NORMAL);

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
            String name_val;
            if (itm.getName().contains(":"))
            {
                name_val   = itm.getName().substring(0, itm.getName().lastIndexOf(':'));
            }
            else
            {
                name_val = itm.getName();
            }
            Log.i(TAG, "Template list name=" + name_val);
            name_value.setText(name_val, TextView.BufferType.NORMAL);

        }
        name_value.setHeight(txtHeight);
        name_value.setWidth(width);
        name_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        return vw;
    }

    public View getEasyTemplRow(ViewGroup parent, int txtHeight, int width, String txt)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.easy_templ_row, parent,false);
        TextView label = (TextView) vw.findViewById(R.id.name);
        label.setText(txt);
        label.setHeight(txtHeight);
        label.setWidth(width);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.35f);
        return vw;
    }

    private View getNameRowView(ViewGroup parent, int txtHeight, int width, final Item itm)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label_text, parent,false);
        TextView name = (TextView) vw.findViewById(R.id.name);
        name.setText("Name:");
        name.setHeight(txtHeight);
        //name.setWidth(width/4);
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        final EditText name_value = (EditText) vw.findViewById(R.id.value);
        String app_name = DBOperations.getInstance().getApp_name();
        if (app_name.equals(EASYGROC)) {
            if (itm.getName() != null && itm.getName().length() > 0) {
                String name_val;
                if (itm.getName().contains(":")) {
                    name_val = itm.getName().substring(0, itm.getName().lastIndexOf(':'));
                } else {
                    name_val = itm.getName();
                }
                Log.i(TAG, "Template list name=" + name_val);

                name_value.setText(name_val, TextView.BufferType.NORMAL);


            }

        }
        else {

           name_value.setText(itm.getName(), TextView.BufferType.EDITABLE); /*// May be unecessary... */
          //  name_value.getText().clear();

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
        name_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
        return vw;
    }

    private View getLabelSwitchInventoryView(ViewGroup parent, int txtHeight, int width, final Item itm, final int vwType)
    {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vw = inflater.inflate(R.layout.label_switch_inv, parent, false);
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
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.35f);
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
                        Log.d(TAG, "Toggled inventory in " + itm.getItem() + " inv=" + itm.getInventory() + " isChecked=" + isChecked );
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
        Log.d(TAG, "getLabelSwitchView text=" + itm.getItem() + " rowno="+ itm.getRowno());
        label.setHeight(txtHeight);
        label.setWidth((width / 10) * 8);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.35f);
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
                            Log.d(TAG, "Toggled tag to false in " + itm.getItem() + " rowno=" + itm.getRowno());
                        }
                        else
                        {
                            itm.setHidden(true);
                            adapter.undoPush(itm.getRowno());
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Toggled tag to true in " + itm.getItem() + " rowno=" + itm.getRowno());
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
        View vw = inflater.inflate(R.layout.label_templ, parent, false);
        TextView label = (TextView) vw.findViewById(R.id.name);
        label.setText(labelTxt);
        label.setHeight(txtHeight);
        label.setWidth((width / 10) * 8);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.4f);
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
           // label.setCheckMarkDrawable(0);
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
                    label.setCheckMarkDrawable(R.drawable.ic_check_box_outline_blank_black_24dp);
                    label.setChecked(false);
                    itm.setSelected(false);
                } else {
                    label.setCheckMarkDrawable(R.drawable.ic_check_box_black_24dp);
                    label.setChecked(true);
                    itm.setSelected(true);
                }
            }
        });
        if (itm.isSelected())
        {
            label.setCheckMarkDrawable(R.drawable.ic_check_box_black_24dp);
            label.setChecked(true);
        }
        else
        {
            label.setCheckMarkDrawable(R.drawable.ic_check_box_outline_blank_black_24dp);
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
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.4f);

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
