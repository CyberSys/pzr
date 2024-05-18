package zombie.ui;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.core.Color;
import zombie.core.textures.Texture;


public class LevelUpCategory extends UIElement implements UIEventHandler {
	public PerkFactory.Perk perk;
	public PerkFactory.Perks perkType;
	public ArrayList subPerks = new ArrayList();
	PerkButton[][] perkbuttons;
	private String chosenButtonPerk = "";
	static Texture white;
	PerkFactory.Perks chosenperk;

	public PerkButton[][] getPerks() {
		return this.perkbuttons;
	}

	public void reset() {
		if (white == null) {
			white = Texture.getSharedTexture("white.png");
		}

		this.subPerks.clear();
		this.getControls().clear();
		this.perk = (PerkFactory.Perk)PerkFactory.PerkMap.get(this.perkType);
		this.setWidth(800.0);
		this.setHeight(80.0);
		int int1;
		for (int1 = 0; int1 < PerkFactory.PerkList.size(); ++int1) {
			PerkFactory.Perk perk = (PerkFactory.Perk)PerkFactory.PerkList.get(int1);
			if (perk.parent == this.perkType) {
				this.subPerks.add(perk);
			}
		}

		this.perkbuttons = new PerkButton[this.subPerks.size()][5];
		for (int1 = 0; int1 < this.subPerks.size(); ++int1) {
			for (int int2 = 0; int2 < 5; ++int2) {
				boolean boolean1 = IsoPlayer.getInstance().getCanUpgradePerk().contains(((PerkFactory.Perk)this.subPerks.get(int1)).type);
				int int3 = IsoPlayer.getInstance().getPerkLevel(((PerkFactory.Perk)this.subPerks.get(int1)).type);
				if (int2 > int3) {
					boolean1 = false;
				}

				if (IsoPlayer.getInstance().getNumberOfPerksToPick() <= 0) {
					boolean1 = false;
				}

				boolean boolean2 = int3 > int2;
				this.perkbuttons[int1][int2] = new PerkButton(((PerkFactory.Perk)this.subPerks.get(int1)).name, 0, 0, white, boolean1, boolean2, this);
				this.perkbuttons[int1][int2].setWidth(16.0);
				this.perkbuttons[int1][int2].setHeight(16.0);
				this.AddChild(this.perkbuttons[int1][int2]);
			}
		}
	}

	public LevelUpCategory(PerkFactory.Perks perks) {
		this.perkType = perks;
		this.reset();
	}

	public void render() {
		this.DrawTextureScaledCol(white, 0.0, 0.0, this.getWidth(), this.getHeight(), new Color(0.3F, 0.3F, 0.3F, 0.2F));
		this.DrawTextureScaledCol(white, 0.0, 0.0, 172.0, this.getHeight(), new Color(0.3F, 0.3F, 0.3F, 0.2F));
		this.DrawText(UIFont.Large, this.perk.name.toUpperCase(), 64.0, this.getHeight() / 2.0 - 12.0, 1.0, 1.0, 1.0, 1.0);
		int int1 = 288;
		int int2 = 20;
		for (int int3 = 0; int3 < this.subPerks.size(); ++int3) {
			PerkFactory.Perk perk = (PerkFactory.Perk)this.subPerks.get(int3);
			this.DrawTextRight(UIFont.Small, perk.name.toUpperCase(), (double)(int1 + 3), (double)int2, 1.0, 1.0, 1.0, 1.0);
			int int4 = int1 + 8;
			for (int int5 = 0; int5 < 5; ++int5) {
				this.perkbuttons[int3][int5].setX((double)(int4 + 5));
				this.perkbuttons[int3][int5].setY((double)(int2 - 3));
				int4 += 17;
			}

			int2 += 30;
			if ((double)int2 > this.getHeight() - 24.0) {
				int2 = 20;
				int1 += 192;
			}
		}

		this.DrawTextureScaledCol(white, 0.0, 78.0, this.getWidth(), 1.0, new Color(0.2F, 0.2F, 0.2F, 0.8F));
		this.DrawTextureScaledCol(white, 0.0, 79.0, this.getWidth(), 1.0, new Color(0.0F, 0.0F, 0.0F, 1.0F));
		super.render();
	}

	public void DoubleClick(String string, int int1, int int2) {
	}

	public void ModalClick(String string, String string2) {
		if (string.equals("chooseperk") && string2.equals("Yes")) {
			IsoPlayer.getInstance().LevelPerk(this.chosenperk);
			this.updateButton();
			for (int int1 = 0; int1 < IsoPlayer.getInstance().getCanUpgradePerk().size(); ++int1) {
				if (IsoPlayer.getInstance().getCanUpgradePerk().get(int1) == this.chosenperk) {
					IsoPlayer.getInstance().getCanUpgradePerk().remove(int1);
					PerkFactory.CheckForUnlockedPerks(IsoPlayer.getInstance());
					break;
				}
			}
		}
	}

	private void updateButton() {
		for (int int1 = 0; int1 < this.perkbuttons.length; ++int1) {
			PerkButton[] perkButtonArray = this.perkbuttons[int1];
			for (int int2 = 0; int2 < perkButtonArray.length; ++int2) {
				PerkButton perkButton = perkButtonArray[int2];
				if (!perkButton.name.equals(this.chosenButtonPerk)) {
					perkButton.bAvailable = false;
					break;
				}

				if (perkButton.bAvailable && !perkButton.bPicked) {
					perkButton.bAvailable = false;
					perkButton.bPicked = true;
					break;
				}
			}
		}
	}

	public void Selected(String string, int int1, int int2) {
		PerkFactory.Perks perks = PerkFactory.getPerkFromName(string);
		PerkFactory.Perk perk = (PerkFactory.Perk)PerkFactory.PerkMap.get(perks);
		this.chosenperk = perks;
		this.chosenButtonPerk = perk.name;
		UIManager.DoModal("chooseperk", "Upgrade this skill?", true, this);
	}
}
