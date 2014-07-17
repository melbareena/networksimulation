package setting;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import setting.BaseConfiguration.TypeOfGenerationEnum;

import dataStructure.IFactorMap;
import dataStructure.Vertex;


/**
 * The class is a public point for interacting with the configuration file. There are several different nested classes here.
 * @author Mahdi Negahi
 *
 */
public class ApplicationSettingFacade
{
	private static EnvironmentConfig _eConf = EnvironmentConfig.Initiating();
	private static SinrConfig _sConf = SinrConfig.Initiating();
	private static IFactorConfig _iCong = IFactorConfig.Initiating();
	private static ChannelConfig _cCong = ChannelConfig.Initiating();
	private static OutputConfig _oCong = OutputConfig.Initiating();
	private static GatewayConfig _gConf = GatewayConfig.Initiating();
	private static RouterConfig _rConf = RouterConfig.Initiating();
	private static TrafficGenerator _tConf = TrafficGenerator.Initiating();
	private static DataRateConfig _dCong = DataRateConfig.Initiating();
	private static ChannelAssignmentConfig _chCong = ChannelAssignmentConfig.Initiating();
	
	public static class ChannelAssignment
	{
		public static String getSterategyClassName()
		{
			return _chCong.getClassName();
		}
	}
	public static class DataRate
	{
		private static int max = 0;
		public static List<dataStructure.DataRate> getDataRate()
		{
			return _dCong.getDataRates();
		}
		
		public static int getMax()
		{
			if(max == 0)
			{
				List<dataStructure.DataRate> rates = getDataRate();
				for (dataStructure.DataRate r : rates)
				{
					if(r.getRate() > max)
						max = r.getRate();
				}
			}

			return max;
		}
	}
	
	
	
	public static class Traffic
	{
		public static TypeOfGenerationEnum getTypeOfGenerator()
		{
			return _tConf.getTypeOfgeneration();
		}
		public static String getAddressUp()
		{
			return _tConf.getAddressUp();
		}
		public static String getAddressDown()
		{
			return _tConf.getAddressDown();
		}
	}
	
	public static class AppOutput
	{
		public static String getOutputFolder()
		{
			return _oCong.getOutput();
		}
		public static boolean isGenerateFileAsOutput()
		{
			return _oCong.getFileAsoutput();
		}
		public static boolean showIntermediateOutput()
		{
			return _oCong.getIntermediateOutput();
		}
	}
	
	
	public static class Gateway
	{
		
		
		public static Map<Integer,Vertex> getGateway()
		{
			return _gConf.getGatways();
		}
		
		
		/**
		 * @return the number of gateway which is specified in XML file
		 */
		public static int getSize()
		{
			return _gConf.getNum();
			
		}
		
		/**
		 * @return  the type of generation method of gateways' position.
		 */
		public static BaseConfiguration.TypeOfGenerationEnum getTypeOfGeneration()
		{
			return _gConf.getTypeOfgeneration();			
		}
		
		/**
		 * @return In random generation method for recreating the same sequence you can set seed for getting a unique random sequence.
		 */
		public static long getSeed()
		{
			try
			{
				return _gConf.getSeed();
			}
			catch(Exception ex)
			{
				System.err.println(ex.getMessage());
				return (Long) null;
			}
		}
		
		public static int getRadio()
		{
			return _gConf.getRadio();
		}
		
	    public static boolean isGateway(Vertex x)
	    {
	    	boolean isGateway = false;
	    	
	    	for (Entry<Integer, Vertex> gateway : getGateway().entrySet())
			{
				if(x.getId() == gateway.getKey())
				{
					isGateway = true;
					break;
				}
			}   	
	    	return isGateway;
	    }
		
	}
	
	public static class Router
	{
		public static int getSize()
		{
			return _rConf.getNum();
		}
				
		public static int getRadio()
		{
			return _rConf.getRadio();
		}
		
		public static Map<Vertex, Vector<Vertex>> getNeigbors()
		{
			return _rConf.getNeighbors();
		}
		
		public static Map<Integer, Vertex> getRouter()
		{
			return _rConf.getRouters();
		}
		/**
		 * @return  the type of generation method of routers' position.
		 */
		public static  BaseConfiguration.TypeOfGenerationEnum getTypeOfGeneration()
		{
			return _rConf.getTypeOfgeneration();
		}
		
		/**
		 * @return List of routers position.
		 */
		public static Map<Integer,Vertex> getRouters()
		{		
				return _rConf.getRouters();	
		}

		/**
		 * @return the minimum distance which each router can have with other routers.
		 */
		public static int getMinDistance()
		{
			return _rConf.getMinDistance();
		}
		
		/**
		 * @return transmission cover of each router.
		 */
		public static int getTransmissionRate()
		{
			return _rConf.getTransmissionRate();
		}
		/**
		 * @return the times of trying generation of a random position which is far away form other routers with minDistance value.
		 */
		public static int getSaftyTest()
		{
			try
			{
				return _rConf.getSaftyTest();
			}
			catch(Exception ex)
			{
				System.err.println(ex.getMessage());		
			}
			return (Integer) null;
			
		}
		
		
		
		/**
		 * @return In random generation method for recreating the same sequence you can set seed for getting a unique random sequence.
		 */
		public static long getSeed()
		{
			try
			{
				return _rConf.getSeed();
			}
			catch(Exception ex)
			{
				System.err.println(ex.getMessage());		
			}
			return (Long) null;
		}
	}
	public static class Nodes
	{
		/**
		 * @return routers + gateways
		 */
		public static  Map<Integer,Vertex> getNodes()
		{
			return _rConf.getNodes();
		}
	}

	/**
	 * getting the dimension of environment.
	 */
	public static class Environment
	{
		public static int getWidth()
		{
			return _eConf.getWidth();
		}
		public static int getHeight()
		{
			return _eConf.getHeight();
		}
	}
	
	public static class SINR
	{
		public static float getPower()
		{
			return _sConf.getPower();
		}
		public static float getAlpha()
		{
			return _sConf.getAlpha();
		}
		public static float getW()
		{
			return _sConf.getW();
		}
		public static double getMue()
		{
			return _sConf.getMue();
		}
		public static float getBeta()
		{
			return _sConf.getBeta();
		}
	}
	
	public static class IFactor
	{
		public static IFactorMap getIFactorMap()
		{
			return _iCong.getIFactorMap();
		}
	}
	
	public static class Channel
	{
		public static List<dataStructure.Channel> getChannel()
		{
			return _cCong.getChannel();
		}
	}




}
