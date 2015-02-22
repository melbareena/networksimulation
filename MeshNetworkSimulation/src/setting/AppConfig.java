package setting;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import common.PrintConsole;

public class AppConfig extends BaseConfiguration
{
	private static final  String TAG = "AppConfiguration";
	private static final String ATTMODE = "Mode";
	private static final String ATTALGMODE = "AlgorithmMode";
	private static final String ATTINTERVAL = "Interval";
	
	private AppExecMode _appExcMode;
	public AppExecMode getAppExceMode()
	{
		return _appExcMode;
	}
	private AlgorithmMode _algMode;
	public AlgorithmMode getAlgorithmMode()
	{
		return _algMode;
	}
	
	private int _interval;
	public int getInterval()
	{
		return _interval;
	}
	private static AppConfig _selfObject;
	public static AppConfig Initiating()
	{
		if(_selfObject == null)
			_selfObject = new AppConfig();
		return _selfObject;
	}
	private AppConfig()
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
				
					_appExcMode =AppExecMode.valueOf(eElement.getAttribute(ATTMODE));
					_algMode = AlgorithmMode.valueOf(eElement.getAttribute(ATTALGMODE));
					if(_algMode == AlgorithmMode.Dynamic)
						_interval = Integer.parseInt(eElement.getAttribute(ATTINTERVAL));					
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
		if(!eElement.hasAttribute(ATTMODE) || !eElement.hasAttribute(ATTALGMODE))
		{
			PrintConsole.printErr(TAG + " must have an attribute. the following template may help you " +
					"\n	<AppConfiguration Mode=\"AllCombination, Single or ApartCombination  \"   AlgorithmMode= \" Dynamic or Static \" /> " );
			System.exit(0);
		}
		return true;
	}

}
