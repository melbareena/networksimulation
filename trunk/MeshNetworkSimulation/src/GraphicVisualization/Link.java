package GraphicVisualization;
import com.mxgraph.model.mxCell;

/**
 * @author Benjamin
 *
 */
public class Link {
	
	/** */
	public static enum Type {DOWNLINK, UPLINK};
	
	/** */
	private mxCell edge;
	
	/** */
	private int channel;
	
	/** */
	private Type type;

	/**
	 * @param edge
	 * @param type
	 * @param channel
	 */
	public Link(mxCell edge, Type type, int channel) {
		this.edge = edge;
		this.channel = channel;
		this.type = type;
	}

	/**
	 * @return
	 */
	public mxCell getEdge() {
		return edge;
	}

	/**
	 * @return
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * @param channel
	 */
	public void setChannel(int channel) {
		this.channel = channel;
	}

	/**
	 * @return
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * @return
	 */
	public boolean isDownlink() {
		return (this.type == Type.DOWNLINK);
	}
	
	/**
	 * @return
	 */
	public boolean isUplink() {
		return (this.type == Type.UPLINK);
	}

}
