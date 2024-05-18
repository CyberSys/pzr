package zombie.scripting.objects;

import java.util.ArrayList;

public class LanguageDefinition extends BaseScriptObject {
   public ArrayList Items = new ArrayList();

   public String get(int var1) {
      return (String)this.Items.get(var1);
   }

   public void Load(String var1, String[] var2) {
      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] != null) {
            this.DoSource(var2[var3].trim());
         }
      }

   }

   private void DoSource(String var1) {
      String var2 = "";
      this.Items.add(var1);
   }
}
