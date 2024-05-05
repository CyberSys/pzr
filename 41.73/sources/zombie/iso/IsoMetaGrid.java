package zombie.iso;

import gnu.trove.list.array.TIntArrayList;
import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import org.joml.Vector2f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapGroups;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.stash.StashSystem;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.objects.IsoMannequin;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.randomizedWorld.randomizedBuilding.RBBasic;
import zombie.randomizedWorld.randomizedZoneStory.RandomizedZoneStoryBase;
import zombie.util.BufferedRandomAccessFile;
import zombie.util.SharedStrings;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.Clipper;
import zombie.vehicles.ClipperOffset;
import zombie.vehicles.PolygonalMap2;


public final class IsoMetaGrid {
	private static final int NUM_LOADER_THREADS = 8;
	private static ArrayList s_PreferredZoneTypes = new ArrayList();
	private static Clipper s_clipper = null;
	private static ClipperOffset s_clipperOffset = null;
	private static ByteBuffer s_clipperBuffer = null;
	private static final ThreadLocal TL_Location = ThreadLocal.withInitial(IsoGameCharacter.Location::new);
	private static final ThreadLocal TL_ZoneList = ThreadLocal.withInitial(ArrayList::new);
	static Rectangle a = new Rectangle();
	static Rectangle b = new Rectangle();
	static ArrayList roomChoices = new ArrayList(50);
	private final ArrayList tempRooms = new ArrayList();
	private final ArrayList tempZones1 = new ArrayList();
	private final ArrayList tempZones2 = new ArrayList();
	private final IsoMetaGrid.MetaGridLoaderThread[] threads = new IsoMetaGrid.MetaGridLoaderThread[8];
	public int minX = 10000000;
	public int minY = 10000000;
	public int maxX = -10000000;
	public int maxY = -10000000;
	public final ArrayList Zones = new ArrayList();
	public final ArrayList Buildings = new ArrayList();
	public final ArrayList VehiclesZones = new ArrayList();
	public IsoMetaCell[][] Grid;
	public final ArrayList MetaCharacters = new ArrayList();
	final ArrayList HighZombieList = new ArrayList();
	private int width;
	private int height;
	private final SharedStrings sharedStrings = new SharedStrings();
	private long createStartTime;

	public void AddToMeta(IsoGameCharacter gameCharacter) {
		IsoWorld.instance.CurrentCell.Remove(gameCharacter);
		if (!this.MetaCharacters.contains(gameCharacter)) {
			this.MetaCharacters.add(gameCharacter);
		}
	}

	public void RemoveFromMeta(IsoPlayer player) {
		this.MetaCharacters.remove(player);
		if (!IsoWorld.instance.CurrentCell.getObjectList().contains(player)) {
			IsoWorld.instance.CurrentCell.getObjectList().add(player);
		}
	}

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
		return this.getZonesAt(int1, int2, int3, new ArrayList());
	}

	public ArrayList getZonesAt(int int1, int int2, int int3, ArrayList arrayList) {
		IsoMetaChunk metaChunk = this.getChunkDataFromTile(int1, int2);
		return metaChunk != null ? metaChunk.getZonesAt(int1, int2, int3, arrayList) : arrayList;
	}

	public ArrayList getZonesIntersecting(int int1, int int2, int int3, int int4, int int5) {
		ArrayList arrayList = new ArrayList();
		return this.getZonesIntersecting(int1, int2, int3, int4, int5, arrayList);
	}

	public ArrayList getZonesIntersecting(int int1, int int2, int int3, int int4, int int5, ArrayList arrayList) {
		for (int int6 = int2 / 300; int6 <= (int2 + int5) / 300; ++int6) {
			for (int int7 = int1 / 300; int7 <= (int1 + int4) / 300; ++int7) {
				if (int7 >= this.minX && int7 <= this.maxX && int6 >= this.minY && int6 <= this.maxY && this.Grid[int7 - this.minX][int6 - this.minY] != null) {
					this.Grid[int7 - this.minX][int6 - this.minY].getZonesIntersecting(int1, int2, int3, int4, int5, arrayList);
				}
			}
		}

		return arrayList;
	}

	public IsoMetaGrid.Zone getZoneWithBoundsAndType(int int1, int int2, int int3, int int4, int int5, String string) {
		ArrayList arrayList = (ArrayList)TL_ZoneList.get();
		arrayList.clear();
		this.getZonesIntersecting(int1, int2, int3, int4, int5, arrayList);
		for (int int6 = 0; int6 < arrayList.size(); ++int6) {
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)arrayList.get(int6);
			if (zone.x == int1 && zone.y == int2 && zone.z == int3 && zone.w == int4 && zone.h == int5 && StringUtils.equalsIgnoreCase(zone.type, string)) {
				return zone;
			}
		}

		return null;
	}

	public IsoMetaGrid.VehicleZone getVehicleZoneAt(int int1, int int2, int int3) {
		IsoMetaCell metaCell = this.getMetaGridFromTile(int1, int2);
		if (metaCell != null && !metaCell.vehicleZones.isEmpty()) {
			for (int int4 = 0; int4 < metaCell.vehicleZones.size(); ++int4) {
				IsoMetaGrid.VehicleZone vehicleZone = (IsoMetaGrid.VehicleZone)metaCell.vehicleZones.get(int4);
				if (vehicleZone.contains(int1, int2, int3)) {
					return vehicleZone;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public BuildingDef getBuildingAt(int int1, int int2) {
		for (int int3 = 0; int3 < this.Buildings.size(); ++int3) {
			BuildingDef buildingDef = (BuildingDef)this.Buildings.get(int3);
			if (buildingDef.x <= int1 && buildingDef.y <= int2 && buildingDef.getW() > int1 - buildingDef.x && buildingDef.getH() > int2 - buildingDef.y) {
				return buildingDef;
			}
		}

		return null;
	}

	public BuildingDef getBuildingAtRelax(int int1, int int2) {
		for (int int3 = 0; int3 < this.Buildings.size(); ++int3) {
			BuildingDef buildingDef = (BuildingDef)this.Buildings.get(int3);
			if (buildingDef.x <= int1 + 1 && buildingDef.y <= int2 + 1 && buildingDef.getW() > int1 - buildingDef.x - 1 && buildingDef.getH() > int2 - buildingDef.y - 1) {
				return buildingDef;
			}
		}

		return null;
	}

	public RoomDef getRoomAt(int int1, int int2, int int3) {
		IsoMetaChunk metaChunk = this.getChunkDataFromTile(int1, int2);
		return metaChunk != null ? metaChunk.getRoomAt(int1, int2, int3) : null;
	}

	public RoomDef getEmptyOutsideAt(int int1, int int2, int int3) {
		IsoMetaChunk metaChunk = this.getChunkDataFromTile(int1, int2);
		return metaChunk != null ? metaChunk.getEmptyOutsideAt(int1, int2, int3) : null;
	}

	public void getRoomsIntersecting(int int1, int int2, int int3, int int4, ArrayList arrayList) {
		for (int int5 = int2 / 300; int5 <= (int2 + this.height) / 300; ++int5) {
			for (int int6 = int1 / 300; int6 <= (int1 + this.width) / 300; ++int6) {
				if (int6 >= this.minX && int6 <= this.maxX && int5 >= this.minY && int5 <= this.maxY) {
					IsoMetaCell metaCell = this.Grid[int6 - this.minX][int5 - this.minY];
					if (metaCell != null) {
						metaCell.getRoomsIntersecting(int1, int2, int3, int4, arrayList);
					}
				}
			}
		}
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
		return this.registerZone(string, string2, int1, int2, int3, int4, int5, IsoMetaGrid.ZoneGeometryType.INVALID, (TIntArrayList)null, 0);
	}

	public IsoMetaGrid.Zone registerZone(String string, String string2, int int1, int int2, int int3, int int4, int int5, IsoMetaGrid.ZoneGeometryType zoneGeometryType, TIntArrayList tIntArrayList, int int6) {
		string = this.sharedStrings.get(string);
		string2 = this.sharedStrings.get(string2);
		IsoMetaGrid.Zone zone = new IsoMetaGrid.Zone(string, string2, int1, int2, int3, int4, int5);
		zone.geometryType = zoneGeometryType;
		if (tIntArrayList != null) {
			zone.points.addAll(tIntArrayList);
			zone.polylineWidth = int6;
		}

		zone.isPreferredZoneForSquare = isPreferredZoneForSquare(string2);
		if (int1 >= this.minX * 300 - 100 && int2 >= this.minY * 300 - 100 && int1 + int4 <= (this.maxX + 1) * 300 + 100 && int2 + int5 <= (this.maxY + 1) * 300 + 100 && int3 >= 0 && int3 < 8 && int4 <= 600 && int5 <= 600) {
			this.addZone(zone);
			return zone;
		} else {
			return zone;
		}
	}

	public IsoMetaGrid.Zone registerGeometryZone(String string, String string2, int int1, String string3, KahluaTable kahluaTable, KahluaTable kahluaTable2) {
		int int2 = Integer.MAX_VALUE;
		int int3 = Integer.MAX_VALUE;
		int int4 = Integer.MIN_VALUE;
		int int5 = Integer.MIN_VALUE;
		TIntArrayList tIntArrayList = new TIntArrayList(kahluaTable.len());
		for (int int6 = 0; int6 < kahluaTable.len(); int6 += 2) {
			Object object = kahluaTable.rawget(int6 + 1);
			Object object2 = kahluaTable.rawget(int6 + 2);
			int int7 = ((Double)object).intValue();
			int int8 = ((Double)object2).intValue();
			tIntArrayList.add(int7);
			tIntArrayList.add(int8);
			int2 = Math.min(int2, int7);
			int3 = Math.min(int3, int8);
			int4 = Math.max(int4, int7);
			int5 = Math.max(int5, int8);
		}

		byte byte1 = -1;
		switch (string3.hashCode()) {
		case -397519558: 
			if (string3.equals("polygon")) {
				byte1 = 1;
			}

			break;
		
		case 106845584: 
			if (string3.equals("point")) {
				byte1 = 0;
			}

			break;
		
		case 561938880: 
			if (string3.equals("polyline")) {
				byte1 = 2;
			}

		
		}
		IsoMetaGrid.ZoneGeometryType zoneGeometryType;
		switch (byte1) {
		case 0: 
			zoneGeometryType = IsoMetaGrid.ZoneGeometryType.Point;
			break;
		
		case 1: 
			zoneGeometryType = IsoMetaGrid.ZoneGeometryType.Polygon;
			break;
		
		case 2: 
			zoneGeometryType = IsoMetaGrid.ZoneGeometryType.Polyline;
			break;
		
		default: 
			throw new IllegalArgumentException("unknown zone geometry type");
		
		}
		IsoMetaGrid.ZoneGeometryType zoneGeometryType2 = zoneGeometryType;
		Double Double1 = zoneGeometryType2 == IsoMetaGrid.ZoneGeometryType.Polyline && kahluaTable2 != null ? (Double)Type.tryCastTo(kahluaTable2.rawget("LineWidth"), Double.class) : null;
		if (Double1 != null) {
			int[] intArray = new int[4];
			this.calculatePolylineOutlineBounds(tIntArrayList, Double1.intValue(), intArray);
			int2 = intArray[0];
			int3 = intArray[1];
			int4 = intArray[2];
			int5 = intArray[3];
		}

		IsoMetaGrid.Zone zone;
		if (!string2.equals("Vehicle") && !string2.equals("ParkingStall")) {
			zone = this.registerZone(string, string2, int2, int3, int1, int4 - int2 + 1, int5 - int3 + 1, zoneGeometryType2, tIntArrayList, Double1 == null ? 0 : Double1.intValue());
			tIntArrayList.clear();
			return zone;
		} else {
			zone = this.registerVehiclesZone(string, string2, int2, int3, int1, int4 - int2 + 1, int5 - int3 + 1, kahluaTable2);
			if (zone != null) {
				zone.geometryType = zoneGeometryType2;
				zone.points.addAll(tIntArrayList);
				zone.polylineWidth = Double1 == null ? 0 : Double1.intValue();
			}

			return zone;
		}
	}

	private void calculatePolylineOutlineBounds(TIntArrayList tIntArrayList, int int1, int[] intArray) {
		if (s_clipperOffset == null) {
			s_clipperOffset = new ClipperOffset();
			s_clipperBuffer = ByteBuffer.allocateDirect(3072);
		}

		s_clipperOffset.clear();
		s_clipperBuffer.clear();
		float float1 = int1 % 2 == 0 ? 0.0F : 0.5F;
		int int2;
		for (int2 = 0; int2 < tIntArrayList.size(); int2 += 2) {
			int int3 = tIntArrayList.get(int2);
			int int4 = tIntArrayList.get(int2 + 1);
			s_clipperBuffer.putFloat((float)int3 + float1);
			s_clipperBuffer.putFloat((float)int4 + float1);
		}

		s_clipperBuffer.flip();
		s_clipperOffset.addPath(tIntArrayList.size() / 2, s_clipperBuffer, ClipperOffset.JoinType.jtMiter.ordinal(), ClipperOffset.EndType.etOpenButt.ordinal());
		s_clipperOffset.execute((double)((float)int1 / 2.0F));
		int2 = s_clipperOffset.getPolygonCount();
		if (int2 < 1) {
			DebugLog.General.warn("Failed to generate polyline outline");
		} else {
			s_clipperBuffer.clear();
			s_clipperOffset.getPolygon(0, s_clipperBuffer);
			short short1 = s_clipperBuffer.getShort();
			float float2 = Float.MAX_VALUE;
			float float3 = Float.MAX_VALUE;
			float float4 = -3.4028235E38F;
			float float5 = -3.4028235E38F;
			for (int int5 = 0; int5 < short1; ++int5) {
				float float6 = s_clipperBuffer.getFloat();
				float float7 = s_clipperBuffer.getFloat();
				float2 = PZMath.min(float2, float6);
				float3 = PZMath.min(float3, float7);
				float4 = PZMath.max(float4, float6);
				float5 = PZMath.max(float5, float7);
			}

			intArray[0] = (int)PZMath.floor(float2);
			intArray[1] = (int)PZMath.floor(float3);
			intArray[2] = (int)PZMath.ceil(float4);
			intArray[3] = (int)PZMath.ceil(float5);
		}
	}

	@Deprecated
	public IsoMetaGrid.Zone registerZoneNoOverlap(String string, String string2, int int1, int int2, int int3, int int4, int int5) {
		return int1 >= this.minX * 300 - 100 && int2 >= this.minY * 300 - 100 && int1 + int4 <= (this.maxX + 1) * 300 + 100 && int2 + int5 <= (this.maxY + 1) * 300 + 100 && int3 >= 0 && int3 < 8 && int4 <= 600 && int5 <= 600 ? this.registerZone(string, string2, int1, int2, int3, int4, int5) : null;
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
					this.removeZone(zone);
					for (int int4 = 0; int4 < arrayList2.size(); ++int4) {
						this.addZone((IsoMetaGrid.Zone)arrayList2.get(int4));
					}
				}
			}

			if (!metaCell.vehicleZones.isEmpty()) {
				metaCell.vehicleZones.clear();
			}

			if (!metaCell.mannequinZones.isEmpty()) {
				metaCell.mannequinZones.clear();
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
					DebugLog.log("checkVehiclesZones: ERROR! Zone \'" + zone.name + "\':\'" + zone.type + "\' (" + zone.x + ", " + zone.y + ") duplicate with Zone \'" + zone2.name + "\':\'" + zone2.type + "\' (" + zone2.x + ", " + zone2.y + ")");
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

	public IsoMetaGrid.Zone registerMannequinZone(String string, String string2, int int1, int int2, int int3, int int4, int int5, KahluaTable kahluaTable) {
		if (!"Mannequin".equals(string2)) {
			return null;
		} else {
			string = this.sharedStrings.get(string);
			string2 = this.sharedStrings.get(string2);
			IsoMannequin.MannequinZone mannequinZone = new IsoMannequin.MannequinZone(string, string2, int1, int2, int3, int4, int5, kahluaTable);
			int int6 = (int)Math.ceil((double)((float)(mannequinZone.x + mannequinZone.w) / 300.0F));
			int int7 = (int)Math.ceil((double)((float)(mannequinZone.y + mannequinZone.h) / 300.0F));
			for (int int8 = mannequinZone.y / 300; int8 < int7; ++int8) {
				for (int int9 = mannequinZone.x / 300; int9 < int6; ++int9) {
					if (int9 >= this.minX && int9 <= this.maxX && int8 >= this.minY && int8 <= this.maxY && this.Grid[int9 - this.minX][int8 - this.minY] != null) {
						this.Grid[int9 - this.minX][int8 - this.minY].mannequinZones.add(mannequinZone);
					}
				}
			}

			return mannequinZone;
		}
	}

	public void registerRoomTone(String string, String string2, int int1, int int2, int int3, int int4, int int5, KahluaTable kahluaTable) {
		if ("RoomTone".equals(string2)) {
			IsoMetaCell metaCell = this.getCellData(int1 / 300, int2 / 300);
			if (metaCell != null) {
				IsoMetaGrid.RoomTone roomTone = new IsoMetaGrid.RoomTone();
				roomTone.x = int1;
				roomTone.y = int2;
				roomTone.z = int3;
				roomTone.enumValue = kahluaTable.getString("RoomTone");
				roomTone.entireBuilding = Boolean.TRUE.equals(kahluaTable.rawget("EntireBuilding"));
				metaCell.roomTones.add(roomTone);
			}
		}
	}

	public boolean isZoneAbove(IsoMetaGrid.Zone zone, IsoMetaGrid.Zone zone2, int int1, int int2, int int3) {
		if (zone != null && zone != zone2) {
			ArrayList arrayList = (ArrayList)TL_ZoneList.get();
			arrayList.clear();
			this.getZonesAt(int1, int2, int3, arrayList);
			return arrayList.indexOf(zone) > arrayList.indexOf(zone2);
		} else {
			return false;
		}
	}

	public void save(ByteBuffer byteBuffer) {
		this.savePart(byteBuffer, 0, false);
		this.savePart(byteBuffer, 1, false);
	}

	public void savePart(ByteBuffer byteBuffer, int int1, boolean boolean1) {
		int int2;
		if (int1 == 0) {
			byteBuffer.put((byte)77);
			byteBuffer.put((byte)69);
			byteBuffer.put((byte)84);
			byteBuffer.put((byte)65);
			byteBuffer.putInt(194);
			byteBuffer.putInt(this.minX);
			byteBuffer.putInt(this.minY);
			byteBuffer.putInt(this.maxX);
			byteBuffer.putInt(this.maxY);
			for (int2 = 0; int2 < this.Grid.length; ++int2) {
				for (int int3 = 0; int3 < this.Grid[0].length; ++int3) {
					IsoMetaCell metaCell = this.Grid[int2][int3];
					int int4 = 0;
					if (metaCell.info != null) {
						int4 = metaCell.info.Rooms.values().size();
					}

					byteBuffer.putInt(int4);
					Iterator iterator;
					short short1;
					if (metaCell.info != null) {
						for (iterator = metaCell.info.Rooms.entrySet().iterator(); iterator.hasNext(); byteBuffer.putShort(short1)) {
							Entry entry = (Entry)iterator.next();
							RoomDef roomDef = (RoomDef)entry.getValue();
							byteBuffer.putLong(roomDef.metaID);
							short1 = 0;
							if (roomDef.bExplored) {
								short1 = (short)(short1 | 1);
							}

							if (roomDef.bLightsActive) {
								short1 = (short)(short1 | 2);
							}

							if (roomDef.bDoneSpawn) {
								short1 = (short)(short1 | 4);
							}

							if (roomDef.isRoofFixed()) {
								short1 = (short)(short1 | 8);
							}
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
							byteBuffer.putLong(buildingDef.metaID);
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

			if (GameServer.bServer) {
				int2 = byteBuffer.position();
				byteBuffer.putInt(0);
				StashSystem.save(byteBuffer);
				byteBuffer.putInt(int2, byteBuffer.position());
			} else if (!GameClient.bClient) {
				StashSystem.save(byteBuffer);
			}

			byteBuffer.putInt(RBBasic.getUniqueRDSSpawned().size());
			for (int2 = 0; int2 < RBBasic.getUniqueRDSSpawned().size(); ++int2) {
				GameWindow.WriteString(byteBuffer, (String)RBBasic.getUniqueRDSSpawned().get(int2));
			}
		}
	}

	public void load() {
		File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_meta.bin");
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					synchronized (SliceY.SliceBufferLock) {
						SliceY.SliceBuffer.clear();
						int int1 = bufferedInputStream.read(SliceY.SliceBuffer.array());
						SliceY.SliceBuffer.limit(int1);
						this.load(SliceY.SliceBuffer);
					}
				} catch (Throwable throwable) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
		} catch (FileNotFoundException fileNotFoundException) {
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
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

		int int2 = this.minX;
		int int3 = this.minY;
		int int4 = this.maxX;
		int int5 = this.maxY;
		int int6;
		int int7;
		if (int1 >= 194) {
			int2 = byteBuffer.getInt();
			int3 = byteBuffer.getInt();
			int4 = byteBuffer.getInt();
			int5 = byteBuffer.getInt();
			int6 = int4 - int2 + 1;
			int7 = int5 - int3 + 1;
		} else {
			int6 = byteBuffer.getInt();
			int7 = byteBuffer.getInt();
			if (int6 == 40 && int7 == 42 && this.width == 66 && this.height == 53 && this.getLotDirectories().contains("Muldraugh, KY")) {
				int2 = 10;
				int3 = 3;
			}

			int4 = int2 + int6 - 1;
			int5 = int3 + int7 - 1;
		}

		if (int6 != this.Grid.length || int7 != this.Grid[0].length) {
			DebugLog.log("map_meta.bin world size (" + int6 + "x" + int7 + ") does not match the current map size (" + this.Grid.length + "x" + this.Grid[0].length + ")");
		}

		int int8 = 0;
		int int9 = 0;
		int int10;
		int int11;
		IsoMetaCell metaCell;
		int int12;
		int int13;
		int int14;
		for (int10 = int2; int10 <= int4; ++int10) {
			for (int11 = int3; int11 <= int5; ++int11) {
				metaCell = this.getCellData(int10, int11);
				int12 = byteBuffer.getInt();
				long long1;
				boolean boolean1;
				boolean boolean2;
				boolean boolean3;
				for (int13 = 0; int13 < int12; ++int13) {
					int14 = int1 < 194 ? byteBuffer.getInt() : 0;
					long1 = int1 >= 194 ? byteBuffer.getLong() : 0L;
					boolean1 = false;
					boolean boolean4 = false;
					boolean2 = false;
					boolean3 = false;
					if (int1 >= 160) {
						short short1 = byteBuffer.getShort();
						boolean1 = (short1 & 1) != 0;
						boolean4 = (short1 & 2) != 0;
						boolean2 = (short1 & 4) != 0;
						boolean3 = (short1 & 8) != 0;
					} else {
						boolean1 = byteBuffer.get() == 1;
						if (int1 >= 34) {
							boolean4 = byteBuffer.get() == 1;
						} else {
							boolean4 = Rand.Next(2) == 0;
						}
					}

					if (metaCell != null && metaCell.info != null) {
						RoomDef roomDef = int1 < 194 ? (RoomDef)metaCell.info.Rooms.get(int14) : (RoomDef)metaCell.info.RoomByMetaID.get(long1);
						if (roomDef != null) {
							roomDef.setExplored(boolean1);
							roomDef.bLightsActive = boolean4;
							roomDef.bDoneSpawn = boolean2;
							roomDef.setRoofFixed(boolean3);
						} else if (int1 < 194) {
							DebugLog.General.error("invalid room ID #" + int14 + " in cell " + int10 + "," + int11 + " while reading map_meta.bin");
						} else {
							DebugLog.General.error("invalid room metaID #" + long1 + " in cell " + int10 + "," + int11 + " while reading map_meta.bin");
						}
					}
				}

				int13 = byteBuffer.getInt();
				int8 += int13;
				for (int14 = 0; int14 < int13; ++int14) {
					long1 = int1 >= 194 ? byteBuffer.getLong() : 0L;
					boolean1 = byteBuffer.get() == 1;
					int int15 = int1 >= 57 ? byteBuffer.getInt() : -1;
					boolean2 = int1 >= 74 ? byteBuffer.get() == 1 : false;
					boolean3 = int1 >= 107 ? byteBuffer.get() == 1 : false;
					if (int1 >= 111 && int1 < 121) {
						byteBuffer.getInt();
					} else {
						boolean boolean5 = false;
					}

					int int16 = int1 >= 125 ? byteBuffer.getInt() : 0;
					if (metaCell != null && metaCell.info != null) {
						BuildingDef buildingDef = null;
						if (int1 >= 194) {
							buildingDef = (BuildingDef)metaCell.info.BuildingByMetaID.get(long1);
						} else if (int14 < metaCell.info.Buildings.size()) {
							buildingDef = (BuildingDef)metaCell.info.Buildings.get(int14);
						}

						if (buildingDef != null) {
							if (boolean1) {
								++int9;
							}

							buildingDef.bAlarmed = boolean1;
							buildingDef.setKeyId(int15);
							if (int1 >= 74) {
								buildingDef.seen = boolean2;
							}

							buildingDef.hasBeenVisited = boolean3;
							buildingDef.lootRespawnHour = int16;
						} else if (int1 >= 194) {
							DebugLog.General.error("invalid building metaID #" + long1 + " in cell " + int10 + "," + int11 + " while reading map_meta.bin");
						}
					}
				}
			}
		}

		if (int1 <= 112) {
			this.Zones.clear();
			for (int10 = 0; int10 < this.height; ++int10) {
				for (int11 = 0; int11 < this.width; ++int11) {
					metaCell = this.Grid[int11][int10];
					if (metaCell != null) {
						for (int12 = 0; int12 < 30; ++int12) {
							for (int13 = 0; int13 < 30; ++int13) {
								metaCell.ChunkMap[int13 + int12 * 30].clearZones();
							}
						}
					}
				}
			}

			this.loadZone(byteBuffer, int1);
		}

		SafeHouse.clearSafehouseList();
		int10 = byteBuffer.getInt();
		for (int11 = 0; int11 < int10; ++int11) {
			SafeHouse.load(byteBuffer, int1);
		}

		NonPvpZone.nonPvpZoneList.clear();
		int11 = byteBuffer.getInt();
		int int17;
		for (int17 = 0; int17 < int11; ++int17) {
			NonPvpZone nonPvpZone = new NonPvpZone();
			nonPvpZone.load(byteBuffer, int1);
			NonPvpZone.getAllZones().add(nonPvpZone);
		}

		Faction.factions = new ArrayList();
		int17 = byteBuffer.getInt();
		for (int12 = 0; int12 < int17; ++int12) {
			Faction faction = new Faction();
			faction.load(byteBuffer, int1);
			Faction.getFactions().add(faction);
		}

		if (GameServer.bServer) {
			int12 = byteBuffer.getInt();
			StashSystem.load(byteBuffer, int1);
		} else if (GameClient.bClient) {
			int12 = byteBuffer.getInt();
			byteBuffer.position(int12);
		} else {
			StashSystem.load(byteBuffer, int1);
		}

		ArrayList arrayList = RBBasic.getUniqueRDSSpawned();
		arrayList.clear();
		int13 = byteBuffer.getInt();
		for (int14 = 0; int14 < int13; ++int14) {
			arrayList.add(GameWindow.ReadString(byteBuffer));
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
			File file = new File(string);
			DebugLog.log("	" + file.getAbsolutePath());
		}

		DebugLog.log("<End of map-folders list>");
		iterator = arrayList.iterator();
		while (true) {
			File file2;
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
				file2 = new File(ZomboidFileSystem.instance.getString("media/maps/" + string + "/"));
			}	 while (!file2.isDirectory());

			ChooseGameInfo.Map map = ChooseGameInfo.getMapDetails(string);
			String[] stringArray = file2.list();
			for (int int2 = 0; int2 < stringArray.length; ++int2) {
				if (!IsoLot.InfoFileNames.containsKey(stringArray[int2])) {
					HashMap hashMap;
					String string2;
					String string3;
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

						hashMap = IsoLot.InfoFileNames;
						string2 = stringArray[int2];
						string3 = file2.getAbsolutePath();
						hashMap.put(string2, string3 + File.separator + stringArray[int2]);
						LotHeader lotHeader = new LotHeader();
						lotHeader.cellX = int3;
						lotHeader.cellY = int4;
						lotHeader.bFixed2x = map.isFixed2x();
						IsoLot.InfoHeaders.put(stringArray[int2], lotHeader);
						IsoLot.InfoHeaderNames.add(stringArray[int2]);
					} else if (stringArray[int2].endsWith(".lotpack")) {
						hashMap = IsoLot.InfoFileNames;
						string2 = stringArray[int2];
						string3 = file2.getAbsolutePath();
						hashMap.put(string2, string3 + File.separator + stringArray[int2]);
					} else if (stringArray[int2].startsWith("chunkdata_")) {
						hashMap = IsoLot.InfoFileNames;
						string2 = stringArray[int2];
						string3 = file2.getAbsolutePath();
						hashMap.put(string2, string3 + File.separator + stringArray[int2]);
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
		if (this.Grid != null) {
			for (int int1 = 0; int1 < this.Grid.length; ++int1) {
				IsoMetaCell[] metaCellArray = this.Grid[int1];
				for (int int2 = 0; int2 < metaCellArray.length; ++int2) {
					IsoMetaCell metaCell = metaCellArray[int2];
					if (metaCell != null) {
						metaCell.Dispose();
					}
				}

				Arrays.fill(metaCellArray, (Object)null);
			}

			Arrays.fill(this.Grid, (Object)null);
			this.Grid = null;
			Iterator iterator = this.Buildings.iterator();
			while (iterator.hasNext()) {
				BuildingDef buildingDef = (BuildingDef)iterator.next();
				buildingDef.Dispose();
			}

			this.Buildings.clear();
			this.VehiclesZones.clear();
			iterator = this.Zones.iterator();
			while (iterator.hasNext()) {
				IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)iterator.next();
				zone.Dispose();
			}

			this.Zones.clear();
			this.sharedStrings.clear();
		}
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
			File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_meta.bin");
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			try {
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				try {
					synchronized (SliceY.SliceBufferLock) {
						SliceY.SliceBuffer.clear();
						this.save(SliceY.SliceBuffer);
						bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
					}
				} catch (Throwable throwable) {
					try {
						bufferedOutputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedOutputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileOutputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileOutputStream.close();
			File file2 = ZomboidFileSystem.instance.getFileInCurrentSave("map_zone.bin");
			FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
			try {
				BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(fileOutputStream2);
				try {
					synchronized (SliceY.SliceBufferLock) {
						SliceY.SliceBuffer.clear();
						this.saveZone(SliceY.SliceBuffer);
						bufferedOutputStream2.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
					}
				} catch (Throwable throwable5) {
					try {
						bufferedOutputStream2.close();
					} catch (Throwable throwable6) {
						throwable5.addSuppressed(throwable6);
					}

					throw throwable5;
				}

				bufferedOutputStream2.close();
			} catch (Throwable throwable7) {
				try {
					fileOutputStream2.close();
				} catch (Throwable throwable8) {
					throwable7.addSuppressed(throwable8);
				}

				throw throwable7;
			}

			fileOutputStream2.close();
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	public void loadZones() {
		File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_zone.bin");
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					synchronized (SliceY.SliceBufferLock) {
						SliceY.SliceBuffer.clear();
						int int1 = bufferedInputStream.read(SliceY.SliceBuffer.array());
						SliceY.SliceBuffer.limit(int1);
						this.loadZone(SliceY.SliceBuffer, -1);
					}
				} catch (Throwable throwable) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
		} catch (FileNotFoundException fileNotFoundException) {
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
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
			Iterator iterator = this.Zones.iterator();
			while (iterator.hasNext()) {
				IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)iterator.next();
				zone.Dispose();
			}

			this.Zones.clear();
			int int3;
			int int4;
			for (int int5 = 0; int5 < this.height; ++int5) {
				for (int int6 = 0; int6 < this.width; ++int6) {
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

			IsoMetaGrid.ZoneGeometryType[] zoneGeometryTypeArray = IsoMetaGrid.ZoneGeometryType.values();
			TIntArrayList tIntArrayList = new TIntArrayList();
			String string;
			int int7;
			int int8;
			int int9;
			int int10;
			int int11;
			int int12;
			int int13;
			if (int1 >= 141) {
				int12 = byteBuffer.getInt();
				HashMap hashMap = new HashMap();
				for (int4 = 0; int4 < int12; ++int4) {
					string = GameWindow.ReadStringUTF(byteBuffer);
					hashMap.put(int4, string);
				}

				int4 = byteBuffer.getInt();
				DebugLog.log("loading " + int4 + " zones from map_zone.bin");
				int int14;
				String string2;
				for (int14 = 0; int14 < int4; ++int14) {
					String string3 = (String)hashMap.get(Integer.valueOf(byteBuffer.getShort()));
					string2 = (String)hashMap.get(Integer.valueOf(byteBuffer.getShort()));
					int8 = byteBuffer.getInt();
					int9 = byteBuffer.getInt();
					byte byte5 = byteBuffer.get();
					int int15 = byteBuffer.getInt();
					int11 = byteBuffer.getInt();
					IsoMetaGrid.ZoneGeometryType zoneGeometryType = IsoMetaGrid.ZoneGeometryType.INVALID;
					tIntArrayList.clear();
					int13 = 0;
					if (int1 >= 185) {
						byte byte6 = byteBuffer.get();
						if (byte6 < 0 || byte6 >= zoneGeometryTypeArray.length) {
							byte6 = 0;
						}

						zoneGeometryType = zoneGeometryTypeArray[byte6];
						if (zoneGeometryType != IsoMetaGrid.ZoneGeometryType.INVALID) {
							if (int1 >= 186 && zoneGeometryType == IsoMetaGrid.ZoneGeometryType.Polyline) {
								int13 = PZMath.clamp(byteBuffer.get(), 0, 255);
							}

							short short1 = byteBuffer.getShort();
							for (int int16 = 0; int16 < short1; ++int16) {
								tIntArrayList.add(byteBuffer.getInt());
							}
						}
					}

					int int17 = byteBuffer.getInt();
					IsoMetaGrid.Zone zone2 = this.registerZone(string3, string2, int8, int9, byte5, int15, int11, zoneGeometryType, zoneGeometryType == IsoMetaGrid.ZoneGeometryType.INVALID ? null : tIntArrayList, int13);
					zone2.hourLastSeen = int17;
					zone2.haveConstruction = byteBuffer.get() == 1;
					zone2.lastActionTimestamp = byteBuffer.getInt();
					zone2.setOriginalName((String)hashMap.get(Integer.valueOf(byteBuffer.getShort())));
					zone2.id = byteBuffer.getDouble();
				}

				int14 = byteBuffer.getInt();
				for (int7 = 0; int7 < int14; ++int7) {
					string2 = GameWindow.ReadString(byteBuffer);
					ArrayList arrayList = new ArrayList();
					int9 = byteBuffer.getInt();
					for (int10 = 0; int10 < int9; ++int10) {
						arrayList.add(byteBuffer.getDouble());
					}

					IsoWorld.instance.getSpawnedZombieZone().put(string2, arrayList);
				}

				return;
			}

			int12 = byteBuffer.getInt();
			DebugLog.log("loading " + int12 + " zones from map_zone.bin");
			if (int1 <= 112 && int12 > int2 * 2) {
				DebugLog.log("ERROR: seems like too many zones in map_zone.bin");
				return;
			}

			for (int3 = 0; int3 < int12; ++int3) {
				String string4 = GameWindow.ReadString(byteBuffer);
				string = GameWindow.ReadString(byteBuffer);
				int7 = byteBuffer.getInt();
				int int18 = byteBuffer.getInt();
				int8 = byteBuffer.getInt();
				int9 = byteBuffer.getInt();
				int10 = byteBuffer.getInt();
				if (int1 < 121) {
					byteBuffer.getInt();
				} else {
					boolean boolean1 = false;
				}

				int11 = int1 < 68 ? byteBuffer.getShort() : byteBuffer.getInt();
				IsoMetaGrid.Zone zone3 = this.registerZone(string4, string, int7, int18, int8, int9, int10);
				zone3.hourLastSeen = int11;
				if (int1 >= 35) {
					boolean boolean2 = byteBuffer.get() == 1;
					zone3.haveConstruction = boolean2;
				}

				if (int1 >= 41) {
					zone3.lastActionTimestamp = byteBuffer.getInt();
				}

				if (int1 >= 98) {
					zone3.setOriginalName(GameWindow.ReadString(byteBuffer));
				}

				if (int1 >= 110 && int1 < 121) {
					int13 = byteBuffer.getInt();
				}

				zone3.id = byteBuffer.getDouble();
			}
		}
	}

	public void saveZone(ByteBuffer byteBuffer) {
		byteBuffer.put((byte)90);
		byteBuffer.put((byte)79);
		byteBuffer.put((byte)78);
		byteBuffer.put((byte)69);
		byteBuffer.putInt(194);
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
			hashMap.put((String)arrayList.get(int2), int2);
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
				byteBuffer.put((byte)zone2.geometryType.ordinal());
				if (!zone2.isRectangle()) {
					if (zone2.isPolyline()) {
						byteBuffer.put((byte)zone2.polylineWidth);
					}

					byteBuffer.putShort((short)zone2.points.size());
					for (int int3 = 0; int3 < zone2.points.size(); ++int3) {
						byteBuffer.putInt(zone2.points.get(int3));
					}
				}

				byteBuffer.putInt(zone2.hourLastSeen);
				byteBuffer.put((byte)(zone2.haveConstruction ? 1 : 0));
				byteBuffer.putInt(zone2.lastActionTimestamp);
				byteBuffer.putShort(((Integer)hashMap.get(zone2.getOriginalName())).shortValue());
				byteBuffer.putDouble(zone2.id);
			}

			hashSet.clear();
			arrayList.clear();
			hashMap.clear();
			byteBuffer.putInt(IsoWorld.instance.getSpawnedZombieZone().size());
			Iterator iterator = IsoWorld.instance.getSpawnedZombieZone().keySet().iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				ArrayList arrayList2 = (ArrayList)IsoWorld.instance.getSpawnedZombieZone().get(string);
				GameWindow.WriteString(byteBuffer, string);
				byteBuffer.putInt(arrayList2.size());
				for (int int4 = 0; int4 < arrayList2.size(); ++int4) {
					byteBuffer.putDouble((Double)arrayList2.get(int4));
				}
			}
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

	public static boolean isPreferredZoneForSquare(String string) {
		return s_PreferredZoneTypes.contains(string);
	}

	static  {
		s_PreferredZoneTypes.add("DeepForest");
		s_PreferredZoneTypes.add("Farm");
		s_PreferredZoneTypes.add("FarmLand");
		s_PreferredZoneTypes.add("Forest");
		s_PreferredZoneTypes.add("Vegitation");
		s_PreferredZoneTypes.add("Nav");
		s_PreferredZoneTypes.add("TownZone");
		s_PreferredZoneTypes.add("TrailerPark");
	}

	private final class MetaGridLoaderThread extends Thread {
		final SharedStrings sharedStrings = new SharedStrings();
		final ArrayList Buildings = new ArrayList();
		final ArrayList tempRooms = new ArrayList();
		int wY;

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
							BufferedRandomAccessFile bufferedRandomAccessFile = new BufferedRandomAccessFile(file.getAbsolutePath(), "r", 4096);
							try {
								lotHeader.version = IsoLot.readInt(bufferedRandomAccessFile);
								int int3 = IsoLot.readInt(bufferedRandomAccessFile);
								int int4;
								for (int4 = 0; int4 < int3; ++int4) {
									String string2 = IsoLot.readString(bufferedRandomAccessFile);
									lotHeader.tilesUsed.add(this.sharedStrings.get(string2.trim()));
								}

								bufferedRandomAccessFile.read();
								lotHeader.width = IsoLot.readInt(bufferedRandomAccessFile);
								lotHeader.height = IsoLot.readInt(bufferedRandomAccessFile);
								lotHeader.levels = IsoLot.readInt(bufferedRandomAccessFile);
								int4 = IsoLot.readInt(bufferedRandomAccessFile);
								int int5;
								int int6;
								int int7;
								for (int7 = 0; int7 < int4; ++int7) {
									String string3 = IsoLot.readString(bufferedRandomAccessFile);
									RoomDef roomDef = new RoomDef(int7, this.sharedStrings.get(string3));
									roomDef.level = IsoLot.readInt(bufferedRandomAccessFile);
									int5 = IsoLot.readInt(bufferedRandomAccessFile);
									for (int6 = 0; int6 < int5; ++int6) {
										RoomDef.RoomRect roomRect = new RoomDef.RoomRect(IsoLot.readInt(bufferedRandomAccessFile) + int1 * 300, IsoLot.readInt(bufferedRandomAccessFile) + int2 * 300, IsoLot.readInt(bufferedRandomAccessFile), IsoLot.readInt(bufferedRandomAccessFile));
										roomDef.rects.add(roomRect);
									}

									roomDef.CalculateBounds();
									roomDef.metaID = roomDef.calculateMetaID(int1, int2);
									lotHeader.Rooms.put(roomDef.ID, roomDef);
									if (lotHeader.RoomByMetaID.contains(roomDef.metaID)) {
										DebugLog.General.error("duplicate RoomDef.metaID for room at %d,%d,%d", roomDef.x, roomDef.y, roomDef.level);
									}

									lotHeader.RoomByMetaID.put(roomDef.metaID, roomDef);
									lotHeader.RoomList.add(roomDef);
									metaCell.addRoom(roomDef, int1 * 300, int2 * 300);
									int6 = IsoLot.readInt(bufferedRandomAccessFile);
									for (int int8 = 0; int8 < int6; ++int8) {
										int int9 = IsoLot.readInt(bufferedRandomAccessFile);
										int int10 = IsoLot.readInt(bufferedRandomAccessFile);
										int int11 = IsoLot.readInt(bufferedRandomAccessFile);
										roomDef.objects.add(new MetaObject(int9, int10 + int1 * 300 - roomDef.x, int11 + int2 * 300 - roomDef.y, roomDef));
									}

									roomDef.bLightsActive = Rand.Next(2) == 0;
								}

								int7 = IsoLot.readInt(bufferedRandomAccessFile);
								int int12 = 0;
								label87: while (true) {
									if (int12 >= int7) {
										int12 = 0;
										while (true) {
											if (int12 >= 30) {
												break label87;
											}

											for (int int13 = 0; int13 < 30; ++int13) {
												int5 = bufferedRandomAccessFile.read();
												IsoMetaChunk metaChunk = metaCell.getChunk(int12, int13);
												metaChunk.setZombieIntensity(int5);
											}

											++int12;
										}
									}

									BuildingDef buildingDef = new BuildingDef();
									int5 = IsoLot.readInt(bufferedRandomAccessFile);
									buildingDef.ID = int12;
									for (int6 = 0; int6 < int5; ++int6) {
										RoomDef roomDef2 = (RoomDef)lotHeader.Rooms.get(IsoLot.readInt(bufferedRandomAccessFile));
										roomDef2.building = buildingDef;
										if (roomDef2.isEmptyOutside()) {
											buildingDef.emptyoutside.add(roomDef2);
										} else {
											buildingDef.rooms.add(roomDef2);
										}
									}

									buildingDef.CalculateBounds(this.tempRooms);
									buildingDef.metaID = buildingDef.calculateMetaID(int1, int2);
									lotHeader.Buildings.add(buildingDef);
									lotHeader.BuildingByMetaID.put(buildingDef.metaID, buildingDef);
									this.Buildings.add(buildingDef);
									++int12;
								}
							} catch (Throwable throwable) {
								try {
									bufferedRandomAccessFile.close();
								} catch (Throwable throwable2) {
									throwable.addSuppressed(throwable2);
								}

								throw throwable;
							}

							bufferedRandomAccessFile.close();
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

	public static class Zone {
		public Double id = 0.0;
		public int hourLastSeen = 0;
		public int lastActionTimestamp = 0;
		public boolean haveConstruction = false;
		public final HashMap spawnedZombies = new HashMap();
		public String zombiesTypeToSpawn = null;
		public Boolean spawnSpecialZombies = null;
		public String name;
		public String type;
		public int x;
		public int y;
		public int z;
		public int w;
		public int h;
		public IsoMetaGrid.ZoneGeometryType geometryType;
		public final TIntArrayList points;
		private boolean bTriangulateFailed;
		public int polylineWidth;
		public float[] polylineOutlinePoints;
		public float[] triangles;
		public float[] triangleAreas;
		public float totalArea;
		public int pickedXForZoneStory;
		public int pickedYForZoneStory;
		public RandomizedZoneStoryBase pickedRZStory;
		private String originalName;
		public boolean isPreferredZoneForSquare;
		static final PolygonalMap2.LiangBarsky LIANG_BARSKY = new PolygonalMap2.LiangBarsky();
		static final Vector2 L_lineSegmentIntersects = new Vector2();

		public Zone(String string, String string2, int int1, int int2, int int3, int int4, int int5) {
			this.geometryType = IsoMetaGrid.ZoneGeometryType.INVALID;
			this.points = new TIntArrayList();
			this.bTriangulateFailed = false;
			this.polylineWidth = 0;
			this.totalArea = 0.0F;
			this.isPreferredZoneForSquare = false;
			this.id = (double)Rand.Next(9999999) + 100000.0;
			this.originalName = string;
			this.name = string;
			this.type = string2;
			this.x = int1;
			this.y = int2;
			this.z = int3;
			this.w = int4;
			this.h = int5;
		}

		public void setX(int int1) {
			this.x = int1;
		}

		public void setY(int int1) {
			this.y = int1;
		}

		public void setW(int int1) {
			this.w = int1;
		}

		public void setH(int int1) {
			this.h = int1;
		}

		public boolean isPoint() {
			return this.geometryType == IsoMetaGrid.ZoneGeometryType.Point;
		}

		public boolean isPolygon() {
			return this.geometryType == IsoMetaGrid.ZoneGeometryType.Polygon;
		}

		public boolean isPolyline() {
			return this.geometryType == IsoMetaGrid.ZoneGeometryType.Polyline;
		}

		public boolean isRectangle() {
			return this.geometryType == IsoMetaGrid.ZoneGeometryType.INVALID;
		}

		public void setPickedXForZoneStory(int int1) {
			this.pickedXForZoneStory = int1;
		}

		public void setPickedYForZoneStory(int int1) {
			this.pickedYForZoneStory = int1;
		}

		public float getHoursSinceLastSeen() {
			return (float)GameTime.instance.getWorldAgeHours() - (float)this.hourLastSeen;
		}

		public void setHourSeenToCurrent() {
			this.hourLastSeen = (int)GameTime.instance.getWorldAgeHours();
		}

		public void setHaveConstruction(boolean boolean1) {
			this.haveConstruction = boolean1;
			if (GameClient.bClient) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.PacketType.ConstructedZone.doPacket(byteBufferWriter);
				byteBufferWriter.putInt(this.x);
				byteBufferWriter.putInt(this.y);
				byteBufferWriter.putInt(this.z);
				PacketTypes.PacketType.ConstructedZone.send(GameClient.connection);
			}
		}

		public boolean haveCons() {
			return this.haveConstruction;
		}

		public int getZombieDensity() {
			IsoMetaChunk metaChunk = IsoWorld.instance.MetaGrid.getChunkDataFromTile(this.x, this.y);
			return metaChunk != null ? metaChunk.getUnadjustedZombieIntensity() : 0;
		}

		public boolean contains(int int1, int int2, int int3) {
			if (int3 != this.z) {
				return false;
			} else if (int1 >= this.x && int1 < this.x + this.w) {
				if (int2 >= this.y && int2 < this.y + this.h) {
					if (this.isPoint()) {
						return false;
					} else if (this.isPolyline()) {
						if (this.polylineWidth > 0) {
							this.checkPolylineOutline();
							return this.isPointInPolyline_WindingNumber((float)int1 + 0.5F, (float)int2 + 0.5F, 0) == IsoMetaGrid.Zone.PolygonHit.Inside;
						} else {
							return false;
						}
					} else if (this.isPolygon()) {
						return this.isPointInPolygon_WindingNumber((float)int1 + 0.5F, (float)int2 + 0.5F, 0) == IsoMetaGrid.Zone.PolygonHit.Inside;
					} else {
						return true;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		public boolean intersects(int int1, int int2, int int3, int int4, int int5) {
			if (this.z != int3) {
				return false;
			} else if (int1 + int4 > this.x && int1 < this.x + this.w) {
				if (int2 + int5 > this.y && int2 < this.y + this.h) {
					if (this.isPolygon()) {
						return this.polygonRectIntersect(int1, int2, int4, int5);
					} else if (this.isPolyline()) {
						if (this.polylineWidth > 0) {
							this.checkPolylineOutline();
							return this.polylineOutlineRectIntersect(int1, int2, int4, int5);
						} else {
							for (int int6 = 0; int6 < this.points.size() - 2; int6 += 2) {
								int int7 = this.points.getQuick(int6);
								int int8 = this.points.getQuick(int6 + 1);
								int int9 = this.points.getQuick(int6 + 2);
								int int10 = this.points.getQuick(int6 + 3);
								if (LIANG_BARSKY.lineRectIntersect((float)int7, (float)int8, (float)(int9 - int7), (float)(int10 - int8), (float)int1, (float)int2, (float)(int1 + int4), (float)(int2 + int5))) {
									return true;
								}
							}

							return false;
						}
					} else {
						return true;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		public boolean difference(int int1, int int2, int int3, int int4, int int5, ArrayList arrayList) {
			arrayList.clear();
			if (!this.intersects(int1, int2, int3, int4, int5)) {
				return false;
			} else if (this.isRectangle()) {
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
				if (this.isPolygon()) {
					if (IsoMetaGrid.s_clipper == null) {
						IsoMetaGrid.s_clipper = new Clipper();
						IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(3072);
					}

					Clipper clipper = IsoMetaGrid.s_clipper;
					ByteBuffer byteBuffer = IsoMetaGrid.s_clipperBuffer;
					byteBuffer.clear();
					int int8;
					for (int8 = 0; int8 < this.points.size(); int8 += 2) {
						byteBuffer.putFloat((float)this.points.getQuick(int8));
						byteBuffer.putFloat((float)this.points.getQuick(int8 + 1));
					}

					clipper.clear();
					clipper.addPath(this.points.size() / 2, byteBuffer, false);
					clipper.clipAABB((float)int1, (float)int2, (float)(int1 + int4), (float)(int2 + int5));
					int8 = clipper.generatePolygons();
					for (int int9 = 0; int9 < int8; ++int9) {
						byteBuffer.clear();
						clipper.getPolygon(int9, byteBuffer);
						short short1 = byteBuffer.getShort();
						if (short1 < 3) {
							byteBuffer.position(byteBuffer.position() + short1 * 4 * 2);
						} else {
							IsoMetaGrid.Zone zone = new IsoMetaGrid.Zone(this.name, this.type, this.x, this.y, this.z, this.w, this.h);
							zone.geometryType = IsoMetaGrid.ZoneGeometryType.Polygon;
							for (int int10 = 0; int10 < short1; ++int10) {
								zone.points.add((int)byteBuffer.getFloat());
								zone.points.add((int)byteBuffer.getFloat());
							}

							arrayList.add(zone);
						}
					}
				}

				if (this.isPolyline()) {
				}

				return true;
			}
		}

		private int pickRandomTriangle() {
			float[] floatArray = this.isPolygon() ? this.getPolygonTriangles() : (this.isPolyline() ? this.getPolylineOutlineTriangles() : null);
			if (floatArray == null) {
				return -1;
			} else {
				int int1 = floatArray.length / 6;
				float float1 = Rand.Next(0.0F, this.totalArea);
				float float2 = 0.0F;
				for (int int2 = 0; int2 < this.triangleAreas.length; ++int2) {
					float2 += this.triangleAreas[int2];
					if (float2 >= float1) {
						return int2;
					}
				}

				return Rand.Next(int1);
			}
		}

		private Vector2 pickRandomPointInTriangle(int int1, Vector2 vector2) {
			float float1 = this.triangles[int1 * 3 * 2];
			float float2 = this.triangles[int1 * 3 * 2 + 1];
			float float3 = this.triangles[int1 * 3 * 2 + 2];
			float float4 = this.triangles[int1 * 3 * 2 + 3];
			float float5 = this.triangles[int1 * 3 * 2 + 4];
			float float6 = this.triangles[int1 * 3 * 2 + 5];
			float float7 = Rand.Next(0.0F, 1.0F);
			float float8 = Rand.Next(0.0F, 1.0F);
			boolean boolean1 = float7 + float8 <= 1.0F;
			float float9;
			float float10;
			if (boolean1) {
				float9 = float7 * (float3 - float1) + float8 * (float5 - float1);
				float10 = float7 * (float4 - float2) + float8 * (float6 - float2);
			} else {
				float9 = (1.0F - float7) * (float3 - float1) + (1.0F - float8) * (float5 - float1);
				float10 = (1.0F - float7) * (float4 - float2) + (1.0F - float8) * (float6 - float2);
			}

			float9 += float1;
			float10 += float2;
			return vector2.set(float9, float10);
		}

		public IsoGameCharacter.Location pickRandomLocation(IsoGameCharacter.Location location) {
			if (this.isPolygon() || this.isPolyline() && this.polylineWidth > 0) {
				int int1 = this.pickRandomTriangle();
				if (int1 == -1) {
					return null;
				} else {
					for (int int2 = 0; int2 < 20; ++int2) {
						Vector2 vector2 = this.pickRandomPointInTriangle(int1, BaseVehicle.allocVector2());
						if (this.contains((int)vector2.x, (int)vector2.y, this.z)) {
							location.set((int)vector2.x, (int)vector2.y, this.z);
							BaseVehicle.releaseVector2(vector2);
							return location;
						}
					}

					return null;
				}
			} else {
				return !this.isPoint() && !this.isPolyline() ? location.set(Rand.Next(this.x, this.x + this.w), Rand.Next(this.y, this.y + this.h), this.z) : null;
			}
		}

		public IsoGridSquare getRandomSquareInZone() {
			IsoGameCharacter.Location location = this.pickRandomLocation((IsoGameCharacter.Location)IsoMetaGrid.TL_Location.get());
			return location == null ? null : IsoWorld.instance.CurrentCell.getGridSquare(location.x, location.y, location.z);
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

		public void setName(String string) {
			this.name = string;
		}

		public String getType() {
			return this.type;
		}

		public void setType(String string) {
			this.type = string;
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

		public int getZ() {
			return this.z;
		}

		public int getHeight() {
			return this.h;
		}

		public int getWidth() {
			return this.w;
		}

		public float getTotalArea() {
			if (!this.isRectangle() && !this.isPoint() && (!this.isPolyline() || this.polylineWidth > 0)) {
				this.getPolygonTriangles();
				this.getPolylineOutlineTriangles();
				return this.totalArea;
			} else {
				return (float)(this.getWidth() * this.getHeight());
			}
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

		public int getClippedSegmentOfPolyline(int int1, int int2, int int3, int int4, double[] doubleArray) {
			if (!this.isPolyline()) {
				return -1;
			} else {
				float float1 = this.polylineWidth % 2 == 0 ? 0.0F : 0.5F;
				for (int int5 = 0; int5 < this.points.size() - 2; int5 += 2) {
					int int6 = this.points.getQuick(int5);
					int int7 = this.points.getQuick(int5 + 1);
					int int8 = this.points.getQuick(int5 + 2);
					int int9 = this.points.getQuick(int5 + 3);
					if (LIANG_BARSKY.lineRectIntersect((float)int6 + float1, (float)int7 + float1, (float)(int8 - int6), (float)(int9 - int7), (float)int1, (float)int2, (float)int3, (float)int4, doubleArray)) {
						return int5 / 2;
					}
				}

				return -1;
			}
		}

		private void checkPolylineOutline() {
			if (this.polylineOutlinePoints == null) {
				if (this.isPolyline()) {
					if (this.polylineWidth > 0) {
						if (IsoMetaGrid.s_clipperOffset == null) {
							IsoMetaGrid.s_clipperOffset = new ClipperOffset();
							IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(3072);
						}

						ClipperOffset clipperOffset = IsoMetaGrid.s_clipperOffset;
						ByteBuffer byteBuffer = IsoMetaGrid.s_clipperBuffer;
						clipperOffset.clear();
						byteBuffer.clear();
						float float1 = this.polylineWidth % 2 == 0 ? 0.0F : 0.5F;
						int int1;
						int int2;
						for (int1 = 0; int1 < this.points.size(); int1 += 2) {
							int int3 = this.points.get(int1);
							int2 = this.points.get(int1 + 1);
							byteBuffer.putFloat((float)int3 + float1);
							byteBuffer.putFloat((float)int2 + float1);
						}

						byteBuffer.flip();
						clipperOffset.addPath(this.points.size() / 2, byteBuffer, ClipperOffset.JoinType.jtMiter.ordinal(), ClipperOffset.EndType.etOpenButt.ordinal());
						clipperOffset.execute((double)((float)this.polylineWidth / 2.0F));
						int1 = clipperOffset.getPolygonCount();
						if (int1 < 1) {
							DebugLog.General.warn("Failed to generate polyline outline");
						} else {
							byteBuffer.clear();
							clipperOffset.getPolygon(0, byteBuffer);
							short short1 = byteBuffer.getShort();
							this.polylineOutlinePoints = new float[short1 * 2];
							for (int2 = 0; int2 < short1; ++int2) {
								this.polylineOutlinePoints[int2 * 2] = byteBuffer.getFloat();
								this.polylineOutlinePoints[int2 * 2 + 1] = byteBuffer.getFloat();
							}
						}
					}
				}
			}
		}

		float isLeft(float float1, float float2, float float3, float float4, float float5, float float6) {
			return (float3 - float1) * (float6 - float2) - (float5 - float1) * (float4 - float2);
		}

		IsoMetaGrid.Zone.PolygonHit isPointInPolygon_WindingNumber(float float1, float float2, int int1) {
			int int2 = 0;
			for (int int3 = 0; int3 < this.points.size(); int3 += 2) {
				int int4 = this.points.getQuick(int3);
				int int5 = this.points.getQuick(int3 + 1);
				int int6 = this.points.getQuick((int3 + 2) % this.points.size());
				int int7 = this.points.getQuick((int3 + 3) % this.points.size());
				if ((float)int5 <= float2) {
					if ((float)int7 > float2 && this.isLeft((float)int4, (float)int5, (float)int6, (float)int7, float1, float2) > 0.0F) {
						++int2;
					}
				} else if ((float)int7 <= float2 && this.isLeft((float)int4, (float)int5, (float)int6, (float)int7, float1, float2) < 0.0F) {
					--int2;
				}
			}

			return int2 == 0 ? IsoMetaGrid.Zone.PolygonHit.Outside : IsoMetaGrid.Zone.PolygonHit.Inside;
		}

		IsoMetaGrid.Zone.PolygonHit isPointInPolyline_WindingNumber(float float1, float float2, int int1) {
			int int2 = 0;
			float[] floatArray = this.polylineOutlinePoints;
			if (floatArray == null) {
				return IsoMetaGrid.Zone.PolygonHit.Outside;
			} else {
				for (int int3 = 0; int3 < floatArray.length; int3 += 2) {
					float float3 = floatArray[int3];
					float float4 = floatArray[int3 + 1];
					float float5 = floatArray[(int3 + 2) % floatArray.length];
					float float6 = floatArray[(int3 + 3) % floatArray.length];
					if (float4 <= float2) {
						if (float6 > float2 && this.isLeft(float3, float4, float5, float6, float1, float2) > 0.0F) {
							++int2;
						}
					} else if (float6 <= float2 && this.isLeft(float3, float4, float5, float6, float1, float2) < 0.0F) {
						--int2;
					}
				}

				return int2 == 0 ? IsoMetaGrid.Zone.PolygonHit.Outside : IsoMetaGrid.Zone.PolygonHit.Inside;
			}
		}

		boolean polygonRectIntersect(int int1, int int2, int int3, int int4) {
			if (this.x >= int1 && this.x + this.w <= int1 + int3 && this.y >= int2 && this.y + this.h <= int2 + int4) {
				return true;
			} else {
				return this.lineSegmentIntersects((float)int1, (float)int2, (float)(int1 + int3), (float)int2) || this.lineSegmentIntersects((float)(int1 + int3), (float)int2, (float)(int1 + int3), (float)(int2 + int4)) || this.lineSegmentIntersects((float)(int1 + int3), (float)(int2 + int4), (float)int1, (float)(int2 + int4)) || this.lineSegmentIntersects((float)int1, (float)(int2 + int4), (float)int1, (float)int2);
			}
		}

		boolean lineSegmentIntersects(float float1, float float2, float float3, float float4) {
			L_lineSegmentIntersects.set(float3 - float1, float4 - float2);
			float float5 = L_lineSegmentIntersects.getLength();
			L_lineSegmentIntersects.normalize();
			float float6 = L_lineSegmentIntersects.x;
			float float7 = L_lineSegmentIntersects.y;
			for (int int1 = 0; int1 < this.points.size(); int1 += 2) {
				float float8 = (float)this.points.getQuick(int1);
				float float9 = (float)this.points.getQuick(int1 + 1);
				float float10 = (float)this.points.getQuick((int1 + 2) % this.points.size());
				float float11 = (float)this.points.getQuick((int1 + 3) % this.points.size());
				float float12 = float1 - float8;
				float float13 = float2 - float9;
				float float14 = float10 - float8;
				float float15 = float11 - float9;
				float float16 = 1.0F / (float15 * float6 - float14 * float7);
				float float17 = (float14 * float13 - float15 * float12) * float16;
				if (float17 >= 0.0F && float17 <= float5) {
					float float18 = (float13 * float6 - float12 * float7) * float16;
					if (float18 >= 0.0F && float18 <= 1.0F) {
						return true;
					}
				}
			}

			if (this.isPointInPolygon_WindingNumber((float1 + float3) / 2.0F, (float2 + float4) / 2.0F, 0) != IsoMetaGrid.Zone.PolygonHit.Outside) {
				return true;
			} else {
				return false;
			}
		}

		boolean polylineOutlineRectIntersect(int int1, int int2, int int3, int int4) {
			if (this.polylineOutlinePoints == null) {
				return false;
			} else if (this.x >= int1 && this.x + this.w <= int1 + int3 && this.y >= int2 && this.y + this.h <= int2 + int4) {
				return true;
			} else {
				return this.polylineOutlineSegmentIntersects((float)int1, (float)int2, (float)(int1 + int3), (float)int2) || this.polylineOutlineSegmentIntersects((float)(int1 + int3), (float)int2, (float)(int1 + int3), (float)(int2 + int4)) || this.polylineOutlineSegmentIntersects((float)(int1 + int3), (float)(int2 + int4), (float)int1, (float)(int2 + int4)) || this.polylineOutlineSegmentIntersects((float)int1, (float)(int2 + int4), (float)int1, (float)int2);
			}
		}

		boolean polylineOutlineSegmentIntersects(float float1, float float2, float float3, float float4) {
			L_lineSegmentIntersects.set(float3 - float1, float4 - float2);
			float float5 = L_lineSegmentIntersects.getLength();
			L_lineSegmentIntersects.normalize();
			float float6 = L_lineSegmentIntersects.x;
			float float7 = L_lineSegmentIntersects.y;
			float[] floatArray = this.polylineOutlinePoints;
			for (int int1 = 0; int1 < floatArray.length; int1 += 2) {
				float float8 = floatArray[int1];
				float float9 = floatArray[int1 + 1];
				float float10 = floatArray[(int1 + 2) % floatArray.length];
				float float11 = floatArray[(int1 + 3) % floatArray.length];
				float float12 = float1 - float8;
				float float13 = float2 - float9;
				float float14 = float10 - float8;
				float float15 = float11 - float9;
				float float16 = 1.0F / (float15 * float6 - float14 * float7);
				float float17 = (float14 * float13 - float15 * float12) * float16;
				if (float17 >= 0.0F && float17 <= float5) {
					float float18 = (float13 * float6 - float12 * float7) * float16;
					if (float18 >= 0.0F && float18 <= 1.0F) {
						return true;
					}
				}
			}

			if (this.isPointInPolyline_WindingNumber((float1 + float3) / 2.0F, (float2 + float4) / 2.0F, 0) != IsoMetaGrid.Zone.PolygonHit.Outside) {
				return true;
			} else {
				return false;
			}
		}

		private boolean isClockwise() {
			if (!this.isPolygon()) {
				return false;
			} else {
				float float1 = 0.0F;
				for (int int1 = 0; int1 < this.points.size(); int1 += 2) {
					int int2 = this.points.getQuick(int1);
					int int3 = this.points.getQuick(int1 + 1);
					int int4 = this.points.getQuick((int1 + 2) % this.points.size());
					int int5 = this.points.getQuick((int1 + 3) % this.points.size());
					float1 += (float)((int4 - int2) * (int5 + int3));
				}

				return (double)float1 > 0.0;
			}
		}

		public float[] getPolygonTriangles() {
			if (this.triangles != null) {
				return this.triangles;
			} else if (this.bTriangulateFailed) {
				return null;
			} else if (!this.isPolygon()) {
				return null;
			} else {
				if (IsoMetaGrid.s_clipper == null) {
					IsoMetaGrid.s_clipper = new Clipper();
					IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(3072);
				}

				Clipper clipper = IsoMetaGrid.s_clipper;
				ByteBuffer byteBuffer = IsoMetaGrid.s_clipperBuffer;
				byteBuffer.clear();
				int int1;
				if (this.isClockwise()) {
					for (int1 = this.points.size() - 1; int1 > 0; int1 -= 2) {
						byteBuffer.putFloat((float)this.points.getQuick(int1 - 1));
						byteBuffer.putFloat((float)this.points.getQuick(int1));
					}
				} else {
					for (int1 = 0; int1 < this.points.size(); int1 += 2) {
						byteBuffer.putFloat((float)this.points.getQuick(int1));
						byteBuffer.putFloat((float)this.points.getQuick(int1 + 1));
					}
				}

				clipper.clear();
				clipper.addPath(this.points.size() / 2, byteBuffer, false);
				int1 = clipper.generatePolygons();
				if (int1 < 1) {
					this.bTriangulateFailed = true;
					return null;
				} else {
					byteBuffer.clear();
					int int2 = clipper.triangulate(0, byteBuffer);
					this.triangles = new float[int2 * 2];
					for (int int3 = 0; int3 < int2; ++int3) {
						this.triangles[int3 * 2] = byteBuffer.getFloat();
						this.triangles[int3 * 2 + 1] = byteBuffer.getFloat();
					}

					this.initTriangleAreas();
					return this.triangles;
				}
			}
		}

		private float triangleArea(float float1, float float2, float float3, float float4, float float5, float float6) {
			float float7 = Vector2f.length(float3 - float1, float4 - float2);
			float float8 = Vector2f.length(float5 - float3, float6 - float4);
			float float9 = Vector2f.length(float1 - float5, float2 - float6);
			float float10 = (float7 + float8 + float9) / 2.0F;
			return (float)Math.sqrt((double)(float10 * (float10 - float7) * (float10 - float8) * (float10 - float9)));
		}

		private void initTriangleAreas() {
			int int1 = this.triangles.length / 6;
			this.triangleAreas = new float[int1];
			this.totalArea = 0.0F;
			for (int int2 = 0; int2 < this.triangles.length; int2 += 6) {
				float float1 = this.triangles[int2];
				float float2 = this.triangles[int2 + 1];
				float float3 = this.triangles[int2 + 2];
				float float4 = this.triangles[int2 + 3];
				float float5 = this.triangles[int2 + 4];
				float float6 = this.triangles[int2 + 5];
				float float7 = this.triangleArea(float1, float2, float3, float4, float5, float6);
				this.triangleAreas[int2 / 6] = float7;
				this.totalArea += float7;
			}
		}

		public float[] getPolylineOutlineTriangles() {
			if (this.triangles != null) {
				return this.triangles;
			} else if (this.isPolyline() && this.polylineWidth > 0) {
				if (this.bTriangulateFailed) {
					return null;
				} else {
					this.checkPolylineOutline();
					float[] floatArray = this.polylineOutlinePoints;
					if (floatArray == null) {
						this.bTriangulateFailed = true;
						return null;
					} else {
						if (IsoMetaGrid.s_clipper == null) {
							IsoMetaGrid.s_clipper = new Clipper();
							IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(3072);
						}

						Clipper clipper = IsoMetaGrid.s_clipper;
						ByteBuffer byteBuffer = IsoMetaGrid.s_clipperBuffer;
						byteBuffer.clear();
						int int1;
						if (this.isClockwise()) {
							for (int1 = floatArray.length - 1; int1 > 0; int1 -= 2) {
								byteBuffer.putFloat(floatArray[int1 - 1]);
								byteBuffer.putFloat(floatArray[int1]);
							}
						} else {
							for (int1 = 0; int1 < floatArray.length; int1 += 2) {
								byteBuffer.putFloat(floatArray[int1]);
								byteBuffer.putFloat(floatArray[int1 + 1]);
							}
						}

						clipper.clear();
						clipper.addPath(floatArray.length / 2, byteBuffer, false);
						int1 = clipper.generatePolygons();
						if (int1 < 1) {
							this.bTriangulateFailed = true;
							return null;
						} else {
							byteBuffer.clear();
							int int2 = clipper.triangulate(0, byteBuffer);
							this.triangles = new float[int2 * 2];
							for (int int3 = 0; int3 < int2; ++int3) {
								this.triangles[int3 * 2] = byteBuffer.getFloat();
								this.triangles[int3 * 2 + 1] = byteBuffer.getFloat();
							}

							this.initTriangleAreas();
							return this.triangles;
						}
					}
				}
			} else {
				return null;
			}
		}

		public float getPolylineLength() {
			if (this.isPolyline() && !this.points.isEmpty()) {
				float float1 = 0.0F;
				for (int int1 = 0; int1 < this.points.size() - 2; int1 += 2) {
					int int2 = this.points.get(int1);
					int int3 = this.points.get(int1 + 1);
					int int4 = this.points.get(int1 + 2);
					int int5 = this.points.get(int1 + 3);
					float1 += Vector2f.length((float)(int4 - int2), (float)(int5 - int3));
				}

				return float1;
			} else {
				return 0.0F;
			}
		}

		public void Dispose() {
			this.pickedRZStory = null;
			this.points.clear();
			this.polylineOutlinePoints = null;
			this.spawnedZombies.clear();
			this.triangles = null;
		}

		private static enum PolygonHit {

			OnEdge,
			Inside,
			Outside;

			private static IsoMetaGrid.Zone.PolygonHit[] $values() {
				return new IsoMetaGrid.Zone.PolygonHit[]{OnEdge, Inside, Outside};
			}
		}
	}

	public static final class VehicleZone extends IsoMetaGrid.Zone {
		public static final short VZF_FaceDirection = 1;
		public IsoDirections dir;
		public short flags;

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

	public static enum ZoneGeometryType {

		INVALID,
		Point,
		Polyline,
		Polygon;

		private static IsoMetaGrid.ZoneGeometryType[] $values() {
			return new IsoMetaGrid.ZoneGeometryType[]{INVALID, Point, Polyline, Polygon};
		}
	}

	public static final class RoomTone {
		public int x;
		public int y;
		public int z;
		public String enumValue;
		public boolean entireBuilding;
	}

	public static final class Trigger {
		public BuildingDef def;
		public int triggerRange;
		public int zombieExclusionRange;
		public String type;
		public boolean triggered = false;
		public KahluaTable data;

		public Trigger(BuildingDef buildingDef, int int1, int int2, String string) {
			this.def = buildingDef;
			this.triggerRange = int1;
			this.zombieExclusionRange = int2;
			this.type = string;
			this.data = LuaManager.platform.newTable();
		}

		public KahluaTable getModData() {
			return this.data;
		}
	}
}
