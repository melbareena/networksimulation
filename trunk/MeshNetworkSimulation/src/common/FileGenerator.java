package common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import launcher.Program;
import dataStructure.Channel;
import dataStructure.ChannelOccuranceMap;
import dataStructure.Link;
import dataStructure.LinkTrafficMap;
import dataStructure.LinksAmbienNoiseMap;
import dataStructure.LinksChannelMap;
import dataStructure.Path;
import dataStructure.PathMap;
import dataStructure.TCUnit;
import dataStructure.UplinkTraffic;
import dataStructure.Vertex;
import setting.ApplicationSettingFacade;
import setting.BaseConfiguration.AppExecMode;


/**
 * @author Mahdi Negahi
 *
 */
public class FileGenerator
{
	private static final String FILEOUTPUTPATH  = ApplicationSettingFacade.AppOutput.getOutputFolder();
	private static final boolean ISFILEENABLE = ApplicationSettingFacade.AppOutput.isGenerateFileAsOutput();
	public static void NodesInFile( Map<Integer, Vertex> nodes)
	{
		if(!ISFILEENABLE) return;
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "node_set.txt"));
			for(Entry<Integer,Vertex> entry: nodes.entrySet())
			{
				writer.write(entry.getValue().toString());
				writer.newLine();
			}
			writer.close();
			PrintConsole.print("Nodes added in a text file successfully.");
		}
		catch(Exception ex)
		{
			System.err.println("NodesInFile/FileGenerator/Message:" + ex.getMessage());
		}
	}
	
	
	public static void NeighborInFile(Map<Vertex, Vector<Vertex>> neighborSet)
	{
		if(!ISFILEENABLE) return;
		try
		{
			BufferedWriter writer2 = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "nodesNeighbor.txt"));
			String out  = "";
			for(Entry<Vertex, Vector<Vertex>> entry: neighborSet.entrySet())
			{
				out  = "";
				out  = entry.getKey().getId() + " ";
				for (Vertex nighbors : entry.getValue())
				{
					out += nighbors.getId() + " ";
				}
				writer2.write(out.trim());
				writer2.newLine();
			}
			writer2.close();
			PrintConsole.print("Neighbors file is generated successfully (Mahdi version).");
		}
		catch(Exception ex)
		{
			System.err.println("NeighborInFile/FileGenerator/Message:" + ex.getMessage());
		}
		
	}


	public static void LinksInFile(List<Link> edges)
	{
		if(!ISFILEENABLE) return;
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "all_Links.txt"));
			String out= "";
			for(Link e : edges)
			{
				out= "";
				out = e.getSource().getId() + " " + e.getDestination().getId() + " " + e.getId();
				writer.write(out);
				writer.newLine();
			}
			writer.close();
			System.out.println("Links are added in file successfully.");
		}
		catch(Exception ex)
		{
			System.err.println("EdgesInFile/FileGenerator/Message:" + ex.getMessage());
		}
		
	}
	
	public static void optimalLinksInFile(List<Link> edges)
	{
		if(!ISFILEENABLE) return;
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "optimal_Links.txt"));
			String out= "";
			for(Link e : edges)
			{
				out= "";
				out = e.getSource().getId() + " " + e.getDestination().getId() + " " + e.getId();
				writer.write(out);
				writer.newLine();
			}
			writer.close();
			PrintConsole.print("Links which are participating in eaither uplink or downlink paths are added in file successfully.");
		}
		catch(Exception ex)
		{
			System.err.println("optimalLinksInFile/FileGenerator/Message:" + ex.getMessage());
		}
		
	}


	public static void allShortestPathsInFile(PathMap paths, boolean isDownlink)
	{
		String fileName = "all_downlink_Paths";
		if(!isDownlink)
			fileName = "all_uplink_Paths";
		if(!ISFILEENABLE) return;
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + fileName + ".txt" ));
			String out= "";
			for(Entry<Vertex,List<Path>> allPath : paths.entrySet())
			{
				
				for(Path p : allPath.getValue())
				{
					out = "";
					for (Link edge : p.getEdgePath())
					{
						out += edge.getId() + " ";
					}
					writer.write(out.trim());
					writer.newLine();
				}
				
				
			}
			writer.close();
			PrintConsole.print("Edges in shortest paths for each node are added in file successfully.");
		}
		catch(Exception ex)
		{
			System.err.println("allShortestPathsInFile/FileGenerator/Message:" + ex.getMessage());
		}
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter( FILEOUTPUTPATH + fileName + "(Nodes).txt"));
			String out= "";
			for(Entry<Vertex,List<Path>> allPath : paths.entrySet())
			{
				
				for(Path p : allPath.getValue())
				{
					out = "";
					for (Vertex vertex : p.getNodePath())
					{
						out += vertex.getId() + " ";
					}
					writer.write(out.trim());
					writer.newLine();
				}
				
				
			}
			writer.close();
			PrintConsole.print("Nodes in shortest paths for each path are added in file successfully.");
		}
		catch(Exception ex)
		{
			System.err.println("allShortestPathsInFile/FileGenerator/Message:" + ex.getMessage());
		}	
	}


	public static void UplinkTrafficInFile(UplinkTraffic uplink)
	{
		if(!ISFILEENABLE) return;
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "uplink_traffic.txt"));
			for(Entry<Vertex, Integer> traffic : uplink.getUplinkTraffic().entrySet())
			{
				writer.write(traffic.getKey().getId() + " " + traffic.getValue());
				writer.newLine();
			}

			writer.close();
			PrintConsole.print("Uplink traffic added in a file sucessfully.");
		} 		
		catch(Exception ex)
		{
			System.err.println("UplinkTrafficInFile/FileGenerator/Message:" + ex.getMessage());
		}	
		
	}
	public static void dowlinkTrafficInFile(Map<Vertex,TreeMap<Vertex,Float>> downlink)
	{
		if(!ISFILEENABLE) return;
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "downlink_traffic.txt"));
			for (Entry<Vertex, TreeMap<Vertex, Float>> grMap : downlink.entrySet())
			{
				//TreeMap<Vertex, Float> routerMap = 
				
				for (Entry<Vertex, Float> routerMap : grMap.getValue().entrySet())
				{
					writer.write(routerMap.getKey().getId() + " " + routerMap.getValue());
					writer.newLine();
				}
				
			}
			writer.close();
			PrintConsole.print("Downlink traffic added in a file sucessfully.");
		} 
		catch(Exception ex)
		{
			System.err.println("dowlinkTrafficInFile/FileGenerator/Message:" + ex.getMessage());
		}	
		
	}


	public static void OptimalUplinkPath(PathMap resultMap)
	{
		if(!ISFILEENABLE) return;
		try
		{
			BufferedWriter writerLinksID = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "optimalUplinkPath(LinksID).txt" ));
			BufferedWriter writerNodesID = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "optimalUplinkPath(NodesID).txt" ));
			String outLinks = "";
			String outNodes = "";
			for(Entry<Vertex,List<Path>> allPath : resultMap.entrySet())
			{
				
				for(Path p : allPath.getValue())
				{
					outLinks  = "";
					outNodes = "";
					for (Link edge : p.getEdgePath())
					{
						outLinks  += edge.getId() + " ";
					}
					for (Vertex node : p.getNodePath())
					{
						outNodes  += node.getId() + " ";
					}
					writerLinksID.write(outLinks .trim());
					writerLinksID.newLine();
					writerNodesID.write(outNodes .trim());
					writerNodesID.newLine();
				}
				
				
			}
			writerLinksID.close();
			writerNodesID.close();
			PrintConsole.print("optimal uplink path shortest paths for each node are added in file successfully.");
		}
		catch(Exception ex)
		{
			System.err.println("OptimalUplinkPath/FileGenerator/Message:" + ex.getMessage());
		}	
		
	}


	public static void TrafficOfLinksInFile(LinkTrafficMap traffic_l)
	{
		if(!ISFILEENABLE) return;	
		try
		{		
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "link_weights.txt"));
			for (Entry<Link, Float> links : traffic_l.Sort().entrySet())
			{
				writer.write(links.getKey().getId() + " " + links.getValue());
				writer.newLine();
				
			}	
			writer.close();
			PrintConsole.print("Traffic estimation added in a file sucessfully.");
		} catch (Exception ex)
		{
			System.err.println("TrafficOfLinksInFile/FileGenerator/Message:" + ex.getMessage());
		}
		
	}


	public static void LinksAmbienNoiseInFile(LinksAmbienNoiseMap noiseSet)
	{
		if(!ISFILEENABLE) return;
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "ambientNoise_EachLink.txt"));
			for(Entry<Link, Double> noise : noiseSet.entrySet())
			{
				writer.write(noise.getKey().getId() + " " + noise.getValue());
				writer.newLine();
			}

			writer.close();
			PrintConsole.print("Ambien noise of each link added in a file sucessfully.");
		} 
		catch (Exception ex)
		{
			System.err.println("LinksAmbienNoiseInFile/FileGenerator/Message:" + ex.getMessage());
		}
		
	}


	public static void ChannelsInFile(LinksChannelMap linksChannel)
	{
		if(!ISFILEENABLE) return;
		if(ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.Single)
		{
			try
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "channels_assignment.txt"));
				for(Entry<Link, Channel> LC : linksChannel.entrySet())
				{
					writer.write(LC.getKey().getId() + " " + LC.getValue().getChannel());
					writer.newLine();
				}
	
				writer.close();
				PrintConsole.print("Channel for each link added in a file sucessfully.");
			} 
			catch (Exception ex)
			{
				System.err.println("ChannelsInFile/FileGenerator/Message:" + ex.getMessage());
			}
		}
		else
		{
			try
			{
				String fileName = "channels_assignment "+ Program.multiExecIndex +".txt";
				BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + fileName));
				for(Entry<Link, Channel> LC : linksChannel.entrySet())
				{
					writer.write(LC.getKey().getId() + " " + LC.getValue().getChannel());
					writer.newLine();
				}
	
				writer.close();
				PrintConsole.print("Channel for each link added in a file sucessfully.");
			} 
			catch (Exception ex)
			{
				System.err.println("ChannelsInFile/FileGenerator/Message:" + ex.getMessage());
			}
		}
		
		
	}


	public static void TransmissionConfige(List<TCUnit> tT)
	{
		if(!ISFILEENABLE) return;
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "transmission_configuration.txt"));
			for(TCUnit tcu : tT)
			{
				for (Entry<Link, Integer> linkDataRate : tcu.entrySet() )
					writer.write(linkDataRate.getKey().getId() + " ");
				writer.newLine();
			}

			writer.close();
			PrintConsole.print("Transmission Configuration inserted in file successfully.");
		} 
		catch (Exception ex)
		{
			System.err.println("TransmissionConfigStepOne/FileGenerator/Message:" + ex.getMessage());
		}
		
	}


	public static void Throughput(Vector<Double> throughput)
	{
		if(!ISFILEENABLE) return;
		
		if(ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.Single)
		{
			try
			{
				
				String path = FILEOUTPUTPATH;
				if(FILEOUTPUTPATH.equals("null")) {
					path = FileGenerator.class.getResource("/output/").getPath();
				}
				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "throughtput.txt"));
				//int timeIndex = 0;
				for (Double th : throughput)
				{
					writer.write(th +"");
					//timeIndex++;
					writer.newLine();
				}
				writer.close();
			
				PrintConsole.print("throughput inserted in file successfully.");
			} 
			catch (Exception ex)
			{
				System.err.println("TransmissionConfigStepOne/FileGenerator/Message:" + ex.getMessage());
			}
		}
		else
		{
			try
			{
				String fileName = "throughtput "+ Program.multiExecIndex +".txt";
				String path = FILEOUTPUTPATH;
				if(FILEOUTPUTPATH.equals("null")) {
					path = FileGenerator.class.getResource("/output/").getPath();
				}
				BufferedWriter writer = new BufferedWriter(new FileWriter(path + fileName));
				//int timeIndex = 0;
				for (Double th : throughput)
				{
					writer.write(th +"");
					//timeIndex++;
					writer.newLine();
				}
				writer.close();
			
				PrintConsole.print("throughput inserted in file successfully.");
			} 
			catch (Exception ex)
			{
				System.err.println("TransmissionConfigStepOne/FileGenerator/Message:" + ex.getMessage());
			}
		}
		
	}


	public static void DataRate(List<TCUnit> tT)
	{
		if(!ISFILEENABLE) return;
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "dataRate.txt"));
			BufferedWriter writer2 = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "dataRate2.txt"));
			int totalDataRate  = 0;
			int counter  = 0;
			for(TCUnit tcu : tT)
			{
				totalDataRate = 0;
				counter  = 0;
				
				for (Entry<Link, Integer> linkDataRate : tcu.entrySet() )
				{
					totalDataRate += linkDataRate.getValue();
					counter++;
					writer.write(linkDataRate.getValue()+ " ");
				}
				writer2.write(counter  + " " + totalDataRate);
				writer2.newLine();
				writer.newLine();
			}

			writer.close();
			writer2.close();
			PrintConsole.print("Data rate  inserted in file successfully.");
		} 
		catch (Exception ex)
		{
			System.err.println("Data rate/FileGenerator/Message:" + ex.getMessage());
		}
		
	}


	public static void ChannelOccurance(ChannelOccuranceMap channelOccurrence)
	{
		if(!ISFILEENABLE) return;
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "channels_ChannelOccurance.txt"));
			for(Entry<Channel, Integer> LC : channelOccurrence.entrySet())
			{
				writer.write(LC.getValue()+"");
				writer.newLine();
			}

			writer.close();
			PrintConsole.print("Channel occurrence for each link added in a file sucessfully.");
		} 
		catch (Exception ex)
		{
			System.err.println("ChannelOccurance/FileGenerator/Message:" + ex.getMessage());
		}
		
	}


	public static void optimalDownlink(PathMap paths)
	{
		if(!ISFILEENABLE) return;
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "optimalDownlinkPaths(LinkID).txt" ));
			String out= "";
			for(Entry<Vertex,List<Path>> allPath : paths.entrySet())
			{
				
				for(Path p : allPath.getValue())
				{
					out = "";
					for (Link edge : p.getEdgePath())
					{
						out += edge.getId() + " ";
					}
					writer.write(out.trim());
					writer.newLine();
				}
				
				
			}
			writer.close();
			PrintConsole.print("Optimal downlink paths added to file successfully.");
		}
		catch(Exception ex)
		{
			System.err.println("optimalDownlink/FileGenerator/Message:" + ex.getMessage());
		}
		
	}


	public static void TCThroughput(List<TCUnit> tT)
	{
		if(!ISFILEENABLE) return;
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "transmission_configuration_throughput.txt"));
			for(TCUnit tcu : tT)
			{
				//for (Entry<Link, Integer> linkDataRate : tcu.entrySet() )
					//writer.write(linkDataRate.getKey().getId() + " ");
				writer.write(tcu.getThroughput()+"");
				writer.newLine();
			}

			writer.close();
			PrintConsole.print("Throughput for each Transmission Configuration inserted in file successfully.");
		} 
		catch (Exception ex)
		{
			System.err.println("TCThroughput/FileGenerator/Message:" + ex.getMessage());
		}
		
	}


	public static void Affectance(Map<Channel, Double> channelAffectSet,
			Link currentLink)
	{
		if(!ISFILEENABLE) return;
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "/affectance/" +currentLink.getId() + ".txt"));
			for (Entry<Channel, Double> chAffectance : channelAffectSet.entrySet())
			{
				writer.write(chAffectance.getKey().getChannel() + ": " + chAffectance.getValue());
				writer.newLine();
			}

			writer.close();
			//PrintConsole.printErr("Affectance for links inserted in file successfully.");
		} 
		catch (Exception ex)
		{
			//System.err.println("Affectance/FileGenerator/Message:" + ex.getMessage());
		}
		
		
	}


	



}
