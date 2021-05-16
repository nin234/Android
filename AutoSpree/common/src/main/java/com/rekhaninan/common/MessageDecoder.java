package com.rekhaninan.common;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.AUTOSPREE_ADD_ITEM;
import static com.rekhaninan.common.Constants.AUTOSPREE_EDIT_ITEM;
import static com.rekhaninan.common.Constants.CONTACTS_ITEM_ADD_NOVWTYP;
import static com.rekhaninan.common.Constants.CONTACTS_ITEM_NOVWTYP;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.EASYGROC_ADD_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_DISPLAY_ITEM;
import static com.rekhaninan.common.Constants.EASYGROC_TEMPL_ADD_ITEM;
import static com.rekhaninan.common.Constants.FRIEND_LIST_MSG;
import static com.rekhaninan.common.Constants.FRNDLSTMSGFRNDSEPERATOR;
import static com.rekhaninan.common.Constants.FRNDLSTMSGNAMESHIDSEPERATOR;
import static com.rekhaninan.common.Constants.GET_PURCHASES_REPLY;
import static com.rekhaninan.common.Constants.GET_SHARE_ID_RPLY_MSG;
import static com.rekhaninan.common.Constants.ITEMSEPARATOR;
import static com.rekhaninan.common.Constants.KEYVALSEPARATORREGEX;
import static com.rekhaninan.common.Constants.MSG_AGGR_BUF_LEN;
import static com.rekhaninan.common.Constants.NSHARELIST;
import static com.rekhaninan.common.Constants.OPENHOUSES;
import static com.rekhaninan.common.Constants.OPENHOUSES_ADD_ITEM;
import static com.rekhaninan.common.Constants.OPENHOUSES_EDIT_ITEM;
import static com.rekhaninan.common.Constants.PIC_METADATA_MSG;
import static com.rekhaninan.common.Constants.PIC_MSG;
import static com.rekhaninan.common.Constants.SHARE_ID_REMOTE_HOST_MSG;
import static com.rekhaninan.common.Constants.SHARE_ITEM_MSG;
import static com.rekhaninan.common.Constants.SHARE_TEMPL_ITEM_MSG;
import static com.rekhaninan.common.Constants.SHOULD_UPLOAD_MSG;
import static com.rekhaninan.common.Constants.STORE_DEVICE_TKN_RPLY_MSG;
import static com.rekhaninan.common.Constants.STORE_TRNSCTN_ID_RPLY_MSG;
import static com.rekhaninan.common.Constants.TEMPLLISTSEPERATOR;
import static com.rekhaninan.common.Constants.UPDATE_MAX_SHARE_ID_MSG;

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
        aggrbuf.order(ByteOrder.LITTLE_ENDIAN);
        decodebuf = ByteBuffer.allocate(MSG_AGGR_BUF_LEN);
        decodebuf.order(ByteOrder.LITTLE_ENDIAN);
    }

   private boolean processFreshMessage (ByteBuffer buffer)
    {
        boolean bMore = false;
        boolean next =true;
        int remaining = buffer.position();

        while (next)
        {
            Log.i(TAG, "processFreshMsg buffer.position="+ buffer.position()  + " aggrbuf.posn" +
                    aggrbuf.position() + " remaining=" + remaining + " start=" + start);
            if (remaining < 4) //4 -> sizeof (int)
            {
                if(bufferOverFlowCheck(remaining))
                    break;
                aggrbuf.put(buffer.array(), buffer.position() - remaining, remaining);
                bMore = true;
                start = false;
                break;
            }
            int len = buffer.getInt(buffer.position()-remaining);
            int mtyp = buffer.getInt(buffer.position()-remaining+4);
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

    boolean processShouldUploadMessage(ByteBuffer buffer, int mlen)
    {
        int upload = buffer.getInt(20);
        int picOffset = buffer.getInt(24);
        ShareMgr.getInstance().setUploadPicOffset(picOffset);
        ShareMgr.getInstance().processShouldUploadMessage(upload);

        return true;
    }

    boolean processStoreIdMessage (ByteBuffer buffer, int mlen)
    {
        return true;
    }

    boolean processRemoteShareIdHostPortMessage(ByteBuffer buffer, int mlen)
    {
        int hostLenOffset = 8;
        int hostlen = buffer.getInt(hostLenOffset);
        int hostOffset = hostLenOffset + 4;
        String host = new String(buffer.array(), hostOffset, hostlen-1);
        int portOffset = hostOffset + hostlen;
        int port = buffer.getInt(portOffset);
        ShareMgr.getInstance().setHostPort(host, port);

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
        int picOffset = picLenOffset + 8; // 8 sizeof long
        int picsofar = buffer.getInt(picOffset);
        String[] arr = picNameArr.split(";");

        int cnt = arr.length;
        if (cnt != 2)
        {

            Log.d(TAG, "Invalid picNameArr in message decoder=" + picNameArr);
            return false;
        }

        ShareMgr.getInstance().setPicDetails(shareId, arr[0], arr[1], picLen, picsofar);
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
        int namelen = buffer.getInt(16); // 8== 2*sizeof(int)
        int nameoffset = 24; // 4 * sizeof(int)
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


    boolean processUpdateMaxShareIdMessage(ByteBuffer buffer, int mlen)
    {
        int offset = 8;
        int shareId =0;
        shareId = (int) buffer.getLong(offset);
        ShareMgr.getInstance().setMaxShareId(shareId);
        return true;
    }

    boolean processFrndLstMessage(ByteBuffer buffer, int mlen)
    {
        boolean bRet = false;
        try {

            DBOperations.getInstance().deleteAll(CONTACTS_ITEM_NOVWTYP);
            int frndLstLen = buffer.getInt(8); // 8 is offset msgId (int) + msglen(int)
            int frndLstOffset = 12;
            String frndLst = new String(buffer.array(), frndLstOffset, frndLstLen-1);
            String[] frnds = frndLst.split(FRNDLSTMSGFRNDSEPERATOR);

            for (String frnd : frnds)
            {
                String[] shIdName = frnd.split(FRNDLSTMSGNAMESHIDSEPERATOR);
                if (shIdName.length != 2)
                    continue;
                Item itm = new Item();
                itm.setShare_id(Long.parseLong(shIdName[0]));
                itm.setName(shIdName[1]);
                DBOperations.getInstance().insertDb(itm, CONTACTS_ITEM_ADD_NOVWTYP);

            }

        }
        catch (Exception excp)
        {
            Log.e(TAG, "processFrndLstMessage exception  " + excp.getMessage(), excp);
            return false;
        }

        return bRet;
    }

    boolean processGetPurchasesReplyMsg(ByteBuffer buffer, int mlen)
    {
        boolean bRet = true;
        try
        {
            int pidOffset = 24;
            int pidLen = buffer.getInt(20);
            String utf8 = new  String("UTF-8");
            String productId = new String(buffer.array(), pidOffset, pidLen-1, utf8 );
            ShareMgr.getInstance().handleGetPurchasesReplyMsg(productId);
        }
        catch (java.io.UnsupportedEncodingException excp)
        {
          Log.e(TAG, " processGetPurchasesReplyMsg UnsupportedEncodingException " + excp.getMessage(), excp);
          bRet = false;
        }
        catch (Exception excp)
        {
             Log.e(TAG, " processGetPurchasesReplyMsg exception  " + excp.getMessage(), excp);
             bRet = false;
        }

        return bRet;
    }

    boolean processShareItemMessage (ByteBuffer buffer, int mlen)
    {

        boolean bRet = false;
        try {

            String utf8 = new  String("UTF-8");
        int namelen = buffer.getInt(16); // 8== 2*sizeof(int)
        int nameoffset = 24; // 4 * sizeof(int)
        String name = new String (buffer.array(), nameoffset, namelen-1, utf8);
        int listlenoffset = 20;
        int listlen = buffer.getInt(listlenoffset);
        int listoffset = namelen + 24;
        String list = new String(buffer.array(), listoffset, listlen-1, utf8);
        Log.i(TAG, "ShareItem details namelen=" + namelen + " listlen=" + listlen + " nameoffset=" + nameoffset + " listoffset=" + listoffset);
        Log.i(TAG, "Processing shareItem msg name=" + name + " list=" + list + " app_name=" + app_name);

        switch (app_name)
        {
            case OPENHOUSES:
                bRet = decodeAndStoreOHItem(list);
            break;

            case AUTOSPREE:
                bRet =  decodeAndStoreASpreeItem(list);
            break;

            case EASYGROC:
            case NSHARELIST:
                bRet =  decodeAndStoreEasyGrocItem(name, list, true);
            break;

            default:
                break;

        }
        if (bRet)
            ShareMgr.getInstance().refreshMainVw();
        }
        catch (java.io.UnsupportedEncodingException excp)
        {
            Log.e(TAG, "processShareItemMsg UnsupportedEncodingException " + excp.getMessage(), excp);
        }
        catch (Exception excp)
        {
            Log.e(TAG, "processShareItemMsg exception  " + excp.getMessage(), excp);
        }

        return bRet;
    }

    boolean decodeAndStoreEasyGrocTemplItem(String share_name, String list)
    {
        Log.i(TAG, "decoding easygroc templ item for name=" + share_name + " list=" + list);
        String[] listcomps = list.split(TEMPLLISTSEPERATOR);
        int comps = listcomps.length;
        long share_id =0;
        for (int j=0; j < comps; ++j)
        {
            if (listcomps[j] == null || listcomps[j].length() < 1)
                continue;
            Log.i(TAG, "decoding comp j=" + j);
            if (j==0)
            {
                String[] shareIdArr = listcomps[j].split(KEYVALSEPARATORREGEX);
                share_id = Long.parseLong(shareIdArr[1]);

                continue;
            }
            String[] pArr = listcomps[j].split(ITEMSEPARATOR);
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
                String[] kvarr = pArr[i].split(KEYVALSEPARATORREGEX);
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
        Log.i(TAG, "Decoded easygroc templ item");
        return true;
    }

    boolean decodeAndStoreCheckList(String share_name, String list , long share_id)
    {
        Item nameItem = new Item();
        nameItem.setShare_id(share_id);
        nameItem.setShare_name(share_name);
        nameItem.setName(share_name);
        Item shareItem = DBOperations.getInstance().shareItemExists(nameItem, EASYGROC_ADD_ITEM);
        if (shareItem != null)
        {

            DBOperations.getInstance().deleteDb(shareItem, EASYGROC_DISPLAY_ITEM);
        }

        String[] pArr = list.split("]:;");
        int cnt = pArr.length;

        for (int i=0; i < cnt; ++i) {
            String[] kvarr = pArr[i].split(":\\|:");
            if (kvarr.length != 3)
                continue;
            Item itm = new Item();
            itm.setName(share_name);
            itm.setShare_name(share_name);
            itm.setShare_id(share_id);
            itm.setItem(kvarr[2]);
            int selected = Integer.parseInt(kvarr[1]);
            if (selected ==1)
            {
                itm.setSelected(true);
            }
            else
            {
                itm.setSelected(false);
            }
            itm.setRowno(Integer.parseInt(kvarr[0]));
            DBOperations.getInstance().insertDb(itm, EASYGROC_ADD_ITEM);
        }

        return true;
    }

    boolean decodeAndStoreEasyGrocItem(String share_name, String list, boolean bEasy)
    {
        String[] pArr = list.split(ITEMSEPARATOR);
        int cnt = pArr.length;
        long share_id =0;
        Log.d(TAG, "Decoding easygrocitem cnt=" + cnt);

        for (int i=0; i < cnt; ++i)
        {
            String[] kvarr = pArr[i].split(KEYVALSEPARATORREGEX);
            if (kvarr.length != 2)
                continue;
            if (i ==0)
            {

                share_id = Long.parseLong(kvarr[1]);
                Log.d(TAG, "Setting share_id" + share_id + " name=" + share_name);
                Item nameItem = new Item();
                nameItem.setShare_id(share_id);

                nameItem.setShare_name(share_name);
                nameItem.setName(share_name);
                Item shareItem = DBOperations.getInstance().shareItemExists(nameItem, EASYGROC_ADD_ITEM);
                if (shareItem != null)
                {
                    Log.d(TAG, "Deleting exisiting before insert share_id" + share_id + " name=" + share_name);
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
            Log.d(TAG, "Inserting item="+ itm.getItem() + " rowno=" + itm.getRowno());
            DBOperations.getInstance().insertDb(itm, EASYGROC_ADD_ITEM);
        }

        return true;
    }

    boolean decodeAndStoreASpreeItem(String list)
    {

        String[] pMainArr = list.split("::]\\}]::");
        String[] pArr = pMainArr[0].split("]:;");
        Log.i(TAG, "list pMainArr[0]=" + pMainArr[0]);
        int cnt = pArr.length;
        int mcnt = pMainArr.length;
        Log.i(TAG, "cnt=" + cnt + " mcnt=" + mcnt);
        HashMap<String, String> keyvals = new HashMap<>();
        for (int i=0; i < cnt; ++i)
        {
            String[] kvarr = pArr[i].split(":\\|:");
            for (int j=0; j < kvarr.length; ++j)
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

        Log.i(TAG, " Item=" + itm.itmStr());
        Item existItem = DBOperations.getInstance().shareItemExists(itm, AUTOSPREE_EDIT_ITEM);
        if (existItem == null)
        {
            long rightNow = System.currentTimeMillis();
            itm.setAlbum_name(Long.toString(rightNow));
            DBOperations.getInstance().insertDb(itm, AUTOSPREE_ADD_ITEM);
        }
        else
        {
            DBOperations.getInstance().updateDb(itm, AUTOSPREE_EDIT_ITEM);
        }

        if (mcnt == 2 && pMainArr[1] != null && pMainArr[1].length() > 0)
        {
            decodeAndStoreCheckList(itm.getName(), pMainArr[1], itm.getShare_id());

        }

        return true;
    }

    boolean decodeAndStoreOHItem(String list)
    {
        String[] pMainArr = list.split("::]\\}]::");
        String[] pArr = pMainArr[0].split("]:;");
        int cnt = pArr.length;
        int mcnt = pMainArr.length;

        HashMap<String, String> keyvals = new HashMap<>();
        for (int i=0; i < cnt; ++i)
        {
            String[] kvarr = pArr[i].split(":\\|:");
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
            //need to create album directory here
             long rightNow = System.currentTimeMillis();
            itm.setAlbum_name(Long.toString(rightNow));
            DBOperations.getInstance().insertDb(itm, OPENHOUSES_ADD_ITEM);
        }
        else
        {
            DBOperations.getInstance().updateDb(itm, OPENHOUSES_EDIT_ITEM);
        }
        if (mcnt == 2 && pMainArr[1] != null && pMainArr[1].length() > 0)
        {
            decodeAndStoreCheckList(itm.getName(), pMainArr[1], itm.getShare_id());

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
        nameval = keyvals.get("Ratings");
        if (nameval != null)
            itm.setRating(Integer.parseInt(nameval));
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
                break;

                case SHOULD_UPLOAD_MSG:
                {
                    bRet = processShouldUploadMessage(buffer, mlen);
                }
                break;

                case STORE_DEVICE_TKN_RPLY_MSG:
                {
                    ShareMgr.getInstance().updateDeviceTknStatus();
                }
                break;

                case FRIEND_LIST_MSG:
                {
                    bRet = processFrndLstMessage(buffer, mlen);
                }
                break;

                case UPDATE_MAX_SHARE_ID_MSG:
                {
                    bRet = processUpdateMaxShareIdMessage(buffer, mlen);
                }
                break;

                case SHARE_ID_REMOTE_HOST_MSG:
                {
                    bRet = processRemoteShareIdHostPortMessage(buffer, mlen);
                }
                break;

                case GET_PURCHASES_REPLY:
                {
                    bRet = processGetPurchasesReplyMsg(buffer, mlen);
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
            Log.d(TAG, "Invalid message received remaining=" + remaining + " bufIndx=" + aggrbuf.position());
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
	int msglen = len;
        while (next)
        {
            Log.i(TAG, "processFragmentedMsg msglen="+msglen + " mlen=" + mlen + " aggrbuf.posn" +
                    aggrbuf.position() + " remaining=" + remaining + " start=" + start);
            if (remaining == msglen-aggrbuf.position())
            {
                if(bufferOverFlowCheck(remaining))
                break;
                aggrbuf.put(buffer.array(), mlen-remaining, remaining);
                decodeMessage(aggrbuf, msglen);
                aggrbuf.clear();
                aggrbuf.order(ByteOrder.LITTLE_ENDIAN);
                start = true;
                break;
            }
            else if (remaining < (msglen - aggrbuf.position()))
            {
                if(bufferOverFlowCheck(remaining))
                    break;
                aggrbuf.put(buffer.array(), mlen-remaining, remaining);
                bMore = true;
                break;

            }
            else
            {
                if(bufferOverFlowCheck(msglen-aggrbuf.position()))
                    break;
                int bufIndx = aggrbuf.position();
                aggrbuf.put(buffer.array(), mlen-remaining, msglen-aggrbuf.position());
                decodeMessage(aggrbuf, msglen);
                remaining -= msglen - bufIndx;
                aggrbuf.clear();
                aggrbuf.order(ByteOrder.LITTLE_ENDIAN);
		        if (remaining > 4)
		        {
			        msglen = buffer.getInt(mlen-remaining);
		        }
		        else
		        {
			        aggrbuf.put(buffer.array(), mlen-remaining, remaining);
		        	bMore = true;
			        break;
		        }
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
