package com.rekhaninan.common;

import java.io.File;
import java.util.ArrayList;
import android.content.Intent;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.PICTURE_VW;
import static com.rekhaninan.common.Constants.SHARE_PICTURE_VW;

public class PhotoRoll extends AppCompatActivity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private final String TAG="PhotoRoll";
    private ArrayList<String> images;
    private String album_name;
    private String app_name;
    private int viewType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_roll);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        album_name = intent.getStringExtra("album_name");
        app_name = intent.getStringExtra("app_name");
        viewType = intent.getIntExtra("ViewType", 0);
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.photo_roll_item, getData());
        gridAdapter.setParams(viewType, app_name);
        gridView.setAdapter(gridAdapter);

        if (viewType == PICTURE_VW)
        {
            gridView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


                    //Create intent
                    if (position < 0 || position >= images.size()) {
                        Log.e(TAG, "Invalid grid position " + position + " images array size=" + images.size());
                        return;
                    }
                    Intent intent = new Intent(PhotoRoll.this, ImageSwipeActivity.class);
                    intent.putExtra("images", images);
                    intent.putExtra("position", position);
                    //as long as app is not Easygroc it is fine as of now
                    intent.putExtra("App", AUTOSPREE);
                    //Start details activity
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * Prepare some dummy data for gridview
     */
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        images = new ArrayList<>();

        File dir = getFilesDir();
        String thumbDir = album_name + File.separator + "thumbnails";
        File thumbNailsDir = new File(dir, thumbDir);
        File albumsDir = new File(dir, album_name);

        if (!thumbNailsDir.exists())
        {
            return imageItems;
        }
        File[] files = thumbNailsDir.listFiles();
        for (File file : files)
        {
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bmp != null)
            {
                imageItems.add(new ImageItem(bmp));
                String imageName = file.getName();
                String imageNum = imageName.substring(0, imageName.lastIndexOf('.'));
                String pictureFile = imageNum + ".jpg";
                File picFile = new File(albumsDir, pictureFile);
                String videoFile = imageNum + ".mp4";
                File vidFile = new File(albumsDir, videoFile);
                if (picFile.exists())
                {
                    images.add(picFile.getAbsolutePath());
                }
                else if (vidFile.exists())
                {
                    images.add(vidFile.getAbsolutePath());
                }
                else
                {
                    Log.e(TAG, "No such file = " + picFile.getAbsolutePath() + " or video file=" + vidFile.getAbsolutePath());
                }
            }
            else
            {
                Log.e(TAG, "Cannot get Bitmap for file " + file.getAbsolutePath());
            }
        }
        return imageItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        switch (viewType)
        {
            case SHARE_PICTURE_VW:
                inflater.inflate(R.menu.add_item, menu);
                break;
            default:
                return super.onCreateOptionsMenu(menu);

        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        if (item.getItemId() == android.R.id.home)
        {
            Log.d(getClass().getName(), "Back button pressed");
            finish();
        }
        else if (item.getItemId() == R.id.add_item_done)
        {
            boolean[] selItems = gridAdapter.getSelectedItems();
            ArrayList<String> selImages = new ArrayList<>();
            int i=0;
            for (boolean selItem : selItems)
            {
                if (selItem)
                {
                    selImages.add(images.get(i))  ;
                }
                ++i;
            }
            Intent intent = new Intent();
            intent.putStringArrayListExtra("image_items", selImages);

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
