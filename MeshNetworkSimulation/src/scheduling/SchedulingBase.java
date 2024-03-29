package scheduling;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import luncher.Luncher;
import common.FileGenerator;
import common.PrintConsole;
import setting.ApplicationSettingFacade;
import trafficEstimating.TrafficEstimatingFacade;
import trafficGenerator.DTGFacade;
import transConf.TCFacade;
import dataStructure.Buffer;
import dataStructure.BufferMap;
import dataStructure.Link;
import dataStructure.Packet;
import dataStructure.SchedulingResult;
import dataStructure.TCUnit;


public abstract class SchedulingBase
{

	protected int k = 3;
	protected BufferMap sourceBuffers;
	protected BufferMap transmitBuffers;
	protected List<TCUnit> configurations;

	
	/* For collecting results */
	protected Vector<Double> throughput;
	protected Vector<Double> trafficSource;
	protected Vector<Double> trafficTransit;
	protected double totalTrafficGenerated;
	protected String trafficGenerator;
	protected double maxTrafficSource;
	protected double maxTrafficTransmit;
	protected static int _totalpacketNumber = 0;
	protected Vector<Double> averageDelayPerTimeSlot;
	protected int instanceIndex;

	/**Creates a new SchedulingBase. Initiates all the buffers in the network.
	 * @param instanceIndex The instance index of the scheduling strategy.
	 * Used when various scheduling strategy are run in a single instance of the program.
	 */
	public SchedulingBase(int instanceIndex) {
		PrintConsole.printErr("Intiate Scheduling..........");
		if(!ApplicationSettingFacade.Traffic.isDynamicType())
			this.sourceBuffers = TrafficEstimatingFacade.getSourceBuffers(0);
		this.configurations = TCFacade.getConfigurations();
		this.transmitBuffers = new BufferMap();
		this.throughput = new Vector<Double>();
		this.trafficSource = new Vector<Double>();
		this.trafficTransit = new Vector<Double>();
		this.averageDelayPerTimeSlot = new Vector<Double>();
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
		double slotThroughtput = 0;
		int numberOfOriginalReceivedPacket = 0;
		double delayTS = 0;
		totalTrafficGenerated = sourceBuffers.trafficSize();
		
		while( sourceBuffers.trafficSize() > 0 || transmitBuffers.trafficSize() > 0 )
		{
			this.calcWeight(true);
			selectedBuffers = getBufferStrategy(true);
			
			slotThroughtput = 0;
			numberOfOriginalReceivedPacket = 0;
			delayTS = 0;
			if(selectedBuffers.size() > 0)
 			{
	 			transmissionConfiguraions = matching(selectedBuffers);
	 			//System.out.println( sourceBuffers.trafficSize()) ;
	 			for (TCUnit tcunit : transmissionConfiguraions)
				{
					slotThroughtput = 0;
					numberOfOriginalReceivedPacket = 0;
					delayTS = 0;
					
					for (Link link : tcunit.getLinks())
					{
						if(sourceBuffers.containsKey(link))
						{
							double dataRate = (double)tcunit.getRate(link) ;															
							List<Packet> movedPackets = sourceBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
							for (Packet moved : movedPackets) 
								if(moved.isReceived())
								{
									double movedTraffic = moved.getTraffic();
									if(moved.isOrginalPacket())
									{
										_totalpacketNumber++;
										numberOfOriginalReceivedPacket++;
										delayTS += moved.getDelay();
									}
									slotThroughtput += movedTraffic;
									tcunit.addThroughput(movedTraffic);
								}						
						}
					}
					AddAverageDelay(numberOfOriginalReceivedPacket, delayTS);
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
					numberOfOriginalReceivedPacket = 0;
					delayTS = 0;
					
					for (Link link : tcunit.getLinks())
					{
						if(transmitBuffers.containsKey(link))
						{
							double dataRate = (double) tcunit.getRate(link);
							List<Packet> movedPackets = transmitBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
							
							for (Packet moved : movedPackets)
							{
								if(moved.isReceived()) 
								{
									double movedTraffic = moved.getTraffic();
									if(moved.isOrginalPacket()) 
									{
										_totalpacketNumber++;
										numberOfOriginalReceivedPacket++;
										delayTS += moved.getDelay();
									}
									slotThroughtput += movedTraffic;
									tcunit.addThroughput(movedTraffic);
								}
							}
							
						}
					}
					
					AddAverageDelay(numberOfOriginalReceivedPacket, delayTS);
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

	protected void AddAverageDelay(int numberOfOriginalReceivedPacket,double delayTS)
	{
		if(numberOfOriginalReceivedPacket > 0)
			averageDelayPerTimeSlot.add(delayTS / numberOfOriginalReceivedPacket);
		else
			averageDelayPerTimeSlot.add(0d);
		
	}

	/**Schedules the different transmission configurations to dispose of the traffic
	 * in the network. Some new traffic will be randomly generated during the
	 * first time solts.
	 * The number of timeslot during which some new traffic will be randomly generated
	 * is parsed from the XML configuration file.
	 * @return The results of the scheduling.
	 */
	public SchedulingResult dynamicScheduling() 
	{
	
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
		
	
		
		trafficGenerator = "Dynamic";	
		Vector<Link> selectedBuffers = null;
		Vector<TCUnit> transmissionConfigurations = null;
		
		int timeSlot = -1; // Current number of time slot
		double slotThroughtput = 0;
		int numberOfOriginalReceivedPacket = 0;
		double delayTS = 0;
		sourceBuffers = null;
		do 
		{
			timeSlot++;
			totalTrafficGenerated = updateTraffic(timeSlot); // Fill the source buffers with random traffic
			
		} while(sourceBuffers.trafficSize() == 0);

		while(sourceBuffers.trafficSize() > 0 || transmitBuffers.trafficSize() > 0 || timeSlot < durationOfTrafficGenerating) 
		{
			slotThroughtput = 0;
			numberOfOriginalReceivedPacket = 0;
			delayTS = 0;
			//both of buffers are empty 
			if(sourceBuffers.trafficSize() == 0 && transmitBuffers.trafficSize() == 0)
			{
				timeSlot++;
				throughput.add(0d);
				averageDelayPerTimeSlot.add(0d);
				trafficSource.add(sourceBuffers.trafficSize());
				trafficTransit.add(transmitBuffers.trafficSize());
				if(timeSlot < durationOfTrafficGenerating)
					totalTrafficGenerated += updateTraffic(timeSlot);
				continue;
			}
			 
		
			if(sourceBuffers.trafficSize() > 0)
			{
				this.calcWeight(true);
				selectedBuffers = getBufferStrategy(true);
				if(selectedBuffers.size() > 0)
				{
					
		 			transmissionConfigurations = matching(selectedBuffers);
		 			for (TCUnit tcunit : transmissionConfigurations) 
		 			{
		 				
		 				slotThroughtput = 0;
		 				numberOfOriginalReceivedPacket = 0;
		 				delayTS = 0;
						for (Link link : tcunit.getLinks()) 
						{
							if(sourceBuffers.containsKey(link)) 
							{
								double dataRate = (double)tcunit.getRate(link) ;															
								List<Packet> movedPackets = sourceBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
								for (Packet moved : movedPackets) 
									if(moved.isReceived())
									{
										double movedTraffic = moved.getTraffic();
										if(moved.isOrginalPacket())
										{
											_totalpacketNumber++;
											numberOfOriginalReceivedPacket++;
											delayTS += moved.getDelay();
										}
										slotThroughtput += movedTraffic;
										tcunit.addThroughput(movedTraffic);
									}						
							}
						}
						throughput.add(slotThroughtput);
						AddAverageDelay(numberOfOriginalReceivedPacket, delayTS);
						trafficSource.add(sourceBuffers.trafficSize());
						trafficTransit.add(transmitBuffers.trafficSize());
						timeSlot++;
						if(timeSlot < durationOfTrafficGenerating) 
						{
							totalTrafficGenerated += updateTraffic(timeSlot);
							Luncher.loadingDialog.setProgress(this.instanceIndex,
									(int) (99*timeSlot/durationOfTrafficGenerating),
									"Generating traffic (slot "+timeSlot+" over "+durationOfTrafficGenerating+")");
						}
						else
							updateProgress(timeSlot);
					}
				}
			}
			if(transmitBuffers.trafficSize() > 0)
			{
				if(selectedBuffers.size() > 0)
	 			{
	 			
		 			transmissionConfigurations = matching(selectedBuffers);
		 			for (TCUnit tcunit : transmissionConfigurations)
		 			{
		 				slotThroughtput = 0;
		 				numberOfOriginalReceivedPacket = 0;
		 				delayTS = 0;
						for (Link link : tcunit.getLinks())
						{
							if(transmitBuffers.containsKey(link)) 
							{
								double dataRate = (double) tcunit.getRate(link);
								List<Packet> movedPackets = transmitBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
								
								for (Packet moved : movedPackets)
								{
									if(moved.isReceived()) 
									{
										double movedTraffic = moved.getTraffic();
										if(moved.isOrginalPacket())
										{
											_totalpacketNumber++;
											numberOfOriginalReceivedPacket++;
											delayTS += moved.getDelay();
										}
										slotThroughtput += movedTraffic;
										tcunit.addThroughput(movedTraffic);
									}
								}
								
							}
						}
						throughput.add(slotThroughtput);
						AddAverageDelay(numberOfOriginalReceivedPacket, delayTS);
						trafficSource.add(sourceBuffers.trafficSize());
						trafficTransit.add(transmitBuffers.trafficSize());
						timeSlot++;
						if(timeSlot < durationOfTrafficGenerating) 
						{
							totalTrafficGenerated += updateTraffic(timeSlot);
							Luncher.loadingDialog.setProgress(this.instanceIndex,
									(int) (99*timeSlot/durationOfTrafficGenerating),
									"Generating traffic (slot "+timeSlot+" over "+durationOfTrafficGenerating+")");
						} else 
							updateProgress(timeSlot);
					}
				}
			}
 			this.calcWeight(false);
 			selectedBuffers = getBufferStrategy(false);
 			
		}
		FileGenerator.TCThroughput(configurations);
		FileGenerator.Throughput(throughput);
		
		assert(accumulationOfThroughput() == DTGFacade.getTotalTraffic())
		: "throughput is not valid \n traffic in MAP:" +  DTGFacade.getTotalTraffic() + " Throughput =" + accumulationOfThroughput();

		
		return getResults();
	}
	
	double accumulationOfThroughput()
	{
		double accu = 0;
		for (Double t : throughput)
		{
			accu += t;
		}
		
		return  (double)Math.round(accu * 100000) / 100000;	
	}

	/**Updates the current traffic in the network.
	 * Some new packets may be added to some source buffers randomly.
	 * @param currentTimeSlot The current timeslot.
	 * @return The amount of new traffic generated.
	 * @see TrafficEstimatingFacade#getDynamicSourceBuffers(BufferMap, DynamicTrafficGenerator, int)
	 */
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
			if(tc.containsLinks(maxKBuffer))
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
	protected SchedulingResult getResults() 
	{
		SchedulingResult results = new SchedulingResult(ApplicationSettingFacade.Channel.getChannel().size() == 11);
		results.setSchedulingStrategy(getName());
		results.setTrafficGenerator(trafficGenerator);
		results.setThroughputData(throughput);
		results.setSourceBufferData(trafficSource);
		results.setTransmitBufferData(trafficTransit);
		results.setTotalTrafficGenerated(totalTrafficGenerated);
		return results;
	}
	
	/**Updates the progress bar of this scheduling,
	 * indicating its current state at the current timeslot.
	 * @param timeSlot The current timeslot.
	 */
	protected void updateProgress(int timeSlot) {
		if (maxTrafficSource < 0) {
			maxTrafficSource = sourceBuffers.trafficSize();
			Luncher.loadingDialog.setProgress(this.instanceIndex, 0);
		}
		if (sourceBuffers.trafficSize() == 0) {
			if (maxTrafficTransmit < 0) {
				maxTrafficTransmit = transmitBuffers.trafficSize();
				Luncher.loadingDialog.setProgress(this.instanceIndex, 0);
			}
			Luncher.loadingDialog.setProgress(this.instanceIndex,
					(int) (100 - (99 * transmitBuffers.trafficSize() / maxTrafficTransmit)),
					"Disposing of transmit traffic ("+ transmitBuffers.trafficSize()
							+ " remaining, timeslot " + timeSlot + ")");
		} else {
			Luncher.loadingDialog.setProgress(this.instanceIndex,
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
