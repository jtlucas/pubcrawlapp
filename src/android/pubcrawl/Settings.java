package android.pubcrawl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.pubcrawl.database.ServiceDB;
import android.pubcrawl.database.ServiceElement;
import android.pubcrawl.services.CellService;
import android.pubcrawl.services.GpsService;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Settings extends Activity implements OnClickListener {

  private static final String TAG = Settings.class.getSimpleName();
  private Button actionGPS;
  private Button actionCELL;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setting);

    actionGPS = (Button)  findViewById(R.id.actionGPS);
    actionCELL = (Button) findViewById(R.id.actionCELL);

    reDrawButtons();

    Log.v(TAG, "onCreate Finished!");
  }

  @Override
  protected void onResume() {
    super.onResume();
    reDrawButtons();
  }

  public void reDrawButtons() {
    ServiceDB db = new ServiceDB(this.getApplicationContext());
    ServiceElement service =
            db.getLatestServiceStatus(GpsService.class.getSimpleName());
    String stat = service.getStatus();
    if (stat == null
            || stat.equalsIgnoreCase(ServiceDB.STATUSOPT.STOPPED.toString())) {
      actionGPS.setText("Start GPS Service");
    } else if (stat.equalsIgnoreCase(ServiceDB.STATUSOPT.STARTED.toString())) {
      actionGPS.setText("Stop GPS Service");
    }
    service = db.getLatestServiceStatus(CellService.class.getSimpleName());
    stat = service.getStatus();
    if (stat == null
            || stat.equalsIgnoreCase(ServiceDB.STATUSOPT.STOPPED.toString())) {
      actionCELL.setText("Start Cell Service");
    } else if (stat.equalsIgnoreCase(ServiceDB.STATUSOPT.STARTED.toString())) {
      actionCELL.setText("Stop Cell Service");
    }

    actionGPS.setOnClickListener(this);
    actionCELL.setOnClickListener(this);
  }

  public void onClick(View view) {
    Log.v(TAG, "onClick Started!");
    ServiceDB db = new ServiceDB(this.getApplicationContext());
    String stat = "";
    ServiceElement service = null;
    switch (view.getId()) {
      case R.id.actionGPS:
        service = db.getLatestServiceStatus(GpsService.class.getSimpleName());
        stat = service.getStatus();
        if (stat == null
                || stat.equalsIgnoreCase(ServiceDB.STATUSOPT.STOPPED.toString())) {
          startService(new Intent(this, GpsService.class));
          actionGPS.setText("Stop GPS Service");
        } else if (stat.equalsIgnoreCase(ServiceDB.STATUSOPT.STARTED.toString())) {
          stopService(new Intent(this, GpsService.class));
          actionGPS.setText("Start GPS Service");
        }
        break;
      case R.id.actionCELL:
        service = db.getLatestServiceStatus(CellService.class.getSimpleName());
        stat = service.getStatus();
        if (stat == null
                || stat.equalsIgnoreCase(ServiceDB.STATUSOPT.STOPPED.toString())) {
          startService(new Intent(this, CellService.class));
          actionCELL.setText("Stop Cell Service");
        } else if (stat.equalsIgnoreCase(ServiceDB.STATUSOPT.STARTED.toString())) {
          stopService(new Intent(this, CellService.class));
          actionCELL.setText("Start Cell Service");
        }
        break;
    }
  }
}
