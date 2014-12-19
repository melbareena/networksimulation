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
import dataStructure.SchedulingResult;
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
	protected double maxTrafficSource;
	protected double maxTrafficTransmit;
	
	protected int instanceIndex;

	/**Creates a new SchedulingStrategy. Initiates all the buffers in the network.
	 * @param instanceIndex The instance index of the scheduling strategy.
	 * Used when various scheduling strategy are run in a single instance of the program.
	 */
	public SchedulingStrategy(int instanceIndex) {
		PrintConsole.printErr("Intiate Scheduling..........");
		if(!ApplicationSettingFacade.Traffic.isDynamicType())
			this.sourceBuffers = TrafficEstimatingFacade.getSourceBuffers(0);
		this.configurations = TCFacade.getConfigurations();
		this.transmitBuffers = new BufferMap();
		this.throughput = new Vector<Double>();
		this.trafficSource = new Vector<Double>();
		this.trafficTransit = new Vector<Double>();
		this.packetsDelay = new Vector<Integer>();
		this.maxTrafficSource = -1.0;
		this.maxTrafficTransmit = -1.0;
		this.instanceIndex = instanceIndex;
	}
	
	/**Schedules the different transmission configurations to dispose of the traffic
	 * in the network. Some new traffic will be randomly generated during the
	 * first time solts.
	 * @return The results of the scheduling.
	 */
	public SchedulingResult staticScheduling() {
		
		trafficGenerator = "Static";
		Vector<Link> selectedBuffers = null;
		Vector<TCUnit> transmissionConfiguraions = null;
		int timeSlot = 0;
		
		totalTrafficGenerated = sourceBuffers.trafficSize();
		
		while( sourceBuffers.trafficSize() > 0 || transmitBuffers.trafficSize() > 0 )
		{
			this.calcWeight(true);
			selectedBuffers = getBufferStrategy(true);
			
			double slotThroughtput = 0;

			if(selectedBuffers.size() > 0)
 			{
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
								if(!moved.isFragment()) {
									packetsDelay.add(moved.getDelay());
								}
								slotThroughtput += movedTraffic;
								tcunit.addThroughput(movedTraffic);
							}
						}
					}
					timeSlot++;
					updateProgress(timeSlot);
					throughput.add(slotThroughtput);
					trafficSource.add(sourceBuffers.trafficSize());
					trafficTransit.add(transmitBuffers.trafficSize());
				}
 			}
 			this.calcWeight(false);
 			selectedBuffers = getBufferStrategy(false);
 			
 			
 			if(selectedBuffers.size() > 0)
 			{
	 			transmissionConfiguraions = matching(selectedBuffers);
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
								if(!moved.isFragment()) {
									packetsDelay.add(moved.getDelay());
								}
								slotThroughtput += movedTraffic;
								tcunit.addThroughput(movedTraffic);
							}
						}
					}
					timeSlot++;
					updateProgress(timeSlot);
					throughput.add(slotThroughtput);
					trafficSource.add(sourceBuffers.trafficSize());
					trafficTransit.add(transmitBuffers.trafficSize());
				}
 			}
 			if(ApplicationSettingFacade.AppOutput.showIntermediateOutput())
 			{
 				PrintConsole.print(sourceBuffers.trafficSize() + "");
 				PrintConsole.printErr(sourceBuffers.trafficSize() + "");
 			}
		}
		FileGenerator.TCThroughput(configurations);
		FileGenerator.Throughput(throughput);
		
		return getResults();
	}

	/**Schedules the different transmission configurations to dispose of the traffic
	 * in the network. Some new traffic will be randomly generated during the
	 * first time solts.
	 * The number of timeslot during which some new traffic will be randomly generated
	 * is parsed from the XML configuration file.
	 * @return The results of the scheduling.
	 */
	public SchedulingResult dynamicScheduling() {
		this.dynamicScheduling(ApplicationSettingFacade.Traffic.getDuration());
		return getResults();
	}
	
	/**Schedules the different transmission configurations to dispose of the traffic
	 * in the network. Some new traffic will be randomly generated during the
	 * <code>durationOfTrafficGenerating</code> first time solts.
	 * @param durationOfTrafficGenerating The number of timeslot during which
	 * some new traffic will be randomly generated.
	 * @return The results of the scheduling.
	 */
	public SchedulingResult dynamicScheduling(long durationOfTrafficGenerating) {
		
		dynamicTrafficGenerator = new DynamicTrafficGenerator();
		
		trafficGenerator = "Dynamic";	
		Vector<Link> selectedBuffers = null;
		Vector<TCUnit> transmissionConfigurations = null;
		
		int timeSlot = 0; // Current number of time slot
		
		sourceBuffers = null;
		do {
			totalTrafficGenerated = updateTraffic(0); // Fill the source buffers with random traffic
		} while(sourceBuffers.trafficSize() == 0);

		while(sourceBuffers.trafficSize() > 0 || transmitBuffers.trafficSize() > 0 || timeSlot < durationOfTrafficGenerating) {
			// Source Buffers
			double slotThroughtput = 0;
			this.calcWeight(true);
			selectedBuffers = getBufferStrategy(true);
			if(selectedBuffers.size() > 0)
			{
				
	 			transmissionConfigurations = matching(selectedBuffers);
	 			for (TCUnit tcunit : transmissionConfigurations) {
					slotThroughtput = 0;
					for (Link link : tcunit.getLinks()) {
						if(sourceBuffers.containsKey(link)) {
							int dataRate = tcunit.getRate(link);
							Packet moved = sourceBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
							if(moved.isReceived()) {
								double movedTraffic = moved.getTraffic();
								if(!moved.isFragment()) {
									packetsDelay.add(moved.getDelay());
								}
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
						Program.loadingDialog.setProgress(this.instanceIndex,
								(int) (99*timeSlot/durationOfTrafficGenerating),
								"Generating traffic (slot "+timeSlot+" over "+durationOfTrafficGenerating+")");
					} else {
						updateProgress(timeSlot);
					}
				}
			}
 			
 			// Transmit buffers
 			this.calcWeight(false);
 			selectedBuffers = getBufferStrategy(false);
 			if(selectedBuffers.size() > 0)
 			{
 			
	 			transmissionConfigurations = matching(selectedBuffers);
	 			for (TCUnit tcunit : transmissionConfigurations) {
					slotThroughtput = 0;
					for (Link link : tcunit.getLinks()) {
						if(transmitBuffers.containsKey(link)) {
							int dataRate = tcunit.getRate(link);
							Packet moved = transmitBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
							if(moved.isReceived()) {
								double movedTraffic = moved.getTraffic();
								if(!moved.isFragment()) {
									packetsDelay.add(moved.getDelay());
								}
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
						Program.loadingDialog.setProgress(this.instanceIndex,
								(int) (99*timeSlot/durationOfTrafficGenerating),
								"Generating traffic (slot "+timeSlot+" over "+durationOfTrafficGenerating+")");
					} else {
						updateProgress(timeSlot);
					}
				}
			}
		}
		FileGenerator.TCThroughput(configurations);
		FileGenerator.Throughput(throughput);
		
		return getResults();
	}
	
	/**Updates the current traffic in the network.
	 * Some new packets may be added to some source buffers randomly.
	 * @param currentTimeSlot The current timeslot.
	 * @return The amount of new traffic generated.
	 * @see TrafficEstimatingFacade#getDynamicSourceBuffers(BufferMap, DynamicTrafficGenerator, int)
	 */
	protected double updateTraffic(int currentTimeSlot) {
		double currentTrafficAmount = 0.0;
		if(sourceBuffers != null) {
			currentTrafficAmount = sourceBuffers.trafficSize();
		}
		sourceBuffers = TrafficEstimatingFacade.getDynamicSourceBuffers(sourceBuffers, dynamicTrafficGenerator, currentTimeSlot);
		return sourceBuffers.trafficSize() - currentTrafficAmount;
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
	
	/**Collects and returns the results of the scheduling.
	 * This should only be used if the scheduling is over, otherwise
	 * the results collected are not guaranteed.
	 * @return The results of the scheduling.
	 */
	protected SchedulingResult getResults() {
		SchedulingResult results = new SchedulingResult();
		results.setSchedulingStrategy(getName());
		results.setTrafficGenerator(trafficGenerator);
		results.setThroughputData(throughput);
		results.setSourceData(trafficSource);
		results.setTransmitData(trafficTransit);
		results.setTotalTrafficGenerated(totalTrafficGenerated);
		double sum = 0;
		for(int i = 0; i < packetsDelay.size(); i++) {
			sum += packetsDelay.get(i);
		}
		System.out.println("Somme: "+sum);
		System.out.println("Mean: "+ (sum / packetsDelay.size()));
		results.setAveragePacketDelay(sum / packetsDelay.size());
		return results;
	}
	
	/**Updates the progress bar of this scheduling,
	 * indicating its current state at the current timeslot.
	 * @param timeSlot The current timeslot.
	 */
	protected void updateProgress(int timeSlot) {
		if (maxTrafficSource < 0) {
			maxTrafficSource = sourceBuffers.trafficSize();
			Program.loadingDialog.setProgress(this.instanceIndex, 0);
		}
		if (sourceBuffers.trafficSize() == 0) {
			if (maxTrafficTransmit < 0) {
				maxTrafficTransmit = transmitBuffers.trafficSize();
				Program.loadingDialog.setProgress(this.instanceIndex, 0);
			}
			Program.loadingDialog.setProgress(this.instanceIndex,
					(int) (100 - (99 * transmitBuffers.trafficSize() / maxTrafficTransmit)),
					"Disposing of transmit traffic ("+ transmitBuffers.trafficSize()
							+ " remaining, timeslot " + timeSlot + ")");
		} else {
			Program.loadingDialog.setProgress(this.instanceIndex,
					(int) (100 - (99 * sourceBuffers.trafficSize() / maxTrafficSource)),
					"Disposing of source traffic (" + sourceBuffers.trafficSize()
							+ " remaining, timeslot " + timeSlot + ")");
		}
	}
	
	/**
	 * @return The name of the scheduling strategy.
	 */
	protected abstract String getName();

}
