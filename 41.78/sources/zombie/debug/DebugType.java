package zombie.debug;



public enum DebugType {

	Packet,
	NetworkFileDebug,
	Network,
	General,
	Lua,
	Mod,
	Sound,
	Zombie,
	Combat,
	Objects,
	Fireplace,
	Radio,
	MapLoading,
	Clothing,
	Animation,
	Asset,
	Script,
	Shader,
	Input,
	Recipe,
	ActionSystem,
	IsoRegion,
	UnitTests,
	FileIO,
	Multiplayer,
	Ownership,
	Death,
	Damage,
	Statistic,
	Vehicle,
	Voice,
	Checksum;

	public static boolean Do(DebugType debugType) {
		return DebugLog.isEnabled(debugType);
	}
	private static DebugType[] $values() {
		return new DebugType[]{Packet, NetworkFileDebug, Network, General, Lua, Mod, Sound, Zombie, Combat, Objects, Fireplace, Radio, MapLoading, Clothing, Animation, Asset, Script, Shader, Input, Recipe, ActionSystem, IsoRegion, UnitTests, FileIO, Multiplayer, Ownership, Death, Damage, Statistic, Vehicle, Voice, Checksum};
	}
}
