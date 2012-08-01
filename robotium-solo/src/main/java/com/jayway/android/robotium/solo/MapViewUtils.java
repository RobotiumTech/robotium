package com.jayway.android.robotium.solo;

import java.util.ArrayList;
import java.util.List;

import android.app.Instrumentation;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

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
	
	/**
	 * @param lat
	 * @param lon
	 */
	public void setCenter( double lat, double lon ) {
		MapView mapView = getMapView();
		mapView.getController().setCenter( new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6)) );
	}
	
	public double[] getMapCenter() {
		MapView mapView = getMapView();
		GeoPoint center = mapView.getMapCenter();
		return new double[] { center.getLatitudeE6() / 1E6, center.getLongitudeE6() / 1E6 };
	}
	
	/**
	 * @param lat
	 * @param lon
	 */
	public void panTo( double lat, double lon ) {
		MapView mapView = getMapView();
		mapView.getController().animateTo( new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6)) );
	}
	
	public int getZoom() {
		MapView mapView = getMapView();
		return mapView.getZoomLevel();
	}
	
	public int setZoom( int zoomLevel ) {
		MapView mapView = getMapView();
		mapView.getController().stopAnimation(true);
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
	 * @return [top, right, bottom, left] in decimal degrees
	 */
	public List<String> getBounds() {
		MapView mapView = getMapView();
		GeoPoint center = mapView.getMapCenter();
		int latCtr = center.getLatitudeE6();
		int lonCtr = center.getLongitudeE6();
		int latSpan = mapView.getLatitudeSpan() >> 1;
		int lonSpan = mapView.getLongitudeSpan() >> 1;
		
		Log.i("MapView", "latSpan: " + latSpan);
		
		ArrayList<String> bounds = new ArrayList<String>(4);
		bounds.add( Double.toString( (latCtr + latSpan) / 1E6 ) );
		bounds.add( Double.toString( (lonCtr + lonSpan) / 1E6 ) );
		bounds.add( Double.toString( (latCtr - latSpan) / 1E6 ) );
		bounds.add( Double.toString( (lonCtr - lonSpan) / 1E6 ) );
		return bounds;
	}
	
	/**
	 * @return A list of JSON strings representing the markers.
	 * @see #getMarkerItem(String)
	 * @see MapViewUtils#toString(OverlayItem, boolean)
	 */
	public List<String> getMarkerItems() {
		ArrayList<String> markers = new ArrayList<String>();
		
		MapView mapView = getMapView();
		for( Overlay overlay : mapView.getOverlays() ) {
			if( overlay instanceof ItemizedOverlay ) {
				@SuppressWarnings("rawtypes")
				ItemizedOverlay markerOverlay = ((ItemizedOverlay)overlay);
				int noOfMarkers = markerOverlay.size();
				int lastFocused = markerOverlay.getLastFocusedIndex();
				
				Log.i("MapView", "Overlay " + markerOverlay + " has " + noOfMarkers + " markers");
				markers.ensureCapacity( markers.size() + noOfMarkers );
				for( int i = 0; i < noOfMarkers; i++ ) {
					OverlayItem item = markerOverlay.getItem(i);
					String str = toString( item, lastFocused == i );
					markers.add( str );
				}
			}
		}
		
		Log.i("MapView", "Sending response with " + markers.size() + " markers");
		return markers;
	}
	
	/**
	 * @param title
	 * @return null or (for example) {"latitude":-33.123456, "longitude":151.123456, "title":"My Marker", "snippet":"More Info about my marker"}
	 */
	public String getMarkerItem( String title ) {
		ArrayList<String> markers = new ArrayList<String>();
		
		MapView mapView = getMapView();
		for( Overlay overlay : mapView.getOverlays() ) {
			if( overlay instanceof ItemizedOverlay ) {
				@SuppressWarnings("rawtypes")
				ItemizedOverlay markerOverlay = ((ItemizedOverlay)overlay);
				int noOfMarkers = markerOverlay.size();
				int lastFocused = markerOverlay.getLastFocusedIndex();
				
				Log.i("MapView", "Overlay " + markerOverlay + " has " + noOfMarkers + " markers");
				markers.ensureCapacity( markers.size() + noOfMarkers );
				for( int i = 0; i < noOfMarkers; i++ ) {
					OverlayItem item = markerOverlay.getItem(i);
					
					if( title.equals(item.getTitle()) ) {
						return toString( item, lastFocused == i );
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * @param title
	 * @param timeout in ms
	 * @return true if the marker was found and tapped upon
	 */
	public boolean tapMarkerItem( String title, long timeout ) {		
		final long endTime = SystemClock.uptimeMillis() + timeout;
		Log.i("TapMarker", "Looking for marker '" + title + "'");
		
		while( SystemClock.uptimeMillis() < endTime ) {
			MapView mapView = getMapView();
			for( Overlay overlay : mapView.getOverlays() ) {
				if( overlay instanceof ItemizedOverlay ) {
					@SuppressWarnings("rawtypes")
					ItemizedOverlay markerOverlay = ((ItemizedOverlay)overlay);
					int noOfMarkers = markerOverlay.size();
					for( int i = 0; i < noOfMarkers; i++ ) {
						OverlayItem item = markerOverlay.getItem(i);
						String itemTitle = item.getTitle();
//						Log.i("TapMarker", "  item title: " + itemTitle);
						if( title.equals( itemTitle ) ) {
							markerOverlay.onTap( item.getPoint(), mapView );
							return true;
						}
					}
				}
			}
			
			Log.i("TapMarker", "Could not find marker '" + title + "', try again in a moment");
			sleeper.sleep();
		}
		
		Log.i("TapMarker", "Nope, could not find marker '" + title + "'");
		return false;
	}
	
	/**
	 * @param step - number of pixels to step when searching for an empty piece of screen
	 * @return true if it was possible to tap on the screen without tapping on a marker
	 */
	 @SuppressWarnings("rawtypes")
	public boolean tapAwayFromMarkerItems( int step ) {		
		MapView mapView = getMapView();
		Projection proj = mapView.getProjection();
		ItemizedOverlay overlayToTap = null;
		Point markerPoint = new Point();
		int w = mapView.getWidth();
		int h = mapView.getHeight();
		
		for( int x = 0; x < w; x += step ) {
NEXT_Y:			
			for( int y = 0; y < h; y += step ) {
				boolean tappedMarkerAtPoint = false;
				for( Overlay overlay : mapView.getOverlays() ) {
					if( overlay instanceof ItemizedOverlay ) {						
						ItemizedOverlay markerOverlay = ((ItemizedOverlay)overlay);		
//						if( markerOverlay.onTap( geoPoint, mapView ) ) {
//							tappedMarkerAtPoint = true;
//							break;
//						}
						int noOfMarkers = markerOverlay.size();
						for( int i = 0; i < noOfMarkers; i++ ) {
							OverlayItem item = markerOverlay.getItem(i);
							proj.toPixels( item.getPoint(), markerPoint );
							Rect markerBounds = item.getMarker(0).getBounds();
							markerBounds.offset( markerPoint.x, markerPoint.y );
							
							Log.d("TapAwayFromMarkers", "markerBounds: " + markerBounds);
							if( markerBounds.contains(x, y) ) {
								Log.d("TapAwayFromMarkers", "Tapping at " + x + ", " + y + " would tap on " + item.getTitle());
								continue NEXT_Y;
							}
						}
						overlayToTap = markerOverlay;
					}
				}
				
				if( tappedMarkerAtPoint == false && overlayToTap != null ) {
					Log.i("TapAwayFromMarkers", "Tapping away from markers at " + x + ", " + y);
					GeoPoint geoPoint = proj.fromPixels(x, y);
					overlayToTap.onTap(geoPoint, mapView);
					return true;
				}
			}
		}
		
		Log.i("TapMarker", "Nope, could not avoid tapping on any markers");
		return false;
	}
	 
	/**
	 * @param item
	 * @param isLastFocused
	 * @return eg: {"latitude":-33.123456, "longitude":151.123456, "title":"My Marker", "snippet":"More Info about my marker"}
	 */
	private String toString( OverlayItem item, boolean isLastFocused ) {
		GeoPoint point = item.getPoint();
		StringBuilder str = new StringBuilder("{\"latitude\":").append(Double.toString( point.getLatitudeE6() / 1E6 ))
				 						.append(", \"longitude\":").append( Double.toString( point.getLongitudeE6() / 1E6 ))
				 						.append(", \"title\":\"").append( item.getTitle().replaceAll("\"", "\\\"") );
		String snippet = item.getSnippet();
		if( snippet != null && !snippet.isEmpty() ) { 
			str.append("\", \"snippet\":\"").append( snippet.replaceAll("\"", "\\\"") );
		}
		if( isLastFocused ) {
			str.append("\", \"focused\":true}");
		} else {
			str.append("\"}");
		}
		return str.toString();
	}
}
