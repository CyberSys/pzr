package zombie.text.templating;


public interface IReplaceProvider {

	boolean hasReplacer(String string);

	IReplace getReplacer(String string);
}
