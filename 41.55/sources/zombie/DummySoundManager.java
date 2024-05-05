package zombie;

import fmod.fmod.Audio;
import java.util.ArrayList;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;


public final class DummySoundManager extends BaseSoundManager {
	private static ArrayList ambientPieces = new ArrayList();

	public boolean isRemastered() {
		return false;
	}

	public void update1() {
	}

	public void update3() {
	}

	public void update2() {
	}

	public void update4() {
	}

	public void CacheSound(String string) {
	}

	public void StopSound(Audio audio) {
	}

	public void StopMusic() {
	}

	public void Purge() {
	}

	public void stop() {
	}

	protected boolean HasMusic(Audio audio) {
		return false;
	}

	public void Update() {
	}

	public Audio Start(Audio audio, float float1, String string) {
		return null;
	}

	public Audio PrepareMusic(String string) {
		return null;
	}

	public void PlayWorldSoundWav(String string, IsoGridSquare square, float float1, float float2, float float3, int int1, boolean boolean1) {
	}

	public Audio PlayWorldSoundWav(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2) {
		return null;
	}

	public Audio PlayWorldSoundWav(String string, IsoGridSquare square, float float1, float float2, float float3, boolean boolean1) {
		return null;
	}

	public Audio PlayWorldSound(String string, IsoGridSquare square, float float1, float float2, float float3, int int1, boolean boolean1) {
		return null;
	}

	public Audio PlayWorldSound(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2) {
		return null;
	}

	public Audio PlayWorldSoundImpl(String string, boolean boolean1, int int1, int int2, int int3, float float1, float float2, float float3, boolean boolean2) {
		return null;
	}

	public Audio PlayWorldSound(String string, IsoGridSquare square, float float1, float float2, float float3, boolean boolean1) {
		return null;
	}

	public void update3D() {
	}

	public Audio PlaySoundWav(String string, int int1, boolean boolean1, float float1) {
		return null;
	}

	public Audio PlaySoundWav(String string, boolean boolean1, float float1) {
		return null;
	}

	public Audio PlaySoundWav(String string, boolean boolean1, float float1, float float2) {
		return null;
	}

	public Audio PlayJukeboxSound(String string, boolean boolean1, float float1) {
		return null;
	}

	public Audio PlaySoundEvenSilent(String string, boolean boolean1, float float1) {
		return null;
	}

	public Audio PlaySound(String string, boolean boolean1, float float1) {
		return null;
	}

	public Audio PlaySound(String string, boolean boolean1, float float1, float float2) {
		return null;
	}

	public Audio PlayMusic(String string, String string2, boolean boolean1, float float1) {
		return null;
	}

	public void PlayAsMusic(String string, Audio audio, boolean boolean1, float float1) {
	}

	public void setMusicState(String string) {
	}

	public void setMusicWakeState(IsoPlayer player, String string) {
	}

	public void DoMusic(String string, boolean boolean1) {
	}

	public float getMusicPosition() {
		return 0.0F;
	}

	public void CheckDoMusic() {
	}

	public void stopMusic(String string) {
	}

	public void playMusicNonTriggered(String string, float float1) {
	}

	public void playAmbient(String string) {
	}

	public void playMusic(String string) {
	}

	public boolean isPlayingMusic() {
		return false;
	}

	public boolean IsMusicPlaying() {
		return false;
	}

	public void PlayAsMusic(String string, Audio audio, float float1, boolean boolean1) {
	}

	public long playUISound(String string) {
		return 0L;
	}

	public boolean isPlayingUISound(String string) {
		return false;
	}

	public boolean isPlayingUISound(long long1) {
		return false;
	}

	public void stopUISound(long long1) {
	}

	public void FadeOutMusic(String string, int int1) {
	}

	public Audio BlendThenStart(Audio audio, float float1, String string) {
		return null;
	}

	public void BlendVolume(Audio audio, float float1, float float2) {
	}

	public void BlendVolume(Audio audio, float float1) {
	}

	public void setSoundVolume(float float1) {
	}

	public float getSoundVolume() {
		return 0.0F;
	}

	public void setMusicVolume(float float1) {
	}

	public float getMusicVolume() {
		return 0.0F;
	}

	public void setVehicleEngineVolume(float float1) {
	}

	public float getVehicleEngineVolume() {
		return 0.0F;
	}

	public void setAmbientVolume(float float1) {
	}

	public float getAmbientVolume() {
		return 0.0F;
	}

	public void playNightAmbient(String string) {
	}

	public ArrayList getAmbientPieces() {
		return ambientPieces;
	}

	public void pauseSoundAndMusic() {
	}

	public void resumeSoundAndMusic() {
	}

	public void debugScriptSounds() {
	}

	public void registerEmitter(BaseSoundEmitter baseSoundEmitter) {
	}

	public void unregisterEmitter(BaseSoundEmitter baseSoundEmitter) {
	}

	public boolean isListenerInRange(float float1, float float2, float float3) {
		return false;
	}

	public Audio PlayWorldSoundWavImpl(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2) {
		return null;
	}

	public String getCurrentMusicName() {
		return null;
	}

	public String getCurrentMusicLibrary() {
		return null;
	}
}
