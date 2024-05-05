package zombie.characters.AttachedItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoWorld;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;


public final class AttachedWeaponDefinitions {
	public static final AttachedWeaponDefinitions instance = new AttachedWeaponDefinitions();
	public boolean m_dirty = true;
	public int m_chanceOfAttachedWeapon;
	public final ArrayList m_definitions = new ArrayList();
	public final ArrayList m_outfitDefinitions = new ArrayList();

	public void checkDirty() {
		if (this.m_dirty) {
			this.m_dirty = false;
			this.init();
		}
	}

	public void addRandomAttachedWeapon(IsoZombie zombie) {
		if (!"Tutorial".equals(Core.getInstance().getGameMode())) {
			this.checkDirty();
			if (!this.m_definitions.isEmpty()) {
				ArrayList arrayList = AttachedWeaponDefinitions.L_addRandomAttachedWeapon.definitions;
				arrayList.clear();
				int int1 = 1;
				AttachedWeaponCustomOutfit attachedWeaponCustomOutfit = null;
				Outfit outfit = zombie.getHumanVisual().getOutfit();
				if (outfit != null) {
					for (int int2 = 0; int2 < this.m_outfitDefinitions.size(); ++int2) {
						attachedWeaponCustomOutfit = (AttachedWeaponCustomOutfit)this.m_outfitDefinitions.get(int2);
						if (attachedWeaponCustomOutfit.outfit.equals(outfit.m_Name) && OutfitRNG.Next(100) < attachedWeaponCustomOutfit.chance) {
							arrayList.addAll(attachedWeaponCustomOutfit.weapons);
							int1 = attachedWeaponCustomOutfit.maxitem > -1 ? attachedWeaponCustomOutfit.maxitem : 1;
							break;
						}

						attachedWeaponCustomOutfit = null;
					}
				}

				if (arrayList.isEmpty()) {
					if (OutfitRNG.Next(100) > this.m_chanceOfAttachedWeapon) {
						return;
					}

					arrayList.addAll(this.m_definitions);
				}

				do {
					if (int1 <= 0) {
						return;
					}

					AttachedWeaponDefinition attachedWeaponDefinition = this.pickRandomInList(arrayList, zombie);
					if (attachedWeaponDefinition == null) {
						return;
					}

					arrayList.remove(attachedWeaponDefinition);
					--int1;
					this.addAttachedWeapon(attachedWeaponDefinition, zombie);
				}		 while (attachedWeaponCustomOutfit == null || OutfitRNG.Next(100) < attachedWeaponCustomOutfit.chance);
			}
		}
	}

	private void addAttachedWeapon(AttachedWeaponDefinition attachedWeaponDefinition, IsoZombie zombie) {
		String string = (String)OutfitRNG.pickRandom(attachedWeaponDefinition.weapons);
		InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
		if (inventoryItem != null) {
			if (inventoryItem instanceof HandWeapon) {
				((HandWeapon)inventoryItem).randomizeBullets();
			}

			inventoryItem.setCondition(OutfitRNG.Next(Math.max(2, inventoryItem.getConditionMax() - 5), inventoryItem.getConditionMax()));
			zombie.setAttachedItem((String)OutfitRNG.pickRandom(attachedWeaponDefinition.weaponLocation), inventoryItem);
			if (attachedWeaponDefinition.ensureItem != null && !this.outfitHasItem(zombie, attachedWeaponDefinition.ensureItem)) {
				Item item = ScriptManager.instance.FindItem(attachedWeaponDefinition.ensureItem);
				if (item != null && item.getClothingItemAsset() != null) {
					zombie.getHumanVisual().addClothingItem(zombie.getItemVisuals(), item);
				} else {
					zombie.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem(attachedWeaponDefinition.ensureItem));
				}
			}

			if (!attachedWeaponDefinition.bloodLocations.isEmpty()) {
				for (int int1 = 0; int1 < attachedWeaponDefinition.bloodLocations.size(); ++int1) {
					BloodBodyPartType bloodBodyPartType = (BloodBodyPartType)attachedWeaponDefinition.bloodLocations.get(int1);
					zombie.addBlood(bloodBodyPartType, true, true, true);
					zombie.addBlood(bloodBodyPartType, true, true, true);
					zombie.addBlood(bloodBodyPartType, true, true, true);
					if (attachedWeaponDefinition.addHoles) {
						zombie.addHole(bloodBodyPartType);
						zombie.addHole(bloodBodyPartType);
						zombie.addHole(bloodBodyPartType);
						zombie.addHole(bloodBodyPartType);
					}
				}
			}
		}
	}

	private AttachedWeaponDefinition pickRandomInList(ArrayList arrayList, IsoZombie zombie) {
		AttachedWeaponDefinition attachedWeaponDefinition = null;
		int int1 = 0;
		ArrayList arrayList2 = AttachedWeaponDefinitions.L_addRandomAttachedWeapon.possibilities;
		arrayList2.clear();
		int int2;
		for (int2 = 0; int2 < arrayList.size(); ++int2) {
			AttachedWeaponDefinition attachedWeaponDefinition2 = (AttachedWeaponDefinition)arrayList.get(int2);
			if (attachedWeaponDefinition2.daySurvived > 0) {
				if (IsoWorld.instance.getWorldAgeDays() > (float)attachedWeaponDefinition2.daySurvived) {
					int1 += attachedWeaponDefinition2.chance;
					arrayList2.add(attachedWeaponDefinition2);
				}
			} else if (!attachedWeaponDefinition2.outfit.isEmpty()) {
				if (zombie.getHumanVisual().getOutfit() != null && attachedWeaponDefinition2.outfit.contains(zombie.getHumanVisual().getOutfit().m_Name)) {
					int1 += attachedWeaponDefinition2.chance;
					arrayList2.add(attachedWeaponDefinition2);
				}
			} else {
				int1 += attachedWeaponDefinition2.chance;
				arrayList2.add(attachedWeaponDefinition2);
			}
		}

		int2 = OutfitRNG.Next(int1);
		int int3 = 0;
		for (int int4 = 0; int4 < arrayList2.size(); ++int4) {
			AttachedWeaponDefinition attachedWeaponDefinition3 = (AttachedWeaponDefinition)arrayList2.get(int4);
			int3 += attachedWeaponDefinition3.chance;
			if (int2 < int3) {
				attachedWeaponDefinition = attachedWeaponDefinition3;
				break;
			}
		}

		return attachedWeaponDefinition;
	}

	public boolean outfitHasItem(IsoZombie zombie, String string) {
		assert string.contains(".");
		ItemVisuals itemVisuals = zombie.getItemVisuals();
		for (int int1 = 0; int1 < itemVisuals.size(); ++int1) {
			ItemVisual itemVisual = (ItemVisual)itemVisuals.get(int1);
			if (StringUtils.equals(itemVisual.getItemType(), string)) {
				return true;
			}

			if ("Base.HolsterSimple".equals(string) && StringUtils.equals(itemVisual.getItemType(), "Base.HolsterDouble")) {
				return true;
			}

			if ("Base.HolsterDouble".equals(string) && StringUtils.equals(itemVisual.getItemType(), "Base.HolsterSimple")) {
				return true;
			}
		}

		return false;
	}

	private void init() {
		this.m_definitions.clear();
		this.m_outfitDefinitions.clear();
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget("AttachedWeaponDefinitions");
		if (kahluaTableImpl != null) {
			this.m_chanceOfAttachedWeapon = kahluaTableImpl.rawgetInt("chanceOfAttachedWeapon");
			Iterator iterator = kahluaTableImpl.delegate.entrySet().iterator();
			while (true) {
				while (true) {
					Entry entry;
					do {
						if (!iterator.hasNext()) {
							Collections.sort(this.m_definitions, (var0,kahluaTableImplx)->{
								return var0.id.compareTo(kahluaTableImplx.id);
							});

							return;
						}

						entry = (Entry)iterator.next();
					}			 while (!(entry.getValue() instanceof KahluaTableImpl));

					KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry.getValue();
					if ("attachedWeaponCustomOutfit".equals(entry.getKey())) {
						KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)entry.getValue();
						Iterator iterator2 = kahluaTableImpl3.delegate.entrySet().iterator();
						while (iterator2.hasNext()) {
							Entry entry2 = (Entry)iterator2.next();
							AttachedWeaponCustomOutfit attachedWeaponCustomOutfit = this.initOutfit((String)entry2.getKey(), (KahluaTableImpl)entry2.getValue());
							if (attachedWeaponCustomOutfit != null) {
								this.m_outfitDefinitions.add(attachedWeaponCustomOutfit);
							}
						}
					} else {
						AttachedWeaponDefinition attachedWeaponDefinition = this.init((String)entry.getKey(), kahluaTableImpl2);
						if (attachedWeaponDefinition != null) {
							this.m_definitions.add(attachedWeaponDefinition);
						}
					}
				}
			}
		}
	}

	private AttachedWeaponCustomOutfit initOutfit(String string, KahluaTableImpl kahluaTableImpl) {
		AttachedWeaponCustomOutfit attachedWeaponCustomOutfit = new AttachedWeaponCustomOutfit();
		attachedWeaponCustomOutfit.outfit = string;
		attachedWeaponCustomOutfit.chance = kahluaTableImpl.rawgetInt("chance");
		attachedWeaponCustomOutfit.maxitem = kahluaTableImpl.rawgetInt("maxitem");
		KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableImpl.rawget("weapons");
		Iterator iterator = kahluaTableImpl2.delegate.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)entry.getValue();
			AttachedWeaponDefinition attachedWeaponDefinition = this.init(kahluaTableImpl3.rawgetStr("id"), kahluaTableImpl3);
			if (attachedWeaponDefinition != null) {
				attachedWeaponCustomOutfit.weapons.add(attachedWeaponDefinition);
			}
		}

		return attachedWeaponCustomOutfit;
	}

	private AttachedWeaponDefinition init(String string, KahluaTableImpl kahluaTableImpl) {
		AttachedWeaponDefinition attachedWeaponDefinition = new AttachedWeaponDefinition();
		attachedWeaponDefinition.id = string;
		attachedWeaponDefinition.chance = kahluaTableImpl.rawgetInt("chance");
		this.tableToArrayList(kahluaTableImpl, "outfit", attachedWeaponDefinition.outfit);
		this.tableToArrayList(kahluaTableImpl, "weaponLocation", attachedWeaponDefinition.weaponLocation);
		KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableImpl.rawget("bloodLocations");
		if (kahluaTableImpl2 != null) {
			KahluaTableIterator kahluaTableIterator = kahluaTableImpl2.iterator();
			while (kahluaTableIterator.advance()) {
				BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromString(kahluaTableIterator.getValue().toString());
				if (bloodBodyPartType != BloodBodyPartType.MAX) {
					attachedWeaponDefinition.bloodLocations.add(bloodBodyPartType);
				}
			}
		}

		attachedWeaponDefinition.addHoles = kahluaTableImpl.rawgetBool("addHoles");
		attachedWeaponDefinition.daySurvived = kahluaTableImpl.rawgetInt("daySurvived");
		attachedWeaponDefinition.ensureItem = kahluaTableImpl.rawgetStr("ensureItem");
		this.tableToArrayList(kahluaTableImpl, "weapons", attachedWeaponDefinition.weapons);
		Collections.sort(attachedWeaponDefinition.weaponLocation);
		Collections.sort(attachedWeaponDefinition.bloodLocations);
		Collections.sort(attachedWeaponDefinition.weapons);
		return attachedWeaponDefinition;
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

	private static final class L_addRandomAttachedWeapon {
		static final ArrayList possibilities = new ArrayList();
		static final ArrayList definitions = new ArrayList();
	}
}
