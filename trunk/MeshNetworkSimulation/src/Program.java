
import GraphicVisualization.GraphViewer;
import scheduling.RoundRobinSchedulingStrategy;
import scheduling.SchedulingStrategy;




public class Program
{

	public static void main(String[] args) throws Exception
	{
		
		
		
		SchedulingStrategy s = new RoundRobinSchedulingStrategy();
		s.scheduling();
		
		new GraphViewer();
		
		/*TrafficEstimatingFacade.getSourceBuffers();
		
		List<TCUnit> configs = TCFacade.getConfigurations();
		
		for (TCUnit tc : configs)
		{
			
			for (Entry<Link, Integer> link : tc.entrySet() )
			{
				print(link.getKey().getId() + "\t" + link.getValue());
			}
			print("*******************************************************************");
		}
		
		print("**********NUMBER OF LINKS In transmission configuration**********");
		for (TCUnit tc : configs)
			print(tc.getLinks().size()+"");		
		
		print("*****************************NUMBER OF CHENNELS*************************");
		
		for (Entry<Channel, Integer> chOccurrance :  ChannelAssignmentFacade.getChannelOccurance().entrySet())
		{
			print(chOccurrance.getKey() + " : " + chOccurrance.getValue());
		}
		/* /*
		print("*****************************Scheduling*************************");
		
		/*List<DataRate> dataRate = ApplicationSettingFacade.DataRate.getDataRate();
		
		for (DataRate dr : dataRate)
		{
			print(dr.toString());
		}
		
		
		
		
		
		LinkTrafficMap traffic = TrafficEstimatingFacade.getLinksTraffic();

		for (Entry<Link, Float> links : traffic.Sort().entrySet())
		{
			print(links.getKey().getId() + " " + channelMap.get(links.getKey()));	
		}
		for (Entry<Vertex, List<Path>> paths : TrafficEstimatingFacade.getOptimalUplinkPath().entrySet())
		{
			String p = "";
			
			
			for (Path pa : paths.getValue())
			{
				p += pa.toString(false,true) + "\n";
			}
			
			p += "\n *************************************************************";
			
			print(p);
		}*/

	}

	private static void print(String str)
	{
		System.out.println(str);

	}
}
