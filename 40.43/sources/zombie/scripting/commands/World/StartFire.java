package zombie.scripting.commands.World;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoFireManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;


public class StartFire extends BaseCommand {
	String position;
	int Energy;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("World")) {
			this.position = stringArray[0].trim().replace("\"", "");
			this.Energy = Integer.parseInt(stringArray[1].trim());
		} else {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		}
	}

	public void begin() {
		Waypoint waypoint = this.module.getWaypoint(this.position);
		if (waypoint != null) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(waypoint.x, waypoint.y, waypoint.z);
			if (square != null) {
				IsoFireManager.StartFire(IsoWorld.instance.CurrentCell, square, true, this.Energy);
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
