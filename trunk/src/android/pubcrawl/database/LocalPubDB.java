package android.pubcrawl.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import java.util.ArrayList;

public class LocalPubDB extends SQLiteOpenHelper {

  private static final String TAG = LocalPubDB.class.getSimpleName();
  private static final String DATABASE_NAME = LocalPubDB.class.getSimpleName() + ".db";
  private static final int DATABASE_VERSION = 1;

  // Table name
  public static final String TABLE = "pubList";
  // Columns
  public static final String NAME = "name";
  public static final String DESCRIP = "description";
  public static final String PHONE = "phone";
  public static final String LOCATION = "location";
  public static final String RATING = "rating";
  public static final String LAT = "lat";
  public static final String LNG = "lng";
  public static final String ZIPCODE = "zipcode";

  public LocalPubDB(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    StringBuilder sql = new StringBuilder();
    sql.append("create table ");
    sql.append(TABLE);
    sql.append("(" + BaseColumns._ID + " integer primary key autoincrement, ");
    sql.append(NAME + " text not null, ");
    sql.append(DESCRIP + " text not null, ");
    sql.append(PHONE + " text not null unique, ");
    sql.append(LOCATION + " text not null, ");
    sql.append(RATING + " text not null, ");
    sql.append(LAT + " float not null, ");
    sql.append(LNG + " float not null, ");
    sql.append(ZIPCODE + " text not null);");
    db.execSQL(sql.toString());
    ContentValues values = new ContentValues();
    values.put(NAME, "Current Location");
    values.put(DESCRIP, "");
    values.put(PHONE, "");
    values.put(LOCATION, "");
    values.put(RATING, "");
    values.put(LAT, "");
    values.put(LNG, "");
    values.put(ZIPCODE, "");
    db.insert(TABLE, null, values);
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

  public void addPub(String name, String description, String phone,
          String location, String rating, double lat, double lng,
          String zipcode) {
    SQLiteDatabase db = null;
    ContentValues values = new ContentValues();
    values.put(NAME, name);
    values.put(DESCRIP, description);
    values.put(PHONE, phone);
    values.put(LOCATION, location);
    values.put(RATING, rating);
    values.put(LAT, lat);
    values.put(LNG, lng);
    values.put(ZIPCODE, zipcode);
    try {
      db = this.getWritableDatabase();
      db.insert(TABLE, null, values);
    } catch (Exception e) {
      db.close();
    } finally {
      db.close();
    }
    Log.v(TAG, "addPub:" + values.toString());
  }

  public void addPub(PubElement pub) {
    SQLiteDatabase db = null;
    ContentValues values = new ContentValues();
    values.put(NAME, pub.getName());
    values.put(DESCRIP, pub.getDescrip());
    values.put(PHONE, pub.getPhone());
    values.put(LOCATION, pub.getLocation());
    values.put(RATING, pub.getRating());
    values.put(LAT, pub.getLat());
    values.put(LNG, pub.getLng());
    values.put(ZIPCODE, pub.getZipcode());
    try {
      db = this.getWritableDatabase();
      db.insert(TABLE, null, values);
    } catch (Exception e) {
      db.close();
    } finally {
      db.close();
    }
    Log.v(TAG, "addPub:" + values.toString());
  }

  public PubElement getPubByName(String name) {
    SQLiteDatabase db = null;
    Cursor cursor = null;
    PubElement pub = new PubElement();
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT * FROM ");
    sql.append(TABLE);
    sql.append(" WHERE name='");
    sql.append(name);
    sql.append("'");
    try {
      db = this.getReadableDatabase();
      cursor = db.rawQuery(sql.toString(), null);
      if (cursor.moveToFirst()) {
        pub.setId(cursor.getLong(0));
        pub.setName(cursor.getString(1));
        pub.setDescrip(cursor.getString(2));
        pub.setPhone(cursor.getString(3));
        pub.setLocation(cursor.getString(4));
        pub.setRating(cursor.getString(5));
        pub.setLat(cursor.getDouble(6));
        pub.setLng(cursor.getDouble(7));
        pub.setZipcode(cursor.getString(8));
      }
    } catch (Exception e) {
      cursor.close();
      db.close();
    } finally {
      cursor.close();
      db.close();
    }
    return pub;
  }

  public PubElement getPubById(long id) {
    SQLiteDatabase db = null;
    Cursor cursor = null;
    StringBuilder sql = new StringBuilder();
    PubElement pub = new PubElement();
    sql.append("SELECT * FROM ");
    sql.append(TABLE);
    sql.append(" WHERE ");
    sql.append(BaseColumns._ID);
    sql.append("=");
    sql.append(id);
    try {
      db = this.getReadableDatabase();
      cursor = db.rawQuery(sql.toString(), null);
      if (cursor.moveToFirst()) {
        pub.setId(cursor.getLong(0));
        pub.setName(cursor.getString(1));
        pub.setDescrip(cursor.getString(2));
        pub.setPhone(cursor.getString(3));
        pub.setLocation(cursor.getString(4));
        pub.setRating(cursor.getString(5));
        pub.setLat(cursor.getDouble(6));
        pub.setLng(cursor.getDouble(7));
        pub.setZipcode(cursor.getString(8));
      }
    } catch (Exception e) {
      cursor.close();
      db.close();
    } finally {
      cursor.close();
      db.close();
    }
    return pub;
  }

  public PubElement getPubByPhone(String phone) {
    SQLiteDatabase db = null;
    Cursor cursor = null;
    StringBuilder sql = new StringBuilder();
    PubElement pub = null;
    sql.append("SELECT * FROM ");
    sql.append(TABLE);
    sql.append(" WHERE ");
    sql.append(PHONE);
    sql.append("='");
    sql.append(phone);
    sql.append("'");
    try {
      db = this.getReadableDatabase();
      cursor = db.rawQuery(sql.toString(), null);
      if (cursor.moveToFirst()) {
        pub = new PubElement();
        pub.setId(cursor.getLong(0));
        pub.setName(cursor.getString(1));
        pub.setDescrip(cursor.getString(2));
        pub.setPhone(cursor.getString(3));
        pub.setLocation(cursor.getString(4));
        pub.setRating(cursor.getString(5));
        pub.setLat(cursor.getDouble(6));
        pub.setLng(cursor.getDouble(7));
        pub.setZipcode(cursor.getString(8));
      }
    } catch (Exception e) {
      cursor.close();
      db.close();
    } finally {
      cursor.close();
      db.close();
    }
    return pub;
  }

  public ArrayList<PubElement> getPubsByZipcode(String zipCode) {
    SQLiteDatabase db = null;
    Cursor cursor = null;
    StringBuilder sql = new StringBuilder();
    ArrayList<PubElement> pubList = new ArrayList();
    sql.append("SELECT * FROM ");
    sql.append(TABLE);
    sql.append(" WHERE zipcode='");
    sql.append(zipCode);
    sql.append("' order by name ASC");
    try {
      db = this.getReadableDatabase();
      cursor = db.rawQuery(sql.toString(), null);


      while (cursor.moveToNext()) {
        PubElement pub = new PubElement();
        pub.setId(cursor.getLong(0));
        pub.setName(cursor.getString(1));
        pub.setDescrip(cursor.getString(2));
        pub.setPhone(cursor.getString(3));
        pub.setLocation(cursor.getString(4));
        pub.setRating(cursor.getString(5));
        pub.setLat(cursor.getDouble(6));
        pub.setLng(cursor.getDouble(7));
        pub.setZipcode(cursor.getString(8));
        pubList.add(pub);
        Log.v(TAG, pub.toString());
      }
    } catch (Exception e) {
      cursor.close();
      db.close();
    } finally {
      cursor.close();
      db.close();
    }
    db.close();
    return pubList;
  }

  public boolean isTableEmpty() {
    SQLiteDatabase db = null;
    Cursor cursor = null;
    StringBuilder sql = new StringBuilder();
    boolean empty = true;
    sql.append("SELECT * FROM ");
    sql.append(TABLE);
    try {
      db = this.getReadableDatabase();
      cursor = db.rawQuery(sql.toString(), null);
      if (cursor.getCount() > 0) {
        empty = false;
      }
    } catch (Exception e) {
      cursor.close();
      db.close();
    } finally {
      cursor.close();
      db.close();
    }
    return empty;
  }
}
