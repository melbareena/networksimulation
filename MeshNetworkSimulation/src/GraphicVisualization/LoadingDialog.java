package GraphicVisualization;

import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import setting.ApplicationSettingFacade;
import setting.BaseConfiguration.AppExecMode;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import net.miginfocom.swing.MigLayout;

/**
 * @author Benjamin
 *
 */
public class LoadingDialog extends JDialog {

	private static final long serialVersionUID = 7227125764223986323L;

	private JPanel contentPanel = new JPanel();
	
	private List<JProgressBar> progressBarList;
	
	private List<JLabel> labelList;
	
	/**
	 * Creates the dialog.
	 */
	public LoadingDialog() {
		setTitle("Loading...");
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(500, 80);
		setUndecorated(false);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new MigLayout("", "[490px]", "[min!,grow][min!,grow]"));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		progressBarList = new ArrayList<JProgressBar>();
		labelList = new ArrayList<JLabel>();

		addBar();
		
		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}
	
	/**Updates the Layout of the contentPane, with 2*<code>nBars</code> rows.
	 * @param nBars The number of progress bars the panel will contain.
	 */
	private void updateLayout(int nBars) {
		StringBuilder rConstr = new StringBuilder();
		for(int i = 0; i < nBars; i++) {
			rConstr.append("[min!,grow][min!,grow]");
		}
		((MigLayout) contentPanel.getLayout()).setRowConstraints(rConstr.toString());
	}
	
	/**Adds a progress bar with its label to the dialog.
	 * @return The index of the progress bar added.
	 */
	public int addBar() {
		this.updateLayout(this.getBarCount()+1);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setIndeterminate(false);
		progressBar.setStringPainted(true);
		this.progressBarList.add(progressBar);
		final int barIndex = this.progressBarList.size()-1;
		progressBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(isDone(barIndex)) {
					if(isAllDone()) {
						//dispose();
						//setVisible(false);
					}
				}
			}
		});
		contentPanel.add(progressBar, "cell 0 "+(2*barIndex)+",grow");
		
		JLabel lblText = new JLabel();
		lblText.setFont(new Font("Tahoma", Font.ITALIC, 12));
		lblText.setHorizontalAlignment(SwingConstants.CENTER);
		this.labelList.add(lblText);
		final int labelIndex = this.labelList.size()-1;
		contentPanel.add(lblText, "cell 0 "+(2*labelIndex+1)+",grow");
		this.setLabel(barIndex, "Waiting...");
		
		pack();
		setLocationRelativeTo(null);
		
		return barIndex;
	}
	
	/**Removes the specified progress bar and its label from the dialog.
	 * @param barIndex The index of the bar to remove.
	 */
	public void removeBar(int barIndex) {
		System.err.println("Removing bar #"+barIndex);
		contentPanel.remove(this.progressBarList.get(barIndex));
		contentPanel.remove(this.labelList.get(barIndex));
		this.updateLayout(getBarCount()-1);
		this.progressBarList.remove(barIndex);
		this.labelList.remove(barIndex);
		pack();
		setLocationRelativeTo(null);
	}
	
	public int getBarCount() {
		return this.progressBarList.size();
	}
	
 	public void addProgress(int barIndex, int n) {
 		int value = this.getProgress(barIndex);
		if((value + n) <= 100) {
			this.setProgress(barIndex, value + n);
		}
	}
	
	public void addProgress(int barIndex, int n, String s) {
		this.addProgress(barIndex, n);
		this.setLabel(barIndex, s);
	}
	
	public void setProgress(int barIndex, int n) {
		if(n <= 100) {
			this.progressBarList.get(barIndex).setValue(n);
		}
	}
	
	public void setProgress(int barIndex, int n, String s) {
		this.setProgress(barIndex, n);
		this.setLabel(barIndex, s);
	}
	
	public void setLabel(int barIndex, String s) {
		this.labelList.get(barIndex).setText(this.getChannelsString(barIndex)+s);
	}
	
	public int getProgress(int barIndex) {
		return this.progressBarList.get(barIndex).getValue();
	}
	
	public boolean isAllDone() {
		for(int i = 0; i < this.getBarCount(); i++) {
			if(!isDone(i)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isDone(int barIndex) {
		return this.progressBarList.get(barIndex).getValue() == 100;
	}
	
	public void setIndeterminate(int barIndex, boolean indeterminate) {
		this.progressBarList.get(barIndex).setIndeterminate(indeterminate);
	}
	
	private String getChannelsString(int index) {
		String result = "";
		if (ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.Single) {
			result = ApplicationSettingFacade.Channel.getChannelMode().name();
		} else if(ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.AllCombination) {
			if(index != 11) {
				result = "1.." + (index+1);
			} else {
				result = "1, 6, 11";
			}
		} else {
			switch(index+1) {
			case 1:
				result = "1";
				break;
			case 2:
				result = "1, 11";
				break;
			case 3:
				result = "1, 6, 11";
				break;
			case 4:
				result = "1, 4, 7, 10";
				break;
			case 5:
				result = "1, 3, 5, 7, 9";
				break;
			case 6:
				result = "1, 3, 5, 7, 9, 11";
				break;
			case 7:
				result = "1, 2, 4, 5, 7, 8, 10";
				break;
			case 8:
				result = "1, 2, 4, 5, 7, 8, 10, 11";
				break;
			case 9:
				result = "1..3, 5..7, 9..11";
				break;
			case 10:
				result = "1..5, 7..11";
				break;
			case 11:
				result = "1..11";
				break;
			
			}
		}
		return "Channels "+result+": ";
	}

}
