package zombie.iso.sprite;

import java.util.function.Consumer;
import org.lwjgl.opengl.GL11;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.input.Mouse;
import zombie.iso.IsoCamera;


public final class IsoCursor {
	private static IsoCursor instance = null;
	IsoCursor.IsoCursorShader m_shader = null;

	public static IsoCursor getInstance() {
		if (instance == null) {
			instance = new IsoCursor();
		}

		return instance;
	}

	private IsoCursor() {
		RenderThread.invokeOnRenderContext(this::createShader);
		if (this.m_shader != null) {
			this.m_shader.m_textureCursor = Texture.getSharedTexture("media/ui/isocursor.png");
		}
	}

	private void createShader() {
		this.m_shader = new IsoCursor.IsoCursorShader();
	}

	public void render(int int1) {
		if (Core.getInstance().getOffscreenBuffer() != null) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null && !player.isDead() && player.isAiming() && player.PlayerIndex == 0 && player.JoypadBind == -1) {
				if (!GameTime.isGamePaused()) {
					if (this.m_shader != null && this.m_shader.isCompiled()) {
						float float1 = 1.0F / Core.getInstance().getZoom(int1);
						int int2 = (int)((float)(this.m_shader.m_textureCursor.getWidth() * Core.TileScale) / 2.0F * float1);
						int int3 = (int)((float)(this.m_shader.m_textureCursor.getHeight() * Core.TileScale) / 2.0F * float1);
						this.m_shader.m_screenX = Mouse.getXA() - int2 / 2;
						this.m_shader.m_screenY = Mouse.getYA() - int3 / 2;
						this.m_shader.width = int2;
						this.m_shader.height = int3;
						int int4 = IsoCamera.getScreenLeft(int1);
						int int5 = IsoCamera.getScreenTop(int1);
						int int6 = IsoCamera.getScreenWidth(int1);
						int int7 = IsoCamera.getScreenHeight(int1);
						SpriteRenderer.instance.StartShader(this.m_shader.getID(), int1);
						SpriteRenderer.instance.renderClamped(this.m_shader.m_textureCursor, this.m_shader.m_screenX, this.m_shader.m_screenY, int2, int3, int4, int5, int6, int7, 1.0F, 1.0F, 1.0F, 1.0F, this.m_shader);
						SpriteRenderer.instance.EndShader();
					}
				}
			}
		}
	}

	private static class IsoCursorShader extends Shader implements Consumer {
		private float m_alpha = 1.0F;
		private Texture m_textureCursor;
		private Texture m_textureWorld;
		private int m_screenX;
		private int m_screenY;

		IsoCursorShader() {
			super("isocursor");
		}

		public void startMainThread(TextureDraw textureDraw, int int1) {
			this.m_alpha = this.calculateAlpha();
			this.m_textureWorld = Core.getInstance().OffscreenBuffer.getTexture(int1);
		}

		public void startRenderThread(TextureDraw textureDraw) {
			this.getProgram().setValue("u_alpha", this.m_alpha);
			this.getProgram().setValue("TextureCursor", this.m_textureCursor, 0);
			this.getProgram().setValue("TextureBackground", this.m_textureWorld, 1);
			SpriteRenderer.ringBuffer.shaderChangedTexture1();
			GL11.glEnable(3042);
		}

		public void accept(TextureDraw textureDraw) {
			byte byte1 = 0;
			int int1 = (int)textureDraw.x0 - this.m_screenX;
			int int2 = (int)textureDraw.y0 - this.m_screenY;
			int int3 = this.m_screenX + this.width - (int)textureDraw.x2;
			int int4 = this.m_screenY + this.height - (int)textureDraw.y2;
			this.m_screenX += int1;
			this.m_screenY += int2;
			this.width -= int1 + int3;
			this.height -= int2 + int4;
			float float1 = (float)this.m_textureWorld.getWidthHW();
			float float2 = (float)this.m_textureWorld.getHeightHW();
			float float3 = (float)(IsoCamera.getScreenTop(byte1) + IsoCamera.getScreenHeight(byte1) - (this.m_screenY + this.height));
			textureDraw.tex1 = this.m_textureWorld;
			textureDraw.tex1_u0 = (float)this.m_screenX / float1;
			textureDraw.tex1_v3 = float3 / float2;
			textureDraw.tex1_u1 = (float)(this.m_screenX + this.width) / float1;
			textureDraw.tex1_v2 = float3 / float2;
			textureDraw.tex1_u2 = (float)(this.m_screenX + this.width) / float1;
			textureDraw.tex1_v1 = (float3 + (float)this.height) / float2;
			textureDraw.tex1_u3 = (float)this.m_screenX / float1;
			textureDraw.tex1_v0 = (float3 + (float)this.height) / float2;
		}

		float calculateAlpha() {
			float float1 = 0.05F;
			switch (Core.getInstance().getIsoCursorVisibility()) {
			case 0: 
				float1 = 0.0F;
				break;
			
			case 1: 
				float1 = 0.05F;
				break;
			
			case 2: 
				float1 = 0.1F;
				break;
			
			case 3: 
				float1 = 0.15F;
				break;
			
			case 4: 
				float1 = 0.3F;
				break;
			
			case 5: 
				float1 = 0.5F;
				break;
			
			case 6: 
				float1 = 0.75F;
			
			}
			return float1;
		}
	}
}
