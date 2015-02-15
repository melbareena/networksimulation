package dataStructure;

public class DataRate implements Comparable<DataRate>
{
	public double getRate()
	{
		return Rate;
	}



	public float getSINR()
	{
		return SINR;
	}



	private double Rate;
	private float SINR;
	
	
	
	public DataRate(double rate, float sinr)
	{
		this.Rate = rate;
		this.SINR = sinr;
	}
	public DataRate(DataRate r)
	{
		this.Rate = r.getRate();
		this.SINR = r.getSINR();
	}
	
	
	@Override
	public String toString() 
	{
		return "SINR: "+ this.SINR + "\t Data Rate:  " + this.Rate;
	}



	@Override
	public int compareTo(DataRate d)
	{
		return Float.compare(this.SINR, d.getSINR());
	}
}
