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
	public float alpha2 = 0.0F;
	public float alphaStep = 0.02F;
	public boolean bFixLogo = false;
	public boolean bFixLogo2 = false;
	public int delay = 20;
	public int leavedelay = 0;
	public float logoUseAlpha = 0.0F;
	public float logoUseAlpha2 = 0.0F;
	public float messageTime = 850.0F;
	public float messageTimeMax = 850.0F;
	public float logoDelay = 20.0F;
	public int stage = 0;
	public float targetAlpha = 0.0F;
	private boolean bNoRender = false;

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
			if (this.logoDelay <= 0.0F) {
				++this.stage;
				this.messageTime = this.messageTimeMax;
				this.targetAlpha = 1.0F;
				this.bFixLogo2 = false;
			}

			if (!this.bFixLogo) {
				this.logoUseAlpha = this.alpha;
			}

			if (this.alpha == 1.0F) {
				this.bFixLogo = true;
			}

			Texture texture = Texture.getSharedTexture("media/ui/TheIndieStoneLogo_Lineart_White.png");
			int int1 = Core.getInstance().getOffscreenWidth(0) / 2;
			int1 -= texture.getWidth() / 2;
			int int2 = Core.getInstance().getOffscreenHeight(0) / 2;
			int2 -= texture.getHeight() / 2;
			SpriteRenderer.instance.renderi(texture, int1, int2, texture.getWidth(), texture.getHeight(), 1.0F, 1.0F, 1.0F, this.alpha, (Consumer)null);
			this.renderAttribution();
			Core.getInstance().EndFrameUI();
			UIManager.useUIFBO = boolean1;
		}
	}

	private void renderAttribution() {
		Texture texture = Texture.getSharedTexture("media/ui/FMODLogo.png");
		if (texture != null && texture.isReady()) {
			int int1 = TextManager.instance.getFontHeight(UIFont.Small);
			byte byte1 = 32;
			int int2 = (int)((float)texture.getWidth() * ((float)byte1 / (float)texture.getHeight()));
			int int3 = Core.getInstance().getScreenWidth() / 2 - int2 / 2;
			int int4 = Core.getInstance().getScreenHeight() - 24 - int1;
			int int5 = int4 - 16 - byte1;
			SpriteRenderer.instance.renderi(texture, int3, int5, int2, byte1, 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
			String string = "Made with FMOD Studio by Firelight Technologies Pty Ltd.";
			TextManager.instance.DrawStringCentre((double)Core.getInstance().getScreenWidth() / 2.0, (double)int4, string, 1.0, 1.0, 1.0, 1.0);
		}
	}

	public GameStateMachine.StateAction update() {
		if (Mouse.isLeftDown() || GameKeyboard.isKeyDown(28) || GameKeyboard.isKeyDown(57) || GameKeyboard.isKeyDown(1)) {
			this.targetAlpha = 0.0F;
			this.stage = 2;
		}

		if (this.stage < 2 && this.alpha == 1.0F && this.targetAlpha == 1.0F) {
			--this.logoDelay;
		}

		if (this.stage >= 1) {
			this.targetAlpha = 0.0F;
			this.bFixLogo = false;
			this.bFixLogo2 = false;
			if (this.leavedelay > 0) {
				--this.leavedelay;
			} else if (this.alpha == 0.0F) {
				Core.getInstance().StartFrame();
				SpriteRenderer.instance.renderi((Texture)null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
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
			if (this.alpha < this.targetAlpha) {
				this.alpha = this.targetAlpha;
			}
		}

		if (this.alpha2 < this.targetAlpha) {
			this.alpha2 += this.alphaStep;
			if (this.alpha2 > this.targetAlpha) {
				this.alpha2 = this.targetAlpha;
			}
		} else if (this.alpha2 > this.targetAlpha) {
			this.alpha2 -= this.alphaStep * 2.0F;
			if (this.alpha2 < this.targetAlpha) {
				this.alpha2 = this.targetAlpha;
			}
		}

		return GameStateMachine.StateAction.Remain;
	}
}
