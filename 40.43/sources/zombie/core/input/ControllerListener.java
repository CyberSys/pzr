package zombie.core.input;


public interface ControllerListener extends ControlledInputReciever {

	void controllerLeftPressed(int int1);

	void controllerLeftReleased(int int1);

	void controllerRightPressed(int int1);

	void controllerRightReleased(int int1);

	void controllerUpPressed(int int1);

	void controllerUpReleased(int int1);

	void controllerDownPressed(int int1);

	void controllerDownReleased(int int1);

	void controllerButtonPressed(int int1, int int2);

	void controllerButtonReleased(int int1, int int2);
}
