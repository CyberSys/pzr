package zombie.vehicles;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.joml.Vector3f;


public class VehicleLight {
	public boolean active;
	public Vector3f offset = new Vector3f();
	public float dist = 16.0F;
	public float intensity = 1.0F;
	public float dot = 0.96F;
	public int focusing = 0;

	public boolean getActive() {
		return this.active;
	}

	public void setActive(boolean boolean1) {
		this.active = boolean1;
	}

	public int getFocusing() {
		return this.focusing;
	}

	public float getIntensity() {
		return this.intensity;
	}

	public float getDistanization() {
		return this.dist;
	}

	public boolean canFocusingUp() {
		return this.focusing != 0;
	}

	public boolean canFocusingDown() {
		return this.focusing != 1;
	}

	public void setFocusingUp() {
		if (this.focusing != 0) {
			if (this.focusing < 4) {
				this.focusing = 4;
			} else if (this.focusing < 10) {
				this.focusing = 10;
			} else if (this.focusing < 30) {
				this.focusing = 30;
			} else if (this.focusing < 100) {
				this.focusing = 100;
			} else {
				this.focusing = 0;
			}
		}
	}

	public void setFocusingDown() {
		if (this.focusing != 1) {
			if (this.focusing == 0) {
				this.focusing = 100;
			} else if (this.focusing > 30) {
				this.focusing = 30;
			} else if (this.focusing > 10) {
				this.focusing = 10;
			} else if (this.focusing > 4) {
				this.focusing = 4;
			} else {
				this.focusing = 1;
			}
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)(this.active ? 1 : 0));
		byteBuffer.putFloat(this.offset.x);
		byteBuffer.putFloat(this.offset.y);
		byteBuffer.putFloat(this.intensity);
		byteBuffer.putFloat(this.dist);
		byteBuffer.putInt(this.focusing);
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.active = byteBuffer.get() == 1;
		if (int1 >= 135) {
			this.offset.x = byteBuffer.getFloat();
			this.offset.y = byteBuffer.getFloat();
			this.intensity = byteBuffer.getFloat();
			this.dist = byteBuffer.getFloat();
			this.focusing = byteBuffer.getInt();
		}
	}
}
