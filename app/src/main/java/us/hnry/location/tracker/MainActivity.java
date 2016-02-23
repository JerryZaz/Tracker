package us.hnry.location.tracker;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSION_RC = 101;
    @Bind(R.id.button_connect)
    Button mButtonConnect;
    @Bind(R.id.button_disconnect)
    Button mButtonDisconnect;
    private Tracking mTrackingService;
    private boolean status;

    /*private Button mConnect;
    private Button mDisconnect;*/

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
        ButterKnife.bind(this);


        /*mConnect = (Button) findViewById(R.id.button_connect);
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
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_PERMISSION_RC);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbind();
    }

    public void bind() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Intent bindingIntent = new Intent(this, Tracking.class);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(bindingIntent);
        status = true;
    }

    public void unbind() {
        if (status) {
            stopService(new Intent(this, Tracking.class));
            unbindService(serviceConnection);
            status = false;
        }
    }

    @OnClick({R.id.button_connect, R.id.button_disconnect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_connect:
                if(!status){
                    bind();
                } else{
                    Toast.makeText(this, "Service bound", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_disconnect:
                if (status) {
                    unbind();
                } else {
                    Toast.makeText(this, "Service not bound", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
