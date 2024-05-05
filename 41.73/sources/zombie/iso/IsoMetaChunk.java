package zombie.iso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import zombie.SandboxOptions;
import zombie.core.Rand;


public final class IsoMetaChunk {
	public static final float zombiesMinPerChunk = 0.06F;
	public static final float zombiesFullPerChunk = 12.0F;
	private int ZombieIntensity = 0;
	private IsoMetaGrid.Zone[] zones;
	private int zonesSize;
	private RoomDef[] rooms;
	private int roomsSize;

	public float getZombieIntensity(boolean boolean1) {
		float float1 = (float)this.ZombieIntensity;
		float float2 = float1 / 255.0F;
		if (SandboxOptions.instance.Distribution.getValue() == 2) {
			float1 = 128.0F;
			float2 = 0.5F;
		}

		float1 *= 0.5F;
		if (SandboxOptions.instance.Zombies.getValue() == 1) {
			float1 *= 4.0F;
		} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
			float1 *= 3.0F;
		} else if (SandboxOptions.instance.Zombies.getValue() == 3) {
			float1 *= 2.0F;
		} else if (SandboxOptions.instance.Zombies.getValue() == 5) {
			float1 *= 0.35F;
		} else if (SandboxOptions.instance.Zombies.getValue() == 6) {
			float1 = 0.0F;
		}

		float2 = float1 / 255.0F;
		float float3 = 11.94F;
		float3 *= float2;
		float1 = 0.06F + float3;
		if (!boolean1) {
			return float1;
		} else {
			float float4 = float2 * 10.0F;
			if (Rand.Next(3) == 0) {
				return 0.0F;
			} else {
				float4 *= 0.5F;
				int int1 = 1000;
				if (SandboxOptions.instance.Zombies.getValue() == 1) {
					int1 = (int)((float)int1 / 2.0F);
				} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
					int1 = (int)((float)int1 / 1.7F);
				} else if (SandboxOptions.instance.Zombies.getValue() == 3) {
					int1 = (int)((float)int1 / 1.5F);
				} else if (SandboxOptions.instance.Zombies.getValue() == 5) {
					int1 = (int)((float)int1 * 1.5F);
				}

				if ((float)Rand.Next(int1) < float4 && IsoWorld.getZombiesEnabled()) {
					float1 = 120.0F;
					if (float1 > 12.0F) {
						float1 = 12.0F;
					}
				}

				return float1;
			}
		}
	}

	public float getZombieIntensity() {
		return this.getZombieIntensity(true);
	}

	public void setZombieIntensity(int int1) {
		if (int1 >= 0) {
			this.ZombieIntensity = int1;
		}
	}

	public float getLootZombieIntensity() {
		float float1 = (float)this.ZombieIntensity;
		float float2 = float1 / 255.0F;
		float2 = float1 / 255.0F;
		float float3 = 11.94F;
		float3 *= float2;
		float1 = 0.06F + float3;
		float float4 = float2 * 10.0F;
		float2 = float2 * float2 * float2;
		if ((float)Rand.Next(300) <= float4) {
			float1 = 120.0F;
		}

		return IsoWorld.getZombiesDisabled() ? 400.0F : float1;
	}

	public int getUnadjustedZombieIntensity() {
		return this.ZombieIntensity;
	}

	public void addZone(IsoMetaGrid.Zone zone) {
		if (this.zones == null) {
			this.zones = new IsoMetaGrid.Zone[8];
		}

		if (this.zonesSize == this.zones.length) {
			IsoMetaGrid.Zone[] zoneArray = new IsoMetaGrid.Zone[this.zones.length + 8];
			System.arraycopy(this.zones, 0, zoneArray, 0, this.zonesSize);
			this.zones = zoneArray;
		}

		this.zones[this.zonesSize++] = zone;
	}

	public void removeZone(IsoMetaGrid.Zone zone) {
		if (this.zones != null) {
			for (int int1 = 0; int1 < this.zonesSize; ++int1) {
				if (this.zones[int1] == zone) {
					while (int1 < this.zonesSize - 1) {
						this.zones[int1] = this.zones[int1 + 1];
						++int1;
					}

					this.zones[this.zonesSize - 1] = null;
					--this.zonesSize;
					break;
				}
			}
		}
	}

	public IsoMetaGrid.Zone getZone(int int1) {
		return int1 >= 0 && int1 < this.zonesSize ? this.zones[int1] : null;
	}

	public IsoMetaGrid.Zone getZoneAt(int int1, int int2, int int3) {
		if (this.zones != null && this.zonesSize > 0) {
			IsoMetaGrid.Zone zone = null;
			for (int int4 = this.zonesSize - 1; int4 >= 0; --int4) {
				IsoMetaGrid.Zone zone2 = this.zones[int4];
				if (zone2.contains(int1, int2, int3)) {
					if (zone2.isPreferredZoneForSquare) {
						return zone2;
					}

					if (zone == null) {
						zone = zone2;
					}
				}
			}

			return zone;
		} else {
			return null;
		}
	}

	public ArrayList getZonesAt(int int1, int int2, int int3, ArrayList arrayList) {
		for (int int4 = 0; int4 < this.zonesSize; ++int4) {
			IsoMetaGrid.Zone zone = this.zones[int4];
			if (zone.contains(int1, int2, int3)) {
				arrayList.add(zone);
			}
		}

		return arrayList;
	}

	public void getZonesUnique(Set set) {
		for (int int1 = 0; int1 < this.zonesSize; ++int1) {
			IsoMetaGrid.Zone zone = this.zones[int1];
			set.add(zone);
		}
	}

	public void getZonesIntersecting(int int1, int int2, int int3, int int4, int int5, ArrayList arrayList) {
		for (int int6 = 0; int6 < this.zonesSize; ++int6) {
			IsoMetaGrid.Zone zone = this.zones[int6];
			if (!arrayList.contains(zone) && zone.intersects(int1, int2, int3, int4, int5)) {
				arrayList.add(zone);
			}
		}
	}

	public void clearZones() {
		if (this.zones != null) {
			for (int int1 = 0; int1 < this.zones.length; ++int1) {
				this.zones[int1] = null;
			}
		}

		this.zones = null;
		this.zonesSize = 0;
	}

	public void clearRooms() {
		if (this.rooms != null) {
			for (int int1 = 0; int1 < this.rooms.length; ++int1) {
				this.rooms[int1] = null;
			}
		}

		this.rooms = null;
		this.roomsSize = 0;
	}

	public int numZones() {
		return this.zonesSize;
	}

	public void addRoom(RoomDef roomDef) {
		if (this.rooms == null) {
			this.rooms = new RoomDef[8];
		}

		if (this.roomsSize == this.rooms.length) {
			RoomDef[] roomDefArray = new RoomDef[this.rooms.length + 8];
			System.arraycopy(this.rooms, 0, roomDefArray, 0, this.roomsSize);
			this.rooms = roomDefArray;
		}

		this.rooms[this.roomsSize++] = roomDef;
	}

	public RoomDef getRoomAt(int int1, int int2, int int3) {
		for (int int4 = 0; int4 < this.roomsSize; ++int4) {
			RoomDef roomDef = this.rooms[int4];
			if (!roomDef.isEmptyOutside() && roomDef.level == int3) {
				for (int int5 = 0; int5 < roomDef.rects.size(); ++int5) {
					RoomDef.RoomRect roomRect = (RoomDef.RoomRect)roomDef.rects.get(int5);
					if (roomRect.x <= int1 && roomRect.y <= int2 && int1 < roomRect.getX2() && int2 < roomRect.getY2()) {
						return roomDef;
					}
				}
			}
		}

		return null;
	}

	public RoomDef getEmptyOutsideAt(int int1, int int2, int int3) {
		for (int int4 = 0; int4 < this.roomsSize; ++int4) {
			RoomDef roomDef = this.rooms[int4];
			if (roomDef.isEmptyOutside() && roomDef.level == int3) {
				for (int int5 = 0; int5 < roomDef.rects.size(); ++int5) {
					RoomDef.RoomRect roomRect = (RoomDef.RoomRect)roomDef.rects.get(int5);
					if (roomRect.x <= int1 && roomRect.y <= int2 && int1 < roomRect.getX2() && int2 < roomRect.getY2()) {
						return roomDef;
					}
				}
			}
		}

		return null;
	}

	public int getNumRooms() {
		return this.roomsSize;
	}

	public void getRoomsIntersecting(int int1, int int2, int int3, int int4, ArrayList arrayList) {
		for (int int5 = 0; int5 < this.roomsSize; ++int5) {
			RoomDef roomDef = this.rooms[int5];
			if (!roomDef.isEmptyOutside() && !arrayList.contains(roomDef) && roomDef.intersects(int1, int2, int3, int4)) {
				arrayList.add(roomDef);
			}
		}
	}

	public void Dispose() {
		if (this.rooms != null) {
			Arrays.fill(this.rooms, (Object)null);
		}

		if (this.zones != null) {
			Arrays.fill(this.zones, (Object)null);
		}
	}
}
