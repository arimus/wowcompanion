/*
 * Created on Jan 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.wowc;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import net.sf.wowc.WoWConfig;
import net.sf.wowc.WoWConfigException;
import net.sf.wowc.WoWConfigPropertyNotFoundException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AboutDialog extends JDialog {
	private static Logger log = LogManager.getLogger(AboutDialog.class);
	private JTextArea textArea;

	public static void main(String args[]) {
/*		try {
			AboutDialog dialog = new AboutDialog();
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	public AboutDialog(Frame frame) {
		super(frame);
		JLabel imageLabel;
		GridBagConstraints gridBagConstraints;
		JButton closeButton;
		getContentPane().setLayout(new GridBagLayout());
		setTitle("About");
		setBounds(100, 100, 242, 158);

		imageLabel = new JLabel();
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 5, 0, 10);
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		imageLabel.setText("");
		imageLabel.setMinimumSize(new Dimension(50, 18));
		imageLabel.setPreferredSize(new Dimension(50, 18));
		imageLabel.setIcon(createImageIcon("/wowc.gif", "WoWC"));
		getContentPane().add(imageLabel, gridBagConstraints);

		textArea = new JTextArea();
		textArea.setOpaque(false);
		textArea.setColumns(30);
		textArea.setRows(4);
		textArea.setTabSize(4);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 0, 10, 5);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;
		getContentPane().add(textArea, gridBagConstraints);
		Font font = new Font("Arial", Font.PLAIN, 10);
		textArea.setFont(new Font("Arial", Font.PLAIN, 10));

		String about = "Created by arimus & ulic\nCouncil of Thoyr, Lightbringer\n\n(C) Copyright David Castro, 2005\nAll rights reserved";
		try {
			about = new WoWConfig().getProperty("about.info");
		} catch (WoWConfigException e) {
			// do nothing
		} catch (WoWConfigPropertyNotFoundException e) {
			// do nothing
		}
		textArea.setText(about);
		textArea.setEditable(false);

		closeButton = new JButton();
		closeButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
				setVisible(false);
			}
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 1;
		getContentPane().add(closeButton, gridBagConstraints);
		closeButton.setText("Close");
		//
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path,
	                                           String description) {
	    java.net.URL imgURL = AboutDialog.class.getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        log.error("AboutDialog: couldn't find file: " + path);
	        return null;
	    }
	}
}
