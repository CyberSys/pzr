package zombie.scripting.commands.Lua;

import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import zombie.Lua.LuaManager;
import zombie.scripting.commands.BaseCommand;

public class LuaCall extends BaseCommand {
   boolean invert = false;
   String position;
   String val;
   Object[] paramProper;
   public String func = "";
   String[] params;

   public boolean getValue() {
      Object[] var1;
      LuaClosure var2;
      String[] var3;
      KahluaTable var4;
      int var5;
      if (this.params.length == 1) {
         this.func = this.params[0].replace("\"", "");
         var2 = null;
         var3 = this.func.split("\\.");
         var4 = LuaManager.env;
         if (var3.length <= 1) {
            var2 = (LuaClosure)var4.rawget(this.func);
         } else {
            for(var5 = 0; var5 < var3.length - 1; ++var5) {
               var4 = (KahluaTable)var4.rawget(var3[var5]);
            }

            var2 = (LuaClosure)var4.rawget(var3[var3.length - 1]);
         }

         var1 = LuaManager.caller.pcall(LuaManager.thread, var2, (Object[])());
      } else {
         this.paramProper = new Object[this.params.length - 1];

         for(int var9 = 0; var9 < this.params.length; ++var9) {
            String var10 = this.params[var9];
            var4 = null;
            if (var9 == 0) {
               this.func = this.params[var9].replace("\"", "");
            } else {
               float var12 = 0.0F;
               boolean var6 = false;

               try {
                  var12 = Float.parseFloat(var10);
                  var6 = true;
               } catch (Exception var8) {
               }

               if (var6) {
                  this.paramProper[var9 - 1] = new Double((double)var12);
               } else if (var10.contains("\"")) {
                  this.paramProper[var9 - 1] = var10.replace("\"", "");
               } else if (this.currentinstance != null && this.currentinstance.HasAlias(var10)) {
                  this.paramProper[var9 - 1] = this.currentinstance.getAlias(var10);
               } else if (this.module.getCharacter(var10) == null) {
                  this.paramProper[var9 - 1] = null;
               } else if (this.module.getCharacter(var10).Actual == null) {
                  this.paramProper[var9 - 1] = null;
               } else {
                  this.paramProper[var9 - 1] = this.module.getCharacter(var10).Actual;
               }
            }
         }

         var2 = null;
         var3 = this.func.split("\\.");
         var4 = LuaManager.env;
         if (var3.length <= 1) {
            var2 = (LuaClosure)var4.rawget(this.func);
         } else {
            for(var5 = 0; var5 < var3.length - 1; ++var5) {
               var4 = (KahluaTable)var4.rawget(var3[var5]);
            }

            var2 = (LuaClosure)var4.rawget(var3[var3.length - 1]);
         }

         var1 = LuaManager.caller.pcall(LuaManager.thread, var2, (Object[])this.paramProper);
      }

      boolean var11 = false;
      if (var1.length > 1) {
         var11 = (Boolean)var1[1];
      }

      if (this.invert) {
         return !var11;
      } else {
         return var11;
      }
   }

   public void init(String var1, String[] var2) {
      this.params = var2;
      this.val = var1;
      if (this.val.indexOf("!") == 0) {
         this.invert = true;
         this.val = this.val.substring(1);
      }

   }

   public void begin() {
      KahluaTable var3;
      LuaClosure var8;
      String[] var9;
      int var10;
      if (this.params.length == 1) {
         var8 = null;
         var9 = this.func.split("\\.");
         var3 = LuaManager.env;
         if (var9.length > 1) {
            for(var10 = 0; var10 < var9.length - 1; ++var10) {
               var3 = (KahluaTable)var3.rawget(var9[var10]);
            }

            var8 = (LuaClosure)var3.rawget(var9[var9.length - 1]);
         } else {
            var8 = (LuaClosure)var3.rawget(this.func);
         }

         LuaManager.caller.pcall(LuaManager.thread, var8, (Object[])());
      } else {
         this.paramProper = new Object[this.params.length - 1];

         for(int var1 = 0; var1 < this.params.length; ++var1) {
            String var2 = this.params[var1];
            var3 = null;
            if (var1 == 0) {
               this.func = this.params[var1].replace("\"", "");
            } else {
               float var4 = 0.0F;
               boolean var5 = false;

               try {
                  var4 = Float.parseFloat(var2);
                  var5 = true;
               } catch (Exception var7) {
               }

               if (var5) {
                  this.paramProper[var1 - 1] = new Double((double)var4);
               } else if (var2.contains("\"")) {
                  this.paramProper[var1 - 1] = var2.replace("\"", "");
               } else if (this.currentinstance != null && this.currentinstance.HasAlias(var2)) {
                  this.paramProper[var1 - 1] = this.currentinstance.getAlias(var2);
               } else if (this.module.getCharacter(var2) == null) {
                  this.paramProper[var1 - 1] = null;
               } else if (this.module.getCharacter(var2).Actual == null) {
                  this.paramProper[var1 - 1] = null;
               } else {
                  this.paramProper[var1 - 1] = this.module.getCharacter(var2).Actual;
               }
            }
         }

         var8 = null;
         var9 = this.func.split("\\.");
         var3 = LuaManager.env;
         if (var9.length > 1) {
            for(var10 = 0; var10 < var9.length - 1; ++var10) {
               var3 = (KahluaTable)var3.rawget(var9[var10]);
            }

            var8 = (LuaClosure)var3.rawget(var9[var9.length - 1]);
         } else {
            var8 = (LuaClosure)var3.rawget(this.func);
         }

         LuaManager.caller.pcall(LuaManager.thread, var8, (Object[])this.paramProper);
      }
   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public boolean DoesInstantly() {
      return true;
   }
}
