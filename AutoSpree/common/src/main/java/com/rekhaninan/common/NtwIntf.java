package com.rekhaninan.common;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.SystemClock;

import org.apache.http.conn.ssl.X509HostnameVerifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.Certificate;

import java.security.SecureRandom;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.security.cert.X509Certificate;

import static com.rekhaninan.common.Constants.AUTOSPREE;
import static com.rekhaninan.common.Constants.CHECK_INTERVAL;
import static com.rekhaninan.common.Constants.EASYGROC;
import static com.rekhaninan.common.Constants.MAX_BUF;
import static com.rekhaninan.common.Constants.OPENHOUSES;

/**
 * Created by ninanthomas on 2/15/17.
 */

public class NtwIntf {

    private String connectAddr;
    private int connectPort;
    private SocketChannel socket;
    private ReadableByteChannel wrappedChannel;
    private long lastChecked;
    private boolean useSSL;
    private Context ctxt;
    private SSLContext sslContext;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private SSLSocket socketSSL;


    private final String TAG="NtwIntf";

    public void setConnectionDetails (String  app_name)
    {
        SharedPreferences sharing = ctxt.getSharedPreferences("Sharing", Context.MODE_PRIVATE);
        switch (app_name)
        {
            case EASYGROC: {
                String host = sharing.getString("easygroc_host", "None");
                if (host.equals("None")) {
                    connectAddr = "easygroclist.ddns.net";
                    if (useSSL)
                        connectPort = 16805;
                    else
                        connectPort = 16791;
                }
                else
                {
                    connectAddr = host;
                    connectPort = sharing.getInt("easygroc_port", 0);
                }
            }
            break;

            case OPENHOUSES: {
                String host = sharing.getString("openhouses_host", "None");
                if (host.equals("None")) {
                    connectAddr = "openhouses.ddns.net";
                    if (useSSL)
                        connectPort = 16803;
                    else
                        connectPort = 16789;
                } else {
                    connectAddr = host;
                    connectPort = sharing.getInt("openhouses_port", 0);
                }
            }
            break;

            case AUTOSPREE:{
                String host = sharing.getString("autospree_host", "None");
                if (host.equals("None")) {
                    connectAddr = "autospree.ddns.net";
                    if (useSSL)
                        connectPort = 16804;
                    else
                        connectPort = 16790;
                } else {
                    connectAddr = host;
                    connectPort = sharing.getInt("autospree_port", 0);
                }
            }
            break;

            default:
                break;
        }
        Log.i(TAG, "Setting connection details for app=" + app_name + " connectAddr=" + connectAddr + " connectPort=" + connectPort);
    }

    public NtwIntf (Context ctx)
    {
        try {
            useSSL = true;
            if (useSSL) {
                ctxt = ctx;
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                AssetManager am = ctxt.getAssets();
                InputStream caInput = am.open("server.der");
                java.security.cert.Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    System.out.println("certificate file=server.der details=" + ((java.security.cert.X509Certificate) ca).getSubjectDN());
                } finally {
                    caInput.close();
                }

// Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                Log.i(TAG, "KeyStoreType=" + keyStoreType);
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

// Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);


                tmf.init(keyStore);

// Create an SSLContext that uses our TrustManager
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
                Log.d(TAG, "SSL Context initialized");


            }

                lastChecked = System.currentTimeMillis();
                socket = SocketChannel.open();
                socket.socket().setSoTimeout(1000);
                socket.configureBlocking(true);
                socket.socket().setReceiveBufferSize(MAX_BUF);
                socket.socket().setTcpNoDelay(true);

        }
        catch (IOException excp)
        {
            Log.e(TAG, "Failed to open socket channel " + excp.getMessage(), excp);
        }
        catch (Exception excp)
        {
            Log.e(TAG, "Failed to open socket channel " + excp.getMessage(), excp);
        }

    }

    public void checkAndCloseIfIdle()
    {

        try {
            if (useSSL)
            {
                if (socketSSL == null || !socketSSL.isConnected()) {
                    return;
                }
                long now = System.currentTimeMillis();
                if (now < lastChecked + CHECK_INTERVAL) {
                    return;
                }

                socketSSL.close();
                Log.i(TAG, "Closing idle socket");

            }
            else {
                if (!socket.isConnected()) {
                    return;
                }
                long now = System.currentTimeMillis();
                if (now < lastChecked + CHECK_INTERVAL) {
                    return;
                }

                socket.close();
                Log.i(TAG, "Closing idle socket");
            }
        }
        catch (IOException excp)
        {
            Log.e (TAG, "sendMsg Caught IOException=" + excp.getMessage(), excp);
        }
        catch (Exception excp)
        {
            Log.e (TAG, "sendMsg Caught Exception=" + excp.getMessage(), excp);
        }

    }

    public boolean sendMsg (ByteBuffer msg)
    {
        if (useSSL)
        {
            if (socketSSL == null || !socketSSL.isConnected()) {
                if (!connect()) {

                    Log.d(TAG, "Failed to connect to socket");
                    return false;
                }

            }
        }
        else {
            if (!socket.isConnected()) {
                if (!connect()) {

                    Log.d(TAG, "Failed to connect to socket");
                    return false;
                }

            }
        }

        try {
            msg.flip();
            Log.i(TAG, "Number of bytes to write to socket=" + msg.remaining());
            if (useSSL)
            {
                socketSSL.getOutputStream().write(msg.array());
                Log.i(TAG, "Wrote to SSL socket buffer bytes=" + msg.array().length);
                return true;
            }
            while (msg.hasRemaining()) {
                int n = socket.write(msg);
                lastChecked = System.currentTimeMillis();
                Log.i(TAG, "Wrote to socket buffer bytes=" + n);
            }
            return true;

        }
        catch (IOException excp)
        {
            Log.e (TAG, "sendMsg Caught IOException=" + excp.getMessage(), excp);
        }
        catch (Exception excp)
        {
            Log.e (TAG, "sendMsg Caught Exception=" + excp.getMessage(), excp);
        }


        return false;
    }

    boolean getResp(ByteBuffer resp)
    {
        try {

            if (useSSL)
            {
                if (socketSSL == null || !socketSSL.isConnected())
                {
                 //   Log.i(TAG, "SSL Socket not connected");
                    return false;
                }
            }
            else {
                if (!socket.isConnected()) {
                    // Log.e(TAG, "Socket not connected");
                    return false;
                }
            }

            if (wrappedChannel == null)
            {
                Log.d(TAG, "NUll wrapped channel");
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
            Log.e (TAG, "getResp Caught IOException" + excp.getMessage(), excp);
        }
        catch (Exception excp)
        {
            Log.e (TAG, "getResp Caught Exception" + excp.getMessage(), excp);
        }

        return false;
    }

    public
    boolean connect()
    {
        try {
            if (useSSL)
            {
                socket = SocketChannel.open();
                socket.socket().setSoTimeout(1000);
                socket.configureBlocking(true);
                socket.socket().setReceiveBufferSize(MAX_BUF);
                socket.socket().setTcpNoDelay(true);
                Log.i(TAG, "Connecting to socket");
                boolean isConnected = socket.connect(new InetSocketAddress(connectAddr, connectPort));
                while (!socket.finishConnect()) ;
                Log.d(TAG, "Connected to  socket connectAddr=" + connectAddr + " connectPort=" + connectPort);
                SSLSocketFactory factory = sslContext.getSocketFactory();

                socketSSL =
                        (SSLSocket)factory.createSocket(socket.socket(), connectAddr, connectPort, true);
                Log.d(TAG, "Connected to ssl socket connectAddr=" + connectAddr + " connectPort=" + connectPort);
                //socketSSL.setReceiveBufferSize(MAX_BUF);

                //socketSSL.setTcpNoDelay(true);

                //socketSSL.setSoTimeout(1000);

                //HttpsURLConnection.setDefaultHostnameVerifier(new X509HostnameVerifier());
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                SystemClock.sleep(1000);
                //socketSSL.startHandshake();
                Log.d(TAG, "Getting  ssl session connectAddr=" + connectAddr + " connectPort=" + connectPort);
                //SystemClock.sleep(1000);
                SSLSession s = socketSSL.getSession();

                Log.d(TAG, "Got  ssl  session after sleep connectAddr=" + s.getPeerHost() + " connectPort=" + s.getPeerPort()
                + " protocol=" + s.getProtocol() + " principal=" + s.getPeerPrincipal());

// Verify that the certicate hostname is for mail.google.com
// This is due to lack of SNI support in the current SSLSocket.
/*
               if (!hv.verify(connectAddr, s)) {
                    throw new SSLHandshakeException("Expected , " + connectAddr +
                            " found " + s.getPeerPrincipal());
                }

*/

                Log.d(TAG, "Host Name verified for  session connectAddr=" + connectAddr + " connectPort=" + connectPort);
                InputStream inStream = socketSSL.getInputStream();
                wrappedChannel = Channels.newChannel(inStream);


                Log.i(TAG, "Connected to SSL socket=" + socketSSL.isConnected());
            }
            else {
                socket = SocketChannel.open();
                socket.socket().setSoTimeout(1000);
                socket.configureBlocking(true);
                socket.socket().setReceiveBufferSize(MAX_BUF);
                socket.socket().setTcpNoDelay(true);
                Log.i(TAG, "Connecting to socket");
                boolean isConnected = socket.connect(new InetSocketAddress(connectAddr, connectPort));
                while (!socket.finishConnect()) ;
                InputStream inStream = socket.socket().getInputStream();
                wrappedChannel = Channels.newChannel(inStream);
                Log.i(TAG, "Connected to socket");
            }
            return true;


        }
        catch (SSLPeerUnverifiedException excp)
        {
            Log.e (TAG, "connect Caught SSLPeerUnverifiedException " + excp.getMessage(), excp);
        }
        catch (SSLHandshakeException excp)
        {
            Log.e (TAG, "connect Caught SSLHandshakeException " + excp.getMessage(), excp);
        }
        catch (UnknownHostException excp)
        {
            Log.e (TAG, "connect Caught UnknownHostException " + excp.getMessage(), excp);
        }
        catch (IOException excp)
        {
            Log.e (TAG, "connect Caught IOException " + excp.getMessage(), excp);
        }
        catch (Exception excp)
        {
            Log.e (TAG, "connect Caught Exception " + excp.getMessage(), excp);
        }

        return false;
    }


}
