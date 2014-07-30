package setting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.PrintConsole;

/**
 * Class's responsibility is fetching data from configuration file (xml file). This class follows <i>singleton</i> design pattern. You <strong>cannot</strong> use this class directly. 
 * for using this class in rest of program please watch {@link ApplicationSettingFacade}
 * @author Mahdi Negahi
 *
 */
class EnvironmentConfig extends BaseConfiguration
{
	private static EnvironmentConfig _selfObject;
	
	public static EnvironmentConfig Initiating()
	{
		if(_selfObject == null)
			_selfObject = new EnvironmentConfig();
		return _selfObject;
	}
	
	private EnvironmentConfig()
	{
		FetchConfig();
	}
	
	private static final String TAG = "Environment";
	private static final String ATTWIDTH = "X";
	private static final String ATTHEIGHT = "Y";
	
	
	
	
	private int _width;
	private int _height;
	
	public int getWidth()
	{
		return _width;
	}
	public int getHeight()
	{
		return _height;
	}
	

	@Override
	protected void FetchConfig()
	{
		Document doc = XMLParser.Parser();	
		NodeList nodes =  doc.getElementsByTagName(TAG);
		Node environmentNode = nodes.item(0);
		if(environmentNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element eElement = (Element) environmentNode;
			try
			{
				if(ValidateXMLDocument(eElement))
				{
					_width =Integer.valueOf(eElement.getAttribute(ATTWIDTH));
					_height =Integer.valueOf(eElement.getAttribute(ATTHEIGHT));
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
		if(!eElement.hasAttribute(ATTWIDTH))
		{
			PrintConsole.printErr(ATTWIDTH + " attribute in " + TAG + " tag is missing. the following template may help you." +
						"\n <Environment Width=\"1000\" Height=\"1000\" />" );
			System.exit(0);
		}
		if(!eElement.hasAttribute(ATTHEIGHT))
		{
			PrintConsole.printErr(ATTHEIGHT + " attribute in " + TAG + " tag is missing. the following template may help you." +
						"\n <Environment Width=\"1000\" Height=\"1000\" />" );
			System.exit(0);
		}
		return true;
	}
	

}
