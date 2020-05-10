package com.rekhaninan.common;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketOption;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.util.Locale;
import java.util.concurrent.SynchronousQueue;

import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.CHECK_INTERVAL;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.MAX_BUF;
import static com.rekhaninan.common.Constants.OPENHOUSES;
import static com.rekhaninan.common.Constants.RCV_BUF_LEN;

/**
 * Created by ninanthomas on 2/15/17.
 */

public class NtwIntf {

    private String connectAddr;
    private int connectPort;
    private SocketChannel socket;
    private ReadableByteChannel wrappedChannel;
    private long lastChecked;

    private final String TAG="NtwIntf";

    public void setConnectionDetails (String  app_name)
    {

        switch (app_name)
        {
            case EASYGROC:
                    connectAddr = "easygroclist.ddns.net";
                    connectPort = 16791;
                break;

            case OPENHOUSES:
                    connectAddr = "openhouses.ddns.net";
                    connectPort = 16789;
                break;

            case AUTOSPREE:
                connectAddr = "autospree.ddns.net";
                connectPort = 16790;
                break;

            default:
                break;
        }
        Log.i(TAG, "Setting connection details for app=" + app_name + " connectAddr=" + connectAddr + " connectPort=" + connectPort);
    }

    public NtwIntf ()
    {
        try {
            lastChecked = System.currentTimeMillis();
            socket = SocketChannel.open();
            socket.socket().setSoTimeout(1000);
            socket.configureBlocking(true);
            socket.socket().setReceiveBufferSize(MAX_BUF);
            socket.socket().setTcpNoDelay(true);

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

    public void checkAndCloseIfIdle()
    {

        try {

            if ( !socket.isConnected())
            {
                return;
            }
            long now = System.currentTimeMillis();
            if (now < lastChecked + CHECK_INTERVAL) {
                return;
            }

            socket.close();
            Log.i(TAG, "Closing idle socket");
        }
        catch (IOException excp)
        {
            Log.e (TAG, "sendMsg Caught IOException=" + excp.getMessage());
        }
        catch (Exception excp)
        {
            Log.e (TAG, "sendMsg Caught Exception=" + excp.getMessage());
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
            msg.flip();
            Log.i(TAG, "Number of bytes to write to socket=" + msg.remaining());
            while (msg.hasRemaining()) {
                int n = socket.write(msg);
                lastChecked = System.currentTimeMillis();
                Log.i(TAG, "Wrote to socket buffer bytes=" + n);
            }
            return true;

        }
        catch (IOException excp)
        {
            Log.e (TAG, "sendMsg Caught IOException=" + excp.getMessage());
        }
        catch (Exception excp)
        {
            Log.e (TAG, "sendMsg Caught Exception=" + excp.getMessage());
        }


        return false;
    }

    boolean getResp(ByteBuffer resp)
    {
        try
        {

        if (!socket.isConnected())
        {
           // Log.e(TAG, "Socket not connected");
            return false;
        }

           //Log.i(TAG, "reading nbytes=" + resp.remaining() + " from socket");
        int bytesRead = wrappedChannel.read(resp);
            if (bytesRead > 0) {
                Log.d(TAG, "Received response of bytes=" + bytesRead);
                lastChecked = System.currentTimeMillis();
                //resp.flip();
                return true;
            }
            else {
               // Log.e(TAG, "Bytes read=" + bytesRead);
                return false;
            }
        }
        catch (SocketTimeoutException excp)
        {
         //   Log.e (TAG, "getResp Caught Time out Exception" + excp.getMessage());

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
            socket = SocketChannel.open();
            socket.socket().setSoTimeout(1000);
            socket.configureBlocking(true);
            socket.socket().setReceiveBufferSize(MAX_BUF);
            socket.socket().setTcpNoDelay(true);
            Log.i(TAG, "Connecting to socket");
             boolean isConnected =  socket.connect (new InetSocketAddress( connectAddr, connectPort));
            while (!socket.finishConnect());
            InputStream inStream = socket.socket().getInputStream();
            wrappedChannel = Channels.newChannel(inStream);
            Log.i(TAG, "Connected to socket");
            return true;


        }
        catch (UnknownHostException excp)
        {
            Log.e (TAG, "connect Caught UnknownHostException " + excp.getMessage());
        }
        catch (IOException excp)
        {
            Log.e (TAG, "connect Caught IOException " + excp.getMessage());
        }
        catch (Exception excp)
        {
            Log.e (TAG, "connect Caught Exception " + excp.getMessage());
        }

        return false;
    }


}
