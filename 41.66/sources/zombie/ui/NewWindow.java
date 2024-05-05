package zombie.ui;

import java.util.Iterator;
import java.util.Stack;
import org.lwjgl.util.Rectangle;
import zombie.core.textures.Texture;


public class NewWindow extends UIElement {
	public int clickX = 0;
	public int clickY = 0;
	public int clientH = 0;
	public int clientW = 0;
	public boolean Movable = true;
	public boolean moving = false;
	public int ncclientH = 0;
	public int ncclientW = 0;
	public Stack nestedItems = new Stack();
	public boolean ResizeToFitY = true;
	float alpha = 1.0F;
	Texture dialogBottomLeft = null;
	Texture dialogBottomMiddle = null;
	Texture dialogBottomRight = null;
	Texture dialogLeft = null;
	Texture dialogMiddle = null;
	Texture dialogRight = null;
	Texture titleCloseIcon = null;
	Texture titleLeft = null;
	Texture titleMiddle = null;
	Texture titleRight = null;
	HUDButton closeButton = null;

	public NewWindow(int int1, int int2, int int3, int int4, boolean boolean1) {
		this.x = (double)int1;
		this.y = (double)int2;
		if (int3 < 156) {
			int3 = 156;
		}

		if (int4 < 78) {
			int4 = 78;
		}

		this.width = (float)int3;
		this.height = (float)int4;
		this.titleLeft = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Left.png");
		this.titleMiddle = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Middle.png");
		this.titleRight = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Right.png");
		this.dialogLeft = Texture.getSharedTexture("media/ui/Dialog_Left.png");
		this.dialogMiddle = Texture.getSharedTexture("media/ui/Dialog_Middle.png");
		this.dialogRight = Texture.getSharedTexture("media/ui/Dialog_Right.png");
		this.dialogBottomLeft = Texture.getSharedTexture("media/ui/Dialog_Bottom_Left.png");
		this.dialogBottomMiddle = Texture.getSharedTexture("media/ui/Dialog_Bottom_Middle.png");
		this.dialogBottomRight = Texture.getSharedTexture("media/ui/Dialog_Bottom_Right.png");
		if (boolean1) {
			this.closeButton = new HUDButton("close", (float)(int3 - 16), 2.0F, "media/ui/Dialog_Titlebar_CloseIcon.png", "media/ui/Dialog_Titlebar_CloseIcon.png", "media/ui/Dialog_Titlebar_CloseIcon.png", this);
			this.AddChild(this.closeButton);
		}

		this.clientW = int3;
		this.clientH = int4;
	}

	public void Nest(UIElement uIElement, int int1, int int2, int int3, int int4) {
		this.AddChild(uIElement);
		this.nestedItems.add(new Rectangle(int4, int1, int2, int3));
		uIElement.setX((double)int4);
		uIElement.setY((double)int1);
		uIElement.update();
	}

	public void ButtonClicked(String string) {
		super.ButtonClicked(string);
		if (string.equals("close")) {
			this.setVisible(false);
		}
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else {
			super.onMouseDown(double1, double2);
			if (double2 < 18.0) {
				this.clickX = (int)double1;
				this.clickY = (int)double2;
				if (this.Movable) {
					this.moving = true;
				}

				this.setCapture(true);
			}

			return Boolean.TRUE;
		}
	}

	public void setMovable(boolean boolean1) {
		this.Movable = boolean1;
	}

	public Boolean onMouseMove(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else {
			super.onMouseMove(double1, double2);
			if (this.moving) {
				this.setX(this.getX() + double1);
				this.setY(this.getY() + double2);
			}

			return Boolean.FALSE;
		}
	}

	public void onMouseMoveOutside(double double1, double double2) {
		if (this.isVisible()) {
			super.onMouseMoveOutside(double1, double2);
			if (this.moving) {
				this.setX(this.getX() + double1);
				this.setY(this.getY() + double2);
			}
		}
	}

	public Boolean onMouseUp(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else {
			super.onMouseUp(double1, double2);
			this.moving = false;
			this.setCapture(false);
			return Boolean.TRUE;
		}
	}

	public void render() {
		float float1 = 0.8F * this.alpha;
		byte byte1 = 0;
		byte byte2 = 0;
		this.DrawTexture(this.titleLeft, (double)byte1, (double)byte2, (double)float1);
		this.DrawTexture(this.titleRight, this.getWidth() - (double)this.titleRight.getWidth(), (double)byte2, (double)float1);
		this.DrawTextureScaled(this.titleMiddle, (double)this.titleLeft.getWidth(), (double)byte2, this.getWidth() - (double)(this.titleLeft.getWidth() * 2), (double)this.titleMiddle.getHeight(), (double)float1);
		int int1 = byte2 + this.titleRight.getHeight();
		this.DrawTextureScaled(this.dialogLeft, (double)byte1, (double)int1, (double)this.dialogLeft.getWidth(), this.getHeight() - (double)this.titleLeft.getHeight() - (double)this.dialogBottomLeft.getHeight(), (double)float1);
		this.DrawTextureScaled(this.dialogMiddle, (double)this.dialogLeft.getWidth(), (double)int1, this.getWidth() - (double)(this.dialogRight.getWidth() * 2), this.getHeight() - (double)this.titleLeft.getHeight() - (double)this.dialogBottomLeft.getHeight(), (double)float1);
		this.DrawTextureScaled(this.dialogRight, this.getWidth() - (double)this.dialogRight.getWidth(), (double)int1, (double)this.dialogLeft.getWidth(), this.getHeight() - (double)this.titleLeft.getHeight() - (double)this.dialogBottomLeft.getHeight(), (double)float1);
		int1 = (int)((double)int1 + (this.getHeight() - (double)this.titleLeft.getHeight() - (double)this.dialogBottomLeft.getHeight()));
		this.DrawTextureScaled(this.dialogBottomMiddle, (double)this.dialogBottomLeft.getWidth(), (double)int1, this.getWidth() - (double)(this.dialogBottomLeft.getWidth() * 2), (double)this.dialogBottomMiddle.getHeight(), (double)float1);
		this.DrawTexture(this.dialogBottomLeft, (double)byte1, (double)int1, (double)float1);
		this.DrawTexture(this.dialogBottomRight, this.getWidth() - (double)this.dialogBottomRight.getWidth(), (double)int1, (double)float1);
		super.render();
	}

	public void update() {
		super.update();
		if (this.closeButton != null) {
			this.closeButton.setX(4.0);
			this.closeButton.setY(3.0);
		}

		int int1 = 0;
		if (!this.ResizeToFitY) {
			Iterator iterator = this.nestedItems.iterator();
			while (iterator.hasNext()) {
				Rectangle rectangle = (Rectangle)iterator.next();
				UIElement uIElement = (UIElement)this.getControls().get(int1);
				if (uIElement != this.closeButton) {
					uIElement.setX((double)rectangle.getX());
					uIElement.setY((double)rectangle.getY());
					uIElement.setWidth((double)(this.clientW - (rectangle.getX() + rectangle.getWidth())));
					uIElement.setHeight((double)(this.clientH - (rectangle.getY() + rectangle.getHeight())));
					uIElement.onresize();
					++int1;
				}
			}
		} else {
			int int2 = 100000;
			int int3 = 100000;
			float float1 = 0.0F;
			float float2 = 0.0F;
			Iterator iterator2 = this.nestedItems.iterator();
			while (iterator2.hasNext()) {
				Rectangle rectangle2 = (Rectangle)iterator2.next();
				UIElement uIElement2 = (UIElement)this.getControls().get(int1);
				if (uIElement2 != this.closeButton) {
					if ((double)int2 > uIElement2.getAbsoluteX()) {
						int2 = uIElement2.getAbsoluteX().intValue();
					}

					if ((double)int3 > uIElement2.getAbsoluteX()) {
						int3 = uIElement2.getAbsoluteX().intValue();
					}

					if ((double)float1 < uIElement2.getWidth()) {
						float1 = (float)uIElement2.getWidth().intValue();
					}

					if ((double)float2 < uIElement2.getHeight()) {
						float2 = (float)uIElement2.getHeight().intValue();
					}

					++int1;
				}
			}

			float2 += 50.0F;
			this.height = float2;
		}
	}
}
