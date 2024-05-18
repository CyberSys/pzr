package zombie.core.input;

public interface ControlledInputReciever {
   void setInput(Input var1);

   boolean isAcceptingInput();

   void inputEnded();

   void inputStarted();
}
