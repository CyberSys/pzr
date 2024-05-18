package zombie.ui;

import java.util.Iterator;
import java.util.Stack;


public class TextBox extends UIElement {
	public boolean ResizeParent;
	UIFont font;
	Stack Lines = new Stack();
	String Text;
	public boolean Centred = false;

	public TextBox(UIFont uIFont, int int1, int int2, int int3, String string) {
		this.font = uIFont;
		this.x = (double)int1;
		this.y = (double)int2;
		this.Text = string;
		this.width = (float)int3;
		this.Paginate();
	}

	public void onresize() {
		this.Paginate();
	}

	public void render() {
		if (this.isVisible()) {
			super.render();
			this.Paginate();
			int int1 = 0;
			for (Iterator iterator = this.Lines.iterator(); iterator.hasNext(); int1 += TextManager.instance.MeasureStringY(this.font, (String)this.Lines.get(0))) {
				String string = (String)iterator.next();
				if (this.Centred) {
					TextManager.instance.DrawStringCentre(this.font, (double)this.getAbsoluteX().intValue() + this.getWidth() / 2.0, (double)(this.getAbsoluteY().intValue() + int1), string, 1.0, 1.0, 1.0, 1.0);
				} else {
					TextManager.instance.DrawString(this.font, (double)this.getAbsoluteX().intValue(), (double)(this.getAbsoluteY().intValue() + int1), string, 1.0, 1.0, 1.0, 1.0);
				}
			}

			this.setHeight((double)int1);
		}
	}

	public void update() {
		this.Paginate();
		int int1 = 0;
		for (Iterator iterator = this.Lines.iterator(); iterator.hasNext(); int1 += TextManager.instance.MeasureStringY(this.font, (String)this.Lines.get(0))) {
			String string = (String)iterator.next();
		}

		this.setHeight((double)int1);
	}

	private void Paginate() {
		int int1 = 0;
		this.Lines.clear();
		String[] stringArray = this.Text.split("<br>");
		String[] stringArray2 = stringArray;
		int int2 = stringArray.length;
		for (int int3 = 0; int3 < int2; ++int3) {
			String string = stringArray2[int3];
			if (string.length() == 0) {
				this.Lines.add(" ");
			} else {
				do {
					int int4 = string.indexOf(" ", int1 + 1);
					int int5 = int4;
					if (int4 == -1) {
						int5 = string.length();
					}

					int int6 = TextManager.instance.MeasureStringX(this.font, string.substring(0, int5));
					if ((double)int6 >= this.getWidth()) {
						String string2 = string.substring(0, int1);
						string = string.substring(int1 + 1);
						this.Lines.add(string2);
						int4 = 0;
					} else if (int4 == -1) {
						this.Lines.add(string);
						break;
					}

					int1 = int4;
				}		 while (string.length() > 0);
			}
		}
	}

	public void SetText(String string) {
		this.Text = string;
		this.Paginate();
	}
}
