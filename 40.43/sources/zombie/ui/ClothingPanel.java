package zombie.ui;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;


public class ClothingPanel extends NewWindow {
	boolean DisableHands = true;
	public static ClothingPanel instance;
	VirtualItemSlot HeadItem = null;
	VirtualItemSlot TorsoItem = null;
	VirtualItemSlot HandsItem = null;
	VirtualItemSlot LegsItem = null;
	VirtualItemSlot FeetItem = null;
	IsoGameCharacter ParentChar;

	public ClothingPanel(int int1, int int2, IsoGameCharacter gameCharacter) {
		super(int1, int2, 10, 10, true);
		if (this.DisableHands) {
			this.HeadItem = new VirtualItemSlot("Head_Clothing", 25, 28, "media/ui/ClothingIcons_Head.png", IsoPlayer.getInstance());
			this.TorsoItem = new VirtualItemSlot("Torso_Clothing", 25, 71, "media/ui/ClothingIcons_Torso.png", IsoPlayer.getInstance());
			this.HandsItem = new VirtualItemSlot("Hands_Clothing", 25, 234523345, "media/ui/ClothingIcons_Torso.png", IsoPlayer.getInstance());
			this.LegsItem = new VirtualItemSlot("Legs_Clothing", 25, 114, "media/ui/ClothingIcons_Legs.png", IsoPlayer.getInstance());
			this.FeetItem = new VirtualItemSlot("Feet_Clothing", 25, 157, "media/ui/ClothingIcons_Feet.png", IsoPlayer.getInstance());
		} else {
			this.HeadItem = new VirtualItemSlot("Head_Clothing", 25, 28, "media/ui/ClothingIcons_Head.png", IsoPlayer.getInstance());
			this.TorsoItem = new VirtualItemSlot("Torso_Clothing", 25, 71, "media/ui/ClothingIcons_Torso.png", IsoPlayer.getInstance());
			this.HandsItem = new VirtualItemSlot("Hands_Clothing", 25, 114, "media/ui/ClothingIcons_Torso.png", IsoPlayer.getInstance());
			this.LegsItem = new VirtualItemSlot("Legs_Clothing", 25, 157, "media/ui/ClothingIcons_Legs.png", IsoPlayer.getInstance());
			this.FeetItem = new VirtualItemSlot("Feet_Clothing", 25, 200, "media/ui/ClothingIcons_Feet.png", IsoPlayer.getInstance());
		}

		this.ParentChar = gameCharacter;
		this.ResizeToFitY = false;
		this.visible = false;
		instance = this;
		this.width = 82.0F;
		if (this.DisableHands) {
			this.height = (float)(170 + this.titleRight.getHeight() + 5);
		} else {
			this.height = (float)(210 + this.titleRight.getHeight() + 5);
		}

		boolean boolean1 = true;
		boolean boolean2 = true;
		this.AddChild(this.HeadItem);
		this.AddChild(this.TorsoItem);
		this.AddChild(this.HandsItem);
		this.AddChild(this.LegsItem);
		this.AddChild(this.FeetItem);
	}

	public void render() {
		if (this.isVisible()) {
			super.render();
			this.DrawTextCentre("Clothing", 40.0, 2.0, 1.0, 1.0, 1.0, 1.0);
		}
	}

	public void update() {
		if (this.isVisible()) {
			super.update();
			float float1 = (float)this.getAbsoluteY().intValue();
			Sidebar sidebar = Sidebar.instance;
			float float2 = float1 - (float)(Sidebar.Clothing.getY().intValue() - 70);
			float float3 = (float)Core.getInstance().getOffscreenHeight(0) - float1;
			if (float3 > 0.0F) {
				float2 /= float3;
			} else {
				float2 = 1.0F;
			}

			float2 *= 4.0F;
			float2 = 1.0F - float2;
			if (float2 < 0.0F) {
				float2 = 0.0F;
			}
		}
	}
}
