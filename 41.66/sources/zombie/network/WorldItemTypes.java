package zombie.network;

import java.nio.ByteBuffer;
import zombie.iso.IsoCell;
import zombie.iso.IsoObject;


public class WorldItemTypes {
	public static IsoObject createFromBuffer(ByteBuffer byteBuffer) {
		IsoObject object = null;
		object = IsoObject.factoryFromFileInput((IsoCell)null, (ByteBuffer)byteBuffer);
		return object;
	}
}
