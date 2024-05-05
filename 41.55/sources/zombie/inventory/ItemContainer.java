package zombie.inventory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Predicate;
import se.krka.kahlua.integration.LuaReturn;
import se.krka.kahlua.vm.LuaClosure;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.AlarmClockClothing;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;


public final class ItemContainer {
	private static final ArrayList tempList = new ArrayList();
	private static final ArrayList s_tempObjects = new ArrayList();
	public boolean active = false;
	private boolean dirty = true;
	public boolean IsDevice = false;
	public float ageFactor = 1.0F;
	public float CookingFactor = 1.0F;
	public int Capacity = 50;
	public InventoryItem containingItem = null;
	public ArrayList Items = new ArrayList();
	public ArrayList IncludingObsoleteItems = new ArrayList();
	public IsoObject parent = null;
	public IsoGridSquare SourceGrid = null;
	public VehiclePart vehiclePart = null;
	public InventoryContainer inventoryContainer = null;
	public boolean bExplored = false;
	public String type = "none";
	public int ID = 0;
	private boolean drawDirty = true;
	private float customTemperature = 0.0F;
	private boolean hasBeenLooted = false;
	private String openSound = null;
	private String closeSound = null;
	private String putSound = null;
	private String OnlyAcceptCategory = null;
	private String AcceptItemFunction = null;
	private int weightReduction = 0;
	private String containerPosition = null;
	private String freezerPosition = null;
	private static final ThreadLocal TL_comparators = ThreadLocal.withInitial(ItemContainer.Comparators::new);
	private static final ThreadLocal TL_itemListPool = ThreadLocal.withInitial(ItemContainer.InventoryItemListPool::new);
	private static final ThreadLocal TL_predicates = ThreadLocal.withInitial(ItemContainer.Predicates::new);

	public ItemContainer(int int1, String string, IsoGridSquare square, IsoObject object) {
		this.ID = int1;
		this.parent = object;
		this.type = string;
		this.SourceGrid = square;
		if (string.equals("fridge")) {
			this.ageFactor = 0.02F;
			this.CookingFactor = 0.0F;
		}
	}

	public ItemContainer(String string, IsoGridSquare square, IsoObject object) {
		this.ID = -1;
		this.parent = object;
		this.type = string;
		this.SourceGrid = square;
		if (string.equals("fridge")) {
			this.ageFactor = 0.02F;
			this.CookingFactor = 0.0F;
		}
	}

	public ItemContainer(int int1) {
		this.ID = int1;
	}

	public ItemContainer() {
		this.ID = -1;
	}

	public static float floatingPointCorrection(float float1) {
		byte byte1 = 100;
		float float2 = float1 * (float)byte1;
		return (float)((int)(float2 - (float)((int)float2) >= 0.5F ? float2 + 1.0F : float2)) / (float)byte1;
	}

	public int getCapacity() {
		return this.Capacity;
	}

	public InventoryItem FindAndReturnWaterItem(int int1) {
		for (int int2 = 0; int2 < this.getItems().size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.getItems().get(int2);
			if (inventoryItem instanceof DrainableComboItem && inventoryItem.isWaterSource()) {
				DrainableComboItem drainableComboItem = (DrainableComboItem)inventoryItem;
				if (drainableComboItem.getDrainableUsesInt() >= int1) {
					return inventoryItem;
				}
			}
		}

		return null;
	}

	public InventoryItem getItemFromTypeRecurse(String string) {
		return this.getFirstTypeRecurse(string);
	}

	public int getEffectiveCapacity(IsoGameCharacter gameCharacter) {
		if (gameCharacter != null && !(this.parent instanceof IsoGameCharacter) && !(this.parent instanceof IsoDeadBody) && !"floor".equals(this.getType())) {
			if (gameCharacter.Traits.Organized.isSet()) {
				return (int)Math.max((float)this.Capacity * 1.3F, (float)(this.Capacity + 1));
			}

			if (gameCharacter.Traits.Disorganized.isSet()) {
				return (int)Math.max((float)this.Capacity * 0.7F, 1.0F);
			}
		}

		return this.Capacity;
	}

	public boolean hasRoomFor(IsoGameCharacter gameCharacter, InventoryItem inventoryItem) {
		if (this.vehiclePart != null && this.vehiclePart.getId().contains("Seat") && this.Items.isEmpty()) {
			return true;
		} else if (floatingPointCorrection(this.getCapacityWeight()) + inventoryItem.getUnequippedWeight() <= (float)this.getEffectiveCapacity(gameCharacter)) {
			if (this.getContainingItem() != null && this.getContainingItem().getEquipParent() != null && this.getContainingItem().getEquipParent().getInventory() != null && !this.getContainingItem().getEquipParent().getInventory().contains(inventoryItem)) {
				return floatingPointCorrection(this.getContainingItem().getEquipParent().getInventory().getCapacityWeight()) + inventoryItem.getUnequippedWeight() <= (float)this.getContainingItem().getEquipParent().getInventory().getEffectiveCapacity(gameCharacter);
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean hasRoomFor(IsoGameCharacter gameCharacter, float float1) {
		return floatingPointCorrection(this.getCapacityWeight()) + float1 <= (float)this.getEffectiveCapacity(gameCharacter);
	}

	public boolean isItemAllowed(InventoryItem inventoryItem) {
		if (inventoryItem == null) {
			return false;
		} else {
			String string = this.getOnlyAcceptCategory();
			if (string != null && !string.equalsIgnoreCase(inventoryItem.getCategory())) {
				return false;
			} else {
				String string2 = this.getAcceptItemFunction();
				if (string2 != null) {
					Object object = LuaManager.getFunctionObject(string2);
					if (object != null) {
						Boolean Boolean1 = LuaManager.caller.protectedCallBoolean(LuaManager.thread, object, this, inventoryItem);
						if (Boolean1 != Boolean.TRUE) {
							return false;
						}
					}
				}

				if (this.parent != null && !this.parent.isItemAllowedInContainer(this, inventoryItem)) {
					return false;
				} else {
					return !this.getType().equals("clothingrack") || inventoryItem instanceof Clothing;
				}
			}
		}
	}

	public boolean isRemoveItemAllowed(InventoryItem inventoryItem) {
		if (inventoryItem == null) {
			return false;
		} else {
			return this.parent == null || this.parent.isRemoveItemAllowedFromContainer(this, inventoryItem);
		}
	}

	public boolean isExplored() {
		return this.bExplored;
	}

	public void setExplored(boolean boolean1) {
		this.bExplored = boolean1;
	}

	public boolean isInCharacterInventory(IsoGameCharacter gameCharacter) {
		if (gameCharacter.getInventory() == this) {
			return true;
		} else {
			if (this.containingItem != null) {
				if (gameCharacter.getInventory().contains(this.containingItem, true)) {
					return true;
				}

				if (this.containingItem.getContainer() != null) {
					return this.containingItem.getContainer().isInCharacterInventory(gameCharacter);
				}
			}

			return false;
		}
	}

	public boolean isInside(InventoryItem inventoryItem) {
		if (this.containingItem == null) {
			return false;
		} else if (this.containingItem == inventoryItem) {
			return true;
		} else {
			return this.containingItem.getContainer() != null && this.containingItem.getContainer().isInside(inventoryItem);
		}
	}

	public InventoryItem getContainingItem() {
		return this.containingItem;
	}

	public InventoryItem DoAddItem(InventoryItem inventoryItem) {
		return this.AddItem(inventoryItem);
	}

	public InventoryItem DoAddItemBlind(InventoryItem inventoryItem) {
		return this.AddItem(inventoryItem);
	}

	public ArrayList AddItems(String string, int int1) {
		ArrayList arrayList = new ArrayList();
		for (int int2 = 0; int2 < int1; ++int2) {
			InventoryItem inventoryItem = this.AddItem(string);
			if (inventoryItem != null) {
				arrayList.add(inventoryItem);
			}
		}

		return arrayList;
	}

	public void AddItems(InventoryItem inventoryItem, int int1) {
		for (int int2 = 0; int2 < int1; ++int2) {
			this.AddItem(inventoryItem.getFullType());
		}
	}

	public int getNumberOfItem(String string, boolean boolean1) {
		return this.getNumberOfItem(string, boolean1, false);
	}

	public int getNumberOfItem(String string) {
		return this.getNumberOfItem(string, false);
	}

	public int getNumberOfItem(String string, boolean boolean1, ArrayList arrayList) {
		int int1 = this.getNumberOfItem(string, boolean1);
		if (arrayList != null) {
			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				ItemContainer itemContainer = (ItemContainer)iterator.next();
				if (itemContainer != this) {
					int1 += itemContainer.getNumberOfItem(string, boolean1);
				}
			}
		}

		return int1;
	}

	public int getNumberOfItem(String string, boolean boolean1, boolean boolean2) {
		int int1 = 0;
		for (int int2 = 0; int2 < this.Items.size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
			if (!inventoryItem.getFullType().equals(string) && !inventoryItem.getType().equals(string)) {
				if (boolean2 && inventoryItem instanceof InventoryContainer) {
					int1 += ((InventoryContainer)inventoryItem).getItemContainer().getNumberOfItem(string);
				} else if (boolean1 && inventoryItem instanceof DrainableComboItem && ((DrainableComboItem)inventoryItem).getReplaceOnDeplete() != null) {
					DrainableComboItem drainableComboItem = (DrainableComboItem)inventoryItem;
					if (drainableComboItem.getReplaceOnDepleteFullType().equals(string) || drainableComboItem.getReplaceOnDeplete().equals(string)) {
						++int1;
					}
				}
			} else {
				++int1;
			}
		}

		return int1;
	}

	public InventoryItem addItem(InventoryItem inventoryItem) {
		return this.AddItem(inventoryItem);
	}

	public InventoryItem AddItem(InventoryItem inventoryItem) {
		if (inventoryItem == null) {
			return null;
		} else if (this.containsID(inventoryItem.id)) {
			System.out.println("Error, container already has id");
			return this.getItemWithID(inventoryItem.id);
		} else {
			this.drawDirty = true;
			if (this.parent != null) {
				this.dirty = true;
			}

			if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
				this.parent.DirtySlice();
			}

			if (inventoryItem.container != null) {
				inventoryItem.container.Remove(inventoryItem);
			}

			inventoryItem.container = this;
			this.Items.add(inventoryItem);
			if (IsoWorld.instance.CurrentCell != null) {
				IsoWorld.instance.CurrentCell.addToProcessItems(inventoryItem);
			}

			return inventoryItem;
		}
	}

	public InventoryItem AddItemBlind(InventoryItem inventoryItem) {
		if (inventoryItem == null) {
			return null;
		} else if (inventoryItem.getWeight() + this.getCapacityWeight() > (float)this.getCapacity()) {
			return null;
		} else {
			if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
				this.parent.DirtySlice();
			}

			this.Items.add(inventoryItem);
			return inventoryItem;
		}
	}

	public InventoryItem AddItem(String string) {
		this.drawDirty = true;
		if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
			this.dirty = true;
		}

		Item item = ScriptManager.instance.FindItem(string);
		if (item == null) {
			DebugLog.log("ERROR: ItemContainer.AddItem: can\'t find " + string);
			return null;
		} else if (item.OBSOLETE) {
			return null;
		} else {
			InventoryItem inventoryItem = null;
			int int1 = item.getCount();
			for (int int2 = 0; int2 < int1; ++int2) {
				inventoryItem = InventoryItemFactory.CreateItem(string);
				if (inventoryItem == null) {
					return null;
				}

				inventoryItem.container = this;
				this.Items.add(inventoryItem);
				if (inventoryItem instanceof Food) {
					((Food)inventoryItem).setHeat(this.getTemprature());
				}

				if (IsoWorld.instance.CurrentCell != null) {
					IsoWorld.instance.CurrentCell.addToProcessItems(inventoryItem);
				}
			}

			return inventoryItem;
		}
	}

	public boolean AddItem(String string, float float1) {
		this.drawDirty = true;
		if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
			this.dirty = true;
		}

		InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
		if (inventoryItem == null) {
			return false;
		} else {
			if (inventoryItem instanceof Drainable) {
				((Drainable)inventoryItem).setUsedDelta(float1);
			}

			inventoryItem.container = this;
			this.Items.add(inventoryItem);
			return true;
		}
	}

	public boolean contains(InventoryItem inventoryItem) {
		return this.Items.contains(inventoryItem);
	}

	public boolean containsWithModule(String string) {
		return this.containsWithModule(string, false);
	}

	public boolean containsWithModule(String string, boolean boolean1) {
		String string2 = string;
		String string3 = "Base";
		if (string.contains(".")) {
			string3 = string.split("\\.")[0];
			string2 = string.split("\\.")[1];
		}

		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem == null) {
				this.Items.remove(int1);
				--int1;
			} else if (inventoryItem.type.equals(string2.trim()) && string3.equals(inventoryItem.getModule()) && (!boolean1 || !(inventoryItem instanceof DrainableComboItem) || !(((DrainableComboItem)inventoryItem).getUsedDelta() <= 0.0F))) {
				return true;
			}
		}

		return false;
	}

	public void removeItemOnServer(InventoryItem inventoryItem) {
		if (GameClient.bClient) {
			if (this.containingItem != null && this.containingItem.getWorldItem() != null) {
				GameClient.instance.addToItemRemoveSendBuffer(this.containingItem.getWorldItem(), this, inventoryItem);
			} else {
				GameClient.instance.addToItemRemoveSendBuffer(this.parent, this, inventoryItem);
			}
		}
	}

	public void addItemOnServer(InventoryItem inventoryItem) {
		if (GameClient.bClient) {
			if (this.containingItem != null && this.containingItem.getWorldItem() != null) {
				GameClient.instance.addToItemSendBuffer(this.containingItem.getWorldItem(), this, inventoryItem);
			} else {
				GameClient.instance.addToItemSendBuffer(this.parent, this, inventoryItem);
			}
		}
	}

	public boolean contains(InventoryItem inventoryItem, boolean boolean1) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		int int1;
		for (int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int1);
			if (inventoryItem2 == null) {
				this.Items.remove(int1);
				--int1;
			} else {
				if (inventoryItem2 == inventoryItem) {
					((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
					return true;
				}

				if (boolean1 && inventoryItem2 instanceof InventoryContainer && ((InventoryContainer)inventoryItem2).getInventory() != null && !inventoryItemList.contains(inventoryItem2)) {
					inventoryItemList.add(inventoryItem2);
				}
			}
		}

		for (int1 = 0; int1 < inventoryItemList.size(); ++int1) {
			ItemContainer itemContainer = ((InventoryContainer)inventoryItemList.get(int1)).getInventory();
			if (itemContainer.contains(inventoryItem, boolean1)) {
				((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
				return true;
			}
		}

		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return false;
	}

	public boolean contains(String string, boolean boolean1) {
		return this.contains(string, boolean1, false);
	}

	public boolean containsType(String string) {
		return this.contains(string, false, false);
	}

	public boolean containsTypeRecurse(String string) {
		return this.contains(string, true, false);
	}

	private boolean testBroken(boolean boolean1, InventoryItem inventoryItem) {
		if (!boolean1) {
			return true;
		} else {
			return !inventoryItem.isBroken();
		}
	}

	public boolean contains(String string, boolean boolean1, boolean boolean2) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		int int1;
		InventoryItem inventoryItem;
		if (string.contains("Type:")) {
			for (int1 = 0; int1 < this.Items.size(); ++int1) {
				inventoryItem = (InventoryItem)this.Items.get(int1);
				if (string.contains("Food") && inventoryItem instanceof Food) {
					((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
					return true;
				}

				if (string.contains("Weapon") && inventoryItem instanceof HandWeapon && this.testBroken(boolean2, inventoryItem)) {
					((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
					return true;
				}

				if (string.contains("AlarmClock") && inventoryItem instanceof AlarmClock) {
					((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
					return true;
				}

				if (string.contains("AlarmClockClothing") && inventoryItem instanceof AlarmClockClothing) {
					((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
					return true;
				}

				if (boolean1 && inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getInventory() != null && !inventoryItemList.contains(inventoryItem)) {
					inventoryItemList.add(inventoryItem);
				}
			}
		} else if (string.contains("/")) {
			String[] stringArray = string.split("/");
			String[] stringArray2 = stringArray;
			int int2 = stringArray.length;
			for (int int3 = 0; int3 < int2; ++int3) {
				String string2 = stringArray2[int3];
				for (int int4 = 0; int4 < this.Items.size(); ++int4) {
					InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int4);
					if (compareType(string2.trim(), inventoryItem2) && this.testBroken(boolean2, inventoryItem2)) {
						((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
						return true;
					}

					if (boolean1 && inventoryItem2 instanceof InventoryContainer && ((InventoryContainer)inventoryItem2).getInventory() != null && !inventoryItemList.contains(inventoryItem2)) {
						inventoryItemList.add(inventoryItem2);
					}
				}
			}
		} else {
			for (int1 = 0; int1 < this.Items.size(); ++int1) {
				inventoryItem = (InventoryItem)this.Items.get(int1);
				if (inventoryItem == null) {
					this.Items.remove(int1);
					--int1;
				} else {
					if (compareType(string.trim(), inventoryItem) && this.testBroken(boolean2, inventoryItem)) {
						((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
						return true;
					}

					if (boolean1 && inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getInventory() != null && !inventoryItemList.contains(inventoryItem)) {
						inventoryItemList.add(inventoryItem);
					}
				}
			}
		}

		for (int1 = 0; int1 < inventoryItemList.size(); ++int1) {
			ItemContainer itemContainer = ((InventoryContainer)inventoryItemList.get(int1)).getInventory();
			if (itemContainer.contains(string, boolean1, boolean2)) {
				((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
				return true;
			}
		}

		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return false;
	}

	public boolean contains(String string) {
		return this.contains(string, false);
	}

	private static InventoryItem getBestOf(ItemContainer.InventoryItemList inventoryItemList, Comparator comparator) {
		if (inventoryItemList != null && !inventoryItemList.isEmpty()) {
			InventoryItem inventoryItem = (InventoryItem)inventoryItemList.get(0);
			for (int int1 = 1; int1 < inventoryItemList.size(); ++int1) {
				InventoryItem inventoryItem2 = (InventoryItem)inventoryItemList.get(int1);
				if (comparator.compare(inventoryItem2, inventoryItem) > 0) {
					inventoryItem = inventoryItem2;
				}
			}

			return inventoryItem;
		} else {
			return null;
		}
	}

	public InventoryItem getBest(Predicate predicate, Comparator comparator) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		this.getAll(predicate, inventoryItemList);
		InventoryItem inventoryItem = getBestOf(inventoryItemList, comparator);
		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return inventoryItem;
	}

	public InventoryItem getBestRecurse(Predicate predicate, Comparator comparator) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		this.getAllRecurse(predicate, inventoryItemList);
		InventoryItem inventoryItem = getBestOf(inventoryItemList, comparator);
		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return inventoryItem;
	}

	public InventoryItem getBestType(String string, Comparator comparator) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		InventoryItem inventoryItem;
		try {
			inventoryItem = this.getBest(typePredicate, comparator);
		} finally {
			((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
		}

		return inventoryItem;
	}

	public InventoryItem getBestTypeRecurse(String string, Comparator comparator) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		InventoryItem inventoryItem;
		try {
			inventoryItem = this.getBestRecurse(typePredicate, comparator);
		} finally {
			((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
		}

		return inventoryItem;
	}

	public InventoryItem getBestEval(LuaClosure luaClosure, LuaClosure luaClosure2) {
		ItemContainer.EvalPredicate evalPredicate = ((ItemContainer.EvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).eval.alloc()).init(luaClosure);
		ItemContainer.EvalComparator evalComparator = ((ItemContainer.EvalComparator)((ItemContainer.Comparators)TL_comparators.get()).eval.alloc()).init(luaClosure2);
		InventoryItem inventoryItem;
		try {
			inventoryItem = this.getBest(evalPredicate, evalComparator);
		} finally {
			((ItemContainer.Predicates)TL_predicates.get()).eval.release((Object)evalPredicate);
			((ItemContainer.Comparators)TL_comparators.get()).eval.release((Object)evalComparator);
		}

		return inventoryItem;
	}

	public InventoryItem getBestEvalRecurse(LuaClosure luaClosure, LuaClosure luaClosure2) {
		ItemContainer.EvalPredicate evalPredicate = ((ItemContainer.EvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).eval.alloc()).init(luaClosure);
		ItemContainer.EvalComparator evalComparator = ((ItemContainer.EvalComparator)((ItemContainer.Comparators)TL_comparators.get()).eval.alloc()).init(luaClosure2);
		InventoryItem inventoryItem;
		try {
			inventoryItem = this.getBestRecurse(evalPredicate, evalComparator);
		} finally {
			((ItemContainer.Predicates)TL_predicates.get()).eval.release((Object)evalPredicate);
			((ItemContainer.Comparators)TL_comparators.get()).eval.release((Object)evalComparator);
		}

		return inventoryItem;
	}

	public InventoryItem getBestEvalArg(LuaClosure luaClosure, LuaClosure luaClosure2, Object object) {
		ItemContainer.EvalArgPredicate evalArgPredicate = ((ItemContainer.EvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).evalArg.alloc()).init(luaClosure, object);
		ItemContainer.EvalArgComparator evalArgComparator = ((ItemContainer.EvalArgComparator)((ItemContainer.Comparators)TL_comparators.get()).evalArg.alloc()).init(luaClosure2, object);
		InventoryItem inventoryItem;
		try {
			inventoryItem = this.getBest(evalArgPredicate, evalArgComparator);
		} finally {
			((ItemContainer.Predicates)TL_predicates.get()).evalArg.release((Object)evalArgPredicate);
			((ItemContainer.Comparators)TL_comparators.get()).evalArg.release((Object)evalArgComparator);
		}

		return inventoryItem;
	}

	public InventoryItem getBestEvalArgRecurse(LuaClosure luaClosure, LuaClosure luaClosure2, Object object) {
		ItemContainer.EvalArgPredicate evalArgPredicate = ((ItemContainer.EvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).evalArg.alloc()).init(luaClosure, object);
		ItemContainer.EvalArgComparator evalArgComparator = ((ItemContainer.EvalArgComparator)((ItemContainer.Comparators)TL_comparators.get()).evalArg.alloc()).init(luaClosure2, object);
		InventoryItem inventoryItem;
		try {
			inventoryItem = this.getBestRecurse(evalArgPredicate, evalArgComparator);
		} finally {
			((ItemContainer.Predicates)TL_predicates.get()).evalArg.release((Object)evalArgPredicate);
			((ItemContainer.Comparators)TL_comparators.get()).evalArg.release((Object)evalArgComparator);
		}

		return inventoryItem;
	}

	public InventoryItem getBestTypeEval(String string, LuaClosure luaClosure) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		ItemContainer.EvalComparator evalComparator = ((ItemContainer.EvalComparator)((ItemContainer.Comparators)TL_comparators.get()).eval.alloc()).init(luaClosure);
		InventoryItem inventoryItem;
		try {
			inventoryItem = this.getBest(typePredicate, evalComparator);
		} finally {
			((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
			((ItemContainer.Comparators)TL_comparators.get()).eval.release((Object)evalComparator);
		}

		return inventoryItem;
	}

	public InventoryItem getBestTypeEvalRecurse(String string, LuaClosure luaClosure) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		ItemContainer.EvalComparator evalComparator = ((ItemContainer.EvalComparator)((ItemContainer.Comparators)TL_comparators.get()).eval.alloc()).init(luaClosure);
		InventoryItem inventoryItem;
		try {
			inventoryItem = this.getBestRecurse(typePredicate, evalComparator);
		} finally {
			((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
			((ItemContainer.Comparators)TL_comparators.get()).eval.release((Object)evalComparator);
		}

		return inventoryItem;
	}

	public InventoryItem getBestTypeEvalArg(String string, LuaClosure luaClosure, Object object) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		ItemContainer.EvalArgComparator evalArgComparator = ((ItemContainer.EvalArgComparator)((ItemContainer.Comparators)TL_comparators.get()).evalArg.alloc()).init(luaClosure, object);
		InventoryItem inventoryItem;
		try {
			inventoryItem = this.getBest(typePredicate, evalArgComparator);
		} finally {
			((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
			((ItemContainer.Comparators)TL_comparators.get()).evalArg.release((Object)evalArgComparator);
		}

		return inventoryItem;
	}

	public InventoryItem getBestTypeEvalArgRecurse(String string, LuaClosure luaClosure, Object object) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		ItemContainer.EvalArgComparator evalArgComparator = ((ItemContainer.EvalArgComparator)((ItemContainer.Comparators)TL_comparators.get()).evalArg.alloc()).init(luaClosure, object);
		InventoryItem inventoryItem;
		try {
			inventoryItem = this.getBestRecurse(typePredicate, evalArgComparator);
		} finally {
			((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
			((ItemContainer.Comparators)TL_comparators.get()).evalArg.release((Object)evalArgComparator);
		}

		return inventoryItem;
	}

	public InventoryItem getBestCondition(Predicate predicate) {
		ItemContainer.ConditionComparator conditionComparator = (ItemContainer.ConditionComparator)((ItemContainer.Comparators)TL_comparators.get()).condition.alloc();
		InventoryItem inventoryItem = this.getBest(predicate, conditionComparator);
		((ItemContainer.Comparators)TL_comparators.get()).condition.release((Object)conditionComparator);
		if (inventoryItem != null && inventoryItem.getCondition() <= 0) {
			inventoryItem = null;
		}

		return inventoryItem;
	}

	public InventoryItem getBestConditionRecurse(Predicate predicate) {
		ItemContainer.ConditionComparator conditionComparator = (ItemContainer.ConditionComparator)((ItemContainer.Comparators)TL_comparators.get()).condition.alloc();
		InventoryItem inventoryItem = this.getBestRecurse(predicate, conditionComparator);
		((ItemContainer.Comparators)TL_comparators.get()).condition.release((Object)conditionComparator);
		if (inventoryItem != null && inventoryItem.getCondition() <= 0) {
			inventoryItem = null;
		}

		return inventoryItem;
	}

	public InventoryItem getBestCondition(String string) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		InventoryItem inventoryItem = this.getBestCondition((Predicate)typePredicate);
		((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
		return inventoryItem;
	}

	public InventoryItem getBestConditionRecurse(String string) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		InventoryItem inventoryItem = this.getBestConditionRecurse((Predicate)typePredicate);
		((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
		return inventoryItem;
	}

	public InventoryItem getBestConditionEval(LuaClosure luaClosure) {
		ItemContainer.EvalPredicate evalPredicate = ((ItemContainer.EvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).eval.alloc()).init(luaClosure);
		InventoryItem inventoryItem = this.getBestCondition((Predicate)evalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).eval.release((Object)evalPredicate);
		return inventoryItem;
	}

	public InventoryItem getBestConditionEvalRecurse(LuaClosure luaClosure) {
		ItemContainer.EvalPredicate evalPredicate = ((ItemContainer.EvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).eval.alloc()).init(luaClosure);
		InventoryItem inventoryItem = this.getBestConditionRecurse((Predicate)evalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).eval.release((Object)evalPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstEval(LuaClosure luaClosure) {
		ItemContainer.EvalPredicate evalPredicate = ((ItemContainer.EvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).eval.alloc()).init(luaClosure);
		InventoryItem inventoryItem = this.getFirst(evalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).eval.release((Object)evalPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstEvalArg(LuaClosure luaClosure, Object object) {
		ItemContainer.EvalArgPredicate evalArgPredicate = ((ItemContainer.EvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).evalArg.alloc()).init(luaClosure, object);
		InventoryItem inventoryItem = this.getFirst(evalArgPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).evalArg.release((Object)evalArgPredicate);
		return inventoryItem;
	}

	public boolean containsEval(LuaClosure luaClosure) {
		return this.getFirstEval(luaClosure) != null;
	}

	public boolean containsEvalArg(LuaClosure luaClosure, Object object) {
		return this.getFirstEvalArg(luaClosure, object) != null;
	}

	public boolean containsEvalRecurse(LuaClosure luaClosure) {
		return this.getFirstEvalRecurse(luaClosure) != null;
	}

	public boolean containsEvalArgRecurse(LuaClosure luaClosure, Object object) {
		return this.getFirstEvalArgRecurse(luaClosure, object) != null;
	}

	public boolean containsTag(String string) {
		return this.getFirstTag(string) != null;
	}

	public boolean containsTagEval(String string, LuaClosure luaClosure) {
		return this.getFirstTagEval(string, luaClosure) != null;
	}

	public boolean containsTagRecurse(String string) {
		return this.getFirstTagRecurse(string) != null;
	}

	public boolean containsTagEvalRecurse(String string, LuaClosure luaClosure) {
		return this.getFirstTagEvalRecurse(string, luaClosure) != null;
	}

	public boolean containsTagEvalArgRecurse(String string, LuaClosure luaClosure, Object object) {
		return this.getFirstTagEvalArgRecurse(string, luaClosure, object) != null;
	}

	public boolean containsTypeEvalRecurse(String string, LuaClosure luaClosure) {
		return this.getFirstTypeEvalRecurse(string, luaClosure) != null;
	}

	public boolean containsTypeEvalArgRecurse(String string, LuaClosure luaClosure, Object object) {
		return this.getFirstTypeEvalArgRecurse(string, luaClosure, object) != null;
	}

	private static boolean compareType(String string, String string2) {
		if (!string.contains("/")) {
			return string.equals(string2);
		} else {
			int int1 = string.indexOf(string2);
			if (int1 == -1) {
				return false;
			} else {
				char char1 = int1 > 0 ? string.charAt(int1 - 1) : 0;
				char char2 = int1 + string2.length() < string.length() ? string.charAt(int1 + string2.length()) : 0;
				return char1 == 0 && char2 == '/' || char1 == '/' && char2 == 0 || char1 == '/' && char2 == '/';
			}
		}
	}

	private static boolean compareType(String string, InventoryItem inventoryItem) {
		if (string.indexOf(46) == -1) {
			return compareType(string, inventoryItem.getType());
		} else {
			return compareType(string, inventoryItem.getFullType()) || compareType(string, inventoryItem.getType());
		}
	}

	public InventoryItem getFirst(Predicate predicate) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem == null) {
				this.Items.remove(int1);
				--int1;
			} else if (predicate.test(inventoryItem)) {
				return inventoryItem;
			}
		}

		return null;
	}

	public InventoryItem getFirstRecurse(Predicate predicate) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		int int1;
		for (int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem == null) {
				this.Items.remove(int1);
				--int1;
			} else {
				if (predicate.test(inventoryItem)) {
					((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
					return inventoryItem;
				}

				if (inventoryItem instanceof InventoryContainer) {
					inventoryItemList.add(inventoryItem);
				}
			}
		}

		for (int1 = 0; int1 < inventoryItemList.size(); ++int1) {
			ItemContainer itemContainer = ((InventoryContainer)inventoryItemList.get(int1)).getInventory();
			InventoryItem inventoryItem2 = itemContainer.getFirstRecurse(predicate);
			if (inventoryItem2 != null) {
				((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
				return inventoryItem2;
			}
		}

		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return null;
	}

	public ArrayList getSome(Predicate predicate, int int1, ArrayList arrayList) {
		for (int int2 = 0; int2 < this.Items.size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
			if (inventoryItem == null) {
				this.Items.remove(int2);
				--int2;
			} else if (predicate.test(inventoryItem)) {
				arrayList.add(inventoryItem);
				if (arrayList.size() >= int1) {
					break;
				}
			}
		}

		return arrayList;
	}

	public ArrayList getSomeRecurse(Predicate predicate, int int1, ArrayList arrayList) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		int int2;
		for (int2 = 0; int2 < this.Items.size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
			if (inventoryItem == null) {
				this.Items.remove(int2);
				--int2;
			} else {
				if (predicate.test(inventoryItem)) {
					arrayList.add(inventoryItem);
					if (arrayList.size() >= int1) {
						((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
						return arrayList;
					}
				}

				if (inventoryItem instanceof InventoryContainer) {
					inventoryItemList.add(inventoryItem);
				}
			}
		}

		for (int2 = 0; int2 < inventoryItemList.size(); ++int2) {
			ItemContainer itemContainer = ((InventoryContainer)inventoryItemList.get(int2)).getInventory();
			itemContainer.getSomeRecurse(predicate, int1, arrayList);
			if (arrayList.size() >= int1) {
				break;
			}
		}

		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return arrayList;
	}

	public ArrayList getAll(Predicate predicate, ArrayList arrayList) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem == null) {
				this.Items.remove(int1);
				--int1;
			} else if (predicate.test(inventoryItem)) {
				arrayList.add(inventoryItem);
			}
		}

		return arrayList;
	}

	public ArrayList getAllRecurse(Predicate predicate, ArrayList arrayList) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		int int1;
		for (int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem == null) {
				this.Items.remove(int1);
				--int1;
			} else {
				if (predicate.test(inventoryItem)) {
					arrayList.add(inventoryItem);
				}

				if (inventoryItem instanceof InventoryContainer) {
					inventoryItemList.add(inventoryItem);
				}
			}
		}

		for (int1 = 0; int1 < inventoryItemList.size(); ++int1) {
			ItemContainer itemContainer = ((InventoryContainer)inventoryItemList.get(int1)).getInventory();
			itemContainer.getAllRecurse(predicate, arrayList);
		}

		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return arrayList;
	}

	public int getCount(Predicate predicate) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		this.getAll(predicate, inventoryItemList);
		int int1 = inventoryItemList.size();
		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return int1;
	}

	public int getCountRecurse(Predicate predicate) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		this.getAllRecurse(predicate, inventoryItemList);
		int int1 = inventoryItemList.size();
		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return int1;
	}

	public int getCountTag(String string) {
		ItemContainer.TagPredicate tagPredicate = ((ItemContainer.TagPredicate)((ItemContainer.Predicates)TL_predicates.get()).tag.alloc()).init(string);
		int int1 = this.getCount(tagPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).tag.release((Object)tagPredicate);
		return int1;
	}

	public int getCountTagEval(String string, LuaClosure luaClosure) {
		ItemContainer.TagEvalPredicate tagEvalPredicate = ((ItemContainer.TagEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEval.alloc()).init(string, luaClosure);
		int int1 = this.getCount(tagEvalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).tagEval.release((Object)tagEvalPredicate);
		return int1;
	}

	public int getCountTagEvalArg(String string, LuaClosure luaClosure, Object object) {
		ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = ((ItemContainer.TagEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.alloc()).init(string, luaClosure, object);
		int int1 = this.getCount(tagEvalArgPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.release((Object)tagEvalArgPredicate);
		return int1;
	}

	public int getCountTagRecurse(String string) {
		ItemContainer.TagPredicate tagPredicate = ((ItemContainer.TagPredicate)((ItemContainer.Predicates)TL_predicates.get()).tag.alloc()).init(string);
		int int1 = this.getCountRecurse(tagPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).tag.release((Object)tagPredicate);
		return int1;
	}

	public int getCountTagEvalRecurse(String string, LuaClosure luaClosure) {
		ItemContainer.TagEvalPredicate tagEvalPredicate = ((ItemContainer.TagEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEval.alloc()).init(string, luaClosure);
		int int1 = this.getCountRecurse(tagEvalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).tagEval.release((Object)tagEvalPredicate);
		return int1;
	}

	public int getCountTagEvalArgRecurse(String string, LuaClosure luaClosure, Object object) {
		ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = ((ItemContainer.TagEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.alloc()).init(string, luaClosure, object);
		int int1 = this.getCountRecurse(tagEvalArgPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.release((Object)tagEvalArgPredicate);
		return int1;
	}

	public int getCountType(String string) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		int int1 = this.getCount(typePredicate);
		((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
		return int1;
	}

	public int getCountTypeEval(String string, LuaClosure luaClosure) {
		ItemContainer.TypeEvalPredicate typeEvalPredicate = ((ItemContainer.TypeEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEval.alloc()).init(string, luaClosure);
		int int1 = this.getCount(typeEvalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).typeEval.release((Object)typeEvalPredicate);
		return int1;
	}

	public int getCountTypeEvalArg(String string, LuaClosure luaClosure, Object object) {
		ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = ((ItemContainer.TypeEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.alloc()).init(string, luaClosure, object);
		int int1 = this.getCount(typeEvalArgPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.release((Object)typeEvalArgPredicate);
		return int1;
	}

	public int getCountTypeRecurse(String string) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		int int1 = this.getCountRecurse(typePredicate);
		((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
		return int1;
	}

	public int getCountTypeEvalRecurse(String string, LuaClosure luaClosure) {
		ItemContainer.TypeEvalPredicate typeEvalPredicate = ((ItemContainer.TypeEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEval.alloc()).init(string, luaClosure);
		int int1 = this.getCountRecurse(typeEvalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).typeEval.release((Object)typeEvalPredicate);
		return int1;
	}

	public int getCountTypeEvalArgRecurse(String string, LuaClosure luaClosure, Object object) {
		ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = ((ItemContainer.TypeEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.alloc()).init(string, luaClosure, object);
		int int1 = this.getCountRecurse(typeEvalArgPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.release((Object)typeEvalArgPredicate);
		return int1;
	}

	public int getCountEval(LuaClosure luaClosure) {
		ItemContainer.EvalPredicate evalPredicate = ((ItemContainer.EvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).eval.alloc()).init(luaClosure);
		int int1 = this.getCount(evalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).eval.release((Object)evalPredicate);
		return int1;
	}

	public int getCountEvalArg(LuaClosure luaClosure, Object object) {
		ItemContainer.EvalArgPredicate evalArgPredicate = ((ItemContainer.EvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).evalArg.alloc()).init(luaClosure, object);
		int int1 = this.getCount(evalArgPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).evalArg.release((Object)evalArgPredicate);
		return int1;
	}

	public int getCountEvalRecurse(LuaClosure luaClosure) {
		ItemContainer.EvalPredicate evalPredicate = ((ItemContainer.EvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).eval.alloc()).init(luaClosure);
		int int1 = this.getCountRecurse(evalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).eval.release((Object)evalPredicate);
		return int1;
	}

	public int getCountEvalArgRecurse(LuaClosure luaClosure, Object object) {
		ItemContainer.EvalArgPredicate evalArgPredicate = ((ItemContainer.EvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).evalArg.alloc()).init(luaClosure, object);
		int int1 = this.getCountRecurse(evalArgPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).evalArg.release((Object)evalArgPredicate);
		return int1;
	}

	public InventoryItem getFirstCategory(String string) {
		ItemContainer.CategoryPredicate categoryPredicate = ((ItemContainer.CategoryPredicate)((ItemContainer.Predicates)TL_predicates.get()).category.alloc()).init(string);
		InventoryItem inventoryItem = this.getFirst(categoryPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).category.release((Object)categoryPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstCategoryRecurse(String string) {
		ItemContainer.CategoryPredicate categoryPredicate = ((ItemContainer.CategoryPredicate)((ItemContainer.Predicates)TL_predicates.get()).category.alloc()).init(string);
		InventoryItem inventoryItem = this.getFirstRecurse(categoryPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).category.release((Object)categoryPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstEvalRecurse(LuaClosure luaClosure) {
		ItemContainer.EvalPredicate evalPredicate = ((ItemContainer.EvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).eval.alloc()).init(luaClosure);
		InventoryItem inventoryItem = this.getFirstRecurse(evalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).eval.release((Object)evalPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstEvalArgRecurse(LuaClosure luaClosure, Object object) {
		ItemContainer.EvalArgPredicate evalArgPredicate = ((ItemContainer.EvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).evalArg.alloc()).init(luaClosure, object);
		InventoryItem inventoryItem = this.getFirstRecurse(evalArgPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).evalArg.release((Object)evalArgPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstTag(String string) {
		ItemContainer.TagPredicate tagPredicate = ((ItemContainer.TagPredicate)((ItemContainer.Predicates)TL_predicates.get()).tag.alloc()).init(string);
		InventoryItem inventoryItem = this.getFirst(tagPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).tag.release((Object)tagPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstTagRecurse(String string) {
		ItemContainer.TagPredicate tagPredicate = ((ItemContainer.TagPredicate)((ItemContainer.Predicates)TL_predicates.get()).tag.alloc()).init(string);
		InventoryItem inventoryItem = this.getFirstRecurse(tagPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).tag.release((Object)tagPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstTagEval(String string, LuaClosure luaClosure) {
		ItemContainer.TagEvalPredicate tagEvalPredicate = ((ItemContainer.TagEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEval.alloc()).init(string, luaClosure);
		InventoryItem inventoryItem = this.getFirstRecurse(tagEvalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).tagEval.release((Object)tagEvalPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstTagEvalRecurse(String string, LuaClosure luaClosure) {
		ItemContainer.TagEvalPredicate tagEvalPredicate = ((ItemContainer.TagEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEval.alloc()).init(string, luaClosure);
		InventoryItem inventoryItem = this.getFirstRecurse(tagEvalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).tagEval.release((Object)tagEvalPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstTagEvalArgRecurse(String string, LuaClosure luaClosure, Object object) {
		ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = ((ItemContainer.TagEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.alloc()).init(string, luaClosure, object);
		InventoryItem inventoryItem = this.getFirstRecurse(tagEvalArgPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.release((Object)tagEvalArgPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstType(String string) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		InventoryItem inventoryItem = this.getFirst(typePredicate);
		((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstTypeRecurse(String string) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		InventoryItem inventoryItem = this.getFirstRecurse(typePredicate);
		((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstTypeEval(String string, LuaClosure luaClosure) {
		ItemContainer.TypeEvalPredicate typeEvalPredicate = ((ItemContainer.TypeEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEval.alloc()).init(string, luaClosure);
		InventoryItem inventoryItem = this.getFirstRecurse(typeEvalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).typeEval.release((Object)typeEvalPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstTypeEvalRecurse(String string, LuaClosure luaClosure) {
		ItemContainer.TypeEvalPredicate typeEvalPredicate = ((ItemContainer.TypeEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEval.alloc()).init(string, luaClosure);
		InventoryItem inventoryItem = this.getFirstRecurse(typeEvalPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).typeEval.release((Object)typeEvalPredicate);
		return inventoryItem;
	}

	public InventoryItem getFirstTypeEvalArgRecurse(String string, LuaClosure luaClosure, Object object) {
		ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = ((ItemContainer.TypeEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.alloc()).init(string, luaClosure, object);
		InventoryItem inventoryItem = this.getFirstRecurse(typeEvalArgPredicate);
		((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.release((Object)typeEvalArgPredicate);
		return inventoryItem;
	}

	public ArrayList getSomeCategory(String string, int int1, ArrayList arrayList) {
		ItemContainer.CategoryPredicate categoryPredicate = ((ItemContainer.CategoryPredicate)((ItemContainer.Predicates)TL_predicates.get()).category.alloc()).init(string);
		ArrayList arrayList2 = this.getSome(categoryPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).category.release((Object)categoryPredicate);
		return arrayList2;
	}

	public ArrayList getSomeCategoryRecurse(String string, int int1, ArrayList arrayList) {
		ItemContainer.CategoryPredicate categoryPredicate = ((ItemContainer.CategoryPredicate)((ItemContainer.Predicates)TL_predicates.get()).category.alloc()).init(string);
		ArrayList arrayList2 = this.getSomeRecurse(categoryPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).category.release((Object)categoryPredicate);
		return arrayList2;
	}

	public ArrayList getSomeTag(String string, int int1, ArrayList arrayList) {
		ItemContainer.TagPredicate tagPredicate = ((ItemContainer.TagPredicate)((ItemContainer.Predicates)TL_predicates.get()).tag.alloc()).init(string);
		ArrayList arrayList2 = this.getSome(tagPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).tag.release((Object)tagPredicate);
		return arrayList2;
	}

	public ArrayList getSomeTagEval(String string, LuaClosure luaClosure, int int1, ArrayList arrayList) {
		ItemContainer.TagEvalPredicate tagEvalPredicate = ((ItemContainer.TagEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEval.alloc()).init(string, luaClosure);
		ArrayList arrayList2 = this.getSome(tagEvalPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).tagEval.release((Object)tagEvalPredicate);
		return arrayList2;
	}

	public ArrayList getSomeTagEvalArg(String string, LuaClosure luaClosure, Object object, int int1, ArrayList arrayList) {
		ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = ((ItemContainer.TagEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.alloc()).init(string, luaClosure, object);
		ArrayList arrayList2 = this.getSome(tagEvalArgPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.release((Object)tagEvalArgPredicate);
		return arrayList2;
	}

	public ArrayList getSomeTagRecurse(String string, int int1, ArrayList arrayList) {
		ItemContainer.TagPredicate tagPredicate = ((ItemContainer.TagPredicate)((ItemContainer.Predicates)TL_predicates.get()).tag.alloc()).init(string);
		ArrayList arrayList2 = this.getSomeRecurse(tagPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).tag.release((Object)tagPredicate);
		return arrayList2;
	}

	public ArrayList getSomeTagEvalRecurse(String string, LuaClosure luaClosure, int int1, ArrayList arrayList) {
		ItemContainer.TagEvalPredicate tagEvalPredicate = ((ItemContainer.TagEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEval.alloc()).init(string, luaClosure);
		ArrayList arrayList2 = this.getSomeRecurse(tagEvalPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).tagEval.release((Object)tagEvalPredicate);
		return arrayList2;
	}

	public ArrayList getSomeTagEvalArgRecurse(String string, LuaClosure luaClosure, Object object, int int1, ArrayList arrayList) {
		ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = ((ItemContainer.TagEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.alloc()).init(string, luaClosure, object);
		ArrayList arrayList2 = this.getSomeRecurse(tagEvalArgPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.release((Object)tagEvalArgPredicate);
		return arrayList2;
	}

	public ArrayList getSomeType(String string, int int1, ArrayList arrayList) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		ArrayList arrayList2 = this.getSome(typePredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
		return arrayList2;
	}

	public ArrayList getSomeTypeEval(String string, LuaClosure luaClosure, int int1, ArrayList arrayList) {
		ItemContainer.TypeEvalPredicate typeEvalPredicate = ((ItemContainer.TypeEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEval.alloc()).init(string, luaClosure);
		ArrayList arrayList2 = this.getSome(typeEvalPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).typeEval.release((Object)typeEvalPredicate);
		return arrayList2;
	}

	public ArrayList getSomeTypeEvalArg(String string, LuaClosure luaClosure, Object object, int int1, ArrayList arrayList) {
		ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = ((ItemContainer.TypeEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.alloc()).init(string, luaClosure, object);
		ArrayList arrayList2 = this.getSome(typeEvalArgPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.release((Object)typeEvalArgPredicate);
		return arrayList2;
	}

	public ArrayList getSomeTypeRecurse(String string, int int1, ArrayList arrayList) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		ArrayList arrayList2 = this.getSomeRecurse(typePredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
		return arrayList2;
	}

	public ArrayList getSomeTypeEvalRecurse(String string, LuaClosure luaClosure, int int1, ArrayList arrayList) {
		ItemContainer.TypeEvalPredicate typeEvalPredicate = ((ItemContainer.TypeEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEval.alloc()).init(string, luaClosure);
		ArrayList arrayList2 = this.getSomeRecurse(typeEvalPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).typeEval.release((Object)typeEvalPredicate);
		return arrayList2;
	}

	public ArrayList getSomeTypeEvalArgRecurse(String string, LuaClosure luaClosure, Object object, int int1, ArrayList arrayList) {
		ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = ((ItemContainer.TypeEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.alloc()).init(string, luaClosure, object);
		ArrayList arrayList2 = this.getSomeRecurse(typeEvalArgPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.release((Object)typeEvalArgPredicate);
		return arrayList2;
	}

	public ArrayList getSomeEval(LuaClosure luaClosure, int int1, ArrayList arrayList) {
		ItemContainer.EvalPredicate evalPredicate = ((ItemContainer.EvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).eval.alloc()).init(luaClosure);
		ArrayList arrayList2 = this.getSome(evalPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).eval.release((Object)evalPredicate);
		return arrayList2;
	}

	public ArrayList getSomeEvalArg(LuaClosure luaClosure, Object object, int int1, ArrayList arrayList) {
		ItemContainer.EvalArgPredicate evalArgPredicate = ((ItemContainer.EvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).evalArg.alloc()).init(luaClosure, object);
		ArrayList arrayList2 = this.getSome(evalArgPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).evalArg.release((Object)evalArgPredicate);
		return arrayList2;
	}

	public ArrayList getSomeEvalRecurse(LuaClosure luaClosure, int int1, ArrayList arrayList) {
		ItemContainer.EvalPredicate evalPredicate = ((ItemContainer.EvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).eval.alloc()).init(luaClosure);
		ArrayList arrayList2 = this.getSomeRecurse(evalPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).eval.release((Object)evalPredicate);
		return arrayList2;
	}

	public ArrayList getSomeEvalArgRecurse(LuaClosure luaClosure, Object object, int int1, ArrayList arrayList) {
		ItemContainer.EvalArgPredicate evalArgPredicate = ((ItemContainer.EvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).evalArg.alloc()).init(luaClosure, object);
		ArrayList arrayList2 = this.getSomeRecurse(evalArgPredicate, int1, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).evalArg.release((Object)evalArgPredicate);
		return arrayList2;
	}

	public ArrayList getAllCategory(String string, ArrayList arrayList) {
		ItemContainer.CategoryPredicate categoryPredicate = ((ItemContainer.CategoryPredicate)((ItemContainer.Predicates)TL_predicates.get()).category.alloc()).init(string);
		ArrayList arrayList2 = this.getAll(categoryPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).category.release((Object)categoryPredicate);
		return arrayList2;
	}

	public ArrayList getAllCategoryRecurse(String string, ArrayList arrayList) {
		ItemContainer.CategoryPredicate categoryPredicate = ((ItemContainer.CategoryPredicate)((ItemContainer.Predicates)TL_predicates.get()).category.alloc()).init(string);
		ArrayList arrayList2 = this.getAllRecurse(categoryPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).category.release((Object)categoryPredicate);
		return arrayList2;
	}

	public ArrayList getAllTag(String string, ArrayList arrayList) {
		ItemContainer.TagPredicate tagPredicate = ((ItemContainer.TagPredicate)((ItemContainer.Predicates)TL_predicates.get()).tag.alloc()).init(string);
		ArrayList arrayList2 = this.getAll(tagPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).tag.release((Object)tagPredicate);
		return arrayList2;
	}

	public ArrayList getAllTagEval(String string, LuaClosure luaClosure, ArrayList arrayList) {
		ItemContainer.TagEvalPredicate tagEvalPredicate = ((ItemContainer.TagEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEval.alloc()).init(string, luaClosure);
		ArrayList arrayList2 = this.getAll(tagEvalPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).tagEval.release((Object)tagEvalPredicate);
		return arrayList2;
	}

	public ArrayList getAllTagEvalArg(String string, LuaClosure luaClosure, Object object, ArrayList arrayList) {
		ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = ((ItemContainer.TagEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.alloc()).init(string, luaClosure, object);
		ArrayList arrayList2 = this.getAll(tagEvalArgPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.release((Object)tagEvalArgPredicate);
		return arrayList2;
	}

	public ArrayList getAllTagRecurse(String string, ArrayList arrayList) {
		ItemContainer.TagPredicate tagPredicate = ((ItemContainer.TagPredicate)((ItemContainer.Predicates)TL_predicates.get()).tag.alloc()).init(string);
		ArrayList arrayList2 = this.getAllRecurse(tagPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).tag.release((Object)tagPredicate);
		return arrayList2;
	}

	public ArrayList getAllTagEvalRecurse(String string, LuaClosure luaClosure, ArrayList arrayList) {
		ItemContainer.TagEvalPredicate tagEvalPredicate = ((ItemContainer.TagEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEval.alloc()).init(string, luaClosure);
		ArrayList arrayList2 = this.getAllRecurse(tagEvalPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).tagEval.release((Object)tagEvalPredicate);
		return arrayList2;
	}

	public ArrayList getAllTagEvalArgRecurse(String string, LuaClosure luaClosure, Object object, ArrayList arrayList) {
		ItemContainer.TagEvalArgPredicate tagEvalArgPredicate = ((ItemContainer.TagEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.alloc()).init(string, luaClosure, object);
		ArrayList arrayList2 = this.getAllRecurse(tagEvalArgPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).tagEvalArg.release((Object)tagEvalArgPredicate);
		return arrayList2;
	}

	public ArrayList getAllType(String string, ArrayList arrayList) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		ArrayList arrayList2 = this.getAll(typePredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
		return arrayList2;
	}

	public ArrayList getAllTypeEval(String string, LuaClosure luaClosure, ArrayList arrayList) {
		ItemContainer.TypeEvalPredicate typeEvalPredicate = ((ItemContainer.TypeEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEval.alloc()).init(string, luaClosure);
		ArrayList arrayList2 = this.getAll(typeEvalPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).typeEval.release((Object)typeEvalPredicate);
		return arrayList2;
	}

	public ArrayList getAllTypeEvalArg(String string, LuaClosure luaClosure, Object object, ArrayList arrayList) {
		ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = ((ItemContainer.TypeEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.alloc()).init(string, luaClosure, object);
		ArrayList arrayList2 = this.getAll(typeEvalArgPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.release((Object)typeEvalArgPredicate);
		return arrayList2;
	}

	public ArrayList getAllTypeRecurse(String string, ArrayList arrayList) {
		ItemContainer.TypePredicate typePredicate = ((ItemContainer.TypePredicate)((ItemContainer.Predicates)TL_predicates.get()).type.alloc()).init(string);
		ArrayList arrayList2 = this.getAllRecurse(typePredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).type.release((Object)typePredicate);
		return arrayList2;
	}

	public ArrayList getAllTypeEvalRecurse(String string, LuaClosure luaClosure, ArrayList arrayList) {
		ItemContainer.TypeEvalPredicate typeEvalPredicate = ((ItemContainer.TypeEvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEval.alloc()).init(string, luaClosure);
		ArrayList arrayList2 = this.getAllRecurse(typeEvalPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).typeEval.release((Object)typeEvalPredicate);
		return arrayList2;
	}

	public ArrayList getAllTypeEvalArgRecurse(String string, LuaClosure luaClosure, Object object, ArrayList arrayList) {
		ItemContainer.TypeEvalArgPredicate typeEvalArgPredicate = ((ItemContainer.TypeEvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.alloc()).init(string, luaClosure, object);
		ArrayList arrayList2 = this.getAllRecurse(typeEvalArgPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).typeEvalArg.release((Object)typeEvalArgPredicate);
		return arrayList2;
	}

	public ArrayList getAllEval(LuaClosure luaClosure, ArrayList arrayList) {
		ItemContainer.EvalPredicate evalPredicate = ((ItemContainer.EvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).eval.alloc()).init(luaClosure);
		ArrayList arrayList2 = this.getAll(evalPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).eval.release((Object)evalPredicate);
		return arrayList2;
	}

	public ArrayList getAllEvalArg(LuaClosure luaClosure, Object object, ArrayList arrayList) {
		ItemContainer.EvalArgPredicate evalArgPredicate = ((ItemContainer.EvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).evalArg.alloc()).init(luaClosure, object);
		ArrayList arrayList2 = this.getAll(evalArgPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).evalArg.release((Object)evalArgPredicate);
		return arrayList2;
	}

	public ArrayList getAllEvalRecurse(LuaClosure luaClosure, ArrayList arrayList) {
		ItemContainer.EvalPredicate evalPredicate = ((ItemContainer.EvalPredicate)((ItemContainer.Predicates)TL_predicates.get()).eval.alloc()).init(luaClosure);
		ArrayList arrayList2 = this.getAllRecurse(evalPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).eval.release((Object)evalPredicate);
		return arrayList2;
	}

	public ArrayList getAllEvalArgRecurse(LuaClosure luaClosure, Object object, ArrayList arrayList) {
		ItemContainer.EvalArgPredicate evalArgPredicate = ((ItemContainer.EvalArgPredicate)((ItemContainer.Predicates)TL_predicates.get()).evalArg.alloc()).init(luaClosure, object);
		ArrayList arrayList2 = this.getAllRecurse(evalArgPredicate, arrayList);
		((ItemContainer.Predicates)TL_predicates.get()).evalArg.release((Object)evalArgPredicate);
		return arrayList2;
	}

	public ArrayList getSomeCategory(String string, int int1) {
		return this.getSomeCategory(string, int1, new ArrayList());
	}

	public ArrayList getSomeEval(LuaClosure luaClosure, int int1) {
		return this.getSomeEval(luaClosure, int1, new ArrayList());
	}

	public ArrayList getSomeEvalArg(LuaClosure luaClosure, Object object, int int1) {
		return this.getSomeEvalArg(luaClosure, object, int1, new ArrayList());
	}

	public ArrayList getSomeTypeEval(String string, LuaClosure luaClosure, int int1) {
		return this.getSomeTypeEval(string, luaClosure, int1, new ArrayList());
	}

	public ArrayList getSomeTypeEvalArg(String string, LuaClosure luaClosure, Object object, int int1) {
		return this.getSomeTypeEvalArg(string, luaClosure, object, int1, new ArrayList());
	}

	public ArrayList getSomeEvalRecurse(LuaClosure luaClosure, int int1) {
		return this.getSomeEvalRecurse(luaClosure, int1, new ArrayList());
	}

	public ArrayList getSomeEvalArgRecurse(LuaClosure luaClosure, Object object, int int1) {
		return this.getSomeEvalArgRecurse(luaClosure, object, int1, new ArrayList());
	}

	public ArrayList getSomeTag(String string, int int1) {
		return this.getSomeTag(string, int1, new ArrayList());
	}

	public ArrayList getSomeTagRecurse(String string, int int1) {
		return this.getSomeTagRecurse(string, int1, new ArrayList());
	}

	public ArrayList getSomeTagEvalRecurse(String string, LuaClosure luaClosure, int int1) {
		return this.getSomeTagEvalRecurse(string, luaClosure, int1, new ArrayList());
	}

	public ArrayList getSomeTagEvalArgRecurse(String string, LuaClosure luaClosure, Object object, int int1) {
		return this.getSomeTagEvalArgRecurse(string, luaClosure, object, int1, new ArrayList());
	}

	public ArrayList getSomeType(String string, int int1) {
		return this.getSomeType(string, int1, new ArrayList());
	}

	public ArrayList getSomeTypeRecurse(String string, int int1) {
		return this.getSomeTypeRecurse(string, int1, new ArrayList());
	}

	public ArrayList getSomeTypeEvalRecurse(String string, LuaClosure luaClosure, int int1) {
		return this.getSomeTypeEvalRecurse(string, luaClosure, int1, new ArrayList());
	}

	public ArrayList getSomeTypeEvalArgRecurse(String string, LuaClosure luaClosure, Object object, int int1) {
		return this.getSomeTypeEvalArgRecurse(string, luaClosure, object, int1, new ArrayList());
	}

	public ArrayList getAll(Predicate predicate) {
		return this.getAll(predicate, new ArrayList());
	}

	public ArrayList getAllCategory(String string) {
		return this.getAllCategory(string, new ArrayList());
	}

	public ArrayList getAllEval(LuaClosure luaClosure) {
		return this.getAllEval(luaClosure, new ArrayList());
	}

	public ArrayList getAllEvalArg(LuaClosure luaClosure, Object object) {
		return this.getAllEvalArg(luaClosure, object, new ArrayList());
	}

	public ArrayList getAllTagEval(String string, LuaClosure luaClosure) {
		return this.getAllTagEval(string, luaClosure, new ArrayList());
	}

	public ArrayList getAllTagEvalArg(String string, LuaClosure luaClosure, Object object) {
		return this.getAllTagEvalArg(string, luaClosure, object, new ArrayList());
	}

	public ArrayList getAllTypeEval(String string, LuaClosure luaClosure) {
		return this.getAllTypeEval(string, luaClosure, new ArrayList());
	}

	public ArrayList getAllTypeEvalArg(String string, LuaClosure luaClosure, Object object) {
		return this.getAllTypeEvalArg(string, luaClosure, object, new ArrayList());
	}

	public ArrayList getAllEvalRecurse(LuaClosure luaClosure) {
		return this.getAllEvalRecurse(luaClosure, new ArrayList());
	}

	public ArrayList getAllEvalArgRecurse(LuaClosure luaClosure, Object object) {
		return this.getAllEvalArgRecurse(luaClosure, object, new ArrayList());
	}

	public ArrayList getAllType(String string) {
		return this.getAllType(string, new ArrayList());
	}

	public ArrayList getAllTypeRecurse(String string) {
		return this.getAllTypeRecurse(string, new ArrayList());
	}

	public ArrayList getAllTypeEvalRecurse(String string, LuaClosure luaClosure) {
		return this.getAllTypeEvalRecurse(string, luaClosure, new ArrayList());
	}

	public ArrayList getAllTypeEvalArgRecurse(String string, LuaClosure luaClosure, Object object) {
		return this.getAllTypeEvalArgRecurse(string, luaClosure, object, new ArrayList());
	}

	public InventoryItem FindAndReturnCategory(String string) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.getCategory().equals(string)) {
				return inventoryItem;
			}
		}

		return null;
	}

	public ArrayList FindAndReturn(String string, int int1) {
		return this.getSomeType(string, int1);
	}

	public InventoryItem FindAndReturn(String string, ArrayList arrayList) {
		if (string == null) {
			return null;
		} else {
			for (int int1 = 0; int1 < this.Items.size(); ++int1) {
				InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
				if (inventoryItem.type != null && compareType(string, inventoryItem) && !arrayList.contains(inventoryItem)) {
					return inventoryItem;
				}
			}

			return null;
		}
	}

	public InventoryItem FindAndReturn(String string) {
		return this.getFirstType(string);
	}

	public ArrayList FindAll(String string) {
		return this.getAllType(string);
	}

	public InventoryItem FindAndReturnStack(String string) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (compareType(string, inventoryItem)) {
				InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem(inventoryItem.module + "." + string);
				if (inventoryItem.CanStack(inventoryItem2)) {
					return inventoryItem;
				}
			}
		}

		return null;
	}

	public InventoryItem FindAndReturnStack(InventoryItem inventoryItem) {
		String string = inventoryItem.type;
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int1);
			if (inventoryItem2.type == null) {
				if (string != null) {
					continue;
				}
			} else if (!inventoryItem2.type.equals(string)) {
				continue;
			}

			if (inventoryItem2.CanStack(inventoryItem)) {
				return inventoryItem2;
			}
		}

		return null;
	}

	public boolean HasType(ItemType itemType) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.cat == itemType) {
				return true;
			}
		}

		return false;
	}

	public void Remove(InventoryItem inventoryItem) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int1);
			if (inventoryItem2 == inventoryItem) {
				if (inventoryItem.uses > 1) {
					--inventoryItem.uses;
				} else {
					this.Items.remove(inventoryItem);
				}

				inventoryItem.container = null;
				this.drawDirty = true;
				this.dirty = true;
				if (this.parent != null) {
					this.dirty = true;
				}

				if (this.parent instanceof IsoDeadBody) {
					((IsoDeadBody)this.parent).checkClothing(inventoryItem);
				}

				if (this.parent instanceof IsoMannequin) {
					((IsoMannequin)this.parent).checkClothing(inventoryItem);
				}

				return;
			}
		}
	}

	public void DoRemoveItem(InventoryItem inventoryItem) {
		this.drawDirty = true;
		if (this.parent != null) {
			this.dirty = true;
		}

		this.Items.remove(inventoryItem);
		inventoryItem.container = null;
		if (this.parent instanceof IsoDeadBody) {
			((IsoDeadBody)this.parent).checkClothing(inventoryItem);
		}

		if (this.parent instanceof IsoMannequin) {
			((IsoMannequin)this.parent).checkClothing(inventoryItem);
		}
	}

	public void Remove(String string) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.type.equals(string)) {
				if (inventoryItem.uses > 1) {
					--inventoryItem.uses;
				} else {
					this.Items.remove(inventoryItem);
				}

				inventoryItem.container = null;
				this.drawDirty = true;
				this.dirty = true;
				if (this.parent != null) {
					this.dirty = true;
				}

				return;
			}
		}
	}

	public InventoryItem Remove(ItemType itemType) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.cat == itemType) {
				this.Items.remove(inventoryItem);
				inventoryItem.container = null;
				this.drawDirty = true;
				this.dirty = true;
				if (this.parent != null) {
					this.dirty = true;
				}

				return inventoryItem;
			}
		}

		return null;
	}

	public InventoryItem Find(ItemType itemType) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.cat == itemType) {
				return inventoryItem;
			}
		}

		return null;
	}

	public void RemoveAll(String string) {
		this.drawDirty = true;
		if (this.parent != null) {
			this.dirty = true;
		}

		ArrayList arrayList = new ArrayList();
		InventoryItem inventoryItem;
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.type.equals(string)) {
				inventoryItem.container = null;
				arrayList.add(inventoryItem);
				this.dirty = true;
			}
		}

		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			inventoryItem = (InventoryItem)iterator.next();
			this.Items.remove(inventoryItem);
		}
	}

	public boolean RemoveOneOf(String string, boolean boolean1) {
		this.drawDirty = true;
		if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
			this.dirty = true;
		}

		int int1;
		InventoryItem inventoryItem;
		for (int1 = 0; int1 < this.Items.size(); ++int1) {
			inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.getFullType().equals(string) || inventoryItem.type.equals(string)) {
				if (inventoryItem.uses > 1) {
					--inventoryItem.uses;
				} else {
					inventoryItem.container = null;
					this.Items.remove(inventoryItem);
				}

				this.dirty = true;
				return true;
			}
		}

		if (boolean1) {
			for (int1 = 0; int1 < this.Items.size(); ++int1) {
				inventoryItem = (InventoryItem)this.Items.get(int1);
				if (inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getItemContainer() != null && ((InventoryContainer)inventoryItem).getItemContainer().RemoveOneOf(string, boolean1)) {
					return true;
				}
			}
		}

		return false;
	}

	public void RemoveOneOf(String string) {
		this.RemoveOneOf(string, true);
	}

	public int getWeight() {
		if (this.parent instanceof IsoPlayer && ((IsoPlayer)this.parent).isGhostMode()) {
			return 0;
		} else {
			float float1 = 0.0F;
			for (int int1 = 0; int1 < this.Items.size(); ++int1) {
				InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
				float1 += inventoryItem.ActualWeight * (float)inventoryItem.uses;
			}

			return (int)(float1 * ((float)this.weightReduction / 0.01F));
		}
	}

	public float getContentsWeight() {
		float float1 = 0.0F;
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			float1 += inventoryItem.getUnequippedWeight();
		}

		return float1;
	}

	public float getMaxWeight() {
		return this.parent instanceof IsoGameCharacter ? (float)((IsoGameCharacter)this.parent).getMaxWeight() : (float)this.Capacity;
	}

	public float getCapacityWeight() {
		if (this.parent instanceof IsoPlayer) {
			if (Core.bDebug && ((IsoPlayer)this.parent).isGhostMode() || !((IsoPlayer)this.parent).getAccessLevel().equals("None") && ((IsoPlayer)this.parent).isUnlimitedCarry()) {
				return 0.0F;
			}

			if (((IsoPlayer)this.parent).isUnlimitedCarry()) {
				return 0.0F;
			}
		}

		return this.parent instanceof IsoGameCharacter ? ((IsoGameCharacter)this.parent).getInventoryWeight() : this.getContentsWeight();
	}

	public boolean isEmpty() {
		return this.Items == null || this.Items.isEmpty();
	}

	public boolean isMicrowave() {
		return "microwave".equals(this.getType());
	}

	private boolean isSquareInRoom(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else {
			return square.getRoom() != null;
		}
	}

	private boolean isSquarePowered(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else {
			boolean boolean1 = GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getElecShutModifier();
			if (boolean1 && square.getRoom() != null) {
				return true;
			} else if (square.haveElectricity()) {
				return true;
			} else {
				if (boolean1 && square.getRoom() == null) {
					IsoGridSquare square2 = square.nav[IsoDirections.N.index()];
					IsoGridSquare square3 = square.nav[IsoDirections.S.index()];
					IsoGridSquare square4 = square.nav[IsoDirections.W.index()];
					IsoGridSquare square5 = square.nav[IsoDirections.E.index()];
					if (this.isSquareInRoom(square2) || this.isSquareInRoom(square3) || this.isSquareInRoom(square4) || this.isSquareInRoom(square5)) {
						return true;
					}
				}

				return false;
			}
		}
	}

	public boolean isPowered() {
		if (this.parent != null && this.parent.getObjectIndex() != -1) {
			IsoGridSquare square = this.parent.getSquare();
			if (this.isSquarePowered(square)) {
				return true;
			} else {
				this.parent.getSpriteGridObjects(s_tempObjects);
				for (int int1 = 0; int1 < s_tempObjects.size(); ++int1) {
					IsoObject object = (IsoObject)s_tempObjects.get(int1);
					if (object != this.parent) {
						IsoGridSquare square2 = object.getSquare();
						if (this.isSquarePowered(square2)) {
							return true;
						}
					}
				}

				return false;
			}
		} else {
			return false;
		}
	}

	public float getTemprature() {
		if (this.customTemperature != 0.0F) {
			return this.customTemperature;
		} else {
			boolean boolean1 = false;
			if (this.getParent() != null && this.getParent().getSprite() != null) {
				boolean1 = this.getParent().getSprite().getProperties().Is("IsFridge");
			}

			if (this.isPowered()) {
				if (this.type.equals("fridge") || this.type.equals("freezer") || boolean1) {
					return 0.2F;
				}

				if (("stove".equals(this.type) || "microwave".equals(this.type)) && this.parent instanceof IsoStove) {
					return ((IsoStove)this.parent).getCurrentTemperature() / 100.0F;
				}
			}

			if ("barbecue".equals(this.type) && this.parent instanceof IsoBarbecue) {
				return ((IsoBarbecue)this.parent).getTemperature();
			} else if ("fireplace".equals(this.type) && this.parent instanceof IsoFireplace) {
				return ((IsoFireplace)this.parent).getTemperature();
			} else if ("woodstove".equals(this.type) && this.parent instanceof IsoFireplace) {
				return ((IsoFireplace)this.parent).getTemperature();
			} else if ((this.type.equals("fridge") || this.type.equals("freezer") || boolean1) && GameTime.instance.NightsSurvived == SandboxOptions.instance.getElecShutModifier() && GameTime.instance.getTimeOfDay() < 13.0F) {
				float float1 = (GameTime.instance.getTimeOfDay() - 7.0F) / 6.0F;
				return GameTime.instance.Lerp(0.2F, 1.0F, float1);
			} else {
				return 1.0F;
			}
		}
	}

	public boolean isTemperatureChanging() {
		return this.parent instanceof IsoStove ? ((IsoStove)this.parent).isTemperatureChanging() : false;
	}

	public ArrayList save(ByteBuffer byteBuffer, IsoGameCharacter gameCharacter) throws IOException {
		GameWindow.WriteString(byteBuffer, this.type);
		byteBuffer.put((byte)(this.bExplored ? 1 : 0));
		ArrayList arrayList = CompressIdenticalItems.save(byteBuffer, this.Items, (IsoGameCharacter)null);
		byteBuffer.put((byte)(this.isHasBeenLooted() ? 1 : 0));
		byteBuffer.putInt(this.Capacity);
		return arrayList;
	}

	public ArrayList save(ByteBuffer byteBuffer) throws IOException {
		return this.save(byteBuffer, (IsoGameCharacter)null);
	}

	public ArrayList load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.type = GameWindow.ReadString(byteBuffer);
		this.bExplored = byteBuffer.get() == 1;
		ArrayList arrayList = CompressIdenticalItems.load(byteBuffer, int1, this.Items, this.IncludingObsoleteItems);
		for (int int2 = 0; int2 < this.Items.size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
			inventoryItem.container = this;
		}

		this.setHasBeenLooted(byteBuffer.get() == 1);
		this.Capacity = byteBuffer.getInt();
		this.dirty = false;
		return arrayList;
	}

	public boolean isDrawDirty() {
		return this.drawDirty;
	}

	public void setDrawDirty(boolean boolean1) {
		this.drawDirty = boolean1;
	}

	public InventoryItem getBestWeapon(SurvivorDesc survivorDesc) {
		InventoryItem inventoryItem = null;
		float float1 = -1.0E7F;
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int1);
			if (inventoryItem2 instanceof HandWeapon) {
				float float2 = inventoryItem2.getScore(survivorDesc);
				if (float2 >= float1) {
					float1 = float2;
					inventoryItem = inventoryItem2;
				}
			}
		}

		return inventoryItem;
	}

	public InventoryItem getBestWeapon() {
		InventoryItem inventoryItem = null;
		float float1 = 0.0F;
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int1);
			if (inventoryItem2 instanceof HandWeapon) {
				float float2 = inventoryItem2.getScore((SurvivorDesc)null);
				if (float2 >= float1) {
					float1 = float2;
					inventoryItem = inventoryItem2;
				}
			}
		}

		return inventoryItem;
	}

	public float getTotalFoodScore(SurvivorDesc survivorDesc) {
		float float1 = 0.0F;
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem instanceof Food) {
				float1 += inventoryItem.getScore(survivorDesc);
			}
		}

		return float1;
	}

	public float getTotalWeaponScore(SurvivorDesc survivorDesc) {
		float float1 = 0.0F;
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem instanceof HandWeapon) {
				float1 += inventoryItem.getScore(survivorDesc);
			}
		}

		return float1;
	}

	public InventoryItem getBestFood(SurvivorDesc survivorDesc) {
		InventoryItem inventoryItem = null;
		float float1 = 0.0F;
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int1);
			if (inventoryItem2 instanceof Food) {
				float float2 = inventoryItem2.getScore(survivorDesc);
				if (((Food)inventoryItem2).isbDangerousUncooked() && !inventoryItem2.isCooked()) {
					float2 *= 0.2F;
				}

				if (((Food)inventoryItem2).Age > (float)inventoryItem2.OffAge) {
					float2 *= 0.2F;
				}

				if (float2 >= float1) {
					float1 = float2;
					inventoryItem = inventoryItem2;
				}
			}
		}

		return inventoryItem;
	}

	public InventoryItem getBestBandage(SurvivorDesc survivorDesc) {
		InventoryItem inventoryItem = null;
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int1);
			if (inventoryItem2.isCanBandage()) {
				inventoryItem = inventoryItem2;
				break;
			}
		}

		return inventoryItem;
	}

	public int getNumItems(String string) {
		int int1 = 0;
		int int2;
		InventoryItem inventoryItem;
		if (string.contains("Type:")) {
			for (int2 = 0; int2 < this.Items.size(); ++int2) {
				inventoryItem = (InventoryItem)this.Items.get(int2);
				if (inventoryItem instanceof Food && string.contains("Food")) {
					int1 += inventoryItem.uses;
				}

				if (inventoryItem instanceof HandWeapon && string.contains("Weapon")) {
					int1 += inventoryItem.uses;
				}
			}
		} else {
			for (int2 = 0; int2 < this.Items.size(); ++int2) {
				inventoryItem = (InventoryItem)this.Items.get(int2);
				if (inventoryItem.type.equals(string)) {
					int1 += inventoryItem.uses;
				}
			}
		}

		return int1;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean boolean1) {
		this.active = boolean1;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty(boolean boolean1) {
		this.dirty = boolean1;
	}

	public boolean isIsDevice() {
		return this.IsDevice;
	}

	public void setIsDevice(boolean boolean1) {
		this.IsDevice = boolean1;
	}

	public float getAgeFactor() {
		return this.ageFactor;
	}

	public void setAgeFactor(float float1) {
		this.ageFactor = float1;
	}

	public float getCookingFactor() {
		return this.CookingFactor;
	}

	public void setCookingFactor(float float1) {
		this.CookingFactor = float1;
	}

	public ArrayList getItems() {
		return this.Items;
	}

	public void setItems(ArrayList arrayList) {
		this.Items = arrayList;
	}

	public IsoObject getParent() {
		return this.parent;
	}

	public void setParent(IsoObject object) {
		this.parent = object;
	}

	public IsoGridSquare getSourceGrid() {
		return this.SourceGrid;
	}

	public void setSourceGrid(IsoGridSquare square) {
		this.SourceGrid = square;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String string) {
		this.type = string;
	}

	public void clear() {
		this.Items.clear();
		this.dirty = true;
		this.drawDirty = true;
	}

	public int getWaterContainerCount() {
		int int1 = 0;
		for (int int2 = 0; int2 < this.Items.size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
			if (inventoryItem.CanStoreWater) {
				++int1;
			}
		}

		return int1;
	}

	public InventoryItem FindWaterSource() {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.isWaterSource()) {
				if (!(inventoryItem instanceof Drainable)) {
					return inventoryItem;
				}

				if (((Drainable)inventoryItem).getUsedDelta() > 0.0F) {
					return inventoryItem;
				}
			}
		}

		return null;
	}

	public ArrayList getAllWaterFillables() {
		tempList.clear();
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.CanStoreWater) {
				tempList.add(inventoryItem);
			}
		}

		return tempList;
	}

	public int getItemCount(String string) {
		return this.getCountType(string);
	}

	public int getItemCountRecurse(String string) {
		return this.getCountTypeRecurse(string);
	}

	public int getItemCount(String string, boolean boolean1) {
		return boolean1 ? this.getCountTypeRecurse(string) : this.getCountType(string);
	}

	private static int getUses(ItemContainer.InventoryItemList inventoryItemList) {
		int int1 = 0;
		for (int int2 = 0; int2 < inventoryItemList.size(); ++int2) {
			DrainableComboItem drainableComboItem = (DrainableComboItem)Type.tryCastTo((InventoryItem)inventoryItemList.get(int2), DrainableComboItem.class);
			if (drainableComboItem != null) {
				int1 += drainableComboItem.getDrainableUsesInt();
			} else {
				++int1;
			}
		}

		return int1;
	}

	public int getUsesRecurse(Predicate predicate) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		this.getAllRecurse(predicate, inventoryItemList);
		int int1 = getUses(inventoryItemList);
		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return int1;
	}

	public int getUsesType(String string) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		this.getAllType(string, inventoryItemList);
		int int1 = getUses(inventoryItemList);
		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return int1;
	}

	public int getUsesTypeRecurse(String string) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		this.getAllTypeRecurse(string, inventoryItemList);
		int int1 = getUses(inventoryItemList);
		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return int1;
	}

	public int getWeightReduction() {
		return this.weightReduction;
	}

	public void setWeightReduction(int int1) {
		int1 = Math.min(int1, 100);
		int1 = Math.max(int1, 0);
		this.weightReduction = int1;
	}

	public void removeAllItems() {
		this.drawDirty = true;
		if (this.parent != null) {
			this.dirty = true;
		}

		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			inventoryItem.container = null;
		}

		this.Items.clear();
		if (this.parent instanceof IsoDeadBody) {
			((IsoDeadBody)this.parent).checkClothing((InventoryItem)null);
		}

		if (this.parent instanceof IsoMannequin) {
			((IsoMannequin)this.parent).checkClothing((InventoryItem)null);
		}
	}

	public boolean containsRecursive(InventoryItem inventoryItem) {
		for (int int1 = 0; int1 < this.getItems().size(); ++int1) {
			InventoryItem inventoryItem2 = (InventoryItem)this.getItems().get(int1);
			if (inventoryItem2 == inventoryItem) {
				return true;
			}

			if (inventoryItem2 instanceof InventoryContainer && ((InventoryContainer)inventoryItem2).getInventory().containsRecursive(inventoryItem)) {
				return true;
			}
		}

		return false;
	}

	public int getItemCountFromTypeRecurse(String string) {
		int int1 = 0;
		for (int int2 = 0; int2 < this.getItems().size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.getItems().get(int2);
			if (inventoryItem.getFullType().equals(string)) {
				++int1;
			}

			if (inventoryItem instanceof InventoryContainer) {
				int int3 = ((InventoryContainer)inventoryItem).getInventory().getItemCountFromTypeRecurse(string);
				int1 += int3;
			}
		}

		return int1;
	}

	public float getCustomTemperature() {
		return this.customTemperature;
	}

	public void setCustomTemperature(float float1) {
		this.customTemperature = float1;
	}

	public InventoryItem getItemFromType(String string, IsoGameCharacter gameCharacter, boolean boolean1, boolean boolean2, boolean boolean3) {
		ItemContainer.InventoryItemList inventoryItemList = (ItemContainer.InventoryItemList)((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).alloc();
		if (string.contains(".")) {
			string = string.split("\\.")[1];
		}

		int int1;
		for (int1 = 0; int1 < this.getItems().size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.getItems().get(int1);
			if (!inventoryItem.getFullType().equals(string) && !inventoryItem.getType().equals(string)) {
				if (boolean3 && inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getInventory() != null && !inventoryItemList.contains(inventoryItem)) {
					inventoryItemList.add(inventoryItem);
				}
			} else if ((!boolean1 || gameCharacter == null || !gameCharacter.isEquippedClothing(inventoryItem)) && this.testBroken(boolean2, inventoryItem)) {
				((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
				return inventoryItem;
			}
		}

		for (int1 = 0; int1 < inventoryItemList.size(); ++int1) {
			ItemContainer itemContainer = ((InventoryContainer)inventoryItemList.get(int1)).getInventory();
			InventoryItem inventoryItem2 = itemContainer.getItemFromType(string, gameCharacter, boolean1, boolean2, boolean3);
			if (inventoryItem2 != null) {
				((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
				return inventoryItem2;
			}
		}

		((ItemContainer.InventoryItemListPool)TL_itemListPool.get()).release(inventoryItemList);
		return null;
	}

	public InventoryItem getItemFromType(String string, boolean boolean1, boolean boolean2) {
		return this.getItemFromType(string, (IsoGameCharacter)null, false, boolean1, boolean2);
	}

	public InventoryItem getItemFromType(String string) {
		return this.getFirstType(string);
	}

	public ArrayList getItemsFromType(String string) {
		return this.getAllType(string);
	}

	public ArrayList getItemsFromFullType(String string) {
		return string != null && string.contains(".") ? this.getAllType(string) : new ArrayList();
	}

	public ArrayList getItemsFromFullType(String string, boolean boolean1) {
		if (string != null && string.contains(".")) {
			return boolean1 ? this.getAllTypeRecurse(string) : this.getAllType(string);
		} else {
			return new ArrayList();
		}
	}

	public ArrayList getItemsFromType(String string, boolean boolean1) {
		return boolean1 ? this.getAllTypeRecurse(string) : this.getAllType(string);
	}

	public ArrayList getItemsFromCategory(String string) {
		return this.getAllCategory(string);
	}

	public void sendContentsToRemoteContainer() {
		if (GameClient.bClient) {
			this.sendContentsToRemoteContainer(GameClient.connection);
		}
	}

	public void requestSync() {
		if (GameClient.bClient) {
			if (this.parent == null || this.parent.square == null || this.parent.square.chunk == null) {
				return;
			}

			GameClient.instance.worldObjectsSyncReq.putRequestSyncIsoChunk(this.parent.square.chunk);
		}
	}

	public void requestServerItemsForContainer() {
		if (this.parent != null && this.parent.square != null) {
			UdpConnection udpConnection = GameClient.connection;
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)44, byteBufferWriter);
			byteBufferWriter.putShort((short)IsoPlayer.getInstance().OnlineID);
			byteBufferWriter.putUTF(this.type);
			if (this.parent.square.getRoom() != null) {
				byteBufferWriter.putUTF(this.parent.square.getRoom().getName());
			} else {
				byteBufferWriter.putUTF("all");
			}

			byteBufferWriter.putInt(this.parent.square.getX());
			byteBufferWriter.putInt(this.parent.square.getY());
			byteBufferWriter.putInt(this.parent.square.getZ());
			int int1 = this.parent.square.getObjects().indexOf(this.parent);
			if (int1 == -1 && this.parent.square.getStaticMovingObjects().indexOf(this.parent) != -1) {
				byteBufferWriter.putShort((short)0);
				int1 = this.parent.square.getStaticMovingObjects().indexOf(this.parent);
				byteBufferWriter.putByte((byte)int1);
			} else if (this.parent instanceof IsoWorldInventoryObject) {
				byteBufferWriter.putShort((short)1);
				byteBufferWriter.putInt(((IsoWorldInventoryObject)this.parent).getItem().id);
			} else if (this.parent instanceof BaseVehicle) {
				byteBufferWriter.putShort((short)3);
				byteBufferWriter.putShort(((BaseVehicle)this.parent).VehicleID);
				byteBufferWriter.putByte((byte)this.vehiclePart.getIndex());
			} else {
				byteBufferWriter.putShort((short)2);
				byteBufferWriter.putByte((byte)int1);
				byteBufferWriter.putByte((byte)this.parent.getContainerIndex(this));
			}

			udpConnection.endPacketUnordered();
		}
	}

	@Deprecated
	public void sendContentsToRemoteContainer(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)20, byteBufferWriter);
		byteBufferWriter.putInt(0);
		boolean boolean1 = false;
		byteBufferWriter.putInt(this.parent.square.getX());
		byteBufferWriter.putInt(this.parent.square.getY());
		byteBufferWriter.putInt(this.parent.square.getZ());
		byteBufferWriter.putByte((byte)this.parent.square.getObjects().indexOf(this.parent));
		try {
			CompressIdenticalItems.save(byteBufferWriter.bb, this.Items, (IsoGameCharacter)null);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		udpConnection.endPacketUnordered();
	}

	public InventoryItem getItemWithIDRecursiv(int int1) {
		for (int int2 = 0; int2 < this.Items.size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
			if (inventoryItem.id == int1) {
				return inventoryItem;
			}

			if (inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getItemContainer() != null && !((InventoryContainer)inventoryItem).getItemContainer().getItems().isEmpty()) {
				inventoryItem = ((InventoryContainer)inventoryItem).getItemContainer().getItemWithIDRecursiv(int1);
				if (inventoryItem != null) {
					return inventoryItem;
				}
			}
		}

		return null;
	}

	public InventoryItem getItemWithID(int int1) {
		for (int int2 = 0; int2 < this.Items.size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
			if (inventoryItem.id == int1) {
				return inventoryItem;
			}
		}

		return null;
	}

	public boolean removeItemWithID(int int1) {
		for (int int2 = 0; int2 < this.Items.size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
			if (inventoryItem.id == int1) {
				this.Remove(inventoryItem);
				return true;
			}
		}

		return false;
	}

	public boolean containsID(int int1) {
		for (int int2 = 0; int2 < this.Items.size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
			if (inventoryItem.id == int1) {
				return true;
			}
		}

		return false;
	}

	public boolean removeItemWithIDRecurse(int int1) {
		for (int int2 = 0; int2 < this.Items.size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
			if (inventoryItem.id == int1) {
				this.Remove(inventoryItem);
				return true;
			}

			if (inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getInventory().removeItemWithIDRecurse(int1)) {
				return true;
			}
		}

		return false;
	}

	public boolean isHasBeenLooted() {
		return this.hasBeenLooted;
	}

	public void setHasBeenLooted(boolean boolean1) {
		this.hasBeenLooted = boolean1;
	}

	public String getOpenSound() {
		return this.openSound;
	}

	public void setOpenSound(String string) {
		this.openSound = string;
	}

	public String getCloseSound() {
		return this.closeSound;
	}

	public void setCloseSound(String string) {
		this.closeSound = string;
	}

	public String getPutSound() {
		return this.putSound;
	}

	public void setPutSound(String string) {
		this.putSound = string;
	}

	public InventoryItem haveThisKeyId(int int1) {
		for (int int2 = 0; int2 < this.getItems().size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.getItems().get(int2);
			if (inventoryItem instanceof Key) {
				Key key = (Key)inventoryItem;
				if (key.getKeyId() == int1) {
					return key;
				}
			} else if (inventoryItem.getType().equals("KeyRing") && ((InventoryContainer)inventoryItem).getInventory().haveThisKeyId(int1) != null) {
				return ((InventoryContainer)inventoryItem).getInventory().haveThisKeyId(int1);
			}
		}

		return null;
	}

	public String getOnlyAcceptCategory() {
		return this.OnlyAcceptCategory;
	}

	public void setOnlyAcceptCategory(String string) {
		this.OnlyAcceptCategory = StringUtils.discardNullOrWhitespace(string);
	}

	public String getAcceptItemFunction() {
		return this.AcceptItemFunction;
	}

	public void setAcceptItemFunction(String string) {
		this.AcceptItemFunction = StringUtils.discardNullOrWhitespace(string);
	}

	public IsoGameCharacter getCharacter() {
		if (this.getParent() instanceof IsoGameCharacter) {
			return (IsoGameCharacter)this.getParent();
		} else {
			return this.containingItem != null && this.containingItem.getContainer() != null ? this.containingItem.getContainer().getCharacter() : null;
		}
	}

	public void emptyIt() {
		this.Items = new ArrayList();
	}

	public LinkedHashMap getItems4Admin() {
		LinkedHashMap linkedHashMap = new LinkedHashMap();
		for (int int1 = 0; int1 < this.getItems().size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.getItems().get(int1);
			inventoryItem.setCount(1);
			if (inventoryItem.getCat() != ItemType.Drainable && inventoryItem.getCat() != ItemType.Weapon && linkedHashMap.get(inventoryItem.getFullType()) != null && !(inventoryItem instanceof InventoryContainer)) {
				((InventoryItem)linkedHashMap.get(inventoryItem.getFullType())).setCount(((InventoryItem)linkedHashMap.get(inventoryItem.getFullType())).getCount() + 1);
			} else if (linkedHashMap.get(inventoryItem.getFullType()) != null) {
				linkedHashMap.put(inventoryItem.getFullType() + Rand.Next(100000), inventoryItem);
			} else {
				linkedHashMap.put(inventoryItem.getFullType(), inventoryItem);
			}
		}

		return linkedHashMap;
	}

	public LinkedHashMap getAllItems(LinkedHashMap linkedHashMap, boolean boolean1) {
		if (linkedHashMap == null) {
			linkedHashMap = new LinkedHashMap();
		}

		for (int int1 = 0; int1 < this.getItems().size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.getItems().get(int1);
			if (boolean1) {
				inventoryItem.setWorker("inInv");
			}

			inventoryItem.setCount(1);
			if (inventoryItem.getCat() != ItemType.Drainable && inventoryItem.getCat() != ItemType.Weapon && linkedHashMap.get(inventoryItem.getFullType()) != null) {
				((InventoryItem)linkedHashMap.get(inventoryItem.getFullType())).setCount(((InventoryItem)linkedHashMap.get(inventoryItem.getFullType())).getCount() + 1);
			} else if (linkedHashMap.get(inventoryItem.getFullType()) != null) {
				linkedHashMap.put(inventoryItem.getFullType() + Rand.Next(100000), inventoryItem);
			} else {
				linkedHashMap.put(inventoryItem.getFullType(), inventoryItem);
			}

			if (inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getItemContainer() != null && !((InventoryContainer)inventoryItem).getItemContainer().getItems().isEmpty()) {
				linkedHashMap = ((InventoryContainer)inventoryItem).getItemContainer().getAllItems(linkedHashMap, true);
			}
		}

		return linkedHashMap;
	}

	public InventoryItem getItemById(long long1) {
		for (int int1 = 0; int1 < this.getItems().size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.getItems().get(int1);
			if ((long)inventoryItem.getID() == long1) {
				return inventoryItem;
			}

			if (inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getItemContainer() != null && !((InventoryContainer)inventoryItem).getItemContainer().getItems().isEmpty()) {
				inventoryItem = ((InventoryContainer)inventoryItem).getItemContainer().getItemById(long1);
				if (inventoryItem != null) {
					return inventoryItem;
				}
			}
		}

		return null;
	}

	public void addItemsToProcessItems() {
		IsoWorld.instance.CurrentCell.addToProcessItems(this.Items);
	}

	public void removeItemsFromProcessItems() {
		IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this.Items);
		if (!"floor".equals(this.type)) {
			ItemSoundManager.removeItems(this.Items);
		}
	}

	public boolean isExistYet() {
		if (!SystemDisabler.doWorldSyncEnable) {
			return true;
		} else if (this.getCharacter() != null) {
			return true;
		} else if (this.getParent() instanceof BaseVehicle) {
			return true;
		} else if (this.parent instanceof IsoDeadBody) {
			return this.parent.getStaticMovingObjectIndex() != -1;
		} else if (this.parent instanceof IsoCompost) {
			return this.parent.getObjectIndex() != -1;
		} else if (this.containingItem != null && this.containingItem.worldItem != null) {
			return this.containingItem.worldItem.getWorldObjectIndex() != -1;
		} else if (this.getType().equals("floor")) {
			return true;
		} else if (this.SourceGrid == null) {
			return false;
		} else {
			IsoGridSquare square = this.SourceGrid;
			if (!square.getObjects().contains(this.parent)) {
				return false;
			} else {
				return this.parent.getContainerIndex(this) != -1;
			}
		}
	}

	public String getContainerPosition() {
		return this.containerPosition;
	}

	public void setContainerPosition(String string) {
		this.containerPosition = string;
	}

	public String getFreezerPosition() {
		return this.freezerPosition;
	}

	public void setFreezerPosition(String string) {
		this.freezerPosition = string;
	}

	public VehiclePart getVehiclePart() {
		return this.vehiclePart;
	}

	private static final class InventoryItemListPool extends ObjectPool {

		public InventoryItemListPool() {
			super(ItemContainer.InventoryItemList::new);
		}

		public void release(ItemContainer.InventoryItemList inventoryItemList) {
			inventoryItemList.clear();
			super.release((Object)inventoryItemList);
		}
	}

	private static final class InventoryItemList extends ArrayList {

		public boolean equals(Object object) {
			return this == object;
		}
	}

	private static final class Predicates {
		final ObjectPool category = new ObjectPool(ItemContainer.CategoryPredicate::new);
		final ObjectPool eval = new ObjectPool(ItemContainer.EvalPredicate::new);
		final ObjectPool evalArg = new ObjectPool(ItemContainer.EvalArgPredicate::new);
		final ObjectPool tag = new ObjectPool(ItemContainer.TagPredicate::new);
		final ObjectPool tagEval = new ObjectPool(ItemContainer.TagEvalPredicate::new);
		final ObjectPool tagEvalArg = new ObjectPool(ItemContainer.TagEvalArgPredicate::new);
		final ObjectPool type = new ObjectPool(ItemContainer.TypePredicate::new);
		final ObjectPool typeEval = new ObjectPool(ItemContainer.TypeEvalPredicate::new);
		final ObjectPool typeEvalArg = new ObjectPool(ItemContainer.TypeEvalArgPredicate::new);
	}

	private static final class TypePredicate implements Predicate {
		String type;

		ItemContainer.TypePredicate init(String string) {
			this.type = (String)Objects.requireNonNull(string);
			return this;
		}

		public boolean test(InventoryItem inventoryItem) {
			return ItemContainer.compareType(this.type, inventoryItem);
		}
	}

	private static final class EvalPredicate implements Predicate {
		LuaClosure functionObj;

		ItemContainer.EvalPredicate init(LuaClosure luaClosure) {
			this.functionObj = (LuaClosure)Objects.requireNonNull(luaClosure);
			return this;
		}

		public boolean test(InventoryItem inventoryItem) {
			return LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.functionObj, (Object)inventoryItem) == Boolean.TRUE;
		}
	}

	private static final class Comparators {
		ObjectPool condition = new ObjectPool(ItemContainer.ConditionComparator::new);
		ObjectPool eval = new ObjectPool(ItemContainer.EvalComparator::new);
		ObjectPool evalArg = new ObjectPool(ItemContainer.EvalArgComparator::new);
	}

	private static final class EvalComparator implements Comparator {
		LuaClosure functionObj;

		ItemContainer.EvalComparator init(LuaClosure luaClosure) {
			this.functionObj = (LuaClosure)Objects.requireNonNull(luaClosure);
			return this;
		}

		public int compare(InventoryItem inventoryItem, InventoryItem inventoryItem2) {
			LuaReturn luaReturn = LuaManager.caller.protectedCall(LuaManager.thread, this.functionObj, inventoryItem, inventoryItem2);
			if (luaReturn.isSuccess() && !luaReturn.isEmpty() && luaReturn.getFirst() instanceof Double) {
				double double1 = (Double)luaReturn.getFirst();
				return Double.compare(double1, 0.0);
			} else {
				return 0;
			}
		}
	}

	private static final class EvalArgPredicate implements Predicate {
		LuaClosure functionObj;
		Object arg;

		ItemContainer.EvalArgPredicate init(LuaClosure luaClosure, Object object) {
			this.functionObj = (LuaClosure)Objects.requireNonNull(luaClosure);
			this.arg = object;
			return this;
		}

		public boolean test(InventoryItem inventoryItem) {
			return LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.functionObj, inventoryItem, this.arg) == Boolean.TRUE;
		}
	}

	private static final class EvalArgComparator implements Comparator {
		LuaClosure functionObj;
		Object arg;

		ItemContainer.EvalArgComparator init(LuaClosure luaClosure, Object object) {
			this.functionObj = (LuaClosure)Objects.requireNonNull(luaClosure);
			this.arg = object;
			return this;
		}

		public int compare(InventoryItem inventoryItem, InventoryItem inventoryItem2) {
			LuaReturn luaReturn = LuaManager.caller.protectedCall(LuaManager.thread, this.functionObj, inventoryItem, inventoryItem2, this.arg);
			if (luaReturn.isSuccess() && !luaReturn.isEmpty() && luaReturn.getFirst() instanceof Double) {
				double double1 = (Double)luaReturn.getFirst();
				return Double.compare(double1, 0.0);
			} else {
				return 0;
			}
		}
	}

	private static final class ConditionComparator implements Comparator {

		public int compare(InventoryItem inventoryItem, InventoryItem inventoryItem2) {
			return inventoryItem.getCondition() - inventoryItem2.getCondition();
		}
	}

	private static final class TagPredicate implements Predicate {
		String tag;

		ItemContainer.TagPredicate init(String string) {
			this.tag = (String)Objects.requireNonNull(string);
			return this;
		}

		public boolean test(InventoryItem inventoryItem) {
			return inventoryItem.hasTag(this.tag);
		}
	}

	private static final class TagEvalPredicate implements Predicate {
		String tag;
		LuaClosure functionObj;

		ItemContainer.TagEvalPredicate init(String string, LuaClosure luaClosure) {
			this.tag = string;
			this.functionObj = (LuaClosure)Objects.requireNonNull(luaClosure);
			return this;
		}

		public boolean test(InventoryItem inventoryItem) {
			return inventoryItem.hasTag(this.tag) && LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.functionObj, (Object)inventoryItem) == Boolean.TRUE;
		}
	}

	private static final class TagEvalArgPredicate implements Predicate {
		String tag;
		LuaClosure functionObj;
		Object arg;

		ItemContainer.TagEvalArgPredicate init(String string, LuaClosure luaClosure, Object object) {
			this.tag = string;
			this.functionObj = (LuaClosure)Objects.requireNonNull(luaClosure);
			this.arg = object;
			return this;
		}

		public boolean test(InventoryItem inventoryItem) {
			return inventoryItem.hasTag(this.tag) && LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.functionObj, inventoryItem, this.arg) == Boolean.TRUE;
		}
	}

	private static final class TypeEvalPredicate implements Predicate {
		String type;
		LuaClosure functionObj;

		ItemContainer.TypeEvalPredicate init(String string, LuaClosure luaClosure) {
			this.type = string;
			this.functionObj = (LuaClosure)Objects.requireNonNull(luaClosure);
			return this;
		}

		public boolean test(InventoryItem inventoryItem) {
			return ItemContainer.compareType(this.type, inventoryItem) && LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.functionObj, (Object)inventoryItem) == Boolean.TRUE;
		}
	}

	private static final class TypeEvalArgPredicate implements Predicate {
		String type;
		LuaClosure functionObj;
		Object arg;

		ItemContainer.TypeEvalArgPredicate init(String string, LuaClosure luaClosure, Object object) {
			this.type = string;
			this.functionObj = (LuaClosure)Objects.requireNonNull(luaClosure);
			this.arg = object;
			return this;
		}

		public boolean test(InventoryItem inventoryItem) {
			return ItemContainer.compareType(this.type, inventoryItem) && LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.functionObj, inventoryItem, this.arg) == Boolean.TRUE;
		}
	}

	private static final class CategoryPredicate implements Predicate {
		String category;

		ItemContainer.CategoryPredicate init(String string) {
			this.category = (String)Objects.requireNonNull(string);
			return this;
		}

		public boolean test(InventoryItem inventoryItem) {
			return inventoryItem.getCategory().equals(this.category);
		}
	}
}
