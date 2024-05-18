package zombie.behaviors.survivor.orders;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;


public class FindSafehouse extends OrderSequence {

	public FindSafehouse(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		IsoSurvivor survivor = (IsoSurvivor)gameCharacter;
		this.Orders.add(new GotoBuildingOrder(survivor, survivor.getDescriptor().getGroup().Safehouse));
		this.Orders.add(new SecureSafehouse(survivor));
		this.Orders.add(new LootBuilding(gameCharacter, survivor.getDescriptor().getGroup().Safehouse, LootBuilding.LootStyle.Safehouse));
	}
}
