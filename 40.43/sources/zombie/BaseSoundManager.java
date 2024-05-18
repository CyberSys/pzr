package zombie;

import fmod.fmod.Audio;
import java.util.ArrayList;
import zombie.audio.BaseSoundEmitter;
import zombie.iso.IsoGridSquare;


public abstract class BaseSoundManager {
	public boolean AllowMusic = true;

	public abstract boolean isRemastered();

	public abstract void update1();

	public abstract void update3();

	public abstract void update2();

	public abstract void update4();

	public abstract void CacheSound(String string);

	public abstract void StopSound(Audio audio);

	public abstract void StopMusic();

	public abstract void Purge();

	public abstract void stop();

	protected abstract boolean HasMusic(Audio audio);

	public abstract void Update();

	public abstract Audio Start(Audio audio, float float1, String string);

	public abstract Audio PrepareMusic(String string);

	public abstract void PlayWorldSoundWav(String string, IsoGridSquare square, float float1, float float2, float float3, int int1, boolean boolean1);

	public abstract Audio PlayWorldSoundWav(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2);

	public abstract Audio PlayWorldSoundWav(String string, IsoGridSquare square, float float1, float float2, float float3, boolean boolean1);

	public abstract Audio PlayWorldSound(String string, IsoGridSquare square, float float1, float float2, float float3, int int1, boolean boolean1);

	public abstract Audio PlayWorldSound(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2);

	public abstract Audio PlayWorldSoundImpl(String string, boolean boolean1, int int1, int int2, int int3, float float1, float float2, float float3, boolean boolean2);

	public abstract Audio PlayWorldSound(String string, IsoGridSquare square, float float1, float float2, float float3, boolean boolean1);

	public abstract void update3D();

	public abstract Audio PlaySoundWav(String string, int int1, boolean boolean1, float float1);

	public abstract Audio PlaySoundWav(String string, boolean boolean1, float float1);

	public abstract Audio PlaySoundWav(String string, boolean boolean1, float float1, float float2);

	public abstract Audio PlayWorldSoundWavImpl(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2);

	public abstract Audio PlayJukeboxSound(String string, boolean boolean1, float float1);

	public abstract Audio PlaySoundEvenSilent(String string, boolean boolean1, float float1);

	public abstract Audio PlaySound(String string, boolean boolean1, float float1);

	public abstract Audio PlaySound(String string, boolean boolean1, float float1, float float2);

	public abstract Audio PlayMusic(String string, String string2, boolean boolean1, float float1);

	public abstract void PlayAsMusic(String string, Audio audio, boolean boolean1, float float1);

	public abstract void DoMusic(String string, boolean boolean1);

	public abstract float getMusicPosition();

	public abstract void CheckDoMusic();

	public abstract void stopMusic(String string);

	public abstract void playMusicNonTriggered(String string, float float1);

	public abstract void playAmbient(String string);

	public abstract void playMusic(String string);

	public abstract boolean isPlayingMusic();

	public abstract boolean IsMusicPlaying();

	public abstract String getCurrentMusicName();

	public abstract String getCurrentMusicLibrary();

	public abstract void PlayAsMusic(String string, Audio audio, float float1, boolean boolean1);

	public abstract void FadeOutMusic(String string, int int1);

	public abstract Audio BlendThenStart(Audio audio, float float1, String string);

	public abstract void BlendVolume(Audio audio, float float1, float float2);

	public abstract void BlendVolume(Audio audio, float float1);

	public abstract void setSoundVolume(float float1);

	public abstract float getSoundVolume();

	public abstract void setAmbientVolume(float float1);

	public abstract float getAmbientVolume();

	public abstract void setMusicVolume(float float1);

	public abstract float getMusicVolume();

	public abstract void setVehicleEngineVolume(float float1);

	public abstract float getVehicleEngineVolume();

	public abstract void playNightAmbient(String string);

	public abstract ArrayList getAmbientPieces();

	public abstract void pauseSoundAndMusic();

	public abstract void resumeSoundAndMusic();

	public abstract void debugScriptSounds();

	public abstract void registerEmitter(BaseSoundEmitter baseSoundEmitter);

	public abstract void unregisterEmitter(BaseSoundEmitter baseSoundEmitter);

	public abstract boolean isListenerInRange(float float1, float float2, float float3);
}
