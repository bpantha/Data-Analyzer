package edu.upenn.cit594.datamanagement;

import java.util.List;

import edu.upenn.cit594.util.CovidData;

public interface CovidReader {
	public List<CovidData> parse();
}
