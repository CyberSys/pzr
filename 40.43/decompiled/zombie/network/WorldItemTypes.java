package zombie.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.iso.IsoCell;
import zombie.iso.IsoObject;

public class WorldItemTypes {
   public static IsoObject createFromBuffer(ByteBuffer var0) {
      IsoObject var1 = null;

      try {
         var1 = IsoObject.factoryFromFileInput((IsoCell)null, (ByteBuffer)var0);
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      return var1;
   }
}
