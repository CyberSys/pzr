package zombie.iso.objects;

import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;


public class IsoRainSplash extends IsoObject {
	public int Age;

	public boolean Serialize() {
		return false;
	}

	public IsoRainSplash(IsoCell cell, IsoGridSquare square) {
		if (square != null) {
			if (!square.getProperties().Is(IsoFlagType.HasRainSplashes)) {
				this.Age = 0;
				this.square = square;
				this.offsetX = 0.0F;
				this.offsetY = 0.0F;
				int int1 = 1 + Rand.Next(2);
				byte byte1 = 16;
				byte byte2 = 8;
				for (int int2 = 0; int2 < int1; ++int2) {
					float float1 = Rand.Next(0.1F, 0.9F);
					float float2 = Rand.Next(0.1F, 0.9F);
					short short1 = (short)((int)(IsoUtils.XToScreen(float1, float2, 0.0F, 0) - (float)(byte1 / 2)));
					short short2 = (short)((int)(IsoUtils.YToScreen(float1, float2, 0.0F, 0) - (float)(byte2 / 2)));
					this.AttachAnim("RainSplash", "00", 4, RainManager.RainSplashAnimDelay, -short1, -short2, true, 0, false, 0.7F, RainManager.RainSplashTintMod);
					((IsoSpriteInstance)this.AttachedAnimSprite.get(int2)).Frame = (float)((short)Rand.Next(4));
					((IsoSpriteInstance)this.AttachedAnimSprite.get(int2)).setScale((float)Core.TileScale, (float)Core.TileScale);
				}

				square.getProperties().Set(IsoFlagType.HasRainSplashes, "Has Rain Splashes");
				RainManager.AddRainSplash(this);
			}
		}
	}

	public String getObjectName() {
		return "RainSplashes";
	}

	public boolean HasTooltip() {
		return false;
	}

	public boolean TestCollide(IsoMovingObject movingObject, IsoGridSquare square) {
		return this.square == square;
	}

	public IsoObject.VisionResult TestVision(IsoGridSquare square, IsoGridSquare square2) {
		return IsoObject.VisionResult.NoEffect;
	}

	public void ChangeTintMod(ColorInfo colorInfo) {
		if (this.AttachedAnimSprite != null) {
			for (int int1 = 0; int1 < this.AttachedAnimSprite.size(); ++int1) {
			}
		}
	}

	public void update() {
		this.sx = this.sy = 0;
		++this.Age;
		int int1;
		for (int1 = 0; int1 < this.AttachedAnimSprite.size(); ++int1) {
			IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int1);
			IsoSprite sprite = (IsoSprite)this.AttachedAnimSpriteActual.get(int1);
			spriteInstance.update();
			spriteInstance.Frame += spriteInstance.AnimFrameIncrease * GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
			if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
				spriteInstance.Frame = 0.0F;
			}
		}

		for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
				this.alpha[int1] = 0.25F;
				this.targetAlpha[int1] = 0.25F;
			} else {
				this.alpha[int1] = 0.6F;
				this.targetAlpha[int1] = 0.6F;
			}
		}
	}

	void Reset(IsoGridSquare square) {
		if (square != null) {
			if (!square.getProperties().Is(IsoFlagType.HasRainSplashes)) {
				this.Age = 0;
				this.square = square;
				int int1 = 1 + Rand.Next(2);
				if (this.AttachedAnimSprite != null) {
					for (int int2 = 0; int2 < this.AttachedAnimSprite.size(); ++int2) {
					}
				}

				square.getProperties().Set(IsoFlagType.HasRainSplashes);
				RainManager.AddRainSplash(this);
			}
		}
	}
}
