package setting;

import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class TrafficGenerator extends BaseConfiguration {
	
	private static TrafficGenerator _selfObject;

	// XML tag'name
	private static final String TAG = "Traffic";
	private static final String ATTTYPE = "Type";
	private static final String ATTRATE = "Rate";
	private static final String ATTDURATION = "Duration";
	private static final String ATTSEED = "Seed";
	private static final String ATTNODES = "NbOfNewEmittingNodes";
	private static final String ATTRATIO = "Ratio";
	private static final String ATTGEN = "Generator";
	private static final String ATTADDRESSUP = "AddressUp";
	private static final String ATTADDRESSDOWN = "AddressDown";
	private static final String ATTUPSEED = "UpSeed";
	private static final String ATTDOWNSEED = "DownSeed";
	
	private boolean dynamicType;
	
	private double trafficRate;
	
	private long duration;
	
	private long seed;
	
	private int numberOfNewEmittingNodes;
	
	private int ratio;
	
	private TypeOfGenerationEnum typeOfgeneration;
	
	private String addressUp;
	
	private String addressDown;
	
	private long upSeed;
	
	private long downSeed;
	
	private TrafficGenerator() {
		this.FetchConfig();
	}
	
	public static TrafficGenerator Initiating() {
		if(_selfObject == null)
			_selfObject = new TrafficGenerator();
		return _selfObject;
	}
	
	@Override
	protected void FetchConfig() {
		Document doc = XMLParser.Parser();	
		NodeList nodes =  doc.getElementsByTagName(TAG);
		Node routerNode = nodes.item(0);
		if(routerNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) routerNode;
			try {
				if(ValidateXMLDocument(eElement)) {
					dynamicType = (eElement.getAttribute(ATTTYPE).compareToIgnoreCase("Dynamic") == 0);
					if(dynamicType) {
						trafficRate = Double.parseDouble(eElement.getAttribute(ATTRATE));
						duration = Long.parseLong(eElement.getAttribute(ATTDURATION));
						seed = fetchSeed(eElement);
						ratio = Integer.parseInt(eElement.getAttribute(ATTRATIO));
						numberOfNewEmittingNodes = Integer.parseInt(eElement.getAttribute(ATTNODES));
					}
					typeOfgeneration = TypeOfGenerationEnum.valueOf(eElement.getAttribute(ATTGEN).toUpperCase());
					if(typeOfgeneration == TypeOfGenerationEnum.FILE  && ValidateXMLDocument(eElement, typeOfgeneration)) {
						FetchFromFile(eElement);
					}
					if(typeOfgeneration == TypeOfGenerationEnum.RANDOM) {
						upSeed =  fetchUpSeed(eElement);
						downSeed = fetchDownSeed(eElement);
					}
				}
			} 
			catch (Exception e) {
			    System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
	}
	
	private long fetchSeed(Element eElement) {
		if(eElement.hasAttribute(ATTSEED))
			return Long.parseLong(eElement.getAttribute(ATTSEED));
		return Math.abs(new Random().nextLong());
	}
	
	private long fetchUpSeed(Element eElement) {
		if(eElement.hasAttribute(ATTUPSEED))
			return Long.valueOf(eElement.getAttribute(ATTUPSEED));
		return Math.abs(new Random().nextLong());
	}
	
	private long fetchDownSeed(Element eElement) {
		if(eElement.hasAttribute(ATTDOWNSEED))
			return Long.valueOf(eElement.getAttribute(ATTDOWNSEED));
		return Math.abs(new Random().nextLong());
	}
	
	private void FetchFromFile(Element eElement) {
		addressDown = eElement.getAttribute(ATTADDRESSDOWN);
		addressUp = eElement.getAttribute(ATTADDRESSUP);
	}
	
	@Override
	protected boolean ValidateXMLDocument(Element eElement) throws Exception {
		if(!eElement.hasAttribute(ATTTYPE))
			throw new Exception(ATTTYPE + " attribute in " + TAG + " tag is missing. the following template may help you." +
						"\n <Traffic Type =\"Static\" Generator=\"File\" AddressUp=\"src/setting/input/trafficUp.txt\" AddressDown=\"src/setting/input/traffic.txt\" /> \n  or \n <Traffic Generator=\"Random\" />" );
		if(!eElement.hasAttribute(ATTGEN))
			throw new Exception(ATTGEN + " attribute in " + TAG + " tag is missing. the following template may help you." +
						"\n <Traffic Type =\"Static\" Generator=\"File\" AddressUp=\"src/setting/input/trafficUp.txt\" AddressDown=\"src/setting/input/traffic.txt\" /> \n  or \n <Traffic Generator=\"Random\" />" );
		return true;
	}
	
	protected boolean ValidateXMLDocument(Element eElement, TypeOfGenerationEnum g) throws Exception {
		if(g == TypeOfGenerationEnum.FILE) {
			if(!eElement.hasAttribute(ATTADDRESSDOWN))
				throw new Exception(ATTADDRESSDOWN + " attribute in " + TAG + " tag is missing. the following template may help you \n" +
						"<Traffic Type =\"Static\" Generator=\"File\" AddressUp=\"src/setting/input/trafficUp.txt\" AddressDown=\"src/setting/input/traffic.txt\"  /> \n " );
			if(!eElement.hasAttribute(ATTADDRESSUP))
				throw new Exception(ATTADDRESSUP + " attribute in " + TAG + " tag is missing. the following template may help you \n" +
						"<Traffic Type =\"Static\" Generator=\"File\"AddressUp=\"src/setting/input/trafficUp.txt\" AddressDown=\"src/setting/input/traffic.txt\"  /> \n " );
		}
		return true;
	}
	
	public TypeOfGenerationEnum getTypeOfgeneration() {
		return typeOfgeneration;
	}
	
	public String getAddressUp() {
			return addressUp;
	}
	
	public String getAddressDown() {
			return addressDown;
	}
	
	public long getUpSeed() {
		return upSeed;
	}
	
	public long getDownSeed() {
		return downSeed;
	}

	public boolean isDynamicType() {
		return dynamicType;
	}

	public double getTrafficRate() {
		return trafficRate;
	}
	
	public long getDuration() {
		return duration;
	}

	public long getSeed() {
		return seed;
	}

	public int getNumberOfNewEmittingNodes() {
		return numberOfNewEmittingNodes;
	}

	public int getRatio() {
		return ratio;
	}
}
