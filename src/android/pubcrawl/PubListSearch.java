package android.pubcrawl;

import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.pubcrawl.database.LocalPubDB;
import android.pubcrawl.database.LocationDB;
import android.pubcrawl.database.PubElement;
import android.pubcrawl.maptools.LocationTools;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class PubListSearch extends ListActivity implements OnItemClickListener {

  private static final String TAG = PubListSearch.class.getSimpleName();
  public final static String PUBLISTSEARCH = "publistsearch";
  private ArrayAdapter<String> pubListArr;
  private ArrayList<String> pubList = new ArrayList<String>();
  private ArrayList<PubElement> pubListData = new ArrayList<PubElement>();
  private ListView listView;
  private Context conText;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.v(TAG, "Super onCreate Finished!");

    conText = this.getApplicationContext();

    pubListArr = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, pubList);
    setListAdapter(pubListArr);

    listView = getListView();

    listView.setTextFilterEnabled(true);
    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    listView.setOnItemClickListener(this);

    LocationDB locDB = new LocationDB(this.getApplicationContext());
    Location loc = locDB.getLatestUserEvent();
    if (loc != null && loc.getTime() != 0) {
      String zip = LocationTools.getZipCodeFromLocationGeoCoder(conText,loc);
      if (!zip.equalsIgnoreCase("")) {
        loadPubList(zip);
      }
    }
  }

  @Override
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
    Log.v(TAG, pubListData.get(position).toString());
    Intent intent = new Intent(this, PubInformation.class);
    intent.putExtra(PubElement.PUBID, pubListData.get(position).getId());
    intent.putExtra(PubInformation.REQUESTOR, PUBLISTSEARCH);
    startActivityForResult(intent, PubInformation.PUBREQUESTID);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.v(TAG, "onCreateOptionsMenu Started!");
    MenuInflater inflater = getMenuInflater();
    Log.v(TAG, "getMenuInflater() Finished!");
    inflater.inflate(R.layout.pubmenu, menu);
    Log.v(TAG, "Inflate Finished!");
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent i;
    LocationDB locDB = new LocationDB(conText);
    Location loc = locDB.getLatestLocationEvent(true);
    String zipCode = "";
    switch (item.getItemId()) {
      case R.id.zipCode:
        Log.v(TAG, "ZipCode Choosen!");
        i = new Intent(this, SearchZipCode.class);
        Log.v(TAG, "Intent Created!");
        i.putExtra("requestCode", SearchZipCode.ZIP_REQUEST);
        startActivityForResult(i, SearchZipCode.ZIP_REQUEST);
        Log.v(TAG, "startActivityForResult Finished!");
        return true;
      case R.id.currentLoc:
        Log.v(TAG, "Cell Choosen!");
        loc = locDB.getLatestLocationEvent(false);
        locDB.addUserEvent(loc);
        zipCode = LocationTools.getZipCodeFromLocationGeoCoder(conText,loc);
        Log.v(TAG, "Got " + zipCode + "!");
        Toast.makeText(getApplicationContext(), "ZipCode Search:" + zipCode,
                Toast.LENGTH_SHORT).show();
        loadPubList(zipCode);
        return true;
      case R.id.pubRefresh:
        loc = locDB.getLatestLocationEvent(true);
        zipCode = LocationTools.getZipCodeFromLocationGeoCoder(conText,loc);
        new getPubInformationTask().execute(zipCode);
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    LocationDB locDB = new LocationDB(conText);
    String zipCode = "";
    if (resultCode == RESULT_OK && requestCode == SearchZipCode.ZIP_REQUEST) {
      zipCode = (String) data.getExtras().get(SearchZipCode.SEARCH_ZIP);
      Log.v(TAG, "Got " + zipCode + "!");
      locDB.addUserEvent(LocationTools.getLocationfromZipCodeGeoCoder(conText,zipCode));
      Toast.makeText(getApplicationContext(), "ZipCode Search:" + zipCode,
              Toast.LENGTH_SHORT).show();
      loadPubList(zipCode);
      return;
    }
  }

  public void loadPubList(String zipCode) {
    LocalPubDB pubDB = new LocalPubDB(this.getApplicationContext());
    ArrayList<PubElement> tmpList = pubDB.getPubsByZipcode(zipCode);

    pubList.clear();
    pubListData.clear();
    pubListArr.notifyDataSetChanged();

    for (int i = 0; i < tmpList.size(); i++) {
      if(tmpList.get(i).getId() == 1) continue;
      pubList.add(tmpList.get(i).getName());
      pubListData.add(tmpList.get(i));
    }

    pubListArr.notifyDataSetChanged();
  }

  protected class getPubInformationTask extends AsyncTask<Object, Integer, String> {

    private String zipCode;

    @Override
    protected String doInBackground(Object... paramss) {
      zipCode = (String) paramss[0];

      getPubCrawlDBData(zipCode);

      return "COMPLETE!";
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      loadPubList(zipCode);
    }

    public void getPubCrawlDBData(String zipCode) {
      pubCrawlGoogleDocumentsDatbase();
      yelpDBData(zipCode, null);
      googleYelpDBData(zipCode);
    }

    //Need to work on seeing if this is query-able
    private void pubCrawlGoogleDocumentsDatbase() {
      try {
        LocalPubDB pubDB = new LocalPubDB(conText);
        URL url = new URL("https://spreadsheets.google.com/pub?hl=en&hl=en&key="
                + "0AsEdmHSO4TKYdEkxcUt1QUFxUl9rcHlyVzZmdENROWc"
                + "&single=true&gid=0&output=csv");
        //Timestamp,PubName,PubDescription,PubLocation,
        //PubRating,PubZipCode,PubLat,PubLong,PubPhone
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));
        String inputLine;
        int i = 0;
        while ((inputLine = in.readLine()) != null) {
          if (i == 0) {
            i++;
            continue;
          } //Remove titles
          String[] pubInfo = inputLine.split(",");
          //public void addPub(String name, String description, String phone,
          //String location, String rating, double lat, double lng, int zipcode)
          try {
            pubDB.addPub(pubInfo[1], pubInfo[2], cleanPhoneNumber(pubInfo[8]),
                    pubInfo[3], pubInfo[4],
                    Double.parseDouble(pubInfo[6]), Double.parseDouble(pubInfo[7]),
                    pubInfo[5]);
          } catch (Exception e) {
            Log.v(TAG, "Failed to add:" + e.getMessage());
          }
        }
        in.close();
        Log.v(TAG, "Loaded localpubDB!");
      } catch (Exception ex) {
        Log.v(TAG, ex.getMessage());
      }
    }

    public String cleanPhoneNumber(String phone) {
      String result = phone;
      result = result.replace("(", "");
      result = result.replace(")", "");
      result = result.replace("-", "");
      result = result.replace(" ", "");
      return result;
    }

    public ArrayList<String> googleParse(String parseString) {
      ArrayList<String> pubNumbers = new ArrayList<String>();
      LocalPubDB pubDB = new LocalPubDB(conText);
      String numPrefix = "<a href=\"wtai://wp/mc;";
      String afterTel = "\"";

      Log.v(TAG, "PhoneNumberAdded: " + parseString);
      try {
        String[] splitNumbers = parseString.split(numPrefix);
        String number = "";
        //throw away the first one.
        for (int i = 1; i < splitNumbers.length; i++) {
          number = splitNumbers[i].substring(0, splitNumbers[i].indexOf(afterTel));
          if(pubDB.getPubByPhone(cleanPhoneNumber(number)) == null)
            pubNumbers.add(number);
          Log.v(TAG, "PhoneNumberAdded: " + number);
        }

      } catch (Exception e) {
        Log.v(TAG, "googleParse Failed!: " + e.getMessage());
      }

      return pubNumbers;
    }

    public void yelpParse(String parseString) {
      LocalPubDB pubDB = new LocalPubDB(conText);
      Log.v(TAG, "yelpParse");
      try {
        JSONObject object = (JSONObject) new JSONTokener(parseString).nextValue();
        JSONArray pubs = object.getJSONArray("businesses");
        StringBuilder addPubString = new StringBuilder();

        for (int i = 0; i < pubs.length(); i++) {
          //public void addPub(String name, String description, String location,
          //  String rating, double lat, double lng, int zipcode) {
          addPubString.append(pubs.getJSONObject(i).getString("name"));
          addPubString.append(",");
          //Reviews save for a rainy day, Since we keep this data on the phone
          // We don't want to not have anyway of refreshing this data easily
//        JSONArray review = pubs.getJSONObject(i).getJSONArray("reviews");
//        int randReview = (int) (review.length() * Math.random());
//        if (randReview >= review.length()) {
//          randReview = 0;
//        }
//        ((JSONObject) review.get(randReview)).getString("text_excerpt");
          JSONArray categories = pubs.getJSONObject(i).getJSONArray("categories");
          StringBuilder catList = new StringBuilder();
          for (int j = 0; j < categories.length(); j++) {
            catList.append(((JSONObject) categories.get(j)).getString("name"));
            catList.append(";");
          }
          addPubString.append(catList.toString());
          addPubString.append(",");
          StringBuilder addr = new StringBuilder();
          addr.append(pubs.getJSONObject(i).getString("address1"));
          addr.append(pubs.getJSONObject(i).getString("address2"));
          addr.append(pubs.getJSONObject(i).getString("address3"));
          addr.append("\n");
          addr.append(pubs.getJSONObject(i).getString("city"));
          addr.append(",");
          addr.append(pubs.getJSONObject(i).getString("state_code"));
          addr.append("   ");
          addr.append(pubs.getJSONObject(i).getString("zip"));
          addPubString.append(addr.toString());
          addPubString.append(",");
          String rating = pubs.getJSONObject(i).getString("rating_img_url");
          String starRating = "";
          if (rating.contains("stars") && rating.contains("1.png")) {
            starRating = "1";
          } else if (rating.contains("stars") && rating.contains("2.png")) {
            starRating = "2";
          } else if (rating.contains("stars") && rating.contains("3.png")) {
            starRating = "3";
          } else if (rating.contains("stars") && rating.contains("4.png")) {
            starRating = "4";
          } else if (rating.contains("stars") && rating.contains("5.png")) {
            starRating = "5";
          }
          addPubString.append(",");
          addPubString.append(pubs.getJSONObject(i).getDouble("latitude"));
          addPubString.append(",");
          addPubString.append(pubs.getJSONObject(i).getDouble("longitude"));
          addPubString.append(",");
          addPubString.append(pubs.getJSONObject(i).getString("zip"));
          addPubString.append(",");

          try {
            pubDB.addPub(
                    pubs.getJSONObject(i).getString("name"),
                    catList.toString(),
                    cleanPhoneNumber(pubs.getJSONObject(i).getString("phone")),
                    addr.toString(),
                    starRating,
                    pubs.getJSONObject(i).getDouble("latitude"),
                    pubs.getJSONObject(i).getDouble("longitude"),
                    pubs.getJSONObject(i).getString("zip"));
          } catch (Exception e) {
            Log.v(TAG, "Failed to add:" + e.getMessage());
          }

          Log.v(TAG, addPubString.toString());

          addPubString.delete(0, addPubString.length());
        }
      } catch (Exception e) {
        Log.v(TAG, e.getMessage());
      }
    }

    private void googleYelpDBData(String zipCode) {
      HttpClient client = new DefaultHttpClient();
      HttpGet get = null;
      HttpResponse response = null;

      StringBuilder googleSearch = new StringBuilder();
      googleSearch.append("http://maps.google.com/m?ie=UTF-8&q=pub+near+");
      googleSearch.append(zipCode);
      googleSearch.append("&oi=nojs");

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

      ArrayList<String> pubNumbers = googleParse(responseBuilder.toString());

      for (int i = 0; i < pubNumbers.size(); i++) {
        yelpDBData(null, cleanPhoneNumber(pubNumbers.get(i)));
      }

    }

    private void yelpDBData(String zipCode, String phoneNumber) {
      StringBuilder yelpSearch = new StringBuilder();
      if (zipCode != null) {
        yelpSearch.append("http://api.yelp.com/business_review_search?");
        yelpSearch.append("term=pubs&location=");
        yelpSearch.append(zipCode);
        yelpSearch.append("&radius=2&limit=20&ywsid=fnW_2aT3pH_iN_GN5Aw_Eg");
      } else if (phoneNumber != null) {
        yelpSearch.append("http://api.yelp.com/phone_search?phone=");
        yelpSearch.append(phoneNumber);
        yelpSearch.append("&ywsid=fnW_2aT3pH_iN_GN5Aw_Eg");
      }
      Log.v(TAG, yelpSearch.toString());

      HttpClient client = new DefaultHttpClient();
      HttpGet get = new HttpGet(yelpSearch.toString());
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
        Log.v(TAG, e.getMessage());
      }

      yelpParse(responseBuilder.toString());
    }
  }
}
