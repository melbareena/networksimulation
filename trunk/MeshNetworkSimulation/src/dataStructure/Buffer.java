package dataStructure;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Buffer
{

	private List<Packet> packets;
	
	public Buffer()
	{
		this.packets = new ArrayList<>();
	}

	public List<Packet> getPackets()
	{
		return packets;
	}
	public Packet getPacket()
	{
		if(packets.size() > 0)
			return packets.get(0);
		return null;
	}
	public void add (Packet newP)
	{
		Packet mergedPacket = null;
		
		for (Packet p : packets)
		{
			if(p.equals(newP))
				mergedPacket = p;
		}
		
		if(mergedPacket != null)
			mergedPacket.addTarffic(newP.getTraffic());
		else
			this.packets.add(newP);
		
		Collections.sort(packets, Collections.reverseOrder());
			
	}
	
	public double size()
	{
		
		double size = 0;
		for (Packet p : packets)
		{
			size += p.getTraffic();
		}
		return size;
	}
	
	public Packet getMax()
	{
		if(packets.size() > 0)
			return packets.get(0);	
		return null;
	}


	public Packet send(int dataRate, int currentTimeSlot)
	{
		
		Packet max = this.getMax();
		if(max != null)
		{
			if(max.getTraffic() < dataRate)
				packets.remove(max);
			return max.send(dataRate, currentTimeSlot);
		}
		return null;
	}
	
	@Override
	public String toString() 
	{
		return "Packet size:" + packets.size() + ", Traffic:" + this.size();
	}


}
