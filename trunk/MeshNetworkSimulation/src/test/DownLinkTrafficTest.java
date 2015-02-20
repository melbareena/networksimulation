package test;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import dataStructure.DownlinkTraffic;
import dataStructure.Vertex;

public class DownLinkTrafficTest
{

	DownlinkTraffic traffic;
	
	@Before
	public void setUp() throws Exception
	{
		traffic = new DownlinkTraffic();
	}


	

	@Test
	public void test()
	{
		
		Vertex source = new Vertex(1, new Point(10, 20));
		Vertex destination = new Vertex(2, new Point(10, 20));
		TreeMap<Vertex, Double> trafficTo = new TreeMap<Vertex, Double>();
		trafficTo.put(destination, 30d);
		traffic.add(source , trafficTo);
		
		traffic.add(source, destination, 50);
		
		traffic.add(source, new Vertex(3, new Point(55, 60)), 20);
		
		
		traffic.appendTraffic(new Vertex(50, new Point(300, 40)), destination, 20);
		double result = 120;
		double amountOfTraffic = traffic.size();
		
		
		
		assertEquals((Object)result,(Object) amountOfTraffic);
		
	}

}
