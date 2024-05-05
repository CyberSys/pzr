package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import zombie.util.StringUtils;


public final class HairStyle {
	public String name = "";
	public String model;
	public String texture = "F_Hair_White";
	public final ArrayList alternate = new ArrayList();
	public int level = 0;
	public final ArrayList trimChoices = new ArrayList();
	public boolean growReference = false;
	public boolean attachedHair = false;
	public boolean noChoose = false;

	public boolean isValid() {
		return !StringUtils.isNullOrWhitespace(this.model) && !StringUtils.isNullOrWhitespace(this.texture);
	}

	public String getAlternate(String string) {
		for (int int1 = 0; int1 < this.alternate.size(); ++int1) {
			HairStyle.Alternate alternate = (HairStyle.Alternate)this.alternate.get(int1);
			if (string.equalsIgnoreCase(alternate.category)) {
				return alternate.style;
			}
		}

		return this.name;
	}

	public int getLevel() {
		return this.level;
	}

	public String getName() {
		return this.name;
	}

	public ArrayList getTrimChoices() {
		return this.trimChoices;
	}

	public boolean isAttachedHair() {
		return this.attachedHair;
	}

	public boolean isGrowReference() {
		return this.growReference;
	}

	public boolean isNoChoose() {
		return this.noChoose;
	}

	public static final class Alternate {
		@XmlAttribute
		public String category;
		@XmlAttribute
		public String style;
	}
}
