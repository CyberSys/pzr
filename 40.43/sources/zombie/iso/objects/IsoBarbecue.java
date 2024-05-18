package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class IsoBarbecue extends IsoObject {
	boolean bHasPropaneTank = false;
	int FuelAmount = 0;
	boolean bLit = false;
	protected float LastUpdateTime = -1.0F;
	protected float MinuteAccumulator = 0.0F;
	protected int MinutesSinceExtinguished = -1;
	IsoSprite normalSprite = null;
	IsoSprite noTankSprite = null;
	private IsoHeatSource heatSource;
	private static int SMOULDER_MINUTES = 10;

	public IsoBarbecue(IsoCell cell) {
		super(cell);
	}

	public IsoBarbecue(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
		this.container = new ItemContainer("barbecue", square, this, 1, 1);
		this.container.setExplored(true);
		this.bHasPropaneTank = this.isPropaneBBQ();
		if (this.bHasPropaneTank) {
			this.FuelAmount = 1200;
		}

		this.normalSprite = this.sprite;
		if (this.sprite != null && this.bHasPropaneTank) {
			byte byte1 = 8;
			this.noTankSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (IsoSprite)this.sprite, byte1);
		}
	}

	public String getObjectName() {
		return "Barbecue";
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.bHasPropaneTank = byteBuffer.get() == 1;
		this.FuelAmount = byteBuffer.getInt();
		this.bLit = byteBuffer.get() == 1;
		this.LastUpdateTime = byteBuffer.getFloat();
		this.MinutesSinceExtinguished = byteBuffer.getInt();
		if (byteBuffer.get() == 1) {
			this.normalSprite = IsoSprite.getSprite(this.getCell().SpriteManager, byteBuffer.getInt());
		}

		if (byteBuffer.get() == 1) {
			this.noTankSprite = IsoSprite.getSprite(this.getCell().SpriteManager, byteBuffer.getInt());
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.put((byte)(this.bHasPropaneTank ? 1 : 0));
		byteBuffer.putInt(this.FuelAmount);
		byteBuffer.put((byte)(this.bLit ? 1 : 0));
		byteBuffer.putFloat(this.LastUpdateTime);
		byteBuffer.putInt(this.MinutesSinceExtinguished);
		if (this.normalSprite != null) {
			byteBuffer.put((byte)1);
			byteBuffer.putInt(this.normalSprite.ID);
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.noTankSprite != null) {
			byteBuffer.put((byte)1);
			byteBuffer.putInt(this.noTankSprite.ID);
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void setFuelAmount(int int1) {
		int1 = Math.max(0, int1);
		int int2 = this.getFuelAmount();
		if (int1 != int2) {
			this.FuelAmount = int1;
		}
	}

	public int getFuelAmount() {
		return this.FuelAmount;
	}

	public void addFuel(int int1) {
		this.setFuelAmount(this.getFuelAmount() + int1);
	}

	public int useFuel(int int1) {
		int int2 = this.getFuelAmount();
		boolean boolean1 = false;
		int int3;
		if (int2 >= int1) {
			int3 = int1;
		} else {
			int3 = int2;
		}

		this.setFuelAmount(int2 - int3);
		return int3;
	}

	public boolean hasFuel() {
		return this.getFuelAmount() > 0;
	}

	public boolean hasPropaneTank() {
		return this.isPropaneBBQ() && this.bHasPropaneTank;
	}

	public boolean isPropaneBBQ() {
		return this.getSprite() != null && this.getProperties().Is("propaneTank");
	}

	public void setPropaneTank(InventoryItem inventoryItem) {
		if (inventoryItem.getFullType().equals("Base.PropaneTank")) {
			this.bHasPropaneTank = true;
			this.FuelAmount = 1200;
			if (inventoryItem instanceof DrainableComboItem) {
				this.FuelAmount = (int)((float)this.FuelAmount * ((DrainableComboItem)inventoryItem).getUsedDelta());
			}
		}
	}

	public InventoryItem removePropaneTank() {
		if (!this.bHasPropaneTank) {
			return null;
		} else {
			this.bHasPropaneTank = false;
			this.bLit = false;
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.PropaneTank");
			if (inventoryItem instanceof DrainableComboItem) {
				((DrainableComboItem)inventoryItem).setUsedDelta((float)this.getFuelAmount() / 1200.0F);
			}

			this.FuelAmount = 0;
			return inventoryItem;
		}
	}

	public void setLit(boolean boolean1) {
		this.bLit = boolean1;
	}

	public boolean isLit() {
		return this.bLit;
	}

	public void turnOn() {
		if (!this.isLit()) {
			this.setLit(true);
		}
	}

	public void turnOff() {
		if (this.isLit()) {
			this.setLit(false);
		}
	}

	public void toggle() {
		this.setLit(!this.isLit());
	}

	public void extinguish() {
		if (this.isLit()) {
			this.setLit(false);
			if (this.hasFuel() && !this.isPropaneBBQ()) {
				this.MinutesSinceExtinguished = 0;
			}
		}
	}

	public float getTemperature() {
		return this.isLit() ? 1.8F : 1.0F;
	}

	private void updateSprite() {
		if (this.isPropaneBBQ()) {
			if (this.hasPropaneTank()) {
				this.sprite = this.normalSprite;
			} else {
				this.sprite = this.noTankSprite;
			}
		}
	}

	private void updateHeatSource() {
		if (this.isLit()) {
			if (this.heatSource == null) {
				this.heatSource = new IsoHeatSource((int)this.getX(), (int)this.getY(), (int)this.getZ(), 3, 25);
				IsoWorld.instance.CurrentCell.addHeatSource(this.heatSource);
			}
		} else if (this.heatSource != null) {
			IsoWorld.instance.CurrentCell.removeHeatSource(this.heatSource);
			this.heatSource = null;
		}
	}

	public void update() {
		if (!GameClient.bClient) {
			boolean boolean1 = this.hasFuel();
			boolean boolean2 = this.isLit();
			float float1 = (float)GameTime.getInstance().getWorldAgeHours();
			if (this.LastUpdateTime < 0.0F) {
				this.LastUpdateTime = float1;
			} else if (this.LastUpdateTime > float1) {
				this.LastUpdateTime = float1;
			}

			if (float1 > this.LastUpdateTime) {
				this.MinuteAccumulator += (float1 - this.LastUpdateTime) * 60.0F;
				int int1 = (int)Math.floor((double)this.MinuteAccumulator);
				if (int1 > 0) {
					if (this.isLit()) {
						DebugLog.log(DebugType.Fireplace, "IsoBarbecue burned " + int1 + " minutes (" + this.getFuelAmount() + " remaining)");
						this.useFuel(int1);
						if (!this.hasFuel()) {
							this.extinguish();
						}
					} else if (this.MinutesSinceExtinguished != -1) {
						int int2 = Math.min(int1, SMOULDER_MINUTES - this.MinutesSinceExtinguished);
						DebugLog.log(DebugType.Fireplace, "IsoBarbecue smoldered " + int2 + " minutes (" + this.getFuelAmount() + " remaining)");
						this.MinutesSinceExtinguished += int1;
						this.useFuel(int2);
						if (!this.hasFuel() || this.MinutesSinceExtinguished >= SMOULDER_MINUTES) {
							this.MinutesSinceExtinguished = -1;
						}
					}

					this.MinuteAccumulator -= (float)int1;
				}
			}

			this.LastUpdateTime = float1;
			if (GameServer.bServer) {
				if (boolean1 != this.hasFuel() || boolean2 != this.isLit()) {
					this.sendObjectChange("state");
				}

				return;
			}
		}

		this.updateSprite();
		this.updateHeatSource();
		if (this.isLit() && (this.AttachedAnimSprite == null || this.AttachedAnimSprite.isEmpty())) {
			ColorInfo colorInfo = new ColorInfo(0.95F, 0.95F, 0.85F, 1.0F);
			this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -14, 58, true, 0, false, 0.7F, colorInfo);
			((IsoSpriteInstance)this.AttachedAnimSprite.get(0)).alpha = ((IsoSpriteInstance)this.AttachedAnimSprite.get(0)).targetAlpha = 0.55F;
			((IsoSpriteInstance)this.AttachedAnimSprite.get(0)).bCopyTargetAlpha = false;
		} else if (!this.isLit() && this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
			this.RemoveAttachedAnims();
		}

		if (this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
			int int3 = this.AttachedAnimSprite.size();
			for (int int4 = 0; int4 < int3; ++int4) {
				IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int4);
				IsoSprite sprite = (IsoSprite)this.AttachedAnimSpriteActual.get(int4);
				spriteInstance.update();
				float float2 = GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
				spriteInstance.Frame += spriteInstance.AnimFrameIncrease * float2;
				if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
					spriteInstance.Frame = 0.0F;
				}
			}
		}
	}

	public void setSprite(IsoSprite sprite) {
		this.noTankSprite = sprite;
		this.normalSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (IsoSprite)sprite, -8);
	}

	public void addToWorld() {
		IsoCell cell = this.getCell();
		if (!cell.getProcessIsoObjects().contains(this)) {
			cell.getProcessIsoObjects().add(this);
		}

		this.container.addItemsToProcessItems();
	}

	public void removeFromWorld() {
		if (this.heatSource != null) {
			IsoWorld.instance.CurrentCell.removeHeatSource(this.heatSource);
			this.heatSource = null;
		}

		super.removeFromWorld();
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		if (this.AttachedAnimSpriteActual != null) {
			int int1 = Core.TileScale;
			for (int int2 = 0; int2 < this.AttachedAnimSpriteActual.size(); ++int2) {
				IsoSprite sprite = (IsoSprite)this.AttachedAnimSpriteActual.get(int2);
				sprite.soffX = (short)(14 * int1);
				sprite.soffY = (short)(-58 * int1);
				((IsoSpriteInstance)this.AttachedAnimSprite.get(int2)).setScale((float)int1, (float)int1);
			}
		}

		super.render(float1, float2, float3, colorInfo, boolean1);
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		if ("state".equals(string)) {
			byteBuffer.putInt(this.getFuelAmount());
			byteBuffer.put((byte)(this.isLit() ? 1 : 0));
			byteBuffer.put((byte)(this.hasPropaneTank() ? 1 : 0));
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		if ("state".equals(string)) {
			this.setFuelAmount(byteBuffer.getInt());
			this.setLit(byteBuffer.get() == 1);
			this.bHasPropaneTank = byteBuffer.get() == 1;
		}
	}
}
