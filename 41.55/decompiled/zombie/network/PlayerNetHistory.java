package zombie.network;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.textures.ColorInfo;

public class PlayerNetHistory {
   private ArrayList states = new ArrayList();
   private int INTERP_TIME = 100;
   private ColorInfo lightInfo = new ColorInfo(1.0F, 1.0F, 1.0F, 0.5F);

   public void add(PlayerNetState var1) {
      this.INTERP_TIME = 400;
      this.states.add(var1);
      long var2 = System.currentTimeMillis();
      String var4 = "";

      for(int var5 = 0; var5 < this.states.size(); ++var5) {
         var4 = var4 + (var2 - ((PlayerNetState)this.states.get(var5)).time) + " ";
      }

      while(!this.states.isEmpty() && ((PlayerNetState)this.states.get(0)).time < var2 - 1000L) {
         PlayerNetState.release((PlayerNetState)this.states.get(0));
         this.states.remove(0);
      }

   }

   public void interpolate(IsoPlayer var1) {
   }

   public void render(IsoPlayer var1) {
   }

   private int findStateBefore(long var1) {
      for(int var3 = this.states.size() - 1; var3 >= 0; --var3) {
         if (((PlayerNetState)this.states.get(var3)).time <= var1) {
            return var3;
         }
      }

      return -1;
   }
}
