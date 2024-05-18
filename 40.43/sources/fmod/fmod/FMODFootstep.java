package fmod.fmod;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;


public class FMODFootstep {
	public String wood;
	public String concrete;
	public String grass;
	public String upstairs;
	public String woodCreak;

	public FMODFootstep(String string, String string2, String string3, String string4) {
		this.grass = string;
		this.wood = string2;
		this.concrete = string3;
		this.upstairs = string4;
		this.woodCreak = "HumanFootstepFloorCreaking";
	}

	public boolean isUpstairs(IsoGameCharacter gameCharacter) {
		IsoGridSquare square = IsoPlayer.instance.getCurrentSquare();
		return square.getZ() < gameCharacter.getCurrentSquare().getZ();
	}

	public String getSoundToPlay(IsoGameCharacter gameCharacter) {
		if (FMODManager.instance.getNumListeners() == 1) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && player != gameCharacter && !player.HasTrait("Deaf")) {
					if ((int)player.getZ() < (int)gameCharacter.getZ()) {
						return this.upstairs;
					}

					break;
				}
			}
		}

		IsoObject object = gameCharacter.getCurrentSquare().getFloor();
		if (object != null && object.getSprite() != null && object.getSprite().getName() != null) {
			String string = object.getSprite().getName();
			if (string.contains("blends_natural_01")) {
				return this.grass;
			} else if (string.contains("floors_interior_tilesandwood_01_")) {
				int int2 = Integer.parseInt(string.replaceFirst("floors_interior_tilesandwood_01_", ""));
				return int2 > 40 && int2 < 48 ? this.wood : this.concrete;
			} else if (string.startsWith("carpentry_02_")) {
				return this.wood;
			} else {
				return string.contains("interior_carpet_") ? this.wood : this.concrete;
			}
		} else {
			return this.concrete;
		}
	}
}
