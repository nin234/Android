package com.rekhaninan.openhouses;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.rekhaninan.common.MainVwActivity;

public class MainActivity extends AppCompatActivity {

    public final static String APP_NAME = "APP_NAME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainVwActivity.class);
        String app_name = "OpenHouses";
        intent.putExtra(APP_NAME, app_name);

        startActivity(intent);
    }
}
