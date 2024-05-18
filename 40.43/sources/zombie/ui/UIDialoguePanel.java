package zombie.ui;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Stack;
import zombie.core.Color;
import zombie.core.textures.Texture;


public class UIDialoguePanel extends UIElement {
	float alpha = 1.0F;
	Texture dialogBottomLeft = null;
	Texture dialogBottomMiddle = null;
	Texture dialogBottomRight = null;
	Texture dialogLeft = null;
	Texture dialogMiddle = null;
	Texture dialogRight = null;
	Texture titleLeft = null;
	Texture titleMiddle = null;
	Texture titleRight = null;
	public float clientH = 0.0F;
	public float clientW = 0.0F;
	public Stack nestedItems = new Stack();

	public UIDialoguePanel(float float1, float float2, float float3, float float4) {
		this.x = (double)float1;
		this.y = (double)float2;
		this.width = float3;
		this.height = float4;
		this.titleLeft = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Left.png");
		this.titleMiddle = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Middle.png");
		this.titleRight = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Right.png");
		this.dialogLeft = Texture.getSharedTexture("media/ui/Dialog_Left.png");
		this.dialogMiddle = Texture.getSharedTexture("media/ui/Dialog_Middle.png");
		this.dialogRight = Texture.getSharedTexture("media/ui/Dialog_Right.png");
		this.dialogBottomLeft = Texture.getSharedTexture("media/ui/Dialog_Bottom_Left.png");
		this.dialogBottomMiddle = Texture.getSharedTexture("media/ui/Dialog_Bottom_Middle.png");
		this.dialogBottomRight = Texture.getSharedTexture("media/ui/Dialog_Bottom_Right.png");
		this.clientW = float3;
		this.clientH = float4;
	}

	public void Nest(UIElement uIElement, int int1, int int2, int int3, int int4) {
		this.AddChild(uIElement);
		this.nestedItems.add(new Rectangle(int4, int1, int2, int3));
		uIElement.setX((double)int4);
		uIElement.setY((double)int1);
		uIElement.update();
	}

	public void render() {
		this.DrawTextureScaledCol(this.titleLeft, 0.0, 0.0, 28.0, 28.0, new Color(255, 255, 255, 100));
		this.DrawTextureScaledCol(this.titleMiddle, 28.0, 0.0, this.getWidth() - 56.0, 28.0, new Color(255, 255, 255, 100));
		this.DrawTextureScaledCol(this.titleRight, 0.0 + this.getWidth() - 28.0, 0.0, 28.0, 28.0, new Color(255, 255, 255, 100));
		this.DrawTextureScaledCol(this.dialogLeft, 0.0, 28.0, 78.0, this.getHeight() - 100.0, new Color(255, 255, 255, 100));
		this.DrawTextureScaledCol(this.dialogMiddle, 78.0, 28.0, this.getWidth() - 156.0, this.getHeight() - 100.0, new Color(255, 255, 255, 100));
		this.DrawTextureScaledCol(this.dialogRight, 0.0 + this.getWidth() - 78.0, 28.0, 78.0, this.getHeight() - 100.0, new Color(255, 255, 255, 100));
		this.DrawTextureScaledCol(this.dialogBottomLeft, 0.0, 0.0 + this.getHeight() - 72.0, 78.0, 72.0, new Color(255, 255, 255, 100));
		this.DrawTextureScaledCol(this.dialogBottomMiddle, 78.0, 0.0 + this.getHeight() - 72.0, this.getWidth() - 156.0, 72.0, new Color(255, 255, 255, 100));
		this.DrawTextureScaledCol(this.dialogBottomRight, 0.0 + this.getWidth() - 78.0, 0.0 + this.getHeight() - 72.0, 78.0, 72.0, new Color(255, 255, 255, 100));
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
}
