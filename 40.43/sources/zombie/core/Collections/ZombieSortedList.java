package zombie.core.Collections;

import java.util.ArrayList;
import java.util.Collections;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoWorld;
import zombie.network.ServerOptions;


public class ZombieSortedList {
	private IsoPlayer player;
	private ArrayList zombies;

	public ZombieSortedList(IsoPlayer player, int int1) {
		this.player = player;
		this.zombies = new ArrayList();
	}

	private boolean contain(IsoZombie zombie) {
		for (int int1 = 0; int1 < this.zombies.size(); ++int1) {
			if (((ZombieSortedList.ZombieWeight)this.zombies.get(int1)).zombie.equals(zombie)) {
				return true;
			}
		}

		return false;
	}

	public void update() {
		this.zombies.clear();
		int int1 = ServerOptions.instance.ZombieUpdateMaxHighPriority.getValue();
		double double1 = ServerOptions.instance.ZombieUpdateRadiusHighPriority.getValue();
		double1 *= double1;
		ArrayList arrayList = IsoWorld.instance.CurrentCell.getZombieList();
		int int2;
		for (int2 = 0; int2 < arrayList.size(); ++int2) {
			IsoZombie zombie = (IsoZombie)arrayList.get(int2);
			double double2 = (double)(this.player.x - zombie.x);
			double double3 = (double)(this.player.y - zombie.y);
			double2 *= double2;
			double3 *= double3;
			float float1 = (float)(double2 + double3);
			if ((double)float1 < double1 && !this.zombies.contains(zombie)) {
				this.zombies.add(new ZombieSortedList.ZombieWeight(zombie, float1));
			}
		}

		Collections.sort(this.zombies);
		if (int1 < this.zombies.size()) {
			for (int2 = this.zombies.size() - 1; int2 >= int1; --int2) {
				this.zombies.remove(int2);
			}
		}
	}

	public IsoZombie getZombie(int int1) {
		if (this.zombies.size() != 0 && int1 <= this.zombies.size() - 1) {
			ZombieSortedList.ZombieWeight zombieWeight = (ZombieSortedList.ZombieWeight)this.zombies.get(this.zombies.size() - 1 - int1);
			zombieWeight.weight = 0.0F;
			return zombieWeight.zombie;
		} else {
			return null;
		}
	}

	class ZombieWeight implements Comparable {
		public IsoZombie zombie;
		public float weight;

		public ZombieWeight(IsoZombie zombie, float float1) {
			this.zombie = zombie;
			this.weight = float1;
		}

		public int compareTo(ZombieSortedList.ZombieWeight zombieWeight) {
			if (this.weight == zombieWeight.weight) {
				return 0;
			} else {
				return this.weight < zombieWeight.weight ? -1 : 1;
			}
		}

		public boolean equals(Object object) {
			return this.zombie.equals(object);
		}
	}
}
