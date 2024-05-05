package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.characters.IsoGameCharacter;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;


public final class RDSSuicidePact extends RandomizedDeadSurvivorBase {

	public RDSSuicidePact() {
		this.name = "Suicide Pact";
		this.setChance(7);
		this.setMinimumDays(60);
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getLivingRoomOrKitchen(buildingDef);
		IsoGameCharacter gameCharacter = RandomizedDeadSurvivorBase.createRandomZombieForCorpse(roomDef);
		if (gameCharacter != null) {
			gameCharacter.addVisualDamage("ZedDmg_HEAD_Bullet");
			IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createBodyFromZombie(gameCharacter);
			if (deadBody != null) {
				this.addBloodSplat(deadBody.getSquare(), 4);
				deadBody.setPrimaryHandItem(this.addWeapon("Base.Pistol", true));
				gameCharacter = RandomizedDeadSurvivorBase.createRandomZombieForCorpse(roomDef);
				if (gameCharacter != null) {
					gameCharacter.addVisualDamage("ZedDmg_HEAD_Bullet");
					deadBody = RandomizedDeadSurvivorBase.createBodyFromZombie(gameCharacter);
					if (deadBody != null) {
						this.addBloodSplat(deadBody.getSquare(), 4);
					}
				}
			}
		}
	}
}
