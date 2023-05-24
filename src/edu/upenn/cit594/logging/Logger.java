package edu.upenn.cit594.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
	
	private FileWriter fw;
	private PrintWriter out;
	
	private Logger() {
		}
	
	private static Logger instance = new Logger();
	
	public static Logger getInstance() {
		return instance;
	}
	
	public void log(String msg) {
		msg = System.currentTimeMillis() + " " + msg;
		out.println(msg);
		out.flush();
	}
	
	public void changeDestination(String fileName) {
		try {
			if (fileName.isBlank()) {
				fw = new FileWriter("errors.txt", true);
			}
			else {
				fw = new FileWriter(fileName, true);	
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        out = new PrintWriter(fw, true);
	}
	
	public void closeFile() {
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.close();
	}
}
