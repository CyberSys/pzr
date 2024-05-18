package zombie;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
import zombie.gameStates.MainScreenState;


public class ZomboidLauncher implements ActionListener,HyperlinkListener {
	JButton launchPz;
	JButton launchPzCompatibility;
	JComboBox languageList;
	JFrame mainFrame;
	static ZomboidLauncherSwing instance;
	static String[] args = new String[0];
	JEditorPane webBrowser;
	File url = new File("media/launcher/page.html");

	public void doComponents() {
		this.mainFrame = new JFrame();
		this.mainFrame.setIconImage((new ImageIcon("media" + File.separator + "ui" + File.separator + "zomboidIcon32.png")).getImage());
		this.mainFrame.setDefaultCloseOperation(2);
		this.mainFrame.setTitle("Project Zomboid Launcher");
		this.mainFrame.setResizable(true);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		double double1 = dimension.getWidth();
		double double2 = dimension.getHeight();
		short short1 = 1180;
		short short2 = 768;
		if (double1 < 1180.0 || double2 < 768.0) {
			short1 = 1024;
			short2 = 680;
		}

		this.mainFrame.setLocationByPlatform(true);
		this.mainFrame.setSize(short1, short2);
		JPanel jPanel = new JPanel(new BorderLayout());
		jPanel.setOpaque(false);
		this.webBrowser = new JEditorPane();
		this.webBrowser.setContentType("text/html");
		this.webBrowser.setEditable(false);
		this.webBrowser.addHyperlinkListener(this);
		HTMLEditorKit hTMLEditorKit = new HTMLEditorKit();
		StyleSheet styleSheet = hTMLEditorKit.getStyleSheet();
		styleSheet.addRule("a {text-decoration: none}");
		try {
			hTMLEditorKit.getStyleSheet().importStyleSheet(new URL("http://projectzomboid.com/blog/wp-content/themes/projectzomboid/style.css?ver=3.7.1"));
		} catch (MalformedURLException malformedURLException) {
			malformedURLException.printStackTrace();
		}

		styleSheet.addRule(".override { font-family: arial, verdana, sans-serif;color: black;text-decoration: bold;text-size: 1em; }");
		styleSheet.addRule(".override2 { font-family: arial, verdana, sans-serif; color: black; font-style: italic; text-size: 1em; }");
		this.webBrowser.setEditorKit(hTMLEditorKit);
		jPanel.add(this.webBrowser);
		try {
			this.webBrowser.setPage(this.url.toURI().toURL());
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		this.webBrowser.setVisible(true);
		this.mainFrame.getContentPane().add(jPanel, "Center");
		this.launchPz = new JButton("Launch Project Zomboid");
		this.launchPz.addActionListener(this);
		this.launchPz.setPreferredSize(new Dimension(190, 40));
		this.launchPzCompatibility = new JButton("Compatibility Mode");
		this.launchPzCompatibility.addActionListener(this);
		this.launchPzCompatibility.setPreferredSize(new Dimension(190, 40));
		JPanel jPanel2 = new JPanel(new BorderLayout());
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(0);
		JPanel jPanel3 = new JPanel(flowLayout);
		jPanel3.add(this.launchPz);
		jPanel3.add(this.launchPzCompatibility);
		jPanel2.add(jPanel3, "West");
		JPanel jPanel4 = new JPanel(new FlowLayout());
		JLabel jLabel = new JLabel("Language");
		jPanel4.add(jLabel);
		Object[] objectArray = this.doLanguage();
		this.languageList = new JComboBox(objectArray);
		this.languageList.setSelectedIndex(this.getLanguageIndex());
		this.languageList.addActionListener(this);
		jPanel4.add(this.languageList);
		jPanel2.add(jPanel4, "East");
		this.mainFrame.getContentPane().add(jPanel2, "South");
		hTMLEditorKit.getStyleSheet().addRule("a {text-decoration: none}");
		hTMLEditorKit.getStyleSheet().addRule("a {color: #8d2123}");
		this.mainFrame.setLocationRelativeTo((Component)null);
		this.mainFrame.setVisible(true);
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
		ZomboidLauncherSwing.args = stringArray;
		try {
			Core.getInstance().loadOptions();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		instance.doComponents();
	}

	public void actionPerformed(ActionEvent actionEvent) {
		Object object = actionEvent.getSource();
		if (object == this.launchPz) {
			this.mainFrame.dispose();
			MainScreenState.main(ZomboidLauncherSwing.args);
		} else if (object == this.launchPzCompatibility) {
			String[] stringArray = new String[ZomboidLauncherSwing.args.length + 1];
			this.mainFrame.dispose();
			for (int int1 = 0; int1 < ZomboidLauncherSwing.args.length; ++int1) {
				stringArray[int1] = ZomboidLauncherSwing.args[int1];
			}

			stringArray[ZomboidLauncherSwing.args.length] = "safemode";
			MainScreenState.main(stringArray);
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

	class ImagePanel extends JPanel {
		private Image image;
		private boolean tile;

		ImagePanel(Image image) {
			this.image = image;
		}

		public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			int int1 = this.image.getWidth(this);
			int int2 = this.image.getHeight(this);
			if (int1 > 0 && int2 > 0) {
				for (int int3 = 0; int3 < this.getWidth(); int3 += int1) {
					for (int int4 = 0; int4 < this.getHeight(); int4 += int2) {
						graphics.drawImage(this.image, int3, int4, int1, int2, this);
					}
				}
			}
		}
	}
}
