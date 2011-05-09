package android.pubcrawl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class SearchZipCode extends Activity {

  private static final String TAG = SearchZipCode.class.getSimpleName();
  protected static final int ZIP_REQUEST = 10;
  protected static final String SEARCH_ZIP = "Search.ZipCode";

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    Log.v(TAG, "Super onCreate Finished!");
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
            WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    Log.v(TAG, "Blurred Parent Finished!");
    setContentView(R.layout.enterzipcode);
    Log.v(TAG, "setContentView Finished!");

    Button button = (Button) findViewById(R.id.zipcodeGo);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View v) {
        Log.v(TAG, "onClick Occurred!");
        Intent returnData = new Intent();
        final EditText zip = (EditText) findViewById(R.id.searchzipcode);
        Log.v(TAG, "New Intent Object & Assigned EditText ZipCode!");
        returnData.putExtra(SEARCH_ZIP, zip.getText().toString());
        Log.v(TAG, "Add zip to Intent Occurred!");
        if(zip.getText().toString().length() == 5)
          setResult(RESULT_OK, returnData);
        Log.v(TAG, "setResult RESULT_OK and returnData!");
        finish();
      }
    });
  }
}
