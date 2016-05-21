package com.dhaval.main;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/** Implements a visual marker for cities on an earthquake map
 * @author Dhaval Doshi
 *
 */
public class CityMarker extends CommonMarker {
	
	public static int TRI_SIZE = 5;  // The size of the triangle marker
	public Button b1;
	public Button b2;
	public PImage img;
	private int nearyByQuakes;

	public void setAverageMag(float averageMag) {
		this.averageMag = averageMag;
	}

	private float averageMag;

	public void setNearyByQuakes(int nearyByQuakes) {
		this.nearyByQuakes = nearyByQuakes;
	}

	public CityMarker(Location location) {
		super(location);
	}

	@Override
	public void displayMenu(PGraphics pg, float x, float y) {
		this.setMenuEnabled(true);
		this.setSelected(false);
		pg.fill(255, 255, 255);
		pg.textSize(12);
		pg.rectMode(PConstants.CORNER);
		b1 = new Button(x + 5 , y - 45 +10);
		pg.rect(x, y-45,  b1.BUTTON_WIDTH + 15, b1.BUTTON_HEIGHT * 2 + 20);

		pg.fill(200,200, 0);

		pg.rect(b1.getX1(), b1.getX2(), b1.BUTTON_WIDTH, b1.BUTTON_HEIGHT);
		pg.fill(0, 0, 0);
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.text("Quakes nearby", x+10, y-35);
		b2 = new Button(x+5, y -45 + b1.BUTTON_HEIGHT + 15);
		pg.fill(200, 200, 0);
		pg.rect(b2.getX1(), b2.getX2(), b2.BUTTON_WIDTH, b2.BUTTON_HEIGHT);
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0,0,0);
		pg.text("Average Mag", x+10, y - 35 + b1.BUTTON_HEIGHT+5);

	}

	@Override
	public void displayResultOnButton(PGraphics pg, float x, float y) {

			if(this.button1Enabled){
				pg.rectMode(PConstants.CORNER);
				pg.fill(242,238,225);
				pg.rect(x+b1.BUTTON_WIDTH+15, y-45, 100, 20);
				pg.fill(0, 0, 0);
				pg.textAlign(PConstants.LEFT, PConstants.TOP);
				pg.text("Total quakes: "+nearyByQuakes, x+b1.BUTTON_WIDTH+15, y-45);
			}

			else{
				if(this.button2Enabled){
					pg.rectMode(PConstants.CORNER);
					pg.fill(242,238,225);
					pg.rect(x+b1.BUTTON_WIDTH+15, y-45, 100, 20);
					pg.fill(0, 0, 0);
					pg.textAlign(PConstants.LEFT, PConstants.TOP);
					pg.text("Average Mag: "+averageMag, x+b1.BUTTON_WIDTH+15, y-45);
				}
			}


	}

//	public void enableB1(){
//		b1.setEnabled(true);
//		System.out.println(b1.isEnabled());
//	}

	public CityMarker(Feature city, PImage img) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		this.img = img;
		// Cities have properties: "name" (city name), "country" (country name)
		// and "population" (population, in millions)
	}

	
	/**
	 * Implementation of method to draw marker on the map.
	 */

	@Override
	public void drawMarker(PGraphics pg, float x, float y) {

		pg.pushStyle();

		// IMPLEMENT: drawing triangle for each city
//		pg.fill(150, 30, 30);
//		pg.triangle(x, y-TRI_SIZE, x-TRI_SIZE, y+TRI_SIZE, x+TRI_SIZE, y+TRI_SIZE);
		pg.imageMode(PConstants.CENTER);
		pg.image(img, x, y);



		// Restore previous drawing style
		pg.popStyle();
	}

	/** Show the title of the city if this marker is selected */
	public void showTitle(PGraphics pg, float x, float y)
	{
//		String city = this.getCity();
//		String country = this.getCountry();
//		float population = this.getPopulation();
//		String text = city + " " + country + " " + population;
//		pg.fill(255, 255, 255);
//		pg.rect(x+10, y-10, pg.textWidth(text), 12);
//		pg.fill(10, 10, 10);
//		pg.text(text, x+10, y);
		String name = getCity() + " " + getCountry() + " ";
		String pop = "Pop: " + getPopulation() + " Million";

		EarthquakeCityMap.buffer.beginDraw();
		EarthquakeCityMap.buffer.pushStyle();

		EarthquakeCityMap.buffer.fill(255, 255, 255);
		EarthquakeCityMap.buffer.textSize(12);
		EarthquakeCityMap.buffer.rectMode(PConstants.CORNER);
		EarthquakeCityMap.buffer.rect(x, y-TRI_SIZE-39, Math.max(pg.textWidth(name), pg.textWidth(pop)) + 6, 39);
		EarthquakeCityMap.buffer.fill(0, 0, 0);
		EarthquakeCityMap.buffer.textAlign(PConstants.LEFT, PConstants.TOP);
		EarthquakeCityMap.buffer.text(name, x+3, y-TRI_SIZE-33);
		EarthquakeCityMap.buffer.text(pop, x+3, y - TRI_SIZE -18);

		EarthquakeCityMap.buffer.popStyle();
		EarthquakeCityMap.buffer.endDraw();

	}

	/* Local getters for some city properties.  
	 */
	public String getCity()
	{
		return getStringProperty("name");
	}
	
	public String getCountry()
	{
		return getStringProperty("country");
	}
	
	public float getPopulation()
	{
		return Float.parseFloat(getStringProperty("population"));
	}
}
