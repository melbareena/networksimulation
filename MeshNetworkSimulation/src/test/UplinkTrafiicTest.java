package test;

import static org.junit.Assert.*;

import java.awt.Point;



import org.junit.Before;
import org.junit.Test;


import dataStructure.UplinkTraffic;
import dataStructure.Vertex;

public class UplinkTrafiicTest
{

	UplinkTraffic traffic;
	
	@Before
	public void setUp() throws Exception
	{
		traffic = new UplinkTraffic();
	}


	

	@Test
	public void test()
	{
		
		Vertex x1 = new Vertex(1, new Point(10, 20));
		Vertex x2 = new Vertex(2, new Point(10, 20));

		traffic.add(x1, 10);
		traffic.add(x2, 20);
		traffic.appendTraffic(x1, 25);
		
		double result = 35;
		
		
		//double amountOfTraffic = traffic.size();
		double amountOfTraffic = traffic.getUplinkTraffic(x1);
		
		
		assertEquals((Object)result,(Object) amountOfTraffic);
		
	}

}
