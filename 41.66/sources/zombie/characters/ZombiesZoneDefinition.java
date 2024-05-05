package zombie.characters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.PersistentOutfits;
import zombie.Lua.LuaManager;
import zombie.characters.AttachedItems.AttachedWeaponDefinitions;
import zombie.core.Rand;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;


public final class ZombiesZoneDefinition {
	private static final ArrayList s_zoneList = new ArrayList();
	private static final HashMap s_zoneMap = new HashMap();
	public static boolean bDirty = true;
	private static final ZombiesZoneDefinition.PickDefinition pickDef = new ZombiesZoneDefinition.PickDefinition();
	private static final HashMap s_customOutfitMap = new HashMap();

	private static void checkDirty() {
		if (bDirty) {
			bDirty = false;
			init();
		}
	}

	private static void init() {
		s_zoneList.clear();
		s_zoneMap.clear();
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)Type.tryCastTo(LuaManager.env.rawget("ZombiesZoneDefinition"), KahluaTableImpl.class);
		if (kahluaTableImpl != null) {
			KahluaTableIterator kahluaTableIterator = kahluaTableImpl.iterator();
			while (kahluaTableIterator.advance()) {
				KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTableImpl.class);
				if (kahluaTableImpl2 != null) {
					ZombiesZoneDefinition.ZZDZone zZDZone = initZone(kahluaTableIterator.getKey().toString(), kahluaTableImpl2);
					if (zZDZone != null) {
						s_zoneList.add(zZDZone);
						s_zoneMap.put(zZDZone.name, zZDZone);
					}
				}
			}
		}
	}

	private static ZombiesZoneDefinition.ZZDZone initZone(String string, KahluaTableImpl kahluaTableImpl) {
		ZombiesZoneDefinition.ZZDZone zZDZone = new ZombiesZoneDefinition.ZZDZone();
		zZDZone.name = string;
		zZDZone.femaleChance = kahluaTableImpl.rawgetInt("femaleChance");
		zZDZone.maleChance = kahluaTableImpl.rawgetInt("maleChance");
		zZDZone.chanceToSpawn = kahluaTableImpl.rawgetInt("chanceToSpawn");
		zZDZone.toSpawn = kahluaTableImpl.rawgetInt("toSpawn");
		KahluaTableIterator kahluaTableIterator = kahluaTableImpl.iterator();
		while (kahluaTableIterator.advance()) {
			KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTableImpl.class);
			if (kahluaTableImpl2 != null) {
				ZombiesZoneDefinition.ZZDOutfit zZDOutfit = initOutfit(kahluaTableImpl2);
				if (zZDOutfit != null) {
					zZDOutfit.customName = "ZZD." + zZDZone.name + "." + zZDOutfit.name;
					zZDZone.outfits.add(zZDOutfit);
				}
			}
		}

		return zZDZone;
	}

	private static ZombiesZoneDefinition.ZZDOutfit initOutfit(KahluaTableImpl kahluaTableImpl) {
		ZombiesZoneDefinition.ZZDOutfit zZDOutfit = new ZombiesZoneDefinition.ZZDOutfit();
		zZDOutfit.name = kahluaTableImpl.rawgetStr("name");
		zZDOutfit.chance = kahluaTableImpl.rawgetFloat("chance");
		zZDOutfit.gender = kahluaTableImpl.rawgetStr("gender");
		zZDOutfit.toSpawn = kahluaTableImpl.rawgetInt("toSpawn");
		zZDOutfit.mandatory = kahluaTableImpl.rawgetStr("mandatory");
		zZDOutfit.room = kahluaTableImpl.rawgetStr("room");
		zZDOutfit.femaleHairStyles = initStringChance(kahluaTableImpl.rawgetStr("femaleHairStyles"));
		zZDOutfit.maleHairStyles = initStringChance(kahluaTableImpl.rawgetStr("maleHairStyles"));
		zZDOutfit.beardStyles = initStringChance(kahluaTableImpl.rawgetStr("beardStyles"));
		return zZDOutfit;
	}

	private static ArrayList initStringChance(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return null;
		} else {
			ArrayList arrayList = new ArrayList();
			String[] stringArray = string.split(";");
			String[] stringArray2 = stringArray;
			int int1 = stringArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				String string2 = stringArray2[int2];
				String[] stringArray3 = string2.split(":");
				ZombiesZoneDefinition.StringChance stringChance = new ZombiesZoneDefinition.StringChance();
				stringChance.str = stringArray3[0];
				stringChance.chance = Float.parseFloat(stringArray3[1]);
				arrayList.add(stringChance);
			}

			return arrayList;
		}
	}

	public static void dressInRandomOutfit(IsoZombie zombie) {
		if (!zombie.isSkeleton()) {
			IsoGridSquare square = zombie.getCurrentSquare();
			if (square != null) {
				ZombiesZoneDefinition.PickDefinition pickDefinition = pickDefinition(square.x, square.y, square.z, zombie.isFemale());
				if (pickDefinition == null) {
					String string = square.getRoom() == null ? null : square.getRoom().getName();
					Outfit outfit = getRandomDefaultOutfit(zombie.isFemale(), string);
					zombie.dressInPersistentOutfit(outfit.m_Name);
					UnderwearDefinition.addRandomUnderwear(zombie);
				} else {
					applyDefinition(zombie, pickDefinition.zone, pickDefinition.table, pickDefinition.bFemale);
					UnderwearDefinition.addRandomUnderwear(zombie);
				}
			}
		}
	}

	public static IsoMetaGrid.Zone getDefinitionZoneAt(int int1, int int2, int int3) {
		ArrayList arrayList = IsoWorld.instance.MetaGrid.getZonesAt(int1, int2, int3);
		for (int int4 = arrayList.size() - 1; int4 >= 0; --int4) {
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)arrayList.get(int4);
			if ("ZombiesType".equalsIgnoreCase(zone.type) || s_zoneMap.containsKey(zone.type)) {
				return zone;
			}
		}

		return null;
	}

	public static ZombiesZoneDefinition.PickDefinition pickDefinition(int int1, int int2, int int3, boolean boolean1) {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return null;
		} else {
			String string = square.getRoom() == null ? null : square.getRoom().getName();
			checkDirty();
			IsoMetaGrid.Zone zone = getDefinitionZoneAt(int1, int2, int3);
			if (zone == null) {
				return null;
			} else if (zone.spawnSpecialZombies == Boolean.FALSE) {
				return null;
			} else {
				String string2 = StringUtils.isNullOrEmpty(zone.name) ? zone.type : zone.name;
				ZombiesZoneDefinition.ZZDZone zZDZone = (ZombiesZoneDefinition.ZZDZone)s_zoneMap.get(string2);
				if (zZDZone == null) {
					return null;
				} else {
					if (zZDZone.chanceToSpawn != -1) {
						int int4 = zZDZone.chanceToSpawn;
						int int5 = zZDZone.toSpawn;
						ArrayList arrayList = (ArrayList)IsoWorld.instance.getSpawnedZombieZone().get(zone.getName());
						if (arrayList == null) {
							arrayList = new ArrayList();
							IsoWorld.instance.getSpawnedZombieZone().put(zone.getName(), arrayList);
						}

						if (arrayList.contains(zone.id)) {
							zone.spawnSpecialZombies = true;
						}

						if (int5 == -1 || zone.spawnSpecialZombies == null && arrayList.size() < int5) {
							if (Rand.Next(100) < int4) {
								zone.spawnSpecialZombies = true;
								arrayList.add(zone.id);
							} else {
								zone.spawnSpecialZombies = false;
								zone = null;
							}
						}
					}

					if (zone == null) {
						return null;
					} else {
						ArrayList arrayList2 = new ArrayList();
						ArrayList arrayList3 = new ArrayList();
						int int6 = zZDZone.maleChance;
						int int7 = zZDZone.femaleChance;
						if (int6 > 0 && Rand.Next(100) < int6) {
							boolean1 = false;
						}

						if (int7 > 0 && Rand.Next(100) < int7) {
							boolean1 = true;
						}

						for (int int8 = 0; int8 < zZDZone.outfits.size(); ++int8) {
							ZombiesZoneDefinition.ZZDOutfit zZDOutfit = (ZombiesZoneDefinition.ZZDOutfit)zZDZone.outfits.get(int8);
							String string3 = zZDOutfit.gender;
							String string4 = zZDOutfit.room;
							if ((string4 == null || string != null && string4.contains(string)) && (!"male".equalsIgnoreCase(string3) || !boolean1) && (!"female".equalsIgnoreCase(string3) || boolean1)) {
								String string5 = zZDOutfit.name;
								boolean boolean2 = Boolean.parseBoolean(zZDOutfit.mandatory);
								if (boolean2) {
									Integer integer = (Integer)zone.spawnedZombies.get(string5);
									if (integer == null) {
										integer = 0;
									}

									if (integer < zZDOutfit.toSpawn) {
										arrayList2.add(zZDOutfit);
									}
								} else {
									arrayList3.add(zZDOutfit);
								}
							}
						}

						ZombiesZoneDefinition.ZZDOutfit zZDOutfit2;
						if (!arrayList2.isEmpty()) {
							zZDOutfit2 = (ZombiesZoneDefinition.ZZDOutfit)PZArrayUtil.pickRandom((List)arrayList2);
						} else {
							zZDOutfit2 = getRandomOutfitInSetList(arrayList3, true);
						}

						if (zZDOutfit2 == null) {
							return null;
						} else {
							pickDef.table = zZDOutfit2;
							pickDef.bFemale = boolean1;
							pickDef.zone = zone;
							return pickDef;
						}
					}
				}
			}
		}
	}

	public static void applyDefinition(IsoZombie zombie, IsoMetaGrid.Zone zone, ZombiesZoneDefinition.ZZDOutfit zZDOutfit, boolean boolean1) {
		zombie.setFemaleEtc(boolean1);
		Outfit outfit = null;
		if (!boolean1) {
			outfit = OutfitManager.instance.FindMaleOutfit(zZDOutfit.name);
		} else {
			outfit = OutfitManager.instance.FindFemaleOutfit(zZDOutfit.name);
		}

		String string = zZDOutfit.customName;
		if (outfit == null) {
			outfit = OutfitManager.instance.GetRandomOutfit(boolean1);
			string = outfit.m_Name;
		} else if (zone != null) {
			Integer integer = (Integer)zone.spawnedZombies.get(outfit.m_Name);
			if (integer == null) {
				integer = 1;
			}

			zone.spawnedZombies.put(outfit.m_Name, integer + 1);
		}

		if (outfit != null) {
			zombie.dressInPersistentOutfit(outfit.m_Name);
		}

		ModelManager.instance.ResetNextFrame(zombie);
		zombie.advancedAnimator.OnAnimDataChanged(false);
	}

	public static Outfit getRandomDefaultOutfit(boolean boolean1, String string) {
		ArrayList arrayList = new ArrayList();
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("ZombiesZoneDefinition");
		ZombiesZoneDefinition.ZZDZone zZDZone = (ZombiesZoneDefinition.ZZDZone)s_zoneMap.get("Default");
		ZombiesZoneDefinition.ZZDOutfit zZDOutfit;
		for (int int1 = 0; int1 < zZDZone.outfits.size(); ++int1) {
			zZDOutfit = (ZombiesZoneDefinition.ZZDOutfit)zZDZone.outfits.get(int1);
			String string2 = zZDOutfit.gender;
			String string3 = zZDOutfit.room;
			if ((string3 == null || string != null && string3.contains(string)) && (string2 == null || "male".equalsIgnoreCase(string2) && !boolean1 || "female".equalsIgnoreCase(string2) && boolean1)) {
				arrayList.add(zZDOutfit);
			}
		}

		zZDOutfit = getRandomOutfitInSetList(arrayList, false);
		Outfit outfit = null;
		if (zZDOutfit != null) {
			if (boolean1) {
				outfit = OutfitManager.instance.FindFemaleOutfit(zZDOutfit.name);
			} else {
				outfit = OutfitManager.instance.FindMaleOutfit(zZDOutfit.name);
			}
		}

		if (outfit == null) {
			outfit = OutfitManager.instance.GetRandomOutfit(boolean1);
		}

		return outfit;
	}

	public static ZombiesZoneDefinition.ZZDOutfit getRandomOutfitInSetList(ArrayList arrayList, boolean boolean1) {
		float float1 = 0.0F;
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			ZombiesZoneDefinition.ZZDOutfit zZDOutfit = (ZombiesZoneDefinition.ZZDOutfit)arrayList.get(int1);
			float1 += zZDOutfit.chance;
		}

		float float2 = Rand.Next(0.0F, 100.0F);
		if (!boolean1 || float1 > 100.0F) {
			float2 = Rand.Next(0.0F, float1);
		}

		float float3 = 0.0F;
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			ZombiesZoneDefinition.ZZDOutfit zZDOutfit2 = (ZombiesZoneDefinition.ZZDOutfit)arrayList.get(int2);
			float3 += zZDOutfit2.chance;
			if (float2 < float3) {
				return zZDOutfit2;
			}
		}

		return null;
	}

	private static String getRandomHairOrBeard(ArrayList arrayList) {
		float float1 = OutfitRNG.Next(0.0F, 100.0F);
		float float2 = 0.0F;
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			ZombiesZoneDefinition.StringChance stringChance = (ZombiesZoneDefinition.StringChance)arrayList.get(int1);
			float2 += stringChance.chance;
			if (float1 < float2) {
				if ("null".equalsIgnoreCase(stringChance.str)) {
					return "";
				}

				return stringChance.str;
			}
		}

		return null;
	}

	public static void registerCustomOutfits() {
		checkDirty();
		s_customOutfitMap.clear();
		Iterator iterator = s_zoneList.iterator();
		while (iterator.hasNext()) {
			ZombiesZoneDefinition.ZZDZone zZDZone = (ZombiesZoneDefinition.ZZDZone)iterator.next();
			Iterator iterator2 = zZDZone.outfits.iterator();
			while (iterator2.hasNext()) {
				ZombiesZoneDefinition.ZZDOutfit zZDOutfit = (ZombiesZoneDefinition.ZZDOutfit)iterator2.next();
				PersistentOutfits.instance.registerOutfitter(zZDOutfit.customName, true, ZombiesZoneDefinition::ApplyCustomOutfit);
				s_customOutfitMap.put(zZDOutfit.customName, zZDOutfit);
			}
		}
	}

	private static void ApplyCustomOutfit(int int1, String string, IsoGameCharacter gameCharacter) {
		ZombiesZoneDefinition.ZZDOutfit zZDOutfit = (ZombiesZoneDefinition.ZZDOutfit)s_customOutfitMap.get(string);
		boolean boolean1 = (int1 & Integer.MIN_VALUE) != 0;
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
		if (zombie != null) {
			zombie.setFemaleEtc(boolean1);
		}

		gameCharacter.dressInNamedOutfit(zZDOutfit.name);
		if (zombie == null) {
			PersistentOutfits.instance.removeFallenHat(int1, gameCharacter);
		} else {
			AttachedWeaponDefinitions.instance.addRandomAttachedWeapon(zombie);
			zombie.addRandomBloodDirtHolesEtc();
			boolean boolean2 = gameCharacter.isFemale();
			if (boolean2 && zZDOutfit.femaleHairStyles != null) {
				zombie.getHumanVisual().setHairModel(getRandomHairOrBeard(zZDOutfit.femaleHairStyles));
			}

			if (!boolean2 && zZDOutfit.maleHairStyles != null) {
				zombie.getHumanVisual().setHairModel(getRandomHairOrBeard(zZDOutfit.maleHairStyles));
			}

			if (!boolean2 && zZDOutfit.beardStyles != null) {
				zombie.getHumanVisual().setBeardModel(getRandomHairOrBeard(zZDOutfit.beardStyles));
			}

			PersistentOutfits.instance.removeFallenHat(int1, gameCharacter);
		}
	}

	public static int pickPersistentOutfit(IsoGridSquare square) {
		if (!GameServer.bServer) {
			return 0;
		} else {
			boolean boolean1 = Rand.Next(2) == 0;
			ZombiesZoneDefinition.PickDefinition pickDefinition = pickDefinition(square.x, square.y, square.z, boolean1);
			Outfit outfit;
			String string;
			if (pickDefinition == null) {
				string = square.getRoom() == null ? null : square.getRoom().getName();
				outfit = getRandomDefaultOutfit(boolean1, string);
			} else {
				boolean1 = pickDefinition.bFemale;
				string = pickDefinition.table.name;
				if (boolean1) {
					outfit = OutfitManager.instance.FindFemaleOutfit(string);
				} else {
					outfit = OutfitManager.instance.FindMaleOutfit(string);
				}
			}

			if (outfit == null) {
				boolean boolean2 = true;
			} else {
				int int1 = PersistentOutfits.instance.pickOutfit(outfit.m_Name, boolean1);
				if (int1 != 0) {
					return int1;
				}

				boolean boolean3 = true;
			}

			return 0;
		}
	}

	private static final class ZZDZone {
		String name;
		int femaleChance;
		int maleChance;
		int chanceToSpawn;
		int toSpawn;
		final ArrayList outfits = new ArrayList();
	}

	private static final class ZZDOutfit {
		String name;
		String customName;
		float chance;
		int toSpawn;
		String gender;
		String mandatory;
		String room;
		ArrayList femaleHairStyles;
		ArrayList maleHairStyles;
		ArrayList beardStyles;
	}

	private static final class StringChance {
		String str;
		float chance;
	}

	public static final class PickDefinition {
		IsoMetaGrid.Zone zone;
		ZombiesZoneDefinition.ZZDOutfit table;
		boolean bFemale;
	}
}
