/*
 * Created on Jan 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.wowc;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConfigureServerDialog extends JDialog {
	private static Logger log = LogManager.getLogger(ConfigureServerDialog.class); 
	private JTextField updateURLField;
	private final JTextField serverURLField;
	private final JLabel serverURLLabel;
	private GridBagConstraints gridBagConstraints;
	private static JDialog dialog = null;
	private static WoWConfig config = null;
	
	public static void main(String args[]) {
/*		try {
			CreateAccountDialog dialog = new CreateAccountDialog();
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

	public ConfigureServerDialog(Frame frame) {
		super(frame);
		dialog = this;
		
		try {
			config = new WoWConfig();
		} catch (WoWConfigException e) {
			// ugg
			log.error("ConfigureServerDialog: error getting configuration", e);
		}
		
		JButton saveButton;
		GridBagConstraints gridBagConstraints_1;
		JLabel updateURLLabel;
		setTitle("Configure Servers");
		getContentPane().setLayout(new GridBagLayout());
		setBounds(100, 100, 320, 123);

		serverURLLabel = new JLabel();
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 0, 5, 10);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		getContentPane().add(serverURLLabel, gridBagConstraints);
		serverURLLabel.setText("Server URL");

		serverURLField = new JTextField();
		serverURLField.setMaximumSize(new Dimension(150, 20));
		serverURLField.setMargin(new Insets(1, 5, 2, 4));
		serverURLField.setToolTipText("The WoWCompanion server URL");
		serverURLField.setPreferredSize(new Dimension(150, 20));
		serverURLField.setMinimumSize(new Dimension(150, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;
		getContentPane().add(serverURLField, gridBagConstraints);

		updateURLLabel = new JLabel();
		gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		getContentPane().add(updateURLLabel, gridBagConstraints_1);
		updateURLLabel.setText("Update URL");

		updateURLField = new JTextField();
		updateURLField.setMaximumSize(new Dimension(150, 20));
		updateURLField.setMinimumSize(new Dimension(150, 20));
		updateURLField.setPreferredSize(new Dimension(150, 20));
		updateURLField.setToolTipText("The WoWCompanion server URL");
		gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 1;
		getContentPane().add(updateURLField, gridBagConstraints_1);

		// populate fields
		try {
			serverURLField.setText(config.getPreference("server.url"));
		} catch (WoWConfigPropertyNotFoundException e) {
			// do nothing
			log.debug("ConfigureServerDialog: no server.url configured, using default");
		} catch (Exception e) {
			// do nothing
			log.debug("ConfigureServerDialog: error retrieving server.url");
		}
		try {
			// set to the default if a custom value doesn't exist
			if (serverURLField.getText().equals("")) {
				log.debug("ConfigureServerDialog: using server.url.default");
				serverURLField.setText(config.getProperty("server.url.default"));
			}
		} catch (WoWConfigPropertyNotFoundException e) {
			// do nothing
			log.error("ConfigureServerDialog: no server.url.default configured");
		} catch (Exception e) {
			// do nothing
			log.debug("ConfigureServerDialog: error retrieving server.default.url");
		}
		try {
			updateURLField.setText(config.getPreference("update.url"));
		} catch (WoWConfigPropertyNotFoundException e) {
			// do nothing
			log.error("ConfigureServerDialog: no update.url configured, using default");
		} catch (Exception e) {
			// do nothing
			log.error("ConfigureServerDialog: error retrieving server.url");
		}

		saveButton = new JButton();
		saveButton.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
            	config.setPreference("server.url", serverURLField.getText());
            	config.setPreference("update.url", updateURLField.getText());
            	try {
            		config.savePreferences();
            		dialog.setVisible(false);
				} catch (IOException ee) {
					// display an error
					log.error("ConfigureServerDialog: error saving preferences", ee);
				}
			}
		});
		gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 2;
		getContentPane().add(saveButton, gridBagConstraints_1);
		saveButton.setText("Save");
		
		MouseAdapter createButtonAdaptor = new MouseAdapter() {
		};
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path,
	                                           String description) {
	    java.net.URL imgURL = ConfigureServerDialog.class.getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        log.error("ConfigureServerDialog: couldn't find icon file: " + path);
	        return null;
	    }
	}
}
