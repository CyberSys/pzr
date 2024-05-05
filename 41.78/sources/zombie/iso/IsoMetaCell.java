package zombie.iso;

import java.util.ArrayList;
import java.util.Set;
import zombie.Lua.LuaEventManager;
import zombie.core.math.PZMath;


public final class IsoMetaCell {
	public final ArrayList vehicleZones = new ArrayList();
	public final IsoMetaChunk[] ChunkMap = new IsoMetaChunk[900];
	public LotHeader info = null;
	public final ArrayList triggers = new ArrayList();
	private int wx = 0;
	private int wy = 0;
	public final ArrayList mannequinZones = new ArrayList();
	public final ArrayList roomTones = new ArrayList();

	public IsoMetaCell(int int1, int int2) {
		this.wx = int1;
		this.wy = int2;
		for (int int3 = 0; int3 < 900; ++int3) {
			this.ChunkMap[int3] = new IsoMetaChunk();
		}
	}

	public void addTrigger(BuildingDef buildingDef, int int1, int int2, String string) {
		this.triggers.add(new IsoMetaGrid.Trigger(buildingDef, int1, int2, string));
	}

	public void checkTriggers() {
		if (IsoCamera.CamCharacter != null) {
			int int1 = (int)IsoCamera.CamCharacter.getX();
			int int2 = (int)IsoCamera.CamCharacter.getY();
			for (int int3 = 0; int3 < this.triggers.size(); ++int3) {
				IsoMetaGrid.Trigger trigger = (IsoMetaGrid.Trigger)this.triggers.get(int3);
				if (int1 >= trigger.def.x - trigger.triggerRange && int1 <= trigger.def.x2 + trigger.triggerRange && int2 >= trigger.def.y - trigger.triggerRange && int2 <= trigger.def.y2 + trigger.triggerRange) {
					if (!trigger.triggered) {
						LuaEventManager.triggerEvent("OnTriggerNPCEvent", trigger.type, trigger.data, trigger.def);
					}

					LuaEventManager.triggerEvent("OnMultiTriggerNPCEvent", trigger.type, trigger.data, trigger.def);
					trigger.triggered = true;
				}
			}
		}
	}

	public IsoMetaChunk getChunk(int int1, int int2) {
		return int2 < 30 && int1 < 30 && int1 >= 0 && int2 >= 0 ? this.ChunkMap[int2 * 30 + int1] : null;
	}

	public void addZone(IsoMetaGrid.Zone zone, int int1, int int2) {
		int int3 = zone.x / 10;
		int int4 = zone.y / 10;
		int int5 = (zone.x + zone.w) / 10;
		if ((zone.x + zone.w) % 10 == 0) {
			--int5;
		}

		int int6 = (zone.y + zone.h) / 10;
		if ((zone.y + zone.h) % 10 == 0) {
			--int6;
		}

		int3 = PZMath.clamp(int3, int1 / 10, (int1 + 300) / 10);
		int4 = PZMath.clamp(int4, int2 / 10, (int2 + 300) / 10);
		int5 = PZMath.clamp(int5, int1 / 10, (int1 + 300) / 10 - 1);
		int6 = PZMath.clamp(int6, int2 / 10, (int2 + 300) / 10 - 1);
		for (int int7 = int4; int7 <= int6; ++int7) {
			for (int int8 = int3; int8 <= int5; ++int8) {
				if (zone.intersects(int8 * 10, int7 * 10, zone.z, 10, 10)) {
					int int9 = int8 - int1 / 10 + (int7 - int2 / 10) * 30;
					if (this.ChunkMap[int9] != null) {
						this.ChunkMap[int9].addZone(zone);
					}
				}
			}
		}
	}

	public void removeZone(IsoMetaGrid.Zone zone) {
		int int1 = (zone.x + zone.w) / 10;
		if ((zone.x + zone.w) % 10 == 0) {
			--int1;
		}

		int int2 = (zone.y + zone.h) / 10;
		if ((zone.y + zone.h) % 10 == 0) {
			--int2;
		}

		int int3 = this.wx * 300;
		int int4 = this.wy * 300;
		for (int int5 = zone.y / 10; int5 <= int2; ++int5) {
			for (int int6 = zone.x / 10; int6 <= int1; ++int6) {
				if (int6 >= int3 / 10 && int6 < (int3 + 300) / 10 && int5 >= int4 / 10 && int5 < (int4 + 300) / 10) {
					int int7 = int6 - int3 / 10 + (int5 - int4 / 10) * 30;
					if (this.ChunkMap[int7] != null) {
						this.ChunkMap[int7].removeZone(zone);
					}
				}
			}
		}
	}

	public void addRoom(RoomDef roomDef, int int1, int int2) {
		int int3 = roomDef.x2 / 10;
		if (roomDef.x2 % 10 == 0) {
			--int3;
		}

		int int4 = roomDef.y2 / 10;
		if (roomDef.y2 % 10 == 0) {
			--int4;
		}

		for (int int5 = roomDef.y / 10; int5 <= int4; ++int5) {
			for (int int6 = roomDef.x / 10; int6 <= int3; ++int6) {
				if (int6 >= int1 / 10 && int6 < (int1 + 300) / 10 && int5 >= int2 / 10 && int5 < (int2 + 300) / 10) {
					int int7 = int6 - int1 / 10 + (int5 - int2 / 10) * 30;
					if (this.ChunkMap[int7] != null) {
						this.ChunkMap[int7].addRoom(roomDef);
					}
				}
			}
		}
	}

	public void getZonesUnique(Set set) {
		for (int int1 = 0; int1 < this.ChunkMap.length; ++int1) {
			IsoMetaChunk metaChunk = this.ChunkMap[int1];
			if (metaChunk != null) {
				metaChunk.getZonesUnique(set);
			}
		}
	}

	public void getZonesIntersecting(int int1, int int2, int int3, int int4, int int5, ArrayList arrayList) {
		int int6 = (int1 + int4) / 10;
		if ((int1 + int4) % 10 == 0) {
			--int6;
		}

		int int7 = (int2 + int5) / 10;
		if ((int2 + int5) % 10 == 0) {
			--int7;
		}

		int int8 = this.wx * 300;
		int int9 = this.wy * 300;
		for (int int10 = int2 / 10; int10 <= int7; ++int10) {
			for (int int11 = int1 / 10; int11 <= int6; ++int11) {
				if (int11 >= int8 / 10 && int11 < (int8 + 300) / 10 && int10 >= int9 / 10 && int10 < (int9 + 300) / 10) {
					int int12 = int11 - int8 / 10 + (int10 - int9 / 10) * 30;
					if (this.ChunkMap[int12] != null) {
						this.ChunkMap[int12].getZonesIntersecting(int1, int2, int3, int4, int5, arrayList);
					}
				}
			}
		}
	}

	public void getRoomsIntersecting(int int1, int int2, int int3, int int4, ArrayList arrayList) {
		int int5 = (int1 + int3) / 10;
		if ((int1 + int3) % 10 == 0) {
			--int5;
		}

		int int6 = (int2 + int4) / 10;
		if ((int2 + int4) % 10 == 0) {
			--int6;
		}

		int int7 = this.wx * 300;
		int int8 = this.wy * 300;
		for (int int9 = int2 / 10; int9 <= int6; ++int9) {
			for (int int10 = int1 / 10; int10 <= int5; ++int10) {
				if (int10 >= int7 / 10 && int10 < (int7 + 300) / 10 && int9 >= int8 / 10 && int9 < (int8 + 300) / 10) {
					int int11 = int10 - int7 / 10 + (int9 - int8 / 10) * 30;
					if (this.ChunkMap[int11] != null) {
						this.ChunkMap[int11].getRoomsIntersecting(int1, int2, int3, int4, arrayList);
					}
				}
			}
		}
	}

	public void Dispose() {
		for (int int1 = 0; int1 < this.ChunkMap.length; ++int1) {
			IsoMetaChunk metaChunk = this.ChunkMap[int1];
			if (metaChunk != null) {
				metaChunk.Dispose();
				this.ChunkMap[int1] = null;
			}
		}

		this.info = null;
		this.mannequinZones.clear();
		this.roomTones.clear();
	}
}
