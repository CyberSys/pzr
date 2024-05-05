package zombie.core.textures;


public final class TextureNotFoundException extends RuntimeException {

	public TextureNotFoundException(String string) {
		super("Image " + string + " not found! ");
	}
}
