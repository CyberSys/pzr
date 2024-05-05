package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class BSFurnace extends IsoObject {
	public float heat = 0.0F;
	public float heatDecrease = 0.005F;
	public float heatIncrease = 0.001F;
	public float fuelAmount = 0.0F;
	public float fuelDecrease = 0.001F;
	public boolean fireStarted = false;
	private IsoLightSource LightSource;
	public String sSprite;
	public String sLitSprite;

	public BSFurnace(IsoCell cell) {
		super(cell);
	}

	public BSFurnace(IsoCell cell, IsoGridSquare square, String string, String string2) {
		super(cell, square, IsoSpriteManager.instance.getSprite(string));
		this.sSprite = string;
		this.sLitSprite = string2;
		this.sprite = IsoSpriteManager.instance.getSprite(string);
		this.square = square;
		this.container = new ItemContainer();
		this.container.setType("stonefurnace");
		this.container.setParent(this);
		square.AddSpecialObject(this);
	}

	public void update() {
		this.updateHeat();
		if (!GameClient.bClient) {
			DrainableComboItem drainableComboItem = null;
			InventoryItem inventoryItem = null;
			int int1;
			for (int1 = 0; int1 < this.getContainer().getItems().size(); ++int1) {
				InventoryItem inventoryItem2 = (InventoryItem)this.getContainer().getItems().get(int1);
				if (inventoryItem2.getType().equals("IronIngot") && ((DrainableComboItem)inventoryItem2).getUsedDelta() < 1.0F) {
					drainableComboItem = (DrainableComboItem)inventoryItem2;
				}

				if (inventoryItem2.getMetalValue() > 0.0F) {
					if (this.getHeat() > 15.0F) {
						if (inventoryItem2.getItemHeat() < 2.0F) {
							inventoryItem2.setItemHeat(inventoryItem2.getItemHeat() + 0.001F * (this.getHeat() / 100.0F) * GameTime.instance.getMultiplier());
						} else {
							inventoryItem2.setMeltingTime(inventoryItem2.getMeltingTime() + 0.1F * (this.getHeat() / 100.0F) * (1.0F + (float)(this.getMeltingSkill(inventoryItem2) * 3) / 100.0F) * GameTime.instance.getMultiplier());
						}

						if (inventoryItem2.getMeltingTime() == 100.0F) {
							inventoryItem = inventoryItem2;
						}
					} else {
						inventoryItem2.setItemHeat(inventoryItem2.getItemHeat() - 0.001F * (this.getHeat() / 100.0F) * GameTime.instance.getMultiplier());
						inventoryItem2.setMeltingTime(inventoryItem2.getMeltingTime() - 0.1F * (this.getHeat() / 100.0F) * GameTime.instance.getMultiplier());
					}
				}
			}

			if (inventoryItem != null) {
				if (inventoryItem.getWorker() != null && !inventoryItem.getWorker().isEmpty()) {
					for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
						IsoPlayer player = IsoPlayer.players[int1];
						if (player != null && !player.isDead() && inventoryItem.getWorker().equals(player.getFullName())) {
							break;
						}
					}
				}

				float float1 = inventoryItem.getMetalValue() + (inventoryItem.getMetalValue() * (1.0F + (float)(this.getMeltingSkill(inventoryItem) * 3) / 100.0F) - inventoryItem.getMetalValue());
				if (drainableComboItem != null) {
					if (float1 + drainableComboItem.getUsedDelta() / drainableComboItem.getUseDelta() > 1.0F / drainableComboItem.getUseDelta()) {
						float1 -= 1.0F / drainableComboItem.getUseDelta() - drainableComboItem.getUsedDelta() / drainableComboItem.getUseDelta();
						drainableComboItem.setUsedDelta(1.0F);
						drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.IronIngot");
						drainableComboItem.setUsedDelta(0.0F);
						this.getContainer().addItem(drainableComboItem);
					}
				} else {
					drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.IronIngot");
					drainableComboItem.setUsedDelta(0.0F);
					this.getContainer().addItem(drainableComboItem);
				}

				float float2 = 0.0F;
				float float3 = float1;
				while (float2 < float3) {
					if (drainableComboItem.getUsedDelta() + float1 * drainableComboItem.getUseDelta() <= 1.0F) {
						drainableComboItem.setUsedDelta(drainableComboItem.getUsedDelta() + float1 * drainableComboItem.getUseDelta());
						float2 += float1;
					} else {
						float1 -= 1.0F / drainableComboItem.getUseDelta();
						float2 += 1.0F / drainableComboItem.getUseDelta();
						drainableComboItem.setUsedDelta(1.0F);
						drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.IronIngot");
						drainableComboItem.setUsedDelta(0.0F);
						this.getContainer().addItem(drainableComboItem);
					}
				}

				this.getContainer().Remove(inventoryItem);
			}
		}
	}

	private void updateHeat() {
		if (!this.isFireStarted()) {
			this.heat -= this.heatDecrease * GameTime.instance.getMultiplier();
		} else if (this.getFuelAmount() == 0.0F) {
			this.setFireStarted(false);
		} else {
			this.fuelAmount -= this.fuelDecrease * (0.2F + this.heatIncrease / 80.0F) * GameTime.instance.getMultiplier();
			if (this.getHeat() < 20.0F) {
				this.heat += this.heatIncrease * GameTime.instance.getMultiplier();
			}

			this.heat -= this.heatDecrease * 0.05F * GameTime.instance.getMultiplier();
		}

		if (this.heat < 0.0F) {
			this.heat = 0.0F;
		}

		if (this.fuelAmount < 0.0F) {
			this.fuelAmount = 0.0F;
		}
	}

	public int getMeltingSkill(InventoryItem inventoryItem) {
		if (inventoryItem.getWorker() != null && !inventoryItem.getWorker().isEmpty()) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && !player.isDead() && inventoryItem.getWorker().equals(player.getFullName())) {
					return player.getPerkLevel(PerkFactory.Perks.Melting);
				}
			}
		}

		return 0;
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.fireStarted = byteBuffer.get() == 1;
		this.heat = byteBuffer.getFloat();
		this.fuelAmount = byteBuffer.getFloat();
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.put((byte)(this.isFireStarted() ? 1 : 0));
		byteBuffer.putFloat(this.getHeat());
		byteBuffer.putFloat(this.getFuelAmount());
	}

	public String getObjectName() {
		return "StoneFurnace";
	}

	public float getHeat() {
		return this.heat;
	}

	public void setHeat(float float1) {
		if (float1 > 100.0F) {
			float1 = 100.0F;
		}

		if (float1 < 0.0F) {
			float1 = 0.0F;
		}

		this.heat = float1;
	}

	public boolean isFireStarted() {
		return this.fireStarted;
	}

	public void updateLight() {
		if (this.fireStarted && this.LightSource == null) {
			this.LightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 0.61F, 0.165F, 0.0F, 7);
			IsoWorld.instance.CurrentCell.addLamppost(this.LightSource);
		} else if (this.LightSource != null) {
			IsoWorld.instance.CurrentCell.removeLamppost(this.LightSource);
			this.LightSource = null;
		}
	}

	public void setFireStarted(boolean boolean1) {
		this.fireStarted = boolean1;
		this.updateLight();
		this.syncFurnace();
	}

	public void syncFurnace() {
		if (GameServer.bServer) {
			GameServer.sendFuranceChange(this, (UdpConnection)null);
		} else if (GameClient.bClient) {
			GameClient.sendFurnaceChange(this);
		}
	}

	public float getFuelAmount() {
		return this.fuelAmount;
	}

	public void setFuelAmount(float float1) {
		if (float1 > 100.0F) {
			float1 = 100.0F;
		}

		if (float1 < 0.0F) {
			float1 = 0.0F;
		}

		this.fuelAmount = float1;
	}

	public void addFuel(float float1) {
		this.setFuelAmount(this.getFuelAmount() + float1);
	}

	public void addToWorld() {
		IsoWorld.instance.getCell().addToProcessIsoObject(this);
	}

	public void removeFromWorld() {
		if (this.emitter != null) {
			this.emitter.stopAll();
			IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
			this.emitter = null;
		}

		super.removeFromWorld();
	}

	public float getFuelDecrease() {
		return this.fuelDecrease;
	}

	public void setFuelDecrease(float float1) {
		this.fuelDecrease = float1;
	}

	public float getHeatDecrease() {
		return this.heatDecrease;
	}

	public void setHeatDecrease(float float1) {
		this.heatDecrease = float1;
	}

	public float getHeatIncrease() {
		return this.heatIncrease;
	}

	public void setHeatIncrease(float float1) {
		this.heatIncrease = float1;
	}
}
