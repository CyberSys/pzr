package zombie.ui;

import zombie.core.Core;
import zombie.core.textures.Texture;


public class DoubleSizer extends UIElement {
	boolean clicked = false;
	UIElement display;
	Texture highlight;
	Texture highlight2;
	boolean mouseOver = false;
	Texture texture;
	Texture texture2;

	public DoubleSizer(int int1, int int2, String string, String string2, String string3, String string4) {
		this.display = this.display;
		this.texture = Texture.getSharedTexture("media/ui/" + string + ".png");
		this.highlight2 = Texture.getSharedTexture("media/ui/" + string4 + ".png");
		this.texture2 = Texture.getSharedTexture("media/ui/" + string3 + ".png");
		this.highlight = Texture.getSharedTexture("media/ui/" + string2 + ".png");
		this.x = (double)int1;
		this.y = (double)int2;
		this.width = (float)this.texture.getWidth();
		this.height = (float)this.texture.getHeight();
	}

	public Boolean onMouseDown(int int1, int int2) {
		this.clicked = true;
		return Boolean.TRUE;
	}

	public Boolean onMouseMove(int int1, int int2) {
		this.mouseOver = true;
		return Boolean.TRUE;
	}

	public void onMouseMoveOutside(int int1, int int2) {
		this.clicked = false;
		this.mouseOver = false;
	}

	public Boolean onMouseUp(int int1, int int2) {
		if (this.clicked) {
			Core.getInstance().doubleSizeToggle();
		}

		this.clicked = false;
		return Boolean.TRUE;
	}

	public void render() {
		if (this.clicked) {
			this.DrawTextureScaled(this.highlight, 0.0, 0.0, (double)this.highlight.getWidth(), (double)this.highlight.getHeight(), 1.0);
		} else if (this.mouseOver) {
			this.DrawTextureScaled(this.texture, 0.0, 0.0, (double)this.texture.getWidth(), (double)this.texture.getHeight(), 1.0);
		} else {
			this.DrawTextureScaled(this.texture, 0.0, 0.0, (double)this.texture.getWidth(), (double)this.texture.getHeight(), 0.8500000238418579);
		}

		super.render();
	}

	public void update() {
		super.update();
	}
}
