package zombie.savefile;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.glu.GLU;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderSettings;
import zombie.core.sprite.SpriteRenderState;
import zombie.core.textures.MultiTextureFBO2;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;
import zombie.interfaces.ITexture;
import zombie.iso.IsoCamera;
import zombie.iso.IsoWorld;
import zombie.iso.PlayerCamera;
import zombie.iso.sprite.IsoSprite;
import zombie.ui.UIManager;


public final class SavefileThumbnail {
	private static final int WIDTH = 256;
	private static final int HEIGHT = 256;

	public static void create() {
		int int1 = -1;
		for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
			if (IsoPlayer.players[int2] != null) {
				int1 = int2;
				break;
			}
		}

		if (int1 != -1) {
			create(int1);
		}
	}

	public static void create(int int1) {
		Core core = Core.getInstance();
		MultiTextureFBO2 multiTextureFBO2 = core.OffscreenBuffer;
		float float1 = multiTextureFBO2.zoom[int1];
		float float2 = multiTextureFBO2.targetZoom[int1];
		setZoom(int1, 1.0F, 1.0F);
		IsoCamera.cameras[int1].center();
		renderWorld(int1, true, true);
		SpriteRenderer.instance.drawGeneric(new SavefileThumbnail.TakeScreenShotDrawer(int1));
		setZoom(int1, float1, float2);
		IsoCamera.cameras[int1].center();
		for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
			IsoPlayer player = IsoPlayer.players[int2];
			if (player != null) {
				renderWorld(int2, false, int2 == int1);
			}
		}

		core.RenderOffScreenBuffer();
		if (core.StartFrameUI()) {
			UIManager.render();
		}

		core.EndFrameUI();
	}

	private static void renderWorld(int int1, boolean boolean1, boolean boolean2) {
		IsoPlayer.setInstance(IsoPlayer.players[int1]);
		IsoCamera.CamCharacter = IsoPlayer.players[int1];
		IsoSprite.globalOffsetX = -1.0F;
		Core.getInstance().StartFrame(int1, boolean1);
		if (boolean2) {
			SpriteRenderer.instance.drawGeneric(new SavefileThumbnail.FixCameraDrawer(int1));
		}

		IsoCamera.frameState.set(int1);
		IsoWorld.instance.render();
		RenderSettings.getInstance().legacyPostRender(int1);
		Core.getInstance().EndFrame(int1);
	}

	private static void setZoom(int int1, float float1, float float2) {
		Core.getInstance().OffscreenBuffer.zoom[int1] = float1;
		Core.getInstance().OffscreenBuffer.targetZoom[int1] = float2;
		IsoCamera.cameras[int1].zoom = float1;
		IsoCamera.cameras[int1].OffscreenWidth = IsoCamera.getOffscreenWidth(int1);
		IsoCamera.cameras[int1].OffscreenHeight = IsoCamera.getOffscreenHeight(int1);
	}

	private static void createWithRenderShader(int int1) {
		short short1 = 256;
		short short2 = 256;
		Texture texture = new Texture(short1, short2, 16);
		TextureFBO textureFBO = new TextureFBO(texture, false);
		GL11.glPushAttrib(1048575);
		try {
			textureFBO.startDrawing(true, false);
			GL11.glViewport(0, 0, short1, short2);
			GL11.glMatrixMode(5889);
			GL11.glLoadIdentity();
			GLU.gluOrtho2D(0.0F, (float)short1, (float)short2, 0.0F);
			GL11.glMatrixMode(5888);
			GL11.glLoadIdentity();
			Core core = Core.getInstance();
			core.RenderShader.Start();
			GL11.glDisable(3089);
			GL11.glDisable(2960);
			GL11.glDisable(3042);
			GL11.glDisable(3008);
			GL11.glDisable(2929);
			GL11.glDisable(2884);
			for (int int2 = 8; int2 > 1; --int2) {
				GL13.glActiveTexture('蓀' + int2 - 1);
				GL11.glDisable(3553);
			}

			GL13.glActiveTexture(33984);
			GL11.glEnable(3553);
			ITexture iTexture = core.getOffscreenBuffer().getTexture();
			iTexture.bind();
			int int3 = IsoCamera.getScreenLeft(int1) + IsoCamera.getScreenWidth(int1) / 2 - short1 / 2;
			int int4 = IsoCamera.getScreenTop(int1) + IsoCamera.getScreenHeight(int1) / 2 - short2 / 2;
			int int5 = core.getOffscreenBuffer().getTexture().getWidthHW();
			int int6 = core.getOffscreenBuffer().getTexture().getHeightHW();
			float float1 = (float)int3 / (float)int5;
			float float2 = (float)(int3 + short1) / (float)int5;
			float float3 = (float)int4 / (float)int6;
			float float4 = (float)(int4 + short2) / (float)int6;
			GL11.glBegin(7);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTexCoord2f(float1, float4);
			GL11.glVertex2d(0.0, 0.0);
			GL11.glTexCoord2f(float1, float3);
			GL11.glVertex2d(0.0, (double)short2);
			GL11.glTexCoord2f(float2, float3);
			GL11.glVertex2d((double)short1, (double)short2);
			GL11.glTexCoord2f(float2, float4);
			GL11.glVertex2d((double)short1, 0.0);
			GL11.glEnd();
			core.RenderShader.End();
			core.TakeScreenshot(0, 0, short1, short2, TextureFBO.getFuncs().GL_COLOR_ATTACHMENT0());
			textureFBO.endDrawing();
		} finally {
			textureFBO.destroy();
			GL11.glPopAttrib();
		}
	}

	private static final class TakeScreenShotDrawer extends TextureDraw.GenericDrawer {
		int m_playerIndex;

		TakeScreenShotDrawer(int int1) {
			this.m_playerIndex = int1;
		}

		public void render() {
			Core core = Core.getInstance();
			MultiTextureFBO2 multiTextureFBO2 = core.OffscreenBuffer;
			if (multiTextureFBO2.Current == null) {
				Core.getInstance().TakeScreenshot(256, 256, 1029);
			} else if (core.RenderShader == null) {
				Core.getInstance().getOffscreenBuffer().startDrawing(false, false);
				Core.getInstance().TakeScreenshot(256, 256, TextureFBO.getFuncs().GL_COLOR_ATTACHMENT0());
				Core.getInstance().getOffscreenBuffer().endDrawing();
			} else {
				SavefileThumbnail.createWithRenderShader(this.m_playerIndex);
			}
		}
	}

	private static final class FixCameraDrawer extends TextureDraw.GenericDrawer {
		int m_playerIndex;
		float m_zoom;
		int m_offscreenWidth;
		int m_offscreenHeight;

		FixCameraDrawer(int int1) {
			PlayerCamera playerCamera = IsoCamera.cameras[int1];
			this.m_playerIndex = int1;
			this.m_zoom = playerCamera.zoom;
			this.m_offscreenWidth = playerCamera.OffscreenWidth;
			this.m_offscreenHeight = playerCamera.OffscreenHeight;
		}

		public void render() {
			SpriteRenderState spriteRenderState = SpriteRenderer.instance.getRenderingState();
			spriteRenderState.playerCamera[this.m_playerIndex].zoom = this.m_zoom;
			spriteRenderState.playerCamera[this.m_playerIndex].OffscreenWidth = this.m_offscreenWidth;
			spriteRenderState.playerCamera[this.m_playerIndex].OffscreenHeight = this.m_offscreenHeight;
			spriteRenderState.zoomLevel[this.m_playerIndex] = this.m_zoom;
		}
	}
}
