package dataStructure;

public class Traffic {
	
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
	
	public int size()
	{
		return uplinkTraffic.size() + downlinkTraffic.size();
	}
	
	public String toString()
	{
		String str =   "Total Size: " + size()  + "\n";
		str += uplinkTraffic + "\n";
		str += downlinkTraffic +"\n";
		return str;
	}

}
