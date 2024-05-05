package zombie.characters;

import zombie.GameTime;


public class HaloTextHelper {
	public static final HaloTextHelper.ColorRGB COLOR_WHITE = new HaloTextHelper.ColorRGB(255, 255, 255);
	public static final HaloTextHelper.ColorRGB COLOR_GREEN = new HaloTextHelper.ColorRGB(137, 232, 148);
	public static final HaloTextHelper.ColorRGB COLOR_RED = new HaloTextHelper.ColorRGB(255, 105, 97);
	private static String[] queuedLines = new String[4];
	private static String[] currentLines = new String[4];
	private static boolean ignoreOverheadCheckOnce = false;

	public static HaloTextHelper.ColorRGB getColorWhite() {
		return COLOR_WHITE;
	}

	public static HaloTextHelper.ColorRGB getColorGreen() {
		return COLOR_GREEN;
	}

	public static HaloTextHelper.ColorRGB getColorRed() {
		return COLOR_RED;
	}

	public static void forceNextAddText() {
		ignoreOverheadCheckOnce = true;
	}

	public static void addTextWithArrow(IsoPlayer player, String string, boolean boolean1, HaloTextHelper.ColorRGB colorRGB) {
		addTextWithArrow(player, string, boolean1, colorRGB.r, colorRGB.g, colorRGB.b, colorRGB.r, colorRGB.g, colorRGB.b);
	}

	public static void addTextWithArrow(IsoPlayer player, String string, boolean boolean1, int int1, int int2, int int3) {
		addTextWithArrow(player, string, boolean1, int1, int2, int3, int1, int2, int3);
	}

	public static void addTextWithArrow(IsoPlayer player, String string, boolean boolean1, HaloTextHelper.ColorRGB colorRGB, HaloTextHelper.ColorRGB colorRGB2) {
		addTextWithArrow(player, string, boolean1, colorRGB.r, colorRGB.g, colorRGB.b, colorRGB2.r, colorRGB2.g, colorRGB2.b);
	}

	public static void addTextWithArrow(IsoPlayer player, String string, boolean boolean1, int int1, int int2, int int3, int int4, int int5, int int6) {
		addText(player, "[col=" + int1 + "," + int2 + "," + int3 + "]" + string + "[/] [img=media/ui/" + (boolean1 ? "ArrowUp.png" : "ArrowDown.png") + "," + int4 + "," + int5 + "," + int6 + "]");
	}

	public static void addText(IsoPlayer player, String string, HaloTextHelper.ColorRGB colorRGB) {
		addText(player, string, colorRGB.r, colorRGB.g, colorRGB.b);
	}

	public static void addText(IsoPlayer player, String string, int int1, int int2, int int3) {
		addText(player, "[col=" + int1 + "," + int2 + "," + int3 + "]" + string + "[/]");
	}

	public static void addText(IsoPlayer player, String string) {
		int int1 = player.getPlayerNum();
		if (!overheadContains(int1, string)) {
			String string2 = queuedLines[int1];
			if (string2 == null) {
				string2 = string;
			} else {
				if (string2.contains(string)) {
					return;
				}

				string2 = string2 + "[col=175,175,175], [/]" + string;
			}

			queuedLines[int1] = string2;
		}
	}

	private static boolean overheadContains(int int1, String string) {
		if (ignoreOverheadCheckOnce) {
			ignoreOverheadCheckOnce = false;
			return false;
		} else {
			return currentLines[int1] != null && currentLines[int1].contains(string);
		}
	}

	public static void update() {
		for (int int1 = 0; int1 < 4; ++int1) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null) {
				if (currentLines[int1] != null && player.getHaloTimerCount() <= 0.2F * GameTime.getInstance().getMultiplier()) {
					currentLines[int1] = null;
				}

				if (queuedLines[int1] != null && player.getHaloTimerCount() <= 0.2F * GameTime.getInstance().getMultiplier()) {
					player.setHaloNote(queuedLines[int1]);
					currentLines[int1] = queuedLines[int1];
					queuedLines[int1] = null;
				}
			} else {
				if (queuedLines[int1] != null) {
					queuedLines[int1] = null;
				}

				if (currentLines[int1] != null) {
					currentLines[int1] = null;
				}
			}
		}
	}

	public static class ColorRGB {
		public int r;
		public int g;
		public int b;
		public int a = 255;

		public ColorRGB(int int1, int int2, int int3) {
			this.r = int1;
			this.g = int2;
			this.b = int3;
		}
	}
}
