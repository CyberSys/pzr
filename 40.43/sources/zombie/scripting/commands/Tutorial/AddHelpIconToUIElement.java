package zombie.scripting.commands.Tutorial;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.scripting.commands.BaseCommand;
import zombie.ui.HUDButton;
import zombie.ui.Sidebar;
import zombie.ui.UIManager;


public class AddHelpIconToUIElement extends BaseCommand {
	String title;
	String message;
	int x;
	int y;
	String uielement;

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

			this.uielement = stringArray[2].trim().replace("\"", "");
			this.x = Integer.parseInt(stringArray[3].trim());
			this.y = Integer.parseInt(stringArray[4].trim());
		} else {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		}
	}

	public void begin() {
		HUDButton hUDButton = null;
		if (this.uielement.equals("SIDEBAR_INVENTORY")) {
			hUDButton = Sidebar.InventoryIcon;
		}

		if (hUDButton != null) {
			UIManager.AddTutorial(hUDButton, (double)this.x, (double)this.y, this.title, this.message, false);
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
