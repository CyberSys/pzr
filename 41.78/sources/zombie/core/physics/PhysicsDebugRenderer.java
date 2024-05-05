package zombie.core.physics;

import gnu.trove.list.array.TFloatArrayList;
import org.lwjgl.opengl.GL11;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.opengl.VBOLines;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.popman.ObjectPool;


public final class PhysicsDebugRenderer extends TextureDraw.GenericDrawer {
	private static final ObjectPool POOL = new ObjectPool(PhysicsDebugRenderer::new);
	private static final VBOLines vboLines = new VBOLines();
	private float camOffX;
	private float camOffY;
	private float deferredX;
	private float deferredY;
	private int drawOffsetX;
	private int drawOffsetY;
	private int playerIndex;
	private float playerX;
	private float playerY;
	private float playerZ;
	private float offscreenWidth;
	private float offscreenHeight;
	private final TFloatArrayList elements = new TFloatArrayList();

	public static PhysicsDebugRenderer alloc() {
		return (PhysicsDebugRenderer)POOL.alloc();
	}

	public void release() {
		POOL.release((Object)this);
	}

	public void init(IsoPlayer player) {
		this.playerIndex = player.getPlayerNum();
		this.camOffX = IsoCamera.getRightClickOffX() + (float)IsoCamera.PLAYER_OFFSET_X;
		this.camOffY = IsoCamera.getRightClickOffY() + (float)IsoCamera.PLAYER_OFFSET_Y;
		this.camOffX += this.XToScreenExact(player.x - (float)((int)player.x), player.y - (float)((int)player.y), 0.0F, 0);
		this.camOffY += this.YToScreenExact(player.x - (float)((int)player.x), player.y - (float)((int)player.y), 0.0F, 0);
		this.deferredX = IsoCamera.cameras[this.playerIndex].DeferedX;
		this.deferredY = IsoCamera.cameras[this.playerIndex].DeferedY;
		this.drawOffsetX = (int)player.x;
		this.drawOffsetY = (int)player.y;
		this.playerX = player.x;
		this.playerY = player.y;
		this.playerZ = player.z;
		this.offscreenWidth = (float)Core.getInstance().getOffscreenWidth(this.playerIndex);
		this.offscreenHeight = (float)Core.getInstance().getOffscreenHeight(this.playerIndex);
		this.elements.clear();
		int int1 = (int)WorldSimulation.instance.offsetX - this.drawOffsetX;
		int int2 = (int)WorldSimulation.instance.offsetY - this.drawOffsetY;
		this.n_debugDrawWorld(int1, int2);
	}

	public void render() {
		GL11.glPushAttrib(1048575);
		GL11.glDisable(3553);
		GL11.glDisable(3042);
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0, (double)this.offscreenWidth, (double)this.offscreenHeight, 0.0, 10000.0, -10000.0);
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		int int1 = -this.drawOffsetX;
		int int2 = -this.drawOffsetY;
		float float1 = this.deferredX;
		float float2 = this.deferredY;
		GL11.glTranslatef(this.offscreenWidth / 2.0F, this.offscreenHeight / 2.0F, 0.0F);
		float float3 = this.XToScreenExact(float1, float2, this.playerZ, 0);
		float float4 = this.YToScreenExact(float1, float2, this.playerZ, 0);
		float3 += this.camOffX;
		float4 += this.camOffY;
		GL11.glTranslatef(-float3, -float4, 0.0F);
		int1 = (int)((float)int1 + WorldSimulation.instance.offsetX);
		int2 = (int)((float)int2 + WorldSimulation.instance.offsetY);
		int int3 = 32 * Core.TileScale;
		float float5 = (float)Math.sqrt((double)(int3 * int3 + int3 * int3));
		GL11.glScalef(float5, float5, float5);
		GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
		vboLines.setLineWidth(1.0F);
		int int4 = 0;
		while (int4 < this.elements.size()) {
			float float6 = this.elements.getQuick(int4++);
			float float7 = this.elements.getQuick(int4++);
			float float8 = this.elements.getQuick(int4++);
			float float9 = this.elements.getQuick(int4++);
			float float10 = this.elements.getQuick(int4++);
			float float11 = this.elements.getQuick(int4++);
			float float12 = this.elements.getQuick(int4++);
			float float13 = this.elements.getQuick(int4++);
			float float14 = this.elements.getQuick(int4++);
			float float15 = this.elements.getQuick(int4++);
			float float16 = this.elements.getQuick(int4++);
			float float17 = this.elements.getQuick(int4++);
			vboLines.addLine(float6, float7, float8, float9, float10, float11, float12, float13, float14, 1.0F, float15, float16, float17, 1.0F);
		}

		vboLines.flush();
		GL11.glLineWidth(1.0F);
		GL11.glBegin(1);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glVertex3d(0.0, 0.0, 0.0);
		GL11.glVertex3d(1.0, 0.0, 0.0);
		GL11.glVertex3d(0.0, 0.0, 0.0);
		GL11.glVertex3d(0.0, 1.0, 0.0);
		GL11.glVertex3d(0.0, 0.0, 0.0);
		GL11.glVertex3d(0.0, 0.0, 1.0);
		GL11.glEnd();
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5888);
		GL11.glPopMatrix();
		GL11.glEnable(3042);
		GL11.glEnable(3553);
		GL11.glPopAttrib();
		Texture.lastTextureID = -1;
	}

	public void postRender() {
		this.release();
	}

	public float YToScreenExact(float float1, float float2, float float3, int int1) {
		return IsoUtils.YToScreen(float1, float2, float3, int1);
	}

	public float XToScreenExact(float float1, float float2, float float3, int int1) {
		return IsoUtils.XToScreen(float1, float2, float3, int1);
	}

	public void drawLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		if (!(float1 < -1000.0F) && !(float1 > 1000.0F) && !(float2 < -1000.0F) && !(float2 > 1000.0F)) {
			this.elements.add(float1);
			this.elements.add(float2);
			this.elements.add(float3);
			this.elements.add(float4);
			this.elements.add(float5);
			this.elements.add(float6);
			this.elements.add(float7);
			this.elements.add(float8);
			this.elements.add(float9);
			this.elements.add(float10);
			this.elements.add(float11);
			this.elements.add(float12);
		}
	}

	public void drawSphere(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
	}

	public void drawTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13) {
	}

	public void drawContactPoint(float float1, float float2, float float3, float float4, float float5, float float6, float float7, int int1, float float8, float float9, float float10) {
	}

	public native void n_debugDrawWorld(int int1, int int2);
}
