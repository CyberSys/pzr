package zombie.worldMap;

import java.util.ArrayList;
import zombie.ZomboidFileSystem;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;


public final class WorldMapSettings {
	public static int VERSION1 = 1;
	public static int VERSION;
	private static WorldMapSettings instance;
	final ArrayList m_options = new ArrayList();
	final WorldMapSettings.WorldMap mWorldMap = new WorldMapSettings.WorldMap();
	final WorldMapSettings.MiniMap mMiniMap = new WorldMapSettings.MiniMap();
	private int m_readVersion = 0;

	public static WorldMapSettings getInstance() {
		if (instance == null) {
			instance = new WorldMapSettings();
			instance.load();
		}

		return instance;
	}

	private BooleanConfigOption newOption(String string, boolean boolean1) {
		BooleanConfigOption booleanConfigOption = new BooleanConfigOption(string, boolean1);
		this.m_options.add(booleanConfigOption);
		return booleanConfigOption;
	}

	private DoubleConfigOption newOption(String string, double double1, double double2, double double3) {
		DoubleConfigOption doubleConfigOption = new DoubleConfigOption(string, double1, double2, double3);
		this.m_options.add(doubleConfigOption);
		return doubleConfigOption;
	}

	public ConfigOption getOptionByName(String string) {
		for (int int1 = 0; int1 < this.m_options.size(); ++int1) {
			ConfigOption configOption = (ConfigOption)this.m_options.get(int1);
			if (configOption.getName().equals(string)) {
				return configOption;
			}
		}

		return null;
	}

	public int getOptionCount() {
		return this.m_options.size();
	}

	public ConfigOption getOptionByIndex(int int1) {
		return (ConfigOption)this.m_options.get(int1);
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

	public void setDouble(String string, double double1) {
		ConfigOption configOption = this.getOptionByName(string);
		if (configOption instanceof DoubleConfigOption) {
			((DoubleConfigOption)configOption).setValue(double1);
		}
	}

	public double getDouble(String string, double double1) {
		ConfigOption configOption = this.getOptionByName(string);
		return configOption instanceof DoubleConfigOption ? ((DoubleConfigOption)configOption).getValue() : double1;
	}

	public int getFileVersion() {
		return this.m_readVersion;
	}

	public void save() {
		String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("InGameMap.ini");
		ConfigFile configFile = new ConfigFile();
		configFile.write(string, VERSION, this.m_options);
		this.m_readVersion = VERSION;
	}

	public void load() {
		this.m_readVersion = 0;
		String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("InGameMap.ini");
		ConfigFile configFile = new ConfigFile();
		if (configFile.read(string)) {
			this.m_readVersion = configFile.getVersion();
			if (this.m_readVersion >= VERSION1 && this.m_readVersion <= VERSION) {
				for (int int1 = 0; int1 < configFile.getOptions().size(); ++int1) {
					ConfigOption configOption = (ConfigOption)configFile.getOptions().get(int1);
					try {
						ConfigOption configOption2 = this.getOptionByName(configOption.getName());
						if (configOption2 != null) {
							configOption2.parse(configOption.getValueAsString());
						}
					} catch (Exception exception) {
					}
				}
			}
		}
	}

	public static void Reset() {
		if (instance != null) {
			instance.m_options.clear();
			instance = null;
		}
	}

	static  {
		VERSION = VERSION1;
	}

	public final class WorldMap {
		public DoubleConfigOption CenterX = WorldMapSettings.this.newOption("WorldMap.CenterX", -1.7976931348623157E308, Double.MAX_VALUE, 0.0);
		public DoubleConfigOption CenterY = WorldMapSettings.this.newOption("WorldMap.CenterY", -1.7976931348623157E308, Double.MAX_VALUE, 0.0);
		public DoubleConfigOption Zoom = WorldMapSettings.this.newOption("WorldMap.Zoom", 0.0, 24.0, 0.0);
		public BooleanConfigOption Isometric = WorldMapSettings.this.newOption("WorldMap.Isometric", true);
		public BooleanConfigOption ShowSymbolsUI = WorldMapSettings.this.newOption("WorldMap.ShowSymbolsUI", true);
	}

	public class MiniMap {
		public DoubleConfigOption Zoom = WorldMapSettings.this.newOption("MiniMap.Zoom", 0.0, 24.0, 19.0);
		public BooleanConfigOption Isometric = WorldMapSettings.this.newOption("MiniMap.Isometric", true);
		public BooleanConfigOption ShowSymbols = WorldMapSettings.this.newOption("MiniMap.ShowSymbols", false);
		public BooleanConfigOption StartVisible = WorldMapSettings.this.newOption("MiniMap.StartVisible", true);
	}
}
