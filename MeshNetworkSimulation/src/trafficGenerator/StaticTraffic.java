package trafficGenerator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import common.FileGenerator;
import common.PrintConsole;
import dataStructure.DownlinkTraffic;
import dataStructure.Path;
import dataStructure.PathMap;
import dataStructure.UplinkTraffic;
import dataStructure.Vertex;
import setting.ApplicationSettingFacade;
import setting.BaseConfiguration.TypeOfGenerationEnum;

public class StaticTraffic
{	
	
	
	private static long upSeed = ApplicationSettingFacade.Traffic.getUpSeed();
	private static long downSeed = ApplicationSettingFacade.Traffic.getDownSeed();
	private static UplinkTraffic  _uplinkTraffic;
	
	private static DownlinkTraffic _downlinkTraffic;
	
	public static UplinkTraffic getUplinkTraffic()
	{
		if(_uplinkTraffic != null) return _uplinkTraffic;
		
		if(ApplicationSettingFacade.Traffic.getTypeOfGenerator() == TypeOfGenerationEnum.RANDOM)
			return randomUpLink();
		return fileUplink();
	}
	
	public static UplinkTraffic getUplinkTraffic(PathMap uplinks)
	{
		if(_uplinkTraffic != null) return _uplinkTraffic;
		
		if(ApplicationSettingFacade.Traffic.getTypeOfGenerator() == TypeOfGenerationEnum.RANDOM)
			return randomUpLink(uplinks);
		
		return fileUplink();
	}
	
	private static UplinkTraffic fileUplink()
	{
		_uplinkTraffic = new UplinkTraffic();
		Map<Integer, Vertex> routers =  ApplicationSettingFacade.Nodes.getNodes();
		String var = null; 
		String[] tokens = null;
		try
		{
		
			InputStream in = StaticTraffic.class.getResourceAsStream(ApplicationSettingFacade.Traffic.getAddressUp());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while((var=reader.readLine())!=null)
			{
				tokens = var.split("[ ]");
				int nodeId = Integer.parseInt(tokens[0]);
				_uplinkTraffic.add(routers.get(nodeId) , Integer.parseInt(tokens[1]));
			}
			reader.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		FileGenerator.UplinkTrafficInFile(_uplinkTraffic);
		return _uplinkTraffic;
	}
	
	private static UplinkTraffic randomUpLink(PathMap uplinks)
	{
		_uplinkTraffic = new UplinkTraffic();
		Random rand = new Random(upSeed);
		PrintConsole.print("Seed for uplink traffic is: " + upSeed);
		for (Entry<Vertex, List<Path>> routerMap : uplinks.entrySet())
		{
			int trf = rand.nextInt(20) + 30;
			_uplinkTraffic.add(routerMap.getKey(), trf*100);
		}
		PrintConsole.print("Generating static traffic for uplinks is done.");
		
		FileGenerator.UplinkTrafficInFile(_uplinkTraffic);
		
		return _uplinkTraffic;

	}
	
	private static UplinkTraffic randomUpLink()
	{
		_uplinkTraffic = new UplinkTraffic();
		Map<Integer,Vertex> routerSet = ApplicationSettingFacade.Router.getRouter();
		PrintConsole.print("Seed for uplink traffic is: " + upSeed);
		Random rand = new Random(upSeed);
		for (Entry<Integer,Vertex> routerMap : routerSet.entrySet())
		{
			
			int trf = rand.nextInt(20) + 30;
			_uplinkTraffic.add(routerMap.getValue(), trf*100);
		}

		PrintConsole.print("Generating static traffic for uplinks is done.");
		
		FileGenerator.UplinkTrafficInFile(_uplinkTraffic);
		
		return _uplinkTraffic;
	}
	
	public static DownlinkTraffic getDownlinkTraffic()
	{
		if(_downlinkTraffic != null) return _downlinkTraffic;
		
		if(ApplicationSettingFacade.Traffic.getTypeOfGenerator() == TypeOfGenerationEnum.RANDOM)
			return downlinkRandom();
		return downlinkFile();
	}
	
	public static DownlinkTraffic getDownlinkTraffic(PathMap downlinkPaths)
	{
		if(_downlinkTraffic != null) return _downlinkTraffic;
		if(ApplicationSettingFacade.Traffic.getTypeOfGenerator() == TypeOfGenerationEnum.RANDOM)
			return downlinkRandom(downlinkPaths);
		return  downlinkFile();
	}

	private static DownlinkTraffic downlinkRandom(PathMap downlinkPaths)
	{
		Random rand = new Random(downSeed);
		_downlinkTraffic = new DownlinkTraffic();
		PrintConsole.print("Seed for downlink traffic is: " + downSeed);
		
		for (Entry<Vertex, List<Path>> ver_path : downlinkPaths.entrySet())
		{
			Vertex gateway = ver_path.getKey();
			TreeMap<Vertex,Float> grMap = new TreeMap<>();
			for (Path p : ver_path.getValue())
			{
				Vertex destination = p.getDestination();
				float trf = rand.nextInt(20) + 30;
				grMap.put(destination, trf*100);
				
				
			}
			_downlinkTraffic.add(gateway, grMap);
		}
		return _downlinkTraffic;
	}

	 static DownlinkTraffic downlinkRandom()
	{
		Map<Integer,Vertex> routerSet = ApplicationSettingFacade.Router.getRouter();
		//int gatewayNumbers = ApplicationSettingFacade.Gateway.getSize();
		Random rand = new Random(downSeed);
		PrintConsole.print("Seed for downlink traffic is: " + downSeed);
		_downlinkTraffic = new DownlinkTraffic();
		for (Entry<Integer, Vertex> getway : ApplicationSettingFacade.Gateway.getGateway().entrySet())
		{
			TreeMap<Vertex,Float> grMap = new TreeMap<>();
			for (Entry<Integer,Vertex> routerMap : routerSet.entrySet())
			{
				
				float trf = rand.nextInt(20) + 30;
				
				grMap.put(routerMap.getValue(), trf*100);
			}
			_downlinkTraffic.add(getway.getValue(), grMap);
		}
		PrintConsole.print("Generating static traffic for downlinks is done.");
		FileGenerator.dowlinkTrafficInFile(_downlinkTraffic.getTraffic());
		return _downlinkTraffic;
	}
	
	private static DownlinkTraffic downlinkFile()
	{
		Map<Integer,Vertex> routerSet = ApplicationSettingFacade.Router.getRouter();
		int routerSize = routerSet.size();
		_downlinkTraffic = new DownlinkTraffic();
		String var = null; 
		int gatwayIndex = 0;
		int RouterIndex = 0;
		String[] tokens = null;
		Vertex gateway = ApplicationSettingFacade.Gateway.getGateway().get(gatwayIndex);
		
		try
		{
			TreeMap<Vertex, Float> grMap = new TreeMap<>();
			InputStream in = StaticTraffic.class.getResourceAsStream(ApplicationSettingFacade.Traffic.getAddressDown());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while((var=reader.readLine())!=null)
			{
				
				if(RouterIndex == routerSize)
				{
					RouterIndex = 0;
					_downlinkTraffic.add(gateway, grMap);
					gatwayIndex++;
					gateway = ApplicationSettingFacade.Gateway.getGateway().get(gatwayIndex);
					grMap = new TreeMap<>();
				}
				tokens = var.split("[ ]");
				int routerID = Integer.parseInt(tokens[0]);
				float traffic = Float.parseFloat(tokens[1]);
				grMap.put(ApplicationSettingFacade.Router.getRouters().get(routerID), traffic);
				RouterIndex++;
			}
			
			_downlinkTraffic.add(gateway, grMap);
			reader.close();
		}
		catch(Exception ex)
		{
			PrintConsole.printErr("StaticTraffic/DownlinkTraffic : message: " + ex.getMessage());
		}
		FileGenerator.dowlinkTrafficInFile(_downlinkTraffic.getTraffic());
		return _downlinkTraffic;
	}

	

	
}
