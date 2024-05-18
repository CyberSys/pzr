package zombie.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameSounds;
import zombie.GameWindow;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoGameCharacter;
import zombie.core.Core;
import zombie.core.IndieFileLoader;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.RecipeManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.MultiStageBuilding;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetChecksum;
import zombie.scripting.objects.ContainerDistribution;
import zombie.scripting.objects.EvolvedRecipe;
import zombie.scripting.objects.Fixing;
import zombie.scripting.objects.FloorDistribution;
import zombie.scripting.objects.Inventory;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.LanguageDefinition;
import zombie.scripting.objects.QuestTaskCondition;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.Room;
import zombie.scripting.objects.Script;
import zombie.scripting.objects.ScriptActivatable;
import zombie.scripting.objects.ScriptCharacter;
import zombie.scripting.objects.ScriptContainer;
import zombie.scripting.objects.ScriptFlag;
import zombie.scripting.objects.ScriptModule;
import zombie.scripting.objects.ScriptTalker;
import zombie.scripting.objects.ShelfDistribution;
import zombie.scripting.objects.Trigger;
import zombie.scripting.objects.UniqueRecipe;
import zombie.scripting.objects.VehicleScript;
import zombie.scripting.objects.VehicleTemplate;
import zombie.scripting.objects.Waypoint;
import zombie.scripting.objects.Zone;

public class ScriptManager implements IScriptObjectStore {
   public static ScriptManager instance = new ScriptManager();
   public HashMap TriggerMap = new HashMap();
   public HashMap CustomTriggerMap = new HashMap();
   public HashMap CustomTriggerLastRan = new HashMap();
   public HashMap HookMap = new HashMap();
   public HashMap ModuleMap = new HashMap();
   public final HashMap FullTypeToItemMap = new HashMap();
   public Stack PlayingScripts = new Stack();
   public ScriptModule CurrentLoadingModule = null;
   public HashMap ModuleAliases = new HashMap();
   public boolean skipping = false;
   public HashMap MapMap = new HashMap();
   ArrayList toStop = new ArrayList();
   ArrayList toStopInstance = new ArrayList();
   StringBuffer buf = new StringBuffer();
   StringBuffer buf2 = new StringBuffer();
   HashMap CachedModules = new HashMap();
   Stack recipesTempList = new Stack();
   Stack evolvedRecipesTempList = new Stack();
   Stack uniqueRecipesTempList = new Stack();
   ArrayList fixingTempList = null;
   ArrayList itemTempList = null;
   final ArrayList vehicleScriptTempList = new ArrayList();
   Stack zoneTempList = new Stack();
   Stack conTempList = new Stack();
   Stack floorTempList = new Stack();
   Stack shelfTempList = new Stack();
   static StringBuilder builder = new StringBuilder();
   static String Base = "Base";
   private String checksum = "";

   public void AddOneTime(String var1, String var2) {
      var1 = var1.toLowerCase();
      Stack var3 = null;
      if (this.HookMap.containsKey(var1)) {
         var3 = (Stack)this.HookMap.get(var1);
      } else {
         var3 = new Stack();
         this.HookMap.put(var1, var3);
      }

      var3.add(var2);
   }

   public void FireHook(String var1) {
      if (this.HookMap.containsKey(var1)) {
         Iterator var2 = ((Stack)this.HookMap.get(var1)).iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            this.PlayScript(var3);
         }

         ((Stack)this.HookMap.get(var1)).clear();
      }

   }

   /** @deprecated */
   @Deprecated
   public void ParseScriptPP(String var1) {
      boolean var2 = false;
      Stack var3 = new Stack();
      boolean var4 = false;
      boolean var5 = false;

      int var10;
      for(boolean var6 = false; !var2; var1 = var1.substring(var10 + 1)) {
         int var9 = 0;
         var10 = 0;
         int var11 = 0;
         if (var1.indexOf("}", var10 + 1) == -1) {
            var2 = true;
            break;
         }

         do {
            var10 = var1.indexOf("{", var10 + 1);
            var11 = var1.indexOf("}", var11 + 1);
            if ((var11 >= var10 || var11 == -1) && var10 != -1) {
               if (var10 != -1) {
                  var11 = var10;
                  ++var9;
               }
            } else {
               var10 = var11;
               --var9;
            }
         } while(var9 > 0);

         var3.add(var1.substring(0, var10 + 1));
      }

      if (var1.trim().length() > 0) {
         var3.add(var1.trim());
      }

      for(int var7 = 0; var7 < var3.size(); ++var7) {
         String var8 = (String)var3.get(var7);
         this.CreateFromTokenPP(var8);
      }

   }

   public void ParseScript(String var1) {
      boolean var2 = false;
      Stack var3 = new Stack();
      boolean var4 = false;
      boolean var5 = false;

      int var10;
      for(boolean var6 = false; !var2; var1 = var1.substring(var10 + 1)) {
         int var9 = 0;
         var10 = 0;
         int var11 = 0;
         if (var1.indexOf("}", var10 + 1) == -1) {
            var2 = true;
            break;
         }

         do {
            var10 = var1.indexOf("{", var10 + 1);
            var11 = var1.indexOf("}", var11 + 1);
            if ((var11 >= var10 || var11 == -1) && var10 != -1) {
               if (var10 != -1) {
                  var11 = var10;
                  ++var9;
               }
            } else {
               var10 = var11;
               --var9;
            }
         } while(var9 > 0);

         var3.add(var1.substring(0, var10 + 1));
      }

      if (var1.trim().length() > 0) {
         var3.add(var1.trim());
      }

      for(int var7 = 0; var7 < var3.size(); ++var7) {
         String var8 = (String)var3.get(var7);
         this.CreateFromToken(var8);
      }

   }

   public void StopScript(String var1) {
      this.toStop.add(var1);
   }

   public void PlayInstanceScript(String var1, String var2, String var3, IsoGameCharacter var4) {
      for(int var5 = 0; var5 < this.PlayingScripts.size(); ++var5) {
         if (var1 != null && var1.equals(((Script.ScriptInstance)this.PlayingScripts.get(var5)).ID)) {
            return;
         }
      }

      Script.ScriptInstance var7 = new Script.ScriptInstance();
      var7.ID = var1;
      Script var6 = this.getScript(var2);
      var7.theScript = var6;
      var7.CharacterAliases.put(var3, var4);
      var7.CharacterAliasesR.put(var4, var3);
      var4.getActiveInInstances().add(var7);
      instance.PlayingScripts.add(var7);
      var7.begin();
   }

   public Script.ScriptInstance PlayInstanceScript(String var1, String var2, KahluaTable var3) {
      KahluaTableIterator var4 = var3.iterator();
      HashMap var5 = new HashMap();

      while(var4.advance()) {
         String var6 = (String)var4.getKey();
         IsoGameCharacter var7 = (IsoGameCharacter)var4.getValue();
         var5.put(var6, var7);
      }

      return this.PlayInstanceScript(var1, var2, var5);
   }

   public Script.ScriptInstance PlayInstanceScript(String var1, String var2, KahluaTable var3, KahluaTable var4) {
      KahluaTableIterator var5 = var3.iterator();
      HashMap var6 = new HashMap();

      while(var5.advance()) {
         String var7 = (String)var5.getKey();
         IsoGameCharacter var8 = (IsoGameCharacter)var5.getValue();
         var6.put(var7, var8);
      }

      HashMap var10 = new HashMap();
      var5 = var4.iterator();

      while(var5.advance()) {
         String var11 = (String)var5.getKey();
         String var9 = (String)var5.getValue();
         var10.put(var11, var9);
      }

      return this.PlayInstanceScript(var1, var2, var6, var10);
   }

   public Script.ScriptInstance PlayInstanceScript(String var1, String var2, HashMap var3) {
      return this.PlayInstanceScript(var1, var2, (HashMap)var3, (HashMap)null);
   }

   public Script.ScriptInstance PlayInstanceScript(String var1, String var2, HashMap var3, HashMap var4) {
      for(int var5 = 0; var5 < this.PlayingScripts.size(); ++var5) {
         if (var1 != null && var1.equals(((Script.ScriptInstance)this.PlayingScripts.get(var5)).ID)) {
            return null;
         }
      }

      Script.ScriptInstance var13 = new Script.ScriptInstance();
      var13.ID = var1;
      Script var6 = this.getScript(var2);
      var13.theScript = var6;
      Iterator var7 = var3.entrySet().iterator();

      while(var7 != null && var7.hasNext()) {
         Entry var8 = (Entry)var7.next();
         String var9 = (String)var8.getKey();
         IsoGameCharacter var10 = (IsoGameCharacter)var8.getValue();
         var13.CharacterAliases.put(var9, var10);
         var13.CharacterAliasesR.put(var10, var9);
         var10.getActiveInInstances().add(var13);
      }

      Iterator var14 = var4.entrySet().iterator();

      while(var14 != null && var14.hasNext()) {
         Entry var15 = (Entry)var14.next();
         String var12 = (String)var15.getKey();
         String var11 = (String)var15.getValue();
         var13.addPair(var12, var11);
      }

      instance.PlayingScripts.add(var13);
      var13.begin();
      return var13;
   }

   public void PlayInstanceScript(String var1, String var2, String var3, IsoGameCharacter var4, String var5, IsoGameCharacter var6) {
      Script.ScriptInstance var7 = new Script.ScriptInstance();
      var7.ID = var1;
      Script var8 = this.getScript(var2);
      var7.theScript = var8;
      var7.CharacterAliases.put(var3, var4);
      var7.CharacterAliasesR.put(var4, var3);
      var7.CharacterAliases.put(var5, var6);
      var7.CharacterAliasesR.put(var6, var5);

      for(int var9 = 0; var9 < this.PlayingScripts.size(); ++var9) {
         if (var1 != null && var1.equals(((Script.ScriptInstance)this.PlayingScripts.get(var9)).ID)) {
            var7.CharactersAlreadyInScript = true;
         }
      }

      var4.getActiveInInstances().add(var7);
      var6.getActiveInInstances().add(var7);
      instance.PlayingScripts.add(var7);
      var7.begin();
   }

   public void PlayInstanceScript(String var1, String var2, String var3, IsoGameCharacter var4, String var5, IsoGameCharacter var6, String var7, IsoGameCharacter var8) {
      for(int var9 = 0; var9 < this.PlayingScripts.size(); ++var9) {
         if (var1 != null && var1.equals(((Script.ScriptInstance)this.PlayingScripts.get(var9)).ID)) {
            return;
         }
      }

      Script.ScriptInstance var11 = new Script.ScriptInstance();
      var11.ID = var1;
      Script var10 = this.getScript(var2);
      var11.theScript = var10;
      var11.CharacterAliases.put(var3, var4);
      var11.CharacterAliasesR.put(var4, var3);
      var11.CharacterAliases.put(var5, var6);
      var11.CharacterAliasesR.put(var6, var5);
      var11.CharacterAliases.put(var7, var8);
      var11.CharacterAliasesR.put(var8, var7);
      var4.getActiveInInstances().add(var11);
      var6.getActiveInInstances().add(var11);
      var8.getActiveInInstances().add(var11);
      instance.PlayingScripts.add(var11);
      var11.begin();
   }

   public void PlayScript(String var1) {
      Script var2 = this.getScript(var1);
      if (var2 != null) {
         for(int var3 = 0; var3 < this.PlayingScripts.size(); ++var3) {
            if (((Script.ScriptInstance)this.PlayingScripts.get(var3)).theScript == var2 && !((Script.ScriptInstance)this.PlayingScripts.get(var3)).theScript.Instancable) {
               this.PlayingScripts.remove(var3);
               --var3;
            }
         }

         var2.module.PlayScript(var2.name);
      }

   }

   public Script.ScriptInstance PlayScript(String var1, Script.ScriptInstance var2) {
      Script var3 = this.getScript(var1);
      if (var3 != null) {
         for(int var4 = 0; var4 < this.PlayingScripts.size(); ++var4) {
            if (((Script.ScriptInstance)this.PlayingScripts.get(var4)).theScript == var3 && !((Script.ScriptInstance)this.PlayingScripts.get(var4)).theScript.Instancable) {
               this.PlayingScripts.remove(var4);
               --var4;
            }
         }

         return var3.module.PlayScript(var3.name, var2);
      } else {
         return null;
      }
   }

   public void update() {
      assert this.toStopInstance.isEmpty();

      assert this.toStop.isEmpty();

      assert this.CustomTriggerMap.isEmpty();

      assert this.CustomTriggerLastRan.isEmpty();

      assert this.PlayingScripts.isEmpty();

   }

   public void LoadFile(String var1, boolean var2) throws FileNotFoundException {
      if (!GameServer.bServer) {
         Thread.yield();
         Core.getInstance().DoFrameReady();
      }

      var1 = ZomboidFileSystem.instance.getString(var1);
      if (var1.contains(".tmx")) {
         IsoWorld.mapPath = var1.substring(0, var1.lastIndexOf("/"));
         IsoWorld.mapUseJar = var2;
      } else if (var1.contains(".txt")) {
         DebugLog.log("script: loading " + var1);
         InputStreamReader var3 = IndieFileLoader.getStreamReader(ZomboidFileSystem.instance.getString(var1), !var2);
         BufferedReader var4 = new BufferedReader(var3);
         this.buf.setLength(0);
         this.buf2.setLength(0);
         String var5 = null;
         String var6 = "";

         label170: {
            try {
               while(true) {
                  if ((var5 = var4.readLine()) == null) {
                     break label170;
                  }

                  if (var5 != null) {
                     IsoGridSquare.Checksum += (long)var5.hashCode();
                     this.buf.append(var5);
                  }
               }
            } catch (Exception var18) {
            } finally {
               try {
                  var4.close();
                  var3.close();
               } catch (Exception var17) {
                  var17.printStackTrace();
               }

            }

            return;
         }

         var6 = this.buf.toString();
         this.buf2.setLength(0);
         this.buf2.append(var6);

         int var8;
         for(int var7 = this.buf2.lastIndexOf("*/"); var7 != -1; var7 = this.buf2.lastIndexOf("*/", var8)) {
            var8 = this.buf2.lastIndexOf("/*", var7 - 1);
            if (var8 == -1) {
               break;
            }

            int var10;
            for(int var9 = this.buf2.lastIndexOf("*/", var7 - 1); var9 > var8; var9 = this.buf2.lastIndexOf("*/", var10 - 2)) {
               var10 = var8;
               this.buf2.substring(var8, var9 + 2);
               var8 = this.buf2.lastIndexOf("/*", var8 - 2);
               if (var8 == -1) {
                  break;
               }
            }

            if (var8 == -1) {
               break;
            }

            this.buf2.substring(var8, var7 + 2);
            this.buf2.replace(var8, var7 + 2, "");
         }

         var6 = this.buf2.toString();
         this.ParseScript(var6);
      }
   }

   /** @deprecated */
   @Deprecated
   public void LoadFilePP(String var1, boolean var2) throws FileNotFoundException, UnsupportedEncodingException {
      if (var1.contains(".tmx")) {
         IsoWorld.mapPath = var1.substring(0, var1.lastIndexOf("/"));
         IsoWorld.mapUseJar = var2;
      } else if (var1.contains(".txt")) {
         InputStreamReader var3 = IndieFileLoader.getStreamReader(var1, !var2);
         BufferedReader var4 = new BufferedReader(var3);
         this.buf.setLength(0);
         this.buf2.setLength(0);
         String var5 = null;
         String var6 = "";

         label127: {
            try {
               while(true) {
                  if ((var5 = var4.readLine()) == null) {
                     break label127;
                  }

                  if (var5 != null) {
                     this.buf.append(var5);
                  }
               }
            } catch (Exception var19) {
            } finally {
               try {
                  var4.close();
                  var3.close();
               } catch (Exception var17) {
                  var17.printStackTrace();
               }

            }

            return;
         }

         var6 = this.buf.toString();

         try {
            while(var6.contains("*/")) {
               int var7 = var6.indexOf("/*");
               int var8 = var6.indexOf("*/");
               this.buf2.setLength(0);
               this.buf2.append(var6.substring(0, var7));
               this.buf2.append("\n");
               this.buf2.append(var6.substring(var8 + 2));
               var6 = this.buf2.toString();
            }
         } catch (Exception var18) {
            Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, (String)null, var18);
         }

         this.ParseScriptPP(var6);
      }
   }

   private void CreateFromToken(String var1) {
      var1 = var1.trim();
      IsoGridSquare.Checksum += (long)var1.hashCode();
      if (var1.indexOf("module") == 0) {
         int var2 = var1.indexOf("{");
         int var3 = var1.lastIndexOf("}");
         String[] var4 = var1.split("[{}]");
         String var5 = var4[0];
         var5 = var5.replace("module", "");
         var5 = var5.trim();
         String var6 = var1.substring(var2 + 1, var3);
         if (!this.ModuleMap.containsKey(var5)) {
            this.ModuleMap.put(var5, new ScriptModule());
         }

         ScriptModule var7 = (ScriptModule)this.ModuleMap.get(var5);
         var7.Load(var5, var6);
      }

   }

   private void CreateFromTokenPP(String var1) {
      var1 = var1.trim();
      IsoGridSquare.Checksum += (long)var1.hashCode();
      if (var1.indexOf("module") == 0) {
         String[] var2 = var1.split("[{}]");
         String var3 = var2[0];
         var3 = var3.replace("module", "");
         var3 = var3.trim();
         ScriptModule var4 = new ScriptModule();
         this.ModuleMap.put(var3, var4);
      }

   }

   public void LoadStory(String var1) throws IOException, URISyntaxException {
      try {
         Enumeration var2 = GameWindow.class.getClassLoader().getResources("stories/" + var1 + "/");
         if (var2.hasMoreElements()) {
            URL var3 = (URL)var2.nextElement();
            File var4 = new File(var3.toURI());
            String[] var5 = var4.list();
            String[] var6 = var5;
            int var7 = var5.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               String var9 = var6[var8];
               this.LoadFile("stories/" + var1 + "/" + var9, true);
            }
         }
      } catch (IOException var10) {
         Logger.getLogger(ScriptManager.class.getName()).log(Level.SEVERE, (String)null, var10);
      }

   }

   public Stack getStoryList() throws IOException, URISyntaxException {
      Stack var1 = new Stack();
      File var2 = new File("mods/stories/");
      String[] var3 = var2.list();
      var1.addAll(Arrays.asList(var3));
      var2 = new File("media/stories/");
      var3 = var2.list();
      var1.addAll(Arrays.asList(var3));
      return var1;
   }

   public void searchFolders(URI var1, File var2, ArrayList var3) {
      if (var2.isDirectory()) {
         String[] var4 = var2.list();

         for(int var5 = 0; var5 < var4.length; ++var5) {
            this.searchFolders(var1, new File(var2.getAbsolutePath() + File.separator + var4[var5]), var3);
         }
      } else if (var2.getAbsolutePath().toLowerCase().contains(".txt")) {
         var3.add(ZomboidFileSystem.instance.getRelativeFile(var1, var2.getAbsolutePath()));
      } else if (var2.getAbsolutePath().toLowerCase().contains(".lot")) {
         String var6 = var2.getAbsolutePath().substring(var2.getAbsolutePath().lastIndexOf("\\") + 1);
         var6 = var6.substring(0, var6.lastIndexOf("."));
         this.MapMap.put(var6, var2.getAbsolutePath());
      }

   }

   public static String getItemName(String var0) {
      return !var0.contains(".") ? var0 : var0.split("\\.")[1];
   }

   public void FillInventory(IsoGameCharacter var1, ItemContainer var2, String var3) {
      Inventory var4 = null;
      String var5 = var1.getScriptModule();
      if (var3.contains(".")) {
         var4 = this.getInventory(var3);
         var5 = var3.split("\\.")[0];
      } else {
         var4 = this.getInventory(var1.getScriptModule() + "." + var3);
      }

      if (var4 != null) {
         for(int var6 = 0; var6 < var4.Items.size(); ++var6) {
            if (((Inventory.Source)var4.Items.get(var6)).type.trim().length() > 0) {
               int var7 = ((Inventory.Source)var4.Items.get(var6)).count;

               for(int var8 = 0; var8 < var7; ++var8) {
                  InventoryItem var9 = InventoryItemFactory.CreateItem(var5 + "." + ((Inventory.Source)var4.Items.get(var6)).type);
                  var2.AddItem(var9);
               }
            }
         }

      }
   }

   public void Trigger(String var1) {
      var1 = var1.toLowerCase();
      this.FireHook(var1);
      if (this.TriggerMap.containsKey(var1)) {
         Stack var2 = (Stack)this.TriggerMap.get(var1);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            if (!((Trigger)var2.get(var3)).module.disabled && !((Trigger)var2.get(var3)).Locked) {
               ((Trigger)var2.get(var3)).TriggerParam = null;
               ((Trigger)var2.get(var3)).TriggerParam2 = null;
               ((Trigger)var2.get(var3)).TriggerParam3 = null;
               ((Trigger)var2.get(var3)).Process();
            }
         }
      }

   }

   public void Trigger(String var1, String var2) {
      var1 = var1.toLowerCase();
      this.FireHook(var1);
      if (this.TriggerMap.containsKey(var1)) {
         Stack var3 = (Stack)this.TriggerMap.get(var1);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            if (!((Trigger)var3.get(var4)).module.disabled && !((Trigger)var3.get(var4)).Locked) {
               ((Trigger)var3.get(var4)).TriggerParam = var2;
               ((Trigger)var3.get(var4)).Process();
            }
         }
      }

   }

   public void Trigger(String var1, String var2, String var3) {
      var1 = var1.toLowerCase();
      this.FireHook(var1);
      if (this.TriggerMap.containsKey(var1)) {
         Stack var4 = (Stack)this.TriggerMap.get(var1);

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            if (!((Trigger)var4.get(var5)).module.disabled && !((Trigger)var4.get(var5)).Locked) {
               ((Trigger)var4.get(var5)).TriggerParam = var2;
               ((Trigger)var4.get(var5)).TriggerParam2 = var3;
               ((Trigger)var4.get(var5)).Process();
            }
         }
      }

   }

   public void Trigger(String var1, String var2, String var3, String var4) {
      var1 = var1.toLowerCase();
      this.FireHook(var1);
      if (this.TriggerMap.containsKey(var1)) {
         Stack var5 = (Stack)this.TriggerMap.get(var1);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            if (!((Trigger)var5.get(var6)).module.disabled && !((Trigger)var5.get(var6)).Locked) {
               ((Trigger)var5.get(var6)).TriggerParam = var2;
               ((Trigger)var5.get(var6)).TriggerParam2 = var3;
               ((Trigger)var5.get(var6)).TriggerParam2 = var4;
               ((Trigger)var5.get(var6)).Process();
            }
         }
      }

   }

   public boolean IsScriptPlaying(String var1) {
      for(int var2 = 0; var2 < this.PlayingScripts.size(); ++var2) {
         if (((Script.ScriptInstance)this.PlayingScripts.get(var2)).theScript.name.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean IsScriptPlaying(Script.ScriptInstance var1) {
      for(int var2 = 0; var2 < this.PlayingScripts.size(); ++var2) {
         if (this.PlayingScripts.get(var2) == var1) {
            return true;
         }
      }

      return false;
   }

   public void PauseScript(String var1) {
      for(int var2 = 0; var2 < this.PlayingScripts.size(); ++var2) {
         if (((Script.ScriptInstance)this.PlayingScripts.get(var2)).theScript.name.equals(var1)) {
            ((Script.ScriptInstance)this.PlayingScripts.get(var2)).Paused = true;
         }
      }

   }

   public void UnPauseScript(String var1) {
      for(int var2 = 0; var2 < this.PlayingScripts.size(); ++var2) {
         if (((Script.ScriptInstance)this.PlayingScripts.get(var2)).theScript.name.equals(var1)) {
            ((Script.ScriptInstance)this.PlayingScripts.get(var2)).Paused = false;
         }
      }

   }

   public ScriptModule getModule(String var1) {
      if (var1.startsWith(Base)) {
         return (ScriptModule)this.ModuleMap.get(Base);
      } else if (this.CachedModules.containsKey(var1)) {
         return (ScriptModule)this.CachedModules.get(var1);
      } else {
         ScriptModule var2 = null;
         if (this.ModuleAliases.containsKey(var1)) {
            var1 = (String)this.ModuleAliases.get(var1);
         }

         if (this.CachedModules.containsKey(var1)) {
            return (ScriptModule)this.CachedModules.get(var1);
         } else {
            if (this.ModuleMap.containsKey(var1)) {
               if (((ScriptModule)this.ModuleMap.get(var1)).disabled) {
                  var2 = null;
               } else {
                  var2 = (ScriptModule)this.ModuleMap.get(var1);
               }
            }

            if (var2 != null) {
               this.CachedModules.put(var1, var2);
               return var2;
            } else {
               int var3 = var1.indexOf(".");
               if (var3 != -1) {
                  var2 = this.getModule(var1.substring(0, var3));
               }

               if (var2 != null) {
                  this.CachedModules.put(var1, var2);
                  return var2;
               } else {
                  return (ScriptModule)this.ModuleMap.get(Base);
               }
            }
         }
      }
   }

   public ScriptModule getModuleNoDisableCheck(String var1) {
      if (this.ModuleAliases.containsKey(var1)) {
         var1 = (String)this.ModuleAliases.get(var1);
      }

      if (this.ModuleMap.containsKey(var1)) {
         return (ScriptModule)this.ModuleMap.get(var1);
      } else {
         return var1.indexOf(".") != -1 ? this.getModule(var1.split("\\.")[0]) : null;
      }
   }

   public Inventory getInventory(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : (Inventory)var2.InventoryMap.get(getItemName(var1));
   }

   public ScriptCharacter getCharacter(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getCharacter(getItemName(var1));
   }

   public ScriptCharacter FindCharacter(String var1) {
      Iterator var2 = this.ModuleMap.values().iterator();

      while(var2 != null && var2.hasNext()) {
         ScriptModule var3 = (ScriptModule)var2.next();
         if (!var3.disabled && var3.CharacterMap.containsKey(var1)) {
            return var3.getCharacter(var1);
         }
      }

      return null;
   }

   public IsoGameCharacter getCharacterActual(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getCharacterActual(getItemName(var1));
   }

   public int getFlagIntValue(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? 0 : var2.getFlagIntValue(getItemName(var1));
   }

   public String getFlagValue(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? "" : var2.getFlagValue(getItemName(var1));
   }

   public Waypoint getWaypoint(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getWaypoint(getItemName(var1));
   }

   public ScriptContainer getScriptContainer(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getScriptContainer(getItemName(var1));
   }

   public Room getRoom(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getRoom(getItemName(var1));
   }

   public LanguageDefinition getLanguageDef(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getLanguageDef(getItemName(var1));
   }

   public String getLanguage(String var1) {
      if (!var1.contains("@")) {
         return var1;
      } else {
         String[] var2 = var1.split("-");
         LanguageDefinition var3 = this.getLanguageDef(var2[0]);
         return var3.get(Integer.parseInt(var2[1]));
      }
   }

   public ScriptTalker getTalker(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getTalker(getItemName(var1));
   }

   public ScriptActivatable getActivatable(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getActivatable(getItemName(var1));
   }

   public ScriptFlag getFlag(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getFlag(getItemName(var1));
   }

   public Zone getZone(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getZone(getItemName(var1));
   }

   public QuestTaskCondition getQuestCondition(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getQuestCondition(getItemName(var1));
   }

   public Item getItem(String var1) {
      if (var1.contains(".") && this.FullTypeToItemMap.containsKey(var1)) {
         return (Item)this.FullTypeToItemMap.get(var1);
      } else {
         ScriptModule var2 = this.getModule(var1);
         return var2 == null ? null : var2.getItem(getItemName(var1));
      }
   }

   public Item FindItem(String var1) {
      ScriptModule var2 = this.getModule(var1);
      if (var2 == null) {
         return null;
      } else {
         Item var3 = var2.getItem(getItemName(var1));
         if (var3 == null) {
            Iterator var4 = this.ModuleMap.values().iterator();

            while(var4 != null && var4.hasNext()) {
               ScriptModule var5 = (ScriptModule)var4.next();
               if (!var5.disabled) {
                  var3 = var2.getItem(getItemName(var1));
                  if (var3 != null) {
                     return var3;
                  }
               }
            }
         }

         return var3;
      }
   }

   public boolean isDrainableItemType(String var1) {
      Item var2 = this.FindItem(var1);
      if (var2 != null) {
         return var2.getType() == Item.Type.Drainable;
      } else {
         return false;
      }
   }

   public Recipe getRecipe(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getRecipe(getItemName(var1));
   }

   public VehicleScript getVehicle(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getVehicle(getItemName(var1));
   }

   public VehicleTemplate getVehicleTemplate(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getVehicleTemplate(getItemName(var1));
   }

   public void CheckExitPoints() {
      Iterator var1 = this.ModuleMap.values().iterator();

      while(var1 != null && var1.hasNext()) {
         ScriptModule var2 = (ScriptModule)var1.next();
         if (!var2.disabled && var2.CheckExitPoints()) {
            return;
         }
      }

   }

   public Script getScript(String var1) {
      ScriptModule var2 = this.getModule(var1);
      return var2 == null ? null : var2.getScript(getItemName(var1));
   }

   public ArrayList getAllItems() {
      if (this.itemTempList == null) {
         this.itemTempList = new ArrayList();
         Iterator var1 = this.ModuleMap.values().iterator();

         while(true) {
            ScriptModule var2;
            do {
               if (var1 == null || !var1.hasNext()) {
                  return this.itemTempList;
               }

               var2 = (ScriptModule)var1.next();
            } while(var2.disabled);

            Iterator var3 = var2.ItemMap.values().iterator();

            while(var3.hasNext()) {
               Item var4 = (Item)var3.next();
               this.itemTempList.add(var4);
            }
         }
      } else {
         return this.itemTempList;
      }
   }

   public ArrayList getAllFixing() {
      this.fixingTempList = new ArrayList();
      Iterator var1 = this.ModuleMap.values().iterator();

      while(var1 != null && var1.hasNext()) {
         ScriptModule var2 = (ScriptModule)var1.next();
         if (!var2.disabled) {
            Iterator var3 = var2.FixingMap.values().iterator();

            while(var3.hasNext()) {
               Fixing var4 = (Fixing)var3.next();
               this.fixingTempList.add(var4);
            }
         }
      }

      return this.fixingTempList;
   }

   public Stack getAllRecipes() {
      Iterator var1 = this.ModuleMap.values().iterator();
      this.recipesTempList.clear();

      while(var1 != null && var1.hasNext()) {
         ScriptModule var2 = (ScriptModule)var1.next();
         if (!var2.disabled) {
            Iterator var3 = var2.RecipeMap.iterator();

            while(var3 != null && var3.hasNext()) {
               Recipe var4 = (Recipe)var3.next();
               this.recipesTempList.add(var4);
            }
         }
      }

      return this.recipesTempList;
   }

   public Stack getAllEvolvedRecipes() {
      Iterator var1 = this.ModuleMap.values().iterator();
      this.evolvedRecipesTempList.clear();

      while(var1 != null && var1.hasNext()) {
         ScriptModule var2 = (ScriptModule)var1.next();
         if (!var2.disabled) {
            Iterator var3 = var2.EvolvedRecipeMap.iterator();

            while(var3 != null && var3.hasNext()) {
               EvolvedRecipe var4 = (EvolvedRecipe)var3.next();
               this.evolvedRecipesTempList.add(var4);
            }
         }
      }

      return this.evolvedRecipesTempList;
   }

   public Stack getAllUniqueRecipes() {
      Iterator var1 = this.ModuleMap.values().iterator();
      this.uniqueRecipesTempList.clear();

      while(var1 != null && var1.hasNext()) {
         ScriptModule var2 = (ScriptModule)var1.next();
         if (!var2.disabled) {
            Iterator var3 = var2.UniqueRecipeMap.iterator();

            while(var3 != null && var3.hasNext()) {
               UniqueRecipe var4 = (UniqueRecipe)var3.next();
               this.uniqueRecipesTempList.add(var4);
            }
         }
      }

      return this.uniqueRecipesTempList;
   }

   public Stack getAllZones() {
      Iterator var1 = this.ModuleMap.values().iterator();
      this.zoneTempList.clear();

      while(var1 != null && var1.hasNext()) {
         ScriptModule var2 = (ScriptModule)var1.next();
         if (!var2.disabled) {
            Iterator var3 = var2.ZoneList.iterator();

            while(var3 != null && var3.hasNext()) {
               Zone var4 = (Zone)var3.next();
               this.zoneTempList.add(var4);
            }
         }
      }

      return this.zoneTempList;
   }

   public Stack getAllContainerDistributions() {
      Iterator var1 = this.ModuleMap.values().iterator();
      this.conTempList.clear();

      while(var1 != null && var1.hasNext()) {
         ScriptModule var2 = (ScriptModule)var1.next();
         if (!var2.disabled) {
            Iterator var3 = var2.ContainerDistributions.iterator();

            while(var3 != null && var3.hasNext()) {
               ContainerDistribution var4 = (ContainerDistribution)var3.next();
               this.conTempList.add(var4);
            }
         }
      }

      return this.conTempList;
   }

   public Stack getAllShelfDistributions() {
      Iterator var1 = this.ModuleMap.values().iterator();
      this.shelfTempList.clear();

      while(var1 != null && var1.hasNext()) {
         ScriptModule var2 = (ScriptModule)var1.next();
         if (!var2.disabled) {
            Iterator var3 = var2.ShelfDistributions.iterator();

            while(var3 != null && var3.hasNext()) {
               ShelfDistribution var4 = (ShelfDistribution)var3.next();
               this.shelfTempList.add(var4);
            }
         }
      }

      return this.shelfTempList;
   }

   public Stack getAllFloorDistributions() {
      Iterator var1 = this.ModuleMap.values().iterator();
      this.floorTempList.clear();

      while(var1 != null && var1.hasNext()) {
         ScriptModule var2 = (ScriptModule)var1.next();
         if (!var2.disabled) {
            Iterator var3 = var2.FloorDistributions.iterator();

            while(var3 != null && var3.hasNext()) {
               FloorDistribution var4 = (FloorDistribution)var3.next();
               this.floorTempList.add(var4);
            }
         }
      }

      return this.floorTempList;
   }

   public ArrayList getAllGameSounds() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.ModuleMap.values().iterator();

      while(var2 != null && var2.hasNext()) {
         ScriptModule var3 = (ScriptModule)var2.next();
         if (!var3.disabled) {
            var1.addAll(var3.GameSoundList);
         }
      }

      return var1;
   }

   public ArrayList getAllVehicleScripts() {
      Iterator var1 = this.ModuleMap.values().iterator();
      this.vehicleScriptTempList.clear();

      while(var1 != null && var1.hasNext()) {
         ScriptModule var2 = (ScriptModule)var1.next();
         if (!var2.disabled) {
            this.vehicleScriptTempList.addAll(var2.VehicleMap.values());
         }
      }

      return this.vehicleScriptTempList;
   }

   public Stack getZones(String var1) {
      Iterator var2 = this.ModuleMap.values().iterator();
      this.zoneTempList.clear();

      while(var2 != null && var2.hasNext()) {
         ScriptModule var3 = (ScriptModule)var2.next();
         if (!var3.disabled) {
            Iterator var4 = var3.ZoneList.iterator();

            while(var4 != null && var4.hasNext()) {
               Zone var5 = (Zone)var4.next();
               if (var5.name.equals(var1)) {
                  this.zoneTempList.add(var5);
               }
            }
         }
      }

      return this.zoneTempList;
   }

   public void AddZone(String var1, String var2, Zone var3) {
      ScriptModule var4 = null;
      if (this.ModuleMap.containsKey(var1)) {
         var4 = this.getModule(var1);
      } else {
         var4 = new ScriptModule();
         var4.name = var1;
         this.ModuleMap.put(var1, var4);
      }

      var4.ZoneMap.put(var2, var3);
      var4.ZoneList.add(var3);
   }

   public void AddRoom(String var1, String var2, Room var3) {
      ScriptModule var4 = null;
      if (this.ModuleMap.containsKey(var1)) {
         var4 = this.getModule(var1);
      } else {
         var4 = new ScriptModule();
         var4.name = var1;
         this.ModuleMap.put(var1, var4);
      }

      var4.RoomMap.put(var2, var3);
      var4.RoomList.add(var3);
   }

   public void Reset() {
      this.ModuleMap.clear();
      this.ModuleAliases.clear();
      this.TriggerMap.clear();
      this.HookMap.clear();
      this.CustomTriggerMap.clear();
      this.CustomTriggerLastRan.clear();
      this.PlayingScripts.clear();
      this.CachedModules.clear();
      this.FullTypeToItemMap.clear();
   }

   public String getChecksum() {
      return this.checksum;
   }

   public void Load() {
      try {
         ArrayList var1 = new ArrayList();
         this.searchFolders(ZomboidFileSystem.instance.baseURI, new File("media" + File.separator + "scripts"), var1);
         ArrayList var2 = new ArrayList();
         ArrayList var3 = ZomboidFileSystem.instance.getModIDs();

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            String var5 = ZomboidFileSystem.instance.getModDir((String)var3.get(var4));
            if (var5 != null) {
               URI var6 = (new File(var5)).toURI();
               this.searchFolders(var6, new File(var5 + File.separator + "media" + File.separator + "scripts"), var2);
            }
         }

         Comparator var13 = new Comparator() {
            public int compare(String var1, String var2) {
               String var3 = (new File(var1)).getName();
               String var4 = (new File(var2)).getName();
               if (var3.startsWith("template_") && !var4.startsWith("template_")) {
                  return -1;
               } else {
                  return !var3.startsWith("template_") && var4.startsWith("template_") ? 1 : var1.compareTo(var2);
               }
            }
         };
         Collections.sort(var1, var13);
         Collections.sort(var2, var13);
         var1.addAll(var2);
         if (GameClient.bClient || GameServer.bServer) {
            NetChecksum.checksummer.reset(true);
            NetChecksum.GroupOfFiles.initChecksum();
         }

         MultiStageBuilding.stages.clear();
         HashSet var15 = new HashSet();
         Iterator var16 = var1.iterator();

         label66:
         while(true) {
            String var7;
            String var8;
            do {
               do {
                  if (!var16.hasNext()) {
                     if (GameClient.bClient || GameServer.bServer) {
                        this.checksum = NetChecksum.checksummer.checksumToString();
                     }
                     break label66;
                  }

                  var7 = (String)var16.next();
               } while(var15.contains(var7));

               var15.add(var7);
               var8 = ZomboidFileSystem.instance.getAbsolutePath(var7);
               this.LoadFile(var8, false);
            } while(!GameClient.bClient && !GameServer.bServer);

            NetChecksum.checksummer.addFile(var7, var8);
         }
      } catch (Exception var9) {
         ExceptionLogger.logException(var9);
      }

      this.buf = new StringBuffer();
      this.buf2 = new StringBuffer();
      Iterator var10 = this.ModuleMap.values().iterator();

      while(var10.hasNext()) {
         ScriptModule var11 = (ScriptModule)var10.next();
         Iterator var12 = var11.ItemMap.values().iterator();

         while(var12.hasNext()) {
            Item var14 = (Item)var12.next();
            this.FullTypeToItemMap.put(var14.getFullName(), var14);
         }
      }

      RecipeManager.Loaded();
      GameSounds.ScriptsLoaded();
      if (SoundManager.instance != null) {
         SoundManager.instance.debugScriptSounds();
      }

      Translator.debugItemNames();
      Translator.debugMultiStageBuildNames();
      Translator.debugRecipeNames();
   }

   public String getRandomMap() {
      int var1 = Rand.Next(this.MapMap.keySet().size());
      Iterator var2 = this.MapMap.keySet().iterator();
      String var3 = "";

      for(int var4 = -1; var2 != null && var2.hasNext() && var4 != var1; ++var4) {
         var3 = (String)var2.next();
      }

      return var3;
   }

   public Stack getAllRecipesFor(String var1) {
      Stack var2 = this.getAllRecipes();
      Stack var3 = new Stack();

      for(int var4 = 0; var4 < var2.size(); ++var4) {
         String var5 = ((Recipe)var2.get(var4)).Result.type;
         if (var5.contains(".")) {
            var5 = var5.substring(var5.indexOf(".") + 1);
         }

         if (var5.equals(var1)) {
            var3.add((Recipe)var2.get(var4));
         }
      }

      return var3;
   }

   public void StopScript(Script.ScriptInstance var1) {
      this.toStopInstance.add(var1);
   }
}
