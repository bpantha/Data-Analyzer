package edu.upenn.cit594.datamanagement;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.json.simple.JSONArray;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.util.CovidData;



public class JsonCovidFileReader implements CovidReader {
		
	public String fileName;
	
	public JsonCovidFileReader(String fileName) {
		this.fileName = fileName;
	}
	
	public List<CovidData> parse() {
		Logger l = Logger.getInstance();
		l.log(fileName);
		Object obj = null; // set object to null
		
		List<CovidData> covidList = new ArrayList<CovidData>();
	
		JSONParser parser = new JSONParser();
		try {
			obj =  parser.parse(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray ja = (JSONArray) obj; // cast object to JsonArray
		
		
		Iterator jaitr = ja.iterator();
		while(jaitr.hasNext()) {
			JSONObject jo = (JSONObject) jaitr.next();
			String timeStamp = (String)jo.get("etl_timestamp");
			String[] timeStampArray = timeStamp.split(" ");
			int zipCode = Integer.parseInt(jo.get("zip_code").toString());
			// regex to match the pattern: “YYYY- MM-DD hh:mm:ss”
			String timeStampRegex = "\\d{4}-\\d{2}-\\d{2}";	
			
			double partiallyVaccinated = 0.0;
			double fullyVaccinated = 0.0;
			
			if (jo.get("zip_code") == null|| jo.get("etl_timestamp") == null || String.valueOf(zipCode).length() != 5 
					|| !timeStampArray[0].matches(timeStampRegex) || !isInt(jo.get("zip_code").toString())) {
				continue;			

			}
			else{
				if(jo.get("partially_vaccinated") != null && isInt(jo.get("partially_vaccinated").toString())) {
					partiallyVaccinated = Integer.parseInt(jo.get("partially_vaccinated").toString());
				}
				
				if(jo.get("fully_vaccinated") != null && isInt(jo.get("fully_vaccinated").toString())) {
					fullyVaccinated = Integer.parseInt(jo.get("fully_vaccinated").toString());
				}
				
				covidList.add(new CovidData(zipCode, timeStamp, partiallyVaccinated, fullyVaccinated));
				
			}
			
			}
		return covidList;
		}
	
			

	
	public boolean isInt(String s){
	    try
	    {
	        Integer.parseInt(s);
	        return true;
	    } 
	    catch (NumberFormatException e){
	        return false;
	    }
	}
	
	public boolean isDouble(String s){
	    try
	    {
	        Double.parseDouble(s);
	        return true;
	    } 
	    catch (NumberFormatException e){
	        return false;
	    }
	}
	

	 {
	
	
}
}
		
		
