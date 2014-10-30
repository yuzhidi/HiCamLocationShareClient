package com.hicam.locationservice;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.hicam.locationservice.LocationShareClient.LocationUpdate;

public class MainActivity extends Activity implements OnClickListener {
    public static final String TAG = "MainActivity";

    private LocationShareClient mLocationShareClient;
    private LocationUpdate mLocationUpdate = new MyLocationUpdate();

    // only for demo
    private static final int MSG_UPDATE = 10;
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

        findViewById(R.id.start_register).setOnClickListener(this);
        findViewById(R.id.stop_register).setOnClickListener(this);
        mEd0 = (EditText) findViewById(R.id.editip);
        mEd1 = (EditText) findViewById(R.id.editport);
        findViewById(R.id.submit).setOnClickListener(this);
        mText = (TextView) findViewById(R.id.textlog);
        //
        mLocationShareClient = new LocationShareClient();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {

        case R.id.start_register:
            Log.v(TAG, "register");
            mLocationShareClient.register(mLocationUpdate);
            break;
        case R.id.stop_register:
            mLocationShareClient.register(null);
            Log.v(TAG, "un register");
            break;
        case R.id.submit:
            String s0 = mEd0.getText().toString();
            String s1 = mEd1.getText().toString();
            Log.v(TAG, "mEd0.getText().toString():" + s0);
            Log.v(TAG, "mEd1.getText().toString():" + s1);
            Log.v(TAG,
                    "Integer.parseInt(mEd1.getText().toString()):"
                            + Integer.parseInt(s1));
            if (mLocationShareClient == null) {
                Log.e(TAG, "R.id.submit, ClientBinder is null");
                break;
            }
            mLocationShareClient.setIpAddress(s0);
            mLocationShareClient.setPort(Integer.parseInt(s1));
        default:
            break;
        }
    }

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
