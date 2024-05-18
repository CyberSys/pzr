package zombie.ui;

import zombie.core.Color;
import zombie.core.textures.Texture;


public class StatBar extends UIElement {
	Texture background;
	Color col;
	float deltaValue = 1.0F;
	Texture foreground;
	boolean vertical = false;

	public StatBar(int int1, int int2, Texture texture, Texture texture2, boolean boolean1, Color color) {
		this.col = color;
		this.vertical = boolean1;
		this.background = texture;
		this.foreground = texture2;
		this.x = (double)int1;
		this.y = (double)int2;
		this.width = (float)texture.getWidth();
		this.height = (float)texture.getHeight();
	}

	public void render() {
		this.DrawUVSliceTexture(this.background, 0.0, 0.0, (double)this.background.getWidth(), (double)this.background.getHeight(), this.col, 0.0, 0.0, 1.0, 1.0);
		if (this.vertical) {
			this.DrawUVSliceTexture(this.foreground, 0.0, 0.0, (double)this.foreground.getWidth(), (double)this.foreground.getHeight(), this.col, 0.0, (double)(1.0F - this.deltaValue), 1.0, 1.0);
		} else {
			this.DrawUVSliceTexture(this.foreground, 0.0, 0.0, (double)this.foreground.getWidth(), (double)this.foreground.getHeight(), this.col, 0.0, 0.0, (double)(1.0F - this.deltaValue), 1.0);
		}
	}

	public void setValue(float float1) {
		this.deltaValue = float1;
	}
}
