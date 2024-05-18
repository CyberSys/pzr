package zombie;

import fmod.javafmod;
import fmod.javafmodJNI;
import fmod.fmod.FMODFootstep;
import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundBank;
import fmod.fmod.FMODVoice;
import fmod.fmod.FMOD_STUDIO_PLAYBACK_STATE;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import zombie.audio.BaseSoundBank;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.IsoPlayer;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.GameSoundScript;


public final class GameSounds {
	protected static final HashMap soundByName = new HashMap();
	protected static final ArrayList sounds = new ArrayList();
	public static final int VERSION = 1;
	private static final GameSounds.BankPreviewSound previewBank = new GameSounds.BankPreviewSound();
	private static final GameSounds.FilePreviewSound previewFile = new GameSounds.FilePreviewSound();
	private static GameSounds.IPreviewSound previewSound;
	public static boolean soundIsPaused = false;

	public static void addSound(GameSound gameSound) {
		assert !sounds.contains(gameSound);
		int int1 = sounds.size();
		if (soundByName.containsKey(gameSound.getName())) {
			for (int1 = 0; int1 < sounds.size() && !((GameSound)sounds.get(int1)).getName().equals(gameSound.getName()); ++int1) {
			}

			sounds.remove(int1);
		}

		sounds.add(int1, gameSound);
		soundByName.put(gameSound.getName(), gameSound);
	}

	public static boolean isKnownSound(String string) {
		return soundByName.containsKey(string);
	}

	public static GameSound getSound(String string) {
		return getOrCreateSound(string);
	}

	public static GameSound getOrCreateSound(String string) {
		GameSound gameSound = (GameSound)soundByName.get(string);
		if (gameSound == null) {
			DebugLog.log("WARNING: no GameSound called \"" + string + "\", adding a new one");
			gameSound = new GameSound();
			gameSound.name = string;
			gameSound.category = "AUTO";
			GameSoundClip gameSoundClip = new GameSoundClip(gameSound);
			gameSound.clips.add(gameSoundClip);
			sounds.add(gameSound);
			soundByName.put(string.replace(".wav", "").replace(".ogg", ""), gameSound);
			if (BaseSoundBank.instance instanceof FMODSoundBank) {
				long long1 = javafmodJNI.FMOD_Studio_GetEvent(string);
				if (long1 > 0L) {
					gameSoundClip.event = string;
				} else {
					String string2 = null;
					if (ZomboidFileSystem.instance.getAbsolutePath("media/sound/" + string + ".ogg") != null) {
						string2 = "media/sound/" + string + ".ogg";
					} else if (ZomboidFileSystem.instance.getAbsolutePath("media/sound/" + string + ".wav") != null) {
						string2 = "media/sound/" + string + ".wav";
					}

					if (string2 != null) {
						long long2 = FMODManager.instance.loadSound(string2);
						if (long2 != 0L) {
							gameSoundClip.file = string2;
						}
					}
				}

				if (gameSoundClip.event == null && gameSoundClip.file == null) {
					DebugLog.log("WARNING: couldn\'t find an FMOD event or .ogg or .wav file for sound \"" + string + "\"");
				}
			}
		}

		return gameSound;
	}

	private static void loadNonBankSounds() {
		if (BaseSoundBank.instance instanceof FMODSoundBank) {
			Iterator iterator = sounds.iterator();
			while (iterator.hasNext()) {
				GameSound gameSound = (GameSound)iterator.next();
				Iterator iterator2 = gameSound.clips.iterator();
				while (iterator2.hasNext()) {
					GameSoundClip gameSoundClip = (GameSoundClip)iterator2.next();
					if (gameSoundClip.getFile() != null && gameSoundClip.getFile().isEmpty()) {
					}
				}
			}
		}
	}

	public static void ScriptsLoaded() {
		ArrayList arrayList = ScriptManager.instance.getAllGameSounds();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			GameSoundScript gameSoundScript = (GameSoundScript)arrayList.get(int1);
			if (!gameSoundScript.gameSound.clips.isEmpty()) {
				addSound(gameSoundScript.gameSound);
			}
		}

		arrayList.clear();
		loadNonBankSounds();
		loadINI();
		if (Core.bDebug && BaseSoundBank.instance instanceof FMODSoundBank) {
			HashSet hashSet = new HashSet();
			Iterator iterator = sounds.iterator();
			while (iterator.hasNext()) {
				GameSound gameSound = (GameSound)iterator.next();
				Iterator iterator2 = gameSound.clips.iterator();
				while (iterator2.hasNext()) {
					GameSoundClip gameSoundClip = (GameSoundClip)iterator2.next();
					if (gameSoundClip.getEvent() != null && !gameSoundClip.getEvent().isEmpty()) {
						hashSet.add(gameSoundClip.getEvent());
					}
				}
			}

			FMODSoundBank fMODSoundBank = (FMODSoundBank)BaseSoundBank.instance;
			Iterator iterator3 = fMODSoundBank.footstepMap.values().iterator();
			while (iterator3.hasNext()) {
				FMODFootstep fMODFootstep = (FMODFootstep)iterator3.next();
				hashSet.add(fMODFootstep.wood);
				hashSet.add(fMODFootstep.concrete);
				hashSet.add(fMODFootstep.grass);
				hashSet.add(fMODFootstep.upstairs);
				hashSet.add(fMODFootstep.woodCreak);
			}

			iterator3 = fMODSoundBank.voiceMap.values().iterator();
			while (iterator3.hasNext()) {
				FMODVoice fMODVoice = (FMODVoice)iterator3.next();
				hashSet.add(fMODVoice.sound);
			}

			long[] longArray = new long[32];
			long[] longArray2 = new long[512];
			int int2 = javafmodJNI.FMOD_Studio_System_GetBankList(longArray);
			for (int int3 = 0; int3 < int2; ++int3) {
				int int4 = javafmodJNI.FMOD_Studio_Bank_GetEventList(longArray[int3], longArray2);
				for (int int5 = 0; int5 < int4; ++int5) {
					try {
						String string = javafmodJNI.FMOD_Studio_EventDescription_GetPath(longArray2[int5]);
						string = string.replace("event:/", "");
						if (!hashSet.contains(string)) {
							DebugLog.log("WARNING: FMOD event \"" + string + "\" not used by any GameSound");
						}
					} catch (Exception exception) {
						DebugLog.log("ERROR: FMOD cannot get path for " + longArray2[int5] + " event");
					}
				}
			}
		}
	}

	public static void ReloadFile(String string) {
		try {
			ScriptManager.instance.LoadFile(string, true);
			ArrayList arrayList = ScriptManager.instance.getAllGameSounds();
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				GameSoundScript gameSoundScript = (GameSoundScript)arrayList.get(int1);
				if (!sounds.contains(gameSoundScript.gameSound) && !gameSoundScript.gameSound.clips.isEmpty()) {
					addSound(gameSoundScript.gameSound);
				}
			}
		} catch (Throwable throwable) {
			ExceptionLogger.logException(throwable);
		}
	}

	public static ArrayList getCategories() {
		HashSet hashSet = new HashSet();
		Iterator iterator = sounds.iterator();
		while (iterator.hasNext()) {
			GameSound gameSound = (GameSound)iterator.next();
			hashSet.add(gameSound.getCategory());
		}

		ArrayList arrayList = new ArrayList(hashSet);
		Collections.sort(arrayList);
		return arrayList;
	}

	public static ArrayList getSoundsInCategory(String string) {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = sounds.iterator();
		while (iterator.hasNext()) {
			GameSound gameSound = (GameSound)iterator.next();
			if (gameSound.getCategory().equals(string)) {
				arrayList.add(gameSound);
			}
		}

		return arrayList;
	}

	public static void loadINI() {
		String string = GameWindow.getCacheDir() + File.separator + "sounds.ini";
		ConfigFile configFile = new ConfigFile();
		if (configFile.read(string)) {
			if (configFile.getVersion() <= 1) {
				Iterator iterator = configFile.getOptions().iterator();
				while (iterator.hasNext()) {
					ConfigOption configOption = (ConfigOption)iterator.next();
					GameSound gameSound = (GameSound)soundByName.get(configOption.getName());
					if (gameSound != null) {
						gameSound.userVolume = Float.parseFloat(configOption.getValueAsString());
					}
				}
			}
		}
	}

	public static void saveINI() {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = sounds.iterator();
		while (iterator.hasNext()) {
			GameSound gameSound = (GameSound)iterator.next();
			DoubleConfigOption doubleConfigOption = new DoubleConfigOption(gameSound.getName(), 0.0, 2.0, 0.0);
			doubleConfigOption.setValue((double)gameSound.userVolume);
			arrayList.add(doubleConfigOption);
		}

		String string = GameWindow.getCacheDir() + File.separator + "sounds.ini";
		ConfigFile configFile = new ConfigFile();
		if (configFile.write(string, 1, arrayList)) {
			arrayList.clear();
		}
	}

	public static void previewSound(String string) {
		if (!Core.SoundDisabled) {
			if (isKnownSound(string)) {
				GameSound gameSound = getSound(string);
				if (gameSound == null) {
					DebugLog.log("no such GameSound " + string);
				} else {
					GameSoundClip gameSoundClip = gameSound.getRandomClip();
					if (gameSoundClip == null) {
						DebugLog.log("GameSound.clips is empty");
					} else {
						if (soundIsPaused) {
							if (!GameClient.bClient) {
								long long1 = javafmod.FMOD_System_GetMasterChannelGroup();
								javafmod.FMOD_ChannelGroup_SetVolume(long1, 1.0F);
							}

							soundIsPaused = false;
						}

						if (previewSound != null) {
							previewSound.stop();
						}

						if (gameSoundClip.getEvent() != null) {
							if (previewBank.play(gameSoundClip)) {
								previewSound = previewBank;
							}
						} else if (gameSoundClip.getFile() != null && previewFile.play(gameSoundClip)) {
							previewSound = previewFile;
						}
					}
				}
			}
		}
	}

	public static void stopPreview() {
		if (previewSound != null) {
			previewSound.stop();
			previewSound = null;
		}
	}

	public static boolean isPreviewPlaying() {
		if (previewSound == null) {
			return false;
		} else if (previewSound.update()) {
			previewSound = null;
			return false;
		} else {
			return previewSound.isPlaying();
		}
	}

	public static void fix3DListenerPosition(boolean boolean1) {
		if (!Core.SoundDisabled) {
			if (boolean1) {
				javafmod.FMOD_Studio_Listener3D(0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F);
			} else {
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoPlayer player = IsoPlayer.players[int1];
					if (player != null && !player.HasTrait("Deaf")) {
						javafmod.FMOD_Studio_Listener3D(int1, player.x, player.y, player.z * 3.0F, 0.0F, 0.0F, 0.0F, -1.0F / (float)Math.sqrt(2.0), -1.0F / (float)Math.sqrt(2.0), 0.0F, 0.0F, 0.0F, 1.0F);
					}
				}
			}
		}
	}

	public static void Reset() {
		sounds.clear();
		soundByName.clear();
		if (previewSound != null) {
			previewSound.stop();
			previewSound = null;
		}
	}

	private static final class FilePreviewSound implements GameSounds.IPreviewSound {
		long channel;
		GameSoundClip clip;
		float effectiveGain;

		private FilePreviewSound() {
		}

		public boolean play(GameSoundClip gameSoundClip) {
			GameSound gameSound = gameSoundClip.gameSound;
			long long1 = FMODManager.instance.loadSound(gameSoundClip.getFile(), gameSound.isLooped());
			if (long1 == 0L) {
				return false;
			} else {
				this.channel = javafmod.FMOD_System_PlaySound(long1, true);
				this.clip = gameSoundClip;
				this.effectiveGain = gameSoundClip.getEffectiveVolumeInMenu();
				javafmod.FMOD_Channel_SetVolume(this.channel, this.effectiveGain);
				javafmod.FMOD_Channel_SetPitch(this.channel, gameSoundClip.pitch);
				if (gameSound.isLooped()) {
					javafmod.FMOD_Channel_SetMode(this.channel, (long)FMODManager.FMOD_LOOP_NORMAL);
				}

				javafmod.FMOD_Channel_SetPaused(this.channel, false);
				return true;
			}
		}

		public boolean isPlaying() {
			return this.channel == 0L ? false : javafmod.FMOD_Channel_IsPlaying(this.channel);
		}

		public boolean update() {
			if (this.channel == 0L) {
				return false;
			} else if (!javafmod.FMOD_Channel_IsPlaying(this.channel)) {
				this.channel = 0L;
				this.clip = null;
				return true;
			} else {
				float float1 = this.clip.getEffectiveVolumeInMenu();
				if (this.effectiveGain != float1) {
					this.effectiveGain = float1;
					javafmod.FMOD_Channel_SetVolume(this.channel, this.effectiveGain);
				}

				return false;
			}
		}

		public void stop() {
			if (this.channel != 0L) {
				javafmod.FMOD_Channel_Stop(this.channel);
				this.channel = 0L;
				this.clip = null;
			}
		}

		FilePreviewSound(Object object) {
			this();
		}
	}

	private static final class BankPreviewSound implements GameSounds.IPreviewSound {
		long instance;
		GameSoundClip clip;
		float effectiveGain;

		private BankPreviewSound() {
		}

		public boolean play(GameSoundClip gameSoundClip) {
			long long1 = javafmod.FMOD_Studio_System_GetEvent("event:/" + gameSoundClip.getEvent());
			if (long1 < 0L) {
				DebugLog.log("failed to get event " + gameSoundClip.getEvent() + ": error=" + long1);
				return false;
			} else {
				this.instance = javafmod.FMOD_Studio_System_CreateEventInstance(long1);
				if (this.instance < 0L) {
					DebugLog.log("failed to create EventInstance: error=" + this.instance);
					this.instance = 0L;
					return false;
				} else {
					this.clip = gameSoundClip;
					this.effectiveGain = gameSoundClip.getEffectiveVolumeInMenu();
					javafmod.FMOD_Studio_SetVolume(this.instance, this.effectiveGain);
					javafmod.FMOD_Studio_SetParameter(this.instance, "Occlusion", 0.0F);
					javafmod.FMOD_Studio_StartEvent(this.instance);
					if (gameSoundClip.gameSound.master == GameSound.MasterVolume.Music) {
						javafmod.FMOD_Studio_SetParameter(this.instance, "Volume", 10.0F);
					}

					return true;
				}
			}
		}

		public boolean isPlaying() {
			if (this.instance == 0L) {
				return false;
			} else {
				int int1 = javafmod.FMOD_Studio_GetPlaybackState(this.instance);
				if (int1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index) {
					return true;
				} else {
					return int1 != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index;
				}
			}
		}

		public boolean update() {
			if (this.instance == 0L) {
				return false;
			} else {
				int int1 = javafmod.FMOD_Studio_GetPlaybackState(this.instance);
				if (int1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index) {
					return false;
				} else if (int1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index) {
					javafmod.FMOD_Studio_ReleaseEventInstance(this.instance);
					this.instance = 0L;
					this.clip = null;
					return true;
				} else {
					float float1 = this.clip.getEffectiveVolumeInMenu();
					if (this.effectiveGain != float1) {
						this.effectiveGain = float1;
						javafmod.FMOD_Studio_SetVolume(this.instance, this.effectiveGain);
					}

					return false;
				}
			}
		}

		public void stop() {
			if (this.instance != 0L) {
				javafmod.FMOD_Studio_StopInstance(this.instance);
				javafmod.FMOD_Studio_ReleaseEventInstance(this.instance);
				this.instance = 0L;
				this.clip = null;
			}
		}

		BankPreviewSound(Object object) {
			this();
		}
	}

	private interface IPreviewSound {

		boolean play(GameSoundClip gameSoundClip);

		boolean isPlaying();

		boolean update();

		void stop();
	}
}
