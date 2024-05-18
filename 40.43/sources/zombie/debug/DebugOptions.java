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

	public ConfigOption getOptionByName(String string) {
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			ConfigOption configOption = (ConfigOption)this.options.get(int1);
			if (configOption.getName().equals(string)) {
				return configOption;
			}
		}

		return null;
	}

	public int getOptionCount() {
		return this.options.size();
	}

	public ConfigOption getOptionByIndex(int int1) {
		return (ConfigOption)this.options.get(int1);
	}

	public void setBoolean(String string, boolean boolean1) {
		ConfigOption configOption = this.getOptionByName(string);
		if (configOption instanceof BooleanConfigOption) {
			((BooleanConfigOption)configOption).setValue(boolean1);
		}
	}

	public boolean getBoolean(String string) {
		ConfigOption configOption = this.getOptionByName(string);
		return configOption instanceof BooleanConfigOption ? ((BooleanConfigOption)configOption).getValue() : false;
	}

	public void save() {
		String string = GameWindow.getCacheDir() + File.separator + "debug-options.ini";
		ConfigFile configFile = new ConfigFile();
		configFile.write(string, 1, this.options);
	}

	public void load() {
		String string = GameWindow.getCacheDir() + File.separator + "debug-options.ini";
		ConfigFile configFile = new ConfigFile();
		if (configFile.read(string)) {
			for (int int1 = 0; int1 < configFile.getOptions().size(); ++int1) {
				ConfigOption configOption = (ConfigOption)configFile.getOptions().get(int1);
				ConfigOption configOption2 = this.getOptionByName(configOption.getName());
				if (configOption2 != null) {
					configOption2.parse(configOption.getValueAsString());
				}
			}
		}
	}

	public class BooleanDebugOption extends BooleanConfigOption {

		public BooleanDebugOption(String string, boolean boolean1) {
			super(string, boolean1);
			DebugOptions.this.options.add(this);
		}
	}
}
