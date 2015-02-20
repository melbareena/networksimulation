package transConf;

import java.util.List;

import setting.ApplicationSettingFacade;
import setting.BaseConfiguration.TCStrategy;
import common.PrintConsole;
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
			PrintConsole.print("Tranmission Configuration Start.......");
			multiExecIndex = Luncher.multiExecIndex;
			
			
			if(ApplicationSettingFacade.TranmissionConfiguration.getStertegy() == TCStrategy.PatternBased) 
			{
				int downOverUpRatio = ApplicationSettingFacade.TranmissionConfiguration.getDownOverUpRatio();
				PatternBasedTC pTC = new PatternBasedTC();
				configurations = pTC.patternBasedConfiguration(downOverUpRatio);
			}
			else 
			{
				GreedyTC gtc = new GreedyTC();
				configurations = gtc.originalConfiguring();
			}

		}
		return configurations;
	}
	
	
	private static int _startTime = -1;
	public static List<TCUnit> getConfigurations(int startTime, int stopTime, BufferMap sourceBuffer, BufferMap transmitBuffer)
	{
		if(startTime != _startTime)
		{

			DynamicGreedyBased gBased = new DynamicGreedyBased();
			configurations = gBased.createConfigurations(startTime, stopTime, sourceBuffer, transmitBuffer);
			_startTime = startTime;
		}
		return configurations;
	}
	
	
}
