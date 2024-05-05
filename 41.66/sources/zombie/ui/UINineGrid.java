package zombie.ui;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Stack;
import zombie.core.Color;
import zombie.core.textures.Texture;


public final class UINineGrid extends UIElement {
	Texture GridTopLeft = null;
	Texture GridTop = null;
	Texture GridTopRight = null;
	Texture GridLeft = null;
	Texture GridCenter = null;
	Texture GridRight = null;
	Texture GridBottomLeft = null;
	Texture GridBottom = null;
	Texture GridBottomRight = null;
	int TopWidth = 10;
	int LeftWidth = 10;
	int RightWidth = 10;
	int BottomWidth = 10;
	public int clientH = 0;
	public int clientW = 0;
	public Stack nestedItems = new Stack();
	public Color Colour = new Color(50, 50, 50, 212);

	public UINineGrid(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, String string, String string2, String string3, String string4, String string5, String string6, String string7, String string8, String string9) {
		this.x = (double)int1;
		this.y = (double)int2;
		this.width = (float)int3;
		this.height = (float)int4;
		this.TopWidth = int5;
		this.LeftWidth = int6;
		this.RightWidth = int7;
		this.BottomWidth = int8;
		this.GridTopLeft = Texture.getSharedTexture(string);
		this.GridTop = Texture.getSharedTexture(string2);
		this.GridTopRight = Texture.getSharedTexture(string3);
		this.GridLeft = Texture.getSharedTexture(string4);
		this.GridCenter = Texture.getSharedTexture(string5);
		this.GridRight = Texture.getSharedTexture(string6);
		this.GridBottomLeft = Texture.getSharedTexture(string7);
		this.GridBottom = Texture.getSharedTexture(string8);
		this.GridBottomRight = Texture.getSharedTexture(string9);
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

	public void render() {
		this.DrawTextureScaledCol(this.GridTopLeft, 0.0, 0.0, (double)this.LeftWidth, (double)this.TopWidth, this.Colour);
		this.DrawTextureScaledCol(this.GridTop, (double)this.LeftWidth, 0.0, this.getWidth() - (double)(this.LeftWidth + this.RightWidth), (double)this.TopWidth, this.Colour);
		this.DrawTextureScaledCol(this.GridTopRight, this.getWidth() - (double)this.RightWidth, 0.0, (double)this.RightWidth, (double)this.TopWidth, this.Colour);
		this.DrawTextureScaledCol(this.GridLeft, 0.0, (double)this.TopWidth, (double)this.LeftWidth, this.getHeight() - (double)(this.TopWidth + this.BottomWidth), this.Colour);
		this.DrawTextureScaledCol(this.GridCenter, (double)this.LeftWidth, (double)this.TopWidth, this.getWidth() - (double)(this.LeftWidth + this.RightWidth), this.getHeight() - (double)(this.TopWidth + this.BottomWidth), this.Colour);
		this.DrawTextureScaledCol(this.GridRight, this.getWidth() - (double)this.RightWidth, (double)this.TopWidth, (double)this.RightWidth, this.getHeight() - (double)(this.TopWidth + this.BottomWidth), this.Colour);
		this.DrawTextureScaledCol(this.GridBottomLeft, 0.0, this.getHeight() - (double)this.BottomWidth, (double)this.LeftWidth, (double)this.BottomWidth, this.Colour);
		this.DrawTextureScaledCol(this.GridBottom, (double)this.LeftWidth, this.getHeight() - (double)this.BottomWidth, this.getWidth() - (double)(this.LeftWidth + this.RightWidth), (double)this.BottomWidth, this.Colour);
		this.DrawTextureScaledCol(this.GridBottomRight, this.getWidth() - (double)this.RightWidth, this.getHeight() - (double)this.BottomWidth, (double)this.RightWidth, (double)this.BottomWidth, this.Colour);
		super.render();
	}

	public void update() {
		super.update();
		int int1 = 0;
		for (Iterator iterator = this.nestedItems.iterator(); iterator.hasNext(); ++int1) {
			Rectangle rectangle = (Rectangle)iterator.next();
			UIElement uIElement = (UIElement)this.getControls().get(int1);
			uIElement.setX((double)((float)rectangle.getX()));
			uIElement.setY((double)((float)rectangle.getY()));
			uIElement.setWidth((double)((int)((double)this.clientW - (rectangle.getX() + rectangle.getWidth()))));
			uIElement.setHeight((double)((int)((double)this.clientH - (rectangle.getY() + rectangle.getHeight()))));
			uIElement.onresize();
		}
	}

	public void setAlpha(float float1) {
		this.Colour.a = float1;
	}

	public float getAlpha() {
		return this.Colour.a;
	}
}
