package zombie;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.iso.IsoCamera;


public class GameApplet extends Applet implements MouseListener,MouseMotionListener {
	public static boolean left = false;
	public static boolean middle = false;
	public static int mx = 0;
	public static int my = 0;
	public static boolean right = false;
	Canvas display_parent;
	Canvas display_parent_full;
	Frame frame = new Frame();
	Thread gameThread;
	boolean keyDown;
	boolean running;
	private float angle;
	private int gear1;
	private int gear2;
	private int gear3;
	private boolean mouseButtonDown;
	private int prevMouseX;
	private int prevMouseY;
	private float view_rotx = 20.0F;
	private float view_roty = 30.0F;
	private float view_rotz;

	public void destroy() {
		this.remove(this.display_parent);
		super.destroy();
		DebugLog.log("Clear up");
	}

	public void drawLoop() {
		GameWindow.render();
	}

	public void gameLoop() {
		long long1 = System.currentTimeMillis() + 5000L;
		long long2 = 0L;
		while (this.running) {
			try {
				Core.getInstance().setScreenSize(this.getWidth(), this.getHeight());
				Display.update();
				this.drawLoop();
				if (IsoCamera.CamCharacter == null || !IsoPlayer.instance.isAsleep()) {
					Display.sync(60);
				}

				GameWindow.logic();
			} catch (Exception exception) {
				JOptionPane.showMessageDialog((Component)null, exception.getStackTrace(), "Error", 0);
				Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
				break;
			}
		}

		Display.destroy();
	}

	public void init() {
		this.setLayout(new BorderLayout());
		try {
			this.display_parent = new Canvas(){
				
				public void addNotify() {
					super.addNotify();
					GameApplet.this.startLWJGL();
				}

				
				public void removeNotify() {
					GameApplet.this.stopLWJGL();
					super.removeNotify();
				}
			};

			this.display_parent.setSize(this.getWidth(), this.getHeight());
			this.add(this.display_parent);
			this.display_parent.setFocusable(true);
			this.display_parent.requestFocus();
			this.display_parent.setIgnoreRepaint(true);
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			this.display_parent.setCursor((Cursor)null);
			this.setVisible(true);
		} catch (Exception exception) {
			System.err.println(exception);
			throw new RuntimeException("Unable to create display");
		}
	}

	public void mouseClicked(MouseEvent mouseEvent) {
	}

	public void mouseDragged(MouseEvent mouseEvent) {
		mx = mouseEvent.getX();
		my = mouseEvent.getY();
		mouseEvent.consume();
	}

	public void mouseEntered(MouseEvent mouseEvent) {
		GameWindow.bDrawMouse = true;
	}

	public void mouseExited(MouseEvent mouseEvent) {
		GameWindow.bDrawMouse = false;
	}

	public void mouseMoved(MouseEvent mouseEvent) {
		mx = mouseEvent.getX();
		my = mouseEvent.getY();
		mouseEvent.consume();
	}

	public void mousePressed(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == 1) {
			left = true;
		}

		if (mouseEvent.getButton() == 2) {
			right = true;
		}

		if (mouseEvent.getButton() == 3) {
			middle = true;
		}
	}

	public void mouseReleased(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == 1) {
			left = false;
		}

		if (mouseEvent.getButton() == 2) {
			right = false;
		}

		if (mouseEvent.getButton() == 3) {
			middle = false;
		}
	}

	public void start() {
	}

	public void startLWJGL() {
		this.gameThread = new Thread(){
			
			public void run() {
				GameApplet.this.running = true;
				try {
					Display.setParent(GameApplet.this.display_parent);
					Display.create();
					Core.getInstance().init(800, 600, 800, 600, GameApplet.this.display_parent, GameApplet.this.display_parent_full);
					GameApplet.this.initGL();
					try {
						GameWindow.initApplet();
					} catch (Exception var2) {
						Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var2);
					}
				} catch (LWJGLException var3) {
					var3.printStackTrace();
				}

				GameApplet.this.gameLoop();
			}
		};
		this.gameThread.start();
	}

	public void stop() {
	}

	protected void initGL() {
	}

	private void stopLWJGL() {
		this.running = false;
		try {
			this.gameThread.join();
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}
}
