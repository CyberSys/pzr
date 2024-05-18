package zombie.scripting.objects;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import javax.swing.JOptionPane;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.iso.MultiStageBuilding;
import zombie.scripting.IScriptObjectStore;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParsingUtils;

public class ScriptModule extends BaseScriptObject implements IScriptObjectStore {
   public Stack ValidMaps = new Stack();
   public Stack ExitPoints = new Stack();
   public String name;
   public String value;
   public HashMap WaypointMap = new HashMap();
   public HashMap RoomMap = new HashMap();
   public Stack RoomList = new Stack();
   public HashMap DoorMap = new HashMap();
   public HashMap ItemMap = new HashMap();
   public final HashMap GameSoundMap = new HashMap();
   public final ArrayList GameSoundList = new ArrayList();
   public HashMap VehicleMap = new HashMap();
   public HashMap VehicleTemplateMap = new HashMap();
   public HashMap ScriptMap = new HashMap();
   public HashMap CharacterMap = new HashMap();
   public ArrayList RecipeMap = new ArrayList();
   public HashMap RecipesWithDotInName = new HashMap();
   public ArrayList EvolvedRecipeMap = new ArrayList();
   public ArrayList UniqueRecipeMap = new ArrayList();
   public HashMap FixingMap = new HashMap();
   public HashMap InventoryMap = new HashMap();
   public HashMap ActivatableMap = new HashMap();
   public HashMap TalkerMap = new HashMap();
   public HashMap ScriptContainerMap = new HashMap();
   public HashMap ConditionMap = new HashMap();
   public HashMap FlagMap = new HashMap();
   public HashMap ZoneMap = new HashMap();
   public Stack ZoneList = new Stack();
   public HashMap RandomSelectorMap = new HashMap();
   public Stack ContainerDistributions = new Stack();
   public Stack FloorDistributions = new Stack();
   public Stack ShelfDistributions = new Stack();
   public Stack Imports = new Stack();
   public boolean disabled = false;
   public HashMap LanguageMap = new HashMap();

   public boolean ValidMapCheck(String var1) {
      return this.ValidMaps.isEmpty() ? true : this.ValidMaps.contains(var1);
   }

   public void Load(String var1, String var2) {
      this.name = var1;
      this.value = var2.trim();
      ScriptManager.instance.CurrentLoadingModule = this;
      this.ParseScriptPP(this.value);
      this.ParseScript(this.value);
      this.value = "";
   }

   private void CreateFromTokenPP(String var1) {
      var1 = var1.trim();
      String[] var2;
      String var3;
      if (var1.indexOf("zone") == 0) {
         var2 = var1.split("[{}]");
         var3 = var2[0];
         var3 = var3.replace("zone", "");
         var3 = var3.trim();
         Zone var4 = new Zone();
         this.ZoneMap.put(var3, var4);
         this.ZoneList.add(var4);
      } else if (var1.indexOf("waypoint") == 0) {
         var2 = var1.split("[{}]");
         var3 = var2[0];
         var3 = var3.replace("waypoint", "");
         var3 = var3.trim();
         Waypoint var8 = new Waypoint();
         this.WaypointMap.put(var3, var8);
      } else if (var1.indexOf("room") == 0) {
         var2 = var1.split("[{}]");
         var3 = var2[0];
         var3 = var3.replace("room", "");
         var3 = var3.trim();
         Room var9 = new Room();
         this.RoomMap.put(var3, var9);
         this.RoomList.add(var9);
      } else if (var1.indexOf("character") == 0) {
         var2 = var1.split("[{}]");
         var3 = var2[0];
         var3 = var3.replace("character", "");
         var3 = var3.trim();
         ScriptCharacter var10 = new ScriptCharacter();
         this.CharacterMap.put(var3, var10);
      } else if (var1.indexOf("item") == 0) {
         var2 = var1.split("[{}]");
         var3 = var2[0];
         var3 = var3.replace("item", "");
         var3 = var3.trim();
         Item var11 = new Item();
         this.ItemMap.put(var3, var11);
      } else if (var1.indexOf("door") == 0) {
         var2 = var1.split("[{}]");
         var3 = var2[0];
         var3 = var3.replace("door", "");
         var3 = var3.trim();
         Door var13 = new Door();
         this.DoorMap.put(var3, var13);
      } else if (var1.indexOf("activatable") == 0) {
         var2 = var1.split("[{}]");
         var3 = var2[0];
         var3 = var3.replace("activatable", "");
         var3 = var3.trim();
         ScriptActivatable var14 = new ScriptActivatable();
         this.ActivatableMap.put(var3, var14);
      } else if (var1.indexOf("talker") == 0) {
         var2 = var1.split("[{}]");
         var3 = var2[0];
         var3 = var3.replace("talker", "");
         var3 = var3.trim();
         ScriptTalker var16 = new ScriptTalker();
         this.TalkerMap.put(var3, var16);
      } else {
         String var17;
         String[] var24;
         if (var1.indexOf("language") == 0) {
            var2 = var1.split("[{}]");
            var24 = ScriptParsingUtils.SplitExceptInbetween(var2[1], ",", "\"");
            var17 = var2[0];
            var17 = var17.replace("language", "");
            var17 = new String(var17.trim());
            LanguageDefinition var5 = new LanguageDefinition();
            var5.Load(var17, var24);
            this.LanguageMap.put(var17, var5);
         } else if (var1.indexOf("container ") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("container", "");
            var3 = new String(var3.trim());
            ScriptContainer var19 = new ScriptContainer();
            this.ScriptContainerMap.put(var3, var19);
         } else if (var1.indexOf("questcondition") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("questcondition", "");
            var3 = var3.trim();
            QuestTaskCondition var20 = new QuestTaskCondition();
            this.ConditionMap.put(var3, var20);
         } else if (var1.indexOf("recipe") != 0) {
            if (var1.indexOf("randomselector") == 0) {
               var2 = var1.split("[{}]");
               var3 = var2[0];
               var3 = var3.replace("randomselector", "");
               var3 = var3.trim();
               RandomSelector var21 = new RandomSelector();
               this.RandomSelectorMap.put(var3, var21);
            } else if (var1.indexOf("inventory") == 0) {
               var2 = var1.split("[{}]");
               var3 = var2[0];
               var3 = var3.replace("inventory", "");
               var3 = var3.trim();
               Inventory var22 = new Inventory();
               this.InventoryMap.put(var3, var22);
            } else if (var1.indexOf("scriptflag") == 0) {
               var2 = var1.split("[{}]");
               var3 = var2[0];
               var3 = var3.replace("scriptflag", "");
               var3 = var3.trim();
               ScriptFlag var23 = new ScriptFlag();
               this.FlagMap.put(var3, var23);
            } else {
               Script var12;
               int var18;
               if (var1.indexOf("instancescript") == 0) {
                  var18 = var1.indexOf("{");
                  var24 = new String[]{var1.substring(0, var18).trim(), var1.substring(var18 + 1)};
                  var24[1] = var24[1].substring(0, var24[1].length() - 1);
                  var17 = var24[0];
                  var17 = var17.replace("instancescript", "");
                  var17 = var17.trim();
                  var12 = new Script();
                  var12.Instancable = true;
                  this.ScriptMap.put(var17, var12);
               } else if (var1.indexOf("script") == 0) {
                  var18 = var1.indexOf("{");
                  var24 = new String[]{var1.substring(0, var18).trim(), var1.substring(var18 + 1)};
                  var24[1] = var24[1].substring(0, var24[1].length() - 1);
                  var17 = var24[0];
                  var17 = var17.replace("script", "");
                  var17 = var17.trim();
                  var12 = new Script();
                  this.ScriptMap.put(var17, var12);
               } else if (var1.indexOf("sound") == 0) {
                  var2 = var1.split("[{}]");
                  var3 = var2[0];
                  var3 = var3.replace("sound", "");
                  var3 = var3.trim();
                  GameSoundScript var25;
                  if (this.GameSoundMap.containsKey(var3)) {
                     var25 = (GameSoundScript)this.GameSoundMap.get(var3);
                     var25.reset();
                  } else {
                     var25 = new GameSoundScript();
                     this.GameSoundMap.put(var3, var25);
                     this.GameSoundList.add(var25);
                  }
               } else if (var1.indexOf("vehicle") == 0) {
                  var2 = var1.split("[{}]");
                  var3 = var2[0];
                  var3 = var3.replace("vehicle", "");
                  var3 = var3.trim();
                  VehicleScript var26 = new VehicleScript();
                  this.VehicleMap.put(var3, var26);
               } else if (var1.indexOf("template") == 0) {
                  var2 = var1.split("[{}]");
                  var3 = var2[0];
                  var3 = var3.replace("template", "");
                  String[] var27 = var3.trim().split("\\s+");
                  if (var27.length == 2) {
                     String var15 = var27[0].trim();
                     String var6 = var27[1].trim();
                     if ("vehicle".equals(var15)) {
                        VehicleTemplate var7 = new VehicleTemplate(this, var6, var1);
                        var7.module = this;
                        this.VehicleTemplateMap.put(var6, var7);
                     }
                  }
               }
            }
         }
      }

   }

   private void CreateFromToken(String var1) {
      var1 = var1.trim();
      String[] var2;
      String var3;
      String[] var4;
      if (var1.indexOf("zone") == 0) {
         var2 = var1.split("[{}]");
         var3 = var2[0];
         var3 = var3.replace("zone", "");
         var3 = var3.trim();
         var4 = var2[1].split(",");
         Zone var5 = (Zone)this.ZoneMap.get(var3);
         var5.module = this;
         var5.Load(var3, var4);
      } else if (var1.indexOf("waypoint") == 0) {
         var2 = var1.split("[{}]");
         var3 = var2[0];
         var3 = var3.replace("waypoint", "");
         var3 = var3.trim();
         var4 = var2[1].split(",");
         Waypoint var13 = (Waypoint)this.WaypointMap.get(var3);
         var13.module = this;
         var13.Load(var3, var4);
      } else {
         int var14;
         if (var1.indexOf("imports") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("waypoint", "");
            var3 = var3.trim();
            var4 = var2[1].split(",");

            for(var14 = 0; var14 < var4.length; ++var14) {
               if (var4[var14].trim().length() > 0) {
                  String var6 = var4[var14].trim();
                  if (var6.equals(this.getName())) {
                     DebugLog.log("ERROR: module \"" + this.getName() + "\" imports itself");
                  } else {
                     this.Imports.add(var6);
                  }
               }
            }
         } else if (var1.indexOf("validmaps") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("validmaps", "");
            var3 = var3.trim();
            var4 = var2[1].split(",");

            for(var14 = 0; var14 < var4.length; ++var14) {
               if (var4[var14].trim().length() > 0) {
                  this.ValidMaps.add(var4[var14].trim());
               }
            }
         } else if (var1.indexOf("cellexit") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("validmaps", "");
            var3 = var3.trim();
            var4 = var2[1].split(",");
            ScriptModule.Exit var17 = new ScriptModule.Exit();
            var17.fromX1 = Integer.parseInt(var4[0].trim());
            var17.fromY1 = Integer.parseInt(var4[1].trim());
            var17.fromZ1 = Integer.parseInt(var4[2].trim());
            var17.fromX2 = Integer.parseInt(var4[3].trim());
            var17.fromY2 = Integer.parseInt(var4[4].trim());
            var17.fromZ2 = Integer.parseInt(var4[5].trim());
            var17.map = var4[6].trim();
            var17.toX1 = Integer.parseInt(var4[7].trim());
            var17.toY1 = Integer.parseInt(var4[8].trim());
            var17.toZ1 = Integer.parseInt(var4[9].trim());
            var17.toX2 = Integer.parseInt(var4[10].trim());
            var17.toY2 = Integer.parseInt(var4[11].trim());
            var17.toZ2 = Integer.parseInt(var4[12].trim());
            this.ExitPoints.add(var17);
         } else if (var1.indexOf("room") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("room", "");
            var3 = var3.trim();
            var4 = var2[1].split(",");
            Room var19 = (Room)this.RoomMap.get(var3);
            var19.module = this;
            var19.Load(var3, var4);
         } else if (var1.indexOf("character") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("character", "");
            var3 = var3.trim();
            var4 = var2[1].split(",");
            ScriptCharacter var21 = (ScriptCharacter)this.CharacterMap.get(var3);
            var21.module = this;
            var21.Load(var3, var4);
         } else if (var1.indexOf("talker") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("talker", "");
            var3 = var3.trim();
            var4 = var2[1].split(",");
            ScriptTalker var23 = (ScriptTalker)this.TalkerMap.get(var3);
            var23.module = this;
            var23.Load(var3, var4);
         } else if (var1.indexOf("activatable") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("activatable", "");
            var3 = var3.trim();
            var4 = var2[1].split(",");
            ScriptActivatable var25 = (ScriptActivatable)this.ActivatableMap.get(var3);
            var25.module = this;
            var25.Load(var3, var4);
         } else if (var1.indexOf("container ") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("container ", "");
            var3 = var3.trim();
            var4 = var2[1].split(",");
            ScriptContainer var26 = (ScriptContainer)this.ScriptContainerMap.get(var3);
            var26.module = this;
            var26.Load(var3, var4);
         } else if (var1.indexOf("questcondition") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("questcondition", "");
            var3 = var3.trim();
            var4 = var2[1].split(",");
            QuestTaskCondition var27 = (QuestTaskCondition)this.ConditionMap.get(var3);
            var27.module = this;
            var27.Load(var3, var4);
         } else if (var1.indexOf("door") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("door", "");
            var3 = var3.trim();
            var4 = var2[1].split(",");
            Door var28 = (Door)this.DoorMap.get(var3);
            var28.module = this;
            var28.Load(var3, var4);
         } else if (var1.indexOf("item") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("item", "");
            var3 = var3.trim();
            var4 = var2[1].split(",");
            Item var29 = (Item)this.ItemMap.get(var3);
            var29.module = this;

            try {
               var29.Load(var3, var4);
            } catch (Exception var12) {
               DebugLog.log((Object)var12);
            }
         } else if (var1.indexOf("randomselector") == 0) {
            var2 = var1.split("[{}]");
            var3 = var2[0];
            var3 = var3.replace("randomselector", "");
            var3 = var3.trim();
            var4 = var2[1].split(",");
            RandomSelector var30 = (RandomSelector)this.RandomSelectorMap.get(var3);
            var30.module = this;
            var30.Load(var3, var4);
         } else {
            Trigger var31;
            if (var1.indexOf("trigger") == 0) {
               var2 = var1.split("[{}]");
               var3 = var2[0];
               var3 = var3.replace("trigger", "");
               var3 = var3.trim();
               var4 = ScriptParsingUtils.SplitExceptInbetween(var2[1], ",", "(", ")");
               var31 = new Trigger();
               var3 = var3.toLowerCase();
               var31.module = this;
               var31.Load(var3, var4);
               if (ScriptManager.instance.TriggerMap.containsKey(var3)) {
                  ((Stack)ScriptManager.instance.TriggerMap.get(var3)).add(var31);
               } else {
                  ScriptManager.instance.TriggerMap.put(var3, new Stack());
                  ((Stack)ScriptManager.instance.TriggerMap.get(var3)).add(var31);
                  ScriptManager.instance.CustomTriggerLastRan.put(var31.name, 0);
               }
            } else if (var1.indexOf("customtrigger") == 0) {
               var2 = var1.split("[{}]");
               var3 = var2[0];
               var3 = var3.replace("customtrigger", "");
               var3 = var3.trim();
               var4 = ScriptParsingUtils.SplitExceptInbetween(var2[1], ",", "(", ")");
               var31 = new Trigger();
               var3 = var3.toLowerCase();
               var31.module = this;
               var31.Load(var3, var4);
               var31.Locked = true;
               if (ScriptManager.instance.CustomTriggerMap.containsKey(var3)) {
                  ((Stack)ScriptManager.instance.CustomTriggerMap.get(var3)).add(var31);
               } else {
                  ScriptManager.instance.CustomTriggerMap.put(var3, new Stack());
                  ((Stack)ScriptManager.instance.CustomTriggerMap.get(var3)).add(var31);
                  ScriptManager.instance.CustomTriggerLastRan.put(var31.name, 0);
               }
            } else if (var1.indexOf("inventory") == 0) {
               var2 = var1.split("[{}]");
               var3 = var2[0];
               var3 = var3.replace("inventory", "");
               var3 = var3.trim();
               var4 = null;
               if (var2.length > 1) {
                  var4 = var2[1].split(",");
               } else {
                  var4 = new String[1];
               }

               Inventory var32 = (Inventory)this.InventoryMap.get(var3);
               var32.module = this;
               var32.Load(var3, var4);
            } else if (var1.indexOf("scriptflag") == 0) {
               var2 = var1.split("[{}]");
               var3 = var2[0];
               var3 = var3.replace("scriptflag", "");
               var3 = var3.trim();
               var4 = var2[1].split(",");
               ScriptFlag var34 = (ScriptFlag)this.FlagMap.get(var3);
               var34.module = this;
               var34.Load(var3, var4);
            } else {
               int var24;
               String var33;
               Script var35;
               String[] var44;
               if (var1.indexOf("script") == 0) {
                  var24 = var1.indexOf("{");
                  var44 = new String[]{var1.substring(0, var24).trim(), var1.substring(var24 + 1)};
                  var44[1] = var44[1].substring(0, var44[1].length() - 1);
                  var33 = var44[0];
                  var33 = var33.replace("script", "");
                  var33 = var33.trim();
                  var35 = (Script)this.ScriptMap.get(var33);
                  var35.module = this;

                  try {
                     var35.DoScriptParsing(var33, var44[1]);
                  } catch (Exception var11) {
                     DebugLog.log((Object)var11);
                  }
               } else if (var1.indexOf("instancescript") == 0) {
                  var24 = var1.indexOf("{");
                  var44 = new String[]{var1.substring(0, var24).trim(), var1.substring(var24 + 1)};
                  var44[1] = var44[1].substring(0, var44[1].length() - 1);
                  var33 = var44[0];
                  var33 = var33.replace("instancescript", "");
                  var33 = var33.trim();
                  var35 = (Script)this.ScriptMap.get(var33);
                  var35.module = this;

                  try {
                     var35.DoScriptParsing(var33, var44[1]);
                  } catch (Exception var10) {
                     DebugLog.log((Object)var10);
                  }
               } else if (var1.indexOf("recipe") == 0) {
                  var2 = var1.split("[{}]");
                  var3 = var2[0];
                  var3 = var3.replace("recipe", "");
                  var3 = var3.trim();
                  var4 = var2[1].split(",");
                  Recipe var36 = new Recipe();
                  this.RecipeMap.add(var36);
                  if (var3.contains(".")) {
                     this.RecipesWithDotInName.put(var3, var36);
                  }

                  var36.module = this;
                  var36.Load(var3, var4);
               } else if (var1.indexOf("uniquerecipe") == 0) {
                  var2 = var1.split("[{}]");
                  var3 = var2[0];
                  var3 = var3.replace("uniquerecipe", "");
                  var3 = var3.trim();
                  var4 = var2[1].split(",");
                  UniqueRecipe var37 = new UniqueRecipe(var3);
                  this.UniqueRecipeMap.add(var37);
                  var37.module = this;
                  var37.Load(var3, var4);
               } else if (var1.indexOf("evolvedrecipe") == 0) {
                  var2 = var1.split("[{}]");
                  var3 = var2[0];
                  var3 = var3.replace("evolvedrecipe", "");
                  var3 = var3.trim();
                  var4 = var2[1].split(",");
                  boolean var38 = false;
                  Iterator var15 = this.EvolvedRecipeMap.iterator();

                  while(var15.hasNext()) {
                     EvolvedRecipe var7 = (EvolvedRecipe)var15.next();
                     if (var7.name.equals(var3)) {
                        var7.Load(var3, var4);
                        var7.module = this;
                        var38 = true;
                     }
                  }

                  if (!var38) {
                     EvolvedRecipe var16 = new EvolvedRecipe(var3);
                     this.EvolvedRecipeMap.add(var16);
                     var16.module = this;
                     var16.Load(var3, var4);
                  }
               } else if (var1.indexOf("fixing") == 0) {
                  var2 = var1.split("[{}]");
                  var3 = var2[0];
                  var3 = var3.replace("fixing", "");
                  var3 = var3.trim();
                  var4 = var2[1].split(",");
                  Fixing var39 = new Fixing();
                  var39.module = this;
                  this.FixingMap.put(var3, var39);
                  var39.Load(var3, var4);
               } else if (var1.indexOf("multistagebuild") == 0) {
                  var2 = var1.split("[{}]");
                  var3 = var2[0];
                  var3 = var3.replace("multistagebuild", "");
                  var3 = var3.trim();
                  var4 = var2[1].split(",");
                  MultiStageBuilding.Stage var40 = new MultiStageBuilding().new Stage();
                  var40.Load(var3, var4);
                  MultiStageBuilding.addStage(var40);
               } else {
                  String[] var41;
                  if (var1.indexOf("containeritemdistribution") == 0) {
                     var24 = var1.indexOf("{");
                     var44 = new String[]{var1.substring(0, var24).trim(), var1.substring(var24 + 1)};
                     var44[1] = var44[1].substring(0, var44[1].length() - 1);
                     var33 = var44[0];
                     var33 = var33.replace("containeritemdistribution", "");
                     var33 = var33.trim();
                     var41 = var44[1].split(",");
                     ContainerDistribution var18 = new ContainerDistribution();
                     var18.module = this;
                     var18.Load(var33, var41);
                     this.ContainerDistributions.add(var18);
                  } else if (var1.indexOf("flooritemdistribution") == 0) {
                     var24 = var1.indexOf("{");
                     var44 = new String[]{var1.substring(0, var24).trim(), var1.substring(var24 + 1)};
                     var44[1] = var44[1].substring(0, var44[1].length() - 1);
                     var33 = var44[0];
                     var33 = var33.replace("flooritemdistribution", "");
                     var33 = var33.trim();
                     var41 = var44[1].split(",");
                     FloorDistribution var20 = new FloorDistribution();
                     var20.module = this;
                     var20.Load(var33, var41);
                     this.FloorDistributions.add(var20);
                  } else if (var1.indexOf("shelfitemdistribution") == 0) {
                     var24 = var1.indexOf("{");
                     var44 = new String[]{var1.substring(0, var24).trim(), var1.substring(var24 + 1)};
                     var44[1] = var44[1].substring(0, var44[1].length() - 1);
                     var33 = var44[0];
                     var33 = var33.replace("shelfitemdistribution", "");
                     var33 = var33.trim();
                     var41 = var44[1].split(",");
                     ShelfDistribution var22 = new ShelfDistribution();
                     var22.module = this;
                     var22.Load(var33, var41);
                     this.ShelfDistributions.add(var22);
                  } else if (var1.indexOf("sound") == 0) {
                     var2 = var1.split("[{}]");
                     var3 = var2[0];
                     var3 = var3.replace("sound", "");
                     var3 = var3.trim();
                     GameSoundScript var42 = (GameSoundScript)this.GameSoundMap.get(var3);
                     var42.module = this;

                     try {
                        var42.Load(var3, var1);
                     } catch (Throwable var9) {
                        ExceptionLogger.logException(var9);
                     }
                  } else if (var1.indexOf("vehicle") == 0) {
                     var2 = var1.split("[{}]");
                     var3 = var2[0];
                     var3 = var3.replace("vehicle", "");
                     var3 = var3.trim();
                     VehicleScript var43 = (VehicleScript)this.VehicleMap.get(var3);
                     var43.module = this;

                     try {
                        var43.Load(var3, var1);
                        var43.Loaded();
                     } catch (Exception var8) {
                        ExceptionLogger.logException(var8);
                     }
                  }
               }
            }
         }
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

   public void PlayScript(String var1) {
      if (var1.contains(".")) {
         ScriptManager.instance.PlayScript(var1);
      } else {
         if (this.ScriptMap.containsKey(var1)) {
            Script var2 = (Script)this.ScriptMap.get(var1);
            Script.ScriptInstance var3 = new Script.ScriptInstance();
            var3.theScript = var2;
            var3.ID = this.name;
            ScriptManager.instance.PlayingScripts.add(var3);
            var3.begin();
         } else if (this.RandomSelectorMap.containsKey(var1)) {
            ((RandomSelector)this.RandomSelectorMap.get(var1)).Process();
         } else {
            for(int var4 = 0; var4 < this.Imports.size(); ++var4) {
               Script var5 = ScriptManager.instance.getModule((String)this.Imports.get(var4)).getScript(this.name);
               if (var5 != null) {
                  ScriptManager.instance.getModule((String)this.Imports.get(var4)).PlayScript(var5.name);
                  return;
               }
            }

            JOptionPane.showMessageDialog((Component)null, "Module: " + this.name + " cannot find script: " + var1, "Error", 0);
         }

      }
   }

   public Script.ScriptInstance PlayScript(String var1, Script.ScriptInstance var2) {
      if (var1.contains(".")) {
         return ScriptManager.instance.PlayScript(var1, var2);
      } else if (this.ScriptMap.containsKey(var1)) {
         Script var5 = (Script)this.ScriptMap.get(var1);
         Script.ScriptInstance var6 = new Script.ScriptInstance();
         var6.theScript = var5;
         var6.ID = this.name;
         var6.CopyAliases(var2);
         ScriptManager.instance.PlayingScripts.add(var6);
         var6.begin();
         return var6;
      } else {
         if (this.RandomSelectorMap.containsKey(var1)) {
            ((RandomSelector)this.RandomSelectorMap.get(var1)).Process(var2);
         } else {
            for(int var3 = 0; var3 < this.Imports.size(); ++var3) {
               Script var4 = ScriptManager.instance.getModule((String)this.Imports.get(var3)).getScript(this.name);
               if (var4 != null) {
                  return ScriptManager.instance.getModule((String)this.Imports.get(var3)).PlayScript(var4.name, var2);
               }
            }

            JOptionPane.showMessageDialog((Component)null, "Module: " + this.name + " cannot find script: " + var1, "Error", 0);
         }

         return null;
      }
   }

   public Script.ScriptInstance PlayScript(Script.ScriptInstance var1) {
      ScriptManager.instance.PlayingScripts.add(var1);
      var1.begin();
      return var1;
   }

   public Inventory getInventory(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getInventory(var1);
      } else if (!this.InventoryMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            Inventory var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getInventory(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (Inventory)this.InventoryMap.get(var1);
      }
   }

   public ScriptCharacter getCharacter(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getCharacter(var1);
      } else if (!this.CharacterMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            ScriptCharacter var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getCharacter(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (ScriptCharacter)this.CharacterMap.get(var1);
      }
   }

   public IsoGameCharacter getCharacterActual(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getCharacterActual(var1);
      } else if (!this.CharacterMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            ScriptCharacter var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getCharacter(var1);
            if (var3 != null) {
               return var3.Actual;
            }
         }

         return null;
      } else {
         return ((ScriptCharacter)this.CharacterMap.get(var1)).Actual;
      }
   }

   public int getFlagIntValue(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getFlagIntValue(var1);
      } else if (!this.FlagMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            ScriptFlag var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getFlag(var1);
            if (var3 != null) {
               return Integer.parseInt(var3.value);
            }
         }

         return 0;
      } else {
         return Integer.parseInt(((ScriptFlag)this.FlagMap.get(var1)).value);
      }
   }

   public String getFlagValue(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getFlagValue(var1);
      } else if (!this.FlagMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            ScriptFlag var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getFlag(var1);
            if (var3 != null) {
               return var3.value;
            }
         }

         return null;
      } else {
         return ((ScriptFlag)this.FlagMap.get(var1)).value;
      }
   }

   public Waypoint getWaypoint(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getWaypoint(var1);
      } else if (!this.WaypointMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            Waypoint var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getWaypoint(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (Waypoint)this.WaypointMap.get(var1);
      }
   }

   public ScriptContainer getScriptContainer(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getScriptContainer(var1);
      } else if (!this.ScriptContainerMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            ScriptContainer var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getScriptContainer(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (ScriptContainer)this.ScriptContainerMap.get(var1);
      }
   }

   public Room getRoom(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getRoom(var1);
      } else if (!this.RoomMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            Room var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getRoom(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (Room)this.RoomMap.get(var1);
      }
   }

   public ScriptActivatable getActivatable(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getActivatable(var1);
      } else if (!this.ActivatableMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            ScriptActivatable var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getActivatable(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (ScriptActivatable)this.ActivatableMap.get(var1);
      }
   }

   public ScriptTalker getTalker(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getTalker(var1);
      } else if (!this.TalkerMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            ScriptTalker var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getTalker(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (ScriptTalker)this.TalkerMap.get(var1);
      }
   }

   public LanguageDefinition getLanguageDef(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getLanguageDef(var1);
      } else if (!this.LanguageMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            LanguageDefinition var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getLanguageDef(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (LanguageDefinition)this.LanguageMap.get(var1);
      }
   }

   public String getLanguage(String var1) {
      if (!var1.contains("@")) {
         return var1;
      } else {
         var1 = var1.substring(1);
         if (var1.contains(".")) {
            return ScriptManager.instance.getLanguage(var1);
         } else {
            String[] var2 = var1.split("-");
            LanguageDefinition var3 = this.getLanguageDef(var2[0]);
            String var4 = var3.get(Integer.parseInt(var2[1]));
            var4 = var4.substring(1);
            var4 = var4.substring(0, var4.length() - 1);
            return var4;
         }
      }
   }

   public ScriptFlag getFlag(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getFlag(var1);
      } else if (!this.FlagMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            ScriptFlag var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getFlag(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (ScriptFlag)this.FlagMap.get(var1);
      }
   }

   public Zone getZone(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getZone(var1);
      } else if (!this.ZoneMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            Zone var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getZone(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (Zone)this.ZoneMap.get(var1);
      }
   }

   public QuestTaskCondition getQuestCondition(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getQuestCondition(var1);
      } else if (!this.ConditionMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            QuestTaskCondition var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getQuestCondition(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (QuestTaskCondition)this.ConditionMap.get(var1);
      }
   }

   public Item getItem(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getItem(var1);
      } else if (!this.ItemMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            Item var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getItem(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (Item)this.ItemMap.get(var1);
      }
   }

   public Recipe getRecipe(String var1) {
      if (var1.contains(".") && !this.RecipesWithDotInName.containsKey(var1)) {
         return ScriptManager.instance.getRecipe(var1);
      } else {
         int var2;
         Recipe var3;
         for(var2 = 0; var2 < this.RecipeMap.size(); ++var2) {
            var3 = (Recipe)this.RecipeMap.get(var2);
            if (var3.getOriginalname().equals(var1)) {
               return var3;
            }
         }

         for(var2 = 0; var2 < this.Imports.size(); ++var2) {
            if (ScriptManager.instance.getModule((String)this.Imports.get(var2)) != null) {
               var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getRecipe(var1);
               if (var3 != null) {
                  return var3;
               }
            }
         }

         return null;
      }
   }

   public VehicleScript getVehicle(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getVehicle(var1);
      } else if (!this.VehicleMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            VehicleScript var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getVehicle(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (VehicleScript)this.VehicleMap.get(var1);
      }
   }

   public VehicleTemplate getVehicleTemplate(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getVehicleTemplate(var1);
      } else if (!this.VehicleTemplateMap.containsKey(var1)) {
         for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
            VehicleTemplate var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getVehicleTemplate(var1);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return (VehicleTemplate)this.VehicleTemplateMap.get(var1);
      }
   }

   private boolean ContainsRecipe(String var1) {
      for(int var2 = 0; var2 < this.RecipeMap.size(); ++var2) {
         Recipe var3 = (Recipe)this.RecipeMap.get(var2);
         if (var3.getOriginalname().equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean CheckExitPoints() {
      return false;
   }

   public Script getScript(String var1) {
      if (var1.contains(".")) {
         return ScriptManager.instance.getScript(var1);
      } else {
         if (this.RandomSelectorMap.containsKey(var1)) {
            var1 = (String)((RandomSelector)this.RandomSelectorMap.get(var1)).scriptsToCall.get(Rand.Next(((RandomSelector)this.RandomSelectorMap.get(var1)).scriptsToCall.size()));
         }

         if (!this.ScriptMap.containsKey(var1)) {
            for(int var2 = 0; var2 < this.Imports.size(); ++var2) {
               Script var3 = ScriptManager.instance.getModule((String)this.Imports.get(var2)).getScript(var1);
               if (var3 != null) {
                  return var3;
               }
            }

            return null;
         } else {
            return (Script)this.ScriptMap.get(var1);
         }
      }
   }

   public String getName() {
      return this.name;
   }

   public static class Exit {
      public int fromX1;
      public int fromY1;
      public int fromZ1;
      public int toX1;
      public int toY1;
      public int toZ1;
      public int fromX2;
      public int fromY2;
      public int fromZ2;
      public int toX2;
      public int toY2;
      public int toZ2;
      public String map;
   }
}
