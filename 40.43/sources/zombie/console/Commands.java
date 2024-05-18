package zombie.console;

import java.security.InvalidParameterException;
import zombie.characters.IsoPlayer;
import zombie.ui.PZConsole;
import zombie.ui.TutorialManager;


public class Commands {

	public static void Log(String string) {
		PZConsole.instance.Log(string);
	}

	public static void ProcessCommand(String string, String[] stringArray) {
		string = string.toLowerCase();
		if (string.equals("addinv")) {
			IsoPlayer.getInstance().getInventory().AddItem(stringArray[0]);
		} else if (string.equals("debug")) {
			if (stringArray.length > 1) {
				throw new InvalidParameterException();
			}

			if (stringArray[0].equals("on")) {
				TutorialManager.Debug = true;
				Log("Debug mode activated");
			}

			if (stringArray[0].equals("off")) {
				TutorialManager.Debug = false;
				Log("Debug mode deactivated");
			}
		}
	}
}
