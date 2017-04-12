package com.rekhaninan.common;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import static com.rekhaninan.common.Constants.GET_SHARE_ID_MSG;
import static com.rekhaninan.common.Constants.LOCATION_DATA_EXTRA;
import static com.rekhaninan.common.Constants.PIC_METADATA_MSG;
import static com.rekhaninan.common.Constants.PIC_MSG;
import static com.rekhaninan.common.Constants.SHARE_ITEM_MSG;
import static com.rekhaninan.common.Constants.STORE_FRIEND_LIST_MSG;

/**
 * Created by ninanthomas on 2/13/17.
 */

public class MessageTranslator {

    private static  String TAG="MessageTranslator";

    public static ByteBuffer sharePicMsg(byte[] msg, int len)
    {
        try {
            int msglen = len + 8;
            ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
            byteBuffer.putInt(0, msglen);
            byteBuffer.putInt(4, PIC_MSG);
            byteBuffer.put(msg, 0, len);
            return byteBuffer;
        }
        catch (Exception excp)
        {
            Log.e (TAG, "Caught exception " + excp.getMessage());
        }
        return null;
    }

    public static ByteBuffer createIdRequest()
    {
        int tridLen = 8;
        long trid = 1000;
        int msglen =  tridLen + 8;
        ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
        byteBuffer.putInt(0, msglen);
        byteBuffer.putInt(4, GET_SHARE_ID_MSG);
        byteBuffer.putLong(8, trid);
        return byteBuffer;

    }

    public static ByteBuffer updateFriendListRequest (long shareId, String frndLst)
    {
        try
        {
        int frndLen = frndLst.length() +1;
        int msglen = frndLen + 16;
        ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
        byteBuffer.putInt(0, msglen);
        byteBuffer.putInt(4, STORE_FRIEND_LIST_MSG);
        byteBuffer.putLong(8, shareId);
        byteBuffer.put(frndLst.getBytes("UTF-8"), 16, frndLen - 1);
        byteBuffer.putChar(16+frndLen-1, '\0');
        return byteBuffer;
        }
        catch (UnsupportedEncodingException excep)
        {
            Log.e(TAG, "Unsupported encoding UTF-8 " + excep.getMessage());

        }
        catch (Exception excp)
        {
            Log.e (TAG, "Caught exception " + excp.getMessage());
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
        int nameLen = picName.length() +1;
        String picMetaStr1 = picMetaStr.substring(0, picMetaStr.lastIndexOf(';')+1);
        int metaStrLen = picMetaStr1.length() +1;
      //  int msglen = 5*sizeof(int) + nameLen  + sizeof(long long) + metaStrLen;
        int msglen = 28 + nameLen + metaStrLen;
        ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
        byteBuffer.putInt(0, msglen);
        byteBuffer.putInt(4, PIC_METADATA_MSG);
        byteBuffer.putLong(8, shareId);
        int namelenoffset = 16;
        byteBuffer.putInt(namelenoffset, nameLen);
        int nameoffset = namelenoffset + 4;
        byteBuffer.put(picName.getBytes("UTF-8"), nameoffset, nameLen - 1);
        byteBuffer.putChar(nameoffset+nameLen-1, '\0');
        int lenghtoffset = nameoffset + nameLen;
        byteBuffer.putInt(lenghtoffset, picLength);
        int metastrlenoffset = lenghtoffset + 4;
        byteBuffer.putInt(metastrlenoffset, metaStrLen);
        int metastroffset = metastrlenoffset+4;
        byteBuffer.put(picMetaStr1.getBytes("UTF-8"), metastroffset, metaStrLen - 1);
        byteBuffer.putChar(metastroffset+metaStrLen-1, '\0');
        return byteBuffer;

    }
    catch (UnsupportedEncodingException excep)
    {
        Log.e(TAG, "Unsupported encoding UTF-8 " + excep.getMessage());

    }
    catch (Exception excp)
    {
        Log.e (TAG, "Caught exception " + excp.getMessage());
    }
    return null;
}

public static ByteBuffer shareItemMsg(long shareId, String name, String item)
{

    try {
        int nameLen = name.length() + 1;
        int listLen = item.length() + 1;
        int msglen = 16 + nameLen + listLen + 8;
        ByteBuffer byteBuffer = ByteBuffer.allocate(msglen);
        byteBuffer.putInt(0, msglen);
        byteBuffer.putInt(4, SHARE_ITEM_MSG);
        byteBuffer.putLong(8, shareId);
        int namelenoffset = 2 * 4 + 8;
        byteBuffer.putInt(namelenoffset, nameLen);
        int listlenoffset = namelenoffset + 4;
        byteBuffer.putInt(listlenoffset, listLen);
        int nameoffset = listlenoffset + 4;
        byteBuffer.put(name.getBytes("UTF-8"), nameoffset, nameLen - 1);
        byteBuffer.putChar(nameoffset+nameLen-1, '\0');
        int shareoff = nameoffset+nameLen;
        byteBuffer.put(item.getBytes("UTF-8"), shareoff, item.length());
        byteBuffer.putChar(shareoff + item.length(), '\0');
        return byteBuffer;
    }catch (UnsupportedEncodingException excep)
    {
        Log.e(TAG, "Unsupported encoding UTF-8 " + excep.getMessage());

    }
    catch (Exception excp)
    {
        Log.e (TAG, "Caught exception " + excp.getMessage());
    }
    return null;
}

}
