package zombie.iso.objects;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.network.GameServer;


public class IsoWindowFrame {
	private static IsoWindowFrame.Direction getDirection(IsoObject object) {
		if (!(object instanceof IsoWindow) && !(object instanceof IsoThumpable)) {
			if (object != null && object.getProperties() != null && object.getObjectIndex() != -1) {
				if (object.getProperties().Is(IsoFlagType.WindowN)) {
					return IsoWindowFrame.Direction.NORTH;
				} else {
					return object.getProperties().Is(IsoFlagType.WindowW) ? IsoWindowFrame.Direction.WEST : IsoWindowFrame.Direction.INVALID;
				}
			} else {
				return IsoWindowFrame.Direction.INVALID;
			}
		} else {
			return IsoWindowFrame.Direction.INVALID;
		}
	}

	public static boolean isWindowFrame(IsoObject object) {
		return getDirection(object).isValid();
	}

	public static boolean isWindowFrame(IsoObject object, boolean boolean1) {
		IsoWindowFrame.Direction direction = getDirection(object);
		return boolean1 && direction == IsoWindowFrame.Direction.NORTH || !boolean1 && direction == IsoWindowFrame.Direction.WEST;
	}

	public static int countAddSheetRope(IsoObject object) {
		IsoWindowFrame.Direction direction = getDirection(object);
		return direction.isValid() ? IsoWindow.countAddSheetRope(object.getSquare(), direction == IsoWindowFrame.Direction.NORTH) : 0;
	}

	public static boolean canAddSheetRope(IsoObject object) {
		IsoWindowFrame.Direction direction = getDirection(object);
		return direction.isValid() && IsoWindow.canAddSheetRope(object.getSquare(), direction == IsoWindowFrame.Direction.NORTH);
	}

	public static boolean haveSheetRope(IsoObject object) {
		IsoWindowFrame.Direction direction = getDirection(object);
		return direction.isValid() && IsoWindow.isTopOfSheetRopeHere(object.getSquare(), direction == IsoWindowFrame.Direction.NORTH);
	}

	public static boolean addSheetRope(IsoObject object, IsoPlayer player, String string) {
		return !canAddSheetRope(object) ? false : IsoWindow.addSheetRope(player, object.getSquare(), getDirection(object) == IsoWindowFrame.Direction.NORTH, string);
	}

	public static boolean removeSheetRope(IsoObject object, IsoPlayer player) {
		return !haveSheetRope(object) ? false : IsoWindow.removeSheetRope(player, object.getSquare(), getDirection(object) == IsoWindowFrame.Direction.NORTH);
	}

	public static IsoGridSquare getOppositeSquare(IsoObject object) {
		IsoWindowFrame.Direction direction = getDirection(object);
		if (!direction.isValid()) {
			return null;
		} else {
			boolean boolean1 = direction == IsoWindowFrame.Direction.NORTH;
			return object.getSquare().getAdjacentSquare(boolean1 ? IsoDirections.N : IsoDirections.W);
		}
	}

	public static IsoGridSquare getIndoorSquare(IsoObject object) {
		IsoWindowFrame.Direction direction = getDirection(object);
		if (!direction.isValid()) {
			return null;
		} else {
			IsoGridSquare square = object.getSquare();
			if (square.getRoom() != null) {
				return square;
			} else {
				IsoGridSquare square2 = getOppositeSquare(object);
				return square2 != null && square2.getRoom() != null ? square2 : null;
			}
		}
	}

	public static IsoCurtain getCurtain(IsoObject object) {
		IsoWindowFrame.Direction direction = getDirection(object);
		if (!direction.isValid()) {
			return null;
		} else {
			boolean boolean1 = direction == IsoWindowFrame.Direction.NORTH;
			IsoCurtain curtain = object.getSquare().getCurtain(boolean1 ? IsoObjectType.curtainN : IsoObjectType.curtainW);
			if (curtain != null) {
				return curtain;
			} else {
				IsoGridSquare square = getOppositeSquare(object);
				return square == null ? null : square.getCurtain(boolean1 ? IsoObjectType.curtainS : IsoObjectType.curtainE);
			}
		}
	}

	public static IsoGridSquare getAddSheetSquare(IsoObject object, IsoGameCharacter gameCharacter) {
		IsoWindowFrame.Direction direction = getDirection(object);
		if (!direction.isValid()) {
			return null;
		} else {
			boolean boolean1 = direction == IsoWindowFrame.Direction.NORTH;
			if (gameCharacter != null && gameCharacter.getCurrentSquare() != null) {
				IsoGridSquare square = gameCharacter.getCurrentSquare();
				IsoGridSquare square2 = object.getSquare();
				if (boolean1) {
					if (square.getY() < square2.getY()) {
						return square2.getAdjacentSquare(IsoDirections.N);
					}
				} else if (square.getX() < square2.getX()) {
					return square2.getAdjacentSquare(IsoDirections.W);
				}

				return square2;
			} else {
				return null;
			}
		}
	}

	public static void addSheet(IsoObject object, IsoGameCharacter gameCharacter) {
		IsoWindowFrame.Direction direction = getDirection(object);
		if (direction.isValid()) {
			boolean boolean1 = direction == IsoWindowFrame.Direction.NORTH;
			IsoGridSquare square = getIndoorSquare(object);
			if (square == null) {
				square = object.getSquare();
			}

			if (gameCharacter != null) {
				square = getAddSheetSquare(object, gameCharacter);
			}

			if (square != null) {
				IsoObjectType objectType;
				if (square == object.getSquare()) {
					objectType = boolean1 ? IsoObjectType.curtainN : IsoObjectType.curtainW;
				} else {
					objectType = boolean1 ? IsoObjectType.curtainS : IsoObjectType.curtainE;
				}

				if (square.getCurtain(objectType) == null) {
					int int1 = 16;
					if (objectType == IsoObjectType.curtainE) {
						++int1;
					}

					if (objectType == IsoObjectType.curtainS) {
						int1 += 3;
					}

					if (objectType == IsoObjectType.curtainN) {
						int1 += 2;
					}

					int1 += 4;
					IsoCurtain curtain = new IsoCurtain(object.getCell(), square, "fixtures_windows_curtains_01_" + int1, boolean1);
					square.AddSpecialTileObject(curtain);
					if (GameServer.bServer) {
						curtain.transmitCompleteItemToClients();
						if (gameCharacter != null) {
							gameCharacter.sendObjectChange("removeOneOf", new Object[]{"type", "Sheet"});
						}
					} else if (gameCharacter != null) {
						gameCharacter.getInventory().RemoveOneOf("Sheet");
					}
				}
			}
		}
	}

	public static boolean canClimbThrough(IsoObject object, IsoGameCharacter gameCharacter) {
		IsoWindowFrame.Direction direction = getDirection(object);
		if (!direction.isValid()) {
			return false;
		} else if (object.getSquare() == null) {
			return false;
		} else {
			IsoWindow window = object.getSquare().getWindow(direction == IsoWindowFrame.Direction.NORTH);
			if (window != null && window.isBarricaded()) {
				return false;
			} else {
				if (gameCharacter != null) {
					IsoGridSquare square = direction == IsoWindowFrame.Direction.NORTH ? object.getSquare().nav[IsoDirections.N.index()] : object.getSquare().nav[IsoDirections.W.index()];
					if (!IsoWindow.canClimbThroughHelper(gameCharacter, object.getSquare(), square, direction == IsoWindowFrame.Direction.NORTH)) {
						return false;
					}
				}

				return true;
			}
		}
	}

	private static enum Direction {

		INVALID,
		NORTH,
		WEST;

		public boolean isValid() {
			return this != INVALID;
		}
		private static IsoWindowFrame.Direction[] $values() {
			return new IsoWindowFrame.Direction[]{INVALID, NORTH, WEST};
		}
	}
}
