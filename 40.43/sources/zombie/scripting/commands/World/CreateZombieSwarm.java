package zombie.scripting.commands.World;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.iso.IsoWorld;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Zone;


public class CreateZombieSwarm extends BaseCommand {
	String position;
	int num = 1;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("World")) {
			this.num = Integer.parseInt(stringArray[0].trim());
			this.position = stringArray[1].trim().replace("\"", "");
		} else {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		}
	}

	public void begin() {
		Zone zone = this.module.getZone(this.position);
		if (zone != null) {
			IsoWorld.instance.CreateSwarm(this.num, zone.x, zone.y, zone.x2, zone.y2);
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
