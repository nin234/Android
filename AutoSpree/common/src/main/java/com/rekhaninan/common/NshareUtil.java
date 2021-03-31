package com.rekhaninan.common;

import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.AUTOSPREE_ID;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.EASYGROC_ID;
import static com.rekhaninan.common.Constants.NSHARELIST;
import static com.rekhaninan.common.Constants.NSHARELIST_ID;
import static com.rekhaninan.common.Constants.OPENHOUSES;
import static com.rekhaninan.common.Constants.OPENHOUSES_ID;
import static com.rekhaninan.common.Constants.SMARTMSG;
import static com.rekhaninan.common.Constants.SMARTMSG_ID;

public class NshareUtil {


    public static String
    removeNonAlphanumeric(String str)
    {
        // replace the given string
        // with empty string
        // except the pattern "[^a-zA-Z0-9]"
        str = str.replaceAll(
                "[^a-zA-Z]", "");

        // return string
        return str;
    }

    public static int
    appNameToAppId(String str)
    {
        switch (str)
        {
            case EASYGROC:
                return EASYGROC_ID;
            case OPENHOUSES:
                return OPENHOUSES_ID;
            case AUTOSPREE:
                return AUTOSPREE_ID;
            case SMARTMSG:
                return SMARTMSG_ID;
            case NSHARELIST:
                return NSHARELIST_ID;
        }

        return -1;
    }

}
