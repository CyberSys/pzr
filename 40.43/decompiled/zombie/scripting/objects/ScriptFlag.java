package zombie.scripting.objects;

public class ScriptFlag extends BaseScriptObject {
   public String name;
   public String value;

   public void Load(String var1, String[] var2) {
      this.name = var1;
      this.value = var2[0].trim();
   }

   public void SetValue(String var1) {
      this.value = var1;
   }

   public boolean IsValue(String var1) {
      return this.value.equals(var1);
   }
}
