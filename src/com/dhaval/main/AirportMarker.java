package com.dhaval.main;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

import java.util.List;

/** 
 * A class to represent AirportMarkers on a world map.
 * @author
 * Dhaval Doshi
 */
public class AirportMarker extends CommonMarker {
	public static List<SimpleLinesMarker> routes;
	private PImage img;
	public AirportMarker(Feature city, PImage img) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		this.img = img;
	
	}

	@Override
	public void displayMenu(PGraphics pg, float x, float y) {

	}

	@Override
	public void displayResultOnButton(PGraphics pg, float x, float y) {

	}

	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.pushStyle();
		pg.imageMode(PConstants.CORNER);
		pg.image(img, x, y);
		
		
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		 // show rectangle with title
		
		// show routes
		
		
	}
	
}
