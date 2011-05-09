package android.pubcrawl;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.pubcrawl.database.CrawlDB;
import android.pubcrawl.database.CrawlPubElement;
import android.pubcrawl.database.LocalPubDB;
import android.pubcrawl.database.LocationDB;
import android.pubcrawl.database.PubElement;
import android.pubcrawl.maptools.PubOverlay;
import android.pubcrawl.maptools.RouteOverlay;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Map extends MapActivity {

  private static final String TAG = Map.class.getSimpleName();
  private MapView mapView;
  private Context conText;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.v(TAG, "Super onCreate Finished!");
    setContentView(R.layout.map);
    Log.v(TAG, "setContentView Finished!");
    mapView = (MapView) findViewById(R.id.map);
    mapView.getController().setZoom(10);
    LocationDB locDB = new LocationDB(this.getApplicationContext());
    Location loc = locDB.getLatestLocationEvent(true);
    GeoPoint cntPnt = new GeoPoint(
            (int) (loc.getLatitude() * 1e6), (int) (loc.getLongitude() * 1e6));
    mapView.getController().animateTo(cntPnt);
    Log.v(TAG, "findViewById Finished!");
    this.conText = this.getApplicationContext();
  }

  @Override
  protected void onResume() {
    super.onResume();
    populateMap();
  }

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.v(TAG, "onCreateOptionsMenu Started!");
    MenuInflater inflater = getMenuInflater();
    Log.v(TAG, "getMenuInflater() Finished!");
    inflater.inflate(R.layout.mapmenu, menu);
    Log.v(TAG, "Inflate Finished!");
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.mapRefresh:
        populateMap();
        break;
      case R.id.mapCurrentLocation:
        CrawlDB crawlDB = new CrawlDB(this.getApplicationContext());
        crawlDB.addPub(1, 0);
        populateMap();
        break;
    }
    return true;
  }

  public void populateMap() {
    mapView.getOverlays().clear();
    CrawlDB crawlDB = new CrawlDB(this.getApplicationContext());
    LocalPubDB pubDB = new LocalPubDB(this.getApplicationContext());
    PubOverlay pubOverlay = new PubOverlay(this, this.getResources().getDrawable(
            R.drawable.beerloc));

    ArrayList<CrawlPubElement> pubList = crawlDB.getCrawlPubs();
    PubElement pub = null;
    for (int i = 0; i < pubList.size(); i++) {
      if (i == 0 && pubList.get(0).getPubID() == 1) {
        pub = new PubElement();
        LocationDB locDB = new LocationDB(this.getApplicationContext());
        Location loc = locDB.getLatestLocationEvent(false);
        pub.setName("Current Location");
        pub.setLat(loc.getLatitude());
        pub.setLng(loc.getLongitude());
      } else {
        pub = pubDB.getPubById(pubList.get(i).getPubID());
      }
      GeoPoint point = new GeoPoint(
              (int) (pub.getLat() * 1e6), (int) (pub.getLng() * 1e6));
      if (i == 0) {
        mapView.getController().setZoom(15);
        mapView.getController().animateTo(point);
      }
      OverlayItem overlayitem = new OverlayItem(point, pub.getName(), null);
      pubOverlay.addOverlay(overlayitem);
    }
    pubOverlay.populateAll();
    mapView.getOverlays().add(pubOverlay);
    getRoute();
  }

  public void getRoute() {
    CrawlDB crawlDB = new CrawlDB(conText);
    LocalPubDB pubDB = new LocalPubDB(conText);
    StringBuilder directionHTML = new StringBuilder();

    directionHTML.append("http://maps.googleapis.com/maps/api/directions/json");
    ArrayList<CrawlPubElement> pubList = crawlDB.getCrawlPubs();
    PubElement pub = null;

    if (pubList.size() < 2) {
      return;
    } else if (pubList.size() >= 2) {
      if (pubList.get(0).getPubID() == 1) {
        pub = new PubElement();
        LocationDB locDB = new LocationDB(conText);
        Location loc = locDB.getLatestLocationEvent(false);
        pub.setLat(loc.getLatitude());
        pub.setLng(loc.getLongitude());
      } else {
        pub = pubDB.getPubById(pubList.get(0).getPubID());
      }
      directionHTML.append("?origin=");
      directionHTML.append(pub.getLat());
      directionHTML.append(",");
      directionHTML.append(pub.getLng());
      pub = pubDB.getPubById(pubList.get(pubList.size() - 1).getPubID());
      directionHTML.append("&destination=");
      directionHTML.append(pub.getLat());
      directionHTML.append(",");
      directionHTML.append(pub.getLng());
      if (pubList.size() > 2) {
        directionHTML.append("&waypoints=");
        for (int i = 1; i < pubList.size() - 1; i++) {
          pub = pubDB.getPubById(pubList.get(i).getPubID());
          directionHTML.append(pub.getLat());
          directionHTML.append(",");
          directionHTML.append(pub.getLng());
          if (i != pubList.size() - 2) {
            directionHTML.append("%7C");  // HTTP char for |
          }
        }
      }
    }
    directionHTML.append("&mode=walking");
    directionHTML.append("&sensor=false");

    Log.v(TAG, directionHTML.toString());

    HttpClient client = new DefaultHttpClient();
    HttpGet get = new HttpGet(directionHTML.toString());
    HttpResponse response = null;
    try {
      response = client.execute(get);
    } catch (Exception e) {
      Log.v(TAG, e.getMessage());
    }
    StringBuilder responseBuilder = new StringBuilder();
    try {
      BufferedReader in = new BufferedReader(
              new InputStreamReader(response.getEntity().getContent()));
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        responseBuilder.append(inputLine);
      }
      in.close();
    } catch (Exception e) {
      Log.v(TAG, "MAP DIRECTION FALED!:" + e.getMessage());
    }

    try {
      JSONObject object = (JSONObject) new JSONTokener(
              responseBuilder.toString()).nextValue();
      JSONObject routes = object.getJSONArray("routes").getJSONObject(0);
      JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);

      JSONArray steps = legs.getJSONArray("steps");
      for (int i = 0; i < steps.length(); i++) {
        Log.v(TAG, steps.getJSONObject(i).getString("travel_mode"));
      }
      Log.v(TAG, routes.getString("summary"));

      Log.v(TAG, routes.getJSONObject("overview_polyline").getString("points"));
      ArrayList<GeoPoint> gpList = decodePolyLine(
              routes.getJSONObject("overview_polyline").getString("points"));

      RouteOverlay routeOverlay = new RouteOverlay(gpList, 2);
      mapView.getOverlays().add(routeOverlay);
    } catch (Exception e) {
      Log.v(TAG, e.getMessage());
    }
  }

  //Decode encoded polyline points
  // based from: http://code.google.com/apis/maps/documentation/utilities/polylinealgorithm.html
  // http://unitstep.net/blog/2008/08/02/decoding-google-maps-encoded-polylines-using-php/
  // http://facstaff.unca.edu/mcmcclur/GoogleMaps/EncodePolyline/decode.js
  private ArrayList<GeoPoint> decodePolyLine(String encodedPolyLine) {

    ArrayList<GeoPoint> geoList = new ArrayList<GeoPoint>();
    int c, i = 0, shift = 0, result = 0;
    double dlat = 0, lat = 0, tlat = 0;
    double dlng = 0, lng = 0, tlng = 0;

    while (i < encodedPolyLine.length()) {
      shift = 0;
      result = 0;
      dlat = 0;
      dlng = 0;
      do {
        c = (encodedPolyLine.codePointAt(i++) - 63);
        result |= (c & 0x1f) << shift;
        shift += 5;
      } while (c >= 0x20);
      if ((result & 1) == 1) {
        dlat = ~(result >> 1);
      } else {
        dlat = (result >> 1);
      }
      lat += dlat;
      shift = 0;
      result = 0;
      do {
        c = (encodedPolyLine.codePointAt(i++) - 63);
        result |= (c & 0x1f) << shift;
        shift += 5;
      } while (c >= 0x20);
      if ((result & 1) == 1) {
        dlng = ~(result >> 1);
      } else {
        dlng = (result >> 1);
      }
      lng += dlng;

      tlat = lat * 1e-5;
      tlng = lng * 1e-5;

      GeoPoint geoPnt = new GeoPoint((int) (tlat * 1e6), (int) (tlng * 1e6));
      geoList.add(geoPnt);
    }
    return geoList;
  }
}
