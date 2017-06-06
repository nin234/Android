package com.rekhaninan.common;

import android.content.ContentValues;
import android.content.Context;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;




import static com.rekhaninan.common.Constants.AUTOSPREE;
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
    private File pictureFile;



    private boolean bUpdateTkn;
    private int failed_attempts;
    private long backoff_time;
    private ShareTokenMgr shareTokenMgr;

    private long share_id;


    private ConcurrentLinkedQueue<ByteBuffer> msgsToSend;
    private ConcurrentLinkedQueue<String> imgsToSend;
    private ConcurrentLinkedQueue<String> imgsMetaData;


    public long getShare_id() {
        return share_id;
    }

    public void setShare_id(long share_id) {
        this.share_id = share_id;
    }

    public boolean isbUpdateTkn() {
        return bUpdateTkn;
    }

    public void setbUpdateTkn(boolean bUpdateTkn) {
        this.bUpdateTkn = bUpdateTkn;
    }

    private ShareMgr ()
    {
        msgsToSend = new ConcurrentLinkedQueue<>();
        imgsToSend = new ConcurrentLinkedQueue<>();
        imgsMetaData = new ConcurrentLinkedQueue<>();
        ntwIntf = new NtwIntf();
        piclen =0;
        picurl = null;
        fos =null;
        picsofar = 0;
        pictureFile = null;
        failed_attempts = 0;

    }

    public static ShareMgr getInstance ()
    {

        return INSTANCE;
    }

    public void setPicDetails (long shareId, String picName, String itemName, long picLen)
    {
        try {
            piclen = picLen;
            picurl = picName;
            switch (app_name) {
                case AUTOSPREE:
                case OPENHOUSES: {
                    java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(MAINVW);
                    for (Item itm : mainLst) {
                        if (itm.getShare_id() == shareId && itm.getShare_name().equals(itemName)) {
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
                            pictureFile = new File(album_dir.getAbsolutePath() + File.separator
                                    + picName);
                            fos = new FileOutputStream(pictureFile);
                            break;
                        }
                    }
                }
                break;


                case EASYGROC:
                {
                    java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(MAINVW);
                    boolean bFnd =false;
                    for (Item itm : mainLst) {
                        if (itm.getShare_id() == shareId && itm.getShare_name().equals(itemName)) {
                            File dir = ctxt.getFilesDir();
                            String thumbDir = EASYGROC + File.separator + "thumbnails";
                            thumbNailsDir = new File(dir, thumbDir);
                            if (!thumbNailsDir.exists()) {
                                thumbNailsDir.mkdirs();
                            }

                            pictureFile = new File(itm.getPicurl());
                            fos = new FileOutputStream(pictureFile);
                            bFnd = true;
                            break;
                        }
                    }
                    if (!bFnd)
                    {
                        Item itm = new Item();
                        itm.setShare_id((int)shareId);
                        itm.setShare_name(itemName);
                        itm.setName(itemName);
                        boolean exists = DBOperations.getInstance().itemExists(itm, EASYGROC_ADD_ITEM);
                        if (exists)
                        {
                            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                            Calendar c = Calendar.getInstance();
                            String formattedDate = df.format(c.getTime());
                            itm.setName("List " + formattedDate);

                        }
                        exists = DBOperations.getInstance().itemExists(itm, EASYGROC_ADD_ITEM);
                        if (exists)
                            break;
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        File dir = ctxt.getFilesDir();
                        File album_dir = new File(dir, EASYGROC);
                        File   mediaFile = new File(album_dir.getAbsolutePath() + File.separator
                                + timeStamp + ".jpg");
                        itm.setPicurl(mediaFile.getAbsolutePath());

                        DBOperations.getInstance().insertDb(itm, EASYGROC_ADD_ITEM);
                        pictureFile = new File(itm.getPicurl());
                        fos = new FileOutputStream(pictureFile);
                    }
                }
                    break;

                default:
                    break;
            }
        }
        catch (FileNotFoundException excp)
        {
            Log.e(TAG, "Caught file not found exception " + excp.getMessage());
        }
        return;
    }

    public void storePicData(ByteBuffer buffer, int offset, int msglen)
    {
        try {
            if (fos != null) {
                fos.write(buffer.array(), offset, msglen);
                picsofar += msglen;
                if (picsofar >= piclen)
                {
                    fos.close();
                    fos = null;
                    piclen =0;
                    picsofar = 0;
                    Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pictureFile.getAbsolutePath()),
                            THUMBSIZE, THUMBSIZE);

                    File thumbNail = new File(thumbNailsDir, pictureFile.getName());
                    FileOutputStream fostn = new FileOutputStream(thumbNail);
                    ThumbImage.compress(Bitmap.CompressFormat.JPEG, 100, fostn); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                    fostn.flush(); // Not really required
                    fostn.close();
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
        handler = new Handler(Looper.getMainLooper());
        ctxt = ctx;
        share_id = 0;
        setShId();
        app_name = appname;


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
        java.util.List<Item> mainLst = DBOperations.getInstance().getMainLst(CONTACTS_MAINVW);
        for (Item itm : mainLst)
        {
            if (itm.getName().equals("ME"))
            {
                share_id = itm.getShare_id();
                break;
            }
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

        bUpdateTkn = sharing.getBoolean("update", false);
        getIdIfRequired();
        if (bUpdateTkn)
        {
            shareDeviceTkn();
        }


        for (;;)
        {
            dataToSend.lock();
            try
            {

                if (msgsToSend.size() == 0 || imgsToSend.size() == 0)
                {
                    dataToSendCondn.await(5, TimeUnit.SECONDS);
                }
                dataToSend.unlock();
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
        boolean more = true;
        for(;;) {
            resp.clear();
            boolean gotResp = ntwIntf.getResp(resp);

            if (!gotResp)
                break;
            more = pDecoder.processMessage(resp);

            if (!more)
                break;
        }

    }

    public void getItems()
    {
        Log.d(TAG, "Putting getItemsMsg in Q");
        putMsgInQ(MessageTranslator.getItemsMsg(share_id));
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
        SharedPreferences sharing =  ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        String devTkn = sharing.getString("token", "None");
        if (devTkn.equals("None"))
            return;

        if (ntwIntf.sendMsg(MessageTranslator.shareDevicTknMsg(share_id, devTkn)))
        {
            bUpdateTkn = false;
            SharedPreferences.Editor editor = sharing.edit();
            editor.putBoolean("update", false);
            editor.commit();
        }



    }

    public void sharePicture(String picUrl, String picMetaStr)
    {
        putPicInQ(picUrl, picMetaStr);
        return;
    }

    public void putPicInQ(String picUrl, String picMetaStr)
    {
        imgsToSend.add(picUrl);
        imgsMetaData.add(picMetaStr);
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
       signalDataReady();
        return;
    }

    void sendMsgs()
    {
        try {
            while (msgsToSend.size() != 0 || imgsToSend.size() != 0) {

                Log.d(TAG, "msgsToSend=" + msgsToSend.size() + " imsgToSend=" + imgsToSend.size());
                getIdIfRequired();
                if (bUpdateTkn)
                {
                    shareDeviceTkn();
                }

                ByteBuffer msg = msgsToSend.poll();

                if (msg != null && ntwIntf.sendMsg(msg) == false)
                {
                    postErrorMessage();
                }

                String picMetaData = imgsMetaData.poll();
                String imgFileStr = imgsToSend.poll();
                if (picMetaData != null && imgFileStr != null) {
                    File imgFile = new File(imgFileStr);
                    int picLength = (int)imgFile.length();
                    if (picLength == 0)
                        continue;
                    if (ntwIntf.sendMsg(MessageTranslator.sharePicMetaDataMsg(share_id, imgFileStr, picLength, picMetaData))== false)
                    {
                        postErrorMessage();
                        continue;
                    }

                    byte[] bytes = new byte[MAX_BUF - 8];



                    FileInputStream imgFilStream = new FileInputStream(imgFileStr);
                    //read(byte[] b, int off, int len)
                    for(;;)
                    {
                        int nRead = imgFilStream.read(bytes);
                        if (nRead == -1)
                        {
                            break;
                        }

                        if (ntwIntf.sendMsg(MessageTranslator.sharePicMsg(bytes, nRead)) == false) {
                            postErrorMessage();
                            break;
                        }

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
            Log.e(TAG, "ShareMgr Thread caught NullPointerException in sendMsgs " + excp.getMessage());
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

    void postErrorMessage()
    {
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
    }


}
