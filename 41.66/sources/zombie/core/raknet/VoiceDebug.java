package zombie.core.raknet;

import fmod.FMODSoundBuffer;
import fmod.SoundBuffer;
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


public class VoiceDebug extends JPanel {
	private static final int PREF_W = 400;
	private static final int PREF_H = 200;
	private static final int BORDER_GAP = 30;
	private static final Color LINE_CURRENT_COLOR;
	private static final Color LINE_LAST_COLOR;
	private static final Color GRAPH_COLOR;
	private static final Color GRAPH_POINT_COLOR;
	private static final Stroke GRAPH_STROKE;
	private static final int GRAPH_POINT_WIDTH = 12;
	private static final int Y_HATCH_CNT = 10;
	public List scores;
	public int scores_max;
	public String title;
	public int psize;
	public int last;
	public int current;
	private static VoiceDebug mainPanel;
	private static VoiceDebug mainPanel2;
	private static VoiceDebug mainPanel3;
	private static VoiceDebug mainPanel4;
	private static JFrame frame;

	public VoiceDebug(List list, String string) {
		this.scores = list;
		this.title = string;
		this.psize = list.size();
		this.last = 5;
		this.current = 8;
		this.scores_max = 100;
	}

	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D graphics2D = (Graphics2D)graphics;
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		double double1 = ((double)this.getWidth() - 60.0) / (double)(this.scores.size() - 1);
		double double2 = ((double)this.getHeight() - 60.0) / (double)(this.scores_max - 1);
		int int1 = (int)(((double)this.getHeight() - 60.0) / 2.0);
		int int2 = (int)(1.0 / double1);
		if (int2 == 0) {
			int2 = 1;
		}

		ArrayList arrayList = new ArrayList();
		int int3;
		int int4;
		int int5;
		for (int3 = 0; int3 < this.scores.size(); int3 += int2) {
			int4 = (int)((double)int3 * double1 + 30.0);
			int5 = (int)((double)(this.scores_max - (Integer)this.scores.get(int3)) * double2 + 30.0 - (double)int1);
			arrayList.add(new Point(int4, int5));
		}

		graphics2D.setColor(Color.black);
		graphics2D.drawLine(30, this.getHeight() - 30, 30, 30);
		graphics2D.drawLine(30, this.getHeight() - 30, this.getWidth() - 30, this.getHeight() - 30);
		int int6;
		for (int3 = 0; int3 < 10; ++int3) {
			byte byte1 = 30;
			byte byte2 = 42;
			int6 = this.getHeight() - ((int3 + 1) * (this.getHeight() - 60) / 10 + 30);
			graphics2D.drawLine(byte1, int6, byte2, int6);
		}

		Stroke stroke = graphics2D.getStroke();
		graphics2D.setColor(GRAPH_COLOR);
		graphics2D.setStroke(GRAPH_STROKE);
		int int7;
		for (int4 = 0; int4 < arrayList.size() - 1; ++int4) {
			int5 = ((Point)arrayList.get(int4)).x;
			int6 = ((Point)arrayList.get(int4)).y;
			int7 = ((Point)arrayList.get(int4 + 1)).x;
			int int8 = ((Point)arrayList.get(int4 + 1)).y;
			graphics2D.drawLine(int5, int6, int7, int8);
		}

		double double3 = ((double)this.getWidth() - 60.0) / (double)(this.psize - 1);
		graphics2D.setColor(LINE_CURRENT_COLOR);
		int6 = (int)((double)this.current * double3 + 30.0);
		graphics2D.drawLine(int6, this.getHeight() - 30, int6, 30);
		graphics2D.drawString("Current", int6, this.getHeight() - 30);
		graphics2D.setColor(LINE_LAST_COLOR);
		int7 = (int)((double)this.last * double3 + 30.0);
		graphics2D.drawLine(int7, this.getHeight() - 30, int7, 30);
		graphics2D.drawString("Last", int7, this.getHeight() - 30);
		graphics2D.setColor(Color.black);
		graphics2D.drawString(this.title, this.getWidth() / 2, 15);
		graphics2D.drawString("Size: " + this.scores.size(), 30, 15);
		graphics2D.drawString("Current/Write: " + this.current, 30, 30);
		graphics2D.drawString("Last/Read: " + this.last, 30, 45);
	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 200);
	}

	public static void createAndShowGui() {
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = new ArrayList();
		ArrayList arrayList3 = new ArrayList();
		ArrayList arrayList4 = new ArrayList();
		mainPanel = new VoiceDebug(arrayList, "SoundBuffer");
		mainPanel.scores_max = 32000;
		mainPanel2 = new VoiceDebug(arrayList2, "SoundBuffer - first 100 sample");
		mainPanel2.scores_max = 32000;
		mainPanel3 = new VoiceDebug(arrayList3, "FMODSoundBuffer");
		mainPanel3.scores_max = 32000;
		mainPanel4 = new VoiceDebug(arrayList4, "FMODSoundBuffer - first 100 sample");
		mainPanel4.scores_max = 32000;
		frame = new JFrame("DrawGraph");
		frame.setDefaultCloseOperation(3);
		frame.setLayout(new GridLayout(2, 2));
		frame.getContentPane().add(mainPanel);
		frame.getContentPane().add(mainPanel2);
		frame.getContentPane().add(mainPanel3);
		frame.getContentPane().add(mainPanel4);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	public static void updateGui(SoundBuffer soundBuffer, FMODSoundBuffer fMODSoundBuffer) {
		mainPanel.scores.clear();
		int int1;
		if (soundBuffer != null) {
			for (int1 = 0; int1 < soundBuffer.buf().length; ++int1) {
				mainPanel.scores.add(Integer.valueOf(soundBuffer.buf()[int1]));
			}

			mainPanel.current = soundBuffer.Buf_Write;
			mainPanel.last = soundBuffer.Buf_Read;
			mainPanel.psize = soundBuffer.Buf_Size;
			mainPanel2.scores.clear();
			for (int1 = 0; int1 < 100; ++int1) {
				mainPanel2.scores.add(Integer.valueOf(soundBuffer.buf()[int1]));
			}
		}

		mainPanel3.scores.clear();
		mainPanel4.scores.clear();
		for (int1 = 0; int1 < fMODSoundBuffer.buf().length / 2; int1 += 2) {
			mainPanel4.scores.add(fMODSoundBuffer.buf()[int1 + 1] * 256 + fMODSoundBuffer.buf()[int1]);
		}

		frame.repaint();
	}

	static  {
		LINE_CURRENT_COLOR = Color.blue;
		LINE_LAST_COLOR = Color.red;
		GRAPH_COLOR = Color.green;
		GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
		GRAPH_STROKE = new BasicStroke(3.0F);
	}
}
