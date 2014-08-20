package setting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.PrintConsole;

public class TCConfig extends BaseConfiguration
{
	private static final  String TAG = "TranmissionConfiguration";
	private static final String ATTSTERATEGY = "Strategy";
	private static final String ATTDOUR = "downOverUpRatio";
	private static final String ATTPTO = "priotityToOrthogonal";
	private static final String ATTRLTRR = "repeatLinksToRespectRatio";
	private static final String ATTEBG = "enlargeByGateways";
	
	
	private boolean _ebg;
	
	public boolean isEBG()
	{
		return _ebg;
	}

	private boolean _rltrr;
	
	public boolean isRLTRR()
	{
		return _rltrr;
	}

	private boolean _pto;
	public boolean isPTO()
	{
		return _pto;
	}
	
	private int _dour;
	public int getDOUR()
	{
		return _dour;
	}
	private TCStrategy _sterategyName;
	public TCStrategy getSterategyName()
	{
		return _sterategyName;
	}
	
	private static TCConfig _selfObject;
	public static TCConfig Initiating()
	{
		if(_selfObject == null)
			_selfObject = new TCConfig();
		return _selfObject;
	}
	private TCConfig()
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
				
					_sterategyName = TCStrategy.valueOf(eElement.getAttribute(ATTSTERATEGY));
					if(_sterategyName == TCStrategy.PatternBased)
					{
						_dour = Integer.valueOf(eElement.getAttribute(ATTDOUR));
						_pto = Boolean.valueOf(eElement.getAttribute(ATTPTO));
						_rltrr = Boolean.valueOf(eElement.getAttribute(ATTRLTRR));
						_ebg = Boolean.valueOf(eElement.getAttribute(ATTEBG));
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
		if(!eElement.hasAttribute(ATTSTERATEGY))
		{
			PrintConsole.printErr(TAG + " must have an attribute. the following template may help you " +
					"\n	<TranmissionConfiguration Strategy=\" PatternBased or Original \" />>" );
			System.exit(0);
		}
		return true;
	}

}
