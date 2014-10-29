package com.hicam.locationservice;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LocationClientService extends Service {
    public static final String TAG = "LocationClientService";

    private NetworkClient mNetworkClient;
    private IBinder mLocalBinder = new LocalBinder();
    private LocationUpdate mLocationUpdate;

    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate()");
        create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
        destroy();
    }

    private void create() {
        try {
            mNetworkClient = NetworkClient.getInstance();
        } catch (IOException e) {
            Log.e(TAG, "NetworkClient.getInstance() fail!!");
            e.printStackTrace();
            return;
        }
    }

    private void destroy() {
        if (mNetworkClient != null) {
            mNetworkClient.stop();
        }
    }

    public void setIpAddress(String s) {
        mNetworkClient.setIpAddress(s);
    }

    public void setPort(int p) {
        mNetworkClient.setPort(p);
    }

    public class LocalBinder extends Binder {
        LocationClientService getService() {
            return LocationClientService.this;
        }
    }

    public interface LocationUpdate {
        public void onLocationUpdate(double latitude, double longitude,
                long timestap);
    }

    public void register(LocationUpdate l) {
        mLocationUpdate = l;
        mNetworkClient.register(mLocationUpdate);
        if (mLocationUpdate != null) {
            mNetworkClient.start();
            return;
        }
        mNetworkClient.stop();
    }

    // public void unregister(LocationUpdate l) {
    //
    // }
}
