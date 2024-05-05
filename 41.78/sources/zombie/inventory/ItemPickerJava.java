package zombie.inventory;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.KahluaUtil;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.stash.StashSystem;
import zombie.debug.DebugLog;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.inventory.types.MapItem;
import zombie.inventory.types.WeaponPart;
import zombie.iso.ContainerOverlays;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.radio.ZomboidRadio;
import zombie.radio.media.MediaData;
import zombie.radio.media.RecordedMedia;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayList;
import zombie.util.list.PZArrayUtil;


public final class ItemPickerJava {
	private static IsoPlayer player;
	private static float OtherLootModifier;
	private static float FoodLootModifier;
	private static float CannedFoodLootModifier;
	private static float WeaponLootModifier;
	private static float RangedWeaponLootModifier;
	private static float AmmoLootModifier;
	private static float LiteratureLootModifier;
	private static float SurvivalGearsLootModifier;
	private static float MedicalLootModifier;
	private static float BagLootModifier;
	private static float MechanicsLootModifier;
	public static float zombieDensityCap = 8.0F;
	public static final ArrayList NoContainerFillRooms = new ArrayList();
	public static final ArrayList WeaponUpgrades = new ArrayList();
	public static final HashMap WeaponUpgradeMap = new HashMap();
	public static final THashMap rooms = new THashMap();
	public static final THashMap containers = new THashMap();
	public static final THashMap ProceduralDistributions = new THashMap();
	public static final THashMap VehicleDistributions = new THashMap();

	public static void Parse() {
		rooms.clear();
		NoContainerFillRooms.clear();
		WeaponUpgradeMap.clear();
		WeaponUpgrades.clear();
		containers.clear();
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget("NoContainerFillRooms");
		Iterator iterator = kahluaTableImpl.delegate.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			String string = entry.getKey().toString();
			NoContainerFillRooms.add(string);
		}

		KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)LuaManager.env.rawget("WeaponUpgrades");
		Iterator iterator2 = kahluaTableImpl2.delegate.entrySet().iterator();
		while (iterator2.hasNext()) {
			Entry entry2 = (Entry)iterator2.next();
			String string2 = entry2.getKey().toString();
			ItemPickerJava.ItemPickerUpgradeWeapons itemPickerUpgradeWeapons = new ItemPickerJava.ItemPickerUpgradeWeapons();
			itemPickerUpgradeWeapons.name = string2;
			WeaponUpgrades.add(itemPickerUpgradeWeapons);
			WeaponUpgradeMap.put(string2, itemPickerUpgradeWeapons);
			KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)entry2.getValue();
			Iterator iterator3 = kahluaTableImpl3.delegate.entrySet().iterator();
			while (iterator3.hasNext()) {
				Entry entry3 = (Entry)iterator3.next();
				String string3 = entry3.getValue().toString();
				itemPickerUpgradeWeapons.Upgrades.add(string3);
			}
		}

		ParseSuburbsDistributions();
		ParseVehicleDistributions();
		ParseProceduralDistributions();
	}

	private static void ParseSuburbsDistributions() {
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget("SuburbsDistributions");
		Iterator iterator = kahluaTableImpl.delegate.entrySet().iterator();
		while (true) {
			label57: while (iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				String string = entry.getKey().toString();
				KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry.getValue();
				if (kahluaTableImpl2.delegate.containsKey("rolls")) {
					ItemPickerJava.ItemPickerContainer itemPickerContainer = ExtractContainersFromLua(kahluaTableImpl2);
					containers.put(string, itemPickerContainer);
				} else {
					ItemPickerJava.ItemPickerRoom itemPickerRoom = new ItemPickerJava.ItemPickerRoom();
					rooms.put(string, itemPickerRoom);
					Iterator iterator2 = kahluaTableImpl2.delegate.entrySet().iterator();
					while (true) {
						while (true) {
							if (!iterator2.hasNext()) {
								continue label57;
							}

							Entry entry2 = (Entry)iterator2.next();
							String string2 = entry2.getKey().toString();
							if (entry2.getValue() instanceof Double) {
								itemPickerRoom.fillRand = ((Double)entry2.getValue()).intValue();
							} else if ("isShop".equals(string2)) {
								itemPickerRoom.isShop = (Boolean)entry2.getValue();
							} else {
								KahluaTableImpl kahluaTableImpl3 = null;
								try {
									kahluaTableImpl3 = (KahluaTableImpl)entry2.getValue();
								} catch (Exception exception) {
									exception.printStackTrace();
								}

								if (kahluaTableImpl3.delegate.containsKey("procedural") || !string2.isEmpty() && kahluaTableImpl3.delegate.containsKey("rolls") && kahluaTableImpl3.delegate.containsKey("items")) {
									ItemPickerJava.ItemPickerContainer itemPickerContainer2 = ExtractContainersFromLua(kahluaTableImpl3);
									itemPickerRoom.Containers.put(string2, itemPickerContainer2);
								} else {
									DebugLog.log("ERROR: SuburbsDistributions[\"" + string + "\"] is broken");
								}
							}
						}
					}
				}
			}

			return;
		}
	}

	private static void ParseVehicleDistributions() {
		VehicleDistributions.clear();
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget("VehicleDistributions");
		if (kahluaTableImpl != null && kahluaTableImpl.rawget(1) instanceof KahluaTableImpl) {
			kahluaTableImpl = (KahluaTableImpl)kahluaTableImpl.rawget(1);
			Iterator iterator = kahluaTableImpl.delegate.entrySet().iterator();
			while (true) {
				Entry entry;
				do {
					do {
						if (!iterator.hasNext()) {
							return;
						}

						entry = (Entry)iterator.next();
					}			 while (!(entry.getKey() instanceof String));
				}		 while (!(entry.getValue() instanceof KahluaTableImpl));

				KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry.getValue();
				ItemPickerJava.VehicleDistribution vehicleDistribution = new ItemPickerJava.VehicleDistribution();
				KahluaTableImpl kahluaTableImpl3;
				if (kahluaTableImpl2.rawget("Normal") instanceof KahluaTableImpl) {
					kahluaTableImpl3 = (KahluaTableImpl)kahluaTableImpl2.rawget("Normal");
					ItemPickerJava.ItemPickerRoom itemPickerRoom = new ItemPickerJava.ItemPickerRoom();
					Iterator iterator2 = kahluaTableImpl3.delegate.entrySet().iterator();
					while (iterator2.hasNext()) {
						Entry entry2 = (Entry)iterator2.next();
						String string = entry2.getKey().toString();
						itemPickerRoom.Containers.put(string, ExtractContainersFromLua((KahluaTableImpl)entry2.getValue()));
					}

					vehicleDistribution.Normal = itemPickerRoom;
				}

				if (kahluaTableImpl2.rawget("Specific") instanceof KahluaTableImpl) {
					kahluaTableImpl3 = (KahluaTableImpl)kahluaTableImpl2.rawget("Specific");
					for (int int1 = 1; int1 <= kahluaTableImpl3.len(); ++int1) {
						KahluaTableImpl kahluaTableImpl4 = (KahluaTableImpl)kahluaTableImpl3.rawget(int1);
						ItemPickerJava.ItemPickerRoom itemPickerRoom2 = new ItemPickerJava.ItemPickerRoom();
						Iterator iterator3 = kahluaTableImpl4.delegate.entrySet().iterator();
						while (iterator3.hasNext()) {
							Entry entry3 = (Entry)iterator3.next();
							String string2 = entry3.getKey().toString();
							if (string2.equals("specificId")) {
								itemPickerRoom2.specificId = (String)entry3.getValue();
							} else {
								itemPickerRoom2.Containers.put(string2, ExtractContainersFromLua((KahluaTableImpl)entry3.getValue()));
							}
						}

						vehicleDistribution.Specific.add(itemPickerRoom2);
					}
				}

				if (vehicleDistribution.Normal != null) {
					VehicleDistributions.put((String)entry.getKey(), vehicleDistribution);
				}
			}
		}
	}

	private static void ParseProceduralDistributions() {
		ProceduralDistributions.clear();
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)Type.tryCastTo(LuaManager.env.rawget("ProceduralDistributions"), KahluaTableImpl.class);
		if (kahluaTableImpl != null) {
			KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)Type.tryCastTo(kahluaTableImpl.rawget("list"), KahluaTableImpl.class);
			if (kahluaTableImpl2 != null) {
				Iterator iterator = kahluaTableImpl2.delegate.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry)iterator.next();
					String string = entry.getKey().toString();
					KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)entry.getValue();
					ItemPickerJava.ItemPickerContainer itemPickerContainer = ExtractContainersFromLua(kahluaTableImpl3);
					ProceduralDistributions.put(string, itemPickerContainer);
				}
			}
		}
	}

	private static ItemPickerJava.ItemPickerContainer ExtractContainersFromLua(KahluaTableImpl kahluaTableImpl) {
		ItemPickerJava.ItemPickerContainer itemPickerContainer = new ItemPickerJava.ItemPickerContainer();
		if (kahluaTableImpl.delegate.containsKey("procedural")) {
			itemPickerContainer.procedural = kahluaTableImpl.rawgetBool("procedural");
			itemPickerContainer.proceduralItems = ExtractProcList(kahluaTableImpl);
			return itemPickerContainer;
		} else {
			if (kahluaTableImpl.delegate.containsKey("noAutoAge")) {
				itemPickerContainer.noAutoAge = kahluaTableImpl.rawgetBool("noAutoAge");
			}

			if (kahluaTableImpl.delegate.containsKey("fillRand")) {
				itemPickerContainer.fillRand = kahluaTableImpl.rawgetInt("fillRand");
			}

			if (kahluaTableImpl.delegate.containsKey("maxMap")) {
				itemPickerContainer.maxMap = kahluaTableImpl.rawgetInt("maxMap");
			}

			if (kahluaTableImpl.delegate.containsKey("stashChance")) {
				itemPickerContainer.stashChance = kahluaTableImpl.rawgetInt("stashChance");
			}

			if (kahluaTableImpl.delegate.containsKey("dontSpawnAmmo")) {
				itemPickerContainer.dontSpawnAmmo = kahluaTableImpl.rawgetBool("dontSpawnAmmo");
			}

			if (kahluaTableImpl.delegate.containsKey("ignoreZombieDensity")) {
				itemPickerContainer.ignoreZombieDensity = kahluaTableImpl.rawgetBool("ignoreZombieDensity");
			}

			double double1 = (Double)kahluaTableImpl.delegate.get("rolls");
			if (kahluaTableImpl.delegate.containsKey("junk")) {
				itemPickerContainer.junk = ExtractContainersFromLua((KahluaTableImpl)kahluaTableImpl.rawget("junk"));
			}

			itemPickerContainer.rolls = (float)((int)double1);
			KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableImpl.delegate.get("items");
			ArrayList arrayList = new ArrayList();
			int int1 = kahluaTableImpl2.len();
			for (int int2 = 0; int2 < int1; int2 += 2) {
				String string = (String)Type.tryCastTo(kahluaTableImpl2.delegate.get(KahluaUtil.toDouble((long)(int2 + 1))), String.class);
				Double Double1 = (Double)Type.tryCastTo(kahluaTableImpl2.delegate.get(KahluaUtil.toDouble((long)(int2 + 2))), Double.class);
				if (string != null && Double1 != null) {
					Item item = ScriptManager.instance.FindItem(string);
					if (item != null && !item.OBSOLETE) {
						ItemPickerJava.ItemPickerItem itemPickerItem = new ItemPickerJava.ItemPickerItem();
						itemPickerItem.itemName = string;
						itemPickerItem.chance = Double1.floatValue();
						arrayList.add(itemPickerItem);
					} else if (Core.bDebug) {
						DebugLog.General.warn("ignoring invalid ItemPicker item type \"%s\"", string);
					}
				}
			}

			itemPickerContainer.Items = (ItemPickerJava.ItemPickerItem[])arrayList.toArray(itemPickerContainer.Items);
			return itemPickerContainer;
		}
	}

	private static ArrayList ExtractProcList(KahluaTableImpl kahluaTableImpl) {
		ArrayList arrayList = new ArrayList();
		KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableImpl.rawget("procList");
		ItemPickerJava.ProceduralItem proceduralItem;
		for (KahluaTableIterator kahluaTableIterator = kahluaTableImpl2.iterator(); kahluaTableIterator.advance(); arrayList.add(proceduralItem)) {
			KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)kahluaTableIterator.getValue();
			proceduralItem = new ItemPickerJava.ProceduralItem();
			proceduralItem.name = kahluaTableImpl3.rawgetStr("name");
			proceduralItem.min = kahluaTableImpl3.rawgetInt("min");
			proceduralItem.max = kahluaTableImpl3.rawgetInt("max");
			proceduralItem.weightChance = kahluaTableImpl3.rawgetInt("weightChance");
			String string = kahluaTableImpl3.rawgetStr("forceForItems");
			String string2 = kahluaTableImpl3.rawgetStr("forceForZones");
			String string3 = kahluaTableImpl3.rawgetStr("forceForTiles");
			String string4 = kahluaTableImpl3.rawgetStr("forceForRooms");
			if (!StringUtils.isNullOrWhitespace(string)) {
				proceduralItem.forceForItems = Arrays.asList(string.split(";"));
			}

			if (!StringUtils.isNullOrWhitespace(string2)) {
				proceduralItem.forceForZones = Arrays.asList(string2.split(";"));
			}

			if (!StringUtils.isNullOrWhitespace(string3)) {
				proceduralItem.forceForTiles = Arrays.asList(string3.split(";"));
			}

			if (!StringUtils.isNullOrWhitespace(string4)) {
				proceduralItem.forceForRooms = Arrays.asList(string4.split(";"));
			}
		}

		return arrayList;
	}

	public static void InitSandboxLootSettings() {
		OtherLootModifier = doSandboxSettings(SandboxOptions.getInstance().OtherLoot.getValue());
		FoodLootModifier = doSandboxSettings(SandboxOptions.getInstance().FoodLoot.getValue());
		WeaponLootModifier = doSandboxSettings(SandboxOptions.getInstance().WeaponLoot.getValue());
		RangedWeaponLootModifier = doSandboxSettings(SandboxOptions.getInstance().RangedWeaponLoot.getValue());
		AmmoLootModifier = doSandboxSettings(SandboxOptions.getInstance().AmmoLoot.getValue());
		CannedFoodLootModifier = doSandboxSettings(SandboxOptions.getInstance().CannedFoodLoot.getValue());
		LiteratureLootModifier = doSandboxSettings(SandboxOptions.getInstance().LiteratureLoot.getValue());
		SurvivalGearsLootModifier = doSandboxSettings(SandboxOptions.getInstance().SurvivalGearsLoot.getValue());
		MedicalLootModifier = doSandboxSettings(SandboxOptions.getInstance().MedicalLoot.getValue());
		MechanicsLootModifier = doSandboxSettings(SandboxOptions.getInstance().MechanicsLoot.getValue());
	}

	private static float doSandboxSettings(int int1) {
		switch (int1) {
		case 1: 
			return 0.0F;
		
		case 2: 
			return 0.05F;
		
		case 3: 
			return 0.2F;
		
		case 4: 
			return 0.6F;
		
		case 5: 
			return 1.0F;
		
		case 6: 
			return 2.0F;
		
		case 7: 
			return 3.0F;
		
		default: 
			return 0.6F;
		
		}
	}

	public static void fillContainer(ItemContainer itemContainer, IsoPlayer player) {
		if (!GameClient.bClient && !"Tutorial".equals(Core.GameMode)) {
			if (itemContainer != null) {
				IsoGridSquare square = itemContainer.getSourceGrid();
				IsoRoom room = null;
				if (square != null) {
					room = square.getRoom();
					ItemPickerJava.ItemPickerContainer itemPickerContainer;
					if (!itemContainer.getType().equals("inventorymale") && !itemContainer.getType().equals("inventoryfemale")) {
						ItemPickerJava.ItemPickerRoom itemPickerRoom = null;
						if (rooms.containsKey("all")) {
							itemPickerRoom = (ItemPickerJava.ItemPickerRoom)rooms.get("all");
						}

						String string;
						if (room != null && rooms.containsKey(room.getName())) {
							string = room.getName();
							ItemPickerJava.ItemPickerRoom itemPickerRoom2 = (ItemPickerJava.ItemPickerRoom)rooms.get(string);
							ItemPickerJava.ItemPickerContainer itemPickerContainer2 = null;
							if (itemPickerRoom2.Containers.containsKey(itemContainer.getType())) {
								itemPickerContainer2 = (ItemPickerJava.ItemPickerContainer)itemPickerRoom2.Containers.get(itemContainer.getType());
							}

							if (itemPickerContainer2 == null && itemPickerRoom2.Containers.containsKey("other")) {
								itemPickerContainer2 = (ItemPickerJava.ItemPickerContainer)itemPickerRoom2.Containers.get("other");
							}

							if (itemPickerContainer2 == null && itemPickerRoom2.Containers.containsKey("all")) {
								itemPickerContainer2 = (ItemPickerJava.ItemPickerContainer)itemPickerRoom2.Containers.get("all");
								string = "all";
							}

							if (itemPickerContainer2 == null) {
								fillContainerType(itemPickerRoom, itemContainer, string, player);
								LuaEventManager.triggerEvent("OnFillContainer", string, itemContainer.getType(), itemContainer);
							} else {
								if (rooms.containsKey(room.getName())) {
									itemPickerRoom = (ItemPickerJava.ItemPickerRoom)rooms.get(room.getName());
								}

								if (itemPickerRoom != null) {
									fillContainerType(itemPickerRoom, itemContainer, room.getName(), player);
									LuaEventManager.triggerEvent("OnFillContainer", room.getName(), itemContainer.getType(), itemContainer);
								}
							}
						} else {
							itemPickerContainer = null;
							if (room != null) {
								string = room.getName();
							} else {
								string = "all";
							}

							fillContainerType(itemPickerRoom, itemContainer, string, player);
							LuaEventManager.triggerEvent("OnFillContainer", string, itemContainer.getType(), itemContainer);
						}
					} else {
						String string2 = itemContainer.getType();
						if (itemContainer.getParent() != null && itemContainer.getParent() instanceof IsoDeadBody) {
							string2 = ((IsoDeadBody)itemContainer.getParent()).getOutfitName();
						}

						for (int int1 = 0; int1 < itemContainer.getItems().size(); ++int1) {
							if (itemContainer.getItems().get(int1) instanceof InventoryContainer) {
								ItemPickerJava.ItemPickerContainer itemPickerContainer3 = (ItemPickerJava.ItemPickerContainer)containers.get(((InventoryItem)itemContainer.getItems().get(int1)).getType());
								if (itemPickerContainer3 != null && Rand.Next(itemPickerContainer3.fillRand) == 0) {
									rollContainerItem((InventoryContainer)itemContainer.getItems().get(int1), (IsoGameCharacter)null, (ItemPickerJava.ItemPickerContainer)containers.get(((InventoryItem)itemContainer.getItems().get(int1)).getType()));
								}
							}
						}

						itemPickerContainer = (ItemPickerJava.ItemPickerContainer)((ItemPickerJava.ItemPickerRoom)rooms.get("all")).Containers.get("Outfit_" + string2);
						if (itemPickerContainer == null) {
							itemPickerContainer = (ItemPickerJava.ItemPickerContainer)((ItemPickerJava.ItemPickerRoom)rooms.get("all")).Containers.get(itemContainer.getType());
						}

						rollItem(itemPickerContainer, itemContainer, true, player, (ItemPickerJava.ItemPickerRoom)null);
					}
				}
			}
		}
	}

	public static void fillContainerType(ItemPickerJava.ItemPickerRoom itemPickerRoom, ItemContainer itemContainer, String string, IsoGameCharacter gameCharacter) {
		boolean boolean1 = true;
		if (NoContainerFillRooms.contains(string)) {
			boolean1 = false;
		}

		ItemPickerJava.ItemPickerContainer itemPickerContainer = null;
		if (itemPickerRoom.Containers.containsKey("all")) {
			itemPickerContainer = (ItemPickerJava.ItemPickerContainer)itemPickerRoom.Containers.get("all");
			rollItem(itemPickerContainer, itemContainer, boolean1, gameCharacter, itemPickerRoom);
		}

		itemPickerContainer = (ItemPickerJava.ItemPickerContainer)itemPickerRoom.Containers.get(itemContainer.getType());
		if (itemPickerContainer == null) {
			itemPickerContainer = (ItemPickerJava.ItemPickerContainer)itemPickerRoom.Containers.get("other");
		}

		if (itemPickerContainer != null) {
			rollItem(itemPickerContainer, itemContainer, boolean1, gameCharacter, itemPickerRoom);
		}
	}

	public static InventoryItem tryAddItemToContainer(ItemContainer itemContainer, String string, ItemPickerJava.ItemPickerContainer itemPickerContainer) {
		Item item = ScriptManager.instance.FindItem(string);
		if (item == null) {
			return null;
		} else if (item.OBSOLETE) {
			return null;
		} else {
			float float1 = item.getActualWeight() * (float)item.getCount();
			if (!itemContainer.hasRoomFor((IsoGameCharacter)null, float1)) {
				return null;
			} else {
				if (itemContainer.getContainingItem() instanceof InventoryContainer) {
					ItemContainer itemContainer2 = itemContainer.getContainingItem().getContainer();
					if (itemContainer2 != null && !itemContainer2.hasRoomFor((IsoGameCharacter)null, float1)) {
						return null;
					}
				}

				return itemContainer.AddItem(string);
			}
		}
	}

	private static void rollProceduralItem(ArrayList arrayList, ItemContainer itemContainer, float float1, IsoGameCharacter gameCharacter, ItemPickerJava.ItemPickerRoom itemPickerRoom) {
		if (itemContainer.getSourceGrid() != null && itemContainer.getSourceGrid().getRoom() != null) {
			HashMap hashMap = itemContainer.getSourceGrid().getRoom().getRoomDef().getProceduralSpawnedContainer();
			HashMap hashMap2 = new HashMap();
			HashMap hashMap3 = new HashMap();
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				ItemPickerJava.ProceduralItem proceduralItem = (ItemPickerJava.ProceduralItem)arrayList.get(int1);
				String string = proceduralItem.name;
				int int2 = proceduralItem.min;
				int int3 = proceduralItem.max;
				int int4 = proceduralItem.weightChance;
				List list = proceduralItem.forceForItems;
				List list2 = proceduralItem.forceForZones;
				List list3 = proceduralItem.forceForTiles;
				List list4 = proceduralItem.forceForRooms;
				if (hashMap.get(string) == null) {
					hashMap.put(string, 0);
				}

				int int5;
				if (list != null) {
					for (int int6 = itemContainer.getSourceGrid().getRoom().getRoomDef().x; int6 < itemContainer.getSourceGrid().getRoom().getRoomDef().x2; ++int6) {
						for (int5 = itemContainer.getSourceGrid().getRoom().getRoomDef().y; int5 < itemContainer.getSourceGrid().getRoom().getRoomDef().y2; ++int5) {
							IsoGridSquare square = itemContainer.getSourceGrid().getCell().getGridSquare(int6, int5, itemContainer.getSourceGrid().z);
							if (square != null) {
								for (int int7 = 0; int7 < square.getObjects().size(); ++int7) {
									IsoObject object = (IsoObject)square.getObjects().get(int7);
									if (list.contains(object.getSprite().name)) {
										hashMap2.clear();
										hashMap2.put(string, -1);
										break;
									}
								}
							}
						}
					}
				} else if (list2 == null) {
					IsoGridSquare square2;
					if (list3 != null) {
						square2 = itemContainer.getSourceGrid();
						if (square2 != null) {
							for (int5 = 0; int5 < square2.getObjects().size(); ++int5) {
								IsoObject object2 = (IsoObject)square2.getObjects().get(int5);
								if (object2.getSprite() != null && list3.contains(object2.getSprite().getName())) {
									hashMap2.clear();
									hashMap2.put(string, -1);
									break;
								}
							}
						}
					} else if (list4 != null) {
						square2 = itemContainer.getSourceGrid();
						if (square2 != null) {
							for (int5 = 0; int5 < list4.size(); ++int5) {
								if (square2.getBuilding().getRandomRoom((String)list4.get(int5)) != null) {
									hashMap2.clear();
									hashMap2.put(string, -1);
									break;
								}
							}
						}
					}
				} else {
					ArrayList arrayList2 = IsoWorld.instance.MetaGrid.getZonesAt(itemContainer.getSourceGrid().x, itemContainer.getSourceGrid().y, 0);
					for (int5 = 0; int5 < arrayList2.size(); ++int5) {
						if ((Integer)hashMap.get(string) < int3 && (list2.contains(((IsoMetaGrid.Zone)arrayList2.get(int5)).type) || list2.contains(((IsoMetaGrid.Zone)arrayList2.get(int5)).name))) {
							hashMap2.clear();
							hashMap2.put(string, -1);
							break;
						}
					}
				}

				if (list == null && list2 == null && list3 == null && list4 == null) {
					if (int2 == 1 && (Integer)hashMap.get(string) == 0) {
						hashMap2.put(string, int4);
					} else if ((Integer)hashMap.get(string) < int3) {
						hashMap3.put(string, int4);
					}
				}
			}

			String string2 = null;
			if (!hashMap2.isEmpty()) {
				string2 = getDistribInHashMap(hashMap2);
			} else if (!hashMap3.isEmpty()) {
				string2 = getDistribInHashMap(hashMap3);
			}

			if (string2 != null) {
				ItemPickerJava.ItemPickerContainer itemPickerContainer = (ItemPickerJava.ItemPickerContainer)ProceduralDistributions.get(string2);
				if (itemPickerContainer != null) {
					if (itemPickerContainer.junk != null) {
						doRollItem(itemPickerContainer.junk, itemContainer, float1, gameCharacter, true, true, itemPickerRoom);
					}

					doRollItem(itemPickerContainer, itemContainer, float1, gameCharacter, true, false, itemPickerRoom);
					hashMap.put(string2, (Integer)hashMap.get(string2) + 1);
				}
			}
		}
	}

	private static String getDistribInHashMap(HashMap hashMap) {
		int int1 = 0;
		int int2 = 0;
		Iterator iterator;
		String string;
		for (iterator = hashMap.keySet().iterator(); iterator.hasNext(); int1 += (Integer)hashMap.get(string)) {
			string = (String)iterator.next();
		}

		int int3;
		if (int1 == -1) {
			int3 = Rand.Next(hashMap.size());
			iterator = hashMap.keySet().iterator();
			for (int int4 = 0; iterator.hasNext(); ++int4) {
				if (int4 == int3) {
					return (String)iterator.next();
				}
			}
		}

		int3 = Rand.Next(int1);
		iterator = hashMap.keySet().iterator();
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			string = (String)iterator.next();
			int int5 = (Integer)hashMap.get(string);
			int2 += int5;
		} while (int2 < int3);

		return string;
	}

	public static void rollItem(ItemPickerJava.ItemPickerContainer itemPickerContainer, ItemContainer itemContainer, boolean boolean1, IsoGameCharacter gameCharacter, ItemPickerJava.ItemPickerRoom itemPickerRoom) {
		if (!GameClient.bClient && !GameServer.bServer) {
			player = IsoPlayer.getInstance();
		}

		if (itemPickerContainer != null && itemContainer != null) {
			float float1 = 0.0F;
			IsoMetaChunk metaChunk = null;
			if (player != null && IsoWorld.instance != null) {
				metaChunk = IsoWorld.instance.getMetaChunk((int)player.getX() / 10, (int)player.getY() / 10);
			} else if (itemContainer.getSourceGrid() != null) {
				metaChunk = IsoWorld.instance.getMetaChunk(itemContainer.getSourceGrid().getX() / 10, itemContainer.getSourceGrid().getY() / 10);
			}

			if (metaChunk != null) {
				float1 = metaChunk.getLootZombieIntensity();
			}

			if (float1 > zombieDensityCap) {
				float1 = zombieDensityCap;
			}

			if (itemPickerContainer.ignoreZombieDensity) {
				float1 = 0.0F;
			}

			if (itemPickerContainer.procedural) {
				rollProceduralItem(itemPickerContainer.proceduralItems, itemContainer, float1, gameCharacter, itemPickerRoom);
			} else {
				if (itemPickerContainer.junk != null) {
					doRollItem(itemPickerContainer.junk, itemContainer, float1, gameCharacter, boolean1, true, itemPickerRoom);
				}

				doRollItem(itemPickerContainer, itemContainer, float1, gameCharacter, boolean1, false, itemPickerRoom);
			}
		}
	}

	public static void doRollItem(ItemPickerJava.ItemPickerContainer itemPickerContainer, ItemContainer itemContainer, float float1, IsoGameCharacter gameCharacter, boolean boolean1, boolean boolean2, ItemPickerJava.ItemPickerRoom itemPickerRoom) {
		boolean boolean3 = false;
		boolean boolean4 = false;
		String string = "";
		if (player != null && gameCharacter != null) {
			boolean3 = gameCharacter.Traits.Lucky.isSet();
			boolean4 = gameCharacter.Traits.Unlucky.isSet();
		}

		for (int int1 = 0; (float)int1 < itemPickerContainer.rolls; ++int1) {
			ItemPickerJava.ItemPickerItem[] itemPickerItemArray = itemPickerContainer.Items;
			for (int int2 = 0; int2 < itemPickerItemArray.length; ++int2) {
				ItemPickerJava.ItemPickerItem itemPickerItem = itemPickerItemArray[int2];
				float float2 = itemPickerItem.chance;
				string = itemPickerItem.itemName;
				if (boolean3) {
					float2 *= 1.1F;
				}

				if (boolean4) {
					float2 *= 0.9F;
				}

				float float3 = getLootModifier(string);
				if (float3 == 0.0F) {
					return;
				}

				if (boolean2) {
					float1 = 0.0F;
					float3 = 1.0F;
					float2 = (float)((double)float2 * 1.4);
				}

				if ((float)Rand.Next(10000) <= float2 * 100.0F * float3 + float1 * 10.0F) {
					InventoryItem inventoryItem = tryAddItemToContainer(itemContainer, string, itemPickerContainer);
					if (inventoryItem == null) {
						return;
					}

					checkStashItem(inventoryItem, itemPickerContainer);
					if (itemContainer.getType().equals("freezer") && inventoryItem instanceof Food && ((Food)inventoryItem).isFreezing()) {
						((Food)inventoryItem).freeze();
					}

					if (inventoryItem instanceof Key) {
						Key key = (Key)inventoryItem;
						key.takeKeyId();
						if (itemContainer.getSourceGrid() != null && itemContainer.getSourceGrid().getBuilding() != null && itemContainer.getSourceGrid().getBuilding().getDef() != null) {
							int int3 = itemContainer.getSourceGrid().getBuilding().getDef().getKeySpawned();
							if (int3 < 2) {
								itemContainer.getSourceGrid().getBuilding().getDef().setKeySpawned(int3 + 1);
							} else {
								itemContainer.Remove(inventoryItem);
							}
						}
					}

					String string2 = inventoryItem.getScriptItem().getRecordedMediaCat();
					if (string2 != null) {
						RecordedMedia recordedMedia = ZomboidRadio.getInstance().getRecordedMedia();
						MediaData mediaData = recordedMedia.getRandomFromCategory(string2);
						if (mediaData == null) {
							itemContainer.Remove(inventoryItem);
							if ("Home-VHS".equalsIgnoreCase(string2)) {
								mediaData = recordedMedia.getRandomFromCategory("Retail-VHS");
								if (mediaData == null) {
									return;
								}

								inventoryItem = itemContainer.AddItem("Base.VHS_Retail");
								if (inventoryItem == null) {
									return;
								}

								inventoryItem.setRecordedMediaData(mediaData);
							}

							return;
						}

						inventoryItem.setRecordedMediaData(mediaData);
					}

					if (WeaponUpgradeMap.containsKey(inventoryItem.getType())) {
						DoWeaponUpgrade(inventoryItem);
					}

					if (!itemPickerContainer.noAutoAge) {
						inventoryItem.setAutoAge();
					}

					boolean boolean5 = false;
					if (itemPickerRoom != null) {
						boolean5 = itemPickerRoom.isShop;
					}

					if (!boolean5 && Rand.Next(100) < 40 && inventoryItem instanceof DrainableComboItem) {
						float float4 = 1.0F / ((DrainableComboItem)inventoryItem).getUseDelta();
						((DrainableComboItem)inventoryItem).setUsedDelta(Rand.Next(1.0F, float4 - 1.0F) * ((DrainableComboItem)inventoryItem).getUseDelta());
					}

					if (!boolean5 && inventoryItem instanceof HandWeapon && Rand.Next(100) < 40) {
						inventoryItem.setCondition(Rand.Next(1, inventoryItem.getConditionMax()));
					}

					if (inventoryItem instanceof HandWeapon && !itemPickerContainer.dontSpawnAmmo && Rand.Next(100) < 90) {
						int int4 = 30;
						HandWeapon handWeapon = (HandWeapon)inventoryItem;
						if (Core.getInstance().getOptionReloadDifficulty() > 1 && !StringUtils.isNullOrEmpty(handWeapon.getMagazineType()) && Rand.Next(100) < 90) {
							if (Rand.NextBool(3)) {
								InventoryItem inventoryItem2 = itemContainer.AddItem(handWeapon.getMagazineType());
								if (Rand.NextBool(5)) {
									inventoryItem2.setCurrentAmmoCount(Rand.Next(1, inventoryItem2.getMaxAmmo()));
								}

								if (!Rand.NextBool(5)) {
									inventoryItem2.setCurrentAmmoCount(inventoryItem2.getMaxAmmo());
								}
							} else {
								if (!StringUtils.isNullOrWhitespace(handWeapon.getMagazineType())) {
									handWeapon.setContainsClip(true);
								}

								if (Rand.NextBool(6)) {
									handWeapon.setCurrentAmmoCount(Rand.Next(1, handWeapon.getMaxAmmo()));
								} else {
									int4 = Rand.Next(60, 100);
								}
							}

							if (handWeapon.haveChamber()) {
								handWeapon.setRoundChambered(true);
							}
						}

						if (Core.getInstance().getOptionReloadDifficulty() == 1 || StringUtils.isNullOrEmpty(handWeapon.getMagazineType()) && Rand.Next(100) < 30) {
							handWeapon.setCurrentAmmoCount(Rand.Next(1, handWeapon.getMaxAmmo()));
							if (handWeapon.haveChamber()) {
								handWeapon.setRoundChambered(true);
							}
						}

						if (!StringUtils.isNullOrEmpty(handWeapon.getAmmoBox()) && Rand.Next(100) < int4) {
							itemContainer.AddItem(handWeapon.getAmmoBox());
						} else if (!StringUtils.isNullOrEmpty(handWeapon.getAmmoType()) && Rand.Next(100) < 50) {
							itemContainer.AddItems(handWeapon.getAmmoType(), Rand.Next(1, 5));
						}
					}

					if (inventoryItem instanceof InventoryContainer && containers.containsKey(inventoryItem.getType())) {
						ItemPickerJava.ItemPickerContainer itemPickerContainer2 = (ItemPickerJava.ItemPickerContainer)containers.get(inventoryItem.getType());
						if (boolean1 && Rand.Next(itemPickerContainer2.fillRand) == 0) {
							rollContainerItem((InventoryContainer)inventoryItem, gameCharacter, (ItemPickerJava.ItemPickerContainer)containers.get(inventoryItem.getType()));
						}
					}
				}
			}
		}
	}

	private static void checkStashItem(InventoryItem inventoryItem, ItemPickerJava.ItemPickerContainer itemPickerContainer) {
		if (itemPickerContainer.stashChance > 0 && inventoryItem instanceof MapItem && !StringUtils.isNullOrEmpty(((MapItem)inventoryItem).getMapID())) {
			inventoryItem.setStashChance(itemPickerContainer.stashChance);
		}

		StashSystem.checkStashItem(inventoryItem);
	}

	public static void rollContainerItem(InventoryContainer inventoryContainer, IsoGameCharacter gameCharacter, ItemPickerJava.ItemPickerContainer itemPickerContainer) {
		if (itemPickerContainer != null) {
			ItemContainer itemContainer = inventoryContainer.getInventory();
			float float1 = 0.0F;
			IsoMetaChunk metaChunk = null;
			if (player != null && IsoWorld.instance != null) {
				metaChunk = IsoWorld.instance.getMetaChunk((int)player.getX() / 10, (int)player.getY() / 10);
			}

			if (metaChunk != null) {
				float1 = metaChunk.getLootZombieIntensity();
			}

			if (float1 > zombieDensityCap) {
				float1 = zombieDensityCap;
			}

			if (itemPickerContainer.ignoreZombieDensity) {
				float1 = 0.0F;
			}

			boolean boolean1 = false;
			boolean boolean2 = false;
			String string = "";
			if (player != null && gameCharacter != null) {
				boolean1 = gameCharacter.Traits.Lucky.isSet();
				boolean2 = gameCharacter.Traits.Unlucky.isSet();
			}

			for (int int1 = 0; (float)int1 < itemPickerContainer.rolls; ++int1) {
				ItemPickerJava.ItemPickerItem[] itemPickerItemArray = itemPickerContainer.Items;
				for (int int2 = 0; int2 < itemPickerItemArray.length; ++int2) {
					ItemPickerJava.ItemPickerItem itemPickerItem = itemPickerItemArray[int2];
					float float2 = itemPickerItem.chance;
					string = itemPickerItem.itemName;
					if (boolean1) {
						float2 *= 1.1F;
					}

					if (boolean2) {
						float2 *= 0.9F;
					}

					float float3 = getLootModifier(string);
					if ((float)Rand.Next(10000) <= float2 * 100.0F * float3 + float1 * 10.0F) {
						InventoryItem inventoryItem = tryAddItemToContainer(itemContainer, string, itemPickerContainer);
						if (inventoryItem == null) {
							return;
						}

						MapItem mapItem = (MapItem)Type.tryCastTo(inventoryItem, MapItem.class);
						int int3;
						if (mapItem != null && !StringUtils.isNullOrEmpty(mapItem.getMapID()) && itemPickerContainer.maxMap > 0) {
							int int4 = 0;
							for (int3 = 0; int3 < itemContainer.getItems().size(); ++int3) {
								MapItem mapItem2 = (MapItem)Type.tryCastTo((InventoryItem)itemContainer.getItems().get(int3), MapItem.class);
								if (mapItem2 != null && !StringUtils.isNullOrEmpty(mapItem2.getMapID())) {
									++int4;
								}
							}

							if (int4 > itemPickerContainer.maxMap) {
								itemContainer.Remove(inventoryItem);
							}
						}

						checkStashItem(inventoryItem, itemPickerContainer);
						if (itemContainer.getType().equals("freezer") && inventoryItem instanceof Food && ((Food)inventoryItem).isFreezing()) {
							((Food)inventoryItem).freeze();
						}

						if (inventoryItem instanceof Key) {
							Key key = (Key)inventoryItem;
							key.takeKeyId();
							if (itemContainer.getSourceGrid() != null && itemContainer.getSourceGrid().getBuilding() != null && itemContainer.getSourceGrid().getBuilding().getDef() != null) {
								int3 = itemContainer.getSourceGrid().getBuilding().getDef().getKeySpawned();
								if (int3 < 2) {
									itemContainer.getSourceGrid().getBuilding().getDef().setKeySpawned(int3 + 1);
								} else {
									itemContainer.Remove(inventoryItem);
								}
							}
						}

						if (!itemContainer.getType().equals("freezer")) {
							inventoryItem.setAutoAge();
						}
					}
				}
			}
		}
	}

	private static void DoWeaponUpgrade(InventoryItem inventoryItem) {
		ItemPickerJava.ItemPickerUpgradeWeapons itemPickerUpgradeWeapons = (ItemPickerJava.ItemPickerUpgradeWeapons)WeaponUpgradeMap.get(inventoryItem.getType());
		if (itemPickerUpgradeWeapons != null) {
			if (itemPickerUpgradeWeapons.Upgrades.size() != 0) {
				int int1 = Rand.Next(itemPickerUpgradeWeapons.Upgrades.size());
				for (int int2 = 0; int2 < int1; ++int2) {
					String string = (String)PZArrayUtil.pickRandom((List)itemPickerUpgradeWeapons.Upgrades);
					InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem(string);
					((HandWeapon)inventoryItem).attachWeaponPart((WeaponPart)inventoryItem2);
				}
			}
		}
	}

	public static float getLootModifier(String string) {
		Item item = ScriptManager.instance.FindItem(string);
		if (item == null) {
			return 0.6F;
		} else {
			float float1 = OtherLootModifier;
			if (item.getType() == Item.Type.Food) {
				if (item.CannedFood) {
					float1 = CannedFoodLootModifier;
				} else {
					float1 = FoodLootModifier;
				}
			}

			if ("Ammo".equals(item.getDisplayCategory())) {
				float1 = AmmoLootModifier;
			}

			if (item.getType() == Item.Type.Weapon && !item.isRanged()) {
				float1 = WeaponLootModifier;
			}

			if (item.getType() == Item.Type.WeaponPart || item.getType() == Item.Type.Weapon && item.isRanged() || item.getType() == Item.Type.Normal && !StringUtils.isNullOrEmpty(item.getAmmoType())) {
				float1 = RangedWeaponLootModifier;
			}

			if (item.getType() == Item.Type.Literature) {
				float1 = LiteratureLootModifier;
			}

			if (item.Medical) {
				float1 = MedicalLootModifier;
			}

			if (item.SurvivalGear) {
				float1 = SurvivalGearsLootModifier;
			}

			if (item.MechanicsItem) {
				float1 = MechanicsLootModifier;
			}

			return float1;
		}
	}

	public static void updateOverlaySprite(IsoObject object) {
		ContainerOverlays.instance.updateContainerOverlaySprite(object);
	}

	public static void doOverlaySprite(IsoGridSquare square) {
		if (!GameClient.bClient) {
			if (square != null && square.getRoom() != null && !square.isOverlayDone()) {
				PZArrayList pZArrayList = square.getObjects();
				for (int int1 = 0; int1 < pZArrayList.size(); ++int1) {
					IsoObject object = (IsoObject)pZArrayList.get(int1);
					if (object != null && object.getContainer() != null && !object.getContainer().isExplored()) {
						fillContainer(object.getContainer(), IsoPlayer.getInstance());
						object.getContainer().setExplored(true);
						if (GameServer.bServer) {
							LuaManager.GlobalObject.sendItemsInContainer(object, object.getContainer());
						}
					}

					updateOverlaySprite(object);
				}

				square.setOverlayDone(true);
			}
		}
	}

	public static ItemPickerJava.ItemPickerContainer getItemContainer(String string, String string2, String string3, boolean boolean1) {
		ItemPickerJava.ItemPickerRoom itemPickerRoom = (ItemPickerJava.ItemPickerRoom)rooms.get(string);
		if (itemPickerRoom == null) {
			return null;
		} else {
			ItemPickerJava.ItemPickerContainer itemPickerContainer = (ItemPickerJava.ItemPickerContainer)itemPickerRoom.Containers.get(string2);
			if (itemPickerContainer != null && itemPickerContainer.procedural) {
				ArrayList arrayList = itemPickerContainer.proceduralItems;
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					ItemPickerJava.ProceduralItem proceduralItem = (ItemPickerJava.ProceduralItem)arrayList.get(int1);
					if (string3.equals(proceduralItem.name)) {
						ItemPickerJava.ItemPickerContainer itemPickerContainer2 = (ItemPickerJava.ItemPickerContainer)ProceduralDistributions.get(string3);
						if (itemPickerContainer2.junk != null && boolean1) {
							return itemPickerContainer2.junk;
						}

						if (!boolean1) {
							return itemPickerContainer2;
						}
					}
				}
			}

			return boolean1 ? itemPickerContainer.junk : itemPickerContainer;
		}
	}

	public static final class ItemPickerUpgradeWeapons {
		public String name;
		public ArrayList Upgrades = new ArrayList();
	}

	public static final class ItemPickerContainer {
		public ItemPickerJava.ItemPickerItem[] Items = new ItemPickerJava.ItemPickerItem[0];
		public float rolls;
		public boolean noAutoAge;
		public int fillRand;
		public int maxMap;
		public int stashChance;
		public ItemPickerJava.ItemPickerContainer junk;
		public boolean procedural;
		public boolean dontSpawnAmmo = false;
		public boolean ignoreZombieDensity = false;
		public ArrayList proceduralItems;
	}

	public static final class ItemPickerRoom {
		public THashMap Containers = new THashMap();
		public int fillRand;
		public boolean isShop;
		public String specificId = null;
	}

	public static final class VehicleDistribution {
		public ItemPickerJava.ItemPickerRoom Normal;
		public final ArrayList Specific = new ArrayList();
	}

	public static final class ItemPickerItem {
		public String itemName;
		public float chance;
	}

	public static final class ProceduralItem {
		public String name;
		public int min;
		public int max;
		public List forceForItems;
		public List forceForZones;
		public List forceForTiles;
		public List forceForRooms;
		public int weightChance;
	}
}
