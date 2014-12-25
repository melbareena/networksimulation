package transConf;
import org.jblas.DoubleMatrix;

import common.FileGenerator;

import setting.ApplicationSettingFacade;
import dataStructure.DataRate;
import dataStructure.Link;
import dataStructure.TCUnit;

public class IncreaseDatarate extends PowerControlUnit
{

	IncreaseDatarate(TCBasic performer)
	{
		super(performer);
		
	}
	
	public TCUnit increaser(TCUnit unit)
	{
		
		
		TCUnit feasible = null;
		TCUnit T_prime = null;
		FileGenerator.Power(unit, 0);
		for (Link l : unit.getLinks())
		{
			int currentDataRate = unit.getRate(l);
			T_prime = unit.Clone();
			if(currentDataRate == ApplicationSettingFacade.DataRate.getMax()) continue;
				for (DataRate dataRateOBJ : ApplicationSettingFacade.DataRate.getDataRate())
				{
					if(feasible != null)
						T_prime = feasible.Clone();
					else
						T_prime = unit.Clone();
					
					
					if(dataRateOBJ.getRate() <= currentDataRate) continue;
					T_prime.setSinrRate(l, dataRateOBJ.getRate(), ApplicationSettingFacade.DataRate.getSINRthreshold( dataRateOBJ.getRate()));
					T_prime.removePower(l);
					DoubleMatrix A = new DoubleMatrix(T_prime.size(), T_prime.size());
					DoubleMatrix D = super.getD(T_prime);
					DoubleMatrix G = super.getG(T_prime);		
					
					D.mmuli(G, A );	
					double perron_eigenvalue = super.getEigenValue(T_prime, A);
					
					if(perron_eigenvalue < 1)
					{
						double[] powers = super.getPowerValues(T_prime, T_prime.getLinks(), A, D);
						if(isPowerFeasible(powers))
						{
							
							int index = 0;
							for (Link ll : T_prime.getLinks())
							{
								T_prime.setPower(ll, powers[index]*1000);
								index++;
							}
							
							feasible = T_prime.Clone();	
						}
					}
				}			
		}
		FileGenerator.Power(feasible, 1);
		return feasible;
	}

	private boolean isPowerFeasible(double[] powers)
	{
		for (double p : powers)
		{
			if((p*1000) >= ApplicationSettingFacade.SINR.getPower())
				return false;
		}
		return true;
	}

}
