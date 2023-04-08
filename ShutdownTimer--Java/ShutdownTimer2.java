/* By CHAT-GPT on 08-April-2023
Write a shutdown timer program for windows using java.
- user can input minutes and hours on textfields. default value for hours is 0, default value for minutes is 10.
- user can choose an option (shutdown, restart, hibernate, standby). the options should be displayed as radio buttons. default value is "hibernate". make sure there is only one option to be chosen at a time.
- we will have to buttons: ok and cancel, below the option.
- when user clicks ok, it will schedule to shutdown/restart/hibernate/standby.
- user can cancel the schedule by clicking a button "cancel" .
- while the timer is running, it should show the time counting down in a label, showing hour, minutes and seconds.
- the program should prevent the computer from going to sleep.
- the program should save these values to cache when user clicks ok, so the program will load them on next launch: hours in textfield, minutes in textfield, option (shutdown, restart, hibernate, standby).
- the window size is 500 as width and 400 as height.
*/
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.util.Properties;
import javax.swing.ButtonGroup;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;


public class ShutdownTimer2 extends JFrame {

	private static final long serialVersionUID = 1L;

	private final JTextField hoursField;
	private final JTextField minutesField;
	private final JRadioButton shutdownButton;
	private final JRadioButton restartButton;
	private final JRadioButton hibernateButton;
	private final JRadioButton standbyButton;
	private final JButton okButton;
	private final JButton cancelButton;
	private final JLabel countdownLabel;

	private Timer countdownTimer;
	private int countdownSeconds;

	private static final String CACHE_FILE_NAME = "shutdown-scheduler.cache";

	public ShutdownTimer2() {
		super("Shutdown Timer by ChatGPT - 1.0.0");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(new Dimension(500, 400));
		setResizable(false);

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		setContentPane(contentPane);

		JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		timePanel.setBorder(BorderFactory.createTitledBorder("Time"));
		contentPane.add(timePanel);

		JLabel hoursLabel = new JLabel("Hours:");
		hoursField = new JTextField("0", 10);
		JLabel minutesLabel = new JLabel("Minutes:");
		minutesField = new JTextField("10", 10);
		timePanel.add(hoursLabel);
		timePanel.add(hoursField);
		timePanel.add(minutesLabel);
		timePanel.add(minutesField);

		JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		optionPanel.setBorder(BorderFactory.createTitledBorder("Option"));
		contentPane.add(optionPanel);

		shutdownButton = new JRadioButton("Shutdown");
		restartButton = new JRadioButton("Restart");
		hibernateButton = new JRadioButton("Hibernate", true);
		standbyButton = new JRadioButton("Standby");

		ButtonGroup group = new ButtonGroup();
		group.add(shutdownButton);
		group.add(restartButton);
		group.add(hibernateButton);
		group.add(standbyButton);

		optionPanel.add(shutdownButton);
		optionPanel.add(restartButton);
		optionPanel.add(hibernateButton);
		optionPanel.add(standbyButton);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		contentPane.add(buttonPanel);

		JPanel countdownPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		countdownPanel.setBorder(BorderFactory.createTitledBorder("Countdown"));
		contentPane.add(countdownPanel);

		countdownLabel = new JLabel(" 00:00:00 ");
		countdownLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
		countdownPanel.add(countdownLabel);

		shutdownButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateOptionButtons(shutdownButton);
			}
		});

		restartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateOptionButtons(restartButton);
			}
		});

		hibernateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateOptionButtons(hibernateButton);
			}
		});

		standbyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateOptionButtons(standbyButton);
			}
		});

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int hours = Integer.parseInt(hoursField.getText());
				int minutes = Integer.parseInt(minutesField.getText());
				String option = "";
				if (shutdownButton.isSelected()) {
					option = "shutdown";
				} else if (restartButton.isSelected()) {
					option = "restart";
				} else if (hibernateButton.isSelected()) {
					option = "hibernate";
				} else if (standbyButton.isSelected()) {
					option = "standby";
				}
				scheduleShutdown(hours, minutes, option);
				saveToCache(hours, minutes, option);
				

//				cancelButton.requestFocusInWindow();
//				// Set the JFrame to be focusable so that it can receive keyboard events
//			    setFocusable(true);
//			    setFocusTraversalKeysEnabled(false);
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelShutdown();

				minutesField.selectAll();
				minutesField.requestFocusInWindow();
			}
		});
		// Add KeyListener to JFrame
	    addKeyListener(new KeyListener() {
	        @Override
	        public void keyTyped(KeyEvent e) {
	            // Check if the Esc key was pressed
	            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	                cancelButton.doClick(); // Simulate click on Cancel button
	            }
	        }
	        
	        @Override
	        public void keyPressed(KeyEvent e) {}
	        
	        @Override
	        public void keyReleased(KeyEvent e) {}
	    });
	    

		loadFromCache();
		setVisible(true);
		requestFocusInWindow(); // Request focus to receive key events immediately

		minutesField.selectAll();
		minutesField.requestFocusInWindow();
		getRootPane().setDefaultButton(okButton);


		
	}

	private void updateOptionButtons(JRadioButton selectedButton) {
		shutdownButton.setSelected(false);
		restartButton.setSelected(false);
		hibernateButton.setSelected(false);
		standbyButton.setSelected(false);
		selectedButton.setSelected(true);
	}

	private void scheduleShutdown(int hours, int minutes, String option) {
		countdownSeconds = hours * 3600 + minutes * 60;
		countdownTimer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				countdownSeconds--;
				if (countdownSeconds == 0) {
					executeShutdown(option);
					countdownTimer.stop();
				} else {
					updateCountdownLabel(countdownSeconds);
				}
			}
		});
		countdownTimer.start();
		setButtonsEnabled(false);
		setPreventSleep(true);
	}

	private void executeShutdown(String option) {
		String command = "";
		if (option.equals("shutdown")) {
			command = "shutdown /s /t 0";
		} else if (option.equals("restart")) {
			command = "shutdown /r /t 0";
		} else if (option.equals("hibernate")) {
			command = "shutdown /h";
		} else if (option.equals("standby")) {
			command = "rundll32.exe powrprof.dll,SetSuspendState 0,1,0";
		}
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Failed to execute shutdown command: " + e.getMessage());
		}
	}

	private void cancelShutdown() {
		if (this.countdownTimer == null) return;
		countdownTimer.stop();
		updateCountdownLabel(0);
		setButtonsEnabled(true);
		setPreventSleep(false);
	}

	private void updateCountdownLabel(int seconds) {
		int h = seconds / 3600;
		int m = (seconds % 3600) / 60;
		int s = seconds % 60;
		DecimalFormat df = new DecimalFormat("00");
		countdownLabel.setText(df.format(h) + ":" + df.format(m) + ":" + df.format(s));
	}

	private void setButtonsEnabled(boolean enabled) {
		hoursField.setEnabled(enabled);
		minutesField.setEnabled(enabled);
		shutdownButton.setEnabled(enabled);
		restartButton.setEnabled(enabled);
		hibernateButton.setEnabled(enabled);
		standbyButton.setEnabled(enabled);
		okButton.setEnabled(enabled);
		cancelButton.setEnabled(!enabled);
	}

	private void setPreventSleep(boolean prevent) {
		String command = "powercfg -change -monitor-timeout-ac ";
		command += prevent ? "0" : "15";
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Failed to set prevent sleep mode: " + e.getMessage());
		}
	}

	private void saveToCache(int hours, int minutes, String option) {
		try {
			Properties prop = new Properties();
			prop.setProperty("hours", Integer.toString(hours));
			prop.setProperty("minutes", Integer.toString(minutes));
			prop.setProperty("option", option);
			File file = new File("cache.properties");
			OutputStream output = new FileOutputStream(file);
			prop.store(output, "Shutdown settings");
			output.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Failed to save to cache: " + e.getMessage());
		}
	}

	private void loadFromCache() {
		try {
			Properties prop = new Properties();
			File file = new File("cache.properties");
			if (file.exists()) {
				InputStream input = new FileInputStream(file);
				prop.load(input);
				input.close();
				hoursField.setText(prop.getProperty("hours"));
				minutesField.setText(prop.getProperty("minutes"));
				String option = prop.getProperty("option");
				if (option.equals("shutdown")) {
					shutdownButton.setSelected(true);
				} else if (option.equals("restart")) {
					restartButton.setSelected(true);
				} else if (option.equals("hibernate")) {
					hibernateButton.setSelected(true);
				} else if (option.equals("standby")) {
					standbyButton.setSelected(true);
				}
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Failed to load from cache: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		new ShutdownTimer2();
	}

}