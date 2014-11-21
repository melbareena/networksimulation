package setting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.PrintConsole;

public class PowerControlConfig  extends BaseConfiguration
{
	private static PowerControlConfig _selfObject;
	public static PowerControlConfig Initiating()
	{
		if(_selfObject == null)
			_selfObject = new PowerControlConfig();
		return _selfObject;
	}
	private PowerControlConfig()
	{
		this.FetchConfig();
	}
	
	private static final String TAG = "PowerControl";
	private final static String ATTENABLE = "Enable";

	
	private boolean enable;
	public boolean isEnbale()
	{
		return enable;
	}
	@Override
	protected void FetchConfig()
	{
		Document doc = XMLParser.Parser();
		NodeList nodes =  doc.getElementsByTagName(TAG);
		Node folderNode = nodes.item(0);
		if(folderNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element eElement = (Element) folderNode;
			
			
			try
			{
				if(ValidateXMLDocument(eElement))
					enable = Boolean.parseBoolean(eElement.getAttribute(ATTENABLE));
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
		if(!eElement.hasAttribute(ATTENABLE))
		{
			PrintConsole.printErr(TAG + " must have Enable attribute. the following template may help you " +
					"\n <PowerControl Enable=\"True\" />" );
			System.exit(0);
		}
		return true;
	}

}
