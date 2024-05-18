package zombie.characters.CharacterTimedActions;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.LosUtil;
import zombie.ui.UIManager;


public class DoBuildAction extends BaseAction {
	IsoGridSquare square = null;
	IsoObject object = null;

	public DoBuildAction(IsoGameCharacter gameCharacter, IsoObject object, IsoGridSquare square) {
		super(gameCharacter);
		this.square = square;
		this.object = object;
		this.MaxTime = 100;
		if (gameCharacter.HasTrait("Handy")) {
			this.MaxTime = 55;
		}

		this.MaxTime = (int)((float)this.MaxTime * gameCharacter.getBarricadeTimeMod());
	}

	public DoBuildAction(IsoGameCharacter gameCharacter, IsoGridSquare square) {
		super(gameCharacter);
		this.square = square;
		this.MaxTime = 100;
		if (gameCharacter.HasTrait("Handy")) {
			this.MaxTime = 55;
		}

		this.MaxTime = (int)((float)this.MaxTime * gameCharacter.getBarricadeTimeMod());
	}

	public boolean valid() {
		return this.chr.hasEquipped("Hammer");
	}

	public void start() {
		this.SoundEffect = this.chr.getEmitter().playSound("PZ_Hammer", true);
	}

	public void perform() {
		this.square.getSpecialObjects().add(this.object);
		this.square.getObjects().add(this.object);
		LosUtil.cachecleared[IsoPlayer.getPlayerIndex()] = true;
		IsoGridSquare.setRecalcLightTime(-1);
		UIManager.getDragInventory().Use(true);
	}
}
