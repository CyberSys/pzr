package zombie.scripting.objects;

public class Zone extends BaseScriptObject {
   public int x;
   public int y;
   public int x2;
   public int y2;
   public int z = 0;
   public String name;

   public Zone() {
   }

   public Zone(String var1, int var2, int var3, int var4, int var5) {
      this.name = new String(var1);
      this.x = var2;
      this.y = var3;
      this.x2 = var4;
      this.y2 = var5;
   }

   public Zone(String var1, int var2, int var3, int var4, int var5, int var6) {
      this.name = new String(var1);
      this.x = var2;
      this.y = var3;
      this.x2 = var4;
      this.y2 = var5;
      this.z = var6;
   }

   public void Load(String var1, String[] var2) {
      this.name = new String(var1);
      this.x = Integer.parseInt(var2[0].trim());
      this.y = Integer.parseInt(var2[1].trim());
      this.x2 = Integer.parseInt(var2[2].trim());
      this.y2 = Integer.parseInt(var2[3].trim());
      if (var2.length > 4) {
         this.z = Integer.parseInt(var2[4].trim());
      }

   }
}
