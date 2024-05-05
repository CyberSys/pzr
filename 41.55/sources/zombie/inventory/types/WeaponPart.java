package zombie.inventory.types;

import java.util.ArrayList;
import zombie.core.Translator;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;


public final class WeaponPart extends InventoryItem {
	public static final String TYPE_CANON = "Canon";
	public static final String TYPE_CLIP = "Clip";
	public static final String TYPE_RECOILPAD = "RecoilPad";
	public static final String TYPE_SCOPE = "Scope";
	public static final String TYPE_SLING = "Sling";
	public static final String TYPE_STOCK = "Stock";
	private float maxRange = 0.0F;
	private float minRangeRanged = 0.0F;
	private float damage = 0.0F;
	private float recoilDelay = 0.0F;
	private int clipSize = 0;
	private int reloadTime = 0;
	private int aimingTime = 0;
	private int hitChance = 0;
	private float angle = 0.0F;
	private float weightModifier = 0.0F;
	private final ArrayList mountOn = new ArrayList();
	private final ArrayList mountOnDisplayName = new ArrayList();
	private String partType = null;

	public WeaponPart(String string, String string2, String string3, String string4) {
		super(string, string2, string3, string4);
		this.cat = ItemType.Weapon;
	}

	public int getSaveType() {
		return Item.Type.WeaponPart.ordinal();
	}

	public String getCategory() {
		return this.mainCategory != null ? this.mainCategory : "WeaponPart";
	}

	public void DoTooltip(ObjectTooltip objectTooltip, ObjectTooltip.Layout layout) {
		ObjectTooltip.LayoutItem layoutItem = layout.addItem();
		layoutItem.setLabel(Translator.getText("Tooltip_weapon_Type") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
		layoutItem.setValue(Translator.getText("Tooltip_weapon_" + this.partType), 1.0F, 1.0F, 0.8F, 1.0F);
		layoutItem = layout.addItem();
		String string = Translator.getText("Tooltip_weapon_CanBeMountOn");
		String string2 = string + this.mountOnDisplayName.toString().replaceAll("\\[", "").replaceAll("\\]", "");
		layoutItem.setLabel(string2, 1.0F, 1.0F, 0.8F, 1.0F);
	}

	public float getMinRangeRanged() {
		return this.minRangeRanged;
	}

	public void setMinRangeRanged(float float1) {
		this.minRangeRanged = float1;
	}

	public float getMaxRange() {
		return this.maxRange;
	}

	public void setMaxRange(float float1) {
		this.maxRange = float1;
	}

	public float getRecoilDelay() {
		return this.recoilDelay;
	}

	public void setRecoilDelay(float float1) {
		this.recoilDelay = float1;
	}

	public int getClipSize() {
		return this.clipSize;
	}

	public void setClipSize(int int1) {
		this.clipSize = int1;
	}

	public float getDamage() {
		return this.damage;
	}

	public void setDamage(float float1) {
		this.damage = float1;
	}

	public ArrayList getMountOn() {
		return this.mountOn;
	}

	public void setMountOn(ArrayList arrayList) {
		this.mountOn.clear();
		this.mountOnDisplayName.clear();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			String string = (String)arrayList.get(int1);
			if (!string.contains(".")) {
				String string2 = this.getModule();
				string = string2 + "." + string;
			}

			Item item = ScriptManager.instance.getItem(string);
			if (item != null) {
				this.mountOn.add(item.getFullName());
				this.mountOnDisplayName.add(item.getDisplayName());
			}
		}
	}

	public String getPartType() {
		return this.partType;
	}

	public void setPartType(String string) {
		this.partType = string;
	}

	public int getReloadTime() {
		return this.reloadTime;
	}

	public void setReloadTime(int int1) {
		this.reloadTime = int1;
	}

	public int getAimingTime() {
		return this.aimingTime;
	}

	public void setAimingTime(int int1) {
		this.aimingTime = int1;
	}

	public int getHitChance() {
		return this.hitChance;
	}

	public void setHitChance(int int1) {
		this.hitChance = int1;
	}

	public float getAngle() {
		return this.angle;
	}

	public void setAngle(float float1) {
		this.angle = float1;
	}

	public float getWeightModifier() {
		return this.weightModifier;
	}

	public void setWeightModifier(float float1) {
		this.weightModifier = float1;
	}
}
