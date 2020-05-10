package com.rekhaninan.common;

import android.content.Context;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;




import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.CONTACTS_ITEM_ADD_NOVWTYP;
import static com.rekhaninan.common.Constants.CONTACTS_MAINVW;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM;
import static com.rekhaninan.common.Constants.MAINVW;
import static com.rekhaninan.common.Constants.MAX_BUF;
import static com.rekhaninan.common.Constants.OPENHOUSES;
import static com.rekhaninan.common.Constants.RCV_BUF_LEN;


/**
 * Created by ninanthomas on 2/12/17.
 */

public class ShareMgr extends Thread {

    private static final ShareMgr INSTANCE = new ShareMgr();
    final Lock dataToSend = new ReentrantLock();
    final Condition dataToSendCondn  = dataToSend.newCondition();
    private final String TAG = "ShareMgr";
    private NtwIntf ntwIntf;
    private MessageDecoder pDecoder;
    Handler handler;
    private Context ctxt;
    private  ByteBuffer resp;
    private String picurl;
    private long piclen;
    private long picsofar;
    private String  app_name;
    private File thumbNailsDir;
    private FileOutputStream fos;
    private final int THUMBSIZE = 100;
    private File pictureFile, pictureFileTmp;
    private ShareDBIntf shareDBIntf;
    private  long lastPicRcvdTime;
    private boolean sendGetItemReq;
    private long lastIdSentTime;
    private long lastTokenUpdateSentTime;
    private boolean bNtwConnected;

    public int getUploadPicOffset() {
        return uploadPicOffset;
    }

    public void setUploadPicOffset(int uploadPicOffset) {
        this.uploadPicOffset = uploadPicOffset;
    }

    private  int uploadPicOffset;


    private boolean bUpdateTkn;
    private int failed_attempts;
    private long backoff_time;
    private ShareTokenMgr shareTokenMgr;

    public boolean isbSendPic() {
        return bSendPic;
    }

    public void setbSendPic(boolean bSendPic) {
        this.bSendPic = bSendPic;
    }

    public boolean isbSendPicMetaData() {
        return bSendPicMetaData;
    }

    public void setbSendPicMetaData(boolean bSendPicMetaData) {
        this.bSendPicMetaData = bSendPicMetaData;
    }

    private boolean bSendPic;
    private boolean bSendPicMetaData;

    private long share_id;


    private ConcurrentLinkedQueue<ByteBuffer> msgsToSend;
    private ConcurrentLinkedQueue<String> imgsToSend;
    private ConcurrentLinkedQueue<String> imgsMetaData;


    public long getShare_id() {
        return share_id;
    }

    public void setShare_id(long share_id) {
        this.share_id = share_id;
        Item newContact = new Item();
        newContact.setName("ME");
        newContact.setShare_id(share_id);
        Log.i(TAG, "Setting share_id=" + share_id);
        DBOperations.getInstance().insertDb(newContact, CONTACTS_ITEM_ADD_NOVWTYP);
        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharing.edit();
        editor.putLong("share_id", share_id);
        editor.commit();
        shareDeviceTkn();
    }

    public boolean isbUpdateTkn() {
        return bUpdateTkn;
    }

    public void setbUpdateTkn(boolean bUpdateTkn) {
        this.bUpdateTkn = bUpdateTkn;
    }

    private ShareMgr ()
    {

        ntwIntf = new NtwIntf();
        piclen =0;
        picurl = null;
        fos =null;
        picsofar = 0;
        pictureFile = null;
        failed_attempts = 0;

    }

    private long getAutoSpreeShareId()
    {
        try {
            Context con = ctxt.createPackageContext("com.rekhaninan.autospree", 0);//first app package name is "com.sharedpref1"
           if (con == null)
           {
               Log.i(TAG, "Cannot obtain context AutoSpree not installed?");
               return 0;
           }
            SharedPreferences pref = con.getSharedPreferences(
                    "Sharing", Context.MODE_PRIVATE);
            if (pref == null)
            {
                Log.i(TAG, "Cannot obtain SharedPreferences AutoSpree not installed?");
                return 0;
            }
            Log.i(TAG, "get share_id in getAutoSpreeShareId");
            long shid = pref.getLong("share_id", 0);
            return shid;
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e("Not data shared", e.toString());
        }
        return 0;
    }

    private long getEasyGrocShareId()
    {
        try {
            Context con = ctxt.createPackageContext("com.rekhaninan.easygroclist", 0);//first app package name is "com.sharedpref1"
            if (con == null)
            {
                Log.i(TAG, "Cannot obtain context EasyGrocList not installed?");
                return 0;
            }
            SharedPreferences pref = con.getSharedPreferences(
                    "Sharing", Context.MODE_PRIVATE);
            long shid = pref.getLong("share_id", 0);
            return shid;
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e("Not data shared", e.toString());
        }
        return 0;
    }

    private long getOpenHousesShareId()
    {
        try {
            Context con = ctxt.createPackageContext("com.rekhaninan.openhouses", 0);//first app package name is "com.sharedpref1"
            if (con == null)
            {
                Log.i(TAG, "Cannot obtain context OpenHouses not installed?");
                return 0;
            }
            SharedPreferences pref = con.getSharedPreferences(
                    "Sharing", Context.MODE_PRIVATE);
            long shid = pref.getLong("share_id", 0);
            return shid;
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e("Not data shared", e.toString());
        }
        return 0;
    }

    public void processShouldUploadMessage(int upload)
    {
        Log.i(TAG, "Received processShouldUploadMessage=" + upload);
        if (upload > 0)
        {
            bSendPic = true;
        }
        else
        {
            bSendPicMetaData = true;
            updatePicIndx();
        }
    }

    public static ShareMgr getInstance ()
    {

        return INSTANCE;
    }

    private boolean setOHAspreePicDetails(long shareId, String picName, String itemName, int picoffset, long picLen)
    {
        try {
            fos  = null;
            java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(MAINVW);
            for (Item itm : mainLst) {
                if (itm.getShare_id() == shareId && itm.getName().equals(itemName)) {
                    File dir = ctxt.getFilesDir();
                    String thumbDir = itm.getAlbum_name() + File.separator + "thumbnails";
                    thumbNailsDir = new File(dir, thumbDir);
                    if (!thumbNailsDir.exists()) {
                        thumbNailsDir.mkdirs();
                    }
                    File album_dir = new File(dir, itm.getAlbum_name());
                    if (!album_dir.exists()) {
                        album_dir.mkdirs();
                    }

                    if (picName.endsWith(".MOV"))
                    {
                        picName = picName.substring(0, picName.length()-4);
                        picName += ".mp4";
                    }
                    String fileName = album_dir.getAbsolutePath() + File.separator + picName;
                    pictureFile = new File(fileName);
                    if (pictureFile.length() >= picLen)
                        return false;
                    boolean append = false;
                    if (picoffset > 0)
                        append = true;
                    fos = new FileOutputStream(pictureFile, append);
                    Log.i(TAG, "Opened picture file for writing=" + fileName);
                    return true;

                }
            }
        }
        catch (FileNotFoundException excp)
        {
                Log.e(TAG, "Caught file not found exception " + excp.getMessage());
        }
        return false;
    }

    private void updatePicLenStored(long picoffset)
    {
        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharing.edit();
        editor.putLong("PicLenStored", picoffset);
        editor.commit();
    }

    private void setPicDownloadInfo(long share_id, String picName, String itemName, long picLen, int picoffset)
    {
        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharing.edit();
        editor.putString("PicName", picName);
        editor.putLong("PicLen", picLen);
        editor.putLong("PicLenStored", picoffset);
        editor.putLong("PicShareId", share_id);
        editor.commit();
    }

    private boolean setEasyGrocPicDetails(long shareId, String picName, String itemName, int picoffset,long picLen)
    {
        try {
            fos  = null;
            java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(MAINVW);
            boolean bFnd = false;
            for (Item itm : mainLst) {
                if (itm.getShare_id() == shareId && itm.getShare_name().equals(itemName)) {
                    File dir = ctxt.getFilesDir();
                    String thumbDir = EASYGROC + File.separator + "thumbnails";
                    thumbNailsDir = new File(dir, thumbDir);
                    if (!thumbNailsDir.exists()) {
                        thumbNailsDir.mkdirs();
                    }

                    pictureFile = new File(itm.getPicurl());
                    if (pictureFile.length() >= picLen)
                        return false;
                    fos = new FileOutputStream(pictureFile);
                    bFnd = true;
                    break;
                }
            }

            if (!bFnd) {
                Item itm = new Item();
                itm.setShare_id((int) shareId);
                itm.setShare_name(itemName);
                itm.setName(itemName);


                File dir = ctxt.getFilesDir();
                File album_dir = new File(dir, EASYGROC);
                if (!album_dir.exists()) {
                    album_dir.mkdirs();
                }
                File mediaFileDir = new File(album_dir.getAbsolutePath() + File.separator
                        + Long.toString(shareId));
                if (!mediaFileDir.exists())
                {
                    mediaFileDir.mkdirs();
                }
                File mediaFile = new File(mediaFileDir.getAbsolutePath() + File.separator
                        + picName);
                itm.setPicurl(mediaFile.getAbsolutePath());


                pictureFile = new File(itm.getPicurl());
                if (pictureFile.length() >= picLen)
                    return false;
                DBOperations.getInstance().insertDb(itm, EASYGROC_ADD_ITEM);
                boolean append = false;
                if (picoffset > 0)
                    append = true;
                fos = new FileOutputStream(pictureFile, append);
                return true;
            }
        }catch (FileNotFoundException excp)
            {
                Log.e(TAG, "Caught file not found exception " + excp.getMessage());
            }
        catch (Exception excp)
        {
            Log.e(TAG, "Caught  exception " + excp.getMessage());
        }
        return false;
    }

    public void setPicDetails (long shareId, String picName, String itemName, long picLen, int picoffset)
    {
        lastPicRcvdTime = System.currentTimeMillis();
        boolean download = true;
        try {
            piclen = picLen;
            picurl = picName;
            switch (app_name) {
                case AUTOSPREE:
                case OPENHOUSES: {
                    download = setOHAspreePicDetails(shareId, picName, itemName, picoffset, picLen);
                }
                break;


                case EASYGROC:
                {
                    download = setEasyGrocPicDetails(shareId, picName, itemName, picoffset, picLen);
                }
                    break;

                default:
                    break;
            }

            setPicDownloadInfo(shareId, picName, itemName, picLen, picoffset);
            picsofar = (long) picoffset;
            putMsgInQ(MessageTranslator.shouldDownLoadMsg(shareId, picName, download));
        }
        catch (Exception excp)
        {
            Log.e(TAG, "Caught  exception " + excp.getMessage());
        }
        return;
    }

    private int getRotateDegrees(int orientation)
    {
        int rotateDegrees = 0;
        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotateDegrees = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotateDegrees = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotateDegrees = 270;
                break;

            default:
                break;
        }
        return rotateDegrees;
    }

    private void rotateImageIfReqd()
    {
        try
        {
            ExifInterface exif = new ExifInterface(pictureFile.getAbsolutePath());
             int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
             Log.i(TAG, "Orientation of image=" + orientation + " Image path=" + pictureFile.getAbsolutePath());

            int rotateDegrees = getRotateDegrees(orientation);
            if (rotateDegrees == 0)
                return;
            Bitmap bmp = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateDegrees);
             bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            FileOutputStream fOut;
            try {
                 pictureFileTmp = new File(pictureFile.getAbsolutePath() + ".tmp");
                fOut = new FileOutputStream(pictureFileTmp);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                pictureFile.delete();
                pictureFileTmp.renameTo(pictureFile);

            } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            } catch (IOException e) {
            // TODO Auto-generated catch block
             e.printStackTrace();
            }
        }
        catch (Exception e) {
         e.printStackTrace();
        }

    }

    private void rotatePictureIfReqd()
    {
        try
        {
            if (pictureFile.getAbsolutePath().endsWith(".jpg"))
                rotateImageIfReqd();

        }
     catch (Exception e) {
        e.printStackTrace();
        }
    }


    private void extractAndStoreThumbNail() {
        try
        {
            Bitmap ThumbImage;
            if (pictureFile.getAbsolutePath().endsWith("mp4"))
            {
                ThumbImage = ThumbnailUtils.createVideoThumbnail(pictureFile.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
            }
            else
            {
                ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pictureFile.getAbsolutePath()),
                        THUMBSIZE, THUMBSIZE);
            }

             if (ThumbImage == null) {
                Log.e(TAG, "Cannot extract thumbnail for " + pictureFile.getAbsolutePath());
               return;
             }

             File thumbNail = new File(thumbNailsDir, pictureFile.getName());
             FileOutputStream fostn = new FileOutputStream(thumbNail);
             ThumbImage.compress(Bitmap.CompressFormat.JPEG, 100, fostn); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
             fostn.flush(); // Not really required
             fostn.close();
    }
        catch (IOException excp)
        {
            Log.e(TAG, "Caught IOException  exception " + excp.getMessage());
        }
    }

    public void storePicData(ByteBuffer buffer, int offset, int msglen)
    {
        try {
            if (fos != null) {
                fos.write(buffer.array(), offset, msglen);
                picsofar += msglen;
                lastPicRcvdTime = System.currentTimeMillis();
                Log.i(TAG, "Storing picture picsofar=" + picsofar + " piclen=" + piclen+ " offset=" + offset +
                        " msglen=" + msglen);
                updatePicLenStored(picsofar);
                if (picsofar >= piclen)
                {
                    fos.close();
                    fos = null;
                    piclen =0;
                    picsofar = 0;
                    lastPicRcvdTime = 0;
                    rotatePictureIfReqd();
                    if (app_name.equals(EASYGROC))
                    {
                        Log.i(TAG, "Finishing storing picture closing fileoutputstream fos");
                        refreshMainVw();
                        return;
                    }
                    extractAndStoreThumbNail();
                    Log.i(TAG, "Finishing storing picture closing fileoutputstream fos");
                    ntwIntf.sendMsg(MessageTranslator.picDoneMsg(ctxt, share_id));
                    Log.i(TAG, "Sent picDone msg");
                }
            }
        }
        catch (IOException excp)
        {
            Log.e(TAG, "Caught IOException  exception " + excp.getMessage());
        }
        return;
    }



    public void  start_thr(Context ctx, String appname)
    {
        Log.i(TAG, "Starting share mgr thread");
        handler = new Handler(Looper.getMainLooper());
        Log.i(TAG, "Got handler handle");
        ctxt = ctx;
        share_id = 0;
        app_name = appname;
        setShId();
        Log.i(TAG, "setShId done");
        shareDBIntf = new ShareDBIntf();
        shareDBIntf.initDb(ctx);
        msgsToSend = shareDBIntf.refreshItemData();
        imgsToSend = shareDBIntf.refreshImages();
        imgsMetaData = shareDBIntf.refreshImagesMetaData();
        INSTANCE.start();
    }

    public void refreshMainVw()
    {
        Runnable refreshMainVwRunnable = new Runnable() {
            @Override
            public void run() {
               MainVwActivity mainVwActivity = (MainVwActivity) ctxt;
                mainVwActivity.refreshVw();
            } // This is your code
        };
        handler.post(refreshMainVwRunnable);
        return;
    }

    private void setShId()
    {
        try {
            java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(CONTACTS_MAINVW);
            for (Item itm : mainLst) {
                if (itm.getName().equals("ME")) {
                    share_id = itm.getShare_id();
                    break;
                }
            }

            Log.i(TAG, "ShareId set to " + share_id);
            if (share_id != 0) {
                Log.i(TAG, "ShareId set to " + share_id);
                return;
            }

            if (share_id == 0) {
                switch (app_name) {
                    case OPENHOUSES: {
                        share_id = getAutoSpreeShareId();
                        if (share_id == 0) {
                            share_id = getEasyGrocShareId();
                        }
                    }
                    break;

                    case AUTOSPREE: {
                        share_id = getOpenHousesShareId();
                        if (share_id == 0) {
                            share_id = getEasyGrocShareId();
                        }
                    }
                    break;

                    case EASYGROC: {
                        share_id = getOpenHousesShareId();
                        if (share_id == 0) {
                            share_id = getAutoSpreeShareId();
                        }
                    }
                    break;
                }
            }
            Log.i(TAG, "ShareId 1 set to " + share_id);
            if (share_id != 0) {
                Item newContact = new Item();
                newContact.setName("ME");
                newContact.setShare_id(share_id);
                Log.i(TAG, "Setting share_id=" + share_id + " from shared preferences");
                DBOperations.getInstance().insertDb(newContact, CONTACTS_ITEM_ADD_NOVWTYP);
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Caught exception " + e.toString());
        }
    }

    public void setConnDetails ()
    {

        return;
    }

    private void getIdIfRequired()
    {
        if (share_id != 0)
            return;
        long now = System.currentTimeMillis();
        if (lastIdSentTime >0)
        {
            if (now < lastIdSentTime+1000*120)
                return;
        }
        lastIdSentTime = now;
        Log.d(TAG, "Getting shareId");
        if (ntwIntf.sendMsg(MessageTranslator.createIdRequest()))
        {
            Log.i(TAG, "Send share_id request");
        }
        else
        {
            Log.e(TAG, "Failed to send share_id request");
        }

        return;
    }

    @Override
    public void run() {
        uploadPicOffset = 0;
        bSendPic = false;
        bSendPicMetaData = true;
        lastIdSentTime  = 0;
        lastPicRcvdTime = 0;
        lastPicRcvdTime = 0;
        sendGetItemReq = false;
        bNtwConnected = true;
        shareTokenMgr = new ShareTokenMgr();
        Log.d(TAG, "getting Firebase token token: ");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (refreshedToken == null)
        {
            Log.d(TAG, "NULL pointer returned by FirebaseInstanceId getToken");
        }

            Log.d(TAG, "Refreshed token: " + refreshedToken);

            // TODO: Implement this method to send any registration to your app's servers.
            // sendRegistrationToServer(refreshedToken);
            SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);


             String devTkn = sharing.getString("token", "None");
            if (!devTkn.equals(refreshedToken)) {
                SharedPreferences.Editor editor = sharing.edit();
                editor.putString("token", refreshedToken);
                editor.putBoolean("update", true);
                editor.commit();
            }



        ntwIntf = new NtwIntf();
        pDecoder = new MessageDecoder();
        ntwIntf.setConnectionDetails(app_name);
        pDecoder.setApp_name(app_name);
        resp = ByteBuffer.allocate(RCV_BUF_LEN);

        bUpdateTkn = sharing.getBoolean("update", true);
        Log.i(TAG, "update token=" + bUpdateTkn);
        getItems();
        for (;;)
        {
            dataToSend.lock();
            try
            {

                if (msgsToSend.size() == 0 || imgsToSend.size() == 0)
                {
                    int delay = 1;
                    if (!bNtwConnected)
                        delay = 30;
                    dataToSendCondn.await(delay, TimeUnit.SECONDS);
                }
                dataToSend.unlock();
                ntwIntf.checkAndCloseIfIdle();
                if (sendGetItemReq && share_id >0)
                {
                    putMsgInQ(MessageTranslator.getItemsMsg(ctxt, share_id));
                    sendGetItemReq = false;
                }
                getIdIfRequired();
                shareDeviceTkn();
                sendMsgs();


                processResponse();

            }
            catch (InterruptedException excp)
            {
                Log.i(TAG, "ShareMgr Thread interrupted " + excp.getMessage());
                dataToSend.unlock();
            }
            catch (Exception excp)
            {
                Log.i(TAG, "ShareMgr Thread caught exception " + excp.getMessage());
                dataToSend.unlock();
            }

        }

    }

    private void processResponse()
    {
        //Log.i(TAG, "In processResponse");
        boolean more = true;
        for(;;) {
            resp.clear();
            resp.order(ByteOrder.LITTLE_ENDIAN);
            boolean gotResp = ntwIntf.getResp(resp);
            //Log.i(TAG, "In processResponse gotResp=" + gotResp);
            if (!gotResp)
                break;
            more = pDecoder.processMessage(resp);
            Log.i(TAG, "In processResponse more=" + gotResp);
           // if (!more)
             //   break;
        }

    }

    public void getItems()
    {
        if (share_id <= 0)
        {
            return;
        }
        Log.d(TAG, "Putting getItemsMsg in Q");
        long now = System.currentTimeMillis();
        if (now - lastPicRcvdTime > 120000) {
            putMsgInQ(MessageTranslator.getItemsMsg(ctxt, share_id));
        }
        else {
            sendGetItemReq = true;
        }
    }

    public void shareItem (String item, String itemName)
    {
        if (item == null || item.length() <= 0 || itemName == null || itemName.length() <= 0)
            return;
        putMsgInQ(MessageTranslator.shareItemMsg(share_id, itemName, item));

    }

    public void shareTemplItem (String item, String itemName)
    {
        if (item == null || item.length() <= 0 || itemName == null || itemName.length() <= 0)
            return;
        putMsgInQ(MessageTranslator.shareTemplItemMsg(share_id, itemName, item));
        return;
    }

    public void updateFriendList(String frndList)
    {
        if (frndList == null || frndList.length() <= 0)
         return;
        putMsgInQ(MessageTranslator.updateFriendListRequest(share_id, frndList));

    }

    public void shareDeviceTkn()
    {
        if (!bUpdateTkn)
        {
            return;
        }
        SharedPreferences sharing =  ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        String devTkn = sharing.getString("token", "None");

        if (devTkn.equals("None"))
            return;
        if (share_id == 0)
        {
            return;
        }

        if (lastTokenUpdateSentTime >0)
        {
            long now = System.currentTimeMillis();
            if (now < lastTokenUpdateSentTime+1000*120)
                return;
        }

        lastTokenUpdateSentTime = System.currentTimeMillis();
        Log.i(TAG, "Sharing device token");
        if (ntwIntf.sendMsg(MessageTranslator.shareDevicTknMsg(share_id, devTkn)))
        {
            Log.i(TAG, "Send device token message");
        }

    }

    public void updateDeviceTknStatus()
    {
        SharedPreferences sharing =  ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        bUpdateTkn = false;
        Log.i(TAG, "Updating device token status to false");
        SharedPreferences.Editor editor = sharing.edit();
        editor.putBoolean("update", false);
        editor.commit();
    }

    public void sharePicture(String picUrl, String picMetaStr, long shareId)
    {
        String picmetashid = picMetaStr;
        picmetashid += ":::]";
        picmetashid += Long.toString(shareId);
        putPicInQ(picUrl, picmetashid);
        return;
    }

    public void putPicInQ(String picUrl, String picMetaStr)
    {
        Log.i(TAG, "Adding image to send picUrl=" + picUrl + " picMetaStr=" + picMetaStr);
        imgsMetaData.add(picMetaStr);
        imgsToSend.add(picUrl);
        shareDBIntf.insertImage(picUrl);
        shareDBIntf.insertImagesMetaData(picMetaStr);
        signalDataReady();
        return;

    }

    public void signalDataReady()
    {
        dataToSend.lock();
        try
        {
            dataToSendCondn.signal();
        }

        catch (Exception excp)
        {
            Log.e(TAG, "ShareMgr Thread caught exception " + excp.getMessage());

        }
        finally {
            dataToSend.unlock();
        }
        return;
    }

    public void putMsgInQ(ByteBuffer msg)
    {
        if (msg == null)
        {
            return;
        }
        Log.d(TAG, "putMsgInQ called");
        msgsToSend.add(msg);
        shareDBIntf.insertItem(msg);
       signalDataReady();
        return;
    }

    void sendMsgs()
    {

        try {

            while (msgsToSend.size() != 0 || (bSendPic && imgsToSend.size() != 0)
                    || (bSendPicMetaData && imgsMetaData.size() !=0)) {

                Log.d(TAG, "msgsToSend=" + msgsToSend.size() + " imsgToSend=" + imgsToSend.size()
                        + " bSendPic=" +bSendPic + " imgsMetaData=" + imgsMetaData.size() +
                        " bSendPicMetaData=" + bSendPicMetaData);



                ByteBuffer msg = msgsToSend.peek();

                if (msg != null && ntwIntf.sendMsg(msg) == false)
                {
                    postErrorMessage();
                    bNtwConnected = false;
                    break;
                }
                else {
                    msgsToSend.poll();
                    shareDBIntf.deleteItem();
                    bNtwConnected = true;
                }

                String picMetaData = null;
                if (bSendPicMetaData) {
                    picMetaData = imgsMetaData.peek();
                }

                String imgFileStr = null;
                if (bSendPic) {
                    imgFileStr = imgsToSend.peek();
                }


                if (picMetaData != null && imgsToSend.peek() != null) {
                    String imgFileStr1 = imgsToSend.peek();
                    File imgFile = new File(imgFileStr1);
                    int picLength = (int) imgFile.length();
                    if (picLength == 0)
                        continue;
                    String[] pMainArr = picMetaData.split(":::]");
                    if (pMainArr.length != 2) {
                        Log.e(TAG, "Invalid picMetaData pMainArr.length=" + pMainArr.length);
                        continue;
                    }
                    if (!ntwIntf.sendMsg(MessageTranslator.sharePicMetaDataMsg(Long.parseLong(pMainArr[1]), imgFileStr1, picLength, pMainArr[0]))) {
                        postErrorMessage();
                        bNtwConnected= false;
                        continue;
                    }
                    else
                    {
                        bNtwConnected = true;
                    }
                    bSendPicMetaData = false;

                }


                if (imgFileStr != null) {
                    byte[] bytes = new byte[MAX_BUF - 8];



                    FileInputStream imgFilStream = new FileInputStream(imgFileStr);
                    if (uploadPicOffset > 0)
                    {
                        imgFilStream.skip(uploadPicOffset);
                    }
                    //read(byte[] b, int off, int len)
                    boolean bSendFinished = false;
                    for(;;)
                    {
                        int nRead = imgFilStream.read(bytes);
                        if (nRead == -1)
                        {
                            bSendFinished = true;
                            break;
                        }

                        if (ntwIntf.sendMsg(MessageTranslator.sharePicMsg(bytes, nRead)) == false) {
                            postErrorMessage();
                            bNtwConnected = false;
                            break;
                        }
                        else
                        {
                            bNtwConnected = true;
                        }

                    }
                    bSendPicMetaData = true;
                    bSendPic = false;
                    if (bSendFinished)
                    {
                        updatePicIndx();

                    }

                }


            }
        }
        catch (NullPointerException excp)
        {
            Log.e(TAG, "ShareMgr Thread caught NullPointerException in sendMsgs " + excp.getMessage());
            postErrorMessage();
        }
        catch(FileNotFoundException excp)
        {
            Log.e(TAG, "ShareMgr Thread caught FileNotFound in sendMsgs " + excp.getMessage());
            postErrorMessage();
        }
        catch(SecurityException excp)
        {
            Log.e(TAG, "Caught SecurityException in sendMsgs " + excp.getMessage());
            postErrorMessage();
        }
        catch (Exception excp)
        {
            Log.e(TAG, "ShareMgr Thread caught Exception in sendMsgs " + excp.getMessage());
            postErrorMessage();
        }

        return;
    }

    void updatePicIndx()
    {
        shareDBIntf.deleteImagesMetaData();
        shareDBIntf.deleteImage();
        imgsMetaData.poll();
        imgsToSend.poll();
    }

    void postErrorMessage()
    {
        /* not needed now
        Runnable alertRunnable = new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(ctxt).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Failed to share item");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                return;
                            }
                        });
                alertDialog.show();
            } // This is your code
        };
        handler.post(alertRunnable);
        */
    }


}
