package setting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.PrintConsole;


public class ChannelAssignmentConfig extends BaseConfiguration
{

	private static final  String TAG = "ChannelAssignment";
	private static final String ATTSTERATEGY = "Strategy";
	
	private String _className;
	public String getClassName()
	{
		return _className;
	}
	
	private static ChannelAssignmentConfig _selfObject;
	public static ChannelAssignmentConfig Initiating()
	{
		if(_selfObject == null)
			_selfObject = new ChannelAssignmentConfig();
		return _selfObject;
	}
	private ChannelAssignmentConfig()
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
					"\n	<ChannelAssignment Strategy=\"Standard\" />" );
			System.exit(0);
		}
		return true;
	}

}
