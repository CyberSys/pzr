package zombie.ui;

import java.util.ArrayList;
import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoObject;


public final class ObjectTooltip extends UIElement {
	public static float alphaStep = 0.1F;
	public boolean bIsItem = false;
	public InventoryItem Item = null;
	public IsoObject Object;
	float alpha = 0.0F;
	int showDelay = 0;
	float targetAlpha = 0.0F;
	Texture texture = Texture.getSharedTexture("black");
	public int padRight = 5;
	public int padBottom = 5;
	private IsoGameCharacter character;
	private boolean measureOnly;
	private float weightOfStack = 0.0F;
	private static int lineSpacing = 14;
	private static String fontSize = "Small";
	private static UIFont font;
	private static Stack freeLayouts;

	public ObjectTooltip() {
		this.width = 130.0F;
		this.height = 130.0F;
		this.defaultDraw = false;
		lineSpacing = TextManager.instance.getFontFromEnum(font).getLineHeight();
		checkFont();
	}

	public static void checkFont() {
		if (!fontSize.equals(Core.getInstance().getOptionTooltipFont())) {
			fontSize = Core.getInstance().getOptionTooltipFont();
			if ("Large".equals(fontSize)) {
				font = UIFont.Large;
			} else if ("Medium".equals(fontSize)) {
				font = UIFont.Medium;
			} else {
				font = UIFont.Small;
			}

			lineSpacing = TextManager.instance.getFontFromEnum(font).getLineHeight();
		}
	}

	public UIFont getFont() {
		return font;
	}

	public int getLineSpacing() {
		return lineSpacing;
	}

	public void DrawText(UIFont uIFont, String string, double double1, double double2, double double3, double double4, double double5, double double6) {
		if (!this.measureOnly) {
			super.DrawText(uIFont, string, double1, double2, double3, double4, double5, double6);
		}
	}

	public void DrawTextCentre(UIFont uIFont, String string, double double1, double double2, double double3, double double4, double double5, double double6) {
		if (!this.measureOnly) {
			super.DrawTextCentre(uIFont, string, double1, double2, double3, double4, double5, double6);
		}
	}

	public void DrawTextRight(UIFont uIFont, String string, double double1, double double2, double double3, double double4, double double5, double double6) {
		if (!this.measureOnly) {
			super.DrawTextRight(uIFont, string, double1, double2, double3, double4, double5, double6);
		}
	}

	public void DrawValueRight(int int1, int int2, int int3, boolean boolean1) {
		Integer integer = int1;
		String string = integer.toString();
		float float1 = 0.3F;
		float float2 = 1.0F;
		float float3 = 0.2F;
		float float4 = 1.0F;
		if (int1 > 0) {
			string = "+" + string;
		}

		if (int1 < 0 && boolean1 || int1 > 0 && !boolean1) {
			float1 = 0.8F;
			float2 = 0.3F;
			float3 = 0.2F;
		}

		this.DrawTextRight(font, string, (double)int2, (double)int3, (double)float1, (double)float2, (double)float3, (double)float4);
	}

	public void DrawValueRightNoPlus(int int1, int int2, int int3) {
		Integer integer = int1;
		String string = integer.toString();
		float float1 = 1.0F;
		float float2 = 1.0F;
		float float3 = 1.0F;
		float float4 = 1.0F;
		this.DrawTextRight(font, string, (double)int2, (double)int3, (double)float1, (double)float2, (double)float3, (double)float4);
	}

	public void DrawValueRightNoPlus(float float1, int int1, int int2) {
		Float Float1 = float1;
		Float1 = (float)((int)(((double)Float1 + 0.01) * 10.0)) / 10.0F;
		String string = Float1.toString();
		float float2 = 1.0F;
		float float3 = 1.0F;
		float float4 = 1.0F;
		float float5 = 1.0F;
		this.DrawTextRight(font, string, (double)int1, (double)int2, (double)float2, (double)float3, (double)float4, (double)float5);
	}

	public void DrawTextureScaled(Texture texture, double double1, double double2, double double3, double double4, double double5) {
		if (!this.measureOnly) {
			super.DrawTextureScaled(texture, double1, double2, double3, double4, double5);
		}
	}

	public void DrawTextureScaledAspect(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		if (!this.measureOnly) {
			super.DrawTextureScaledAspect(texture, double1, double2, double3, double4, double5, double6, double7, double8);
		}
	}

	public void DrawProgressBar(int int1, int int2, int int3, int int4, float float1, double double1, double double2, double double3, double double4) {
		if (!this.measureOnly) {
			if (float1 < 0.0F) {
				float1 = 0.0F;
			}

			if (float1 > 1.0F) {
				float1 = 1.0F;
			}

			int int5 = (int)Math.floor((double)((float)int3 * float1));
			if (float1 > 0.0F && int5 == 0) {
				int5 = 1;
			}

			this.DrawTextureScaledColor((Texture)null, (double)int1, (double)int2, (double)int5, 3.0, double1, double2, double3, double4);
			this.DrawTextureScaledColor((Texture)null, (double)int1 + (double)int5, (double)int2, (double)int3 - (double)int5, 3.0, 0.25, 0.25, 0.25, 1.0);
		}
	}

	public Boolean onMouseMove(double double1, double double2) {
		this.setX(this.getX() + double1);
		this.setY(this.getY() + double2);
		return Boolean.FALSE;
	}

	public void onMouseMoveOutside(double double1, double double2) {
		this.setX(this.getX() + double1);
		this.setY(this.getY() + double2);
	}

	public void render() {
		if (this.isVisible()) {
			if (!(this.alpha <= 0.0F)) {
				if (!this.bIsItem && this.Object != null && this.Object.haveSpecialTooltip()) {
					this.Object.DoSpecialTooltip(this, this.Object.square);
				}

				super.render();
			}
		}
	}

	public void show(IsoObject object, double double1, double double2) {
		this.bIsItem = false;
		this.Object = object;
		this.setX(double1);
		this.setY(double2);
		this.targetAlpha = 0.5F;
		this.showDelay = 15;
		this.alpha = 0.0F;
	}

	public void hide() {
		this.Object = null;
		this.showDelay = 0;
		this.setVisible(false);
	}

	public void update() {
		if (!(this.alpha <= 0.0F) || this.targetAlpha != 0.0F) {
			if (this.showDelay > 0) {
				if (--this.showDelay == 0) {
					this.setVisible(true);
				}
			} else {
				if (this.alpha < this.targetAlpha) {
					this.alpha += alphaStep;
					if (this.alpha > 0.5F) {
						this.alpha = 0.5F;
					}
				} else if (this.alpha > this.targetAlpha) {
					this.alpha -= alphaStep;
					if (this.alpha < this.targetAlpha) {
						this.alpha = this.targetAlpha;
					}
				}
			}
		}
	}

	void show(InventoryItem inventoryItem, int int1, int int2) {
		this.Object = null;
		this.Item = inventoryItem;
		this.bIsItem = true;
		this.setX(this.getX());
		this.setY(this.getY());
		this.targetAlpha = 0.5F;
		this.showDelay = 15;
		this.alpha = 0.0F;
		this.setVisible(true);
	}

	public void adjustWidth(int int1, String string) {
		int int2 = TextManager.instance.MeasureStringX(font, string);
		if ((float)(int1 + int2 + this.padRight) > this.width) {
			this.setWidth((double)(int1 + int2 + this.padRight));
		}
	}

	public ObjectTooltip.Layout beginLayout() {
		ObjectTooltip.Layout layout = null;
		if (freeLayouts.isEmpty()) {
			layout = new ObjectTooltip.Layout();
		} else {
			layout = (ObjectTooltip.Layout)freeLayouts.pop();
		}

		return layout;
	}

	public void endLayout(ObjectTooltip.Layout layout) {
		while (layout != null) {
			ObjectTooltip.Layout layout2 = layout.next;
			layout.free();
			freeLayouts.push(layout);
			layout = layout2;
		}
	}

	public Texture getTexture() {
		return this.texture;
	}

	public void setCharacter(IsoGameCharacter gameCharacter) {
		this.character = gameCharacter;
	}

	public IsoGameCharacter getCharacter() {
		return this.character;
	}

	public void setMeasureOnly(boolean boolean1) {
		this.measureOnly = boolean1;
	}

	public boolean isMeasureOnly() {
		return this.measureOnly;
	}

	public float getWeightOfStack() {
		return this.weightOfStack;
	}

	public void setWeightOfStack(float float1) {
		this.weightOfStack = float1;
	}

	static  {
		font = UIFont.Small;
		freeLayouts = new Stack();
	}

	public static class Layout {
		public ArrayList items = new ArrayList();
		public int minLabelWidth;
		public int minValueWidth;
		public ObjectTooltip.Layout next;
		public int nextPadY;
		private static Stack freeItems = new Stack();

		public ObjectTooltip.LayoutItem addItem() {
			ObjectTooltip.LayoutItem layoutItem = null;
			if (freeItems.isEmpty()) {
				layoutItem = new ObjectTooltip.LayoutItem();
			} else {
				layoutItem = (ObjectTooltip.LayoutItem)freeItems.pop();
			}

			layoutItem.reset();
			this.items.add(layoutItem);
			return layoutItem;
		}

		public void setMinLabelWidth(int int1) {
			this.minLabelWidth = int1;
		}

		public void setMinValueWidth(int int1) {
			this.minValueWidth = int1;
		}

		public int render(int int1, int int2, ObjectTooltip objectTooltip) {
			int int3 = this.minLabelWidth;
			int int4 = this.minValueWidth;
			int int5 = this.minValueWidth;
			int int6 = 0;
			int int7 = 0;
			byte byte1 = 8;
			int int8 = 0;
			int int9;
			ObjectTooltip.LayoutItem layoutItem;
			for (int9 = 0; int9 < this.items.size(); ++int9) {
				layoutItem = (ObjectTooltip.LayoutItem)this.items.get(int9);
				layoutItem.calcSizes();
				if (layoutItem.hasValue) {
					int3 = Math.max(int3, layoutItem.labelWidth);
					int4 = Math.max(int4, layoutItem.valueWidth);
					int5 = Math.max(int5, layoutItem.valueWidthRight);
					int6 = Math.max(int6, layoutItem.progressWidth);
					int8 = Math.max(int8, Math.max(layoutItem.labelWidth, this.minLabelWidth) + byte1);
					int7 = Math.max(int7, int3 + byte1 + Math.max(Math.max(int4, int5), int6));
				} else {
					int3 = Math.max(int3, layoutItem.labelWidth);
					int7 = Math.max(int7, layoutItem.labelWidth);
				}
			}

			if ((float)(int1 + int7 + objectTooltip.padRight) > objectTooltip.width) {
				objectTooltip.setWidth((double)(int1 + int7 + objectTooltip.padRight));
			}

			for (int9 = 0; int9 < this.items.size(); ++int9) {
				layoutItem = (ObjectTooltip.LayoutItem)this.items.get(int9);
				layoutItem.render(int1, int2, int8, int5, objectTooltip);
				int2 += layoutItem.height;
			}

			return this.next != null ? this.next.render(int1, int2 + this.next.nextPadY, objectTooltip) : int2;
		}

		public void free() {
			freeItems.addAll(this.items);
			this.items.clear();
			this.minLabelWidth = 0;
			this.minValueWidth = 0;
			this.next = null;
			this.nextPadY = 0;
		}
	}

	public static class LayoutItem {
		public String label;
		public float r0;
		public float g0;
		public float b0;
		public float a0;
		public boolean hasValue = false;
		public String value;
		public boolean rightJustify = false;
		public float r1;
		public float g1;
		public float b1;
		public float a1;
		public float progressFraction = -1.0F;
		public int labelWidth;
		public int valueWidth;
		public int valueWidthRight;
		public int progressWidth;
		public int height;

		public void reset() {
			this.label = null;
			this.value = null;
			this.hasValue = false;
			this.rightJustify = false;
			this.progressFraction = -1.0F;
		}

		public void setLabel(String string, float float1, float float2, float float3, float float4) {
			this.label = string;
			this.r0 = float1;
			this.b0 = float3;
			this.g0 = float2;
			this.a0 = float4;
		}

		public void setValue(String string, float float1, float float2, float float3, float float4) {
			this.value = string;
			this.r1 = float1;
			this.b1 = float3;
			this.g1 = float2;
			this.a1 = float4;
			this.hasValue = true;
		}

		public void setValueRight(int int1, boolean boolean1) {
			this.value = Integer.toString(int1);
			if (int1 > 0) {
				this.value = "+" + this.value;
			}

			if ((int1 >= 0 || !boolean1) && (int1 <= 0 || boolean1)) {
				this.r1 = Core.getInstance().getGoodHighlitedColor().getR();
				this.g1 = Core.getInstance().getGoodHighlitedColor().getG();
				this.b1 = Core.getInstance().getGoodHighlitedColor().getB();
			} else {
				this.r1 = Core.getInstance().getBadHighlitedColor().getR();
				this.g1 = Core.getInstance().getBadHighlitedColor().getG();
				this.b1 = Core.getInstance().getBadHighlitedColor().getB();
			}

			this.a1 = 1.0F;
			this.hasValue = true;
			this.rightJustify = true;
		}

		public void setValueRightNoPlus(float float1) {
			float1 = (float)((int)((float1 + 0.005F) * 100.0F)) / 100.0F;
			this.value = Float.toString(float1);
			this.r1 = 1.0F;
			this.g1 = 1.0F;
			this.b1 = 1.0F;
			this.a1 = 1.0F;
			this.hasValue = true;
			this.rightJustify = true;
		}

		public void setValueRightNoPlus(int int1) {
			this.value = Integer.toString(int1);
			this.r1 = 1.0F;
			this.g1 = 1.0F;
			this.b1 = 1.0F;
			this.a1 = 1.0F;
			this.hasValue = true;
			this.rightJustify = true;
		}

		public void setProgress(float float1, float float2, float float3, float float4, float float5) {
			this.progressFraction = float1;
			this.r1 = float2;
			this.b1 = float4;
			this.g1 = float3;
			this.a1 = float5;
			this.hasValue = true;
		}

		public void calcSizes() {
			this.labelWidth = this.valueWidth = this.valueWidthRight = this.progressWidth = 0;
			if (this.label != null) {
				this.labelWidth = TextManager.instance.MeasureStringX(ObjectTooltip.font, this.label);
			}

			int int1;
			if (this.hasValue) {
				if (this.value != null) {
					int1 = TextManager.instance.MeasureStringX(ObjectTooltip.font, this.value);
					this.valueWidth = this.rightJustify ? 0 : int1;
					this.valueWidthRight = this.rightJustify ? int1 : 0;
				} else if (this.progressFraction != -1.0F) {
					this.progressWidth = 80;
				}
			}

			int1 = 1;
			int int2;
			int int3;
			if (this.label != null) {
				int2 = 1;
				for (int3 = 0; int3 < this.label.length(); ++int3) {
					if (this.label.charAt(int3) == '\n') {
						++int2;
					}
				}

				int1 = Math.max(int1, int2);
			}

			if (this.hasValue && this.value != null) {
				int2 = 1;
				for (int3 = 0; int3 < this.value.length(); ++int3) {
					if (this.value.charAt(int3) == '\n') {
						++int2;
					}
				}

				int1 = Math.max(int1, int2);
			}

			this.height = int1 * ObjectTooltip.lineSpacing;
		}

		public void render(int int1, int int2, int int3, int int4, ObjectTooltip objectTooltip) {
			if (this.label != null) {
				objectTooltip.DrawText(ObjectTooltip.font, this.label, (double)int1, (double)int2, (double)this.r0, (double)this.g0, (double)this.b0, (double)this.a0);
			}

			if (this.value != null) {
				if (this.rightJustify) {
					objectTooltip.DrawTextRight(ObjectTooltip.font, this.value, (double)(int1 + int3 + int4), (double)int2, (double)this.r1, (double)this.g1, (double)this.b1, (double)this.a1);
				} else {
					objectTooltip.DrawText(ObjectTooltip.font, this.value, (double)(int1 + int3), (double)int2, (double)this.r1, (double)this.g1, (double)this.b1, (double)this.a1);
				}
			}

			if (this.progressFraction != -1.0F) {
				objectTooltip.DrawProgressBar(int1 + int3, int2 + ObjectTooltip.lineSpacing / 2 - 1, this.progressWidth, 2, this.progressFraction, (double)this.r1, (double)this.g1, (double)this.b1, (double)this.a1);
			}
		}
	}
}
