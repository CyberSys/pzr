package zombie.debug;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.function.Consumer;
import zombie.characters.IsoPlayer;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.iso.PlayerCamera;
import zombie.iso.Vector2;


public final class LineDrawer {
	private static final long serialVersionUID = -8792265397633463907L;
	public static int red = 0;
	public static int green = 255;
	public static int blue = 0;
	public static int alpha = 255;
	static int idLayer = -1;
	static final ArrayList lines = new ArrayList();
	static final ArrayDeque pool = new ArrayDeque();
	private static int layer;
	static final Vector2 tempo = new Vector2();
	static final Vector2 tempo2 = new Vector2();

	static void DrawTexturedRect(Texture texture, float float1, float float2, float float3, float float4, int int1, float float5, float float6, float float7) {
		float1 = (float)((int)float1);
		float2 = (float)((int)float2);
		Vector2 vector2 = new Vector2(float1, float2);
		Vector2 vector22 = new Vector2(float1 + float3, float2);
		Vector2 vector23 = new Vector2(float1 + float3, float2 + float4);
		Vector2 vector24 = new Vector2(float1, float2 + float4);
		Vector2 vector25 = new Vector2(IsoUtils.XToScreen(vector2.x, vector2.y, (float)int1, 0), IsoUtils.YToScreen(vector2.x, vector2.y, (float)int1, 0));
		Vector2 vector26 = new Vector2(IsoUtils.XToScreen(vector22.x, vector22.y, (float)int1, 0), IsoUtils.YToScreen(vector22.x, vector22.y, (float)int1, 0));
		Vector2 vector27 = new Vector2(IsoUtils.XToScreen(vector23.x, vector23.y, (float)int1, 0), IsoUtils.YToScreen(vector23.x, vector23.y, (float)int1, 0));
		Vector2 vector28 = new Vector2(IsoUtils.XToScreen(vector24.x, vector24.y, (float)int1, 0), IsoUtils.YToScreen(vector24.x, vector24.y, (float)int1, 0));
		PlayerCamera playerCamera = IsoCamera.cameras[IsoPlayer.getPlayerIndex()];
		vector25.x -= playerCamera.OffX;
		vector26.x -= playerCamera.OffX;
		vector27.x -= playerCamera.OffX;
		vector28.x -= playerCamera.OffX;
		vector25.y -= playerCamera.OffY;
		vector26.y -= playerCamera.OffY;
		vector27.y -= playerCamera.OffY;
		vector28.y -= playerCamera.OffY;
		float float8 = -240.0F;
		float8 -= 128.0F;
		float float9 = -32.0F;
		vector25.y -= float8;
		vector26.y -= float8;
		vector27.y -= float8;
		vector28.y -= float8;
		vector25.x -= float9;
		vector26.x -= float9;
		vector27.x -= float9;
		vector28.x -= float9;
		SpriteRenderer.instance.renderdebug(texture, vector25.x, vector25.y, vector26.x, vector26.y, vector27.x, vector27.y, vector28.x, vector28.y, float5, float6, float7, 1.0F, float5, float6, float7, 1.0F, float5, float6, float7, 1.0F, float5, float6, float7, 1.0F, (Consumer)null);
	}

	static void DrawIsoLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1) {
		tempo.set(float1, float2);
		tempo2.set(float3, float4);
		Vector2 vector2 = new Vector2(IsoUtils.XToScreen(tempo.x, tempo.y, 0.0F, 0), IsoUtils.YToScreen(tempo.x, tempo.y, 0.0F, 0));
		Vector2 vector22 = new Vector2(IsoUtils.XToScreen(tempo2.x, tempo2.y, 0.0F, 0), IsoUtils.YToScreen(tempo2.x, tempo2.y, 0.0F, 0));
		vector2.x -= IsoCamera.getOffX();
		vector22.x -= IsoCamera.getOffX();
		vector2.y -= IsoCamera.getOffY();
		vector22.y -= IsoCamera.getOffY();
		drawLine(vector2.x, vector2.y, vector22.x, vector22.y, float5, float6, float7, float8, int1);
	}

	public static void DrawIsoRect(float float1, float float2, float float3, float float4, int int1, float float5, float float6, float float7) {
		if (float3 < 0.0F) {
			float3 = -float3;
			float1 -= float3;
		}

		if (float4 < 0.0F) {
			float4 = -float4;
			float2 -= float4;
		}

		float float8 = IsoUtils.XToScreenExact(float1, float2, (float)int1, 0);
		float float9 = IsoUtils.YToScreenExact(float1, float2, (float)int1, 0);
		float float10 = IsoUtils.XToScreenExact(float1 + float3, float2, (float)int1, 0);
		float float11 = IsoUtils.YToScreenExact(float1 + float3, float2, (float)int1, 0);
		float float12 = IsoUtils.XToScreenExact(float1 + float3, float2 + float4, (float)int1, 0);
		float float13 = IsoUtils.YToScreenExact(float1 + float3, float2 + float4, (float)int1, 0);
		float float14 = IsoUtils.XToScreenExact(float1, float2 + float4, (float)int1, 0);
		float float15 = IsoUtils.YToScreenExact(float1, float2 + float4, (float)int1, 0);
		drawLine(float8, float9, float10, float11, float5, float6, float7);
		drawLine(float10, float11, float12, float13, float5, float6, float7);
		drawLine(float12, float13, float14, float15, float5, float6, float7);
		drawLine(float14, float15, float8, float9, float5, float6, float7);
	}

	public static void DrawIsoRectRotated(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10) {
		Vector2 vector2 = tempo.setLengthAndDirection(float6, 1.0F);
		Vector2 vector22 = tempo2.set(vector2);
		vector22.tangent();
		vector2.x *= float5 / 2.0F;
		vector2.y *= float5 / 2.0F;
		vector22.x *= float4 / 2.0F;
		vector22.y *= float4 / 2.0F;
		float float11 = float1 + vector2.x;
		float float12 = float2 + vector2.y;
		float float13 = float1 - vector2.x;
		float float14 = float2 - vector2.y;
		float float15 = float11 - vector22.x;
		float float16 = float12 - vector22.y;
		float float17 = float11 + vector22.x;
		float float18 = float12 + vector22.y;
		float float19 = float13 - vector22.x;
		float float20 = float14 - vector22.y;
		float float21 = float13 + vector22.x;
		float float22 = float14 + vector22.y;
		byte byte1 = 1;
		DrawIsoLine(float15, float16, float3, float17, float18, float3, float7, float8, float9, float10, byte1);
		DrawIsoLine(float15, float16, float3, float19, float20, float3, float7, float8, float9, float10, byte1);
		DrawIsoLine(float17, float18, float3, float21, float22, float3, float7, float8, float9, float10, byte1);
		DrawIsoLine(float19, float20, float3, float21, float22, float3, float7, float8, float9, float10, byte1);
	}

	public static void DrawIsoLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, int int1) {
		float float11 = IsoUtils.XToScreenExact(float1, float2, float3, 0);
		float float12 = IsoUtils.YToScreenExact(float1, float2, float3, 0);
		float float13 = IsoUtils.XToScreenExact(float4, float5, float6, 0);
		float float14 = IsoUtils.YToScreenExact(float4, float5, float6, 0);
		drawLine(float11, float12, float13, float14, float7, float8, float9, float10, int1);
	}

	public static void DrawIsoTransform(float float1, float float2, float float3, float float4, float float5, float float6, int int1, float float7, float float8, float float9, float float10, int int2) {
		DrawIsoCircle(float1, float2, float3, float6, int1, float7, float8, float9, float10);
		DrawIsoLine(float1, float2, float3, float1 + float4 + float6 / 2.0F, float2 + float5 + float6 / 2.0F, float3, float7, float8, float9, float10, int2);
	}

	public static void DrawIsoCircle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		byte byte1 = 16;
		DrawIsoCircle(float1, float2, float3, float4, byte1, float5, float6, float7, float8);
	}

	public static void DrawIsoCircle(float float1, float float2, float float3, float float4, int int1, float float5, float float6, float float7, float float8) {
		double double1 = (double)float1 + (double)float4 * Math.cos(Math.toRadians((double)(0.0F / (float)int1)));
		double double2 = (double)float2 + (double)float4 * Math.sin(Math.toRadians((double)(0.0F / (float)int1)));
		for (int int2 = 1; int2 <= int1; ++int2) {
			double double3 = (double)float1 + (double)float4 * Math.cos(Math.toRadians((double)((float)int2 * 360.0F / (float)int1)));
			double double4 = (double)float2 + (double)float4 * Math.sin(Math.toRadians((double)((float)int2 * 360.0F / (float)int1)));
			addLine((float)double1, (float)double2, float3, (float)double3, (float)double4, float3, float5, float6, float7, float8);
			double1 = double3;
			double2 = double4;
		}
	}

	static void drawLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		SpriteRenderer.instance.renderline((Texture)null, (int)float1 - 1, (int)float2 - 1, (int)float3 - 1, (int)float4 - 1, 0.0F, 0.0F, 0.0F, 0.5F);
		SpriteRenderer.instance.renderline((Texture)null, (int)float1, (int)float2, (int)float3, (int)float4, float5, float6, float7, 1.0F);
	}

	public static void drawLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1) {
		SpriteRenderer.instance.renderline((Texture)null, (int)float1, (int)float2, (int)float3, (int)float4, float5, float6, float7, float8);
	}

	public static void drawRect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1) {
		SpriteRenderer.instance.render((Texture)null, float1, float2 + (float)int1, (float)int1, float4 - (float)(int1 * 2), float5, float6, float7, float8, (Consumer)null);
		SpriteRenderer.instance.render((Texture)null, float1, float2, float3, (float)int1, float5, float6, float7, float8, (Consumer)null);
		SpriteRenderer.instance.render((Texture)null, float1 + float3 - (float)int1, float2 + (float)int1, 1.0F, float4 - (float)(int1 * 2), float5, float6, float7, float8, (Consumer)null);
		SpriteRenderer.instance.render((Texture)null, float1, float2 + float4 - (float)int1, float3, (float)int1, float5, float6, float7, float8, (Consumer)null);
	}

	public static void drawArc(float float1, float float2, float float3, float float4, float float5, float float6, int int1, float float7, float float8, float float9, float float10) {
		float float11 = float5 + (float)Math.acos((double)float6);
		float float12 = float5 - (float)Math.acos((double)float6);
		float float13 = float1 + (float)Math.cos((double)float11) * float4;
		float float14 = float2 + (float)Math.sin((double)float11) * float4;
		for (int int2 = 1; int2 <= int1; ++int2) {
			float float15 = float11 + (float12 - float11) * (float)int2 / (float)int1;
			float float16 = float1 + (float)Math.cos((double)float15) * float4;
			float float17 = float2 + (float)Math.sin((double)float15) * float4;
			DrawIsoLine(float13, float14, float3, float16, float17, float3, float7, float8, float9, float10, 1);
			float13 = float16;
			float14 = float17;
		}
	}

	public static void drawCircle(float float1, float float2, float float3, int int1, float float4, float float5, float float6) {
		double double1 = (double)float1 + (double)float3 * Math.cos(Math.toRadians((double)(0.0F / (float)int1)));
		double double2 = (double)float2 + (double)float3 * Math.sin(Math.toRadians((double)(0.0F / (float)int1)));
		for (int int2 = 1; int2 <= int1; ++int2) {
			double double3 = (double)float1 + (double)float3 * Math.cos(Math.toRadians((double)((float)int2 * 360.0F / (float)int1)));
			double double4 = (double)float2 + (double)float3 * Math.sin(Math.toRadians((double)((float)int2 * 360.0F / (float)int1)));
			drawLine((float)double1, (float)double2, (float)double3, (float)double4, float4, float5, float6, 1.0F, 1);
			double1 = double3;
			double2 = double4;
		}
	}

	public static void drawDirectionLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, int int1) {
		float float10 = float1 + (float)Math.cos((double)float5) * float4;
		float float11 = float2 + (float)Math.sin((double)float5) * float4;
		DrawIsoLine(float1, float2, float3, float10, float11, float3, float6, float7, float8, float9, int1);
	}

	public static void drawDotLines(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, int int1) {
		drawDirectionLine(float1, float2, float3, float4, float5 + (float)Math.acos((double)float6), float7, float8, float9, float10, int1);
		drawDirectionLine(float1, float2, float3, float4, float5 - (float)Math.acos((double)float6), float7, float8, float9, float10, int1);
	}

	public static void addLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10) {
		LineDrawer.DrawableLine drawableLine = pool.isEmpty() ? new LineDrawer.DrawableLine() : (LineDrawer.DrawableLine)pool.pop();
		lines.add(drawableLine.init(float1, float2, float3, float4, float5, float6, float7, float8, float9, float10));
	}

	public static void addLine(float float1, float float2, float float3, float float4, float float5, float float6, int int1, int int2, int int3, String string) {
		addLine(float1, float2, float3, float4, float5, float6, (float)int1, (float)int2, (float)int3, string, true);
	}

	public static void addLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, String string, boolean boolean1) {
		LineDrawer.DrawableLine drawableLine = pool.isEmpty() ? new LineDrawer.DrawableLine() : (LineDrawer.DrawableLine)pool.pop();
		lines.add(drawableLine.init(float1, float2, float3, float4, float5, float6, float7, float8, float9, string, boolean1));
	}

	public static void addRect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		LineDrawer.DrawableLine drawableLine = pool.isEmpty() ? new LineDrawer.DrawableLine() : (LineDrawer.DrawableLine)pool.pop();
		lines.add(drawableLine.init(float1, float2, float3, float1 + float4, float2 + float5, float3, float6, float7, float8, (String)null, false));
	}

	public static void clear() {
		if (!lines.isEmpty()) {
			for (int int1 = 0; int1 < lines.size(); ++int1) {
				pool.push((LineDrawer.DrawableLine)lines.get(int1));
			}

			lines.clear();
		}
	}

	public void removeLine(String string) {
		for (int int1 = 0; int1 < lines.size(); ++int1) {
			if (((LineDrawer.DrawableLine)lines.get(int1)).name.equals(string)) {
				lines.remove(lines.get(int1));
				--int1;
			}
		}
	}

	public static void render() {
		for (int int1 = 0; int1 < lines.size(); ++int1) {
			LineDrawer.DrawableLine drawableLine = (LineDrawer.DrawableLine)lines.get(int1);
			if (!drawableLine.bLine) {
				DrawIsoRect(drawableLine.xstart, drawableLine.ystart, drawableLine.xend - drawableLine.xstart, drawableLine.yend - drawableLine.ystart, (int)drawableLine.zstart, drawableLine.red, drawableLine.green, drawableLine.blue);
			} else {
				DrawIsoLine(drawableLine.xstart, drawableLine.ystart, drawableLine.zstart, drawableLine.xend, drawableLine.yend, drawableLine.zend, drawableLine.red, drawableLine.green, drawableLine.blue, drawableLine.alpha, 1);
			}
		}
	}

	public static void drawLines() {
		clear();
	}

	static class DrawableLine {
		public boolean bLine = false;
		String name;
		float red;
		float green;
		float blue;
		float alpha;
		float xstart;
		float ystart;
		float zstart;
		float xend;
		float yend;
		float zend;

		public LineDrawer.DrawableLine init(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, String string) {
			this.xstart = float1;
			this.ystart = float2;
			this.zstart = float3;
			this.xend = float4;
			this.yend = float5;
			this.zend = float6;
			this.red = float7;
			this.green = float8;
			this.blue = float9;
			this.alpha = 1.0F;
			this.name = string;
			return this;
		}

		public LineDrawer.DrawableLine init(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, String string, boolean boolean1) {
			this.xstart = float1;
			this.ystart = float2;
			this.zstart = float3;
			this.xend = float4;
			this.yend = float5;
			this.zend = float6;
			this.red = float7;
			this.green = float8;
			this.blue = float9;
			this.alpha = 1.0F;
			this.name = string;
			this.bLine = boolean1;
			return this;
		}

		public LineDrawer.DrawableLine init(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10) {
			this.xstart = float1;
			this.ystart = float2;
			this.zstart = float3;
			this.xend = float4;
			this.yend = float5;
			this.zend = float6;
			this.red = float7;
			this.green = float8;
			this.blue = float9;
			this.alpha = float10;
			this.name = null;
			this.bLine = true;
			return this;
		}

		public boolean equals(Object object) {
			if (object instanceof LineDrawer.DrawableLine) {
				return ((LineDrawer.DrawableLine)object).name.equals(this.name);
			} else {
				return object.equals(this);
			}
		}
	}
}
