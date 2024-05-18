package zombie.scripting.objects;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map.Entry;
import javax.swing.JOptionPane;
import zombie.characters.IsoGameCharacter;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParsingUtils;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.commands.CommandFactory;
import zombie.scripting.commands.ConditionalCommand;

public class Script extends BaseScriptObject {
   public boolean Instancable = false;
   public String name;
   public ArrayList CommandList = new ArrayList();
   ConditionalCommand LastConditional = null;

   public void Load(String var1, String[] var2) {
      this.name = var1;

      for(int var3 = 0; var3 < var2.length; ++var3) {
         this.ParseCommand(var2[var3].trim());
      }

   }

   public void begin(Script.ScriptInstance var1) {
      var1.CommandIndex = 0;
      if (var1.CommandIndex < this.CommandList.size()) {
         BaseCommand var2 = (BaseCommand)this.CommandList.get(var1.CommandIndex);
         var2.currentinstance = var1;
         var2.begin();

         while(var2.DoesInstantly()) {
            var2.currentinstance = var1;
            var2.update();
            var2.Finish();
            ++var1.CommandIndex;
            if (var1.CommandIndex >= this.CommandList.size()) {
               return;
            }

            var2 = (BaseCommand)this.CommandList.get(var1.CommandIndex);
            var2.currentinstance = var1;
            var2.begin();
         }

      }
   }

   public boolean finished(Script.ScriptInstance var1) {
      return var1.CommandIndex >= this.CommandList.size();
   }

   public void reset(Script.ScriptInstance var1) {
      var1.CommandIndex = 0;
      var1.Paused = false;
   }

   public void update(Script.ScriptInstance var1) {
      if (var1.CommandIndex < this.CommandList.size()) {
         if (!var1.Paused) {
            BaseCommand var2 = (BaseCommand)this.CommandList.get(var1.CommandIndex);
            var2.currentinstance = var1;
            if (ScriptManager.instance.skipping) {
               var2.updateskip();
            } else {
               var2.update();
            }

            if (ScriptManager.instance.skipping || var2.IsFinished()) {
               var2.Finish();
               ++var1.CommandIndex;
               if (var1.CommandIndex >= this.CommandList.size()) {
                  return;
               }

               BaseCommand var3 = (BaseCommand)this.CommandList.get(var1.CommandIndex);
               var3.currentinstance = var1;
               var3.begin();

               while(var3.DoesInstantly()) {
                  var3.update();
                  var3.Finish();
                  ++var1.CommandIndex;
                  if (var1.CommandIndex >= this.CommandList.size()) {
                     return;
                  }

                  var3 = (BaseCommand)this.CommandList.get(var1.CommandIndex);
                  var3.currentinstance = var1;
                  var3.begin();
               }
            }

         }
      }
   }

   protected void ParseCommand(String var1) {
      if (var1.trim().length() != 0) {
         BaseCommand var2 = this.ReturnCommand(var1);
         if (var2 != null) {
            var2.script = this;
            this.CommandList.add(var2);
         }

      }
   }

   protected BaseCommand ReturnCommand(String var1) {
      if (var1.indexOf("callwait") == 0) {
         var1 = var1.replace("callwait", "");
         var1 = var1.trim() + ".CallWait()";
      }

      if (var1.indexOf("call") == 0) {
         var1 = var1.replace("call", "");
         var1 = var1.trim() + ".Call()";
      }

      int var8;
      int var9;
      if (var1.indexOf("else") == 0) {
         var8 = var1.indexOf("{");
         var9 = var1.lastIndexOf("}");
         String var11 = var1.substring(var8 + 1, var9);
         this.LastConditional.AddElse(var11);
         this.LastConditional = null;
         return null;
      } else {
         int var5;
         if (var1.indexOf("if") == 0) {
            var8 = var1.indexOf("{");
            var9 = var1.lastIndexOf("}");
            int var10 = var1.indexOf("(");
            var5 = var1.indexOf(")");
            String var12 = var1.substring(var8 + 1, var9);
            String var13 = var1.substring(var10 + 1, var8).trim();
            var13 = var13.substring(0, var13.length() - 1);
            this.LastConditional = new ConditionalCommand(var13, var12, this);
            return this.LastConditional;
         } else {
            String var2 = null;
            String var3 = null;
            if (var1.indexOf(".") != -1 && var1.indexOf(".") < var1.indexOf("(")) {
               String[] var4 = new String[2];
               var5 = var1.indexOf(".");
               int var6 = var1.indexOf("(");

               int var7;
               for(var7 = var1.indexOf("."); var5 < var6 && var5 != -1; var5 = var1.indexOf(".", var5 + 1)) {
                  var7 = var5;
               }

               var4[0] = var1.substring(0, var7);
               var4[1] = var1.substring(var7 + 1);
               var2 = var4[0];
               var3 = var4[1];
            } else {
               var3 = var1;
            }

            return var3.trim().length() > 0 ? this.DoActual(var3, var2) : null;
         }
      }
   }

   protected BaseCommand DoActual(String var1, String var2) {
      if (var1.contains("Wait")) {
         var1 = var1;
      }

      String var3 = null;

      try {
         var3 = new String(var1.substring(0, var1.indexOf("(")));
      } catch (Exception var10) {
      }

      var1 = var1.replace(var3, "");
      var1 = var1.trim().substring(1);
      var1 = var1.trim().substring(0, var1.trim().lastIndexOf(")"));
      String[] var4 = ScriptParsingUtils.SplitExceptInbetween(var1, ",", "\"");

      for(int var5 = 0; var5 < var4.length; ++var5) {
         var4[var5] = new String(var4[var5].trim());
      }

      boolean var12 = false;
      if (var3.indexOf("!") == 0) {
         var3 = var3.replace("!", "");
         var12 = true;
      }

      BaseCommand var6 = CommandFactory.CreateCommand(var3);
      if (var6 == null) {
         JOptionPane.showMessageDialog((Component)null, "Command: " + var3 + " not found", "Error", 0);
      }

      var6.module = ScriptManager.instance.CurrentLoadingModule;

      try {
         if (var12) {
            var6.init("!", var4);
         } else if (var2 != null) {
            var6.init(new String(var2), var4);
         } else {
            var6.init((String)null, var4);
         }
      } catch (Exception var11) {
         String var8 = ": [";
         if (var4.length <= 0) {
            var8 = ".";
         } else {
            for(int var9 = 0; var9 < var4.length; ++var9) {
               var8 = var8 + var4[var9] + ", ";
            }

            var8 = var8.substring(0, var8.length() - 2) + "]";
         }

         JOptionPane.showMessageDialog((Component)null, "Command: " + var3 + " parameters incorrect" + var8, "Error", 0);
      }

      return var6;
   }

   public String[] DoScriptParsing(String var1, String var2) {
      Stack var3 = new Stack();
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;
      boolean var8 = false;
      int var14 = 0;
      int var11 = 0;
      int var12 = 0;
      int var13 = 0;
      if (var2.indexOf("}", var11) == -1) {
         var4 = true;
         this.Load(var1, var2.split(";"));
         return var2.split(";");
      } else {
         do {
            var11 = var2.indexOf("{", var11 + 1);
            var12 = var2.indexOf("}", var12 + 1);
            var13 = var2.indexOf(";", var13 + 1);
            if ((var13 < var11 || var11 == -1 && var13 != -1) && var14 == 0) {
               var3.add(var2.substring(0, var13));
               var2 = var2.substring(var13 + 1);
               var11 = 0;
               var12 = 0;
               var13 = 0;
            } else if ((var12 >= var11 || var12 == -1) && var11 != -1) {
               if (var11 != -1) {
                  var12 = var11;
                  ++var14;
               }
            } else {
               var11 = var12;
               --var14;
               if (var14 == 0) {
                  var3.add(var2.substring(0, var12 + 1));
                  var2 = var2.substring(var12 + 1);
                  var11 = 0;
                  var12 = 0;
                  var13 = 0;
               }
            }
         } while(var2.trim().length() > 0);

         String[] var9 = new String[var3.size()];

         for(int var10 = 0; var10 < var3.size(); ++var10) {
            var9[var10] = (String)var3.get(var10);
         }

         this.Load(var1, var9);
         return var9;
      }
   }

   public boolean AllowCharacterBehaviour(String var1, Script.ScriptInstance var2) {
      if (var2.CommandIndex >= this.CommandList.size()) {
         return true;
      } else {
         return var2.Paused ? true : ((BaseCommand)this.CommandList.get(var2.CommandIndex)).AllowCharacterBehaviour(var1);
      }
   }

   public static class ScriptInstance {
      public HashMap luaMap = new HashMap();
      public Script.ScriptInstance parent = null;
      public Script theScript;
      public int CommandIndex = 0;
      public boolean Paused = false;
      public HashMap CharacterAliases = new HashMap();
      public HashMap CharacterAliasesR = new HashMap();
      public String ID = "";
      public boolean CharactersAlreadyInScript = false;

      public void update() {
         this.theScript.update(this);
      }

      public void addPair(String var1, String var2) {
         this.luaMap.put(var1.toUpperCase(), var2);
      }

      public boolean HasAlias(String var1) {
         return this.CharacterAliases.containsKey(var1);
      }

      public IsoGameCharacter getAlias(String var1) {
         return (IsoGameCharacter)this.CharacterAliases.get(var1);
      }

      public boolean finished() {
         return this.theScript.finished(this);
      }

      public void begin() {
         this.theScript.begin(this);
      }

      public boolean AllowBehaviours(IsoGameCharacter var1) {
         return this.theScript.AllowCharacterBehaviour((String)this.CharacterAliasesR.get(var1), this);
      }

      public void CopyAliases(Script.ScriptInstance var1) {
         if (var1 != this) {
            this.parent = var1;
            Iterator var2 = var1.CharacterAliases.entrySet().iterator();
            this.CharacterAliases.clear();
            this.CharacterAliasesR.clear();
            this.luaMap = var1.luaMap;

            while(var2 != null && var2.hasNext()) {
               Entry var3 = (Entry)var2.next();
               this.CharacterAliases.put(var3.getKey(), var3.getValue());
               this.CharacterAliasesR.put(var3.getValue(), var3.getKey());
            }

         }
      }
   }
}
