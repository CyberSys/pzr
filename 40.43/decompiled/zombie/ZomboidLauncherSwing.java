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

public class ZomboidLauncherSwing implements ActionListener, HyperlinkListener {
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
      Dimension var1 = Toolkit.getDefaultToolkit().getScreenSize();
      double var2 = var1.getWidth();
      double var4 = var1.getHeight();
      short var6 = 1280;
      short var7 = 900;
      if (var2 < 1280.0D || var4 < 900.0D) {
         var6 = 1024;
         var7 = 680;
      }

      this.mainFrame.setLocationByPlatform(true);
      this.mainFrame.setSize(var6, var7);
      this.launchPz = new JButton("Launch Project Zomboid");
      this.launchPz.addActionListener(this);
      this.launchPz.setPreferredSize(new Dimension(180, 40));
      this.launchPzCompatibility = new JButton("Compatibility Mode");
      this.launchPzCompatibility.addActionListener(this);
      this.launchPzCompatibility.setPreferredSize(new Dimension(180, 40));
      JPanel var8 = new JPanel(new BorderLayout());
      FlowLayout var9 = new FlowLayout();
      var9.setAlignment(0);
      JPanel var10 = new JPanel(var9);
      var10.add(this.launchPz);
      var10.add(this.launchPzCompatibility);
      var8.add(var10, "West");
      JPanel var11 = new JPanel(new FlowLayout());
      JLabel var12 = new JLabel("Language");
      var11.add(var12);
      Object[] var13 = this.doLanguage();
      this.languageList = new JComboBox(var13);
      this.languageList.setSelectedIndex(this.getLanguageIndex());
      this.languageList.addActionListener(this);
      var11.add(this.languageList);
      var8.add(var11, "East");
      this.mainFrame.getContentPane().add(var8, "South");
      JPanel var14 = new JPanel(new BorderLayout());
      var14.setOpaque(false);
      this.webBrowser = new JEditorPane();
      this.webBrowser.setContentType("text/html");
      this.webBrowser.setEditable(false);
      this.webBrowser.addHyperlinkListener(this);
      HTMLEditorKit var15 = new HTMLEditorKit();
      StyleSheet var16 = var15.getStyleSheet();
      var16.addRule("a {text-decoration: none}");

      try {
         var15.getStyleSheet().importStyleSheet((new File("media/launcher/style.css?ver=3.7.1")).toURI().toURL());
      } catch (MalformedURLException var19) {
         var19.printStackTrace();
      }

      var16.addRule(".override { font-family: arial, verdana, sans-serif;color: black;text-decoration: bold;text-size: 1em; }");
      var16.addRule(".override2 { font-family: arial, verdana, sans-serif; color: black; font-style: italic; text-size: 1em; }");
      this.webBrowser.setEditorKit(var15);
      var14.add(this.webBrowser);

      try {
         this.webBrowser.setPage(this.url.toURI().toURL());
      } catch (IOException var18) {
         var18.printStackTrace();
      }

      this.webBrowser.setVisible(true);
      this.mainFrame.getContentPane().add(var14, "Center");
      var15.getStyleSheet().addRule("a {text-decoration: none}");
      var15.getStyleSheet().addRule("a {color: #8d2123}");
      this.mainFrame.setLocationRelativeTo((Component)null);
      this.mainFrame.setVisible(true);
      this.mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent var1) {
            if (ZomboidLauncherSwing.this.launchedProcess != null) {
               ZomboidLauncherSwing.this.launchedProcess.destroy();
            }

            ZomboidLauncherSwing.this.mainFrame.dispose();
            System.exit(0);
         }

         public void windowClosed(WindowEvent var1) {
            if (ZomboidLauncherSwing.this.launchedProcess != null) {
               ZomboidLauncherSwing.this.launchedProcess.destroy();
            }

            ZomboidLauncherSwing.this.mainFrame.dispose();
            System.exit(0);
         }
      });
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
      args = var0;

      try {
         Core.getInstance().loadOptions();
      } catch (IOException var2) {
         var2.printStackTrace();
      }

      instance.doComponents();
   }

   public void actionPerformed(ActionEvent var1) {
      Object var2 = var1.getSource();
      Runtime var3;
      int var5;
      final Process var10;
      if (var2 == this.launchPz) {
         var3 = Runtime.getRuntime();

         try {
            String var4 = "";

            for(var5 = 0; var5 < args.length; ++var5) {
               var4 = var4 + " " + args[var5];
            }

            var10 = var3.exec("java -Xms768m -Xmx768m -Djava.library.path=./ -cp lwjgl.jar;lwjgl_util.jar;./ zombie.gameStates.MainScreenState " + var4);
            this.launchedProcess = var10;
            (new Thread() {
               public void run() {
                  try {
                     BufferedReader var1 = new BufferedReader(new InputStreamReader(var10.getInputStream()));
                     String var2 = "";

                     try {
                        while((var2 = var1.readLine()) != null) {
                           System.out.println(var2);
                        }
                     } finally {
                        var1.close();
                     }
                  } catch (IOException var7) {
                     var7.printStackTrace();
                  }

               }
            }).start();
            (new Thread() {
               public void run() {
                  try {
                     BufferedReader var1 = new BufferedReader(new InputStreamReader(var10.getErrorStream()));
                     String var2 = "";

                     try {
                        while((var2 = var1.readLine()) != null) {
                           System.out.println(var2);
                        }
                     } finally {
                        var1.close();
                     }
                  } catch (IOException var7) {
                     var7.printStackTrace();
                  }

               }
            }).start();
         } catch (Exception var8) {
            System.out.println(var8);
         }
      } else if (var2 == this.launchPzCompatibility) {
         var3 = Runtime.getRuntime();
         String[] var9 = new String[args.length + 1];

         for(var5 = 0; var5 < args.length; ++var5) {
            var9[var5] = args[var5];
         }

         var9[args.length] = "safemode";

         try {
            var10 = var3.exec("java -Xms768m -Xmx768m -Djava.library.path=./ -cp lwjgl.jar;lwjgl_util.jar;./ zombie.gameStates.MainScreenState " + var9);
            this.launchedProcess = var10;
            (new Thread() {
               public void run() {
                  try {
                     BufferedReader var1 = new BufferedReader(new InputStreamReader(var10.getInputStream()));
                     String var2 = "";

                     try {
                        while((var2 = var1.readLine()) != null) {
                           System.out.println(var2);
                        }
                     } finally {
                        var1.close();
                     }
                  } catch (IOException var7) {
                     var7.printStackTrace();
                  }

               }
            }).start();
            (new Thread() {
               public void run() {
                  try {
                     BufferedReader var1 = new BufferedReader(new InputStreamReader(var10.getErrorStream()));
                     String var2 = "";

                     try {
                        while((var2 = var1.readLine()) != null) {
                           System.out.println(var2);
                        }
                     } finally {
                        var1.close();
                     }
                  } catch (IOException var7) {
                     var7.printStackTrace();
                  }

               }
            }).start();
         } catch (Exception var7) {
            System.out.println(var7);
         }
      } else if (var2 == this.languageList) {
         Translator.setLanguage(this.languageList.getSelectedIndex());

         try {
            Core.getInstance().saveOptions();
         } catch (IOException var6) {
            var6.printStackTrace();
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
}
