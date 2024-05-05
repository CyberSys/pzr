package zombie.vehicles;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoGridSquare;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.objects.VehicleScript;


public final class VehicleWindow {
	protected VehiclePart part;
	protected int health;
	protected boolean openable;
	protected boolean open;
	protected float openDelta = 0.0F;

	VehicleWindow(VehiclePart vehiclePart) {
		this.part = vehiclePart;
	}

	public void init(VehicleScript.Window window) {
		this.health = 100;
		this.openable = window.openable;
		this.open = false;
	}

	public int getHealth() {
		return this.part.getCondition();
	}

	public void setHealth(int int1) {
		int1 = Math.max(int1, 0);
		int1 = Math.min(int1, 100);
		this.health = int1;
	}

	public boolean isDestroyed() {
		return this.getHealth() == 0;
	}

	public boolean isOpenable() {
		return this.openable;
	}

	public boolean isOpen() {
		return this.open;
	}

	public void setOpen(boolean boolean1) {
		this.open = boolean1;
		this.part.getVehicle().bDoDamageOverlay = true;
	}

	public void setOpenDelta(float float1) {
		this.openDelta = float1;
	}

	public float getOpenDelta() {
		return this.openDelta;
	}

	public boolean isHittable() {
		if (this.isDestroyed()) {
			return false;
		} else if (this.isOpen()) {
			return false;
		} else {
			return this.part.getItemType() == null || this.part.getInventoryItem() != null;
		}
	}

	public void hit(IsoGameCharacter gameCharacter) {
		this.damage(this.getHealth());
		this.part.setCondition(0);
	}

	public void damage(int int1) {
		if (int1 > 0) {
			if (this.isHittable()) {
				if (GameClient.bClient) {
					GameClient.instance.sendClientCommandV((IsoPlayer)null, "vehicle", "damageWindow", "vehicle", this.part.vehicle.getId(), "part", this.part.getId(), "amount", int1);
				} else {
					if (this.part.getVehicle().isAlarmed()) {
						this.part.getVehicle().triggerAlarm();
					}

					this.part.setCondition(this.part.getCondition() - int1);
					if (this.isDestroyed()) {
						if (this.part.getInventoryItem() != null) {
							this.part.setInventoryItem((InventoryItem)null);
							this.part.getVehicle().transmitPartItem(this.part);
						}

						IsoGridSquare square = this.part.vehicle.square;
						if (GameServer.bServer) {
							GameServer.PlayWorldSoundServer("SmashWindow", false, square, 0.2F, 20.0F, 1.1F, true);
						} else {
							SoundManager.instance.PlayWorldSound("SmashWindow", square, 0.2F, 20.0F, 1.0F, true);
						}

						this.part.getVehicle().getSquare().addBrokenGlass();
						WorldSoundManager.instance.addSound((Object)null, square.getX(), square.getY(), square.getZ(), 10, 20, true, 4.0F, 15.0F);
					}

					this.part.getVehicle().transmitPartWindow(this.part);
				}
			}
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)this.part.getCondition());
		byteBuffer.put((byte)(this.open ? 1 : 0));
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.part.setCondition(byteBuffer.get());
		this.health = this.part.getCondition();
		this.open = byteBuffer.get() == 1;
		this.openDelta = this.open ? 1.0F : 0.0F;
	}
}
