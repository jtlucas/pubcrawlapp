package android.pubcrawl.maptools;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import java.util.ArrayList;

// Code help from
// http://stackoverflow.com/questions/4408671/android-draw-route-on-a-mapview-with-twoo-poi-s
// http://mobile.synyx.de/2010/06/routing-driving-directions-on-android-%E2%80%93-part-2-draw-the-route/
public class RouteOverlay extends Overlay {

  private ArrayList<GeoPoint> gpList;
  private int mode = 0;
  private int defaultColor;

  public RouteOverlay(ArrayList<GeoPoint> gpList, int mode) {
    this.gpList = gpList;
    this.mode = mode;
    defaultColor = 999; // no defaultColor
  }

  public RouteOverlay(ArrayList<GeoPoint> gpList, int mode, int defaultColor) {
    this.gpList = gpList;
    this.mode = mode;
    this.defaultColor = defaultColor;
  }

  public int getMode() {
    return mode;
  }

  @Override
  public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
    GeoPoint gp1 = null, gp2 = null;
    Projection projection = mapView.getProjection();

    for (int i = 0; i < gpList.size(); i++) {
      if (i == 0) {
        gp1 = gpList.get(i);
        continue;
      }
      gp2 = gpList.get(i);
      if (shadow == false) {
        Paint paint = new Paint();
        Point point = new Point();
        projection.toPixels(gp1, point);
        if (mode == 2) {
          if (defaultColor == 999) {
            paint.setColor(Color.BLUE);
          } else {
            paint.setColor(defaultColor);
          }
          Point point2 = new Point();
          projection.toPixels(gp2, point2);
          paint.setStrokeWidth(5);
          paint.setAlpha(120);
          canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
        }
      }
      gp1 = gp2;
    }
    return super.draw(canvas, mapView, shadow, when);
  }
}
