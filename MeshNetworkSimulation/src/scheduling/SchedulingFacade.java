package scheduling;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import setting.ApplicationSettingFacade;
import setting.BaseConfiguration.AlgorithmMode;
import dataStructure.SchedulingResult;

public class SchedulingFacade
{
	public static SchedulingResult getScheduling(int instanceIndex)
	{
		
		String postFix = "";
		String className = "";
		try
		{
			
			postFix = ApplicationSettingFacade.Scheduling.getSterategyClassName();
			if(ApplicationSettingFacade.getAlgorithmMode() == AlgorithmMode.Dynamic)
				className = "Dynamic" + postFix;
			else
				className = postFix;
			
			Class<?> myClass = Class.forName("scheduling." + className);
			Constructor<?> myConstructor = myClass.getConstructor(int.class);
			
			String methodName = "";
			
			
			if(ApplicationSettingFacade.getAlgorithmMode() == AlgorithmMode.Static)
			{
				if(ApplicationSettingFacade.Traffic.isDynamicType())
					methodName = "dynamicScheduling";
				else
					methodName = "staticScheduling";
			}
			else
				methodName = "doDeliveryPackets";
					
			
			Method m = myClass.getMethod(methodName);
			
		    Object o =	m.invoke(myConstructor.newInstance(instanceIndex));
		    
		    return (SchedulingResult) o;
			
			
			//Method m2 = myClass.getMethod("getResults");
			//return (SchedulingResult) m2.invoke(myClass.newInstance());
			
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			System.err.println("Please check the name of class in XML file. \" " + className + " \" not found. ");
			System.exit(0);
		} catch (InstantiationException e)
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		} catch (SecurityException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	
}
