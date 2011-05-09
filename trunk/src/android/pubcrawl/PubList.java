package android.pubcrawl;

import android.app.AlertDialog;
import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.pubcrawl.database.CrawlDB;
import android.pubcrawl.database.CrawlPubElement;
import android.pubcrawl.database.LocalPubDB;
import android.pubcrawl.database.PubElement;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class PubList extends ListActivity implements OnItemLongClickListener, OnItemClickListener {

  private static final String TAG = PubList.class.getSimpleName();
  public final static String PUBLIST = "publist";
  private ArrayAdapter<String> pubListArr;
  private ArrayList<String> pubList = new ArrayList<String>();
  private ArrayList<CrawlPubElement> pubListData =
          new ArrayList<CrawlPubElement>();
  private ListView listView;
  private PubList parent;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    pubListArr = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, pubList);

    loadPubList();

    setListAdapter(pubListArr);

    listView = getListView();

    listView.setTextFilterEnabled(true);
    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    listView.setOnItemClickListener(this);
    listView.setOnItemLongClickListener(this);
    parent = this;
  }

  @Override
  protected void onResume() {
    loadPubList();
    super.onResume();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.v(TAG, "onCreateOptionsMenu Started!");
    MenuInflater inflater = getMenuInflater();
    Log.v(TAG, "getMenuInflater() Finished!");
    inflater.inflate(R.layout.crawlmenu, menu);
    Log.v(TAG, "Inflate Finished!");
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent i;
    CrawlDB crawlDB = new CrawlDB(this.getApplicationContext());
    switch (item.getItemId()) {
      case R.id.crawlSave:
        Log.v(TAG, "Save Crawl Choosen!");
        i = new Intent(this, SavePubCrawl.class);
        Log.v(TAG, "Intent Created!");
        i.putExtra("requestCode", SavePubCrawl.SAVE_REQUEST);
        startActivityForResult(i, SavePubCrawl.SAVE_REQUEST);
        Log.v(TAG, "startActivityForResult Finished!");
        return true;
      case R.id.crawlLoad:
        final CharSequence[] files = fileList();
        final ArrayList<CharSequence> goodFiles = new ArrayList<CharSequence>();
        for (int j = 0; j < files.length; j++) {
          if (files[j].toString().contains(".pbcrwl")) {
            goodFiles.add(files[j].toString().replace(".pbcrwl", ""));
          }
        }
        final CharSequence[] goodSelect = new CharSequence[goodFiles.size()];
        goodFiles.toArray(goodSelect);

        if (goodFiles.isEmpty()) {
          Toast.makeText(getApplicationContext(), "No saved Pub Crawls!",
                  Toast.LENGTH_SHORT).show();
          break;
        }

        AlertDialog.Builder loadBuilder = new AlertDialog.Builder(this);
        loadBuilder.setTitle("Select a Pub Crawl to Load");
        loadBuilder.setItems(goodSelect, new DialogInterface.OnClickListener() {

          public void onClick(DialogInterface dialog, int item) {
            final int itemSelect = item;
            AlertDialog.Builder confirmLoadBuilder =
                    new AlertDialog.Builder(parent);
            confirmLoadBuilder.setMessage("Are you sure you want to load "
                    + goodSelect[item] + "?").setCancelable(false).setPositiveButton(
                    "Yes", new DialogInterface.OnClickListener() {

              public void onClick(DialogInterface dialog, int id) {
                try {
                  StringBuilder db = new StringBuilder();
                  FileInputStream fis = openFileInput(
                          goodFiles.get(itemSelect).toString() + ".pbcrwl");
                  BufferedReader in = new BufferedReader(
                          new InputStreamReader(fis));
                  String inputLine;
                  while ((inputLine = in.readLine()) != null) {
                    db.append(inputLine);
                    db.append("\n");
                  }
                  in.close();
                  fis.close();
                  CrawlDB crawlDB = new CrawlDB(parent.getApplicationContext());
                  crawlDB.loadDB(db.toString());
                  loadPubList();
                } catch (Exception e) {
                  Log.v(TAG, "DumpDB Failed" + e.getMessage());
                }
              }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {

              public void onClick(DialogInterface dialog, int id) {
                //No
                dialog.cancel();
              }
            });
            AlertDialog confirmAlert = confirmLoadBuilder.create();
            confirmAlert.show();
          }
        });
        AlertDialog loadAlert = loadBuilder.create();
        loadAlert.show();
        break;
      case R.id.crawlClear:
        if (crawlDB.isTableEmpty()) {
          final CharSequence[] delfiles = fileList();
          final ArrayList<CharSequence> delFiles =
                  new ArrayList<CharSequence>();
          for (int j = 0; j < delfiles.length; j++) {
            if (delfiles[j].toString().contains(".pbcrwl")) {
              delFiles.add(delfiles[j].toString());
            }
          }
          AlertDialog.Builder delBuilder = new AlertDialog.Builder(parent);
          delBuilder.setMessage("Do you want to delete all saved Pub Crawls?")
                  .setCancelable(false).setPositiveButton(
                  "Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
              //YES
              for (int i = 0; i < delFiles.size(); i++) {
                deleteFile(delFiles.get(i).toString());
              }
            }
          }).setNegativeButton("No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
              //No
              dialog.cancel();
            }
          });
          AlertDialog delAlert = delBuilder.create();
          delAlert.show();
        } else {
          crawlDB.clearCrawlDB();
        }
        break;
      default:
        return super.onOptionsItemSelected(item);
    }
    loadPubList();
    return true;
  }

  @Override
  public void onItemClick(AdapterView<?> arg0, View arg1, int position,
          long id) {
    Intent intent = new Intent().setClass(this, PubInformation.class);
    intent.putExtra(PubElement.PUBID, pubListData.get(position).getPubID());
    intent.putExtra(
            CrawlPubElement.PUBPOS, pubListData.get(position).getPosition());
    intent.putExtra(PubInformation.REQUESTOR, PUBLIST);
    startActivityForResult(intent, PubInformation.PUBREQUESTID);
  }

  private void loadPubList() {
    CrawlDB crawlPubDB = new CrawlDB(this.getApplicationContext());
    LocalPubDB localPubDB = new LocalPubDB(this.getApplicationContext());
    ArrayList<CrawlPubElement> tmpList = crawlPubDB.getCrawlPubs();

    pubList.clear();
    pubListData.clear();
    pubListArr.notifyDataSetChanged();

    for (int i = 0; i < tmpList.size(); i++) {
      pubList.add(localPubDB.getPubById(tmpList.get(i).getPubID()).getName());
      pubListData.add(tmpList.get(i));
    }

    pubListArr.notifyDataSetChanged();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data){
    Log.v(TAG, "requestCode=" + requestCode + "resultCode" + resultCode);
    String fileName = "";

    if (resultCode == RESULT_OK && requestCode == PubInformation.PUBREQUESTID) {
      loadPubList();
    }
    if (resultCode == RESULT_OK && requestCode == SavePubCrawl.SAVE_REQUEST) {
      fileName = (String) data.getExtras().get(SavePubCrawl.SAVE_FILENAME);
      Log.v(TAG, "Got " + fileName + "!");
      CrawlDB crawlDB = new CrawlDB(this.getApplicationContext());
      try {
        FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
        fos.write(crawlDB.dumpDB().getBytes());
        fos.close();
      } catch (Exception e) {
        Log.v(TAG, "DumpDB Failed" + e.getMessage());
      }
      return;
    }
  }

  public boolean onItemLongClick(AdapterView<?> av, View view, int pos,
          long id) {

    final String remove = "Remove";
    final String moveUp = "Move Up";
    final String moveDown = "Move Down";

    ArrayList<String> chPub = new ArrayList<String>();

    if (pubListData.size() > 1 &&
            pos != 0 &&
            pubListData.get(pos-1).getPubID() != 1) {
      chPub.add(moveUp);
    }
    if (pos != (pubListData.size()-1) &&
            pubListData.size() > 1 &&
            pubListData.get(pos).getPubID() != 1) {
      chPub.add(moveDown);
    }
    chPub.add(remove);

    final CharSequence[] choicePub = new CharSequence[chPub.size()];
    final int position = pos;
    chPub.toArray(choicePub);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Select an Action");
    builder.setItems(choicePub, new DialogInterface.OnClickListener() {

      public void onClick(DialogInterface dialog, int item) {
        CrawlDB crawlDB = new CrawlDB(parent.getApplicationContext());
        if (choicePub[item].toString().equalsIgnoreCase(remove)) {
          crawlDB.removePub(pubListData.get(position).getPubID(),
                  pubListData.get(position).getPosition());
        } else if (choicePub[item].toString().equalsIgnoreCase(moveUp)) {
          crawlDB.swapPosition(pubListData.get(position).getPosition(),
                  pubListData.get(position-1).getPosition());
        } else if (choicePub[item].toString().equalsIgnoreCase(moveDown)) {
          crawlDB.swapPosition(pubListData.get(position).getPosition(),
                  pubListData.get(position+1).getPosition());
        }
        loadPubList();
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
    return true;
  }
}
