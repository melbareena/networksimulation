package setting;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import GraphicVisualization.GraphViewer;

public class XMLWriter {
	
	private static Document doc;
	
	private static Element rootElement;
	
	public static void Initialize() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			doc = docBuilder.newDocument();
			rootElement = doc.createElement("AppConfiguration");
			doc.appendChild(rootElement);
		} catch (Exception e) {}
	}
	
	public static boolean write(File file) {
		try {		
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
			JOptionPane.showMessageDialog(null,
        			"The current configuration has been saved to: \n"+file.getAbsolutePath(),
    			    "Configuration successfully saved",            			    
    			    JOptionPane.INFORMATION_MESSAGE);
			return true;
		} catch (Exception e) {
			GraphViewer.showErrorDialog("Error while writing configuration",
					"Failed to write configuration file:"+
					"\n"+file.getAbsolutePath()+"\nExiting.");
		}
		System.exit(0);
		return false;
	}
	
	public static void writeEnvironment(int x, int y) {
		Element e = doc.createElement("Environment");
		rootElement.appendChild(e);
		e.setAttribute("X", x+"");
		e.setAttribute("Y", y+"");
	}
	
	public static void writeOutputFolder(String path, boolean generateFileAsOutput) {
		Element e = doc.createElement("OutputFolder");
		rootElement.appendChild(e);
		e.setAttribute("Path", path);
		e.setAttribute("GenerateFileAsOutput", generateFileAsOutput+"");
		e.setAttribute("IntermidiateConsoleOutput", "false");
	}
	
	public static void writeTraffic(boolean dynamic, String generator, String addrUp, String addrDown,
			long upseed, long downseed, double rate, long seed, int nodes, int ratio, long duration) {
		Element e = doc.createElement("Traffic");
		rootElement.appendChild(e);
		e.setAttribute("Type", (dynamic ? "Dynamic" : "Static"));
		e.setAttribute("Generator", generator);
		if(generator == "File") {
			e.setAttribute("AddressUp", addrUp);
			e.setAttribute("AddressDown", addrDown);
		} else {
			e.setAttribute("UpSeed", upseed+"");
			e.setAttribute("DownSeed", downseed+"");
		}
		if(dynamic) {
			e.setAttribute("Rate", rate+"");
			e.setAttribute("Seed", seed+"");
			e.setAttribute("NbOfNewEmittingNodes", nodes+"");
			e.setAttribute("Ratio", ratio+"");
			e.setAttribute("Duration", duration+"");
		}
	}
	
	public static void writeGateways(int radio, int number, String generator, String path, int[][] gateways) {
		Element e = doc.createElement("Gateways");
		rootElement.appendChild(e);
		e.setAttribute("Radio", radio+"");
		e.setAttribute("Number", number+"");
		e.setAttribute("TypeOfGeneration", generator);
		if(generator == "File") {
			e.setAttribute("Address", path);
		} else if(generator == "Static") {
			for(int i = 0; i < gateways.length; i++) {
				Element e1 = doc.createElement("Add");
				e1.setAttribute("x", gateways[i][0]+"");
				e1.setAttribute("y", gateways[i][1]+"");
				e.appendChild(e1);
			}
		}
	}
	
	public static void writeDatarates(HashMap<Double, Integer> datarates) {
		Element e = doc.createElement("DataRate");
		rootElement.appendChild(e);
		for(Double d : datarates.keySet()) {
			Element e1 = doc.createElement("Add");
			e1.setAttribute("SINR", d+"");
			e1.setAttribute("Rate", datarates.get(d)+"");
			e.appendChild(e1);
		}
	}
	
	public static void writeIFactor(double[] ifactor) {
		Element e = doc.createElement("IFactor");
		rootElement.appendChild(e);
		for(int i = 0; i < ifactor.length; i++) {
			Element e1 = doc.createElement("Add");
			e1.setAttribute("Key", i+"");
			e1.setAttribute("Value", ifactor[i]+"");
			e.appendChild(e1);
		}
	}
	
	public static void writeRouters(int radio, String generator, int minDistance,
			int transmissionRate, String path, int[][] routers, int number, int safetyTest,
			long seed) {
		Element e = doc.createElement("Routers");
		rootElement.appendChild(e);
		e.setAttribute("Radio", radio+"");
		e.setAttribute("MinDistance", minDistance+"");
		e.setAttribute("TransmissionRate", transmissionRate+"");
		e.setAttribute("TypeOfGeneration", generator);
		if(generator == "File") {
			e.setAttribute("Address", path);
		} else if(generator == "Static") {
			for(int i = 0; i < routers.length; i++) {
				Element e1 = doc.createElement("Add");
				e1.setAttribute("x", routers[i][0]+"");
				e1.setAttribute("y", routers[i][1]+"");
				e.appendChild(e1);
			}
		} else if(generator == "Random") {
			e.setAttribute("Number", number+"");
			e.setAttribute("SaftyTest", safetyTest+"");
			e.setAttribute("Seed", seed+"");
		}
	}
	
	public static void writeSINR(int alpha, int w, int power, double beta, double mu) {
		Element e = doc.createElement("SINR");
		rootElement.appendChild(e);
		e.setAttribute("Alpha", alpha+"");
		e.setAttribute("W", w+"");
		e.setAttribute("Power", power+"");
		e.setAttribute("Beta", beta+"");
		e.setAttribute("Mue", mu+"");
	}
	
	public static void writeChannels(String mode, HashSet<Integer> channels) {
		Element e = doc.createElement("Channels");
		rootElement.appendChild(e);
		e.setAttribute("Mode", mode+"");
		if(mode == "Partially") {
			for(int c : channels) {
				Element e1 = doc.createElement("Add");
				e1.setAttribute("Value", c+"");
				e.appendChild(e1);
			}
		}
	}
	
	public static void writeChannelAssignment(String strategy) {
		Element e = doc.createElement("ChannelAssignment");
		rootElement.appendChild(e);
		e.setAttribute("Strategy", strategy+"");
	}

}
