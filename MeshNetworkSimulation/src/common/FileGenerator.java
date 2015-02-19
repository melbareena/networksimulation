package common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import luncher.Luncher;
import dataStructure.Channel;
import dataStructure.ChannelOccuranceMap;
import dataStructure.Link;
import dataStructure.LinkTrafficMap;
import dataStructure.LinksAmbienNoiseMap;
import dataStructure.LinksChannelMap;
import dataStructure.Path;
import dataStructure.PathMap;
import dataStructure.SchedulingResult;
import dataStructure.TCUnit;
import dataStructure.Traffic;
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
		if(!ISFILEENABLE) return;
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
			for(Entry<Vertex, Double> traffic : uplink.getTraffic().entrySet())
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
	public static void dowlinkTrafficInFile(Map<Vertex,TreeMap<Vertex,Double>> downlink)
	{
		if(!ISFILEENABLE) return;
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "downlink_traffic.txt"));
			for (Entry<Vertex, TreeMap<Vertex, Double>> grMap : downlink.entrySet())
			{
				//TreeMap<Vertex, Float> routerMap = 
				
				for (Entry<Vertex, Double> routerMap : grMap.getValue().entrySet())
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
			for (Entry<Link, Double> links : traffic_l.Sort().entrySet())
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
				String fileName = "channels_assignment "+ Luncher.multiExecIndex +".txt";
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
				for (Entry<Link, Double> linkDataRate : tcu.entrySetRate() )
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

	private static Vector<Double> toMegaBits(Vector<Double> bits)
	{
	
			
			Vector<Double> mbps = new Vector<Double>();
			
			mbps.add(0d);
			
			int slotCounter = 0;
			double throughputAccumulation = 0;
			for (Double slotT : bits)
			{
				throughputAccumulation += slotT;
				slotCounter++;
				if(slotCounter == 50)
				{
					mbps.add(throughputAccumulation);
					slotCounter = 0;
					throughputAccumulation = 0;
				}
			}
			return mbps;
	}
	public static void Throughput(Vector<Double> throughput)
	{
		//if(!ISFILEENABLE) return;
		
		if(ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.Single)
		{
			try
			{
				
				String path = FILEOUTPUTPATH;
				if(FILEOUTPUTPATH.equals("null")) {
					path = FileGenerator.class.getResource("/output/").getPath();
				}
				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "throughtput_timeslot.txt"));
				
				for (Double th : throughput)
				{
				
						writer.write(th +"");
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
				String fileName = "throughtput_timeslot_ "+ Luncher.multiExecIndex +".txt";
				String path = FILEOUTPUTPATH;
				if(FILEOUTPUTPATH.equals("null")) {
					path = FileGenerator.class.getResource("/output/").getPath();
				}
				BufferedWriter writer = new BufferedWriter(new FileWriter(path + fileName));
				for (Double th : toMegaBits(throughput))
				{		
						writer.write(th +"");
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
				
				for (Entry<Link, Double> linkDataRate : tcu.entrySetRate() )
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
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "optimalDownlinkPaths(NodeID).txt" ));
			String out= "";
			for(Entry<Vertex,List<Path>> allPath : paths.entrySet())
			{
				writer.write("Gateway: #" + allPath.getKey().getId());
				writer.newLine();
				for(Path p : allPath.getValue())
				{
					out= "\t";
					for (Vertex n : p.getNodePath())
					{
						out += n.getId() + " ";
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


	public static void seceduleResult(SchedulingResult[] results)
	{
		
			

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		StringBuilder throughputBuilder = new StringBuilder();
		StringBuilder delayBuilder = new StringBuilder();
		for (int i = results.length - 1 ; i >= 0 ; i--)
		{
			SchedulingResult sr = results[i];
			throughputBuilder.append(df.format(sr.getAverageThorughput())  );
			throughputBuilder.append(System.lineSeparator());
			delayBuilder.append(((double)sr.getDelay()));
			delayBuilder.append(System.lineSeparator());
			
		}
			try
			{
			
				BufferedWriter writer = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "results.txt"));
				
				writer.write(throughputBuilder.toString());
				writer.newLine();
				writer.newLine();
				writer.write(delayBuilder.toString());
				writer.close();
			
		} 
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
			
	}


	public static void SchedulingResult(SchedulingResult results)
	{
		
		
		try
		{
			BufferedWriter THwriter = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "schedulingResult_throughput_mbps.txt" ));
			
			for(Double throughput : results.getThroughputData())
			{
				THwriter.write(throughput + "");
				THwriter.newLine();
			}
			THwriter.close();
			
			BufferedWriter Swriter = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "schedulingResult_SourceBuffer_mbps.txt" ));
			for(Double SBuffer : results.getSourceData())
			{
				Swriter.write(SBuffer + "");
				Swriter.newLine();
			}
			Swriter.close();
			
			BufferedWriter Twriter = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "schedulingResult_TransmitBuffer_mbps.txt" ));
			for(Double TBuffer : results.getTransmitData())
			{
				Twriter.write(TBuffer + "");
				Twriter.newLine();
			}
			Twriter.close();
			
			PrintConsole.print("Schdeuling Result In File..........................");
		}
		catch(Exception ex)
		{
			System.err.println("SchedulingResult/FileGenerator/Message:" + ex.getMessage());
		}
		
	}


	public static void dynamicTraffic(Map<Integer, Traffic> _dynamicTraffic)
	{
		if(!ISFILEENABLE) return;
		try
		{
			BufferedWriter THwriter = new BufferedWriter(new FileWriter(FILEOUTPUTPATH + "dynamicTraffic.txt" ));
			
			for(Entry<Integer, Traffic> dt : _dynamicTraffic.entrySet())
			{
				THwriter.write("Time slot:" + dt.getKey());
				THwriter.newLine();
				THwriter.write("\t Downlink:");
				THwriter.newLine();
				for (Entry<Vertex, TreeMap<Vertex, Double>> downlinkT : dt.getValue().getDownlinkTraffic().getTraffic().entrySet())
				{
					THwriter.write("\t\t Gateway: #" + downlinkT.getKey().getId());
					THwriter.newLine();
					for (Entry<Vertex, Double> rT : downlinkT.getValue().entrySet())
					{
						THwriter.write("\t\t\t Router: #" + rT.getKey().getId() + " t: " + rT.getValue());
						THwriter.newLine();
					}
					THwriter.write("--------------------------------------------------------------------------------------------------");
					THwriter.newLine();
				} 
				THwriter.write("___________________________________________________________________________________________________________");
				THwriter.newLine();
				THwriter.newLine();
				THwriter.write("\t Uplink:");
				THwriter.newLine();
				for (Entry<Vertex, Double> uplinkT : dt.getValue().getUplinkTraffic().getTraffic().entrySet())
				{
					THwriter.write("\t\t Routers: #" + uplinkT.getKey().getId() + " t: " + uplinkT.getValue());
					THwriter.newLine();
				}
				THwriter.write("*************************************************************************************************************");
				THwriter.newLine();
			}
			
			
			
			
			THwriter.close();
			
			
			PrintConsole.print("Dynamic Traffic In File..........................");
		}
		catch(Exception ex)
		{
			System.err.println("SchedulingResult/FileGenerator/Message:" + ex.getMessage());
		}
		
	}


}
