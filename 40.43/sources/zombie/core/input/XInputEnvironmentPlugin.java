package zombie.core.input;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.util.plugins.Plugin;


public class XInputEnvironmentPlugin extends ControllerEnvironment implements Plugin {

	public Controller[] getControllers() {
		Controller[] controllerArray = new Controller[]{XInputController.create(0, "XInputController0"), XInputController.create(1, "XInputController1"), XInputController.create(2, "XInputController2"), XInputController.create(3, "XInputController3")};
		return controllerArray;
	}

	public boolean isSupported() {
		return true;
	}
}
