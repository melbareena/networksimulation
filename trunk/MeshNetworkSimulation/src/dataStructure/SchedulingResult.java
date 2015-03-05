package dataStructure;

import java.util.Vector;

import setting.ApplicationSettingFacade;
import trafficEstimating.TrafficEstimatingFacade;

public class SchedulingResult
{
	public SchedulingResult(boolean isAllChannelsAvailable)
	{
		allChannelsAvailable = isAllChannelsAvailable;
	}
	private boolean allChannelsAvailable;
	
	private Vector<Double> throughputData;
	
	private Vector<Double> sourceData;
	
	private Vector<Double> transmitData;
	
	private Vector<Double> averageDelayPerTimeSlot;
	
	
	private String schedulingStrategy;
	
	private String trafficGenerator;
	
	private double totalTrafficGenerated;
		
	
	
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

	
	public Vector<Double> getThroughputPerTimeSlot()
	{
		return throughputData;
	}
	
	/**
	 * 
	 * @return return throughput in Mbps
	 */
	public Vector<Double> getThroughputData() 
	{
		
		Vector<Double> mbps = new Vector<Double>();
		
		mbps.add(throughputData.get(0));
		
		int slotCounter = 0;
		double throughputAccumulation = 0;
		for (Double slotT : throughputData)
		{
			throughputAccumulation += slotT;
			slotCounter++;
			if(slotCounter == 50)
			{
				mbps.add(throughputAccumulation);
				slotCounter = 0;
				throughputAccumulation = 0;
			}
		}
		return mbps;
	}

	public Vector<Double> getSourceData() 
	{
		Vector<Double> mbps = new Vector<Double>();
		mbps.add(0d);
		int slotCounter = 0;
		for (Double slotT : sourceData)
		{
			slotCounter++;
			if(slotCounter == 50)
			{
				mbps.add(slotT);
				slotCounter = 0;
			}
		}

		return mbps;
	}
	public double getAverageThorughput() 
	{
		Vector<Double> th = getThroughputPerTimeSlot();
		double sum = 0;
		int index = 0;
		//int limit = (int) (ApplicationSettingFacade.Traffic.getDuration() / 50);
		int limit = allChannelsAvailable ? (int) (ApplicationSettingFacade.Traffic.getDuration()) - 50 :  th.size();
		int start = allChannelsAvailable ? 500 : 0;
		for(index = start; index < limit ; index++)
			sum += th.get(index);
		double average = (double) sum / (limit - start);
		
		average *= 50;
		return  (double) Math.round( average * 1000 ) / 1000 ;
		
		
			
	}
	public Vector<Double> getTransmitData() 
	{
		
		
		Vector<Double> mbps = new Vector<Double>();
		mbps.add(0d);
		int slotCounter = 0;
		for (Double slotT : transmitData)
		{
			slotCounter++;
			if(slotCounter == 50)
			{
				mbps.add(slotT);
				slotCounter = 0;
			}
		}
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
	
	
	public double getNetworkDelay()
	{
		long actualStopTime = getThroughputPerTimeSlot().size();
		long stopTime = ApplicationSettingFacade.Traffic.getDuration();
		
		long delaySlot = actualStopTime - stopTime;
		return (double) delaySlot / 50;
	}
	
	
	
	/**
	 * get end-to-end of received of packets over number of them for <b>a second</b>
	 * @return
	 */
	public Vector<Double> averageDelayPerSecond()
	{
		Vector<Double> dSecond = new Vector<Double>();
		int slotCounter = 0;
		for (Double slotT : averageDelayPerTimeSlot)
		{
			slotCounter++;
			if(slotCounter == 49)
			{
				dSecond.add(slotT);
				slotCounter = 0;
			}
		}
		return dSecond;
	}
	
	
	
	private Vector<Double> totalDelay;
	
	/**
	 * end-to-end delay of packets in each time slot
	 * @return
	 */
 	public  Vector<Double> getTotalDelay()
	{
		return totalDelay;
	}
 	
 	
 	public void setTotalDelay( Vector<Double> value)
	{
		this.totalDelay = value;
	}
 	
 	
 	/**
 	 * Get end-to-end delay of received packets over number of them for each time slot
 	 * @param value
 	 */
 	public Vector<Double> getAverageDelayPerTimeSlot()
	{
		return averageDelayPerTimeSlot;
	}

 	
 	/**
 	 * Set end-to-end delay of received packets over number of them for each time slot
 	 * @param value
 	 */
	public void setAverageDelayPerTimeSlot(Vector<Double> value)
	{
		this.averageDelayPerTimeSlot = value;
	}
	
	
	/**
	 * summation of end-to-end delay over number of packets 
	 * @return
	 */
	public double getAverageDelayOfPacket()
	{
		double sum = 0d;
		for (Double d1 : totalDelay)
		{
			sum += d1;
		}
		double average = sum / TrafficEstimatingFacade.totalGeneratedPackets;
		return  (double) Math.round( average * 10 ) / 10 ;
	}


}
