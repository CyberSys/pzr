package zombie;

import fmod.javafmod;
import fmod.javafmodJNI;
import fmod.fmod.Audio;
import fmod.fmod.FMODAudio;
import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import fmod.fmod.FMOD_STUDIO_PLAYBACK_STATE;
import fmod.fmod.IFMODParameterUpdater;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.FMODParameter;
import zombie.audio.FMODParameterList;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.audio.parameters.ParameterMusicLibrary;
import zombie.audio.parameters.ParameterMusicState;
import zombie.audio.parameters.ParameterMusicWakeState;
import zombie.audio.parameters.ParameterMusicZombiesTargeting;
import zombie.audio.parameters.ParameterMusicZombiesVisible;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.ScriptModule;
import zombie.util.StringUtils;


public final class SoundManager extends BaseSoundManager implements IFMODParameterUpdater {
	public float SoundVolume = 0.8F;
	public float MusicVolume = 0.36F;
	public float AmbientVolume = 0.8F;
	public float VehicleEngineVolume = 0.5F;
	private final ParameterMusicLibrary parameterMusicLibrary = new ParameterMusicLibrary();
	private final ParameterMusicState parameterMusicState = new ParameterMusicState();
	private final ParameterMusicWakeState parameterMusicWakeState = new ParameterMusicWakeState();
	private final ParameterMusicZombiesTargeting parameterMusicZombiesTargeting = new ParameterMusicZombiesTargeting();
	private final ParameterMusicZombiesVisible parameterMusicZombiesVisible = new ParameterMusicZombiesVisible();
	private final FMODParameterList fmodParameters = new FMODParameterList();
	private boolean initialized = false;
	private long inGameGroupBus = 0L;
	private long musicGroupBus = 0L;
	private FMODSoundEmitter musicEmitter = null;
	private long musicCombinedEvent = 0L;
	private FMODSoundEmitter uiEmitter = null;
	private final SoundManager.Music music = new SoundManager.Music();
	public ArrayList ambientPieces = new ArrayList();
	private boolean muted = false;
	private long[] bankList = new long[32];
	private long[] eventDescList = new long[256];
	private long[] eventInstList = new long[256];
	private long[] pausedEventInstances = new long[128];
	private float[] pausedEventVolumes = new float[128];
	private int pausedEventCount;
	private final HashSet emitters = new HashSet();
	private static ArrayList ambientSoundEffects = new ArrayList();
	public static BaseSoundManager instance;
	private String currentMusicName;
	private String currentMusicLibrary;

	public FMODParameterList getFMODParameters() {
		return this.fmodParameters;
	}

	public void startEvent(long long1, GameSoundClip gameSoundClip, BitSet bitSet) {
		FMODParameterList fMODParameterList = this.getFMODParameters();
		ArrayList arrayList = gameSoundClip.eventDescription.parameters;
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION = (FMOD_STUDIO_PARAMETER_DESCRIPTION)arrayList.get(int1);
			if (!bitSet.get(fMOD_STUDIO_PARAMETER_DESCRIPTION.globalIndex)) {
				FMODParameter fMODParameter = fMODParameterList.get(fMOD_STUDIO_PARAMETER_DESCRIPTION);
				if (fMODParameter != null) {
					fMODParameter.startEventInstance(long1);
				}
			}
		}
	}

	public void updateEvent(long long1, GameSoundClip gameSoundClip) {
	}

	public void stopEvent(long long1, GameSoundClip gameSoundClip, BitSet bitSet) {
		FMODParameterList fMODParameterList = this.getFMODParameters();
		ArrayList arrayList = gameSoundClip.eventDescription.parameters;
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION = (FMOD_STUDIO_PARAMETER_DESCRIPTION)arrayList.get(int1);
			if (!bitSet.get(fMOD_STUDIO_PARAMETER_DESCRIPTION.globalIndex)) {
				FMODParameter fMODParameter = fMODParameterList.get(fMOD_STUDIO_PARAMETER_DESCRIPTION);
				if (fMODParameter != null) {
					fMODParameter.stopEventInstance(long1);
				}
			}
		}
	}

	public boolean isRemastered() {
		int int1 = Core.getInstance().getOptionMusicLibrary();
		return int1 == 1 || int1 == 3 && Rand.Next(2) == 0;
	}

	public void BlendVolume(Audio audio, float float1) {
	}

	public void BlendVolume(Audio audio, float float1, float float2) {
	}

	public Audio BlendThenStart(Audio audio, float float1, String string) {
		return null;
	}

	public void FadeOutMusic(String string, int int1) {
	}

	public void PlayAsMusic(String string, Audio audio, float float1, boolean boolean1) {
	}

	public long playUISound(String string) {
		GameSound gameSound = GameSounds.getSound(string);
		if (gameSound != null && !gameSound.clips.isEmpty()) {
			GameSoundClip gameSoundClip = gameSound.getRandomClip();
			long long1 = this.uiEmitter.playClip(gameSoundClip, (IsoObject)null);
			this.uiEmitter.tick();
			javafmod.FMOD_System_Update();
			return long1;
		} else {
			return 0L;
		}
	}

	public boolean isPlayingUISound(String string) {
		return this.uiEmitter.isPlaying(string);
	}

	public boolean isPlayingUISound(long long1) {
		return this.uiEmitter.isPlaying(long1);
	}

	public void stopUISound(long long1) {
		this.uiEmitter.stopSound(long1);
	}

	public boolean IsMusicPlaying() {
		return false;
	}

	public boolean isPlayingMusic() {
		return this.music.isPlaying();
	}

	public ArrayList getAmbientPieces() {
		return this.ambientPieces;
	}

	private void gatherInGameEventInstances() {
		this.pausedEventCount = 0;
		int int1 = javafmodJNI.FMOD_Studio_System_GetBankCount();
		if (this.bankList.length < int1) {
			this.bankList = new long[int1];
		}

		int1 = javafmodJNI.FMOD_Studio_System_GetBankList(this.bankList);
		for (int int2 = 0; int2 < int1; ++int2) {
			int int3 = javafmodJNI.FMOD_Studio_Bank_GetEventCount(this.bankList[int2]);
			if (this.eventDescList.length < int3) {
				this.eventDescList = new long[int3];
			}

			int3 = javafmodJNI.FMOD_Studio_Bank_GetEventList(this.bankList[int2], this.eventDescList);
			for (int int4 = 0; int4 < int3; ++int4) {
				int int5 = javafmodJNI.FMOD_Studio_EventDescription_GetInstanceCount(this.eventDescList[int4]);
				if (this.eventInstList.length < int5) {
					this.eventInstList = new long[int5];
				}

				int5 = javafmodJNI.FMOD_Studio_EventDescription_GetInstanceList(this.eventDescList[int4], this.eventInstList);
				for (int int6 = 0; int6 < int5; ++int6) {
					int int7 = javafmod.FMOD_Studio_GetPlaybackState(this.eventInstList[int6]);
					if (int7 != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index) {
						boolean boolean1 = javafmodJNI.FMOD_Studio_EventInstance_GetPaused(this.eventInstList[int6]);
						if (!boolean1) {
							if (this.pausedEventInstances.length < this.pausedEventCount + 1) {
								this.pausedEventInstances = Arrays.copyOf(this.pausedEventInstances, this.pausedEventCount + 128);
								this.pausedEventVolumes = Arrays.copyOf(this.pausedEventVolumes, this.pausedEventInstances.length);
							}

							this.pausedEventInstances[this.pausedEventCount] = this.eventInstList[int6];
							this.pausedEventVolumes[this.pausedEventCount] = javafmodJNI.FMOD_Studio_EventInstance_GetVolume(this.eventInstList[int6]);
							++this.pausedEventCount;
						}
					}
				}
			}
		}
	}

	public void pauseSoundAndMusic() {
		boolean boolean1 = true;
		if (GameClient.bClient) {
			this.muted = true;
			if (boolean1) {
				javafmod.FMOD_Studio_Bus_SetMute(this.inGameGroupBus, true);
				javafmod.FMOD_Studio_Bus_SetMute(this.musicGroupBus, true);
			} else {
				this.setSoundVolume(0.0F);
				this.setMusicVolume(0.0F);
				this.setAmbientVolume(0.0F);
				this.setVehicleEngineVolume(0.0F);
			}

			GameSounds.soundIsPaused = true;
		} else if (boolean1) {
			javafmod.FMOD_Studio_Bus_SetPaused(this.inGameGroupBus, true);
			javafmod.FMOD_Studio_Bus_SetPaused(this.musicGroupBus, true);
			javafmod.FMOD_Channel_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, true);
			GameSounds.soundIsPaused = true;
		} else {
			long long1 = javafmod.FMOD_System_GetMasterChannelGroup();
			javafmod.FMOD_ChannelGroup_SetPaused(long1, true);
			javafmod.FMOD_ChannelGroup_SetVolume(long1, 0.0F);
			javafmodJNI.FMOD_Studio_System_FlushCommands();
			this.gatherInGameEventInstances();
			for (int int1 = 0; int1 < this.pausedEventCount; ++int1) {
				javafmodJNI.FMOD_Studio_EventInstance_SetPaused(this.pausedEventInstances[int1], true);
			}

			javafmod.FMOD_Channel_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, true);
			javafmod.FMOD_ChannelGroup_SetPaused(long1, false);
			javafmodJNI.FMOD_Studio_System_FlushCommands();
			javafmod.FMOD_ChannelGroup_SetVolume(long1, 1.0F);
			GameSounds.soundIsPaused = true;
		}
	}

	public void resumeSoundAndMusic() {
		boolean boolean1 = true;
		if (this.muted) {
			this.muted = false;
			if (boolean1) {
				javafmod.FMOD_Studio_Bus_SetMute(this.inGameGroupBus, false);
				javafmod.FMOD_Studio_Bus_SetMute(this.musicGroupBus, false);
				javafmod.FMOD_ChannelGroup_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, false);
			} else {
				this.setSoundVolume((float)Core.getInstance().getOptionSoundVolume() / 10.0F);
				this.setMusicVolume((float)Core.getInstance().getOptionMusicVolume() / 10.0F);
				this.setAmbientVolume((float)Core.getInstance().getOptionAmbientVolume() / 10.0F);
				this.setVehicleEngineVolume((float)Core.getInstance().getOptionVehicleEngineVolume() / 10.0F);
			}

			GameSounds.soundIsPaused = false;
		} else if (boolean1) {
			javafmod.FMOD_Studio_Bus_SetPaused(this.inGameGroupBus, false);
			javafmod.FMOD_Studio_Bus_SetPaused(this.musicGroupBus, false);
			javafmod.FMOD_ChannelGroup_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, false);
			GameSounds.soundIsPaused = false;
		} else {
			long long1 = javafmod.FMOD_System_GetMasterChannelGroup();
			javafmod.FMOD_ChannelGroup_SetPaused(long1, true);
			javafmodJNI.FMOD_Studio_System_FlushCommands();
			for (int int1 = 0; int1 < this.pausedEventCount; ++int1) {
				try {
					javafmodJNI.FMOD_Studio_EventInstance_SetPaused(this.pausedEventInstances[int1], false);
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
			}

			this.pausedEventCount = 0;
			javafmod.FMOD_ChannelGroup_SetPaused(long1, false);
			javafmod.FMOD_ChannelGroup_SetVolume(long1, 1.0F);
			javafmod.FMOD_ChannelGroup_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, false);
			GameSounds.soundIsPaused = false;
		}
	}

	private void debugScriptSound(Item item, String string) {
		if (string != null && !string.isEmpty()) {
			if (!GameSounds.isKnownSound(string)) {
				DebugLog.General.warn("no such sound \"" + string + "\" in item " + item.getFullName());
			}
		}
	}

	public void debugScriptSounds() {
		if (Core.bDebug) {
			Iterator iterator = ScriptManager.instance.ModuleMap.values().iterator();
			while (iterator.hasNext()) {
				ScriptModule scriptModule = (ScriptModule)iterator.next();
				Iterator iterator2 = scriptModule.ItemMap.values().iterator();
				while (iterator2.hasNext()) {
					Item item = (Item)iterator2.next();
					this.debugScriptSound(item, item.getBreakSound());
					this.debugScriptSound(item, item.getBulletOutSound());
					this.debugScriptSound(item, item.getCloseSound());
					this.debugScriptSound(item, item.getCustomEatSound());
					this.debugScriptSound(item, item.getDoorHitSound());
					this.debugScriptSound(item, item.getCountDownSound());
					this.debugScriptSound(item, item.getExplosionSound());
					this.debugScriptSound(item, item.getImpactSound());
					this.debugScriptSound(item, item.getOpenSound());
					this.debugScriptSound(item, item.getPutInSound());
					this.debugScriptSound(item, item.getShellFallSound());
					this.debugScriptSound(item, item.getSwingSound());
				}
			}
		}
	}

	public void registerEmitter(BaseSoundEmitter baseSoundEmitter) {
		this.emitters.add(baseSoundEmitter);
	}

	public void unregisterEmitter(BaseSoundEmitter baseSoundEmitter) {
		this.emitters.remove(baseSoundEmitter);
	}

	public boolean isListenerInRange(float float1, float float2, float float3) {
		if (GameServer.bServer) {
			return false;
		} else {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && !player.Traits.Deaf.isSet() && IsoUtils.DistanceToSquared(player.x, player.y, float1, float2) < float3 * float3) {
					return true;
				}
			}

			return false;
		}
	}

	public void playNightAmbient(String string) {
		DebugLog.log("playNightAmbient: " + string);
		for (int int1 = 0; int1 < ambientSoundEffects.size(); ++int1) {
			SoundManager.AmbientSoundEffect ambientSoundEffect = (SoundManager.AmbientSoundEffect)ambientSoundEffects.get(int1);
			if (ambientSoundEffect.getName().equals(string)) {
				ambientSoundEffect.setVolume((float)Rand.Next(700, 1500) / 1000.0F);
				ambientSoundEffect.start();
				this.ambientPieces.add(ambientSoundEffect);
				return;
			}
		}

		SoundManager.AmbientSoundEffect ambientSoundEffect2 = new SoundManager.AmbientSoundEffect(string);
		ambientSoundEffect2.setVolume((float)Rand.Next(700, 1500) / 1000.0F);
		ambientSoundEffect2.setName(string);
		ambientSoundEffect2.start();
		this.ambientPieces.add(ambientSoundEffect2);
		ambientSoundEffects.add(ambientSoundEffect2);
	}

	public void playMusic(String string) {
		this.DoMusic(string, false);
	}

	public void playAmbient(String string) {
	}

	public void playMusicNonTriggered(String string, float float1) {
	}

	public void stopMusic(String string) {
		if (this.isPlayingMusic()) {
			if (StringUtils.isNullOrWhitespace(string) || string.equalsIgnoreCase(this.getCurrentMusicName())) {
				this.StopMusic();
			}
		}
	}

	public void CheckDoMusic() {
	}

	public float getMusicPosition() {
		return this.isPlayingMusic() ? this.music.getPosition() : 0.0F;
	}

	public void DoMusic(String string, boolean boolean1) {
		if (this.AllowMusic && Core.getInstance().getOptionMusicVolume() != 0) {
			if (this.isPlayingMusic()) {
				this.StopMusic();
			}

			int int1 = Core.getInstance().getOptionMusicLibrary();
			boolean boolean2 = int1 == 1;
			GameSound gameSound = GameSounds.getSound(string);
			GameSoundClip gameSoundClip = null;
			if (gameSound != null && !gameSound.clips.isEmpty()) {
				gameSoundClip = gameSound.getRandomClip();
			}

			long long1;
			if (gameSoundClip != null && gameSoundClip.getEvent() != null) {
				if (gameSoundClip.eventDescription != null) {
					long1 = gameSoundClip.eventDescription.address;
					javafmod.FMOD_Studio_LoadEventSampleData(long1);
					this.music.instance = javafmod.FMOD_Studio_System_CreateEventInstance(long1);
					this.music.clip = gameSoundClip;
					this.music.effectiveVolume = gameSoundClip.getEffectiveVolume();
					javafmod.FMOD_Studio_EventInstance_SetParameterByName(this.music.instance, "Volume", 10.0F);
					javafmod.FMOD_Studio_EventInstance_SetVolume(this.music.instance, this.music.effectiveVolume);
					javafmod.FMOD_Studio_StartEvent(this.music.instance);
				}
			} else if (gameSoundClip != null && gameSoundClip.getFile() != null) {
				long1 = FMODManager.instance.loadSound(gameSoundClip.getFile());
				if (long1 > 0L) {
					this.music.channel = javafmod.FMOD_System_PlaySound(long1, true);
					this.music.clip = gameSoundClip;
					this.music.effectiveVolume = gameSoundClip.getEffectiveVolume();
					javafmod.FMOD_Channel_SetVolume(this.music.channel, this.music.effectiveVolume);
					javafmod.FMOD_Channel_SetPitch(this.music.channel, gameSoundClip.pitch);
					javafmod.FMOD_Channel_SetPaused(this.music.channel, false);
				}
			}

			this.currentMusicName = string;
			this.currentMusicLibrary = boolean2 ? "official" : "earlyaccess";
		}
	}

	public void PlayAsMusic(String string, Audio audio, boolean boolean1, float float1) {
	}

	public void setMusicState(String string) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -2101240105: 
			if (string.equals("InGame")) {
				byte1 = 2;
			}

			break;
		
		case -1461787563: 
			if (string.equals("PauseMenu")) {
				byte1 = 3;
			}

			break;
		
		case 55996120: 
			if (string.equals("MainMenu")) {
				byte1 = 0;
			}

			break;
		
		case 257920894: 
			if (string.equals("Tutorial")) {
				byte1 = 4;
			}

			break;
		
		case 2001303836: 
			if (string.equals("Loading")) {
				byte1 = 1;
			}

		
		}
		switch (byte1) {
		case 0: 
			this.parameterMusicState.setState(ParameterMusicState.State.MainMenu);
			break;
		
		case 1: 
			this.parameterMusicState.setState(ParameterMusicState.State.Loading);
			break;
		
		case 2: 
			this.parameterMusicState.setState(ParameterMusicState.State.InGame);
			break;
		
		case 3: 
			this.parameterMusicState.setState(ParameterMusicState.State.PauseMenu);
			break;
		
		case 4: 
			this.parameterMusicState.setState(ParameterMusicState.State.Tutorial);
			break;
		
		default: 
			DebugLog.General.warn("unknown MusicState \"%s\"", string);
		
		}
	}

	public void setMusicWakeState(IsoPlayer player, String string) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -1321219637: 
			if (string.equals("Sleeping")) {
				byte1 = 1;
			}

			break;
		
		case 63670629: 
			if (string.equals("Awake")) {
				byte1 = 0;
			}

			break;
		
		case 138562507: 
			if (string.equals("WakeNormal")) {
				byte1 = 2;
			}

			break;
		
		case 1582036347: 
			if (string.equals("WakeNightmare")) {
				byte1 = 3;
			}

			break;
		
		case 2055642281: 
			if (string.equals("WakeZombies")) {
				byte1 = 4;
			}

		
		}
		switch (byte1) {
		case 0: 
			this.parameterMusicWakeState.setState(player, ParameterMusicWakeState.State.Awake);
			break;
		
		case 1: 
			this.parameterMusicWakeState.setState(player, ParameterMusicWakeState.State.Sleeping);
			break;
		
		case 2: 
			this.parameterMusicWakeState.setState(player, ParameterMusicWakeState.State.WakeNormal);
			break;
		
		case 3: 
			this.parameterMusicWakeState.setState(player, ParameterMusicWakeState.State.WakeNightmare);
			break;
		
		case 4: 
			this.parameterMusicWakeState.setState(player, ParameterMusicWakeState.State.WakeZombies);
			break;
		
		default: 
			DebugLog.General.warn("unknown MusicWakeState \"%s\"", string);
		
		}
	}

	public Audio PlayMusic(String string, String string2, boolean boolean1, float float1) {
		return null;
	}

	public Audio PlaySound(String string, boolean boolean1, float float1, float float2) {
		return null;
	}

	public Audio PlaySound(String string, boolean boolean1, float float1) {
		if (GameServer.bServer) {
			return null;
		} else if (IsoWorld.instance == null) {
			return null;
		} else {
			BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter();
			baseSoundEmitter.setPos(0.0F, 0.0F, 0.0F);
			long long1 = baseSoundEmitter.playSound(string);
			return long1 != 0L ? new FMODAudio(baseSoundEmitter) : null;
		}
	}

	public Audio PlaySoundEvenSilent(String string, boolean boolean1, float float1) {
		return null;
	}

	public Audio PlayJukeboxSound(String string, boolean boolean1, float float1) {
		return null;
	}

	public Audio PlaySoundWav(String string, boolean boolean1, float float1, float float2) {
		return null;
	}

	public Audio PlaySoundWav(String string, boolean boolean1, float float1) {
		return null;
	}

	public Audio PlaySoundWav(String string, int int1, boolean boolean1, float float1) {
		return null;
	}

	public void update3D() {
	}

	public Audio PlayWorldSound(String string, IsoGridSquare square, float float1, float float2, float float3, boolean boolean1) {
		return this.PlayWorldSound(string, false, square, float1, float2, float3, boolean1);
	}

	public Audio PlayWorldSound(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2) {
		if (!GameServer.bServer && square != null) {
			if (GameClient.bClient) {
				GameClient.instance.PlayWorldSound(string, boolean1, square.getX(), square.getY(), square.getZ());
			}

			return this.PlayWorldSoundImpl(string, boolean1, square.getX(), square.getY(), square.getZ(), float1, float2, float3, boolean2);
		} else {
			return null;
		}
	}

	public Audio PlayWorldSoundImpl(String string, boolean boolean1, int int1, int int2, int int3, float float1, float float2, float float3, boolean boolean2) {
		BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter((float)int1 + 0.5F, (float)int2 + 0.5F, (float)int3);
		baseSoundEmitter.playSoundImpl(string, (IsoObject)null);
		return new FMODAudio(baseSoundEmitter);
	}

	public Audio PlayWorldSound(String string, IsoGridSquare square, float float1, float float2, float float3, int int1, boolean boolean1) {
		return this.PlayWorldSound(string, square, float1, float2, float3, boolean1);
	}

	public Audio PlayWorldSoundWav(String string, IsoGridSquare square, float float1, float float2, float float3, boolean boolean1) {
		return this.PlayWorldSoundWav(string, false, square, float1, float2, float3, boolean1);
	}

	public Audio PlayWorldSoundWav(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2) {
		if (!GameServer.bServer && square != null) {
			if (GameClient.bClient) {
				GameClient.instance.PlayWorldSound(string, boolean1, square.getX(), square.getY(), square.getZ());
			}

			return this.PlayWorldSoundWavImpl(string, boolean1, square, float1, float2, float3, boolean2);
		} else {
			return null;
		}
	}

	public Audio PlayWorldSoundWavImpl(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2) {
		BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter((float)square.getX() + 0.5F, (float)square.getY() + 0.5F, (float)square.getZ());
		baseSoundEmitter.playSound(string);
		return new FMODAudio(baseSoundEmitter);
	}

	public void PlayWorldSoundWav(String string, IsoGridSquare square, float float1, float float2, float float3, int int1, boolean boolean1) {
		Integer integer = Rand.Next(int1) + 1;
		this.PlayWorldSoundWav(string + integer.toString(), square, float1, float2, float3, boolean1);
	}

	public Audio PrepareMusic(String string) {
		return null;
	}

	public Audio Start(Audio audio, float float1, String string) {
		return null;
	}

	public void Update() {
		if (!this.initialized) {
			this.initialized = true;
			this.inGameGroupBus = javafmod.FMOD_Studio_System_GetBus("bus:/InGame");
			this.musicGroupBus = javafmod.FMOD_Studio_System_GetBus("bus:/Music");
			this.musicEmitter = new FMODSoundEmitter();
			this.musicEmitter.parameterUpdater = this;
			this.fmodParameters.add(this.parameterMusicLibrary);
			this.fmodParameters.add(this.parameterMusicState);
			this.fmodParameters.add(this.parameterMusicWakeState);
			this.fmodParameters.add(this.parameterMusicZombiesTargeting);
			this.fmodParameters.add(this.parameterMusicZombiesVisible);
			this.uiEmitter = new FMODSoundEmitter();
		}

		FMODSoundEmitter.update();
		this.updateMusic();
		this.uiEmitter.tick();
		for (int int1 = 0; int1 < this.ambientPieces.size(); ++int1) {
			Audio audio = (Audio)this.ambientPieces.get(int1);
			if (IsoPlayer.allPlayersDead()) {
				audio.stop();
			}

			if (!audio.isPlaying()) {
				audio.stop();
				this.ambientPieces.remove(audio);
				--int1;
			} else if (audio instanceof SoundManager.AmbientSoundEffect) {
				((SoundManager.AmbientSoundEffect)audio).update();
			}
		}

		AmbientStreamManager.instance.update();
		if (!this.AllowMusic) {
			this.StopMusic();
		}

		if (this.music.isPlaying()) {
			this.music.update();
		}

		FMODManager.instance.tick();
	}

	protected boolean HasMusic(Audio audio) {
		return false;
	}

	public void Purge() {
	}

	public void stop() {
		Iterator iterator = this.emitters.iterator();
		while (iterator.hasNext()) {
			BaseSoundEmitter baseSoundEmitter = (BaseSoundEmitter)iterator.next();
			baseSoundEmitter.stopAll();
		}

		this.emitters.clear();
		long long1 = javafmod.FMOD_System_GetMasterChannelGroup();
		javafmod.FMOD_ChannelGroup_Stop(long1);
		this.pausedEventCount = 0;
	}

	public void StopMusic() {
		this.music.stop();
	}

	public void StopSound(Audio audio) {
		audio.stop();
	}

	public void CacheSound(String string) {
	}

	public void update4() {
	}

	public void update2() {
	}

	public void update3() {
	}

	public void update1() {
	}

	public void setSoundVolume(float float1) {
		this.SoundVolume = float1;
	}

	public float getSoundVolume() {
		return this.SoundVolume;
	}

	public void setAmbientVolume(float float1) {
		this.AmbientVolume = float1;
	}

	public float getAmbientVolume() {
		return this.AmbientVolume;
	}

	public void setMusicVolume(float float1) {
		this.MusicVolume = float1;
		if (!this.muted) {
			;
		}
	}

	public float getMusicVolume() {
		return this.MusicVolume;
	}

	public void setVehicleEngineVolume(float float1) {
		this.VehicleEngineVolume = float1;
	}

	public float getVehicleEngineVolume() {
		return this.VehicleEngineVolume;
	}

	public String getCurrentMusicName() {
		return this.isPlayingMusic() ? this.currentMusicName : null;
	}

	public String getCurrentMusicLibrary() {
		return this.isPlayingMusic() ? this.currentMusicLibrary : null;
	}

	private void updateMusic() {
		this.fmodParameters.update();
		if (!this.musicEmitter.isPlaying(this.musicCombinedEvent)) {
			this.musicCombinedEvent = this.musicEmitter.playSound("MusicCombined");
		}

		if (this.musicEmitter.isPlaying(this.musicCombinedEvent)) {
			this.musicEmitter.setVolume(this.musicCombinedEvent, this.AllowMusic ? this.getMusicVolume() : 0.0F);
		}

		this.musicEmitter.tick();
	}

	private static final class Music {
		public GameSoundClip clip;
		public long instance;
		public long channel;
		public long sound;
		public float effectiveVolume;

		public boolean isPlaying() {
			if (this.instance != 0L) {
				int int1 = javafmod.FMOD_Studio_GetPlaybackState(this.instance);
				return int1 != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index && int1 != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index;
			} else {
				return this.channel != 0L && javafmod.FMOD_Channel_IsPlaying(this.channel);
			}
		}

		public void update() {
			this.clip = this.clip.checkReloaded();
			float float1 = this.clip.getEffectiveVolume();
			if (this.effectiveVolume != float1) {
				this.effectiveVolume = float1;
				if (this.instance != 0L) {
					javafmod.FMOD_Studio_EventInstance_SetVolume(this.instance, this.effectiveVolume);
				}

				if (this.channel != 0L) {
					javafmod.FMOD_Channel_SetVolume(this.channel, this.effectiveVolume);
				}
			}
		}

		public float getPosition() {
			long long1;
			if (this.instance != 0L) {
				long1 = javafmod.FMOD_Studio_GetTimelinePosition(this.instance);
				return (float)long1;
			} else if (this.channel != 0L) {
				long1 = javafmod.FMOD_Channel_GetPosition(this.channel, FMODManager.FMOD_TIMEUNIT_MS);
				return (float)long1;
			} else {
				return 0.0F;
			}
		}

		public void stop() {
			if (this.instance != 0L) {
				javafmod.FMOD_Studio_EventInstance_Stop(this.instance, false);
				javafmod.FMOD_Studio_ReleaseEventInstance(this.instance);
				this.instance = 0L;
			}

			if (this.channel != 0L) {
				javafmod.FMOD_Channel_Stop(this.channel);
				this.channel = 0L;
				javafmod.FMOD_Sound_Release(this.sound);
				this.sound = 0L;
			}
		}
	}

	public static final class AmbientSoundEffect implements Audio {
		public String name;
		public long eventInstance;
		public float gain;
		public GameSoundClip clip;
		public float effectiveVolume;

		public AmbientSoundEffect(String string) {
			GameSound gameSound = GameSounds.getSound(string);
			if (gameSound != null && !gameSound.clips.isEmpty()) {
				GameSoundClip gameSoundClip = gameSound.getRandomClip();
				if (gameSoundClip.getEvent() != null) {
					if (gameSoundClip.eventDescription != null) {
						this.eventInstance = javafmod.FMOD_Studio_System_CreateEventInstance(gameSoundClip.eventDescription.address);
						if (this.eventInstance >= 0L) {
							this.clip = gameSoundClip;
						}
					}
				}
			}
		}

		public void setVolume(float float1) {
			if (this.eventInstance > 0L) {
				this.gain = float1;
				this.effectiveVolume = this.clip.getEffectiveVolume();
				javafmod.FMOD_Studio_EventInstance_SetVolume(this.eventInstance, this.gain * this.effectiveVolume);
			}
		}

		public void start() {
			if (this.eventInstance > 0L) {
				javafmod.FMOD_Studio_StartEvent(this.eventInstance);
			}
		}

		public void pause() {
		}

		public void stop() {
			DebugLog.log("stop ambient " + this.name);
			if (this.eventInstance > 0L) {
				javafmod.FMOD_Studio_EventInstance_Stop(this.eventInstance, false);
			}
		}

		public boolean isPlaying() {
			if (this.eventInstance <= 0L) {
				return false;
			} else {
				int int1 = javafmod.FMOD_Studio_GetPlaybackState(this.eventInstance);
				return int1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STARTING.index || int1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_PLAYING.index || int1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_SUSTAINING.index;
			}
		}

		public void setName(String string) {
			this.name = string;
		}

		public String getName() {
			return this.name;
		}

		public void update() {
			if (this.clip != null) {
				this.clip = this.clip.checkReloaded();
				float float1 = this.clip.getEffectiveVolume();
				if (this.effectiveVolume != float1) {
					this.effectiveVolume = float1;
					javafmod.FMOD_Studio_EventInstance_SetVolume(this.eventInstance, this.gain * this.effectiveVolume);
				}
			}
		}
	}
}
