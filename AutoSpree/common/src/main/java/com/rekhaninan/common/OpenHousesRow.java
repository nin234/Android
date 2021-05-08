package com.rekhaninan.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import static com.rekhaninan.common.Constants.*;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by nin234 on 8/27/16.
 */

public class OpenHousesRow extends RowView {
    private EditText name_value;
    private EditText area_value;
    private EditText beds_value;
    private EditText baths_value;
    private EditText year_value;
    private EditText price_value;
    private View name_row, area_row, price_row, beds_row;
    private View pictures_row, notes_row;
    private final String TAG = "OpenHousesRow";

    @Override
    public  String getItemName(Item itm)
    {
        String housename = itm.getName() + " - " ;
        if (itm.getStreet() != null)
            housename += itm.getStreet();
        return housename;
    }

    OpenHousesRow()
    {
        setRatings(1);
    }

    private void displayItem(View view, String housename)
    {
        if (checkEntitlement() == false)
        {
            return;
        }
        Item itm = (Item) view.getTag();
        Log.d(getClass().getName(), "Clicked row " + housename);
        Intent intent = new Intent(ctxt, SingleItemActivity.class);
        intent.putExtra("ViewType", OPENHOUSES_DISPLAY_ITEM);
        intent.putExtra("item", itm);
        java.util.List<Item> list = DBOperations.getInstance().getList(itm.getName(), itm.getShare_id());
        if (list == null)
            list = new ArrayList<Item>();
        intent.putParcelableArrayListExtra("check_list", (ArrayList<Item>)list);
        Activity itemAct = (Activity) ctxt;
        itemAct.startActivityForResult(intent, OPENHOUSES_DISPLAY_ITEM_REQUEST);
    }

    public  View getView(final Item itm, int position, ViewGroup parent)
    {
        View svw = super.getView(itm, position, parent);
        if (svw != null)
        {
            return svw;
        }

        WindowManager wm = (WindowManager) ctxt.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int txtHeight = height / 12;
        Log.d(getClass().getName(), "In OpenHouses getView position=" + position);

        switch (vwType)
        {

            case MAINVW: {
                LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View vw = inflater.inflate(R.layout.label, parent, false);
                TextView tv = (TextView) vw.findViewById(R.id.name);


                String housename = itm.getName() + " - " ;
                if (itm.getStreet() != null)
                 housename += itm.getStreet();
                Log.d(getClass().getName(), "In OpenHouses setting house name=" + housename);
                tv.setText(housename);
                tv.setTag(itm);
                //tv.setText(Item, TextView.BufferType.EDITABLE);
                tv.setHeight(txtHeight);
                tv.setWidth((width / 10) * 8);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.35f);
                final String hname = housename;
                ImageView disclosure = (ImageView) vw.findViewById(R.id.label_image_icon);
                disclosure.setMaxHeight(txtHeight);
                disclosure.setMaxWidth(width / 10);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                lp.setMargins((width / 10) * 8, 5, 5, 5);
                disclosure.setLayoutParams(lp);
                disclosure.setTag(itm);
                tv.setOnClickListener(view -> displayItem(view, hname)
                );
                disclosure.setOnClickListener(view -> displayItem(view, hname)
                );
                return vw;
            }



            case OPENHOUSES_ADD_ITEM:
            case OPENHOUSES_DISPLAY_ITEM:
            case OPENHOUSES_EDIT_ITEM:
            {
                Log.d(getClass().getName(), "In OpenHousesRow ADD ITEM position=" +position);
                switch (position)
                {
                    case OPENHOUSES_NAME_ROW: {
                        if (name_row != null)
                            return name_row;
                        Log.d(getClass().getName(), "In OpenHouses Name Row " + itm.getName() + " position=" +position);
                        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View vw = inflater.inflate(R.layout.label_text, parent,false);
                        TextView name = (TextView) vw.findViewById(R.id.name);
                        name.setText("Name");
                        name.setTypeface(name.getTypeface(), Typeface.BOLD);
                        name.setHeight(txtHeight);
                        name.setWidth(width/4);
                        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);
                        name_value = (EditText) vw.findViewById(R.id.value);
                        if (vwType == OPENHOUSES_ADD_ITEM || vwType == OPENHOUSES_EDIT_ITEM) {
                            name_value.setText(itm.getName(), TextView.BufferType.EDITABLE);
                        } else if (vwType == OPENHOUSES_DISPLAY_ITEM){
                            name_value.setText(itm.getName());
                            name_value.setKeyListener(null);
                        }
                        name_value.setHeight(txtHeight);
                        name_value.setWidth((width/4)*3);
                        name_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
                        name_row =vw;
                        return name_row;
                    }

                    case OPENHOUSES_PRICE_ROW: {
                        if (price_row != null)
                            return price_row;

                        Log.d(getClass().getName(), "In AutoSpreeRow Price Row " + itm.getName() + " position=" +position);
                        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View vw = inflater.inflate(R.layout.label_text, parent,false);
                        TextView name = (TextView) vw.findViewById(R.id.name);
                        name.setText("Price");
                        name.setHeight(txtHeight);
                        name.setWidth(width/4);
                        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);
                        name.setTypeface(name.getTypeface(), Typeface.BOLD);
                        price_value = (EditText) vw.findViewById(R.id.value);
                        price_value.setHeight(txtHeight);
                        price_value.setWidth((width/4)*3);
                        price_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);


                        if (vwType == OPENHOUSES_DISPLAY_ITEM){
                            price_value.setText(Double.toString(itm.getPrice()));
                            price_value.setKeyListener(null);
                        } else if (vwType == OPENHOUSES_EDIT_ITEM)
                        {
                            price_value.setText(Double.toString(itm.getPrice()), TextView.BufferType.EDITABLE);

                        }
                        price_row = vw;
                        return price_row;
                    }

                    case OPENHOUSES_AREA_ROW:
                    {
                        if (area_row != null)
                            return area_row;
                        Log.d(getClass().getName(), "In OpenHouses Area Row " + itm.getName() + " position=" +position);
                        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View vw = inflater.inflate(R.layout.tarun, parent,false);
                        TextView name = (TextView) vw.findViewById(R.id.name_tarun);
                        name.setText("Area");
                        name.setHeight(txtHeight);
                        name.setTypeface(name.getTypeface(), Typeface.BOLD);
                        name.setWidth(width/7);
                        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);
                        TextView name1 = (TextView) vw.findViewById(R.id.name);
                        name1.setHeight(txtHeight);
                        name1.setWidth(width/7);
                        name1.setTypeface(name.getTypeface(), Typeface.BOLD);
                        name1.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);

                        name1.setText("Year");
                        area_value = (EditText) vw.findViewById(R.id.value_tarun);
                        area_value.setHeight(txtHeight);
                        area_value.setWidth(width/3);
                        area_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
                        year_value= (EditText) vw.findViewById(R.id.value);
                        year_value.setHeight(txtHeight);
                        year_value.setWidth(width/3);
                        year_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
                        if (vwType == OPENHOUSES_DISPLAY_ITEM){
                            area_value.setText(Double.toString(itm.getArea()));
                            area_value.setKeyListener(null);
                            year_value.setText(Integer.toString(itm.getYear()));
                            year_value.setKeyListener(null);
                        } else if (vwType == OPENHOUSES_EDIT_ITEM)
                        {
                            area_value.setText(Double.toString(itm.getArea()), TextView.BufferType.EDITABLE);
                            year_value.setText(Integer.toString(itm.getYear()), TextView.BufferType.EDITABLE);
                        }
                        area_row =vw;
                        return area_row;
                    }

                    case OPENHOUSES_BEDS_ROW:
                    {
                        if (beds_row != null)
                            return beds_row;
                        Log.d(getClass().getName(), "In OpenHouses Beds Row " + itm.getName() + " position=" +position);
                        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View vw = inflater.inflate(R.layout.tarun, parent,false);
                        TextView name = (TextView) vw.findViewById(R.id.name_tarun);
                        name.setText("Beds");
                        TextView name1 = (TextView) vw.findViewById(R.id.name);
                        name1.setText("Baths");
                        name.setHeight(txtHeight);
                        name.setWidth(width/7);
                        name.setTypeface(name.getTypeface(), Typeface.BOLD);
                        name1.setTypeface(name.getTypeface(), Typeface.BOLD);
                        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);
                        name1.setHeight(txtHeight);
                        name1.setWidth(width/7);
                        name1.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);
                        beds_value = (EditText) vw.findViewById(R.id.value_tarun);
                        baths_value = (EditText) vw.findViewById(R.id.value);
                        beds_value.setHeight(txtHeight);
                        beds_value.setWidth(width/3);
                        beds_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
                        baths_value.setHeight(txtHeight);
                        baths_value.setWidth(width/3);
                        baths_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
                        if (vwType == OPENHOUSES_DISPLAY_ITEM){
                            beds_value.setText(Double.toString(itm.getBeds()));
                            baths_value.setText(Double.toString(itm.getBaths()));
                            beds_value.setKeyListener(null);
                            baths_value.setKeyListener(null);
                        } else if (vwType == OPENHOUSES_EDIT_ITEM)
                        {
                            beds_value.setText(Double.toString(itm.getBeds()), TextView.BufferType.EDITABLE);
                            baths_value.setText(Double.toString(itm.getBaths()), TextView.BufferType.EDITABLE);
                        }
                        beds_row = vw;
                        return beds_row;
                    }



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

    public void getKeyVals(Item itm)
    {


        itm.setName(name_value.getText().toString());

        String area = area_value.getText().toString();
        String beds = beds_value.getText().toString();
        String baths = baths_value.getText().toString();
        String year = year_value.getText().toString();
        String price = price_value.getText().toString();

        itm.setRating(getRatings());

        try
        {
            itm.setYear(Integer.parseInt(year));
        }
        catch (NumberFormatException e)
        {
            Log.i(TAG, "Caught NumberFormatException exception" + e.getMessage());
        }
        catch (Exception e)
        {
            Log.i(TAG, "Caught Exception exception" + e.getMessage());
        }

        try
        {
            itm.setPrice(Double.parseDouble(price));
        }
        catch (NumberFormatException e)
        {
            Log.i(TAG, "Caught NumberFormatException exception" + e.getMessage());
        }
        catch (Exception e)
        {
            Log.i(TAG, "Caught Exception exception" + e.getMessage());
        }

        try
        {
            itm.setArea(Double.parseDouble(area));
        }
        catch (NumberFormatException e)
        {
            Log.i(TAG, "Caught NumberFormatException exception" + e.getMessage());
        }
        catch (Exception e)
        {
            Log.i(TAG, "Caught Exception exception" + e.getMessage());
        }

        try
        {
            itm.setBeds(Double.parseDouble(beds));
        }
        catch (NumberFormatException e)
        {
            Log.i(TAG, "Caught NumberFormatException exception" + e.getMessage());
        }
        catch (Exception e)
        {
            Log.i(TAG, "Caught Exception exception" + e.getMessage());
        }

        try
        {
            itm.setBaths(Double.parseDouble(baths));
        }
        catch (NumberFormatException e)
        {
            Log.i(TAG, "Caught NumberFormatException exception" + e.getMessage());
        }
        catch (Exception e)
        {
            Log.i(TAG, "Caught Exception exception" + e.getMessage());
        }

        return;
    }
}
