package zombie.audio.parameters;

import fmod.fmod.FMODManager;
import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.properties.PropertyContainer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.util.list.PZArrayList;


public final class ParameterFootstepMaterial extends FMODLocalParameter {
	private final IsoGameCharacter character;

	public ParameterFootstepMaterial(IsoGameCharacter gameCharacter) {
		super("FootstepMaterial");
		this.character = gameCharacter;
	}

	public float calculateCurrentValue() {
		return (float)this.getMaterial().label;
	}

	private ParameterFootstepMaterial.FootstepMaterial getMaterial() {
		if (FMODManager.instance.getNumListeners() == 1) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && player != this.character && !player.Traits.Deaf.isSet()) {
					if ((int)player.getZ() < (int)this.character.getZ()) {
						return ParameterFootstepMaterial.FootstepMaterial.Upstairs;
					}

					break;
				}
			}
		}

		Object object = null;
		IsoObject object2 = null;
		IsoGridSquare square = this.character.getCurrentSquare();
		if (square != null) {
			PZArrayList pZArrayList = square.getObjects();
			for (int int2 = 0; int2 < pZArrayList.size(); ++int2) {
				IsoObject object3 = (IsoObject)pZArrayList.get(int2);
				if (!(object3 instanceof IsoWorldInventoryObject)) {
					PropertyContainer propertyContainer = object3.getProperties();
					if (propertyContainer != null) {
						if (propertyContainer.Is(IsoFlagType.solidfloor)) {
							;
						}

						if (propertyContainer.Is("FootstepMaterial")) {
							object2 = object3;
						}
					}
				}
			}
		}

		if (object2 != null) {
			try {
				String string = object2.getProperties().Val("FootstepMaterial");
				return ParameterFootstepMaterial.FootstepMaterial.valueOf(string);
			} catch (IllegalArgumentException illegalArgumentException) {
				boolean boolean1 = true;
			}
		}

		return ParameterFootstepMaterial.FootstepMaterial.Concrete;
	}

	static enum FootstepMaterial {

		Upstairs,
		BrokenGlass,
		Concrete,
		Grass,
		Gravel,
		Puddle,
		Snow,
		Wood,
		Carpet,
		Dirt,
		Sand,
		Ceramic,
		Metal,
		label;

		private FootstepMaterial(int int1) {
			this.label = int1;
		}
		private static ParameterFootstepMaterial.FootstepMaterial[] $values() {
			return new ParameterFootstepMaterial.FootstepMaterial[]{Upstairs, BrokenGlass, Concrete, Grass, Gravel, Puddle, Snow, Wood, Carpet, Dirt, Sand, Ceramic, Metal};
		}
	}
}
