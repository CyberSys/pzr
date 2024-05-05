package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.scripting.objects.Item;


public final class ParameterShoeType extends FMODLocalParameter {
	private static final ItemVisuals tempItemVisuals = new ItemVisuals();
	private final IsoGameCharacter character;
	private ParameterShoeType.ShoeType shoeType = null;

	public ParameterShoeType(IsoGameCharacter gameCharacter) {
		super("ShoeType");
		this.character = gameCharacter;
	}

	public float calculateCurrentValue() {
		if (this.shoeType == null) {
			this.shoeType = this.getShoeType();
		}

		return (float)this.shoeType.label;
	}

	private ParameterShoeType.ShoeType getShoeType() {
		this.character.getItemVisuals(tempItemVisuals);
		Item item = null;
		for (int int1 = 0; int1 < tempItemVisuals.size(); ++int1) {
			ItemVisual itemVisual = (ItemVisual)tempItemVisuals.get(int1);
			Item item2 = itemVisual.getScriptItem();
			if (item2 != null && "Shoes".equals(item2.getBodyLocation())) {
				item = item2;
				break;
			}
		}

		if (item == null) {
			return ParameterShoeType.ShoeType.Barefoot;
		} else {
			String string = item.getName();
			if (!string.contains("Boots") && !string.contains("Wellies")) {
				if (string.contains("FlipFlop")) {
					return ParameterShoeType.ShoeType.FlipFlops;
				} else if (string.contains("Slippers")) {
					return ParameterShoeType.ShoeType.Slippers;
				} else {
					return string.contains("Trainer") ? ParameterShoeType.ShoeType.Sneakers : ParameterShoeType.ShoeType.Shoes;
				}
			} else {
				return ParameterShoeType.ShoeType.Boots;
			}
		}
	}

	public void setShoeType(ParameterShoeType.ShoeType shoeType) {
		this.shoeType = shoeType;
	}

	private static enum ShoeType {

		Barefoot,
		Boots,
		FlipFlops,
		Shoes,
		Slippers,
		Sneakers,
		label;

		private ShoeType(int int1) {
			this.label = int1;
		}
		private static ParameterShoeType.ShoeType[] $values() {
			return new ParameterShoeType.ShoeType[]{Barefoot, Boots, FlipFlops, Shoes, Slippers, Sneakers};
		}
	}
}
