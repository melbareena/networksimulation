package setting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class OutputConfig extends BaseConfiguration
{
	private static OutputConfig _selfObject;
	public static OutputConfig Initiating()
	{
		if(_selfObject == null)
			_selfObject = new OutputConfig();
		return _selfObject;
	}
	private OutputConfig()
	{
		this.FetchConfig();
	}
	
	private static final String TAG = "OutputFolder";
	private final static String ATTOUTPUT = "Path";
	private final static String ATTFILEASOUTPUT = "GenerateFileAsOutput";
	private final static String ATTINTERMIDIATE = "IntermidiateConsoleOutput";
	
	
	private String output;
	public String getOutput()
	{
		return output;
	}
	
	
	private boolean fileAsoutput;
	public boolean getFileAsoutput()
	{
		return fileAsoutput;
	}
	private boolean intermediateOutput;
	public boolean getIntermediateOutput()
	{
		return intermediateOutput;
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
				{
					output = eElement.getAttribute(ATTOUTPUT);
					fileAsoutput = Boolean.parseBoolean(eElement.getAttribute(ATTFILEASOUTPUT));
					intermediateOutput = Boolean.parseBoolean(eElement.getAttribute(ATTINTERMIDIATE));
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
		eElement.getAttributes();
		if(!eElement.hasAttribute(ATTOUTPUT))
			throw new Exception(ATTOUTPUT + " attribute in " + TAG + " tag is missing. the following template may help you." +
					"\n <OutputFolder Path=\"src/output/\" GenerateFileAsOutput=\"True\"  IntermidiateConsoleOutput=\"false\"  />");
		if(!eElement.hasAttribute(ATTFILEASOUTPUT))
			throw new Exception(ATTFILEASOUTPUT + " attribute in " + TAG + " tag is missing. the following template may help you." +
					"\n <OutputFolder Path=\"src/output/\" GenerateFileAsOutput=\"True\"  IntermidiateConsoleOutput=\"false\"  />");
		if(!eElement.hasAttribute(ATTINTERMIDIATE))
			throw new Exception(ATTINTERMIDIATE + " attribute in " + TAG + " tag is missing. the following template may help you." +
					"\n <OutputFolder Path=\"src/output/\" GenerateFileAsOutput=\"True\"  IntermidiateConsoleOutput=\"false\" />");
		return true;
	}
}
