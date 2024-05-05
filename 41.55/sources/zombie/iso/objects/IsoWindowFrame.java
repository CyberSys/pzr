package zombie.iso.objects;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;


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
