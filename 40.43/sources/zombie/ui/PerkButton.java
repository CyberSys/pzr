package zombie.ui;

import zombie.core.textures.Texture;
import zombie.network.GameServer;


public class PerkButton extends UIElement {
	boolean clicked = false;
	UIElement display;
	Texture overicon = null;
	boolean mouseOver = false;
	String name;
	Texture texture;
	boolean bPicked = false;
	boolean bAvailable = false;
	UIEventHandler handler;
	public float notclickedAlpha = 0.85F;
	public float clickedalpha = 1.0F;

	public PerkButton(String string, int int1, int int2, Texture texture, boolean boolean1, boolean boolean2, UIEventHandler uIEventHandler) {
		if (!GameServer.bServer) {
			this.bAvailable = boolean1;
			this.bPicked = boolean2;
			this.texture = texture;
			this.handler = uIEventHandler;
			this.name = string;
			if (this.texture == null) {
				this.texture = texture;
			}

			this.x = (double)int1;
			this.y = (double)int2;
			this.width = (float)this.texture.getWidth();
			this.height = (float)this.texture.getHeight();
		}
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (this.bAvailable && !this.bPicked) {
			this.clicked = true;
		}

		return Boolean.TRUE;
	}

	public Boolean onMouseMove(double double1, double double2) {
		this.mouseOver = true;
		return Boolean.TRUE;
	}

	public void onMouseMoveOutside(double double1, double double2) {
		this.clicked = false;
		if (this.display != null) {
			if (!this.name.equals(this.display.getClickedValue())) {
				this.mouseOver = false;
			}
		}
	}

	public Boolean onMouseUp(double double1, double double2) {
		if (this.clicked) {
			if (this.display != null) {
				this.display.ButtonClicked(this.name);
			} else if (this.handler != null) {
				this.handler.Selected(this.name, 0, 0);
			}
		}

		this.clicked = false;
		return Boolean.TRUE;
	}

	public void render() {
		int int1 = 0;
		if (this.clicked) {
			++int1;
		}

		float float1 = 1.0F;
		if (this.bAvailable && !this.bPicked) {
			float1 = 0.5F;
		}

		if (!this.bAvailable) {
			float1 = 0.2F;
		}

		if (this.bPicked) {
			float1 = 1.0F;
		}

		this.DrawTextureScaled(this.texture, 0.0, (double)int1, this.getWidth(), this.getHeight(), (double)float1);
		super.render();
	}

	public void update() {
		super.update();
	}
}
