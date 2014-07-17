package launcher;

import java.io.File;
import java.lang.management.ManagementFactory;

import scheduling.RoundRobinSchedulingStrategy;
import scheduling.SchedulingStrategy;
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
		StartOptionsDialog startOptionDialog = new StartOptionsDialog();
		startOptionDialog.setVisible(true);
	}
	
	public static void launch() {
		SchedulingStrategy s = new RoundRobinSchedulingStrategy();
		s.scheduling();
		new GraphViewer(s.getThroughput());
	}

}
