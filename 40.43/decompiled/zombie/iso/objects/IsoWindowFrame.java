package zombie.iso.objects;

import zombie.characters.IsoPlayer;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;

public class IsoWindowFrame {
   private static IsoWindowFrame.Direction getDirection(IsoObject var0) {
      if (!(var0 instanceof IsoWindow) && !(var0 instanceof IsoThumpable)) {
         if (var0 != null && var0.getProperties() != null && var0.getObjectIndex() != -1) {
            if (var0.getProperties().Is(IsoFlagType.WindowN)) {
               return IsoWindowFrame.Direction.NORTH;
            } else {
               return var0.getProperties().Is(IsoFlagType.WindowW) ? IsoWindowFrame.Direction.WEST : IsoWindowFrame.Direction.INVALID;
            }
         } else {
            return IsoWindowFrame.Direction.INVALID;
         }
      } else {
         return IsoWindowFrame.Direction.INVALID;
      }
   }

   public static boolean isWindowFrame(IsoObject var0) {
      return getDirection(var0).isValid();
   }

   public static boolean isWindowFrame(IsoObject var0, boolean var1) {
      IsoWindowFrame.Direction var2 = getDirection(var0);
      return var1 && var2 == IsoWindowFrame.Direction.NORTH || !var1 && var2 == IsoWindowFrame.Direction.WEST;
   }

   public static int countAddSheetRope(IsoObject var0) {
      IsoWindowFrame.Direction var1 = getDirection(var0);
      return var1.isValid() ? IsoWindow.countAddSheetRope(var0.getSquare(), var1 == IsoWindowFrame.Direction.NORTH) : 0;
   }

   public static boolean canAddSheetRope(IsoObject var0) {
      IsoWindowFrame.Direction var1 = getDirection(var0);
      return var1.isValid() && IsoWindow.canAddSheetRope(var0.getSquare(), var1 == IsoWindowFrame.Direction.NORTH);
   }

   public static boolean haveSheetRope(IsoObject var0) {
      IsoWindowFrame.Direction var1 = getDirection(var0);
      return var1.isValid() && IsoWindow.isTopOfSheetRopeHere(var0.getSquare(), var1 == IsoWindowFrame.Direction.NORTH);
   }

   public static boolean addSheetRope(IsoObject var0, IsoPlayer var1, String var2) {
      return !canAddSheetRope(var0) ? false : IsoWindow.addSheetRope(var1, var0.getSquare(), getDirection(var0) == IsoWindowFrame.Direction.NORTH, var2);
   }

   public static boolean removeSheetRope(IsoObject var0, IsoPlayer var1) {
      return !haveSheetRope(var0) ? false : IsoWindow.removeSheetRope(var1, var0.getSquare(), getDirection(var0) == IsoWindowFrame.Direction.NORTH);
   }

   private static enum Direction {
      INVALID,
      NORTH,
      WEST;

      public boolean isValid() {
         return this != INVALID;
      }
   }
}