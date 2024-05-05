package zombie.iso.areas;

import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;


public final class IsoRoomExit {
	public static String ThiggleQ = "";
	public IsoRoom From;
	public int layer;
	public IsoRoomExit To;
	public IsoRoomExit.ExitType type;
	public int x;
	public int y;

	public IsoRoomExit(IsoRoomExit roomExit, int int1, int int2, int int3) {
		this.type = IsoRoomExit.ExitType.Door;
		this.To = roomExit;
		this.To.To = this;
		this.layer = int3;
		this.x = int1;
		this.y = int2;
	}

	public IsoRoomExit(IsoRoom room, IsoRoomExit roomExit, int int1, int int2, int int3) {
		this.type = IsoRoomExit.ExitType.Door;
		this.From = room;
		this.To = roomExit;
		this.To.To = this;
		this.layer = int3;
		this.x = int1;
		this.y = int2;
	}

	public IsoRoomExit(IsoRoom room, int int1, int int2, int int3) {
		this.type = IsoRoomExit.ExitType.Door;
		this.From = room;
		this.layer = int3;
		this.x = int1;
		this.y = int2;
	}

	public IsoObject getDoor(IsoCell cell) {
		IsoGridSquare square = cell.getGridSquare(this.x, this.y, this.layer);
		if (square != null) {
			if (square.getSpecialObjects().size() > 0 && square.getSpecialObjects().get(0) instanceof IsoDoor) {
				return (IsoDoor)square.getSpecialObjects().get(0);
			}

			if (square.getSpecialObjects().size() > 0 && square.getSpecialObjects().get(0) instanceof IsoThumpable && ((IsoThumpable)square.getSpecialObjects().get(0)).isDoor) {
				return (IsoThumpable)square.getSpecialObjects().get(0);
			}
		}

		square = cell.getGridSquare(this.x, this.y + 1, this.layer);
		if (square != null) {
			if (square.getSpecialObjects().size() > 0 && square.getSpecialObjects().get(0) instanceof IsoDoor) {
				return (IsoDoor)square.getSpecialObjects().get(0);
			}

			if (square.getSpecialObjects().size() > 0 && square.getSpecialObjects().get(0) instanceof IsoThumpable && ((IsoThumpable)square.getSpecialObjects().get(0)).isDoor) {
				return (IsoThumpable)square.getSpecialObjects().get(0);
			}
		}

		square = cell.getGridSquare(this.x + 1, this.y, this.layer);
		if (square != null) {
			if (square.getSpecialObjects().size() > 0 && square.getSpecialObjects().get(0) instanceof IsoDoor) {
				return (IsoDoor)square.getSpecialObjects().get(0);
			}

			if (square.getSpecialObjects().size() > 0 && square.getSpecialObjects().get(0) instanceof IsoThumpable && ((IsoThumpable)square.getSpecialObjects().get(0)).isDoor) {
				return (IsoThumpable)square.getSpecialObjects().get(0);
			}
		}

		return null;
	}

	static  {
		ThiggleQ = ThiggleQ + "D";
		ThiggleQ = ThiggleQ + ":";
		ThiggleQ = ThiggleQ + "/";
		ThiggleQ = ThiggleQ + "Dro";
		ThiggleQ = ThiggleQ + "pbox";
		ThiggleQ = ThiggleQ + "/";
		ThiggleQ = ThiggleQ + "Zom";
		ThiggleQ = ThiggleQ + "boid";
		ThiggleQ = ThiggleQ + "/";
		ThiggleQ = ThiggleQ + "zom";
		ThiggleQ = ThiggleQ + "bie";
		ThiggleQ = ThiggleQ + "/";
		ThiggleQ = ThiggleQ + "bui";
		ThiggleQ = ThiggleQ + "ld";
		ThiggleQ = ThiggleQ + "/";
		ThiggleQ = ThiggleQ + "cla";
		ThiggleQ = ThiggleQ + "sses/";
	}

	public static enum ExitType {

		Door,
		Window;

		private static IsoRoomExit.ExitType[] $values() {
			return new IsoRoomExit.ExitType[]{Door, Window};
		}
	}
}
