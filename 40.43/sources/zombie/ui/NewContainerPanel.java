package zombie.ui;

import zombie.GameTime;
import zombie.core.textures.Texture;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoStove;


public class NewContainerPanel extends UIElement {
	public Texture BL;
	public Texture BM;
	public Texture BR;
	public InventoryFlowControl Flow;
	public Texture ML;
	public Texture MM;
	public Texture MR;
	public Texture TL;
	public Texture TM;
	public Texture TR;
	DialogButton button;
	IsoStove stove = null;

	public NewContainerPanel(int int1, int int2, int int3, int int4, ItemContainer itemContainer) {
		this.x = (double)int1;
		this.y = (double)int2;
		this.Flow = new InventoryFlowControl(2, 8, int3, int4, itemContainer);
		this.width = (float)(int3 * 32 + 4);
		this.height = (float)(int4 * 32 + 16);
		this.followGameWorld = true;
		this.TL = Texture.getSharedTexture("media/ui/Container_TL.png");
		this.TM = Texture.getSharedTexture("media/ui/Container_TM.png");
		this.TR = Texture.getSharedTexture("media/ui/Container_TR.png");
		this.ML = Texture.getSharedTexture("media/ui/Container_ML.png");
		this.MM = Texture.getSharedTexture("media/ui/Container_MM.png");
		this.MR = Texture.getSharedTexture("media/ui/Container_MR.png");
		this.BL = Texture.getSharedTexture("media/ui/Container_BL.png");
		this.BM = Texture.getSharedTexture("media/ui/Container_BM.png");
		this.BR = Texture.getSharedTexture("media/ui/Container_BR.png");
		this.AddChild(this.Flow);
	}

	public NewContainerPanel(int int1, int int2, int int3, int int4, IsoStove stove) {
		this.stove = stove;
		this.x = (double)int1;
		this.y = (double)int2;
		this.Flow = new InventoryFlowControl(2, 8, int3, int4, stove.container);
		this.width = (float)(int3 * 32 + 4);
		this.height = (float)(int4 * 32 + 16);
		this.followGameWorld = true;
		this.TL = Texture.getSharedTexture("media/ui/Container_TL.png");
		this.TM = Texture.getSharedTexture("media/ui/Container_TM.png");
		this.TR = Texture.getSharedTexture("media/ui/Container_TR.png");
		this.ML = Texture.getSharedTexture("media/ui/Container_ML.png");
		this.MM = Texture.getSharedTexture("media/ui/Container_MM.png");
		this.MR = Texture.getSharedTexture("media/ui/Container_MR.png");
		this.BL = Texture.getSharedTexture("media/ui/Container_BL.png");
		this.BM = Texture.getSharedTexture("media/ui/Container_BM.png");
		this.BR = Texture.getSharedTexture("media/ui/Container_BR.png");
		this.AddChild(this.Flow);
		boolean boolean1 = stove.Activated();
		if (boolean1) {
			this.button = new DialogButton(this, 21.0F, -5.0F, "On", "On");
			this.AddChild(this.button);
		} else {
			this.button = new DialogButton(this, 21.0F, -5.0F, "Off", "Off");
			this.AddChild(this.button);
		}
	}

	public void ButtonClicked(String string) {
		if (GameTime.instance.NightsSurvived < 30) {
			if (string.equals("On")) {
				this.button.name = "Off";
				this.stove.Toggle();
				this.button.text = "Off";
			}

			if (string.equals("Off")) {
				this.button.name = "On";
				this.button.text = "On";
				this.stove.Toggle();
			}
		}
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (this.isVisible() && SpeedControls.instance.getCurrentGameSpeed() != 0) {
			super.onMouseDown(double1, double2);
			IsoGridSquare.setRecalcLightTime(0);
			return Boolean.TRUE;
		} else {
			return Boolean.TRUE;
		}
	}

	public Boolean onMouseUp(double double1, double double2) {
		if (this.isVisible() && SpeedControls.instance.getCurrentGameSpeed() != 0) {
			super.onMouseUp(double1, double2);
			IsoGridSquare.setRecalcLightTime(0);
			return Boolean.TRUE;
		} else {
			return Boolean.TRUE;
		}
	}

	public void render() {
		this.DrawTexture(this.TL, 0.0, 0.0, 0.800000011920929);
		this.DrawTextureScaled(this.TM, 8.0, 0.0, this.getWidth() - 16.0, 8.0, 0.800000011920929);
		this.DrawTexture(this.TR, this.getWidth() - 8.0, 0.0, 0.800000011920929);
		this.DrawTextureScaled(this.ML, 0.0, 8.0, 8.0, this.getHeight() - 16.0, 0.800000011920929);
		this.DrawTextureScaled(this.MM, 8.0, 8.0, this.getWidth() - 16.0, this.getHeight() - 16.0, 0.800000011920929);
		this.DrawTextureScaled(this.MR, this.getWidth() - 8.0, 8.0, 8.0, this.getHeight() - 16.0, 0.800000011920929);
		this.DrawTexture(this.BL, 0.0, this.getHeight() - 8.0, 0.800000011920929);
		this.DrawTextureScaled(this.BM, 8.0, this.getHeight() - 8.0, this.getWidth() - 16.0, 8.0, 0.800000011920929);
		this.DrawTexture(this.BR, this.getWidth() - 8.0, this.getHeight() - 8.0, 0.800000011920929);
		super.render();
	}
}
