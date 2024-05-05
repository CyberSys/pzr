package zombie.characters.AttachedItems;

import java.util.ArrayList;
import zombie.inventory.types.HandWeapon;
import zombie.util.StringUtils;
import zombie.util.Type;


public final class AttachedModelNames {
	protected AttachedLocationGroup group;
	protected final ArrayList models = new ArrayList();

	AttachedLocationGroup getGroup() {
		return this.group;
	}

	public void copyFrom(AttachedModelNames attachedModelNames) {
		this.models.clear();
		for (int int1 = 0; int1 < attachedModelNames.models.size(); ++int1) {
			AttachedModelName attachedModelName = (AttachedModelName)attachedModelNames.models.get(int1);
			this.models.add(new AttachedModelName(attachedModelName));
		}
	}

	public void initFrom(AttachedItems attachedItems) {
		this.group = attachedItems.getGroup();
		this.models.clear();
		for (int int1 = 0; int1 < attachedItems.size(); ++int1) {
			AttachedItem attachedItem = attachedItems.get(int1);
			String string = attachedItem.getItem().getStaticModel();
			if (!StringUtils.isNullOrWhitespace(string)) {
				String string2 = this.group.getLocation(attachedItem.getLocation()).getAttachmentName();
				HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(attachedItem.getItem(), HandWeapon.class);
				float float1 = handWeapon == null ? 0.0F : handWeapon.getBloodLevel();
				this.models.add(new AttachedModelName(string2, string, float1));
			}
		}
	}

	public int size() {
		return this.models.size();
	}

	public AttachedModelName get(int int1) {
		return (AttachedModelName)this.models.get(int1);
	}

	public void clear() {
		this.models.clear();
	}
}
