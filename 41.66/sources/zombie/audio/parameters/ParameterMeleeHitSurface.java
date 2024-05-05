package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoGameCharacter;


public final class ParameterMeleeHitSurface extends FMODLocalParameter {
	private final IsoGameCharacter character;
	private ParameterMeleeHitSurface.Material material;

	public ParameterMeleeHitSurface(IsoGameCharacter gameCharacter) {
		super("MeleeHitSurface");
		this.material = ParameterMeleeHitSurface.Material.Default;
		this.character = gameCharacter;
	}

	public float calculateCurrentValue() {
		return (float)this.getMaterial().label;
	}

	private ParameterMeleeHitSurface.Material getMaterial() {
		return this.material;
	}

	public void setMaterial(ParameterMeleeHitSurface.Material material) {
		this.material = material;
	}

	public static enum Material {

		Default,
		Body,
		Fabric,
		Glass,
		Head,
		Metal,
		Plastic,
		Stone,
		Wood,
		GarageDoor,
		MetalDoor,
		MetalGate,
		PrisonMetalDoor,
		SlidingGlassDoor,
		WoodDoor,
		WoodGate,
		label;

		private Material(int int1) {
			this.label = int1;
		}
		private static ParameterMeleeHitSurface.Material[] $values() {
			return new ParameterMeleeHitSurface.Material[]{Default, Body, Fabric, Glass, Head, Metal, Plastic, Stone, Wood, GarageDoor, MetalDoor, MetalGate, PrisonMetalDoor, SlidingGlassDoor, WoodDoor, WoodGate};
		}
	}
}
