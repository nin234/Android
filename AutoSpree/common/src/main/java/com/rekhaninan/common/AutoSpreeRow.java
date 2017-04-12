package com.rekhaninan.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout;
import static com.rekhaninan.common.Constants.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.Manifest;

import static com.rekhaninan.common.Constants.AUTOSPREE_ADD_ITEM;
import static com.rekhaninan.common.Constants.AUTOSPREE_MAKE_ROW;
import static com.rekhaninan.common.Constants.AUTOSPREE_MODEL_ROW;
import static com.rekhaninan.common.Constants.AUTOSPREE_NAME_ROW;
import static com.rekhaninan.common.Constants.AUTOSPREE_PRICE_ROW;
import static com.rekhaninan.common.Constants.MAINVW;
import static com.rekhaninan.common.Constants.OPENHOUSES_ADD_ITEM;
import static com.rekhaninan.common.Constants.OPENHOUSES_NAME_ROW;
import android.content.pm.PackageManager;

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
                View vw = inflater.inflate(R.layout.text, parent, false);
                TextView tv = (TextView) vw.findViewById(R.id.main_row_name);


                tv.setHeight(txtHeight);

                String carname = itm.getName() + " - ";
                if (itm.getColor() != null)
                    carname += itm.getColor() + " " ;
                if (itm.getColor() != null)
                     carname += itm.getModel();
                Log.d(TAG, "In AutoSpreeRow setting car name=" + carname);
                tv.setText(carname);
                tv.setTag(itm);

                //tv.setText(Item, TextView.BufferType.EDITABLE);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);

                tv.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {

                                              TextView tv = (TextView) view;
                                              Item itm = (Item) tv.getTag();
                                              Log.d(getClass().getName(), "Clicked row " + tv.getText());
                                              Intent intent = new Intent(ctxt, SingleItemActivity.class);
                                              intent.putExtra("ViewType", AUTOSPREE_DISPLAY_ITEM);
                                              intent.putExtra("item", itm);
                                              java.util.List<Item> list = DBOperations.getInstance().getList(itm.getName());
                                              if (list == null)
                                                  list = new ArrayList<Item>();
                                              intent.putParcelableArrayListExtra("check_list", (ArrayList<Item>)list);
                                              ctxt.startActivity(intent);
                                          }
                                      }

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
                        name.setText("Name:");
                        name.setHeight(txtHeight);
                        name.setWidth(width/4);
                        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
                        name_value = (EditText) vw.findViewById(R.id.value);
                        if (vwType == AUTOSPREE_ADD_ITEM || vwType == AUTOSPREE_EDIT_ITEM) {
                            name_value.setText(itm.getName(), TextView.BufferType.EDITABLE);
                        } else if (vwType == AUTOSPREE_DISPLAY_ITEM){
                            name_value.setText(itm.getName());
                            name_value.setKeyListener(null);
                        }
                        name_value.setHeight(txtHeight);
                        name_value.setWidth((width/4)*3);
                        name_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
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
                        name.setHeight(txtHeight);
                        name.setWidth(width/4);
                        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);

                        model_value = (EditText) vw.findViewById(R.id.value_tarun);
                        model_value.setHeight(txtHeight);
                        model_value.setWidth(width/4);
                        model_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);

                        TextView name1 = (TextView) vw.findViewById(R.id.name);
                        name1.setHeight(txtHeight);
                        name1.setWidth(width/4);
                        name1.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);

                        name1.setText("Color");
                        color_value = (EditText) vw.findViewById(R.id.value);
                        color_value.setHeight(txtHeight);
                        color_value.setWidth(width/4);
                        color_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
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
                        name.setWidth(width/4);
                        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
                        TextView name1 = (TextView) vw.findViewById(R.id.name);
                        name1.setHeight(txtHeight);
                        name1.setWidth(width/4);
                        name1.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);

                        name1.setText("Year");
                        make_value = (EditText) vw.findViewById(R.id.value_tarun);
                        make_value.setHeight(txtHeight);
                        make_value.setWidth(width/4);
                        make_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
                        year_value= (EditText) vw.findViewById(R.id.value);
                        year_value.setHeight(txtHeight);
                        year_value.setWidth(width/4);
                        year_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
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
                        name.setWidth(width/4);
                        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
                        name1.setHeight(txtHeight);
                        name1.setWidth(width/4);
                        name1.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
                        price_value = (EditText) vw.findViewById(R.id.value_tarun);
                        miles_value = (EditText) vw.findViewById(R.id.value);
                        price_value.setHeight(txtHeight);
                        price_value.setWidth(width/4);
                        price_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
                        miles_value.setHeight(txtHeight);
                        miles_value.setWidth(width/4);
                        miles_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtHeight / 2);
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
