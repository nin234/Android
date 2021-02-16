package com.rekhaninan.common;

import java.net.PortUnreachableException;

/**
 * Created by nin234 on 8/28/16.
 */
public final class Constants {
    public static final String AUTOSPREE = "AutoSpree";
    public static final String OPENHOUSES= "OpenHouses";
    public static final String EASYGROC= "EasyGrocList";

    public static final int OPENHOUSES_ID=0;
    public static final int AUTOSPREE_ID = 1;
    public static final int EASYGROC_ID = 1;

    public static final  int NOTES_ACTIVITY_REQUEST=1;
    public static final  int PICTURE_ACTIVITY_REQUEST=2;
    public static final  int IMAGE_SWIPE_ACTIVITY_REQUEST=3;
    public static final  int GET_CONTACTS_ACTIVITY_REQUEST=4;
    public static final  int SHARE_PICTURE_ACTIVITY_REQUEST=5;
    public static final int ADD_CHECK_LIST_ACTIVITY_REQUEST=6;
    public static final int EDIT_CHECK_LIST_ACTIVITY_REQUEST=7;
    public static final int ADD_CHECK_LIST_ACTIVITY_REQUEST_2=8;
    public static final int ADD_CONTACT_ITEM_ACTIVITY_REQUEST=9;
    public static final int DELETE_CONTACT_ITEM_ACTIVITY_REQUEST=10;
    public static final int ADD_TEMPL_CHECKLIST_ACTIVITY_REQUEST=11;
    public static final int DELETE_TEMPL_CHECKLIST_ACTIVITY_REQUEST=12;
    public static final int DELETE_TEMPL_ACTIVITY_REQUEST=13;
    public static final int EASYGROC_ADD_ITEM_REQUEST=14;
    public static final int EASYGROC_BRAND_NEW_ADD_REQUEST=15;
    public static final int EASYGROC_DELETE_ITEM_REQUEST=16;
    public static final int HELP_SCREEN_REQUEST=17;
    public static final int OPENHOUSES_ADD_ITEM_REQUEST=18;
    public static final int AUTOSPREE_ADD_ITEM_REQUEST=19;
    public static final int OPENHOUSES_DISPLAY_ITEM_REQUEST=20;
    public static final int AUTOSPREE_DISPLAY_ITEM_REQUEST=21;
    public static final  int CHECKLIST_EDIT_DELETE_REQUEST=22;
    public static final  int CHECKLIST_EDIT_REQUEST=23;




    public static final int MAINVW=0;
    public static final  int OPENHOUSES_ADD_ITEM=1;
    public static final int AUTOSPREE_ADD_ITEM=2;
    public static final int EASYGROC_ADD_ITEM_OPTIONS=3;
    public static final  int OPENHOUSES_DISPLAY_ITEM=4;
    public static final int AUTOSPREE_DISPLAY_ITEM=5;
    public static final int EASYGROC_DISPLAY_ITEM=6;
    public static final  int OPENHOUSES_EDIT_ITEM=7;
    public static final int AUTOSPREE_EDIT_ITEM=8;
    public static final int EASYGROC_TEMPL_LISTS=10;
    public static final int EASYGROC_TEMPL_DISPLAY_ITEM=11;
    public static final int EASYGROC_TEMPL_ADD_ITEM=12;
    public static final int EASYGROC_TEMPL_EDIT_ITEM=13;
    public static final int EASYGROC_ADD_ITEM=14;
    public static final int EASYGROC_EDIT_ITEM=15;
    public static final int SHARE_MAINVW=16;
    public static final int CONTACTS_VW=17;
    public static final int SHARE_PICTURE_VW=18;
    public static final int PICTURE_VW=19;
    public static final int CONTACTS_MAINVW=20;
    public static final int CONTACTS_ITEM_ADD=21;
    public static final int CONTACTS_ITEM_DISPLAY=22;
    public static final int CHECK_LIST_TEMPL_SELECTOR=23;
    public static final int CHECK_LIST_ADD=24;
    public static final int CHECK_LIST_DISPLAY=25;
    public static final int CHECK_LIST_EDIT=26;
    public static final int EASYGROC_TEMPL_NAME_ADD_ITEM=27;
    public static final int SHARE_TEMPL_MAINVW=28;
    public static final int SORT_MAINVW=29;
    public static final int EASYGROC_TEMPL_DELETE_ITEM=30;
    public static final int CONTACTS_ITEM_ADD_NOVWTYP=31;
    public static final int EASYGROC_TEMPL_NAME_LISTS=32;
    public static final int EASYGROC_EDIT_VIEW=33;
    public static final int EASYGROC_SAVE_VIEW=34;
    public static final int HELP_SCREEN_VIEW=35;
    public static final int CONTACTS_ITEM_NOVWTYP=35;

    public static final int AUTOSPREE_NAME_ROW=0;
    public static final int AUTOSPREE_MODEL_ROW=1;
    public static final int AUTOSPREE_MAKE_ROW=2;
    public static final int AUTOSPREE_PRICE_ROW=3;

    public static final int OPENHOUSES_NAME_ROW=0;
    public static final int OPENHOUSES_PRICE_ROW=1;
    public static final int OPENHOUSES_AREA_ROW=2;
    public static final int OPENHOUSES_BEDS_ROW=3;


    public static final int EASYGROC_ADD_NEW_LIST =0;
    public static final int EASYGROC_ADD_ROW_TWO = 1;

    public static final int EASYGROC_ADD_TEMPL_LIST = 2;


    public static final int EASYGROC_NAME_ROW = 0;

    public static final int CONTACTS_ROW_ONE=0;
    public static final int CONTACTS_NAME_ROW=1;
    public static final int CONTACTS_ROW_THREE=2;
    public static final int CONTACTS_SHARE_ID_ROW=3;
    public static final int CONTACTS_ROW_FIVE=4;


    public static final int ROW_FIVE=4;
    public static final int ROW_SIX=5;
    public static final int ROW_SEVEN=6;
    public static final int ROW_EIGHT=7;
    public static final int ROW_NINE=8;
    public static final int ROW_TEN=9;
    public static final int ROW_ELEVEN=10;
    public static final int ROW_TWELVE=11;
    public static final int ROW_THIRTEEN=12;
    public static final int ROW_FOURTEEN=13;

    public static final int SHARE_ITEM_MSG=9;
    public static final int PIC_METADATA_MSG=15;
    public static final int PIC_MSG=17;
    public static final int STORE_FRIEND_LIST_MSG=5;
    public static final int GET_SHARE_ID_MSG=1;
    public static final int GET_SHARE_ID_RPLY_MSG =2;
    public static final int  STORE_TRNSCTN_ID_RPLY_MSG =4;
    public static final int GET_EASYGROC_LIST_MSG=40;
    public static final int SHARE_TEMPL_ITEM_MSG=22;
    public static final int SHARE_TEMPL_ITEM_RPLY_MSG=23;
    public static final int  STORE_DEVICE_TKN_MSG =11;
    public static final int  STORE_DEVICE_TKN_RPLY_MSG =12;
    public static final int  GET_ITEMS_MSG =13;
    public static final int  PIC_DONE_MSG =19;
    public static final int SHOULD_UPLOAD_MSG = 20;
    public static final int SHOULD_DOWNLOAD_MSG = 21;
    public static final int  FRIEND_LIST_MSG = 25;

    public static final int  TOTAL_PIC_LEN_MSG = 1500;
    public static final int  UPDATE_MAX_SHARE_ID_MSG = 1501;
    public static final int  SHARE_ID_REMOTE_HOST_MSG = 1502;
    public static final int  GET_REMOTE_HOST_MSG = 1503;

    public static final int SHARE_POSN=0;
    public static final int CONTACTS_POSN=1;
    public static final int PLANNER_POSN=2;
    public static final int HOME_POSN=3;

    public static final int ONETIME_POSN=0;
    public static final int REPLENISH_POSN=1;
    public static final int ALWAYS_POSN=2;

    public static final int  MAX_BUF=16384;
    public static final int RCV_BUF_LEN=16384;
    public static final int MSG_AGGR_BUF_LEN = 32768;
    public static final long CHECK_INTERVAL = 120000;


    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final int RESULT_NO_CONTACT_SELECTED = 10;

    public static final String PACKAGE_NAME =
            "com.rekhaninan.common";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";

    public static final String KEYVALSEPARATORREGEX= ":\\|:";
    public static final String KEYVALSEPARATOR= ":|:";
    public static final String ITEMSEPARATOR= "]:;";
    public static final String CONTACTITEMSEPARATOR = ":::";
    public static final String TEMPLLISTSEPERATOR  = ":;]:;";

    public static final String FRIENDLISTITEMSEPERATOR = ";];";
    public static final String FRIENDLISTTOKENSEPERATOR = ":]:";

    public static final String FRNDLSTMSGFRNDSEPERATOR = ";";
    public static final String FRNDLSTMSGNAMESHIDSEPERATOR = ":";

}
