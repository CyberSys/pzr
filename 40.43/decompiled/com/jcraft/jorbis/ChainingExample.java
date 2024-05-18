package com.jcraft.jorbis;

import zombie.debug.DebugLog;

class ChainingExample {
   public static void main(String[] var0) {
      VorbisFile var1 = null;

      try {
         if (var0.length > 0) {
            var1 = new VorbisFile(var0[0]);
         } else {
            var1 = new VorbisFile(System.in, (byte[])null, -1);
         }
      } catch (Exception var5) {
         System.err.println(var5);
         return;
      }

      if (var1.seekable()) {
         DebugLog.log("Input bitstream contained " + var1.streams() + " logical bitstream section(s).");
         DebugLog.log("Total bitstream playing time: " + var1.time_total(-1) + " seconds\n");
      } else {
         DebugLog.log("Standard input was not seekable.");
         DebugLog.log("First logical bitstream information:\n");
      }

      for(int var2 = 0; var2 < var1.streams(); ++var2) {
         Info var3 = var1.getInfo(var2);
         DebugLog.log("\tlogical bitstream section " + (var2 + 1) + " information:");
         DebugLog.log("\t\t" + var3.rate + "Hz " + var3.channels + " channels bitrate " + var1.bitrate(var2) / 1000 + "kbps serial number=" + var1.serialnumber(var2));
         System.out.print("\t\tcompressed length: " + var1.raw_total(var2) + " bytes ");
         DebugLog.log(" play time: " + var1.time_total(var2) + "s");
         Comment var4 = var1.getComment(var2);
         DebugLog.log((Object)var4);
      }

   }
}
