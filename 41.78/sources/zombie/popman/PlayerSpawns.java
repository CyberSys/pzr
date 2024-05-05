package zombie.popman;

import java.util.ArrayList;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;


final class PlayerSpawns {
	private final ArrayList playerSpawns = new ArrayList();

	public void addSpawn(int int1, int int2, int int3) {
		PlayerSpawns.PlayerSpawn playerSpawn = new PlayerSpawns.PlayerSpawn(int1, int2, int3);
		if (playerSpawn.building != null) {
			this.playerSpawns.add(playerSpawn);
		}
	}

	public void update() {
		long long1 = System.currentTimeMillis();
		for (int int1 = 0; int1 < this.playerSpawns.size(); ++int1) {
			PlayerSpawns.PlayerSpawn playerSpawn = (PlayerSpawns.PlayerSpawn)this.playerSpawns.get(int1);
			if (playerSpawn.counter == -1L) {
				playerSpawn.counter = long1;
			}

			if (playerSpawn.counter + 10000L <= long1) {
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
		public long counter;
		public BuildingDef building;

		public PlayerSpawn(int int1, int int2, int int3) {
			this.x = int1;
			this.y = int2;
			this.counter = -1L;
			RoomDef roomDef = IsoWorld.instance.getMetaGrid().getRoomAt(int1, int2, int3);
			if (roomDef != null) {
				this.building = roomDef.getBuilding();
			}
		}

		public boolean allowZombie(IsoGridSquare square) {
			if (this.building == null) {
				return true;
			} else if (square.getBuilding() != null && this.building == square.getBuilding().getDef()) {
				return false;
			} else {
				return square.getX() < this.building.getX() - 15 || square.getX() >= this.building.getX2() + 15 || square.getY() < this.building.getY() - 15 || square.getY() >= this.building.getY2() + 15;
			}
		}
	}
}
