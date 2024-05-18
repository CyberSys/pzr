package fmod.fmod;

import fmod.javafmod;
import java.security.InvalidParameterException;
import java.util.HashMap;
import zombie.audio.BaseSoundBank;
import zombie.core.Core;


public class FMODSoundBank extends BaseSoundBank {
	public HashMap voiceMap = new HashMap();
	public HashMap footstepMap = new HashMap();

	private void check(String string) {
		if (Core.bDebug && javafmod.FMOD_Studio_System_GetEvent("event:/" + string) < 0L) {
			System.out.println("MISSING in .bank " + string);
		}
	}

	public void addVoice(String string, String string2, float float1) {
		FMODVoice fMODVoice = new FMODVoice(string2, float1);
		this.voiceMap.put(string, fMODVoice);
	}

	public void addFootstep(String string, String string2, String string3, String string4, String string5) {
		FMODFootstep fMODFootstep = new FMODFootstep(string2, string3, string4, string5);
		this.footstepMap.put(string, fMODFootstep);
	}

	public FMODVoice getVoice(String string) {
		if (this.voiceMap.containsKey(string)) {
			return (FMODVoice)this.voiceMap.get(string);
		} else {
			throw new InvalidParameterException("Voice not loaded: " + string);
		}
	}

	public FMODFootstep getFootstep(String string) {
		if (this.footstepMap.containsKey(string)) {
			return (FMODFootstep)this.footstepMap.get(string);
		} else {
			throw new InvalidParameterException("Footstep not loaded: " + string);
		}
	}
}
