package scheduling;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import setting.ApplicationSettingFacade;
import dataStructure.SchedulingResult;

public class SchedulingFacade
{
	public static SchedulingResult getScheduling(int instanceIndex)
	{
		
		String className = "";
		try
		{
			className = ApplicationSettingFacade.Scheduling.getSterategyClassName();
			Class<?> myClass = Class.forName("scheduling." + className);
			Constructor<?> myConstructor = myClass.getConstructor(int.class);
			
			String methodName = "";
			
			if(ApplicationSettingFacade.Traffic.isDynamicType())
				methodName = "dynamicScheduling";
			else
				methodName = "staticScheduling";
					
			
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
