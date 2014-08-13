package GraphicVisualization;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


/**
 * @author Benjamin
 *
 */
public class ColorViewer extends JDialog {

	private static final long serialVersionUID = 1670390005809658314L;
	
	private static final int layoutSpace = 10;
	
	private final JPanel channelColorPanel = new JPanel();
	private final JPanel linkColorPanel = new JPanel();
	private final JPanel buttonPane = new JPanel();
	
	public static enum ColorType {Links, Channels};

	/** Creates the dialog.
	 */
	public ColorViewer(GraphViewer frame, ColorType ct) {
		super(frame, false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		//setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		
		channelColorPanel.setLayout(new BoxLayout(channelColorPanel, BoxLayout.Y_AXIS));
		channelColorPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		if(ct == ColorType.Channels) {
			getContentPane().add(channelColorPanel, BorderLayout.CENTER);
		}
		
		channelColorPanel.add(Box.createRigidArea(new Dimension(layoutSpace, layoutSpace)));
		
		for(int channel = 1; channel <= GraphViewer.MaxChannelNumber; channel++) {
			float hue = channel * (1.0F / GraphViewer.MaxChannelNumber);
			//Make the yellow colors darkers:
			float value = ((hue > 55F/360F) && (hue < 75F/360F)) ? 0.8F : 1.0F;
			Color color = Color.getHSBColor(hue, 1.0F, value);
			Color bColor = Color.getHSBColor(hue, 0.1F, 1.0F);
			JLabel label = new JLabel(" Chanel "+channel+" ");
			label.setOpaque(true);
			label.setBackground(bColor);
			label.setForeground(color);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			label.setBorder(BorderFactory.createLineBorder(color));
			channelColorPanel.add(label);
			channelColorPanel.add(Box.createRigidArea(new Dimension(layoutSpace, layoutSpace)));
		}
		
		linkColorPanel.setLayout(new BoxLayout(linkColorPanel, BoxLayout.Y_AXIS));
		linkColorPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		if(ct == ColorType.Links) {
			getContentPane().add(linkColorPanel, BorderLayout.CENTER);
		}
		
		linkColorPanel.add(Box.createRigidArea(new Dimension(layoutSpace, layoutSpace)));

		Color downlinkColor = new Color(255, 0, 0);
		Color downlinkBGColor = new Color(255, 234, 234);
		JLabel downlinkLabel = new JLabel(" Downlinks ");
		downlinkLabel.setOpaque(true);
		downlinkLabel.setBackground(downlinkBGColor);
		downlinkLabel.setForeground(downlinkColor);
		downlinkLabel.setHorizontalAlignment(JLabel.CENTER);
		downlinkLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		downlinkLabel.setBorder(BorderFactory.createLineBorder(downlinkColor));
		linkColorPanel.add(downlinkLabel);
		
		linkColorPanel.add(Box.createRigidArea(new Dimension(layoutSpace, layoutSpace)));
		
		Color uplinkColor = new Color(0, 255, 0);
		Color uplinkBGColor = new Color(234, 255, 241);
		JLabel uplinkLabel = new JLabel(" Uplinks ");
		uplinkLabel.setOpaque(true);
		uplinkLabel.setBackground(uplinkBGColor);
		uplinkLabel.setForeground(uplinkColor);
		uplinkLabel.setHorizontalAlignment(JLabel.CENTER);
		uplinkLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		uplinkLabel.setBorder(BorderFactory.createLineBorder(uplinkColor));
		linkColorPanel.add(uplinkLabel);
		
		linkColorPanel.add(Box.createRigidArea(new Dimension(layoutSpace, layoutSpace)));
	
		{
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton closeButton = new JButton("Close");
				closeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				buttonPane.add(closeButton);
				getRootPane().setDefaultButton(closeButton);
			}
		}
		setLocationRelativeTo(frame);
		pack();
	}
	
	
	/** Updates the dialog content given the type of the coloring 
	 * scheme currently used.
	 * @param ct The type of the coloring scheme currently used.
	 */
	public void updateContent(ColorType ct) {
		getContentPane().removeAll();
		if(ct == ColorType.Channels) {
			getContentPane().add(channelColorPanel, BorderLayout.CENTER);
		} else {
			getContentPane().add(linkColorPanel, BorderLayout.CENTER);
		}
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		getContentPane().validate();
		pack();
	}

}
