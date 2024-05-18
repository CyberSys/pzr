package zombie.scripting.commands.Tutorial;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;
import zombie.ui.UIManager;


public class AddHelpIconToWorld extends BaseCommand {
	String title;
	String message;
	String location;
	int offset = 0;
	int x = 0;
	int y = 0;
	int z = 0;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("Tutorial")) {
			this.title = stringArray[0].trim().replace("\"", "");
			this.title = this.module.getLanguage(this.title);
			if (this.title.indexOf("\"") == 0) {
				this.title = this.title.substring(1);
				this.title = this.title.substring(0, this.title.length() - 1);
			}

			this.message = stringArray[1].trim().replace("\"", "");
			this.message = this.module.getLanguage(this.message);
			if (this.message.indexOf("\"") == 0) {
				this.message = this.message.substring(1);
				this.message = this.message.substring(0, this.message.length() - 1);
			}

			this.location = stringArray[2].trim().replace("\"", "");
			this.offset = Integer.parseInt(stringArray[3].trim());
		} else {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		}
	}

	public void begin() {
		Waypoint waypoint = this.module.getWaypoint(this.location.trim());
		if (waypoint != null) {
			this.x = waypoint.x;
			this.y = waypoint.y;
			this.z = waypoint.z;
		} else {
			IsoGameCharacter gameCharacter = this.module.getCharacterActual(this.location);
			if (gameCharacter != null) {
				this.x = (int)gameCharacter.getX();
				this.y = (int)gameCharacter.getY();
				this.z = (int)gameCharacter.getZ();
			}
		}

		UIManager.AddTutorial((float)this.x, (float)this.y, (float)this.z, this.title, this.message, false, (float)this.offset);
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
