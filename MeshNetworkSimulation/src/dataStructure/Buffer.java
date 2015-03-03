package dataStructure;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Buffer {

	private List<Packet> packets;
	
	public Buffer() {
		this.packets = new ArrayList<>();
	}

	public List<Packet> getPackets() {
		return packets;
	}
	
	public Packet getFirstPacket() {
		if(packets.size() > 0)
			return packets.get(0);
		return null;
	}
	
	public void add(Packet newP) 
	{
		Packet mergedPacket = null;
		for (Packet p : packets) 
			if(p.equals(newP))
				mergedPacket = p;
		if(mergedPacket != null) 
			mergedPacket.addTarffic(newP.getTraffic());
		 else 
			this.packets.add(newP);
		
	Collections.sort(packets, Collections.reverseOrder());
	}
	
	
	/**
	 *  
	 * @return: available traffic in the current buffer
	 */
	public double size()
	{
		
		double size = 0;
		for (Packet p : packets)
		{
			size += p.getTraffic();
		}
		return size;
	}
	
	public Packet getMax() {
		if(packets.size() > 0)
			return packets.get(0);	
		return null;
	}
	
	
	private List<Packet> getPackets(double dataRate)
	{
		List<Packet> candidatePackets = new ArrayList<Packet>();
		
		double remainedDataRate = dataRate;
		for (Packet p : packets)
		{
			remainedDataRate -= p.getTraffic();
			candidatePackets.add(p);
			if(remainedDataRate <= 0)
				break;
		}
		return candidatePackets;
		
		
	}

	public List<Packet> send(double dataRate, int currentTimeSlot)
	{
		List<Packet> candidatePackets  = getPackets(dataRate);
		assert(candidatePackets.size() > 0) : "No packets, class: Buffer, Method: send";
		List<Packet> movedPackets = new ArrayList<Packet>();
		double remainedDataRate = dataRate;
		
		for (Packet p : candidatePackets) 
		{
			if(p.getTraffic() <= remainedDataRate)
				packets.remove(p);
			
			movedPackets.add(p.send(remainedDataRate, currentTimeSlot));
			remainedDataRate -= p.getTraffic();
		}
		return movedPackets;
		/*Packet max = this.getMax();
		if(max != null)
		{
			if(max.getTraffic() < dataRate)
				packets.remove(max);
			return max.send(dataRate, currentTimeSlot);
		}
		return null;
		*/
	}
	
	@Override
	public String toString() 
	{
		return "Packet size:" + packets.size() + ", Traffic:" + this.size();
	}
	
	/**Return a map containing the list of packet for a given destination.
	 * @return A map containing the list of packet for a given destination.
	 */
	public Map<Vertex, List<Packet>> getPacketDestinationMap() {
		Map<Vertex, List<Packet>> result = new HashMap<Vertex, List<Packet>>();
		for(Packet p : packets) {
			if(!result.containsKey(p.getDestination())) {
				List<Packet> list = new ArrayList<Packet>();
				result.put(p.getDestination(), list);
			}
			result.get(p.getDestination()).add(p);
		}
		return result;
	}

	/**Return the amount of traffic in this buffer toward the given destination.
	 * @param destination The destination node to look for in the buffer.
	 * @return The amount of traffic.
	 */
	public double getTrafficTowardDestination(Vertex destination) {
		double totalTraffic = 0.0;
		Map<Vertex, List<Packet>> map = getPacketDestinationMap();
		if(!map.containsKey(destination)) { 
			return 0.0;
		} else {
			for(Packet p : map.get(destination)) {
				totalTraffic += p.getTraffic();
			}
		}
		return totalTraffic;
	}

}
