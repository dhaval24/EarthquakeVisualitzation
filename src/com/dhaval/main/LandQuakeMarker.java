package com.dhaval.main;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/** Implements a visual marker for land earthquakes on an earthquake map
 * @author Dhaval Doshi
 *
 */
public class LandQuakeMarker extends EarthquakeMarker {
	
	
	public LandQuakeMarker(PointFeature quake) {
		
		// calling EarthquakeMarker constructor
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = true;
	}


	/** Draw the earthquake as an ellipse */
	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {

		if(getMagnitude() > 3){
			pg.stroke(186,39,39);
			pg.strokeWeight(3);
			pg.ellipse(x, y, radius, radius);
			pg.strokeWeight(1);
			pg.ellipse(x, y, 2*radius, 2*radius);
			pg.stroke(0, 0 ,0);
		}
		else{
			this.setHidden(true);
		}
		
	}

	// Get the country the earthquake is in
	public String getCountry() {
		return (String) getProperty("country");
	}

		
}