package zombie.behaviors.survivor.orders;

import zombie.behaviors.survivor.orders.LittleTasks.CloseDoor;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.iso.IsoObject;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoomExit;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;


public class SecureSafehouse extends OrderSequence {

	public SecureSafehouse(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		IsoBuilding building = ((IsoSurvivor)gameCharacter).getDescriptor().getGroup().Safehouse;
		for (int int1 = 0; int1 < building.Exits.size(); ++int1) {
			IsoRoomExit roomExit = (IsoRoomExit)building.Exits.get(int1);
			IsoObject object = roomExit.getDoor(gameCharacter.getCell());
			boolean boolean1 = false;
			boolean boolean2 = false;
			boolean boolean3 = false;
			int int2;
			int int3;
			int int4;
			if (roomExit.From == null) {
				int2 = roomExit.To.x;
				int3 = roomExit.To.y;
				int4 = roomExit.To.layer;
			} else {
				int2 = roomExit.x;
				int3 = roomExit.y;
				int4 = roomExit.layer;
			}

			this.Orders.add(new GotoOrder(gameCharacter, int2, int3, int4));
			if (object instanceof IsoDoor) {
				this.Orders.add(new CloseDoor(gameCharacter, (IsoDoor)object));
			} else if (object instanceof IsoThumpable) {
				this.Orders.add(new CloseDoor(gameCharacter, (IsoThumpable)object));
			}

			this.Orders.add(new WaitUntilFollowersArrive(gameCharacter));
			this.Orders.add(new BlockWindows(gameCharacter, building));
		}
	}
}
