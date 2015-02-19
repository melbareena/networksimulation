package transConf;

import java.util.List;

import setting.ApplicationSettingFacade;
import setting.BaseConfiguration.TCStrategy;
import common.PrintConsole;
import luncher.Luncher;
import dataStructure.TCUnit;

public class TCFacade
{
	public static int downOverUpRatio = ApplicationSettingFacade.TranmissionConfiguration.getDownOverUpRatio();
	public static boolean priotityToOrthogonal = ApplicationSettingFacade.TranmissionConfiguration.isPriotityToOrthogonal();
	public static boolean repeatLinksToRespectRatio = ApplicationSettingFacade.TranmissionConfiguration.isRepeatLinksToRespectRatio();
	public static boolean enlargeByGateways = ApplicationSettingFacade.TranmissionConfiguration.isEnlargeByGateways();
	
	
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
				PatternBasedTC pTC = new PatternBasedTC();
				configurations = pTC.patternBasedConfiguration(downOverUpRatio, priotityToOrthogonal,
						repeatLinksToRespectRatio, enlargeByGateways);
			}
			else 
			{
				GreedyTC gtc = new GreedyTC();
				configurations = gtc.originalConfiguring();
			}

		}
		return configurations;
	}
}
