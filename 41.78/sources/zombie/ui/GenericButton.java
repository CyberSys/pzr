package zombie.ui;

import zombie.Lua.LuaManager;
import zombie.core.textures.Texture;


public final class GenericButton extends UIElement {
	public boolean clicked = false;
	public UIElement MessageTarget;
	public boolean mouseOver = false;
	public String name;
	public String text;
	Texture UpTexture = null;
	Texture DownTexture = null;
	private UIEventHandler MessageTarget2 = null;

	public GenericButton(UIElement uIElement, float float1, float float2, float float3, float float4, String string, String string2, Texture texture, Texture texture2) {
		this.x = (double)float1;
		this.y = (double)float2;
		this.MessageTarget = uIElement;
		this.name = string;
		this.text = string2;
		this.width = float3;
		this.height = float4;
		this.UpTexture = texture;
		this.DownTexture = texture2;
	}

	public GenericButton(UIEventHandler uIEventHandler, float float1, float float2, float float3, float float4, String string, String string2, Texture texture, Texture texture2) {
		this.x = (double)float1;
		this.y = (double)float2;
		this.MessageTarget2 = uIEventHandler;
		this.name = string;
		this.text = string2;
		this.width = float3;
		this.height = float4;
		this.UpTexture = texture;
		this.DownTexture = texture2;
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else {
			if (this.getTable() != null && this.getTable().rawget("onMouseDown") != null) {
				Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onMouseDown"), this.table, double1, double2);
			}

			this.clicked = true;
			return Boolean.TRUE;
		}
	}

	public Boolean onMouseMove(double double1, double double2) {
		if (this.getTable() != null && this.getTable().rawget("onMouseMove") != null) {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onMouseMove"), this.table, double1, double2);
		}

		this.mouseOver = true;
		return Boolean.TRUE;
	}

	public void onMouseMoveOutside(double double1, double double2) {
		if (this.getTable() != null && this.getTable().rawget("onMouseMoveOutside") != null) {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onMouseMoveOutside"), this.table, double1, double2);
		}

		this.clicked = false;
		this.mouseOver = false;
	}

	public Boolean onMouseUp(double double1, double double2) {
		if (this.getTable() != null && this.getTable().rawget("onMouseUp") != null) {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onMouseUp"), this.table, double1, double2);
		}

		if (this.clicked) {
			if (this.MessageTarget2 != null) {
				this.MessageTarget2.Selected(this.name, 0, 0);
			} else {
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
				this.DrawTextureScaled(this.DownTexture, 0.0, 0.0, this.getWidth(), this.getHeight(), 1.0);
				this.DrawTextCentre(this.text, this.getWidth() / 2.0, 1.0, 1.0, 1.0, 1.0, 1.0);
			} else {
				this.DrawTextureScaled(this.UpTexture, 0.0, 0.0, this.getWidth(), this.getHeight(), 1.0);
				this.DrawTextCentre(this.text, this.getWidth() / 2.0, 1.0, 1.0, 1.0, 1.0, 1.0);
			}

			super.render();
		}
	}
}
