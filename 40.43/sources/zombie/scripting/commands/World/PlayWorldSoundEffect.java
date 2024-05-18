package zombie.scripting.commands.World;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.WorldSoundManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;


public class PlayWorldSoundEffect extends BaseCommand {
	String position;
	int radius;
	int volume;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("World")) {
			this.position = stringArray[0].trim().replace("\"", "");
			this.radius = Integer.parseInt(stringArray[1].trim().replace("\"", ""));
			this.volume = Integer.parseInt(stringArray[2].trim().replace("\"", ""));
		} else {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		}
	}

	public void begin() {
		Waypoint waypoint = this.module.getWaypoint(this.position);
		if (waypoint != null) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(waypoint.x, waypoint.y, waypoint.z);
			if (square != null) {
				WorldSoundManager.instance.addSound((IsoObject)null, waypoint.x, waypoint.y, waypoint.z, this.radius, this.volume);
			}
		}
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
