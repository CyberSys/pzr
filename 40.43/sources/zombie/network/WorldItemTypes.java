package zombie.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.iso.IsoCell;
import zombie.iso.IsoObject;


public class WorldItemTypes {
	public static IsoObject createFromBuffer(ByteBuffer byteBuffer) {
		IsoObject object = null;
		try {
			object = IsoObject.factoryFromFileInput((IsoCell)null, (ByteBuffer)byteBuffer);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return object;
	}
}
