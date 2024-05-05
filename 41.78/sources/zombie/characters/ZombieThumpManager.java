package zombie.characters;

import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import zombie.core.math.PZMath;
import zombie.iso.IsoObject;


public final class ZombieThumpManager extends BaseZombieSoundManager {
	public static final ZombieThumpManager instance = new ZombieThumpManager();

	public ZombieThumpManager() {
		super(40, 100);
	}

	public void playSound(IsoZombie zombie) {
		long long1 = 0L;
		if (zombie.thumpFlag == 1) {
			long1 = zombie.getEmitter().playSoundImpl("ZombieThumpGeneric", (IsoObject)null);
		} else if (zombie.thumpFlag == 2) {
			zombie.getEmitter().playSoundImpl("ZombieThumpGeneric", (IsoObject)null);
			long1 = zombie.getEmitter().playSoundImpl("ZombieThumpWindow", (IsoObject)null);
		} else if (zombie.thumpFlag == 3) {
			long1 = zombie.getEmitter().playSoundImpl("ZombieThumpWindow", (IsoObject)null);
		} else if (zombie.thumpFlag == 4) {
			long1 = zombie.getEmitter().playSoundImpl("ZombieThumpMetal", (IsoObject)null);
		} else if (zombie.thumpFlag == 5) {
			long1 = zombie.getEmitter().playSoundImpl("ZombieThumpGarageDoor", (IsoObject)null);
		}

		FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION = FMODManager.instance.getParameterDescription("ObjectCondition");
		zombie.getEmitter().setParameterValue(long1, fMOD_STUDIO_PARAMETER_DESCRIPTION, PZMath.ceil(zombie.getThumpCondition() * 100.0F));
	}

	public void postUpdate() {
		for (int int1 = 0; int1 < this.characters.size(); ++int1) {
			((IsoZombie)this.characters.get(int1)).setThumpFlag(0);
		}
	}
}
