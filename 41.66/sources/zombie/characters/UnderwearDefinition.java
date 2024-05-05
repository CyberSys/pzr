package zombie.characters;

import java.util.ArrayList;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.Type;


public class UnderwearDefinition {
	public static final UnderwearDefinition instance = new UnderwearDefinition();
	public boolean m_dirty = true;
	private static final ArrayList m_outfitDefinition = new ArrayList();
	private static int baseChance = 50;

	public void checkDirty() {
		this.init();
	}

	private void init() {
		m_outfitDefinition.clear();
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget("UnderwearDefinition");
		if (kahluaTableImpl != null) {
			baseChance = kahluaTableImpl.rawgetInt("baseChance");
			KahluaTableIterator kahluaTableIterator = kahluaTableImpl.iterator();
			while (true) {
				ArrayList arrayList;
				KahluaTableImpl kahluaTableImpl2;
				do {
					if (!kahluaTableIterator.advance()) {
						return;
					}

					arrayList = null;
					kahluaTableImpl2 = (KahluaTableImpl)Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTableImpl.class);
				}		 while (kahluaTableImpl2 == null);

				KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)Type.tryCastTo(kahluaTableImpl2.rawget("top"), KahluaTableImpl.class);
				if (kahluaTableImpl3 != null) {
					arrayList = new ArrayList();
					KahluaTableIterator kahluaTableIterator2 = kahluaTableImpl3.iterator();
					while (kahluaTableIterator2.advance()) {
						KahluaTableImpl kahluaTableImpl4 = (KahluaTableImpl)Type.tryCastTo(kahluaTableIterator2.getValue(), KahluaTableImpl.class);
						if (kahluaTableImpl4 != null) {
							arrayList.add(new UnderwearDefinition.StringChance(kahluaTableImpl4.rawgetStr("name"), kahluaTableImpl4.rawgetFloat("chance")));
						}
					}
				}

				UnderwearDefinition.OutfitUnderwearDefinition outfitUnderwearDefinition = new UnderwearDefinition.OutfitUnderwearDefinition(arrayList, kahluaTableImpl2.rawgetStr("bottom"), kahluaTableImpl2.rawgetInt("chanceToSpawn"), kahluaTableImpl2.rawgetStr("gender"));
				m_outfitDefinition.add(outfitUnderwearDefinition);
			}
		}
	}

	public static void addRandomUnderwear(IsoZombie zombie) {
		instance.checkDirty();
		if (Rand.Next(100) <= baseChance) {
			ArrayList arrayList = new ArrayList();
			int int1 = 0;
			int int2;
			UnderwearDefinition.OutfitUnderwearDefinition outfitUnderwearDefinition;
			for (int2 = 0; int2 < m_outfitDefinition.size(); ++int2) {
				outfitUnderwearDefinition = (UnderwearDefinition.OutfitUnderwearDefinition)m_outfitDefinition.get(int2);
				if (zombie.isFemale() && outfitUnderwearDefinition.female || !zombie.isFemale() && !outfitUnderwearDefinition.female) {
					arrayList.add(outfitUnderwearDefinition);
					int1 += outfitUnderwearDefinition.chanceToSpawn;
				}
			}

			int2 = OutfitRNG.Next(int1);
			outfitUnderwearDefinition = null;
			int int3 = 0;
			for (int int4 = 0; int4 < arrayList.size(); ++int4) {
				UnderwearDefinition.OutfitUnderwearDefinition outfitUnderwearDefinition2 = (UnderwearDefinition.OutfitUnderwearDefinition)arrayList.get(int4);
				int3 += outfitUnderwearDefinition2.chanceToSpawn;
				if (int2 < int3) {
					outfitUnderwearDefinition = outfitUnderwearDefinition2;
					break;
				}
			}

			if (outfitUnderwearDefinition != null) {
				Item item = ScriptManager.instance.FindItem(outfitUnderwearDefinition.bottom);
				ItemVisual itemVisual = null;
				if (item != null) {
					itemVisual = zombie.getHumanVisual().addClothingItem(zombie.getItemVisuals(), item);
				}

				if (outfitUnderwearDefinition.top != null) {
					String string = null;
					int2 = OutfitRNG.Next(outfitUnderwearDefinition.topTotalChance);
					int3 = 0;
					for (int int5 = 0; int5 < outfitUnderwearDefinition.top.size(); ++int5) {
						UnderwearDefinition.StringChance stringChance = (UnderwearDefinition.StringChance)outfitUnderwearDefinition.top.get(int5);
						int3 = (int)((float)int3 + stringChance.chance);
						if (int2 < int3) {
							string = stringChance.str;
							break;
						}
					}

					if (string != null) {
						item = ScriptManager.instance.FindItem(string);
						if (item != null) {
							ItemVisual itemVisual2 = zombie.getHumanVisual().addClothingItem(zombie.getItemVisuals(), item);
							if (Rand.Next(100) < 60 && itemVisual2 != null && itemVisual != null) {
								itemVisual2.setTint(itemVisual.getTint());
							}
						}
					}
				}
			}
		}
	}

	private static final class StringChance {
		String str;
		float chance;

		public StringChance(String string, float float1) {
			this.str = string;
			this.chance = float1;
		}
	}

	public static final class OutfitUnderwearDefinition {
		public ArrayList top;
		public int topTotalChance = 0;
		public String bottom;
		public int chanceToSpawn;
		public boolean female = false;

		public OutfitUnderwearDefinition(ArrayList arrayList, String string, int int1, String string2) {
			this.top = arrayList;
			if (arrayList != null) {
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					this.topTotalChance = (int)((float)this.topTotalChance + ((UnderwearDefinition.StringChance)arrayList.get(int2)).chance);
				}
			}

			this.bottom = string;
			this.chanceToSpawn = int1;
			if ("female".equals(string2)) {
				this.female = true;
			}
		}
	}
}
