package zombie.core.textures;

import java.nio.ByteBuffer;
import zombie.core.utils.DirectBufferAllocator;
import zombie.core.utils.WrappedBuffer;


public final class MipMapLevel {
	public final int width;
	public final int height;
	public final WrappedBuffer data;

	public MipMapLevel(int int1, int int2) {
		this.width = int1;
		this.height = int2;
		this.data = DirectBufferAllocator.allocate(int1 * int2 * 4);
	}

	public MipMapLevel(int int1, int int2, WrappedBuffer wrappedBuffer) {
		this.width = int1;
		this.height = int2;
		this.data = wrappedBuffer;
	}

	public void dispose() {
		if (this.data != null) {
			this.data.dispose();
		}
	}

	public boolean isDisposed() {
		return this.data != null && this.data.isDisposed();
	}

	public void rewind() {
		if (this.data != null) {
			this.data.getBuffer().rewind();
		}
	}

	public ByteBuffer getBuffer() {
		return this.data == null ? null : this.data.getBuffer();
	}

	public int getDataSize() {
		return this.width * this.height * 4;
	}
}
