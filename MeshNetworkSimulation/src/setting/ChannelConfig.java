package setting;


import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.PrintConsole;

import dataStructure.Channel;


/**
 * Class's responsibility is fetching data from configuration file (xml file). This class follows <i>singleton</i> design pattern. You <strong>cannot</strong> use this class directly. 
 * for using this class in rest of program please watch {@link ApplicationSettingFacade}
 * @author Mahdi Negahi
 *
 */
class ChannelConfig extends BaseConfiguration
{
	private static ChannelConfig _selfObject;
	public static ChannelConfig Initiating()
	{
		if(_selfObject == null)
			_selfObject = new ChannelConfig();
		return _selfObject;
	}
	private ChannelConfig()
	{
		this.FetchConfig();
	}
	
	private ChannelMode _mode;
	public ChannelMode getMode()
	{
		return _mode;
	}
	private static final String TAG = "Channels";
	private static final String ATTMODE = "Mode";
	private static final String CHILD = "Add";
	private static final String CHILDVALUE = "Value";
	
	// Map(KEY,VALUE>
	// KEY = the number of channel
	private List<Channel> _channel;
	
	public  List<Channel> getChannel()
	{	
		return _channel;
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
				
					_mode  = ChannelMode.valueOf(eElement.getAttribute(ATTMODE));
					if(_mode == ChannelMode.Partially)
						FetchPartially(eElement);
					else if (_mode == ChannelMode.All)
						FetchALL();
					
				}
			} 
			catch (Exception e)
			{
			    System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}

	}

	private void FetchALL()
	{
		_channel = new  ArrayList<Channel>();
		for(int i = 1 ; i<= 11 ; i++)
		{
			_channel.add(new Channel(i));
		}
		
	}
	private void FetchPartially(Element eElement)
	{
		NodeList childNodes = eElement.getElementsByTagName(CHILD);
		_channel = new  ArrayList<Channel>();
		for (int counter = 0; counter < childNodes.getLength(); counter++)
		{
			Node childNode = childNodes.item(counter);
			if (childNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element childElem = (Element) childNode;
				int val = Integer.valueOf(childElem.getAttribute(CHILDVALUE));
				_channel.add(new Channel(val)); 
			}
		}
		
	}
	@Override
	protected boolean ValidateXMLDocument(Element eElement) throws Exception
	{
		if(!eElement.hasAttribute(ATTMODE))
		{
			PrintConsole.printErr(TAG + " must have child node. the following template may help you " +
					"\n <Channels Mode=\"Partially|All\"> \n \t <Add Value=\"1\" />\n\t<Add Value=\"2\" /> \n </Channels>" );
			System.exit(0);
		}
		return true;
	}

	protected boolean ValidateXMLDocument(Element eElement,
			ChannelMode mode) throws Exception
	{
		if(mode == ChannelMode.Partially)
		{
			if(eElement.hasChildNodes())
			{
				PrintConsole.printErr(TAG + " must have child node. the following template may help you " +
						"\n <Channels Mode=\"Partially\"> \n \t <Add Value=\"1\" />\n\t<Add Value=\"2\" /> \n </Channels>" );
				System.exit(0);
			}
		}
		return true;
	}

}
