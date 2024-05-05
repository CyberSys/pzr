package zombie.characters;



public enum Stance {

	Stealth,
	Normal,
	Haste;

	private static Stance[] $values() {
		return new Stance[]{Stealth, Normal, Haste};
	}
}
