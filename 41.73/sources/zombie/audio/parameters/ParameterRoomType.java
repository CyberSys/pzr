package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.iso.BuildingDef;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;


public final class ParameterRoomType extends FMODGlobalParameter {
	static ParameterRoomType instance;
	static ParameterRoomType.RoomType roomType = null;

	public ParameterRoomType() {
		super("RoomType");
		instance = this;
	}

	public float calculateCurrentValue() {
		return (float)this.getRoomType().label;
	}

	private ParameterRoomType.RoomType getRoomType() {
		if (roomType != null) {
			return roomType;
		} else {
			IsoGameCharacter gameCharacter = this.getCharacter();
			if (gameCharacter == null) {
				return ParameterRoomType.RoomType.Generic;
			} else {
				BuildingDef buildingDef = gameCharacter.getCurrentBuildingDef();
				if (buildingDef == null) {
					return ParameterRoomType.RoomType.Generic;
				} else {
					IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
					IsoMetaCell metaCell = metaGrid.getCellData(PZMath.fastfloor(gameCharacter.x / 300.0F), PZMath.fastfloor(gameCharacter.y / 300.0F));
					if (metaCell != null && !metaCell.roomTones.isEmpty()) {
						RoomDef roomDef = gameCharacter.getCurrentRoomDef();
						IsoMetaGrid.RoomTone roomTone = null;
						for (int int1 = 0; int1 < metaCell.roomTones.size(); ++int1) {
							IsoMetaGrid.RoomTone roomTone2 = (IsoMetaGrid.RoomTone)metaCell.roomTones.get(int1);
							RoomDef roomDef2 = metaGrid.getRoomAt(roomTone2.x, roomTone2.y, roomTone2.z);
							if (roomDef2 != null) {
								if (roomDef2 == roomDef) {
									return ParameterRoomType.RoomType.valueOf(roomTone2.enumValue);
								}

								if (roomTone2.entireBuilding && roomDef2.building == buildingDef) {
									roomTone = roomTone2;
								}
							}
						}

						if (roomTone != null) {
							return ParameterRoomType.RoomType.valueOf(roomTone.enumValue);
						} else {
							return ParameterRoomType.RoomType.Generic;
						}
					} else {
						return ParameterRoomType.RoomType.Generic;
					}
				}
			}
		}
	}

	private IsoGameCharacter getCharacter() {
		IsoPlayer player = null;
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player2 = IsoPlayer.players[int1];
			if (player2 != null && (player == null || player.isDead() && player2.isAlive() || player.Traits.Deaf.isSet() && !player2.Traits.Deaf.isSet())) {
				player = player2;
			}
		}

		return player;
	}

	public static void setRoomType(int int1) {
		try {
			roomType = ParameterRoomType.RoomType.values()[int1];
		} catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
			roomType = null;
		}
	}

	public static void render(IsoPlayer player) {
		if (instance != null) {
			if (player == instance.getCharacter()) {
				player.drawDebugTextBelow("RoomType : " + instance.getRoomType().name());
			}
		}
	}

	private static enum RoomType {

		Generic,
		Barn,
		Mall,
		Warehouse,
		Prison,
		Church,
		Office,
		Factory,
		label;

		private RoomType(int int1) {
			this.label = int1;
		}
		private static ParameterRoomType.RoomType[] $values() {
			return new ParameterRoomType.RoomType[]{Generic, Barn, Mall, Warehouse, Prison, Church, Office, Factory};
		}
	}
}
