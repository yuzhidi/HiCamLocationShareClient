package com.hicam.locationservice;

import com.hicam.locationservice.LocationClientService.LocationUpdate;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
    public static final String TAG = "MainActivity";

    private static final int MSG_UPDATE = 10;
    private NetworkState mNetworkState;
    private boolean mIsClientBind;
    private LocationClientService mClientBinder;
    private LocationUpdate mLocationUpdate = new MyLocationUpdate();
    private EditText mEd0;
    private EditText mEd1;
    private TextView mText;
    private double mLatitude;
    private double mlongitude;
    private Handler mHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.start_client).setOnClickListener(this);
        findViewById(R.id.stop_client).setOnClickListener(this);

        findViewById(R.id.start_register).setOnClickListener(this);
        findViewById(R.id.stop_register).setOnClickListener(this);
        mEd0 = (EditText) findViewById(R.id.editip);
        mEd1 = (EditText) findViewById(R.id.editport);
        findViewById(R.id.submit).setOnClickListener(this);
        mText = (TextView) findViewById(R.id.textlog);
        mNetworkState = new NetworkState(this);
        mNetworkState.start();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {

        case R.id.start_client:
            Log.v(TAG, "onClick, start client");
            if (!mIsClientBind) {
                bindService(new Intent(this, LocationClientService.class),
                        mClientConnection, BIND_AUTO_CREATE);
            }
            break;
        case R.id.stop_client:
            Log.v(TAG, "onClick, stop client");
            if (mIsClientBind) {
                unbindService(mClientConnection);
            }
            break;

        case R.id.start_register:
            if (mIsClientBind) {
                Log.v(TAG, "register");
                mClientBinder.register(mLocationUpdate);
            }
            break;
        case R.id.stop_register:
            if (mIsClientBind) {
                mClientBinder.register(null);
                Log.v(TAG, "un register");
                break;
            }
            break;
        case R.id.submit:
            String s0 = mEd0.getText().toString();
            String s1 = mEd1.getText().toString();
            Log.v(TAG, "mEd0.getText().toString():" + s0);
            Log.v(TAG, "mEd1.getText().toString():" + s1);
            Log.v(TAG,
                    "Integer.parseInt(mEd1.getText().toString()):"
                            + Integer.parseInt(s1));
            if (mClientBinder == null) {
                Log.e(TAG, "R.id.submit, ClientBinder is null");
                break;
            }
            mClientBinder.setIpAddress(s0);
            mClientBinder.setPort(Integer.parseInt(s1));
        default:
            break;
        }
    }

    private ServiceConnection mClientConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "onServiceConnected");
            mIsClientBind = true;
            mClientBinder = ((LocationClientService.LocalBinder) service)
                    .getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "onServiceDisconnected");
            mIsClientBind = false;
            mClientBinder = null;
        }
    };

    private class MyLocationUpdate implements LocationUpdate {
        @Override
        public void onLocationUpdate(double latitude, double longitude,
                long timestap) {
            Log.v(TAG, "onLocationUpdate");
            mLatitude = latitude;
            mlongitude = longitude;
            mHandler.sendEmptyMessage(MSG_UPDATE);
        }
    };

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UPDATE:
                mText.setText("latitude:" + mLatitude + " ,longitude:"
                        + mlongitude + " system time:"
                        + System.currentTimeMillis());
                break;
            }
        }
    };
}
