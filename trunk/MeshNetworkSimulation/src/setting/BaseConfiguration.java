package setting;

import org.w3c.dom.Element;




/**
 * It is base class for all config classes 
 * @author Mahdi 
 *
 */
public abstract class BaseConfiguration
{
	
	public static enum TypeOfGenerationEnum { STATIC , FILE, RANDOM };
	public static enum ChannelMode { All, Partially };
	public static enum AppExecMode { Single, AllCombination, ApartCombination };
	public static enum TCStrategy { PatternBased, Original};
	
	
	protected abstract void FetchConfig();
	
	/**
	 * validate a specific XML tag.
	 */
	protected abstract boolean ValidateXMLDocument(Element eElement) throws Exception;
}
