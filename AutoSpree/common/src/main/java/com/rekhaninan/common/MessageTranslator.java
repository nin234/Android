package com.rekhaninan.common;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.rekhaninan.common.Constants.GET_ITEMS_MSG;
import static com.rekhaninan.common.Constants.GET_REMOTE_HOST_MSG;
import static com.rekhaninan.common.Constants.GET_SHARE_ID_MSG;
import static com.rekhaninan.common.Constants.PIC_DONE_MSG;
import static com.rekhaninan.common.Constants.PIC_METADATA_MSG;
import static com.rekhaninan.common.Constants.PIC_MSG;
import static com.rekhaninan.common.Constants.SHARE_ITEM_MSG;
import static com.rekhaninan.common.Constants.SHARE_TEMPL_ITEM_MSG;
import static com.rekhaninan.common.Constants.SHOULD_DOWNLOAD_MSG;
import static com.rekhaninan.common.Constants.STORE_DEVICE_TKN_MSG;
import static com.rekhaninan.common.Constants.STORE_FRIEND_LIST_MSG;
import static com.rekhaninan.common.Constants.*;

/**
 * Created by ninanthomas on 2/13/17.
 */

public class MessageTranslator {

    private static final String TAG="MessageTranslator";

    public static int getAppId() {
        return APP_ID;
    }

    public static void setAppId(int appId) {
        APP_ID = appId;
    }

    private static int APP_ID;


    public static ByteBuffer sharePicMsg(byte[] msg, int len)
    {
        try {
            int msglen = len + 8;
            if (APP_ID > SMARTMSG_ID)
            {
                msglen += 4;
            }
            ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(msglen);
            if (APP_ID > SMARTMSG_ID)
            {
                byteBuffer.putInt(PIC_1_MSG);
                byteBuffer.putInt(APP_ID);
            }
            else
            {
                byteBuffer.putInt(PIC_MSG);
            }
            byteBuffer.put(msg, 0, len);
            return byteBuffer;
        }
        catch (Exception excp)
        {
            Log.e (TAG, "sharePicMsg Caught exception " + excp.getMessage(), excp);
        }
        return null;
    }

    private static ByteBuffer createIdRequestNoAppId(String androidId)
    {
        try
        {
            int tridLen = 8;
            long trid = 1000;
            int idLen = androidId.getBytes("UTF-8").length+1;
            int msglen =  tridLen + 8 + idLen;
            ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);

            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt( msglen);
            byteBuffer.putInt(GET_SHARE_ID_MSG);
            byteBuffer.putLong(trid);
            byteBuffer.put(androidId.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);
            Log.i(TAG, "Created id request of length=" + msglen);
            return byteBuffer;
        }
        catch (UnsupportedEncodingException excep)
        {
            Log.e(TAG, "shareDevicTknMsg Unsupported encoding UTF-8 " + excep.getMessage(), excep);

        }
        catch (Exception excp)
        {
            Log.e (TAG, "shareDevicTknMsg Caught exception " + excp.getMessage(), excp);
        }
        return null;
    }

    private static ByteBuffer createIdRequestAppId(String androidId)
    {
        try
        {
            int tridLen = 8;
            long trid = 1000;
            int idLen = androidId.getBytes("UTF-8").length+1;
            int msglen =  tridLen + 12 + idLen;
            ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);

            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt( msglen);
            byteBuffer.putInt(GET_SHARE_ID_1_MSG);
            byteBuffer.putInt(APP_ID);
            byteBuffer.putLong(trid);
            byteBuffer.put(androidId.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);
            Log.i(TAG, "Created id request of length=" + msglen);
            return byteBuffer;
        }
        catch (UnsupportedEncodingException excep)
        {
            Log.e(TAG, "shareDevicTknMsg Unsupported encoding UTF-8 " + excep.getMessage(), excep);

        }
        catch (Exception excp)
        {
            Log.e (TAG, "shareDevicTknMsg Caught exception " + excp.getMessage(), excp);
        }
        return null;
    }

    public static ByteBuffer createIdRequest(String androidId)
    {

        if (APP_ID > SMARTMSG_ID)
        {
            return createIdRequestAppId(androidId);
        }
        else
        {
            return createIdRequestNoAppId(androidId);
        }

    }

    public static ByteBuffer getRemoteHostPortMsg(long shareId, int appId)
    {
        try {
            int msglen = 3*4 + 8;
            if (APP_ID > SMARTMSG_ID)
            {
                msglen += 4;
            }
            ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(msglen);
            if (APP_ID > SMARTMSG_ID)
            {
                byteBuffer.putInt(GET_REMOTE_HOST_1_MSG);
                byteBuffer.putInt(APP_ID);
            }
            else
            {
                byteBuffer.putInt(GET_REMOTE_HOST_MSG);
            }
            byteBuffer.putInt(appId);
            byteBuffer.putLong(shareId);
            return byteBuffer;
        }
        catch (Exception excp)
        {
            Log.e (TAG, "shareDevicTknMsg Caught exception " + excp.getMessage(), excp);
        }
        return null;
    }
    public static ByteBuffer shareDevicTknMsg(long shareId, String deviceTkn)
    {
        try
        {
            int devTknLen = deviceTkn.getBytes("UTF-8").length+1;
            String  os = "android";
            int osLen = os.getBytes("UTF-8").length+1;
            int msglen = osLen + devTknLen + 16;
            if (APP_ID > SMARTMSG_ID)
            {
                msglen += 4;
            }
            Log.d(TAG, "SharedDevice token msg osLen=" + osLen+ " devTknLen=" + devTknLen + " msglen=" +msglen);
            ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(msglen);
            if (APP_ID > SMARTMSG_ID)
            {
                byteBuffer.putInt(STORE_DEVICE_TKN_1_MSG);
                byteBuffer.putInt(APP_ID);
            }
            else
            {
                byteBuffer.putInt(STORE_DEVICE_TKN_MSG);
            }
            byteBuffer.putLong(shareId);
            byteBuffer.put(deviceTkn.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);
            byteBuffer.put(os.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);
            return byteBuffer;
        }
        catch (UnsupportedEncodingException excep)
        {
            Log.e(TAG, "shareDevicTknMsg Unsupported encoding UTF-8 " + excep.getMessage(), excep);

        }
        catch (Exception excp)
        {
            Log.e (TAG, "shareDevicTknMsg Caught exception " + excp.getMessage(), excp);
        }
        return null;
    }

    public static ByteBuffer updateFriendListRequest (long shareId, String frndLst)
    {
        try
        {
        int frndLen = frndLst.getBytes("UTF-8").length +1;
        int msglen = frndLen + 16;
        if (APP_ID > SMARTMSG_ID)
        {
            msglen += 4;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(msglen);
        if (APP_ID > SMARTMSG_ID)
        {
            byteBuffer.putInt(STORE_FRIEND_LIST_1_MSG);
            byteBuffer.putInt(APP_ID);
        }
        else
        {
            byteBuffer.putInt(STORE_FRIEND_LIST_MSG);
        }

        byteBuffer.putLong(shareId);
        byteBuffer.put(frndLst.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);

        return byteBuffer;
        }
        catch (UnsupportedEncodingException excep)
        {
            Log.e(TAG, "updateFriendListRequest Unsupported encoding UTF-8 " + excep.getMessage(), excep);

        }
        catch (Exception excp)
        {
            Log.e (TAG, "updateFriendListRequest Caught exception " + excp.getMessage(), excp);
        }
        return null;
    }

    public static ByteBuffer shouldDownLoadMsg(long shareId, String picName, boolean download)
    {
        try
        {
            Log.i(TAG, "shouldDownloadmsg shareId=" + shareId + " picName=" + picName + " download=" + download);
            int picnamelen = picName.getBytes("UTF-8").length + 1;
            int msglen = 20 + picnamelen;
            if (APP_ID > SMARTMSG_ID)
            {
                msglen += 4;
            }
            ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(msglen);
            if (APP_ID > SMARTMSG_ID)
            {
                byteBuffer.putInt(SHOULD_DOWNLOAD_1_MSG);
                byteBuffer.putInt(APP_ID);
            }
            else
            {
                byteBuffer.putInt(SHOULD_DOWNLOAD_MSG);
            }
            byteBuffer.putLong(shareId);
            int dwld=0;
            if (download)
                dwld = 1;
            byteBuffer.putInt(dwld);
            byteBuffer.put(picName.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);
            return byteBuffer;
        }
        catch (UnsupportedEncodingException excep)
        {
            Log.e(TAG, "getItemsMsg Unsupported encoding UTF-8 " + excep.getMessage(), excep);

        }
        catch (Exception excp)
        {
            Log.e (TAG, "getItemsMsg Caught exception " + excp.getMessage(), excp);
        }
        return null;
    }

    public static ByteBuffer picDoneMsg (Context ctxt, long shareId)
    {
        try
        {
            SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
            String picName = sharing.getString("PicName", "NoName");
            int picnamelen = picName.getBytes("UTF-8").length + 1;
            int msglen = 24 + picnamelen;
            ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(msglen);
            byteBuffer.putInt(PIC_DONE_MSG);
            byteBuffer.putLong(shareId);
            byteBuffer.putLong(sharing.getLong("PicShareId", 0));
            byteBuffer.put(picName.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);
            return byteBuffer;
        }
        catch (UnsupportedEncodingException excep)
        {
            Log.e(TAG, "getItemsMsg Unsupported encoding UTF-8 " + excep.getMessage(), excep);

        }
        catch (Exception excp)
        {
            Log.e (TAG, "getItemsMsg Caught exception " + excp.getMessage(), excp);
        }
        return null;
    }

    public static ByteBuffer getPurchasesMsg(long shareId, String androidId)
    {
        try
        {
            int idLen = androidId.getBytes("UTF-8").length+1;
            int msglen = 4 + 4 + 4 + 8 + 4 +  idLen;
            //msgLen + msgId + appId + shareId + idLen + pIdLen + androidId + productId
            ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(msglen);
            byteBuffer.putInt(GET_PURCHASES);
            byteBuffer.putInt(APP_ID);
            byteBuffer.putLong(shareId);
            byteBuffer.putInt(idLen);
            byteBuffer.put(androidId.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);
            Log.i(TAG, "Created GET_PURCHASES request of length=" + msglen);
        }
        catch (UnsupportedEncodingException excep)
        {
            Log.e(TAG, "getStorePurchaseMsg Unsupported encoding UTF-8 " + excep.getMessage(), excep);

        }
        catch (Exception excp)
        {
            Log.e (TAG, " Caught exception getStorePurchaseMsg" + excp.getMessage(), excp);

        }
        return null;
    }

    public static ByteBuffer storePurchasedMsg(long shareId, String androidId, String productId)
    {
        try
        {
            int idLen = androidId.getBytes("UTF-8").length+1;
            int pIdLen = productId.getBytes("UTF-8").length+1;
            int msglen = 4 + 4 + 4 + 8 + 4 + 4+ idLen + pIdLen;
            //msgLen + msgId + appId + shareId + idLen + pIdLen + androidId + productId
            ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(msglen);
            byteBuffer.putInt(STORE_PURCHASED);
            byteBuffer.putInt(APP_ID);
            byteBuffer.putLong(shareId);
            byteBuffer.putInt(idLen);
            byteBuffer.putInt(pIdLen);
            byteBuffer.put(androidId.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);
            byteBuffer.put(productId.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);
            Log.i(TAG, "Created STORE_PURCHASED request of length=" + msglen);
        }
        catch (UnsupportedEncodingException excep)
        {
            Log.e(TAG, "getStorePurchaseMsg Unsupported encoding UTF-8 " + excep.getMessage(), excep);

        }
        catch (Exception excp)
        {
            Log.e (TAG, " Caught exception getStorePurchaseMsg" + excp.getMessage(), excp);

        }
        return null;
    }

    public static ByteBuffer getItemsMsg(Context ctxt, long shareId)
    {
        try
        {
            String uuid = "Android";
            SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
            String picName = sharing.getString("PicName", "NoName");
            int uuidLen = uuid.getBytes("UTF-8").length +1;
            int msglen = uuidLen + 36 + picName.getBytes("UTF-8").length + 1;
            if (APP_ID > SMARTMSG_ID)
            {
                msglen += 4;
            }
            ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(msglen);
            if (APP_ID > SMARTMSG_ID)
            {
                byteBuffer.putInt(GET_ITEMS_1_MSG);
                byteBuffer.putInt(APP_ID);
            }
            else
            {
                byteBuffer.putInt(GET_ITEMS_MSG);
            }
            byteBuffer.putLong(shareId);
            byteBuffer.put(uuid.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);

            long picLen = sharing.getLong("PicLen", 0);
            long picStored = sharing.getLong("PicLenStored", 0);
            int picRemaining = (int) (picLen - picStored);
            if (picRemaining < 0)
                picRemaining = 0;
           byteBuffer.putInt(picRemaining);
            byteBuffer.put(picName.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);
            byteBuffer.putLong(sharing.getLong("PicShareId", 0));
            byteBuffer.putLong(sharing.getLong("MaxShareId", 0));
            return byteBuffer;
        }
        catch (UnsupportedEncodingException excep)
        {
            Log.e(TAG, "getItemsMsg Unsupported encoding UTF-8 " + excep.getMessage(), excep);

        }
        catch (Exception excp)
        {
            Log.e (TAG, "getItemsMsg Caught exception " + excp.getMessage(), excp);
        }
        return null;
    }

public static ByteBuffer sharePicMetaDataMsg(long shareId, String picUrl, int picLength, String picMetaStr)
{
    try
    {
       String picName =  picUrl.substring(picUrl.lastIndexOf('/')+1);
       String objName = picMetaStr.substring(picMetaStr.lastIndexOf(';')+1);
        if (picName == null || objName == null)
            return null;
        picName += ";";
        picName += objName;
        int nameLen = picName.getBytes("UTF-8").length +1;
        String picMetaStr1 = picMetaStr.substring(0, picMetaStr.lastIndexOf(';')+1);
        int metaStrLen = picMetaStr1.getBytes("UTF-8").length +1;
      //  int msglen = 5*sizeof(int) + nameLen  + sizeof(long long) + metaStrLen;
        int msglen = 28 + nameLen + metaStrLen;
        if (APP_ID > SMARTMSG_ID)
        {
            msglen += 4;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(msglen);
        if (APP_ID > SMARTMSG_ID)
        {
            byteBuffer.putInt(PIC_METADATA_1_MSG);
            byteBuffer.putInt(APP_ID);
        }
        else
        {
            byteBuffer.putInt(PIC_METADATA_MSG);
        }
        byteBuffer.putLong( shareId);

        byteBuffer.putInt(nameLen);
        byteBuffer.put(picName.getBytes("UTF-8"));
        byteBuffer.put((byte)0x00);

        byteBuffer.putInt(picLength);
        byteBuffer.putInt(metaStrLen);
        byteBuffer.put(picMetaStr1.getBytes("UTF-8"));
        byteBuffer.put((byte)0x00);
        return byteBuffer;

    }
    catch (UnsupportedEncodingException excep)
    {
        Log.e(TAG, "sharePicMetaDataMsg Unsupported encoding UTF-8 " + excep.getMessage(), excep);

    }
    catch (Exception excp)
    {
        Log.e (TAG, "sharePicMetaDataMsg Caught exception " + excp.getMessage(), excp);
    }
    return null;
}

    private static ByteBuffer shareCmnItemMsg (long shareId, String name, String item, int msgId)
    {
        try {
            int nameLen = name.getBytes("UTF-8").length+1;
            int listLen = item.getBytes("UTF-8").length+1;
            int msglen = 16 + nameLen + listLen + 8;
            if (APP_ID > SMARTMSG_ID)
            {
                msglen += 4;
            }
            ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(msglen);
            byteBuffer.putInt(msgId);
            if (APP_ID > SMARTMSG_ID)
            {
                byteBuffer.putInt(APP_ID);
            }
            byteBuffer.putLong(shareId);
            byteBuffer.putInt(nameLen);;
            byteBuffer.putInt(listLen);
            byteBuffer.put(name.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);
            byteBuffer.put(item.getBytes("UTF-8"));
            byteBuffer.put((byte)0x00);
            return byteBuffer;
        }catch (UnsupportedEncodingException excep)
        {
            Log.e(TAG, "shareCmnItemMsg Unsupported encoding UTF-8 " + excep.getMessage(), excep);

        }
        catch (Exception excp)
        {
            Log.e (TAG, "shareCmnItemMsg Caught exception " + excp.getMessage(), excp);
        }
        return null;
    }

    public static ByteBuffer  shareTemplItemMsg (long shareId, String name, String item)
    {
        return shareCmnItemMsg(shareId, name, item, SHARE_TEMPL_ITEM_MSG);
    }



public static ByteBuffer shareItemMsg(long shareId, String name, String item)
{
    if (APP_ID > SMARTMSG_ID)
    {
        return shareCmnItemMsg(shareId, name, item, SHARE_ITEM_1_MSG);
    }
   return shareCmnItemMsg(shareId, name, item, SHARE_ITEM_MSG);
}

}
