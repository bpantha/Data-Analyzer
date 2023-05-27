package edu.upenn.cit594.processor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Stack;

import edu.upenn.cit594.datamanagement.CsvCovidFileReader;
import edu.upenn.cit594.datamanagement.CsvPopulationFileReader;
import edu.upenn.cit594.datamanagement.CsvPropertyFileReader;
import edu.upenn.cit594.datamanagement.JsonCovidFileReader;
import edu.upenn.cit594.util.CovidData;
import edu.upenn.cit594.util.Population;
import edu.upenn.cit594.util.Property;

public class Processor implements Averages {
	
	List<Property> properties;
	List<Population> populations;
	List<CovidData> covidDatas;

	// memoization
	String[] dataSets;
	int totalPopulationForAllZipCodes;
	private Map<Integer, Integer> zipsAndPopulations;
	private Map<Integer, Double> avgMarketValue;
	private Map<Integer, Double> totalMarketValue;
	private Map<Integer, Double> avgTotalLivableArea;
	private Map<Integer, Double> totalMktValuePerCapita;
	private Set<String> fullVaxDates;
	Map<String, Map<Integer, Double>> totalPartialVaccinationsPerCapita;
	Map<String, Map<Integer, Double>> totalFullVaccinationsPerCapita;
	// vaccination rates per capita for top 10 richest neighborhoods
	private Map<Integer, Double> t10VaxPerCapita;
	
	// readers
	CsvPopulationFileReader populationReader;
	CsvPropertyFileReader propertyReader;
	CsvCovidFileReader covidReader;
	JsonCovidFileReader js;
	

	boolean populationAllowed = false;
	boolean propertyAllowed = false;
	boolean covidAllowed = false;


	public boolean isPopulationAllowed() {
		return populationAllowed;
	}
	public boolean isPropertyAllowed() {
		return propertyAllowed;
	}
	public boolean isCovidAllowed() {
		return covidAllowed;
	}
	
	/**
	 * Processor constructor checks if the given files are null or empty and 
	 * initializes the data readers based on which datasets are available. Also 
	 * stores each file name and outputs it to the dataSet array 
	 * @param populationFile
	 * @param propertyFile
	 * @param covidFile
	 * @param json
	 */
	public Processor(String populationFile, String propertyFile, String covidFile, boolean json) {
		if (!json) {
			String aData = "";
						
			if(populationFile != null && populationFile != "") {
				this.populationReader = new CsvPopulationFileReader(populationFile);
				this.populations = this.populationReader.parseCsv();
				populationAllowed = true;
				aData += "population,";
			}
						
			if(propertyFile != null && propertyFile != "") {
				this.propertyReader = new CsvPropertyFileReader(propertyFile);
				this.properties = this.propertyReader.parseCsv();
				propertyAllowed = true;
				aData += "properties,";
			}
						
			if(covidFile != null && covidFile != "") {
				this.covidReader = new CsvCovidFileReader(covidFile);
				this.covidDatas = this.covidReader.parse();
				covidAllowed = true;
				aData += "covid";
			
			}
			
			this.dataSets = aData.split(",");
		}
		else {
			String aData = "";
						
			if(populationFile != null && populationFile != "") {
				this.populationReader = new CsvPopulationFileReader(populationFile);
				this.populations = this.populationReader.parseCsv();
				populationAllowed = true;
				aData += "population,";
			}
			
			
			if(propertyFile != null && propertyFile != "") {
				this.propertyReader = new CsvPropertyFileReader(propertyFile);
				this.properties = this.propertyReader.parseCsv();
				propertyAllowed = true;
				aData += "properties,";
			}
			
			
			if(covidFile != null && covidFile != "") {
				this.js = new JsonCovidFileReader(covidFile);
				this.covidDatas = this.js.parse();
				covidAllowed = true;
				aData += "covid";
			
			}
			
			this.dataSets = aData.split(",");
		}
		
		initialize();
	}
	
	/**
	 * initializes the hashmaps for memoization based on which datasets are available
	 */
	public void initialize() {
		// 3.2
		if (this.populationAllowed) {
			this.zipsAndPopulations = zipPop();
			this.totalPopulationForAllZipCodes = totalPopAllZip();
		}
		
		
		//3.4
		if (this.populationAllowed && this.covidAllowed) {
			this.totalPartialVaccinationsPerCapita = initPartialVaccinationsPerCapita();
			this.totalFullVaccinationsPerCapita = initFullVaccinationsPerCapita();
			
		}
		
		if (this.propertyAllowed) {
			this.avgMarketValue = initAvgMktValue();
			this.avgTotalLivableArea = initAvgTotalLivableArea();
			if(this.populationAllowed) {
				this.totalMktValuePerCapita = initTotalMktValuePerCapita();	
			}
		}
		
		if(this.covidAllowed) {
			this.fullVaxDates = initFullVaxDates();
		}
		
	}
	/**
	 * prints to the console to signify the start of the output
	 */
	public void beginOutput(){
		System.out.println("");
		System.out.println("BEGIN OUTPUT");

	}
	/**
	 *  prints to the console to signify the end of the output
	 */
	public void endOutput(){
		System.out.println("END OUTPUT");
	}

	/**
	 *  prints to the console if a given output is zero
	 */
	public void zeroOutput(){
		System.out.println("");
		System.out.println("BEGIN OUTPUT");
		System.out.println(0);
		System.out.println("END OUTPUT");
	}

	public void fileNotAvailableOutput(){
		System.out.println("");
		System.out.println("BEGIN OUTPUT");
		System.out.println("Sorry, the files were not available.");	
		System.out.println("END OUTPUT");
	}	
	//3.1 
	/**
	 * iterates through each data set in this.datSets and prints to console each dataset
	 */
	public void availableDataSets() {
		Arrays.sort(this.dataSets);
		beginOutput();
		for(String d: dataSets) {
			System.out.println(d);
		}
		endOutput();
		
	}
	
	/**
	 * sums up values in zipsAndPopulations hashmap
	 * @return
	 */
	public int totalPopAllZip() {
		int tot = 0;
		for(int pop: this.getZipsAndPopulations().values()) {
			tot += pop;
		}
		
		this.totalPopulationForAllZipCodes = tot;
		return tot;
		
	}
	
	//3.2 
	/**
	 * If the user enters a 2 at the main menu, the program should display 
	 * the total population for all of the ZIP Codes in the population input file .
	 * @return zipsAndPopulations hashmap to initialize the memoized hashmap
	 */
	public Map<Integer, Integer> zipPop() {
		// hashmap to memoize zipcode and its given population
		Map<Integer, Integer> zipsAndPopulations = new HashMap<>();
		for (Population p: populations) {
			if(p.getZipCode() != 0 && p.getPopulation() != 0) {
				zipsAndPopulations.put(p.getZipCode(), p.getPopulation());
			}
		}
		return zipsAndPopulations;
	}
	
	
	//3.3
	
	/**
	 * Store the partial vaxinated dates in a set for quick acess
	 * @return set of dates for partial vaccinations
	 */
	
	public Set<String> initPartialVaxDates() {
		 Set<String> datesSet = new HashSet<>();
		 for(CovidData c: this.covidDatas) {
			 if(c != null) {
				 if (c.getPartiallyVaccinated() != 0.0) {
					// add date to the set by splitting the given etl_timestamp and only taking the first element 
					// which is the formated date "yyyy-mm-dd"
					 datesSet.add(c.getTimeStamp().split(" ")[0]);
				 }
			 }
		 }
		 return datesSet;
	}
	
	/**
	 * Iterate thorugh covid object and for each non zero partial vaccination for 
	 * each date, updates the vaxPerCapita hashmap. Additionally checks if the 
	 * population for a given zipcode is 0. If so, ignores the zipcode.
	 * @return hashMap conatining the date as they key and the value as a hashmap 
	 * of zipcode as the key and the partial vaccinations per capita as the value
	 */
	public Map<String, Map<Integer, Double>> initPartialVaccinationsPerCapita(){
		Map<String, Map<Integer, Double>> vaxPerCapita = new HashMap<>();
		
		for (CovidData c: covidDatas) {
			if (c != null) {
				String date = c.getTimeStamp().split(" ")[0];
				if(c.getPartiallyVaccinated() != 0.0 && 
					this.getZipsAndPopulations().keySet().contains(c.getZipCode()) && 
					c.getZipCode() != 0 && 
					this.getZipsAndPopulations().get(c.getZipCode()) != 0) {
							double pVaxPerC = c.getPartiallyVaccinated()/this.getZipsAndPopulations().get(c.getZipCode());
							if (vaxPerCapita.containsKey(date)) {
								vaxPerCapita.get(date).put(c.getZipCode(), pVaxPerC);
							}	
							else {
								vaxPerCapita.put(date, new HashMap<>());
								vaxPerCapita.get(date).put(c.getZipCode(), pVaxPerC);
						}
					}
				}
			}
			return vaxPerCapita;
	}
	
	// fully vaccinated 
	
	public Set<String> initFullVaxDates(){
		 Set<String> datesSet = new HashSet<>();
		 for(CovidData c: this.covidDatas) {
			 if (c != null) {
				 if (c.getFullyVaccinated() != 0.0) {
					 datesSet.add(c.getTimeStamp().split(" ")[0]);
				 }
			 }
		 }
		 return datesSet;
	}

	/**
	 * Iterate thorugh covid object and for each non zero full vaccination for 
	 * each date, updates the fullVaxPerCapita hashmap. Additionally checks if the 
	 * population for a given zipcode is 0. If so, ignores the zipcode.
	 * @return hashMap conatining the date as they key and the value as a hashmap 
	 * of zipcode as the key and the partial vaccinations per capita as the value
	 */
	
	public Map<String, Map<Integer, Double>> initFullVaccinationsPerCapita() {
		Map<String, Map<Integer, Double>> fullVaxPerCapita = new HashMap<>();
		
		for (CovidData c: covidDatas) {
			if (c!= null) {
				String date = c.getTimeStamp().split(" ")[0];
				if(c.getFullyVaccinated() != 0.0 && 
					this.getZipsAndPopulations().keySet().contains(c.getZipCode()) && 
					c.getZipCode() != 0 && 
					this.getZipsAndPopulations().get(c.getZipCode()) != 0) {
							double fVaxPerC = c.getFullyVaccinated()/this.getZipsAndPopulations().get(c.getZipCode());
							if (fullVaxPerCapita.containsKey(date)) {
								fullVaxPerCapita.get(date).put(c.getZipCode(), fVaxPerC);
							}	
							else {
								fullVaxPerCapita.put(date, new HashMap<>());
								fullVaxPerCapita.get(date).put(c.getZipCode(), fVaxPerC);
						}
					}
				}
			}
		
			return fullVaxPerCapita;

		}

	/**
	 * prints output of partial or full vax per capita to the console
	 * @param data resulting hashmap from date specified by user input from the 
	 * memoized hashmap of partial for full vaccination for a given date
	 */

	public void printVaxPerCapita(Map<Integer, Double> data) {
		System.out.println("");
		System.out.println("BEGIN OUTPUT");
		DecimalFormat df = new DecimalFormat("0.0000");
	
		for(Map.Entry<Integer,Double> entry : data.entrySet()) {
			System.out.println(entry.getKey() + " " + df.format(entry.getValue()));
		}
		
		System.out.println("END OUTPUT");
	}
		
	//3.4
	public Map<Integer,Double> initAvgMktValue() {
		Map<Integer,Double> zTot = new HashMap<Integer, Double>();
		Map<Integer,Integer> zCount = new HashMap<Integer, Integer>();
		for (Property p: this.getProperties()) {
			//double mktValue = p.getMarketValue();
			int zip = p.getZipCode();
			if (zTot.containsKey(zip)) {
				double currentMktValue = zTot.get(zip);
				zTot.put(zip, currentMktValue + p.getMarketValue());
				int currentCount = zCount.get(zip);
				zCount.put(zip, currentCount + 1);
			}
			else {
				zTot.put(zip, p.getMarketValue());
				zCount.put(zip, 1);
			}
		}
		
		setTotalMarketValue(zTot);
		
		// now, lets find the averages
		Map<Integer,Double> zAvg = new HashMap<Integer, Double>();
		for (Map.Entry<Integer, Double> entry : zTot.entrySet()) {
			double avgMktValue = 0.0;
			double propTotalMktValue = entry.getValue();
			double zipCount = zCount.get(entry.getKey());
			
			if(!(zipCount == 0.0)) {
				avgMktValue = propTotalMktValue/zipCount;
			}
			
			zAvg.put(entry.getKey(), avgMktValue);
	    }
		return zAvg;
	}
	
	public void printAvgMktValue(int zipCode) {
		System.out.println("");
		System.out.println("BEGIN OUTPUT");
		if (!this.avgMarketValue.keySet().contains(zipCode)) {
			System.out.println(0);
		}
		else {
			int val = this.avgMarketValue.get(zipCode).intValue();
			System.out.println(val);
		}
		System.out.println("END OUTPUT");
	}
	
	

	
	//3.5
	public Map<Integer, Double> getTotalMarketValue() {
		return totalMarketValue;
	}

	public void setTotalMarketValue(Map<Integer, Double> totalMarketValue) {
		this.totalMarketValue = totalMarketValue;
	}
	
	public Map<Integer, Double> initTotalMarketValue(){
		Map<Integer, Double> zTot = new HashMap<>();
		for (Property p: properties) {
			int zip = p.getZipCode();
			if (zTot.containsKey(zip)) {
				double currentMktValue = zTot.get(zip);
				zTot.put(zip, currentMktValue + p.getMarketValue());
			}
			else {
				zTot.put(zip, p.getMarketValue());
			}
		}
		
		return zTot;
	}
	
	public void printTotalMktValuePerCapita(int zipCode) {
		System.out.println("");
		System.out.println("BEGIN OUTPUT");
		if (!this.totalMktValuePerCapita.keySet().contains(zipCode)) {
			System.out.println(0);
		}
		
		else {
			int val = this.totalMktValuePerCapita.get(zipCode).intValue();
			System.out.println(val);
		}
		
		System.out.println("END OUTPUT");
	}
	
	//3.5 
	public Map<Integer,Double> initAvgTotalLivableArea() {
		
		Map<Integer,Double> zTot = new HashMap<Integer, Double>();
		Map<Integer,Integer> zCount = new HashMap<Integer, Integer>();
		for (Property p: this.getProperties()) {
			//double mktValue = p.getMarketValue();
			int zip = p.getZipCode();
			if (zTot.containsKey(zip)) {
				double currentLivableArea = zTot.get(zip);
				zTot.put(zip, currentLivableArea + p.getTotalLivableArea());
				int currentCount = zCount.get(zip);
				zCount.put(zip, currentCount + 1);
			}
			else{
				zTot.put(zip, p.getTotalLivableArea());
				zCount.put(zip, 1);
			}
		}
		
		// now, lets find the averages
		Map<Integer,Double> zAvg = new HashMap<Integer, Double>();
		for (Map.Entry<Integer, Double> entry : zTot.entrySet()) {
			double avgLivableArea = 0.0;
			double propTotalLivableArea = entry.getValue();
			double zipCount = zCount.get(entry.getKey());
			
			if(!(zipCount == 0.0)) {
				avgLivableArea = propTotalLivableArea/zipCount;
			}
			
			zAvg.put(entry.getKey(), avgLivableArea);
	    }
	
		return zAvg;
	}
	
	public void printAvgTotalLivableArea(int zipCode) {
		System.out.println("");
		System.out.println("BEGIN OUTPUT");
		if (!this.avgTotalLivableArea.keySet().contains(zipCode)) {
			System.out.println(0);
		}
		
		else {
			int val = this.avgTotalLivableArea.get(zipCode).intValue();
			System.out.println(val);
		}
		System.out.println("END OUTPUT");
	}
	
	public double getAvgTotalLivableArea(int zipCode) {
		if (!this.avgTotalLivableArea.keySet().contains(zipCode)) {
			return 0;
		}
		
		return avgTotalLivableArea.get(zipCode).intValue();
	}
	
	//3.6
	public Map<Integer,Double> initTotalMktValuePerCapita() {
		Map<Integer,Double> zTot = this.getTotalMarketValue();
		Map<Integer, Integer> zPop = this.getZipsAndPopulations();
		
		Map<Integer, Double> zFinal = new HashMap<>();
		
		for (Map.Entry<Integer,Double> entry : zTot.entrySet()) {
			if(zPop.keySet().contains(entry.getKey())) {
				//if() do all of the if conditions
				if (entry.getValue() != 0 && zPop.get(entry.getKey()) != 0) {
					zFinal.put(entry.getKey(), entry.getValue()/zPop.get(entry.getKey()));
				}
				
			}
		}	
		return zFinal;
	}
	
	public int getTotalMktValuePerCapita(int zipCode) {
		
		if (!this.totalMktValuePerCapita.keySet().contains(zipCode)) {
			return 0;
		}
	
		return this.totalMktValuePerCapita.get(zipCode).intValue();
	}

	public Map<Integer, Integer> getZipsAndPopulations() {
		return zipsAndPopulations;
	}

	public void setZipsAndPopulations(Map<Integer, Integer> zipsAndPopulations) {
		this.zipsAndPopulations = zipsAndPopulations;
	}

	public double getAvgMktValue(int zipCode) {
		if (!this.avgMarketValue.keySet().contains(zipCode)) {
			return 0;
		}
		
		return avgMarketValue.get(zipCode).intValue();
	}
	
	// 3.7 
	
	public Map<Integer, Double> initTop10VaxRate(String date){
		
		
		Map<Integer,Double> marketValuePerCapita = this.totalMktValuePerCapita;
		Map<Integer,Double> top10Vax = new TreeMap<Integer, Double>();
		Map<Integer,Double> vaxPerCapita =  this.totalFullVaccinationsPerCapita.get(date);
		if (vaxPerCapita == null) {
			return null;
		}
		List<Double> listOfValues = new ArrayList<Double>(marketValuePerCapita.values());
		Collections.sort(listOfValues);
		Stack<Double> stackOfValues = new Stack<Double>();

		for (double value: listOfValues) {
			stackOfValues.add(value);
		}
		
		while (top10Vax.size() < 10) {
			double max = stackOfValues.pop();
			for (Map.Entry<Integer,Double> entry : marketValuePerCapita.entrySet()) {
				if (entry.getValue() == max) {
					int key = entry.getKey();
					if (vaxPerCapita.containsKey(key)) {
						double vaxRate = vaxPerCapita.get(key);
						top10Vax.put(key, vaxRate);
					}
					else {
						continue;
					}
				}
			}
		}
		return top10Vax;
	}
	
	public void print10VaxRate(Map<Integer, Double> data) {
		System.out.println("");
		System.out.println("BEGIN OUTPUT");
		if (data != null) {
			for(Map.Entry<Integer,Double> entry : data.entrySet()) {
				System.out.print(entry.getKey() + " " + String.format("%.4g%n", entry.getValue()));
			}
		}
		else {
			System.out.println(0);
		}
		
		System.out.println("END OUTPUT");
	}
	
	
	public int getTotalPopulationForAllZipCodes() {
		return totalPopulationForAllZipCodes;
	}

	public void setTotalPopulationForAllZipCodes(int totalPopulationForAllZipCodes) {
		this.totalPopulationForAllZipCodes = totalPopulationForAllZipCodes;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	public List<Population> getPopulations() {
		return populations;
	}
	
	//3.6
	public int totalMktValuePerCapita(String zipCode) {
				
		double totalMVpC = 0.0;
		double totalMV = 0.0;
		double population = 0.0;
		Double zip = Double.valueOf(zipCode);
		for (Property p: properties) {
			if(zip.equals(p.getZipCode())) {
				//totalMV += p.getTotalLivableArea();
			}
		}
		
		for (Population pop: populations) {
			if (zip.equals(pop.getZipCode())) {
				population = pop.getPopulation();
			}
		}
		
		if (population == 0.0) {
			return 0;
		}
		
		totalMVpC = totalMV/population;
		
		return (int) totalMVpC;
	}

	public List<CovidData> getCovidDatas() {
		return covidDatas;
	}

	public void setCovidDatas(List<CovidData> covidDatas) {
		this.covidDatas = covidDatas;
	}

	public Map<Integer, Double> getAvgMarketValue() {
		return avgMarketValue;
	}

	public void setAvgMarketValue(Map<Integer, Double> avgMarketValue) {
		this.avgMarketValue = avgMarketValue;
	}

	public Map<Integer, Double> getAvgTotalLivableArea() {
		return avgTotalLivableArea;
	}

	public void setAvgTotalLivableArea(Map<Integer, Double> avgTotalLivableArea) {
		this.avgTotalLivableArea = avgTotalLivableArea;
	}

	public Map<Integer, Double> getTotalMktValuePerCapita() {
		return totalMktValuePerCapita;
	}

	public void setTotalMktValuePerCapita(Map<Integer, Double> totalMktValuePerCapita) {
		this.totalMktValuePerCapita = totalMktValuePerCapita;
	}

	public Set<String> getFullVaxDates() {
		return fullVaxDates;
	}

	public void setFullVaxDates(Set<String> fullVaxDates) {
		this.fullVaxDates = fullVaxDates;
	}

	public Map<String, Map<Integer, Double>> getTotalPartialVaccinationsPerCapita() {
		return totalPartialVaccinationsPerCapita;
	}

	public void setTotalPartialVaccinationsPerCapita(Map<String, Map<Integer, Double>> totalPartialVaccinationsPerCapita) {
		this.totalPartialVaccinationsPerCapita = totalPartialVaccinationsPerCapita;
	}

	public Map<String, Map<Integer, Double>> getTotalFullVaccinationsPerCapita() {
		return totalFullVaccinationsPerCapita;
	}

	public void setTotalFullVaccinationsPerCapita(Map<String, Map<Integer, Double>> totalFullVaccinationsPerCapita) {
		this.totalFullVaccinationsPerCapita = totalFullVaccinationsPerCapita;
	}

	public Map<Integer, Double> getT10VaxPerCapita() {
		return t10VaxPerCapita;
	}

	public void setT10VaxPerCapita(Map<Integer, Double> t10VaxPerCapita) {
		this.t10VaxPerCapita = t10VaxPerCapita;
	}

	public CsvPopulationFileReader getPopulationReader() {
		return populationReader;
	}

	public void setPopulationReader(CsvPopulationFileReader populationReader) {
		this.populationReader = populationReader;
	}

	public CsvPropertyFileReader getPropertyReader() {
		return propertyReader;
	}

	public void setPropertyReader(CsvPropertyFileReader propertyReader) {
		this.propertyReader = propertyReader;
	}

	public CsvCovidFileReader getCovidReader() {
		return covidReader;
	}

	public void setCovidReader(CsvCovidFileReader covidReader) {
		this.covidReader = covidReader;
	}


	public String[] getDataSets() {
		return dataSets;
	}

	public void setDataSets(String[] dataSets) {
		this.dataSets = dataSets;
	}

	public void setPopulations(List<Population> populations) {
		this.populations = populations;
	}
	public void setPopulationAllowed(boolean populationAllowed) {
		this.populationAllowed = populationAllowed;
	}
	
	public void setPropertyAllowed(boolean propertyAllowed) {
		this.propertyAllowed = propertyAllowed;
	}

	

	public void setCovidAllowed(boolean covidAllowed) {
		this.covidAllowed = covidAllowed;
	}
}
	