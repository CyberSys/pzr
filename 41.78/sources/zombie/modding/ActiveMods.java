package zombie.modding;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import zombie.GameWindow;
import zombie.MapGroups;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.gameStates.ChooseGameInfo;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.util.StringUtils;


public final class ActiveMods {
	private static final ArrayList s_activeMods = new ArrayList();
	private static final ActiveMods s_loaded = new ActiveMods("loaded");
	private final String id;
	private final ArrayList mods = new ArrayList();
	private final ArrayList mapOrder = new ArrayList();

	private static int count() {
		return s_activeMods.size();
	}

	public static ActiveMods getByIndex(int int1) {
		return (ActiveMods)s_activeMods.get(int1);
	}

	public static ActiveMods getById(String string) {
		int int1 = indexOf(string);
		return int1 == -1 ? create(string) : (ActiveMods)s_activeMods.get(int1);
	}

	public static int indexOf(String string) {
		string = string.trim();
		requireValidId(string);
		for (int int1 = 0; int1 < s_activeMods.size(); ++int1) {
			ActiveMods activeMods = (ActiveMods)s_activeMods.get(int1);
			if (activeMods.id.equalsIgnoreCase(string)) {
				return int1;
			}
		}

		return -1;
	}

	private static ActiveMods create(String string) {
		requireValidId(string);
		if (indexOf(string) != -1) {
			throw new IllegalStateException("id \"" + string + "\" exists");
		} else {
			ActiveMods activeMods = new ActiveMods(string);
			s_activeMods.add(activeMods);
			return activeMods;
		}
	}

	private static void requireValidId(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			throw new IllegalArgumentException("id is null or whitespace");
		}
	}

	public static void setLoadedMods(ActiveMods activeMods) {
		if (activeMods != null) {
			s_loaded.copyFrom(activeMods);
		}
	}

	public static boolean requiresResetLua(ActiveMods activeMods) {
		Objects.requireNonNull(activeMods);
		return !s_loaded.mods.equals(activeMods.mods);
	}

	public static void renderUI() {
		if (DebugOptions.instance.ModRenderLoaded.getValue()) {
			if (!GameWindow.DrawReloadingLua) {
				UIFont uIFont = UIFont.DebugConsole;
				int int1 = TextManager.instance.getFontHeight(uIFont);
				String string = "Active Mods:";
				int int2 = TextManager.instance.MeasureStringX(uIFont, string);
				int int3;
				for (int int4 = 0; int4 < s_loaded.mods.size(); ++int4) {
					String string2 = (String)s_loaded.mods.get(int4);
					int3 = TextManager.instance.MeasureStringX(uIFont, string2);
					int2 = Math.max(int2, int3);
				}

				byte byte1 = 10;
				int2 += byte1 * 2;
				int int5 = Core.width - 20 - int2;
				byte byte2 = 20;
				int int6 = (1 + s_loaded.mods.size()) * int1 + byte1 * 2;
				SpriteRenderer.instance.renderi((Texture)null, int5, byte2, int2, int6, 0.0F, 0.5F, 0.75F, 1.0F, (Consumer)null);
				TextManager.instance.DrawString(uIFont, (double)(int5 + byte1), (double)(int3 = byte2 + byte1), string, 1.0, 1.0, 1.0, 1.0);
				for (int int7 = 0; int7 < s_loaded.mods.size(); ++int7) {
					String string3 = (String)s_loaded.mods.get(int7);
					TextManager.instance.DrawString(uIFont, (double)(int5 + byte1), (double)(int3 += int1), string3, 1.0, 1.0, 1.0, 1.0);
				}
			}
		}
	}

	public static void Reset() {
		s_loaded.clear();
	}

	public ActiveMods(String string) {
		requireValidId(string);
		this.id = string;
	}

	public void clear() {
		this.mods.clear();
		this.mapOrder.clear();
	}

	public ArrayList getMods() {
		return this.mods;
	}

	public ArrayList getMapOrder() {
		return this.mapOrder;
	}

	public void copyFrom(ActiveMods activeMods) {
		this.mods.clear();
		this.mapOrder.clear();
		this.mods.addAll(activeMods.mods);
		this.mapOrder.addAll(activeMods.mapOrder);
	}

	public void setModActive(String string, boolean boolean1) {
		string = string.trim();
		if (!StringUtils.isNullOrWhitespace(string)) {
			if (boolean1) {
				if (!this.mods.contains(string)) {
					this.mods.add(string);
				}
			} else {
				this.mods.remove(string);
			}
		}
	}

	public boolean isModActive(String string) {
		string = string.trim();
		return StringUtils.isNullOrWhitespace(string) ? false : this.mods.contains(string);
	}

	public void removeMod(String string) {
		string = string.trim();
		this.mods.remove(string);
	}

	public void removeMapOrder(String string) {
		this.mapOrder.remove(string);
	}

	public void checkMissingMods() {
		if (!this.mods.isEmpty()) {
			for (int int1 = this.mods.size() - 1; int1 >= 0; --int1) {
				String string = (String)this.mods.get(int1);
				if (ChooseGameInfo.getAvailableModDetails(string) == null) {
					this.mods.remove(int1);
				}
			}
		}
	}

	public void checkMissingMaps() {
		if (!this.mapOrder.isEmpty()) {
			MapGroups mapGroups = new MapGroups();
			mapGroups.createGroups(this, false);
			if (mapGroups.checkMapConflicts()) {
				ArrayList arrayList = mapGroups.getAllMapsInOrder();
				for (int int1 = this.mapOrder.size() - 1; int1 >= 0; --int1) {
					String string = (String)this.mapOrder.get(int1);
					if (!arrayList.contains(string)) {
						this.mapOrder.remove(int1);
					}
				}
			} else {
				this.mapOrder.clear();
			}
		}
	}
}
