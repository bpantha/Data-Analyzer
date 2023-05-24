package edu.upenn.cit594.datamanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.util.Population;

public class CsvPopulationFileReader extends CsvReader<Object> {

	public CsvPopulationFileReader(String fileName) {
		super(fileName);
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<Population> parseCsv(){
		
		Logger l = Logger.getInstance();
		
		l.log(fileName);
		
		ArrayList<Population> populationDataList = new ArrayList<Population>();
		
        try {
        	
            List populationsData = readRows();
            List header = (List) populationsData.get(0);
            int indexOfZipCode = header.indexOf("zip_code");
            int indexOfPopulation = header.indexOf("population");

            for (int i = 1; i < populationsData.size(); i++) {
            	List fields = (List) populationsData.get(i);
            	String zipCode = (String) fields.get(indexOfZipCode);
            	String population = (String) fields.get(indexOfPopulation);
            	if (zipCode.length() != 5 || isInt(population) == false) {
            		continue;
            	}
            	else {
            		int zipCodeInt = Integer.parseInt(zipCode);
            		int populationInt = Integer.parseInt(population);
            		populationDataList.add(new Population (zipCodeInt, populationInt));
            		
            	}
            	
            }
   
	    } catch (IOException e) {
	        System.err.println(e.getMessage());
	        e.printStackTrace();
	    }
		
		return populationDataList;
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

	/*
	 * public static void main(String[] args) {
		CsvPopulationFileReader pr = new CsvPopulationFileReader("/Users/sunnykarim/Downloads/CIT594-Project/population.csv");
		ArrayList<Population> populations = pr.parseCsv();
		for (Population population : populations) {
			System.out.println(population);
		}
	}
	 */
	 
	
	
	

}
