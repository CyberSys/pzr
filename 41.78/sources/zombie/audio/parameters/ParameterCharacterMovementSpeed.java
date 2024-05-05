package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoGameCharacter;


public final class ParameterCharacterMovementSpeed extends FMODLocalParameter {
	private final IsoGameCharacter character;
	private ParameterCharacterMovementSpeed.MovementType movementType;

	public ParameterCharacterMovementSpeed(IsoGameCharacter gameCharacter) {
		super("CharacterMovementSpeed");
		this.movementType = ParameterCharacterMovementSpeed.MovementType.Walk;
		this.character = gameCharacter;
	}

	public float calculateCurrentValue() {
		return (float)this.movementType.label;
	}

	public void setMovementType(ParameterCharacterMovementSpeed.MovementType movementType) {
		this.movementType = movementType;
	}

	public static enum MovementType {

		SneakWalk,
		SneakRun,
		Strafe,
		Walk,
		Run,
		Sprint,
		label;

		private MovementType(int int1) {
			this.label = int1;
		}
		private static ParameterCharacterMovementSpeed.MovementType[] $values() {
			return new ParameterCharacterMovementSpeed.MovementType[]{SneakWalk, SneakRun, Strafe, Walk, Run, Sprint};
		}
	}
}
