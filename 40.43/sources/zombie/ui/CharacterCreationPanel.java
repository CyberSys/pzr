package zombie.ui;

import zombie.core.Core;
import zombie.scripting.ScriptManager;


public class CharacterCreationPanel extends NewWindow {
	public static CharacterCreationPanel instance;

	public CharacterCreationPanel(int int1, int int2) {
		super(int1, int2, 10, 10, false);
		this.ResizeToFitY = false;
		this.visible = false;
		instance = this;
		this.width = 750.0F;
		this.height = 570.0F;
		this.Movable = false;
		boolean boolean1 = true;
		boolean boolean2 = true;
	}

	public void enter() {
		ScriptManager.instance.Trigger("OnPreCharacterCreation");
	}

	public void render() {
		if (this.isVisible()) {
			super.render();
			this.DrawTextCentre("Create your character", this.getWidth() / 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
			this.DrawTextCentre("Viewer", 430.0, 33.0, 1.0, 1.0, 1.0, 1.0);
			this.DrawText("Forename", 532.0, 31.0, 1.0, 1.0, 1.0, 1.0);
			this.DrawText("Surname", 532.0, 72.0, 1.0, 1.0, 1.0, 1.0);
			this.DrawTextRight("Face", 624.0, 133.0, 1.0, 1.0, 1.0, 1.0);
			this.DrawTextRight("Skin-tone", 624.0, 150.0, 1.0, 1.0, 1.0, 1.0);
			this.DrawText("Select a profession (many more to come in future updates)", 30.0, 194.0, 1.0, 1.0, 1.0, 1.0);
			this.DrawTextCentre("Available traits", 438.0, 194.0, 1.0, 1.0, 1.0, 1.0);
			this.DrawTextCentre("Chosen traits", 626.0, 194.0, 1.0, 1.0, 1.0, 1.0);
		}
	}

	public void update() {
		if (this.isVisible()) {
			super.update();
			float float1 = (float)this.getAbsoluteY().intValue();
			float float2 = float1 - 40.0F;
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
