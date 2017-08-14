package com.rekhaninan.common;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.AUTOSPREE_ADD_ITEM;
import static com.rekhaninan.common.Constants.AUTOSPREE_EDIT_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_EDIT_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_ADD_ITEM;
import static com.rekhaninan.common.Constants.GET_EASYGROC_LIST_MSG;
import static com.rekhaninan.common.Constants.GET_SHARE_ID_RPLY_MSG;
import static com.rekhaninan.common.Constants.MSG_AGGR_BUF_LEN;
import static com.rekhaninan.common.Constants.OPENHOUSES;
import static com.rekhaninan.common.Constants.OPENHOUSES_ADD_ITEM;
import static com.rekhaninan.common.Constants.OPENHOUSES_EDIT_ITEM;
import static com.rekhaninan.common.Constants.PIC_METADATA_MSG;
import static com.rekhaninan.common.Constants.PIC_MSG;
import static com.rekhaninan.common.Constants.SHARE_ITEM_MSG;
import static com.rekhaninan.common.Constants.SHARE_TEMPL_ITEM_MSG;
import static com.rekhaninan.common.Constants.STORE_TRNSCTN_ID_RPLY_MSG;

/**
 * Created by ninanthomas on 2/23/17.
 */

public class MessageDecoder {

    private boolean start;


    private ByteBuffer aggrbuf;
    private ByteBuffer decodebuf;
    private final String TAG = "MessageDecoder";

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    private String app_name;

    public MessageDecoder()
    {
        start = true;

        aggrbuf = ByteBuffer.allocate(MSG_AGGR_BUF_LEN);
        decodebuf  = ByteBuffer.allocate(MSG_AGGR_BUF_LEN);
    }

   private boolean processFreshMessage (ByteBuffer buffer)
    {
        boolean bMore = false;
        boolean next =true;
        int remaining = buffer.position();

        while (next)
        {
            if (remaining < 4) //4 -> sizeof (int)
            {
                if(bufferOverFlowCheck(remaining))
                    break;
                aggrbuf.put(buffer.array(), buffer.position() - remaining, remaining);
                bMore = true;
                start = false;
                break;
            }
            int len = buffer.getInt(0);
            int mtyp = buffer.getInt(4);
            Log.d(TAG, "Received message of length=" + len + " remaining=" + remaining + " msgTyp=" + mtyp);
            if (remaining == len)
            {
                decodebuf.clear();
                decodebuf.order(ByteOrder.LITTLE_ENDIAN);
                decodebuf.put(buffer.array(), buffer.position()-remaining, remaining);
                decodeMessage(decodebuf,  remaining);
                break;
            }
            else if (remaining < len)
            {
                if(bufferOverFlowCheck(remaining))
                    break;
                aggrbuf.put(buffer.array(), buffer.position() - remaining, remaining);
                bMore = true;
                start = false;
                break;

            }
            else
            {
                decodebuf.clear();
                decodebuf.order(ByteOrder.LITTLE_ENDIAN);
                decodebuf.put(buffer.array(), buffer.position()-remaining, len);
                decodeMessage (decodebuf, len);
                remaining -= len;
            }
        }
        return bMore;

    }

    boolean processShareIdMessage(ByteBuffer buffer, int mlen)
    {
        int offset = 8;
        int shareId =0;
        shareId = (int) buffer.getLong(offset);
        ShareMgr.getInstance().setShare_id(shareId);
        return true;
    }

    boolean processStoreIdMessage (ByteBuffer buffer, int mlen)
    {
        return true;
    }

    boolean processPicMetaDataMessage(ByteBuffer buffer, int mlen)
    {

        long shareId;
        shareId = buffer.getLong(8);

        int picNameLenOffset = 2*4 + 8;
        int picNameLen;
        long picLen;
        picNameLen = buffer.getInt(picNameLenOffset);
        int picNameOffset = picNameLenOffset+4;
        String picNameArr = new String(buffer.array(), picNameOffset, picNameLen-1);
        //NSString *picNameArr = [NSString stringWithCString:(buffer + picNameOffset) encoding:NSASCIIStringEncoding];
        int picLenOffset = picNameOffset+picNameLen;
        picLen = buffer.getLong(picLenOffset);
        String[] arr = picNameArr.split(";");

        int cnt = arr.length;
        if (cnt != 2)
        {

            Log.e(TAG, "Invalid picNameArr in message decoder");
            return false;
        }

        ShareMgr.getInstance().setPicDetails(shareId, arr[0], arr[1], picLen);
        return true;
    }

    boolean processPicMessage(ByteBuffer buffer, int mlen)
    {
        int msglen = buffer.getInt(0);
        int offset = 8;
        msglen -= offset;
        ShareMgr.getInstance().storePicData(buffer, offset, msglen);
        return true;
    }

    boolean processShareTemplItemMessage (ByteBuffer buffer, int mlen)
    {
        int namelen = buffer.getInt(8); // 8== 2*sizeof(int)
        int nameoffset = 16; // 4 * sizeof(int)
        String name = new String (buffer.array(), nameoffset, namelen-1);
        int listlenoffset = 20;
        int listlen = buffer.getInt(listlenoffset);
        int listoffset = namelen + 24;
        String list = new String(buffer.array(), listoffset, listlen-1);
        boolean bRet = false;
        switch (app_name)
        {

            case EASYGROC:
                bRet =  decodeAndStoreEasyGrocTemplItem(name, list);
                break;

            default:
                break;

        }
        if (bRet)
            ShareMgr.getInstance().refreshMainVw();
        return bRet;

    }

    boolean processShareItemMessage (ByteBuffer buffer, int mlen)
    {
        int namelen = buffer.getInt(8); // 8== 2*sizeof(int)
        int nameoffset = 16; // 4 * sizeof(int)
        String name = new String (buffer.array(), nameoffset, namelen-1);
        int listlenoffset = 20;
        int listlen = buffer.getInt(listlenoffset);
        int listoffset = namelen + 24;
        String list = new String(buffer.array(), listoffset, listlen-1);
        boolean bRet = false;
        switch (app_name)
        {
            case OPENHOUSES:
                bRet = decodeAndStoreOHItem(list);
            break;

            case AUTOSPREE:
                bRet =  decodeAndStoreASpreeItem(list);
            break;

            case EASYGROC:
                bRet =  decodeAndStoreEasyGrocItem(name, list, true);
            break;

            default:
                break;

        }
        if (bRet)
            ShareMgr.getInstance().refreshMainVw();
        return bRet;
    }

    boolean decodeAndStoreEasyGrocTemplItem(String share_name, String list)
    {
        String[] listcomps = list.split(":;]:;");
        int comps = listcomps.length;
        long share_id =0;
        for (int j=0; j < comps; ++j)
        {
            if (j==0)
            {
                String[] shareIdArr = listcomps[j].split(":");
                share_id = Long.parseLong(shareIdArr[1]);

                continue;
            }
            String[] pArr = listcomps[j].split("]:;");
            int cnt = pArr.length;
            String adjstedname = share_name;
            if (j ==2)
            {
                adjstedname += ":INV";
            }
            else if (j == 3)
            {
                adjstedname += ":SCRTCH";
            }
            Item nameItem = new Item();
            nameItem.setShare_id(share_id);
            nameItem.setShare_name(adjstedname);
            nameItem.setName(adjstedname);
            Item shareItem = DBOperations.getInstance().shareItemExists(nameItem, EASYGROC_ADD_ITEM);
            if (shareItem != null)
            {

                DBOperations.getInstance().deleteDb(shareItem, EASYGROC_TEMPL_ADD_ITEM);
            }
            for (int i=0; i < cnt; ++i) {
                String[] kvarr = pArr[i].split(":");
                if (kvarr.length != 5)
                    continue;
                Item itm = new Item();
                itm.setShare_id(share_id);
                itm.setShare_name(adjstedname);
                itm.setName(adjstedname);
                itm.setRowno(Integer.parseInt(kvarr[0]));
                itm.setStart_month(Integer.parseInt(kvarr[1]));
                itm.setEnd_month(Integer.parseInt(kvarr[2]));
                itm.setInventory(Integer.parseInt(kvarr[3]));
                itm.setItem(kvarr[4]);
                DBOperations.getInstance().insertDb(itm, EASYGROC_TEMPL_ADD_ITEM);
            }
        }
        return true;
    }
    boolean decodeAndStoreEasyGrocItem(String share_name, String list, boolean bEasy)
    {
        String[] pArr = list.split("]:;");
        int cnt = pArr.length;
        long share_id =0;


        for (int i=0; i < cnt; ++i)
        {
            String[] kvarr = pArr[i].split(":");
            if (kvarr.length != 2)
                continue;
            if (i ==0)
            {
                share_id = Long.parseLong(kvarr[1]);
                Item nameItem = new Item();
                nameItem.setShare_id(share_id);
                nameItem.setShare_name(share_name);
                nameItem.setName(share_name);
                Item shareItem = DBOperations.getInstance().shareItemExists(nameItem, EASYGROC_ADD_ITEM);
                if (shareItem != null)
                {

                    DBOperations.getInstance().deleteDb(shareItem, EASYGROC_DISPLAY_ITEM);
                }



                continue;
            }
            Item itm = new Item();
            itm.setName(share_name);
            itm.setShare_name(share_name);
            itm.setShare_id(share_id);
            itm.setItem(kvarr[1]);
            itm.setRowno(Integer.parseInt(kvarr[0]));
            DBOperations.getInstance().insertDb(itm, EASYGROC_ADD_ITEM);
        }

        return true;
    }

    boolean decodeAndStoreASpreeItem(String list)
    {

        String[] pMainArr = list.split("::]}]::");
        String[] pArr = pMainArr[0].split("]:;");
        int cnt = pArr.length;
        int mcnt = pMainArr.length;
        HashMap<String, String> keyvals = new HashMap<>();
        for (int i=0; i < cnt; ++i)
        {
            String[] kvarr = pArr[i].split(":|:");
            if (kvarr.length != 2)
                continue;
            keyvals.put(kvarr[0], kvarr[1]);

        }
        Item itm = new Item();
        decodeAndStoreCommonItem(keyvals, itm);
        String nameval = keyvals.get("Model");
        if (nameval != null)
            itm.setModel(nameval);
        nameval = keyvals.get("Make");
        if (nameval != null)
            itm.setMake(nameval);
        nameval = keyvals.get("Color");
        if (nameval != null)
            itm.setColor(nameval);
        nameval = keyvals.get("Miles");
        if (nameval != null)
            itm.setMiles(Integer.parseInt(nameval));

        Item existItem = DBOperations.getInstance().shareItemExists(itm, AUTOSPREE_EDIT_ITEM);
        if (existItem == null)
        {
            DBOperations.getInstance().insertDb(itm, AUTOSPREE_ADD_ITEM);
        }
        else
        {
            DBOperations.getInstance().updateDb(itm, AUTOSPREE_EDIT_ITEM);
        }

        if (mcnt == 2 && pMainArr[1] != null && pMainArr[1].length() > 0)
        {
            decodeAndStoreEasyGrocItem(itm.getName(), pMainArr[1], false);
        }

        return true;
    }

    boolean decodeAndStoreOHItem(String list)
    {
        String[] pMainArr = list.split("::]}]::");
        String[] pArr = pMainArr[0].split("]:;");
        int cnt = pArr.length;
        int mcnt = pMainArr.length;

        HashMap<String, String> keyvals = new HashMap<>();
        for (int i=0; i < cnt; ++i)
        {
            String[] kvarr = pArr[i].split(":|:");
            if (kvarr.length != 2)
                continue;
            keyvals.put(kvarr[0], kvarr[1]);

        }
        Item itm = new Item();
        decodeAndStoreCommonItem(keyvals, itm);
        String nameval = keyvals.get("Area");
        if (nameval != null)
        {
            itm.setArea(Double.parseDouble(nameval));
        }

        nameval = keyvals.get("Beds");
        if (nameval != null)
        {
            itm.setBeds(Double.parseDouble(nameval));
        }

        nameval = keyvals.get("Baths");
        if (nameval != null)
        {
            itm.setBaths(Double.parseDouble(nameval));
        }
        Item existItem = DBOperations.getInstance().shareItemExists(itm, OPENHOUSES_EDIT_ITEM);
        if (existItem == null)
        {
            DBOperations.getInstance().insertDb(itm, OPENHOUSES_ADD_ITEM);
        }
        else
        {
            DBOperations.getInstance().updateDb(itm, OPENHOUSES_EDIT_ITEM);
        }
        if (mcnt == 2 && pMainArr[1] != null && pMainArr[1].length() > 0)
        {
            decodeAndStoreEasyGrocItem(itm.getName(), pMainArr[1], false);
        }

        return true;
    }

    boolean decodeAndStoreCommonItem( HashMap<String, String> keyvals , Item itm)
    {
        String nameval = keyvals.get("Name");
        if (nameval != null) {
            itm.setName(nameval);
            itm.setShare_name(nameval);
        }
        nameval = keyvals.get("Price");
         if (nameval != null)
             itm.setPrice(Double.parseDouble(nameval));
        nameval = keyvals.get("Year");
        if (nameval != null)
            itm.setYear(Integer.parseInt(nameval));
        nameval = keyvals.get("Notes");
        if (nameval != null)
            itm.setNotes(nameval);
        nameval = keyvals.get("Street");
        if (nameval != null)
            itm.setStreet(nameval);
        nameval = keyvals.get("City");
        if (nameval != null)
            itm.setCity(nameval);
        nameval = keyvals.get("State");
        if (nameval != null)
            itm.setState(nameval);
        nameval = keyvals.get("PostalCode");
        if (nameval != null)
            itm.setZip(nameval);
        nameval = keyvals.get("latitude");
        if (nameval != null)
            itm.setLatitude(Double.parseDouble(nameval));
        nameval = keyvals.get("longitude");
        if (nameval != null)
            itm.setLongitude(Double.parseDouble(nameval));
        nameval = keyvals.get("shareId");
        if (nameval != null)
            itm.setShare_id(Long.parseLong(nameval));
            return true;
        }

        boolean decodeMessage(ByteBuffer buffer, int mlen)
        {

            boolean bRet = true;
            int msgTyp;
            msgTyp = buffer.getInt(4);

            Log.i(TAG, "Decoding received message of type=" + msgTyp);
            switch (msgTyp)
            {

                case GET_SHARE_ID_RPLY_MSG:
                    bRet = processShareIdMessage(buffer, mlen);
                    break;

                case STORE_TRNSCTN_ID_RPLY_MSG:
                {
                    bRet = processStoreIdMessage (buffer, mlen);
                }
                break;

                case PIC_METADATA_MSG:
                {
                    bRet = processPicMetaDataMessage(buffer, mlen);
                }
                break;


                case PIC_MSG:
                {
                    bRet = processPicMessage(buffer, mlen);
                 }
                break;

                case SHARE_ITEM_MSG:
                {
                     bRet = processShareItemMessage(buffer, mlen);
                }
                break;

                case SHARE_TEMPL_ITEM_MSG:
                {
                    bRet = processShareTemplItemMessage(buffer, mlen);
                }


                default:
                    bRet = true;
                break;
        }

        return bRet;
    }

    boolean bufferOverFlowCheck ( int remaining)
    {
        if (MSG_AGGR_BUF_LEN - aggrbuf.position()  < remaining)
        {
            Log.e(TAG, "Invalid message received remaining=" + remaining + " bufIndx=" + aggrbuf.position());
            aggrbuf.clear();
            start = true;
            return true;
        }
        return false;
    }

    private boolean processFragmentedMessage (ByteBuffer buffer, int mlen, int remaining, int len)
    {
        boolean bMore = false;
        boolean next =true;
        while (next)
        {
            if (remaining == len-aggrbuf.position())
            {
                if(bufferOverFlowCheck(remaining))
                break;
                aggrbuf.put(buffer.array(), mlen-remaining, remaining);
                decodeMessage(aggrbuf, len);
                aggrbuf.clear();
                start = true;
                break;
            }
            else if (remaining < (len - aggrbuf.position()))
            {
                if(bufferOverFlowCheck(remaining))
                    break;
                aggrbuf.put(buffer.array(), mlen-remaining, remaining);
                bMore = true;
                break;

            }
            else
            {
                if(bufferOverFlowCheck(len-aggrbuf.position()))
                    break;
                aggrbuf.put(buffer.array(), mlen-remaining, len-aggrbuf.position());
                decodeMessage(aggrbuf, len);
                remaining -= len - aggrbuf.position();
                aggrbuf.clear();
            }
        }

        return bMore;

    }

    boolean processMessage(ByteBuffer buffer)
    {
        boolean bMore = false;

        if (start)
        {
            bMore = processFreshMessage(buffer);
        }
        else
        {
            if (aggrbuf.position() >= 4) //4 is sizeof (int)
            {
                int len = aggrbuf.getInt(0);

                int remaining = buffer.position();
                bMore = processFragmentedMessage(buffer, buffer.position(), remaining, len);
            }
            else
            {
                int len = 0;
                int remaining = buffer.position();
                if (remaining + aggrbuf.position() < 4)
                {
                    bMore = true;
                    aggrbuf.put(buffer.array(), 0, remaining);

                }
                else
                {
                    int lenRmng = 4 - aggrbuf.position();
                    aggrbuf.put(buffer.array(), 0, lenRmng);
                    remaining -= lenRmng;
                    len = aggrbuf.getInt(0);

                  //  bMore = [self processFragmentedMessage:buffer msglen:mlen remain:remaining length:len];
                    bMore = processFragmentedMessage(buffer, buffer.position(), remaining, len);
                }

            }

        }
        return bMore;
    }

}
