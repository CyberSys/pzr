package zombie.worldMap;

import java.util.ArrayList;
import java.util.List;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;


public final class MapDefinitions {
	private static MapDefinitions instance;
	private final ArrayList m_definitions = new ArrayList();

	public static MapDefinitions getInstance() {
		if (instance == null) {
			instance = new MapDefinitions();
		}

		return instance;
	}

	public String pickRandom() {
		if (this.m_definitions.isEmpty()) {
			this.initDefinitionsFromLua();
		}

		return this.m_definitions.isEmpty() ? "Default" : (String)PZArrayUtil.pickRandom((List)this.m_definitions);
	}

	private void initDefinitionsFromLua() {
		KahluaTable kahluaTable = (KahluaTable)Type.tryCastTo(LuaManager.env.rawget("LootMaps"), KahluaTable.class);
		if (kahluaTable != null) {
			KahluaTable kahluaTable2 = (KahluaTable)Type.tryCastTo(kahluaTable.rawget("Init"), KahluaTable.class);
			if (kahluaTable2 != null) {
				KahluaTableIterator kahluaTableIterator = kahluaTable2.iterator();
				while (kahluaTableIterator.advance()) {
					String string = (String)Type.tryCastTo(kahluaTableIterator.getKey(), String.class);
					if (string != null) {
						this.m_definitions.add(string);
					}
				}
			}
		}
	}

	public static void Reset() {
		if (instance != null) {
			instance.m_definitions.clear();
			instance = null;
		}
	}
}
