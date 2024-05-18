package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.scripting.commands.BaseCommand;


public class TestStat extends BaseCommand {
	String owner;
	String stat;
	int modifier = 0;
	IsoGameCharacter chr;
	boolean invert = false;

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		if (string.indexOf("!") == 0) {
			this.invert = true;
			string = string.substring(1);
		}

		this.owner = string;
		this.stat = stringArray[0].trim();
		if (stringArray.length > 1) {
			this.modifier = Integer.parseInt(stringArray[1].trim());
		}
	}

	public boolean getValue() {
		float float1 = 0.0F;
		if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
			this.chr = this.currentinstance.getAlias(this.owner);
		} else {
			this.chr = this.module.getCharacter(this.owner).Actual;
		}

		if (this.chr == null) {
			return false;
		} else {
			if (this.stat.contains("Compassion")) {
				float1 = this.chr.getDescriptor().getCompassion();
			}

			if (this.stat.contains("Bravery")) {
				float1 = this.chr.getDescriptor().getBravery() * 2.0F;
			}

			if (this.stat.contains("Loner")) {
				float1 = this.chr.getDescriptor().getLoner();
			}

			if (this.stat.contains("Temper")) {
				float1 = this.chr.getDescriptor().getTemper();
			}

			float1 *= 10.0F;
			if (this.invert) {
				return !((float)Rand.Next(100) < float1 + (float)this.modifier);
			} else {
				return (float)Rand.Next(100) < float1 + (float)this.modifier;
			}
		}
	}

	public void begin() {
	}

	public boolean AllowCharacterBehaviour(String string) {
		return true;
	}

	public boolean DoesInstantly() {
		return true;
	}
}
