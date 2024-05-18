package zombie.scripting.commands.World;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.characters.IsoZombie;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;


public class SpawnZombie extends BaseCommand {
	String position;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("World")) {
			this.position = stringArray[0].trim().replace("\"", "");
		} else {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		}
	}

	public void begin() {
		Waypoint waypoint = this.module.getWaypoint(this.position);
		if (waypoint != null) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(waypoint.x, waypoint.y, waypoint.z);
			if (square != null) {
				IsoZombie zombie = new IsoZombie(IsoWorld.instance.CurrentCell);
				zombie.KeepItReal = true;
				zombie.setX((float)waypoint.x);
				zombie.setY((float)waypoint.y);
				zombie.setZ((float)waypoint.z);
				zombie.setCurrent(square);
				IsoWorld.instance.CurrentCell.getZombieList().add(zombie);
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
