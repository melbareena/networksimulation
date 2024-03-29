package transConf;

import java.util.List;
import java.util.Map;

import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;
import org.jblas.Solve;
import setting.ApplicationSettingFacade;
import transConf.TCBasic.DeleteAction;
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
	
	
	protected DoubleMatrix getD(TCUnit unit)
	{
		List<Link> links = unit.getLinks();
		double[][] arr_D = new double[unit.size()][unit.size()];
		for(int i = 0 ; i < unit.size() ; i++)
			for(int j=0; j < unit.size() ; j++)
			{
				Link ell_i = links.get(i);
				if(i==j)	
					arr_D[i][j] = unit.getSinrThreshold(ell_i); // get gamma
			}
		DoubleMatrix D = new DoubleMatrix(arr_D);
		return D;
	}
	protected DoubleMatrix getG(TCUnit unit)
	{
		List<Link> links = unit.getLinks();
		
		double[][] arr_G = new double[unit.size()][unit.size()];
		for(int i = 0 ; i < unit.size() ; i++)
			for(int j=0; j < unit.size() ; j++)
			{
				Link ell_i = links.get(i);
				Link ell_j = links.get(j);
				if(i!=j)	
				{
					double d =  (Math.pow(links.get(j).getCrossDistance(links.get(i)),-ApplicationSettingFacade.SINR.getAlpha()) /
							Math.pow(ell_i.getDistance(), -ApplicationSettingFacade.SINR.getAlpha()));
					double IfactorValue = _performer._sinr.getIFactorValue(ell_i, ell_j);
					d = d * IfactorValue;
					double rounded = (double) Math.round(d * 10000000) / 10000000;
					arr_G[i][j] = rounded;
				}
			}
		
		return new DoubleMatrix(arr_G); 
		
	}
	TCUnit powerControl(TCUnit unit)
	{
		
		//unit = calcDataRate(unit);
		boolean powerAllocationIsOk = true;
		List<Link> links = unit.getLinks();
		DoubleMatrix A = new DoubleMatrix(unit.size(), unit.size());
		DoubleMatrix D = getD(unit);
		DoubleMatrix G = getG(unit);		
		
		D.mmuli(G, A );	
		double perron_eigenvalue = getEigenValue(unit, A);
		if(perron_eigenvalue < 1)
		{
			
			double[] powers = getPowerValues(unit, links, A, D);
			
			if(isPowerFeasible(powers))
			{
				int i = 0;
				for (double e : powers)
				{
					double power_watTOmWat = e * 1000;
					if(power_watTOmWat < ApplicationSettingFacade.SINR.getPower())
						unit.setPower(links.get(i), power_watTOmWat) ;		// the power is ok	
					i++;
				}
			}
			else
			{
				unit.setNeedAdjusmentpower(true);
				powerAllocationIsOk = false;
			}
			if(powerAllocationIsOk)
			{
				//calculate data rate with the powers------------------------------------------
				Map<Link, Double> newRates = _performer._sinr.calcDataRate(unit,unit.getPower());
				unit.setRates(newRates);
				
				
				unit.setLocked();
				return unit;
			}
			
		  }
		  // there is no feasible solution  
		  else
		  {
			  System.err.println("Dead TC............................");
	  		  unit.setDead(true);
		  }
		  
		  if(unit.needAdjusmentPower() && !unit.isDead())		  
			  unit = adjustmentPower(unit); 
		
		  if(unit.isDead())
			  unit = reStructTC(unit);
		  
		  
		  return unit;
	}

	/**
	 * @param unit
	 * @param links
	 * @param A
	 * @param D
	 * @return
	 */
	protected double[] getPowerValues(TCUnit unit, List<Link> links,
			DoubleMatrix A, DoubleMatrix D) 
	{
		double[][] h = new double[unit.size()][unit.size()];
		for (int i=0;i<unit.size();i++)
			for (int j=0;j<unit.size();j++) 
				h[i][j]= i==j ? 1-A.get(i,j) : -1*A.get(i,j);
					  
		DoubleMatrix H = new DoubleMatrix(h); //(I-A)	
		DoubleMatrix q = new DoubleMatrix(unit.size());
		for (int i=0;i<unit.size();i++) 
			  q.put(i,0,  ApplicationSettingFacade.SINR.getMue() /
				  Math.pow(links.get(i).getDistance(),-ApplicationSettingFacade.SINR.getAlpha()));
		DoubleMatrix b = D.mmul(q);  
		DoubleMatrix H_inv=Solve.solve(H,DoubleMatrix.eye(unit.size()));
		DoubleMatrix P = H_inv.mmul(b);
		double[] power = P.toArray();
		return power;
	}

	/**
	 * @param unit
	 * @param A
	 * @return
	 */
	protected double getEigenValue(TCUnit unit, DoubleMatrix A) {
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
		return perron_eigenvalue;
	}

   

	TCUnit adjustmentPower(TCUnit unit)
	{
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
				Link l = unit.getLinkRandomly();
				DeleteAction act = _performer.removeFromConsiderList(l);
				int safty = 20;
				int counter  = 0;
				while (act == DeleteAction.Impossible && counter < safty)
				{
					l = unit.getLinkRandomly();
					act = _performer.removeFromConsiderList(l);
					counter++;
				}
				if(counter > safty )
				{
					System.err.println("can not reach to feasible soultion for a transmission configuration:" + unit.toString());
					System.err.println("Exception in PowerControl Class Line 183");
					System.exit(0);
				}
				if(act != DeleteAction.Impossible)
					unit.removeLink(l);
				System.err.println("Action: " + act.toString());
				unit.setNeedAdjusmentpower(false);	
				unit.setDead(true);
				unit = _performer._sinr.calcDataRate(unit);
				return powerControl(unit);
			}
		}
		return unit;
		
	}
	DataRate preDataRate(double curValue)
	{
		if(curValue == ApplicationSettingFacade.DataRate.getMin()) return null;
		
		List<DataRate> rates = ApplicationSettingFacade.DataRate.getDataRate();
		for(int i =rates.size() -1  ; i >= 0 ; i--)
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
