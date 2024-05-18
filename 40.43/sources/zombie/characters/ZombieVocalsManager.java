package zombie.characters;


public class ZombieVocalsManager extends BaseZombieSoundManager {
	public static final ZombieVocalsManager instance = new ZombieVocalsManager();

	public ZombieVocalsManager() {
		super(40, 1000);
	}

	public void playSound(IsoZombie zombie) {
		String string = zombie.bFemale ? "FemaleZombieIdle" : "MaleZombieIdle";
		zombie.getEmitter().playVocals(string);
	}

	public void postUpdate() {
	}
}
