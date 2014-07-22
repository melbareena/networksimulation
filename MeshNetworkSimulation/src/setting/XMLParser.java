package setting;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import common.PrintConsole;

public class XMLParser
{

	private static DocumentBuilder _documentBuilder;
	
	private static final String DEFAULTCONFIGFILE = "/setting/input/config.xml";
	
	public static String CONFIGFILE;
	
	public static Document Parser() {
		String path = (XMLParser.CONFIGFILE == null) ? DEFAULTCONFIGFILE : CONFIGFILE;
		return Parser(path);
	}
	
	public static Document Parser(String path)
	{
			
			try
			{
				InputStream fXmlFile = XMLParser.class.getResourceAsStream(path);
				if(fXmlFile == null) {
					fXmlFile = new FileInputStream(path);
				}
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
