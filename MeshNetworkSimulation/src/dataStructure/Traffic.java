package dataStructure;

public class Traffic implements Cloneable {
	
	private UplinkTraffic uplinkTraffic;
	
	private DownlinkTraffic downlinkTraffic;

	public Traffic(UplinkTraffic uplinkTraffic, DownlinkTraffic downlinkTraffic) {
		this.uplinkTraffic = uplinkTraffic;
		this.downlinkTraffic = downlinkTraffic;
	}

	public UplinkTraffic getUplinkTraffic() {
		return uplinkTraffic;
	}

	public void setUplinkTraffic(UplinkTraffic uplinkTraffic) {
		this.uplinkTraffic = uplinkTraffic;
	}

	public DownlinkTraffic getDownlinkTraffic() {
		return downlinkTraffic;
	}

	public void setDownlinkTraffic(DownlinkTraffic downlinkTraffic) {
		this.downlinkTraffic = downlinkTraffic;
	}
	
	public double size()
	{
		return (double)Math.round((uplinkTraffic.size() + downlinkTraffic.size()) * 100000) / 100000;	
	}
	
	public String toString()
	{
		String str =   "Total Size: " + size()  + "\n";
		str += uplinkTraffic + "\n";
		str += downlinkTraffic +"\n";
		return str;
	}
	
	public Traffic clone()
	{
		Traffic temp = new Traffic(this.getUplinkTraffic().clone(), this.getDownlinkTraffic().clone());
		return temp;
	}

}
