package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoWindow;


public class HangSheet extends Order {
	IsoWindow door = null;
	IsoGameCharacter chr;
	IsoGridSquare sq;

	public HangSheet(IsoGameCharacter gameCharacter, IsoWindow window, IsoGridSquare square) {
		super(gameCharacter);
		this.door = window;
		this.sq = square;
		this.chr = gameCharacter;
	}

	public boolean complete() {
		return true;
	}

	public Behavior.BehaviorResult process() {
		if (!this.character.getInventory().contains("Sheet")) {
			return Behavior.BehaviorResult.Succeeded;
		} else if (this.door.HasCurtains() != null) {
			return Behavior.BehaviorResult.Succeeded;
		} else {
			int int1 = IsoCell.getSheetCurtains();
			IsoDirections directions = IsoDirections.N;
			if (this.door.north && this.sq == this.door.square) {
				directions = IsoDirections.N;
			}

			if (this.door.north && this.sq != this.door.square) {
				directions = IsoDirections.S;
			}

			if (!this.door.north && this.sq == this.door.square) {
				directions = IsoDirections.W;
			}

			if (!this.door.north && this.sq != this.door.square) {
				directions = IsoDirections.E;
			}

			int1 = 16;
			if (directions == IsoDirections.E) {
				++int1;
			}

			if (directions == IsoDirections.S) {
				int1 += 3;
			}

			if (directions == IsoDirections.N) {
				int1 += 2;
			}

			int1 += 4;
			IsoCurtain curtain = new IsoCurtain(this.door.getCell(), this.sq, "TileObjects3_" + int1, this.door.north);
			this.sq.AddSpecialTileObject(curtain);
			if (curtain.open) {
				curtain.ToggleDoorSilent();
			}

			this.character.getInventory().RemoveOneOf("Sheet");
			return Behavior.BehaviorResult.Succeeded;
		}
	}

	public void update() {
	}
}
