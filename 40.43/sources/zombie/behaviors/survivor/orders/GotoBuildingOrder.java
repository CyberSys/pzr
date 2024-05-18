package zombie.behaviors.survivor.orders;

import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoomExit;


public class GotoBuildingOrder extends GotoOrder {
	IsoBuilding b;
	IsoGameCharacter chr;

	public GotoBuildingOrder(IsoGameCharacter gameCharacter, IsoBuilding building) {
		super(gameCharacter);
		this.b = building;
		this.chr = gameCharacter;
		IsoRoomExit roomExit = (IsoRoomExit)building.Exits.get(Rand.Next(building.Exits.size()));
		if (roomExit.From == null) {
			roomExit = roomExit.To;
		}

		IsoGridSquare square = roomExit.From.getFreeTile();
		this.init(square.getX(), square.getY(), square.getZ());
	}

	public boolean complete() {
		return super.complete();
	}
}
