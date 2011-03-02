package android.pubcrawl;

import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class Map extends MapActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapdebug);
		
		MapView mapView = (MapView) findViewById(R.id.mapdebug);
		mapView.setBuiltInZoomControls(true);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
