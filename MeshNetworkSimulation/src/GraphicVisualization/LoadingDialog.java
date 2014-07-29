package GraphicVisualization;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;

public class LoadingDialog extends JDialog {

	private static final long serialVersionUID = 7227125764223986323L;

	private JPanel contentPanel = new JPanel();
	
	private JProgressBar progressBar;
	
	private JLabel lblText;
	
	/**
	 * Create the dialog.
	 */
	public LoadingDialog(Frame parent, String title, boolean indeterminate) {
		setTitle("Loading "+title+"...");
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(500, 80);
		setUndecorated(true);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setIndeterminate(indeterminate);
		progressBar.setStringPainted(!indeterminate);
		progressBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(isDone()) {
					System.out.println("Progress done...");
					dispose();
					setVisible(false);
				}
			}
		});
		contentPanel.add(progressBar, BorderLayout.CENTER);
		
		lblText = new JLabel("Loading...");
		lblText.setFont(new Font("Tahoma", Font.ITALIC, 12));
		lblText.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblText, BorderLayout.SOUTH);
		
		setAlwaysOnTop(true);
		setLocationRelativeTo(parent);
	}
	
	public void addProgress(int n) {
		if((this.progressBar.getValue()+n) <= 100) {
			this.progressBar.setValue(this.progressBar.getValue()+n);
		}
	}
	
	public void addProgress(int n, String s) {
		addProgress(n);
		setLabel(s);
	}
	
	public void setProgress(int n) {
		this.progressBar.setValue(n);
	}
	
	public void setLabel(String s) {
		this.lblText.setText(s);
	}
	
	public int getProgress() {
		return this.progressBar.getValue();
	}
	
	public boolean isDone() {
		return this.progressBar.getValue() == 100;
	}
	
	public void setIndeterminate(boolean indeterminate) {
		progressBar.setIndeterminate(indeterminate);
		progressBar.setStringPainted(!indeterminate);
	}

}
