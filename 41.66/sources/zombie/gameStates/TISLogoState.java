package zombie.gameStates;

import java.util.function.Consumer;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;


public final class TISLogoState extends GameState {
	public float alpha = 0.0F;
	public float alphaStep = 0.02F;
	public float logoDisplayTime = 20.0F;
	public int screenNumber = 1;
	public int stage = 0;
	public float targetAlpha = 0.0F;
	private boolean bNoRender = false;
	private final TISLogoState.LogoElement logoTIS = new TISLogoState.LogoElement("media/ui/TheIndieStoneLogo_Lineart_White.png");
	private final TISLogoState.LogoElement logoFMOD = new TISLogoState.LogoElement("media/ui/FMODLogo.png");
	private final TISLogoState.LogoElement logoGA = new TISLogoState.LogoElement("media/ui/GA-1280-white.png");
	private final TISLogoState.LogoElement logoNW = new TISLogoState.LogoElement("media/ui/NW_Logo_Combined.png");
	private static final int SCREEN_TIS = 1;
	private static final int SCREEN_OTHER = 2;
	private static final int STAGE_FADING_IN_LOGO = 0;
	private static final int STAGE_HOLDING_LOGO = 1;
	private static final int STAGE_FADING_OUT_LOGO = 2;
	private static final int STAGE_EXIT = 3;

	public void enter() {
		UIManager.bSuspend = true;
		this.alpha = 0.0F;
		this.targetAlpha = 1.0F;
	}

	public void exit() {
		UIManager.bSuspend = false;
	}

	public void render() {
		if (this.bNoRender) {
			Core.getInstance().StartFrame();
			SpriteRenderer.instance.renderi((Texture)null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 0.0F, 0.0F, 0.0F, 1.0F, (Consumer)null);
			Core.getInstance().EndFrame();
		} else {
			Core.getInstance().StartFrame();
			Core.getInstance().EndFrame();
			boolean boolean1 = UIManager.useUIFBO;
			UIManager.useUIFBO = false;
			Core.getInstance().StartFrameUI();
			SpriteRenderer.instance.renderi((Texture)null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 0.0F, 0.0F, 0.0F, 1.0F, (Consumer)null);
			if (this.screenNumber == 1) {
				this.logoTIS.centerOnScreen();
				this.logoTIS.render(this.alpha);
			}

			if (this.screenNumber == 2) {
				this.renderAttribution();
			}

			Core.getInstance().EndFrameUI();
			UIManager.useUIFBO = boolean1;
		}
	}

	private void renderAttribution() {
		int int1 = Core.getInstance().getScreenWidth();
		int int2 = Core.getInstance().getScreenHeight();
		byte byte1 = 50;
		byte byte2 = 3;
		int int3 = (int2 - (byte2 + 1) * byte1) / 3;
		Texture texture = this.logoGA.m_texture;
		int int4;
		if (texture != null && texture.isReady()) {
			int int5 = (int)((float)(texture.getWidth() * int3) / (float)texture.getHeight());
			int4 = (int1 - int5) / 2;
			this.logoGA.setPos(int4, byte1);
			this.logoGA.setSize(int5, int3);
			this.logoGA.render(this.alpha);
		}

		int int6 = byte1 + int3 + byte1;
		int6 = (int)((float)int6 + (float)int3 * 0.15F);
		texture = this.logoNW.m_texture;
		int int7;
		int int8;
		int int9;
		float float1;
		if (texture != null && texture.isReady()) {
			float1 = 0.5F;
			int4 = (int)((float)texture.getWidth() * float1 * (float)int3 / (float)texture.getHeight());
			int7 = (int)((float)int3 * float1);
			int8 = (int1 - int4) / 2;
			int9 = (int3 - int7) / 2;
			this.logoNW.setPos(int8, int6 + int9);
			this.logoNW.setSize(int4, int7);
			this.logoNW.render(this.alpha);
		}

		int6 += int3 + byte1;
		texture = this.logoFMOD.m_texture;
		if (texture != null && texture.isReady()) {
			float1 = 0.35F;
			int4 = TextManager.instance.getFontHeight(UIFont.Small);
			int7 = (int)((float)int3 * float1 - 16.0F - (float)int4);
			int8 = (int)((float)texture.getWidth() * ((float)int7 / (float)texture.getHeight()));
			int9 = (int1 - int8) / 2;
			int int10 = (int3 - int7) / 2;
			int int11 = int6 + int10 + int7 + 16;
			this.logoFMOD.setPos(int9, int6 + int10);
			this.logoFMOD.setSize(int8, int7);
			this.logoFMOD.render(this.alpha);
			String string = "Made with FMOD Studio by Firelight Technologies Pty Ltd.";
			TextManager.instance.DrawStringCentre((double)int1 / 2.0, (double)int11, string, 1.0, 1.0, 1.0, (double)this.alpha);
		}
	}

	public GameStateMachine.StateAction update() {
		if (Mouse.isLeftDown() || GameKeyboard.isKeyDown(28) || GameKeyboard.isKeyDown(57) || GameKeyboard.isKeyDown(1)) {
			this.stage = 3;
		}

		if (this.stage == 0) {
			this.targetAlpha = 1.0F;
			if (this.alpha == 1.0F) {
				this.stage = 1;
				this.logoDisplayTime = 20.0F;
			}
		}

		if (this.stage == 1) {
			this.logoDisplayTime -= GameTime.getInstance().getMultiplier() / 1.6F;
			if (this.logoDisplayTime <= 0.0F) {
				this.stage = 2;
			}
		}

		if (this.stage == 2) {
			this.targetAlpha = 0.0F;
			if (this.alpha == 0.0F) {
				if (this.screenNumber == 1) {
					this.screenNumber = 2;
					this.stage = 0;
				} else {
					this.stage = 3;
				}
			}
		}

		if (this.stage == 3) {
			this.targetAlpha = 0.0F;
			if (this.alpha == 0.0F) {
				this.bNoRender = true;
				return GameStateMachine.StateAction.Continue;
			}
		}

		if (this.alpha < this.targetAlpha) {
			this.alpha += this.alphaStep * GameTime.getInstance().getMultiplier();
			if (this.alpha > this.targetAlpha) {
				this.alpha = this.targetAlpha;
			}
		} else if (this.alpha > this.targetAlpha) {
			this.alpha -= this.alphaStep * GameTime.getInstance().getMultiplier();
			if (this.stage == 3) {
				this.alpha -= this.alphaStep * GameTime.getInstance().getMultiplier();
			}

			if (this.alpha < this.targetAlpha) {
				this.alpha = this.targetAlpha;
			}
		}

		return GameStateMachine.StateAction.Remain;
	}

	private static final class LogoElement {
		Texture m_texture;
		int m_x;
		int m_y;
		int m_width;
		int m_height;

		LogoElement(String string) {
			this.m_texture = Texture.getSharedTexture(string);
			if (this.m_texture != null) {
				this.m_width = this.m_texture.getWidth();
				this.m_height = this.m_texture.getHeight();
			}
		}

		void centerOnScreen() {
			this.m_x = (Core.getInstance().getScreenWidth() - this.m_width) / 2;
			this.m_y = (Core.getInstance().getScreenHeight() - this.m_height) / 2;
		}

		void setPos(int int1, int int2) {
			this.m_x = int1;
			this.m_y = int2;
		}

		void setSize(int int1, int int2) {
			this.m_width = int1;
			this.m_height = int2;
		}

		void render(float float1) {
			if (this.m_texture != null && this.m_texture.isReady()) {
				SpriteRenderer.instance.renderi(this.m_texture, this.m_x, this.m_y, this.m_width, this.m_height, 1.0F, 1.0F, 1.0F, float1, (Consumer)null);
			}
		}
	}
}
