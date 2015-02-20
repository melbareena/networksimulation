package sinr;

import java.util.List;
import java.util.Map;


import dataStructure.DataRate;
import dataStructure.Link;
import dataStructure.TCUnit;

public abstract class SINRAbstract
{
	public abstract double calc(Link l , List<Link> L );
	public abstract double calc(Link l , List<Link> L, double power );	
	abstract double getCrossDistance(Link l, Link currentLink);	
	abstract double I_l_lprime(Link l , List<Link> L);	
	abstract double I_l_lprime(Link l , List<Link> L, double power);	
	public abstract  double getIFactorValue(Link l_i, Link l_j);
	public abstract DataRate calcDataRate(double sinr);
	public abstract TCUnit calcDataRate(TCUnit tConfUnit);
	public abstract Map<Link, Double> calcDataRate(TCUnit tConfUnit, Map<Link, Double> powerCollection);

}
