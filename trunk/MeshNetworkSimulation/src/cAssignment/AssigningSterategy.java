package cAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import common.FileGenerator;
import common.IntermediateOutput;

import setting.ApplicationSettingFacade;
import topology.TopologyFacade;
import trafficEstimating.TrafficEstimatingFacade;
import dataStructure.Channel;
import dataStructure.ChannelOccuranceMap;
import dataStructure.IFactorMap;
import dataStructure.Link;
import dataStructure.LinkTrafficMap;
import dataStructure.LinksAmbienNoiseMap;
import dataStructure.LinksChannelMap;
import dataStructure.Vertex;

/**
 * 
 * @author Mahdi Negahi
 * 
 */
public class AssigningSterategy {
	protected static LinksChannelMap linksChannel;
	protected static LinkTrafficMap linksTraffics;
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

	protected void InitiateVriables() 
	{
		if (nodesDistances == null && linksChannel == null) {
			nodesDistances = TopologyFacade.getDistanceForEachNode();
			linksChannel = new LinksChannelMap();

			linksTraffics = new LinkTrafficMap(TrafficEstimatingFacade.getLinksTraffic().Sort());
			for (Entry<Link, Double> tLink : linksTraffics.entrySet())
				linksChannel.put(tLink.getKey(), new Channel(0));

			linksAmbientNoise = calcLinksAmbienNoise();
		}
	}

	protected LinksAmbienNoiseMap calcLinksAmbienNoise()
	{
		
		if(linksAmbientNoise != null) return linksAmbientNoise;
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

	public LinksChannelMap assigning() {
		List<Link> linkSet = new ArrayList<>();

		for (Entry<Link, Double> tLink : linksTraffics.entrySet()) {
			Link currentLink = tLink.getKey();
			if (linkSet.size() == 0) {
				linkSet.add(currentLink);
				Channel channel = availableChannels.get(0);
				linksChannel.put(currentLink, channel);
				ChannelOccurance.Increase(channel);
			} else {
				Channel channel = calc(linkSet, currentLink);
				ChannelOccurance.Increase(channel);
				linksChannel.put(currentLink, channel);
				linkSet.add(currentLink);
			}
		}

		IntermediateOutput.ChannelAssignment(linksChannel);
		FileGenerator.ChannelsInFile(linksChannel);
		FileGenerator.ChannelOccurance(ChannelOccurance);
		return linksChannel;
	}

	// performing the mathematical
	protected Channel calc(List<Link> linkSet, Link currentLink) {

		double distance = getDistance(currentLink);
		Map<Channel, Double> channelAffectSet = new TreeMap<Channel, Double>();
		double affect = 0;
		for (Channel chan : availableChannels) {
			affect = 0;
			int currentChannel = chan.getChannel();
			// ∑ RI_l(l')
			for (Link l : linkSet) {

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

	protected Channel getActualChannel(Map<Channel, Double> channelAffectSet) {
		return null;
	}
}
