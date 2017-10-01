package com.rekhaninan.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.media.MediaRecorder;
import android.media.CamcorderProfile;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.view.Surface;
import android.widget.FrameLayout;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.CompoundButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.view.MenuItem;
import android.media.ThumbnailUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static com.rekhaninan.common.Constants.EASYGROC;

public class CameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private CameraPreview1 mPreview;
    private String album_name;
    private int orientation;
    private File album_dir;
    private File thumbNailsDir;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String TAG="CameraActivity";
    private boolean isRecording = false;
    private File videoFile;
    private final int THUMBSIZE = 100;
    private String timeStamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        // Create an instance of Camera

        Log.d(TAG, "Starting camera activity");

        mCamera = getCameraInstance();
        setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        Intent intent = getIntent();
        album_name = intent.getStringExtra("album_name");
        File dir = getFilesDir();
        String thumbDir = album_name + File.separator + "thumbnails";
        thumbNailsDir = new File(dir, thumbDir);
        if (!thumbNailsDir.exists())
        {
            thumbNailsDir.mkdirs();
        }
        album_dir = new File(dir, album_name);
        if (!album_dir.exists())
        {
            album_dir.mkdirs();
        }

       ActionBar actionBar = getSupportActionBar();
       // actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.hide();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview1(this, mCamera);
        Log.d(TAG, "Starting camera activity5");
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        Log.d(TAG, "Starting camera activity6");
        if (preview == null)
        {
            Log.d(TAG, "Null pointer preview returning");
            return;
        }
        preview.addView(mPreview);
       // mCamera.setDisplayOrientation(90);

        final ImageButton cameraButton = (ImageButton) findViewById(R.id.photo_capture);
        Log.d(TAG, "Starting camera activity8");
        cameraButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );

        cameraButton.setVisibility(View.VISIBLE);
        final ImageButton videoButton = (ImageButton) findViewById(R.id.video_capture);

        Switch pictureVideoToggle = (Switch) findViewById(R.id.video_picture);

        videoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isRecording) {
                            // stop recording and release camera
                            mMediaRecorder.stop();  // stop the recording
                            releaseMediaRecorder(); // release the MediaRecorder object
                            mCamera.lock();         // take camera access back from MediaRecorder

                            // inform the user that recording has stopped
                           // setCaptureButtonText("Capture");
                            isRecording = false;
                            Bitmap ThumbImage = ThumbnailUtils.createVideoThumbnail(videoFile.getAbsolutePath(), android.provider.MediaStore.Images.Thumbnails.MICRO_KIND);
                            File thumbNail = new File(thumbNailsDir, timeStamp + ".jpg");
                            writeThumbImage(ThumbImage, thumbNail);
                            videoButton.setImageResource(R.drawable.ic_videocam_off_black_48dp);
                        } else {
                            // initialize video camera
                            if (prepareVideoRecorder()) {
                                // Camera is available and unlocked, MediaRecorder is prepared,
                                // now you can start recording
                                mMediaRecorder.start();

                                // inform the user that recording has started
                                //setCaptureButtonText("Stop");
                                isRecording = true;
                                videoButton.setImageResource(R.drawable.ic_videocam_black_48dp);
                            } else {
                                // prepare didn't work, release the camera
                                releaseMediaRecorder();
                                // inform user
                            }
                        }
                    }
                }
        );
        videoButton.setVisibility(View.GONE);
        pictureVideoToggle.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // do something, the isChecked will be
                        // true if the switch is in the On position
                        if (isChecked)
                        {
                            cameraButton.setVisibility(View.GONE);
                            videoButton.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            videoButton.setVisibility(View.GONE);
                            cameraButton.setVisibility(View.VISIBLE);
                        }

                    }
                }
        );

        if (album_name.equals(EASYGROC))
        {
            pictureVideoToggle.setVisibility(View.GONE);
        }

    }

    private void writeThumbImage(Bitmap ThumbImage, File thumbNail)
    {
        try
        {
            if (thumbNail == null)
            {
                Log.d(TAG, "Error creating thumbnail file, check storage permissions");
                return;
            }

            FileOutputStream fostn = new FileOutputStream(thumbNail);
            ThumbImage.compress(Bitmap.CompressFormat.JPEG, 100, fostn); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fostn.flush(); // Not really required
            fostn.close();

        }
        catch (FileNotFoundException e)
        {
            Log.d(TAG, "File not found: " + e.getMessage());
        }
        catch (IOException e)
        {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

    }

    private void saveFile (final File pictureFile, final byte[] data)
    {
        try
        {
        FileOutputStream fos = new FileOutputStream(pictureFile);
        fos.write(data);
        Log.d(TAG, "Saved picture in file " + pictureFile);
        fos.close();


        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pictureFile.getAbsolutePath()),
                THUMBSIZE, THUMBSIZE);

        File thumbNail = new File(thumbNailsDir, pictureFile.getName());
        writeThumbImage(ThumbImage, thumbNail);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        catch (Exception e) {
            Log.d(TAG, "Caught exception: " + e.getMessage());
        }
    }

    public  void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        Camera.Parameters params = camera.getParameters();
        params.setRotation(result);
        camera.setParameters(params);
        orientation = result;
//        mMediaRecorder.setOrientationHint(result);
        //camera.setDisplayOrientation(-90);
    }

    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {

           final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {

                if (album_name.equals(EASYGROC))
                {
                    final boolean bSave = true;
                    AlertDialog alertDialog = new AlertDialog.Builder(CameraActivity.this).create();
                    alertDialog.setTitle("Picture");
                    alertDialog.setMessage("Use Picture for List");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    saveFile(pictureFile, data);
                                    dialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra("pic_url", pictureFile.getAbsolutePath());
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    return;
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Retake",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mPreview.startPreview();
                                    return;
                                }
                            });
                    alertDialog.show();
                    return;
                }
                saveFile(pictureFile, data);
                mPreview.startPreview();

            } catch (Exception e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.d(TAG, "Camera open failed " + e.getMessage());
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private boolean prepareVideoRecorder(){

        Log.d(TAG, "Preparing video recorder");

        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        Log.d(TAG, "Preparing video recorder 1");
        if (mCamera == null)
        {
            Log.d(TAG, "Got null pointer for camera");
            mCamera = getCameraInstance();
            setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);

        }

        mCamera.unlock();
        Log.d(TAG, "Preparing video recorder 2");
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setOrientationHint(orientation);
        // Step 2: Set sources

        if (PermissionsManager.getInstance().hasAudioRecordPermission(getApplicationContext())) {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        }
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        Log.d(TAG, "Preparing video recorder 3");
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        Log.d(TAG, "Preparing video recorder 4");
        // Step 4: Set output file

        videoFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        mMediaRecorder.setOutputFile(videoFile.toString());

        Log.d(TAG, "Preparing video recorder 5");

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        Log.d(TAG, "Preparing video recorder 6");
        // Step 6: Prepare configured MediaRecorder
        try {
            Log.d(TAG, "Preparing video recorder 7");
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }
    private  Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    @Nullable
    private  File getOutputMediaFile(int type){


        // Create the storage directory if it does not exist
        if (! album_dir.exists()){

                Log.d("MyCameraApp", "failed to create directory");
                return null;

        }

        // Create a media file name
         timeStamp = Long.toString(System.currentTimeMillis()/2000);
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(album_dir.getAbsolutePath() + File.separator
                    + timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(album_dir.getAbsolutePath() + File.separator +
                     timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        if (item.getItemId() == android.R.id.home)
        {
            Log.d(getClass().getName(), "Back button pressed");
            finish();
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
