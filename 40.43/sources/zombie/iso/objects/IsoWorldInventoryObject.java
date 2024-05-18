package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameApplet;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemSoundManager;
import zombie.inventory.types.InventoryContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.ui.ObjectTooltip;


public class IsoWorldInventoryObject extends IsoObject {
	public InventoryItem item;
	public float xoff;
	public float yoff;
	public float zoff;
	public boolean removeProcess = false;
	public double dropTime = -1.0;

	public IsoWorldInventoryObject(InventoryItem inventoryItem, IsoGridSquare square, float float1, float float2, float float3) {
		this.OutlineOnMouseover = true;
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
		this.sprite = IsoSprite.CreateSprite(square.getCell().SpriteManager);
		if (!GameServer.bServer || ServerGUI.isCreated()) {
			String string = inventoryItem.getWorldTexture();
			Texture texture;
			try {
				texture = Texture.getSharedTexture(string);
				if (texture == null) {
					string = inventoryItem.getTex().getName();
				}
			} catch (Exception exception) {
				string = "media/inventory/world/WItem_Sack.png";
			}

			texture = this.sprite.LoadFrameExplicit(string);
			if (inventoryItem.getScriptItem() != null && !inventoryItem.getScriptItem().ResizeWorldIcon) {
				if (inventoryItem.getScriptItem() != null) {
					this.sprite.def.setScale(inventoryItem.getScriptItem().ScaleWorldIcon, inventoryItem.getScriptItem().ScaleWorldIcon);
				}
			} else {
				this.sprite.def.scaleAspect((float)texture.getWidthOrig(), (float)texture.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
			}
		}

		this.sprite.setTintMod(new ColorInfo(inventoryItem.col.r, inventoryItem.col.g, inventoryItem.col.b, inventoryItem.col.a));
		this.square = square;
		this.offsetY = 0.0F;
		this.offsetX = 0.0F;
		this.dropTime = GameTime.getInstance().getWorldAgeHours();
	}

	public void update() {
		IsoCell cell = IsoWorld.instance.getCell();
		if (!this.removeProcess && this.item != null && this.item.shouldUpdateInWorld()) {
			cell.addToProcessItems(this.item);
		}
	}

	public void updateSprite() {
		if (!GameServer.bServer || ServerGUI.isCreated()) {
			String string = this.item.getWorldTexture();
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
			if (this.item.getScriptItem() != null && !this.item.getScriptItem().ResizeWorldIcon) {
				if (this.item.getScriptItem() != null) {
					this.sprite.def.setScale(this.item.getScriptItem().ScaleWorldIcon, this.item.getScriptItem().ScaleWorldIcon);
				}
			} else {
				this.sprite.def.scaleAspect((float)texture.getWidthOrig(), (float)texture.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
			}

			this.sprite.setTintMod(new ColorInfo(this.item.col.r, this.item.col.g, this.item.col.b, this.item.col.a));
		}
	}

	public boolean finishupdate() {
		return this.removeProcess || this.item == null || !this.item.shouldUpdateInWorld();
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.xoff = byteBuffer.getFloat();
		this.yoff = byteBuffer.getFloat();
		this.zoff = byteBuffer.getFloat();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		this.sprite = IsoSprite.CreateSprite(IsoWorld.instance.spriteManager);
		int int2 = -1;
		if (int1 >= 54) {
			int2 = int1 >= 72 ? byteBuffer.getInt() : byteBuffer.getShort();
		}

		int int3 = byteBuffer.position();
		String string = GameWindow.ReadString(byteBuffer);
		byte byte1 = -1;
		if (int1 >= 70) {
			byte1 = byteBuffer.get();
			if (byte1 < 0) {
				throw new IOException("invalid item save-type " + byte1);
			}
		}

		if (string.contains("..")) {
			string = string.replace("..", ".");
		}

		byte byte2 = 0;
		if (int1 >= 108) {
			byte2 = 8;
		}

		this.item = InventoryItemFactory.CreateItem(string);
		if (this.item == null && int2 != -1) {
			if (string.length() > 40) {
				string = "<unknown>";
			}

			DebugLog.log("Cannot load \"" + string + "\" item. Make sure all mods used in save are installed.");
			while (byteBuffer.position() < int3 + int2 + byte2) {
				byteBuffer.get();
			}
		} else if (this.item == null) {
			if (string.length() > 40) {
				string = "<unknown>";
			}

			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, "Cannot load \"" + string + "\" item. Make sure all mods used in save are installed.", (Object)null);
			throw new RuntimeException("Cannot load \"" + string + "\" item");
		} else if (byte1 != -1 && this.item.getSaveType() != byte1) {
			DebugLog.log("ignoring \"" + string + "\" because type changed from " + byte1 + " to " + this.item.getSaveType());
			while (byteBuffer.position() < int3 + int2 + byte2) {
				byteBuffer.get();
			}

			this.item = null;
		} else {
			this.item.load(byteBuffer, int1, false);
			if (int2 != -1 && byteBuffer.position() != int3 + int2) {
				throw new IOException("item load() read more data than save() wrote (" + string + ")");
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

				if (!GameServer.bServer || ServerGUI.isCreated()) {
					String string2 = this.item.getWorldTexture();
					Texture texture;
					try {
						texture = Texture.getSharedTexture(string2);
						if (texture == null) {
							string2 = this.item.getTex().getName();
						}
					} catch (Exception exception) {
						string2 = "media/inventory/world/WItem_Sack.png";
					}

					texture = this.sprite.LoadFrameExplicit(string2);
					if (texture != null) {
						if (int1 < 33) {
							float float3 = float1 - (float)(texture.getWidthOrig() / 2);
							float3 = float2 - (float)texture.getHeightOrig();
						}

						if (this.item.getScriptItem() != null && !this.item.getScriptItem().ResizeWorldIcon) {
							if (this.item.getScriptItem() != null) {
								this.sprite.def.setScale(this.item.getScriptItem().ScaleWorldIcon, this.item.getScriptItem().ScaleWorldIcon);
							}
						} else {
							this.sprite.def.scaleAspect((float)texture.getWidthOrig(), (float)texture.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
						}
					}
				}
			}
		}
	}

	public boolean Serialize() {
		return true;
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)(this.Serialize() ? 1 : 0));
		if (this.Serialize()) {
			byteBuffer.putInt(this.getObjectName().hashCode());
			byteBuffer.putFloat(this.xoff);
			byteBuffer.putFloat(this.yoff);
			byteBuffer.putFloat(this.zoff);
			byteBuffer.putFloat(this.offsetX);
			byteBuffer.putFloat(this.offsetY);
			this.item.saveWithSize(byteBuffer, false);
			byteBuffer.putDouble(this.dropTime);
		}
	}

	public void softReset() {
		this.square.removeWorldObject(this);
	}

	public String getObjectName() {
		return "WorldInventoryItem";
	}

	public IsoWorldInventoryObject(IsoCell cell) {
		super(cell);
		this.offsetY = 0.0F;
		this.offsetX = 0.0F;
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

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		if (this.sprite.CurrentAnim != null && !this.sprite.CurrentAnim.Frames.isEmpty()) {
			Texture texture = ((IsoDirectionFrame)this.sprite.CurrentAnim.Frames.get(0)).getTexture(this.dir);
			if (texture != null) {
				float float4 = (float)texture.getWidthOrig() * this.sprite.def.getScaleX() / 2.0F;
				float float5 = (float)texture.getHeightOrig() * this.sprite.def.getScaleY() * 3.0F / 4.0F;
				this.sprite.render(this, float1 + this.xoff, float2 + this.yoff, float3 + this.zoff, this.dir, this.offsetX + float4, this.offsetY + float5, colorInfo);
				if (Core.bDebug) {
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
					this.sprite.renderObjectPicker(this.sprite.def, this, float1 + this.xoff, float2 + this.yoff, float3 + this.zoff, this.dir, this.offsetX + float4, this.offsetY + float5, colorInfo);
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
}
