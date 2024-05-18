package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Room;


public class IsInRoom extends BaseCommand {
	String owner;
	String room;
	boolean invert = false;

	public void init(String string, String[] stringArray) {
		if (stringArray[0].contains(")")) {
			string = string;
		}

		this.owner = string;
		if (this.owner.indexOf("!") == 0) {
			this.invert = true;
			this.owner = this.owner.substring(1);
		}

		this.room = stringArray[0].trim();
	}

	public boolean getValue() {
		IsoGameCharacter gameCharacter = this.module.getCharacterActual(this.owner);
		if (gameCharacter == null) {
			return false;
		} else {
			Room room = this.module.getRoom(this.room);
			if (room != null) {
				if (gameCharacter.getCurrentSquare().getRoom() == null) {
					return false;
				} else if (this.invert) {
					return !room.name.equals(gameCharacter.getCurrentSquare().getRoom().RoomDef);
				} else {
					return room.name.equals(gameCharacter.getCurrentSquare().getRoom().RoomDef);
				}
			} else {
				IsoGameCharacter gameCharacter2 = this.module.getCharacterActual(this.room);
				if (gameCharacter2 == null) {
					return false;
				} else {
					boolean boolean1 = false;
					if (gameCharacter.getCurrentSquare() != null && gameCharacter2.getCurrentSquare() != null) {
						if (gameCharacter.getCurrentSquare().getRoom() == gameCharacter2.getCurrentSquare().getRoom()) {
							boolean1 = true;
						}

						if (this.invert) {
							return !boolean1;
						} else {
							return boolean1;
						}
					} else {
						return false;
					}
				}
			}
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
