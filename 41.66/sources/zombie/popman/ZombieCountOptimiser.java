package zombie.popman;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.network.GameClient;
import zombie.network.MPStatistics;


public class ZombieCountOptimiser {
	private static int zombieCountForDelete = 0;
	public static final int maxZombieCount = 500;
	public static final int minZombieDistance = 20;
	public static final ArrayList zombiesForDelete = new ArrayList();

	public static void startCount() {
		zombieCountForDelete = (int)(1.0F * (float)Math.max(0, GameClient.IDToZombieMap.values().length - 500));
	}

	public static void incrementZombie(IsoZombie zombie) {
		if (zombieCountForDelete > 0 && Rand.Next(10) == 0 && zombie.canBeDeletedUnnoticed(20.0F) && !zombie.isReanimatedPlayer()) {
			synchronized (zombiesForDelete) {
				zombiesForDelete.add(zombie);
			}

			--zombieCountForDelete;
			MPStatistics.clientZombieCulled();
		}
	}
}
