package com.hicam.locationservice;

import java.io.IOException;

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
            mNetworkClient = NetworkClient.getInstance();
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
        if(s == null ) {
            return;
        }
        mNetworkClient.setIpAddress(s);
    }

    public void setPort(int p) {
        if(p < 0) {
            return;
        }
        mNetworkClient.setPort(p);
    }

    public interface LocationUpdate {
        public void onLocationUpdate(double latitude, double longitude,
                long timestap);
    }

    public void register(LocationUpdate l) {
        mLocationUpdate = l;
        mNetworkClient.register(mLocationUpdate);
        if(mAddr == null) {
            return;
        }
        if (mLocationUpdate != null) {
            mNetworkClient.start();
            return;
        }
        mNetworkClient.stop();
    }
}
