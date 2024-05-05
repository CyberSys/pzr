package fmod.fmod;

import java.util.BitSet;
import zombie.audio.FMODParameterList;
import zombie.audio.GameSoundClip;


public interface IFMODParameterUpdater {
	FMODParameterList getFMODParameters();

	void startEvent(long long1, GameSoundClip gameSoundClip, BitSet bitSet);

	void updateEvent(long long1, GameSoundClip gameSoundClip);

	void stopEvent(long long1, GameSoundClip gameSoundClip, BitSet bitSet);
}
