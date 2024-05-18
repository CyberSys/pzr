package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class IsoFireplace extends IsoObject {
	int FuelAmount = 0;
	boolean bLit = false;
	protected float LastUpdateTime = -1.0F;
	protected float MinuteAccumulator = 0.0F;
	protected int MinutesSinceExtinguished = -1;
	protected IsoSprite FuelSprite = null;
	protected int FuelSpriteIndex = -1;
	protected int FireSpriteIndex = -1;
	protected IsoLightSource LightSource = null;
	protected IsoHeatSource heatSource = null;
	private static int SMOULDER_MINUTES = 10;

	public IsoFireplace(IsoCell cell) {
		super(cell);
	}

	public IsoFireplace(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
		String string = sprite != null && sprite.getProperties().Is("container") ? sprite.getProperties().Val("container") : "fireplace";
		this.container = new ItemContainer(string, square, this, 1, 1);
		this.container.setExplored(true);
	}

	public String getObjectName() {
		return "Fireplace";
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.FuelAmount = byteBuffer.getInt();
		this.bLit = byteBuffer.get() == 1;
		this.LastUpdateTime = byteBuffer.getFloat();
		this.MinutesSinceExtinguished = byteBuffer.getInt();
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.putInt(this.FuelAmount);
		byteBuffer.put((byte)(this.bLit ? 1 : 0));
		byteBuffer.putFloat(this.LastUpdateTime);
		byteBuffer.putInt(this.MinutesSinceExtinguished);
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

	public void setLit(boolean boolean1) {
		this.bLit = boolean1;
	}

	public boolean isLit() {
		return this.bLit;
	}

	public void extinguish() {
		if (this.isLit()) {
			this.setLit(false);
			if (this.hasFuel()) {
				this.MinutesSinceExtinguished = 0;
			}
		}
	}

	public float getTemperature() {
		return this.isLit() ? 1.8F : 1.0F;
	}

	private void updateFuelSprite() {
		if (this.container == null || !"woodstove".equals(this.container.getType())) {
			if (this.hasFuel()) {
				if (this.FuelSprite == null) {
					this.FuelSprite = IsoSprite.CreateSprite(this.getCell().SpriteManager);
					Texture texture = this.FuelSprite.LoadFrameExplicit("Item_Logs");
				}

				if (this.FuelSpriteIndex == -1) {
					DebugLog.log(DebugType.Fireplace, "fireplace: added fuel sprite");
					this.FuelSpriteIndex = this.AttachedAnimSprite != null ? this.AttachedAnimSprite.size() : 0;
					if (this.getProperties() != null && this.getProperties().Is(IsoFlagType.collideW)) {
						this.AttachExistingAnim(this.FuelSprite, -10 * Core.TileScale, -90 * Core.TileScale, false, 0, false, 0.0F);
					} else {
						this.AttachExistingAnim(this.FuelSprite, -35 * Core.TileScale, -90 * Core.TileScale, false, 0, false, 0.0F);
					}

					if (Core.TileScale == 1) {
						((IsoSpriteInstance)this.AttachedAnimSprite.get(this.FuelSpriteIndex)).setScale(0.5F, 0.5F);
					}
				}
			} else if (this.FuelSpriteIndex != -1) {
				DebugLog.log(DebugType.Fireplace, "fireplace: removed fuel sprite");
				this.AttachedAnimSprite.remove(this.FuelSpriteIndex);
				this.AttachedAnimSpriteActual.remove(this.FuelSpriteIndex);
				if (this.FireSpriteIndex > this.FuelSpriteIndex) {
					--this.FireSpriteIndex;
				}

				this.FuelSpriteIndex = -1;
			}
		}
	}

	private void updateFireSprite() {
		if (this.container == null || !"woodstove".equals(this.container.getType())) {
			if (this.isLit()) {
				if (this.FireSpriteIndex == -1) {
					DebugLog.log(DebugType.Fireplace, "fireplace: added fire sprite");
					this.FireSpriteIndex = this.AttachedAnimSprite != null ? this.AttachedAnimSprite.size() : 0;
					if (this.getProperties() != null && this.getProperties().Is(IsoFlagType.collideW)) {
						this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -11 * Core.TileScale, -84 * Core.TileScale, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
					} else {
						this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -35 * Core.TileScale, -84 * Core.TileScale, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
					}

					if (Core.TileScale == 1) {
						((IsoSpriteInstance)this.AttachedAnimSprite.get(this.FireSpriteIndex)).setScale(0.5F, 0.5F);
					}
				}
			} else if (this.FireSpriteIndex != -1) {
				DebugLog.log(DebugType.Fireplace, "fireplace: removed fire sprite");
				this.AttachedAnimSprite.remove(this.FireSpriteIndex);
				this.AttachedAnimSpriteActual.remove(this.FireSpriteIndex);
				if (this.FuelSpriteIndex > this.FireSpriteIndex) {
					--this.FuelSpriteIndex;
				}

				this.FireSpriteIndex = -1;
			}
		}
	}

	private int calcLightRadius() {
		return (int)GameTime.instance.Lerp(1.0F, 8.0F, (float)Math.min(this.getFuelAmount(), 60) / 60.0F);
	}

	private void updateLightSource() {
		if (this.isLit()) {
			int int1 = this.calcLightRadius();
			if (this.LightSource != null && this.LightSource.getRadius() != int1) {
				this.LightSource.life = 0;
				this.LightSource = null;
			}

			if (this.LightSource == null) {
				this.LightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 1.0F, 0.1F, 0.1F, int1);
				IsoWorld.instance.CurrentCell.addLamppost(this.LightSource);
				IsoGridSquare.RecalcLightTime = -1;
				GameTime.instance.lightSourceUpdate = 100.0F;
			}
		} else if (this.LightSource != null) {
			IsoWorld.instance.CurrentCell.removeLamppost(this.LightSource);
			this.LightSource = null;
		}
	}

	private void updateHeatSource() {
		if (this.isLit()) {
			int int1 = this.calcLightRadius();
			if (this.heatSource == null) {
				this.heatSource = new IsoHeatSource((int)this.getX(), (int)this.getY(), (int)this.getZ(), int1, 35);
				IsoWorld.instance.CurrentCell.addHeatSource(this.heatSource);
			} else if (int1 != this.heatSource.getRadius()) {
				this.heatSource.setRadius(int1);
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
			int int1 = this.calcLightRadius();
			float float1 = (float)GameTime.getInstance().getWorldAgeHours();
			if (this.LastUpdateTime < 0.0F) {
				this.LastUpdateTime = float1;
			} else if (this.LastUpdateTime > float1) {
				this.LastUpdateTime = float1;
			}

			if (float1 > this.LastUpdateTime) {
				this.MinuteAccumulator += (float1 - this.LastUpdateTime) * 60.0F;
				int int2 = (int)Math.floor((double)this.MinuteAccumulator);
				if (int2 > 0) {
					if (this.isLit()) {
						DebugLog.log(DebugType.Fireplace, "IsoFireplace burned " + int2 + " minutes (" + this.getFuelAmount() + " remaining)");
						this.useFuel(int2);
						if (!this.hasFuel()) {
							this.extinguish();
						}
					} else if (this.MinutesSinceExtinguished != -1) {
						int int3 = Math.min(int2, SMOULDER_MINUTES - this.MinutesSinceExtinguished);
						DebugLog.log(DebugType.Fireplace, "IsoFireplace smoldered " + int3 + " minutes (" + this.getFuelAmount() + " remaining)");
						this.MinutesSinceExtinguished += int2;
						this.useFuel(int3);
						if (!this.hasFuel() || this.MinutesSinceExtinguished >= SMOULDER_MINUTES) {
							this.MinutesSinceExtinguished = -1;
						}
					}

					this.MinuteAccumulator -= (float)int2;
				}
			}

			this.LastUpdateTime = float1;
			if (GameServer.bServer) {
				if (boolean1 != this.hasFuel() || boolean2 != this.isLit() || int1 != this.calcLightRadius()) {
					this.sendObjectChange("state");
				}

				return;
			}
		}

		this.updateFuelSprite();
		this.updateFireSprite();
		this.updateLightSource();
		this.updateHeatSource();
		if (this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
			int int4 = this.AttachedAnimSprite.size();
			for (int int5 = 0; int5 < int4; ++int5) {
				IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int5);
				IsoSprite sprite = (IsoSprite)this.AttachedAnimSpriteActual.get(int5);
				spriteInstance.update();
				float float2 = GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
				spriteInstance.Frame += spriteInstance.AnimFrameIncrease * float2;
				if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
					spriteInstance.Frame = 0.0F;
				}
			}
		}
	}

	public void addToWorld() {
		IsoCell cell = this.getCell();
		if (!cell.getProcessIsoObjects().contains(this)) {
			cell.getProcessIsoObjects().add(this);
		}

		this.container.addItemsToProcessItems();
	}

	public void removeFromWorld() {
		if (this.LightSource != null) {
			IsoWorld.instance.CurrentCell.removeLamppost(this.LightSource);
			this.LightSource = null;
		}

		if (this.heatSource != null) {
			IsoWorld.instance.CurrentCell.removeHeatSource(this.heatSource);
			this.heatSource = null;
		}

		super.removeFromWorld();
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		super.render(float1, float2, float3, colorInfo, false);
		if (this.AttachedAnimSprite != null) {
			for (int int1 = 0; int1 < this.AttachedAnimSprite.size(); ++int1) {
				IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int1);
				spriteInstance.getParentSprite().render(spriteInstance, this, float1, float2, float3, this.dir, this.offsetX, this.offsetY, colorInfo, true);
			}
		}
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		if ("state".equals(string)) {
			byteBuffer.putInt(this.getFuelAmount());
			byteBuffer.put((byte)(this.isLit() ? 1 : 0));
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		if ("state".equals(string)) {
			this.setFuelAmount(byteBuffer.getInt());
			this.setLit(byteBuffer.get() == 1);
		}
	}
}
