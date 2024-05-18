package zombie.iso;

import java.nio.ByteBuffer;
import zombie.core.utils.ObjectGrid;

public class SliceY {
   IsoCell cell;
   public ObjectGrid Squares;
   int tall;
   int width;
   int y;
   boolean bSaveDirty = true;
   public static ByteBuffer SliceBuffer;
   public static ByteBuffer SliceBuffer2;

   public SliceY(IsoCell var1, int var2, int var3, int var4) {
      this.y = var4;
      this.cell = var1;
      this.width = var2;
      this.tall = IsoCell.getMaxHeight();
      this.Squares = new ObjectGrid(var2, var3);
   }
}
