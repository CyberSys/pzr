package zombie.inventory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.inventory.types.AlarmClock;
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
import zombie.iso.SliceY;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;


public class ItemContainer {
	public boolean active = false;
	public boolean dirty = true;
	public boolean IsDevice = false;
	public float ageFactor = 1.0F;
	public float CookingFactor = 1.0F;
	private float customTemperature = 0.0F;
	private boolean hasBeenLooted = false;
	private String openSound = null;
	private String closeSound = null;
	private String putSound = null;
	private String OnlyAcceptCategory = null;
	public int Capacity = 50;
	public InventoryItem containingItem = null;
	public ArrayList Items = new ArrayList();
	public ArrayList IncludingObsoleteItems = new ArrayList();
	public IsoObject parent = null;
	public IsoGridSquare SourceGrid = null;
	public VehiclePart vehiclePart = null;
	private int weightReduction = 0;
	public InventoryContainer inventoryContainer = null;
	public boolean bExplored = false;
	public String type = "none";
	public int ID = 0;
	boolean drawDirty = true;
	static ArrayList tempList = new ArrayList();

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

	public int getEffectiveCapacity(IsoGameCharacter gameCharacter) {
		if (gameCharacter != null && !(this.parent instanceof IsoGameCharacter) && !"floor".equals(this.getType())) {
			if (gameCharacter.HasTrait("Organized")) {
				return (int)((float)this.Capacity * 1.3F);
			}

			if (gameCharacter.HasTrait("Disorganized")) {
				return (int)Math.max((float)this.Capacity * 0.7F, 1.0F);
			}
		}

		return this.Capacity;
	}

	public void setExplored(boolean boolean1) {
		this.bExplored = boolean1;
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

	public boolean isExplored() {
		return this.bExplored;
	}

	public boolean isInCharacterInventory(IsoGameCharacter gameCharacter) {
		if (gameCharacter.getInventory() == this) {
			return true;
		} else {
			if (this.containingItem != null) {
				if (gameCharacter.getInventory().contains(this.containingItem)) {
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

	public ItemContainer(int int1, String string, IsoGridSquare square, IsoObject object, int int2, int int3) {
		this.ID = int1;
		this.parent = object;
		this.type = string;
		this.SourceGrid = square;
		if (string.equals("fridge")) {
			this.ageFactor = 0.02F;
			this.CookingFactor = 0.0F;
		}
	}

	public ItemContainer(String string, IsoGridSquare square, IsoObject object, int int1, int int2) {
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
			for (int int2 = 0; int2 < arrayList.size(); ++int2) {
				if (arrayList.get(int2) != this) {
					int1 += ((ItemContainer)arrayList.get(int2)).getNumberOfItem(string, boolean1);
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

			if (this.SourceGrid != null) {
				this.SourceGrid.clientModify();
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

			if (this.SourceGrid != null) {
				this.SourceGrid.clientModify();
			}
		}
	}

	public boolean contains(InventoryItem inventoryItem, boolean boolean1) {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int1);
			if (inventoryItem2 == null) {
				this.Items.remove(int1);
				--int1;
			} else {
				if (inventoryItem2 == inventoryItem) {
					return true;
				}

				if (boolean1 && this.getItems().get(int1) instanceof InventoryContainer && ((InventoryContainer)this.getItems().get(int1)).getItemContainer() != null && !arrayList.contains((InventoryContainer)this.getItems().get(int1))) {
					arrayList.add((InventoryContainer)this.getItems().get(int1));
				}
			}
		}

		boolean boolean2 = false;
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			boolean2 = ((InventoryContainer)arrayList.get(int2)).getItemContainer().contains(inventoryItem, boolean1);
			if (boolean2) {
				return true;
			}
		}

		return false;
	}

	public boolean contains(String string, boolean boolean1) {
		return this.contains(string, boolean1, false);
	}

	private boolean testBroken(boolean boolean1, InventoryItem inventoryItem) {
		if (!boolean1) {
			return true;
		} else {
			return !inventoryItem.isBroken();
		}
	}

	public boolean contains(String string, boolean boolean1, boolean boolean2) {
		ArrayList arrayList = new ArrayList();
		int int1;
		InventoryItem inventoryItem;
		int int2;
		if (string.contains("Type:")) {
			for (int1 = 0; int1 < this.Items.size(); ++int1) {
				inventoryItem = (InventoryItem)this.Items.get(int1);
				if (string.contains("Food") && inventoryItem instanceof Food) {
					return true;
				}

				if (string.contains("Weapon") && inventoryItem instanceof HandWeapon && this.testBroken(boolean2, inventoryItem)) {
					return true;
				}

				if (string.contains("AlarmClock") && inventoryItem instanceof AlarmClock) {
					return true;
				}
			}
		} else if (string.contains("/")) {
			String[] stringArray = string.split("/");
			for (int2 = 0; int2 < stringArray.length; ++int2) {
				for (int int3 = 0; int3 < this.Items.size(); ++int3) {
					InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int3);
					if (inventoryItem2.type.equals(stringArray[int2].trim()) && this.testBroken(boolean2, inventoryItem2)) {
						return true;
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
					if (inventoryItem.type.equals(string.trim()) && this.testBroken(boolean2, inventoryItem)) {
						return true;
					}

					if (boolean1 && this.getItems().get(int1) instanceof InventoryContainer && ((InventoryContainer)this.getItems().get(int1)).getItemContainer() != null && !arrayList.contains((InventoryContainer)this.getItems().get(int1))) {
						arrayList.add((InventoryContainer)this.getItems().get(int1));
					}
				}
			}
		}

		boolean boolean3 = false;
		for (int2 = 0; int2 < arrayList.size(); ++int2) {
			boolean3 = ((InventoryContainer)arrayList.get(int2)).getItemContainer().contains(string, boolean1, boolean2);
			if (boolean3) {
				return true;
			}
		}

		return false;
	}

	public boolean contains(String string) {
		return this.contains(string, false);
	}

	public InventoryItem getBestCondition(String string) {
		if (string == null) {
			return null;
		} else {
			if (string.contains(".")) {
				string = string.substring(string.indexOf(".") + 1);
			}

			InventoryItem inventoryItem = null;
			int int1 = 0;
			for (int int2 = 0; int2 < this.Items.size(); ++int2) {
				InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int2);
				if (inventoryItem2.type != null && inventoryItem2.type.equals(string) && inventoryItem2.Condition > int1) {
					int1 = inventoryItem2.Condition;
					inventoryItem = inventoryItem2;
				}
			}

			return inventoryItem;
		}
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
		ArrayList arrayList = new ArrayList();
		int int2 = 0;
		while (int2 < int1) {
			InventoryItem inventoryItem = this.FindAndReturn(string, arrayList);
			if (inventoryItem == null) {
				return arrayList;
			}

			++int2;
			arrayList.add(inventoryItem);
		}

		return arrayList;
	}

	public InventoryItem FindAndReturn(String string, ArrayList arrayList) {
		if (string == null) {
			return null;
		} else {
			if (string.contains(".")) {
				string = string.substring(string.indexOf(".") + 1);
			}

			if (string.contains("/")) {
				String[] stringArray = string.split("/");
				for (int int1 = 0; int1 < stringArray.length; ++int1) {
					for (int int2 = 0; int2 < this.Items.size(); ++int2) {
						InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
						if (inventoryItem.type != null && inventoryItem.type.equals(stringArray[int1]) && !arrayList.contains(inventoryItem)) {
							return inventoryItem;
						}
					}
				}
			} else {
				for (int int3 = 0; int3 < this.Items.size(); ++int3) {
					InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int3);
					if (inventoryItem2.type != null && inventoryItem2.type.equals(string) && !arrayList.contains(inventoryItem2)) {
						return inventoryItem2;
					}
				}
			}

			return null;
		}
	}

	public InventoryItem FindAndReturn(String string) {
		if (string == null) {
			return null;
		} else {
			if (string.contains(".")) {
				string = string.substring(string.indexOf(".") + 1);
			}

			if (string.contains("/")) {
				String[] stringArray = string.split("/");
				for (int int1 = 0; int1 < stringArray.length; ++int1) {
					for (int int2 = 0; int2 < this.Items.size(); ++int2) {
						InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
						if (inventoryItem.type != null && inventoryItem.type.equals(stringArray[int1])) {
							return inventoryItem;
						}
					}
				}
			} else {
				for (int int3 = 0; int3 < this.Items.size(); ++int3) {
					InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int3);
					if (inventoryItem2.type != null && inventoryItem2.type.equals(string)) {
						return inventoryItem2;
					}
				}
			}

			return null;
		}
	}

	public ArrayList FindAll(String string) {
		ArrayList arrayList = new ArrayList();
		if (string == null) {
			return arrayList;
		} else {
			if (string.contains(".")) {
				string = string.substring(string.indexOf(".") + 1);
			}

			if (string.contains("/")) {
				String[] stringArray = string.split("/");
				for (int int1 = 0; int1 < stringArray.length; ++int1) {
					for (int int2 = 0; int2 < this.Items.size(); ++int2) {
						InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
						if (inventoryItem.type != null && inventoryItem.type.equals(stringArray[int1]) && !arrayList.contains(inventoryItem)) {
							arrayList.add(inventoryItem);
						}
					}
				}
			} else {
				for (int int3 = 0; int3 < this.Items.size(); ++int3) {
					InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int3);
					if (inventoryItem2.type != null && inventoryItem2.type.equals(string)) {
						arrayList.add(inventoryItem2);
					}
				}
			}

			return arrayList;
		}
	}

	public InventoryItem FindAndReturnStack(String string) {
		if (string.contains(".")) {
			string = string.substring(string.indexOf(".") + 1);
		}

		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem(inventoryItem.module + "." + string);
			if (inventoryItem.type == null) {
				if (string != null) {
					continue;
				}
			} else if (!inventoryItem.type.equals(string)) {
				continue;
			}

			if (inventoryItem.CanStack(inventoryItem2)) {
				return inventoryItem;
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
		this.drawDirty = true;
		if (this.parent != null) {
			this.dirty = true;
		}

		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem2 = (InventoryItem)this.Items.get(int1);
			if (inventoryItem2 == inventoryItem) {
				if (inventoryItem.uses > 1) {
					--inventoryItem.uses;
				} else {
					this.Items.remove(inventoryItem);
				}

				inventoryItem.container = null;
				this.dirty = true;
				if (this.parent instanceof IsoDeadBody) {
					((IsoDeadBody)this.parent).checkClothing();
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
			((IsoDeadBody)this.parent).checkClothing();
		}
	}

	public void Remove(String string) {
		this.drawDirty = true;
		if (this.parent != null) {
			this.dirty = true;
		}

		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.type.equals(string)) {
				if (inventoryItem.uses > 1) {
					--inventoryItem.uses;
				} else {
					this.Items.remove(inventoryItem);
				}

				inventoryItem.container = null;
				this.dirty = true;
				return;
			}
		}
	}

	public InventoryItem Remove(ItemType itemType) {
		this.drawDirty = true;
		if (this.parent != null) {
			this.dirty = true;
		}

		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.cat == itemType) {
				this.Items.remove(inventoryItem);
				this.dirty = true;
				inventoryItem.container = null;
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
		int int1;
		for (int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.type.equals(string)) {
				inventoryItem.container = null;
				arrayList.add(inventoryItem);
				this.dirty = true;
			}
		}

		for (int1 = 0; int1 < arrayList.size(); ++int1) {
			this.Items.remove(arrayList.get(int1));
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
		if (this.parent instanceof IsoPlayer && ((IsoPlayer)this.parent).GhostMode) {
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
		if (!(this.parent instanceof IsoPlayer) || (!Core.bDebug || !((IsoPlayer)this.parent).GhostMode) && (((IsoPlayer)this.parent).getAccessLevel().equals("None") || !((IsoPlayer)this.parent).isUnlimitedCarry())) {
			return this.parent instanceof IsoGameCharacter ? ((IsoGameCharacter)this.parent).getInventoryWeight() : this.getContentsWeight();
		} else {
			return 0.0F;
		}
	}

	public boolean isEmpty() {
		return this.Items.isEmpty();
	}

	public boolean isPowered() {
		boolean boolean1 = false;
		if (GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getElecShutModifier()) {
			boolean1 = true;
		}

		if (this.parent != null && this.parent.getSquare() != null) {
			IsoGridSquare square = this.parent.getSquare();
			if (square.haveElectricity()) {
				boolean1 = true;
			} else if (square.getRoom() == null && boolean1) {
				boolean1 = false;
				if (square.nav[IsoDirections.N.index()] != null && square.nav[IsoDirections.N.index()].getRoom() != null || square.nav[IsoDirections.S.index()] != null && square.nav[IsoDirections.S.index()].getRoom() != null || square.nav[IsoDirections.W.index()] != null && square.nav[IsoDirections.W.index()].getRoom() != null || square.nav[IsoDirections.E.index()] != null && square.nav[IsoDirections.E.index()].getRoom() != null) {
					boolean1 = true;
				}

				if (!boolean1 && this.parent.getSprite() != null && this.parent.getSprite().getSpriteGrid() != null) {
					IsoSpriteGrid spriteGrid = this.parent.getSprite().getSpriteGrid();
					int int1 = spriteGrid.getSpriteGridPosX(this.parent.getSprite());
					int int2 = spriteGrid.getSpriteGridPosY(this.parent.getSprite());
					int int3 = (int)this.parent.getX();
					int int4 = (int)this.parent.getY();
					int int5 = (int)this.parent.getZ();
					IsoGridSquare square2 = null;
					for (int int6 = int4 - int2; int6 < int4 - int2 + spriteGrid.getHeight(); ++int6) {
						for (int int7 = int3 - int1; int7 < int3 - int1 + spriteGrid.getWidth(); ++int7) {
							square2 = IsoWorld.instance.getCell().getGridSquare(int7, int6, int5);
							if (square2 != null) {
								if (square2.haveElectricity()) {
									boolean1 = true;
									break;
								}

								if (square2.getRoom() == null && (square2.nav[IsoDirections.N.index()] != null && square2.nav[IsoDirections.N.index()].getRoom() != null || square2.nav[IsoDirections.S.index()] != null && square2.nav[IsoDirections.S.index()].getRoom() != null || square2.nav[IsoDirections.W.index()] != null && square2.nav[IsoDirections.W.index()].getRoom() != null || square2.nav[IsoDirections.E.index()] != null && square2.nav[IsoDirections.E.index()].getRoom() != null)) {
									boolean1 = true;
									break;
								}
							}
						}
					}
				}
			}
		} else {
			boolean1 = false;
		}

		return boolean1;
	}

	public float getTemprature() {
		if (this.customTemperature != 0.0F) {
			return this.customTemperature;
		} else {
			if (this.isPowered()) {
				if (this.type.equals("fridge") || this.type.equals("freezer")) {
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
			} else if ((this.type.equals("fridge") || this.type.equals("freezer")) && GameTime.instance.NightsSurvived == SandboxOptions.instance.getElecShutModifier() && GameTime.instance.getTimeOfDay() < 13.0F) {
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

	public ArrayList save(ByteBuffer byteBuffer, boolean boolean1, IsoGameCharacter gameCharacter) throws IOException {
		GameWindow.WriteString(byteBuffer, this.type);
		byteBuffer.put((byte)(this.bExplored ? 1 : 0));
		ArrayList arrayList = CompressIdenticalItems.save(byteBuffer, this.Items, (IsoGameCharacter)null);
		byteBuffer.put((byte)(this.isHasBeenLooted() ? 1 : 0));
		byteBuffer.putInt(this.Capacity);
		return arrayList;
	}

	public ArrayList save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		return this.save(byteBuffer, boolean1, (IsoGameCharacter)null);
	}

	public ArrayList load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		boolean1 = false;
		this.type = GameWindow.ReadString(byteBuffer);
		this.bExplored = byteBuffer.get() == 1;
		ArrayList arrayList = CompressIdenticalItems.load(byteBuffer, int1, this.Items, this.IncludingObsoleteItems);
		for (int int2 = 0; int2 < this.Items.size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
			inventoryItem.container = this;
		}

		if (int1 >= 37) {
			this.setHasBeenLooted(byteBuffer.get() == 1);
		}

		if (int1 >= 84) {
			this.Capacity = byteBuffer.getInt();
		}

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
				float float2 = ((HandWeapon)inventoryItem2).getScore(survivorDesc);
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
				float float2 = ((HandWeapon)inventoryItem2).getScore((SurvivorDesc)null);
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
				if (((Food)inventoryItem2).isbDangerousUncooked() && !((Food)inventoryItem2).isCooked()) {
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
		Object object = null;
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.CanBandage) {
				return inventoryItem;
			}
		}

		return (InventoryItem)object;
	}

	public int getNumItems(String string) {
		int int1 = 0;
		int int2;
		if (string.contains("Type:")) {
			for (int2 = 0; int2 < this.Items.size(); ++int2) {
				if (this.Items.get(int2) instanceof Food && string.contains("Food")) {
					int1 += ((InventoryItem)this.Items.get(int2)).uses;
				}

				if (this.Items.get(int2) instanceof HandWeapon && string.contains("Weapon")) {
					int1 += ((InventoryItem)this.Items.get(int2)).uses;
				}
			}
		} else {
			for (int2 = 0; int2 < this.Items.size(); ++int2) {
				if (((InventoryItem)this.Items.get(int2)).type.equals(string)) {
					int1 += ((InventoryItem)this.Items.get(int2)).uses;
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
		return this.getItemCount(string, false);
	}

	public int getItemCount(String string, boolean boolean1) {
		int int1 = 0;
		for (int int2 = 0; int2 < this.Items.size(); ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int2);
			if (string.equals(inventoryItem.getModule() + "." + inventoryItem.getType())) {
				++int1;
			} else if (inventoryItem instanceof InventoryContainer && boolean1) {
				int1 += ((InventoryContainer)inventoryItem).getInventory().getItemCount(string);
			}
		}

		return int1;
	}

	public void setWeightReduction(int int1) {
		int1 = Math.min(int1, 100);
		int1 = Math.max(int1, 0);
		this.weightReduction = int1;
	}

	public int getWeightReduction() {
		return this.weightReduction;
	}

	public boolean doLoad() {
		return true;
	}

	public boolean doLoadActual() throws FileNotFoundException, IOException {
		if (SliceY.SliceBuffer2 == null) {
			SliceY.SliceBuffer2 = ByteBuffer.allocate(10000000);
		}

		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_con_" + this.ID + ".bin");
		if (!file.exists()) {
			return false;
		} else {
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			SliceY.SliceBuffer2.rewind();
			byte[] byteArray = SliceY.SliceBuffer2.array();
			bufferedInputStream.read(SliceY.SliceBuffer2.array());
			SliceY.SliceBuffer2.rewind();
			this.load(SliceY.SliceBuffer2, 666, false);
			return true;
		}
	}

	public void removeAllItems() {
		this.drawDirty = true;
		if (this.parent != null) {
			this.dirty = true;
		}

		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			((InventoryItem)this.Items.get(int1)).container = null;
		}

		this.Items.clear();
		if (this.parent instanceof IsoDeadBody) {
			((IsoDeadBody)this.parent).checkClothing();
		}
	}

	public void setCustomTemperature(float float1) {
		this.customTemperature = float1;
	}

	public float getCustomTemperature() {
		return this.customTemperature;
	}

	public InventoryItem getItemFromType(String string, IsoGameCharacter gameCharacter, boolean boolean1, boolean boolean2, boolean boolean3) {
		ArrayList arrayList = new ArrayList();
		if (string.contains(".")) {
			string = string.split("\\.")[1];
		}

		int int1;
		InventoryItem inventoryItem;
		for (int1 = 0; int1 < this.getItems().size(); ++int1) {
			inventoryItem = (InventoryItem)this.getItems().get(int1);
			if (!inventoryItem.getFullType().equals(string) && !inventoryItem.getType().equals(string)) {
				if (boolean3 && this.getItems().get(int1) instanceof InventoryContainer && ((InventoryContainer)this.getItems().get(int1)).getItemContainer() != null && !arrayList.contains((InventoryContainer)this.getItems().get(int1))) {
					arrayList.add((InventoryContainer)this.getItems().get(int1));
				}
			} else if ((!boolean1 || gameCharacter == null || inventoryItem != gameCharacter.getClothingItem_Back() && inventoryItem != gameCharacter.getClothingItem_Feet() && inventoryItem != gameCharacter.getClothingItem_Hands() && inventoryItem != gameCharacter.getClothingItem_Head() && inventoryItem != gameCharacter.getClothingItem_Legs() && inventoryItem != gameCharacter.getClothingItem_Torso()) && this.testBroken(boolean2, inventoryItem)) {
				return inventoryItem;
			}
		}

		for (int1 = 0; int1 < arrayList.size(); ++int1) {
			inventoryItem = ((InventoryContainer)arrayList.get(int1)).getItemContainer().getItemFromType(string, gameCharacter, boolean1, boolean2, boolean3);
			if (inventoryItem != null) {
				return inventoryItem;
			}
		}

		return null;
	}

	public InventoryItem getItemFromType(String string, boolean boolean1, boolean boolean2) {
		return this.getItemFromType(string, (IsoGameCharacter)null, false, boolean1, boolean2);
	}

	public InventoryItem getItemFromType(String string) {
		return this.getItemFromType(string, (IsoGameCharacter)null, false, false, false);
	}

	public ArrayList getItemsFromType(String string) {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < this.getItems().size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.getItems().get(int1);
			if (inventoryItem.getFullType().equals(string) || inventoryItem.getType().equals(string)) {
				arrayList.add(inventoryItem);
			}
		}

		return arrayList;
	}

	public ArrayList getItemsFromFullType(String string) {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < this.getItems().size(); ++int1) {
			if (((InventoryItem)this.getItems().get(int1)).getFullType().equals(string)) {
				arrayList.add(this.getItems().get(int1));
			}
		}

		return arrayList;
	}

	public ArrayList getItemsFromFullType(String string, boolean boolean1) {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < this.getItems().size(); ++int1) {
			if (((InventoryItem)this.getItems().get(int1)).getFullType().equals(string)) {
				arrayList.add(this.getItems().get(int1));
			}

			if (boolean1 && this.getItems().get(int1) instanceof InventoryContainer && ((InventoryContainer)this.getItems().get(int1)).getItemContainer() != null) {
				arrayList.addAll(((InventoryContainer)this.getItems().get(int1)).getItemContainer().getItemsFromFullType(string, true));
			}
		}

		return arrayList;
	}

	public ArrayList getItemsFromType(String string, boolean boolean1) {
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = new ArrayList();
		int int1;
		for (int1 = 0; int1 < this.getItems().size(); ++int1) {
			if (((InventoryItem)this.getItems().get(int1)).getType().equals(string)) {
				arrayList.add(this.getItems().get(int1));
			}

			if (boolean1 && this.getItems().get(int1) instanceof InventoryContainer && ((InventoryContainer)this.getItems().get(int1)).getItemContainer() != null && !arrayList2.contains((InventoryContainer)this.getItems().get(int1))) {
				arrayList2.add((InventoryContainer)this.getItems().get(int1));
			}
		}

		for (int1 = 0; int1 < arrayList2.size(); ++int1) {
			arrayList.addAll(((InventoryContainer)arrayList2.get(int1)).getItemContainer().getItemsFromType(string, true));
		}

		return arrayList;
	}

	public ArrayList getItemsFromCategory(String string) {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < this.getItems().size(); ++int1) {
			if (((InventoryItem)this.getItems().get(int1)).getCategory().equals(string)) {
				arrayList.add(this.getItems().get(int1));
			}
		}

		return arrayList;
	}

	public void sendContentsToRemoteContainer() {
		if (GameClient.bClient) {
			GameClient gameClient = GameClient.instance;
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
			GameClient gameClient = GameClient.instance;
			UdpConnection udpConnection = GameClient.connection;
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)44, byteBufferWriter);
			byteBufferWriter.putShort((short)IsoPlayer.instance.OnlineID);
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
				byteBufferWriter.putLong(((IsoWorldInventoryObject)this.parent).getItem().id);
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

	public InventoryItem getItemWithIDRecursiv(long long1) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.id == long1) {
				return inventoryItem;
			}

			if (inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getItemContainer() != null && !((InventoryContainer)inventoryItem).getItemContainer().getItems().isEmpty()) {
				inventoryItem = ((InventoryContainer)inventoryItem).getItemContainer().getItemWithIDRecursiv(long1);
				if (inventoryItem != null) {
					return inventoryItem;
				}
			}
		}

		return null;
	}

	public InventoryItem getItemWithID(long long1) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			if (((InventoryItem)this.Items.get(int1)).id == long1) {
				return (InventoryItem)this.Items.get(int1);
			}
		}

		return null;
	}

	public boolean removeItemWithID(long long1) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			if (((InventoryItem)this.Items.get(int1)).id == long1) {
				this.Remove((InventoryItem)this.Items.get(int1));
				return true;
			}
		}

		return false;
	}

	public boolean containsID(long long1) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			if (((InventoryItem)this.Items.get(int1)).id == long1) {
				return true;
			}
		}

		return false;
	}

	public boolean removeItemWithIDRecurse(long long1) {
		for (int int1 = 0; int1 < this.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.Items.get(int1);
			if (inventoryItem.id == long1) {
				this.Remove((InventoryItem)this.Items.get(int1));
				return true;
			}

			if (inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getInventory().removeItemWithIDRecurse(long1)) {
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
		this.OnlyAcceptCategory = string;
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
			if (inventoryItem.getID() == long1) {
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

	public static float floatingPointCorrection(float float1) {
		byte byte1 = 100;
		float float2 = float1 * (float)byte1;
		return (float)((int)(float2 - (float)((int)float2) >= 0.5F ? float2 + 1.0F : float2)) / (float)byte1;
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
}
