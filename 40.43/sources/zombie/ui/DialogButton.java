package zombie.ui;

import zombie.Lua.LuaManager;
import zombie.core.Color;
import zombie.core.textures.Texture;
import zombie.core.textures.TexturePackPage;


public class DialogButton extends UIElement {
	public boolean clicked = false;
	public UIElement MessageTarget;
	public boolean mouseOver = false;
	public String name;
	public String text;
	Texture downLeft;
	Texture downMid;
	Texture downRight;
	float origX;
	Texture upLeft;
	Texture upMid;
	Texture upRight;
	private UIEventHandler MessageTarget2 = null;

	public DialogButton(UIElement uIElement, float float1, float float2, String string, String string2) {
		this.x = (double)float1;
		this.y = (double)float2;
		this.origX = float1;
		this.MessageTarget = uIElement;
		this.upLeft = TexturePackPage.getTexture("ButtonL_Up");
		this.upMid = TexturePackPage.getTexture("ButtonM_Up");
		this.upRight = TexturePackPage.getTexture("ButtonR_Up");
		this.downLeft = TexturePackPage.getTexture("ButtonL_Down");
		this.downMid = TexturePackPage.getTexture("ButtonM_Down");
		this.downRight = TexturePackPage.getTexture("ButtonR_Down");
		this.name = string2;
		this.text = string;
		this.width = (float)TextManager.instance.MeasureStringX(UIFont.Small, string);
		this.width += 8.0F;
		if (this.width < 40.0F) {
			this.width = 40.0F;
		}

		this.height = (float)this.downMid.getHeight();
		this.x -= (double)(this.width / 2.0F);
	}

	public DialogButton(UIEventHandler uIEventHandler, int int1, int int2, String string, String string2) {
		this.x = (double)int1;
		this.y = (double)int2;
		this.origX = (float)int1;
		this.MessageTarget2 = uIEventHandler;
		this.upLeft = TexturePackPage.getTexture("ButtonL_Up");
		this.upMid = TexturePackPage.getTexture("ButtonM_Up");
		this.upRight = TexturePackPage.getTexture("ButtonR_Up");
		this.downLeft = TexturePackPage.getTexture("ButtonL_Down");
		this.downMid = TexturePackPage.getTexture("ButtonM_Down");
		this.downRight = TexturePackPage.getTexture("ButtonR_Down");
		this.name = string2;
		this.text = string;
		this.width = (float)TextManager.instance.MeasureStringX(UIFont.Small, string);
		this.width += 8.0F;
		if (this.width < 40.0F) {
			this.width = 40.0F;
		}

		this.height = (float)this.downMid.getHeight();
		this.x -= (double)(this.width / 2.0F);
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (!this.isVisible()) {
			return false;
		} else {
			if (this.getTable() != null && this.getTable().rawget("onMouseDown") != null) {
				Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onMouseDown"), this.table, double1, double2);
			}

			this.clicked = true;
			return Boolean.TRUE;
		}
	}

	public Boolean onMouseMove(double double1, double double2) {
		this.mouseOver = true;
		if (this.getTable() != null && this.getTable().rawget("onMouseMove") != null) {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onMouseMove"), this.table, double1, double2);
		}

		return Boolean.TRUE;
	}

	public void onMouseMoveOutside(double double1, double double2) {
		this.clicked = false;
		if (this.getTable() != null && this.getTable().rawget("onMouseMoveOutside") != null) {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onMouseMoveOutside"), this.table, double1, double2);
		}

		this.mouseOver = false;
	}

	public Boolean onMouseUp(double double1, double double2) {
		if (this.getTable() != null && this.getTable().rawget("onMouseUp") != null) {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onMouseUp"), this.table, double1, double2);
		}

		if (this.clicked) {
			if (this.MessageTarget2 != null) {
				this.MessageTarget2.Selected(this.name, 0, 0);
			} else if (this.MessageTarget != null) {
				this.MessageTarget.ButtonClicked(this.name);
			}
		}

		this.clicked = false;
		return Boolean.TRUE;
	}

	public void render() {
		if (this.isVisible()) {
			boolean boolean1 = false;
			if (this.clicked) {
				this.DrawTexture(this.downLeft, 0.0, 0.0, 1.0);
				this.DrawTextureScaledCol(this.downMid, (double)this.downLeft.getWidth(), 0.0, (double)((int)(this.getWidth() - (double)(this.downLeft.getWidth() * 2))), (double)this.downLeft.getHeight(), new Color(255, 255, 255, 255));
				this.DrawTexture(this.downRight, (double)((int)(this.getWidth() - (double)this.downRight.getWidth())), 0.0, 1.0);
				this.DrawTextCentre(this.text, this.getWidth() / 2.0, 1.0, 1.0, 1.0, 1.0, 1.0);
			} else {
				this.DrawTexture(this.upLeft, 0.0, 0.0, 1.0);
				this.DrawTextureScaledCol(this.upMid, (double)this.downLeft.getWidth(), 0.0, (double)((int)(this.getWidth() - (double)(this.downLeft.getWidth() * 2))), (double)this.downLeft.getHeight(), new Color(255, 255, 255, 255));
				this.DrawTexture(this.upRight, (double)((int)(this.getWidth() - (double)this.downRight.getWidth())), 0.0, 1.0);
				this.DrawTextCentre(this.text, this.getWidth() / 2.0, 0.0, 1.0, 1.0, 1.0, 1.0);
			}

			super.render();
		}
	}
}
