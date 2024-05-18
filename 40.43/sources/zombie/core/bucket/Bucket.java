package zombie.core.bucket;

import java.util.HashMap;
import java.util.Iterator;
import zombie.core.textures.Texture;
import zombie.iso.sprite.IsoSpriteManager;


public class Bucket {
	public IsoSpriteManager SpriteManager;
	private String name;
	private HashMap textures = new HashMap();

	public Bucket(IsoSpriteManager spriteManager) {
		this.SpriteManager = spriteManager;
	}

	public IsoSpriteManager getSpriteManager() {
		return this.SpriteManager;
	}

	public Bucket() {
		this.SpriteManager = new IsoSpriteManager();
	}

	public void AddTexture(String string, Texture texture) {
		if (texture != null) {
			this.textures.put(string, texture);
		}
	}

	public void Dispose() {
		Iterator iterator = this.textures.values().iterator();
		while (iterator.hasNext()) {
			Texture texture = (Texture)iterator.next();
			texture.destroy();
		}

		this.SpriteManager.Dispose();
	}

	public Texture getTexture(String string) {
		return (Texture)this.textures.get(string);
	}

	public boolean HasTexture(String string) {
		return this.textures.containsKey(string);
	}

	String getName() {
		return this.name;
	}

	void setName(String string) {
		this.name = string;
	}

	public void forgetTexture(String string) {
		this.textures.remove(string);
	}
}
