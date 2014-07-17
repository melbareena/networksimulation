package setting;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;

import common.PrintConsole;

import java.io.File;

class XMLParser
{

	private static DocumentBuilder _documentBuilder;
	
	
	private static final String CONFIGFILE = "src/setting/input/config.xml";
	
	public static Document Parser()
	{
			
			try
			{
				File fXmlFile = new File(CONFIGFILE);
				if(_documentBuilder == null)
				{
					
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					_documentBuilder = dbFactory.newDocumentBuilder();
					return _documentBuilder.parse(fXmlFile);
				}
				else
				{
					return _documentBuilder.parse(fXmlFile);
				}
			}
			catch(Exception ex)
			{
				PrintConsole.printErr("XMLParser/Parser message:" + ex.getMessage());
				return null;
			}	
	}

}
