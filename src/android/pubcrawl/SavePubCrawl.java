package android.pubcrawl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class SavePubCrawl extends Activity {

  private static final String TAG = SavePubCrawl.class.getSimpleName();
  protected static final int SAVE_REQUEST = 30;
  protected static final String SAVE_FILENAME = "save.filename";

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    Log.v(TAG, "Super onCreate Finished!");
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
            WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    Log.v(TAG, "Blurred Parent Finished!");
    setContentView(R.layout.savecrawl);
    Log.v(TAG, "setContentView Finished!");

    Button button = (Button) findViewById(R.id.saveCrawl);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View v) {
        Log.v(TAG, "onClick Occurred!");
        Intent returnData = new Intent();
        final EditText fileName = (EditText) findViewById(R.id.saveFileName);
        Log.v(TAG, "New Intent Object & Assigned EditText ZipCode!");
        returnData.putExtra(SAVE_FILENAME, fileName.getText().toString() + ".pbcrwl");
        Log.v(TAG, "Add zip to Intent Occurred!");
        setResult(RESULT_OK, returnData);
        Log.v(TAG, "setResult RESULT_OK and returnData!");
        finish();
      }
    });
  }
}
