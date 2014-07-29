package scheduling;

import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import dataStructure.Buffer;
import dataStructure.BufferMap;
import dataStructure.Link;

public class NormalSchedulingStrategy extends SchedulingStrategy
{
	

	@Override
	protected Vector<Link> getBufferStrategy(boolean isSourceBuffer)
	{

		BufferMap targetBuffer;
		
		if(isSourceBuffer)
			targetBuffer = super.sourceBuffers;
		else
			targetBuffer = super.transmitBuffers;
		
		
		Vector<Link> selectedLinks = new Vector<>();
		
		
		TreeMap<Link, Buffer> sortedBuffer = targetBuffer.sort();
		int index = 0;
		for (Entry<Link, Buffer> lb : sortedBuffer.entrySet())
		{
			index++;
			
			
			selectedLinks.add(lb.getKey() );
			
			if(super.k == index)
				break;

		}
		return selectedLinks;
	}

	@Override
	protected String getName() {
		return "Normal Scheduling Strategy";
	}

}
