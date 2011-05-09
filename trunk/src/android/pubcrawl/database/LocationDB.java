package android.pubcrawl.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationManager;
import android.provider.BaseColumns;
import android.pubcrawl.PubCrawl;
import android.util.Log;

public class LocationDB extends SQLiteOpenHelper {

  private static final String TAG = LocationDB.class.getSimpleName();
  private static final String DATABASE_NAME = LocationDB.class.getSimpleName() + ".db";
  private static final int DATABASE_VERSION = 1;
  private static Context conText;
  // Table name
  public static final String GPS_TABLE = "gpsLocation";
  public static final String CELL_TABLE = "cellLocation";
  public static final String USER_TABLE = "userLocation";
  // Columns
  public static final String TIME = "time";
  public static final String LAT = "lat";
  public static final String LNG = "lng";

  public LocationDB(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    conText = context;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    StringBuilder sql = new StringBuilder();
    sql.append("create table ");
    sql.append(GPS_TABLE);
    sql.append("(" + BaseColumns._ID + " integer primary key autoincrement, ");
    sql.append(TIME + " integer, ");
    sql.append(LAT + " float not null, ");
    sql.append(LNG + " float not null);");
    db.execSQL(sql.toString());
    Log.d(TAG, "onCreate: " + sql.toString());

    sql = new StringBuilder();
    sql.append("create table ");
    sql.append(CELL_TABLE);
    sql.append("(" + BaseColumns._ID + " integer primary key autoincrement, ");
    sql.append(TIME + " integer, ");
    sql.append(LAT + " float not null, ");
    sql.append(LNG + " float not null);");
    db.execSQL(sql.toString());

    sql = new StringBuilder();
    sql.append("create table ");
    sql.append(USER_TABLE);
    sql.append("(" + BaseColumns._ID + " integer primary key autoincrement, ");
    sql.append(TIME + " integer, ");
    sql.append(LAT + " float not null, ");
    sql.append(LNG + " float not null);");
    db.execSQL(sql.toString());
    Log.d(TAG, "onCreate: " + sql.toString());
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion >= newVersion) {
      return;
    }

    String sql = null;
    if (oldVersion == 1) {
      sql = "";
    }

    Log.d(TAG, "onUpgrade	: " + sql);
    if (sql != null) {
      db.execSQL(sql);
    }
  }

  public void addGpsEvent(Location loc) {
    SQLiteDatabase db = null;
    ContentValues values = new ContentValues();
    values.put(TIME, System.currentTimeMillis());
    values.put(LAT, loc.getLatitude());
    values.put(LNG, loc.getLongitude());
    try {
    db = this.getWritableDatabase();
    db.insert(GPS_TABLE, null, values);
    } catch (Exception e){
      db.close();
    } finally {
      db.close();
    }
    Log.v(TAG, "addGpsEvent:" + values.toString());
  }

  public void addCellEvent(Location loc) {
    SQLiteDatabase db = null;
    ContentValues values = new ContentValues();
    values.put(TIME, System.currentTimeMillis());
    values.put(LAT, loc.getLatitude());
    values.put(LNG, loc.getLongitude());
    try {
    db = this.getWritableDatabase();
    db.insert(CELL_TABLE, null, values);
    } catch (Exception e){
      db.close();
    } finally {
      db.close();
    }
    Log.v(TAG, "addCellEvent:" + values.toString());
  }

  public void addUserEvent(Location loc) {
    SQLiteDatabase db = null;
    ContentValues values = new ContentValues();
    values.put(TIME, System.currentTimeMillis());
    values.put(LAT, loc.getLatitude());
    values.put(LNG, loc.getLongitude());
    try {
    db = this.getWritableDatabase();
    db.insert(USER_TABLE, null, values);
    } catch (Exception e){
      db.close();
    } finally {
      db.close();
    }
    Log.v(TAG, "addUserEvent:" + values.toString());
  }

  public Location getLatestGpsEvent() {
    SQLiteDatabase db = null;
    Cursor cursor = null;
    StringBuilder sql = new StringBuilder();
    double lat = 0, lng = 0;
    long time = 0;
    sql.append("SELECT * FROM ");
    sql.append(GPS_TABLE);
    sql.append(" order by ");
    sql.append(BaseColumns._ID);
    sql.append(" DESC limit 10");
    try {
    db = this.getReadableDatabase();
    cursor = db.rawQuery(sql.toString(), null);
    if (cursor.moveToFirst()) {
      long id = cursor.getLong(0);
      time = cursor.getLong(1);
      lat = cursor.getDouble(2);
      lng = cursor.getDouble(3);
      Log.v(TAG, "ID:" + id + " TIME:" + time + "lat:" + lat + "lng:" + lng);
    }
    } catch (Exception e){
      cursor.close();
      db.close();
    } finally {
      cursor.close();
      db.close();
    }
    Location loc = new Location(LocationManager.GPS_PROVIDER);
    loc.setTime(time);
    loc.setLatitude(lat);
    loc.setLongitude(lng);
    return loc;
  }

  public Location getLatestCellEvent() {
    SQLiteDatabase db = null;
    Cursor cursor = null;
    StringBuilder sql = new StringBuilder();
    double lat = 0, lng = 0;
    long time = 0;
    sql.append("SELECT * FROM ");
    sql.append(CELL_TABLE);
    sql.append(" order by ");
    sql.append(BaseColumns._ID);
    sql.append(" DESC limit 10");
    try {
    db = this.getReadableDatabase();
    cursor = db.rawQuery(sql.toString(), null);
    if (cursor.moveToFirst()) {
      long id = cursor.getLong(0);
      time = cursor.getLong(1);
      lat = cursor.getDouble(2);
      lng = cursor.getDouble(3);
      Log.v(TAG, "ID:" + id + " TIME:" + time + "lat:" + lat + "lng:" + lng);
    }
    } catch (Exception e){
      cursor.close();
      db.close();
    } finally {
      cursor.close();
      db.close();
    }
    Location loc = new Location(LocationManager.PASSIVE_PROVIDER);
    loc.setTime(time);
    loc.setLatitude(lat);
    loc.setLongitude(lng);
    return loc;
  }

  public Location getLatestUserEvent() {
    SQLiteDatabase db = null;
    Cursor cursor = null;
    StringBuilder sql = new StringBuilder();
        double lat = 0, lng = 0;
    long time = 0;
    sql.append("SELECT * FROM ");
    sql.append(USER_TABLE);
    sql.append(" order by ");
    sql.append(BaseColumns._ID);
    sql.append(" DESC limit 10");
    try {
    db = this.getReadableDatabase();
    cursor = db.rawQuery(sql.toString(), null);
 if (cursor.moveToFirst()) {
      long id = cursor.getLong(0);
      time = cursor.getLong(1);
      lat = cursor.getDouble(2);
      lng = cursor.getDouble(3);
      Log.v(TAG, "ID:" + id + " TIME:" + time + "lat:" + lat + "lng:" + lng);
    }
    } catch (Exception e){
      cursor.close();
      db.close();
    } finally {
      cursor.close();
      db.close();
    }
    Location loc = new Location(LocationManager.PASSIVE_PROVIDER);
    loc.setTime(time);
    loc.setLatitude(lat);
    loc.setLongitude(lng);
    return loc;
  }

  public Location getLatestLocationEvent(boolean useService) {
    ServiceDB servDB = new ServiceDB(conText);
    ServiceElement serv = servDB.getLatestServiceStatus(PubCrawl.class.getSimpleName());
    Location cellLocation = getLatestCellEvent();
    Location gpsLocation = getLatestGpsEvent();
    Location userLocation = getLatestUserEvent();
    if (useService && (userLocation.getTime() > serv.getTime())) {
      return userLocation;
    }
    Log.v(TAG, "cell:" + cellLocation.getTime() + "gps:" + gpsLocation.getTime());
    if (cellLocation.getTime() > gpsLocation.getTime()) {
      return cellLocation;
    }
    return gpsLocation;
  }
}
