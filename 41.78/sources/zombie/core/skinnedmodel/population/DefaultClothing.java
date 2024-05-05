package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.util.Type;


public final class DefaultClothing {
	public static final DefaultClothing instance = new DefaultClothing();
	public final DefaultClothing.Clothing Pants = new DefaultClothing.Clothing();
	public final DefaultClothing.Clothing TShirt = new DefaultClothing.Clothing();
	public final DefaultClothing.Clothing TShirtDecal = new DefaultClothing.Clothing();
	public final DefaultClothing.Clothing Vest = new DefaultClothing.Clothing();
	public boolean m_dirty = true;

	private void checkDirty() {
		if (this.m_dirty) {
			this.m_dirty = false;
			this.init();
		}
	}

	private void init() {
		this.Pants.clear();
		this.TShirt.clear();
		this.TShirtDecal.clear();
		this.Vest.clear();
		KahluaTable kahluaTable = (KahluaTable)Type.tryCastTo(LuaManager.env.rawget("DefaultClothing"), KahluaTable.class);
		if (kahluaTable != null) {
			this.initClothing(kahluaTable, this.Pants, "Pants");
			this.initClothing(kahluaTable, this.TShirt, "TShirt");
			this.initClothing(kahluaTable, this.TShirtDecal, "TShirtDecal");
			this.initClothing(kahluaTable, this.Vest, "Vest");
		}
	}

	private void initClothing(KahluaTable kahluaTable, DefaultClothing.Clothing clothing, String string) {
		KahluaTable kahluaTable2 = (KahluaTable)Type.tryCastTo(kahluaTable.rawget(string), KahluaTable.class);
		if (kahluaTable2 != null) {
			this.tableToArrayList(kahluaTable2, "hue", clothing.hue);
			this.tableToArrayList(kahluaTable2, "texture", clothing.texture);
			this.tableToArrayList(kahluaTable2, "tint", clothing.tint);
		}
	}

	private void tableToArrayList(KahluaTable kahluaTable, String string, ArrayList arrayList) {
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)kahluaTable.rawget(string);
		if (kahluaTableImpl != null) {
			int int1 = 1;
			for (int int2 = kahluaTableImpl.len(); int1 <= int2; ++int1) {
				Object object = kahluaTableImpl.rawget(int1);
				if (object != null) {
					arrayList.add(object.toString());
				}
			}
		}
	}

	public String pickPantsHue() {
		this.checkDirty();
		return (String)OutfitRNG.pickRandom(this.Pants.hue);
	}

	public String pickPantsTexture() {
		this.checkDirty();
		return (String)OutfitRNG.pickRandom(this.Pants.texture);
	}

	public String pickPantsTint() {
		this.checkDirty();
		return (String)OutfitRNG.pickRandom(this.Pants.tint);
	}

	public String pickTShirtTexture() {
		this.checkDirty();
		return (String)OutfitRNG.pickRandom(this.TShirt.texture);
	}

	public String pickTShirtTint() {
		this.checkDirty();
		return (String)OutfitRNG.pickRandom(this.TShirt.tint);
	}

	public String pickTShirtDecalTexture() {
		this.checkDirty();
		return (String)OutfitRNG.pickRandom(this.TShirtDecal.texture);
	}

	public String pickTShirtDecalTint() {
		this.checkDirty();
		return (String)OutfitRNG.pickRandom(this.TShirtDecal.tint);
	}

	public String pickVestTexture() {
		this.checkDirty();
		return (String)OutfitRNG.pickRandom(this.Vest.texture);
	}

	public String pickVestTint() {
		this.checkDirty();
		return (String)OutfitRNG.pickRandom(this.Vest.tint);
	}

	private static final class Clothing {
		final ArrayList hue = new ArrayList();
		final ArrayList texture = new ArrayList();
		final ArrayList tint = new ArrayList();

		void clear() {
			this.hue.clear();
			this.texture.clear();
			this.tint.clear();
		}
	}
}
