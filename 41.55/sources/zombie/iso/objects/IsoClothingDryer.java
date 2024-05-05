package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.WorldSoundManager;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Clothing;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class IsoClothingDryer extends IsoObject {
	private boolean bActivated;
	private long SoundInstance = -1L;
	private float lastUpdate = -1.0F;
	private boolean cycleFinished = false;
	private float startTime = 0.0F;
	private float cycleLengthMinutes = 90.0F;
	private boolean alreadyExecuted = false;

	public IsoClothingDryer(IsoCell cell) {
		super(cell);
	}

	public IsoClothingDryer(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
	}

	public String getObjectName() {
		return "ClothingDryer";
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.bActivated = byteBuffer.get() == 1;
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
	}

	public void update() {
		if (this.getObjectIndex() != -1) {
			if (this.container != null) {
				if (!this.container.isPowered()) {
					this.setActivated(false);
				}

				this.cycleFinished();
				this.updateSound();
				if (GameClient.bClient) {
				}

				if (!this.isActivated()) {
					this.lastUpdate = -1.0F;
				} else {
					float float1 = (float)GameTime.getInstance().getWorldAgeHours();
					if (this.lastUpdate < 0.0F) {
						this.lastUpdate = float1;
					} else if (this.lastUpdate > float1) {
						this.lastUpdate = float1;
					}

					float float2 = float1 - this.lastUpdate;
					int int1 = (int)(float2 * 60.0F);
					if (int1 >= 1) {
						this.lastUpdate = float1;
						for (int int2 = 0; int2 < this.container.getItems().size(); ++int2) {
							InventoryItem inventoryItem = (InventoryItem)this.container.getItems().get(int2);
							if (inventoryItem instanceof Clothing) {
								Clothing clothing = (Clothing)inventoryItem;
								float float3 = clothing.getWetness();
								if (float3 > 0.0F) {
									float3 -= (float)int1;
									clothing.setWetness(float3);
									if (GameServer.bServer) {
									}
								}
							}

							if (inventoryItem.isWet() && inventoryItem.getItemWhenDry() != null) {
								inventoryItem.setWetCooldown(inventoryItem.getWetCooldown() - (float)(int1 * 250));
								if (inventoryItem.getWetCooldown() <= 0.0F) {
									InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem(inventoryItem.getItemWhenDry());
									this.getContainer().addItem(inventoryItem2);
									this.getContainer().Remove(inventoryItem);
									--int2;
									inventoryItem.setWet(false);
									IsoWorld.instance.CurrentCell.addToProcessItemsRemove(inventoryItem);
								}
							}
						}
					}
				}
			}
		}
	}

	public void addToWorld() {
		IsoCell cell = this.getCell();
		cell.addToProcessIsoObject(this);
	}

	public void removeFromWorld() {
		super.removeFromWorld();
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		if ("state".equals(string)) {
			byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		if ("state".equals(string)) {
			this.setActivated(byteBuffer.get() == 1);
		}
	}

	private void updateSound() {
		if (this.isActivated()) {
			if (!GameServer.bServer) {
				if (this.emitter != null && this.emitter.isPlaying("ClothingDryerFinished")) {
					this.emitter.stopSoundByName("ClothingDryerFinished");
				}

				if (this.SoundInstance == -1L) {
					this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (float)((int)this.getZ()));
					this.SoundInstance = this.emitter.playSoundLoopedImpl("ClothingDryerRunning");
				}
			}

			if (!GameClient.bClient) {
				WorldSoundManager.instance.addSound(this, this.square.x, this.square.y, this.square.z, 10, 10);
			}
		} else if (this.SoundInstance != -1L) {
			this.emitter.stopSound(this.SoundInstance);
			this.SoundInstance = -1L;
			if (this.cycleFinished) {
				this.cycleFinished = false;
				this.emitter.playSoundImpl("ClothingDryerFinished", (IsoObject)this);
			} else {
				this.emitter = null;
			}
		}
	}

	private boolean cycleFinished() {
		if (this.isActivated()) {
			if (!this.alreadyExecuted) {
				this.startTime = (float)GameTime.getInstance().getWorldAgeHours();
				this.alreadyExecuted = true;
			}

			float float1 = (float)GameTime.getInstance().getWorldAgeHours() - this.startTime;
			int int1 = (int)(float1 * 60.0F);
			if ((float)int1 < this.cycleLengthMinutes) {
				return false;
			}

			this.cycleFinished = true;
			this.setActivated(false);
		}

		return true;
	}

	public boolean isItemAllowedInContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		return !this.isActivated();
	}

	public boolean isRemoveItemAllowedFromContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		if (this.container.Items.size() > 0 && this.isActivated()) {
			return false;
		} else {
			return this.container == itemContainer;
		}
	}

	public boolean isActivated() {
		return this.bActivated;
	}

	public void setActivated(boolean boolean1) {
		this.bActivated = boolean1;
		this.alreadyExecuted = false;
	}
}
