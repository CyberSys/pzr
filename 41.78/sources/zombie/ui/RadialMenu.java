package zombie.ui;

import java.util.ArrayList;
import zombie.core.SpriteRenderer;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.textures.Texture;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.util.StringUtils;


public final class RadialMenu extends UIElement {
	protected int outerRadius = 200;
	protected int innerRadius = 100;
	protected ArrayList slices = new ArrayList();
	protected int highlight = -1;
	protected int joypad = -1;
	protected UITransition transition = new UITransition();
	protected UITransition select = new UITransition();
	protected UITransition deselect = new UITransition();
	protected int selectIndex = -1;
	protected int deselectIndex = -1;

	public RadialMenu(int int1, int int2, int int3, int int4) {
		this.setX((double)int1);
		this.setY((double)int2);
		this.setWidth((double)(int4 * 2));
		this.setHeight((double)(int4 * 2));
		this.innerRadius = int3;
		this.outerRadius = int4;
	}

	public void update() {
	}

	public void render() {
		if (this.isVisible()) {
			this.transition.setIgnoreUpdateTime(true);
			this.transition.setFadeIn(true);
			this.transition.update();
			if (!this.slices.isEmpty()) {
				float float1 = this.transition.fraction();
				float float2 = (float)this.innerRadius * 0.85F + (float)this.innerRadius * float1 * 0.15F;
				float float3 = (float)this.outerRadius * 0.85F + (float)this.outerRadius * float1 * 0.15F;
				float float4;
				double double1;
				double double2;
				double double3;
				double double4;
				double double5;
				double double6;
				double double7;
				for (int int1 = 0; int1 < 48; ++int1) {
					float4 = 7.5F;
					double double8 = Math.toRadians((double)((float)int1 * float4));
					double double9 = Math.toRadians((double)((float)(int1 + 1) * float4));
					double double10 = this.x + (double)(this.width / 2.0F);
					double1 = this.y + (double)(this.height / 2.0F);
					double2 = this.x + (double)(this.width / 2.0F);
					double3 = this.y + (double)(this.height / 2.0F);
					double4 = this.x + (double)(this.width / 2.0F) + (double)(float3 * (float)Math.cos(double8));
					double5 = this.y + (double)(this.height / 2.0F) + (double)(float3 * (float)Math.sin(double8));
					double6 = this.x + (double)(this.width / 2.0F) + (double)(float3 * (float)Math.cos(double9));
					double7 = this.y + (double)(this.height / 2.0F) + (double)(float3 * (float)Math.sin(double9));
					if (int1 == 47) {
						double7 = double3;
					}

					float float5 = 0.1F;
					float float6 = 0.1F;
					float float7 = 0.1F;
					float float8 = 0.45F + 0.45F * float1;
					SpriteRenderer.instance.renderPoly((float)double10, (float)double1, (float)double4, (float)double5, (float)double6, (float)double7, (float)double2, (float)double3, float5, float6, float7, float8);
				}

				float float9 = 360.0F / (float)Math.max(this.slices.size(), 2);
				float4 = this.slices.size() == 1 ? 0.0F : 1.5F;
				int int2 = this.highlight;
				if (int2 == -1) {
					if (this.joypad != -1) {
						int2 = this.getSliceIndexFromJoypad(this.joypad);
					} else {
						int2 = this.getSliceIndexFromMouse(Mouse.getXA() - this.getAbsoluteX().intValue(), Mouse.getYA() - this.getAbsoluteY().intValue());
					}
				}

				RadialMenu.Slice slice = this.getSlice(int2);
				if (slice != null && slice.isEmpty()) {
					int2 = -1;
				}

				if (int2 != this.selectIndex) {
					this.select.reset();
					this.select.setIgnoreUpdateTime(true);
					if (this.selectIndex != -1) {
						this.deselectIndex = this.selectIndex;
						this.deselect.reset();
						this.deselect.setFadeIn(false);
						this.deselect.init(66.666664F, true);
					}

					this.selectIndex = int2;
				}

				this.select.update();
				this.deselect.update();
				float float10 = this.getStartAngle() - 180.0F;
				for (int int3 = 0; int3 < this.slices.size(); ++int3) {
					int int4 = Math.max(6, 48 / Math.max(this.slices.size(), 2));
					for (int int5 = 0; int5 < int4; ++int5) {
						double1 = Math.toRadians((double)(float10 + (float)int3 * float9 + (float)int5 * float9 / (float)int4 + (int5 == 0 ? float4 : 0.0F)));
						double2 = Math.toRadians((double)(float10 + (float)int3 * float9 + (float)(int5 + 1) * float9 / (float)int4 - (int5 == int4 - 1 ? float4 : 0.0F)));
						double3 = Math.toRadians((double)(float10 + (float)int3 * float9 + (float)int5 * float9 / (float)int4 + (int5 == 0 ? float4 / 2.0F : 0.0F)));
						double4 = Math.toRadians((double)(float10 + (float)int3 * float9 + (float)(int5 + 1) * float9 / (float)int4) - (int5 == int4 - 1 ? (double)float4 / 1.5 : 0.0));
						double5 = this.x + (double)(this.width / 2.0F) + (double)(float2 * (float)Math.cos(double1));
						double6 = this.y + (double)(this.height / 2.0F) + (double)(float2 * (float)Math.sin(double1));
						double7 = this.x + (double)(this.width / 2.0F) + (double)(float2 * (float)Math.cos(double2));
						double double11 = this.y + (double)(this.height / 2.0F) + (double)(float2 * (float)Math.sin(double2));
						double double12 = this.x + (double)(this.width / 2.0F) + (double)(float3 * (float)Math.cos(double3));
						double double13 = this.y + (double)(this.height / 2.0F) + (double)(float3 * (float)Math.sin(double3));
						double double14 = this.x + (double)(this.width / 2.0F) + (double)(float3 * (float)Math.cos(double4));
						double double15 = this.y + (double)(this.height / 2.0F) + (double)(float3 * (float)Math.sin(double4));
						float float11 = 1.0F;
						float float12 = 1.0F;
						float float13 = 1.0F;
						float float14 = 0.025F;
						if (int3 == int2) {
							float14 = 0.25F + 0.25F * this.select.fraction();
						} else if (int3 == this.deselectIndex) {
							float14 = 0.025F + 0.475F * this.deselect.fraction();
						}

						SpriteRenderer.instance.renderPoly((float)double5, (float)double6, (float)double12, (float)double13, (float)double14, (float)double15, (float)double7, (float)double11, float11, float12, float13, float14);
					}

					Texture texture = ((RadialMenu.Slice)this.slices.get(int3)).texture;
					if (texture != null) {
						double1 = Math.toRadians((double)(float10 + (float)int3 * float9 + float9 / 2.0F));
						float float15 = 0.0F + this.width / 2.0F + (float2 + (float3 - float2) / 2.0F) * (float)Math.cos(double1);
						float float16 = 0.0F + this.height / 2.0F + (float2 + (float3 - float2) / 2.0F) * (float)Math.sin(double1);
						this.DrawTexture(texture, (double)(float15 - (float)(texture.getWidth() / 2) - texture.offsetX), (double)(float16 - (float)(texture.getHeight() / 2) - texture.offsetY), (double)float1);
					}
				}

				if (slice != null && !StringUtils.isNullOrWhitespace(slice.text)) {
					this.formatTextInsideCircle(slice.text);
				}
			}
		}
	}

	private void formatTextInsideCircle(String string) {
		UIFont uIFont = UIFont.Medium;
		AngelCodeFont angelCodeFont = TextManager.instance.getFontFromEnum(uIFont);
		int int1 = angelCodeFont.getLineHeight();
		int int2 = 1;
		int int3;
		for (int3 = 0; int3 < string.length(); ++int3) {
			if (string.charAt(int3) == '\n') {
				++int2;
			}
		}

		if (int2 > 1) {
			int3 = int2 * int1;
			int int4 = this.getAbsoluteX().intValue() + (int)this.width / 2;
			int int5 = this.getAbsoluteY().intValue() + (int)this.height / 2 - int3 / 2;
			int int6 = 0;
			int int7;
			for (int7 = 0; int7 < string.length(); ++int7) {
				if (string.charAt(int7) == '\n') {
					int int8 = angelCodeFont.getWidth(string, int6, int7);
					angelCodeFont.drawString((float)(int4 - int8 / 2), (float)int5, string, 1.0F, 1.0F, 1.0F, 1.0F, int6, int7 - 1);
					int6 = int7 + 1;
					int5 += int1;
				}
			}

			if (int6 < string.length()) {
				int7 = angelCodeFont.getWidth(string, int6, string.length() - 1);
				angelCodeFont.drawString((float)(int4 - int7 / 2), (float)int5, string, 1.0F, 1.0F, 1.0F, 1.0F, int6, string.length() - 1);
			}
		} else {
			this.DrawTextCentre(uIFont, string, (double)(this.width / 2.0F), (double)(this.height / 2.0F - (float)(int1 / 2)), 1.0, 1.0, 1.0, 1.0);
		}
	}

	public void clear() {
		this.slices.clear();
		this.transition.reset();
		this.transition.init(66.666664F, false);
		this.selectIndex = -1;
		this.deselectIndex = -1;
	}

	public void addSlice(String string, Texture texture) {
		RadialMenu.Slice slice = new RadialMenu.Slice();
		slice.text = string;
		slice.texture = texture;
		this.slices.add(slice);
	}

	private RadialMenu.Slice getSlice(int int1) {
		return int1 >= 0 && int1 < this.slices.size() ? (RadialMenu.Slice)this.slices.get(int1) : null;
	}

	public void setSliceText(int int1, String string) {
		RadialMenu.Slice slice = this.getSlice(int1);
		if (slice != null) {
			slice.text = string;
		}
	}

	public void setSliceTexture(int int1, Texture texture) {
		RadialMenu.Slice slice = this.getSlice(int1);
		if (slice != null) {
			slice.texture = texture;
		}
	}

	private float getStartAngle() {
		float float1 = 360.0F / (float)Math.max(this.slices.size(), 2);
		return 90.0F - float1 / 2.0F;
	}

	public int getSliceIndexFromMouse(int int1, int int2) {
		float float1 = 0.0F + this.width / 2.0F;
		float float2 = 0.0F + this.height / 2.0F;
		double double1 = Math.sqrt(Math.pow((double)((float)int1 - float1), 2.0) + Math.pow((double)((float)int2 - float2), 2.0));
		if (!(double1 > (double)this.outerRadius) && !(double1 < (double)this.innerRadius)) {
			double double2 = Math.atan2((double)((float)int2 - float2), (double)((float)int1 - float1)) + 3.141592653589793;
			double double3 = Math.toDegrees(double2);
			float float3 = 360.0F / (float)Math.max(this.slices.size(), 2);
			return double3 < (double)this.getStartAngle() ? (int)((double3 + 360.0 - (double)this.getStartAngle()) / (double)float3) : (int)((double3 - (double)this.getStartAngle()) / (double)float3);
		} else {
			return -1;
		}
	}

	public int getSliceIndexFromJoypad(int int1) {
		float float1 = JoypadManager.instance.getAimingAxisX(int1);
		float float2 = JoypadManager.instance.getAimingAxisY(int1);
		if (!(Math.abs(float1) > 0.3F) && !(Math.abs(float2) > 0.3F)) {
			return -1;
		} else {
			double double1 = Math.atan2((double)(-float2), (double)(-float1));
			double double2 = Math.toDegrees(double1);
			float float3 = 360.0F / (float)Math.max(this.slices.size(), 2);
			return double2 < (double)this.getStartAngle() ? (int)((double2 + 360.0 - (double)this.getStartAngle()) / (double)float3) : (int)((double2 - (double)this.getStartAngle()) / (double)float3);
		}
	}

	public void setJoypad(int int1) {
		this.joypad = int1;
	}

	protected static class Slice {
		public String text;
		public Texture texture;

		boolean isEmpty() {
			return this.text == null && this.texture == null;
		}
	}
}
