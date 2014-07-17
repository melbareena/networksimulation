package common;

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import dataStructure.Vertex;


public class PrintConsole
{
	
	public static void print(String str)
	{
		System.out.println(str);
	}
	public static void printErr(String str)
	{
		System.err.println(str);
	}
	public static void PrintShortestPath(Map<Integer, LinkedList<Vertex>> shortestPaths)
	{
		PrintConsole.print("\nprinting shortest path for each routers");
		int routerId;
		String out = "";
		for(Entry<Integer,LinkedList<Vertex>> shortPath : shortestPaths.entrySet())
		{
			out = "";
			routerId = shortPath.getKey();
			out = routerId  + ": ";
			for(Vertex node : shortPath.getValue())
			{
				out += node.getId() + ">";
			}
			out = (String) out.subSequence(0, out.length() -1 ); // remove last ">"
			PrintConsole.print(out);
		}
		
		
	}
}
