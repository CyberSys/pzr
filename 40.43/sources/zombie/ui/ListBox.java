package zombie.ui;

import java.util.Stack;
import zombie.core.Color;
import zombie.core.textures.Texture;
import zombie.interfaces.IListBoxItem;


public class ListBox extends UIElement {
	public Color background = new Color(255, 255, 255, 10);
	public Color selColour = new Color(0, 0, 255, 10);
	public Color selColourDis = new Color(255, 0, 255, 30);
	public int itemHeight = 20;
	public int topIndex = 0;
	int timeSinceClick = 0;
	boolean clicked = false;
	private boolean mouseOver = false;
	public int Selected = -1;
	public int LastSelected = -1;
	UIEventHandler messageParent;
	private String name;
	public Stack Items = new Stack();

	public ListBox(String string, UIEventHandler uIEventHandler) {
		this.messageParent = uIEventHandler;
		this.name = string;
	}

	public void SetItemHeight(int int1) {
		this.itemHeight = int1;
	}

	private void Selected(int int1) {
		this.messageParent.Selected(this.name, int1, this.LastSelected);
	}

	public void remove(IListBoxItem iListBoxItem) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			if (((ListBox.ListItem)this.Items.get(int1)).item == iListBoxItem) {
				this.Items.remove(int1);
				--int1;
			}
		}
	}

	public IListBoxItem getSelected() {
		return this.Selected != -1 ? ((ListBox.ListItem)this.Items.get(this.Selected)).item : null;
	}

	public void AddItem(IListBoxItem iListBoxItem, Color color, Color color2, Color color3) {
		this.Items.add(new ListBox.ListItem(iListBoxItem, color, color2, color3));
	}

	public void AddItem(IListBoxItem iListBoxItem, Texture texture, Color color, Color color2, Color color3) {
		this.Items.add(new ListBox.ListItem(iListBoxItem, texture, color, color2, color3));
	}

	public void AddItem(IListBoxItem iListBoxItem, Color color, Color color2, Color color3, boolean boolean1) {
		this.Items.add(new ListBox.ListItem(iListBoxItem, color, color2, color3, boolean1));
	}

	public void AddItem(IListBoxItem iListBoxItem, String string, Color color, Color color2, Color color3) {
		this.Items.add(new ListBox.ListItem(iListBoxItem, color, color2, color3));
	}

	public void AddItem(IListBoxItem iListBoxItem, String string, Color color, Color color2, Color color3, boolean boolean1) {
		this.Items.add(new ListBox.ListItem(iListBoxItem, color, color2, color3, boolean1));
	}

	public void render() {
		this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), 0.0, 0.0, (double)this.getWidth().intValue(), (double)this.getHeight().intValue(), this.background);
		for (int int1 = this.topIndex; int1 < this.Items.size() && (double)int1 < (double)this.topIndex + this.getHeight() / (double)this.itemHeight; ++int1) {
			Texture texture = ((ListBox.ListItem)this.Items.get(int1)).Icon;
			ListBox.ListItem listItem = (ListBox.ListItem)this.Items.get(int1);
			int int2 = (int1 - this.topIndex) * this.itemHeight;
			if (this.Selected == int1) {
				if (((ListBox.ListItem)this.Items.get(this.Selected)).bDisabled) {
					this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), 0.0, (double)(int2 + 1), (double)this.getWidth().intValue(), (double)(this.itemHeight - 2), this.selColourDis);
				} else {
					this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), 0.0, (double)(int2 + 1), (double)this.getWidth().intValue(), (double)(this.itemHeight - 2), this.selColour);
				}
			} else if (((ListBox.ListItem)this.Items.get(int1)).bDisabled) {
				this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), 0.0, (double)(int2 + 1), (double)this.getWidth().intValue(), (double)(this.itemHeight - 2), new Color(0.4F, 0.2F, 0.2F, 0.5F));
			} else {
				this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), 0.0, (double)(int2 + 1), (double)this.getWidth().intValue(), (double)(this.itemHeight - 2), listItem.backCol);
			}

			if (texture == null) {
				this.DrawText(listItem.item.getLeftLabel(), 10.0, (double)(int2 + this.itemHeight / 2 - 6), (double)listItem.leftCol.r, (double)listItem.leftCol.g, (double)listItem.leftCol.b, (double)listItem.leftCol.a);
				this.DrawTextRight(listItem.item.getRightLabel(), 0.0 + this.getWidth() - 10.0, (double)(int2 + this.itemHeight / 2 - 6), (double)listItem.rightCol.r, (double)listItem.rightCol.g, (double)listItem.rightCol.b, (double)listItem.rightCol.a);
			} else {
				if (((ListBox.ListItem)this.Items.get(int1)).bDisabled) {
					this.DrawTextureScaledCol(texture, 2.0, (double)(int2 + 2), (double)texture.getWidth(), (double)texture.getWidth(), Color.gray);
				} else {
					this.DrawTextureScaledCol(texture, 2.0, (double)(int2 + 2), (double)texture.getWidth(), (double)texture.getWidth(), Color.white);
				}

				this.DrawText(listItem.item.getLeftLabel(), (double)(10 + texture.getWidth() + 4), (double)(int2 + this.itemHeight / 2 - 6), (double)listItem.leftCol.r, (double)listItem.leftCol.g, (double)listItem.leftCol.b, (double)listItem.leftCol.a);
				this.DrawTextRight(listItem.item.getRightLabel(), 0.0 + this.getWidth() - 10.0 + (double)texture.getWidth() + 4.0, (double)(int2 + this.itemHeight / 2 - 6), (double)listItem.rightCol.r, (double)listItem.rightCol.g, (double)listItem.rightCol.b, (double)listItem.rightCol.a);
			}
		}
	}

	public Boolean onMouseMove(double double1, double double2) {
		this.mouseOver = true;
		return Boolean.TRUE;
	}

	public void onMouseMoveOutside(double double1, double double2) {
		this.mouseOver = false;
	}

	private void DoubleClick(double double1, double double2) {
		if (this.Selected != -1 && !((ListBox.ListItem)this.Items.get(this.Selected)).bDisabled) {
			this.messageParent.DoubleClick(this.name, (int)double1, (int)double2);
		}
	}

	public Boolean onMouseUp(double double1, double double2) {
		if (this.clicked) {
			this.timeSinceClick = 0;
			this.LastSelected = this.Selected;
			int int1 = (int)double2 / this.itemHeight;
			int1 += this.topIndex;
			if (int1 < this.Items.size() && int1 >= 0) {
				this.Selected = int1;
				this.Selected(int1);
			}
		}

		this.clicked = false;
		this.setCapture(false);
		return Boolean.FALSE;
	}

	public void update() {
		super.update();
		++this.timeSinceClick;
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (this.timeSinceClick < 10) {
			this.DoubleClick(double1, double2);
		}

		this.clicked = true;
		this.setCapture(true);
		return Boolean.FALSE;
	}

	public static class ListItem {
		public Color leftCol;
		public Color rightCol;
		public Color backCol;
		public IListBoxItem item;
		public boolean bDisabled = false;
		public Texture Icon = null;

		public ListItem(IListBoxItem iListBoxItem, Color color, Color color2, Color color3) {
			this.item = iListBoxItem;
			this.bDisabled = false;
			this.leftCol = color;
			this.rightCol = color2;
			this.backCol = color3;
		}

		public ListItem(IListBoxItem iListBoxItem, Texture texture, Color color, Color color2, Color color3) {
			this.item = iListBoxItem;
			this.bDisabled = false;
			this.leftCol = color;
			this.rightCol = color2;
			this.backCol = color3;
			this.Icon = texture;
		}

		public ListItem(IListBoxItem iListBoxItem, Color color, Color color2, Color color3, boolean boolean1) {
			this.item = iListBoxItem;
			this.bDisabled = boolean1;
			this.leftCol = color;
			this.rightCol = color2;
			this.backCol = color3;
		}
	}
}
