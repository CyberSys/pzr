package zombie.core.textures;


public class TextureNotFoundException extends RuntimeException {

	public TextureNotFoundException(String string) {
		super("Image " + string + " not found! ");
	}
}
