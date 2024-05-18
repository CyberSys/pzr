package zombie.ui;

import gnu.trove.list.array.TIntArrayList;
import java.util.Stack;
import org.lwjgl.input.Keyboard;
import zombie.FrameLoader;
import zombie.GameTime;
import zombie.Lua.LuaManager;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.input.Mouse;


public class UITextBox2 extends UIElement {
	public static boolean ConsoleHasFocus = false;
	public boolean ResizeParent;
	UIFont font;
	public Stack Lines = new Stack();
	int[] HighlightLines = new int[1000];
	private ColorInfo textColor = new ColorInfo();
	public UINineGrid Frame = null;
	boolean HasFrame = false;
	public String Text = "";
	public boolean Centred = false;
	int NumVisibleLines = 0;
	int TopLineIndex = 0;
	private int EdgeSize = 5;
	public Color StandardFrameColour = new Color(50, 50, 50, 212);
	public Color TextEntryFrameColour = new Color(50, 50, 127, 212);
	public Color TextEntryCursorColour = new Color(170, 170, 220, 240);
	public Color TextEntryCursorColour2 = new Color(100, 100, 220, 160);
	int BlinkFramesOn = 12;
	int BlinkFramesOff = 8;
	float BlinkFrame;
	boolean BlinkState;
	public Color NuetralColour;
	public Color NuetralColour2;
	public Color BadColour;
	public Color GoodColour;
	public boolean DoingTextEntry;
	public int TextEntryCursorPos;
	public int TextEntryMaxLength;
	public boolean IsEditable;
	private boolean SelectingRange;
	private int maxTextLength;
	private boolean forceUpperCase;
	public int CursorLine;
	private int XOffset;
	public boolean multipleLine;
	private int maxLines;
	public TIntArrayList TextOffsetOfLineStart;
	private boolean onlyNumbers;
	private Texture clearButtonTexture;
	private boolean bClearButton;
	private UITransition clearButtonTransition;
	public int ToSelectionIndex;
	public String internalText;
	public String maskChr;
	public boolean bMask;
	public boolean ignoreFirst;

	public UITextBox2(UIFont uIFont, int int1, int int2, int int3, int int4, String string, boolean boolean1) {
		this.BlinkFrame = (float)this.BlinkFramesOn;
		this.BlinkState = true;
		this.NuetralColour = new Color(0, 0, 255, 33);
		this.NuetralColour2 = new Color(127, 0, 255, 33);
		this.BadColour = new Color(255, 0, 0, 33);
		this.GoodColour = new Color(0, 255, 33);
		this.DoingTextEntry = false;
		this.TextEntryCursorPos = 0;
		this.TextEntryMaxLength = 2000;
		this.IsEditable = false;
		this.SelectingRange = false;
		this.maxTextLength = -1;
		this.forceUpperCase = false;
		this.CursorLine = 0;
		this.XOffset = 0;
		this.multipleLine = false;
		this.maxLines = 1;
		this.TextOffsetOfLineStart = new TIntArrayList();
		this.onlyNumbers = false;
		this.bClearButton = false;
		this.ToSelectionIndex = 0;
		this.internalText = "";
		this.maskChr = "*";
		this.bMask = false;
		if (!FrameLoader.bDedicated) {
			this.font = uIFont;
			this.x = (double)int1;
			this.y = (double)int2;
			this.SetText(string);
			this.width = (float)int3;
			this.height = (float)int4;
			this.NumVisibleLines = 10;
			this.TopLineIndex = 0;
			Core.CurrentTextEntryBox = this;
			for (int int5 = 0; int5 < 1000; ++int5) {
				this.HighlightLines[int5] = 0;
			}

			this.HasFrame = boolean1;
			if (boolean1) {
				this.Frame = new UINineGrid(0, 0, int3, int4, this.EdgeSize, this.EdgeSize, this.EdgeSize, this.EdgeSize, "media/ui/Box_TopLeft.png", "media/ui/Box_Top.png", "media/ui/Box_TopRight.png", "media/ui/Box_Left.png", "media/ui/Box_Center.png", "media/ui/Box_Right.png", "media/ui/Box_BottomLeft.png", "media/ui/Box_Bottom.png", "media/ui/Box_BottomRight.png");
				this.AddChild(this.Frame);
			}

			this.Paginate();
			this.DoingTextEntry = false;
			this.TextEntryMaxLength = 2000;
			this.TextEntryCursorPos = 0;
			this.ToSelectionIndex = this.TextEntryCursorPos;
			this.IsEditable = false;
			Keyboard.enableRepeatEvents(true);
			this.clearButtonTexture = Texture.getSharedTexture("media/ui/Panel_Icon_Close.png");
		}
	}

	public void ClearHighlights() {
		for (int int1 = 0; int1 < 1000; ++int1) {
			this.HighlightLines[int1] = 0;
		}
	}

	public void setMasked(boolean boolean1) {
		this.bMask = boolean1;
	}

	public void onresize() {
		this.Paginate();
	}

	public void render() {
		if (this.isVisible()) {
			if (this.Parent == null || this.Parent.maxDrawHeight == -1 || !((double)this.Parent.maxDrawHeight <= this.y)) {
				int int1;
				if (this.bMask) {
					if (this.internalText.length() != this.Text.length()) {
						String string = "";
						for (int1 = 0; int1 < this.internalText.length(); ++int1) {
							string = string + this.maskChr;
						}

						this.Text = string;
					}
				} else {
					this.Text = this.internalText;
				}

				super.render();
				this.Paginate();
				int int2 = TextManager.instance.getFontFromEnum(this.font).getLineHeight();
				int1 = this.getInset();
				this.keepCursorVisible();
				int int3 = (int)this.width - int1;
				if (this.bClearButton && this.clearButtonTexture != null && !this.Lines.isEmpty()) {
					int3 -= 2 + this.clearButtonTexture.getWidth() + 2;
					float float1 = 0.5F;
					if (!this.SelectingRange && this.isMouseOver() && (double)Mouse.getXA() >= this.getAbsoluteX() + (double)int3) {
						float1 = 1.0F;
					}

					this.clearButtonTransition.setFadeIn(float1 == 1.0F);
					this.clearButtonTransition.update();
					this.DrawTexture(this.clearButtonTexture, (double)(this.width - (float)int1 - 2.0F - (float)this.clearButtonTexture.getWidth()), (double)(int1 + (int2 - this.clearButtonTexture.getHeight()) / 2), (double)(float1 * this.clearButtonTransition.fraction() + 0.35F * (1.0F - this.clearButtonTransition.fraction())));
				}

				Double Double1 = this.clampToParentX((double)(this.getAbsoluteX().intValue() + int1));
				Double Double2 = this.clampToParentX((double)(this.getAbsoluteX().intValue() + int3));
				Double Double3 = this.clampToParentY((double)(this.getAbsoluteY().intValue() + int1));
				Double Double4 = this.clampToParentY((double)(this.getAbsoluteY().intValue() + (int)this.height - int1));
				this.setStencilRect((double)(Double1.intValue() - this.getAbsoluteX().intValue()), (double)(Double3.intValue() - this.getAbsoluteY().intValue()), (double)(Double2.intValue() - Double1.intValue()), (double)(Double4.intValue() - Double3.intValue()));
				int int4;
				int int5;
				if (this.Lines.size() > 0) {
					int4 = int1;
					for (int5 = this.TopLineIndex; int5 < this.TopLineIndex + this.NumVisibleLines && int5 < this.Lines.size(); ++int5) {
						if (this.Lines.get(int5) != null) {
							if (int5 >= 0 && int5 < this.HighlightLines.length) {
								if (this.HighlightLines[int5] == 1) {
									this.DrawTextureScaledCol((Texture)null, (double)(int1 - 1), (double)int4, (double)(this.getWidth().intValue() - int1 * 2 + 2), (double)int2, this.NuetralColour);
								} else if (this.HighlightLines[int5] == 2) {
									this.DrawTextureScaledCol((Texture)null, (double)(int1 - 1), (double)int4, (double)(this.getWidth().intValue() - int1 * 2 + 2), (double)int2, this.NuetralColour2);
								} else if (this.HighlightLines[int5] == 3) {
									this.DrawTextureScaledCol((Texture)null, (double)(int1 - 1), (double)int4, (double)(this.getWidth().intValue() - int1 * 2 + 2), (double)int2, this.BadColour);
								} else if (this.HighlightLines[int5] == 4) {
									this.DrawTextureScaledCol((Texture)null, (double)(int1 - 1), (double)int4, (double)(this.getWidth().intValue() - int1 * 2 + 2), (double)int2, this.GoodColour);
								}
							}

							String string2 = (String)this.Lines.get(int5);
							if (this.Centred) {
								TextManager.instance.DrawStringCentre(this.font, (double)this.getAbsoluteX().intValue() + this.getWidth() / 2.0 + (double)int1, (double)(this.getAbsoluteY().intValue() + int4), string2, (double)this.textColor.r, (double)this.textColor.g, (double)this.textColor.b, 1.0);
							} else {
								TextManager.instance.DrawString(this.font, (double)(-this.XOffset + this.getAbsoluteX().intValue() + int1), (double)(this.getAbsoluteY().intValue() + int4), string2, (double)this.textColor.r, (double)this.textColor.g, (double)this.textColor.b, 1.0);
							}

							int4 += int2;
						}
					}
				}

				ConsoleHasFocus = this.DoingTextEntry;
				if (this.TextEntryCursorPos > this.Text.length()) {
					this.TextEntryCursorPos = this.Text.length();
				}

				if (this.ToSelectionIndex > this.Text.length()) {
					this.ToSelectionIndex = this.Text.length();
				}

				this.CursorLine = this.toDisplayLine(this.TextEntryCursorPos);
				if (this.DoingTextEntry) {
					if (this.BlinkState) {
						int4 = 0;
						if (this.Lines.size() > 0) {
							int5 = this.TextEntryCursorPos - this.TextOffsetOfLineStart.get(this.CursorLine);
							int5 = Math.min(int5, ((String)this.Lines.get(this.CursorLine)).length());
							int4 = TextManager.instance.getFontFromEnum(this.font).getWidth(((String)this.Lines.get(this.CursorLine)).substring(0, int5), true);
							if (int4 > 0) {
								--int4;
							}
						}

						this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), (double)(-this.XOffset + int1 + int4), (double)(int1 + this.CursorLine * int2), 1.0, (double)int2, this.TextEntryCursorColour);
					}

					if (this.Lines.size() > 0 && this.ToSelectionIndex != this.TextEntryCursorPos) {
						int4 = Math.min(this.TextEntryCursorPos, this.ToSelectionIndex);
						int5 = Math.max(this.TextEntryCursorPos, this.ToSelectionIndex);
						int int6 = this.toDisplayLine(int4);
						int int7 = this.toDisplayLine(int5);
						for (int int8 = int6; int8 <= int7; ++int8) {
							int int9 = this.TextOffsetOfLineStart.get(int8);
							int int10 = int9 + ((String)this.Lines.get(int8)).length();
							int9 = Math.max(int9, int4);
							int10 = Math.min(int10, int5);
							String string3 = (String)this.Lines.get(int8);
							int int11 = TextManager.instance.getFontFromEnum(this.font).getWidth(string3.substring(0, int9 - this.TextOffsetOfLineStart.get(int8)), true);
							int int12 = TextManager.instance.getFontFromEnum(this.font).getWidth(string3.substring(0, int10 - this.TextOffsetOfLineStart.get(int8)), true);
							this.DrawTextureScaledCol((Texture)null, (double)(-this.XOffset + int1 + int11), (double)(int1 + int8 * int2), (double)(int12 - int11), (double)int2, this.TextEntryCursorColour2);
						}
					}
				}

				this.clearStencilRect();
				if (StencilLevel > 0) {
					this.repaintStencilRect((double)(Double1.intValue() - this.getAbsoluteX().intValue()), (double)(Double3.intValue() - this.getAbsoluteY().intValue()), (double)(Double2.intValue() - Double1.intValue()), (double)(Double4.intValue() - Double3.intValue()));
				}
			}
		}
	}

	public void setFrameAlpha(float float1) {
		this.Frame.setAlpha(float1);
	}

	public float getFrameAlpha() {
		return this.Frame.getAlpha();
	}

	public void setTextColor(ColorInfo colorInfo) {
		this.textColor = colorInfo;
	}

	private void keepCursorVisible() {
		if (!this.Lines.isEmpty() && this.DoingTextEntry && !this.multipleLine) {
			if (this.TextEntryCursorPos > this.Text.length()) {
				this.TextEntryCursorPos = this.Text.length();
			}

			String string = (String)this.Lines.get(0);
			int int1 = TextManager.instance.MeasureStringX(this.font, string);
			int int2 = this.getInset();
			int int3 = this.getWidth().intValue() - int2 * 2;
			if (this.bClearButton && this.clearButtonTexture != null) {
				int3 -= 2 + this.clearButtonTexture.getWidth() + 2;
			}

			if (int1 <= int3) {
				this.XOffset = 0;
			} else if (-this.XOffset + int1 < int3) {
				this.XOffset = int1 - int3;
			}

			int int4 = TextManager.instance.MeasureStringX(this.font, string.substring(0, this.TextEntryCursorPos));
			int int5 = -this.XOffset + int2 + int4 - 1;
			if (int5 < int2) {
				this.XOffset = int4;
			} else if (int5 >= int2 + int3) {
				this.XOffset = 0;
				int int6 = this.getCursorPosFromX(int4 - int3);
				this.XOffset = TextManager.instance.MeasureStringX(this.font, string.substring(0, int6));
				int5 = -this.XOffset + int2 + int4 - 1;
				if (int5 >= int2 + int3) {
					this.XOffset = TextManager.instance.MeasureStringX(this.font, string.substring(0, int6 + 1));
				}

				if (-this.XOffset + int1 < int3) {
					this.XOffset = int1 - int3;
				}
			}
		} else {
			this.XOffset = 0;
		}
	}

	public String getText() {
		return this.Text;
	}

	public String getInternalText() {
		return this.internalText;
	}

	public void update() {
		if (this.maxTextLength > -1 && this.internalText.length() > this.maxTextLength) {
			this.internalText = this.internalText.substring(this.maxTextLength);
		}

		if (this.forceUpperCase) {
			this.internalText = this.internalText.toUpperCase();
		}

		int int1;
		if (this.bMask) {
			if (this.internalText.length() != this.Text.length()) {
				String string = "";
				for (int1 = 0; int1 < this.internalText.length(); ++int1) {
					string = string + this.maskChr;
				}

				if (this.DoingTextEntry && this.Text != string) {
					this.resetBlink();
				}

				this.Text = string;
			}
		} else {
			if (this.DoingTextEntry && this.Text != this.internalText) {
				this.resetBlink();
			}

			this.Text = this.internalText;
		}

		this.Paginate();
		int int2 = this.getInset();
		int1 = TextManager.instance.getFontFromEnum(this.font).getLineHeight();
		if ((double)(int1 + int2 * 2) > this.getHeight()) {
			this.setHeight((double)(int1 + int2 * 2));
		}

		if (this.Frame != null) {
			this.Frame.setHeight(this.getHeight());
		}

		this.NumVisibleLines = (int)(this.getHeight() - (double)(int2 * 2)) / int1;
		if (this.BlinkFrame > 0.0F) {
			this.BlinkFrame -= GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * 30.0F;
		} else {
			this.BlinkState = !this.BlinkState;
			if (this.BlinkState) {
				this.BlinkFrame = (float)this.BlinkFramesOn;
			} else {
				this.BlinkFrame = (float)this.BlinkFramesOff;
			}
		}

		if (this.NumVisibleLines * int1 + int2 * 2 < this.getHeight().intValue()) {
			if (this.NumVisibleLines < this.Lines.size()) {
				this.setScrollHeight((double)((this.Lines.size() + 1) * int1));
			}

			++this.NumVisibleLines;
		} else {
			this.setScrollHeight((double)(this.Lines.size() * int1));
		}

		if (UIDebugConsole.instance == null || this != UIDebugConsole.instance.OutputLog) {
			this.TopLineIndex = (int)(-this.getYScroll() + (double)int2) / int1;
		}

		this.setYScroll((double)(-this.TopLineIndex * int1));
	}

	private void Paginate() {
		this.Lines.clear();
		this.TextOffsetOfLineStart.clear();
		if (!this.Text.isEmpty()) {
			if (!this.multipleLine) {
				this.Lines.add(this.Text);
				this.TextOffsetOfLineStart.add(0);
			} else {
				String[] stringArray = this.Text.split("\n", -1);
				int int1 = 0;
				String[] stringArray2 = stringArray;
				int int2 = stringArray.length;
				for (int int3 = 0; int3 < int2; ++int3) {
					String string = stringArray2[int3];
					int int4 = 0;
					if (string.length() == 0) {
						this.Lines.add(this.multipleLine ? "" : " ");
						this.TextOffsetOfLineStart.add(int1);
						++int1;
					} else {
						do {
							int int5 = string.indexOf(" ", int4 + 1);
							int int6 = int5;
							if (int5 == -1) {
								int6 = string.length();
							}

							int int7 = TextManager.instance.MeasureStringX(this.font, string.substring(0, int6));
							byte byte1 = 17;
							if ((double)int7 >= this.getWidth() - (double)(this.getInset() * 2) - (double)byte1 && int4 > 0) {
								String string2 = string.substring(0, int4);
								string = string.substring(int4 + 1);
								this.Lines.add(string2);
								this.TextOffsetOfLineStart.add(int1);
								int1 += string2.length() + 1;
								int5 = 0;
							} else if (int5 == -1) {
								this.Lines.add(string);
								this.TextOffsetOfLineStart.add(int1);
								int1 += string.length() + 1;
								break;
							}

							int4 = int5;
						}				 while (string.length() > 0);
					}
				}
			}
		}
	}

	public int getInset() {
		int int1 = 2;
		if (this.HasFrame) {
			int1 = this.EdgeSize;
		}

		return int1;
	}

	public void setEditable(boolean boolean1) {
		this.IsEditable = boolean1;
	}

	public Boolean onMouseUp(double double1, double double2) {
		if (!this.isVisible()) {
			return false;
		} else {
			super.onMouseUp(double1, double2);
			this.SelectingRange = false;
			return Boolean.TRUE;
		}
	}

	public void onMouseUpOutside(double double1, double double2) {
		if (this.isVisible()) {
			super.onMouseUpOutside(double1, double2);
			this.SelectingRange = false;
		}
	}

	public Boolean onMouseMove(double double1, double double2) {
		int int1 = Mouse.getXA();
		int int2 = Mouse.getYA();
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else {
			boolean boolean1 = this.isConsumeMouseEvents();
			this.setConsumeMouseEvents(false);
			Boolean Boolean1 = super.onMouseMove(double1, double2);
			this.setConsumeMouseEvents(boolean1);
			if (Boolean1) {
				return Boolean.TRUE;
			} else if (this.IsEditable && this.SelectingRange) {
				if (this.multipleLine) {
					int int3 = this.getInset();
					int int4 = TextManager.instance.getFontFromEnum(this.font).getLineHeight();
					this.CursorLine = (int2 - this.getAbsoluteY().intValue() - int3 - this.getYScroll().intValue()) / int4;
					if (this.CursorLine > this.Lines.size() - 1) {
						this.CursorLine = this.Lines.size() - 1;
					}
				}

				this.TextEntryCursorPos = this.getCursorPosFromX((int)((double)int1 - this.getAbsoluteX()));
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		}
	}

	public void onMouseMoveOutside(double double1, double double2) {
		int int1 = Mouse.getXA();
		int int2 = Mouse.getYA();
		if (!Mouse.isButtonDown(0)) {
			this.SelectingRange = false;
		}

		if (this.isVisible()) {
			super.onMouseMoveOutside(double1, double2);
			if (this.IsEditable && this.SelectingRange) {
				if (this.multipleLine) {
					int int3 = this.getInset();
					int int4 = TextManager.instance.getFontFromEnum(this.font).getLineHeight();
					this.CursorLine = (int2 - this.getAbsoluteY().intValue() - int3 - this.getYScroll().intValue()) / int4;
					if (this.CursorLine < 0) {
						this.CursorLine = 0;
					}

					if (this.CursorLine > this.Lines.size() - 1) {
						this.CursorLine = this.Lines.size() - 1;
					}
				}

				this.TextEntryCursorPos = this.getCursorPosFromX((int)((double)int1 - this.getAbsoluteX()));
			}
		}
	}

	public void focus() {
		this.DoingTextEntry = true;
		Core.CurrentTextEntryBox = this;
	}

	public void unfocus() {
		this.DoingTextEntry = false;
		if (Core.CurrentTextEntryBox == this) {
			Core.CurrentTextEntryBox = null;
		}
	}

	public void ignoreFirstInput() {
		this.ignoreFirst = true;
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else {
			int int1;
			if (!this.getControls().isEmpty()) {
				for (int1 = 0; int1 < this.getControls().size(); ++int1) {
					UIElement uIElement = (UIElement)this.getControls().get(int1);
					if (uIElement != this.Frame && uIElement.isMouseOver()) {
						return uIElement.onMouseDown(double1 - (double)uIElement.getXScrolled(this).intValue(), double2 - (double)uIElement.getYScrolled(this).intValue()) ? Boolean.TRUE : Boolean.FALSE;
					}
				}
			}

			if (this.bClearButton && this.clearButtonTexture != null && !this.Lines.isEmpty()) {
				int1 = this.getWidth().intValue() - this.getInset();
				int1 -= 2 + this.clearButtonTexture.getWidth() + 2;
				if (double1 >= (double)int1) {
					this.clearInput();
					return Boolean.TRUE;
				}
			}

			if (this.multipleLine) {
				int1 = this.getInset();
				int int2 = TextManager.instance.getFontFromEnum(this.font).getLineHeight();
				this.CursorLine = ((int)double2 - int1 - this.getYScroll().intValue()) / int2;
				if (this.CursorLine > this.Lines.size() - 1) {
					this.CursorLine = this.Lines.size() - 1;
				}
			}

			if (this.IsEditable) {
				if (Core.CurrentTextEntryBox != this) {
					if (Core.CurrentTextEntryBox != null) {
						Core.CurrentTextEntryBox.DoingTextEntry = false;
						if (Core.CurrentTextEntryBox.Frame != null) {
							Core.CurrentTextEntryBox.Frame.Colour = this.StandardFrameColour;
						}
					}

					Core.CurrentTextEntryBox = this;
					Core.CurrentTextEntryBox.SelectingRange = true;
				}

				if (!this.DoingTextEntry) {
					this.DoingTextEntry = true;
					this.TextEntryCursorPos = this.getCursorPosFromX((int)double1);
					this.ToSelectionIndex = this.TextEntryCursorPos;
					if (this.Frame != null) {
						this.Frame.Colour = this.TextEntryFrameColour;
					}
				} else {
					this.TextEntryCursorPos = this.getCursorPosFromX((int)double1);
					this.ToSelectionIndex = this.TextEntryCursorPos;
				}

				return Boolean.TRUE;
			} else {
				if (this.Frame != null) {
					this.Frame.Colour = this.StandardFrameColour;
				}

				this.DoingTextEntry = false;
				return Boolean.FALSE;
			}
		}
	}

	private int getCursorPosFromX(int int1) {
		if (this.Lines.isEmpty()) {
			return 0;
		} else {
			String string = (String)this.Lines.get(this.CursorLine);
			if (string.length() == 0) {
				return this.TextOffsetOfLineStart.get(this.CursorLine);
			} else if (int1 + this.XOffset < 0) {
				return this.TextOffsetOfLineStart.get(this.CursorLine);
			} else {
				for (int int2 = 0; int2 <= string.length(); ++int2) {
					String string2 = "";
					if (int2 > 0) {
						string2 = string.substring(0, int2);
					}

					int int3 = TextManager.instance.MeasureStringX(this.font, string2);
					if (int3 > int1 + this.XOffset && int2 >= 0) {
						return this.TextOffsetOfLineStart.get(this.CursorLine) + int2 - 1;
					}
				}

				return this.TextOffsetOfLineStart.get(this.CursorLine) + string.length();
			}
		}
	}

	public void updateText() {
		if (this.bMask) {
			String string = "";
			for (int int1 = 0; int1 < this.internalText.length(); ++int1) {
				string = string + this.maskChr;
			}

			this.Text = string;
		} else {
			this.Text = this.internalText;
		}
	}

	public void SetText(String string) {
		this.internalText = string;
		int int1;
		if (this.bMask) {
			string = "";
			for (int1 = 0; int1 < this.internalText.length(); ++int1) {
				string = string + this.maskChr;
			}

			this.Text = string;
		} else {
			this.Text = string;
		}

		this.TextEntryCursorPos = string.length();
		this.ToSelectionIndex = this.TextEntryCursorPos;
		this.update();
		this.TextEntryCursorPos = this.ToSelectionIndex = 0;
		if (!this.Lines.isEmpty()) {
			int1 = this.Lines.size() - 1;
			this.TextEntryCursorPos = this.ToSelectionIndex = this.TextOffsetOfLineStart.get(int1) + ((String)this.Lines.get(int1)).length();
		}
	}

	public void clearInput() {
		this.Text = "";
		this.internalText = "";
		this.TextEntryCursorPos = 0;
		this.ToSelectionIndex = 0;
		this.update();
		this.onTextChange();
	}

	public void onPressUp() {
		if (this.getTable() != null && this.getTable().rawget("onPressUp") != null) {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onPressUp"));
		}
	}

	public void onPressDown() {
		if (this.getTable() != null && this.getTable().rawget("onPressDown") != null) {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onPressDown"));
		}
	}

	public void onCommandEntered() {
		if (this.getTable() != null && this.getTable().rawget("onCommandEntered") != null) {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onCommandEntered"), (Object)this.table);
		}
	}

	public void onTextChange() {
		if (this.getTable() != null && this.getTable().rawget("onTextChange") != null) {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onTextChange"), (Object)this.getTable());
		}
	}

	public void onOtherKey(int int1) {
		if (this.getTable() != null && this.getTable().rawget("onOtherKey") != null) {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onOtherKey"), (Object)int1);
		}
	}

	public int getMaxTextLength() {
		return this.maxTextLength;
	}

	public void setMaxTextLength(int int1) {
		this.maxTextLength = int1;
	}

	public boolean getForceUpperCase() {
		return this.forceUpperCase;
	}

	public void setForceUpperCase(boolean boolean1) {
		this.forceUpperCase = boolean1;
	}

	public void setHasFrame(boolean boolean1) {
		if (this.HasFrame != boolean1) {
			this.HasFrame = boolean1;
			if (this.HasFrame) {
				this.Frame = new UINineGrid(0, 0, (int)this.width, (int)this.height, this.EdgeSize, this.EdgeSize, this.EdgeSize, this.EdgeSize, "media/ui/Box_TopLeft.png", "media/ui/Box_Top.png", "media/ui/Box_TopRight.png", "media/ui/Box_Left.png", "media/ui/Box_Center.png", "media/ui/Box_Right.png", "media/ui/Box_BottomLeft.png", "media/ui/Box_Bottom.png", "media/ui/Box_BottomRight.png");
				this.Frame.setAnchorRight(true);
				this.AddChild(this.Frame);
			} else {
				this.RemoveChild(this.Frame);
				this.Frame = null;
			}
		}
	}

	public void setClearButton(boolean boolean1) {
		this.bClearButton = boolean1;
		if (this.bClearButton && this.clearButtonTransition == null) {
			this.clearButtonTransition = new UITransition();
		}
	}

	public int toDisplayLine(int int1) {
		for (int int2 = 0; int2 < this.Lines.size(); ++int2) {
			if (int1 >= this.TextOffsetOfLineStart.get(int2) && int1 <= this.TextOffsetOfLineStart.get(int2) + ((String)this.Lines.get(int2)).length()) {
				return int2;
			}
		}

		return 0;
	}

	public void setMultipleLine(boolean boolean1) {
		this.multipleLine = boolean1;
	}

	public void setCursorLine(int int1) {
		this.CursorLine = int1;
	}

	public int getMaxLines() {
		return this.maxLines;
	}

	public void setMaxLines(int int1) {
		this.maxLines = int1;
	}

	public boolean isFocused() {
		return this.DoingTextEntry;
	}

	public boolean isOnlyNumbers() {
		return this.onlyNumbers;
	}

	public void setOnlyNumbers(boolean boolean1) {
		this.onlyNumbers = boolean1;
	}

	public void resetBlink() {
		this.BlinkState = true;
		this.BlinkFrame = (float)this.BlinkFramesOn;
	}
}
