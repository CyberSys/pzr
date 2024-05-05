package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.model.WorldItemModelDrawer;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemSoundManager;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.InventoryContainer;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.PlayerCamera;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;
import zombie.util.Type;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;


public class IsoWorldInventoryObject extends IsoObject {
	public InventoryItem item;
	public float xoff;
	public float yoff;
	public float zoff;
	public boolean removeProcess = false;
	public double dropTime = -1.0;
	public boolean ignoreRemoveSandbox = false;

	public IsoWorldInventoryObject(InventoryItem inventoryItem, IsoGridSquare square, float float1, float float2, float float3) {
		this.OutlineOnMouseover = true;
		if (inventoryItem.worldZRotation < 0) {
			inventoryItem.worldZRotation = Rand.Next(0, 360);
		}

		inventoryItem.setContainer((ItemContainer)null);
		this.xoff = float1;
		this.yoff = float2;
		this.zoff = float3;
		if (this.xoff == 0.0F) {
			this.xoff = (float)Rand.Next(1000) / 1000.0F;
		}

		if (this.yoff == 0.0F) {
			this.yoff = (float)Rand.Next(1000) / 1000.0F;
		}

		this.item = inventoryItem;
		this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
		this.updateSprite();
		this.square = square;
		this.offsetY = 0.0F;
		this.offsetX = 0.0F;
		this.dropTime = GameTime.getInstance().getWorldAgeHours();
	}

	public IsoWorldInventoryObject(IsoCell cell) {
		super(cell);
		this.offsetY = 0.0F;
		this.offsetX = 0.0F;
	}

	public void swapItem(InventoryItem inventoryItem) {
		if (inventoryItem != null) {
			if (this.getItem() != null) {
				IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this.getItem());
				ItemSoundManager.removeItem(this.getItem());
				this.getItem().setWorldItem((IsoWorldInventoryObject)null);
				inventoryItem.setID(this.getItem().getID());
				inventoryItem.worldScale = this.getItem().worldScale;
				inventoryItem.worldZRotation = this.getItem().worldZRotation;
			}

			this.item = inventoryItem;
			if (inventoryItem.getWorldItem() != null) {
				throw new IllegalArgumentException("newItem.getWorldItem() != null");
			} else {
				this.getItem().setWorldItem(this);
				this.setKeyId(this.getItem().getKeyId());
				this.setName(this.getItem().getName());
				if (this.getItem().shouldUpdateInWorld()) {
					IsoWorld.instance.CurrentCell.addToProcessWorldItems(this);
				}

				IsoWorld.instance.CurrentCell.addToProcessItems(inventoryItem);
				this.updateSprite();
				LuaEventManager.triggerEvent("OnContainerUpdate");
				if (GameServer.bServer) {
					this.sendObjectChange("swapItem");
				}
			}
		}
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		if ("swapItem".equals(string)) {
			if (this.getItem() == null) {
				return;
			}

			try {
				this.getItem().saveWithSize(byteBuffer, false);
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		} else {
			super.saveChange(string, kahluaTable, byteBuffer);
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		if ("swapItem".equals(string)) {
			try {
				InventoryItem inventoryItem = InventoryItem.loadItem(byteBuffer, 195);
				if (inventoryItem != null) {
					this.swapItem(inventoryItem);
				}
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		} else {
			super.loadChange(string, byteBuffer);
		}
	}

	private boolean isWaterSource() {
		if (this.item == null) {
			return false;
		} else {
			if (this.item.isBroken()) {
			}

			if (!this.item.canStoreWater()) {
				return false;
			} else if (this.item.isWaterSource() && this.item instanceof DrainableComboItem) {
				return ((DrainableComboItem)this.item).getRainFactor() > 0.0F;
			} else {
				if (this.item.hasReplaceType("WaterSource")) {
					Item item = ScriptManager.instance.getItem(this.item.getReplaceType("WaterSource"));
					if (item != null && item.getType() == Item.Type.Drainable) {
						return item.getCanStoreWater() && item.getRainFactor() > 0.0F;
					}
				}

				return false;
			}
		}
	}

	public int getWaterAmount() {
		if (this.isWaterSource()) {
			return this.item instanceof DrainableComboItem ? ((DrainableComboItem)this.item).getRemainingUses() : 0;
		} else {
			return 0;
		}
	}

	public void setWaterAmount(int int1) {
		if (this.isWaterSource()) {
			DrainableComboItem drainableComboItem = (DrainableComboItem)Type.tryCastTo(this.item, DrainableComboItem.class);
			InventoryItem inventoryItem;
			if (drainableComboItem != null) {
				drainableComboItem.setUsedDelta((float)int1 * drainableComboItem.getUseDelta());
				if (int1 == 0 && drainableComboItem.getReplaceOnDeplete() != null) {
					inventoryItem = InventoryItemFactory.CreateItem(drainableComboItem.getReplaceOnDepleteFullType());
					if (inventoryItem != null) {
						inventoryItem.setCondition(this.getItem().getCondition());
						inventoryItem.setFavorite(this.getItem().isFavorite());
						this.swapItem(inventoryItem);
					}
				}
			} else if (int1 > 0 && this.getItem().hasReplaceType("WaterSource")) {
				inventoryItem = InventoryItemFactory.CreateItem(this.getItem().getReplaceType("WaterSource"));
				if (inventoryItem != null) {
					inventoryItem.setCondition(this.getItem().getCondition());
					inventoryItem.setFavorite(this.getItem().isFavorite());
					inventoryItem.setTaintedWater(this.getItem().isTaintedWater());
					drainableComboItem = (DrainableComboItem)Type.tryCastTo(inventoryItem, DrainableComboItem.class);
					if (drainableComboItem != null) {
						drainableComboItem.setUsedDelta((float)int1 * drainableComboItem.getUseDelta());
					}

					this.swapItem(inventoryItem);
				}
			}
		}
	}

	public int getWaterMax() {
		if (this.isWaterSource()) {
			float float1;
			if (this.item instanceof DrainableComboItem) {
				float1 = 1.0F / ((DrainableComboItem)this.item).getUseDelta();
			} else {
				if (!this.getItem().hasReplaceType("WaterSource")) {
					return 0;
				}

				Item item = ScriptManager.instance.getItem(this.getItem().getReplaceType("WaterSource"));
				if (item == null) {
					return 0;
				}

				float1 = 1.0F / item.getUseDelta();
			}

			return float1 - (float)((int)float1) > 0.99F ? (int)float1 + 1 : (int)float1;
		} else {
			return 0;
		}
	}

	public boolean isTaintedWater() {
		return this.isWaterSource() ? this.getItem().isTaintedWater() : false;
	}

	public void setTaintedWater(boolean boolean1) {
		if (this.isWaterSource()) {
			this.getItem().setTaintedWater(boolean1);
		}
	}

	public void update() {
		IsoCell cell = IsoWorld.instance.getCell();
		if (!this.removeProcess && this.item != null && this.item.shouldUpdateInWorld()) {
			cell.addToProcessItems(this.item);
		}
	}

	public void updateSprite() {
		this.sprite.setTintMod(new ColorInfo(this.item.col.r, this.item.col.g, this.item.col.b, this.item.col.a));
		if (!GameServer.bServer || ServerGUI.isCreated()) {
			String string = this.item.getTex().getName();
			if (this.item.isUseWorldItem()) {
				string = this.item.getWorldTexture();
			}

			Texture texture;
			try {
				texture = Texture.getSharedTexture(string);
				if (texture == null) {
					string = this.item.getTex().getName();
				}
			} catch (Exception exception) {
				string = "media/inventory/world/WItem_Sack.png";
			}

			texture = this.sprite.LoadFrameExplicit(string);
			if (this.item.getScriptItem() == null) {
				this.sprite.def.scaleAspect((float)texture.getWidthOrig(), (float)texture.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
			} else {
				float float1 = (float)Core.TileScale;
				float float2 = this.item.getScriptItem().ScaleWorldIcon * (float1 / 2.0F);
				this.sprite.def.setScale(float2, float2);
			}
		}
	}

	public boolean finishupdate() {
		return this.removeProcess || this.item == null || !this.item.shouldUpdateInWorld();
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		this.xoff = byteBuffer.getFloat();
		this.yoff = byteBuffer.getFloat();
		this.zoff = byteBuffer.getFloat();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
		this.item = InventoryItem.loadItem(byteBuffer, int1);
		BitHeaderRead bitHeaderRead;
		if (this.item == null) {
			byteBuffer.getDouble();
			if (int1 >= 193) {
				bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
				bitHeaderRead.release();
			}
		} else {
			this.item.setWorldItem(this);
			this.sprite.getTintMod().r = this.item.getR();
			this.sprite.getTintMod().g = this.item.getG();
			this.sprite.getTintMod().b = this.item.getB();
			if (int1 >= 108) {
				this.dropTime = byteBuffer.getDouble();
			} else {
				this.dropTime = GameTime.getInstance().getWorldAgeHours();
			}

			if (int1 >= 193) {
				bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
				this.ignoreRemoveSandbox = bitHeaderRead.hasFlags(1);
				bitHeaderRead.release();
			}

			if (!GameServer.bServer || ServerGUI.isCreated()) {
				String string = this.item.getTex().getName();
				if (this.item.isUseWorldItem()) {
					string = this.item.getWorldTexture();
				}

				Texture texture;
				try {
					texture = Texture.getSharedTexture(string);
					if (texture == null) {
						string = this.item.getTex().getName();
					}
				} catch (Exception exception) {
					string = "media/inventory/world/WItem_Sack.png";
				}

				texture = this.sprite.LoadFrameExplicit(string);
				if (texture != null) {
					if (int1 < 33) {
						float float3 = float1 - (float)(texture.getWidthOrig() / 2);
						float3 = float2 - (float)texture.getHeightOrig();
					}

					if (this.item.getScriptItem() == null) {
						this.sprite.def.scaleAspect((float)texture.getWidthOrig(), (float)texture.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
					} else {
						float float4 = (float)Core.TileScale;
						float float5 = this.item.getScriptItem().ScaleWorldIcon * (float4 / 2.0F);
						this.sprite.def.setScale(float5, float5);
					}
				}
			}
		}
	}

	public boolean Serialize() {
		return true;
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		byteBuffer.put((byte)(this.Serialize() ? 1 : 0));
		if (this.Serialize()) {
			byteBuffer.put(IsoObject.factoryGetClassID(this.getObjectName()));
			byteBuffer.putFloat(this.xoff);
			byteBuffer.putFloat(this.yoff);
			byteBuffer.putFloat(this.zoff);
			byteBuffer.putFloat(this.offsetX);
			byteBuffer.putFloat(this.offsetY);
			this.item.saveWithSize(byteBuffer, false);
			byteBuffer.putDouble(this.dropTime);
			BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
			if (this.ignoreRemoveSandbox) {
				bitHeaderWrite.addFlags(1);
			}

			bitHeaderWrite.write();
			bitHeaderWrite.release();
		}
	}

	public void softReset() {
		this.square.removeWorldObject(this);
	}

	public String getObjectName() {
		return "WorldInventoryItem";
	}

	public void DoTooltip(ObjectTooltip objectTooltip) {
		this.item.DoTooltip(objectTooltip);
	}

	public boolean HasTooltip() {
		return false;
	}

	public boolean onMouseLeftClick(int int1, int int2) {
		return false;
	}

	private void debugDrawLocation(float float1, float float2, float float3) {
		if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue()) {
			float1 += this.xoff;
			float2 += this.yoff;
			float3 += this.zoff;
			LineDrawer.DrawIsoLine(float1 - 0.25F, float2, float3, float1 + 0.25F, float2, float3, 1.0F, 1.0F, 1.0F, 0.5F, 1);
			LineDrawer.DrawIsoLine(float1, float2 - 0.25F, float3, float1, float2 + 0.25F, float3, 1.0F, 1.0F, 1.0F, 0.5F, 1);
		}
	}

	private void debugHitTest() {
		int int1 = IsoCamera.frameState.playerIndex;
		float float1 = Core.getInstance().getZoom(int1);
		float float2 = (float)Mouse.getXA();
		float float3 = (float)Mouse.getYA();
		float2 -= (float)IsoCamera.getScreenLeft(int1);
		float3 -= (float)IsoCamera.getScreenTop(int1);
		float2 *= float1;
		float3 *= float1;
		float float4 = this.getScreenPosX(int1) * float1;
		float float5 = this.getScreenPosY(int1) * float1;
		float float6 = IsoUtils.DistanceTo2D(float4, float5, float2, float3);
		byte byte1 = 48;
		if (float6 < (float)byte1) {
			LineDrawer.drawCircle(float4, float5, (float)byte1, 16, 1.0F, 1.0F, 1.0F);
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (Core.bDebug) {
		}

		if (this.getItem().getScriptItem().isWorldRender()) {
			if (WorldItemModelDrawer.renderMain(this.getItem(), this.getSquare(), this.getX() + this.xoff, this.getY() + this.yoff, this.getZ() + this.zoff, 0.0F)) {
				this.debugDrawLocation(float1, float2, float3);
			} else if (this.sprite.CurrentAnim != null && !this.sprite.CurrentAnim.Frames.isEmpty()) {
				Texture texture = ((IsoDirectionFrame)this.sprite.CurrentAnim.Frames.get(0)).getTexture(this.dir);
				if (texture != null) {
					float float4 = (float)texture.getWidthOrig() * this.sprite.def.getScaleX() / 2.0F;
					float float5 = (float)texture.getHeightOrig() * this.sprite.def.getScaleY() * 3.0F / 4.0F;
					int int1 = IsoCamera.frameState.playerIndex;
					float float6 = this.getAlpha(int1);
					float float7 = this.getTargetAlpha(int1);
					float float8 = PZMath.min(getSurfaceAlpha(this.square, this.zoff), float6);
					this.setAlphaAndTarget(int1, float8);
					this.sprite.render(this, float1 + this.xoff, float2 + this.yoff, float3 + this.zoff, this.dir, this.offsetX + float4, this.offsetY + float5, colorInfo, true);
					this.setAlpha(int1, float6);
					this.setTargetAlpha(int1, float7);
					this.debugDrawLocation(float1, float2, float3);
				}
			}
		}
	}

	public void renderObjectPicker(float float1, float float2, float float3, ColorInfo colorInfo) {
		if (this.sprite != null) {
			if (this.sprite.CurrentAnim != null && !this.sprite.CurrentAnim.Frames.isEmpty()) {
				Texture texture = ((IsoDirectionFrame)this.sprite.CurrentAnim.Frames.get(0)).getTexture(this.dir);
				if (texture != null) {
					float float4 = (float)(texture.getWidthOrig() / 2);
					float float5 = (float)texture.getHeightOrig();
					this.sprite.renderObjectPicker(this.sprite.def, this, this.dir);
				}
			}
		}
	}

	public InventoryItem getItem() {
		return this.item;
	}

	public void addToWorld() {
		if (this.item != null && this.item.shouldUpdateInWorld() && !IsoWorld.instance.CurrentCell.getProcessWorldItems().contains(this)) {
			IsoWorld.instance.CurrentCell.getProcessWorldItems().add(this);
		}

		if (this.item instanceof InventoryContainer) {
			ItemContainer itemContainer = ((InventoryContainer)this.item).getInventory();
			if (itemContainer != null) {
				itemContainer.addItemsToProcessItems();
			}
		}

		super.addToWorld();
	}

	public void removeFromWorld() {
		this.removeProcess = true;
		IsoWorld.instance.getCell().getProcessWorldItems().remove(this);
		if (this.item != null) {
			IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this.item);
			ItemSoundManager.removeItem(this.item);
			this.item.atlasTexture = null;
		}

		if (this.item instanceof InventoryContainer) {
			ItemContainer itemContainer = ((InventoryContainer)this.item).getInventory();
			if (itemContainer != null) {
				itemContainer.removeItemsFromProcessItems();
			}
		}

		super.removeFromWorld();
	}

	public void removeFromSquare() {
		if (this.square != null) {
			this.square.getWorldObjects().remove(this);
			this.square.chunk.recalcHashCodeObjects();
		}

		super.removeFromSquare();
	}

	public float getScreenPosX(int int1) {
		float float1 = IsoUtils.XToScreen(this.getX() + this.xoff, this.getY() + this.yoff, this.getZ() + this.zoff, 0);
		PlayerCamera playerCamera = IsoCamera.cameras[int1];
		return (float1 - playerCamera.getOffX()) / Core.getInstance().getZoom(int1);
	}

	public float getScreenPosY(int int1) {
		Texture texture = this.sprite == null ? null : this.sprite.getTextureForCurrentFrame(this.dir);
		float float1 = texture == null ? 0.0F : (float)texture.getHeightOrig() * this.sprite.def.getScaleY() * 1.0F / 4.0F;
		float float2 = IsoUtils.YToScreen(this.getX() + this.xoff, this.getY() + this.yoff, this.getZ() + this.zoff, 0);
		PlayerCamera playerCamera = IsoCamera.cameras[int1];
		return (float2 - playerCamera.getOffY() - float1) / Core.getInstance().getZoom(int1);
	}

	public void setIgnoreRemoveSandbox(boolean boolean1) {
		this.ignoreRemoveSandbox = boolean1;
	}

	public boolean isIgnoreRemoveSandbox() {
		return this.ignoreRemoveSandbox;
	}

	public float getWorldPosX() {
		return this.getX() + this.xoff;
	}

	public float getWorldPosY() {
		return this.getY() + this.yoff;
	}

	public float getWorldPosZ() {
		return this.getZ() + this.zoff;
	}

	public static float getSurfaceAlpha(IsoGridSquare square, float float1) {
		if (square == null) {
			return 1.0F;
		} else {
			int int1 = IsoCamera.frameState.playerIndex;
			float float2 = 1.0F;
			if (float1 > 0.01F) {
				boolean boolean1 = false;
				for (int int2 = 0; int2 < square.getObjects().size(); ++int2) {
					IsoObject object = (IsoObject)square.getObjects().get(int2);
					if (object.getSurfaceOffsetNoTable() > 0.0F) {
						if (!boolean1) {
							boolean1 = true;
							float2 = 0.0F;
						}

						float2 = PZMath.max(float2, object.getAlpha(int1));
					}
				}
			}

			return float2;
		}
	}
}
