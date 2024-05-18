package zombie.inventory;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.core.stash.StashSystem;
import zombie.debug.DebugLog;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.inventory.types.WeaponPart;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoStove;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.list.PZArrayList;


public class ItemPickerJava {
	private static IsoPlayer player;
	private static float OtherLootModifier;
	private static float FoodLootModifier;
	private static float WeaponLootModifier;
	public static float zombieDensityCap = 8.0F;
	public static ArrayList NoContainerFillRooms = new ArrayList();
	public static ArrayList WeaponUpgrades = new ArrayList();
	public static HashMap WeaponUpgradeMap = new HashMap();
	public static THashMap rooms = new THashMap();
	public static THashMap containers = new THashMap();
	public static THashMap overlayMap = new THashMap();

	public static void Parse() {
		rooms.clear();
		NoContainerFillRooms.clear();
		WeaponUpgradeMap.clear();
		WeaponUpgrades.clear();
		containers.clear();
		InitSandboxLootSettings();
		ParseOverlayMap();
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget("NoContainerFillRooms");
		Iterator iterator = kahluaTableImpl.delegate.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			String string = entry.getKey().toString();
			NoContainerFillRooms.add(string);
		}

		KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)LuaManager.env.rawget("WeaponUpgrades");
		Iterator iterator2 = kahluaTableImpl2.delegate.entrySet().iterator();
		KahluaTableImpl kahluaTableImpl3;
		while (iterator2.hasNext()) {
			Entry entry2 = (Entry)iterator2.next();
			String string2 = entry2.getKey().toString();
			ItemPickerJava.ItemPickerUpgradeWeapons itemPickerUpgradeWeapons = new ItemPickerJava.ItemPickerUpgradeWeapons();
			itemPickerUpgradeWeapons.name = string2;
			WeaponUpgrades.add(itemPickerUpgradeWeapons);
			WeaponUpgradeMap.put(string2, itemPickerUpgradeWeapons);
			kahluaTableImpl3 = (KahluaTableImpl)entry2.getValue();
			Iterator iterator3 = kahluaTableImpl3.delegate.entrySet().iterator();
			while (iterator3.hasNext()) {
				Entry entry3 = (Entry)iterator3.next();
				String string3 = entry3.getValue().toString();
				itemPickerUpgradeWeapons.Upgrades.add(string3);
			}
		}

		KahluaTableImpl kahluaTableImpl4 = (KahluaTableImpl)LuaManager.env.rawget("SuburbsDistributions");
		Iterator iterator4 = kahluaTableImpl4.delegate.entrySet().iterator();
		while (true) {
			label46: while (iterator4.hasNext()) {
				Entry entry4 = (Entry)iterator4.next();
				String string4 = entry4.getKey().toString();
				kahluaTableImpl3 = (KahluaTableImpl)entry4.getValue();
				if (kahluaTableImpl3.delegate.containsKey("rolls")) {
					ItemPickerJava.ItemPickerContainer itemPickerContainer = ExtractContainersFromLua(kahluaTableImpl3);
					containers.put(string4, itemPickerContainer);
				} else {
					ItemPickerJava.ItemPickerRoom itemPickerRoom = new ItemPickerJava.ItemPickerRoom();
					rooms.put(string4, itemPickerRoom);
					Iterator iterator5 = kahluaTableImpl3.delegate.entrySet().iterator();
					while (true) {
						while (true) {
							if (!iterator5.hasNext()) {
								continue label46;
							}

							Entry entry5 = (Entry)iterator5.next();
							String string5 = entry5.getKey().toString();
							if (entry5.getValue() instanceof Double) {
								itemPickerRoom.fillRand = ((Double)entry5.getValue()).intValue();
							} else {
								KahluaTableImpl kahluaTableImpl5 = (KahluaTableImpl)entry5.getValue();
								if (!string5.isEmpty() && kahluaTableImpl5.delegate.containsKey("rolls") && kahluaTableImpl5.delegate.containsKey("items")) {
									ItemPickerJava.ItemPickerContainer itemPickerContainer2 = ExtractContainersFromLua(kahluaTableImpl5);
									itemPickerRoom.Containers.put(string5, itemPickerContainer2);
								} else {
									DebugLog.log("ERROR: SuburbsDistributions[\"" + string4 + "\"] is broken");
								}
							}
						}
					}
				}
			}

			return;
		}
	}

	private static void ParseOverlayMap() {
		overlayMap.clear();
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget("overlayMap");
		Iterator iterator = kahluaTableImpl.delegate.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			String string = entry.getKey().toString();
			ItemPickerJava.Overlay overlay = new ItemPickerJava.Overlay();
			overlay.name = string;
			overlayMap.put(overlay.name, overlay);
			KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry.getValue();
			Iterator iterator2 = kahluaTableImpl2.delegate.entrySet().iterator();
			while (iterator2.hasNext()) {
				Entry entry2 = (Entry)iterator2.next();
				String string2 = entry2.getKey().toString();
				KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)entry2.getValue();
				String string3 = null;
				if (kahluaTableImpl3.delegate.containsKey(1.0)) {
					string3 = kahluaTableImpl3.rawget(1.0).toString();
				}

				String string4 = null;
				if (kahluaTableImpl3.delegate.containsKey(2.0)) {
					string4 = kahluaTableImpl3.rawget(2.0).toString();
				}

				ItemPickerJava.OverlayEntry overlayEntry = new ItemPickerJava.OverlayEntry();
				overlayEntry.a = string3;
				overlayEntry.b = string4;
				overlayEntry.room = string2;
				overlay.entries.put(overlayEntry.room, overlayEntry);
			}
		}
	}

	private static ItemPickerJava.ItemPickerContainer ExtractContainersFromLua(KahluaTableImpl kahluaTableImpl) {
		ItemPickerJava.ItemPickerContainer itemPickerContainer = new ItemPickerJava.ItemPickerContainer();
		ArrayList arrayList = new ArrayList();
		if (kahluaTableImpl.delegate.containsKey("noAutoAge")) {
			itemPickerContainer.noAutoAge = kahluaTableImpl.rawgetBool("noAutoAge");
		}

		if (kahluaTableImpl.delegate.containsKey("fillRand")) {
			itemPickerContainer.fillRand = kahluaTableImpl.rawgetInt("fillRand");
		}

		double double1 = (Double)kahluaTableImpl.delegate.get("rolls");
		itemPickerContainer.rolls = (float)((int)double1);
		KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableImpl.delegate.get("items");
		boolean boolean1 = false;
		double double2 = 0.0;
		String string = "";
		double double3 = 1.0;
		if (kahluaTableImpl2.delegate.containsKey(double3)) {
			do {
				Object object = kahluaTableImpl2.delegate.get(double3);
				if (boolean1) {
					double2 = (Double)object;
				} else {
					string = object.toString();
				}

				if (boolean1) {
					ItemPickerJava.ItemPickerItem itemPickerItem = new ItemPickerJava.ItemPickerItem();
					itemPickerItem.itemName = string;
					itemPickerItem.chance = (float)double2;
					arrayList.add(itemPickerItem);
				}

				boolean1 = !boolean1;
				++double3;
			}	 while (kahluaTableImpl2.delegate.containsKey(double3));
		}

		itemPickerContainer.Items = (ItemPickerJava.ItemPickerItem[])arrayList.toArray(itemPickerContainer.Items);
		return itemPickerContainer;
	}

	private static void InitSandboxLootSettings() {
		switch (SandboxOptions.getInstance().getOtherLootModifier()) {
		case 1: 
			OtherLootModifier = 0.2F;
			break;
		
		case 2: 
			OtherLootModifier = 0.6F;
			break;
		
		case 3: 
			OtherLootModifier = 1.0F;
			break;
		
		case 4: 
			OtherLootModifier = 2.0F;
			break;
		
		case 5: 
			OtherLootModifier = 3.0F;
		
		}
		switch (SandboxOptions.getInstance().getFoodLootModifier()) {
		case 1: 
			FoodLootModifier = 0.2F;
			break;
		
		case 2: 
			FoodLootModifier = 0.6F;
			break;
		
		case 3: 
			FoodLootModifier = 1.0F;
			break;
		
		case 4: 
			FoodLootModifier = 2.0F;
			break;
		
		case 5: 
			FoodLootModifier = 3.0F;
		
		}
		switch (SandboxOptions.getInstance().getWeaponLootModifier()) {
		case 1: 
			WeaponLootModifier = 0.2F;
			break;
		
		case 2: 
			WeaponLootModifier = 0.6F;
			break;
		
		case 3: 
			WeaponLootModifier = 1.0F;
			break;
		
		case 4: 
			WeaponLootModifier = 2.0F;
			break;
		
		case 5: 
			WeaponLootModifier = 3.0F;
		
		}
	}

	public static void fillContainer(ItemContainer itemContainer, IsoPlayer player) {
		if (!GameClient.bClient) {
			if (itemContainer != null) {
				IsoGridSquare square = itemContainer.getSourceGrid();
				IsoRoom room = square.getRoom();
				if (itemContainer.getType().equals("inventorymale") || itemContainer.getType().equals("inventoryfemale")) {
					ItemPickerJava.ItemPickerContainer itemPickerContainer = (ItemPickerJava.ItemPickerContainer)((ItemPickerJava.ItemPickerRoom)rooms.get("all")).Containers.get(itemContainer.getType());
					rollItem(itemPickerContainer, itemContainer, true, player);
				}

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
					} else if (room != null) {
						if (rooms.containsKey(room.getName())) {
							itemPickerRoom = (ItemPickerJava.ItemPickerRoom)rooms.get(room.getName());
						}

						if (itemPickerRoom != null) {
							fillContainerType(itemPickerRoom, itemContainer, room.getName(), player);
							LuaEventManager.triggerEvent("OnFillContainer", room.getName(), itemContainer.getType(), itemContainer);
						}
					}
				} else {
					string = null;
					if (room != null) {
						string = room.getName();
					} else {
						string = "all";
					}

					fillContainerType(itemPickerRoom, itemContainer, string, player);
					LuaEventManager.triggerEvent("OnFillContainer", string, itemContainer.getType(), itemContainer);
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
			rollItem(itemPickerContainer, itemContainer, boolean1, gameCharacter);
		}

		itemPickerContainer = (ItemPickerJava.ItemPickerContainer)itemPickerRoom.Containers.get(itemContainer.getType());
		if (itemPickerContainer == null) {
			itemPickerContainer = (ItemPickerJava.ItemPickerContainer)itemPickerRoom.Containers.get("other");
		}

		if (itemPickerContainer != null) {
			rollItem(itemPickerContainer, itemContainer, boolean1, gameCharacter);
		}
	}

	public static InventoryItem tryAddItemToContainer(ItemContainer itemContainer, String string) {
		Item item = ScriptManager.instance.FindItem(string);
		if (item == null) {
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

	public static void rollItem(ItemPickerJava.ItemPickerContainer itemPickerContainer, ItemContainer itemContainer, boolean boolean1, IsoGameCharacter gameCharacter) {
		if (!GameClient.bClient && !GameServer.bServer) {
			player = IsoPlayer.getInstance();
		}

		if (itemPickerContainer != null && itemContainer != null) {
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

			boolean boolean2 = false;
			boolean boolean3 = false;
			String string = "";
			if (player != null && gameCharacter != null) {
				boolean2 = gameCharacter.HasTrait("Lucky");
				boolean3 = gameCharacter.HasTrait("Unlucky");
			}

			for (int int1 = 0; (float)int1 < itemPickerContainer.rolls; ++int1) {
				ItemPickerJava.ItemPickerItem[] itemPickerItemArray = itemPickerContainer.Items;
				for (int int2 = 0; int2 < itemPickerItemArray.length; ++int2) {
					ItemPickerJava.ItemPickerItem itemPickerItem = itemPickerItemArray[int2];
					float float2 = itemPickerItem.chance;
					string = itemPickerItem.itemName;
					if (boolean2) {
						float2 *= 1.1F;
					}

					if (boolean3) {
						float2 *= 0.9F;
					}

					float float3 = getLootModifier(string);
					if ((float)Rand.Next(10000) <= float2 * 100.0F * float3 + float1 * 10.0F) {
						InventoryItem inventoryItem = tryAddItemToContainer(itemContainer, string);
						if (inventoryItem == null) {
							return;
						}

						StashSystem.checkStashItem(inventoryItem);
						if (itemContainer.getType().equals("freezer") && inventoryItem instanceof Food && ((Food)inventoryItem).isFreezing()) {
							((Food)((Food)inventoryItem)).freeze();
						}

						if (inventoryItem instanceof Key) {
							Key key = (Key)inventoryItem;
							key.takeKeyId();
							key.setName("Key " + key.getKeyId());
							if (itemContainer.getSourceGrid() != null && itemContainer.getSourceGrid().getBuilding() != null && itemContainer.getSourceGrid().getBuilding().getDef() != null) {
								int int3 = itemContainer.getSourceGrid().getBuilding().getDef().getKeySpawned();
								if (int3 < 2) {
									itemContainer.getSourceGrid().getBuilding().getDef().setKeySpawned(int3 + 1);
								} else {
									itemContainer.Remove(inventoryItem);
								}
							}
						}

						if (WeaponUpgradeMap.containsKey(inventoryItem.getType())) {
							DoWeaponUpgrade(inventoryItem);
						}

						if (!itemPickerContainer.noAutoAge) {
							inventoryItem.setAutoAge();
						}

						if (Rand.Next(100) < 40 && inventoryItem instanceof DrainableComboItem) {
							float float4 = 1.0F / ((DrainableComboItem)inventoryItem).getUseDelta();
							((DrainableComboItem)inventoryItem).setUsedDelta(Rand.Next(1.0F, float4 - 1.0F) * ((DrainableComboItem)inventoryItem).getUseDelta());
						}

						if (inventoryItem instanceof HandWeapon && Rand.Next(100) < 40) {
							inventoryItem.setCondition(Rand.Next(1, inventoryItem.getConditionMax()));
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

			boolean boolean1 = false;
			boolean boolean2 = false;
			String string = "";
			if (player != null && gameCharacter != null) {
				boolean1 = gameCharacter.HasTrait("Lucky");
				boolean2 = gameCharacter.HasTrait("Unlucky");
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
						InventoryItem inventoryItem = tryAddItemToContainer(itemContainer, string);
						if (inventoryItem == null) {
							return;
						}

						StashSystem.checkStashItem(inventoryItem);
						if (itemContainer.getType().equals("freezer") && inventoryItem instanceof Food && ((Food)inventoryItem).isFreezing()) {
							((Food)((Food)inventoryItem)).freeze();
						}

						if (inventoryItem instanceof Key) {
							Key key = (Key)inventoryItem;
							key.takeKeyId();
							key.setName("Key " + key.getKeyId());
							if (itemContainer.getSourceGrid() != null && itemContainer.getSourceGrid().getBuilding() != null && itemContainer.getSourceGrid().getBuilding().getDef() != null) {
								int int3 = itemContainer.getSourceGrid().getBuilding().getDef().getKeySpawned();
								if (int3 < 2) {
									itemContainer.getSourceGrid().getBuilding().getDef().setKeySpawned(int3 + 1);
								} else {
									itemContainer.Remove(inventoryItem);
								}
							}
						}
					}
				}
			}
		}
	}

	private static void DoWeaponUpgrade(InventoryItem inventoryItem) {
		ItemPickerJava.ItemPickerUpgradeWeapons itemPickerUpgradeWeapons = (ItemPickerJava.ItemPickerUpgradeWeapons)WeaponUpgradeMap.get(inventoryItem.getName());
		if (itemPickerUpgradeWeapons != null) {
			if (itemPickerUpgradeWeapons.Upgrades.size() != 0) {
				int int1 = Rand.Next(itemPickerUpgradeWeapons.Upgrades.size());
				for (int int2 = 0; int2 < int1; ++int2) {
					String string = (String)itemPickerUpgradeWeapons.Upgrades.get(Rand.Next(itemPickerUpgradeWeapons.Upgrades.size()));
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
				float1 = FoodLootModifier;
			}

			if (item.getType() == Item.Type.Weapon || item.getType() == Item.Type.WeaponPart || "Ammo".equals(item.getDisplayCategory())) {
				float1 = WeaponLootModifier;
			}

			return float1;
		}
	}

	public static void updateOverlaySprite(IsoObject object) {
		if (object != null) {
			if (!(object instanceof IsoStove)) {
				IsoGridSquare square = object.getSquare();
				if (square != null) {
					String string = "other";
					if (square.getRoom() != null) {
						string = square.getRoom().getName();
					}

					String string2 = null;
					ItemContainer itemContainer = object.getContainer();
					if ((object.sprite != null && object.sprite.name != null && itemContainer != null && itemContainer.getItems() != null && !itemContainer.getItems().isEmpty() || itemContainer == null) && overlayMap.containsKey(object.sprite.name)) {
						ItemPickerJava.Overlay overlay = (ItemPickerJava.Overlay)overlayMap.get(object.sprite.name);
						ItemPickerJava.OverlayEntry overlayEntry = null;
						if (overlay.entries.containsKey(string)) {
							overlayEntry = (ItemPickerJava.OverlayEntry)overlay.entries.get(string);
						}

						if (overlayEntry == null && overlay.entries.containsKey("other")) {
							overlayEntry = (ItemPickerJava.OverlayEntry)overlay.entries.get("other");
						}

						if (overlayEntry != null) {
							String string3 = overlayEntry.a;
							if (string3 != "none") {
								if (itemContainer == null && Rand.Next(2) == 0) {
									return;
								}

								string2 = overlayEntry.a;
								if (overlayEntry.b != null && itemContainer != null && itemContainer.getItems() != null && itemContainer.getItems().size() < 7) {
									string2 = overlayEntry.b;
								}
							}
						}
					}

					object.setOverlaySprite(string2);
				}
			}
		}
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

	public static class Overlay {
		public String name;
		public THashMap entries = new THashMap();
	}

	public static class OverlayEntry {
		public String room;
		public String a;
		public String b;
	}

	public static class ItemPickerUpgradeWeapons {
		public String name;
		public ArrayList Upgrades = new ArrayList();
	}

	public static class ItemPickerRoom {
		public THashMap Containers = new THashMap();
		public int fillRand;
	}

	public static class ItemPickerContainer {
		public ItemPickerJava.ItemPickerItem[] Items = new ItemPickerJava.ItemPickerItem[0];
		public float rolls;
		public boolean noAutoAge;
		public int fillRand;
	}

	public static class ItemPickerItem {
		public String itemName;
		public float chance;
	}
}
