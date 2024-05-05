package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoBrokenGlass;


public final class ParameterFootstepMaterial2 extends FMODLocalParameter {
	private final IsoGameCharacter character;

	public ParameterFootstepMaterial2(IsoGameCharacter gameCharacter) {
		super("FootstepMaterial2");
		this.character = gameCharacter;
	}

	public float calculateCurrentValue() {
		return (float)this.getMaterial().label;
	}

	private ParameterFootstepMaterial2.FootstepMaterial2 getMaterial() {
		if (this.character.getCurrentSquare() == null) {
			return ParameterFootstepMaterial2.FootstepMaterial2.None;
		} else {
			IsoBrokenGlass brokenGlass = this.character.getCurrentSquare().getBrokenGlass();
			if (brokenGlass != null) {
				return ParameterFootstepMaterial2.FootstepMaterial2.BrokenGlass;
			} else {
				float float1 = this.character.getCurrentSquare().getPuddlesInGround();
				if (float1 > 0.5F) {
					return ParameterFootstepMaterial2.FootstepMaterial2.PuddleDeep;
				} else {
					return float1 > 0.1F ? ParameterFootstepMaterial2.FootstepMaterial2.PuddleShallow : ParameterFootstepMaterial2.FootstepMaterial2.None;
				}
			}
		}
	}

	static enum FootstepMaterial2 {

		None,
		BrokenGlass,
		PuddleShallow,
		PuddleDeep,
		label;

		private FootstepMaterial2(int int1) {
			this.label = int1;
		}
		private static ParameterFootstepMaterial2.FootstepMaterial2[] $values() {
			return new ParameterFootstepMaterial2.FootstepMaterial2[]{None, BrokenGlass, PuddleShallow, PuddleDeep};
		}
	}
}
