package android.pubcrawl.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.pubcrawl.database.LocationDB;
import android.pubcrawl.database.ServiceDB;
import android.util.Log;
import android.widget.Toast;

public class CellService extends Service {

  private static final String TAG = CellService.class.getSimpleName();
  private LocationManager locManager;
  private CellLocationListener locListener;
  private Context conText;

  @Override
  public void onCreate() {
    Log.d(TAG, "onCreate");
  }

  @Override
  public void onDestroy() {
    locManager.removeUpdates(locListener);
    Toast.makeText(this, "Cell Service Stopped", Toast.LENGTH_LONG).show();
    ServiceDB db = new ServiceDB(this);
    db.addService(CellService.class.getSimpleName(), ServiceDB.STATUSOPT.STOPPED.name());
    Log.v(TAG, "onDestroy");
  }

  @Override
  public void onStart(Intent intent, int startid) {
    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    locListener = new CellLocationListener();
    locManager.requestLocationUpdates(
            LocationManager.PASSIVE_PROVIDER, 0, 0, locListener);
    conText = this.getApplicationContext();
    LocationDB locDB = new LocationDB(conText);
    Location loc = locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    if(loc != null)
      locDB.addCellEvent(loc);
    Toast.makeText(this, "Cell Service Started", Toast.LENGTH_LONG).show();
    ServiceDB db = new ServiceDB(conText);
    db.addService(CellService.class.getSimpleName(), ServiceDB.STATUSOPT.STARTED.name());
    Log.v(TAG, "onStart");
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  public class CellLocationListener implements LocationListener {

    public CellLocationListener() {
      super();
      Log.v(TAG, CellLocationListener.class.getSimpleName() + " Started!");
    }

    @Override
    public void onLocationChanged(Location loc) {
      LocationDB db = new LocationDB(conText);
      db.addCellEvent(loc);
    }

    @Override
    public void onProviderDisabled(String provider) {
      //Toast.makeText(getApplicationContext(), "Gps Disabled",
      //Toast.LENGTH_SHORT).show();
      Log.v(TAG, CellLocationListener.class.getSimpleName() + " Provider Disabled!");
    }

    @Override
    public void onProviderEnabled(String provider) {
      //Toast.makeText(getApplicationContext(), "Gps Enabled",
      //Toast.LENGTH_SHORT).show();
      Log.v(TAG, CellLocationListener.class.getSimpleName() + " Provider Enabled!");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      Log.v(TAG, CellLocationListener.class.getSimpleName() + " Status Changed!");
    }
  }
}
