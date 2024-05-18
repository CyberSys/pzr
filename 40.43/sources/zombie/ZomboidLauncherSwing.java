package zombie;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;
import zombie.core.Core;
import zombie.core.Language;
import zombie.core.Translator;


public class ZomboidLauncherSwing implements ActionListener,HyperlinkListener {
	JButton launchPz;
	JButton launchPzCompatibility;
	JComboBox languageList;
	JFrame mainFrame;
	static ZomboidLauncherSwing instance;
	static String[] args = new String[0];
	JEditorPane webBrowser;
	File url = new File("media/launcher/page.html");
	Process launchedProcess = null;

	public void doComponents() {
		this.mainFrame = new JFrame();
		this.mainFrame.setIconImage((new ImageIcon("media" + File.separator + "ui" + File.separator + "zomboidIcon32.png")).getImage());
		this.mainFrame.setDefaultCloseOperation(2);
		this.mainFrame.setTitle("Project Zomboid Launcher");
		this.mainFrame.setResizable(true);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		double double1 = dimension.getWidth();
		double double2 = dimension.getHeight();
		short short1 = 1280;
		short short2 = 900;
		if (double1 < 1280.0 || double2 < 900.0) {
			short1 = 1024;
			short2 = 680;
		}

		this.mainFrame.setLocationByPlatform(true);
		this.mainFrame.setSize(short1, short2);
		this.launchPz = new JButton("Launch Project Zomboid");
		this.launchPz.addActionListener(this);
		this.launchPz.setPreferredSize(new Dimension(180, 40));
		this.launchPzCompatibility = new JButton("Compatibility Mode");
		this.launchPzCompatibility.addActionListener(this);
		this.launchPzCompatibility.setPreferredSize(new Dimension(180, 40));
		JPanel jPanel = new JPanel(new BorderLayout());
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(0);
		JPanel jPanel2 = new JPanel(flowLayout);
		jPanel2.add(this.launchPz);
		jPanel2.add(this.launchPzCompatibility);
		jPanel.add(jPanel2, "West");
		JPanel jPanel3 = new JPanel(new FlowLayout());
		JLabel jLabel = new JLabel("Language");
		jPanel3.add(jLabel);
		Object[] objectArray = this.doLanguage();
		this.languageList = new JComboBox(objectArray);
		this.languageList.setSelectedIndex(this.getLanguageIndex());
		this.languageList.addActionListener(this);
		jPanel3.add(this.languageList);
		jPanel.add(jPanel3, "East");
		this.mainFrame.getContentPane().add(jPanel, "South");
		JPanel jPanel4 = new JPanel(new BorderLayout());
		jPanel4.setOpaque(false);
		this.webBrowser = new JEditorPane();
		this.webBrowser.setContentType("text/html");
		this.webBrowser.setEditable(false);
		this.webBrowser.addHyperlinkListener(this);
		HTMLEditorKit hTMLEditorKit = new HTMLEditorKit();
		StyleSheet styleSheet = hTMLEditorKit.getStyleSheet();
		styleSheet.addRule("a {text-decoration: none}");
		try {
			hTMLEditorKit.getStyleSheet().importStyleSheet((new File("media/launcher/style.css?ver=3.7.1")).toURI().toURL());
		} catch (MalformedURLException malformedURLException) {
			malformedURLException.printStackTrace();
		}

		styleSheet.addRule(".override { font-family: arial, verdana, sans-serif;color: black;text-decoration: bold;text-size: 1em; }");
		styleSheet.addRule(".override2 { font-family: arial, verdana, sans-serif; color: black; font-style: italic; text-size: 1em; }");
		this.webBrowser.setEditorKit(hTMLEditorKit);
		jPanel4.add(this.webBrowser);
		try {
			this.webBrowser.setPage(this.url.toURI().toURL());
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		this.webBrowser.setVisible(true);
		this.mainFrame.getContentPane().add(jPanel4, "Center");
		hTMLEditorKit.getStyleSheet().addRule("a {text-decoration: none}");
		hTMLEditorKit.getStyleSheet().addRule("a {color: #8d2123}");
		this.mainFrame.setLocationRelativeTo((Component)null);
		this.mainFrame.setVisible(true);
		this.mainFrame.addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent dimension) {
				if (ZomboidLauncherSwing.this.launchedProcess != null) {
					ZomboidLauncherSwing.this.launchedProcess.destroy();
				}

				ZomboidLauncherSwing.this.mainFrame.dispose();
				System.exit(0);
			}

			
			public void windowClosed(WindowEvent dimension) {
				if (ZomboidLauncherSwing.this.launchedProcess != null) {
					ZomboidLauncherSwing.this.launchedProcess.destroy();
				}

				ZomboidLauncherSwing.this.mainFrame.dispose();
				System.exit(0);
			}
		});
	}

	private int getLanguageIndex() {
		for (int int1 = 0; int1 < Translator.getAvailableLanguage().size(); ++int1) {
			if (Translator.getAvailableLanguage().get(int1) == Translator.getLanguage()) {
				return int1;
			}
		}

		return 0;
	}

	private Object[] doLanguage() {
		Object[] objectArray = new Object[Translator.getAvailableLanguage().size()];
		for (int int1 = 0; int1 < Translator.getAvailableLanguage().size(); ++int1) {
			objectArray[int1] = ((Language)Translator.getAvailableLanguage().get(int1)).text();
		}

		return objectArray;
	}

	public static void main(String[] stringArray) {
		instance = new ZomboidLauncherSwing();
		args = stringArray;
		try {
			Core.getInstance().loadOptions();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		instance.doComponents();
	}

	public void actionPerformed(ActionEvent actionEvent) {
		Object object = actionEvent.getSource();
		Runtime runtime;
		int int1;
		final Process process;
		if (object == this.launchPz) {
			runtime = Runtime.getRuntime();
			try {
				String string = "";
				for (int1 = 0; int1 < args.length; ++int1) {
					string = string + " " + args[int1];
				}

				process = runtime.exec("java -Xms768m -Xmx768m -Djava.library.path=./ -cp lwjgl.jar;lwjgl_util.jar;./ zombie.gameStates.MainScreenState " + string);
				this.launchedProcess = process;
				(new Thread(){
					
					public void run() {
						try {
							BufferedReader actionEvent = new BufferedReader(new InputStreamReader(process.getInputStream()));
							String object = "";
							try {
								while ((object = actionEvent.readLine()) != null) {
									System.out.println(object);
								}
							} finally {
								actionEvent.close();
							}
						} catch (IOException exception2) {
							exception2.printStackTrace();
						}
					}
				}).start();

				(new Thread(){
					
					public void run() {
						try {
							BufferedReader actionEvent = new BufferedReader(new InputStreamReader(process.getErrorStream()));
							String object = "";
							try {
								while ((object = actionEvent.readLine()) != null) {
									System.out.println(object);
								}
							} finally {
								actionEvent.close();
							}
						} catch (IOException exception2) {
							exception2.printStackTrace();
						}
					}
				}).start();
			} catch (Exception exception) {
				System.out.println(exception);
			}
		} else if (object == this.launchPzCompatibility) {
			runtime = Runtime.getRuntime();
			String[] stringArray = new String[args.length + 1];
			for (int1 = 0; int1 < args.length; ++int1) {
				stringArray[int1] = args[int1];
			}

			stringArray[args.length] = "safemode";
			try {
				process = runtime.exec("java -Xms768m -Xmx768m -Djava.library.path=./ -cp lwjgl.jar;lwjgl_util.jar;./ zombie.gameStates.MainScreenState " + stringArray);
				this.launchedProcess = process;
				(new Thread(){
					
					public void run() {
						try {
							BufferedReader actionEvent = new BufferedReader(new InputStreamReader(process.getInputStream()));
							String object = "";
							try {
								while ((object = actionEvent.readLine()) != null) {
									System.out.println(object);
								}
							} finally {
								actionEvent.close();
							}
						} catch (IOException exception2) {
							exception2.printStackTrace();
						}
					}
				}).start();

				(new Thread(){
					
					public void run() {
						try {
							BufferedReader actionEvent = new BufferedReader(new InputStreamReader(process.getErrorStream()));
							String object = "";
							try {
								while ((object = actionEvent.readLine()) != null) {
									System.out.println(object);
								}
							} finally {
								actionEvent.close();
							}
						} catch (IOException exception2) {
							exception2.printStackTrace();
						}
					}
				}).start();
			} catch (Exception exception2) {
				System.out.println(exception2);
			}
		} else if (object == this.languageList) {
			Translator.setLanguage(this.languageList.getSelectedIndex());
			try {
				Core.getInstance().saveOptions();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public void openURl(String string) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Action.BROWSE)) {
			try {
				URI uRI = new URI(string);
				desktop.browse(uRI);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
		EventType eventType = hyperlinkEvent.getEventType();
		if (eventType == EventType.ACTIVATED) {
			if (hyperlinkEvent instanceof HTMLFrameHyperlinkEvent) {
				HTMLFrameHyperlinkEvent hTMLFrameHyperlinkEvent = (HTMLFrameHyperlinkEvent)hyperlinkEvent;
				HTMLDocument hTMLDocument = (HTMLDocument)this.webBrowser.getDocument();
				hTMLDocument.processHTMLFrameHyperlinkEvent(hTMLFrameHyperlinkEvent);
			} else {
				this.openURl(hyperlinkEvent.getURL().toString());
			}
		}
	}
}
