package zombie.Lua;

import java.util.Stack;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import zombie.debug.DebugLog;

public class Event {
   public static final int ADD = 0;
   public static final int NUM_FUNCTIONS = 1;
   private final Event.Add add;
   private final Event.Remove remove;
   public Stack callbacks = new Stack();
   public String name;
   private int index = 0;

   public boolean trigger(KahluaTable var1, LuaCaller var2, Object[] var3) {
      for(int var4 = 0; var4 < this.callbacks.size(); ++var4) {
         try {
            var2.protectedCallVoid(LuaManager.thread, this.callbacks.get(var4), var3);
         } catch (RuntimeException var6) {
            DebugLog.log(var6.getMessage());
         }
      }

      return !this.callbacks.isEmpty();
   }

   public Event(String var1, int var2) {
      this.index = var2;
      this.name = var1;
      this.add = new Event.Add(this);
      this.remove = new Event.Remove(this);
   }

   public void register(Platform var1, KahluaTable var2) {
      KahluaTable var3 = var1.newTable();
      var3.rawset("Add", this.add);
      var3.rawset("Remove", this.remove);
      var2.rawset(this.name, var3);
   }

   public class Remove implements JavaFunction {
      Event e;

      public Remove(Event var2) {
         this.e = var2;
      }

      public int call(LuaCallFrame var1, int var2) {
         if (LuaCompiler.rewriteEvents) {
            return 0;
         } else {
            Object var3 = var1.get(0);
            if (var3 instanceof LuaClosure) {
               LuaClosure var4 = (LuaClosure)var3;
               this.e.callbacks.remove(var4);
            }

            return 0;
         }
      }
   }

   public class Add implements JavaFunction {
      Event e;

      public Add(Event var2) {
         this.e = var2;
      }

      public int call(LuaCallFrame var1, int var2) {
         if (LuaCompiler.rewriteEvents) {
            return 0;
         } else {
            Object var3 = var1.get(0);
            if (this.e.name.contains("CreateUI")) {
               boolean var4 = false;
            }

            if (var3 instanceof LuaClosure) {
               LuaClosure var5 = (LuaClosure)var3;
               this.e.callbacks.add(var5);
            }

            return 0;
         }
      }
   }
}
