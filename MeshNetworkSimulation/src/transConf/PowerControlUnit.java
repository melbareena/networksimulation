package transConf;

import java.util.List;

import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;
import org.jblas.Solve;

import common.FileGenerator;

import setting.ApplicationSettingFacade;
import dataStructure.DataRate;
import dataStructure.Link;
import dataStructure.TCUnit;

/**
 * 
 * @author Mahdi
 * Power control unit
 */
class PowerControlUnit
{
	private TCBasic _performer;
	
	PowerControlUnit ( TCBasic performer)
	{
		_performer = performer;
	}
	
	TCUnit powerControl(TCUnit unit)
	{
		
		//unit = calcDataRate(unit);
		boolean powerAllocationIsOk = true;
		List<Link> links = unit.getLinks();
		
		double[][] arr_D = new double[unit.size()][unit.size()];
		double[][] arr_G = new double[unit.size()][unit.size()];
		for(int i = 0 ; i < unit.size() ; i++)
			for(int j=0; j < unit.size() ; j++)
			{
				Link ell_i = links.get(i);
				Link ell_j = links.get(j);
				if(i==j)	
					arr_D[i][j] = unit.getSinr(ell_i); // get gamma
				else
				{
					double d =  (Math.pow(links.get(j).getCrossDistance(links.get(i)),-ApplicationSettingFacade.SINR.getAlpha()) /
							Math.pow(links.get(i).getDistance(), -ApplicationSettingFacade.SINR.getAlpha()));
					double IfactorValue = SINR.getIFactorValue(ell_i, ell_j);
					d = d * IfactorValue;
					double rounded = (double) Math.round(d * 10000) / 10000;
					arr_G[i][j] = rounded;
				}
			}
		
		
		DoubleMatrix A = new DoubleMatrix(unit.size(), unit.size());
		DoubleMatrix D = new DoubleMatrix(arr_D);
		DoubleMatrix G = new DoubleMatrix(arr_G);		
		
		D.mmuli(G,A);	
		ComplexDoubleMatrix cdm = Eigen.eigenvalues(A);
		ComplexDouble[] cd = new ComplexDouble[unit.size()];
		for (int i=0;i<unit.size();i++) cd[i]=cdm.get(i);
		double perron_eigenvalue=Double.MIN_VALUE;
		for (int i=0;i<unit.size();i++) 
		{
			if (cd[i].isReal() && cd[i].real()>perron_eigenvalue) 
				perron_eigenvalue=cd[i].real();
		}
		// there is a feasible solution  	  
		if(perron_eigenvalue < 1)
		{
			
			double[][] h = new double[unit.size()][unit.size()];
		  	for (int i=0;i<unit.size();i++)
		  		for (int j=0;j<unit.size();j++) 
		  			h[i][j]= i==j ? 1-A.get(i,j) : -1*A.get(i,j);
						  
		  	DoubleMatrix H = new DoubleMatrix(h); //(I-A)	
			DoubleMatrix b = new DoubleMatrix(unit.size());
			for (int i=0;i<unit.size();i++) 
				  b.put(i,0, ApplicationSettingFacade.SINR.getMue() /
					  Math.pow(links.get(i).getDistance(),-ApplicationSettingFacade.SINR.getAlpha()));
				  
			DoubleMatrix H_inv=Solve.solve(H,DoubleMatrix.eye(unit.size()));
			DoubleMatrix P = H_inv.mmul(b);
			double[] power = P.toArray();
			
			int i = 0;

			for (double e : power)
			{
				double power_watTOmWat = e * 1000;
				if(power_watTOmWat < ApplicationSettingFacade.SINR.getPower())
					unit.setPower(links.get(i), power_watTOmWat) ;		// the power is ok	
				else
				{
					unit.setPower(links.get(i), power_watTOmWat) ;
					unit.setNeedAdjusmentpower(true);
					powerAllocationIsOk = false;
				}
				i++;
			}
			
			if(powerAllocationIsOk)
			{
				FileGenerator.needToAdjust(counter, unit);
				unit.setNeedAdjusmentpower(false);
				unit.setDead(false);
				unit.setLock(true);
				return unit;
			}
			
		  }
		  // there is no feasible solution  
		  else
		  {
	  		  unit.setDead(true);
	  		  FileGenerator.deadTC(unit);
		  }
		  
		  if(unit.needAdjusmentPower() && !unit.isDead())		  
			  unit = adjustmentPower(unit); 
		
		  if(unit.isDead())
			  unit = reStructTC(unit);
		  
		  
		  return unit;
	}

	private static int counter = 0;
    TCUnit adjustmentPower(TCUnit unit)
	{
    	counter++;
		if(unit.needAdjusmentPower())
		{
			
			
			double maxRate = Double.MIN_VALUE;
			Link maxLink = null;
			for (Link ell_i : unit.getLinks())
			{
				if(maxRate < unit.getRate(ell_i))
				{	
					maxRate = unit.getRate(ell_i);
					maxLink = ell_i;
				}
			}
			
			DataRate previousDataRate = preDataRate(maxRate);
			
			if(previousDataRate != null)
			{
				unit.setSinrRate(maxLink, previousDataRate.getRate(), previousDataRate.getSINR());
				return powerControl(unit);
			}
			else
			{
				
				System.err.println("remove linked");
				_performer.removeFromConsiderList(unit.removeLinkRandomly());
				unit.setNeedAdjusmentpower(false);	
				unit.setDead(true);
				unit = _performer.calcDataRate(unit);
				return unit;
			}
		}
		return unit;
		
	}
	DataRate preDataRate(double curValue)
	{
		if(curValue == ApplicationSettingFacade.DataRate.getMin()) return null;
		
		List<DataRate> rates = ApplicationSettingFacade.DataRate.getDataRate();
		for(int i =rates.size() -1  ; i > 0 ; i--)
		{
			if(rates.get(i).getRate() < curValue)
				return rates.get(i);
		}
		
		return null;
	}

	TCUnit reStructTC(TCUnit unit)
	{
		//if(!unit.needAdjusmentPower())
			//unit = calcDataRate(unit);
		unit.setNeedAdjusmentpower(true);
		return adjustmentPower(unit);
	}
}
