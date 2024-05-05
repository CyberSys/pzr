package fmod.fmod;

import fmod.FMOD_STUDIO_EVENT_PROPERTY;
import fmod.javafmod;
import fmod.javafmodJNI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import zombie.GameSounds;
import zombie.SoundManager;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.FMODParameter;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.audio.parameters.ParameterOcclusion;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.SoundTimelineScript;


public final class FMODSoundEmitter extends BaseSoundEmitter {
	private final ArrayList ToStart = new ArrayList();
	private final ArrayList Instances = new ArrayList();
	public float x;
	public float y;
	public float z;
	public EmitterType emitterType;
	public IsoObject parent;
	private final ParameterOcclusion occlusion = new ParameterOcclusion(this);
	private final ArrayList parameters = new ArrayList();
	public IFMODParameterUpdater parameterUpdater;
	private final ArrayList parameterValues = new ArrayList();
	private static final ObjectPool parameterValuePool = new ObjectPool(FMODSoundEmitter.ParameterValue::new);
	private static BitSet parameterSet;
	private final ArrayDeque eventSoundPool = new ArrayDeque();
	private final ArrayDeque fileSoundPool = new ArrayDeque();
	private static long CurrentTimeMS = 0L;

	public FMODSoundEmitter() {
		SoundManager.instance.registerEmitter(this);
		if (parameterSet == null) {
			parameterSet = new BitSet(FMODManager.instance.getParameterCount());
		}
	}

	public void randomStart() {
	}

	public void setPos(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
	}

	public int stopSound(long long1) {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			if (sound.getRef() == long1) {
				this.sendStopSound(sound.name, false);
				sound.release();
				this.ToStart.remove(int1--);
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			if (sound.getRef() == long1) {
				sound.stop();
				this.sendStopSound(sound.name, false);
				sound.release();
				this.Instances.remove(int1--);
			}
		}

		return 0;
	}

	public int stopSoundByName(String string) {
		GameSound gameSound = GameSounds.getSound(string);
		if (gameSound == null) {
			return 0;
		} else {
			int int1;
			FMODSoundEmitter.Sound sound;
			for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
				sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
				if (gameSound.clips.contains(sound.clip)) {
					sound.release();
					this.ToStart.remove(int1--);
				}
			}

			for (int1 = 0; int1 < this.Instances.size(); ++int1) {
				sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
				if (gameSound.clips.contains(sound.clip)) {
					sound.stop();
					sound.release();
					this.Instances.remove(int1--);
				}
			}

			return 0;
		}
	}

	public void stopOrTriggerSound(long long1) {
		int int1 = this.findToStart(long1);
		FMODSoundEmitter.Sound sound;
		if (int1 != -1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.remove(int1);
			this.sendStopSound(sound.name, true);
			sound.release();
		} else {
			int1 = this.findInstance(long1);
			if (int1 != -1) {
				sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
				this.sendStopSound(sound.name, true);
				if (sound.clip.hasSustainPoints()) {
					sound.triggerCue();
				} else {
					this.Instances.remove(int1);
					sound.stop();
					sound.release();
				}
			}
		}
	}

	public void stopOrTriggerSoundByName(String string) {
		GameSound gameSound = GameSounds.getSound(string);
		if (gameSound != null) {
			int int1;
			FMODSoundEmitter.Sound sound;
			for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
				sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
				if (gameSound.clips.contains(sound.clip)) {
					this.ToStart.remove(int1--);
					sound.release();
				}
			}

			for (int1 = 0; int1 < this.Instances.size(); ++int1) {
				sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
				if (gameSound.clips.contains(sound.clip)) {
					if (sound.clip.hasSustainPoints()) {
						sound.triggerCue();
					} else {
						sound.stop();
						sound.release();
						this.Instances.remove(int1--);
					}
				}
			}
		}
	}

	public void setVolume(long long1, float float1) {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			if (sound.getRef() == long1) {
				sound.volume = float1;
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			if (sound.getRef() == long1) {
				sound.volume = float1;
			}
		}
	}

	public void setPitch(long long1, float float1) {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			if (sound.getRef() == long1) {
				sound.pitch = float1;
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			if (sound.getRef() == long1) {
				sound.pitch = float1;
			}
		}
	}

	public boolean hasSustainPoints(long long1) {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			if (sound.getRef() == long1) {
				if (sound.clip.eventDescription == null) {
					return false;
				}

				return sound.clip.eventDescription.bHasSustainPoints;
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			if (sound.getRef() == long1) {
				if (sound.clip.eventDescription == null) {
					return false;
				}

				return sound.clip.eventDescription.bHasSustainPoints;
			}
		}

		return false;
	}

	public void triggerCue(long long1) {
		for (int int1 = 0; int1 < this.Instances.size(); ++int1) {
			FMODSoundEmitter.Sound sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			if (sound.getRef() == long1) {
				sound.triggerCue();
			}
		}
	}

	public void setVolumeAll(float float1) {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			sound.volume = float1;
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			sound.volume = float1;
		}
	}

	public void stopAll() {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			sound.release();
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			sound.stop();
			sound.release();
		}

		this.ToStart.clear();
		this.Instances.clear();
	}

	public long playSound(String string) {
		if (GameClient.bClient) {
			if (this.parent instanceof IsoMovingObject) {
				if (!(this.parent instanceof IsoPlayer) || !((IsoPlayer)this.parent).isInvisible()) {
					GameClient.instance.PlaySound(string, false, (IsoMovingObject)this.parent);
				}
			} else {
				GameClient.instance.PlayWorldSound(string, (int)this.x, (int)this.y, (byte)((int)this.z));
			}
		}

		return GameServer.bServer ? 0L : this.playSoundImpl(string, (IsoObject)null);
	}

	public long playSound(String string, IsoGameCharacter gameCharacter) {
		if (GameClient.bClient) {
			if (!gameCharacter.isInvisible()) {
				GameClient.instance.PlaySound(string, false, gameCharacter);
			}

			return gameCharacter.isInvisible() && !DebugOptions.instance.Character.Debug.PlaySoundWhenInvisible.getValue() ? 0L : this.playSoundImpl(string, (IsoObject)null);
		} else {
			return GameServer.bServer ? 0L : this.playSoundImpl(string, (IsoObject)null);
		}
	}

	public long playSound(String string, int int1, int int2, int int3) {
		this.x = (float)int1;
		this.y = (float)int2;
		this.z = (float)int3;
		return this.playSound(string);
	}

	public long playSound(String string, IsoGridSquare square) {
		this.x = (float)square.x + 0.5F;
		this.y = (float)square.y + 0.5F;
		this.z = (float)square.z;
		return this.playSound(string);
	}

	public long playSoundImpl(String string, IsoGridSquare square) {
		this.x = (float)square.x + 0.5F;
		this.y = (float)square.y + 0.5F;
		this.z = (float)square.z + 0.5F;
		return this.playSoundImpl(string, (IsoObject)null);
	}

	public long playSound(String string, boolean boolean1) {
		return this.playSound(string);
	}

	public long playSoundImpl(String string, boolean boolean1, IsoObject object) {
		return this.playSoundImpl(string, object);
	}

	public long playSoundLooped(String string) {
		if (GameClient.bClient) {
			if (this.parent instanceof IsoMovingObject) {
				GameClient.instance.PlaySound(string, true, (IsoMovingObject)this.parent);
			} else {
				GameClient.instance.PlayWorldSound(string, (int)this.x, (int)this.y, (byte)((int)this.z));
			}
		}

		return this.playSoundLoopedImpl(string);
	}

	public long playSoundLoopedImpl(String string) {
		return this.playSoundImpl(string, false, (IsoObject)null);
	}

	public long playSound(String string, IsoObject object) {
		if (GameClient.bClient) {
			if (object instanceof IsoMovingObject) {
				GameClient.instance.PlaySound(string, false, (IsoMovingObject)this.parent);
			} else {
				GameClient.instance.PlayWorldSound(string, (int)this.x, (int)this.y, (byte)((int)this.z));
			}
		}

		return GameServer.bServer ? 0L : this.playSoundImpl(string, object);
	}

	public long playSoundImpl(String string, IsoObject object) {
		GameSound gameSound = GameSounds.getSound(string);
		if (gameSound == null) {
			return 0L;
		} else {
			GameSoundClip gameSoundClip = gameSound.getRandomClip();
			return this.playClip(gameSoundClip, object);
		}
	}

	public long playClip(GameSoundClip gameSoundClip, IsoObject object) {
		FMODSoundEmitter.Sound sound = this.addSound(gameSoundClip, 1.0F, object);
		return sound == null ? 0L : sound.getRef();
	}

	public long playAmbientSound(String string) {
		if (GameServer.bServer) {
			return 0L;
		} else {
			GameSound gameSound = GameSounds.getSound(string);
			if (gameSound == null) {
				return 0L;
			} else {
				GameSoundClip gameSoundClip = gameSound.getRandomClip();
				FMODSoundEmitter.Sound sound = this.addSound(gameSoundClip, 1.0F, (IsoObject)null);
				if (sound instanceof FMODSoundEmitter.FileSound) {
					((FMODSoundEmitter.FileSound)sound).ambient = true;
				}

				return sound == null ? 0L : sound.getRef();
			}
		}
	}

	public long playAmbientLoopedImpl(String string) {
		if (GameServer.bServer) {
			return 0L;
		} else {
			GameSound gameSound = GameSounds.getSound(string);
			if (gameSound == null) {
				return 0L;
			} else {
				GameSoundClip gameSoundClip = gameSound.getRandomClip();
				FMODSoundEmitter.Sound sound = this.addSound(gameSoundClip, 1.0F, (IsoObject)null);
				return sound == null ? 0L : sound.getRef();
			}
		}
	}

	public void set3D(long long1, boolean boolean1) {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			if (sound.getRef() == long1) {
				sound.set3D(boolean1);
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			if (sound.getRef() == long1) {
				sound.set3D(boolean1);
			}
		}
	}

	public void tick() {
		int int1;
		if (!this.isEmpty()) {
			this.occlusion.update();
			for (int1 = 0; int1 < this.parameters.size(); ++int1) {
				FMODParameter fMODParameter = (FMODParameter)this.parameters.get(int1);
				fMODParameter.update();
			}
		}

		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			this.Instances.add(sound);
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			boolean boolean1 = this.ToStart.contains(sound);
			if (sound.tick(boolean1)) {
				this.Instances.remove(int1--);
				sound.release();
			}
		}

		this.ToStart.clear();
	}

	public boolean hasSoundsToStart() {
		return !this.ToStart.isEmpty();
	}

	public boolean isEmpty() {
		return this.ToStart.isEmpty() && this.Instances.isEmpty();
	}

	public boolean isPlaying(long long1) {
		int int1;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			if (((FMODSoundEmitter.Sound)this.ToStart.get(int1)).getRef() == long1) {
				return true;
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			if (((FMODSoundEmitter.Sound)this.Instances.get(int1)).getRef() == long1) {
				return true;
			}
		}

		return false;
	}

	public boolean isPlaying(String string) {
		int int1;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			if (string.equals(((FMODSoundEmitter.Sound)this.ToStart.get(int1)).name)) {
				return true;
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			if (string.equals(((FMODSoundEmitter.Sound)this.Instances.get(int1)).name)) {
				return true;
			}
		}

		return false;
	}

	public boolean restart(long long1) {
		int int1 = this.findToStart(long1);
		if (int1 != -1) {
			return true;
		} else {
			int1 = this.findInstance(long1);
			return int1 != -1 && ((FMODSoundEmitter.Sound)this.Instances.get(int1)).restart();
		}
	}

	private int findInstance(long long1) {
		for (int int1 = 0; int1 < this.Instances.size(); ++int1) {
			FMODSoundEmitter.Sound sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			if (sound.getRef() == long1) {
				return int1;
			}
		}

		return -1;
	}

	private int findInstance(String string) {
		GameSound gameSound = GameSounds.getSound(string);
		if (gameSound == null) {
			return -1;
		} else {
			for (int int1 = 0; int1 < this.Instances.size(); ++int1) {
				FMODSoundEmitter.Sound sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
				if (gameSound.clips.contains(sound.clip)) {
					return int1;
				}
			}

			return -1;
		}
	}

	private int findToStart(long long1) {
		for (int int1 = 0; int1 < this.ToStart.size(); ++int1) {
			FMODSoundEmitter.Sound sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			if (sound.getRef() == long1) {
				return int1;
			}
		}

		return -1;
	}

	private int findToStart(String string) {
		GameSound gameSound = GameSounds.getSound(string);
		if (gameSound == null) {
			return -1;
		} else {
			for (int int1 = 0; int1 < this.ToStart.size(); ++int1) {
				FMODSoundEmitter.Sound sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
				if (gameSound.clips.contains(sound.clip)) {
					return int1;
				}
			}

			return -1;
		}
	}

	public void addParameter(FMODParameter fMODParameter) {
		this.parameters.add(fMODParameter);
	}

	public void setParameterValue(long long1, FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION, float float1) {
		if (long1 != 0L && fMOD_STUDIO_PARAMETER_DESCRIPTION != null) {
			int int1 = this.findInstance(long1);
			if (int1 != -1) {
				FMODSoundEmitter.Sound sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
				sound.setParameterValue(fMOD_STUDIO_PARAMETER_DESCRIPTION, float1);
			} else {
				int1 = this.findParameterValue(long1, fMOD_STUDIO_PARAMETER_DESCRIPTION);
				if (int1 != -1) {
					((FMODSoundEmitter.ParameterValue)this.parameterValues.get(int1)).value = float1;
				} else {
					FMODSoundEmitter.ParameterValue parameterValue = (FMODSoundEmitter.ParameterValue)parameterValuePool.alloc();
					parameterValue.eventInstance = long1;
					parameterValue.parameterDescription = fMOD_STUDIO_PARAMETER_DESCRIPTION;
					parameterValue.value = float1;
					this.parameterValues.add(parameterValue);
				}
			}
		}
	}

	public void setTimelinePosition(long long1, String string) {
		if (long1 != 0L) {
			int int1 = this.findToStart(long1);
			if (int1 != -1) {
				FMODSoundEmitter.Sound sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
				sound.setTimelinePosition(string);
			}
		}
	}

	private int findParameterValue(long long1, FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION) {
		for (int int1 = 0; int1 < this.parameterValues.size(); ++int1) {
			FMODSoundEmitter.ParameterValue parameterValue = (FMODSoundEmitter.ParameterValue)this.parameterValues.get(int1);
			if (parameterValue.eventInstance == long1 && parameterValue.parameterDescription == fMOD_STUDIO_PARAMETER_DESCRIPTION) {
				return int1;
			}
		}

		return -1;
	}

	public void clearParameters() {
		this.occlusion.resetToDefault();
		this.parameters.clear();
		parameterValuePool.releaseAll(this.parameterValues);
		this.parameterValues.clear();
	}

	private void startEvent(long long1, GameSoundClip gameSoundClip) {
		parameterSet.clear();
		ArrayList arrayList = this.parameters;
		ArrayList arrayList2 = gameSoundClip.eventDescription.parameters;
		for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
			FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION = (FMOD_STUDIO_PARAMETER_DESCRIPTION)arrayList2.get(int1);
			int int2 = this.findParameterValue(long1, fMOD_STUDIO_PARAMETER_DESCRIPTION);
			if (int2 != -1) {
				FMODSoundEmitter.ParameterValue parameterValue = (FMODSoundEmitter.ParameterValue)this.parameterValues.get(int2);
				javafmod.FMOD_Studio_EventInstance_SetParameterByID(long1, fMOD_STUDIO_PARAMETER_DESCRIPTION.id, parameterValue.value, false);
				parameterSet.set(fMOD_STUDIO_PARAMETER_DESCRIPTION.globalIndex, true);
			} else if (fMOD_STUDIO_PARAMETER_DESCRIPTION == this.occlusion.getParameterDescription()) {
				this.occlusion.startEventInstance(long1);
				parameterSet.set(fMOD_STUDIO_PARAMETER_DESCRIPTION.globalIndex, true);
			} else {
				for (int int3 = 0; int3 < arrayList.size(); ++int3) {
					FMODParameter fMODParameter = (FMODParameter)arrayList.get(int3);
					if (fMODParameter.getParameterDescription() == fMOD_STUDIO_PARAMETER_DESCRIPTION) {
						fMODParameter.startEventInstance(long1);
						parameterSet.set(fMOD_STUDIO_PARAMETER_DESCRIPTION.globalIndex, true);
						break;
					}
				}
			}
		}

		if (this.parameterUpdater != null) {
			this.parameterUpdater.startEvent(long1, gameSoundClip, parameterSet);
		}
	}

	private void updateEvent(long long1, GameSoundClip gameSoundClip) {
		if (this.parameterUpdater != null) {
			this.parameterUpdater.updateEvent(long1, gameSoundClip);
		}
	}

	private void stopEvent(long long1, GameSoundClip gameSoundClip) {
		parameterSet.clear();
		ArrayList arrayList = this.parameters;
		ArrayList arrayList2 = gameSoundClip.eventDescription.parameters;
		for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
			FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION = (FMOD_STUDIO_PARAMETER_DESCRIPTION)arrayList2.get(int1);
			int int2 = this.findParameterValue(long1, fMOD_STUDIO_PARAMETER_DESCRIPTION);
			if (int2 != -1) {
				FMODSoundEmitter.ParameterValue parameterValue = (FMODSoundEmitter.ParameterValue)this.parameterValues.remove(int2);
				parameterValuePool.release((Object)parameterValue);
				parameterSet.set(fMOD_STUDIO_PARAMETER_DESCRIPTION.globalIndex, true);
			} else if (fMOD_STUDIO_PARAMETER_DESCRIPTION == this.occlusion.getParameterDescription()) {
				this.occlusion.stopEventInstance(long1);
				parameterSet.set(fMOD_STUDIO_PARAMETER_DESCRIPTION.globalIndex, true);
			} else {
				for (int int3 = 0; int3 < arrayList.size(); ++int3) {
					FMODParameter fMODParameter = (FMODParameter)arrayList.get(int3);
					if (fMODParameter.getParameterDescription() == fMOD_STUDIO_PARAMETER_DESCRIPTION) {
						fMODParameter.stopEventInstance(long1);
						parameterSet.set(fMOD_STUDIO_PARAMETER_DESCRIPTION.globalIndex, true);
						break;
					}
				}
			}
		}

		if (this.parameterUpdater != null) {
			this.parameterUpdater.stopEvent(long1, gameSoundClip, parameterSet);
		}
	}

	private FMODSoundEmitter.EventSound allocEventSound() {
		return this.eventSoundPool.isEmpty() ? new FMODSoundEmitter.EventSound(this) : (FMODSoundEmitter.EventSound)this.eventSoundPool.pop();
	}

	private void releaseEventSound(FMODSoundEmitter.EventSound eventSound) {
		assert !this.eventSoundPool.contains(eventSound);
		this.eventSoundPool.push(eventSound);
	}

	private FMODSoundEmitter.FileSound allocFileSound() {
		return this.fileSoundPool.isEmpty() ? new FMODSoundEmitter.FileSound(this) : (FMODSoundEmitter.FileSound)this.fileSoundPool.pop();
	}

	private void releaseFileSound(FMODSoundEmitter.FileSound fileSound) {
		assert !this.fileSoundPool.contains(fileSound);
		this.fileSoundPool.push(fileSound);
	}

	private FMODSoundEmitter.Sound addSound(GameSoundClip gameSoundClip, float float1, IsoObject object) {
		if (gameSoundClip == null) {
			DebugLog.log("null sound passed to SoundEmitter.playSoundImpl");
			return null;
		} else {
			long long1;
			if (gameSoundClip.event != null && !gameSoundClip.event.isEmpty()) {
				if (gameSoundClip.eventDescription == null) {
					return null;
				} else {
					long1 = javafmod.FMOD_Studio_System_CreateEventInstance(gameSoundClip.eventDescription.address);
					if (long1 < 0L) {
						return null;
					} else {
						if (gameSoundClip.hasMinDistance()) {
							javafmodJNI.FMOD_Studio_EventInstance_SetProperty(long1, FMOD_STUDIO_EVENT_PROPERTY.FMOD_STUDIO_EVENT_PROPERTY_MINIMUM_DISTANCE.ordinal(), gameSoundClip.getMinDistance());
						}

						if (gameSoundClip.hasMaxDistance()) {
							javafmodJNI.FMOD_Studio_EventInstance_SetProperty(long1, FMOD_STUDIO_EVENT_PROPERTY.FMOD_STUDIO_EVENT_PROPERTY_MAXIMUM_DISTANCE.ordinal(), gameSoundClip.getMaxDistance());
						}

						FMODSoundEmitter.EventSound eventSound = this.allocEventSound();
						eventSound.clip = gameSoundClip;
						eventSound.name = gameSoundClip.gameSound.getName();
						eventSound.eventInstance = long1;
						eventSound.volume = float1;
						eventSound.parent = object;
						eventSound.setVolume = 1.0F;
						eventSound.setX = eventSound.setY = eventSound.setZ = 0.0F;
						this.ToStart.add(eventSound);
						return eventSound;
					}
				}
			} else if (gameSoundClip.file != null && !gameSoundClip.file.isEmpty()) {
				long1 = FMODManager.instance.loadSound(gameSoundClip.file);
				if (long1 == 0L) {
					return null;
				} else {
					long long2 = javafmod.FMOD_System_PlaySound(long1, true);
					javafmod.FMOD_Channel_SetVolume(long2, 0.0F);
					javafmod.FMOD_Channel_SetPriority(long2, 9 - gameSoundClip.priority);
					javafmod.FMOD_Channel_SetChannelGroup(long2, FMODManager.instance.channelGroupInGameNonBankSounds);
					if (gameSoundClip.distanceMax == 0.0F || this.x == 0.0F && this.y == 0.0F) {
						javafmod.FMOD_Channel_SetMode(long2, (long)FMODManager.FMOD_2D);
					}

					FMODSoundEmitter.FileSound fileSound = this.allocFileSound();
					fileSound.clip = gameSoundClip;
					fileSound.name = gameSoundClip.gameSound.getName();
					fileSound.sound = long1;
					fileSound.pitch = gameSoundClip.pitch;
					fileSound.channel = long2;
					fileSound.parent = object;
					fileSound.volume = float1;
					fileSound.setVolume = 1.0F;
					fileSound.setX = fileSound.setY = fileSound.setZ = 0.0F;
					fileSound.is3D = -1;
					fileSound.ambient = false;
					this.ToStart.add(fileSound);
					return fileSound;
				}
			} else {
				return null;
			}
		}
	}

	private void sendStopSound(String string, boolean boolean1) {
		if (GameClient.bClient && this.parent instanceof IsoMovingObject) {
			GameClient.instance.StopSound((IsoMovingObject)this.parent, string, boolean1);
		}
	}

	public static void update() {
		CurrentTimeMS = System.currentTimeMillis();
	}

	private abstract static class Sound {
		FMODSoundEmitter emitter;
		public GameSoundClip clip;
		public String name;
		public float volume = 1.0F;
		public float pitch = 1.0F;
		public IsoObject parent;
		public float setVolume = 1.0F;
		float setX = 0.0F;
		float setY = 0.0F;
		float setZ = 0.0F;

		Sound(FMODSoundEmitter fMODSoundEmitter) {
			this.emitter = fMODSoundEmitter;
		}

		abstract long getRef();

		abstract void stop();

		abstract void set3D(boolean boolean1);

		abstract void release();

		abstract boolean tick(boolean boolean1);

		float getVolume() {
			this.clip = this.clip.checkReloaded();
			return this.volume * this.clip.getEffectiveVolume();
		}

		abstract void setParameterValue(FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION, float float1);

		abstract void setTimelinePosition(String string);

		abstract void triggerCue();

		abstract boolean restart();
	}

	private static final class FileSound extends FMODSoundEmitter.Sound {
		long sound;
		long channel;
		byte is3D = -1;
		boolean ambient;
		float lx;
		float ly;
		float lz;

		FileSound(FMODSoundEmitter fMODSoundEmitter) {
			super(fMODSoundEmitter);
		}

		long getRef() {
			return this.channel;
		}

		void stop() {
			if (this.channel != 0L) {
				javafmod.FMOD_Channel_Stop(this.channel);
				this.sound = 0L;
				this.channel = 0L;
			}
		}

		void set3D(boolean boolean1) {
			if (this.is3D != (byte)(boolean1 ? 1 : 0)) {
				javafmod.FMOD_Channel_SetMode(this.channel, boolean1 ? (long)FMODManager.FMOD_3D : (long)FMODManager.FMOD_2D);
				if (boolean1) {
					javafmod.FMOD_Channel_Set3DAttributes(this.channel, this.emitter.x, this.emitter.y, this.emitter.z * 3.0F, 0.0F, 0.0F, 0.0F);
				}

				this.is3D = (byte)(boolean1 ? 1 : 0);
			}
		}

		void release() {
			this.stop();
			this.emitter.releaseFileSound(this);
		}

		boolean tick(boolean boolean1) {
			if (boolean1 && this.clip.gameSound.isLooped()) {
				javafmod.FMOD_Channel_SetMode(this.channel, (long)FMODManager.FMOD_LOOP_NORMAL);
			}

			float float1 = this.clip.distanceMin;
			if (!boolean1 && !javafmod.FMOD_Channel_IsPlaying(this.channel)) {
				return true;
			} else {
				float float2 = this.emitter.x;
				float float3 = this.emitter.y;
				float float4 = this.emitter.z;
				if (this.clip.gameSound.is3D && (float2 != 0.0F || float3 != 0.0F)) {
					this.lx = float2;
					this.ly = float3;
					this.lz = float4;
					javafmod.FMOD_Channel_Set3DAttributes(this.channel, float2, float3, float4 * 3.0F, float2 - this.lx, float3 - this.ly, float4 * 3.0F - this.lz * 3.0F);
					float float5 = Float.MAX_VALUE;
					float float6;
					for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
						IsoPlayer player = IsoPlayer.players[int1];
						if (player != null && !player.isDeaf()) {
							float6 = IsoUtils.DistanceTo(float2, float3, float4 * 3.0F, player.x, player.y, player.z * 3.0F);
							float5 = PZMath.min(float5, float6);
						}
					}

					float float7 = 2.0F;
					float float8 = float5 >= float7 ? 1.0F : 1.0F - (float7 - float5) / float7;
					javafmodJNI.FMOD_Channel_Set3DLevel(this.channel, float8);
					if (IsoPlayer.numPlayers > 1) {
						if (boolean1) {
							javafmod.FMOD_System_SetReverbDefault(0, FMODManager.FMOD_PRESET_OFF);
							javafmod.FMOD_Channel_Set3DMinMaxDistance(this.channel, this.clip.distanceMin, this.clip.distanceMax);
							javafmod.FMOD_Channel_Set3DOcclusion(this.channel, 0.0F, 0.0F);
						}

						javafmod.FMOD_Channel_SetVolume(this.channel, this.getVolume());
						if (boolean1) {
							javafmod.FMOD_Channel_SetPaused(this.channel, false);
						}

						javafmod.FMOD_Channel_SetReverbProperties(this.channel, 0, 0.0F);
						javafmod.FMOD_Channel_SetReverbProperties(this.channel, 1, 0.0F);
						javafmod.FMOD_System_SetReverbDefault(1, FMODManager.FMOD_PRESET_OFF);
						javafmod.FMOD_Channel_Set3DOcclusion(this.channel, 0.0F, 0.0F);
						return false;
					} else {
						float5 = this.clip.reverbMaxRange;
						float7 = IsoUtils.DistanceManhatten(float2, float3, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, float4, IsoPlayer.getInstance().z) / float5;
						IsoGridSquare square = IsoPlayer.getInstance().getCurrentSquare();
						if (square == null) {
							javafmod.FMOD_Channel_Set3DMinMaxDistance(this.channel, float1, this.clip.distanceMax);
							javafmod.FMOD_Channel_SetVolume(this.channel, this.getVolume());
							if (boolean1) {
								javafmod.FMOD_Channel_SetPaused(this.channel, false);
							}

							return false;
						} else {
							if (square.getRoom() == null) {
								if (!this.ambient) {
									float7 += IsoPlayer.getInstance().numNearbyBuildingsRooms / 32.0F;
								}

								if (!this.ambient) {
									float7 += 0.08F;
								}
							} else {
								float6 = (float)square.getRoom().Squares.size();
								if (!this.ambient) {
									float7 += float6 / 500.0F;
								}
							}

							if (float7 > 1.0F) {
								float7 = 1.0F;
							}

							float7 *= float7;
							float7 *= float7;
							float7 *= this.clip.reverbFactor;
							float7 *= 10.0F;
							if (IsoPlayer.getInstance().getCurrentSquare().getRoom() == null && float7 < 0.1F) {
								float7 = 0.1F;
							}

							byte byte1;
							byte byte2;
							byte byte3;
							if (!this.ambient) {
								if (square.getRoom() != null) {
									byte3 = 0;
									byte1 = 1;
									byte2 = 2;
								} else {
									byte3 = 2;
									byte1 = 0;
									byte2 = 1;
								}
							} else {
								byte3 = 2;
								byte1 = 0;
								byte2 = 1;
							}

							IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)float2, (double)float3, (double)float4);
							if (square2 != null && square2.getZone() != null && (square2.getZone().getType().equals("Forest") || square2.getZone().getType().equals("DeepForest"))) {
								byte3 = 1;
								byte1 = 0;
								byte2 = 2;
							}

							javafmod.FMOD_Channel_SetReverbProperties(this.channel, byte3, 0.0F);
							javafmod.FMOD_Channel_SetReverbProperties(this.channel, byte1, 0.0F);
							javafmod.FMOD_Channel_SetReverbProperties(this.channel, byte2, 0.0F);
							javafmod.FMOD_Channel_Set3DMinMaxDistance(this.channel, float1, this.clip.distanceMax);
							IsoGridSquare square3 = IsoWorld.instance.CurrentCell.getGridSquare((double)float2, (double)float3, (double)float4);
							float float9 = 0.0F;
							float float10 = 0.0F;
							IsoRoom room;
							if (square3 != null) {
								if (!(this.emitter.parent instanceof IsoWindow) && !(this.emitter.parent instanceof IsoDoor)) {
									if (square3.getRoom() != null) {
										room = IsoPlayer.getInstance().getCurrentSquare().getRoom();
										if (room == null) {
											float9 = 0.33F;
											float10 = 0.23F;
										} else if (room != square3.getRoom()) {
											float9 = 0.24F;
											float10 = 0.24F;
										}

										if (room != null && square3.getRoom().getBuilding() != room.getBuilding()) {
											float9 = 1.0F;
											float10 = 0.8F;
										}

										if (room != null && square3.getRoom().def.level != (int)IsoPlayer.getInstance().z) {
											float9 = 0.6F;
											float10 = 0.6F;
										}
									} else {
										room = IsoPlayer.getInstance().getCurrentSquare().getRoom();
										if (room != null) {
											float9 = 0.79F;
											float10 = 0.59F;
										}
									}
								} else {
									room = IsoPlayer.getInstance().getCurrentSquare().getRoom();
									if (room != this.emitter.parent.square.getRoom()) {
										if (room != null && room.getBuilding() == this.emitter.parent.square.getBuilding()) {
											float9 = 0.33F;
											float10 = 0.33F;
										} else {
											IsoGridSquare square4 = null;
											if (this.emitter.parent instanceof IsoDoor) {
												IsoDoor door = (IsoDoor)this.emitter.parent;
												if (door.north) {
													square4 = IsoWorld.instance.CurrentCell.getGridSquare((double)door.getX(), (double)(door.getY() - 1.0F), (double)door.getZ());
												} else {
													square4 = IsoWorld.instance.CurrentCell.getGridSquare((double)(door.getX() - 1.0F), (double)door.getY(), (double)door.getZ());
												}
											} else {
												IsoWindow window = (IsoWindow)this.emitter.parent;
												if (window.north) {
													square4 = IsoWorld.instance.CurrentCell.getGridSquare((double)window.getX(), (double)(window.getY() - 1.0F), (double)window.getZ());
												} else {
													square4 = IsoWorld.instance.CurrentCell.getGridSquare((double)(window.getX() - 1.0F), (double)window.getY(), (double)window.getZ());
												}
											}

											if (square4 != null) {
												room = IsoPlayer.getInstance().getCurrentSquare().getRoom();
												if (room != null || square4.getRoom() == null) {
													if (room != null && square4.getRoom() != null && room.building == square4.getBuilding()) {
														if (room != square4.getRoom()) {
															if (room.def.level == square4.getZ()) {
																float9 = 0.33F;
																float10 = 0.33F;
															} else {
																float9 = 0.6F;
																float10 = 0.6F;
															}
														}
													} else {
														float9 = 0.33F;
														float10 = 0.33F;
													}
												}
											}
										}
									}
								}

								if (!square3.isCouldSee(IsoPlayer.getPlayerIndex()) && square3 != IsoPlayer.getInstance().getCurrentSquare()) {
									float9 += 0.4F;
								}
							} else {
								if (IsoWorld.instance.MetaGrid.getRoomAt((int)float2, (int)float3, (int)float4) != null) {
									float9 = 1.0F;
									float10 = 1.0F;
								}

								room = IsoPlayer.getInstance().getCurrentSquare().getRoom();
								if (room != null) {
									float9 += 0.94F;
								} else {
									float9 += 0.6F;
								}
							}

							if (square3 != null && (int)IsoPlayer.getInstance().z != square3.getZ()) {
								float9 *= 1.3F;
							}

							if (float9 > 0.9F) {
								float9 = 0.9F;
							}

							if (float10 > 0.9F) {
								float10 = 0.9F;
							}

							if (this.emitter.emitterType == EmitterType.Footstep && float4 > IsoPlayer.getInstance().z && square3.getBuilding() == IsoPlayer.getInstance().getBuilding()) {
								float9 = 0.0F;
								float10 = 0.0F;
							}

							if ("HouseAlarm".equals(this.name)) {
								float9 = 0.0F;
								float10 = 0.0F;
							}

							javafmod.FMOD_Channel_Set3DOcclusion(this.channel, float9, float10);
							javafmod.FMOD_Channel_SetVolume(this.channel, this.getVolume());
							javafmod.FMOD_Channel_SetPitch(this.channel, this.pitch);
							if (boolean1) {
								javafmod.FMOD_Channel_SetPaused(this.channel, false);
							}

							this.lx = float2;
							this.ly = float3;
							this.lz = float4;
							return false;
						}
					}
				} else {
					if ((float2 != 0.0F || float3 != 0.0F) && (boolean1 || float2 != this.lx || float3 != this.ly) && this.is3D == 1) {
						javafmod.FMOD_Channel_Set3DAttributes(this.channel, float2, float3, float4 * 3.0F, 0.0F, 0.0F, 0.0F);
					}

					javafmod.FMOD_Channel_SetVolume(this.channel, this.getVolume());
					javafmod.FMOD_Channel_SetPitch(this.channel, this.pitch);
					if (boolean1) {
						javafmod.FMOD_Channel_SetPaused(this.channel, false);
					}

					return false;
				}
			}
		}

		void setParameterValue(FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION, float float1) {
		}

		void setTimelinePosition(String string) {
		}

		void triggerCue() {
		}

		boolean restart() {
			return false;
		}
	}

	private static final class ParameterValue {
		long eventInstance;
		FMOD_STUDIO_PARAMETER_DESCRIPTION parameterDescription;
		float value;
	}

	private static final class EventSound extends FMODSoundEmitter.Sound {
		long eventInstance;
		boolean bTriggeredCue = false;
		long checkTimeMS = 0L;

		EventSound(FMODSoundEmitter fMODSoundEmitter) {
			super(fMODSoundEmitter);
		}

		long getRef() {
			return this.eventInstance;
		}

		void stop() {
			if (this.eventInstance != 0L) {
				this.emitter.stopEvent(this.eventInstance, this.clip);
				javafmod.FMOD_Studio_EventInstance_Stop(this.eventInstance, false);
				javafmod.FMOD_Studio_ReleaseEventInstance(this.eventInstance);
				this.eventInstance = 0L;
			}
		}

		void set3D(boolean boolean1) {
		}

		void release() {
			this.stop();
			this.checkTimeMS = 0L;
			this.bTriggeredCue = false;
			this.emitter.releaseEventSound(this);
		}

		boolean tick(boolean boolean1) {
			IsoPlayer player = IsoPlayer.getInstance();
			if (IsoPlayer.numPlayers > 1) {
				player = null;
			}

			if (!boolean1) {
				int int1 = javafmod.FMOD_Studio_GetPlaybackState(this.eventInstance);
				if (int1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index) {
					return false;
				}

				if (int1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index) {
					javafmod.FMOD_Studio_ReleaseEventInstance(this.eventInstance);
					this.emitter.stopEvent(this.eventInstance, this.clip);
					this.eventInstance = 0L;
					return true;
				}

				if (this.bTriggeredCue && FMODSoundEmitter.CurrentTimeMS - this.checkTimeMS > 250L && int1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_SUSTAINING.index) {
					javafmodJNI.FMOD_Studio_EventInstance_KeyOff(this.eventInstance);
				}

				if (this.bTriggeredCue && this.clip.eventDescription.length > 0L && FMODSoundEmitter.CurrentTimeMS - this.checkTimeMS > 1500L) {
					long long1 = javafmodJNI.FMOD_Studio_GetTimelinePosition(this.eventInstance);
					if (long1 > this.clip.eventDescription.length + 1000L) {
						javafmod.FMOD_Studio_EventInstance_Stop(this.eventInstance, false);
					}

					this.checkTimeMS = FMODSoundEmitter.CurrentTimeMS;
				}
			}

			boolean boolean2 = Float.compare(this.emitter.x, this.setX) != 0 || Float.compare(this.emitter.y, this.setY) != 0 || Float.compare(this.emitter.z, this.setZ) != 0;
			if (boolean2) {
				this.setX = this.emitter.x;
				this.setY = this.emitter.y;
				this.setZ = this.emitter.z;
				javafmod.FMOD_Studio_EventInstance3D(this.eventInstance, this.emitter.x, this.emitter.y, this.emitter.z * 3.0F);
			}

			float float1 = this.getVolume();
			if (Float.compare(float1, this.setVolume) != 0) {
				this.setVolume = float1;
				javafmod.FMOD_Studio_EventInstance_SetVolume(this.eventInstance, float1);
			}

			if (boolean1) {
				this.emitter.startEvent(this.eventInstance, this.clip);
				javafmod.FMOD_Studio_StartEvent(this.eventInstance);
			} else {
				this.emitter.updateEvent(this.eventInstance, this.clip);
			}

			return false;
		}

		void setParameterValue(FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION, float float1) {
			if (this.eventInstance != 0L) {
				javafmod.FMOD_Studio_EventInstance_SetParameterByID(this.eventInstance, fMOD_STUDIO_PARAMETER_DESCRIPTION.id, float1, false);
			}
		}

		void setTimelinePosition(String string) {
			if (this.eventInstance != 0L && this.clip != null && this.clip.event != null) {
				SoundTimelineScript soundTimelineScript = ScriptManager.instance.getSoundTimeline(this.clip.event);
				if (soundTimelineScript != null) {
					int int1 = soundTimelineScript.getPosition(string);
					if (int1 != -1) {
						javafmodJNI.FMOD_Studio_EventInstance_SetTimelinePosition(this.eventInstance, int1);
					}
				}
			}
		}

		void triggerCue() {
			if (this.eventInstance != 0L) {
				if (this.clip.hasSustainPoints()) {
					javafmodJNI.FMOD_Studio_EventInstance_KeyOff(this.eventInstance);
					this.bTriggeredCue = true;
					this.checkTimeMS = FMODSoundEmitter.CurrentTimeMS;
				}
			}
		}

		boolean restart() {
			if (this.eventInstance == 0L) {
				return false;
			} else {
				javafmodJNI.FMOD_Studio_StartEvent(this.eventInstance);
				return true;
			}
		}
	}
}
