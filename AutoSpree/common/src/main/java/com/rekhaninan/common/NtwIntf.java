package com.rekhaninan.common;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Locale;

import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.OPENHOUSES;
import static com.rekhaninan.common.Constants.RCV_BUF_LEN;

/**
 * Created by ninanthomas on 2/15/17.
 */

public class NtwIntf {

    private String connectAddr;
    private int connectPort;
    private SocketChannel socket;

    private final String TAG="NtwIntf";

    public void setConnectionDetails (String  app_name)
    {
        switch (app_name)
        {
            case EASYGROC:
                    connectAddr = "easygroclist.ddns.net";
                    connectPort = 16972;
                break;

            case OPENHOUSES:
                    connectAddr = "openhouses.ddns.net";
                    connectPort = 16973;
                break;

            case AUTOSPREE:
                connectAddr = "autospree.ddns.net";
                connectPort = 16971;
                break;

            default:
                break;
        }
    }

    public NtwIntf ()
    {
        try {
            socket = SocketChannel.open();
            socket.configureBlocking(false);
        }
        catch (IOException excp)
        {
            Log.e(TAG, "Failed to open socket channel " + excp.getMessage());
        }
        catch (Exception excp)
        {
            Log.e(TAG, "Failed to open socket channel " + excp.getMessage());
        }

    }

    public boolean sendMsg (ByteBuffer msg)
    {
        if ( !socket.isConnected())
        {
            if (!connect()){

                Log.e (TAG, "Failed to connect to socket");
                return false;
            }

        }

        try {

            socket.write(msg);
            return true;

        }
        catch (IOException excp)
        {
            Log.e (TAG, "sendMsg Caught IOException" + excp.getMessage());
        }
        catch (Exception excp)
        {
            Log.e (TAG, "sendMsg Caught Exception" + excp.getMessage());
        }


        return false;
    }

    boolean getResp(ByteBuffer resp)
    {
        try
        {

        if (!socket.isConnected())
        {
            return false;
        }

        int bytesRead = socket.read(resp);
            if (bytesRead > 0)
                    return true;
            else
                return false;
        }
        catch (IOException excp)
        {
            Log.e (TAG, "getResp Caught IOException" + excp.getMessage());
        }
        catch (Exception excp)
        {
            Log.e (TAG, "getResp Caught Exception" + excp.getMessage());
        }

        return false;
    }

    public
    boolean connect()
    {
        try {
             boolean isConnected =  socket.connect (new InetSocketAddress( connectAddr, connectPort));
            long startTime = System.nanoTime();

            while (!isConnected &&  !socket.finishConnect())
            {
                long estimatedTime = System.nanoTime() - startTime;
                wait(1000);
                if (estimatedTime > 60000000000L)
                {
                    return false;
                }
            }

            return true;


        }
        catch (UnknownHostException excp)
        {
            Log.e (TAG, "connect Caught UnknownHostException" + excp.getMessage());
        }
        catch (IOException excp)
        {
            Log.e (TAG, "connect Caught IOException" + excp.getMessage());
        }
        catch (Exception excp)
        {
            Log.e (TAG, "connect Caught Exception" + excp.getMessage());
        }

        return false;
    }


}
