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

	public void setThroughputData(Vector<Double> throughputData) 
	
	{
		this.throughputData = throughputData;
	}

	public void setSourceBufferData(Vector<Double> sourceData) 
	{
		this.sourceData = sourceData;
	}

	public void setTransmitBufferData(Vector<Double> transmitData)
	{
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

	/**
	 * 
	 * @return return throughput in Mbps
	 */
	public Vector<Double> getThroughputData() 
	{
		
		Vector<Double> mbps = new Vector<Double>();
		
		mbps.add(throughputData.get(0));
		
		int slotCounter = 1;
		double throughputAccumulation = 0;
		for (Double slotT : throughputData)
		{
			throughputAccumulation += slotT;
			slotCounter++;
			if(slotCounter == 50)
			{
				mbps.add(throughputAccumulation);
				slotCounter = 1;
				throughputAccumulation = 0;
			}
		}
		if(sourceData.size() % 50 != 0)
			mbps.add(throughputData.get(throughputData.size() - 1 ));
		return mbps;
	}

	public Vector<Double> getSourceData() 
	{
		Vector<Double> mbps = new Vector<Double>();
		mbps.add(sourceData.get(0));
		int slotCounter = 1;
		for (Double slotT : sourceData)
		{
			slotCounter++;
			if(slotCounter == 50)
			{
				mbps.add(slotT);
				slotCounter = 1;
			}
		}
		if(sourceData.size() % 50 != 0)
			mbps.add(0d);
		return mbps;
	}

	public Vector<Double> getTransmitData() 
	{
		
		
		Vector<Double> mbps = new Vector<Double>();
		mbps.add(transmitData.get(0));
		int slotCounter = 1;
		for (Double slotT : transmitData)
		{
			slotCounter++;
			if(slotCounter == 50)
			{
				mbps.add(slotT);
				slotCounter = 1;
			}
		}
		if(transmitData.size() % 50 != 0)
			mbps.add(0d);
		return mbps;
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

	public void setAveragePacketDelay(double averagePacketsDelay) 
	{
		this.averagePacketDelay = (int)(averagePacketsDelay / 50);
	}
	
	public double getAverageThroughputInSteadyState() {
		double sum = 0.0;
        for(int i = 0; i < ApplicationSettingFacade.Traffic.getDuration(); i++) {
        	sum += throughputData.get(i);
        }
        return sum / ApplicationSettingFacade.Traffic.getDuration();
	}
	public double getAverageThroughput()
	{
		return getTotalTrafficGenerated() / throughputData.size();
	}

}
