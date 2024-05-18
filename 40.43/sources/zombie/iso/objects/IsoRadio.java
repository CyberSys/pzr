package zombie.iso.objects;

import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.sprite.IsoSprite;


public class IsoRadio extends IsoWaveSignal {

	public IsoRadio(IsoCell cell) {
		super(cell);
	}

	public IsoRadio(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
	}

	public String getObjectName() {
		return "Radio";
	}

	protected void init(boolean boolean1) {
		super.init(boolean1);
	}
}
