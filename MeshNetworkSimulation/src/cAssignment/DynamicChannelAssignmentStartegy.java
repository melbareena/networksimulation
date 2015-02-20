package cAssignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import setting.ApplicationSettingFacade;
import topology.TopologyFacade;
import trafficEstimating.TrafficEstimatingFacade;
import common.FileGenerator;
import dataStructure.BufferMap;
import dataStructure.Channel;
import dataStructure.ChannelOccuranceMap;
import dataStructure.IFactorMap;
import dataStructure.Link;
import dataStructure.LinkTrafficMap;
import dataStructure.LinksAmbienNoiseMap;
import dataStructure.LinksChannelMap;
import dataStructure.Vertex;

public class DynamicChannelAssignmentStartegy
{
	protected static LinksChannelMap linksChannel;
	protected static LinksAmbienNoiseMap linksAmbientNoise;
	protected static Map<Vertex, Map<Vertex, Double>> nodesDistances;
	protected static final float ALPHA = ApplicationSettingFacade.SINR.getAlpha();
	protected static final float BETA = ApplicationSettingFacade.SINR.getBeta();
	protected static final double MUE = ApplicationSettingFacade.SINR.getMue();
	protected static final float POWER = ApplicationSettingFacade.SINR.getPower();
	protected List<Channel> availableChannels = ApplicationSettingFacade.Channel.getChannel();
	protected static final IFactorMap iFactor = ApplicationSettingFacade.IFactor.getIFactorMap();
	protected ChannelOccuranceMap ChannelOccurance = new ChannelOccuranceMap();

	public ChannelOccuranceMap getChannelOccurance() {
		return ChannelOccurance;
	}

	private void InitiateVriables()
	{
		if (nodesDistances == null) 
			nodesDistances = TopologyFacade.getDistanceForEachNode();
			
	}

	private LinksAmbienNoiseMap calcLinksAmbienNoise(LinkTrafficMap linksTraffics)
	{
		LinksAmbienNoiseMap result = new LinksAmbienNoiseMap();
		
		for (Entry<Link, Double> tLink : linksTraffics.entrySet()) 
		{
			double distance = getDistance(tLink.getKey());
			// beta / (1- ((beta*Mue* d^a)/P))
			double noise = BETA	/ (1 - ((BETA * MUE * Math.pow(distance, ALPHA)) / POWER));
			result.put(tLink.getKey(), noise);
		}
		FileGenerator.LinksAmbienNoiseInFile(result);
		return result;
	}

	protected double getCrossDistance(Link l1, Link l2) {
		if (l1.getSource().equals(l2.getDestination())
				|| l1.getDestination().equals(l2.getSource())
				|| l1.getSource().equals(l2.getSource())
				|| l1.getDestination().equals(l2.getDestination()))
			return 0.000001;
		return nodesDistances.get(l1.getSource()).get(l2.getDestination());
	}

	protected double getDistance(Link l) {
		return nodesDistances.get(l.getSource()).get(l.getDestination());
	}

	
	public LinksChannelMap assigningDynamic(int startTime, int stopTime, BufferMap sourceBuffer, BufferMap transmitBuffer)
	{
		List<Link> linkSet = new ArrayList<>();
		linksChannel = new LinksChannelMap();
		LinkTrafficMap periodicTraffic = new LinkTrafficMap(TrafficEstimatingFacade.getLinksTraffic( startTime, stopTime, sourceBuffer, transmitBuffer).Sort());
		InitiateVriables();
		linksAmbientNoise = this.calcLinksAmbienNoise(periodicTraffic);
		
		for (Entry<Link, Double> tLink : periodicTraffic.entrySet())
		{
			Link currentLink = tLink.getKey();
			if (linkSet.size() == 0) 
			{
				linkSet.add(currentLink);
				Channel channel = availableChannels.get(0);
				linksChannel.put(currentLink, channel);
				ChannelOccurance.Increase(channel);
			} 
			else
			{
				Channel channel = calc(linkSet, currentLink);
				ChannelOccurance.Increase(channel);
				linksChannel.put(currentLink, channel);
				linkSet.add(currentLink);
			}
		}

		//IntermediateOutput.ChannelAssignment(linksChannel);
		FileGenerator.ChannelsInFile(linksChannel,startTime);
		//FileGenerator.ChannelOccurance(ChannelOccurance);
		return linksChannel;
	}

	// performing the mathematical
	protected Channel calc(List<Link> linkSet, Link currentLink) {

		double distance = getDistance(currentLink);
		Map<Channel, Double> channelAffectSet = new TreeMap<Channel, Double>();
		double affect = 0;
		for (Channel chan : availableChannels) 
		{
			affect = 0;
			int currentChannel = chan.getChannel();
			// ∑ RI_l(l')
			for (Link l : linkSet)
			{

				int chn_sep = Math.abs(currentChannel - linksChannel.get(l).getChannel());
				double overlap = iFactor.get(chn_sep);
				double cross_distance = getCrossDistance(l, currentLink);

				// I_f(c_l,c_l′) d^a_uv / d^a_uv
				double e = (overlap * (Math.pow(distance, ALPHA) / Math.pow(cross_distance, ALPHA)));
				affect += e;
			}
			affect *= linksAmbientNoise.get(currentLink); // a_s(l_v) = C_v
															// ∑RI_l (l′)
			channelAffectSet.put(chan, affect);
		}
		Channel actualChannel = getActualChannel(channelAffectSet);
		return actualChannel;
	}

	private Channel getActualChannel(Map<Channel, Double> channelAffectSet)
	{
		Channel min = getMinAffectance(channelAffectSet);
		return min;
	}
	private Channel getMinAffectance(Map<Channel, Double> channelAffectSet)
	{
		
		double affectanceValue = Collections.max(channelAffectSet.values());
		Channel min = null;
		
	
		for (Entry<Channel, Double> channelAff : channelAffectSet.entrySet())
		{
			if(channelAff.getValue() >= 0 && affectanceValue > channelAff.getValue())
			{
				affectanceValue  = channelAff.getValue();
				min = channelAff.getKey();
			}
		}
		if(min == null)
		{
			Channel[] chA = new Channel[channelAffectSet.size()];
			chA = channelAffectSet.keySet().toArray(chA);
			min = chA[0];
		}
		return min;
	}

}
