package transConf;

import java.util.ArrayList;
import java.util.List;

import setting.ApplicationSettingFacade;
import setting.BaseConfiguration.TCStrategy;
import luncher.Luncher;
import dataStructure.BufferMap;
import dataStructure.TCUnit;

public class TCFacade
{
	
	
	
	private static int multiExecIndex = 0;
	private static List<TCUnit> configurations;
	
	public static List<TCUnit> getConfigurations()
	{
		if(configurations == null || multiExecIndex != Luncher.multiExecIndex)
		{
			multiExecIndex = Luncher.multiExecIndex;	
			if(ApplicationSettingFacade.TranmissionConfiguration.getStertegy() == TCStrategy.PatternBased) 
			{
				PatternBasedTC pTC = new PatternBasedTC();
				configurations = pTC.createConfigurations();
				System.out.println("Average of Transmission Configuration: " + getAverageCapacity() );
				
			}
			else 
			{
				GreedyTC gtc = new GreedyTC();
				configurations = gtc.createConfigurations();
				System.out.println("Average of Transmission Configuration: " + getAverageCapacity() );
			}

		}
		return configurations;
	}
	
	private static double getAverageCapacity()
	{
		double sum = 0d;
		for (TCUnit tcUnit : configurations)
		{
			sum += tcUnit.getTCAP();
		}
		return (double) sum / configurations.size();
	}

	
	public static ArrayList<List<TCUnit>> _all = new ArrayList<List<TCUnit>>();
	private static int _startTime = -1;
	public static List<TCUnit> getConfigurations(int startTime, int stopTime, BufferMap sourceBuffer, BufferMap transmitBuffer)
	{
		if(startTime != _startTime ||  multiExecIndex != Luncher.multiExecIndex)
		{
			if(ApplicationSettingFacade.TranmissionConfiguration.getStertegy() == TCStrategy.Greedy) 
			{
				DynamicGreedyBased gBased = new DynamicGreedyBased();
				configurations = gBased.createConfigurations(startTime, stopTime, sourceBuffer, transmitBuffer);
				_all.add(configurations);
			}
			else
			{
				DynamicPatternBased pBased = new DynamicPatternBased();
				configurations = pBased.createConfigurations(startTime, stopTime, sourceBuffer, transmitBuffer);
				_all.add(configurations);
			}
			
			
			multiExecIndex = Luncher.multiExecIndex;
			_startTime = startTime;
		}
		return configurations;
	}
	
	
}
