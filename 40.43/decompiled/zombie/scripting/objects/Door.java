package zombie.scripting.objects;

public class Door extends BaseScriptObject {
   public int x;
   public int y;
   public int z;
   public String name;

   public void Load(String var1, String[] var2) {
      this.name = var1;
      this.x = Integer.parseInt(var2[0].trim());
      this.y = Integer.parseInt(var2[1].trim());
      this.z = Integer.parseInt(var2[2].trim());
   }
}
