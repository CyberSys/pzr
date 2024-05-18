package zombie.scripting.commands.World;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.SoundManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;


public class PlaySoundEffect extends BaseCommand {
	String position;
	String sound;
	float pitchVar;
	int radius;
	float volume;
	boolean ignoreOutside;
	public String format;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("World")) {
			this.format = stringArray[0].trim().replace("\"", "");
			this.sound = stringArray[1].trim().replace("\"", "");
			this.position = stringArray[2].trim().replace("\"", "");
			this.pitchVar = Float.parseFloat(stringArray[3].trim().replace("\"", ""));
			this.radius = Integer.parseInt(stringArray[4].trim().replace("\"", ""));
			this.volume = Float.parseFloat(stringArray[5].trim().replace("\"", ""));
			this.ignoreOutside = stringArray[6].trim().replace("\"", "").equals("true");
		} else {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		}
	}

	public void begin() {
		Waypoint waypoint = this.module.getWaypoint(this.position);
		if (waypoint != null) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(waypoint.x, waypoint.y, waypoint.z);
			if (square != null) {
				if (this.format.equals("WAV")) {
					SoundManager.instance.PlayWorldSoundWav(this.sound, square, this.pitchVar, (float)this.radius, this.volume, this.ignoreOutside);
				} else if (this.format.equals("OGG")) {
					SoundManager.instance.PlayWorldSound(this.sound, square, this.pitchVar, (float)this.radius, this.volume, this.ignoreOutside);
				}
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
