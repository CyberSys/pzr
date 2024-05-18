package zombie.scripting.objects;

public class VehicleTemplate extends BaseScriptObject {
   public String name;
   public String body;
   public VehicleScript script;

   public VehicleTemplate(ScriptModule var1, String var2, String var3) {
      this.module = var1;
      this.name = var2;
      this.body = var3;
   }

   public VehicleScript getScript() {
      if (this.script == null) {
         this.script = new VehicleScript();
         this.script.module = this.getModule();
         this.script.Load(this.name, this.body);
      }

      return this.script;
   }
}
