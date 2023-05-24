package edu.upenn.cit594.datamanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.util.CovidData;
import edu.upenn.cit594.util.Population;

public class CsvCovidFileReader extends CsvReader<Object> implements CovidReader{
	

	public CsvCovidFileReader(String fileName){
		super(fileName);
	
	}
	
	public ArrayList<CovidData> parse(){
		
		Logger l = Logger.getInstance();
		
		l.log(fileName);

		
		List<CovidData> covidDataList = new ArrayList<CovidData>();
		
        try {
        	 List covidData = readRows();
             List header = (List) covidData.get(0);
             //System.out.println(header);
             int indexOfZipCode = header.indexOf("zip_code");
             //System.out.println(indexOfZipCode);
             int indexOfPartialVaccination = header.indexOf("partially_vaccinated");
             int indexOfFullVaccination = header.indexOf("fully_vaccinated");
             int indexOfTimeStamp = header.indexOf("etl_timestamp");

             
             for (int i = 1; i < covidData.size(); i++) {
             	List fields = (List) covidData.get(i);
             	String zipCode = (String) fields.get(indexOfZipCode);
             	int zipCodeDouble = Integer.parseInt(zipCode);;
             	String timeStamp = (String) fields.get(indexOfTimeStamp);
				// regex to match the pattern: 2021-03-25 17:20:02
				String timeStampRegex = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}";
				if (zipCode.length() != 5 || !timeStamp.matches(timeStampRegex)) {
					// ignore entries with incorrect format for zipcode and time stamp
					continue;
				}
				
				double pVax = 0.0;
				double fVax = 0.0;
				
				if (!fields.get(indexOfPartialVaccination).equals(" ")){
					if(isDouble(fields.get(indexOfPartialVaccination).toString())) {
						pVax = Double.parseDouble(fields.get(indexOfPartialVaccination).toString());
					}
				}
				
				if (!fields.get(indexOfFullVaccination).equals(" ")){
					if(isDouble(fields.get(indexOfFullVaccination).toString())) {
						fVax = Double.parseDouble(fields.get(indexOfFullVaccination).toString());
					}
				}
				
				
				covidDataList.add(new CovidData(zipCodeDouble, timeStamp, pVax, fVax));
				
				
					
				}	
             
       }
     catch (IOException e) {
       System.err.println(e.getMessage());
       e.printStackTrace();
     }
	return (ArrayList<CovidData>) covidDataList;
	
	
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
	
	/*
	 *public static void main(String[] args) {
		CsvCovidFileReader covidReader = new CsvCovidFileReader("covid_data.csv");
		
		List<CovidData> cd = covidReader.parseCsv();
		
		for(CovidData c: cd) {
			System.out.println(c);
		} 
	 */
	
	
		 
	
	}	

