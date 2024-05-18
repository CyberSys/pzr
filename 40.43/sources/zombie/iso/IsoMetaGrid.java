package zombie.iso;

import java.awt.Rectangle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapGroups;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.characters.Faction;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.stash.StashSystem;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.util.SharedStrings;


public final class IsoMetaGrid {
	public int minX = 10000000;
	public int minY = 10000000;
	public int maxX = -10000000;
	public int maxY = -10000000;
	private final ArrayList tempRooms = new ArrayList();
	static Rectangle a = new Rectangle();
	static Rectangle b = new Rectangle();
	public ArrayList Zones = new ArrayList();
	public ArrayList Buildings = new ArrayList();
	private final ArrayList tempZones1 = new ArrayList();
	private final ArrayList tempZones2 = new ArrayList();
	public ArrayList VehiclesZones = new ArrayList();
	public IsoMetaCell[][] Grid;
	private int width;
	private int height;
	private SharedStrings sharedStrings = new SharedStrings();
	private final IsoMetaGrid.MetaGridLoaderThread[] threads = new IsoMetaGrid.MetaGridLoaderThread[8];
	private long createStartTime;
	private static final int NUM_LOADER_THREADS = 8;
	ArrayList HighZombieList = new ArrayList();
	static ArrayList roomChoices = new ArrayList(50);

	public int getMinX() {
		return this.minX;
	}

	public int getMinY() {
		return this.minY;
	}

	public int getMaxX() {
		return this.maxX;
	}

	public int getMaxY() {
		return this.maxY;
	}

	public IsoMetaGrid.Zone getZoneAt(int int1, int int2, int int3) {
		IsoMetaChunk metaChunk = this.getChunkDataFromTile(int1, int2);
		return metaChunk != null ? metaChunk.getZoneAt(int1, int2, int3) : null;
	}

	public ArrayList getZonesAt(int int1, int int2, int int3) {
		ArrayList arrayList = new ArrayList();
		IsoMetaChunk metaChunk = this.getChunkDataFromTile(int1, int2);
		return metaChunk != null ? metaChunk.getZonesAt(int1, int2, int3, arrayList) : arrayList;
	}

	public ArrayList getZonesIntersecting(int int1, int int2, int int3, int int4, int int5) {
		ArrayList arrayList = new ArrayList();
		return this.getZonesIntersecting(int1, int2, int3, int4, int5, arrayList);
	}

	public ArrayList getZonesIntersecting(int int1, int int2, int int3, int int4, int int5, ArrayList arrayList) {
		for (int int6 = int2 / 300; int6 <= (int2 + this.height) / 300; ++int6) {
			for (int int7 = int1 / 300; int7 <= (int1 + this.width) / 300; ++int7) {
				if (int7 >= this.minX && int7 <= this.maxX && int6 >= this.minY && int6 <= this.maxY && this.Grid[int7 - this.minX][int6 - this.minY] != null) {
					this.Grid[int7 - this.minX][int6 - this.minY].getZonesIntersecting(int1, int2, int3, int4, int5, arrayList);
				}
			}
		}

		return arrayList;
	}

	public RoomDef getRoomAt(int int1, int int2, int int3) {
		IsoMetaChunk metaChunk = this.getChunkDataFromTile(int1, int2);
		return metaChunk != null ? metaChunk.getRoomAt(int1, int2, int3) : null;
	}

	public RoomDef getEmptyOutsideAt(int int1, int int2, int int3) {
		IsoMetaChunk metaChunk = this.getChunkDataFromTile(int1, int2);
		return metaChunk != null ? metaChunk.getEmptyOutsideAt(int1, int2, int3) : null;
	}

	public int countRoomsIntersecting(int int1, int int2, int int3, int int4) {
		this.tempRooms.clear();
		for (int int5 = int2 / 300; int5 <= (int2 + this.height) / 300; ++int5) {
			for (int int6 = int1 / 300; int6 <= (int1 + this.width) / 300; ++int6) {
				if (int6 >= this.minX && int6 <= this.maxX && int5 >= this.minY && int5 <= this.maxY) {
					IsoMetaCell metaCell = this.Grid[int6 - this.minX][int5 - this.minY];
					if (metaCell != null) {
						metaCell.getRoomsIntersecting(int1, int2, int3, int4, this.tempRooms);
					}
				}
			}
		}

		return this.tempRooms.size();
	}

	public int countNearbyBuildingsRooms(IsoPlayer player) {
		int int1 = (int)player.getX() - 20;
		int int2 = (int)player.getY() - 20;
		byte byte1 = 40;
		byte byte2 = 40;
		int int3 = this.countRoomsIntersecting(int1, int2, byte1, byte2);
		return int3;
	}

	private boolean isInside(IsoMetaGrid.Zone zone, BuildingDef buildingDef) {
		a.x = zone.x;
		a.y = zone.y;
		a.width = zone.w;
		a.height = zone.h;
		b.x = buildingDef.x;
		b.y = buildingDef.y;
		b.width = buildingDef.getW();
		b.height = buildingDef.getH();
		return a.contains(b);
	}

	private boolean isAdjacent(IsoMetaGrid.Zone zone, IsoMetaGrid.Zone zone2) {
		if (zone == zone2) {
			return false;
		} else {
			a.x = zone.x;
			a.y = zone.y;
			a.width = zone.w;
			a.height = zone.h;
			b.x = zone2.x;
			b.y = zone2.y;
			b.width = zone2.w;
			b.height = zone2.h;
			--a.x;
			--a.y;
			Rectangle rectangle = a;
			rectangle.width += 2;
			rectangle = a;
			rectangle.height += 2;
			--b.x;
			--b.y;
			rectangle = b;
			rectangle.width += 2;
			rectangle = b;
			rectangle.height += 2;
			return a.intersects(b);
		}
	}

	public IsoMetaGrid.Zone registerZone(String string, String string2, int int1, int int2, int int3, int int4, int int5) {
		string = this.sharedStrings.get(string);
		string2 = this.sharedStrings.get(string2);
		IsoMetaGrid.Zone zone = new IsoMetaGrid.Zone(string, string2, int1, int2, int3, int4, int5);
		if (int1 >= this.minX * 300 - 100 && int2 >= this.minY * 300 - 100 && int1 + int4 <= (this.maxX + 1) * 300 + 100 && int2 + int5 <= (this.maxY + 1) * 300 + 100 && int3 >= 0 && int3 < 8 && int4 <= 600 && int5 <= 600) {
			this.addZone(zone);
			return zone;
		} else {
			DebugLog.log("ERROR: not adding suspicious zone \"" + string + "\" \"" + string2 + "\" " + int1 + "," + int2 + "," + int3 + " " + int4 + "x" + int5);
			return zone;
		}
	}

	public IsoMetaGrid.Zone registerZoneNoOverlap(String string, String string2, int int1, int int2, int int3, int int4, int int5) {
		if (int1 >= this.minX * 300 - 100 && int2 >= this.minY * 300 - 100 && int1 + int4 <= (this.maxX + 1) * 300 + 100 && int2 + int5 <= (this.maxY + 1) * 300 + 100 && int3 >= 0 && int3 < 8 && int4 <= 600 && int5 <= 600) {
			this.tempZones1.clear();
			ArrayList arrayList = this.getZonesIntersecting(int1, int2, int3, int4, int5, this.tempZones1);
			ArrayList arrayList2 = this.tempZones2;
			for (int int6 = 0; int6 < arrayList.size(); ++int6) {
				IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)arrayList.get(int6);
				if (zone.difference(int1, int2, int3, int4, int5, arrayList2)) {
					if (Core.bDebug) {
						if (arrayList2.isEmpty()) {
							DebugLog.log("zone: removed overlapped zone x,y,z=" + zone.x + "," + zone.y + "," + zone.z);
						} else {
							DebugLog.log("zone: cut zone into " + arrayList2.size() + " pieces x,y,z=" + zone.x + "," + zone.y + "," + zone.z);
						}
					}

					this.removeZone(zone);
					for (int int7 = 0; int7 < arrayList2.size(); ++int7) {
						this.addZone((IsoMetaGrid.Zone)arrayList2.get(int7));
					}
				}
			}

			return this.registerZone(string, string2, int1, int2, int3, int4, int5);
		} else {
			DebugLog.log("ERROR: not adding suspicious zone \"" + string + "\" \"" + string2 + "\" " + int1 + "," + int2 + "," + int3 + " " + int4 + "x" + int5);
			return null;
		}
	}

	private void addZone(IsoMetaGrid.Zone zone) {
		this.Zones.add(zone);
		for (int int1 = zone.y / 300; int1 <= (zone.y + zone.h) / 300; ++int1) {
			for (int int2 = zone.x / 300; int2 <= (zone.x + zone.w) / 300; ++int2) {
				if (int2 >= this.minX && int2 <= this.maxX && int1 >= this.minY && int1 <= this.maxY && this.Grid[int2 - this.minX][int1 - this.minY] != null) {
					this.Grid[int2 - this.minX][int1 - this.minY].addZone(zone, int2 * 300, int1 * 300);
				}
			}
		}
	}

	public void removeZone(IsoMetaGrid.Zone zone) {
		this.Zones.remove(zone);
		for (int int1 = zone.y / 300; int1 <= (zone.y + zone.h) / 300; ++int1) {
			for (int int2 = zone.x / 300; int2 <= (zone.x + zone.w) / 300; ++int2) {
				if (int2 >= this.minX && int2 <= this.maxX && int1 >= this.minY && int1 <= this.maxY && this.Grid[int2 - this.minX][int1 - this.minY] != null) {
					this.Grid[int2 - this.minX][int1 - this.minY].removeZone(zone);
				}
			}
		}
	}

	public void removeZonesForCell(int int1, int int2) {
		IsoMetaCell metaCell = this.getCellData(int1, int2);
		if (metaCell != null) {
			ArrayList arrayList = this.tempZones1;
			arrayList.clear();
			int int3;
			for (int3 = 0; int3 < 900; ++int3) {
				metaCell.ChunkMap[int3].getZonesIntersecting(int1 * 300, int2 * 300, 0, 300, 300, arrayList);
			}

			for (int3 = 0; int3 < arrayList.size(); ++int3) {
				IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)arrayList.get(int3);
				ArrayList arrayList2 = this.tempZones2;
				if (zone.difference(int1 * 300, int2 * 300, 0, 300, 300, arrayList2)) {
					if (Core.bDebug) {
						if (arrayList2.isEmpty()) {
							DebugLog.log("zone: removed overlapped zone x,y,z=" + zone.x + "," + zone.y + "," + zone.z);
						} else {
							DebugLog.log("zone: cut zone into " + arrayList2.size() + " pieces x,y,z=" + zone.x + "," + zone.y + "," + zone.z);
						}
					}

					this.removeZone(zone);
					for (int int4 = 0; int4 < arrayList2.size(); ++int4) {
						this.addZone((IsoMetaGrid.Zone)arrayList2.get(int4));
					}
				}
			}

			if (!metaCell.vehicleZones.isEmpty()) {
				metaCell.vehicleZones.clear();
			}
		}
	}

	public void removeZonesForLotDirectory(String string) {
		if (!this.Zones.isEmpty()) {
			File file = new File(ZomboidFileSystem.instance.getString("media/maps/" + string + "/"));
			if (file.isDirectory()) {
				ChooseGameInfo.Map map = ChooseGameInfo.getMapDetails(string);
				if (map != null) {
					String[] stringArray = file.list();
					if (stringArray != null) {
						for (int int1 = 0; int1 < stringArray.length; ++int1) {
							String string2 = stringArray[int1];
							if (string2.endsWith(".lotheader")) {
								String[] stringArray2 = string2.split("_");
								stringArray2[1] = stringArray2[1].replace(".lotheader", "");
								int int2 = Integer.parseInt(stringArray2[0].trim());
								int int3 = Integer.parseInt(stringArray2[1].trim());
								this.removeZonesForCell(int2, int3);
							}
						}
					}
				}
			}
		}
	}

	public void processZones() {
		int int1 = 0;
		for (int int2 = this.minX; int2 <= this.maxX; ++int2) {
			for (int int3 = this.minY; int3 <= this.maxY; ++int3) {
				if (this.Grid[int2 - this.minX][int3 - this.minY] != null) {
					for (int int4 = 0; int4 < 30; ++int4) {
						for (int int5 = 0; int5 < 30; ++int5) {
							int1 = Math.max(int1, this.Grid[int2 - this.minX][int3 - this.minY].getChunk(int5, int4).numZones());
						}
					}
				}
			}
		}

		DebugLog.log("Max #ZONES on one chunk is " + int1);
	}

	public IsoMetaGrid.Zone registerVehiclesZone(String string, String string2, int int1, int int2, int int3, int int4, int int5, KahluaTable kahluaTable) {
		if (!string2.equals("Vehicle") && !string2.equals("ParkingStall")) {
			return null;
		} else {
			string = this.sharedStrings.get(string);
			string2 = this.sharedStrings.get(string2);
			IsoMetaGrid.VehicleZone vehicleZone = new IsoMetaGrid.VehicleZone(string, string2, int1, int2, int3, int4, int5, kahluaTable);
			this.VehiclesZones.add(vehicleZone);
			int int6 = (int)Math.ceil((double)((float)(vehicleZone.x + vehicleZone.w) / 300.0F));
			int int7 = (int)Math.ceil((double)((float)(vehicleZone.y + vehicleZone.h) / 300.0F));
			for (int int8 = vehicleZone.y / 300; int8 < int7; ++int8) {
				for (int int9 = vehicleZone.x / 300; int9 < int6; ++int9) {
					if (int9 >= this.minX && int9 <= this.maxX && int8 >= this.minY && int8 <= this.maxY && this.Grid[int9 - this.minX][int8 - this.minY] != null) {
						this.Grid[int9 - this.minX][int8 - this.minY].vehicleZones.add(vehicleZone);
					}
				}
			}

			return vehicleZone;
		}
	}

	public void checkVehiclesZones() {
		int int1 = 0;
		while (int1 < this.VehiclesZones.size()) {
			boolean boolean1 = true;
			for (int int2 = 0; int2 < int1; ++int2) {
				IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)this.VehiclesZones.get(int1);
				IsoMetaGrid.Zone zone2 = (IsoMetaGrid.Zone)this.VehiclesZones.get(int2);
				if (zone.getX() == zone2.getX() && zone.getY() == zone2.getY() && zone.h == zone2.h && zone.w == zone2.w) {
					boolean1 = false;
					DebugLog.log("checkVehiclesZones: ERROR! Zone \'" + zone.name + "\':\'" + zone.type + "\' (" + zone.x + ", " + zone.y + ") dublicate with Zone \'" + zone2.name + "\':\'" + zone2.type + "\' (" + zone2.x + ", " + zone2.y + ")");
					break;
				}
			}

			if (boolean1) {
				++int1;
			} else {
				this.VehiclesZones.remove(int1);
			}
		}
	}

	public void save(ByteBuffer byteBuffer) {
		this.savePart(byteBuffer, 0);
		this.savePart(byteBuffer, 1);
	}

	public void savePart(ByteBuffer byteBuffer, int int1) {
		int int2;
		if (int1 == 0) {
			byteBuffer.put((byte)77);
			byteBuffer.put((byte)69);
			byteBuffer.put((byte)84);
			byteBuffer.put((byte)65);
			byteBuffer.putInt(143);
			byteBuffer.putInt(this.Grid.length);
			byteBuffer.putInt(this.Grid[0].length);
			for (int2 = 0; int2 < this.Grid.length; ++int2) {
				for (int int3 = 0; int3 < this.Grid[0].length; ++int3) {
					IsoMetaCell metaCell = this.Grid[int2][int3];
					int int4 = 0;
					if (metaCell.info != null) {
						int4 = metaCell.info.Rooms.values().size();
					}

					byteBuffer.putInt(int4);
					Iterator iterator;
					if (metaCell.info != null) {
						iterator = metaCell.info.Rooms.entrySet().iterator();
						while (iterator.hasNext()) {
							Entry entry = (Entry)iterator.next();
							byteBuffer.putInt((Integer)entry.getKey());
							byteBuffer.put((byte)(((RoomDef)entry.getValue()).bExplored ? 1 : 0));
							byteBuffer.put((byte)(((RoomDef)entry.getValue()).bLightsActive ? 1 : 0));
						}
					}

					if (metaCell.info != null) {
						byteBuffer.putInt(metaCell.info.Buildings.size());
					} else {
						byteBuffer.putInt(0);
					}

					if (metaCell.info != null) {
						iterator = metaCell.info.Buildings.iterator();
						while (iterator.hasNext()) {
							BuildingDef buildingDef = (BuildingDef)iterator.next();
							byteBuffer.put((byte)(buildingDef.bAlarmed ? 1 : 0));
							byteBuffer.putInt(buildingDef.getKeyId());
							byteBuffer.put((byte)(buildingDef.seen ? 1 : 0));
							byteBuffer.put((byte)(buildingDef.isHasBeenVisited() ? 1 : 0));
							byteBuffer.putInt(buildingDef.lootRespawnHour);
						}
					}
				}
			}
		} else {
			byteBuffer.putInt(SafeHouse.getSafehouseList().size());
			for (int2 = 0; int2 < SafeHouse.getSafehouseList().size(); ++int2) {
				((SafeHouse)SafeHouse.getSafehouseList().get(int2)).save(byteBuffer);
			}

			byteBuffer.putInt(NonPvpZone.getAllZones().size());
			for (int2 = 0; int2 < NonPvpZone.getAllZones().size(); ++int2) {
				((NonPvpZone)NonPvpZone.getAllZones().get(int2)).save(byteBuffer);
			}

			byteBuffer.putInt(Faction.getFactions().size());
			for (int2 = 0; int2 < Faction.getFactions().size(); ++int2) {
				((Faction)Faction.getFactions().get(int2)).save(byteBuffer);
			}

			if (!GameClient.bClient) {
				StashSystem.save(byteBuffer);
			}
		}
	}

	public void load(ByteBuffer byteBuffer) {
		byteBuffer.mark();
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		byte byte3 = byteBuffer.get();
		byte byte4 = byteBuffer.get();
		int int1;
		if (byte1 == 77 && byte2 == 69 && byte3 == 84 && byte4 == 65) {
			int1 = byteBuffer.getInt();
		} else {
			int1 = 33;
			byteBuffer.reset();
		}

		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		if (int2 != this.Grid.length || int3 != this.Grid[0].length) {
			DebugLog.log("map_meta.bin world size (" + int2 + "x" + int3 + ") does not match the current map size (" + this.Grid.length + "x" + this.Grid[0].length + ")");
			int2 = Math.min(int2, this.Grid.length);
			int3 = Math.min(int3, this.Grid[0].length);
		}

		int int4 = 0;
		int int5 = 0;
		int int6;
		int int7;
		IsoMetaCell metaCell;
		int int8;
		int int9;
		for (int6 = 0; int6 < int2; ++int6) {
			for (int7 = 0; int7 < int3; ++int7) {
				metaCell = this.Grid[int6][int7];
				int8 = byteBuffer.getInt();
				int int10;
				boolean boolean1;
				for (int9 = 0; int9 < int8; ++int9) {
					int10 = byteBuffer.getInt();
					boolean1 = byteBuffer.get() == 1;
					boolean boolean2 = false;
					if (int1 >= 34) {
						boolean2 = byteBuffer.get() == 1;
					} else {
						boolean2 = Rand.Next(2) == 0;
					}

					if (metaCell.info != null) {
						RoomDef roomDef = (RoomDef)metaCell.info.Rooms.get(int10);
						if (roomDef != null) {
							roomDef.setExplored(boolean1);
							roomDef.bLightsActive = boolean2;
						} else {
							DebugLog.log("ERROR: invalid room ID #" + int10 + " in cell " + int6 + "," + int7 + " while reading map_meta.bin");
						}
					}
				}

				int9 = byteBuffer.getInt();
				int4 += int9;
				for (int10 = 0; int10 < int9; ++int10) {
					boolean1 = byteBuffer.get() == 1;
					int int11 = int1 >= 57 ? byteBuffer.getInt() : -1;
					boolean boolean3 = int1 >= 74 ? byteBuffer.get() == 1 : false;
					boolean boolean4 = int1 >= 107 ? byteBuffer.get() == 1 : false;
					if (int1 >= 111 && int1 < 121) {
						byteBuffer.getInt();
					} else {
						boolean boolean5 = false;
					}

					int int12 = int1 >= 125 ? byteBuffer.getInt() : 0;
					if (metaCell.info != null && int10 < metaCell.info.Buildings.size()) {
						BuildingDef buildingDef = (BuildingDef)metaCell.info.Buildings.get(int10);
						if (boolean1) {
							++int5;
						}

						buildingDef.bAlarmed = boolean1;
						buildingDef.setKeyId(int11);
						if (int1 >= 74) {
							buildingDef.seen = boolean3;
						}

						buildingDef.hasBeenVisited = boolean4;
						buildingDef.lootRespawnHour = int12;
					}
				}
			}
		}

		if (int1 <= 112) {
			this.Zones = new ArrayList();
			for (int6 = 0; int6 < this.height; ++int6) {
				for (int7 = 0; int7 < this.width; ++int7) {
					metaCell = this.Grid[int7][int6];
					if (metaCell != null) {
						for (int8 = 0; int8 < 30; ++int8) {
							for (int9 = 0; int9 < 30; ++int9) {
								metaCell.ChunkMap[int9 + int8 * 30].clearZones();
							}
						}
					}
				}
			}

			this.loadZone(byteBuffer, int1);
		}

		if (int1 >= 66) {
			SafeHouse.clearSafehouseList();
			int6 = byteBuffer.getInt();
			for (int7 = 0; int7 < int6; ++int7) {
				SafeHouse.load(byteBuffer, int1);
			}
		}

		if (int1 >= 101) {
			NonPvpZone.nonPvpZoneList = new ArrayList();
			int6 = byteBuffer.getInt();
			for (int7 = 0; int7 < int6; ++int7) {
				NonPvpZone nonPvpZone = new NonPvpZone();
				nonPvpZone.load(byteBuffer, int1);
				NonPvpZone.getAllZones().add(nonPvpZone);
			}
		}

		if (int1 >= 102) {
			Faction.factions = new ArrayList();
			int6 = byteBuffer.getInt();
			for (int7 = 0; int7 < int6; ++int7) {
				Faction faction = new Faction();
				faction.load(byteBuffer, int1);
				Faction.getFactions().add(faction);
			}
		}

		if (int1 >= 107 && !GameClient.bClient) {
			StashSystem.load(byteBuffer, int1);
		}
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public IsoMetaCell getCellData(int int1, int int2) {
		return int1 - this.minX >= 0 && int2 - this.minY >= 0 && int1 - this.minX < this.width && int2 - this.minY < this.height ? this.Grid[int1 - this.minX][int2 - this.minY] : null;
	}

	public IsoMetaCell getCellDataAbs(int int1, int int2) {
		return this.Grid[int1][int2];
	}

	public IsoMetaCell getCurrentCellData() {
		int int1 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX;
		int int2 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY;
		float float1 = (float)int1;
		float float2 = (float)int2;
		float1 /= 30.0F;
		float2 /= 30.0F;
		if (float1 < 0.0F) {
			float1 = (float)((int)float1 - 1);
		}

		if (float2 < 0.0F) {
			float2 = (float)((int)float2 - 1);
		}

		int1 = (int)float1;
		int2 = (int)float2;
		return this.getCellData(int1, int2);
	}

	public IsoMetaCell getMetaGridFromTile(int int1, int int2) {
		int int3 = int1 / 300;
		int int4 = int2 / 300;
		return this.getCellData(int3, int4);
	}

	public IsoMetaChunk getCurrentChunkData() {
		int int1 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX;
		int int2 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY;
		float float1 = (float)int1;
		float float2 = (float)int2;
		float1 /= 30.0F;
		float2 /= 30.0F;
		if (float1 < 0.0F) {
			float1 = (float)((int)float1 - 1);
		}

		if (float2 < 0.0F) {
			float2 = (float)((int)float2 - 1);
		}

		int1 = (int)float1;
		int2 = (int)float2;
		return this.getCellData(int1, int2).getChunk(IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX - int1 * 30, IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY - int2 * 30);
	}

	public IsoMetaChunk getChunkData(int int1, int int2) {
		float float1 = (float)int1;
		float float2 = (float)int2;
		float1 /= 30.0F;
		float2 /= 30.0F;
		if (float1 < 0.0F) {
			float1 = (float)((int)float1 - 1);
		}

		if (float2 < 0.0F) {
			float2 = (float)((int)float2 - 1);
		}

		int int3 = (int)float1;
		int int4 = (int)float2;
		IsoMetaCell metaCell = this.getCellData(int3, int4);
		return metaCell == null ? null : metaCell.getChunk(int1 - int3 * 30, int2 - int4 * 30);
	}

	public IsoMetaChunk getChunkDataFromTile(int int1, int int2) {
		int int3 = int1 / 10;
		int int4 = int2 / 10;
		int3 -= this.minX * 30;
		int4 -= this.minY * 30;
		int int5 = int3 / 30;
		int int6 = int4 / 30;
		int3 += this.minX * 30;
		int4 += this.minY * 30;
		int5 += this.minX;
		int6 += this.minY;
		IsoMetaCell metaCell = this.getCellData(int5, int6);
		return metaCell == null ? null : metaCell.getChunk(int3 - int5 * 30, int4 - int6 * 30);
	}

	public boolean isValidSquare(int int1, int int2) {
		if (int1 < this.minX * 300) {
			return false;
		} else if (int1 >= (this.maxX + 1) * 300) {
			return false;
		} else if (int2 < this.minY * 300) {
			return false;
		} else {
			return int2 < (this.maxY + 1) * 300;
		}
	}

	public boolean isValidChunk(int int1, int int2) {
		int1 *= 10;
		int2 *= 10;
		if (int1 < this.minX * 300) {
			return false;
		} else if (int1 >= (this.maxX + 1) * 300) {
			return false;
		} else if (int2 < this.minY * 300) {
			return false;
		} else if (int2 >= (this.maxY + 1) * 300) {
			return false;
		} else {
			return this.Grid[int1 / 300 - this.minX][int2 / 300 - this.minY].info != null;
		}
	}

	public void Create() {
		this.CreateStep1();
		this.CreateStep2();
	}

	public void CreateStep1() {
		this.minX = 10000000;
		this.minY = 10000000;
		this.maxX = -10000000;
		this.maxY = -10000000;
		IsoLot.InfoHeaders.clear();
		IsoLot.InfoHeaderNames.clear();
		IsoLot.InfoFileNames.clear();
		long long1 = System.currentTimeMillis();
		DebugLog.log("IsoMetaGrid.Create: begin scanning directories");
		ArrayList arrayList = this.getLotDirectories();
		DebugLog.log("Looking in these map folders:");
		Iterator iterator = arrayList.iterator();
		String string;
		while (iterator.hasNext()) {
			string = (String)iterator.next();
			string = ZomboidFileSystem.instance.getString("media/maps/" + string + "/");
			DebugLog.log("	" + (new File(string)).getAbsolutePath());
		}

		DebugLog.log("<End of map-folders list>");
		iterator = arrayList.iterator();
		while (true) {
			File file;
			do {
				if (!iterator.hasNext()) {
					if (this.maxX >= this.minX && this.maxY >= this.minY) {
						this.Grid = new IsoMetaCell[this.maxX - this.minX + 1][this.maxY - this.minY + 1];
						this.width = this.maxX - this.minX + 1;
						this.height = this.maxY - this.minY + 1;
						long long2 = System.currentTimeMillis() - long1;
						DebugLog.log("IsoMetaGrid.Create: finished scanning directories in " + (float)long2 / 1000.0F + " seconds");
						DebugLog.log("IsoMetaGrid.Create: begin loading");
						this.createStartTime = System.currentTimeMillis();
						for (int int1 = 0; int1 < 8; ++int1) {
							IsoMetaGrid.MetaGridLoaderThread metaGridLoaderThread = new IsoMetaGrid.MetaGridLoaderThread(this.minY + int1);
							metaGridLoaderThread.setDaemon(true);
							metaGridLoaderThread.setName("MetaGridLoaderThread" + int1);
							metaGridLoaderThread.start();
							this.threads[int1] = metaGridLoaderThread;
						}

						return;
					}

					throw new IllegalStateException("Failed to find any .lotheader files");
				}

				string = (String)iterator.next();
				file = new File(ZomboidFileSystem.instance.getString("media/maps/" + string + "/"));
			}	 while (!file.isDirectory());

			ChooseGameInfo.Map map = ChooseGameInfo.getMapDetails(string);
			String[] stringArray = file.list();
			for (int int2 = 0; int2 < stringArray.length; ++int2) {
				if (!IsoLot.InfoFileNames.containsKey(stringArray[int2])) {
					if (stringArray[int2].endsWith(".lotheader")) {
						String[] stringArray2 = stringArray[int2].split("_");
						stringArray2[1] = stringArray2[1].replace(".lotheader", "");
						int int3 = Integer.parseInt(stringArray2[0].trim());
						int int4 = Integer.parseInt(stringArray2[1].trim());
						if (int3 < this.minX) {
							this.minX = int3;
						}

						if (int4 < this.minY) {
							this.minY = int4;
						}

						if (int3 > this.maxX) {
							this.maxX = int3;
						}

						if (int4 > this.maxY) {
							this.maxY = int4;
						}

						IsoLot.InfoFileNames.put(stringArray[int2], file.getAbsolutePath() + File.separator + stringArray[int2]);
						LotHeader lotHeader = new LotHeader();
						lotHeader.bFixed2x = map.isFixed2x();
						IsoLot.InfoHeaders.put(stringArray[int2], lotHeader);
						IsoLot.InfoHeaderNames.add(stringArray[int2]);
					} else if (stringArray[int2].endsWith(".lotpack")) {
						IsoLot.InfoFileNames.put(stringArray[int2], file.getAbsolutePath() + File.separator + stringArray[int2]);
					} else if (stringArray[int2].startsWith("chunkdata_")) {
						IsoLot.InfoFileNames.put(stringArray[int2], file.getAbsolutePath() + File.separator + stringArray[int2]);
					}
				}
			}
		}
	}

	public void CreateStep2() {
		boolean boolean1 = true;
		while (true) {
			int int1;
			while (boolean1) {
				boolean1 = false;
				for (int1 = 0; int1 < 8; ++int1) {
					if (this.threads[int1].isAlive()) {
						boolean1 = true;
						try {
							Thread.sleep(100L);
						} catch (InterruptedException interruptedException) {
						}

						break;
					}
				}
			}

			for (int1 = 0; int1 < 8; ++int1) {
				this.threads[int1].postLoad();
				this.threads[int1] = null;
			}

			for (int1 = 0; int1 < this.Buildings.size(); ++int1) {
				BuildingDef buildingDef = (BuildingDef)this.Buildings.get(int1);
				if (!Core.GameMode.equals("LastStand") && buildingDef.rooms.size() > 2) {
					int int2 = 11;
					if (SandboxOptions.instance.getElecShutModifier() > -1 && GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier()) {
						int2 = 9;
					}

					if (SandboxOptions.instance.Alarm.getValue() == 1) {
						int2 = -1;
					} else if (SandboxOptions.instance.Alarm.getValue() == 2) {
						int2 += 5;
					} else if (SandboxOptions.instance.Alarm.getValue() == 3) {
						int2 += 3;
					} else if (SandboxOptions.instance.Alarm.getValue() == 5) {
						int2 -= 3;
					} else if (SandboxOptions.instance.Alarm.getValue() == 6) {
						int2 -= 5;
					}

					if (int2 > -1) {
						buildingDef.bAlarmed = Rand.Next(int2) == 0;
					}
				}
			}

			long long1 = System.currentTimeMillis() - this.createStartTime;
			DebugLog.log("IsoMetaGrid.Create: finished loading in " + (float)long1 / 1000.0F + " seconds");
			return;
		}
	}

	public void Dispose() {
		this.Grid = (IsoMetaCell[][])null;
	}

	public Vector2 getRandomIndoorCoord() {
		return null;
	}

	public RoomDef getRandomRoomBetweenRange(float float1, float float2, float float3, float float4) {
		RoomDef roomDef = null;
		float float5 = 0.0F;
		roomChoices.clear();
		LotHeader lotHeader = null;
		for (int int1 = 0; int1 < IsoLot.InfoHeaderNames.size(); ++int1) {
			lotHeader = (LotHeader)IsoLot.InfoHeaders.get(IsoLot.InfoHeaderNames.get(int1));
			if (!lotHeader.RoomList.isEmpty()) {
				for (int int2 = 0; int2 < lotHeader.RoomList.size(); ++int2) {
					roomDef = (RoomDef)lotHeader.RoomList.get(int2);
					float5 = IsoUtils.DistanceManhatten(float1, float2, (float)roomDef.x, (float)roomDef.y);
					if (float5 > float3 && float5 < float4) {
						roomChoices.add(roomDef);
					}
				}
			}
		}

		if (!roomChoices.isEmpty()) {
			return (RoomDef)roomChoices.get(Rand.Next(roomChoices.size()));
		} else {
			return null;
		}
	}

	public RoomDef getRandomRoomNotInRange(float float1, float float2, int int1) {
		RoomDef roomDef = null;
		do {
			LotHeader lotHeader = null;
			do {
				lotHeader = (LotHeader)IsoLot.InfoHeaders.get(IsoLot.InfoHeaderNames.get(Rand.Next(IsoLot.InfoHeaderNames.size())));
			}	 while (lotHeader.RoomList.isEmpty());

			roomDef = (RoomDef)lotHeader.RoomList.get(Rand.Next(lotHeader.RoomList.size()));
		} while (roomDef == null || IsoUtils.DistanceManhatten(float1, float2, (float)roomDef.x, (float)roomDef.y) < (float)int1);

		return roomDef;
	}

	public void save() {
		try {
			if (SliceY.SliceBuffer == null) {
				SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
			}

			SliceY.SliceBuffer.rewind();
			File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_meta.bin");
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			this.save(SliceY.SliceBuffer);
			bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
			bufferedOutputStream.flush();
			bufferedOutputStream.close();
			if (SliceY.SliceBuffer == null) {
				SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
			}

			SliceY.SliceBuffer.rewind();
			File file2 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_zone.bin");
			FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
			BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(fileOutputStream2);
			this.saveZone(SliceY.SliceBuffer);
			bufferedOutputStream2.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
			bufferedOutputStream2.flush();
			bufferedOutputStream2.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void loadZone(ByteBuffer byteBuffer, int int1) {
		if (int1 == -1) {
			byte byte1 = byteBuffer.get();
			byte byte2 = byteBuffer.get();
			byte byte3 = byteBuffer.get();
			byte byte4 = byteBuffer.get();
			if (byte1 != 90 || byte2 != 79 || byte3 != 78 || byte4 != 69) {
				DebugLog.log("ERROR: expected \'ZONE\' at start of map_zone.bin");
				return;
			}

			int1 = byteBuffer.getInt();
		}

		int int2 = this.Zones.size();
		if (!GameServer.bServer && int1 >= 34 || GameServer.bServer && int1 >= 36) {
			this.Zones.clear();
			int int3;
			int int4;
			int int5;
			int int6;
			for (int5 = 0; int5 < this.height; ++int5) {
				for (int6 = 0; int6 < this.width; ++int6) {
					IsoMetaCell metaCell = this.Grid[int6][int5];
					if (metaCell != null) {
						for (int3 = 0; int3 < 30; ++int3) {
							for (int4 = 0; int4 < 30; ++int4) {
								metaCell.ChunkMap[int4 + int3 * 30].clearZones();
							}
						}
					}
				}
			}

			int int7;
			int int8;
			int int9;
			String string;
			if (int1 >= 141) {
				int5 = byteBuffer.getInt();
				HashMap hashMap = new HashMap();
				int int10;
				for (int10 = 0; int10 < int5; ++int10) {
					string = GameWindow.ReadStringUTF(byteBuffer);
					hashMap.put(int10, string);
				}

				int10 = byteBuffer.getInt();
				DebugLog.log("loading " + int10 + " zones from map_zone.bin");
				for (int3 = 0; int3 < int10; ++int3) {
					String string2 = (String)hashMap.get(Integer.valueOf(byteBuffer.getShort()));
					String string3 = (String)hashMap.get(Integer.valueOf(byteBuffer.getShort()));
					int7 = byteBuffer.getInt();
					int8 = byteBuffer.getInt();
					byte byte5 = byteBuffer.get();
					int int11 = byteBuffer.getInt();
					int9 = byteBuffer.getInt();
					int int12 = byteBuffer.getInt();
					IsoMetaGrid.Zone zone = this.registerZone(string2, string3, int7, int8, byte5, int11, int9);
					zone.hourLastSeen = int12;
					zone.haveConstruction = byteBuffer.get() == 1;
					zone.lastActionTimestamp = byteBuffer.getInt();
					zone.setOriginalName((String)hashMap.get(Integer.valueOf(byteBuffer.getShort())));
				}

				return;
			}

			int5 = byteBuffer.getInt();
			DebugLog.log("loading " + int5 + " zones from map_zone.bin");
			if (int1 <= 112 && int5 > int2 * 2) {
				DebugLog.log("ERROR: seems like too many zones in map_zone.bin");
				return;
			}

			for (int6 = 0; int6 < int5; ++int6) {
				String string4 = GameWindow.ReadString(byteBuffer);
				string = GameWindow.ReadString(byteBuffer);
				int4 = byteBuffer.getInt();
				int int13 = byteBuffer.getInt();
				int7 = byteBuffer.getInt();
				int8 = byteBuffer.getInt();
				int int14 = byteBuffer.getInt();
				if (int1 < 121) {
					byteBuffer.getInt();
				} else {
					boolean boolean1 = false;
				}

				int9 = int1 < 68 ? byteBuffer.getShort() : byteBuffer.getInt();
				IsoMetaGrid.Zone zone2 = this.registerZone(string4, string, int4, int13, int7, int8, int14);
				zone2.hourLastSeen = int9;
				if (int1 >= 35) {
					boolean boolean2 = byteBuffer.get() == 1;
					zone2.haveConstruction = boolean2;
				}

				if (int1 >= 41) {
					zone2.lastActionTimestamp = byteBuffer.getInt();
				}

				if (int1 >= 98) {
					zone2.setOriginalName(GameWindow.ReadString(byteBuffer));
				}

				if (int1 >= 110 && int1 < 121) {
					int int15 = byteBuffer.getInt();
				}
			}
		}
	}

	public void saveZone(ByteBuffer byteBuffer) {
		byteBuffer.put((byte)90);
		byteBuffer.put((byte)79);
		byteBuffer.put((byte)78);
		byteBuffer.put((byte)69);
		byteBuffer.putInt(143);
		HashSet hashSet = new HashSet();
		for (int int1 = 0; int1 < this.Zones.size(); ++int1) {
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)this.Zones.get(int1);
			hashSet.add(zone.getName());
			hashSet.add(zone.getOriginalName());
			hashSet.add(zone.getType());
		}

		ArrayList arrayList = new ArrayList(hashSet);
		HashMap hashMap = new HashMap();
		int int2;
		for (int2 = 0; int2 < arrayList.size(); ++int2) {
			hashMap.put(arrayList.get(int2), int2);
		}

		if (arrayList.size() > 32767) {
			throw new IllegalStateException("IsoMetaGrid.saveZone() string table is too large");
		} else {
			byteBuffer.putInt(arrayList.size());
			for (int2 = 0; int2 < arrayList.size(); ++int2) {
				GameWindow.WriteString(byteBuffer, (String)arrayList.get(int2));
			}

			byteBuffer.putInt(this.Zones.size());
			for (int2 = 0; int2 < this.Zones.size(); ++int2) {
				IsoMetaGrid.Zone zone2 = (IsoMetaGrid.Zone)this.Zones.get(int2);
				byteBuffer.putShort(((Integer)hashMap.get(zone2.getName())).shortValue());
				byteBuffer.putShort(((Integer)hashMap.get(zone2.getType())).shortValue());
				byteBuffer.putInt(zone2.x);
				byteBuffer.putInt(zone2.y);
				byteBuffer.put((byte)zone2.z);
				byteBuffer.putInt(zone2.w);
				byteBuffer.putInt(zone2.h);
				byteBuffer.putInt(zone2.hourLastSeen);
				byteBuffer.put((byte)(zone2.haveConstruction ? 1 : 0));
				byteBuffer.putInt(zone2.lastActionTimestamp);
				byteBuffer.putShort(((Integer)hashMap.get(zone2.getOriginalName())).shortValue());
			}

			hashSet.clear();
			arrayList.clear();
			hashMap.clear();
		}
	}

	private void getLotDirectories(String string, ArrayList arrayList) {
		if (!arrayList.contains(string)) {
			ChooseGameInfo.Map map = ChooseGameInfo.getMapDetails(string);
			if (map != null) {
				arrayList.add(string);
				Iterator iterator = map.getLotDirectories().iterator();
				while (iterator.hasNext()) {
					String string2 = (String)iterator.next();
					this.getLotDirectories(string2, arrayList);
				}
			}
		}
	}

	public ArrayList getLotDirectories() {
		if (GameClient.bClient) {
			Core.GameMap = GameClient.GameMap;
		}

		if (GameServer.bServer) {
			Core.GameMap = GameServer.GameMap;
		}

		if (Core.GameMap.equals("DEFAULT")) {
			MapGroups mapGroups = new MapGroups();
			mapGroups.createGroups();
			if (mapGroups.getNumberOfGroups() != 1) {
				throw new RuntimeException("GameMap is DEFAULT but there are multiple worlds to choose from");
			}

			mapGroups.setWorld(0);
		}

		ArrayList arrayList = new ArrayList();
		if (Core.GameMap.contains(";")) {
			String[] stringArray = Core.GameMap.split(";");
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				String string = stringArray[int1].trim();
				if (!string.isEmpty() && !arrayList.contains(string)) {
					arrayList.add(string);
				}
			}
		} else {
			this.getLotDirectories(Core.GameMap, arrayList);
		}

		return arrayList;
	}

	private final class MetaGridLoaderThread extends Thread {
		int wY;
		final SharedStrings sharedStrings = new SharedStrings();
		final ArrayList Buildings = new ArrayList();
		final ArrayList tempRooms = new ArrayList();

		MetaGridLoaderThread(int int1) {
			this.wY = int1;
		}

		public void run() {
			try {
				this.runInner();
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}

		void runInner() {
			for (int int1 = this.wY; int1 <= IsoMetaGrid.this.maxY; int1 += 8) {
				for (int int2 = IsoMetaGrid.this.minX; int2 <= IsoMetaGrid.this.maxX; ++int2) {
					this.loadCell(int2, int1);
				}
			}
		}

		void loadCell(int int1, int int2) {
			IsoMetaCell metaCell = new IsoMetaCell(int1, int2);
			IsoMetaGrid.this.Grid[int1 - IsoMetaGrid.this.minX][int2 - IsoMetaGrid.this.minY] = metaCell;
			String string = int1 + "_" + int2 + ".lotheader";
			if (IsoLot.InfoFileNames.containsKey(string)) {
				LotHeader lotHeader = (LotHeader)IsoLot.InfoHeaders.get(string);
				if (lotHeader != null) {
					File file = new File((String)IsoLot.InfoFileNames.get(string));
					if (file.exists()) {
						metaCell.info = lotHeader;
						try {
							RandomAccessFile randomAccessFile = new RandomAccessFile(file.getAbsolutePath(), "r");
							Throwable throwable = null;
							try {
								lotHeader.version = IsoLot.readInt(randomAccessFile);
								int int3 = IsoLot.readInt(randomAccessFile);
								int int4;
								for (int4 = 0; int4 < int3; ++int4) {
									String string2 = IsoLot.readString(randomAccessFile);
									lotHeader.tilesUsed.add(this.sharedStrings.get(string2.trim()));
								}

								randomAccessFile.read();
								lotHeader.width = IsoLot.readInt(randomAccessFile);
								lotHeader.height = IsoLot.readInt(randomAccessFile);
								lotHeader.levels = IsoLot.readInt(randomAccessFile);
								int4 = IsoLot.readInt(randomAccessFile);
								int int5;
								int int6;
								int int7;
								for (int7 = 0; int7 < int4; ++int7) {
									String string3 = IsoLot.readString(randomAccessFile);
									RoomDef roomDef = new RoomDef(int7, this.sharedStrings.get(string3));
									roomDef.level = IsoLot.readInt(randomAccessFile);
									int5 = IsoLot.readInt(randomAccessFile);
									for (int6 = 0; int6 < int5; ++int6) {
										RoomDef.RoomRect roomRect = new RoomDef.RoomRect(IsoLot.readInt(randomAccessFile) + int1 * 300, IsoLot.readInt(randomAccessFile) + int2 * 300, IsoLot.readInt(randomAccessFile), IsoLot.readInt(randomAccessFile));
										roomDef.rects.add(roomRect);
									}

									roomDef.CalculateBounds();
									lotHeader.Rooms.put(new Integer(roomDef.ID), roomDef);
									lotHeader.RoomList.add(roomDef);
									metaCell.addRoom(roomDef, int1 * 300, int2 * 300);
									int6 = IsoLot.readInt(randomAccessFile);
									for (int int8 = 0; int8 < int6; ++int8) {
										int int9 = IsoLot.readInt(randomAccessFile);
										int int10 = IsoLot.readInt(randomAccessFile);
										int int11 = IsoLot.readInt(randomAccessFile);
										roomDef.objects.add(new MetaObject(int9, int10 + int1 * 300 - roomDef.x, int11 + int2 * 300 - roomDef.y, roomDef));
									}

									roomDef.bLightsActive = Rand.Next(2) == 0;
								}

								int7 = IsoLot.readInt(randomAccessFile);
								int int12;
								for (int12 = 0; int12 < int7; ++int12) {
									BuildingDef buildingDef = new BuildingDef();
									int5 = IsoLot.readInt(randomAccessFile);
									buildingDef.ID = int12;
									for (int6 = 0; int6 < int5; ++int6) {
										RoomDef roomDef2 = (RoomDef)lotHeader.Rooms.get(IsoLot.readInt(randomAccessFile));
										roomDef2.building = buildingDef;
										if (roomDef2.isEmptyOutside()) {
											buildingDef.emptyoutside.add(roomDef2);
										} else {
											buildingDef.rooms.add(roomDef2);
										}
									}

									buildingDef.CalculateBounds(this.tempRooms);
									lotHeader.Buildings.add(buildingDef);
									this.Buildings.add(buildingDef);
								}

								for (int12 = 0; int12 < 30; ++int12) {
									for (int int13 = 0; int13 < 30; ++int13) {
										int5 = randomAccessFile.read();
										IsoMetaChunk metaChunk = metaCell.getChunk(int12, int13);
										metaChunk.setZombieIntensity(int5);
									}
								}
							} catch (Throwable throwable2) {
								throwable = throwable2;
								throw throwable2;
							} finally {
								if (randomAccessFile != null) {
									if (throwable != null) {
										try {
											randomAccessFile.close();
										} catch (Throwable throwable3) {
											throwable.addSuppressed(throwable3);
										}
									} else {
										randomAccessFile.close();
									}
								}
							}
						} catch (Exception exception) {
							DebugLog.log("ERROR loading " + file.getAbsolutePath());
							ExceptionLogger.logException(exception);
						}
					}
				}
			}
		}

		void postLoad() {
			IsoMetaGrid.this.Buildings.addAll(this.Buildings);
			this.Buildings.clear();
			this.sharedStrings.clear();
			this.tempRooms.clear();
		}
	}

	public static class Trigger {
		public BuildingDef def;
		public int triggerRange;
		public int zombieExclusionRange;
		public String type;
		public boolean triggered = false;
		public KahluaTable data;

		public KahluaTable getModData() {
			return this.data;
		}

		public Trigger(BuildingDef buildingDef, int int1, int int2, String string) {
			this.def = buildingDef;
			this.triggerRange = int1;
			this.zombieExclusionRange = int2;
			this.type = string;
			this.data = LuaManager.platform.newTable();
		}
	}

	public static final class VehicleZone extends IsoMetaGrid.Zone {
		public IsoDirections dir;
		public short flags;
		public static final short VZF_FaceDirection = 1;

		public VehicleZone(String string, String string2, int int1, int int2, int int3, int int4, int int5, KahluaTable kahluaTable) {
			super(string, string2, int1, int2, int3, int4, int5);
			this.dir = IsoDirections.Max;
			this.flags = 0;
			if (kahluaTable != null) {
				Object object = kahluaTable.rawget("Direction");
				if (object instanceof String) {
					this.dir = IsoDirections.valueOf((String)object);
				}

				object = kahluaTable.rawget("FaceDirection");
				if (object == Boolean.TRUE) {
					this.flags = (short)(this.flags | 1);
				}
			}
		}

		public boolean isFaceDirection() {
			return (this.flags & 1) != 0;
		}
	}

	public static class Zone {
		public int hourLastSeen = 0;
		public int lastActionTimestamp = 0;
		public boolean haveConstruction = false;
		public String name;
		public String type;
		public int x;
		public int y;
		public int z;
		public int w;
		public int h;
		private String originalName;

		public float getHoursSinceLastSeen() {
			return (float)GameTime.instance.getWorldAgeHours() - (float)this.hourLastSeen;
		}

		public void setHourSeenToCurrent() {
			this.hourLastSeen = (int)GameTime.instance.getWorldAgeHours();
		}

		public Zone(String string, String string2, int int1, int int2, int int3, int int4, int int5) {
			this.h = int5;
			this.originalName = string;
			this.name = string;
			this.type = string2;
			this.x = int1;
			this.y = int2;
			this.z = int3;
			this.w = int4;
		}

		public void setHaveConstruction(boolean boolean1) {
			this.haveConstruction = boolean1;
			if (GameClient.bClient) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)92, byteBufferWriter);
				byteBufferWriter.putInt(this.x);
				byteBufferWriter.putInt(this.y);
				byteBufferWriter.putInt(this.z);
				GameClient.connection.endPacketImmediate();
			}
		}

		public boolean haveCons() {
			return this.haveConstruction;
		}

		public int getZombieDensity() {
			IsoMetaChunk metaChunk = IsoWorld.instance.MetaGrid.getChunkDataFromTile(this.x, this.y);
			return metaChunk != null ? metaChunk.getUnadjustedZombieIntensity() : 0;
		}

		public boolean intersects(int int1, int int2, int int3, int int4, int int5) {
			if (this.z != int3) {
				return false;
			} else if (int1 + int4 > this.x && int1 < this.x + this.w) {
				return int2 + int5 > this.y && int2 < this.y + this.h;
			} else {
				return false;
			}
		}

		public boolean difference(int int1, int int2, int int3, int int4, int int5, ArrayList arrayList) {
			arrayList.clear();
			if (this.intersects(int1, int2, int3, int4, int5)) {
				int int6;
				int int7;
				if (this.x < int1) {
					int6 = Math.max(int2, this.y);
					int7 = Math.min(int2 + int5, this.y + this.h);
					arrayList.add(new IsoMetaGrid.Zone(this.name, this.type, this.x, int6, int3, int1 - this.x, int7 - int6));
				}

				if (int1 + int4 < this.x + this.w) {
					int6 = Math.max(int2, this.y);
					int7 = Math.min(int2 + int5, this.y + this.h);
					arrayList.add(new IsoMetaGrid.Zone(this.name, this.type, int1 + int4, int6, int3, this.x + this.w - (int1 + int4), int7 - int6));
				}

				if (this.y < int2) {
					arrayList.add(new IsoMetaGrid.Zone(this.name, this.type, this.x, this.y, int3, this.w, int2 - this.y));
				}

				if (int2 + int5 < this.y + this.h) {
					arrayList.add(new IsoMetaGrid.Zone(this.name, this.type, this.x, int2 + int5, int3, this.w, this.y + this.h - (int2 + int5)));
				}

				return true;
			} else {
				return false;
			}
		}

		public IsoGridSquare getRandomSquareInZone() {
			return IsoWorld.instance.getCell().getGridSquare(Rand.Next(this.x, this.x + this.w), Rand.Next(this.y, this.y + this.h), this.z);
		}

		public IsoGridSquare getRandomUnseenSquareInZone() {
			return null;
		}

		public void addSquare(IsoGridSquare square) {
		}

		public ArrayList getSquares() {
			return null;
		}

		public void removeSquare(IsoGridSquare square) {
		}

		public String getName() {
			return this.name;
		}

		public String getType() {
			return this.type;
		}

		public void setType(String string) {
			this.type = string;
		}

		public void setName(String string) {
			this.name = string;
		}

		public int getLastActionTimestamp() {
			return this.lastActionTimestamp;
		}

		public void setLastActionTimestamp(int int1) {
			this.lastActionTimestamp = int1;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public int getHeight() {
			return this.h;
		}

		public int getWidth() {
			return this.w;
		}

		public void sendToServer() {
			if (GameClient.bClient) {
				GameClient.registerZone(this, true);
			}
		}

		public String getOriginalName() {
			return this.originalName;
		}

		public void setOriginalName(String string) {
			this.originalName = string;
		}
	}
}
