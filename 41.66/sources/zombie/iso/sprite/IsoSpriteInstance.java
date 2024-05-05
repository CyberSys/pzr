package zombie.iso.sprite;

import java.util.concurrent.atomic.AtomicBoolean;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCamera;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.popman.ObjectPool;


public final class IsoSpriteInstance {
	public static final ObjectPool pool = new ObjectPool(IsoSpriteInstance::new);
	private static final AtomicBoolean lock = new AtomicBoolean(false);
	public IsoSprite parentSprite;
	public float tintb = 1.0F;
	public float tintg = 1.0F;
	public float tintr = 1.0F;
	public float Frame = 0.0F;
	public float alpha = 1.0F;
	public float targetAlpha = 1.0F;
	public boolean bCopyTargetAlpha = true;
	public boolean bMultiplyObjectAlpha = false;
	public boolean Flip;
	public float offZ = 0.0F;
	public float offX = 0.0F;
	public float offY = 0.0F;
	public float AnimFrameIncrease = 1.0F;
	static float multiplier = 1.0F;
	public boolean Looped = true;
	public boolean Finished = false;
	public boolean NextFrame;
	public float scaleX = 1.0F;
	public float scaleY = 1.0F;

	public static IsoSpriteInstance get(IsoSprite sprite) {
		while (!lock.compareAndSet(false, true)) {
			Thread.onSpinWait();
		}

		IsoSpriteInstance spriteInstance = (IsoSpriteInstance)pool.alloc();
		lock.set(false);
		spriteInstance.parentSprite = sprite;
		spriteInstance.reset();
		return spriteInstance;
	}

	private void reset() {
		this.tintb = 1.0F;
		this.tintg = 1.0F;
		this.tintr = 1.0F;
		this.Frame = 0.0F;
		this.alpha = 1.0F;
		this.targetAlpha = 1.0F;
		this.bCopyTargetAlpha = true;
		this.bMultiplyObjectAlpha = false;
		this.Flip = false;
		this.offZ = 0.0F;
		this.offX = 0.0F;
		this.offY = 0.0F;
		this.AnimFrameIncrease = 1.0F;
		multiplier = 1.0F;
		this.Looped = true;
		this.Finished = false;
		this.NextFrame = false;
		this.scaleX = 1.0F;
		this.scaleY = 1.0F;
	}

	public IsoSpriteInstance() {
	}

	public void setFrameSpeedPerFrame(float float1) {
		this.AnimFrameIncrease = float1 * multiplier;
	}

	public int getID() {
		return this.parentSprite.ID;
	}

	public String getName() {
		return this.parentSprite.getName();
	}

	public IsoSprite getParentSprite() {
		return this.parentSprite;
	}

	public IsoSpriteInstance(IsoSprite sprite) {
		this.parentSprite = sprite;
	}

	public float getTintR() {
		return this.tintr;
	}

	public float getTintG() {
		return this.tintg;
	}

	public float getTintB() {
		return this.tintb;
	}

	public float getAlpha() {
		return this.alpha;
	}

	public float getTargetAlpha() {
		return this.targetAlpha;
	}

	public boolean isCopyTargetAlpha() {
		return this.bCopyTargetAlpha;
	}

	public boolean isMultiplyObjectAlpha() {
		return this.bMultiplyObjectAlpha;
	}

	public void render(IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, ColorInfo colorInfo) {
		this.parentSprite.render(this, object, float1, float2, float3, directions, float4, float5, colorInfo, true);
	}

	public void SetAlpha(float float1) {
		this.alpha = float1;
		this.bCopyTargetAlpha = false;
	}

	public void SetTargetAlpha(float float1) {
		this.targetAlpha = float1;
		this.bCopyTargetAlpha = false;
	}

	public void update() {
	}

	protected void renderprep(IsoObject object) {
		if (object != null && this.bCopyTargetAlpha) {
			this.targetAlpha = object.getTargetAlpha(IsoCamera.frameState.playerIndex);
			this.alpha = object.getAlpha(IsoCamera.frameState.playerIndex);
		} else if (!this.bMultiplyObjectAlpha) {
			if (this.alpha < this.targetAlpha) {
				this.alpha += IsoSprite.alphaStep;
				if (this.alpha > this.targetAlpha) {
					this.alpha = this.targetAlpha;
				}
			} else if (this.alpha > this.targetAlpha) {
				this.alpha -= IsoSprite.alphaStep;
				if (this.alpha < this.targetAlpha) {
					this.alpha = this.targetAlpha;
				}
			}

			if (this.alpha < 0.0F) {
				this.alpha = 0.0F;
			}

			if (this.alpha > 1.0F) {
				this.alpha = 1.0F;
			}
		}
	}

	public float getFrame() {
		return this.Frame;
	}

	public boolean isFinished() {
		return this.Finished;
	}

	public void Dispose() {
	}

	public void RenderGhostTileColor(int int1, int int2, int int3, float float1, float float2, float float3, float float4) {
		if (this.parentSprite != null) {
			IsoSpriteInstance spriteInstance = get(this.parentSprite);
			spriteInstance.Frame = this.Frame;
			spriteInstance.tintr = float1;
			spriteInstance.tintg = float2;
			spriteInstance.tintb = float3;
			spriteInstance.alpha = spriteInstance.targetAlpha = float4;
			IsoGridSquare.getDefColorInfo().r = IsoGridSquare.getDefColorInfo().g = IsoGridSquare.getDefColorInfo().b = IsoGridSquare.getDefColorInfo().a = 1.0F;
			this.parentSprite.render(spriteInstance, (IsoObject)null, (float)int1, (float)int2, (float)int3, IsoDirections.N, 0.0F, -144.0F, IsoGridSquare.getDefColorInfo(), true);
		}
	}

	public void setScale(float float1, float float2) {
		this.scaleX = float1;
		this.scaleY = float2;
	}

	public float getScaleX() {
		return this.scaleX;
	}

	public float getScaleY() {
		return this.scaleY;
	}

	public void scaleAspect(float float1, float float2, float float3, float float4) {
		if (float1 > 0.0F && float2 > 0.0F && float3 > 0.0F && float4 > 0.0F) {
			float float5 = float4 * float1 / float2;
			float float6 = float3 * float2 / float1;
			boolean boolean1 = float5 <= float3;
			if (boolean1) {
				float3 = float5;
			} else {
				float4 = float6;
			}

			this.scaleX = float3 / float1;
			this.scaleY = float4 / float2;
		}
	}

	public static void add(IsoSpriteInstance spriteInstance) {
		spriteInstance.reset();
		while (!lock.compareAndSet(false, true)) {
			Thread.onSpinWait();
		}

		pool.release((Object)spriteInstance);
		lock.set(false);
	}
}
