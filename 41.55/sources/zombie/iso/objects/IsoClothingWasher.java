package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.WorldSoundManager;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Clothing;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class IsoClothingWasher extends IsoObject {
	private boolean bActivated;
	private long soundInstance = -1L;
	private float lastUpdate = -1.0F;
	private boolean cycleFinished = false;
	private float startTime = 0.0F;
	private float cycleLengthMinutes = 90.0F;
	private boolean alreadyExecuted = false;
	private static final ArrayList coveredParts = new ArrayList();

	public IsoClothingWasher(IsoCell cell) {
		super(cell);
	}

	public IsoClothingWasher(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
	}

	public String getObjectName() {
		return "ClothingWasher";
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.bActivated = byteBuffer.get() == 1;
		this.lastUpdate = byteBuffer.getFloat();
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
		byteBuffer.putFloat(this.lastUpdate);
	}

	public void update() {
		if (this.getObjectIndex() != -1) {
			if (!this.container.isPowered()) {
				this.setActivated(false);
			}

			this.updateSound();
			this.cycleFinished();
			if (GameClient.bClient) {
			}

			if (this.getWaterAmount() <= 0) {
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
					this.useWater(1 * int1);
					for (int int2 = 0; int2 < this.container.getItems().size(); ++int2) {
						InventoryItem inventoryItem = (InventoryItem)this.container.getItems().get(int2);
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
				if (this.emitter != null && this.emitter.isPlaying("ClothingWasherFinished")) {
					this.emitter.stopSoundByName("ClothingWasherFinished");
				}

				if (this.soundInstance == -1L) {
					this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (float)((int)this.getZ()));
					this.soundInstance = this.emitter.playSoundLoopedImpl("ClothingWasherRunning");
				}
			}

			if (!GameClient.bClient) {
				WorldSoundManager.instance.addSound(this, this.square.x, this.square.y, this.square.z, 10, 10);
			}
		} else if (this.soundInstance != -1L) {
			this.emitter.stopSound(this.soundInstance);
			this.soundInstance = -1L;
			if (this.cycleFinished) {
				this.cycleFinished = false;
				this.emitter.playSoundImpl("ClothingWasherFinished", (IsoObject)this);
			} else {
				this.emitter = null;
			}
		}
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
		this.bActivated = boolean1;
		this.alreadyExecuted = false;
	}
}
