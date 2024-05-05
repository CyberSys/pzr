package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoGameCharacter;


public final class ParameterEquippedBaggageContainer extends FMODLocalParameter {
	private final IsoGameCharacter character;
	private ParameterEquippedBaggageContainer.ContainerType containerType;

	public ParameterEquippedBaggageContainer(IsoGameCharacter gameCharacter) {
		super("EquippedBaggageContainer");
		this.containerType = ParameterEquippedBaggageContainer.ContainerType.None;
		this.character = gameCharacter;
	}

	public float calculateCurrentValue() {
		return (float)this.containerType.label;
	}

	public void setContainerType(ParameterEquippedBaggageContainer.ContainerType containerType) {
		this.containerType = containerType;
	}

	public void setContainerType(String string) {
		if (string != null) {
			try {
				this.containerType = ParameterEquippedBaggageContainer.ContainerType.valueOf(string);
			} catch (IllegalArgumentException illegalArgumentException) {
			}
		}
	}

	public static enum ContainerType {

		None,
		HikingBag,
		DuffleBag,
		PlasticBag,
		SchoolBag,
		ToteBag,
		GarbageBag,
		label;

		private ContainerType(int int1) {
			this.label = int1;
		}
		private static ParameterEquippedBaggageContainer.ContainerType[] $values() {
			return new ParameterEquippedBaggageContainer.ContainerType[]{None, HikingBag, DuffleBag, PlasticBag, SchoolBag, ToteBag, GarbageBag};
		}
	}
}
