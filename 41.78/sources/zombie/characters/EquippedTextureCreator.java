package zombie.characters;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import zombie.characterTextures.ItemSmartTexture;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.textures.SmartTexture;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.popman.ObjectPool;


public final class EquippedTextureCreator extends TextureDraw.GenericDrawer {
	private boolean bRendered;
	private ModelInstance modelInstance;
	private float bloodLevel;
	private final ArrayList texturesNotReady = new ArrayList();
	private static final ObjectPool pool = new ObjectPool(EquippedTextureCreator::new);

	public void init(ModelInstance modelInstance, InventoryItem inventoryItem) {
		float float1 = 0.0F;
		if (inventoryItem instanceof HandWeapon) {
			float1 = ((HandWeapon)inventoryItem).getBloodLevel();
		}

		this.init(modelInstance, float1);
	}

	public void init(ModelInstance modelInstance, float float1) {
		this.bRendered = false;
		this.texturesNotReady.clear();
		this.modelInstance = modelInstance;
		this.bloodLevel = float1;
		if (this.modelInstance != null) {
			++this.modelInstance.renderRefCount;
			Texture texture = this.modelInstance.tex;
			if (texture instanceof SmartTexture) {
				texture = null;
			}

			if (texture != null && !texture.isReady()) {
				this.texturesNotReady.add(texture);
			}

			texture = Texture.getSharedTexture("media/textures/BloodTextures/BloodOverlayWeapon.png");
			if (texture != null && !texture.isReady()) {
				this.texturesNotReady.add(texture);
			}

			texture = Texture.getSharedTexture("media/textures/BloodTextures/BloodOverlayWeaponMask.png");
			if (texture != null && !texture.isReady()) {
				this.texturesNotReady.add(texture);
			}
		}
	}

	public void render() {
		for (int int1 = 0; int1 < this.texturesNotReady.size(); ++int1) {
			Texture texture = (Texture)this.texturesNotReady.get(int1);
			if (!texture.isReady()) {
				return;
			}
		}

		GL11.glPushAttrib(2048);
		try {
			this.updateTexture(this.modelInstance, this.bloodLevel);
		} finally {
			GL11.glPopAttrib();
		}

		this.bRendered = true;
	}

	private void updateTexture(ModelInstance modelInstance, float float1) {
		if (modelInstance != null) {
			ItemSmartTexture itemSmartTexture = null;
			if (float1 > 0.0F) {
				if (modelInstance.tex instanceof ItemSmartTexture) {
					itemSmartTexture = (ItemSmartTexture)modelInstance.tex;
				} else if (modelInstance.tex != null) {
					itemSmartTexture = new ItemSmartTexture(modelInstance.tex.getName());
				}
			} else if (modelInstance.tex instanceof ItemSmartTexture) {
				itemSmartTexture = (ItemSmartTexture)modelInstance.tex;
			}

			if (itemSmartTexture != null) {
				itemSmartTexture.setBlood("media/textures/BloodTextures/BloodOverlayWeapon.png", "media/textures/BloodTextures/BloodOverlayWeaponMask.png", float1, 300);
				itemSmartTexture.calculate();
				modelInstance.tex = itemSmartTexture;
			}
		}
	}

	public void postRender() {
		ModelManager.instance.derefModelInstance(this.modelInstance);
		this.texturesNotReady.clear();
		if (!this.bRendered) {
		}

		pool.release((Object)this);
	}

	public boolean isRendered() {
		return this.bRendered;
	}

	public static EquippedTextureCreator alloc() {
		return (EquippedTextureCreator)pool.alloc();
	}
}
