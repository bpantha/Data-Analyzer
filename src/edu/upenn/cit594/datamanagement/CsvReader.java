package edu.upenn.cit594.datamanagement;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class CsvReader<E> {
	
	String fileName; 
	
	public CsvReader(String fileName) {
		this.fileName = fileName;
	}
	
	public enum STATE{
	    START, DQUOTE, ESCAPED, TEXTDATA;
	}
	
	
	
	public List<E> readRows() throws IOException {
		STATE state = STATE.START; // start
        StringBuilder field = new StringBuilder(); // stores each field before adding to array list
        List<String> strings = new ArrayList<>(); // stores each field for a line
        List<List<String>> allStrings = new ArrayList<>();
        File file = new File(fileName);
        FileReader fileReader = null;

		try{
			fileReader = new FileReader(file);
		}
		catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
		}
		
       
        int charVal;
      
		while ((charVal = fileReader.read()) != -1){ // loop until we hit the end of the stream
		    char cur = (char) charVal;
		    
		    switch(state){
		        case START:   // start at the first character
		            switch(cur){
		                case ',': // if comma break
		                    strings.add(" ");
		                    break;
		                case '"': // if double quote change state to DQUOTE but don't append to stringbuilder
		                    state = STATE.DQUOTE;
		                    break;
		                default: // if its none of those above we append to string builder
		                    field.append(cur);
		                    state = STATE.TEXTDATA; // set state to TEXTDATA
		                    break;
		            }
		            break; // break out and go to the next iteration
		        case TEXTDATA: 
		            switch (cur){
		                case ',': // if comma encountered after TEXTDATA, add the field to the arraylist
		                    strings.add(field.toString());
		                    field.setLength(0); // reset the field string builder
		                    state = STATE.START; // state is set to start
		                    break;
		                 case '\n':
		                	strings.add(field.toString());
		                	List<String> newList = new ArrayList<>(strings);
		                	allStrings.add(newList);
		                    strings.clear();
		                    field.setLength(0); 
		                	state = STATE.START;
		                	break;		                
		                default:
		                    field.append(cur);  // otherwise append cur to field
		                    break;
		            }
		            break;
		        case DQUOTE:
		            switch(cur){
		                case '"': // quote after another quote is escaped and we don't append
		                    state = STATE.ESCAPED;
		                    break;
		                default: // otherwise append to field
		                    field.append(cur);
		                    break;
		            }
		            break;
		            
		        case ESCAPED:
		            switch(cur){
	                	case ',': // comma after escaped add field to strings array
		                    strings.add(field.toString());
		                    field.setLength(0);
		                    state = STATE.START;
		                    break;
	                	case '\n':
	                		strings.add(field.toString());
	                		List<String> newList = new ArrayList<>(strings);
		                	allStrings.add(newList);
	                		strings.clear();
		                    field.setLength(0);
		                    state = STATE.START;
							break;
		                case '"':
		                    field.append(cur);
		                    state = STATE.DQUOTE;
		                    break;
		            }
		           break;
		    }
		}
		
		return (List<E>) allStrings;
}
	
	
	public List<E> parseCsv;
		

}