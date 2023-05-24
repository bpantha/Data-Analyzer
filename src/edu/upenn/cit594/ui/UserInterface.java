package edu.upenn.cit594.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.processor.Processor;

public class UserInterface {
	
	private String options;
	private Processor p;
	
	public UserInterface(Processor p) {
		this.p = p;
		this.options = "O. Exit the program" + 
				"\n1. Show the available data sets"
				+"\n2. Show the total population for all ZIP Codes"
				+"\n3. Show the total vaccinations per capita for each ZIP Code for the specified date"
				+"\n4. Show the average market value for properties in a specified ZIP Code"
				+"\n5. Show the average total livable area for properties in a specified ZIP Code"
				+ "\n6. Show the total market value of properties, per capita, for a specified ZIP Code"
				+"\n7. Show the full vaccination rates per capita for the top 10 richest homes by average market value for a specified date";
	}
	
	Logger l = Logger.getInstance();
		
	public void getUserResponse() {
		
		boolean exitProgram = false;
		Scanner scanner = new Scanner(System.in);

		while(exitProgram == false) {
			
			System.out.println("");
			System.out.println(this.options);
			System.out.print("> ");
			String userInput = scanner.next().trim();
			l.log(userInput);
			System.out.flush();
			
			while (isInt(userInput) == false || (!(Integer.parseInt(userInput) >= 0) && !(Integer.parseInt(userInput) <= 7))) {
				System.out.println(userInput + " was not a valid response! Please try again.");
				this.getUserResponse();
			}
			
			if (Integer.parseInt(userInput) == 0) {
				System.out.println("Goodbye.");
				exitProgram = true;
				scanner.close();
			}
			
			else if (Integer.parseInt(userInput) == 1) {				
				p.availableDataSets();
			}
			
			else if (Integer.parseInt(userInput) == 2) {
				if (p.isPopulationAllowed()) {
					System.out.println("");
					System.out.println("BEGIN OUTPUT");
					System.out.println(p.getTotalPopulationForAllZipCodes());
					System.out.println("END OUTPUT");
				}
				else {
					System.out.println("");
					System.out.println("BEGIN OUTPUT");
					System.out.println("Sorry, the files were not available.");	
					System.out.println("END OUTPUT");
				}
			}
			
			else if (Integer.parseInt(userInput) == 3) {
				if (p.isPopulationAllowed() && p.isCovidAllowed()) {
					System.out.println("Please type either \"partial\" or \"full\"");
					System.out.print("> ");
					userInput = scanner.next().trim().toLowerCase();
					l.log(userInput);
					String regexTimeStamp = "\\d{4}-\\d{2}-\\d{2}";
					
					String answer = "";
					
					while(!userInput.equals("partial") && !userInput.equals("full")) {
						askAgain();
						userInput = scanner.next().trim().toLowerCase();
					}
					
					answer = userInput;
					
					System.out.println("Please type a date, in the format YYYY-MM-DD, for which you would like to see partial vaccinations per capita.");
					System.out.print("> ");
					userInput = scanner.next().trim().toLowerCase();
					while (!userInput.matches(regexTimeStamp)) {
						System.out.println("Please input a valid format for the date.");
						System.out.print("> ");
						userInput = scanner.next().trim().toLowerCase();
						l.log(userInput);
					}
					if (userInput.matches(regexTimeStamp)) {
						if (answer.trim().equalsIgnoreCase("partial")) {
							if (p.initPartialVaxDates().contains(userInput)) {
								Map<String, Map<Integer, Double>> partial = p.getTotalPartialVaccinationsPerCapita();
								p.printVaxPerCapita(partial.get(userInput));
							}
							else {
								System.out.println("");
								System.out.println("BEGIN OUTPUT");
								System.out.println(0);
								System.out.println("END OUTPUT");
							}
						}
						else if (answer.trim().equalsIgnoreCase("full")){
							if (p.initFullVaxDates().contains(userInput)) {
								Map<String, Map<Integer, Double>> full = p.getTotalFullVaccinationsPerCapita();
								p.printVaxPerCapita(full.get(userInput));
							}
							else {
								System.out.println("");
								System.out.println("BEGIN OUTPUT");
								System.out.println(0);
								System.out.println("END OUTPUT");
							}
					}
					}					
				}
				else {
					System.out.println("");
					System.out.println("BEGIN OUTPUT");
					System.out.println("Sorry, the files were not available.");	
					System.out.println("END OUTPUT");

				}
				
			

				
			}
			else if (Integer.parseInt(userInput) == 4) {
				if (p.isPropertyAllowed()) {
					askZip();
					userInput = scanner.next().trim().toLowerCase();
					while(userInput.length() != 5 || isInt(userInput) == false) {
						askAgain();
						userInput = scanner.next().trim().toLowerCase();
						l.log(userInput);
					}
					p.printAvgMktValue(Integer.parseInt(userInput));
				}
				else {
					System.out.println("");
					System.out.println("BEGIN OUTPUT");
					System.out.println("Sorry, the files were not available.");	
					System.out.println("END OUTPUT");
				}
				
			}
			else if(Integer.parseInt(userInput) == 5) {
				if (p.isPropertyAllowed()) {
					askZip();
					userInput = scanner.next().trim().toLowerCase();
					while(userInput.length() != 5 || isInt(userInput) == false) {
						askAgain();
						userInput= scanner.next().trim().toLowerCase();
						l.log(userInput);
					}
					p.printAvgTotalLivableArea(Integer.parseInt(userInput));
				}
				else {
					System.out.println("");
					System.out.println("BEGIN OUTPUT");
					System.out.println("Sorry, the files were not available.");	
					System.out.println("END OUTPUT");
				}
				
			}
				
			else if(Integer.parseInt(userInput) == 6) {
				if (p.isPropertyAllowed() && p.isPopulationAllowed()) {
					askZip();
					userInput = scanner.next().trim().toLowerCase();
					while(userInput.length() != 5 || !isInt(userInput)) {
						askAgain();
						userInput = scanner.next().trim().toLowerCase();
						l.log(userInput);
					}
					if (isInt(userInput)) {
						p.printTotalMktValuePerCapita(Integer.parseInt(userInput));
					}
				}
				else {
					System.out.println("");
					System.out.println("BEGIN OUTPUT");
					System.out.println("Sorry, the files were not available.");	
					System.out.println("END OUTPUT");
				}

				
			}
			else if(Integer.parseInt(userInput) == 7) {
				if (p.isPopulationAllowed() && p.isCovidAllowed() && p.isPropertyAllowed()) {
					System.out.println("Please type a date, in the format YYYY-MM-DD, for which you would like to see the top 10 full vaccination rates per capita.");
					System.out.print("> ");
					userInput = scanner.next().trim().toLowerCase();
					String regexDate= "\\d{4}-\\d{2}-\\d{2}";
					
					while(!userInput.matches(regexDate)) {
						System.out.println("Please input a valid format for the date.");
						System.out.print("> ");
						userInput = scanner.next().trim().toLowerCase();
					}
					
					if (userInput.matches(regexDate)) {
						l.log(userInput);
						p.print10VaxRate(p.initTop10VaxRate(userInput));
					}
					
				}
				else {
					System.out.println("");
					System.out.println("BEGIN OUTPUT");
					System.out.println("Sorry, the files were not available.");	
					System.out.println("END OUTPUT");
				}
				
			}
	}
}

	public void askZip() {
		System.out.println("Please enter a 5-digit zipcode");
		System.out.print("> ");		
	}
	
	public void askAgain() {
		System.out.println("Please try again");
		System.out.print("> ");
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

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}
	
	
}
