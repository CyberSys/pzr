package zombie.debug;

import java.util.ArrayDeque;
import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;


public class LineDrawer {
	private static final long serialVersionUID = -8792265397633463907L;
	public static int red = 0;
	public static int green = 255;
	public static int blue = 0;
	public static int alpha = 255;
	static int idLayer = -1;
	static ArrayList lines = new ArrayList();
	static ArrayDeque pool = new ArrayDeque();
	private static int layer;
	static Vector2 tempo = new Vector2();
	static Vector2 tempo2 = new Vector2();

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
		vector25.x -= IsoCamera.OffX[IsoPlayer.getPlayerIndex()];
		vector26.x -= IsoCamera.OffX[IsoPlayer.getPlayerIndex()];
		vector27.x -= IsoCamera.OffX[IsoPlayer.getPlayerIndex()];
		vector28.x -= IsoCamera.OffX[IsoPlayer.getPlayerIndex()];
		vector25.y -= IsoCamera.OffY[IsoPlayer.getPlayerIndex()];
		vector26.y -= IsoCamera.OffY[IsoPlayer.getPlayerIndex()];
		vector27.y -= IsoCamera.OffY[IsoPlayer.getPlayerIndex()];
		vector28.y -= IsoCamera.OffY[IsoPlayer.getPlayerIndex()];
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
		SpriteRenderer.instance.renderdebug(texture, (int)vector25.x, (int)vector25.y, (int)vector26.x, (int)vector26.y, (int)vector27.x, (int)vector27.y, (int)vector28.x, (int)vector28.y, float5, float6, float7, 1.0F, float5, float6, float7, 1.0F, float5, float6, float7, 1.0F, float5, float6, float7, 1.0F);
	}

	static void DrawIsoLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1) {
		tempo = new Vector2(float1, float2);
		tempo2 = new Vector2(float3, float4);
		Vector2 vector2 = new Vector2(IsoUtils.XToScreen(tempo.x, tempo.y, 0.0F, 0), IsoUtils.YToScreen(tempo.x, tempo.y, 0.0F, 0));
		Vector2 vector22 = new Vector2(IsoUtils.XToScreen(tempo2.x, tempo2.y, 0.0F, 0), IsoUtils.YToScreen(tempo2.x, tempo2.y, 0.0F, 0));
		vector2.x -= IsoCamera.getOffX();
		vector22.x -= IsoCamera.getOffX();
		vector2.y -= IsoCamera.getOffY();
		vector22.y -= IsoCamera.getOffY();
		drawLine(vector2.x, vector2.y, vector22.x, vector22.y, float5, float6, float7, float8, int1);
	}

	static void DrawIsoRect(float float1, float float2, float float3, float float4, int int1, float float5, float float6, float float7) {
		if (float3 < 0.0F) {
			float3 = -float3;
			float1 -= float3;
		}

		if (float4 < 0.0F) {
			float4 = -float4;
			float2 -= float4;
		}

		float float8 = IsoUtils.XToScreen(float1, float2, (float)int1, 0);
		float float9 = IsoUtils.YToScreen(float1, float2, (float)int1, 0);
		float float10 = IsoUtils.XToScreen(float1 + float3, float2, (float)int1, 0);
		float float11 = IsoUtils.YToScreen(float1 + float3, float2, (float)int1, 0);
		float float12 = IsoUtils.XToScreen(float1 + float3, float2 + float4, (float)int1, 0);
		float float13 = IsoUtils.YToScreen(float1 + float3, float2 + float4, (float)int1, 0);
		float float14 = IsoUtils.XToScreen(float1, float2 + float4, (float)int1, 0);
		float float15 = IsoUtils.YToScreen(float1, float2 + float4, (float)int1, 0);
		float8 -= IsoCamera.getOffX();
		float10 -= IsoCamera.getOffX();
		float12 -= IsoCamera.getOffX();
		float14 -= IsoCamera.getOffX();
		float9 -= IsoCamera.getOffY();
		float11 -= IsoCamera.getOffY();
		float13 -= IsoCamera.getOffY();
		float15 -= IsoCamera.getOffY();
		drawLine(float8, float9, float10, float11, float5, float6, float7);
		drawLine(float10, float11, float12, float13, float5, float6, float7);
		drawLine(float12, float13, float14, float15, float5, float6, float7);
		drawLine(float14, float15, float8, float9, float5, float6, float7);
	}

	public static void DrawIsoLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, int int1) {
		float float11 = IsoUtils.XToScreenExact(float1, float2, float3, 0);
		float float12 = IsoUtils.YToScreenExact(float1, float2, float3, 0);
		float float13 = IsoUtils.XToScreenExact(float4, float5, float6, 0);
		float float14 = IsoUtils.YToScreenExact(float4, float5, float6, 0);
		drawLine(float11, float12, float13, float14, float7, float8, float9, float10, int1);
	}

	static void drawLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		SpriteRenderer.instance.renderline((Texture)null, (int)float1 - 1, (int)float2 - 1, (int)float3 - 1, (int)float4 - 1, 0.0F, 0.0F, 0.0F, 0.5F);
		SpriteRenderer.instance.renderline((Texture)null, (int)float1, (int)float2, (int)float3, (int)float4, float5, float6, float7, 1.0F);
	}

	public static void drawLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1) {
		SpriteRenderer.instance.renderline((Texture)null, (int)float1, (int)float2, (int)float3, (int)float4, float5, float6, float7, float8);
	}

	public static void drawRect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1) {
		SpriteRenderer.instance.render((Texture)null, float1, float2 + (float)int1, (float)int1, float4 - (float)(int1 * 2), float5, float6, float7, float8);
		SpriteRenderer.instance.render((Texture)null, float1, float2, float3, (float)int1, float5, float6, float7, float8);
		SpriteRenderer.instance.render((Texture)null, float1 + float3 - (float)int1, float2 + (float)int1, 1.0F, float4 - (float)(int1 * 2), float5, float6, float7, float8);
		SpriteRenderer.instance.render((Texture)null, float1, float2 + float4 - (float)int1, float3, (float)int1, float5, float6, float7, float8);
	}

	public static void addLine(float float1, float float2, float float3, float float4, float float5, float float6, int int1, int int2, int int3, String string) {
		addLine(float1, float2, float3, float4, float5, float6, (float)int1, (float)int2, (float)int3, string, true);
	}

	public static void addLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, String string, boolean boolean1) {
		LineDrawer.DrawableLine drawableLine = pool.isEmpty() ? new LineDrawer.DrawableLine() : (LineDrawer.DrawableLine)pool.pop();
		lines.add(drawableLine.init(float1, float2, float3, float4, float5, float6, float7, float8, float9, string, boolean1));
	}

	public static void clear() {
		pool.addAll(lines);
		lines.clear();
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
				DrawIsoLine(drawableLine.xstart, drawableLine.ystart, drawableLine.zstart, drawableLine.xend, drawableLine.yend, drawableLine.zend, drawableLine.red, drawableLine.green, drawableLine.blue, 1.0F, 1);
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
			this.name = string;
			this.bLine = boolean1;
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
