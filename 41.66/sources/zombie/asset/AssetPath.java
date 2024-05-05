package zombie.asset;

import zombie.util.StringUtils;


public final class AssetPath {
	protected String m_path;

	public AssetPath(String string) {
		this.m_path = string;
	}

	public boolean isValid() {
		return !StringUtils.isNullOrEmpty(this.m_path);
	}

	public int getHash() {
		return this.m_path.hashCode();
	}

	public String getPath() {
		return this.m_path;
	}

	public String toString() {
		String string = this.getClass().getSimpleName();
		return string + "{ \"" + this.getPath() + "\" }";
	}
}
