package com.hicam.locationservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hicam.locationservice.LocationClientService.LocationUpdate;

import android.util.Log;

public class NetworkClient {
    public static final String TAG = "NetworkClient";

    private static final int MAGIC_NUMBER = 0xFFFFFFFF;
    private String mHost = "localhost";
    private int mPort = -1;
    private Socket mSocket;
    private static NetworkClient sNetworkClient;
    private ExecutorService mExecutorService;
    private SubMainHandler mSubMainHandler;
    private boolean mRun = true;
    private LocationUpdate mLocationUpdate;

    private NetworkClient() throws IOException {
        int avp = Runtime.getRuntime().availableProcessors();
        Log.v(TAG, "availableProcessors:" + avp);
        mExecutorService = Executors.newFixedThreadPool(avp);
    }

    public static final NetworkClient getInstance() throws IOException {
        if (sNetworkClient != null) {
            return sNetworkClient;
        }
        sNetworkClient = new NetworkClient();
        return sNetworkClient;
    }

    public void setIpAddress(String s) {
        mHost = s;
    }

    public void setPort(int p) {
        mPort = p;
    }

    public void start() {
        Log.v(TAG, "start()");
        mRun = true;
        mSubMainHandler = new SubMainHandler();
        mExecutorService.execute(mSubMainHandler);
    }

    public void stop() {
        Log.v(TAG, "stop()");
        mRun = false;
        try {
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            Log.v(TAG, "close socket fail");
            e.printStackTrace();
        }
    }

    public void connect() throws UnknownHostException, IOException {
        Log.v(TAG, "connect()");
        mSocket = new Socket(mHost, mPort);
        Log.v(TAG, "connect() new Socket done");
        InputStream in = mSocket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(in);

        while (mRun) {
            Log.v(TAG, "connect() while(" + mRun + " )");
            while (mRun && ois.readInt() != MAGIC_NUMBER) {
                Log.v(TAG, "connect() not magic number");
            }
            int high = ois.readInt();
            int low = ois.readInt();
            double latitude = high + (double) low / 100000000;// 10 -8
            Log.v(TAG, "connect() high:" + high + ",low:" + low + " ,latitude:"
                    + latitude);

            high = ois.readInt();
            low = ois.readInt();
            double longitude = high + (double) low / 100000000; // 10 -8
            Log.v(TAG, "connect() high:" + high + ",low:" + low
                    + " ,longitude:" + longitude);
            if (mLocationUpdate != null) {
                mLocationUpdate.onLocationUpdate(latitude, longitude, 0);
            }
        }
        Log.v(TAG, "close socekt");
        ois.close();
        mSocket.close();
    }

    public void register(LocationUpdate l) {
        mLocationUpdate = l;
    }

    private class SubMainHandler implements Runnable {

        @Override
        public void run() {
            try {
                connect();
            } catch (UnknownHostException e) {
                Log.e(TAG, "UnknownHostException:" + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "IOException:" + e.getMessage());
                e.printStackTrace();
            }
            Log.v(TAG, "SubMainHandler is over");
        }
    }
}
