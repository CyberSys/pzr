package zombie.radio.StorySounds;

import fmod.javafmod;
import fmod.fmod.FMODManager;
import java.util.ArrayList;
import java.util.Stack;
import zombie.GameSounds;
import zombie.SoundManager;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;


public class StoryEmitter {
	public int max = -1;
	public float volumeMod = 1.0F;
	public boolean coordinate3D = true;
	public Stack SoundStack = new Stack();
	public ArrayList Instances = new ArrayList();
	public ArrayList ToStart = new ArrayList();
	private Vector2 soundVect = new Vector2();
	private Vector2 playerVect = new Vector2();

	public int stopSound(long long1) {
		return javafmod.FMOD_Channel_Stop(long1);
	}

	public long playSound(String string, float float1, float float2, float float3, float float4, float float5, float float6) {
		if (this.max != -1 && this.max <= this.Instances.size() + this.ToStart.size()) {
			return 0L;
		} else {
			GameSound gameSound = GameSounds.getSound(string);
			if (gameSound == null) {
				return 0L;
			} else {
				GameSoundClip gameSoundClip = gameSound.getRandomClip();
				long long1 = FMODManager.instance.loadSound(string);
				if (long1 == 0L) {
					return 0L;
				} else {
					StoryEmitter.Sound sound;
					if (this.SoundStack.isEmpty()) {
						sound = new StoryEmitter.Sound();
					} else {
						sound = (StoryEmitter.Sound)this.SoundStack.pop();
					}

					sound.minRange = float5;
					sound.maxRange = float6;
					sound.x = float2;
					sound.y = float3;
					sound.z = float4;
					sound.volume = SoundManager.instance.getSoundVolume() * float1 * this.volumeMod;
					sound.sound = long1;
					sound.channel = javafmod.FMOD_System_PlaySound(long1, true);
					this.ToStart.add(sound);
					javafmod.FMOD_Channel_Set3DAttributes(sound.channel, sound.x - IsoPlayer.instance.x, sound.y - IsoPlayer.instance.y, sound.z - IsoPlayer.instance.z, 0.0F, 0.0F, 0.0F);
					javafmod.FMOD_Channel_Set3DOcclusion(sound.channel, 1.0F, 1.0F);
					if (IsoPlayer.instance != null && IsoPlayer.instance.HasTrait("Deaf")) {
						javafmod.FMOD_Channel_SetVolume(sound.channel, 0.0F);
					} else {
						javafmod.FMOD_Channel_SetVolume(sound.channel, sound.volume);
					}

					return sound.channel;
				}
			}
		}
	}

	public void tick() {
		int int1;
		StoryEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (StoryEmitter.Sound)this.ToStart.get(int1);
			javafmod.FMOD_Channel_SetPaused(sound.channel, false);
			this.Instances.add(sound);
		}

		this.ToStart.clear();
		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (StoryEmitter.Sound)this.Instances.get(int1);
			if (!javafmod.FMOD_Channel_IsPlaying(sound.channel)) {
				this.SoundStack.push(sound);
				this.Instances.remove(sound);
				--int1;
			} else {
				float float1 = IsoUtils.DistanceManhatten(sound.x, sound.y, IsoPlayer.instance.x, IsoPlayer.instance.y, sound.z, IsoPlayer.instance.z) / sound.maxRange;
				if (float1 > 1.0F) {
					float1 = 1.0F;
				}

				if (!this.coordinate3D) {
					javafmod.FMOD_Channel_Set3DAttributes(sound.channel, Math.abs(sound.x - IsoPlayer.instance.x), Math.abs(sound.y - IsoPlayer.instance.y), Math.abs(sound.z - IsoPlayer.instance.z), 0.0F, 0.0F, 0.0F);
				} else {
					javafmod.FMOD_Channel_Set3DAttributes(sound.channel, Math.abs(sound.x - IsoPlayer.instance.x), Math.abs(sound.z - IsoPlayer.instance.z), Math.abs(sound.y - IsoPlayer.instance.y), 0.0F, 0.0F, 0.0F);
				}

				javafmod.FMOD_System_SetReverbDefault(0, FMODManager.FMOD_PRESET_MOUNTAINS);
				javafmod.FMOD_Channel_SetReverbProperties(sound.channel, 0, 1.0F);
				javafmod.FMOD_Channel_Set3DMinMaxDistance(sound.channel, sound.minRange, sound.maxRange);
				float float2 = 0.0F;
				float float3 = 0.0F;
				IsoGridSquare square = IsoPlayer.instance.getCurrentSquare();
				this.soundVect.set(sound.x, sound.y);
				this.playerVect.set(IsoPlayer.instance.x, IsoPlayer.instance.y);
				float float4 = (float)Math.toDegrees((double)this.playerVect.angleTo(this.soundVect));
				float float5 = (float)Math.toDegrees((double)IsoPlayer.instance.angle.getDirection());
				if (float5 >= 0.0F && float5 <= 90.0F) {
					float5 = -90.0F - float5;
				} else if (float5 > 90.0F && float5 <= 180.0F) {
					float5 = 90.0F + (180.0F - float5);
				} else if (float5 < 0.0F && float5 >= -90.0F) {
					float5 = 0.0F - (90.0F + float5);
				} else if (float5 < 0.0F && float5 >= -180.0F) {
					float5 = 90.0F - (180.0F + float5);
				}

				float float6 = Math.abs(float4 - float5) % 360.0F;
				float float7 = float6 > 180.0F ? 360.0F - float6 : float6;
				float float8 = (180.0F - float7) / 180.0F;
				float1 /= 0.4F;
				if (float1 > 1.0F) {
					float1 = 1.0F;
				}

				float2 = 0.85F * float1 * float8;
				float3 = 0.85F * float1 * float8;
				if (square.getRoom() != null) {
					float2 = 0.75F + 0.1F * float1 + 0.1F * float8;
					float3 = 0.75F + 0.1F * float1 + 0.1F * float8;
				}

				javafmod.FMOD_Channel_Set3DOcclusion(sound.channel, float2, float3);
			}
		}
	}

	public static class Sound {
		public long sound;
		public long channel;
		public float volume;
		public float x;
		public float y;
		public float z;
		public float minRange;
		public float maxRange;
	}
}
