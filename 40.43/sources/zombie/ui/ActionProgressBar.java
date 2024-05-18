package zombie.ui;

import zombie.core.Color;
import zombie.core.textures.Texture;
import zombie.core.textures.TexturePackPage;


public class ActionProgressBar extends UIElement {
	Texture background = TexturePackPage.getTexture("BuildBar_Bkg");
	Texture foreground = TexturePackPage.getTexture("BuildBar_Bar");
	float deltaValue = 1.0F;
	public int delayHide = 0;

	public ActionProgressBar(int int1, int int2) {
		this.x = (double)int1;
		this.y = (double)int2;
		this.width = (float)this.background.getWidth();
		this.height = (float)this.background.getHeight();
		this.followGameWorld = true;
	}

	public void render() {
		if (this.isVisible()) {
			this.DrawUVSliceTexture(this.background, 0.0, 0.0, (double)this.background.getWidth(), (double)this.background.getHeight(), Color.white, 0.0, 0.0, 1.0, 1.0);
			this.DrawUVSliceTexture(this.foreground, 3.0, 0.0, (double)this.foreground.getWidth(), (double)this.foreground.getHeight(), Color.white, 0.0, 0.0, (double)this.deltaValue, 1.0);
		}
	}

	public void setValue(float float1) {
		this.deltaValue = float1;
	}

	public float getValue() {
		return this.deltaValue;
	}
}
