package dataStructure;

public class DeliverPacketResult
{
	public int numberOfOriginalPakcets;
	public double delayInTs;
	public  double throughputInTS;
	public double remainedRate;
	public DeliverPacketResult(int numberOfOriginalPakcets, double delayInTs, double throughputInTS, double remainedRate)
	{
		this.numberOfOriginalPakcets = numberOfOriginalPakcets;
		this.delayInTs = delayInTs;
		this.throughputInTS = throughputInTS;
		this.remainedRate = remainedRate;
	}

}
