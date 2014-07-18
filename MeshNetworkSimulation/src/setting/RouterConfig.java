package setting;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.PrintConsole;
import dataStructure.Vertex;
import topology.BaseTopology;
import topology.RandomTopology;
import topology.StaticTopology;






/**
 * Class's responsibility is fetching data from configuration file (xml file). This class follows <i>singleton</i> design pattern. You <strong>cannot</strong> use this class directly. 
 * for using this class in rest of program please watch {@link ApplicationSettingFacade}
 * @author Mahdi Negahi
 *
 */
class RouterConfig extends BaseConfiguration
{
	private BaseTopology topologyBuilder;
	private static RouterConfig _selfObject;
	public static RouterConfig Initiating()
	{
		if(_selfObject == null)
			_selfObject = new RouterConfig();
		return _selfObject;
	}
	private RouterConfig()
	{
		this.FetchConfig();
	}
	
	
	// XML tag'name
	private static final String TAG = "Routers";
	private static final String ATTNUM = "Number";
	private static final String ATTTOG = "TypeOfGeneration";
	private static final String ATTMINDISTANCE = "MinDistance";
	private static final String CHILD = "Add";
	private static final String ATTSAFTYTEST = "SaftyTest";
	private static final String ATTSEED = "Seed";
	private static final String ATTFILE = "Address";
	private static final String ATTTRANSCOVER = "TransmissionRate";
	private static final String ATTRADIO = "Radio";
	
	
	private Map<Integer,Vertex> routers;
	public Map<Integer,Vertex> getRouters()
	{
		return routers;
	}
	
	private long seed;
	public long getSeed() throws Exception
	{
		if(typeOfgeneration == TypeOfGenerationEnum.RANDOM)
			return seed;
		return (Long) null;
	}
	
	private int saftyTest;
	public int getSaftyTest() throws Exception
	{
		if(typeOfgeneration == TypeOfGenerationEnum.RANDOM)
			return saftyTest;
		return (Integer) null;
	}
	private int mindistance;
	public int getMinDistance()
	{
		return mindistance;
	}
	private int numberOfNodes = 0;
	public int getNum()
	{
		return numberOfNodes;
	}
	
	private int transmissionRate = 0;
	public int getTransmissionRate()
	{
		return transmissionRate;
	}
	private TypeOfGenerationEnum typeOfgeneration;
	public TypeOfGenerationEnum getTypeOfgeneration()
	{
		return typeOfgeneration;
	}
	private Map<Integer, Vertex> nodes;
	/*
	 * all nodes ( routers + gateway )
	 */
	public Map<Integer, Vertex> getNodes()
	{
		return nodes;
	}
	
	private Map<Vertex,Vector<Vertex>> neighbors;
	public Map<Vertex,Vector<Vertex>> getNeighbors()
	{
		return neighbors;
	}

	
	private int radio;
	public int getRadio()
	{
		return radio;
	}


	private String fileAddress;
	
	@Override
	protected void FetchConfig()
	{
		Document doc = XMLParser.Parser();	
		NodeList nodes =  doc.getElementsByTagName(TAG);
		Node routerNode = nodes.item(0);
		if(routerNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element eElement = (Element) routerNode;
			
			try
			{
				if(ValidateXMLDocument(eElement))
				{
				
				
					mindistance =Integer.valueOf(eElement.getAttribute(ATTMINDISTANCE));
					typeOfgeneration = TypeOfGenerationEnum.valueOf(eElement.getAttribute(ATTTOG).toUpperCase());
					transmissionRate = Integer.parseInt(eElement.getAttribute(ATTTRANSCOVER));	
					radio =  Integer.parseInt(eElement.getAttribute(ATTRADIO));	
					if(typeOfgeneration == TypeOfGenerationEnum.RANDOM && ValidateXMLDocument(eElement, typeOfgeneration))
						FetchRandom(eElement);
					if(typeOfgeneration == TypeOfGenerationEnum.STATIC  && ValidateXMLDocument(eElement, typeOfgeneration))
						FetchStatic(eElement);				
					if(typeOfgeneration == TypeOfGenerationEnum.FILE  && ValidateXMLDocument(eElement, typeOfgeneration))
						FetchFromFile(eElement);
				}
			} 
			catch (Exception e)
			{
			    System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
	}
	private void FetchRandom(Element eElement)
	{
		PrintConsole.print("App generate routers' position in random way.");
		numberOfNodes =Integer.valueOf(eElement.getAttribute(ATTNUM));
		saftyTest = Integer.valueOf(eElement.getAttribute(ATTSAFTYTEST));
		seed = fetchSeed(eElement);
		topologyBuilder = RandomTopology.Initiate(mindistance, numberOfNodes, transmissionRate,seed, saftyTest);
		nodes = topologyBuilder.CreateTopology();
		routers = topologyBuilder.getRouterSet();
		neighbors = topologyBuilder.getNeighbors();
	}
	private long fetchSeed(Element eElement)
	{
		if(eElement.hasAttribute(ATTSEED))
			return Long.valueOf(eElement.getAttribute(ATTSEED));
		return System.nanoTime();
	}
	private void FetchStatic(Element eElement)
	{
		PrintConsole.print("App fetchs routers from cofiguration file <config.xml>.");
		Vector<Point> staticRouters = new Vector<Point>();
		NodeList childNodes = eElement.getElementsByTagName(CHILD);
		int size = 0;
		for (int counter = 0; counter < childNodes.getLength() && counter < numberOfNodes; counter++)
		{
			Node childNode = childNodes.item(counter);
			if (childNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element childElem = (Element) childNode;
				staticRouters.add(new Point ( Integer.valueOf(childElem.getAttribute("x")),
						Integer.valueOf(childElem.getAttribute("y")  ))); 
				size++;
			}
		}
		numberOfNodes = size;
		topologyBuilder = StaticTopology.Initiate(mindistance, transmissionRate, staticRouters);
		nodes = topologyBuilder.CreateTopology();
		routers = topologyBuilder.getRouterSet();
		neighbors = topologyBuilder.getNeighbors();
	}
	private void FetchFromFile(Element eElement)
	{
		PrintConsole.print("App fetchs routers from a different file.");
		fileAddress = eElement.getAttribute(ATTFILE);
		
		try
		{
			int size = 0;
			BufferedReader reader =  null;
			InputStream in = getClass().getResourceAsStream(fileAddress);
			reader =new BufferedReader(new InputStreamReader(in));
			String var = "";
			String[] Pos;
			Vector<Point> staticRouters = new Vector<Point>();
			while((var = reader.readLine()) != null)
			{
				Pos = var.split("[ ]");
				staticRouters.add(new Point ( Integer.valueOf(Pos[0]),
						Integer.valueOf(Pos[1])));
				size++;
			}
			reader.close();
		
			topologyBuilder = StaticTopology.Initiate(mindistance, transmissionRate, staticRouters);
			nodes = topologyBuilder.CreateTopology();
			neighbors = topologyBuilder.getNeighbors();
			routers = topologyBuilder.getRouterSet();
			numberOfNodes = size;
			
		}
		catch(Exception ex)
		{
			PrintConsole.printErr("GatewayConfig/FetchFromFile message:" + ex.getMessage());
		}
		
	}
	@Override
	protected boolean ValidateXMLDocument(Element eElement) throws Exception
	{
		
		if(!eElement.hasAttribute(ATTMINDISTANCE))
			throw new Exception(ATTMINDISTANCE + " attribute in " + TAG + " tag is missing. the following template may help you." +
						"\n <Routers Number=\"20\" TypeOfGeneration=\"RANDOM\" MinDistance=\"50\" Seed=\" 12252554 \" " +
						"SaftyTest=\"50\" />\n\n" );
		else if(!Pattern.matches("-?[0-9]+", eElement.getAttribute(ATTMINDISTANCE)))
			throw new Exception("The value of "+ ATTMINDISTANCE + " attribute must be an interger value.\n\n" );
		
		if(!eElement.hasAttribute(ATTTRANSCOVER))
			throw new Exception(ATTTRANSCOVER + " attribute in " + TAG + " tag is missing. the following template may help you." +
						"\n <Routers Number=\"20\" TypeOfGeneration=\"RANDOM\" MinDistance=\"50\" Seed=\" 12252554 \" " +
						"SaftyTest=\"50\" />\n\n" );
		else if(!Pattern.matches("-?[0-9]+", eElement.getAttribute(ATTTRANSCOVER)))
			throw new Exception("The value of "+ ATTTRANSCOVER + " attribute must be an interger value.\n\n" );
		
		if(!eElement.hasAttribute(ATTTOG))
			throw new Exception(ATTTOG + " attribute in " + TAG + " tag is missing. this template may help you." +
						"\n <Routers Number=\"20\" TypeOfGeneration=\"RANDOM\" MinDistance=\"50\" Seed=\" 12252554 \" " +
						"SaftyTest=\"50\" />\n\n" );
		else if(!Pattern.matches("random|static|file", eElement.getAttribute(ATTTOG).toLowerCase()))
			throw new Exception("the value of " + ATTTOG + " must be on of the following item:" +
					" \n 1- File \n 2- Static \n 3- Random.\n\n");
		
		if(!eElement.hasAttribute(ATTRADIO))
			throw new Exception(ATTRADIO + " attribute in " + TAG + " tag is missing. the following template may help you." +
						"\n <Routers Number=\"20\" TypeOfGeneration=\"RANDOM\" MinDistance=\"50\" Seed=\" 12252554 \" " +
						"SaftyTest=\"50\" />\n\n" );
		// check the value of tag
		else if(!Pattern.matches("-?[0-9]+", eElement.getAttribute(ATTRADIO)))
			throw new Exception("The value of " + ATTRADIO + " attribute must be an interger value. \n\n" );

		return true;

	}

	protected boolean ValidateXMLDocument(Element eElement, TypeOfGenerationEnum type) throws Exception
	{
		if(type == TypeOfGenerationEnum.RANDOM)
		{
			// check the existence of tag
			if(!eElement.hasAttribute(ATTNUM))
				throw new Exception(ATTNUM + " attribute in " + TAG + " tag is missing. the following template may help you." +
							"\n <Routers Number=\"20\" TypeOfGeneration=\"RANDOM\" MinDistance=\"50\" Seed=\" 12252554 \" " +
							"SaftyTest=\"50\" />\n\n" );
			// check the value of tag
			else if(!Pattern.matches("-?[0-9]+", eElement.getAttribute(ATTNUM)))
				throw new Exception("The value of " + ATTNUM + " attribute must be an interger value. \n\n" );
			
			if(!eElement.hasAttribute(ATTSAFTYTEST))
				throw new Exception(ATTSAFTYTEST + " attribute in " + TAG + " tag is missing. the following template may help you." +
						"\n <Routers Number=\"20\" TypeOfGeneration=\"RANDOM\" MinDistance=\"50\" Seed=\" 12252554 \" " +
						"SaftyTest=\"50\" />" );
			else if(!Pattern.matches("-?[0-9]+", eElement.getAttribute(ATTSAFTYTEST)))
				throw new Exception("The value of "+ ATTSAFTYTEST + " attribute must be an interger value. \n\n" );
			
			
			if(eElement.hasAttribute(ATTSEED))
			     if(!Pattern.matches("-?[0-9]+", eElement.getAttribute(ATTSEED)))
			    	 throw new Exception("The value of "+ ATTSEED + " attribute must be an interger value. \n\n");
		}
		if(type == TypeOfGenerationEnum.FILE)
		{
			if(!eElement.hasAttribute(ATTFILE))
				throw new Exception(ATTFILE + " attribute in " + TAG + " tag is missing. the following template may help you \n" +
						"<Routers Number=\"50\" TypeOfGeneration=\"file\" Address=\"src/setting/routers.txt\" /> \n\n" );
		}
		if(type == TypeOfGenerationEnum.STATIC)
		{
			if(!eElement.hasChildNodes())
				throw new Exception(TAG + " must have child node. the following template may help you " +
						"\n <Routers Number=\"2\" TypeOfGeneration=\"Static\" MinDistance=\"50\" " +
						"SaftyTest=\"50\"> \n <Add x=\"150\" y=\"975\" /> \n <Add x=\"274\" y=\"785\" />\n</Routers> \n\n " );
		}
		return true;
	}
	



}
