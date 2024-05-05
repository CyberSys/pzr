package zombie.ui;

import zombie.core.Color;
import zombie.core.textures.Texture;
import zombie.input.Mouse;


public final class ScrollBar extends UIElement {
	public final Color BackgroundColour = new Color(255, 255, 255, 255);
	public final Color ButtonColour = new Color(255, 255, 255, 127);
	public final Color ButtonHighlightColour = new Color(255, 255, 255, 255);
	public boolean IsVerticle = true;
	private int FullLength = 114;
	private int InsideLength = 100;
	private int EndLength = 7;
	private float ButtonInsideLength = 30.0F;
	private int ButtonEndLength = 6;
	private int Thickness = 10;
	private int ButtonThickness = 9;
	private float ButtonOffset = 40.0F;
	private int MouseDragStartPos = 0;
	private float ButtonDragStartPos = 0.0F;
	private Texture BackVertical;
	private Texture TopVertical;
	private Texture BottomVertical;
	private Texture ButtonBackVertical;
	private Texture ButtonTopVertical;
	private Texture ButtonBottomVertical;
	private Texture BackHorizontal;
	private Texture LeftHorizontal;
	private Texture RightHorizontal;
	private Texture ButtonBackHorizontal;
	private Texture ButtonLeftHorizontal;
	private Texture ButtonRightHorizontal;
	private boolean mouseOver = false;
	private boolean BeingDragged = false;
	private UITextBox2 ParentTextBox = null;
	UIEventHandler messageParent;
	private String name;

	public ScrollBar(String string, UIEventHandler uIEventHandler, int int1, int int2, int int3, boolean boolean1) {
		this.messageParent = uIEventHandler;
		this.name = string;
		this.x = (double)((float)int1);
		this.y = (double)((float)int2);
		this.FullLength = int3;
		this.InsideLength = int3 - this.EndLength * 2;
		this.IsVerticle = true;
		this.width = (float)this.Thickness;
		this.height = (float)int3;
		this.ButtonInsideLength = this.height - (float)(this.ButtonEndLength * 2);
		this.ButtonOffset = 0.0F;
		this.BackVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bkg_Middle.png");
		this.TopVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bkg_Top.png");
		this.BottomVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bkg_Bottom.png");
		this.ButtonBackVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Middle.png");
		this.ButtonTopVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Top.png");
		this.ButtonBottomVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bottom.png");
		this.BackHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bkg_Middle.png");
		this.LeftHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bkg_Bottom.png");
		this.RightHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bkg_Top.png");
		this.ButtonBackHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Middle.png");
		this.ButtonLeftHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bottom.png");
		this.ButtonRightHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Top.png");
	}

	public void SetParentTextBox(UITextBox2 uITextBox2) {
		this.ParentTextBox = uITextBox2;
	}

	public void setHeight(double double1) {
		super.setHeight(double1);
		this.FullLength = (int)double1;
		this.InsideLength = (int)double1 - this.EndLength * 2;
	}

	public void render() {
		if (this.IsVerticle) {
			this.DrawTextureScaledCol(this.TopVertical, 0.0, 0.0, (double)this.Thickness, (double)this.EndLength, this.BackgroundColour);
			this.DrawTextureScaledCol(this.BackVertical, 0.0, (double)(0 + this.EndLength), (double)this.Thickness, (double)this.InsideLength, this.BackgroundColour);
			this.DrawTextureScaledCol(this.BottomVertical, 0.0, (double)(0 + this.EndLength + this.InsideLength), (double)this.Thickness, (double)this.EndLength, this.BackgroundColour);
			Color color;
			if (this.mouseOver) {
				color = this.ButtonHighlightColour;
			} else {
				color = this.ButtonColour;
			}

			this.DrawTextureScaledCol(this.ButtonTopVertical, 1.0, (double)((int)this.ButtonOffset + 1), (double)this.ButtonThickness, (double)this.ButtonEndLength, color);
			this.DrawTextureScaledCol(this.ButtonBackVertical, 1.0, (double)((int)this.ButtonOffset + 1 + this.ButtonEndLength), (double)this.ButtonThickness, (double)this.ButtonInsideLength, color);
			this.DrawTextureScaledCol(this.ButtonBottomVertical, 1.0, (double)((float)((int)this.ButtonOffset + 1 + this.ButtonEndLength) + this.ButtonInsideLength), (double)this.ButtonThickness, (double)this.ButtonEndLength, color);
		}
	}

	public Boolean onMouseMove(double double1, double double2) {
		this.mouseOver = true;
		return Boolean.TRUE;
	}

	public void onMouseMoveOutside(double double1, double double2) {
		this.mouseOver = false;
	}

	public Boolean onMouseUp(double double1, double double2) {
		this.BeingDragged = false;
		return Boolean.FALSE;
	}

	public Boolean onMouseDown(double double1, double double2) {
		boolean boolean1 = false;
		if (double2 >= (double)this.ButtonOffset && double2 <= (double)(this.ButtonOffset + this.ButtonInsideLength + (float)(this.ButtonEndLength * 2))) {
			boolean1 = true;
		}

		if (boolean1) {
			this.BeingDragged = true;
			this.MouseDragStartPos = Mouse.getY();
			this.ButtonDragStartPos = this.ButtonOffset;
		} else {
			this.ButtonOffset = (float)(double2 - (double)((this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) / 2.0F));
		}

		if (this.ButtonOffset < 0.0F) {
			this.ButtonOffset = 0.0F;
		}

		if (this.ButtonOffset > (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 1.0F) {
			this.ButtonOffset = (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 1.0F;
		}

		return Boolean.FALSE;
	}

	public void update() {
		super.update();
		int int1;
		if (this.BeingDragged) {
			int1 = this.MouseDragStartPos - Mouse.getY();
			this.ButtonOffset = this.ButtonDragStartPos - (float)int1;
			if (this.ButtonOffset < 0.0F) {
				this.ButtonOffset = 0.0F;
			}

			if (this.ButtonOffset > (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 0.0F) {
				this.ButtonOffset = (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 0.0F;
			}

			if (!Mouse.isButtonDown(0)) {
				this.BeingDragged = false;
			}
		}

		if (this.ParentTextBox != null) {
			int1 = TextManager.instance.getFontFromEnum(this.ParentTextBox.font).getLineHeight();
			if (this.ParentTextBox.Lines.size() > this.ParentTextBox.NumVisibleLines) {
				if (this.ParentTextBox.Lines.size() > 0) {
					int int2 = this.ParentTextBox.NumVisibleLines;
					if (int2 * int1 > this.ParentTextBox.getHeight().intValue() - this.ParentTextBox.getInset() * 2) {
						--int2;
					}

					float float1 = (float)int2 / (float)this.ParentTextBox.Lines.size();
					this.ButtonInsideLength = (float)((int)((float)this.getHeight().intValue() * float1) - this.ButtonEndLength * 2);
					this.ButtonInsideLength = Math.max(this.ButtonInsideLength, 0.0F);
					float float2 = this.ButtonInsideLength + (float)(this.ButtonEndLength * 2);
					if (this.ButtonOffset < 0.0F) {
						this.ButtonOffset = 0.0F;
					}

					if (this.ButtonOffset > (float)this.getHeight().intValue() - float2 - 0.0F) {
						this.ButtonOffset = (float)this.getHeight().intValue() - float2 - 0.0F;
					}

					float float3 = this.ButtonOffset / (float)this.getHeight().intValue();
					this.ParentTextBox.TopLineIndex = (int)((float)this.ParentTextBox.Lines.size() * float3);
					int int3 = this.getHeight().intValue();
					int int4 = int3 - (int)float2;
					int int5 = int1 * (this.ParentTextBox.Lines.size() - int2);
					float float4 = (float)int4 / (float)int5;
					float float5 = this.ButtonOffset / float4;
					this.ParentTextBox.TopLineIndex = (int)(float5 / (float)int1);
				} else {
					this.ButtonOffset = 0.0F;
					this.ButtonInsideLength = (float)(this.getHeight().intValue() - this.ButtonEndLength * 2);
					this.ParentTextBox.TopLineIndex = 0;
				}
			} else {
				this.ButtonOffset = 0.0F;
				this.ButtonInsideLength = (float)(this.getHeight().intValue() - this.ButtonEndLength * 2);
				this.ParentTextBox.TopLineIndex = 0;
			}
		}
	}

	public void scrollToBottom() {
		this.ButtonOffset = (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 0.0F;
	}
}
