package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class IsoCompost extends IsoObject {
	private float compost = 0.0F;
	private float LastUpdated = -1.0F;

	public IsoCompost(IsoCell cell) {
		super(cell);
	}

	public IsoCompost(IsoCell cell, IsoGridSquare square) {
		super(cell, square, IsoSpriteManager.instance.getSprite("camping_01_19"));
		this.sprite = IsoSpriteManager.instance.getSprite("camping_01_19");
		this.square = square;
		this.container = new ItemContainer();
		this.container.setType("crate");
		this.container.setParent(this);
		this.container.bExplored = true;
	}

	public void update() {
		if (!GameClient.bClient && this.container != null) {
			float float1 = (float)GameTime.getInstance().getWorldAgeHours();
			if (this.LastUpdated < 0.0F) {
				this.LastUpdated = float1;
			} else if (this.LastUpdated > float1) {
				this.LastUpdated = float1;
			}

			float float2 = float1 - this.LastUpdated;
			if (!(float2 <= 0.0F)) {
				this.LastUpdated = float1;
				int int1 = SandboxOptions.instance.getCompostHours();
				for (int int2 = 0; int2 < this.container.getItems().size(); ++int2) {
					InventoryItem inventoryItem = (InventoryItem)this.container.getItems().get(int2);
					if (inventoryItem instanceof Food) {
						Food food = (Food)inventoryItem;
						if (GameServer.bServer) {
							food.updateAge();
						}

						if (food.isRotten()) {
							if (this.getCompost() < 100.0F) {
								food.setRottenTime(0.0F);
								food.setCompostTime(food.getCompostTime() + float2);
							}

							if (food.getCompostTime() >= (float)int1) {
								this.setCompost(this.getCompost() + Math.abs(food.getHungChange()) * 2.0F);
								if (this.getCompost() > 100.0F) {
									this.setCompost(100.0F);
								}

								if (GameServer.bServer) {
									GameServer.sendCompost(this, (UdpConnection)null);
									GameServer.sendRemoveItemFromContainer(this.container, inventoryItem);
								}

								if (Rand.Next(10) == 0) {
									InventoryItem inventoryItem2 = this.container.AddItem("Base.Worm");
									if (GameServer.bServer && inventoryItem2 != null) {
										GameServer.sendAddItemToContainer(this.container, inventoryItem2);
									}
								}

								inventoryItem.Use();
								IsoWorld.instance.CurrentCell.addToProcessItemsRemove(inventoryItem);
							}
						}
					}
				}

				this.updateSprite();
			}
		}
	}

	public void updateSprite() {
		if (this.getCompost() >= 10.0F && this.sprite.getName().equals("camping_01_19")) {
			this.sprite = IsoSpriteManager.instance.getSprite("camping_01_20");
			this.transmitUpdatedSpriteToClients();
		} else if (this.getCompost() < 10.0F && this.sprite.getName().equals("camping_01_20")) {
			this.sprite = IsoSpriteManager.instance.getSprite("camping_01_19");
			this.transmitUpdatedSpriteToClients();
		}
	}

	public void syncCompost() {
		if (GameClient.bClient) {
			GameClient.sendCompost(this);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.compost = byteBuffer.getFloat();
		if (int1 >= 130) {
			this.LastUpdated = byteBuffer.getFloat();
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.putFloat(this.compost);
		byteBuffer.putFloat(this.LastUpdated);
	}

	public String getObjectName() {
		return "IsoCompost";
	}

	public float getCompost() {
		return this.compost;
	}

	public void setCompost(float float1) {
		this.compost = PZMath.clamp(float1, 0.0F, 100.0F);
	}

	public void remove() {
		if (this.getSquare() != null) {
			this.getSquare().transmitRemoveItemFromSquare(this);
		}
	}

	public void addToWorld() {
		this.getCell().addToProcessIsoObject(this);
	}
}
