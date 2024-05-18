package zombie.radio.StorySounds;

import zombie.characters.IsoPlayer;
import zombie.iso.Vector2;


public class StorySound {
	protected String name = null;
	protected float baseVolume = 1.0F;

	public StorySound(String string, float float1) {
		this.name = string;
		this.baseVolume = float1;
	}

	public long playSound() {
		Vector2 vector2 = SLSoundManager.getInstance().getRandomBorderPosition();
		return SLSoundManager.Emitter.playSound(this.name, this.baseVolume, vector2.x, vector2.y, 0.0F, 100.0F, SLSoundManager.getInstance().getRandomBorderRange());
	}

	public long playSound(float float1) {
		return SLSoundManager.Emitter.playSound(this.name, float1, IsoPlayer.instance.x, IsoPlayer.instance.y, IsoPlayer.instance.z, 10.0F, 50.0F);
	}

	public long playSound(float float1, float float2, float float3, float float4, float float5) {
		return this.playSound(this.baseVolume, float1, float2, float3, float4, float5);
	}

	public long playSound(float float1, float float2, float float3, float float4, float float5, float float6) {
		return SLSoundManager.Emitter.playSound(this.name, this.baseVolume * float1, float2, float3, float4, float5, float6);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String string) {
		this.name = string;
	}

	public float getBaseVolume() {
		return this.baseVolume;
	}

	public void setBaseVolume(float float1) {
		this.baseVolume = float1;
	}

	public StorySound getClone() {
		return new StorySound(this.name, this.baseVolume);
	}
}
