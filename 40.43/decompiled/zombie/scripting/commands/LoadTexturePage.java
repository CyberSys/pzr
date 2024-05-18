package zombie.scripting.commands;

import java.util.Stack;
import zombie.core.textures.TexturePackPage;
import zombie.network.GameServer;

public class LoadTexturePage extends BaseCommand {
   String page = null;
   public Stack Pairs = new Stack();

   public void init(String var1, String[] var2) {
      this.page = var2[0];
      if (var2.length > 1) {
         String var3 = null;
         String var4 = null;
         boolean var5 = false;

         for(int var6 = 1; var6 < var2.length; ++var6) {
            if (!var5) {
               var4 = null;
               var3 = var2[var6];
            } else {
               var4 = var2[var6];
               this.Pairs.add(new LoadTexturePage.WatchPair(var3, var4));
               var3 = null;
               var4 = null;
            }

            var5 = !var5;
         }
      }

   }

   public void begin() {
      if (!GameServer.bServer) {
         if (!this.Pairs.isEmpty()) {
            TexturePackPage.getPackPage(this.page, this.Pairs);
         } else {
            TexturePackPage.getPackPage(this.page);
         }

      }
   }

   public void Finish() {
   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public boolean DoesInstantly() {
      return true;
   }

   public class WatchPair {
      public String name;
      public String token;

      public WatchPair(String var2, String var3) {
         this.name = var2;
         this.token = var3;
      }
   }
}
