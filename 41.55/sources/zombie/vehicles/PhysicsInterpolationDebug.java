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


public class PhysicsInterpolationDebug extends JPanel {
	private static final int PREF_W = 400;
	private static final int PREF_H = 200;
	private static final int BORDER_GAP = 30;
	private static final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
	private static final Stroke GRAPH_STROKE = new BasicStroke(3.0F);
	private static final int GRAPH_POINT_WIDTH = 12;
	private static final int Y_HATCH_CNT = 10;
	public VehicleInterpolation idata;
	private static int time_divider = 1000;
	private long ci_time;
	private float ci_x;
	private float ci_y;
	private String ci_user;
	private List graphPoints_x = new ArrayList();
	private List graphPoints_y = new ArrayList();
	private List graphPoints_i = new ArrayList();
	private static PhysicsInterpolationDebug mainPanel;
	private static JFrame frame;

	public PhysicsInterpolationDebug(VehicleInterpolation vehicleInterpolation) {
		this.idata = vehicleInterpolation;
	}

	public void addCurrentData(long long1, float float1, float float2, String string) {
		this.ci_time = long1;
		this.ci_x = float1;
		this.ci_y = float2;
		this.ci_user = string;
	}

	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		if (this.idata != null) {
			Graphics2D graphics2D = (Graphics2D)graphics;
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			double double1 = ((double)this.getWidth() - 60.0) / (double)(time_divider - 1);
			double double2 = ((double)this.getHeight() - 60.0) / 99.0;
			this.graphPoints_x.clear();
			this.graphPoints_y.clear();
			this.graphPoints_i.clear();
			int int1;
			int int2;
			int int3;
			for (int1 = 0; int1 < this.idata.dataList.size(); ++int1) {
				VehicleInterpolationData vehicleInterpolationData = (VehicleInterpolationData)this.idata.dataList.get(int1);
				int2 = (int)((double)(vehicleInterpolationData.time / 1000000L % (long)time_divider) * double1 + 30.0);
				int3 = (int)((double)(vehicleInterpolationData.x * 10.0F % 100.0F) * double2 + 30.0);
				this.graphPoints_x.add(new Point(int2, int3));
				int3 = (int)((double)(vehicleInterpolationData.y * 10.0F % 100.0F) * double2 + 30.0);
				this.graphPoints_y.add(new Point(int2, int3));
			}

			int1 = (int)((double)(this.ci_time / 1000000L % (long)time_divider) * double1 + 30.0);
			int int4 = (int)((double)(this.ci_x * 10.0F % 100.0F) * double2 + 30.0);
			this.graphPoints_i.add(new Point(int1, int4));
			int4 = (int)((double)(this.ci_y * 10.0F % 100.0F) * double2 + 30.0);
			this.graphPoints_i.add(new Point(int1, int4));
			graphics2D.setColor(Color.black);
			graphics2D.drawLine(30, this.getHeight() - 30, 30, 30);
			graphics2D.drawLine(30, this.getHeight() - 30, this.getWidth() - 30, this.getHeight() - 30);
			int int5;
			for (int2 = 0; int2 < 10; ++int2) {
				byte byte1 = 30;
				byte byte2 = 42;
				int5 = this.getHeight() - ((int2 + 1) * (this.getHeight() - 60) / 10 + 30);
				graphics2D.drawLine(byte1, int5, byte2, int5);
			}

			Stroke stroke = graphics2D.getStroke();
			graphics2D.setColor(Color.red);
			graphics2D.setStroke(GRAPH_STROKE);
			int int6;
			int int7;
			int int8;
			for (int3 = 0; int3 < this.graphPoints_x.size() - 1; ++int3) {
				int8 = ((Point)this.graphPoints_x.get(int3)).x;
				int5 = ((Point)this.graphPoints_x.get(int3)).y;
				int6 = ((Point)this.graphPoints_x.get(int3 + 1)).x;
				int7 = ((Point)this.graphPoints_x.get(int3 + 1)).y;
				graphics2D.drawLine(int8, int5, int6, int7);
			}

			graphics2D.setColor(Color.green);
			graphics2D.setStroke(GRAPH_STROKE);
			for (int3 = 0; int3 < this.graphPoints_y.size() - 1; ++int3) {
				int8 = ((Point)this.graphPoints_y.get(int3)).x;
				int5 = ((Point)this.graphPoints_y.get(int3)).y;
				int6 = ((Point)this.graphPoints_y.get(int3 + 1)).x;
				int7 = ((Point)this.graphPoints_y.get(int3 + 1)).y;
				graphics2D.drawLine(int8, int5, int6, int7);
			}

			graphics2D.setStroke(stroke);
			graphics2D.setColor(GRAPH_POINT_COLOR);
			for (int3 = 0; int3 < this.graphPoints_i.size(); ++int3) {
				int8 = ((Point)this.graphPoints_i.get(int3)).x - 6;
				int5 = ((Point)this.graphPoints_i.get(int3)).y - 6;
				byte byte3 = 12;
				byte byte4 = 12;
				graphics2D.fillOval(int8, int5, byte3, byte4);
			}

			graphics2D.setColor(Color.black);
			graphics2D.drawString("dataList.size: " + this.idata.dataList.size(), 30, 15);
			graphics2D.drawString("Current t=" + this.ci_time + " x=" + this.ci_x + " y=" + this.ci_y + " user=" + this.ci_user, 30, 30);
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 200);
	}

	public static void createAndShowGui() {
		mainPanel = new PhysicsInterpolationDebug((VehicleInterpolation)null);
		frame = new JFrame("DrawGraph");
		frame.setDefaultCloseOperation(3);
		frame.setLayout(new GridLayout(1, 1));
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	public static void updateGui() {
		ArrayList arrayList = VehicleManager.instance.getVehicles();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			BaseVehicle baseVehicle = (BaseVehicle)arrayList.get(int1);
			if (baseVehicle.getPassenger(0).character != null && !baseVehicle.isKeyboardControlled()) {
				mainPanel.idata = baseVehicle.interpolation;
				break;
			}
		}

		frame.repaint();
	}

	public static void addCurrentDataS(long long1, float float1, float float2, String string) {
		mainPanel.addCurrentData(long1, float1, float2, string);
	}
}
