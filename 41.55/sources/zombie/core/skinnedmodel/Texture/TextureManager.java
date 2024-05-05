package zombie.core.skinnedmodel.Texture;

import java.util.HashMap;
import zombie.core.textures.Texture;


public class TextureManager {
	public static TextureManager Instance = new TextureManager();
	public HashMap Textures = new HashMap();

	public boolean AddTexture(String string) {
		Texture texture = Texture.getSharedTexture(string);
		if (texture == null) {
			return false;
		} else {
			this.Textures.put(string, new Texture2D(texture));
			return true;
		}
	}

	public void AddTexture(String string, Texture texture) {
		if (!this.Textures.containsKey(string)) {
			this.Textures.put(string, new Texture2D(texture));
		}
	}
}
