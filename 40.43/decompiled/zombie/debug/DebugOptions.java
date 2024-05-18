package zombie.debug;

import java.io.File;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;

public class DebugOptions {
   public static final int VERSION = 1;
   public static final DebugOptions instance = new DebugOptions();
   private final ArrayList options = new ArrayList();
   public DebugOptions.BooleanDebugOption AimConeRender = new DebugOptions.BooleanDebugOption("AimCone.Render", false);
   public DebugOptions.BooleanDebugOption CollideWithObstaclesRadius = new DebugOptions.BooleanDebugOption("CollideWithObstacles.Radius", false);
   public DebugOptions.BooleanDebugOption CollideWithObstaclesRender = new DebugOptions.BooleanDebugOption("CollideWithObstacles.Render", false);
   public DebugOptions.BooleanDebugOption DeadBodyAtlasRender = new DebugOptions.BooleanDebugOption("DeadBodyAtlas.Render", false);
   public DebugOptions.BooleanDebugOption MechanicsRenderHitbox = new DebugOptions.BooleanDebugOption("Mechanics.Render.Hitbox", false);
   public DebugOptions.BooleanDebugOption ModelRenderAxis = new DebugOptions.BooleanDebugOption("Model.Render.Axis", false);
   public DebugOptions.BooleanDebugOption ModelRenderBounds = new DebugOptions.BooleanDebugOption("Model.Render.Bounds", false);
   public DebugOptions.BooleanDebugOption PathfindRenderPath = new DebugOptions.BooleanDebugOption("Pathfind.Render.Path", false);
   public DebugOptions.BooleanDebugOption PhysicsRender = new DebugOptions.BooleanDebugOption("Physics.Render", false);
   public DebugOptions.BooleanDebugOption PolymapRenderClusters = new DebugOptions.BooleanDebugOption("Polymap.Render.Clusters", false);
   public DebugOptions.BooleanDebugOption PolymapRenderLineClearCollide = new DebugOptions.BooleanDebugOption("Polymap.Render.LineClearCollide", false);
   public DebugOptions.BooleanDebugOption PolymapRenderPathToMouse = new DebugOptions.BooleanDebugOption("Polymap.Render.PathToMouse", false);
   public DebugOptions.BooleanDebugOption TooltipInfo = new DebugOptions.BooleanDebugOption("Tooltip.Info", false);
   public DebugOptions.BooleanDebugOption TranslationPrefix = new DebugOptions.BooleanDebugOption("Translation.Prefix", false);
   public DebugOptions.BooleanDebugOption UIRenderOutline = new DebugOptions.BooleanDebugOption("UI.Render.Outline", false);
   public DebugOptions.BooleanDebugOption VehicleRenderOutline = new DebugOptions.BooleanDebugOption("Vehicle.Render.Outline", false);
   public DebugOptions.BooleanDebugOption VehicleRenderArea = new DebugOptions.BooleanDebugOption("Vehicle.Render.Area", false);
   public DebugOptions.BooleanDebugOption VehicleRenderAuthorizations = new DebugOptions.BooleanDebugOption("Vehicle.Render.Authorizations", false);
   public DebugOptions.BooleanDebugOption VehicleRenderAttackPositions = new DebugOptions.BooleanDebugOption("Vehicle.Render.AttackPositions", false);
   public DebugOptions.BooleanDebugOption VehicleRenderExit = new DebugOptions.BooleanDebugOption("Vehicle.Render.Exit", false);
   public DebugOptions.BooleanDebugOption VehicleRenderIntersectedSquares = new DebugOptions.BooleanDebugOption("Vehicle.Render.IntersectedSquares", false);
   public DebugOptions.BooleanDebugOption WorldSoundRender = new DebugOptions.BooleanDebugOption("WorldSound.Render", false);
   public DebugOptions.BooleanDebugOption LightingRender = new DebugOptions.BooleanDebugOption("Lighting.Render", false);
   public DebugOptions.BooleanDebugOption SkyboxShow = new DebugOptions.BooleanDebugOption("Skybox.Show", false);
   public DebugOptions.BooleanDebugOption WorldStreamerSlowLoad = new DebugOptions.BooleanDebugOption("WorldStreamer.SlowLoad", false);

   public ConfigOption getOptionByName(String var1) {
      for(int var2 = 0; var2 < this.options.size(); ++var2) {
         ConfigOption var3 = (ConfigOption)this.options.get(var2);
         if (var3.getName().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public int getOptionCount() {
      return this.options.size();
   }

   public ConfigOption getOptionByIndex(int var1) {
      return (ConfigOption)this.options.get(var1);
   }

   public void setBoolean(String var1, boolean var2) {
      ConfigOption var3 = this.getOptionByName(var1);
      if (var3 instanceof BooleanConfigOption) {
         ((BooleanConfigOption)var3).setValue(var2);
      }

   }

   public boolean getBoolean(String var1) {
      ConfigOption var2 = this.getOptionByName(var1);
      return var2 instanceof BooleanConfigOption ? ((BooleanConfigOption)var2).getValue() : false;
   }

   public void save() {
      String var1 = GameWindow.getCacheDir() + File.separator + "debug-options.ini";
      ConfigFile var2 = new ConfigFile();
      var2.write(var1, 1, this.options);
   }

   public void load() {
      String var1 = GameWindow.getCacheDir() + File.separator + "debug-options.ini";
      ConfigFile var2 = new ConfigFile();
      if (var2.read(var1)) {
         for(int var3 = 0; var3 < var2.getOptions().size(); ++var3) {
            ConfigOption var4 = (ConfigOption)var2.getOptions().get(var3);
            ConfigOption var5 = this.getOptionByName(var4.getName());
            if (var5 != null) {
               var5.parse(var4.getValueAsString());
            }
         }
      }

   }

   public class BooleanDebugOption extends BooleanConfigOption {
      public BooleanDebugOption(String var2, boolean var3) {
         super(var2, var3);
         DebugOptions.this.options.add(this);
      }
   }
}
