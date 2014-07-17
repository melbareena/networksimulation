package setting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import common.PrintConsole;
import dataStructure.Vertex;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;



/**
 * Class's responsibility is fetching data from configuration file (xml file). This class follows <i>singleton</i> design pattern. You <strong>cannot</strong> use this class directly. 
 * for using this class in rest of program please watch {@link ApplicationSettingFacade}
 * @author Mahdi Negahi
 *
 */
class GatewayConfig extends BaseConfiguration
{
	private static GatewayConfig _selfObject;
	public static GatewayConfig Initiating()
	{
		if(_selfObject == null)
			_selfObject = new GatewayConfig();
		return _selfObject;
	}
	private GatewayConfig()
	{
		this.FetchConfig();
	}
	private static final String TAG = "Gateways";
	private static final String ATTNUM = "Number";
	private static final String ATTTOG = "TypeOfGeneration";
	private static final String CHILD = "Add";
	private static final String ATTFILE = "Address";
	private static final String ATTSEED = "Seed";
	private static final String ATTRADIO = "Radio";
	
	private long seed;
	public long getSeed() throws Exception
	{
		return seed;
	}
	
	private int num = 0;
	public int getNum()
	{
		return num;
	}
	
	private TypeOfGenerationEnum typeOfgeneration;
	public TypeOfGenerationEnum getTypeOfgeneration()
	{
		return typeOfgeneration;
	}
	
	private Map<Integer, Vertex> gatways;
	public Map<Integer, Vertex> getGatways()
	{
		return gatways;
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
		Node gatewayNode = nodes.item(0);
		if(gatewayNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element eElement = (Element) gatewayNode;
			
			
			try
			{
				if(ValidateXMLDocument(eElement))
				{
					num =Integer.valueOf(eElement.getAttribute(ATTNUM));
					typeOfgeneration = TypeOfGenerationEnum.valueOf(eElement.getAttribute(ATTTOG).toUpperCase());
					radio =  Integer.parseInt(eElement.getAttribute(ATTRADIO));	
					if(typeOfgeneration == TypeOfGenerationEnum.STATIC && ValidateXMLDocument(eElement, typeOfgeneration))
						FetchStatic(eElement);				
					if(typeOfgeneration == TypeOfGenerationEnum.FILE && ValidateXMLDocument(eElement, typeOfgeneration))
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


	private void FetchStatic(Element eElement)
	{
		PrintConsole.print("App fetchs gatways from cofiguration file <config.xml>.");
		gatways = new TreeMap<Integer,Vertex>();
		int Id = 0;
		NodeList childNodes = eElement.getElementsByTagName(CHILD);
		Vertex newGateway;
		for (int counter = 0; counter < childNodes.getLength() && counter < num; counter++)
		{
			Node childNode = childNodes.item(counter);
			if (childNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element childElem = (Element) childNode;
				newGateway = new Vertex(Id, new Point ( Integer.valueOf(childElem.getAttribute("x")),
						Integer.valueOf(childElem.getAttribute("y")  )));
				
				gatways.put(newGateway.getId(), newGateway);
				Id++;
			}
		}
	}

	private void FetchFromFile(Element eElement)
	{
		PrintConsole.print("App fetchs gatways from a different file.");
		fileAddress = eElement.getAttribute(ATTFILE);
	
		try
		{
			BufferedReader reader =  null;
			int Id = 0;
			reader =new BufferedReader(new FileReader(fileAddress));
			String var = "";
			String[] Pos;
			gatways = new TreeMap<>();
			Vertex newGateway;
			while((var = reader.readLine()) != null)
			{
				Pos = var.split("[ ]");
				newGateway = new Vertex(Id, new Point ( Integer.valueOf(Pos[0]),Integer.valueOf(Pos[1])));			
				gatways.put(newGateway.getId(), newGateway);
				Id++;
			}
			reader.close();
			
		}
		catch(Exception ex)
		{
			PrintConsole.printErr("GatewayConfig/FetchFromFile message:" + ex.getMessage());
		}
		
	}


	@Override
	protected boolean ValidateXMLDocument(Element eElement) throws Exception
	{
		if(!eElement.hasAttribute(ATTNUM))
			throw new Exception(ATTNUM + " attribute in " + TAG + " tag is missing. the following template may help you." +
							"\n <Gateways Number=\"2\" TypeOfGeneration=\"Static\"> \n	" +
							"<Add x=\"114\" y=\"457\" />\n<Add x=\"12\" y=\"333\" /></Gateways>" );
		else if(!Pattern.matches("-?[0-9]+", eElement.getAttribute(ATTNUM)))
			throw new Exception("The value of " + ATTNUM + " attribute  in "+ TAG +" must be an interger value. \n\n" );
		
		if(!eElement.hasAttribute(ATTTOG))
			throw new Exception(ATTTOG + " attribute in " + TAG + " tag is missing. this template may help you." +
						"\n <Gateways Number=\"2\" TypeOfGeneration=\"Static\"> \n	" +
						"<Add x=\"114\" y=\"457\" />\n<Add x=\"12\" y=\"333\" /></Gateways>" );
		else if(!Pattern.matches("random|static|file", eElement.getAttribute(ATTTOG).toLowerCase()))
			throw new Exception("the value of " + ATTTOG + " in "+ TAG +" node must be on of the following item:" +
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


	protected boolean ValidateXMLDocument(Element eElement,
			TypeOfGenerationEnum type) throws Exception
	{
		if(type == TypeOfGenerationEnum.RANDOM)
		{
			if(eElement.hasAttribute(ATTSEED))
				if(!Pattern.matches("-?[0-9]+", eElement.getAttribute(ATTSEED)))
					throw new Exception("The value of "+ ATTSEED + " attribute in "+ TAG +" must be an interger value. \n\n");
		}
		if(type == TypeOfGenerationEnum.FILE)
			if(!eElement.hasAttribute(ATTFILE))
				throw new Exception(ATTFILE + " attribute in " + TAG + " tag is missing. the following template may help you \n" +
						"<Gateways Number=\"2\" TypeOfGeneration=\"file\" Address=\"src/setting/gatway.txt\" />" );
		if(type == TypeOfGenerationEnum.STATIC)
			if(!eElement.hasChildNodes())
				throw new Exception(TAG + " must have child node. the following template may help you " +
						"\n <Gateways Number=\"2\" TypeOfGeneration=\"Static\">" +
						" \n <Add x=\"150\" y=\"975\" /> \n <Add x=\"274\" y=\"785\" />\n</Gateways> " );
		
		return true;
	}

}
