package zombie.core.input;


public interface ControlledInputReciever {

	void setInput(Input input);

	boolean isAcceptingInput();

	void inputEnded();

	void inputStarted();
}
