package zombie.radio.scripting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;

public class RadioScript {
   private ArrayList broadcasts;
   private ArrayList exitOptions;
   private String GUID;
   private String name;
   private int startDay;
   private int startDayStamp;
   private int loopMin;
   private int loopMax;
   private int internalStamp;
   private RadioBroadCast currentBroadcast;
   private boolean currentHasAired;

   public RadioScript(String var1, int var2, int var3) {
      this(var1, var2, var3, UUID.randomUUID().toString());
   }

   public RadioScript(String var1, int var2, int var3, String var4) {
      this.broadcasts = new ArrayList();
      this.exitOptions = new ArrayList();
      this.name = "Unnamed radioscript";
      this.startDay = 0;
      this.startDayStamp = 0;
      this.loopMin = 1;
      this.loopMax = 1;
      this.internalStamp = 0;
      this.currentBroadcast = null;
      this.currentHasAired = false;
      this.name = var1;
      this.loopMin = var2;
      this.loopMax = var3;
      this.GUID = var4;
   }

   public String GetGUID() {
      return this.GUID;
   }

   public String GetName() {
      return this.name;
   }

   public int getStartDayStamp() {
      return this.startDayStamp;
   }

   public int getStartDay() {
      return this.startDay;
   }

   public int getLoopMin() {
      return this.loopMin;
   }

   public int getLoopMax() {
      return this.loopMax;
   }

   public RadioBroadCast getCurrentBroadcast() {
      return this.currentBroadcast;
   }

   public ArrayList getBroadcastList() {
      return this.broadcasts;
   }

   public void clearExitOptions() {
      this.exitOptions.clear();
   }

   public void setStartDayStamp(int var1) {
      this.startDay = var1;
      this.startDayStamp = var1 * 24 * 60;
   }

   public RadioBroadCast getValidAirBroadcast() {
      if (!this.currentHasAired && this.currentBroadcast != null && this.internalStamp >= this.currentBroadcast.getStartStamp() && this.internalStamp < this.currentBroadcast.getEndStamp()) {
         this.currentHasAired = true;
         return this.currentBroadcast;
      } else {
         return null;
      }
   }

   public void Reset() {
      this.currentBroadcast = null;
      this.currentHasAired = false;
   }

   private RadioBroadCast getNextBroadcast() {
      if (this.currentBroadcast != null && this.currentBroadcast.getEndStamp() > this.internalStamp) {
         return this.currentBroadcast;
      } else {
         Iterator var1 = this.broadcasts.iterator();

         RadioBroadCast var2;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            var2 = (RadioBroadCast)var1.next();
         } while(var2.getEndStamp() <= this.internalStamp);

         this.currentHasAired = false;
         return var2;
      }
   }

   public RadioBroadCast getBroadcastWithID(String var1) {
      Iterator var2 = this.broadcasts.iterator();

      RadioBroadCast var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (RadioBroadCast)var2.next();
      } while(!var3.getID().equals(var1));

      this.currentBroadcast = var3;
      this.currentHasAired = true;
      return var3;
   }

   public boolean UpdateScript(int var1) {
      this.internalStamp = var1 - this.startDayStamp;
      this.currentBroadcast = this.getNextBroadcast();
      return this.currentBroadcast != null;
   }

   public RadioScript.ExitOption getNextScript() {
      int var1 = 0;
      int var2 = Rand.Next(100);

      RadioScript.ExitOption var4;
      for(Iterator var3 = this.exitOptions.iterator(); var3.hasNext(); var1 += var4.getChance()) {
         var4 = (RadioScript.ExitOption)var3.next();
         if (var2 >= var1 && var2 < var1 + var4.getChance()) {
            return var4;
         }
      }

      return null;
   }

   public void AddBroadcast(RadioBroadCast var1) {
      this.AddBroadcast(var1, false);
   }

   public void AddBroadcast(RadioBroadCast var1, boolean var2) {
      boolean var3 = false;
      if (var1 != null && var1.getID() != null) {
         if (var2) {
            this.broadcasts.add(var1);
            var3 = true;
         } else if (var1.getStartStamp() >= 0 && var1.getEndStamp() > var1.getStartStamp()) {
            if (this.broadcasts.size() != 0 && ((RadioBroadCast)this.broadcasts.get(this.broadcasts.size() - 1)).getEndStamp() > var1.getStartStamp()) {
               if (this.broadcasts.size() > 0) {
                  DebugLog.log(DebugType.Radio, "startstamp = '" + var1.getStartStamp() + "', endstamp = '" + var1.getEndStamp() + "', previous endstamp = '" + ((RadioBroadCast)this.broadcasts.get(this.broadcasts.size() - 1)).getEndStamp() + "'.");
               }
            } else {
               this.broadcasts.add(var1);
               var3 = true;
            }
         } else {
            DebugLog.log(DebugType.Radio, "startstamp = '" + var1.getStartStamp() + "', endstamp = '" + var1.getEndStamp() + "'.");
         }
      }

      if (!var3) {
         String var4 = var1 != null ? var1.getID() : "null";
         DebugLog.log(DebugType.Radio, "Error cannot add broadcast ID: '" + var4 + "' to script '" + this.name + "', null or timestamp error");
      }

   }

   public void AddExitOption(String var1, int var2, int var3) {
      int var4 = var2;

      RadioScript.ExitOption var6;
      for(Iterator var5 = this.exitOptions.iterator(); var5.hasNext(); var4 += var6.getChance()) {
         var6 = (RadioScript.ExitOption)var5.next();
      }

      if (var4 <= 100) {
         this.exitOptions.add(new RadioScript.ExitOption(var1, var2, var3));
      } else {
         DebugLog.log(DebugType.Radio, "Error cannot add exitoption with scriptname '" + var1 + "' to script '" + this.name + "', total chance exceeding 100");
      }

   }

   public class ExitOption {
      private String scriptname = "";
      private int chance = 0;
      private int startDelay = 0;

      public ExitOption(String var2, int var3, int var4) {
         this.scriptname = var2;
         this.chance = var3;
         this.startDelay = var4;
      }

      public String getScriptname() {
         return this.scriptname;
      }

      public int getChance() {
         return this.chance;
      }

      public int getStartDelay() {
         return this.startDelay;
      }
   }
}
