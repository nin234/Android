package com.rekhaninan.common;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


public class NotesActivity extends AppCompatActivity {

    private boolean display;
    private EditText notes_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        boolean bDisplay = intent.getBooleanExtra("display", false);
        String notes = intent.getStringExtra("notes");
        notes_value = (EditText)  findViewById(R.id.item_notes);
        if (bDisplay)
        {
            notes_value.setText(notes);
            notes_value.setKeyListener(null);
        }
        else
        {
            notes_value.setText(notes, TextView.BufferType.EDITABLE);

        }
       return;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        if (item.getItemId() == android.R.id.home)
        {
            Log.d(getClass().getName(), "Back button pressed");
            Intent intent = new Intent();
            intent.putExtra("notes_value", notes_value.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
