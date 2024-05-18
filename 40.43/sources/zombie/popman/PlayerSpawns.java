package zombie.popman;

import java.util.ArrayList;
import zombie.core.PerformanceSettings;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoBuilding;


final class PlayerSpawns {
	private ArrayList playerSpawns = new ArrayList();

	public void addSpawn(int int1, int int2, int int3) {
		PlayerSpawns.PlayerSpawn playerSpawn = new PlayerSpawns.PlayerSpawn(int1, int2, int3);
		if (playerSpawn.building != null) {
			this.playerSpawns.add(playerSpawn);
		}
	}

	public void update() {
		for (int int1 = 0; int1 < this.playerSpawns.size(); ++int1) {
			PlayerSpawns.PlayerSpawn playerSpawn = (PlayerSpawns.PlayerSpawn)this.playerSpawns.get(int1);
			if (--playerSpawn.counter <= 0) {
				this.playerSpawns.remove(int1--);
			}
		}
	}

	public boolean allowZombie(IsoGridSquare square) {
		for (int int1 = 0; int1 < this.playerSpawns.size(); ++int1) {
			PlayerSpawns.PlayerSpawn playerSpawn = (PlayerSpawns.PlayerSpawn)this.playerSpawns.get(int1);
			if (!playerSpawn.allowZombie(square)) {
				return false;
			}
		}

		return true;
	}

	private static class PlayerSpawn {
		public int x;
		public int y;
		public int counter;
		public IsoBuilding building;

		public PlayerSpawn(int int1, int int2, int int3) {
			this.x = int1;
			this.y = int2;
			this.counter = PerformanceSettings.LockFPS * 10;
			IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(int1, int2, int3);
			if (square != null) {
				this.building = square.getBuilding();
			}
		}

		public boolean allowZombie(IsoGridSquare square) {
			if (this.building == square.getBuilding()) {
				return false;
			} else if (this.building == null) {
				return true;
			} else {
				return square.getX() < this.building.def.getX() - 15 || square.getX() >= this.building.def.getX2() + 15 || square.getY() < this.building.def.getY() - 15 || square.getY() >= this.building.def.getY2() + 15;
			}
		}
	}
}
