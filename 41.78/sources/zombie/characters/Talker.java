package zombie.characters;


public interface Talker {

	boolean IsSpeaking();

	void Say(String string);

	String getSayLine();

	String getTalkerType();
}
