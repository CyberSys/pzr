package zombie.ui;



public enum UIFont {

	Small,
	Medium,
	Large,
	Massive,
	MainMenu1,
	MainMenu2,
	Cred1,
	Cred2,
	NewSmall,
	NewMedium,
	NewLarge,
	Code,
	MediumNew,
	AutoNormSmall,
	AutoNormMedium,
	AutoNormLarge,
	Dialogue,
	Intro,
	Handwritten,
	DebugConsole,
	Title;

	public static UIFont FromString(String string) {
		try {
			return valueOf(string);
		} catch (Exception exception) {
			return null;
		}
	}
	private static UIFont[] $values() {
		return new UIFont[]{Small, Medium, Large, Massive, MainMenu1, MainMenu2, Cred1, Cred2, NewSmall, NewMedium, NewLarge, Code, MediumNew, AutoNormSmall, AutoNormMedium, AutoNormLarge, Dialogue, Intro, Handwritten, DebugConsole, Title};
	}
}
