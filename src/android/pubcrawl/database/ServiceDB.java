package android.pubcrawl.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.pubcrawl.PubCrawl;
import android.util.Log;

public class ServiceDB extends SQLiteOpenHelper {

  private static final String TAG = ServiceDB.class.getSimpleName();
  private static final String DATABASE_NAME = ServiceDB.class.getSimpleName() + ".db";
  private static final int DATABASE_VERSION = 1;

  public static enum STATUSOPT {

    STARTED, STOPPED
  }
  // Table name
  public static final String TABLE = "pubServices";
  // Columns
  public static final String TIME = "time";
  public static final String NAME = "name";
  public static final String STATUS = "status";

  public ServiceDB(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    StringBuilder sql = new StringBuilder();
    sql.append("create table ");
    sql.append(TABLE);
    sql.append("(" + BaseColumns._ID + " integer primary key autoincrement, ");
    sql.append(TIME + " integer, ");
    sql.append(NAME + " text not null, ");
    sql.append(STATUS + " text not null);");
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

  public void addService(String name, String status) {
    SQLiteDatabase db = null;
    ContentValues values = new ContentValues();
    values.put(TIME, System.currentTimeMillis());
    values.put(NAME, name);
    values.put(STATUS, status);
    try {
      db = this.getWritableDatabase();
      db.insert(TABLE, null, values);
    } catch (Exception e) {
      db.close();
    } finally {
      db.close();
    }

    Log.v(TAG, "addService:" + values.toString());
  }

  //We need to find app start so we know the latest and greatest!!
  public ServiceElement getLatestServiceStatus(String service) {
    SQLiteDatabase db = null;
    Cursor cursor = null;
    StringBuilder sql = new StringBuilder();
    ServiceElement serv = new ServiceElement();
    sql.append("SELECT * FROM ");
    sql.append(TABLE);
    sql.append(" WHERE ");
    sql.append(BaseColumns._ID);
    sql.append(">=( SELECT ");
    sql.append(BaseColumns._ID);
    sql.append(" FROM ");
    sql.append(TABLE);
    sql.append(" WHERE name='");
    sql.append(PubCrawl.class.getSimpleName());
    sql.append("' ORDER BY ");
    sql.append(BaseColumns._ID);
    sql.append(" DESC LIMIT 1)");
    sql.append("AND name='");
    sql.append(service);
    sql.append("' ORDER BY ");
    sql.append(BaseColumns._ID);
    sql.append(" DESC LIMIT 10");
    try {
      db = this.getReadableDatabase();
      cursor = db.rawQuery(sql.toString(), null);

      if (cursor.moveToFirst()) {
        serv.setServiceID(cursor.getLong(0));
        serv.setTime(cursor.getLong(1));
        serv.setName(cursor.getString(2));
        serv.setStatus(cursor.getString(3));
        Log.v(TAG, serv.toString());
      }
    } catch (Exception e) {
      cursor.close();
      db.close();
    } finally {
      cursor.close();
      db.close();
    }
    return serv;
  }
}
