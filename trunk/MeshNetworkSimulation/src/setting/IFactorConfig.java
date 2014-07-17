package setting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dataStructure.IFactorMap;


/**
 * Class's responsibility is fetching data from configuration file (xml file). This class follows <i>singleton</i> design pattern. You <strong>cannot</strong> use this class directly. 
 * for using this class in rest of program please watch {@link ApplicationSettingFacade}
 * @author Mahdi Negahi
 *
 */
public class IFactorConfig extends BaseConfiguration
{
	private static IFactorConfig _selfObject;
	public static IFactorConfig Initiating()
	{
		if(_selfObject == null)
			_selfObject = new IFactorConfig();
		return _selfObject;
	}
	private IFactorConfig()
	{
		this.FetchConfig();
	}
	
	private static final String TAG = "IFactor";
	private static final String CHILD = "Add";
	
	
	private IFactorMap IFactorMap;
	
	public IFactorMap getIFactorMap()
	{
			return IFactorMap;
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
					IFactorMap = new IFactorMap();
					NodeList childNodes = eElement.getElementsByTagName(CHILD);
					for (int counter = 0; counter < childNodes.getLength(); counter++)
					{
						Node childNode = childNodes.item(counter);
						if (childNode.getNodeType() == Node.ELEMENT_NODE)
						{
							Element childElem = (Element) childNode;
							IFactorMap.put( Integer.valueOf(childElem.getAttribute("Key")),
									Double.valueOf(childElem.getAttribute("Value") )); 
						}
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

	@Override
	protected boolean ValidateXMLDocument(Element eElement) throws Exception
	{
		// TODO Auto-generated method stub
		if(!eElement.hasChildNodes())
			throw new Exception(TAG + " must have child node. the following template may help you " +
					"<IFactor> \n \t <Add Key=\"0\" Value=\"1\" /> \n </IFactor>" );
		return true;
	}


}
