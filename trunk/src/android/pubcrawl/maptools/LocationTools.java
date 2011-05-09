package android.pubcrawl.maptools;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class LocationTools {

  private static final String TAG = LocationTools.class.getSimpleName();

  static public String getZipCodeFromLocationGeoCoder(Context con, Location loc) {
    String zipCode = "";
    try {
      Geocoder geocoder = new Geocoder(con, Locale.getDefault());
      List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
      zipCode = addresses.get(0).getPostalCode();
      return zipCode;
    } catch (Exception ex) {
      Log.v(TAG, "Geocodeaddressesr Failed!");
      Log.v(TAG, ex.getMessage());
      try {
        zipCode = getZipCodefromLocationJSON(con, loc);
        return zipCode;
      } catch (Exception e) {
        Log.v(TAG, "Backup JSON Failed!");
        Log.v(TAG, e.getMessage());
      }
    }
    return zipCode;
  }

  static public Location getLocationfromZipCodeGeoCoder(Context con, String zipCode) {
    Location loc = new Location(LocationManager.PASSIVE_PROVIDER);
    try {
      Geocoder geocoder = new Geocoder(con, Locale.getDefault());
      List<Address> addresses = geocoder.getFromLocationName(zipCode, 1);
      loc.setTime(System.currentTimeMillis());
      loc.setLatitude(addresses.get(0).getLatitude());
      loc.setLongitude(addresses.get(0).getLongitude());
      return loc;
    } catch (Exception ex) {
      Log.v(TAG, "Geocodeaddressesr Failed!");
      Log.v(TAG, ex.getMessage());
      try {
        loc = getLocationfromZipCodeJSON(con, zipCode);
        return loc;
      } catch (Exception e) {
        Log.v(TAG, "Backup JSON Failed!");
        Log.v(TAG, e.getMessage());
      }
    }
    return loc;
  }

  static public String getZipCodefromLocationJSON(Context con, Location loc) {
    HttpClient client = new DefaultHttpClient();
    HttpGet get = null;
    HttpResponse response = null;
    String zipCode = "";

    StringBuilder googleSearch = new StringBuilder();
    googleSearch.append("http://maps.google.com/maps/api/geocode/json?");
    googleSearch.append("latlng=");
    googleSearch.append(loc.getLatitude());
    googleSearch.append(",");
    googleSearch.append(loc.getLongitude());
    googleSearch.append("&sensor=false");

    Log.v(TAG, googleSearch.toString());

    try {
      get = new HttpGet(googleSearch.toString());
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
      Log.v(TAG, e.getMessage());
    }

    try {
      JSONObject object = (JSONObject) new JSONTokener(
              responseBuilder.toString()).nextValue();
              Log.v(TAG, "Got JSONObject!");
      JSONObject results = object.getJSONArray("results").getJSONObject(0);
      JSONArray addressComponents = results.getJSONArray("address_components");
      Log.v(TAG, "Get array of AddressComponents" + addressComponents.length());
      for(int i = 0; i < addressComponents.length(); i++){
        JSONObject address = addressComponents.getJSONObject(i);
        Log.v(TAG, "Stepping through JSON addressComponents");
        if(address.getJSONArray("types").getString(0).equalsIgnoreCase("postal_code")){
          zipCode = address.getString("short_name");
          Log.v(TAG, "Found postalCode and pulled Short_name");
          break;
        }
      }
    } catch (Exception e) {
      Log.v(TAG, e.getMessage());
    }
    Log.v(TAG, "Returning zipCode:" + zipCode);
   return zipCode;
  }

  static public Location getLocationfromZipCodeJSON(Context con, String zipCode) {
    HttpClient client = new DefaultHttpClient();
    HttpGet get = null;
    HttpResponse response = null;
    Location loc = new Location(LocationManager.PASSIVE_PROVIDER);

    StringBuilder googleSearch = new StringBuilder();
    googleSearch.append("http://maps.google.com/maps/api/geocode/json?");
    googleSearch.append("address=");
    googleSearch.append(zipCode);
    googleSearch.append("&sensor=false");

    Log.v(TAG, googleSearch.toString());

    try {
      get = new HttpGet(googleSearch.toString());
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
      Log.v(TAG, e.getMessage());
    }

    try {
      JSONObject object = (JSONObject) new JSONTokener(
              responseBuilder.toString()).nextValue();

      JSONObject results = object.getJSONArray("results").getJSONObject(0);
      JSONObject geometry = results.getJSONObject("geometry");
      JSONObject location = geometry.getJSONObject("location");
      loc.setTime(System.currentTimeMillis());
      loc.setLatitude(location.getDouble("lat"));
      loc.setLongitude(location.getDouble("lng"));
    } catch (Exception e) {
      Log.v(TAG, e.getMessage());
    }

    return loc;
  }
}
