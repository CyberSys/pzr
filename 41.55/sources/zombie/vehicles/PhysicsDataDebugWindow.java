package zombie.vehicles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class PhysicsDataDebugWindow extends JPanel {
	private static final int PREF_W = 400;
	private static final int PREF_H = 200;
	private static final int BORDER_GAP = 30;
	private static final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
	private static final Stroke GRAPH_STROKE = new BasicStroke(3.0F);
	private static final int GRAPH_POINT_WIDTH = 12;
	private static final int Y_HATCH_CNT = 10;
	private static int time_divider = 1000;
	private List graphPoints_x = new ArrayList();
	private List graphPoints_y = new ArrayList();
	private static PhysicsDataDebugWindow mainPanel;
	private static JFrame frame;

	public void addCurrentData(long long1, float float1, float float2) {
		if (this.graphPoints_x.size() > 100) {
			this.graphPoints_x.clear();
			this.graphPoints_y.clear();
		}

		double double1 = ((double)this.getWidth() - 60.0) / (double)(time_divider - 1);
		double double2 = ((double)this.getHeight() - 60.0) / 99.0;
		int int1 = (int)((double)(long1 % (long)time_divider) * double1 + 30.0);
		int int2 = (int)((double)(float1 * 10.0F % 100.0F) * double2 + 30.0);
		this.graphPoints_x.add(new Point(int1, int2));
		int2 = (int)((double)(float2 * 10.0F % 100.0F) * double2 + 30.0);
		this.graphPoints_y.add(new Point(int1, int2));
	}

	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D graphics2D = (Graphics2D)graphics;
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setColor(Color.black);
		graphics2D.drawLine(30, this.getHeight() - 30, 30, 30);
		graphics2D.drawLine(30, this.getHeight() - 30, this.getWidth() - 30, this.getHeight() - 30);
		int int1;
		for (int int2 = 0; int2 < 10; ++int2) {
			byte byte1 = 30;
			byte byte2 = 42;
			int1 = this.getHeight() - ((int2 + 1) * (this.getHeight() - 60) / 10 + 30);
			graphics2D.drawLine(byte1, int1, byte2, int1);
		}

		Stroke stroke = graphics2D.getStroke();
		graphics2D.setColor(Color.red);
		graphics2D.setStroke(GRAPH_STROKE);
		int int3;
		int int4;
		int int5;
		int int6;
		for (int5 = 0; int5 < this.graphPoints_x.size() - 1; ++int5) {
			int6 = ((Point)this.graphPoints_x.get(int5)).x;
			int1 = ((Point)this.graphPoints_x.get(int5)).y;
			int3 = ((Point)this.graphPoints_x.get(int5 + 1)).x;
			int4 = ((Point)this.graphPoints_x.get(int5 + 1)).y;
			graphics2D.drawLine(int6, int1, int3, int4);
		}

		graphics2D.setColor(Color.green);
		graphics2D.setStroke(GRAPH_STROKE);
		for (int5 = 0; int5 < this.graphPoints_y.size() - 1; ++int5) {
			int6 = ((Point)this.graphPoints_y.get(int5)).x;
			int1 = ((Point)this.graphPoints_y.get(int5)).y;
			int3 = ((Point)this.graphPoints_y.get(int5 + 1)).x;
			int4 = ((Point)this.graphPoints_y.get(int5 + 1)).y;
			graphics2D.drawLine(int6, int1, int3, int4);
		}

		graphics2D.setColor(Color.black);
	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 200);
	}

	public static void createAndShowGui() {
		mainPanel = new PhysicsDataDebugWindow();
		frame = new JFrame("PhysicsData");
		frame.setDefaultCloseOperation(3);
		frame.setLayout(new GridLayout(1, 1));
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	public static void updateGui() {
		frame.repaint();
	}

	public static void addCurrentDataS(long long1, float float1, float float2) {
		mainPanel.addCurrentData(long1, float1, float2);
		updateGui();
	}
}
