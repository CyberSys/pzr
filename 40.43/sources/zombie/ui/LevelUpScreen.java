package zombie.ui;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;


public class LevelUpScreen extends NewWindow implements UIEventHandler {
	ArrayList cats = new ArrayList();
	DialogButton but;
	boolean bDirty = true;

	public LevelUpScreen(int int1, int int2) {
		super(int1, int2, 10, 10, true);
		this.IgnoreLossControl = true;
		this.ResizeToFitY = false;
		this.width = 820.0F;
		this.height = 600.0F;
		int int3 = 40;
		for (int int4 = 0; int4 < PerkFactory.PerkList.size(); ++int4) {
			PerkFactory.Perk perk = (PerkFactory.Perk)PerkFactory.PerkList.get(int4);
			if (perk.parent == PerkFactory.Perks.None) {
				LevelUpCategory levelUpCategory = new LevelUpCategory(perk.type);
				levelUpCategory.x = 10.0;
				levelUpCategory.y = (double)int3;
				this.AddChild(levelUpCategory);
				int3 += 80;
				this.cats.add(levelUpCategory);
			}
		}

		this.height = (float)(int3 + 8 + 24);
		this.but = new DialogButton(this, (float)(this.getWidth().intValue() - 30), (float)(this.getHeight().intValue() - 24), "Done", "done");
		this.AddChild(this.but);
	}

	public void ButtonClicked(String string) {
		if (string.equals("done") || string.equals("close")) {
			if (!IsoPlayer.instance.getCanUpgradePerk().isEmpty() && IsoPlayer.getInstance().getNumberOfPerksToPick() > 0) {
				UIManager.DoModal("close", "You still have skills points available.", false, this);
			} else {
				this.setVisible(false);
				UIManager.getSpeedControls().SetCurrentGameSpeed(3);
			}
		}
	}

	public void init() {
	}

	public void update() {
		if (this.bDirty && this.visible) {
			this.reset();
		}

		super.update();
	}

	public void render() {
		super.render();
		if (this.isVisible()) {
			this.DrawText(UIFont.Small, "Skills to pick: " + IsoPlayer.getInstance().getNumberOfPerksToPick(), 10.0, 23.0, 1.0, 1.0, 1.0, 1.0);
		}
	}

	public void DoubleClick(String string, int int1, int int2) {
	}

	public void Selected(String string, int int1, int int2) {
	}

	public void reset() {
		if (!this.visible) {
			this.bDirty = true;
		} else if (this.bDirty) {
			for (int int1 = 0; int1 < this.cats.size(); ++int1) {
				((LevelUpCategory)this.cats.get(int1)).reset();
			}

			this.bDirty = false;
		}
	}

	public void ModalClick(String string, String string2) {
		if (string.equals("close") && string2.equals("Yes")) {
			this.setVisible(false);
			UIManager.getSpeedControls().SetCurrentGameSpeed(3);
		}
	}

	public void resetAllButton() {
		for (int int1 = 0; int1 < this.cats.size(); ++int1) {
			LevelUpCategory levelUpCategory = (LevelUpCategory)this.cats.get(int1);
			for (int int2 = 0; int2 < levelUpCategory.getPerks().length; ++int2) {
				PerkButton[] perkButtonArray = levelUpCategory.getPerks()[int2];
				for (int int3 = 0; int3 < perkButtonArray.length; ++int3) {
					PerkButton perkButton = perkButtonArray[int3];
					if (!perkButton.bPicked) {
						perkButton.bAvailable = false;
					}
				}
			}
		}
	}
}
