package com.dhaval.main;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PGraphics;

/** Implements a visual marker for ocean earthquakes on an earthquake map
 *
 * @author Dhaval Doshi
 *
 */
public class OceanQuakeMarker extends EarthquakeMarker {
	
	public OceanQuakeMarker(PointFeature quake) {
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = false;
	}
	

	/** Draw the earthquake as a square */
	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {

		UnfoldingMap map = EarthquakeCityMap.getMap();
		double km = this.threatCircle();
		if(this.getClicked()){
			for(Marker marker : EarthquakeCityMap.getCityMarkers()){
				if(this.getDistanceTo(marker.getLocation()) <= km*10 ){
					marker.setHidden(false);
					Location loc = marker.getLocation();
					ScreenPosition position =  map.getScreenPosition(loc);
					pg.line(x, y, position.x-200, position.y-50);
					//pg.rect(position.x-radius-200, position.y-radius-50, 2*radius, 2*radius);
				}
			}
		}
		if(getMagnitude() > 3)
		pg.rect(x-radius, y-radius, 2*radius, 2*radius);
	}


}
