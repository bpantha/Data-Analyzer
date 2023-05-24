package edu.upenn.cit594;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.processor.Processor;
import edu.upenn.cit594.ui.UserInterface;

public class Main {
	
public static void main(String[] args) {
			
		boolean j = false;
		
		String commandLineRegex = "^--(?<name>.+?)=(?<value>.+)$";
		
		// does not match regex
		
		Map<String, Integer> validNames = new HashMap<>();
		validNames.put("--covid", 0);
		validNames.put("--properties", 0);
		validNames.put("--log", 0);
		validNames.put("--population", 0);
		
		for (String arg: args) {
			String[] name = arg.split("=");

			
			if (!arg.matches(commandLineRegex) || !validNames.keySet().contains(name[0])) {
				System.out.println("ERROR IN FILES DETECTED. TERMINATING PROGRAM.");
				return;
			}	
			
			else {
				validNames.put(name[0], validNames.get(name[0] + 1));
			}
							
			if (name[0].contains("covid")) {
				if (!name[1].contains("csv") && !name[1].contains("json")) {
					System.out.println("ERROR IN FILES DETECTED. TERMINATING PROGRAM.");
					return; 
					}
				}
			
		}
	
		
		String populationFile = "";
		String propertiesFile = "";
		String covidFile = "";
		String logFile = "";
		
		for(String arg: args) {
			String[] name = arg.split("=");
			if(name[0].contains("population")) {
				populationFile = name[1];
			}
			if(name[0].contains("properties")) {
				propertiesFile = name[1];
			}
			if(name[0].contains("covid")) {
				covidFile = name[1];
				if (name[1].contains("json")) {
					j = true;
				}
			}
			if(name[0].contains("log")) {
				logFile = name[1];
			}
		}
		
		
		Logger l = Logger.getInstance();
		l.changeDestination(logFile);
		Processor p = new Processor(populationFile, propertiesFile, covidFile, j);
			
		UserInterface ui = new UserInterface(p);
		
		ui.getUserResponse();
		
}
}

