package zombie;

import java.util.EnumMap;


public class MusicManager {
	public static EnumMap Choices = new EnumMap(MusicManager.Categories.class);
	public static enum Categories {

		Intro,
		Danger,
		Sad,
		Raider,
		Action,
		Ambient,
		Max;
	}
}
