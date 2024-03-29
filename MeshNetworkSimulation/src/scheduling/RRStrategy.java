package scheduling;

import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import dataStructure.Buffer;
import dataStructure.BufferMap;
import dataStructure.Link;

public class RRStrategy  extends SchedulingBase
{
	
	private int sourcePositionIndex;
	private int tranmissionPositionIndex;

	public RRStrategy(int instanceIndex) {
		super(instanceIndex);
		sourcePositionIndex = 0;
		tranmissionPositionIndex = 0;
	}

	@Override
	protected Vector<Link> getBufferStrategy(boolean isSourceBuffer) {
		if (sourcePositionIndex >= super.sourceBuffers.size()) {
			sourcePositionIndex = 0;
		}
		if (tranmissionPositionIndex >= super.transmitBuffers.size()) {
			tranmissionPositionIndex = 0;
		}

		BufferMap targetBuffer;

		int roundRobinIndex = 0;

		if (isSourceBuffer) {
			targetBuffer = super.sourceBuffers;
			roundRobinIndex = sourcePositionIndex;
		} else {
			targetBuffer = super.transmitBuffers;
			roundRobinIndex = tranmissionPositionIndex;
		}

		Vector<Link> selectedLinks = new Vector<>();

		TreeMap<Link, Buffer> sortedBuffer = targetBuffer.sortByTraffic();
		int index = 0;
		int inserted = 0;
		for (Entry<Link, Buffer> lb : sortedBuffer.entrySet()) {
			if (index == roundRobinIndex) {
				selectedLinks.add(lb.getKey());
				inserted++;
				roundRobinIndex++;
				if (isSourceBuffer) {
					sourcePositionIndex++;
				} else {
					tranmissionPositionIndex++;
				}
			}
			if (super.k == inserted) {
				break;
			}
			index++;
		}
		return selectedLinks;
	}

	
	@Override
	protected String getName() {
		return "RR";
	}
	
}
