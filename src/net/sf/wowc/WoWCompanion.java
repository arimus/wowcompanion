/*
 * Created on Jan 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.wowc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.JCheckBox;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import net.sf.wowc.util.*;
import javax.swing.JCheckBoxMenuItem;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WoWCompanion {
	private static Logger log = LogManager.getLogger(WoWCompanion.class);
	private static String loglevel = "INFO";
	public final static int ONE_SECOND = 1000;
    private static WoWConfig config;
    private static String defaultBaseDirPath = "";
    private static String baseDirPath = defaultBaseDirPath;
    private static String accountPath = "WTF\\Account";
    private static String savedVarFileName = "";
    private static String selectedAccountName = "";
    private static String savedUsername = "";
    private static String savedPassword = "";

    private JProgressBar progressBar;
	private JPasswordField passwordField;
	private JTextField usernameField;
	private JComboBox accountSelect;
	private static JFrame frame;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;
	private JLabel label;
	private GridBagConstraints gridBagConstraints;
	private JButton browseButton;
	private JLabel usernameLabel;
	private JLabel statusLabel;
	private JLabel passwordLabel;
	private JButton uploadButton;
	private JFileChooser fileChooser;
	private GridBagConstraints gridBagConstraints_1;
	private JLabel imageLabel;
	private JMenuItem createAccountMenuItem;
	private JMenu helpMenu;
	private JMenuItem aboutMenuItem;
	private UploadTask uploadTask;    
    private Timer timer;
	private JCheckBox savePasswordCheckBox;
	private JCheckBoxMenuItem debugMenuItem;


	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		try {
			log.debug("WoWCompanion: loading configuration");
			config = new WoWConfig();
		} catch (WoWConfigException e) {
			log.error("WoWCompanion: could not load configuration");
		}
		
		try {
			WoWCompanion window = new WoWCompanion();
			// center our window
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			WoWCompanion.frame.setLocation((d.width-WoWCompanion.frame.getWidth())/2, (d.height-WoWCompanion.frame.getHeight())/2);
			// show our window
			WoWCompanion.frame.setVisible(true);
		} catch (Exception e) {
			log.error("WoWCompanion: error creating UI", e);
		}
	}

	public WoWCompanion() {
		initConfig();
		initialize();
		postInitialize();
	}

	public void initConfig() {
		log.debug("WoWCompanion: loading preferences");
		// load preferences
		try {
			Map m = config.getPreferences();

			if (m.containsKey("loglevel")) {
				// set the level to debug if needed
				loglevel = config.getPreference("loglevel");
				if (loglevel.equals("DEBUG")) {
					log.debug("WoWCompanion: setting log level to DEBUG");
					log.setLevel(Level.DEBUG);
				} else {
					log.debug("WoWCompanion: setting log level to INFO");
				}
			} else {
				log.debug("WoWCompanion: setting log level to INFO");
			}
			
			if (m.containsKey("basedir")) {
				baseDirPath = config.getPreference("basedir");
				log.debug("WoWCompanion: setting basedir to '"+baseDirPath+"'");
			}
			if (m.containsKey("account.selected")) {
				selectedAccountName = config.getPreference("account.selected");
				log.debug("WoWCompanion: setting selected account name to '"+selectedAccountName+"'");
			}
			
			Iterator i = m.keySet().iterator();
			Perl5Util util = new Perl5Util();
			if (m.containsKey("account."+selectedAccountName+".username")) {
				savedUsername = (String)m.get("account."+selectedAccountName+".username");
				log.debug("WoWCompanion: setting username to '"+savedUsername+"'");
			}
			while (i.hasNext()) {
				String key = (String)i.next();
				if (util.match("/account."+selectedAccountName+".password/", key)) {
					savedPassword = (String)m.get("account."+selectedAccountName+".password");
					log.debug("WoWCompanion: setting password to '"+savedPassword+"'");
				}
			}
		} catch (WoWConfigPropertyNotFoundException e) {
			// FIXME - show an error dialog here, then exit
			log.error("WoWCompanion: failed to load preferences", e);
		}
		
		log.debug("WoWCompanion: loading default properties");
		// load default properties
		try {
			defaultBaseDirPath = config.getProperty("basedir.default");
			log.debug("WoWCompanion: setting default basedir to '"+defaultBaseDirPath+"'");
			if (baseDirPath.equals("")) {
				log.debug("WoWCompanion: setting basedir to '"+defaultBaseDirPath+"'");
				baseDirPath = defaultBaseDirPath;
			}
			accountPath = config.getProperty("account.path");
			log.debug("WoWCompanion: setting account path to '"+accountPath+"'");
		    savedVarFileName = config.getProperty("data.filename");
			log.debug("WoWCompanion: setting saved var filename to '"+savedVarFileName+"'");
		} catch (WoWConfigPropertyNotFoundException e) {
			// FIXME - show an error dialog here, then exit
			log.error("WoWCompanion: failed to load default properties", e);
		}
	}

	private void initialize() {
		log.debug("WoWCompanion: initializing interface");
		
		GridBagConstraints gridBagConstraints_2;
		JMenuItem configureServerMenuItem;
		frame = new JFrame();
		frame.setResizable(false);
		frame.setSize(new Dimension(0, 0));
		frame.getContentPane().setLayout(new GridBagLayout());
		frame.setBounds(100, 100, 326, 195);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("WoWCompanion");

		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		menu = new JMenu();
		menuBar.add(menu);
		menu.setText("File");

		createAccountMenuItem = new JMenuItem();
		createAccountMenuItem.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
				CreateAccountDialog inst = new CreateAccountDialog(frame);
				// center our window
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				inst.setLocation((d.width-inst.getWidth())/2, (d.height-inst.getHeight())/2);
				// show the dialog
				inst.setVisible(true);
			}
		});
		menu.add(createAccountMenuItem);
		createAccountMenuItem.setText("Create Account");

		configureServerMenuItem = new JMenuItem();
		configureServerMenuItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				ConfigureServerDialog inst = new ConfigureServerDialog(frame);
				//inst.setConfig(config);
				// center our window
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				inst.setLocation((d.width-inst.getWidth())/2, (d.height-inst.getHeight())/2);
				// show the dialog
				inst.setVisible(true);
			}
		});
		menu.add(configureServerMenuItem);
		configureServerMenuItem.setText("Configure Server");

		menuItem = new JMenuItem();
		menuItem.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
				System.exit(0);
			}
		});
		menu.add(menuItem);
		menuItem.setText("Exit");

		helpMenu = new JMenu();
		menuBar.add(helpMenu);
		helpMenu.setText("Help");

		debugMenuItem = new JCheckBoxMenuItem();
		debugMenuItem.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
				if (debugMenuItem.isSelected()) {
					loglevel = "DEBUG";
					log.info("WoWCompanion: setting loglevel to DEBUG");
					log.setLevel(Level.DEBUG);
				} else {
					loglevel = "INFO";
					log.info("WoWCompanion: setting loglevel to INFO");
					log.setLevel(Level.INFO);
				}
				
	            // save the debug flag to the configuration
	            try {
	                config.setPreference("loglevel", loglevel);
	                log.info("WoWCompanion: saving loglevel to preferences");
	            	config.savePreferences();
	            } catch (IOException ee) {
	    			log.error("WoWCompanion: couldn't save preferences", ee);
	            }

			}
		});
		helpMenu.add(debugMenuItem);
		debugMenuItem.setText("Enable Debug");

		aboutMenuItem = new JMenuItem();
		aboutMenuItem.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
				AboutDialog inst = new AboutDialog(frame);
				// center our window
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				inst.setLocation((d.width-inst.getWidth())/2, (d.height-inst.getHeight())/2);
				// show the dialog
				inst.setVisible(true);
			}
		});
		helpMenu.add(aboutMenuItem);
		aboutMenuItem.setText("About");

		label = new JLabel();
		label.setFocusable(false);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.ipady = 8;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		frame.getContentPane().add(label, gridBagConstraints);
		label.setText("Account:");

		accountSelect = new JComboBox();
		accountSelect.setDoubleBuffered(true);
		accountSelect.setMinimumSize(new Dimension(150, 20));
		accountSelect.setMaximumSize(new Dimension(150, 20));
		accountSelect.setPreferredSize(new Dimension(150, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		//gridBagConstraints.ipadx = 102;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;
		frame.getContentPane().add(accountSelect, gridBagConstraints);

		browseButton = new JButton();
		accountSelect.setNextFocusableComponent(browseButton);
		browseButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				browseHandler(e);
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
				//progressBar.setVisible(true);
			}
			public void mouseReleased(MouseEvent e) {
				//progressBar.setVisible(false);
			}
		});
		browseButton.setPreferredSize(new Dimension(75, 25));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 2;
		frame.getContentPane().add(browseButton, gridBagConstraints);
		browseButton.setText("Browse");
		
		usernameLabel = new JLabel();
		usernameLabel.setFocusable(false);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.ipady = 4;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 0;
		frame.getContentPane().add(usernameLabel, gridBagConstraints);
		usernameLabel.setText("Username:");

		usernameField = new JTextField();
		browseButton.setNextFocusableComponent(usernameField);
		usernameField.setMinimumSize(new Dimension(150, 20));
		usernameField.setMaximumSize(new Dimension(150, 20));
		usernameField.setPreferredSize(new Dimension(150, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		//gridBagConstraints.ipadx = 102;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 1;
		frame.getContentPane().add(usernameField, gridBagConstraints);
		
		savePasswordCheckBox = new JCheckBox();
		gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 2;
		frame.getContentPane().add(savePasswordCheckBox, gridBagConstraints_2);
		savePasswordCheckBox.setText("save pass");

		passwordLabel = new JLabel();
		passwordLabel.setFocusable(false);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridx = 0;
		frame.getContentPane().add(passwordLabel, gridBagConstraints);
		passwordLabel.setText("Password:");

		passwordField = new JPasswordField();
		usernameField.setNextFocusableComponent(passwordField);
		passwordField.setNextFocusableComponent(savePasswordCheckBox);
		usernameField.setNextFocusableComponent(passwordField);
		passwordField.setMinimumSize(new Dimension(150, 20));
		passwordField.setMaximumSize(new Dimension(150, 20));
		passwordField.setPreferredSize(new Dimension(150, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		//gridBagConstraints.ipadx = 102;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridx = 1;
		frame.getContentPane().add(passwordField, gridBagConstraints);

		passwordField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					submitHandler();
				}
			}
			public void keyTyped(KeyEvent e) { }
			public void keyReleased(KeyEvent e) { }
		}
		);

		
		uploadButton = new JButton();
		passwordField.setNextFocusableComponent(uploadButton);
		uploadButton.setNextFocusableComponent(accountSelect);
		savePasswordCheckBox.setNextFocusableComponent(uploadButton);
		uploadButton.addMouseListener(new MouseListener() {			
			public void mouseClicked(MouseEvent e) { 
				submitHandler();
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
			}
		});
		uploadButton.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_ENTER) ||
					(e.getKeyCode() == KeyEvent.VK_SPACE))
				{
					submitHandler();
				}
				
			}
			public void keyTyped(KeyEvent e) { }
			public void keyReleased(KeyEvent e) { }
		}
		);
		uploadButton.setPreferredSize(new Dimension(75, 25));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridx = 2;
		frame.getContentPane().add(uploadButton, gridBagConstraints);
		uploadButton.setText("Upload");

		imageLabel = new JLabel();
		imageLabel.setFocusable(false);
		imageLabel.setPreferredSize(new Dimension(50, 18));
		imageLabel.setMinimumSize(new Dimension(50, 18));
		imageLabel.setIcon(createImageIcon("/wowc.gif", "WoWC"));
		gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_1.anchor = GridBagConstraints.EAST;
		gridBagConstraints_1.gridy = 3;
		gridBagConstraints_1.gridx = 0;
		frame.getContentPane().add(imageLabel, gridBagConstraints_1);
		
		statusLabel = new JLabel("", JLabel.CENTER);
		statusLabel.setFocusable(false);
		statusLabel.setMaximumSize(new Dimension(150, 20));
		statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_1.gridy = 3;
		gridBagConstraints_1.gridx = 1;
		frame.getContentPane().add(statusLabel, gridBagConstraints_1);
		statusLabel.setPreferredSize(new Dimension(150, 20));
		statusLabel.setMinimumSize(new Dimension(150, 20));

		progressBar = new JProgressBar();
		progressBar.setFocusable(false);
		progressBar.setMaximumSize(new Dimension(60, 8));
		progressBar.setMinimumSize(new Dimension(60, 8));
		progressBar.setPreferredSize(new Dimension(60, 8));
		gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_1.gridy = 3;
		gridBagConstraints_1.gridx = 2;
		frame.getContentPane().add(progressBar, gridBagConstraints_1);
		progressBar.setToolTipText("transfering data");
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
	}
	
	public void postInitialize() {
		log.debug("WoWCompanion: initializing second stage");
		
		fileChooser = new JFileChooser();
 		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		frame.getContentPane().add(fileChooser, gridBagConstraints); 
		fileChooser.setSize(430, 225);
		fileChooser.setName("Browse");
		fileChooser.setDialogTitle("Browse");
		fileChooser.setDoubleBuffered(true);
		fileChooser.setVisible(false);
		fileChooser.setApproveButtonText("OK");
		fileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setToolTipText("Browse to your WoW Account Directory");
		fileChooser.setSelectedFile(new File(defaultBaseDirPath));
		fileChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fileChooserActionPerformed(evt);
			}
		});
		
		if (savedUsername != null && !savedUsername.equals("")) {
			usernameField.setText(savedUsername);
		}
		if (savedPassword != null && !savedPassword.equals("")) {
			passwordField.setText("********");
			savePasswordCheckBox.setSelected(true);
		}
		updateAccountList();
		
        // create a timer for upload task
        timer = new Timer(ONE_SECOND, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	progressBar.setVisible(true);
                //progressBar.setValue(uploadTask.getCurrent());
                String s = uploadTask.getMessage();
                if (s != null) {
                    //taskOutput.append(s + newline);
                    //taskOutput.setCaretPosition(
                    //taskOutput.getDocument().getLength());
                	statusLabel.setForeground(Color.BLACK);
                	statusLabel.setText(s);
                }
                if (uploadTask.isDone()) {
                    Toolkit.getDefaultToolkit().beep();
                    timer.stop();
                    uploadButton.setEnabled(true);
                    frame.setCursor(null); //turn off the wait cursor
                    //progressBar.setValue(progressBar.getMinimum());
                    progressBar.setVisible(false);
                    
                    if (uploadTask.hasErrors()) {
                   		statusLabel.setForeground(Color.RED);
        				statusLabel.setText(uploadTask.getErrorMessage());
                    } else {
                   		statusLabel.setForeground(new Color(0, 150, 0));
        				statusLabel.setText("Success");
        				log.debug("WoWCompanion: successfully uploaded data");
                    }
                }
            }
        });

        // make usernameField get the focus whenever frame is activated.
        frame.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                usernameField.requestFocusInWindow();
            }
        });
        
        // set the initial focus on the password box
	    //usernameField.setFocusable(true);
		//if (!usernameField.isRequestFocusEnabled()) { 
	    //	usernameField.setRequestFocusEnabled(true); 
	    //}
	    //usernameField.requestFocus();
	    //usernameField.requestFocusInWindow();
        
        if (loglevel.equals("DEBUG")) {
			debugMenuItem.setSelected(true);
        }
	}
	
    private void fileChooserActionPerformed(java.awt.event.ActionEvent evt) {
    	log.debug("WoWCompanion: fileChooserActionPerformed");
    }
    
	// when our "Browse" button gets clicked
    private void browseHandler(java.awt.event.MouseEvent evt) {
    	log.debug("WoWCompanion: browse clicked, launching filechooser");
        fileChooser.setVisible(true);
    	int retval = fileChooser.showOpenDialog(frame.getContentPane());
    	log.debug("WoWCompanion: filechooser retvalue: "+retval);
    	
        if (retval == JFileChooser.APPROVE_OPTION) {
            File theFile = fileChooser.getSelectedFile();
            if (theFile != null) {
                if (theFile.isDirectory()) {
                    // set the directory here
                    baseDirPath = fileChooser.getSelectedFile().getAbsolutePath();
                    log.debug("WoWCompanion: setting accountDir to "+fileChooser.getSelectedFile().getAbsolutePath());
                    statusLabel.setForeground(new Color(0, 150, 0));
                    statusLabel.setText("Valid Account Dir");
                } else {
                	log.debug("WoWCompanion: file is not a directory");
                }
            } else {
            	log.debug("WoWCompanion: no file selected");
            }
        } else {
        	log.debug("WoWCompanion: didn't approve");
        }

        retval = 0;
    	updateAccountList();
    }
    

    // update the account list
    private void updateAccountList() {
    	log.debug("WoWCompanion: updating account list");
    	accountSelect.removeAllItems();
	    try {
	        Collection dirs = getAccountDirectories();
	        Iterator iter = dirs.iterator();
	        while (iter.hasNext()) {
	            File f = (File)iter.next();
	            accountSelect.addItem(f.getName());
	            if (f.getName().equals(selectedAccountName)) {
	            	accountSelect.setSelectedItem(f.getName());
	            }
	        }
	    } catch (FileNotFoundException e) {
	    	// see if the WoW.exe exists
	    	log.debug("WoWCompanion: looking for '"+ baseDirPath + File.separator + "WoW.exe" +"'");
	    	File wowExe = new File(baseDirPath + File.separator + "WoW.exe");
	    	if (wowExe.isFile()) {
		    	log.debug("WoWCompanion: no valid account dir, but WoW basedir found");
		        accountSelect.addItem("- NO ACCOUNTS -");
	            statusLabel.setForeground(Color.RED);
	            statusLabel.setText("No Account Dirs");
	    	} else {
		    	log.error("WoWCompanion: invalid account directory selected", e);
		        accountSelect.addItem("- INVALID ACCOUNT DIR -");
	            statusLabel.setForeground(Color.RED);
	            statusLabel.setText("Invalid Account Dir");
	    	}
	    }
	}
    
    
	private Collection getAccountDirectories() throws FileNotFoundException {
		log.debug("WoWCompanion: getting account directories from '"+ baseDirPath + File.separator + accountPath +"'");
		
        File accountDir = new File(baseDirPath + File.separator + accountPath);
        ArrayList validAccountDirs = new ArrayList();
        if (accountDir.isDirectory()) {
            // search for all account directories under the base path with a
            File[] files = accountDir.listFiles();
            for (int i=0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    if (new File(files[i].getAbsolutePath() + 
							File.separator +
							savedVarFileName).isFile()) {
                    	log.debug("WoWCompanion: found account '"+files[i].toString()+"'");
                        validAccountDirs.add(files[i]);
                    }
                }
            }

            if (validAccountDirs.size() < 1) {
            	log.error("WoWCompanion: no valid account directories in WoW directory '"+baseDirPath + File.separator + accountPath+"'");
                throw new FileNotFoundException("no valid account directories in WoW directory '"+baseDirPath + File.separator + accountPath+"'");
            }
		} else {
			log.error("WoWCompanion: couldn't find valid WoW account directory");
            throw new FileNotFoundException("no valid account directories");
		}
        return validAccountDirs;                                
    }
	
    // when out "Submit" button gets clicked
    private void submitHandler() {
    	try {
			config = new WoWConfig();
		} catch (WoWConfigException e) {
			log.error("WoWCompanion: failed to load configuration", e);
		}

        String tmp = "";
        try {
            String accountName = (String)accountSelect.getSelectedItem();
            if (accountName == null) {
                throw new FileNotFoundException("an account has not been selected");
            }

            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String hashedPassword;
            if (savedPassword != null && !savedPassword.equals("")) {
            	hashedPassword = savedPassword;
            } else {
            	hashedPassword = MD5.getMD5(password.getBytes("UTF8"));
            }
            log.debug("WoWCompanion: got username: '"+usernameField.getText()+"'");
            log.debug("WoWCompanion: got password: '"+new String(passwordField.getPassword())+"'");
            log.debug("WoWCompanion: hashed password is: '"+hashedPassword+"'");
            
            // save the selected account information to preferences
            try {
                config.setPreference("account.selected", accountName);
                config.setPreference("account."+accountName+".username", username);
                if (savePasswordCheckBox.isSelected() &&
                	savedPassword != null && 
					!savedPassword.equals("********")) 
                {
                	savedPassword = hashedPassword;
                	config.setPreference("account."+accountName+".password", savedPassword);
                	log.debug("WoWCompanion: saving password '"+savedPassword+"'");
                } else {
                	config.setPreference("account."+accountName+".password", "");
                	savedPassword = "";
                	passwordField.setText("");
                	log.debug("WoWCompanion: clearing password");
                }
                
                log.debug("WoWCompanion: saving preferences");
            	config.savePreferences();
            } catch (IOException e) {
    			log.error("WoWCompanion: couldn't save preferences", e);
            }
            
            String filename = baseDirPath + File.separator + 
							  accountPath + File.separator +
                              accountName + File.separator + 
                              savedVarFileName;
            
			// send the data to the server
            if (((username != null) && !username.equals("") &&
			     ((password != null) && 
			      !password.equals("") && 
				  !password.equals("********"))))
			{
           		log.debug("WoWCompanion: using password");
           		
           		//WoWUploader.upload(data, username, hashedPassword);
        		uploadTask = new UploadTask(new String[] {filename, username, hashedPassword});
                uploadButton.setEnabled(false);
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                uploadTask.go();
                timer.start();
           		//statusLabel.setForeground(new Color(0, 150, 0));
				//statusLabel.setText("Success");
			} else if (((username != null) && !username.equals("") &&
	   			     ((savedPassword != null) && 
	   			      !savedPassword.equals(""))))
   			{
           		log.debug("WoWCompanion: using saved password");
           		
           		//WoWUploader.upload(data, username, savedPassword);
        		uploadTask = new UploadTask(new String[] {filename, username, savedPassword});
                uploadButton.setEnabled(false);
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                uploadTask.go();
                timer.start();
                //statusLabel.setForeground(new Color(0, 150, 0));
				//statusLabel.setText("Success");
			} else {
	            //dialogText.setText("Please specify a valid username and password");
	            //dialog.pack();
	            //dialog.show();
				log.error("WoWCompanion: invalid user/pass specified");
                statusLabel.setForeground(Color.RED);
				statusLabel.setText("Invalid user/pass");
			}
        } catch (UnsupportedEncodingException e) {
            statusLabel.setForeground(Color.RED);
			statusLabel.setText("Error hashing password");
			log.error("WoWCompanion: failed to hash password");
        } catch (FileNotFoundException e) {
            //dialogText.setText("Error opening account: "+e);
            //dialog.pack();
            //dialog.show()
			statusLabel.setText("Error opening account dir");
			log.error("WoWCompanion: error opening account directory");
//		} catch (WoWParserException e) {
//			System.err.println(e);
//            statusLabel.setForeground(Color.RED);
//			statusLabel.setText("Unable to parse account data");
//		} catch (WoWUploaderConnectException e) {
//			System.err.println(e);
//            statusLabel.setForeground(Color.RED);
//			statusLabel.setText("Unable to connect");
//		} catch (InvalidUserPassException e) {
//			System.err.println(e);
//            statusLabel.setForeground(Color.RED);
//			statusLabel.setText("Invalid user/pass");
//		} catch (WoWUploaderException e) {
//			System.err.println("error: "+e);
//            statusLabel.setForeground(Color.RED);
//			statusLabel.setText("Error sending data");
        }
    }
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path,
	                                           String description) {
	    java.net.URL imgURL = WoWCompanion.class.getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        log.error("WoWCompanion: couldn't find icon file: " + path);
	        return null;
	    }
	}
}
