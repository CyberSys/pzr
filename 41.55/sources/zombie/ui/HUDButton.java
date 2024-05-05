package zombie.ui;

import zombie.core.textures.Texture;
import zombie.network.GameServer;


public class HUDButton extends UIElement {
	boolean clicked = false;
	UIElement display;
	Texture highlight;
	Texture overicon = null;
	boolean mouseOver = false;
	String name;
	Texture texture;
	UIEventHandler handler;
	public float notclickedAlpha = 0.85F;
	public float clickedalpha = 1.0F;

	public HUDButton(String string, double double1, double double2, String string2, String string3, UIElement uIElement) {
		if (!GameServer.bServer) {
			this.display = uIElement;
			this.name = string;
			if (this.texture == null) {
				this.texture = Texture.getSharedTexture(string2);
				this.highlight = Texture.getSharedTexture(string3);
			}

			this.x = double1;
			this.y = double2;
			this.width = (float)this.texture.getWidth();
			this.height = (float)this.texture.getHeight();
		}
	}

	public HUDButton(String string, float float1, float float2, String string2, String string3, UIEventHandler uIEventHandler) {
		if (!GameServer.bServer) {
			this.texture = Texture.getSharedTexture(string2);
			this.highlight = Texture.getSharedTexture(string3);
			this.handler = uIEventHandler;
			this.name = string;
			if (this.texture == null) {
				this.texture = Texture.getSharedTexture(string2);
				this.highlight = Texture.getSharedTexture(string3);
			}

			this.x = (double)float1;
			this.y = (double)float2;
			this.width = (float)this.texture.getWidth();
			this.height = (float)this.texture.getHeight();
		}
	}

	public HUDButton(String string, float float1, float float2, String string2, String string3, String string4, UIElement uIElement) {
		if (!GameServer.bServer) {
			this.overicon = Texture.getSharedTexture(string4);
			this.display = uIElement;
			this.texture = Texture.getSharedTexture(string2);
			this.highlight = Texture.getSharedTexture(string3);
			this.name = string;
			if (this.texture == null) {
				this.texture = Texture.getSharedTexture(string2);
				this.highlight = Texture.getSharedTexture(string3);
			}

			this.x = (double)float1;
			this.y = (double)float2;
			this.width = (float)this.texture.getWidth();
			this.height = (float)this.texture.getHeight();
		}
	}

	public HUDButton(String string, float float1, float float2, String string2, String string3, String string4, UIEventHandler uIEventHandler) {
		if (!GameServer.bServer) {
			this.texture = Texture.getSharedTexture(string2);
			this.highlight = Texture.getSharedTexture(string3);
			this.overicon = Texture.getSharedTexture(string4);
			this.handler = uIEventHandler;
			this.name = string;
			if (this.texture == null) {
				this.texture = Texture.getSharedTexture(string2);
				this.highlight = Texture.getSharedTexture(string3);
			}

			this.x = (double)float1;
			this.y = (double)float2;
			this.width = (float)this.texture.getWidth();
			this.height = (float)this.texture.getHeight();
		}
	}

	public Boolean onMouseDown(double double1, double double2) {
		this.clicked = true;
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

		if (!this.mouseOver && !this.name.equals(this.display.getClickedValue())) {
			this.DrawTextureScaled(this.texture, 0.0, (double)int1, this.getWidth(), this.getHeight(), (double)this.notclickedAlpha);
		} else {
			this.DrawTextureScaled(this.highlight, 0.0, (double)int1, this.getWidth(), this.getHeight(), (double)this.clickedalpha);
		}

		if (this.overicon != null) {
			this.DrawTextureScaled(this.overicon, 0.0, (double)int1, (double)this.overicon.getWidth(), (double)this.overicon.getHeight(), 1.0);
		}

		super.render();
	}

	public void update() {
		super.update();
	}
}
