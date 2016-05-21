package com.dhaval.main;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;
import processing.core.PImage;

/** Implements a common marker for cities and earthquakes on an earthquake map
 * @author Dhaval Doshi
 */
public abstract class CommonMarker extends SimplePointMarker {

	// Records whether this marker has been clicked (most recently)
	protected boolean clicked = false;
	public boolean menuEnabled = false;
	public boolean button1Enabled = false;
	public boolean button2Enabled = false;

	public boolean isButton2Enabled() {
		return button2Enabled;
	}

	public void setButton2Enabled(boolean button2Enabled) {
		this.button2Enabled = button2Enabled;
	}

	public boolean isButton1Enabled() {
		return button1Enabled;
	}

	public void setButton1Enabled(boolean button1Enabled) {
		this.button1Enabled = button1Enabled;
	}

	public boolean isMenuEnabled() {
		return menuEnabled;
	}

	public void setMenuEnabled(boolean menuEnabled) {
		this.menuEnabled = menuEnabled;
	}

	public CommonMarker(Location location) {
		super(location);
	}
	public CommonMarker(Location location, PImage img){
		super(location);
	}
	
	public CommonMarker(Location location, java.util.HashMap<java.lang.String,java.lang.Object> properties) {
		super(location, properties);
	}
	
	// Getter method for clicked field
	public boolean getClicked() {
		return clicked;
	}
	
	// Setter method for clicked field
	public void setClicked(boolean state) {
		clicked = state;
	}
	
	// Common piece of drawing method for markers; 
	// Note that you should implement this by making calls 
	// drawMarker and showTitle, which are abstract methods 
	// implemented in subclasses
	public void draw(PGraphics pg, float x, float y) {
		// For starter code just drawMaker(...)
		if (!hidden) {
			drawMarker(pg, x, y);
			if (clicked){
				displayMenu(pg, x, y);
			}
			if(button1Enabled || button2Enabled){
				displayResultOnButton(pg, x, y);
			}
			if (selected) {
				showTitle(pg, x, y);  // You will implement this in the subclasses
			}

		}
	}
	public abstract void displayMenu(PGraphics pg, float x, float y);
	public abstract void displayResultOnButton(PGraphics pg, float x, float y);
	public abstract void drawMarker(PGraphics pg, float x, float y);
	public abstract void showTitle(PGraphics pg, float x, float y);
}