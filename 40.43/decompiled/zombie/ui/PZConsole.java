package zombie.ui;

import java.util.ArrayList;
import zombie.console.Commands;

public class PZConsole extends UIElement {
   public ArrayList buffer = new ArrayList();
   public String currentline = "";
   public static PZConsole instance = new PZConsole();
   ArrayList charactersTypedSinceUpdate = new ArrayList();
   ArrayList keysReleasedSinceUpdate = new ArrayList();

   public void render() {
   }

   public void update() {
      int var1;
      for(var1 = 0; var1 < this.charactersTypedSinceUpdate.size(); ++var1) {
         this.currentline = this.currentline + this.charactersTypedSinceUpdate.get(var1);
      }

      for(var1 = 0; var1 < this.keysReleasedSinceUpdate.size(); ++var1) {
         if ((Integer)this.keysReleasedSinceUpdate.get(var1) == 28) {
            this.buffer.add("> " + this.currentline.trim());
            this.Process(this.currentline);
            this.currentline = "";
         }
      }

   }

   public void Log(String var1) {
      this.buffer.add(var1);
   }

   public Boolean isVisible() {
      return Boolean.FALSE;
   }

   private void Process(String var1) {
      try {
         String[] var2 = var1.split(",");
         String var3 = "";
         if (var2[0].trim().contains(" ")) {
            var3 = var2[0].split(" ")[0].trim();
            var2[0] = var2[0].trim().split(" ")[1].trim();
         } else {
            var3 = var2[0].trim();
         }

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var2[var4] = var2[var4].trim();
         }

         var3 = var3.toLowerCase();
         Commands.ProcessCommand(var3, var2);
      } catch (Exception var5) {
         this.buffer.add("Invalid command: " + var1);
      }

   }
}
