package zombie.vehicles;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.scripting.objects.VehicleScript;


public final class VehicleDoor {
	protected VehiclePart part;
	protected boolean open;
	protected boolean locked;
	protected boolean lockBroken;

	public VehicleDoor(VehiclePart vehiclePart) {
		this.part = vehiclePart;
	}

	public void init(VehicleScript.Door door) {
		this.open = false;
		this.locked = false;
		this.lockBroken = false;
	}

	public boolean isOpen() {
		return this.open;
	}

	public void setOpen(boolean boolean1) {
		this.open = boolean1;
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean boolean1) {
		this.locked = boolean1;
	}

	public boolean isLockBroken() {
		return this.lockBroken;
	}

	public void setLockBroken(boolean boolean1) {
		this.lockBroken = boolean1;
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)(this.open ? 1 : 0));
		byteBuffer.put((byte)(this.locked ? 1 : 0));
		byteBuffer.put((byte)(this.lockBroken ? 1 : 0));
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.open = byteBuffer.get() == 1;
		this.locked = byteBuffer.get() == 1;
		this.lockBroken = byteBuffer.get() == 1;
	}
}
