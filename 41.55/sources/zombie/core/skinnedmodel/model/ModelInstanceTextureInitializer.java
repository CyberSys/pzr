package zombie.core.skinnedmodel.model;

import zombie.characters.EquippedTextureCreator;
import zombie.core.SpriteRenderer;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.popman.ObjectPool;
import zombie.util.Type;


public final class ModelInstanceTextureInitializer {
	private boolean m_bRendered;
	private ModelInstance m_modelInstance;
	private InventoryItem m_item;
	private float m_bloodLevel;
	private int m_changeNumberMain = 0;
	private int m_changeNumberThread = 0;
	private final ModelInstanceTextureInitializer.RenderData[] m_renderData = new ModelInstanceTextureInitializer.RenderData[3];
	private static final ObjectPool pool = new ObjectPool(ModelInstanceTextureInitializer::new);

	public void init(ModelInstance modelInstance, InventoryItem inventoryItem) {
		this.m_item = inventoryItem;
		this.m_modelInstance = modelInstance;
		HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(inventoryItem, HandWeapon.class);
		this.m_bloodLevel = handWeapon == null ? 0.0F : handWeapon.getBloodLevel();
		this.setDirty();
	}

	public void init(ModelInstance modelInstance, float float1) {
		this.m_item = null;
		this.m_modelInstance = modelInstance;
		this.m_bloodLevel = float1;
		this.setDirty();
	}

	public void setDirty() {
		++this.m_changeNumberMain;
		this.m_bRendered = false;
	}

	public boolean isDirty() {
		return !this.m_bRendered;
	}

	public void renderMain() {
		if (!this.m_bRendered) {
			int int1 = SpriteRenderer.instance.getMainStateIndex();
			if (this.m_renderData[int1] == null) {
				this.m_renderData[int1] = new ModelInstanceTextureInitializer.RenderData();
			}

			ModelInstanceTextureInitializer.RenderData renderData = this.m_renderData[int1];
			if (renderData.m_textureCreator == null) {
				renderData.m_changeNumber = this.m_changeNumberMain;
				renderData.m_textureCreator = EquippedTextureCreator.alloc();
				if (this.m_item == null) {
					renderData.m_textureCreator.init(this.m_modelInstance, this.m_bloodLevel);
				} else {
					renderData.m_textureCreator.init(this.m_modelInstance, this.m_item);
				}

				renderData.m_bRendered = false;
			}
		}
	}

	public void render() {
		int int1 = SpriteRenderer.instance.getRenderStateIndex();
		ModelInstanceTextureInitializer.RenderData renderData = this.m_renderData[int1];
		if (renderData != null) {
			if (renderData.m_textureCreator != null) {
				if (!renderData.m_bRendered) {
					if (renderData.m_changeNumber == this.m_changeNumberThread) {
						renderData.m_bRendered = true;
					} else {
						renderData.m_textureCreator.render();
						if (renderData.m_textureCreator.isRendered()) {
							this.m_changeNumberThread = renderData.m_changeNumber;
							renderData.m_bRendered = true;
						}
					}
				}
			}
		}
	}

	public void postRender() {
		int int1 = SpriteRenderer.instance.getMainStateIndex();
		ModelInstanceTextureInitializer.RenderData renderData = this.m_renderData[int1];
		if (renderData != null) {
			if (renderData.m_textureCreator != null) {
				if (renderData.m_textureCreator.isRendered() && renderData.m_changeNumber == this.m_changeNumberMain) {
					this.m_bRendered = true;
				}

				if (renderData.m_bRendered) {
					renderData.m_textureCreator.postRender();
					renderData.m_textureCreator = null;
				}
			}
		}
	}

	public boolean isRendered() {
		int int1 = SpriteRenderer.instance.getRenderStateIndex();
		ModelInstanceTextureInitializer.RenderData renderData = this.m_renderData[int1];
		if (renderData == null) {
			return true;
		} else {
			return renderData.m_textureCreator == null ? true : renderData.m_bRendered;
		}
	}

	public static ModelInstanceTextureInitializer alloc() {
		return (ModelInstanceTextureInitializer)pool.alloc();
	}

	public void release() {
		pool.release((Object)this);
	}

	private static final class RenderData {
		int m_changeNumber = 0;
		boolean m_bRendered;
		EquippedTextureCreator m_textureCreator;
	}
}
