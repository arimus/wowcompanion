/*
 * Created on Jan 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.wowc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CreateAccountDialog extends JDialog {
	private JTextField emailField;
	private JLabel emailLabel;
	private static Logger log = null; 
	private final JPasswordField confirmPasswordField;
	private final JPasswordField passwordField;
	private final JTextField realnameField;
	private final JTextField usernameField;
	private final JLabel usernameLabel;
	private final JLabel realnameLabel;
	private final JLabel passwordLabel;
	private final JLabel confirmLabel;
	private final JButton createButton;
	private final JLabel statusMessage;
	private final JLabel statusMessage2;
	private final JLabel imageLabel;
	private GridBagConstraints gridBagConstraints;

	{
		try {
			WoWConfig config = new WoWConfig();
			Map m = config.getPreferences();
			
			log = config.getLogger();

			if (m.containsKey("loglevel")) {
				// set the level to debug if needed
				String loglevel = config.getPreference("loglevel");
				if (loglevel.equals("DEBUG")) {
					log.setLevel(Level.DEBUG);
				}
			}
		} catch (WoWConfigException e) {
			log.error("WoWCompanion: failed to load configuration", e);
		} catch (WoWConfigPropertyNotFoundException e) {
			// FIXME - show an error dialog here, then exit
			log.error("WoWCompanion: failed to load preferences", e);
		}

	}

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

	public CreateAccountDialog(Frame frame) {
		super(frame);
		GridBagConstraints gridBagConstraints_1;
		setTitle("Create Account");
		getContentPane().setLayout(new GridBagLayout());
		setBounds(100, 100, 301, 187);

		usernameLabel = new JLabel();
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 0, 5, 10);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		getContentPane().add(usernameLabel, gridBagConstraints);
		usernameLabel.setText("Username:");

		usernameField = new JTextField();
		usernameField.setToolTipText("The username you will use to login");
		usernameField.setPreferredSize(new Dimension(100, 20));
		usernameField.setMinimumSize(new Dimension(100, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 0, 5, 0);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;
		getContentPane().add(usernameField, gridBagConstraints);

		imageLabel = new JLabel();
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 2;
		getContentPane().add(imageLabel, gridBagConstraints);
		imageLabel.setMinimumSize(new Dimension(50, 18));
		imageLabel.setPreferredSize(new Dimension(50, 18));
		imageLabel.setIcon(createImageIcon("/wowc.gif", "WoWC"));

		realnameLabel = new JLabel();
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 0, 5, 10);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 0;
		getContentPane().add(realnameLabel, gridBagConstraints);
		realnameLabel.setText("Real Name:");

		realnameField = new JTextField();
		realnameField.setToolTipText("Your real life name");
		realnameField.setMinimumSize(new Dimension(100, 20));
		realnameField.setPreferredSize(new Dimension(100, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 0, 5, 0);
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 1;
		getContentPane().add(realnameField, gridBagConstraints);

		statusMessage = new JLabel("", JLabel.CENTER);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 10, 0, 0);
		gridBagConstraints.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 2;
		statusMessage.setMinimumSize(new Dimension(100, 20));
		statusMessage.setPreferredSize(new Dimension(100, 20));
		getContentPane().add(statusMessage, gridBagConstraints);

	    emailLabel = new JLabel();
	    gridBagConstraints_1 = new GridBagConstraints();
	    gridBagConstraints_1.insets = new Insets(5, 0, 5, 10);
	    gridBagConstraints_1.anchor = GridBagConstraints.NORTHEAST;
	    gridBagConstraints_1.gridy = 2;
	    gridBagConstraints_1.gridx = 0;
	    getContentPane().add(emailLabel, gridBagConstraints_1);
	    emailLabel.setText("Email:");

	    emailField = new JTextField();
	    emailField.setPreferredSize(new Dimension(100, 20));
	    emailField.setMinimumSize(new Dimension(100, 20));
	    emailField.setMaximumSize(new Dimension(100, 20));
	    emailField.setToolTipText("Your email address");
	    gridBagConstraints_1 = new GridBagConstraints();
	    gridBagConstraints_1.gridy = 2;
	    gridBagConstraints_1.gridx = 1;
	    getContentPane().add(emailField, gridBagConstraints_1);

		statusMessage2 = new JLabel("", JLabel.CENTER);
		statusMessage2.setMinimumSize(new Dimension(100, 20));
		statusMessage2.setPreferredSize(new Dimension(100, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 10, 0, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridx = 2;
		getContentPane().add(statusMessage2, gridBagConstraints);

		passwordLabel = new JLabel();
		gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 0, 5, 10);
		gridBagConstraints_1.anchor = GridBagConstraints.EAST;
		gridBagConstraints_1.gridy = 3;
		gridBagConstraints_1.gridx = 0;
		getContentPane().add(passwordLabel, gridBagConstraints_1);
		passwordLabel.setText("Password:");

		passwordField = new JPasswordField();
		gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridy = 3;
		gridBagConstraints_1.gridx = 1;
		getContentPane().add(passwordField, gridBagConstraints_1);
		passwordField.setToolTipText("The password you will use to login");
		passwordField.setMinimumSize(new Dimension(100, 20));
		passwordField.setPreferredSize(new Dimension(100, 20));
		
		MouseAdapter createButtonAdaptor = new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				log.debug("CreateAccountDialog: createButton.mouseReleased, event=" + evt);
				//TODO add your code for createButton.mouseReleased
				
				String username = new String(usernameField.getText());
				String realName = new String(realnameField.getText());
				String email = new String(emailField.getText());
				String password1 = new String(passwordField.getPassword());
				String password2 = new String(confirmPasswordField.getPassword());
				String message1 = "";
				String message2 = "";
				boolean success = false;
				
				// TODO - make sure that username >= 3 chars long

				// if the password and confirmation password match and != null
				if (password1 != null && 
					password1.equals(password2) && 
					password1.length() >= 6 &&
					username != null &&
					!username.equals(""))
				{
				
					// send information for the account to be created
					try {
					    log.debug("Creating account with password '"+password1+"'");
						String password = MD5.getMD5(password1.getBytes("UTF8"));
						WoWUploader.createAccount(realName, email, username, password);
						log.debug("Account successfully created");
						message1 = "Account created";
						success = true;
					} catch (InvalidUsernameException e) {
						log.error("CreateAccountDialog: invalid username", e);
						message1 = "Invalid Username";
					} catch (DuplicateUsernameException e) {
						log.error("CreateAccountDialog: duplicate username", e);
						message1 = "Duplicate Username";
					} catch (WoWUploaderException e) {
						log.error("CreateAccountDialog: create error", e);
						message1 = "Create Error";
					} catch (WoWUploaderConnectException e) {
						log.error("CreateAccountDialog: unable to connect to server", e);
						message1 = "Failed to connect";
					} catch (UnsupportedEncodingException e) {
						log.error("CreateAccountDialog: unable to get UTF8 password", e);
						message1 = "System error";
					}
				} else if (username == null || username.equals("")){
					log.debug("CreateAccountDialog: empty username field");
					message1 = "Username field";
					message2 = "is empty";
				} else if (password1 != null && password1.length() < 6){
					log.debug("CreateAccountDialog: password too short");
					message1 = "Passwords must be";
					message2 = "> 6 chars in length";
				} else if (password1 != null && !password1.equals(password2)) {
					log.debug("CreateAccountDialog: passwords don't match");
					message1 = "Passwords must";
					message2 = "match";
				} else if (password1 == null) {
					log.debug("CreateAccountDialog: empty password");
					message1 = "Password is empty";
				} else {
					log.debug("CreateAccountDialog: error creating account");
					message1 = "Error";
				}

				
				if (success) {
					// display message
					statusMessage.setForeground(new Color(0, 150, 0));
					statusMessage.setText(message1);
					statusMessage2.setForeground(new Color(0, 150, 0));
					statusMessage2.setText(message2);

					createButton.setText("Done");
					createButton.removeMouseListener(this);
					createButton.addMouseListener(new MouseAdapter() {
						public void mouseReleased(MouseEvent evt) {
							log.debug("CreateAccountDialog: createButton.mouseReleased, event=" + evt);
							// hide this dialog
							setVisible(false);
							
							// scrub the dialog in case we want to use it again
							//newUsernameField.setText("");
							//newPasswordField.setText("");
							//newPasswordConfirmField.setText("");
						}
					});
				} else {
					// display message
					statusMessage.setForeground(Color.RED);
					statusMessage.setText(message1);
					statusMessage2.setForeground(Color.RED);
					statusMessage2.setText(message2);
				}
			}
		};

		confirmLabel = new JLabel();
		gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 0, 5, 10);
		gridBagConstraints_1.anchor = GridBagConstraints.EAST;
		gridBagConstraints_1.gridy = 4;
		gridBagConstraints_1.gridx = 0;
		getContentPane().add(confirmLabel, gridBagConstraints_1);
		confirmLabel.setText("Confirm:");

		confirmPasswordField = new JPasswordField();
		gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridy = 4;
		gridBagConstraints_1.gridx = 1;
		getContentPane().add(confirmPasswordField, gridBagConstraints_1);
		confirmPasswordField.setToolTipText("Re-type the password you entered");
		confirmPasswordField.setPreferredSize(new Dimension(100, 20));
		confirmPasswordField.setMinimumSize(new Dimension(100, 20));

		createButton = new JButton();
		gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridy = 4;
		gridBagConstraints_1.gridx = 2;
		getContentPane().add(createButton, gridBagConstraints_1);
		createButton.setPreferredSize(new Dimension(75, 20));
		createButton.setText("Create");
		createButton.addMouseListener(createButtonAdaptor);
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path,
	                                           String description) {
	    java.net.URL imgURL = CreateAccountDialog.class.getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        log.error("CreateAccountDialog: couldn't find icon file: " + path);
	        return null;
	    }
	}
}
