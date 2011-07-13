package android.pubcrawl.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.pubcrawl.database.LocationDB;
import android.pubcrawl.database.ServiceDB;
import android.util.Log;
import android.widget.Toast;

public class GpsService extends Service {

  private static final String TAG = GpsService.class.getSimpleName();
  private LocationManager locManager;
  private GpsLocationListener locListener;
  private Context conText;

  @Override
  public void onCreate() {
    Log.d(TAG, "onCreate");
  }

  @Override
  public void onDestroy() {
    locManager.removeUpdates(locListener);
    Toast.makeText(this, "GPS Service Stopped", Toast.LENGTH_LONG).show();
    ServiceDB db = new ServiceDB(this);
    db.addService(GpsService.class.getSimpleName(), ServiceDB.STATUSOPT.STOPPED.name());
    Log.v(TAG, "onDestroy");
  }

  @Override
  public void onStart(Intent intent, int startid) {
    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (!locManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
      Intent myIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
      startActivity(myIntent);
    }
    locListener = new GpsLocationListener();
    locManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 0, 0, locListener);
    conText = this.getApplicationContext();
    LocationDB locDB = new LocationDB(conText);
    Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if (loc != null) 
      locDB.addGpsEvent(loc);
    Toast.makeText(this, "GPS Service Started", Toast.LENGTH_LONG).show();
    ServiceDB db = new ServiceDB(conText);
    db.addService(GpsService.class.getSimpleName(), ServiceDB.STATUSOPT.STARTED.name());
    Log.v(TAG, "onStart");
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  public class GpsLocationListener implements LocationListener {

    public GpsLocationListener() {
      super();
      Log.v(TAG, GpsLocationListener.class.getSimpleName() + " Started!");
    }

    @Override
    public void onLocationChanged(Location loc) {
//      locManager.removeUpdates(locListener);
      LocationDB db = new LocationDB(conText);
      db.addGpsEvent(loc);
//      try {
//        Thread.sleep(3000,0);
//      } catch (Exception e) {
//        Log.v(TAG, e.getMessage());
//      }
//      locManager.requestLocationUpdates(
//              LocationManager.GPS_PROVIDER, 0, 0, locListener);
    }

    @Override
    public void onProviderDisabled(String provider) {
      //Toast.makeText(getApplicationContext(), "Gps Disabled",
      //Toast.LENGTH_SHORT).show();
      Log.v(TAG, GpsLocationListener.class.getSimpleName() + " Provider Disabled!");
    }

    @Override
    public void onProviderEnabled(String provider) {
      //Toast.makeText(getApplicationContext(), "Gps Enabled",
      //Toast.LENGTH_SHORT).show();
      Log.v(TAG, GpsLocationListener.class.getSimpleName() + " Provider Enabled!");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      Log.v(TAG, GpsLocationListener.class.getSimpleName() + " Status Changed!");
    }
  }
}
