package scheduling;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import launcher.Program;
import common.FileGenerator;
import common.PrintConsole;
import setting.ApplicationSettingFacade;
import trafficEstimating.TrafficEstimatingFacade;
import trafficGenerator.DynamicTrafficGenerator;
import transConf.TCFacade;
import dataStructure.Buffer;
import dataStructure.BufferMap;
import dataStructure.Link;
import dataStructure.Packet;
import dataStructure.TCUnit;


public abstract class SchedulingStrategy
{

	protected int k = 3;
	protected BufferMap sourceBuffers;
	protected BufferMap transmitBuffers;
	protected List<TCUnit> configurations;
	
	/** The generator used to generate dynamically some new traffic in the network. */
	protected DynamicTrafficGenerator dynamicTrafficGenerator;
	
	/* For collecting results */
	protected Vector<Double> throughput;
	protected Vector<Double> trafficSource;
	protected Vector<Double> trafficTransit;
	protected Vector<Integer> packetsDelay;
	protected double totalTrafficGenerated;
	protected String trafficGenerator;
	
	private void Initiation() {
 		sourceBuffers = TrafficEstimatingFacade.getSourceBuffers(0);
		configurations = TCFacade.getConfigurations();
		transmitBuffers = new BufferMap();
		throughput = new Vector<Double>();
		trafficSource = new Vector<Double>();
		trafficTransit = new Vector<Double>();
		packetsDelay = new Vector<Integer>();
	}
	
	protected SchedulingStrategy(DynamicTrafficGenerator dynamicTrafficGenerator) {
		this.dynamicTrafficGenerator = dynamicTrafficGenerator;
		this.Initiation();
	}
	
	protected SchedulingStrategy() {
		this.Initiation();
	}
	
	public void scheduling()
	{
		trafficGenerator = "Static";
		Program.loadingDialog.setIndeterminate(false);
		Vector<Link> selectedBuffers = null;
		Vector<TCUnit> transmissionConfiguraions = null;
		int timeSlot = 0;
		
		totalTrafficGenerated = sourceBuffers.trafficSize();
		double maxTrafficTransmit = -1.0;
		
		while( sourceBuffers.trafficSize() > 0 || transmitBuffers.trafficSize() > 0 )
		{
			this.calcWeight(true);
			selectedBuffers = getBufferStrategy(true);
			
			double slotThroughtput = 0;

 			transmissionConfiguraions = matching(selectedBuffers);
 			//System.out.println( sourceBuffers.trafficSize()) ;
 			for (TCUnit tcunit : transmissionConfiguraions)
			{
				slotThroughtput = 0;
			
				
				for (Link link : tcunit.getLinks())
				{
					if(sourceBuffers.containsKey(link))
					{
						int dataRate = tcunit.getRate(link);
						Packet moved = sourceBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
						if(moved.isReceived())
						{
							double movedTraffic = moved.getTraffic();
							packetsDelay.add(moved.getDelay());
							slotThroughtput += movedTraffic;
							tcunit.addThroughput(movedTraffic);
						}
					}
				}
				timeSlot++;
				if(sourceBuffers.trafficSize() == 0) {
					if(maxTrafficTransmit < 0) {
						maxTrafficTransmit = transmitBuffers.trafficSize();
						Program.loadingDialog.setProgress(0);
					}
					Program.loadingDialog.setProgress((int) (100-(99*transmitBuffers.trafficSize()/maxTrafficTransmit)),
							"Disposing of transmit traffic ("+transmitBuffers.trafficSize()+" remaining, timeslot "+timeSlot+")");
				} else {
					Program.loadingDialog.setProgress((int) (100-(99*sourceBuffers.trafficSize()/totalTrafficGenerated)),
							"Disposing of source traffic ("+sourceBuffers.trafficSize()+" remaining, timeslot "+timeSlot+")");
				}
				throughput.add(slotThroughtput);
				trafficSource.add(sourceBuffers.trafficSize());
				trafficTransit.add(transmitBuffers.trafficSize());
			}
 			this.calcWeight(false);
 			selectedBuffers = getBufferStrategy(false);
 			
 			transmissionConfiguraions = matching(selectedBuffers);
 			//System.err.println(transmitBuffers.trafficSize()) ;
 			for (TCUnit tcunit : transmissionConfiguraions)
			{
				slotThroughtput = 0;
				
				for (Link link : tcunit.getLinks())
				{
					if(transmitBuffers.containsKey(link))
					{
						int dataRate = tcunit.getRate(link);
						Packet moved = transmitBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
						if(moved.isReceived())
						{
							double movedTraffic = moved.getTraffic();
							packetsDelay.add(moved.getDelay());
							slotThroughtput += movedTraffic;
							tcunit.addThroughput(movedTraffic);
						}
					}
				}
				timeSlot++;
				if(sourceBuffers.trafficSize() == 0) {
					if(maxTrafficTransmit < 0) {
						maxTrafficTransmit = transmitBuffers.trafficSize();
						Program.loadingDialog.setProgress(0);
					}
					Program.loadingDialog.setProgress((int) (100-(99*transmitBuffers.trafficSize()/maxTrafficTransmit)),
							"Disposing of transmit traffic ("+transmitBuffers.trafficSize()+" remaining, timeslot "+timeSlot+")");
				} else {
					Program.loadingDialog.setProgress((int) (100-(99*sourceBuffers.trafficSize()/totalTrafficGenerated)),
							"Disposing of source traffic ("+sourceBuffers.trafficSize()+" remaining, timeslot "+timeSlot+")");
				}
				throughput.add(slotThroughtput);
				trafficSource.add(sourceBuffers.trafficSize());
				trafficTransit.add(transmitBuffers.trafficSize());
			}
 			if(ApplicationSettingFacade.AppOutput.showIntermediateOutput())
 			{
 				PrintConsole.print(sourceBuffers.trafficSize() + "");
 				PrintConsole.printErr(sourceBuffers.trafficSize() + "");
 			}
		}
		FileGenerator.TCThroughput(configurations);
		FileGenerator.Throughput(throughput);
	}

	/**Schedule the different transmission configurations to dispose of the traffic
	 * in the network. Some new traffic will be randomly generated during the
	 * first time solts.
	 * The number of timeslot during which some new traffic will be randomly generated
	 * is parsed from the XML configuration file.
	 * @return The results of the scheduling.
	 */
	public void dynamicScheduling() {
		this.dynamicScheduling(ApplicationSettingFacade.Traffic.getDuration());
	}
	
	/**Schedule the different transmission configurations to dispose of the traffic
	 * in the network. Some new traffic will be randomly generated during the
	 * <code>durationOfTrafficGenerating</code> first time solts.
	 * @param durationOfTrafficGenerating The number of timeslot during which
	 * some new traffic will be randomly generated.
	 */
	public void dynamicScheduling(long durationOfTrafficGenerating) {
		trafficGenerator = "Dynamic";
		
		Vector<Link> selectedBuffers = null;
		Vector<TCUnit> transmissionConfigurations = null;
		
		int timeSlot = 0; // Current number of time slot
		
		sourceBuffers = null;
		do {
			totalTrafficGenerated = updateTraffic(0); // Fill the source buffers with random traffic
		} while(sourceBuffers.trafficSize() == 0);

		double maxTrafficSource = -1.0;
		double maxTrafficTransmit = -1.0;
		
		while(sourceBuffers.trafficSize() > 0 || transmitBuffers.trafficSize() > 0 || timeSlot < durationOfTrafficGenerating) {
			// Source Buffers
			this.calcWeight(true);
			selectedBuffers = getBufferStrategy(true);
			double slotThroughtput = 0;
 			transmissionConfigurations = matching(selectedBuffers);
 			for (TCUnit tcunit : transmissionConfigurations) {
				slotThroughtput = 0;
				for (Link link : tcunit.getLinks()) {
					if(sourceBuffers.containsKey(link)) {
						int dataRate = tcunit.getRate(link);
						Packet moved = sourceBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
						if(moved.isReceived()) {
							double movedTraffic = moved.getTraffic();
							packetsDelay.add(moved.getDelay());
							slotThroughtput += movedTraffic;
							tcunit.addThroughput(movedTraffic);
						}
					}
				}
				throughput.add(slotThroughtput);
				trafficSource.add(sourceBuffers.trafficSize());
				trafficTransit.add(transmitBuffers.trafficSize());
				/*----------------------*
				 * Generate new traffic *
				 * And display progress *
				 *----------------------*/
				timeSlot++;
				if(timeSlot < durationOfTrafficGenerating) {
					totalTrafficGenerated += updateTraffic(timeSlot);
					Program.loadingDialog.setProgress((int) (99*timeSlot/durationOfTrafficGenerating),
							"Generating traffic (slot "+timeSlot+" over "+durationOfTrafficGenerating+")");
				} else {
					if(maxTrafficSource < 0) {
						maxTrafficSource = sourceBuffers.trafficSize();
						Program.loadingDialog.setProgress(0);
					}
					if(sourceBuffers.trafficSize() == 0) {
						if(maxTrafficTransmit < 0) {
							maxTrafficTransmit = transmitBuffers.trafficSize();
							Program.loadingDialog.setProgress(0);
						}
						Program.loadingDialog.setProgress((int) (100-(99*transmitBuffers.trafficSize()/maxTrafficTransmit)),
								"Disposing of transmit traffic ("+transmitBuffers.trafficSize()+" remaining, timeslot "+timeSlot+")");
					} else {
						Program.loadingDialog.setProgress((int) (100-(99*sourceBuffers.trafficSize()/maxTrafficSource)),
								"Disposing of source traffic ("+sourceBuffers.trafficSize()+" remaining, timeslot "+timeSlot+")");
					}
				}
			}
 			
 			// Transmit buffers
 			this.calcWeight(false);
 			selectedBuffers = getBufferStrategy(false);
 			transmissionConfigurations = matching(selectedBuffers);
 			for (TCUnit tcunit : transmissionConfigurations) {
				slotThroughtput = 0;
				for (Link link : tcunit.getLinks()) {
					if(transmitBuffers.containsKey(link)) {
						int dataRate = tcunit.getRate(link);
						Packet moved = transmitBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
						if(moved.isReceived()) {
							double movedTraffic = moved.getTraffic();
							packetsDelay.add(moved.getDelay());
							slotThroughtput += movedTraffic;
							tcunit.addThroughput(movedTraffic);
						}
					}
				}
				throughput.add(slotThroughtput);
				trafficSource.add(sourceBuffers.trafficSize());
				trafficTransit.add(transmitBuffers.trafficSize());
				/*----------------------*
				 * Generate new traffic *
				 * And display progress *
				 *----------------------*/
				timeSlot++;
				if(timeSlot < durationOfTrafficGenerating) {
					totalTrafficGenerated += updateTraffic(timeSlot);
					Program.loadingDialog.setProgress((int) (99*timeSlot/durationOfTrafficGenerating),
							"Generating traffic (slot "+timeSlot+" over "+durationOfTrafficGenerating+")");
				} else {
					if(maxTrafficSource < 0) {
						maxTrafficSource = sourceBuffers.trafficSize();
						Program.loadingDialog.setProgress(0);
					}
					if(sourceBuffers.trafficSize() == 0) {
						if(maxTrafficTransmit < 0) {
							maxTrafficTransmit = transmitBuffers.trafficSize();
							Program.loadingDialog.setProgress(0);
						}
						Program.loadingDialog.setProgress((int) (100-(99*transmitBuffers.trafficSize()/maxTrafficTransmit)),
								"Disposing of transmit traffic ("+transmitBuffers.trafficSize()+" remaining, timeslot "+timeSlot+")");
					} else {
						Program.loadingDialog.setProgress((int) (100-(99*sourceBuffers.trafficSize()/maxTrafficSource)),
								"Disposing of source traffic ("+sourceBuffers.trafficSize()+" remaining, timeslot "+timeSlot+")");
					}
				}
			}
		}
		FileGenerator.TCThroughput(configurations);
		FileGenerator.Throughput(throughput);
		
		int sum = 0;
		for(int i = 0; i < packetsDelay.size(); i++) {
			sum += packetsDelay.get(i);
		}
		System.out.println("Average delay: "+(sum / packetsDelay.size())+" timeslots");
	}
	
	/**Update the current traffic in the network. Some new packets may be added
	 * to some source buffers randomly.
	 */
	protected double updateTraffic(int currentTimeSlot) {
		double currentTrafficAmount = 0.0;
		if(sourceBuffers != null) {
			currentTrafficAmount = sourceBuffers.trafficSize();
		}
		sourceBuffers = TrafficEstimatingFacade.getDynamicSourceBuffers(sourceBuffers, dynamicTrafficGenerator, currentTimeSlot);
		return sourceBuffers.trafficSize() - currentTrafficAmount;
	}
	
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
	
	private Vector<TCUnit> matching(Vector<Link> maxKBuffer)
	{
		Vector<TCUnit> result = new Vector<>();
		Vector<TCUnit> allInOneTC = new Vector<>();
		for (TCUnit tc : configurations)
		{
			if(tc.isLinksAvailable(maxKBuffer))
				allInOneTC.add(tc);
		}
		if(allInOneTC.size() >= k)
		{
			TreeSet<Double> weights = new TreeSet<>(Collections.reverseOrder());
			for (TCUnit tcUnit : allInOneTC)
			{
				weights.add(tcUnit.getMatchingRate(maxKBuffer));
			}
			int indexK = 0;
			for (Double rate : weights)
			{
				if(indexK == k) break;
				for (TCUnit tcUnit : allInOneTC)
				{
					if(tcUnit.getMatchingRate(maxKBuffer) == rate)
						result.add(tcUnit);
				}
				indexK++;
			}
			
		}
		else if(allInOneTC.size() > 0 && allInOneTC.size() <= k)
			result = allInOneTC;
		
		else if(allInOneTC.size() == 0)
		{
			double max = 0;
			TCUnit maxTC;
			for (Link l : maxKBuffer)
			{
				max = 0;
				maxTC = null;
				for (TCUnit tc : configurations)
				{
					double rate = tc.getMatchingRate(l);
					if(rate > max)
					{
						max = rate;
						maxTC = tc;
					}
				}
				if(maxTC != null)
					result.add(maxTC);
			}
		}
		
		return result;
		
	}
	
	protected abstract Vector<Link> getBufferStrategy(boolean isSourceBuffer);
	
	public Results getResults() {
		Results results = new Results();
		results.setSchedulingStrategy(getName());
		results.setTrafficGenerator(trafficGenerator);
		results.setThroughputData(throughput);
		results.setSourceData(trafficSource);
		results.setTransmitData(trafficTransit);
		results.setTotalTrafficGenerated(totalTrafficGenerated);
		return results;
	}
	
	protected abstract String getName();

}
