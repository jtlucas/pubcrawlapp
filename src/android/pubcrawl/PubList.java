package android.pubcrawl;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PubList extends ListActivity implements OnItemLongClickListener, OnItemClickListener {

	private ArrayAdapter<String> pubListArr;
	private ArrayList<String> pubList = new ArrayList<String>();
	private ListView listView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		pubListArr = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pubList);
		pubList.add("asdf");
		pubList.add("asdf1");
		
		setListAdapter(pubListArr);
		
		listView = getListView();
		
		listView.setTextFilterEnabled(true);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		listView.setOnItemLongClickListener(this);
		listView.setOnItemClickListener(this);
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		pubList.add((String)listView.getItemAtPosition(position));
		pubListArr.notifyDataSetChanged();
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		pubList.add((String)listView.getItemAtPosition(position)+listView.getItemAtPosition(position));
		pubListArr.notifyDataSetChanged();
		return false;
	}
	
}
