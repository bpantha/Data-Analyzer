package edu.upenn.cit594.util;

public class CovidData {
	
	int zipCode;
	String timeStamp;
	double partiallyVaccinated;
	double fullyVaccinated;
	
	public CovidData(int zipCode, String timeStamp, double partiallyVaccinated, double fullyVaccinated) {
		this.zipCode = zipCode;
		this.timeStamp = timeStamp;
		this.partiallyVaccinated = partiallyVaccinated;
		this.fullyVaccinated = fullyVaccinated;
	}

	public int getZipCode() {
		return zipCode;
	}

	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public double getPartiallyVaccinated() {
		return partiallyVaccinated;
	}

	public void setPartiallyVaccinated(double partiallyVaccinated) {
		this.partiallyVaccinated = partiallyVaccinated;
	}

	public double getFullyVaccinated() {
		return fullyVaccinated;
	}

	public void setFullyVaccinated(double fullyVaccinated) {
		this.fullyVaccinated = fullyVaccinated;
	}

	@Override
	public String toString() {
		return "CovidData [zipCode=" + zipCode + ", timeStamp=" + timeStamp + ", partiallyVaccinated="
				+ partiallyVaccinated + ", fullyVaccinated=" + fullyVaccinated + "]";
	}
}
