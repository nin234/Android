package com.rekhaninan.common;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import static com.rekhaninan.common.Constants.*;

public class SortActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int VIEWED_SPINNER_ID=0;

    private static final int YEAR_SPINNER_ID=1;

    private static final int PRICE_SPINNER_ID=2;

    private static final int RATINGS_SPINNER_ID=3;

    private static final int BEDS_SPINNER_ID=4;

    private static final int BATHS_SPINNER_ID=5;

    private static final int AREA_SPINNER_ID=6;

    private static final int MODEL_SPINNER_ID=7;

    private static final int MAKE_SPINNER_ID=8;

    private static final int COLOR_SPINNER_ID=9;


    private String app_name;
    private final String TAG = "ShareActivity";
    private int viewType;
    private String orderBy;

    private CheckBox viewedCbx;
    private Spinner viewedSpnr;
    private String orderByViewed;

    private CheckBox yearCbx;
    private Spinner yearSpnr;
    private String orderByYear;

    private CheckBox priceCbx;
    private Spinner priceSpnr;
    private String orderByPrice;

    private CheckBox ratingsCbx;
    private Spinner ratingsSpnr;
    private String orderByRatings;

    private CheckBox modelCbx;
    private Spinner modelSpnr;
    private String orderByModel;

    private CheckBox makeCbx;
    private Spinner makeSpnr;
    private String orderByMake;

    private CheckBox colorCbx;
    private Spinner colorSpnr;
    private String orderByColor;


    private CheckBox bedsCbx;
    private Spinner bedsSpnr;
    private String orderByBeds;

    private CheckBox bathsCbx;
    private Spinner bathsSpnr;
    private String orderByBaths;

    private CheckBox areaCbx;
    private Spinner areaSpnr;
    private String orderByArea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        orderBy = "album_name ASC";
        Intent intent = getIntent();
        app_name = intent.getStringExtra("app_name");
        viewType = intent.getIntExtra("ViewType", 0);
        DBOperations.getInstance().setSortStr(orderBy);

        switch (app_name)
        {
            case OPENHOUSES:
                setContentView(R.layout.sort_options);
                setOpenHousesCbxAndSpinners();
                break;

            case AUTOSPREE:
                setContentView(R.layout.sort_options_aspree);
                setAutoSpreeCbxAndSpinners();
                break;

            default:

                break;

        }
    }

    private void setOpenHousesCbxAndSpinners()
    {
       setCommonCbxAndSpinners();
        bedsSpnr = (Spinner) findViewById(R.id.beds);
        ArrayAdapter<CharSequence> bedsadapter = ArrayAdapter.createFromResource(this,
                R.array.beds, android.R.layout.simple_spinner_item);

        bedsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bedsSpnr.setAdapter(bedsadapter);
        bedsSpnr.setOnItemSelectedListener(this);
        bedsSpnr.setSelection(0);
        bedsSpnr.setTag(BEDS_SPINNER_ID);
        orderByBeds = "beds ASC";

        bedsCbx = (CheckBox)findViewById(R.id.beds_check);
        bedsCbx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderBy = orderByBeds;
                resetChecked();
                bedsCbx.setChecked(true);
            }
        });

        bathsSpnr = (Spinner) findViewById(R.id.baths);
        ArrayAdapter<CharSequence> bathsadapter = ArrayAdapter.createFromResource(this,
                R.array.baths, android.R.layout.simple_spinner_item);

        bathsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bathsSpnr.setAdapter(bathsadapter);
        bathsSpnr.setOnItemSelectedListener(this);
        bathsSpnr.setSelection(0);
        bathsSpnr.setTag(BATHS_SPINNER_ID);
        orderByBaths = "baths ASC";


        bathsCbx = (CheckBox)findViewById(R.id.baths_check);
        bathsCbx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderBy = orderByBaths;
                resetChecked();
                bathsCbx.setChecked(true);
            }
        });

        areaSpnr = (Spinner) findViewById(R.id.area);
        ArrayAdapter<CharSequence> areaadapter = ArrayAdapter.createFromResource(this,
                R.array.area, android.R.layout.simple_spinner_item);

        areaadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpnr.setAdapter(areaadapter);
        areaSpnr.setOnItemSelectedListener(this);
        areaSpnr.setSelection(0);
        areaSpnr.setTag(AREA_SPINNER_ID);
        orderByArea = "area ASC";

        areaCbx = (CheckBox)findViewById(R.id.area_check);
        areaCbx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderBy = orderByArea;
                resetChecked();
                areaCbx.setChecked(true);
            }
        });

    }

    private void resetChecked()
    {
        switch (app_name)
        {
            case AUTOSPREE:
            {
                viewedCbx.setChecked(false);
                yearCbx.setChecked(false);
                priceCbx.setChecked(false);
                ratingsCbx.setChecked(false);
                modelCbx.setChecked(false);
                makeCbx.setChecked(false);
                colorCbx.setChecked(false);
            }
            break;

            case OPENHOUSES:
            {
                viewedCbx.setChecked(false);
                yearCbx.setChecked(false);
                priceCbx.setChecked(false);
                ratingsCbx.setChecked(false);
                areaCbx.setChecked(false);
                bedsCbx.setChecked(false);
                bathsCbx.setChecked(false);
            }
            break;

            default:

                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        if (item.getItemId() == android.R.id.home)
        {
            DBOperations.getInstance().setSortStr(orderBy);
            finish();
        }

        return true;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        Spinner spinner = (Spinner) parent;
        Integer tag = (Integer) spinner.getTag();
        int tagValue = tag.intValue();
        switch (tagValue)
        {
            case VIEWED_SPINNER_ID:
            {
                if (pos == 0)
                {
                    orderByViewed = "album_name ASC";
                }
                else
                {
                    orderByViewed = "album_name DESC";
                }
            }
            break;

            case PRICE_SPINNER_ID:
        {
            if (pos == 0) {
                orderByPrice = "price DESC";
            }
            else
            {
                orderByPrice = "price ASC";
            }
        }
        break;

            case RATINGS_SPINNER_ID:
            {
                if (pos == 0) {
                    orderByRatings = "ratings DESC";
                }
                else
                {
                    orderByRatings = "ratings ASC";
                }
            }
            break;

            case YEAR_SPINNER_ID:
            {
                if (pos == 0) {
                    orderByYear = "year DESC";
                }
                else
                {
                    orderByYear = "year ASC";
                }
            }
            break;

            case BEDS_SPINNER_ID:
            {
                if (pos == 0) {
                    orderByBeds = "beds DESC";
                }
                else
                {
                    orderByBeds = "beds ASC";
                }
            }
            break;

            case BATHS_SPINNER_ID:
            {
                if (pos == 0) {
                    orderByBaths = "baths DESC";
                }
                else
                {
                    orderByBaths = "baths ASC";
                }
            }
            break;

            case AREA_SPINNER_ID:
            {
                if (pos == 0) {
                    orderByArea = "area DESC";
                }
                else
                {
                    orderByArea = "area ASC";
                }
            }
            break;

            case MAKE_SPINNER_ID:
            {
                if (pos == 0) {
                    orderByMake = "make ASC";
                }
                else
                {
                    orderByMake = "make DESC";
                }
            }
            break;

            case MODEL_SPINNER_ID:
            {
                if (pos == 0) {
                    orderByModel = "model ASC";
                }
                else
                {
                    orderByModel = "model DESC";
                }
            }
            break;

            case COLOR_SPINNER_ID:
            {
                if (pos == 0) {
                    orderByColor = "color ASC";
                }
                else
                {
                    orderByColor = "color DESC";
                }
            }
            break;



            default:
                break;
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void setCommonCbxAndSpinners()
    {
        if (app_name.equals("AUTOSPREE")) {
            viewedSpnr = (Spinner) findViewById(R.id.viewed_aspree);
        }
        else {
            viewedSpnr = (Spinner) findViewById(R.id.viewed);
        }

        ArrayAdapter<CharSequence> viewedadapter = ArrayAdapter.createFromResource(this,
                R.array.viewed, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        viewedadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        viewedSpnr.setAdapter(viewedadapter);
        viewedSpnr.setOnItemSelectedListener(this);
        viewedSpnr.setSelection(0);
        viewedSpnr.setTag(VIEWED_SPINNER_ID);

        orderByViewed = "album_name ASC";

        viewedCbx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderBy = orderByViewed;
                resetChecked();
                viewedCbx.setChecked(true);
            }
        });

        if (app_name.equals("AUTOSPREE")) {
            yearSpnr = (Spinner) findViewById(R.id.year_aspree);
        }
        else {
            yearSpnr = (Spinner) findViewById(R.id.year);
        }
        ArrayAdapter<CharSequence> yearadapter = ArrayAdapter.createFromResource(this,
                R.array.year, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        yearadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        yearSpnr.setAdapter(yearadapter);
        yearSpnr.setOnItemSelectedListener(this);
        yearSpnr.setSelection(0);
        yearSpnr.setTag(YEAR_SPINNER_ID);
        orderByYear = "year ASC";

        yearCbx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderBy = orderByYear;
                resetChecked();
                yearCbx.setChecked(true);
            }
        });

        if (app_name.equals("AUTOSPREE")) {
            ratingsSpnr = (Spinner) findViewById(R.id.ratings_aspree);
            ratingsCbx = (CheckBox)findViewById(R.id.ratings_check_aspree);
            priceSpnr = (Spinner) findViewById(R.id.price_aspree);
            priceCbx = (CheckBox)findViewById(R.id.price_check_aspree);
        }
        else {
            ratingsSpnr = (Spinner) findViewById(R.id.ratings);
            priceSpnr = (Spinner) findViewById(R.id.price);
            ratingsCbx = (CheckBox)findViewById(R.id.ratings_check);
            priceCbx = (CheckBox)findViewById(R.id.price_check);
        }


        ArrayAdapter<CharSequence> priceadapter = ArrayAdapter.createFromResource(this,
                R.array.price, android.R.layout.simple_spinner_item);

        priceadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpnr.setAdapter(priceadapter);
        priceSpnr.setOnItemSelectedListener(this);
        priceSpnr.setSelection(0);
        priceSpnr.setTag(PRICE_SPINNER_ID);

        orderByPrice = "price ASC";

        priceCbx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderBy = orderByPrice;
                resetChecked();
                priceCbx.setChecked(true);
            }
        });


        ArrayAdapter<CharSequence> ratingsadapter = ArrayAdapter.createFromResource(this,
                R.array.ratings, android.R.layout.simple_spinner_item);

        ratingsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingsSpnr.setAdapter(ratingsadapter);
        ratingsSpnr.setOnItemSelectedListener(this);
        ratingsSpnr.setSelection(0);
        ratingsSpnr.setTag(RATINGS_SPINNER_ID);

        orderByRatings = "ratings ASC";

        ratingsCbx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderBy = orderByRatings;
                resetChecked();
                ratingsCbx.setChecked(true);
            }
        });

    }

    private void setAutoSpreeCbxAndSpinners()
    {

        setCommonCbxAndSpinners();
        modelSpnr = (Spinner) findViewById(R.id.model);
        ArrayAdapter<CharSequence> modeladapter = ArrayAdapter.createFromResource(this,
                R.array.model, android.R.layout.simple_spinner_item);

        modeladapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpnr.setAdapter(modeladapter);
        modelSpnr.setOnItemSelectedListener(this);
        modelSpnr.setSelection(0);
        modelSpnr.setTag(MODEL_SPINNER_ID);

        orderByModel = "model ASC";


        modelCbx = (CheckBox)findViewById(R.id.model_check);
        modelCbx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderBy = orderByModel;
                resetChecked();
                modelCbx.setChecked(true);
            }
        });

        makeSpnr = (Spinner) findViewById(R.id.make);
        ArrayAdapter<CharSequence> makeAdapter = ArrayAdapter.createFromResource(this,
                R.array.make, android.R.layout.simple_spinner_item);

        makeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        makeSpnr.setAdapter(makeAdapter);
        makeSpnr.setOnItemSelectedListener(this);
        makeSpnr.setSelection(0);
        makeSpnr.setTag(MAKE_SPINNER_ID);

        orderByMake = "make ASC";


        makeCbx = (CheckBox)findViewById(R.id.make_check);
        makeCbx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderBy = orderByMake;
                resetChecked();
                makeCbx.setChecked(true);
            }
        });

        colorSpnr = (Spinner) findViewById(R.id.color);
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.color, android.R.layout.simple_spinner_item);

        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpnr.setAdapter(colorAdapter);
        colorSpnr.setOnItemSelectedListener(this);
        colorSpnr.setSelection(0);
        colorSpnr.setTag(COLOR_SPINNER_ID);

        orderByColor = "color ASC";


        colorCbx = (CheckBox)findViewById(R.id.color_check);
        colorCbx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderBy = orderByColor;
                resetChecked();
                colorCbx.setChecked(true);
            }
        });


    }
}
