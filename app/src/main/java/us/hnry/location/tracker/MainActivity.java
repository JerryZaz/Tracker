package us.hnry.location.tracker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private Tracking mTrackingService;
    private boolean status;

    private Button mConnect;
    private Button mDisconnect;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Tracking.LocalBinder binder = (Tracking.LocalBinder) service;
            mTrackingService = binder.getService();
            status = true;
            Log.v(LOG_TAG, "Service Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(LOG_TAG, "Service Crashed");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConnect = (Button) findViewById(R.id.button_connect);
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind();
                mConnect.setEnabled(false);
                mDisconnect.setEnabled(true);
            }
        });
        mDisconnect = (Button) findViewById(R.id.button_disconnect);

        mDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbind();
                mDisconnect.setEnabled(false);
                mConnect.setEnabled(true);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbind();
    }

    public void bind(){
        Intent bindingIntent = new Intent(this, Tracking.class);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(bindingIntent);
        status = true;
    }
    public void unbind(){
        if(status) {
            stopService(new Intent(this, Tracking.class));
            unbindService(serviceConnection);
            status = false;
        }
    }
}
