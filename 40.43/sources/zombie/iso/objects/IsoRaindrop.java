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


public class IsoRaindrop extends IsoObject {
	public int AnimSpriteIndex;
	public float GravMod;
	public int Life;
	public float SplashY;
	public float OffsetY;
	public float Vel_Y;

	public boolean Serialize() {
		return false;
	}

	public IsoRaindrop(IsoCell cell, IsoGridSquare square, boolean boolean1) {
		if (boolean1) {
			if (square != null) {
				if (!square.getProperties().Is(IsoFlagType.HasRaindrop)) {
					this.Life = 0;
					this.square = square;
					int int1 = 1 * Core.TileScale;
					int int2 = 64 * Core.TileScale;
					float float1 = Rand.Next(0.1F, 0.9F);
					float float2 = Rand.Next(0.1F, 0.9F);
					short short1 = (short)((int)(IsoUtils.XToScreen(float1, float2, 0.0F, 0) - (float)(int1 / 2)));
					short short2 = (short)((int)(IsoUtils.YToScreen(float1, float2, 0.0F, 0) - (float)int2));
					this.offsetX = 0.0F;
					this.offsetY = 0.0F;
					this.OffsetY = RainManager.RaindropStartDistance;
					this.SplashY = (float)short2;
					this.AttachAnim("Rain", "00", 1, 0.0F, -short1, -short2, true, 0, false, 0.7F, RainManager.RaindropTintMod);
					if (this.AttachedAnimSpriteActual != null) {
						this.AnimSpriteIndex = this.AttachedAnimSpriteActual.size() - 1;
					} else {
						this.AnimSpriteIndex = 0;
					}

					((IsoSpriteInstance)this.AttachedAnimSprite.get(this.AnimSpriteIndex)).setScale((float)Core.TileScale, (float)Core.TileScale);
					square.getProperties().Set(IsoFlagType.HasRaindrop, "");
					this.Vel_Y = 0.0F;
					float float3 = 1000000.0F / (float)Rand.Next(1000000) + 1.0E-5F;
					this.GravMod = -(RainManager.GravModMin + (RainManager.GravModMax - RainManager.GravModMin) * float3);
					RainManager.AddRaindrop(this);
				}
			}
		}
	}

	public boolean HasTooltip() {
		return false;
	}

	public String getObjectName() {
		return "RainDrops";
	}

	public boolean TestCollide(IsoMovingObject movingObject, IsoGridSquare square) {
		return this.square == square;
	}

	public IsoObject.VisionResult TestVision(IsoGridSquare square, IsoGridSquare square2) {
		return IsoObject.VisionResult.NoEffect;
	}

	public void ChangeTintMod(ColorInfo colorInfo) {
	}

	public void update() {
		this.sx = this.sy = 0;
		++this.Life;
		int int1;
		for (int1 = 0; int1 < this.AttachedAnimSprite.size(); ++int1) {
			IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int1);
			spriteInstance.update();
			spriteInstance.Frame += spriteInstance.AnimFrameIncrease * GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
			if (this.AttachedAnimSpriteActual.size() > int1) {
				IsoSprite sprite = (IsoSprite)this.AttachedAnimSpriteActual.get(int1);
				if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
					spriteInstance.Frame = 0.0F;
				}
			}
		}

		this.Vel_Y += this.GravMod * GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
		this.OffsetY += this.Vel_Y;
		if (this.AttachedAnimSprite != null && this.AttachedAnimSpriteActual.size() > this.AnimSpriteIndex && this.AnimSpriteIndex >= 0) {
			((IsoSprite)this.AttachedAnimSpriteActual.get(this.AnimSpriteIndex)).soffY = (short)((int)(this.SplashY + (float)((int)this.OffsetY)));
		}

		if (this.OffsetY < 0.0F) {
			this.OffsetY = RainManager.RaindropStartDistance;
			this.Vel_Y = 0.0F;
			float float1 = 1000000.0F / (float)Rand.Next(1000000) + 1.0E-5F;
			this.GravMod = -(RainManager.GravModMin + (RainManager.GravModMax - RainManager.GravModMin) * float1);
		}

		for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
				this.alpha[int1] = 0.55F;
				this.targetAlpha[int1] = 0.55F;
			} else {
				this.alpha[int1] = 1.0F;
				this.targetAlpha[int1] = 1.0F;
			}
		}
	}

	void Reset(IsoGridSquare square, boolean boolean1) {
		if (boolean1) {
			if (square != null) {
				if (!square.getProperties().Is(IsoFlagType.HasRaindrop)) {
					this.Life = 0;
					this.square = square;
					this.OffsetY = RainManager.RaindropStartDistance;
					if (this.AttachedAnimSprite != null) {
						this.AnimSpriteIndex = this.AttachedAnimSprite.size() - 1;
					} else {
						this.AnimSpriteIndex = 0;
					}

					square.getProperties().Set(IsoFlagType.HasRaindrop, "");
					this.Vel_Y = 0.0F;
					float float1 = 1000000.0F / (float)Rand.Next(1000000) + 1.0E-5F;
					this.GravMod = -(RainManager.GravModMin + (RainManager.GravModMax - RainManager.GravModMin) * float1);
					RainManager.AddRaindrop(this);
				}
			}
		}
	}
}
