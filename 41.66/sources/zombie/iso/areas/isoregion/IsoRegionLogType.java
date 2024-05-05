package zombie.iso.areas.isoregion;



public enum IsoRegionLogType {

	Normal,
	Warn;

	private static IsoRegionLogType[] $values() {
		return new IsoRegionLogType[]{Normal, Warn};
	}
}
