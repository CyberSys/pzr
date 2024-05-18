package se.krka.kahlua.integration.expose.caller;

import se.krka.kahlua.integration.expose.ReturnValues;

public abstract class AbstractCaller implements Caller {
   protected final Class[] parameters;
   protected final boolean needsMultipleReturnValues;
   protected final Class varargType;

   protected AbstractCaller(Class[] var1) {
      boolean var2 = false;
      Class var3 = null;
      if (var1.length > 0) {
         Class var4 = var1[0];
         if (var4 == ReturnValues.class) {
            var2 = true;
         }

         Class var5 = var1[var1.length - 1];
         if (var5.isArray()) {
            var3 = var5.getComponentType();
         }
      }

      this.needsMultipleReturnValues = var2;
      this.varargType = var3;
      int var7 = var2 ? 1 : 0;
      int var8 = var1.length - (var3 == null ? 0 : 1);
      int var6 = var8 - var7;
      this.parameters = new Class[var6];
      System.arraycopy(var1, var7, this.parameters, 0, var6);
   }

   public final Class[] getParameterTypes() {
      return this.parameters;
   }

   public final Class getVarargType() {
      return this.varargType;
   }

   public final boolean hasVararg() {
      return this.varargType != null;
   }

   public final boolean needsMultipleReturnValues() {
      return this.needsMultipleReturnValues;
   }
}
