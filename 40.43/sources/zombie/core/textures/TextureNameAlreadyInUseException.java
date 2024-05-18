package zombie.core.textures;


public class TextureNameAlreadyInUseException extends RuntimeException {

	public TextureNameAlreadyInUseException(String string) {
		super("Texture Name " + string + " is already in use");
	}
}
