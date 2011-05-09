package android.pubcrawl.maptools;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.pubcrawl.Map;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import java.util.ArrayList;

public class PubOverlay extends ItemizedOverlay {

  private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
  private Map parent;

  public PubOverlay(Map parent, Drawable defaultMarker) {
    super(boundCenterBottom(defaultMarker));
    this.parent = parent;
  }

  public void addOverlay(OverlayItem overlay) {
    mOverlays.add(overlay);
  }

  public void populateAll() {
    populate();
  }

  @Override
  protected OverlayItem createItem(int i) {
    return mOverlays.get(i);
  }

  @Override
  public int size() {
    return mOverlays.size();
  }

  @Override
  protected boolean onTap(int index) {
    OverlayItem item = mOverlays.get(index);
    AlertDialog.Builder dialog = new AlertDialog.Builder(parent);
    dialog.setTitle(item.getTitle());
    dialog.show();
    return true;
  }
}
