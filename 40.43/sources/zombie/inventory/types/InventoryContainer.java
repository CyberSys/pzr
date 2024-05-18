package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Translator;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;


public class InventoryContainer extends InventoryItem {
	ItemContainer container = new ItemContainer();
	int capacity = 0;
	int weightReduction = 0;
	private String CanBeEquipped = "";

	public InventoryContainer(String string, String string2, String string3, String string4) {
		super(string, string2, string3, string4);
		this.container.containingItem = this;
		this.container.type = string3;
		this.container.inventoryContainer = this;
	}

	public int getSaveType() {
		return Item.Type.Container.ordinal();
	}

	public String getCategory() {
		return this.mainCategory != null ? this.mainCategory : "Container";
	}

	public ItemContainer getInventory() {
		return this.container;
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.putInt(this.container.ID);
		byteBuffer.putInt(this.weightReduction);
		this.container.save(byteBuffer, boolean1);
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		int int2 = byteBuffer.getInt();
		this.setWeightReduction(byteBuffer.getInt());
		if (this.container == null) {
			this.container = new ItemContainer();
		}

		this.container.clear();
		this.container.containingItem = this;
		this.container.setWeightReduction(this.weightReduction);
		this.container.Capacity = this.capacity;
		this.container.ID = int2;
		this.container.load(byteBuffer, int1, boolean1);
		String string = null;
		if (int1 < 46) {
			if (byteBuffer.get() == 1) {
				string = GameWindow.ReadString(byteBuffer);
			}

			if ("back".equals(string) && IsoPlayer.instance != null) {
				IsoPlayer.instance.setClothingItem_Back(this);
			}

			if ("secondary".equals(string) && IsoPlayer.instance != null) {
				IsoPlayer.instance.setSecondaryHandItem(this);
			}

			if ("primary".equals(string) && IsoPlayer.instance != null) {
				IsoPlayer.instance.setPrimaryHandItem(this);
			}
		}
	}

	public void setCapacity(int int1) {
		this.capacity = int1;
		if (this.container == null) {
			this.container = new ItemContainer();
		}

		this.container.Capacity = int1;
	}

	public int getCapacity() {
		return this.container.getCapacity();
	}

	public int getEffectiveCapacity(IsoGameCharacter gameCharacter) {
		return this.container.getEffectiveCapacity(gameCharacter);
	}

	public void setWeightReduction(int int1) {
		int1 = Math.min(int1, 100);
		int1 = Math.max(int1, 0);
		this.weightReduction = int1;
		this.container.setWeightReduction(int1);
	}

	public int getWeightReduction() {
		return this.weightReduction;
	}

	public void DoTooltip(ObjectTooltip objectTooltip) {
		objectTooltip.render();
		super.DoTooltip(objectTooltip);
		int int1 = objectTooltip.getHeight().intValue();
		int1 -= objectTooltip.padBottom;
		if (objectTooltip.getWidth() < 160.0) {
			objectTooltip.setWidth(160.0);
		}

		if (!this.getItemContainer().getItems().isEmpty()) {
			int int2 = 5;
			int1 += 4;
			HashSet hashSet = new HashSet();
			for (int int3 = this.getItemContainer().getItems().size() - 1; int3 >= 0; --int3) {
				InventoryItem inventoryItem = (InventoryItem)this.getItemContainer().getItems().get(int3);
				if (inventoryItem.getName() != null) {
					if (hashSet.contains(inventoryItem.getName())) {
						continue;
					}

					hashSet.add(inventoryItem.getName());
				}

				objectTooltip.DrawTextureScaledAspect(inventoryItem.getTex(), (double)int2, (double)int1, 16.0, 16.0, 1.0, 1.0, 1.0, 1.0);
				int2 += 17;
				if ((float)(int2 + 16) > objectTooltip.width - (float)objectTooltip.padRight) {
					break;
				}
			}

			int1 += 16;
		}

		int1 += objectTooltip.padBottom;
		objectTooltip.setHeight((double)int1);
	}

	public void DoTooltip(ObjectTooltip objectTooltip, ObjectTooltip.Layout layout) {
		ObjectTooltip.LayoutItem layoutItem;
		if (this.getEffectiveCapacity(objectTooltip.getCharacter()) != 0) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_container_Capacity") + ":", 1.0F, 1.0F, 1.0F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getEffectiveCapacity(objectTooltip.getCharacter()));
		}

		if (this.getWeightReduction() != 0) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_container_Weight_Reduction") + ":", 1.0F, 1.0F, 1.0F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getWeightReduction());
		}
	}

	public void setCanBeEquipped(String string) {
		this.CanBeEquipped = string;
	}

	public String canBeEquipped() {
		return this.CanBeEquipped;
	}

	public ItemContainer getItemContainer() {
		return this.container;
	}

	public float getContentsWeight() {
		return this.getInventory().getContentsWeight();
	}

	public float getEquippedWeight() {
		float float1 = 1.0F;
		if (this.getWeightReduction() > 0) {
			float1 = 1.0F - (float)this.getWeightReduction() / 100.0F;
		}

		return this.getActualWeight() * 0.3F + this.getContentsWeight() * float1;
	}
}
