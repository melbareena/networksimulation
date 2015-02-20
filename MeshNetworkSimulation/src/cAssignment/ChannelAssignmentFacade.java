package cAssignment;

import common.PrintConsole;
import luncher.Luncher;
import dataStructure.BufferMap;
import dataStructure.ChannelOccuranceMap;
import dataStructure.LinksChannelMap;

public class ChannelAssignmentFacade
{	
	
	private static LinksChannelMap linksChannel;
	private static ChannelOccuranceMap channelOccurance;
	
	private static int multiExecIndex = 0;
	
	
	public static ChannelOccuranceMap getChannelOccurance()
	{
		if(linksChannel == null || multiExecIndex != Luncher.multiExecIndex)
			getChannels();
		return channelOccurance;
		
	}
	
	
	
	private static int _startTime = -1;
	public static LinksChannelMap getChannels(int startTime, int stopTime, BufferMap sourceBuffer, BufferMap transmitBuffer)
	{
		if(_startTime != startTime)
		{
			//multiExecIndex = Luncher.multiExecIndex;
			DynamicChannelAssignmentStartegy strategy = new DynamicChannelAssignmentStartegy();
			linksChannel = strategy.assigningDynamic( startTime, stopTime, sourceBuffer, transmitBuffer);
			channelOccurance = strategy.getChannelOccurance();	
			_startTime = startTime;
		}
		return linksChannel;
	}
	
	
	public static LinksChannelMap getChannels()
	{
		if(linksChannel == null || multiExecIndex != Luncher.multiExecIndex)
		{
			PrintConsole.print("Channel Assignment Start.......");
			multiExecIndex = Luncher.multiExecIndex;
			OriginalSterategy strategy = new OriginalSterategy();
			linksChannel = strategy.assigning();
			channelOccurance = strategy.getChannelOccurance();
			
			
			
			
			
			// if another channels strategies available please use reflection for more flexibility
			/*String className = "";
			try
			{
				className = ApplicationSettingFacade.ChannelAssignment.getSterategyClassName();
				Class<?> myClass = Class.forName("cAssignment." + className);
				Method m = myClass.getMethod("assigning");
				Object o = m.invoke(myClass.newInstance());	
				LinksChannelMap lcm = (LinksChannelMap) o;
				linksChannel = lcm;
				Method m2 = myClass.getMethod("getChannelOccurance");
				channelOccurance =(ChannelOccuranceMap) m2.invoke(myClass.newInstance());
				
			} catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e)
			{
				System.err.println("Please check the name of class in XML file. \" " + className + " \" not found. ");
				//e.printStackTrace();
			} catch (InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		return linksChannel;
	}
}
