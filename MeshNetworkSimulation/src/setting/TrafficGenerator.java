package setting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



class TrafficGenerator extends BaseConfiguration
{
	
	private static TrafficGenerator _selfObject;
	public static TrafficGenerator Initiating()
	{
		if(_selfObject == null)
			_selfObject = new TrafficGenerator();
		return _selfObject;
	}
	
	// XML tag'name
	private static final String TAG = "Traffic";
	private static final String ATTGEN = "Generator";
	private static final String ATTADDRESSUP = "AddressUp";
	private static final String ATTADDRESSDOWN = "AddressDown";
	private static final String ATTUPSEED = "UpSeed";
	private static final String ATTDOWNSEED = "DownSeed";
	
	private TypeOfGenerationEnum typeOfgeneration;
	public TypeOfGenerationEnum getTypeOfgeneration()
	{
			return typeOfgeneration;
	}
	
	private String addressUp;
	public String getAddressUp()
	{
			return addressUp;
	}
	
	private String addressDown;
	public String getAddressDown()
	{
			return addressDown;
	}
	private TrafficGenerator()
	{
		this.FetchConfig();
	}

	private long upSeed;
	public long getUpSeed()
	{
		return upSeed;
	}
	private long downSeed;
	public long getDownSeed()
	{
		return downSeed;
	}
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
					typeOfgeneration = TypeOfGenerationEnum.valueOf(eElement.getAttribute(ATTGEN).toUpperCase());
					if(typeOfgeneration == TypeOfGenerationEnum.FILE  && ValidateXMLDocument(eElement, typeOfgeneration))
						FetchFromFile(eElement);
					if(typeOfgeneration == TypeOfGenerationEnum.RANDOM)
					{
						upSeed =  fetchUpSeed(eElement);
						downSeed = fetchDownSeed(eElement);
					}
				}
			} 
			catch (Exception e)
			{
			    System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
	}
	private long fetchUpSeed(Element eElement)
	{
		if(eElement.hasAttribute(ATTUPSEED))
			return Long.valueOf(eElement.getAttribute(ATTUPSEED));
		return System.nanoTime();
	}
	private long fetchDownSeed(Element eElement)
	{
		if(eElement.hasAttribute(ATTDOWNSEED))
			return Long.valueOf(eElement.getAttribute(ATTDOWNSEED));
		return System.nanoTime();
	}
	private void FetchFromFile(Element eElement)
	{
		addressDown = eElement.getAttribute(ATTADDRESSDOWN);
		addressUp = eElement.getAttribute(ATTADDRESSUP);
		
	}
	@Override
	protected boolean ValidateXMLDocument(Element eElement) throws Exception
	{
		if(!eElement.hasAttribute(ATTGEN))
			throw new Exception(ATTGEN + " attribute in " + TAG + " tag is missing. the following template may help you." +
						"\n <Traffic Generator=\"File\" AddressUp=\"src/setting/input/trafficUp.txt\" AddressDown=\"src/setting/input/traffic.txt\" /> \n  or \n <Traffic Generator=\"Random\" />" );
		
		return true;
	}
	
	
	protected boolean ValidateXMLDocument(Element eElement, TypeOfGenerationEnum g) throws Exception
	{
		if(g == TypeOfGenerationEnum.FILE)
		{
			if(!eElement.hasAttribute(ATTADDRESSDOWN))
				throw new Exception(ATTADDRESSDOWN + " attribute in " + TAG + " tag is missing. the following template may help you \n" +
						"<Traffic Generator=\"File\" AddressUp=\"src/setting/input/trafficUp.txt\" AddressDown=\"src/setting/input/traffic.txt\"  /> \n " );
			if(!eElement.hasAttribute(ATTADDRESSUP))
				throw new Exception(ATTADDRESSUP + " attribute in " + TAG + " tag is missing. the following template may help you \n" +
						"<Traffic Generator=\"File\"AddressUp=\"src/setting/input/trafficUp.txt\" AddressDown=\"src/setting/input/traffic.txt\"  /> \n " );
		}
		return true;
	}
	public void getSeed()
	{
		// TODO Auto-generated method stub
		
	}
}
