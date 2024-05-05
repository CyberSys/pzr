package zombie.asset;

import java.util.zip.CRC32;


public final class AssetType {
	public static final AssetType INVALID_ASSET_TYPE = new AssetType("");
	public long type;

	public AssetType(String string) {
		CRC32 cRC32 = new CRC32();
		cRC32.update(string.getBytes());
		this.type = cRC32.getValue();
	}
}
