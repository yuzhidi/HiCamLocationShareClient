package com.hicam.locationservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkState {
    public static final String TAG = "NetworkState";
    private Context mContext;
    private IntentFilter mIntentFilter;

    public NetworkState(Context c) {
        mContext = c;
    }

    public void start() {
        Log.v(TAG, "start()");
        if (mIntentFilter != null) {
            return;
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mConnectionReceiver, mIntentFilter);
    }

    public void stop() {
        Log.v(TAG, "stop()");
        if (mConnectionReceiver != null) {
            mContext.unregisterReceiver(mConnectionReceiver);
        }
    }

    BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            Log.v(TAG, "mobNetInfo.isAvailable():" + mobNetInfo.isAvailable()
                    + ",    isConnected():" + mobNetInfo.isConnected());
            Log.v(TAG, "wifiNetInfo.isAvailable():" + wifiNetInfo.isAvailable()
                    + ",    isConnected():" + wifiNetInfo.isConnected()+"\n\n");
            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                // Log.i(TAG, "unconnect");
                // unconnect network
            } else {
                // connect network
            }
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.d("mark", "网络状态已经改变");
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    String name = info.getTypeName();
                    Log.d("mark", "当前网络名称：" + name);
                } else {
                    Log.d("mark", "没有可用网络");
                }
            }
        }
    };

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}
