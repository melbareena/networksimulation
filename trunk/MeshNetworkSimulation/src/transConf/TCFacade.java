package transConf;

import java.util.List;

import dataStructure.TCUnit;

public class TCFacade
{
	private static List<TCUnit> configurations;
	public static List<TCUnit> getConfigurations()
	{
		if(configurations == null)
		{
			TransmissionConfiguration	tc = new TransmissionConfiguration();
			configurations = tc.Configuring();
		}
		return configurations;
	}
}
