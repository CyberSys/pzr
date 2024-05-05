package zombie.iso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import zombie.core.properties.PropertyContainer;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoRoom;
import zombie.iso.sprite.IsoSprite;


public final class IsoRoofFixer {
	private static final boolean PER_ROOM_MODE = true;
	private static final int MAX_Z = 8;
	private static final int SCAN_RANGE = 3;
	private static final boolean ALWAYS_INVIS_FLOORS = false;
	private static boolean roofTileGlassCacheDirty = true;
	private static boolean roofTileIsGlass = false;
	private static IsoSprite roofTileCache;
	private static int roofTilePlaceFloorIndexCache = 0;
	private static String invisFloor = "invisible_01_0";
	private static Map roofGroups = new HashMap();
	private static IsoRoofFixer.PlaceFloorInfo[] placeFloorInfos = new IsoRoofFixer.PlaceFloorInfo[10000];
	private static int floorInfoIndex = 0;
	private static IsoGridSquare[] sqCache;
	private static IsoRoom workingRoom;
	private static int[] interiorAirSpaces;
	private static final int I_UNCHECKED = 0;
	private static final int I_TRUE = 1;
	private static final int I_FALSE = 2;

	private static void ensureCapacityFloorInfos() {
		if (floorInfoIndex == placeFloorInfos.length) {
			IsoRoofFixer.PlaceFloorInfo[] placeFloorInfoArray = placeFloorInfos;
			placeFloorInfos = new IsoRoofFixer.PlaceFloorInfo[placeFloorInfos.length + 400];
			System.arraycopy(placeFloorInfoArray, 0, placeFloorInfos, 0, placeFloorInfoArray.length);
		}
	}

	private static void setRoofTileCache(IsoObject object) {
		IsoSprite sprite = object != null ? object.sprite : null;
		if (roofTileCache != sprite) {
			roofTileCache = sprite;
			roofTilePlaceFloorIndexCache = 0;
			if (sprite != null && sprite.getProperties() != null && sprite.getProperties().Val("RoofGroup") != null) {
				try {
					int int1 = Integer.parseInt(sprite.getProperties().Val("RoofGroup"));
					if (roofGroups.containsKey(int1)) {
						roofTilePlaceFloorIndexCache = int1;
					}
				} catch (Exception exception) {
				}
			}

			roofTileGlassCacheDirty = true;
		}
	}

	private static boolean isRoofTileCacheGlass() {
		if (roofTileGlassCacheDirty) {
			roofTileIsGlass = false;
			if (roofTileCache != null) {
				PropertyContainer propertyContainer = roofTileCache.getProperties();
				if (propertyContainer != null) {
					String string = propertyContainer.Val("Material");
					roofTileIsGlass = string != null && string.equalsIgnoreCase("glass");
				}
			}

			roofTileGlassCacheDirty = false;
		}

		return roofTileIsGlass;
	}

	public static void FixRoofsAt(IsoGridSquare square) {
		try {
			FixRoofsPerRoomAt(square);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static void FixRoofsPerRoomAt(IsoGridSquare square) {
		floorInfoIndex = 0;
		if (square.getZ() > 0 && !square.TreatAsSolidFloor() && square.getRoom() == null) {
			IsoRoom room = getRoomBelow(square);
			if (room != null && !room.def.isRoofFixed()) {
				resetInteriorSpaceCache();
				workingRoom = room;
				ArrayList arrayList = room.getSquares();
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					IsoGridSquare square2 = (IsoGridSquare)arrayList.get(int1);
					IsoGridSquare square3 = getRoofFloorForColumn(square2);
					if (square3 != null) {
						ensureCapacityFloorInfos();
						placeFloorInfos[floorInfoIndex++].set(square3, roofTilePlaceFloorIndexCache);
					}
				}

				room.def.setRoofFixed(true);
			}
		}

		for (int int2 = 0; int2 < floorInfoIndex; ++int2) {
			placeFloorInfos[int2].square.addFloor((String)roofGroups.get(placeFloorInfos[int2].floorType));
		}
	}

	private static void clearSqCache() {
		for (int int1 = 0; int1 < sqCache.length; ++int1) {
			sqCache[int1] = null;
		}
	}

	private static IsoGridSquare getRoofFloorForColumn(IsoGridSquare square) {
		if (square == null) {
			return null;
		} else {
			IsoCell cell = IsoCell.getInstance();
			int int1 = 0;
			boolean boolean1 = false;
			IsoGridSquare square2;
			for (int int2 = 7; int2 >= square.getZ() + 1; --int2) {
				square2 = cell.getGridSquare(square.x, square.y, int2);
				if (square2 == null) {
					if (int2 == square.getZ() + 1 && int2 > 0 && !isStairsBelow(square.x, square.y, int2)) {
						square2 = IsoGridSquare.getNew(cell, (SliceY)null, square.x, square.y, int2);
						cell.ConnectNewSquare(square2, false);
						square2.EnsureSurroundNotNull();
						square2.RecalcAllWithNeighbours(true);
						sqCache[int1++] = square2;
					}

					boolean1 = true;
				} else if (square2.TreatAsSolidFloor()) {
					if (square2.getRoom() != null) {
						if (boolean1) {
							square2 = IsoGridSquare.getNew(cell, (SliceY)null, square.x, square.y, int2 + 1);
							cell.ConnectNewSquare(square2, false);
							square2.EnsureSurroundNotNull();
							square2.RecalcAllWithNeighbours(true);
							sqCache[int1++] = square2;
						}

						break;
					}

					IsoObject object = square2.getFloor();
					if (object == null || !isObjectRoof(object) || object.getProperties() == null) {
						return null;
					}

					PropertyContainer propertyContainer = object.getProperties();
					if (propertyContainer.Is(IsoFlagType.FloorHeightOneThird) || propertyContainer.Is(IsoFlagType.FloorHeightTwoThirds)) {
						return null;
					}

					IsoGridSquare square3 = cell.getGridSquare(square.x, square.y, int2 - 1);
					if (square3 == null || square3.getRoom() != null) {
						return null;
					}

					boolean1 = false;
				} else {
					if (square2.HasStairsBelow()) {
						break;
					}

					boolean1 = false;
					sqCache[int1++] = square2;
				}
			}

			if (int1 == 0) {
				return null;
			} else {
				for (int int3 = 0; int3 < int1; ++int3) {
					square2 = sqCache[int3];
					if (square2.getRoom() == null && isInteriorAirSpace(square2.getX(), square2.getY(), square2.getZ())) {
						return null;
					}

					if (isRoofAt(square2, true)) {
						return square2;
					}

					for (int int4 = square2.x - 3; int4 <= square2.x + 3; ++int4) {
						for (int int5 = square2.y - 3; int5 <= square2.y + 3; ++int5) {
							if (int4 != square2.x || int5 != square2.y) {
								IsoGridSquare square4 = cell.getGridSquare(int4, int5, square2.z);
								if (square4 != null) {
									IsoObject object2;
									for (int int6 = 0; int6 < square4.getObjects().size(); ++int6) {
										object2 = (IsoObject)square4.getObjects().get(int6);
										if (isObjectRoofNonFlat(object2)) {
											setRoofTileCache(object2);
											return square2;
										}
									}

									IsoGridSquare square5 = cell.getGridSquare(square4.x, square4.y, square4.z + 1);
									if (square5 != null && square5.getObjects().size() > 0) {
										for (int int7 = 0; int7 < square5.getObjects().size(); ++int7) {
											object2 = (IsoObject)square5.getObjects().get(int7);
											if (isObjectRoofFlatFloor(object2)) {
												setRoofTileCache(object2);
												return square2;
											}
										}
									}
								}
							}
						}
					}
				}

				return null;
			}
		}
	}

	private static void FixRoofsPerTileAt(IsoGridSquare square) {
		if (square.getZ() > 0 && !square.TreatAsSolidFloor() && square.getRoom() == null && hasRoomBelow(square) && (isRoofAt(square, true) || scanIsRoofAt(square, true))) {
			if (isRoofTileCacheGlass()) {
				square.addFloor(invisFloor);
			} else {
				square.addFloor("carpentry_02_58");
			}
		}
	}

	private static boolean scanIsRoofAt(IsoGridSquare square, boolean boolean1) {
		if (square == null) {
			return false;
		} else {
			for (int int1 = square.x - 3; int1 <= square.x + 3; ++int1) {
				for (int int2 = square.y - 3; int2 <= square.y + 3; ++int2) {
					if (int1 != square.x || int2 != square.y) {
						IsoGridSquare square2 = square.getCell().getGridSquare(int1, int2, square.z);
						if (square2 != null && isRoofAt(square2, boolean1)) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	private static boolean isRoofAt(IsoGridSquare square, boolean boolean1) {
		if (square == null) {
			return false;
		} else {
			IsoObject object;
			for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
				object = (IsoObject)square.getObjects().get(int1);
				if (isObjectRoofNonFlat(object)) {
					setRoofTileCache(object);
					return true;
				}
			}

			if (boolean1) {
				IsoGridSquare square2 = square.getCell().getGridSquare(square.x, square.y, square.z + 1);
				if (square2 != null && square2.getObjects().size() > 0) {
					for (int int2 = 0; int2 < square2.getObjects().size(); ++int2) {
						object = (IsoObject)square2.getObjects().get(int2);
						if (isObjectRoofFlatFloor(object)) {
							setRoofTileCache(object);
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	private static boolean isObjectRoof(IsoObject object) {
		return object != null && (object.getType() == IsoObjectType.WestRoofT || object.getType() == IsoObjectType.WestRoofB || object.getType() == IsoObjectType.WestRoofM);
	}

	private static boolean isObjectRoofNonFlat(IsoObject object) {
		if (isObjectRoof(object)) {
			PropertyContainer propertyContainer = object.getProperties();
			if (propertyContainer != null) {
				return !propertyContainer.Is(IsoFlagType.solidfloor) || propertyContainer.Is(IsoFlagType.FloorHeightOneThird) || propertyContainer.Is(IsoFlagType.FloorHeightTwoThirds);
			}
		}

		return false;
	}

	private static boolean isObjectRoofFlatFloor(IsoObject object) {
		if (isObjectRoof(object)) {
			PropertyContainer propertyContainer = object.getProperties();
			if (propertyContainer != null && propertyContainer.Is(IsoFlagType.solidfloor)) {
				return !propertyContainer.Is(IsoFlagType.FloorHeightOneThird) && !propertyContainer.Is(IsoFlagType.FloorHeightTwoThirds);
			}
		}

		return false;
	}

	private static boolean hasRoomBelow(IsoGridSquare square) {
		return getRoomBelow(square) != null;
	}

	private static IsoRoom getRoomBelow(IsoGridSquare square) {
		if (square == null) {
			return null;
		} else {
			for (int int1 = square.z - 1; int1 >= 0; --int1) {
				IsoGridSquare square2 = square.getCell().getGridSquare(square.x, square.y, int1);
				if (square2 != null) {
					if (square2.TreatAsSolidFloor() && square2.getRoom() == null) {
						return null;
					}

					if (square2.getRoom() != null) {
						return square2.getRoom();
					}
				}
			}

			return null;
		}
	}

	private static boolean isStairsBelow(int int1, int int2, int int3) {
		if (int3 == 0) {
			return false;
		} else {
			IsoCell cell = IsoCell.getInstance();
			IsoGridSquare square = cell.getGridSquare(int1, int2, int3 - 1);
			return square != null && square.HasStairs();
		}
	}

	private static void resetInteriorSpaceCache() {
		for (int int1 = 0; int1 < interiorAirSpaces.length; ++int1) {
			interiorAirSpaces[int1] = 0;
		}
	}

	private static boolean isInteriorAirSpace(int int1, int int2, int int3) {
		if (interiorAirSpaces[int3] != 0) {
			return interiorAirSpaces[int3] == 1;
		} else {
			ArrayList arrayList = workingRoom.getSquares();
			boolean boolean1 = false;
			if (arrayList.size() > 0 && int3 > ((IsoGridSquare)arrayList.get(0)).getZ()) {
				for (int int4 = 0; int4 < workingRoom.rects.size(); ++int4) {
					RoomDef.RoomRect roomRect = (RoomDef.RoomRect)workingRoom.rects.get(int4);
					int int5;
					for (int5 = roomRect.getX(); int5 < roomRect.getX2(); ++int5) {
						if (hasRailing(int5, roomRect.getY(), int3, IsoDirections.N) || hasRailing(int5, roomRect.getY2() - 1, int3, IsoDirections.S)) {
							boolean1 = true;
							break;
						}
					}

					if (boolean1) {
						break;
					}

					for (int5 = roomRect.getY(); int5 < roomRect.getY2(); ++int5) {
						if (hasRailing(roomRect.getX(), int5, int3, IsoDirections.W) || hasRailing(roomRect.getX2() - 1, int5, int3, IsoDirections.E)) {
							boolean1 = true;
							break;
						}
					}
				}
			}

			interiorAirSpaces[int3] = boolean1 ? 1 : 2;
			return boolean1;
		}
	}

	private static boolean hasRailing(int int1, int int2, int int3, IsoDirections directions) {
		IsoCell cell = IsoCell.getInstance();
		IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return false;
		} else {
			switch (directions) {
			case N: 
				return square.isHoppableTo(cell.getGridSquare(int1, int2 - 1, int3));
			
			case E: 
				return square.isHoppableTo(cell.getGridSquare(int1 + 1, int2, int3));
			
			case S: 
				return square.isHoppableTo(cell.getGridSquare(int1, int2 + 1, int3));
			
			case W: 
				return square.isHoppableTo(cell.getGridSquare(int1 - 1, int2, int3));
			
			default: 
				return false;
			
			}
		}
	}

	static  {
		roofGroups.put(0, "carpentry_02_57");
		roofGroups.put(1, "roofs_01_22");
		roofGroups.put(2, "roofs_01_54");
		roofGroups.put(3, "roofs_02_22");
		roofGroups.put(4, invisFloor);
		roofGroups.put(5, "roofs_03_22");
		roofGroups.put(6, "roofs_03_54");
		roofGroups.put(7, "roofs_04_22");
		roofGroups.put(8, "roofs_04_54");
		roofGroups.put(9, "roofs_05_22");
		roofGroups.put(10, "roofs_05_54");
	for (int var0 = 0; var0 < placeFloorInfos.length; ++var0) {
		placeFloorInfos[var0] = new IsoRoofFixer.PlaceFloorInfo();
	}

		sqCache = new IsoGridSquare[8];
		interiorAirSpaces = new int[8];
	}

	private static final class PlaceFloorInfo {
		private IsoGridSquare square;
		private int floorType;

		private void set(IsoGridSquare square, int int1) {
			this.square = square;
			this.floorType = int1;
		}
	}
}
