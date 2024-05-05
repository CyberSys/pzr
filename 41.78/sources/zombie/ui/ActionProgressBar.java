package zombie.ui;

import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;


public final class ActionProgressBar extends UIElement {
	Texture background = Texture.getSharedTexture("BuildBar_Bkg");
	Texture foreground = Texture.getSharedTexture("BuildBar_Bar");
	float deltaValue = 1.0F;
	float animationProgress = 0.0F;
	public int delayHide = 0;
	private final int offsetX;
	private final int offsetY;

	public ActionProgressBar(int int1, int int2) {
		this.offsetX = int1;
		this.offsetY = int2;
		this.width = (float)this.background.getWidth();
		this.height = (float)this.background.getHeight();
		this.followGameWorld = true;
	}

	public void render() {
		if (this.isVisible() && UIManager.VisibleAllUI) {
			this.DrawUVSliceTexture(this.background, 0.0, 0.0, (double)this.background.getWidth(), (double)this.background.getHeight(), Color.white, 0.0, 0.0, 1.0, 1.0);
			if (this.deltaValue == Float.POSITIVE_INFINITY) {
				if (this.animationProgress < 0.5F) {
					this.DrawUVSliceTexture(this.foreground, 3.0, 0.0, (double)this.foreground.getWidth(), (double)this.foreground.getHeight(), Color.white, 0.0, 0.0, (double)(this.animationProgress * 2.0F), 1.0);
				} else {
					this.DrawUVSliceTexture(this.foreground, 3.0, 0.0, (double)this.foreground.getWidth(), (double)this.foreground.getHeight(), Color.white, (double)((this.animationProgress - 0.5F) * 2.0F), 0.0, 1.0, 1.0);
				}
			} else {
				this.DrawUVSliceTexture(this.foreground, 3.0, 0.0, (double)this.foreground.getWidth(), (double)this.foreground.getHeight(), Color.white, 0.0, 0.0, (double)this.deltaValue, 1.0);
			}
		}
	}

	public void setValue(float float1) {
		this.deltaValue = float1;
	}

	public float getValue() {
		return this.deltaValue;
	}

	public void update(int int1) {
		if (this.deltaValue == Float.POSITIVE_INFINITY) {
			this.animationProgress += 0.02F * GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * 60.0F;
			if (this.animationProgress > 1.0F) {
				this.animationProgress = 0.0F;
			}

			this.setVisible(true);
			this.updateScreenPos(int1);
			this.delayHide = 2;
		} else {
			if (this.getValue() > 0.0F && this.getValue() < 1.0F) {
				this.setVisible(true);
				this.delayHide = 2;
			} else if (this.isVisible() && this.delayHide > 0 && --this.delayHide == 0) {
				this.setVisible(false);
			}

			if (!UIManager.VisibleAllUI) {
				this.setVisible(false);
			}

			if (this.isVisible()) {
				this.updateScreenPos(int1);
			}
		}
	}

	private void updateScreenPos(int int1) {
		IsoPlayer player = IsoPlayer.players[int1];
		if (player != null) {
			float float1 = IsoUtils.XToScreen(player.getX(), player.getY(), player.getZ(), 0);
			float float2 = IsoUtils.YToScreen(player.getX(), player.getY(), player.getZ(), 0);
			float1 = float1 - IsoCamera.getOffX() - player.offsetX;
			float2 = float2 - IsoCamera.getOffY() - player.offsetY;
			float2 -= (float)(128 / (2 / Core.TileScale));
			float1 /= Core.getInstance().getZoom(int1);
			float2 /= Core.getInstance().getZoom(int1);
			float1 -= this.width / 2.0F;
			float2 -= this.height;
			if (player.getUserNameHeight() > 0) {
				float2 -= (float)(player.getUserNameHeight() + 2);
			}

			this.setX((double)(float1 + (float)this.offsetX));
			this.setY((double)(float2 + (float)this.offsetY));
		}
	}
}
