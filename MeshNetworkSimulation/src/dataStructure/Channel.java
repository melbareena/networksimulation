package dataStructure;

public class Channel implements Comparable<Channel>
{
	private Integer channel;

	public int getChannel()
	{
		return channel;
	}
	
	public Channel(int channel)
	{
		this.channel = channel;
	}

	@Override
	public int compareTo(Channel o)
	{
		return Integer.compare(o.getChannel(), channel);
	}
	
	@Override
	public String toString() 
	{
		return " C["+ this.channel + "]";
	}
	@Override
	public int hashCode()
	{
		final int prime = 3571 ;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		return result;
	}
}
