package com.rekhaninan.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.rekhaninan.common.Constants.*;


import java.util.ArrayList;

import static com.rekhaninan.common.Constants.AUTOSPREE_ADD_ITEM;
import static com.rekhaninan.common.Constants.AUTOSPREE_MAKE_ROW;
import static com.rekhaninan.common.Constants.AUTOSPREE_MODEL_ROW;
import static com.rekhaninan.common.Constants.AUTOSPREE_NAME_ROW;
import static com.rekhaninan.common.Constants.AUTOSPREE_PRICE_ROW;
import static com.rekhaninan.common.Constants.MAINVW;

/**
 * Created by nin234 on 8/28/16.
 */
public class AutoSpreeRow extends RowView
{
    private EditText name_value;
    private EditText model_value;
    private EditText color_value;
    private EditText make_value;
    private EditText year_value;
    private EditText price_value;
    private EditText miles_value;
    private View name_row, model_row, price_row, make_row;

    private final String TAG = "AutoSpreeRow";

    AutoSpreeRow()
    {
        setRatings(1);
    }

    @Override
    public  String getItemName(Item itm)
    {
        String carname = itm.getName() + " - ";
        if (itm.getColor() != null)
            carname += itm.getColor() + " " ;
        if (itm.getColor() != null)
            carname += itm.getModel();
        return carname;
    }

    private void displayAutoSpreeItem(View view, String carname)
    {
        Item itm = (Item)view.getTag();
        Log.d(getClass().getName(), "Clicked row " + carname);
        Intent intent = new Intent(ctxt, SingleItemActivity.class);
        intent.putExtra("ViewType", AUTOSPREE_DISPLAY_ITEM);
        intent.putExtra("item", itm);
        Log.i(TAG, "Getting list for item name=" + itm.getName() + " share_id=" + itm.getShare_id());
        java.util.List<Item> list = DBOperations.getInstance().getList(itm.getName(), itm.getShare_id());
        if (list == null) {
            Log.i(TAG, "Null check list retrieved for item name=" + itm.getName() + " share_id=" + itm.getShare_id());
            list = new ArrayList<Item>();
        }
        intent.putParcelableArrayListExtra("check_list", (ArrayList<Item>)list);
        Activity itemAct = (Activity) ctxt;
        itemAct.startActivityForResult(intent, AUTOSPREE_DISPLAY_ITEM_REQUEST);
    }

    @Override
    public View getView(final Item itm, int position, ViewGroup parent)
    {
        Log.d(getClass().getName(), "In AutoSpreeRow getView position=" + position);
        View svw = super.getView(itm, position, parent);
        if (svw != null)
        {
            Log.d(TAG, "svw not null");
            return svw;
        }
        WindowManager wm = (WindowManager) ctxt.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int txtHeight = height / 12;


        switch (vwType)
        {

            case MAINVW: {

                LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View vw = inflater.inflate(R.layout.label, parent, false);
                TextView tv = (TextView) vw.findViewById(R.id.name);



                String carname = itm.getName() + " - ";
                if (itm.getColor() != null)
                    carname += itm.getColor() + " " ;
                if (itm.getColor() != null)
                     carname += itm.getModel();
                Log.d(TAG, "In AutoSpreeRow setting car name=" + carname);
                tv.setText(carname);
                tv.setTag(itm);

                final String cname = carname;
                tv.setHeight(txtHeight);
                tv.setWidth((width / 10) * 8);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.35f);
                //tv.setText(Item, TextView.BufferType.EDITABLE);

                ImageView disclosure = (ImageView) vw.findViewById(R.id.label_image_icon);
                disclosure.setMaxHeight(txtHeight);
                disclosure.setMaxWidth(width / 10);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                lp.setMargins((width / 10) * 8, 5, 5, 5);
                disclosure.setLayoutParams(lp);
                disclosure.setTag(itm);
                tv.setOnClickListener(view -> displayAutoSpreeItem(view, cname)
                );
                disclosure.setOnClickListener(view -> displayAutoSpreeItem(view, cname)
                );
                return vw;
            }


            case AUTOSPREE_DISPLAY_ITEM:
            case AUTOSPREE_ADD_ITEM:
            case AUTOSPREE_EDIT_ITEM:
            {
                Log.d(getClass().getName(), "In AutoSpreeRow ADD ITEM position=" +position);
                switch (position)
                {
                    case AUTOSPREE_NAME_ROW: {
                        if (name_row != null)
                            return name_row;
                        Log.d(getClass().getName(), "In AutoSpreeRow Name Row " + itm.getName() + " position=" +position);
                        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View vw = inflater.inflate(R.layout.label_text, parent,false);
                        TextView name = (TextView) vw.findViewById(R.id.name);
                        name.setTypeface(name.getTypeface(), Typeface.BOLD);
                        name.setText("Name");
                        name.setHeight(txtHeight);
                        name.setWidth(width/4);
                        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);
                        name_value = (EditText) vw.findViewById(R.id.value);
                        if (vwType == AUTOSPREE_ADD_ITEM || vwType == AUTOSPREE_EDIT_ITEM) {
                            name_value.setText(itm.getName(), TextView.BufferType.EDITABLE);
                        } else if (vwType == AUTOSPREE_DISPLAY_ITEM){
                            name_value.setText(itm.getName());
                            name_value.setKeyListener(null);
                        }
                        name_value.setHeight(txtHeight);
                        name_value.setWidth((width/4)*3);
                        name_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
                        name_row =vw;
                        return name_row;
                    }

                    case AUTOSPREE_MODEL_ROW: {
                        if (model_row != null)
                            return model_row;

                        Log.d(getClass().getName(), "In AutoSpreeRow Name Row " + itm.getName() + " position=" +position);
                        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View vw = inflater.inflate(R.layout.tarun, parent,false);
                       TextView name = (TextView) vw.findViewById(R.id.name_tarun);
                        name.setText("Model");
                        name.setTypeface(name.getTypeface(), Typeface.BOLD);
                        name.setHeight(txtHeight);
                        name.setWidth(width/7);
                        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);

                        model_value = (EditText) vw.findViewById(R.id.value_tarun);
                        model_value.setHeight(txtHeight);
                        model_value.setWidth(width/3);
                        model_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f );

                        TextView name1 = (TextView) vw.findViewById(R.id.name);
                        name1.setHeight(txtHeight);
                        name1.setWidth(width/7);
                        name1.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);

                        name1.setText("Color");
                        name1.setTypeface(name1.getTypeface(), Typeface.BOLD);
                        color_value = (EditText) vw.findViewById(R.id.value);
                        color_value.setHeight(txtHeight);
                        color_value.setWidth(width/3);
                        color_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
                        if (vwType == AUTOSPREE_DISPLAY_ITEM){
                            model_value.setText(itm.getModel());
                            color_value.setText(itm.getColor());
                            model_value.setKeyListener(null);
                            color_value.setKeyListener(null);
                        } else if (vwType == AUTOSPREE_EDIT_ITEM)
                        {
                            model_value.setText(itm.getModel(), TextView.BufferType.EDITABLE);
                            color_value.setText(itm.getColor(), TextView.BufferType.EDITABLE);
                        }
                        model_row = vw;
                        return model_row;
                    }

                    case AUTOSPREE_MAKE_ROW:
                    {
                        if (make_row != null)
                            return make_row;
                        Log.d(getClass().getName(), "In AutoSpreeRow Name Row " + itm.getName() + " position=" +position);
                        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View vw = inflater.inflate(R.layout.tarun, parent,false);
                        TextView name = (TextView) vw.findViewById(R.id.name_tarun);
                        name.setText("Make");
                        name.setHeight(txtHeight);
                        name.setWidth(width/7);
                        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);
                        name.setTypeface(name.getTypeface(), Typeface.BOLD);
                        TextView name1 = (TextView) vw.findViewById(R.id.name);
                        name1.setHeight(txtHeight);
                        name1.setWidth(width/7);
                        name1.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);
                        name1.setTypeface(name1.getTypeface(), Typeface.BOLD);

                        name1.setText("Year");
                        make_value = (EditText) vw.findViewById(R.id.value_tarun);
                        make_value.setHeight(txtHeight);
                        make_value.setWidth(width/3);
                        make_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
                        year_value= (EditText) vw.findViewById(R.id.value);
                        year_value.setHeight(txtHeight);
                        year_value.setWidth(width/3);
                        year_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
                        if (vwType == AUTOSPREE_DISPLAY_ITEM){
                            make_value.setText(itm.getMake());
                            make_value.setKeyListener(null);
                            year_value.setText(Integer.toString(itm.getYear()));
                            year_value.setKeyListener(null);
                        } else if (vwType == AUTOSPREE_EDIT_ITEM)
                        {
                            make_value.setText(itm.getMake(), TextView.BufferType.EDITABLE);
                            year_value.setText(Integer.toString(itm.getYear()), TextView.BufferType.EDITABLE);
                        }
                        make_row =vw;
                        return make_row;
                    }

                    case AUTOSPREE_PRICE_ROW:
                    {
                        if (price_row != null)
                            return price_row;
                        Log.d(getClass().getName(), "In AutoSpreeRow Name Row " + itm.getName() + " position=" +position);
                        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View vw = inflater.inflate(R.layout.tarun, parent,false);
                        TextView name = (TextView) vw.findViewById(R.id.name_tarun);
                        name.setText("Price");
                        TextView name1 = (TextView) vw.findViewById(R.id.name);
                        name1.setText("Miles");
                        name.setHeight(txtHeight);
                        name.setWidth(width/7);
                        name.setTypeface(name.getTypeface(), Typeface.BOLD);
                        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);
                        name1.setTypeface(name1.getTypeface(), Typeface.BOLD);
                        name1.setHeight(txtHeight);
                        name1.setWidth(width/7);
                        name1.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.27f);
                        price_value = (EditText) vw.findViewById(R.id.value_tarun);
                        miles_value = (EditText) vw.findViewById(R.id.value);
                        price_value.setHeight(txtHeight);
                        price_value.setWidth(width/3);
                        price_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
                        miles_value.setHeight(txtHeight);
                        miles_value.setWidth(width/3);
                        miles_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight*0.32f);
                        if (vwType == AUTOSPREE_DISPLAY_ITEM){
                            price_value.setText(Double.toString(itm.getPrice()));
                            miles_value.setText(Integer.toString(itm.getMiles()));
                            price_value.setKeyListener(null);
                            miles_value.setKeyListener(null);
                        } else if (vwType == AUTOSPREE_EDIT_ITEM)
                        {
                            price_value.setText(Double.toString(itm.getPrice()), TextView.BufferType.EDITABLE);
                            miles_value.setText(Integer.toString(itm.getMiles()), TextView.BufferType.EDITABLE);
                        }
                        price_row = vw;
                        return price_row;
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



    public  void getKeyVals(Item itm)
    {


            itm.setName(name_value.getText().toString());

            itm.setModel(model_value.getText().toString());

            itm.setColor(color_value.getText().toString());

            itm.setMake(make_value.getText().toString());

            String year = year_value.getText().toString();
            String price = price_value.getText().toString();
            String miles = miles_value.getText().toString();

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
            itm.setMiles(Integer.parseInt(miles));
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
