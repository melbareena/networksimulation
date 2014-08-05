package setting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.PrintConsole;

public class SchedulingConfig  extends BaseConfiguration
{
	private static final  String TAG = "Scheduling";
	private static final String ATTSTERATEGY = "Strategy";
	
	private String _className;
	public String getClassName()
	{
		return _className;
	}
	
	private static SchedulingConfig _selfObject;
	public static SchedulingConfig Initiating()
	{
		if(_selfObject == null)
			_selfObject = new SchedulingConfig();
		return _selfObject;
	}
	private SchedulingConfig()
	{
		this.FetchConfig();
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
				
					_className = eElement.getAttribute(ATTSTERATEGY);
					
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
		if(!eElement.hasAttribute(ATTSTERATEGY))
		{
			PrintConsole.printErr(TAG + " must have an attribute. the following template may help you " +
					"\n	<Scheduling Strategy=\"NormalStrategy\" />" );
			System.exit(0);
		}
		return true;
	} 

}
