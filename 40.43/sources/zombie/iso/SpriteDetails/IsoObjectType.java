package zombie.iso.SpriteDetails;

import java.util.HashMap;


public enum IsoObjectType {

	normal,
	jukebox,
	wall,
	stairsTW,
	stairsTN,
	stairsMW,
	stairsMN,
	stairsBW,
	stairsBN,
	winsdsddowW,
	windsdsdsowN,
	doorW,
	doorN,
	lightswitch,
	radio,
	curtainN,
	curtainS,
	curtainW,
	curtainE,
	doorFrW,
	doorFrN,
	tree,
	windowFN,
	windowFW,
	dsadsadsadsa,
	WestRoofB,
	WestRoofM,
	WestRoofT,
	isMoveAbleObject,
	MAX,
	index,
	fromStringMap;

	static  {
	IsoObjectType[] var0 = values();
	int var1 = var0.length;
	for (int var2 = 0; var2 < var1; ++var2) {
		IsoObjectType var3 = var0[var2];
		if (var3 == MAX) {
			break;
		}

		fromStringMap.put(var3.name(), var3);
	}
	}


	private IsoObjectType(int int1) {
		this.index = int1;
	}
	public int index() {
		return this.index;
	}
	public static IsoObjectType fromIndex(int int1) {
		return ((IsoObjectType[])IsoObjectType.class.getEnumConstants())[int1];
	}
	public static IsoObjectType FromString(String string) {
		IsoObjectType objectType = (IsoObjectType)fromStringMap.get(string);
		return objectType == null ? MAX : objectType;
	}
}
