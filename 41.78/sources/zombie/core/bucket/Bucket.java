package zombie.core.bucket;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import zombie.DebugFileWatcher;
import zombie.PredicatedFileWatcher;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.Texture;


public final class Bucket {
	private String m_name;
	private final HashMap m_textures = new HashMap();
	private static final FileSystem m_fs = FileSystems.getDefault();
	private final PredicatedFileWatcher m_fileWatcher = new PredicatedFileWatcher((var1)->{
    return this.HasTexture(var1);
}, (var1)->{
    Texture var2 = this.getTexture(var1);
    var2.reloadFromFile(var1);
    ModelManager.instance.reloadAllOutfits();
});

	public Bucket() {
		DebugFileWatcher.instance.add(this.m_fileWatcher);
	}

	public void AddTexture(Path path, Texture texture) {
		if (texture != null) {
			this.m_textures.put(path, texture);
		}
	}

	public void AddTexture(String string, Texture texture) {
		if (texture != null) {
			this.AddTexture(m_fs.getPath(string), texture);
		}
	}

	public void Dispose() {
		Iterator iterator = this.m_textures.values().iterator();
		while (iterator.hasNext()) {
			Texture texture = (Texture)iterator.next();
			texture.destroy();
		}

		this.m_textures.clear();
	}

	public Texture getTexture(Path path) {
		return (Texture)this.m_textures.get(path);
	}

	public Texture getTexture(String string) {
		return this.getTexture(m_fs.getPath(string));
	}

	public boolean HasTexture(Path path) {
		return this.m_textures.containsKey(path);
	}

	public boolean HasTexture(String string) {
		return this.HasTexture(m_fs.getPath(string));
	}

	String getName() {
		return this.m_name;
	}

	void setName(String string) {
		this.m_name = string;
	}

	public void forgetTexture(String string) {
		this.m_textures.remove(string);
	}
}
