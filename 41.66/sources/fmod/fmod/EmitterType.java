package fmod.fmod;



public enum EmitterType {

	Footstep,
	Voice,
	Extra;

	private static EmitterType[] $values() {
		return new EmitterType[]{Footstep, Voice, Extra};
	}
}
