package scheduling;

import java.util.Vector;

public class Results {
	
	private Vector<Double> throughputData;
	
	private Vector<Double> sourceData;
	
	private Vector<Double> transmitData;
	
	private String schedulingStrategy;
	
	private String trafficGenerator;
	
	private double totalTrafficGenerated;
	
	private double averagePacketDelay;

	protected void setThroughputData(Vector<Double> throughputData) {
		this.throughputData = throughputData;
	}

	protected void setSourceData(Vector<Double> sourceData) {
		this.sourceData = sourceData;
	}

	protected void setTransmitData(Vector<Double> transmitData) {
		this.transmitData = transmitData;
	}

	protected void setSchedulingStrategy(String schedulingStrategy) {
		this.schedulingStrategy = schedulingStrategy;
	}

	protected void setTrafficGenerator(String trafficGenerator) {
		this.trafficGenerator = trafficGenerator;
	}

	protected void setTotalTrafficGenerated(double totalTrafficGenerated) {
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

}
