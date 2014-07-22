package setting;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import dataStructure.DataRate;


class DataRateConfig extends BaseConfiguration
{
	
	private static DataRateConfig self;
	public static DataRateConfig Initiating()
	{
		if(self == null)
			self = new DataRateConfig();
		return self;
	}
	
	private DataRateConfig()
	{
		FetchConfig();
	}

	private static final String TAG = "DataRate";
	private static final String SINRATT = "SINR";
	private static final String RATEATT = "Rate";
	private static final String CHILD = "Add";
	
	
	private List<DataRate> dataRates;

	public List<DataRate> getDataRates()
	{
		return dataRates;
	}

	@Override
	protected void FetchConfig()
	{
		Document doc = XMLParser.Parser();	
		NodeList nodes =  doc.getElementsByTagName(TAG);
		Node dataRateNode = nodes.item(0);
		if(dataRateNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element eElement = (Element) dataRateNode;
			
			try
			{
				if(ValidateXMLDocument(eElement))
				{
					NodeList childNodes = eElement.getElementsByTagName(CHILD);
					dataRates = new ArrayList<>();
					for (int counter = 0; counter < childNodes.getLength(); counter++)
					{
						Node childNode = childNodes.item(counter);
						if (childNode.getNodeType() == Node.ELEMENT_NODE)
						{
							Element childElem = (Element) childNode;
							int r = Integer.valueOf(childElem.getAttribute(RATEATT));
							float d = Float.valueOf(childElem.getAttribute(SINRATT));
							DataRate drate = new DataRate( r ,  d);
							dataRates.add( drate );
							 
						}
					}
					
				}
			}
			catch(Exception ex)
			{
				
			}
		}
		
	}

	@Override
	protected boolean ValidateXMLDocument(Element eElement) throws Exception
	{
		
		
		if(!eElement.hasChildNodes())
			throw new Exception(TAG + " must have child node. the following template may help you " +
					"\n <DataRate> \n "+
					"\t <Add SINR=\"8.15\" Rate=\"6\" /> \n" +
					"</DataRate>" );
		
		
		
		return true;
	}

}
