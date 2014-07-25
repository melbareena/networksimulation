package launcher;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Random;

import javax.swing.UIManager;

import scheduling.RoundRobinSchedulingStrategy;
import scheduling.SchedulingStrategy;
import trafficGenerator.DynamicTrafficGenerator;
import GraphicVisualization.GraphViewer;
import GraphicVisualization.StartOptionsDialog;

public class Program {
	
	
	public static void restartApplication() {
		try {
			StringBuilder cmd = new StringBuilder();
	        cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
	        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
	            cmd.append(jvmArg + " ");
	        }
	        cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
	        cmd.append(Program.class.getName()).append(" ");
	        /*for (String arg : args) {
	            cmd.append(arg).append(" ");
	        }*/
	        Runtime.getRuntime().exec(cmd.toString());
	        System.exit(0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Unable to set the UI look and feel...");
		}	
		StartOptionsDialog startOptionDialog = new StartOptionsDialog();
		startOptionDialog.setVisible(true);
	}
	
	public static void launch(boolean dynamic) {
		DynamicTrafficGenerator dtg = new DynamicTrafficGenerator(0.2, new Random().nextLong(),
				45, 1);
		SchedulingStrategy s = new RoundRobinSchedulingStrategy(dtg);
		
		if(dynamic) {
			s.dynamicScheduling(500000);
		} else {
			s.scheduling();
		}

		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Unable to set the UI look and feel...");
		}	
		
		new GraphViewer(s.getThroughput(), s.getTrafficSource(), s.getTrafficTransit());
	}

}
