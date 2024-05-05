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
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.interfaces.IClothingWasherDryerLogic;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class ClothingDryerLogic implements IClothingWasherDryerLogic {
	private final IsoObject m_object;
	private boolean bActivated;
	private long soundInstance = -1L;
	private float lastUpdate = -1.0F;
	private boolean cycleFinished = false;
	private float startTime = 0.0F;
	private float cycleLengthMinutes = 90.0F;
	private boolean alreadyExecuted = false;

	public ClothingDryerLogic(IsoObject object) {
		this.m_object = object;
	}

	public IsoObject getObject() {
		return this.m_object;
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		this.bActivated = byteBuffer.get() == 1;
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
	}

	public void update() {
		if (this.getObject().getObjectIndex() != -1) {
			if (this.getContainer() != null) {
				if (!this.getContainer().isPowered()) {
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
						for (int int2 = 0; int2 < this.getContainer().getItems().size(); ++int2) {
							InventoryItem inventoryItem = (InventoryItem)this.getContainer().getItems().get(int2);
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

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		if ("dryer.state".equals(string)) {
			byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		if ("dryer.state".equals(string)) {
			this.setActivated(byteBuffer.get() == 1);
		}
	}

	public ItemContainer getContainer() {
		return this.getObject().getContainerByType("clothingdryer");
	}

	private void updateSound() {
		if (this.isActivated()) {
			if (!GameServer.bServer) {
				if (this.getObject().emitter != null && this.getObject().emitter.isPlaying("ClothingDryerFinished")) {
					this.getObject().emitter.stopOrTriggerSoundByName("ClothingDryerFinished");
				}

				if (this.soundInstance == -1L) {
					this.getObject().emitter = IsoWorld.instance.getFreeEmitter(this.getObject().getX() + 0.5F, this.getObject().getY() + 0.5F, (float)((int)this.getObject().getZ()));
					IsoWorld.instance.setEmitterOwner(this.getObject().emitter, this.getObject());
					this.soundInstance = this.getObject().emitter.playSoundLoopedImpl("ClothingDryerRunning");
				}
			}

			if (!GameClient.bClient) {
				WorldSoundManager.instance.addSoundRepeating(this, this.getObject().square.x, this.getObject().square.y, this.getObject().square.z, 10, 10, false);
			}
		} else if (this.soundInstance != -1L) {
			this.getObject().emitter.stopOrTriggerSound(this.soundInstance);
			this.soundInstance = -1L;
			if (this.cycleFinished) {
				this.cycleFinished = false;
				this.getObject().emitter.playSoundImpl("ClothingDryerFinished", this.getObject());
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
		if (this.isActivated()) {
			return false;
		} else {
			return this.getContainer() == itemContainer;
		}
	}

	public boolean isRemoveItemAllowedFromContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		if (!this.getContainer().isEmpty() && this.isActivated()) {
			return false;
		} else {
			return this.getContainer() == itemContainer;
		}
	}

	public boolean isActivated() {
		return this.bActivated;
	}

	public void setActivated(boolean boolean1) {
		this.bActivated = boolean1;
		this.alreadyExecuted = false;
	}

	public void switchModeOn() {
	}

	public void switchModeOff() {
		this.setActivated(false);
		this.updateSound();
		this.cycleFinished = false;
	}
}
