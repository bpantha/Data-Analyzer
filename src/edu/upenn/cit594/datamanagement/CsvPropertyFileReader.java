package edu.upenn.cit594.datamanagement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.util.CovidData;
import edu.upenn.cit594.util.Property;

public class CsvPropertyFileReader extends CsvReader<Object> {
	
	public CsvPropertyFileReader(String fileName) {
		super(fileName);
		// TODO Auto-generated constructor stub
	}

	
	public List<Property> parseCsv(){
		
		Logger l = Logger.getInstance();
		
		l.log(fileName);
		
		
		//public Property(double marketValue, double totalLivableArea, double zipCode)
		
		ArrayList<Property> propertyDataList = new ArrayList<Property>();
		
        try {
        	List propertyData = readRows();
            List header = (List) propertyData.get(0);
            int indexOfMarketValue = header.indexOf("market_value");
            int indexOfTotalLivableArea = header.indexOf("total_livable_area");
            int indexOfZipCode = header.indexOf("zip_code");
            
            for (int i = 1; i < propertyData.size(); i++) {
            	List fields = (List) propertyData.get(i);
            	String zipCode = (String) fields.get(indexOfZipCode);
            	String marketValue = (String) fields.get(indexOfMarketValue);
            	String totalLivableArea = (String) fields.get(indexOfTotalLivableArea);
            	
            	if ( isInt(zipCode) && zipCode.length() >=5) {
            		StringBuilder fiveDigitZip = new StringBuilder();
            		for (int j = 0; j < 5; j ++) {
            			fiveDigitZip.append(zipCode.charAt(j));
            		}
            		String zip = fiveDigitZip.toString();
            		int zipDouble = Integer.parseInt(zip);
            		if (isDouble(marketValue) == true && isDouble(totalLivableArea) == true) {
                		propertyDataList.add(new Property(Double.parseDouble(marketValue), Double.parseDouble(totalLivableArea),zipDouble));

            		}
            		else if (isDouble(marketValue) == false && isDouble(totalLivableArea) == true) {
                		propertyDataList.add(new Property(0, Double.parseDouble(totalLivableArea), zipDouble));

            		}
            		else if(isDouble(marketValue) == true && isDouble(totalLivableArea) == false) {
                		propertyDataList.add(new Property(Double.parseDouble(marketValue), 0, zipDouble));
            		}
            	}
            	else {
            		continue;
                    	
            } 
            	
            	
     }  
         
        } 
        catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
		
		return propertyDataList;
		
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
	 * public static void main(String[] args) {
		CsvPropertyFileReader pr = new CsvPropertyFileReader("properties.csv");
		List<Property> propertyList = pr.parseCsv();
		for (Property property : propertyList) {
			System.out.println(property);
		}
	}
	 */
	
	
	 
}
