package zombie.characters;

import zombie.iso.IsoObject;


public class ZombieThumpManager extends BaseZombieSoundManager {
	public static final ZombieThumpManager instance = new ZombieThumpManager();

	public ZombieThumpManager() {
		super(40, 100);
	}

	public void playSound(IsoZombie zombie) {
		if (zombie.thumpFlag == 1) {
			zombie.getEmitter().playSoundImpl("ZombieThumpGeneric", (IsoObject)null);
		} else if (zombie.thumpFlag == 2) {
			zombie.getEmitter().playSoundImpl("ZombieThumpGeneric", (IsoObject)null);
			zombie.getEmitter().playSoundImpl("ZombieThumpWindow", (IsoObject)null);
		} else if (zombie.thumpFlag == 3) {
			zombie.getEmitter().playSoundImpl("ZombieThumpWindow", (IsoObject)null);
		} else if (zombie.thumpFlag == 4) {
			zombie.getEmitter().playSoundImpl("ZombieThumpMetal", (IsoObject)null);
		}
	}

	public void postUpdate() {
		for (int int1 = 0; int1 < this.characters.size(); ++int1) {
			((IsoZombie)this.characters.get(int1)).thumpFlag = 0;
		}
	}
}
