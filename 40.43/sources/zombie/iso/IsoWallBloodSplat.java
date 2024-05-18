package zombie.iso;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.textures.ColorInfo;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;


public class IsoWallBloodSplat {
	public float worldAge;
	public IsoSprite sprite;
	private static ColorInfo info = new ColorInfo();

	public IsoWallBloodSplat() {
	}

	public IsoWallBloodSplat(float float1, IsoSprite sprite) {
		this.worldAge = float1;
		this.sprite = sprite;
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo) {
		if (this.sprite != null) {
			if (this.sprite.CurrentAnim != null && !this.sprite.CurrentAnim.Frames.isEmpty()) {
				int int1 = Core.TileScale;
				int int2 = 32 * int1;
				int int3 = 96 * int1;
				if (IsoSprite.globalOffsetX == -1) {
					IsoSprite.globalOffsetX = -((int)IsoCamera.frameState.OffX);
					IsoSprite.globalOffsetY = -((int)IsoCamera.frameState.OffY);
				}

				float float4 = IsoUtils.XToScreen(float1, float2, float3, 0);
				float float5 = IsoUtils.YToScreen(float1, float2, float3, 0);
				float4 = (float)((int)float4);
				float5 = (float)((int)float5);
				float4 -= (float)int2;
				float5 -= (float)int3;
				float4 += (float)IsoSprite.globalOffsetX;
				float5 += (float)IsoSprite.globalOffsetY;
				if (!(float4 >= (float)IsoCamera.frameState.OffscreenWidth) && !(float4 + (float)(64 * int1) <= 0.0F)) {
					if (!(float5 >= (float)IsoCamera.frameState.OffscreenHeight) && !(float5 + (float)(128 * int1) <= 0.0F)) {
						info.r = 0.7F * colorInfo.r;
						info.g = 0.9F * colorInfo.g;
						info.b = 0.9F * colorInfo.b;
						info.a = 0.4F;
						float float6 = (float)GameTime.getInstance().getWorldAgeHours();
						float float7 = float6 - this.worldAge;
						ColorInfo colorInfo2;
						if (float7 >= 0.0F && float7 < 72.0F) {
							float float8 = 1.0F - float7 / 72.0F;
							colorInfo2 = info;
							colorInfo2.r *= 0.2F + float8 * 0.8F;
							colorInfo2 = info;
							colorInfo2.g *= 0.2F + float8 * 0.8F;
							colorInfo2 = info;
							colorInfo2.b *= 0.2F + float8 * 0.8F;
							colorInfo2 = info;
							colorInfo2.a *= 0.25F + float8 * 0.75F;
						} else {
							colorInfo2 = info;
							colorInfo2.r *= 0.2F;
							colorInfo2 = info;
							colorInfo2.g *= 0.2F;
							colorInfo2 = info;
							colorInfo2.b *= 0.2F;
							colorInfo2 = info;
							colorInfo2.a *= 0.25F;
						}

						info.a = Math.max(info.a, 0.15F);
						((IsoDirectionFrame)this.sprite.CurrentAnim.Frames.get(0)).render((float)((int)float4), (float)((int)float5), IsoDirections.N, info, false, this.sprite.Angle);
					}
				}
			}
		}
	}

	public void save(ByteBuffer byteBuffer) {
		byteBuffer.putFloat(this.worldAge);
		byteBuffer.putInt(this.sprite.ID);
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.worldAge = byteBuffer.getFloat();
		int int2 = byteBuffer.getInt();
		this.sprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, int2);
	}
}
