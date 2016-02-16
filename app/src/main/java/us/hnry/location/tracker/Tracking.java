package us.hnry.location.tracker;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Henry on 2/15/2016.
 *
 */
public class Tracking extends Service {

    private final IBinder mBinder = new LocalBinder();
    private FallbackLocationTracker mLocationTracker;

    @Override
    public boolean onUnbind(Intent intent) {
        mLocationTracker.stop();
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mLocationTracker = new FallbackLocationTracker(getApplicationContext(), ProviderLocationTracker.ProviderType.GPS);
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationTracker.start(new LocationTracker.LocationUpdateListener() {
            @Override
            public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
                Log.v("Update", String.valueOf(newTime));
                Toast.makeText(getApplicationContext(), String.valueOf(newLoc.getAccuracy()), Toast.LENGTH_SHORT).show();
            }
        });
        return Service.START_STICKY;
    }

    public class LocalBinder extends Binder{
        public Tracking getService(){
            return Tracking.this;
        }
    }
}
