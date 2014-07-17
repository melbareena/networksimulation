package cAssignment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import setting.ApplicationSettingFacade;
import dataStructure.ChannelOccuranceMap;
import dataStructure.LinksChannelMap;

public class ChannelAssignmentFacade
{	
	
	private static LinksChannelMap linksChannel;
	private static ChannelOccuranceMap channelOccurance;
	public static LinksChannelMap getChannels()
	{
		if(linksChannel == null)
		{
			getDynamicChannel();
		}
		return linksChannel;
	}
	public static ChannelOccuranceMap getChannelOccurance()
	{
		if(linksChannel == null)
		{
			getDynamicChannel();
		}
		return channelOccurance;
		
	}
	
	private static LinksChannelMap getDynamicChannel()
	{
		if(linksChannel == null)
		{
			String className = "";
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
			}
		}
		return linksChannel;
	}
}
