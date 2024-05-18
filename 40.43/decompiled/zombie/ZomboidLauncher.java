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

public class ZomboidLauncher implements ActionListener, HyperlinkListener {
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
      Dimension var1 = Toolkit.getDefaultToolkit().getScreenSize();
      double var2 = var1.getWidth();
      double var4 = var1.getHeight();
      short var6 = 1180;
      short var7 = 768;
      if (var2 < 1180.0D || var4 < 768.0D) {
         var6 = 1024;
         var7 = 680;
      }

      this.mainFrame.setLocationByPlatform(true);
      this.mainFrame.setSize(var6, var7);
      JPanel var8 = new JPanel(new BorderLayout());
      var8.setOpaque(false);
      this.webBrowser = new JEditorPane();
      this.webBrowser.setContentType("text/html");
      this.webBrowser.setEditable(false);
      this.webBrowser.addHyperlinkListener(this);
      HTMLEditorKit var9 = new HTMLEditorKit();
      StyleSheet var10 = var9.getStyleSheet();
      var10.addRule("a {text-decoration: none}");

      try {
         var9.getStyleSheet().importStyleSheet(new URL("http://projectzomboid.com/blog/wp-content/themes/projectzomboid/style.css?ver=3.7.1"));
      } catch (MalformedURLException var18) {
         var18.printStackTrace();
      }

      var10.addRule(".override { font-family: arial, verdana, sans-serif;color: black;text-decoration: bold;text-size: 1em; }");
      var10.addRule(".override2 { font-family: arial, verdana, sans-serif; color: black; font-style: italic; text-size: 1em; }");
      this.webBrowser.setEditorKit(var9);
      var8.add(this.webBrowser);

      try {
         this.webBrowser.setPage(this.url.toURI().toURL());
      } catch (IOException var17) {
         var17.printStackTrace();
      }

      this.webBrowser.setVisible(true);
      this.mainFrame.getContentPane().add(var8, "Center");
      this.launchPz = new JButton("Launch Project Zomboid");
      this.launchPz.addActionListener(this);
      this.launchPz.setPreferredSize(new Dimension(190, 40));
      this.launchPzCompatibility = new JButton("Compatibility Mode");
      this.launchPzCompatibility.addActionListener(this);
      this.launchPzCompatibility.setPreferredSize(new Dimension(190, 40));
      JPanel var11 = new JPanel(new BorderLayout());
      FlowLayout var12 = new FlowLayout();
      var12.setAlignment(0);
      JPanel var13 = new JPanel(var12);
      var13.add(this.launchPz);
      var13.add(this.launchPzCompatibility);
      var11.add(var13, "West");
      JPanel var14 = new JPanel(new FlowLayout());
      JLabel var15 = new JLabel("Language");
      var14.add(var15);
      Object[] var16 = this.doLanguage();
      this.languageList = new JComboBox(var16);
      this.languageList.setSelectedIndex(this.getLanguageIndex());
      this.languageList.addActionListener(this);
      var14.add(this.languageList);
      var11.add(var14, "East");
      this.mainFrame.getContentPane().add(var11, "South");
      var9.getStyleSheet().addRule("a {text-decoration: none}");
      var9.getStyleSheet().addRule("a {color: #8d2123}");
      this.mainFrame.setLocationRelativeTo((Component)null);
      this.mainFrame.setVisible(true);
   }

   private int getLanguageIndex() {
      for(int var1 = 0; var1 < Translator.getAvailableLanguage().size(); ++var1) {
         if (Translator.getAvailableLanguage().get(var1) == Translator.getLanguage()) {
            return var1;
         }
      }

      return 0;
   }

   private Object[] doLanguage() {
      Object[] var1 = new Object[Translator.getAvailableLanguage().size()];

      for(int var2 = 0; var2 < Translator.getAvailableLanguage().size(); ++var2) {
         var1[var2] = ((Language)Translator.getAvailableLanguage().get(var2)).text();
      }

      return var1;
   }

   public static void main(String[] var0) {
      instance = new ZomboidLauncherSwing();
      ZomboidLauncherSwing.args = var0;

      try {
         Core.getInstance().loadOptions();
      } catch (IOException var2) {
         var2.printStackTrace();
      }

      instance.doComponents();
   }

   public void actionPerformed(ActionEvent var1) {
      Object var2 = var1.getSource();
      if (var2 == this.launchPz) {
         this.mainFrame.dispose();
         MainScreenState.main(ZomboidLauncherSwing.args);
      } else if (var2 == this.launchPzCompatibility) {
         String[] var3 = new String[ZomboidLauncherSwing.args.length + 1];
         this.mainFrame.dispose();

         for(int var4 = 0; var4 < ZomboidLauncherSwing.args.length; ++var4) {
            var3[var4] = ZomboidLauncherSwing.args[var4];
         }

         var3[ZomboidLauncherSwing.args.length] = "safemode";
         MainScreenState.main(var3);
      } else if (var2 == this.languageList) {
         Translator.setLanguage(this.languageList.getSelectedIndex());

         try {
            Core.getInstance().saveOptions();
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      }

   }

   public void openURl(String var1) {
      Desktop var2 = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
      if (var2 != null && var2.isSupported(Action.BROWSE)) {
         try {
            URI var3 = new URI(var1);
            var2.browse(var3);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

   }

   public void hyperlinkUpdate(HyperlinkEvent var1) {
      EventType var2 = var1.getEventType();
      if (var2 == EventType.ACTIVATED) {
         if (var1 instanceof HTMLFrameHyperlinkEvent) {
            HTMLFrameHyperlinkEvent var3 = (HTMLFrameHyperlinkEvent)var1;
            HTMLDocument var4 = (HTMLDocument)this.webBrowser.getDocument();
            var4.processHTMLFrameHyperlinkEvent(var3);
         } else {
            this.openURl(var1.getURL().toString());
         }
      }

   }

   class ImagePanel extends JPanel {
      private Image image;
      private boolean tile;

      ImagePanel(Image var2) {
         this.image = var2;
      }

      public void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         int var2 = this.image.getWidth(this);
         int var3 = this.image.getHeight(this);
         if (var2 > 0 && var3 > 0) {
            for(int var4 = 0; var4 < this.getWidth(); var4 += var2) {
               for(int var5 = 0; var5 < this.getHeight(); var5 += var3) {
                  var1.drawImage(this.image, var4, var5, var2, var3, this);
               }
            }
         }

      }
   }
}
