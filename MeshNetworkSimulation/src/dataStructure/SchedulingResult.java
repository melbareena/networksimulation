package dataStructure;

import java.util.Vector;

import setting.ApplicationSettingFacade;

public class SchedulingResult {
	
	private Vector<Double> throughputData;
	
	private Vector<Double> sourceData;
	
	private Vector<Double> transmitData;
	
	private String schedulingStrategy;
	
	private String trafficGenerator;
	
	private double totalTrafficGenerated;
	
	private double averagePacketDelay;

	public void setThroughputData(Vector<Double> throughputData) {
		this.throughputData = throughputData;
	}

	public void setSourceData(Vector<Double> sourceData) {
		this.sourceData = sourceData;
	}

	public void setTransmitData(Vector<Double> transmitData) {
		this.transmitData = transmitData;
	}

	public void setSchedulingStrategy(String schedulingStrategy) {
		this.schedulingStrategy = schedulingStrategy;
	}

	public void setTrafficGenerator(String trafficGenerator) {
		this.trafficGenerator = trafficGenerator;
	}

	public void setTotalTrafficGenerated(double totalTrafficGenerated) {
		this.totalTrafficGenerated = totalTrafficGenerated;
	}

	public Vector<Double> getThroughputData() {
		return throughputData;
	}

	public Vector<Double> getSourceData() {
		return sourceData;
	}

	public Vector<Double> getTransmitData() {
		return transmitData;
	}

	public String getSchedulingStrategy() {
		return schedulingStrategy;
	}

	public String getTrafficGenerator() {
		return trafficGenerator;
	}

	public double getTotalTrafficGenerated() {
		return totalTrafficGenerated;
	}

	public double getAveragePacketDelay() {
		return averagePacketDelay;
	}

	public void setAveragePacketDelay(double averagePacketsDelay) {
		this.averagePacketDelay = averagePacketsDelay;
	}
	
	public double getAverageThroughputInSteadyState() {
		double sum = 0.0;
        for(int i = 0; i < ApplicationSettingFacade.Traffic.getDuration(); i++) {
        	sum += throughputData.get(i);
        }
        return sum / ApplicationSettingFacade.Traffic.getDuration();
	}

}
