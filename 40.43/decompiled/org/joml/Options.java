package org.joml;

class Options {
   static final boolean DEBUG = hasOption("joml.debug");
   static final boolean NO_UNSAFE = hasOption("joml.nounsafe");
   static final boolean FASTMATH = hasOption("joml.fastmath");
   static final boolean SIN_LOOKUP = hasOption("joml.sinLookup");
   static final int SIN_LOOKUP_BITS = Integer.parseInt(System.getProperty("joml.sinLookup.bits", "14"));

   static boolean hasOption(String var0) {
      String var1 = System.getProperty(var0);
      if (var1 == null) {
         return false;
      } else {
         return var1.trim().length() == 0 ? true : Boolean.valueOf(var1);
      }
   }
}
