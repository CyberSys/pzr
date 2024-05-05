package zombie.iso;

import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.util.Type;


public final class SpawnPoints {
	public static final SpawnPoints instance = new SpawnPoints();
	private KahluaTable SpawnRegions;
	private final ArrayList SpawnPoints = new ArrayList();
	private final ArrayList SpawnBuildings = new ArrayList();
	private final IsoGameCharacter.Location m_tempLocation = new IsoGameCharacter.Location(-1, -1, -1);

	public void init() {
		this.SpawnRegions = LuaManager.platform.newTable();
		this.SpawnPoints.clear();
		this.SpawnBuildings.clear();
	}

	public void initServer1() {
		this.init();
		this.initSpawnRegions();
	}

	public void initServer2() {
		if (!this.parseServerSpawnPoint()) {
			this.parseSpawnRegions();
			this.initSpawnBuildings();
		}
	}

	public void initSinglePlayer() {
		this.init();
		this.initSpawnRegions();
		this.parseSpawnRegions();
		this.initSpawnBuildings();
	}

	private void initSpawnRegions() {
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("SpawnRegionMgr");
		if (kahluaTable == null) {
			DebugLog.General.error("SpawnRegionMgr is undefined");
		} else {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, kahluaTable.rawget("getSpawnRegions"));
			if (objectArray.length > 1 && objectArray[1] instanceof KahluaTable) {
				this.SpawnRegions = (KahluaTable)objectArray[1];
			}
		}
	}

	private boolean parseServerSpawnPoint() {
		if (!GameServer.bServer) {
			return false;
		} else if (ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
			return false;
		} else {
			String[] stringArray = ServerOptions.instance.SpawnPoint.getValue().split(",");
			if (stringArray.length == 3) {
				try {
					int int1 = Integer.parseInt(stringArray[0].trim());
					int int2 = Integer.parseInt(stringArray[1].trim());
					int int3 = Integer.parseInt(stringArray[2].trim());
					if (int1 != 0 || int2 != 0) {
						this.SpawnPoints.add(new IsoGameCharacter.Location(int1, int2, int3));
						return true;
					}
				} catch (NumberFormatException numberFormatException) {
					DebugLog.General.error("SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
				}
			} else {
				DebugLog.General.error("SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
			}

			return false;
		}
	}

	private void parseSpawnRegions() {
		KahluaTableIterator kahluaTableIterator = this.SpawnRegions.iterator();
		while (kahluaTableIterator.advance()) {
			KahluaTable kahluaTable = (KahluaTable)Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTable.class);
			if (kahluaTable != null) {
				this.parseRegion(kahluaTable);
			}
		}
	}

	private void parseRegion(KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = (KahluaTable)Type.tryCastTo(kahluaTable.rawget("points"), KahluaTable.class);
		if (kahluaTable2 != null) {
			KahluaTableIterator kahluaTableIterator = kahluaTable2.iterator();
			while (kahluaTableIterator.advance()) {
				KahluaTable kahluaTable3 = (KahluaTable)Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTable.class);
				if (kahluaTable3 != null) {
					this.parseProfession(kahluaTable3);
				}
			}
		}
	}

	private void parseProfession(KahluaTable kahluaTable) {
		KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
		while (kahluaTableIterator.advance()) {
			KahluaTable kahluaTable2 = (KahluaTable)Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTable.class);
			if (kahluaTable2 != null) {
				this.parsePoint(kahluaTable2);
			}
		}
	}

	private void parsePoint(KahluaTable kahluaTable) {
		Double Double1 = (Double)Type.tryCastTo(kahluaTable.rawget("worldX"), Double.class);
		Double Double2 = (Double)Type.tryCastTo(kahluaTable.rawget("worldY"), Double.class);
		Double Double3 = (Double)Type.tryCastTo(kahluaTable.rawget("posX"), Double.class);
		Double Double4 = (Double)Type.tryCastTo(kahluaTable.rawget("posY"), Double.class);
		Double Double5 = (Double)Type.tryCastTo(kahluaTable.rawget("posZ"), Double.class);
		if (Double1 != null && Double2 != null && Double3 != null && Double4 != null) {
			this.m_tempLocation.x = Double1.intValue() * 300 + Double3.intValue();
			this.m_tempLocation.y = Double2.intValue() * 300 + Double4.intValue();
			this.m_tempLocation.z = Double5 == null ? 0 : Double5.intValue();
			if (!this.SpawnPoints.contains(this.m_tempLocation)) {
				IsoGameCharacter.Location location = new IsoGameCharacter.Location(this.m_tempLocation.x, this.m_tempLocation.y, this.m_tempLocation.z);
				this.SpawnPoints.add(location);
			}
		}
	}

	private void initSpawnBuildings() {
		for (int int1 = 0; int1 < this.SpawnPoints.size(); ++int1) {
			IsoGameCharacter.Location location = (IsoGameCharacter.Location)this.SpawnPoints.get(int1);
			RoomDef roomDef = IsoWorld.instance.MetaGrid.getRoomAt(location.x, location.y, location.z);
			if (roomDef != null && roomDef.getBuilding() != null) {
				this.SpawnBuildings.add(roomDef.getBuilding());
			} else {
				DebugLog.General.warn("initSpawnBuildings: no room or building at %d,%d,%d", location.x, location.y, location.z);
			}
		}
	}

	public boolean isSpawnBuilding(BuildingDef buildingDef) {
		return this.SpawnBuildings.contains(buildingDef);
	}

	public KahluaTable getSpawnRegions() {
		return this.SpawnRegions;
	}
}
