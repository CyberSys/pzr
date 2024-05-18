package zombie;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.Display;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.gameStates.MainScreenState;

public class FrameLoader extends Frame {
   private static final String KEY_MAINWINDOW_X = "mainwindow.x";
   private static final String KEY_MAINWINDOW_Y = "mainwindow.y";
   private static final String KEY_MAINWINDOW_WIDTH = "mainwindow.width";
   private static final String KEY_MAINWINDOW_HEIGHT = "mainwindow.height";
   public static Canvas canvas;
   public static String IP;
   final AtomicReference newCanvasSize = new AtomicReference();
   public static volatile boolean closeRequested;
   public static File makefile = null;
   public static boolean bServer = false;
   public static boolean bClient = false;
   public static boolean bFullscreen = false;
   public static int FullX = 960;
   public static int FullY = 540;
   static Cursor cur;
   public static boolean bDedicated = false;
   public static final String SUN_JAVA_COMMAND = "sun.java.command";

   public FrameLoader() {
      super("Project Zomboid Alpha " + MainScreenState.Version);
      canvas = new Canvas();
      canvas.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent var1) {
            FrameLoader.this.newCanvasSize.set(FrameLoader.canvas.getSize());
         }
      });
      this.add(canvas, "Center");
      String var1 = GameWindow.getCacheDir() + File.separator;
      File var2 = new File(var1);
      if (!var2.exists()) {
         var2.mkdirs();
      }

      var1 = var1 + "2133243254543.log";
      makefile = new File(var1);
      if (LWJGLUtil.getPlatform() == 3) {
         this.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent var1) {
               FrameLoader.canvas.requestFocusInWindow();
            }
         });
      }

      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent var1) {
            FrameLoader.closeRequested = true;
         }
      });
      this.setIgnoreRepaint(true);
   }

   public static void main(String[] var0) throws Exception {
      LuaManager.init();
      LuaManager.LoadDirBase();
      ZomboidGlobals.Load();
      String var1 = System.getProperty("server");
      String var2 = System.getProperty("client");
      String var3 = System.getProperty("fullscreen");
      String var4 = System.getProperty("debug");
      String var5 = System.getProperty("xres");
      String var6 = System.getProperty("yres");
      if (var3 != null) {
         bFullscreen = true;
      }

      if (var4 != null) {
         Core.bDebug = true;
      }

      if (var5 != null) {
         FullX = Integer.parseInt(var5);
      }

      if (var6 != null) {
         FullY = Integer.parseInt(var6);
      }

      String var7 = System.getProperty("graphiclevel");
      if (var7 != null) {
         Core.getInstance().nGraphicLevel = Integer.parseInt(var7);
      }

      if (var1 != null && var1.equals("true")) {
         bServer = true;
      }

      if (var2 != null) {
      }

      if (bServer) {
      }

      try {
         System.setProperty("org.lwjgl.input.Mouse.allowNegativeMouseCoords", "true");
         System.setProperty("sun.java2d.noddraw", "true");
         System.setProperty("sun.java2d.opengl", "false");
         System.setProperty("sun.java2d.d3d", "false");
         System.setProperty("sun.java2d.pmoffscreen", "false");
         System.setProperty("sun.awt.noerasebackground", "true");
      } catch (Throwable var19) {
      }

      int var8 = Display.getDesktopDisplayMode().getWidth();
      int var9 = Display.getDesktopDisplayMode().getHeight();
      final Preferences var10 = Preferences.userNodeForPackage(FrameLoader.class);
      int var11 = Math.max(400, Math.min(var8, var10.getInt("mainwindow.width", var8 * 4 / 5)));
      int var12 = Math.max(300, Math.min(var9, var10.getInt("mainwindow.height", var9 * 4 / 5)));
      final FrameLoader var13 = new FrameLoader();
      var13.setSize(var11, var12);
      String var14 = var10.get("mainwindow.x", (String)null);
      String var15 = var10.get("mainwindow.y", (String)null);
      if (var14 != null && var15 != null) {
         try {
            int var16 = Math.max(0, Math.min(var8 - var11, Integer.parseInt(var14)));
            int var17 = Math.max(0, Math.min(var9 - var12, Integer.parseInt(var15)));
            var13.setLocation(var16, var17);
         } catch (Throwable var18) {
            var13.setLocationRelativeTo((Component)null);
         }
      } else {
         var13.setLocationRelativeTo((Component)null);
      }

      var13.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent var1) {
            var10.putInt("mainwindow.width", var13.getWidth());
            var10.putInt("mainwindow.height", var13.getHeight());
         }

         public void componentMoved(ComponentEvent var1) {
            var10.putInt("mainwindow.x", var13.getX());
            var10.putInt("mainwindow.y", var13.getY());
         }
      });
      cur = Cursor.getPredefinedCursor(0);
      var13.setVisible(true);
      var13.run();
      var13.dispose();
      System.exit(0);
   }

   void run() {
      try {
         boolean var1 = false;
         canvas.setIgnoreRepaint(true);
         if (!bFullscreen) {
            Display.setParent(canvas);
         }

         if (!bServer) {
            try {
               if (bFullscreen) {
                  Core.getInstance().init(FullX, FullY);
               } else {
                  Core.getInstance().init(canvas.getWidth(), canvas.getHeight());
               }
            } catch (LWJGLException var9) {
               if (!var9.getMessage().equals("Pixel format not accelerated")) {
                  throw var9;
               }

               try {
                  System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
                  Core.getInstance().init(canvas.getWidth(), canvas.getHeight());
                  var1 = true;
               } catch (LWJGLException var6) {
                  throw var6;
               } catch (SecurityException var7) {
                  throw var9;
               }
            }
         }

         if (!bServer) {
            GameWindow.initApplet();
         }
      } catch (Exception var10) {
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var10);
      }

      if (bServer) {
         System.exit(0);
      } else {
         LuaEventManager.triggerEvent("OnGameBoot");

         while(!Display.isCloseRequested() && !closeRequested) {
            try {
               this.setCursor(cur);
               Dimension var11 = (Dimension)this.newCanvasSize.getAndSet((Object)null);
               if (var11 != null) {
                  Core.getInstance().setScreenSize(var11.width, var11.height);
               }

               Display.update();
               Display.sync(60);
               GameWindow.logic();
            } catch (Exception var8) {
               JOptionPane.showMessageDialog((Component)null, var8.getStackTrace(), "Error: " + var8.getMessage(), 0);
               Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var8);
               break;
            }
         }

         try {
            GameWindow.save(true);
         } catch (FileNotFoundException var4) {
            Logger.getLogger(FrameLoader.class.getName()).log(Level.SEVERE, (String)null, var4);
         } catch (IOException var5) {
            Logger.getLogger(FrameLoader.class.getName()).log(Level.SEVERE, (String)null, var5);
         }

         Display.destroy();
         System.exit(0);
      }
   }

   public static void restartApplication(Runnable var0) throws IOException {
      try {
         String var1 = System.getProperty("java.home") + "/bin/java";
         List var2 = ManagementFactory.getRuntimeMXBean().getInputArguments();
         StringBuffer var3 = new StringBuffer();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            if (!var5.contains("-agentlib")) {
               var3.append(var5);
               var3.append(" ");
            }
         }

         final StringBuffer var8 = new StringBuffer("\"" + var1 + "\" " + var3);
         String[] var9 = System.getProperty("sun.java.command").split(" ");
         if (var9[0].endsWith(".jar")) {
            var8.append("-jar " + (new File(var9[0])).getPath());
         } else {
            var8.append("-cp \"" + System.getProperty("java.class.path") + "\" " + var9[0]);
         }

         for(int var6 = 1; var6 < var9.length; ++var6) {
            var8.append(" ");
            var8.append(var9[var6]);
         }

         Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
               try {
                  Runtime.getRuntime().exec(var8.toString());
               } catch (IOException var2) {
                  var2.printStackTrace();
               }

            }
         });
         if (var0 != null) {
            var0.run();
         }

         Display.destroy();
         System.exit(0);
      } catch (Exception var7) {
         var7.printStackTrace();
         throw new IOException("Error while trying to restart the application", var7);
      }
   }
}
