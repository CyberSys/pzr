package zombie.inventory;



public enum ItemType {

	None,
	Weapon,
	Food,
	Literature,
	Drainable,
	Clothing,
	Key,
	KeyRing,
	Moveable,
	AlarmClock,
	index;

	private ItemType(int int1) {
		this.index = int1;
	}
	public int index() {
		return this.index;
	}
	public static ItemType fromIndex(int int1) {
		return ((ItemType[])ItemType.class.getEnumConstants())[int1];
	}
}
