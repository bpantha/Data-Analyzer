package edu.upenn.cit594.util;

public class Property {
	double marketValue;
	double totalLivableArea;
	int zipCode;
	
	public Property(double marketValue, double totalLivableArea, int zipCode) {
		this.marketValue = marketValue;
		this.totalLivableArea = totalLivableArea;
		this.zipCode = zipCode;
	}

	public double getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}

	public double getTotalLivableArea() {
		return totalLivableArea;
	}

	public void setTotalLivableArea(double totalLivableArea) {
		this.totalLivableArea = totalLivableArea;
	}

	public int getZipCode() {
		return zipCode;
	}

	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}

	@Override
	public String toString() {
		return "Property [marketValue=" + marketValue + ", totalLivableArea=" + totalLivableArea + ", zipCode="
				+ zipCode + "]";
	}	
}
