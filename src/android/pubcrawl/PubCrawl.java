package android.pubcrawl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.pubcrawl.database.ServiceDB;
import android.pubcrawl.services.CellService;
import android.pubcrawl.services.GpsService;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class PubCrawl extends TabActivity {

  private static final String TAG = PubCrawl.class.getSimpleName();

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.v(TAG, "Super onCreate Finished!");
    setContentView(R.layout.tabmain);
    Log.v(TAG, "setContentView Finished");

    AlertDialog.Builder dialog = new AlertDialog.Builder(this);

    dialog.setTitle("Welcome to Pub Crawl!");

    StringBuilder welcomeText = new StringBuilder();
    welcomeText.append("Remember: \n");
    welcomeText.append("\"Drunk Driving, Over the Limit. Under Arrest\"");
    welcomeText.append(" - MADD\n\n");
    welcomeText.append("\"You Drink, You Drive, You Lose\" - NHTSA\n\n");
    welcomeText.append("Please drink responsibly!\n\n");
    welcomeText.append("Enjoy!");
    dialog.setMessage(welcomeText.toString());
    dialog.setNeutralButton("Ok", null);
    dialog.show();

    Resources res = getResources(); // Resource object to get Drawables
    TabHost tabHost = getTabHost();  // The activity TabHost
    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
    Intent intent;  // Reusable Intent for each tab

    // Create an Intent to launch an Activity for the tab (to be reused)
    intent = new Intent().setClass(this, Map.class);
    // Initialize a TabSpec for each tab and add it to the TabHost
    spec = tabHost.newTabSpec("Pub Map").setIndicator("Pub Map",
            res.getDrawable(R.drawable.world)).setContent(intent);
    tabHost.addTab(spec);
    // Create an Intent to launch an Activity for the tab (to be reused)
    intent = new Intent().setClass(this, PubList.class);
    // Initialize a TabSpec for each tab and add it to the TabHost
    spec = tabHost.newTabSpec("Pub List").setIndicator("Pub List",
            res.getDrawable(R.drawable.beer)).setContent(intent);
    tabHost.addTab(spec);
    // Create an Intent to launch an Activity for the tab (to be reused)
    intent = new Intent().setClass(this, PubListSearch.class);
    // Initialize a TabSpec for each tab and add it to the TabHost
    spec = tabHost.newTabSpec("Local Pubs").setIndicator("Local Pubs",
            res.getDrawable(R.drawable.beerfolder)).setContent(intent);
    tabHost.addTab(spec);
    // Create an Intent to launch an Activity for the tab (to be reused)
    intent = new Intent().setClass(this, Settings.class);
    // Initialize a TabSpec for each tab and add it to the TabHost
    spec = tabHost.newTabSpec("Settings").setIndicator("Settings",
            res.getDrawable(R.drawable.settings)).setContent(intent);
    tabHost.addTab(spec);

    ServiceDB db = new ServiceDB(this.getApplicationContext());
    db.addService(PubCrawl.class.getSimpleName(),
            ServiceDB.STATUSOPT.STARTED.name());

    Log.v(TAG, "Tab Initialization Finished!");
  }

  @Override
  protected void onDestroy() {
    ServiceDB db = new ServiceDB(this);
    db.addService(PubCrawl.class.getSimpleName(),
            ServiceDB.STATUSOPT.STOPPED.name());
    stopService(new Intent(this, GpsService.class));
    stopService(new Intent(this, CellService.class));
    super.onDestroy();
  }
}
