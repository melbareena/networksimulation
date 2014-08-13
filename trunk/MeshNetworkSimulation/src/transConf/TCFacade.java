package transConf;

import java.util.List;

import common.PrintConsole;

import launcher.Program;
import dataStructure.TCUnit;

public class TCFacade
{
	public static boolean newAlgortihm;
	public static int downOverUpRatio;
	public static boolean alternateOrder;
	public static boolean repeatLinksToRespectRatio;
	public static boolean enlargeByGateways;
	
	
	private static int multiExecIndex = 0;
	private static List<TCUnit> configurations;
	
	public static List<TCUnit> getConfigurations()
	{
		if(configurations == null || multiExecIndex != Program.multiExecIndex)
		{
			PrintConsole.print("Tranmission Configuration Start.......");
			multiExecIndex = Program.multiExecIndex;
			TransmissionConfiguration	tc = new TransmissionConfiguration();
			if(newAlgortihm) 
				configurations = tc.ConfiguringBenjamin(downOverUpRatio, alternateOrder,
						repeatLinksToRespectRatio, enlargeByGateways);
			else 
				configurations = tc.Configuring();

		}
		return configurations;
	}
}
