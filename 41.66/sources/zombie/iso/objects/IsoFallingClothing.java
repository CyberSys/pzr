package zombie.iso.objects;

import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.model.WorldItemModelDrawer;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoPhysicsObject;


public class IsoFallingClothing extends IsoPhysicsObject {
	private InventoryItem clothing = null;
	private int dropTimer = 0;
	public boolean addWorldItem = true;

	public String getObjectName() {
		return "FallingClothing";
	}

	public IsoFallingClothing(IsoCell cell) {
		super(cell);
	}

	public IsoFallingClothing(IsoCell cell, float float1, float float2, float float3, float float4, float float5, InventoryItem inventoryItem) {
		super(cell);
		this.clothing = inventoryItem;
		this.dropTimer = 60;
		this.velX = float4;
		this.velY = float5;
		float float6 = (float)Rand.Next(4000) / 10000.0F;
		float float7 = (float)Rand.Next(4000) / 10000.0F;
		float6 -= 0.2F;
		float7 -= 0.2F;
		this.velX += float6;
		this.velY += float7;
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.nx = float1;
		this.ny = float2;
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		this.terminalVelocity = -0.02F;
		Texture texture = this.sprite.LoadFrameExplicit(inventoryItem.getTex().getName());
		if (texture != null) {
			this.sprite.Animate = false;
			int int1 = Core.TileScale;
			this.sprite.def.scaleAspect((float)texture.getWidthOrig(), (float)texture.getHeightOrig(), (float)(16 * int1), (float)(16 * int1));
		}

		this.speedMod = 4.5F;
	}

	public void collideGround() {
		this.drop();
	}

	public void collideWall() {
		this.drop();
	}

	public void update() {
		super.update();
		--this.dropTimer;
		if (this.dropTimer <= 0) {
			this.drop();
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		float float4 = (float)(60 - this.dropTimer) / 60.0F * 360.0F;
		if (!WorldItemModelDrawer.renderMain(this.clothing, this.getCurrentSquare(), this.getX(), this.getY(), this.getZ(), float4)) {
			super.render(float1, float2, float3, colorInfo, boolean1, boolean2, shader);
		}
	}

	void drop() {
		IsoGridSquare square = this.getCurrentSquare();
		if (square != null && this.clothing != null) {
			if (this.addWorldItem) {
				float float1 = square.getApparentZ(this.getX() % 1.0F, this.getY() % 1.0F);
				square.AddWorldInventoryItem(this.clothing, this.getX() % 1.0F, this.getY() % 1.0F, float1 - (float)square.getZ());
			}

			this.clothing = null;
			this.setDestroyed(true);
			square.getMovingObjects().remove(this);
			this.getCell().Remove(this);
			LuaEventManager.triggerEvent("OnContainerUpdate", square);
		}
	}

	void Trigger() {
	}
}
