package zombie.characters;


public final class ZombieFootstepManager extends BaseZombieSoundManager {
	public static final ZombieFootstepManager instance = new ZombieFootstepManager();

	public ZombieFootstepManager() {
		super(40, 100);
	}

	public void playSound(IsoZombie zombie) {
		zombie.getEmitter().playFootsteps("ZombieFootstepsCombined", zombie.getFootstepVolume());
	}

	public void postUpdate() {
	}
}
