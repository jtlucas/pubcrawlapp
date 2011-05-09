package android.pubcrawl;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.pubcrawl.database.CrawlDB;
import android.pubcrawl.database.CrawlPubElement;
import android.pubcrawl.database.LocalPubDB;
import android.pubcrawl.database.LocationDB;
import android.pubcrawl.database.PubElement;
import android.pubcrawl.maptools.LocationTools;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PubInformation extends Activity {

  private static final String TAG = PubInformation.class.getSimpleName();
  public final static String REQUESTOR = "REQUESTOR";
  public final static int PUBREQUESTID = 20;
  private Context conText;
  private long pubId;
  private int pubPos;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.pubinformation);

    conText = this.getApplicationContext();

    pubId = getIntent().getLongExtra(PubElement.PUBID, 0);
    pubPos = getIntent().getIntExtra(CrawlPubElement.PUBPOS, 0);
    String requestFrom = getIntent().getStringExtra(REQUESTOR);
    PubElement pub = null;

    Log.v(TAG, "GOT PUB ID OF:" + pubId);
    LocalPubDB locPubDB = new LocalPubDB(this.getApplicationContext());
    pub = locPubDB.getPubById(pubId);
    TextView tv = (TextView) findViewById(R.id.pubName);
    tv.setText(pub.getName());
    tv.setTextSize(24);
    Log.v(TAG, "SIZE:" + tv.getTextSize());
    ((TextView) findViewById(R.id.pubDescription)).setText(pub.getDescrip());
    ((TextView) findViewById(R.id.pubLocation)).setText(pub.getLocation());
    ((TextView) findViewById(R.id.pubRating)).setText(pub.getRating());

    if (pubId == 1) {
      ((TextView) findViewById(R.id.pubDescriptionTag)).setText("");
      LocationDB locDB = new LocationDB(this.getApplicationContext());
      Location loc = locDB.getLatestLocationEvent(true);
      StringBuilder locationString = new StringBuilder();
      locationString.append("Latitude:");
      locationString.append(loc.getLatitude());
      locationString.append("\n");
      locationString.append("Longitude:");
      locationString.append(loc.getLongitude());
      locationString.append("\n");
      locationString.append("ZipCode:");
      locationString.append(LocationTools.getZipCodeFromLocationGeoCoder(conText, loc));
      ((TextView) findViewById(R.id.pubLocation)).setText(
              locationString.toString());
      ((TextView) findViewById(R.id.pubRatingTag)).setText("");
    }
    if (requestFrom != null) {
      if (requestFrom.equalsIgnoreCase(PubListSearch.PUBLISTSEARCH)) {
        ((Button) findViewById(R.id.addRemove)).setText("Add to Crawl");
        ((Button) findViewById(R.id.addRemove)).setOnClickListener(
                new AddOnClickListener());
      } else if (requestFrom.equalsIgnoreCase(PubList.PUBLIST)) {
        ((Button) findViewById(R.id.addRemove)).setText("Remove from Crawl");
        ((Button) findViewById(R.id.addRemove)).setOnClickListener(
                new RemoveOnClickListener());
      }
    }

//HttpResponse response = null;
//    try{
//    HttpClient client = new DefaultHttpClient();
//    HttpPost post = new HttpPost("https://spreadsheets.google.com/
    //formResponse?formkey=dEkxcUt1QUFxUl9rcHlyVzZmdENROWc6MQ&amp;ifq");
//
//    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//    nameValuePairs.add(new BasicNameValuePair("entry.0.single","Razzys"));
//    nameValuePairs.add(new BasicNameValuePair("entry.1.single","Razzys1"));
//    nameValuePairs.add(new BasicNameValuePair("entry.2.single","Razzys2"));
//    nameValuePairs.add(new BasicNameValuePair("entry.3.single","Razzys3"));
//    nameValuePairs.add(new BasicNameValuePair("entry.4.single","Razzys4"));
//    nameValuePairs.add(new BasicNameValuePair("entry.5.single","Razzys5"));
//    nameValuePairs.add(new BasicNameValuePair("entry.6.single","Razzys6"));
//    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//    response = client.execute(post);

  }

  public class AddOnClickListener implements OnClickListener {

    public void onClick(View view) {
      CrawlDB crawlDB = new CrawlDB(conText);
      crawlDB.addPub(pubId);
      finish();
    }
  }

  public class RemoveOnClickListener implements OnClickListener {

    public void onClick(View view) {
      CrawlDB crawlDB = new CrawlDB(conText);
      crawlDB.removePub(pubId, pubPos);
      finish();
    }
  }

  @Override
  public void finish() {
    this.setResult(RESULT_OK);
    super.finish();
  }
}
