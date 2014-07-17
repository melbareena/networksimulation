package setting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class's responsibility is fetching data from configuration file (xml file). This class follows <i>singleton</i> design pattern. You <strong>cannot</strong> use this class directly. 
 * for using this class in rest of program please watch {@link ApplicationSettingFacade}
 * @author Mahdi Negahi
 *
 */
class SinrConfig extends BaseConfiguration
{
	
	private static SinrConfig _selfObject;
	public static SinrConfig Initiating()
	{
		if(_selfObject == null)
			_selfObject = new SinrConfig();
		return _selfObject;
	}
	private SinrConfig()
	{
		this.FetchConfig();
	}
	
	private static final String TAG = "SINR";
	private static final String ATTALPHA = "Alpha";
	private static final String ATTW = "W";
	private static final String ATTPOWER = "Power";
	
	private static final String ATTBETA = "Beta";
	private static final String ATTMUE = "Mue";
	
	private double _mue;
	public double getMue()
	{
		return _mue;
	}
	
	private float _beta;
	public float getBeta()
	{
		return _beta;
	}
	
	private float _alpha;
	public float getAlpha()
	{
		return _alpha;
	}
	
	private float _w;
	public float getW()
	{
		return _w;
	}
	
	private float _power;
	public float getPower()
	{
		return _power;
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
				
					_alpha =Float.valueOf(eElement.getAttribute(ATTALPHA));
					_w =Float.valueOf(eElement.getAttribute(ATTW));
					_power = Float.valueOf(eElement.getAttribute(ATTPOWER));
					_beta = Float.valueOf(eElement.getAttribute(ATTBETA));
					_mue = Float.valueOf(eElement.getAttribute(ATTMUE));
				}
			} 
			catch (Exception e)
			{
			    System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}

	}

	@Override
	protected boolean ValidateXMLDocument(Element eElement) throws Exception
	{
		if(!eElement.hasAttribute(ATTALPHA))
			throw new Exception(ATTALPHA + " attribute in " + TAG + " tag is missing. the following template may help you." +
					"\n	<SINR Alpha=\"4\" W=\"20\" Power=\"25\" Beta=\"8.51\" Mue=\"0.000000001\"/>" );
		if(!eElement.hasAttribute(ATTW))
			throw new Exception(ATTW + " attribute in " + TAG + " tag is missing. the following template may help you." +
					"\n	<SINR Alpha=\"4\" W=\"20\" Power=\"25\" Beta=\"8.51\" Mue=\"0.000000001\"/>" );
		if(!eElement.hasAttribute(ATTPOWER))
			throw new Exception(ATTPOWER + " attribute in " + TAG + " tag is missing. the following template may help you." +
					"\n	<SINR Alpha=\"4\" W=\"20\" Power=\"25\" Beta=\"8.51\" Mue=\"0.000000001\"/>" );
		if(!eElement.hasAttribute(ATTBETA))
			throw new Exception(ATTBETA + " attribute in " + TAG + " tag is missing. the following template may help you." +
						"\n	<SINR Alpha=\"4\" W=\"20\" Power=\"25\" Beta=\"8.51\" Mue=\"0.000000001\"/>" );
		if(!eElement.hasAttribute(ATTMUE))
			throw new Exception(ATTMUE + " attribute in " + TAG + " tag is missing. the following template may help you." +
						"\n	<SINR Alpha=\"4\" W=\"20\" Power=\"25\" Beta=\"8.51\" Mue=\"0.000000001\"/>" );
		return true;
	}

}
