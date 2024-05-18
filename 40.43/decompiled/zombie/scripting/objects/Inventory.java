package zombie.scripting.objects;

import java.util.ArrayList;

public class Inventory extends BaseScriptObject {
   public ArrayList Items = new ArrayList();

   public void Load(String var1, String[] var2) {
      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] != null) {
            this.DoSource(var2[var3].trim());
         }
      }

   }

   private void DoSource(String var1) {
      Inventory.Source var2 = new Inventory.Source();
      if (var1.contains("=")) {
         var2.count = Integer.parseInt(var1.split("=")[1].trim());
         var1 = var1.split("=")[0].trim();
      }

      if (var1.equals("null")) {
         var2.type = null;
      } else {
         var2.type = var1;
      }

      this.Items.add(var2);
   }

   public class Source {
      public String type;
      public int count = 1;
   }
}
