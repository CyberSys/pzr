package zombie.ui;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;
import se.krka.kahlua.vm.KahluaTable;
import zombie.IndieGL;
import zombie.Lua.LuaManager;
import zombie.core.BoxedStaticValues;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.input.Mouse;
import zombie.iso.IsoCamera;


public class UIElement {
	public boolean capture = false;
	public boolean IgnoreLossControl = false;
	public String clickedValue = null;
	public ArrayList Controls = new ArrayList();
	public boolean defaultDraw = true;
	public boolean followGameWorld = false;
	public float height = 256.0F;
	public UIElement Parent = null;
	public boolean visible = true;
	public float width = 256.0F;
	public double x = 0.0;
	public double y = 0.0;
	public KahluaTable table;
	boolean alwaysOnTop = false;
	private boolean bConsumeMouseEvents = true;
	int maxDrawHeight = -1;
	private long leftDownTime = 0L;
	public boolean alwaysBack;
	Double yScroll = 0.0;
	Double xScroll = 0.0;
	int scrollHeight = 0;
	private boolean clicked;
	private double clickX;
	private double clickY;
	static Color tempcol = new Color(0, 0, 0, 0);
	public boolean bScrollChildren = false;
	public boolean bScrollWithParent = true;
	public boolean anchorTop = true;
	public boolean anchorLeft = true;
	public boolean anchorRight = false;
	public boolean anchorBottom = false;
	double lastheight = -1.0;
	double lastwidth = -1.0;
	static ArrayList toAdd = new ArrayList(0);
	boolean bResizeDirty = false;
	boolean enabled = true;
	static Texture white;
	static int StencilLevel = 0;
	static Stack StencilStack = new Stack();
	ArrayList toTop = new ArrayList(0);
	public int playerContext = -1;
	private String uiname = "";

	public UIElement() {
	}

	public Double getMaxDrawHeight() {
		return BoxedStaticValues.toDouble((double)this.maxDrawHeight);
	}

	public void setMaxDrawHeight(double double1) {
		this.maxDrawHeight = (int)double1;
	}

	public void clearMaxDrawHeight() {
		this.maxDrawHeight = -1;
	}

	public Double getXScroll() {
		return this.xScroll;
	}

	public Double getYScroll() {
		return this.yScroll;
	}

	public void setAlwaysOnTop(boolean boolean1) {
		this.alwaysOnTop = boolean1;
	}

	public UIElement(KahluaTable kahluaTable) {
		this.table = kahluaTable;
	}

	public void backMost() {
		this.alwaysBack = true;
	}

	public void AddChild(UIElement uIElement) {
		this.getControls().add(uIElement);
		uIElement.setParent(this);
	}

	public void RemoveChild(UIElement uIElement) {
		this.getControls().remove(uIElement);
		uIElement.setParent((UIElement)null);
	}

	public void setXScroll(double double1) {
		this.xScroll = double1;
	}

	public void setYScroll(double double1) {
		this.yScroll = double1;
	}

	public void setScrollHeight(double double1) {
		this.scrollHeight = (int)double1;
	}

	public Double getScrollHeight() {
		return BoxedStaticValues.toDouble((double)this.scrollHeight);
	}

	public void setConsumeMouseEvents(boolean boolean1) {
		this.bConsumeMouseEvents = boolean1;
	}

	public boolean isConsumeMouseEvents() {
		return this.bConsumeMouseEvents;
	}

	public void ClearChildren() {
		this.getControls().clear();
	}

	public void ButtonClicked(String string) {
		this.setClickedValue(string);
	}

	public void DrawText(UIFont uIFont, String string, double double1, double double2, double double3, double double4, double double5, double double6, double double7) {
		TextManager.instance.DrawString(uIFont, double1 + this.getAbsoluteX() + this.xScroll, double2 + this.getAbsoluteY() + this.yScroll, (float)double3, string, double4, double5, double6, double7);
	}

	public void DrawText(String string, double double1, double double2, double double3, double double4, double double5, double double6) {
		TextManager.instance.DrawString(double1 + this.getAbsoluteX() + this.xScroll, double2 + this.getAbsoluteY() + this.yScroll, string, double3, double4, double5, double6);
	}

	public void DrawText(String string, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		TextManager.instance.DrawString(double1 + this.getAbsoluteX() + this.xScroll, double2 + this.getAbsoluteY() + this.yScroll, string, double5, double6, double7, double8);
	}

	public void DrawText(UIFont uIFont, String string, double double1, double double2, double double3, double double4, double double5, double double6) {
		if (string != null) {
			int int1 = (int)(double2 + this.getAbsoluteY() + this.yScroll);
			if (int1 + 100 >= 0 && int1 <= 4096) {
				TextManager.instance.DrawString(uIFont, double1 + this.getAbsoluteX() + this.xScroll, double2 + this.getAbsoluteY() + this.yScroll, string, double3, double4, double5, double6);
			}
		}
	}

	public void DrawTextUntrimmed(UIFont uIFont, String string, double double1, double double2, double double3, double double4, double double5, double double6) {
		if (string != null) {
			TextManager.instance.DrawStringUntrimmed(uIFont, double1 + this.getAbsoluteX() + this.xScroll, double2 + this.getAbsoluteY() + this.yScroll, string, double3, double4, double5, double6);
		}
	}

	public void DrawTextCentre(String string, double double1, double double2, double double3, double double4, double double5, double double6) {
		TextManager.instance.DrawStringCentre(double1 + this.getAbsoluteX() + this.xScroll, double2 + this.getAbsoluteY() + this.yScroll, string, double3, double4, double5, double6);
	}

	public void DrawTextCentre(UIFont uIFont, String string, double double1, double double2, double double3, double double4, double double5, double double6) {
		TextManager.instance.DrawStringCentre(uIFont, double1 + this.getAbsoluteX() + this.xScroll, double2 + this.getAbsoluteY() + this.yScroll, string, double3, double4, double5, double6);
	}

	public void DrawTextRight(String string, double double1, double double2, double double3, double double4, double double5, double double6) {
		TextManager.instance.DrawStringRight(double1 + this.getAbsoluteX() + this.xScroll, double2 + this.getAbsoluteY() + this.yScroll, string, double3, double4, double5, double6);
	}

	public void DrawTextRight(UIFont uIFont, String string, double double1, double double2, double double3, double double4, double double5, double double6) {
		TextManager.instance.DrawStringRight(uIFont, double1 + this.getAbsoluteX() + this.xScroll, double2 + this.getAbsoluteY() + this.yScroll, string, double3, double4, double5, double6);
	}

	public void DrawTextureAngle(Texture texture, double double1, double double2, double double3) {
		if (this.isVisible()) {
			float float1 = 1.0F;
			float float2 = 1.0F;
			float float3 = 1.0F;
			float float4 = 1.0F;
			float float5 = (float)(texture.getWidth() / 2);
			float float6 = (float)(texture.getHeight() / 2);
			double double4 = Math.toRadians(180.0 + double3);
			double double5 = Math.cos(double4) * (double)float5;
			double double6 = Math.sin(double4) * (double)float5;
			double double7 = Math.cos(double4) * (double)float6;
			double double8 = Math.sin(double4) * (double)float6;
			double double9 = double5 - double8;
			double double10 = double7 + double6;
			double double11 = -double5 - double8;
			double double12 = double7 - double6;
			double double13 = -double5 + double8;
			double double14 = -double7 - double6;
			double double15 = double5 + double8;
			double double16 = -double7 + double6;
			double9 += this.getAbsoluteX() + double1;
			double10 += this.getAbsoluteY() + double2;
			double11 += this.getAbsoluteX() + double1;
			double12 += this.getAbsoluteY() + double2;
			double13 += this.getAbsoluteX() + double1;
			double14 += this.getAbsoluteY() + double2;
			double15 += this.getAbsoluteX() + double1;
			double16 += this.getAbsoluteY() + double2;
			SpriteRenderer.instance.render(texture, double9, double10, double11, double12, double13, double14, double15, double16, float1, float2, float3, float4, float1, float2, float3, float4, float1, float2, float3, float4, float1, float2, float3, float4);
		}
	}

	public void DrawTexture(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12) {
		SpriteRenderer.instance.render(texture, double1, double2, double3, double4, double5, double6, double7, double8, (float)double9, (float)double10, (float)double11, (float)double12, (float)double9, (float)double10, (float)double11, (float)double12, (float)double9, (float)double10, (float)double11, (float)double12, (float)double9, (float)double10, (float)double11, (float)double12);
	}

	public void DrawTexture(Texture texture, double double1, double double2, double double3) {
		if (this.isVisible()) {
			double double4 = double1 + this.getAbsoluteX();
			double double5 = double2 + this.getAbsoluteY();
			double4 += (double)texture.offsetX;
			double5 += (double)texture.offsetY;
			int int1 = (int)(double5 + this.yScroll);
			if (int1 + texture.getHeight() >= 0 && int1 <= 4096) {
				double3 = Math.min(double3 * (double)UIManager.FBO_ALPHA_MULT, 1.0);
				SpriteRenderer.instance.render(texture, (float)(double4 + this.xScroll), (float)(double5 + this.yScroll), (float)texture.getWidth(), (float)texture.getHeight(), 1.0F, 1.0F, 1.0F, (float)double3);
			}
		}
	}

	public void DrawTextureCol(Texture texture, double double1, double double2, Color color) {
		if (this.isVisible()) {
			double double3 = double1 + this.getAbsoluteX();
			double double4 = double2 + this.getAbsoluteY();
			int int1 = 0;
			int int2 = 0;
			if (texture != null) {
				double3 += (double)texture.offsetX;
				double4 += (double)texture.offsetY;
				int1 = texture.getWidth();
				int2 = texture.getHeight();
			}

			int int3 = (int)(double4 + this.yScroll);
			if (int3 + int2 >= 0 && int3 <= 4096) {
				float float1 = Math.min(color.a * UIManager.FBO_ALPHA_MULT, 1.0F);
				SpriteRenderer.instance.render(texture, (float)(double3 + this.xScroll), (float)(double4 + this.yScroll), (float)int1, (float)int2, color.r, color.g, color.b, float1);
			}
		}
	}

	public void DrawTextureScaled(Texture texture, double double1, double double2, double double3, double double4, double double5) {
		if (this.isVisible()) {
			double double6 = double1 + this.getAbsoluteX();
			double double7 = double2 + this.getAbsoluteY();
			double5 = Math.min(double5 * (double)UIManager.FBO_ALPHA_MULT, 1.0);
			SpriteRenderer.instance.render(texture, (float)(double6 + this.xScroll), (float)(double7 + this.yScroll), (float)double3, (float)double4, 1.0F, 1.0F, 1.0F, (float)double5);
		}
	}

	public void DrawTextureScaledUniform(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7) {
		if (this.isVisible() && texture != null) {
			double double8 = double1 + this.getAbsoluteX();
			double double9 = double2 + this.getAbsoluteY();
			double8 += (double)texture.offsetX * double3;
			double9 += (double)texture.offsetY * double3;
			SpriteRenderer.instance.render(texture, (float)(double8 + this.xScroll), (float)(double9 + this.yScroll), (float)((double)texture.getWidth() * double3), (float)((double)texture.getHeight() * double3), (float)double4, (float)double5, (float)double6, (float)double7);
		}
	}

	public void DrawTextureScaledAspect(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		if (this.isVisible() && texture != null) {
			double double9 = double1 + this.getAbsoluteX();
			double double10 = double2 + this.getAbsoluteY();
			if (texture.getWidth() > 0 && texture.getHeight() > 0 && double3 > 0.0 && double4 > 0.0) {
				double double11 = Math.min(double3 / (double)texture.getWidthOrig(), double4 / (double)texture.getHeightOrig());
				double double12 = double3;
				double double13 = double4;
				double3 = (double)texture.getWidth() * double11;
				double4 = (double)texture.getHeight() * double11;
				double9 -= (double3 - double12) / 2.0;
				double10 -= (double4 - double13) / 2.0;
			}

			double8 = Math.min(double8 * (double)UIManager.FBO_ALPHA_MULT, 1.0);
			SpriteRenderer.instance.render(texture, (float)(double9 + this.xScroll), (float)(double10 + this.yScroll), (float)double3, (float)double4, (float)double5, (float)double6, (float)double7, (float)double8);
		}
	}

	public void DrawTextureScaledAspect2(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		if (this.isVisible() && texture != null) {
			double double9 = double1 + this.getAbsoluteX();
			double double10 = double2 + this.getAbsoluteY();
			if (texture.getWidth() > 0 && texture.getHeight() > 0 && double3 > 0.0 && double4 > 0.0) {
				double double11 = Math.min(double3 / (double)texture.getWidth(), double4 / (double)texture.getHeight());
				double double12 = double3;
				double double13 = double4;
				double3 = (double)texture.getWidth() * double11;
				double4 = (double)texture.getHeight() * double11;
				double9 -= (double3 - double12) / 2.0;
				double10 -= (double4 - double13) / 2.0;
			}

			SpriteRenderer.instance.render(texture, (float)(double9 + this.xScroll), (float)(double10 + this.yScroll), (float)double3, (float)double4, (float)double5, (float)double6, (float)double7, (float)double8);
		}
	}

	public void DrawTextureScaledCol(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		if (texture != null) {
			boolean boolean1 = false;
		}

		if (this.isVisible()) {
			double double9 = double1 + this.getAbsoluteX();
			double double10 = double2 + this.getAbsoluteY();
			int int1 = (int)(double10 + this.yScroll);
			if (!((double)int1 + double4 < 0.0) && int1 <= 4096) {
				double8 = Math.min(double8 * (double)UIManager.FBO_ALPHA_MULT, 1.0);
				SpriteRenderer.instance.render(texture, (float)(double9 + this.xScroll), (float)(double10 + this.yScroll), (float)double3, (float)double4, (float)double5, (float)double6, (float)double7, (float)double8);
			}
		}
	}

	public void DrawTextureScaledCol(Texture texture, double double1, double double2, double double3, double double4, Color color) {
		if (texture != null) {
			boolean boolean1 = false;
		}

		if (this.isVisible()) {
			double double5 = double1 + this.getAbsoluteX();
			double double6 = double2 + this.getAbsoluteY();
			float float1 = Math.min(color.a * UIManager.FBO_ALPHA_MULT, 1.0F);
			SpriteRenderer.instance.render(texture, (float)(double5 + this.xScroll), (float)(double6 + this.yScroll), (float)double3, (float)double4, color.r, color.g, color.b, float1);
		}
	}

	public void DrawTextureScaledColor(Texture texture, Double Double1, Double Double2, Double Double3, Double Double4, Double Double5, Double Double6, Double Double7, Double Double8) {
		this.DrawTextureScaledCol(texture, Double1, Double2, Double3, Double4, Double5, Double6, Double7, Double8);
	}

	public void DrawTextureColor(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6) {
		tempcol.r = (float)double3;
		tempcol.g = (float)double4;
		tempcol.b = (float)double5;
		tempcol.a = (float)double6;
		this.DrawTextureCol(texture, double1, double2, tempcol);
	}

	public void DrawTextureIgnoreOffset(Texture texture, double double1, double double2, int int1, int int2, Color color) {
		if (this.isVisible()) {
			double double3 = double1 + this.getAbsoluteX();
			double double4 = double2 + this.getAbsoluteY();
			SpriteRenderer.instance.render(texture, (float)(double3 + this.xScroll), (float)(double4 + this.yScroll), (float)int1, (float)int2, color.r, color.g, color.b, color.a);
		}
	}

	public void DrawTexture_FlippedX(Texture texture, double double1, double double2, int int1, int int2, Color color) {
		if (this.isVisible()) {
			double double3 = double1 + this.getAbsoluteX();
			double double4 = double2 + this.getAbsoluteY();
			SpriteRenderer.instance.renderflipped(texture, (float)(double3 + this.xScroll), (float)(double4 + this.yScroll), (float)int1, (float)int2, color.r, color.g, color.b, color.a);
		}
	}

	public void DrawTexture_FlippedXIgnoreOffset(Texture texture, double double1, double double2, int int1, int int2, Color color) {
		if (this.isVisible()) {
			double double3 = double1 + this.getAbsoluteX();
			double double4 = double2 + this.getAbsoluteY();
			SpriteRenderer.instance.renderflipped(texture, (float)(double3 + this.xScroll), (float)(double4 + this.yScroll), (float)int1, (float)int2, color.r, color.g, color.b, color.a);
		}
	}

	public void DrawUVSliceTexture(Texture texture, double double1, double double2, double double3, double double4, Color color, double double5, double double6, double double7, double double8) {
		if (this.isVisible()) {
			double double9 = double1 + this.getAbsoluteX();
			double double10 = double2 + this.getAbsoluteY();
			double9 += (double)texture.offsetX;
			double10 += (double)texture.offsetY;
			Texture.lr = color.r;
			Texture.lg = color.g;
			Texture.lb = color.b;
			Texture.la = color.a;
			double double11 = (double)texture.getXStart();
			double double12 = (double)texture.getYStart();
			double double13 = (double)texture.getXEnd();
			double double14 = (double)texture.getYEnd();
			double double15 = double13 - double11;
			double double16 = double14 - double12;
			double double17 = double7 - double5;
			double double18 = double8 - double6;
			double double19 = double17 / 1.0;
			double double20 = double18 / 1.0;
			double11 += double5 * double15;
			double12 += double6 * double16;
			double13 -= (1.0 - double7) * double15;
			double14 -= (1.0 - double8) * double16;
			double11 = (double)((float)((int)(double11 * 1000.0)) / 1000.0F);
			double13 = (double)((float)((int)(double13 * 1000.0)) / 1000.0F);
			double12 = (double)((float)((int)(double12 * 1000.0)) / 1000.0F);
			double14 = (double)((float)((int)(double14 * 1000.0)) / 1000.0F);
			double double21 = double9 + double3;
			double double22 = double10 + double4;
			double9 += double5 * double3;
			double10 += double6 * double4;
			double21 -= (1.0 - double7) * double3;
			double22 -= (1.0 - double8) * double4;
			SpriteRenderer.instance.render(texture, (float)double9 + (float)this.getXScroll().intValue(), (float)double10 + (float)this.getYScroll().intValue(), (float)(double21 - double9), (float)(double22 - double10), color.r, color.g, color.b, color.a, (float)double11, (float)double12, (float)double13, (float)double12, (float)double13, (float)double14, (float)double11, (float)double14);
		}
	}

	public Boolean getScrollChildren() {
		return this.bScrollChildren ? Boolean.TRUE : Boolean.FALSE;
	}

	public void setScrollChildren(boolean boolean1) {
		this.bScrollChildren = boolean1;
	}

	public Boolean getScrollWithParent() {
		return this.bScrollWithParent ? Boolean.TRUE : Boolean.FALSE;
	}

	public void setScrollWithParent(boolean boolean1) {
		this.bScrollWithParent = boolean1;
	}

	public Double getAbsoluteX() {
		if (this.getParent() != null) {
			return this.getParent().getScrollChildren() && this.getScrollWithParent() ? BoxedStaticValues.toDouble(this.getParent().getAbsoluteX() + (double)this.getX().intValue() + (double)this.getParent().getXScroll().intValue()) : BoxedStaticValues.toDouble(this.getParent().getAbsoluteX() + (double)this.getX().intValue());
		} else {
			return BoxedStaticValues.toDouble((double)this.getX().intValue());
		}
	}

	public Double getAbsoluteY() {
		if (this.getParent() != null) {
			return this.getParent().getScrollChildren() && this.getScrollWithParent() ? BoxedStaticValues.toDouble(this.getParent().getAbsoluteY() + (double)this.getY().intValue() + (double)this.getParent().getYScroll().intValue()) : BoxedStaticValues.toDouble(this.getParent().getAbsoluteY() + (double)this.getY().intValue());
		} else {
			return BoxedStaticValues.toDouble((double)this.getY().intValue());
		}
	}

	public String getClickedValue() {
		return this.clickedValue;
	}

	public void bringToTop() {
		UIManager.pushToTop(this);
		if (this.Parent != null) {
			this.Parent.addBringToTop(this);
		}
	}

	void onRightMouseUpOutside(double double1, double double2) {
		if (this.getTable() != null && this.getTable().rawget("onRightMouseUpOutside") != null) {
			Object[] objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onRightMouseUpOutside"), this.table, double1 - this.xScroll, double2 - this.yScroll);
		}

		for (int int1 = this.getControls().size() - 1; int1 >= 0; --int1) {
			UIElement uIElement = (UIElement)this.getControls().get(int1);
			uIElement.onRightMouseUpOutside(double1 - (double)uIElement.getXScrolled(this).intValue(), double2 - (double)uIElement.getYScrolled(this).intValue());
		}
	}

	void onRightMouseDownOutside(double double1, double double2) {
		if (this.getTable() != null && this.getTable().rawget("onRightMouseDownOutside") != null) {
			Object[] objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onRightMouseDownOutside"), this.table, double1 - this.xScroll, double2 - this.yScroll);
		}

		for (int int1 = this.getControls().size() - 1; int1 >= 0; --int1) {
			UIElement uIElement = (UIElement)this.getControls().get(int1);
			uIElement.onRightMouseDownOutside(double1 - (double)uIElement.getXScrolled(this).intValue(), double2 - (double)uIElement.getYScrolled(this).intValue());
		}
	}

	void onMouseUpOutside(double double1, double double2) {
		if (this.getTable() != null && this.getTable().rawget("onMouseUpOutside") != null) {
			Object[] objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onMouseUpOutside"), this.table, double1 - this.xScroll, double2 - this.yScroll);
		}

		for (int int1 = this.getControls().size() - 1; int1 >= 0; --int1) {
			UIElement uIElement = (UIElement)this.getControls().get(int1);
			uIElement.onMouseUpOutside(double1 - (double)uIElement.getXScrolled(this).intValue(), double2 - (double)uIElement.getYScrolled(this).intValue());
		}
	}

	void onMouseDownOutside(double double1, double double2) {
		if (this.getTable() != null && this.getTable().rawget("onMouseDownOutside") != null) {
			Object[] objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onMouseDownOutside"), this.table, double1 - this.xScroll, double2 - this.yScroll);
		}

		for (int int1 = this.getControls().size() - 1; int1 >= 0; --int1) {
			UIElement uIElement = (UIElement)this.getControls().get(int1);
			uIElement.onMouseDownOutside(double1 - (double)uIElement.getX().intValue(), double2 - (double)uIElement.getY().intValue());
		}
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (this.clicked && UIManager.isDoubleClick((double)((int)this.clickX), (double)((int)this.clickY), (double)((int)double1), (double)((int)double2), (double)this.leftDownTime) && this.getTable() != null && this.getTable().rawget("onMouseDoubleClick") != null) {
			this.clicked = false;
			return this.onMouseDoubleClick(double1, double2) ? Boolean.TRUE : Boolean.FALSE;
		} else {
			this.clicked = true;
			this.clickX = double1;
			this.clickY = double2;
			this.leftDownTime = System.currentTimeMillis();
			if (this.Parent != null && this.Parent.maxDrawHeight != -1 && (double)this.Parent.maxDrawHeight <= double2) {
				return Boolean.FALSE;
			} else if (this.maxDrawHeight != -1 && (double)this.maxDrawHeight <= double2) {
				return Boolean.FALSE;
			} else if (!this.visible) {
				return Boolean.FALSE;
			} else {
				if (this.getTable() != null && this.getTable().rawget("onFocus") != null) {
					Object[] objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onFocus"), this.table, double1 - this.xScroll, double2 - this.yScroll);
				}

				boolean boolean1 = false;
				for (int int1 = this.getControls().size() - 1; int1 >= 0; --int1) {
					UIElement uIElement = (UIElement)this.getControls().get(int1);
					if (!boolean1 && (double1 > uIElement.getXScrolled(this) && double2 > uIElement.getYScrolled(this) && double1 < uIElement.getXScrolled(this) + uIElement.getWidth() && double2 < uIElement.getYScrolled(this) + uIElement.getHeight() || uIElement.isCapture())) {
						if (uIElement.onMouseDown(double1 - (double)uIElement.getXScrolled(this).intValue(), double2 - (double)uIElement.getYScrolled(this).intValue())) {
							boolean1 = true;
						}
					} else if (uIElement.getTable() != null && uIElement.getTable().rawget("onMouseDownOutside") != null) {
						Object[] objectArray2 = LuaManager.caller.pcall(UIManager.getDefaultThread(), uIElement.getTable().rawget("onMouseDownOutside"), uIElement.getTable(), double1 - this.xScroll, double2 - this.yScroll);
					}
				}

				if (this.getTable() != null) {
					Object[] objectArray3;
					if (boolean1) {
						if (this.getTable().rawget("onMouseDownOutside") != null) {
							objectArray3 = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onMouseDownOutside"), this.table, double1 - this.xScroll, double2 - this.yScroll);
							if (objectArray3.length == 1) {
								return true;
							}

							if (objectArray3.length > 1 && objectArray3[1] instanceof Boolean && (Boolean)((Boolean)objectArray3[1])) {
								return Boolean.TRUE;
							}
						}
					} else if (this.getTable().rawget("onMouseDown") != null) {
						objectArray3 = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onMouseDown"), this.table, double1 - this.xScroll, double2 - this.yScroll);
						if (objectArray3.length == 1) {
							return true;
						}

						if (objectArray3.length > 1 && objectArray3[1] instanceof Boolean && (Boolean)((Boolean)objectArray3[1])) {
							return Boolean.TRUE;
						}
					}
				}

				return boolean1;
			}
		}
	}

	private Boolean onMouseDoubleClick(double double1, double double2) {
		if (this.Parent != null && this.Parent.maxDrawHeight != -1 && (double)this.Parent.maxDrawHeight <= this.y) {
			return Boolean.FALSE;
		} else if (this.maxDrawHeight != -1 && (double)this.maxDrawHeight <= this.y) {
			return Boolean.FALSE;
		} else if (!this.visible) {
			return Boolean.FALSE;
		} else {
			if (this.getTable().rawget("onMouseDoubleClick") != null) {
				Object[] objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onMouseDoubleClick"), this.table, double1 - this.xScroll, double2 - this.yScroll);
				if (objectArray.length == 1) {
					return true;
				}

				if (objectArray.length > 1 && objectArray[1] instanceof Boolean && (Boolean)((Boolean)objectArray[1])) {
					return Boolean.TRUE;
				}
			}

			return Boolean.TRUE;
		}
	}

	public Boolean onMouseWheel(double double1) {
		int int1 = Mouse.getXA();
		int int2 = Mouse.getYA();
		if (this.getTable() != null && this.getTable().rawget("onMouseWheel") != null) {
			Object[] objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onMouseWheel"), this.table, double1);
			if (objectArray.length > 1 && objectArray[1] instanceof Boolean && (Boolean)((Boolean)objectArray[1])) {
				return Boolean.TRUE;
			}
		}

		for (int int3 = this.getControls().size() - 1; int3 >= 0; --int3) {
			UIElement uIElement = (UIElement)this.getControls().get(int3);
			if (uIElement.isVisible() && ((double)int1 >= uIElement.getAbsoluteX() && (double)int2 >= uIElement.getAbsoluteY() && (double)int1 < uIElement.getAbsoluteX() + uIElement.getWidth() && (double)int2 < uIElement.getAbsoluteY() + uIElement.getHeight() || uIElement.isCapture()) && uIElement.onMouseWheel(double1)) {
				return this.bConsumeMouseEvents ? Boolean.TRUE : Boolean.FALSE;
			}
		}

		return Boolean.FALSE;
	}

	public Boolean onMouseMove(double double1, double double2) {
		int int1 = Mouse.getXA();
		int int2 = Mouse.getYA();
		if (this.Parent != null && this.Parent.maxDrawHeight != -1 && (double)this.Parent.maxDrawHeight <= this.y) {
			return Boolean.FALSE;
		} else if (this.maxDrawHeight != -1 && (double)this.maxDrawHeight <= (double)int2 - this.getAbsoluteY()) {
			return Boolean.FALSE;
		} else if (!this.visible) {
			return Boolean.FALSE;
		} else {
			if (this.getTable() != null && this.getTable().rawget("onMouseMove") != null) {
				Object[] objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onMouseMove"), this.table, double1, double2);
			}

			boolean boolean1 = false;
			for (int int3 = this.getControls().size() - 1; int3 >= 0; --int3) {
				UIElement uIElement = (UIElement)this.getControls().get(int3);
				if ((!((double)int1 >= uIElement.getAbsoluteX()) || !((double)int2 >= uIElement.getAbsoluteY()) || !((double)int1 < uIElement.getAbsoluteX() + uIElement.getWidth()) || !((double)int2 < uIElement.getAbsoluteY() + uIElement.getHeight())) && !uIElement.isCapture()) {
					uIElement.onMouseMoveOutside(double1, double2);
				} else if (!boolean1 && uIElement.onMouseMove(double1, double2)) {
					boolean1 = true;
				}
			}

			return this.bConsumeMouseEvents ? Boolean.TRUE : Boolean.FALSE;
		}
	}

	public void onMouseMoveOutside(double double1, double double2) {
		int int1 = Mouse.getX();
		int int2 = Mouse.getY();
		if (this.getTable() != null && this.getTable().rawget("onMouseMoveOutside") != null) {
			Object[] objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onMouseMoveOutside"), this.table, double1, double2);
		}

		for (int int3 = this.getControls().size() - 1; int3 >= 0; --int3) {
			UIElement uIElement = (UIElement)this.getControls().get(int3);
			uIElement.onMouseMoveOutside(double1, double2);
		}
	}

	public Boolean onMouseUp(double double1, double double2) {
		if (this.Parent != null && this.Parent.maxDrawHeight != -1 && (double)this.Parent.maxDrawHeight <= double2) {
			return Boolean.FALSE;
		} else if (this.maxDrawHeight != -1 && (double)this.maxDrawHeight <= double2) {
			return Boolean.FALSE;
		} else if (!this.visible) {
			return Boolean.FALSE;
		} else {
			boolean boolean1 = false;
			for (int int1 = this.getControls().size() - 1; int1 >= 0; --int1) {
				UIElement uIElement = (UIElement)this.getControls().get(int1);
				if (boolean1 || (!(double1 >= uIElement.getXScrolled(this)) || !(double2 >= uIElement.getYScrolled(this)) || !(double1 < uIElement.getXScrolled(this) + uIElement.getWidth()) || !(double2 < uIElement.getYScrolled(this) + uIElement.getHeight())) && !uIElement.isCapture()) {
					uIElement.onMouseUpOutside(double1 - (double)uIElement.getXScrolled(this).intValue(), double2 - (double)uIElement.getYScrolled(this).intValue());
				} else if (uIElement.onMouseUp(double1 - (double)uIElement.getXScrolled(this).intValue(), double2 - (double)uIElement.getYScrolled(this).intValue())) {
					boolean1 = true;
				}
			}

			if (this.getTable() != null) {
				Object[] objectArray;
				if (boolean1) {
					if (this.getTable().rawget("onMouseUpOutside") != null) {
						objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onMouseUpOutside"), this.table, double1 - this.xScroll, double2 - this.yScroll);
						if (objectArray.length == 1) {
							return true;
						}

						if (objectArray.length > 1 && objectArray[1] instanceof Boolean && (Boolean)((Boolean)objectArray[1])) {
							return Boolean.TRUE;
						}
					}
				} else if (this.getTable().rawget("onMouseUp") != null) {
					objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onMouseUp"), this.table, double1 - this.xScroll, double2 - this.yScroll);
					if (objectArray.length == 1) {
						return true;
					}

					if (objectArray.length > 1 && objectArray[1] instanceof Boolean && (Boolean)((Boolean)objectArray[1])) {
						return Boolean.TRUE;
					}
				}
			}

			return boolean1 ? Boolean.TRUE : Boolean.FALSE;
		}
	}

	public void onresize() {
	}

	public void onResize() {
		if (this.Parent != null && this.Parent.bResizeDirty) {
			double double1 = this.Parent.getWidth() - this.Parent.lastwidth;
			double double2 = this.Parent.getHeight() - this.Parent.lastheight;
			if (!this.anchorTop && this.anchorBottom) {
				this.setY(this.getY() + double2);
			}

			if (this.anchorTop && this.anchorBottom) {
				this.setHeight(this.getHeight() + double2);
			}

			if (!this.anchorLeft && this.anchorRight) {
				this.setX(this.getX() + double1);
			}

			if (this.anchorLeft && this.anchorRight) {
				this.setWidth(this.getWidth() + double1);
			}
		}

		if (this.getTable() != null && this.getTable().rawget("onResize") != null) {
			LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.getTable().rawget("onResize"), this.table, this.getWidth(), this.getHeight());
		}

		for (int int1 = this.getControls().size() - 1; int1 >= 0; --int1) {
			UIElement uIElement = (UIElement)this.getControls().get(int1);
			if (uIElement == null) {
				this.RemoveChild(uIElement);
				--int1;
			} else {
				uIElement.onResize();
			}
		}

		this.bResizeDirty = false;
		this.lastwidth = this.getWidth();
		this.lastheight = this.getHeight();
	}

	public Boolean onRightMouseDown(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else if (this.Parent != null && this.Parent.maxDrawHeight != -1 && (double)this.Parent.maxDrawHeight <= double2) {
			return Boolean.FALSE;
		} else if (this.maxDrawHeight != -1 && (double)this.maxDrawHeight <= double2) {
			return Boolean.FALSE;
		} else {
			boolean boolean1 = false;
			for (int int1 = this.getControls().size() - 1; int1 >= 0; --int1) {
				UIElement uIElement = (UIElement)this.getControls().get(int1);
				if (!boolean1 && (double1 >= uIElement.getXScrolled(this) && double2 >= uIElement.getYScrolled(this) && double1 < uIElement.getXScrolled(this) + uIElement.getWidth() && double2 < uIElement.getYScrolled(this) + uIElement.getHeight() || uIElement.isCapture())) {
					if (uIElement.onRightMouseDown(double1 - (double)uIElement.getXScrolled(this).intValue(), double2 - (double)uIElement.getYScrolled(this).intValue())) {
						boolean1 = true;
					}
				} else if (uIElement.getTable() != null && uIElement.getTable().rawget("onRightMouseDownOutside") != null) {
					Object[] objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), uIElement.getTable().rawget("onRightMouseDownOutside"), uIElement.getTable(), double1 - this.xScroll, double2 - this.yScroll);
				}
			}

			if (this.getTable() != null) {
				Object[] objectArray2;
				if (boolean1) {
					if (this.getTable().rawget("onRightMouseDownOutside") != null) {
						objectArray2 = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onRightMouseDownOutside"), this.table, double1 - this.xScroll, double2 - this.yScroll);
						if (objectArray2.length == 1) {
							return true;
						}

						if (objectArray2.length > 1 && objectArray2[1] instanceof Boolean && (Boolean)((Boolean)objectArray2[1])) {
							return Boolean.TRUE;
						}
					}
				} else if (this.getTable().rawget("onRightMouseDown") != null) {
					objectArray2 = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onRightMouseDown"), this.table, double1 - this.xScroll, double2 - this.yScroll);
					if (objectArray2.length == 1) {
						return true;
					}

					if (objectArray2.length > 1 && objectArray2[1] instanceof Boolean && (Boolean)((Boolean)objectArray2[1])) {
						return Boolean.TRUE;
					}
				}
			}

			return boolean1 ? Boolean.TRUE : Boolean.FALSE;
		}
	}

	public Boolean onRightMouseUp(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else if (this.Parent != null && this.Parent.maxDrawHeight != -1 && (double)this.Parent.maxDrawHeight <= double2) {
			return Boolean.FALSE;
		} else if (this.maxDrawHeight != -1 && (double)this.maxDrawHeight <= double2) {
			return Boolean.FALSE;
		} else {
			boolean boolean1 = false;
			for (int int1 = this.getControls().size() - 1; int1 >= 0; --int1) {
				UIElement uIElement = (UIElement)this.getControls().get(int1);
				if (boolean1 || (!(double1 >= uIElement.getXScrolled(this)) || !(double2 >= uIElement.getYScrolled(this)) || !(double1 < uIElement.getXScrolled(this) + uIElement.getWidth()) || !(double2 < uIElement.getYScrolled(this) + uIElement.getHeight())) && !uIElement.isCapture()) {
					uIElement.onRightMouseUpOutside(double1 - (double)uIElement.getXScrolled(this).intValue(), double2 - (double)uIElement.getYScrolled(this).intValue());
				} else if (uIElement.onRightMouseUp(double1 - (double)uIElement.getXScrolled(this).intValue(), double2 - (double)uIElement.getYScrolled(this).intValue())) {
					boolean1 = true;
				}
			}

			if (this.getTable() != null) {
				Object[] objectArray;
				if (boolean1) {
					if (this.getTable().rawget("onRightMouseUpOutside") != null) {
						objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onRightMouseUpOutside"), this.table, double1 - this.xScroll, double2 - this.yScroll);
						if (objectArray.length == 1) {
							return true;
						}

						if (objectArray.length > 1 && objectArray[1] instanceof Boolean && (Boolean)((Boolean)objectArray[1])) {
							return Boolean.TRUE;
						}
					}
				} else if (this.getTable().rawget("onRightMouseUp") != null) {
					objectArray = LuaManager.caller.pcall(UIManager.getDefaultThread(), this.getTable().rawget("onRightMouseUp"), this.table, double1 - this.xScroll, double2 - this.yScroll);
					if (objectArray.length == 1) {
						return true;
					}

					if (objectArray.length > 1 && objectArray[1] instanceof Boolean && (Boolean)((Boolean)objectArray[1])) {
						return Boolean.TRUE;
					}
				}
			}

			return boolean1 ? Boolean.TRUE : Boolean.FALSE;
		}
	}

	public void RemoveControl(UIElement uIElement) {
		this.getControls().remove(uIElement);
		uIElement.setParent((UIElement)null);
	}

	public void render() {
		if (this.enabled) {
			if (this.isVisible()) {
				if (this.Parent == null || this.Parent.maxDrawHeight == -1 || !((double)this.Parent.maxDrawHeight <= this.y)) {
					if (this.getTable() != null && this.getTable().rawget("prerender") != null) {
						try {
							LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.getTable().rawget("prerender"), (Object)this.table);
						} catch (Exception exception) {
							boolean boolean1 = false;
						}
					}

					for (int int1 = 0; int1 < this.getControls().size(); ++int1) {
						((UIElement)this.getControls().get(int1)).render();
					}

					if (this.getTable() != null && this.getTable().rawget("render") != null) {
						LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.getTable().rawget("render"), (Object)this.table);
					}

					if (Core.bDebug && DebugOptions.instance.UIRenderOutline.getValue()) {
						if (this.table != null && "ISScrollingListBox".equals(this.table.rawget("Type"))) {
							this.repaintStencilRect(0.0, 0.0, (double)((int)this.width), (double)((int)this.height));
						}

						Double Double1 = -this.getXScroll();
						Double Double2 = -this.getYScroll();
						this.DrawTextureScaledColor((Texture)null, Double1, Double2, 1.0, (double)this.height, 1.0, 1.0, 1.0, 0.5);
						this.DrawTextureScaledColor((Texture)null, Double1 + 1.0, Double2, (double)this.width - 2.0, 1.0, 1.0, 1.0, 1.0, 0.5);
						this.DrawTextureScaledColor((Texture)null, Double1 + (double)this.width - 1.0, Double2, 1.0, (double)this.height, 1.0, 1.0, 1.0, 0.5);
						this.DrawTextureScaledColor((Texture)null, Double1 + 1.0, Double2 + (double)this.height - 1.0, (double)this.width - 2.0, 1.0, 1.0, 1.0, 1.0, 0.5);
					}
				}
			}
		}
	}

	public void update() {
		if (this.enabled) {
			int int1;
			for (int1 = 0; int1 < this.Controls.size(); ++int1) {
				if (this.toTop.contains(this.Controls.get(int1))) {
					UIElement uIElement = (UIElement)this.Controls.remove(int1);
					--int1;
					toAdd.add(uIElement);
				}
			}

			this.Controls.addAll(toAdd);
			toAdd.clear();
			this.toTop.clear();
			if (UIManager.doTick && this.getTable() != null && this.getTable().rawget("update") != null) {
				LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.getTable().rawget("update"), (Object)this.table);
			}

			if (this.bResizeDirty) {
				this.onResize();
				this.lastwidth = (double)this.width;
				this.lastheight = (double)this.height;
				this.bResizeDirty = false;
			}

			for (int1 = 0; int1 < this.getControls().size(); ++int1) {
				((UIElement)this.getControls().get(int1)).update();
			}

			if (this.isFollowGameWorld()) {
				this.setX(this.getX() - (double)(IsoCamera.getOffX() - IsoCamera.getLastOffX()));
				this.setY(this.getY() - (double)(IsoCamera.getOffY() - IsoCamera.getLastOffY()));
			}
		}
	}

	public void BringToTop(UIElement uIElement) {
		this.getControls().remove(uIElement);
		this.getControls().add(uIElement);
	}

	public Boolean isCapture() {
		return this.capture ? Boolean.TRUE : Boolean.FALSE;
	}

	public void setCapture(boolean boolean1) {
		this.capture = boolean1;
	}

	public Boolean isIgnoreLossControl() {
		return this.IgnoreLossControl ? Boolean.TRUE : Boolean.FALSE;
	}

	public void setIgnoreLossControl(boolean boolean1) {
		this.IgnoreLossControl = boolean1;
	}

	public void setClickedValue(String string) {
		this.clickedValue = string;
	}

	public ArrayList getControls() {
		return this.Controls;
	}

	public void setControls(Vector vector) {
		this.setControls(vector);
	}

	public Boolean isDefaultDraw() {
		return this.defaultDraw ? Boolean.TRUE : Boolean.FALSE;
	}

	public void setDefaultDraw(boolean boolean1) {
		this.defaultDraw = boolean1;
	}

	public Boolean isFollowGameWorld() {
		return this.followGameWorld ? Boolean.TRUE : Boolean.FALSE;
	}

	public void setFollowGameWorld(boolean boolean1) {
		this.followGameWorld = boolean1;
	}

	public Double getHeight() {
		return BoxedStaticValues.toDouble((double)this.height);
	}

	public UIElement getParent() {
		return this.Parent;
	}

	public void setParent(UIElement uIElement) {
		this.Parent = uIElement;
	}

	public Boolean isVisible() {
		return this.visible ? Boolean.TRUE : Boolean.FALSE;
	}

	public void setVisible(boolean boolean1) {
		this.visible = boolean1;
	}

	public Double getWidth() {
		return BoxedStaticValues.toDouble((double)this.width);
	}

	public void setWidth(double double1) {
		if ((double)this.width != double1) {
			this.bResizeDirty = true;
		}

		this.lastwidth = (double)this.width;
		this.width = (float)double1;
	}

	public Double getX() {
		return BoxedStaticValues.toDouble(this.x);
	}

	public Double getXScrolled(UIElement uIElement) {
		return uIElement != null && uIElement.bScrollChildren && this.bScrollWithParent ? BoxedStaticValues.toDouble(this.x + uIElement.getXScroll()) : BoxedStaticValues.toDouble(this.x);
	}

	public Double getYScrolled(UIElement uIElement) {
		return uIElement != null && uIElement.bScrollChildren && this.bScrollWithParent ? BoxedStaticValues.toDouble(this.y + uIElement.getYScroll()) : BoxedStaticValues.toDouble(this.y);
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean boolean1) {
		this.enabled = boolean1;
	}

	public void setX(double double1) {
		this.x = (double)((float)double1);
	}

	public Double getY() {
		return BoxedStaticValues.toDouble(this.y);
	}

	public void setY(double double1) {
		this.y = (double)((float)double1);
	}

	public void suspendStencil() {
		IndieGL.disableStencilTest();
		IndieGL.disableAlphaTest();
	}

	public void resumeStencil() {
		IndieGL.enableStencilTest();
		IndieGL.enableAlphaTest();
	}

	public void setStencilRect(double double1, double double2, double double3, double double4) {
		if (white == null) {
			white = Texture.getSharedTexture("white");
		}

		double1 += this.getAbsoluteX();
		double2 += this.getAbsoluteY();
		IndieGL.glStencilMask(255);
		IndieGL.enableStencilTest();
		IndieGL.enableAlphaTest();
		++StencilLevel;
		IndieGL.glStencilFunc(519, StencilLevel, 255);
		IndieGL.glStencilOp(7680, 7680, 7681);
		SpriteRenderer.instance.render(white, (int)double1, (int)double2, (int)double3, (int)double4, 0.0F, 0.0F, 0.0F, 0.01F);
		IndieGL.glStencilOp(7680, 7680, 7680);
		IndieGL.glStencilFunc(514, StencilLevel, 255);
	}

	public void clearStencilRect() {
		if (StencilLevel > 0) {
			--StencilLevel;
		}

		if (StencilLevel > 0) {
			IndieGL.glStencilFunc(514, StencilLevel, 255);
		} else {
			IndieGL.glAlphaFunc(519, 0.0F);
			IndieGL.disableStencilTest();
			IndieGL.disableAlphaTest();
			IndieGL.glStencilFunc(519, 255, 255);
			IndieGL.glStencilOp(7680, 7680, 7680);
			IndieGL.glClear(1280);
		}
	}

	public void repaintStencilRect(double double1, double double2, double double3, double double4) {
		if (StencilLevel > 0) {
			if (white == null) {
				white = Texture.getSharedTexture("white");
			}

			double1 += this.getAbsoluteX();
			double2 += this.getAbsoluteY();
			IndieGL.glStencilFunc(519, StencilLevel, 255);
			IndieGL.glStencilOp(7680, 7680, 7681);
			SpriteRenderer.instance.render(white, (int)double1, (int)double2, (int)double3, (int)double4, 0.0F, 0.0F, 0.0F, 0.01F);
			IndieGL.glStencilOp(7680, 7680, 7680);
			IndieGL.glStencilFunc(514, StencilLevel, 255);
		}
	}

	public KahluaTable getTable() {
		return this.table;
	}

	public void setTable(KahluaTable kahluaTable) {
		this.table = kahluaTable;
	}

	public void setHeight(double double1) {
		if ((double)this.height != double1) {
			this.bResizeDirty = true;
		}

		this.lastheight = (double)this.height;
		this.height = (float)double1;
	}

	public void setHeightSilent(double double1) {
		this.lastheight = (double)this.height;
		this.height = (float)double1;
	}

	public void setWidthSilent(double double1) {
		this.lastwidth = (double)this.width;
		this.width = (float)double1;
	}

	public void setHeightOnly(double double1) {
		this.height = (float)double1;
	}

	public void setWidthOnly(double double1) {
		this.width = (float)double1;
	}

	public boolean isAnchorTop() {
		return this.anchorTop;
	}

	public void ignoreWidthChange() {
		this.lastwidth = (double)this.width;
	}

	public void ignoreHeightChange() {
		this.lastheight = (double)this.height;
	}

	public void setAnchorTop(boolean boolean1) {
		this.anchorTop = boolean1;
		this.lastwidth = (double)this.width;
		this.lastheight = (double)this.height;
	}

	public Boolean isAnchorLeft() {
		return this.anchorLeft ? Boolean.TRUE : Boolean.FALSE;
	}

	public void setAnchorLeft(boolean boolean1) {
		this.anchorLeft = boolean1;
		this.lastwidth = (double)this.width;
		this.lastheight = (double)this.height;
	}

	public Boolean isAnchorRight() {
		return this.anchorRight ? Boolean.TRUE : Boolean.FALSE;
	}

	public void setAnchorRight(boolean boolean1) {
		this.anchorRight = boolean1;
		this.lastwidth = (double)this.width;
		this.lastheight = (double)this.height;
	}

	public Boolean isAnchorBottom() {
		return this.anchorBottom ? Boolean.TRUE : Boolean.FALSE;
	}

	public void setAnchorBottom(boolean boolean1) {
		this.anchorBottom = boolean1;
		this.lastwidth = (double)this.width;
		this.lastheight = (double)this.height;
	}

	private void addBringToTop(UIElement uIElement) {
		this.toTop.add(uIElement);
	}

	public int getPlayerContext() {
		return this.playerContext;
	}

	public void setPlayerContext(int int1) {
		this.playerContext = int1;
	}

	public String getUIName() {
		return this.uiname;
	}

	public void setUIName(String string) {
		this.uiname = string != null ? string : "";
	}

	public Double clampToParentX(double double1) {
		if (this.getParent() == null) {
			return BoxedStaticValues.toDouble(double1);
		} else {
			double double2 = this.getParent().clampToParentX(this.getParent().getAbsoluteX());
			double double3 = this.getParent().clampToParentX(double2 + (double)this.getParent().getWidth().intValue());
			if (double1 < double2) {
				double1 = double2;
			}

			if (double1 > double3) {
				double1 = double3;
			}

			return BoxedStaticValues.toDouble(double1);
		}
	}

	public Double clampToParentY(double double1) {
		if (this.getParent() == null) {
			return double1;
		} else {
			double double2 = this.getParent().clampToParentY(this.getParent().getAbsoluteY());
			double double3 = this.getParent().clampToParentY(double2 + (double)this.getParent().getHeight().intValue());
			if (double1 < double2) {
				double1 = double2;
			}

			if (double1 > double3) {
				double1 = double3;
			}

			return double1;
		}
	}

	public Boolean isPointOver(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else {
			int int1 = this.getHeight().intValue();
			if (this.maxDrawHeight != -1) {
				int1 = Math.min(int1, this.maxDrawHeight);
			}

			double double3 = double1 - this.getAbsoluteX();
			double double4 = double2 - this.getAbsoluteY();
			if (!(double3 < 0.0) && !(double3 >= this.getWidth()) && !(double4 < 0.0) && !(double4 >= (double)int1)) {
				if (this.Parent == null) {
					Stack stack = UIManager.getUI();
					for (int int2 = stack.size() - 1; int2 >= 0; --int2) {
						UIElement uIElement = (UIElement)stack.get(int2);
						if (uIElement == this) {
							break;
						}

						if (uIElement.isPointOver(double1, double2)) {
							return Boolean.FALSE;
						}
					}

					return Boolean.TRUE;
				} else {
					for (int int3 = this.Parent.Controls.size() - 1; int3 >= 0; --int3) {
						UIElement uIElement2 = (UIElement)this.Parent.Controls.get(int3);
						if (uIElement2 == this) {
							break;
						}

						if (uIElement2.isVisible()) {
							int1 = uIElement2.getHeight().intValue();
							if (uIElement2.maxDrawHeight != -1) {
								int1 = Math.min(int1, uIElement2.maxDrawHeight);
							}

							double3 = double1 - uIElement2.getAbsoluteX();
							double4 = double2 - uIElement2.getAbsoluteY();
							if (double3 >= 0.0 && double3 < uIElement2.getWidth() && double4 >= 0.0 && double4 < (double)int1) {
								return Boolean.FALSE;
							}
						}
					}

					return this.Parent.isPointOver(double1, double2) ? Boolean.TRUE : Boolean.FALSE;
				}
			} else {
				return Boolean.FALSE;
			}
		}
	}

	public Boolean isMouseOver() {
		return this.isPointOver((double)Mouse.getXA(), (double)Mouse.getYA()) ? Boolean.TRUE : Boolean.FALSE;
	}
}
