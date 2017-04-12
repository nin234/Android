package com.rekhaninan.common;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

import static com.rekhaninan.common.Constants.EASYGROC;


public class ImageSwipeActivity extends AppCompatActivity {

    private int position;
    private ArrayList<String> images;
    private final  String TAG = "ImageSwipeActivity";
    private VideoView videoView;
    private ImageView imageView;
    private MediaController mc;
    private String app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_swipe);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String title = "Swipe for next";
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        images = intent.getStringArrayListExtra("images");
        app = intent.getStringExtra("App");
        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(title);
        imageView = (ImageView) findViewById(R.id.image);
        videoView = (VideoView) findViewById(R.id.video);
        mc = new MediaController(this);
        mc.setAnchorView(videoView);
        mc.setMediaPlayer(videoView);
        videoView.setMediaController(mc);


        changeImage();

        videoView.setOnTouchListener(new OnSwipeTouchListener(ImageSwipeActivity.this) {
            public void onSwipeTop() {

            }
            public void onSwipeLeft()
            {
                if (position+1 >= images.size())
                {
                    Log.d(TAG, "In last image left swipe ignored position=" + position);
                    return;
                }
                ++position;
                changeImage();



            }
            public void onSwipeRight()
            {
                if (position-1 < 0)
                {
                    Log.d(TAG, "In first image right swipe ignored position=" + position);
                    return;
                }
                --position;
                changeImage();
            }

            public void onSwipeBottom() {

            }

        });

        imageView.setOnTouchListener(new OnSwipeTouchListener(ImageSwipeActivity.this) {
            public void onSwipeTop() {

            }
            public void onSwipeLeft()
            {
                if (position+1 >= images.size())
                {
                    Log.d(TAG, "In last image left swipe ignored position=" + position);
                    return;
                }
                ++position;
                changeImage();



            }
            public void onSwipeRight()
            {
                if (position-1 < 0)
                {
                    Log.d(TAG, "In first image right swipe ignored position=" + position);
                    return;
                }
                --position;
                changeImage();
            }

            public void onSwipeBottom() {

            }

        });
    }

        private void changeImage()
        {

            String filePath = images.get(position);
            if (filePath.endsWith("jpg"))
            {
                Bitmap bmp = BitmapFactory.decodeFile(filePath);
                videoView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bmp);

            }
            else if (filePath.endsWith("mp4"))
            {
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                videoView.setVideoPath(filePath);
                videoView.start();
            }
            return;
        }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        if (item.getItemId() == android.R.id.home)
        {
            Log.d(getClass().getName(), "Back button pressed");
            if (app == EASYGROC)
            {
                Intent intent = new Intent();
                intent.putExtra("App", EASYGROC);
                setResult(RESULT_OK, intent);
            }
            finish();
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
