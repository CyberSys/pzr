package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.WorldSoundManager;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Clothing;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.interfaces.IClothingWasherDryerLogic;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class ClothingWasherLogic implements IClothingWasherDryerLogic {
	private final IsoObject m_object;
	private boolean bActivated;
	private long soundInstance = -1L;
	private float lastUpdate = -1.0F;
	private boolean cycleFinished = false;
	private float startTime = 0.0F;
	private float cycleLengthMinutes = 90.0F;
	private boolean alreadyExecuted = false;

	public ClothingWasherLogic(IsoObject object) {
		this.m_object = object;
	}

	public IsoObject getObject() {
		return this.m_object;
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		this.bActivated = byteBuffer.get() == 1;
		this.lastUpdate = byteBuffer.getFloat();
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
		byteBuffer.putFloat(this.lastUpdate);
	}

	public void update() {
		if (this.getObject().getObjectIndex() != -1) {
			if (!this.getContainer().isPowered()) {
				this.setActivated(false);
			}

			this.updateSound();
			this.cycleFinished();
			if (GameClient.bClient) {
			}

			if (this.getObject().getWaterAmount() <= 0) {
				this.setActivated(false);
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
					this.getObject().useWater(1 * int1);
					for (int int2 = 0; int2 < this.getContainer().getItems().size(); ++int2) {
						InventoryItem inventoryItem = (InventoryItem)this.getContainer().getItems().get(int2);
						if (inventoryItem instanceof Clothing) {
							Clothing clothing = (Clothing)inventoryItem;
							float float3 = clothing.getBloodlevel();
							if (float3 > 0.0F) {
								this.removeBlood(clothing, (float)(int1 * 2));
							}

							float float4 = clothing.getDirtyness();
							if (float4 > 0.0F) {
								this.removeDirt(clothing, (float)(int1 * 2));
							}

							clothing.setWetness(100.0F);
						}
					}
				}
			}
		}
	}

	private void removeBlood(Clothing clothing, float float1) {
		ItemVisual itemVisual = clothing.getVisual();
		if (itemVisual != null) {
			for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
				BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int1);
				float float2 = itemVisual.getBlood(bloodBodyPartType);
				if (float2 > 0.0F) {
					itemVisual.setBlood(bloodBodyPartType, float2 - float1 / 100.0F);
				}
			}

			BloodClothingType.calcTotalBloodLevel(clothing);
		}
	}

	private void removeDirt(Clothing clothing, float float1) {
		ItemVisual itemVisual = clothing.getVisual();
		if (itemVisual != null) {
			for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
				BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int1);
				float float2 = itemVisual.getDirt(bloodBodyPartType);
				if (float2 > 0.0F) {
					itemVisual.setDirt(bloodBodyPartType, float2 - float1 / 100.0F);
				}
			}

			BloodClothingType.calcTotalDirtLevel(clothing);
		}
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		if ("washer.state".equals(string)) {
			byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		if ("washer.state".equals(string)) {
			this.setActivated(byteBuffer.get() == 1);
		}
	}

	public ItemContainer getContainer() {
		return this.getObject().getContainerByType("clothingwasher");
	}

	private void updateSound() {
		if (this.isActivated()) {
			if (!GameServer.bServer) {
				if (this.getObject().emitter != null && this.getObject().emitter.isPlaying("ClothingWasherFinished")) {
					this.getObject().emitter.stopOrTriggerSoundByName("ClothingWasherFinished");
				}

				if (this.soundInstance == -1L) {
					this.getObject().emitter = IsoWorld.instance.getFreeEmitter(this.getObject().getX() + 0.5F, this.getObject().getY() + 0.5F, (float)((int)this.getObject().getZ()));
					IsoWorld.instance.setEmitterOwner(this.getObject().emitter, this.getObject());
					this.soundInstance = this.getObject().emitter.playSoundLoopedImpl("ClothingWasherRunning");
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
				this.getObject().emitter.playSoundImpl("ClothingWasherFinished", this.getObject());
			}
		}
	}

	public boolean isItemAllowedInContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		if (itemContainer != this.getContainer()) {
			return false;
		} else {
			return !this.isActivated();
		}
	}

	public boolean isRemoveItemAllowedFromContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		if (itemContainer != this.getContainer()) {
			return false;
		} else {
			return itemContainer.isEmpty() || !this.isActivated();
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

	public boolean isActivated() {
		return this.bActivated;
	}

	public void setActivated(boolean boolean1) {
		boolean boolean2 = boolean1 != this.bActivated;
		this.bActivated = boolean1;
		this.alreadyExecuted = false;
		if (boolean2) {
			Thread thread = Thread.currentThread();
			if (thread == GameWindow.GameThread || thread == GameServer.MainThread) {
				IsoGenerator.updateGenerator(this.getObject().getSquare());
			}
		}
	}

	public void switchModeOn() {
	}

	public void switchModeOff() {
		this.setActivated(false);
		this.updateSound();
		this.cycleFinished = false;
	}
}
