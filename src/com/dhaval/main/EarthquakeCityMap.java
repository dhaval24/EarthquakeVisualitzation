package com.dhaval.main;

import java.util.*;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * @author Dhaval Doshi
 */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setup and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;
	private boolean isGDPActive = false;


	public PImage getImg() {
		return img;
	}

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private static UnfoldingMap map;

	public static UnfoldingMap getMap() {
		return map;
	}
	// Markers for each city

	public static List<Marker> getCityMarkers() {
		return cityMarkers;
	}

	private static List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;

	private List<Marker> airportList;

	List<Marker> routeList;

	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	public  static PGraphics buffer;
	Map<String, Float> gdpPerCountry;
	PImage img;
	PImage imgAirport;
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(1000, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 750, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 850, 600, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		gdpPerCountry = ParseFeed.loadGDPFromCSV(this, "D:\\Object Oriented Programming UCSD\\_d80f47178937b2affba26343e747a7a0_OOPStarterCode(1)\\UCSDUnfoldingMaps\\data\\GDP.csv");

		imgAirport = loadImage("D:\\Object Oriented Programming UCSD\\_d80f47178937b2affba26343e747a7a0_OOPStarterCode(1)\\UCSDUnfoldingMaps\\Image\\airplane.png");
		imgAirport.resize(15,15);
		img = loadImage("D:\\Object Oriented Programming UCSD\\_d80f47178937b2affba26343e747a7a0_OOPStarterCode(1)\\UCSDUnfoldingMaps\\Image\\icon.png");
		img.resize(20,20);
		//earthquakesURL = "quiz2.atom";
		buffer = createGraphics(850,600);
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		for(Marker marker : countryMarkers){
			marker.setHidden(true);
		}
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city, img));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

		List<PointFeature> airportFeatures = ParseFeed.parseAirports(this, "airports.dat");
		airportList = new ArrayList<>();
		HashMap<Integer, Location> airports = new HashMap<>();
		for(PointFeature feature : airportFeatures) {
			AirportMarker m = new AirportMarker(feature,imgAirport);
			m.setHidden(true);
			m.setRadius(5);
			airportList.add(m);
			// put airport in hashmap with OpenFlights unique id for key

			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());

		}

		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {

			// get source and destination airportIds
			int source = Integer.parseInt((String) route.getProperty("source"));
			int dest = Integer.parseInt((String) route.getProperty("destination"));

			// get locations for airports on route
			if (airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}

			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
			sl.setHidden(true);
			routeList.add(sl);
		}
	    // could be used for debugging
	    printQuakes();
		shadeCountries();
		sortAndPrint(20);
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
		map.addMarkers(airportList);
		map.addMarkers(routeList);
		map.addMarkers(countryMarkers);
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		image(buffer, 200, 50);
		addKey();
		
	}
	private void shadeCountries() {

		for (Marker marker : countryMarkers) {
			String countryId = marker.getId();
			//System.out.println(countryId);
			if (gdpPerCountry.containsKey(countryId)) {
				System.out.println(countryId);
				float gdp = gdpPerCountry.get(countryId)/100;
				System.out.println(gdp);
				marker.setColor(color(255 - gdp, 100+gdp, gdp));
			} else {
				marker.setColor(color(150, 150, 150));
			}
		}
	}
	private void sortAndPrint(int numToPrint){

		Object[] earthquakeMarkers = quakeMarkers.toArray();
		Arrays.sort(earthquakeMarkers);
		if(numToPrint > earthquakeMarkers.length){
			numToPrint = earthquakeMarkers.length;
		}
		for(int i = 0; i < numToPrint; i++){
			String title = ((EarthquakeMarker)earthquakeMarkers[i]).getTitle();
			System.out.println(title);
		}
	}

	/**
	 * Event handler that handles the key event UP key is presses
	 */

	@Override
	public void keyReleased(){

		if(keyCode == UP){
			displayTop10EarthQuakes(quakeMarkers);
		}

		if(keyCode == DOWN){
			unhideMarkers();
		}
	}
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			buffer = createGraphics(850, 600);
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
	}
	
	// If there is a marker under the cursor, and lastSelected is null 
	// set the lastSelected to be the first marker found under the cursor
	// Make sure you do not select two markers.
	// 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		int x = mouseX;
		int y = mouseY;
		for(Marker marker : markers){
			if(marker.isInside(map, x, y)){
				if(lastSelected == null){
					lastSelected = (CommonMarker) marker;
					lastSelected.setSelected(true);
					break;
				}

			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		if(mouseX >= 25 && mouseY >= 350 && mouseX < 200 && mouseY < 400 ){
			activateCountryMarkers();
		}

		if(lastClicked != null){
			if(lastClicked.isMenuEnabled()){
				if(lastClicked instanceof CityMarker){
					System.out.println(((CityMarker)lastClicked).b1.getX1());
					System.out.println(mouseX);
					if(mouseX-200 < ((CityMarker)lastClicked).b1.getX1() + ((CityMarker)lastClicked).b1.BUTTON_WIDTH
							&& mouseY - 50 < ((CityMarker)lastClicked).b1.getX2() + ((CityMarker)lastClicked).b1.BUTTON_HEIGHT
							&& mouseX-200 >= ((CityMarker)lastClicked).b1.getX1()
							&& mouseY - 50 >= ((CityMarker)lastClicked).b1.getX2()){
						enable1Button((CityMarker)lastClicked, mouseX, mouseY);
						lastClicked.button2Enabled=false;

						return;
					}
					else if(mouseX-200 < ((CityMarker)lastClicked).b2.getX1() + ((CityMarker)lastClicked).b2.BUTTON_WIDTH
							&& mouseY - 50 < ((CityMarker)lastClicked).b2.getX2() + ((CityMarker)lastClicked).b2.BUTTON_HEIGHT
							&& mouseX-200 >= ((CityMarker)lastClicked).b2.getX1()
							&& mouseY - 50 >= ((CityMarker)lastClicked).b2.getX2()){
						enable2Button((CityMarker)lastClicked);
						lastClicked.button1Enabled = false;
						return;
					}
				}
			}
			if(lastClicked instanceof EarthquakeMarker){
				((EarthquakeMarker) lastClicked).isActive = false;
			}
			lastClicked.setSelected(false);
			lastClicked.setClicked(false);
			lastClicked.button2Enabled=false;
			lastClicked.button1Enabled = false;
			lastClicked.setButton1Enabled(false);
			lastClicked = null;
			unhideMarkers();
			hideAirportMarkers();
		}
		else{
			Marker marker = selectMarkerIfClicked(quakeMarkers);
			if(marker != null){
				if(marker instanceof EarthquakeMarker){
					((EarthquakeMarker) marker).isActive = true;
				}
				lastClicked = (CommonMarker)marker;
				lastClicked.setClicked(true);
				displayCityInThreat((EarthquakeMarker)marker);
				displayNearByAirports(marker);
			}
			else{
				marker = selectMarkerIfClicked(cityMarkers);
				if(marker != null){
					lastClicked = (CommonMarker)marker;
					lastClicked.setClicked(true);
					displayThreatningEarthQuake((CityMarker) marker);
				}

			}
		}

	}

	private void activateCountryMarkers(){
		for(Marker marker : countryMarkers){
			marker.setHidden(false);
		}
	}

	private void displayNearByAirports(Marker earthquake){
		double threatCircle = ((EarthquakeMarker)earthquake).threatCircle();
		for(Marker marker: airportList){
			if(marker.getDistanceTo(earthquake.getLocation()) < threatCircle/2){
				marker.setHidden(false);
			}
		}
	}

	private void hideAirportMarkers(){

		for(Marker marker : airportList){
			marker.setHidden(true);
		}

	}


	private void enable1Button(CityMarker marker, float x, float y){
		marker.setButton1Enabled(true);
	}

	private void enable2Button(CityMarker marker){
		marker.setButton2Enabled(true);
	}

	private void displayTop10EarthQuakes(List<Marker> quakeMarkers){

		text("Displaying top 10 earthquakes", 100, 100);
		for(int i = 10; i < quakeMarkers.size(); i++){
			quakeMarkers.get(i).setHidden(true);
		}
	}

	private void displayCityInThreat(EarthquakeMarker earthQuake){

		double km = earthQuake.threatCircle();
		for(Marker marker: cityMarkers){
			if(marker.getDistanceTo(earthQuake.getLocation()) > km){
				marker.setHidden(true);
			}
		}
		for(Marker marker : quakeMarkers){
			if(marker != earthQuake){
				marker.setHidden(true);
			}
		}
	}

	private void displayThreatningEarthQuake(CityMarker city){

		int total = 0;
		float totalMag = 0;
		for(Marker marker : quakeMarkers){

			double km = ((EarthquakeMarker)marker).threatCircle();
			if(city.getDistanceTo(marker.getLocation()) > km){
				marker.setHidden(true);
			}
			else{
				total++;
				totalMag += ((EarthquakeMarker) marker).getMagnitude();
			}
		}
		city.setNearyByQuakes(total);
		if(totalMag != 0){
			city.setAverageMag(totalMag/total);
		}else{
			city.setAverageMag(0);
		}

		for(Marker marker : cityMarkers){
			if(marker != city){
				marker.setHidden(true);
			}
		}
	}

	private Marker selectMarkerIfClicked(List<Marker> markers){
		int x = mouseX;
		int y = mouseY;
		for(Marker marker : markers){
			if(marker.isInside(map, x, y)){
				lastSelected = (CommonMarker) marker;
				lastSelected.setSelected(true);
				return marker;
			}
		}
		return null;
	}
	
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}


	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 150, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", xbase+25, ybase+25);
		
		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
//		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE,
//				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE,
//				tri_ybase+CityMarker.TRI_SIZE);
		imageMode(PConstants.CORNER);
		image(img, xbase+25, ybase+40);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);
		
		text("Land Quake", xbase+50, ybase+70);
		text("Ocean Quake", xbase+50, ybase+90);
		text("Size ~ Magnitude", xbase+25, ybase+110);
		
		fill(255, 255, 255);
		ellipse(xbase+35, 
				ybase+70, 
				10, 
				10);
		noFill();
		ellipse(xbase + 35, ybase +70, 15, 15);
		rect(xbase+35-5, ybase+90-5, 10, 10);
		
		fill(color(255, 255, 0));
		ellipse(xbase+35, ybase+140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+35, ybase+160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+35, ybase+180, 12, 12);
		fill(color(212, 180, 209));
		rectMode(PConstants.CORNER);
		rect(xbase, ybase+300, 150, 50);


		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+50, ybase+140);
		text("Intermediate", xbase+50, ybase+160);
		text("Deep", xbase+50, ybase+180);
		text("Past hour", xbase+50, ybase+200);
		text("Enable GDP Map", xbase+20,ybase+320);
		
		fill(255, 255, 255);
		int centerx = xbase+35;
		int centery = ybase+200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);
			
	}

	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.	
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}
}
