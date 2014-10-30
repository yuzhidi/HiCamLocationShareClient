package com.hicam.locationservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

public class LocationShareClient {
    public static final String TAG = "LocationClientService";

    private NetworkClient mNetworkClient;
    private LocationUpdate mLocationUpdate;
    private String mAddr;
    private int mPort = -1;

    public LocationShareClient() {
        init();
    }

    private void init() {
        try {
            mNetworkClient = new NetworkClient();
        } catch (IOException e) {
            Log.e(TAG, "NetworkClient.getInstance() fail!!");
            e.printStackTrace();
            return;
        }
    }

    private void uninit() {

    }

    public void setIpAddress(String s) {
        mAddr = s;
        if (s == null) {
            return;
        }
        mNetworkClient.setIpAddress(s);
    }

    public void setPort(int p) {
        if (p < 0) {
            return;
        }
        mPort = p;
        mNetworkClient.setPort(p);
    }

    public interface LocationUpdate {
        public void onLocationUpdate(double latitude, double longitude,
                long timestap);
    }

    public void register(LocationUpdate l) {
        mLocationUpdate = l;
        mNetworkClient.register(mLocationUpdate);
        if (mAddr == null || mPort < 0) {
            return;
        }
        mNetworkClient.start();
    }

    public void unregister(LocationUpdate l) {
        if (mLocationUpdate == l) {
            mLocationUpdate = null;
            mNetworkClient.stop();
        }
    }

    private class NetworkClient {
        public static final String TAG = "NetworkClient";

        private static final int MAGIC_NUMBER = 0xFFFFFFFF;
        private String mHost = "localhost";
        private int mPort = -1;
        private Socket mSocket;

        private ExecutorService mExecutorService;
        private SubMainHandler mSubMainHandler;
        private boolean mRun;
        private LocationUpdate mLocationUpdate;

        private NetworkClient() throws IOException {
            int avp = Runtime.getRuntime().availableProcessors();
            Log.v(TAG, "availableProcessors:" + avp);
            mExecutorService = Executors.newFixedThreadPool(avp);
        }

        public void setIpAddress(String s) {
            mHost = s;
        }

        public void setPort(int p) {
            mPort = p;
        }

        public void start() {
            Log.v(TAG, "start()");
            if (mRun) {
                return;
            }
            mRun = true;
            mSubMainHandler = new SubMainHandler();
            mExecutorService.execute(mSubMainHandler);
        }

        public void stop() {
            Log.v(TAG, "stop()");
            if (!mRun) {
                return;
            }
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

        private int byteToInt2(byte[] b, int index) {

            int mask = 0xff;
            int temp = 0;
            int n = 0;
            for (int i = 0; i < 4; i++) {
                n <<= 8;
                temp = b[i + index] & mask;
                n |= temp;
            }
            System.out.println(n);
            return n;
        }

        public void connect() throws UnknownHostException, IOException {
            Log.v(TAG, "connect()");
            mSocket = new Socket(mHost, mPort);
            Log.v(TAG, "connect() new Socket done");
            InputStream in = mSocket.getInputStream();
            byte[] buffer = ByteBuffer.allocate(16).array();

            while (mRun) {
                Log.v(TAG, "connect() while(" + mRun + " )");

                if (in.read(buffer) <= 0) {
                    mRun = false;
                    break;
                }

                int high = byteToInt2(buffer, 0);
                int low = byteToInt2(buffer, 4);
                double latitude = high + (double) low / 100000000;// 10 -8
                Log.v(TAG, "connect() high:" + high + ",low:" + low
                        + " ,latitude:" + latitude);

                high = byteToInt2(buffer, 8);
                low = byteToInt2(buffer, 12);
                double longitude = high + (double) low / 100000000; // 10 -8
                Log.v(TAG, "connect() high:" + high + ",low:" + low
                        + " ,longitude:" + longitude);
                if (mLocationUpdate != null) {
                    mLocationUpdate.onLocationUpdate(latitude, longitude, 0);
                }
            }
            Log.v(TAG, "close socekt");
            in.close();
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
}
