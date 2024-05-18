package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoUtils;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptCharacter;
import zombie.scripting.objects.Waypoint;


public class InRange extends BaseCommand {
	String owner;
	String other;
	int min = 0;
	int x;
	int y;
	int z;
	boolean bChar = false;
	boolean invert = false;

	public void init(String string, String[] stringArray) {
		this.owner = string;
		if (this.owner.indexOf("!") == 0) {
			this.invert = true;
			this.owner = this.owner.substring(1);
		}

		if (stringArray.length == 2) {
			Waypoint waypoint = this.module.getWaypoint(stringArray[0].trim());
			if (waypoint != null) {
				this.x = waypoint.x;
				this.y = waypoint.y;
				this.z = waypoint.z;
			} else {
				this.bChar = true;
				this.other = stringArray[0].trim();
			}

			this.min = Integer.parseInt(stringArray[1].trim());
		}
	}

	public boolean getValue() {
		IsoGameCharacter gameCharacter = null;
		if (this.currentinstance.HasAlias(this.owner)) {
			gameCharacter = this.currentinstance.getAlias(this.owner);
		} else {
			gameCharacter = this.module.getCharacterActual(this.owner);
			ScriptCharacter scriptCharacter = this.module.getCharacter(this.owner);
			if (scriptCharacter.Actual == null) {
				return false;
			}
		}

		IsoGameCharacter gameCharacter2 = gameCharacter;
		if (gameCharacter.isDead()) {
			return true;
		} else if (this.bChar) {
			if (this.currentinstance.HasAlias(this.other)) {
				gameCharacter = this.currentinstance.getAlias(this.other);
			} else {
				gameCharacter = this.module.getCharacterActual(this.other);
				ScriptCharacter scriptCharacter2 = this.module.getCharacter(this.other);
				if (scriptCharacter2.Actual == null) {
					return false;
				}
			}

			if (gameCharacter.isDead()) {
				return true;
			} else if (this.invert) {
				return !(IsoUtils.DistanceManhatten(gameCharacter.getX(), gameCharacter.getY(), gameCharacter2.getX(), gameCharacter2.getY()) <= (float)this.min) || gameCharacter2.getZ() != gameCharacter.getZ();
			} else {
				return IsoUtils.DistanceManhatten(gameCharacter.getX(), gameCharacter.getY(), gameCharacter2.getX(), gameCharacter2.getY()) <= (float)this.min && gameCharacter2.getZ() == gameCharacter.getZ();
			}
		} else if (this.invert) {
			return !(IsoUtils.DistanceManhatten((float)this.x, (float)this.y, gameCharacter.getX(), gameCharacter.getY()) <= (float)this.min) || gameCharacter.getZ() != (float)this.z;
		} else {
			return IsoUtils.DistanceManhatten((float)this.x, (float)this.y, gameCharacter.getX(), gameCharacter.getY()) <= (float)this.min && gameCharacter.getZ() == (float)this.z;
		}
	}

	public void begin() {
	}

	public void Finish() {
	}

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public boolean DoesInstantly() {
		return true;
	}
}
