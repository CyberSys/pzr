package zombie.ui;

import java.util.Iterator;
import zombie.core.Color;
import zombie.core.textures.Texture;


public class HelpIcon extends UIElement {
	public static boolean doOthers = true;
	public boolean Closed = true;
	public UIElement follow;
	boolean clicked = false;
	boolean mouseOver = false;
	int origX;
	int origY;
	Texture tex = Texture.getSharedTexture("media/ui/Question_Off.png");
	Texture tex2 = Texture.getSharedTexture("media/ui/Question_On.png");
	TextBox text;
	String title;
	NewWindow window;

	public HelpIcon(int int1, int int2, String string, String string2) {
		this.x = (double)int1;
		this.y = (double)int2;
		this.origX = int1;
		this.origY = int2;
		this.title = string;
		this.followGameWorld = true;
		this.text = new TextBox(UIFont.Small, 0, 0, 180, string2);
		this.window = new NewWindow(0, 0, 200, 50, false);
		this.window.Movable = false;
		NewWindow newWindow = this.window;
		newWindow.x += 0.0;
		newWindow = this.window;
		newWindow.y -= 0.0;
		this.text.ResizeParent = true;
		this.window.Nest(this.text, 20, 10, 20, 10);
		this.window.Parent = this;
		this.window.ResizeToFitY = true;
		this.width = 16.0F;
		this.height = 16.0F;
		this.window.AddChild(new DialogButton(this, (float)(this.window.getWidth().intValue() - 30), (float)(this.window.getHeight().intValue() - 18), "Ok", "Ok"));
		this.window.AddChild(new DialogButton(this, 10.0F, (float)(this.window.getHeight().intValue() - 18), "No more", "No more"));
	}

	public void ButtonClicked(String string) {
		if (string.equals("Ok")) {
			this.setVisible(false);
			this.window.setVisible(false);
		}

		if (string.equals("No more")) {
			this.setVisible(false);
			this.window.setVisible(false);
			doOthers = false;
			for (int int1 = 0; int1 < UIManager.getUI().size(); ++int1) {
				if (UIManager.getUI().get(int1) instanceof HelpIcon) {
					UIManager.getUI().remove(int1);
					--int1;
				}
			}
		}
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else if (!this.Closed) {
			return this.window.onMouseDown((double)((int)(double1 - this.window.getX())), (double)((int)(double2 - this.window.getY())));
		} else {
			this.clicked = true;
			return Boolean.FALSE;
		}
	}

	public Boolean onMouseMove(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else {
			this.mouseOver = true;
			return Boolean.FALSE;
		}
	}

	public void onMouseMoveOutside(double double1, double double2) {
		if (this.isVisible()) {
			this.clicked = false;
			this.mouseOver = false;
		}
	}

	public Boolean onMouseUp(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else if (!this.Closed) {
			return this.window.onMouseUp((double)((int)(double1 - this.window.getX())), (double)((int)(double2 - this.window.getY())));
		} else {
			if (this.clicked) {
				this.Closed = false;
				Iterator iterator = UIManager.getUI().iterator();
				while (iterator.hasNext()) {
					UIElement uIElement = (UIElement)iterator.next();
					if (uIElement instanceof HelpIcon && uIElement != this) {
						((HelpIcon)uIElement).Closed = true;
					}
				}
			}

			this.clicked = false;
			return Boolean.FALSE;
		}
	}

	public void render() {
		if (this.isVisible()) {
			if (this.mouseOver) {
				this.DrawTextureCol(this.tex2, (double)(this.getX().intValue() - 8), (double)(this.getY().intValue() - 8), new Color(1.0F, 1.0F, 1.0F, 1.0F));
			} else {
				this.DrawTextureCol(this.tex, (double)(this.getX().intValue() - 8), (double)(this.getY().intValue() - 8), new Color(1.0F, 1.0F, 1.0F, 1.0F));
			}

			if (!this.Closed) {
				this.window.render();
			}

			super.render();
		}
	}

	public void update() {
		if (this.isVisible()) {
			super.update();
			if (this.follow != null) {
				this.setX(this.follow.getAbsoluteX() + (double)this.origX);
				this.setY(this.follow.getAbsoluteY() + (double)this.origY);
				this.window.setX(0.0);
				this.window.setY(0.0);
			}

			if (!this.Closed) {
				this.setWidth(this.window.getWidth() + 80.0);
				this.setHeight(this.window.getHeight());
			}

			if (!this.Closed) {
				((UIElement)this.window.getControls().get(1)).setX(this.window.getWidth() - 50.0);
				((UIElement)this.window.getControls().get(1)).setY(this.window.getHeight() - 18.0);
				((UIElement)this.window.getControls().get(2)).setX(10.0);
				((UIElement)this.window.getControls().get(2)).setY(this.window.getHeight() - 18.0);
				this.window.update();
			}
		}
	}
}
