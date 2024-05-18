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
		canvas.addComponentListener(new ComponentAdapter(){
			
			public void componentResized(ComponentEvent string) {
				FrameLoader.this.newCanvasSize.set(FrameLoader.canvas.getSize());
			}
		});
		this.add(canvas, "Center");
		String string = GameWindow.getCacheDir() + File.separator;
		File file = new File(string);
		if (!file.exists()) {
			file.mkdirs();
		}

		string = string + "2133243254543.log";
		makefile = new File(string);
		if (LWJGLUtil.getPlatform() == 3) {
			this.addWindowFocusListener(new WindowAdapter(){
				
				public void windowGainedFocus(WindowEvent string) {
					FrameLoader.canvas.requestFocusInWindow();
				}
			});
		}

		this.addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent string) {
				FrameLoader.closeRequested = true;
			}
		});
		this.setIgnoreRepaint(true);
	}

	public static void main(String[] stringArray) throws Exception {
		LuaManager.init();
		LuaManager.LoadDirBase();
		ZomboidGlobals.Load();
		String string = System.getProperty("server");
		String string2 = System.getProperty("client");
		String string3 = System.getProperty("fullscreen");
		String string4 = System.getProperty("debug");
		String string5 = System.getProperty("xres");
		String string6 = System.getProperty("yres");
		if (string3 != null) {
			bFullscreen = true;
		}

		if (string4 != null) {
			Core.bDebug = true;
		}

		if (string5 != null) {
			FullX = Integer.parseInt(string5);
		}

		if (string6 != null) {
			FullY = Integer.parseInt(string6);
		}

		String string7 = System.getProperty("graphiclevel");
		if (string7 != null) {
			Core.getInstance().nGraphicLevel = Integer.parseInt(string7);
		}

		if (string != null && string.equals("true")) {
			bServer = true;
		}

		if (string2 != null) {
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
		} catch (Throwable throwable) {
		}

		int int1 = Display.getDesktopDisplayMode().getWidth();
		int int2 = Display.getDesktopDisplayMode().getHeight();
		final Preferences preferences = Preferences.userNodeForPackage(FrameLoader.class);
		int int3 = Math.max(400, Math.min(int1, preferences.getInt("mainwindow.width", int1 * 4 / 5)));
		int int4 = Math.max(300, Math.min(int2, preferences.getInt("mainwindow.height", int2 * 4 / 5)));
		final FrameLoader frameLoader = new FrameLoader();
		frameLoader.setSize(int3, int4);
		String string8 = preferences.get("mainwindow.x", (String)null);
		String string9 = preferences.get("mainwindow.y", (String)null);
		if (string8 != null && string9 != null) {
			try {
				int int5 = Math.max(0, Math.min(int1 - int3, Integer.parseInt(string8)));
				int int6 = Math.max(0, Math.min(int2 - int4, Integer.parseInt(string9)));
				frameLoader.setLocation(int5, int6);
			} catch (Throwable throwable2) {
				frameLoader.setLocationRelativeTo((Component)null);
			}
		} else {
			frameLoader.setLocationRelativeTo((Component)null);
		}

		frameLoader.addComponentListener(new ComponentAdapter(){
			
			public void componentResized(ComponentEvent string) {
				preferences.putInt("mainwindow.width", frameLoader.getWidth());
				preferences.putInt("mainwindow.height", frameLoader.getHeight());
			}

			
			public void componentMoved(ComponentEvent string) {
				preferences.putInt("mainwindow.x", frameLoader.getX());
				preferences.putInt("mainwindow.y", frameLoader.getY());
			}
		});
		cur = Cursor.getPredefinedCursor(0);
		frameLoader.setVisible(true);
		frameLoader.run();
		frameLoader.dispose();
		System.exit(0);
	}

	void run() {
		try {
			boolean boolean1 = false;
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
				} catch (LWJGLException lWJGLException) {
					if (!lWJGLException.getMessage().equals("Pixel format not accelerated")) {
						throw lWJGLException;
					}

					try {
						System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
						Core.getInstance().init(canvas.getWidth(), canvas.getHeight());
						boolean1 = true;
					} catch (LWJGLException lWJGLException2) {
						throw lWJGLException2;
					} catch (SecurityException securityException) {
						throw lWJGLException;
					}
				}
			}

			if (!bServer) {
				GameWindow.initApplet();
			}
		} catch (Exception exception) {
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
		}

		if (bServer) {
			System.exit(0);
		} else {
			LuaEventManager.triggerEvent("OnGameBoot");
			while (!Display.isCloseRequested() && !closeRequested) {
				try {
					this.setCursor(cur);
					Dimension dimension = (Dimension)this.newCanvasSize.getAndSet((Object)null);
					if (dimension != null) {
						Core.getInstance().setScreenSize(dimension.width, dimension.height);
					}

					Display.update();
					Display.sync(60);
					GameWindow.logic();
				} catch (Exception exception2) {
					JOptionPane.showMessageDialog((Component)null, exception2.getStackTrace(), "Error: " + exception2.getMessage(), 0);
					Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception2);
					break;
				}
			}

			try {
				GameWindow.save(true);
			} catch (FileNotFoundException fileNotFoundException) {
				Logger.getLogger(FrameLoader.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
			} catch (IOException ioException) {
				Logger.getLogger(FrameLoader.class.getName()).log(Level.SEVERE, (String)null, ioException);
			}

			Display.destroy();
			System.exit(0);
		}
	}

	public static void restartApplication(Runnable runnable) throws IOException {
		try {
			String string = System.getProperty("java.home") + "/bin/java";
			List list = ManagementFactory.getRuntimeMXBean().getInputArguments();
			StringBuffer stringBuffer = new StringBuffer();
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				if (!string2.contains("-agentlib")) {
					stringBuffer.append(string2);
					stringBuffer.append(" ");
				}
			}

			final StringBuffer stringBuffer2 = new StringBuffer("\"" + string + "\" " + stringBuffer);
			String[] stringArray = System.getProperty("sun.java.command").split(" ");
			if (stringArray[0].endsWith(".jar")) {
				stringBuffer2.append("-jar " + (new File(stringArray[0])).getPath());
			} else {
				stringBuffer2.append("-cp \"" + System.getProperty("java.class.path") + "\" " + stringArray[0]);
			}

			for (int int1 = 1; int1 < stringArray.length; ++int1) {
				stringBuffer2.append(" ");
				stringBuffer2.append(stringArray[int1]);
			}

			Runtime.getRuntime().addShutdownHook(new Thread(){
				
				public void run() {
					try {
						Runtime.getRuntime().exec(stringBuffer2.toString());
					} catch (IOException list) {
						list.printStackTrace();
					}
				}
			});

			if (runnable != null) {
				runnable.run();
			}

			Display.destroy();
			System.exit(0);
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new IOException("Error while trying to restart the application", exception);
		}
	}
}
