package zombie;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;


public class ZomboidBitFlag {
	EnumSet isoFlagTypeES = EnumSet.noneOf(IsoFlagType.class);

	public ZomboidBitFlag(int int1) {
	}

	public ZomboidBitFlag(ZomboidBitFlag zomboidBitFlag) {
		if (zomboidBitFlag != null) {
			this.isoFlagTypeES.addAll(zomboidBitFlag.isoFlagTypeES);
		}
	}

	public void set(int int1, boolean boolean1) {
		if (int1 < IsoFlagType.MAX.index()) {
			if (boolean1) {
				this.isoFlagTypeES.add(IsoFlagType.fromIndex(int1));
			} else {
				this.isoFlagTypeES.remove(IsoFlagType.fromIndex(int1));
			}
		}
	}

	public void clear() {
		this.isoFlagTypeES.clear();
	}

	public boolean isSet(int int1) {
		return this.isoFlagTypeES.contains(IsoFlagType.fromIndex(int1));
	}

	public boolean isSet(IsoFlagType flagType) {
		return this.isoFlagTypeES.contains(flagType);
	}

	public void set(IsoFlagType flagType, boolean boolean1) {
		if (boolean1) {
			this.isoFlagTypeES.add(flagType);
		} else {
			this.isoFlagTypeES.remove(flagType);
		}
	}

	public boolean isSet(IsoObjectType objectType) {
		return this.isSet(objectType.index());
	}

	public void set(IsoObjectType objectType, boolean boolean1) {
		this.set(objectType.index(), boolean1);
	}

	public void Or(ZomboidBitFlag zomboidBitFlag) {
		this.isoFlagTypeES.addAll(zomboidBitFlag.isoFlagTypeES);
	}

	public void save(DataOutputStream dataOutputStream) throws IOException {
	}

	public void load(DataInputStream dataInputStream) throws IOException {
	}

	public void getFromLong(long long1) {
	}
}
