package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.List;

import android.app.Instrumentation;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * @author Nicholas Albion
 */
public class MapViewUtils {
	private final Instrumentation inst;
	private final ViewFetcher viewFetcher;
	private final Sleeper sleeper;
	
	/**
	 * Constructs this object.
	 *
	 * @param inst the {@code Instrumentation} instance.
	 * @param viewFetcher the {@code ViewFetcher} instance.
	 * @param sleeper the {@code Sleeper} instance
	 */
	public MapViewUtils( Instrumentation inst, ViewFetcher viewFetcher, Sleeper sleeper ) {
		this.inst = inst;
		this.viewFetcher = viewFetcher;
		this.sleeper = sleeper;
	}
	
	private MapView getMapView() {
		final ArrayList<View> viewList = RobotiumUtils.removeInvisibleViews(viewFetcher.getAllViews(true));
		return RobotiumUtils.filterViews( MapView.class, viewList ).get(0);
	}
	
	public void setCenter( double lat, double lon ) {
		MapView mapView = getMapView();
		mapView.getController().setCenter( new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6)) );
	}
	
	public int setZoom( int zoomLevel ) {
		MapView mapView = getMapView();
		return mapView.getController().setZoom( zoomLevel );
	}
		
	public boolean zoomIn() {
		MapView mapView = getMapView();
		return mapView.getController().zoomIn();
	}
	
	public boolean zoomOut() {
		MapView mapView = getMapView();
		return mapView.getController().zoomOut();
	}
	
	/**
	 * @return A list of JSON strings representing the markers. eg: {"latitude":-33.89483, "longitude":151.19524, "title":"2000133"}
	 */
	public List<String> getMarkerItems() {
		ArrayList<String> markers = new ArrayList<String>();
		
		MapView mapView = getMapView();
		for( Overlay overlay : mapView.getOverlays() ) {
			if( overlay instanceof ItemizedOverlay ) {
				@SuppressWarnings("rawtypes")
				ItemizedOverlay markerOverlay = ((ItemizedOverlay)overlay);
				int noOfMarkers = markerOverlay.size();
				markers.ensureCapacity( markers.size() + noOfMarkers );
				for( int i = 0; i < noOfMarkers; i++ ) {
					OverlayItem item = markerOverlay.getItem(i);
					GeoPoint point = item.getPoint();
					StringBuilder str = new StringBuilder("{\"latitude\":\"").append(Double.toString( point.getLatitudeE6() / 1E6 ))
										.append("\", \"longitude\":\"").append( Double.toString( point.getLongitudeE6() / 1E6 ))
										.append("\", \"title\":\"").append( item.getTitle().replaceAll("\"", "\\\"") );
					String snippet = item.getSnippet();
					if( snippet != null && !snippet.isEmpty() ) { 
						str.append("\", \"subtitle\":\"").append( snippet.replaceAll("\"", "\\\"") );
					}
					str.append("\"}");
					markers.add( str.toString() );
				}
			}
		}
		
		return markers;
	}
}
