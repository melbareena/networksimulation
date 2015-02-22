package scheduling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import setting.ApplicationSettingFacade;
import trafficEstimating.TrafficEstimatingFacade;
import luncher.Luncher;
import dataStructure.Buffer;
import dataStructure.BufferMap;
import dataStructure.Link;
import dataStructure.SchedulingResult;
import dataStructure.TCUnit;

public abstract class DynamicAbstract
{
	protected int instanceIndex;
	protected int k = 3;
	protected BufferMap sourceBuffers;
	protected BufferMap transmitBuffers;
	protected List<TCUnit> configurations;
	protected final int durationOfTrafficGenerating = (int)ApplicationSettingFacade.Traffic.getDuration();
	
	/* For collecting results */
	protected Vector<Double> throughput;
	protected Vector<Double> trafficSource;
	protected Vector<Double> trafficTransit;
	protected Vector<Integer> packetsDelay;
	protected double totalTrafficGenerated;
	protected String trafficGenerator = "Dynamic";
	protected double maxTrafficSource;
	protected double maxTrafficTransmit;
	
	protected int _redoTimeSlot = ApplicationSettingFacade.getInterval();
	
	
	protected void updateProgress(int timeSlot)
	{
		if (maxTrafficSource < 0) 
		{
			maxTrafficSource = sourceBuffers.trafficSize();
			Luncher.loadingDialog.setProgress(this.instanceIndex, 0);
		}
		if (sourceBuffers.trafficSize() == 0) 
		{
			if (maxTrafficTransmit < 0) 
			{
				maxTrafficTransmit = transmitBuffers.trafficSize();
				Luncher.loadingDialog.setProgress(this.instanceIndex, 0);
			}
			Luncher.loadingDialog.setProgress(this.instanceIndex,
					(int) (100 - (99 * transmitBuffers.trafficSize() / maxTrafficTransmit)),
					"Disposing of transmit traffic ("+ transmitBuffers.trafficSize()
							+ " remaining, timeslot " + timeSlot + ")");
		} 
		else 
		{
			Luncher.loadingDialog.setProgress(this.instanceIndex,
					(int) (100 - (99 * sourceBuffers.trafficSize() / maxTrafficSource)),
					"Disposing of source traffic (" + sourceBuffers.trafficSize()
							+ " remaining, timeslot " + timeSlot + ")");
		}
	}
	
	protected SchedulingResult getResults() 
	{
		SchedulingResult results = new SchedulingResult();
		results.setSchedulingStrategy(getName());
		results.setTrafficGenerator(trafficGenerator);
		results.setThroughputData(throughput);
		results.setSourceBufferData(trafficSource);
		results.setTransmitBufferData(trafficTransit);
		results.setTotalTrafficGenerated(totalTrafficGenerated);
		double sum = 0;
		for(int i = 0; i < packetsDelay.size(); i++) {
			sum += packetsDelay.get(i);
		}
		results.setAveragePacketDelay(sum );
		return results;
	}
	
	/**
	 * calculate the weight of link in each TC
	 * @param isSourceBufferTraffic : {@value true} : calculate for source buffer
	 */
	protected void calcWeight(boolean isSourceBufferTraffic)
	{
		BufferMap targetMap;		
		if(isSourceBufferTraffic)
			targetMap = this.sourceBuffers;
		else
			targetMap = this.transmitBuffers;
		
		for(TCUnit tc : configurations)
		{
			Map<Link, Double> linksTraffic = new HashMap<Link, Double>();
			for (Link l : tc.getLinks())
			{
				double traffic = 0;
					
				if(targetMap.containsKey(l))
				{
					Buffer bs = targetMap.get(l);
					traffic = bs.size();
					linksTraffic.put(l, traffic);
				}
			}
			tc.calcLinkWeight(linksTraffic);
		}
		
	}
	
	protected double updateTraffic(int currentTimeSlot)
	{
		double currentTrafficAmount = 0.0;
		if(sourceBuffers != null) {
			currentTrafficAmount = sourceBuffers.trafficSize();
		}
		
	
		BufferMap timesoltBuffer = TrafficEstimatingFacade.getDynamicSourceBuffers(currentTimeSlot);
		if(timesoltBuffer != null && timesoltBuffer.size() > 0)
		{
			if(sourceBuffers == null) sourceBuffers = new BufferMap();
			sourceBuffers.Append(timesoltBuffer);
			return sourceBuffers.trafficSize() - currentTrafficAmount;
		}
		
		return 0;
		
	}
	
	protected double accumulationOfThroughput()
	{
		double accu = 0;
		for (Double t : throughput)
		{
			accu += t;
		}
		
		return  (double)Math.round(accu * 100000) / 100000;	
	}
	
	public abstract SchedulingResult doDeliveryPackets() ;
	
	protected abstract String getName();
}
