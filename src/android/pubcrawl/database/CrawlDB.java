package android.pubcrawl.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import java.util.ArrayList;

public class CrawlDB extends SQLiteOpenHelper {

  private static final String TAG = CrawlDB.class.getSimpleName();
  private static final String DATABASE_NAME = CrawlDB.class.getSimpleName() + ".db";
  private static final int DATABASE_VERSION = 1;
  // Table name
  public static final String TABLE = "crawl";
  // Columns
  public static final String PUBID = "pubid";
  public static final String POSITION = "position";

  public CrawlDB(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    StringBuilder sql = new StringBuilder();
    sql.append("create table ");
    sql.append(TABLE);
    sql.append("(" + BaseColumns._ID + " integer primary key autoincrement, ");
    sql.append(PUBID + " integer not null, ");
    sql.append(POSITION + " integer not null) ");
    db.execSQL(sql.toString());
    Log.d(TAG, "onCreate: " + sql.toString());
    return;
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
    return;
  }

  public void addPub(long pubId, int position) {
    SQLiteDatabase db = null;
    ContentValues values = new ContentValues();
    values.put(PUBID, pubId);
    values.put(POSITION, position);
    try {
      db = this.getWritableDatabase();
      db.insert(TABLE, null, values);
    } catch (Exception e) {
      db.close();
    } finally {
      db.close();
    }
    Log.v(TAG, "addPub:" + values.toString());
    return;
  }

  public void addPub(long pubId) {
    SQLiteDatabase db = null;
    ContentValues values = new ContentValues();
    values.put(PUBID, pubId);
    values.put(POSITION, getNextPosition());
    try {
      db = this.getWritableDatabase();
      db.insert(TABLE, null, values);
    } catch (Exception e) {
      db.close();
    } finally {
      db.close();
    }
    Log.v(TAG, "addPub:" + values.toString());
    return;
  }

  public int getNextPosition() {
    SQLiteDatabase db = null;
    Cursor cursor = null;
    StringBuilder sql = new StringBuilder();
    int retVal = 1;
    sql.append("SELECT position FROM ");
    sql.append(TABLE);
    sql.append(" order by position DESC limit 1");
    try {
      db = this.getReadableDatabase();
      cursor = db.rawQuery(sql.toString(), null);
      if (cursor.moveToFirst()) {
        retVal = cursor.getInt(0) + 1;
      }
    } catch (Exception e) {
      cursor.close();
      db.close();
    } finally {
      cursor.close();
      db.close();
    }
    return retVal;
  }

  public ArrayList<CrawlPubElement> getCrawlPubs() {
    SQLiteDatabase db = null;
    Cursor cursor = null;
    StringBuilder sql = new StringBuilder();
    ArrayList<CrawlPubElement> pubList = new ArrayList();
    sql.append("SELECT * FROM ");
    sql.append(TABLE);
    sql.append(" order by position ASC");
    try {
      db = this.getReadableDatabase();
      cursor = db.rawQuery(sql.toString(), null);
      while (cursor.moveToNext()) {
        CrawlPubElement pub = new CrawlPubElement();
        pub.setCrawlPubID(cursor.getLong(0));
        pub.setPubID(cursor.getLong(1));
        pub.setPosition(cursor.getInt(2));
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
    return pubList;
  }

  public CrawlPubElement getCrawlPubByPosition(long pos) {
    SQLiteDatabase db = null;
    Cursor cursor = null;
    StringBuilder sql = new StringBuilder();
    CrawlPubElement pub = new CrawlPubElement();
    sql.append("SELECT * FROM ");
    sql.append(TABLE);
    sql.append(" WHERE ");
    sql.append(POSITION);
    sql.append("=");
    sql.append(pos);
    try {
      db = this.getReadableDatabase();
      cursor = db.rawQuery(sql.toString(), null);
      if (cursor.moveToFirst()) {
        pub.setCrawlPubID(cursor.getLong(0));
        pub.setPubID(cursor.getLong(1));
        pub.setPosition(cursor.getInt(2));
        Log.v(TAG, pub.toString());
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

  public void clearCrawlDB() {
    SQLiteDatabase db = null;
    StringBuilder sql = new StringBuilder();
    sql.append("DELETE FROM ");
    sql.append(TABLE);
    try {
      db = this.getWritableDatabase();
      db.execSQL(sql.toString());
    } catch (Exception e) {
      db.close();
    } finally {
      db.close();
    }
    Log.v(TAG, "clearPubDB called!");
    return;
  }

  public void removePubs(long pubId) {
    SQLiteDatabase db = null;
    StringBuilder sql = new StringBuilder();
    sql.append("DELETE FROM ");
    sql.append(TABLE);
    sql.append(" WHERE pubID=");
    sql.append(pubId);
    try {
      db = this.getWritableDatabase();
      db.execSQL(sql.toString());
    } catch (Exception e) {
      db.close();
    } finally {
      db.close();
    }
    return;
  }

  public void removePub(long pubId, int pubPos) {
    SQLiteDatabase db = null;
    StringBuilder sql = new StringBuilder();
    sql.append("DELETE FROM ");
    sql.append(TABLE);
    sql.append(" WHERE ");
    sql.append(PUBID);
    sql.append("=");
    sql.append(pubId);
    sql.append(" AND ");
    sql.append(POSITION);
    sql.append("=");
    sql.append(pubPos);
    try {
      db = this.getWritableDatabase();
      db.execSQL(sql.toString());
    } catch (Exception e) {
      db.close();
    } finally {
      db.close();
    }
    return;
  }

  public void swapPosition(int fromPos, int toPos) {
    CrawlPubElement pub1 = getCrawlPubByPosition(fromPos);
    CrawlPubElement pub2 = getCrawlPubByPosition(toPos);

    SQLiteDatabase db = null;
    StringBuilder where = new StringBuilder();
    where.append(BaseColumns._ID);
    where.append("=?");

    String[] whereArgsPos1 = {String.valueOf(pub1.getCrawlPubID())};
    ContentValues valuesPos1 = new ContentValues();
    valuesPos1.put(PUBID, pub1.getPubID());
    valuesPos1.put(POSITION, toPos);
    Log.v(TAG, where.toString() + whereArgsPos1[0] + valuesPos1.toString());


    String[] whereArgsPos2 = {String.valueOf(pub2.getCrawlPubID())};
    ContentValues valuesPos2 = new ContentValues();
    valuesPos2.put(PUBID, pub2.getPubID());
    valuesPos2.put(POSITION, fromPos);
    Log.v(TAG, where.toString() + whereArgsPos2[0] + valuesPos2.toString());
    try {
      db = this.getWritableDatabase();
      db.update(TABLE, valuesPos1, where.toString(), whereArgsPos1);
      db.update(TABLE, valuesPos2, where.toString(), whereArgsPos2);
    } catch (Exception e) {
      db.close();
    } finally {
      db.close();
    }
  }

  public String dumpDB() {
    StringBuilder dbDump = new StringBuilder();
    ArrayList<CrawlPubElement> pubList = getCrawlPubs();

    for (int i = 0; i < pubList.size(); i++) {
      if (pubList.get(i).getPubID() == 1) {
        continue;
      }
      dbDump.append(pubList.get(i).getPubID());
      dbDump.append(",");
      dbDump.append(pubList.get(i).getPosition());
      dbDump.append("\n");
    }
    return dbDump.toString();
  }

  public void loadDB(String dbLoad) {
    clearCrawlDB();
    String[] pubList = dbLoad.split("\n");
    String[] pubValues = null;
    for (int i = 0; i < pubList.length; i++) {
      pubValues = pubList[i].split(",");
      addPub(Long.parseLong(pubValues[0]), Integer.parseInt(pubValues[1]));
    }
  }
}
