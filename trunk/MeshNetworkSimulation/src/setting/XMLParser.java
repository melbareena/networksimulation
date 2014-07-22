package setting;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;

import common.PrintConsole;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

class XMLParser
{

	private static DocumentBuilder _documentBuilder;
	
	private static final String DEFAULTCONFIGFILE = "/setting/input/config.xml";
	
	public static String CONFIGFILE;
	
	public static Document Parser() {
		String path = (CONFIGFILE == null) ? DEFAULTCONFIGFILE : CONFIGFILE;
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
