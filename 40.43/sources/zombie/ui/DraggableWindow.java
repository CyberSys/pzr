package zombie.ui;

import java.io.FileNotFoundException;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.textures.Texture;
import zombie.core.textures.TexturePackPage;


public class DraggableWindow extends UIElement {
	public float alpha = 1.0F;
	public int clickX = 0;
	public int clickY = 0;
	public boolean closing = false;
	public boolean moving = false;
	public int rwidth = 256;
	Texture background = null;
	AngelCodeFont font = new AngelCodeFont("media/zombiefont2.fnt", "zombiefont2_0");
	Texture titlebar = null;

	public DraggableWindow(int int1, int int2, String string, String string2) throws FileNotFoundException {
		this.background = TexturePackPage.getTexture(string2);
		this.titlebar = TexturePackPage.getTexture(string);
		this.x = (double)int1;
		this.y = (double)int2;
		this.width = 256.0F;
		this.height = 256.0F;
		this.alpha = 0.75F;
		this.visible = false;
	}

	public Boolean onMouseDown(double double1, double double2) {
		super.onMouseDown(double1, double2);
		if (double2 < 16.0 && double1 < (double)(this.rwidth - 20)) {
			this.clickX = (int)double1;
			this.clickY = (int)double2;
			this.moving = true;
			this.setCapture(true);
		} else if (double2 < 16.0 && double1 < this.getWidth()) {
			this.closing = true;
		}

		return Boolean.TRUE;
	}

	public Boolean onMouseMove(double double1, double double2) {
		super.onMouseMove(double1, double2);
		if (this.moving) {
			this.setX(this.getX() + double1);
			this.setY(this.getY() + double2);
		}

		return Boolean.FALSE;
	}

	public Boolean onMouseUp(double double1, double double2) {
		super.onMouseUp(double1, double2);
		if (double2 < 16.0 && double1 >= (double)(this.rwidth - 20) && this.closing) {
			this.setVisible(false);
		}

		this.moving = false;
		this.setCapture(false);
		return Boolean.TRUE;
	}

	public void render() {
		if (this.background != null) {
			if (this.moving) {
				this.DrawTexture(this.titlebar, 0.0, 0.0, 1.0);
			} else {
				this.DrawTexture(this.titlebar, 0.0, 0.0, 0.75);
			}

			this.DrawTexture(this.background, 0.0, 16.0, 0.75);
			super.render();
		}
	}

	public void update() {
		super.update();
	}
}
