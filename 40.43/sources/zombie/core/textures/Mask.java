package zombie.core.textures;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.BooleanGrid;
import zombie.core.utils.WrappedBuffer;
import zombie.interfaces.ITexture;


public class Mask implements Serializable,Cloneable {
	private static final long serialVersionUID = -5679205580926696806L;
	private boolean full;
	private int height;
	BooleanGrid mask;
	private int width;

	protected Mask() {
	}

	public Mask(int int1, int int2) {
		this.width = int1;
		this.height = int2;
		this.mask = new BooleanGrid(int1, int2);
		this.full();
	}

	public Mask(Texture texture, Texture texture2, int int1, int int2, int int3, int int4) {
		if (texture.getMask() != null) {
			int3 = texture2.getWidth();
			int4 = texture2.getHeight();
			texture2.setMask(this);
			this.mask = new BooleanGrid(int3, int4);
			for (int int5 = int1; int5 < int1 + int3; ++int5) {
				for (int int6 = int2; int6 < int2 + int4; ++int6) {
					this.mask.setValue(int5 - int1, int6 - int2, texture.getMask().mask.getValue(int5, int6));
				}
			}
		}
	}

	protected Mask(Texture texture, WrappedBuffer wrappedBuffer) {
		this.width = texture.getWidth();
		this.height = texture.getHeight();
		int int1 = texture.getWidthHW();
		int int2 = texture.getHeightHW();
		int int3 = (int)(texture.getXStart() * (float)int1);
		int int4 = (int)(texture.getXEnd() * (float)int1);
		int int5 = (int)(texture.getYStart() * (float)int2);
		int int6 = (int)(texture.getYEnd() * (float)int2);
		this.mask = new BooleanGrid(this.width, this.height);
		texture.setMask(this);
		ByteBuffer byteBuffer = wrappedBuffer.getBuffer();
		byteBuffer.rewind();
		for (int int7 = 0; int7 < texture.getHeightHW(); ++int7) {
			for (int int8 = 0; int8 < texture.getWidthHW(); ++int8) {
				byteBuffer.get();
				byteBuffer.get();
				byteBuffer.get();
				byte byte1 = byteBuffer.get();
				if (int8 >= int3 && int8 < int4 && int7 >= int5 && int7 < int6) {
					if (byte1 == 0) {
						this.mask.setValue(int8 - int3, int7 - int5, false);
						this.full = false;
					} else {
						if (byte1 < 127) {
							this.mask.setValue(int8 - int3, int7 - int5, true);
						}

						this.mask.setValue(int8 - int3, int7 - int5, true);
					}
				}

				if (int7 >= int6) {
					break;
				}
			}
		}

		wrappedBuffer.dispose();
	}

	public Mask(ITexture iTexture, boolean[] booleanArray) {
		this.width = iTexture.getWidth();
		this.height = iTexture.getHeight();
		int int1 = iTexture.getWidthHW();
		int int2 = (int)(iTexture.getXStart() * (float)int1);
		int2 = (int)(iTexture.getXEnd() * (float)int1);
		int int3 = (int)(iTexture.getYStart() * (float)(int1 = iTexture.getHeightHW()));
		int2 = (int)(iTexture.getYEnd() * (float)int1);
		iTexture.setMask(this);
		this.mask = new BooleanGrid(this.width, this.height);
		for (int int4 = 0; int4 < iTexture.getHeight(); ++int4) {
			for (int int5 = 0; int5 < iTexture.getWidth(); ++int5) {
				this.mask.setValue(int5, int4, booleanArray[int4 * iTexture.getWidth() + int5]);
			}
		}
	}

	public Mask(ITexture iTexture) {
		this.width = iTexture.getWidth();
		this.height = iTexture.getHeight();
		int int1 = iTexture.getWidthHW();
		int int2 = (int)(iTexture.getXStart() * (float)int1);
		int int3 = (int)(iTexture.getXEnd() * (float)int1);
		int int4 = (int)(iTexture.getYStart() * (float)(int1 = iTexture.getHeightHW()));
		int int5 = (int)(iTexture.getYEnd() * (float)int1);
		iTexture.setMask(this);
		this.mask = new BooleanGrid(this.width, this.height);
		RenderThread.borrowContext();
		WrappedBuffer wrappedBuffer = iTexture.getData();
		ByteBuffer byteBuffer = wrappedBuffer.getBuffer();
		byteBuffer.rewind();
		for (int int6 = 0; int6 < iTexture.getHeightHW(); ++int6) {
			for (int int7 = 0; int7 < iTexture.getWidthHW(); ++int7) {
				byteBuffer.get();
				byteBuffer.get();
				byteBuffer.get();
				byte byte1 = byteBuffer.get();
				if (int7 >= int2 && int7 < int3 && int6 >= int4 && int6 < int5) {
					if (byte1 == 0) {
						this.mask.setValue(int7 - int2, int6 - int4, false);
						this.full = false;
					} else {
						if (byte1 < 127) {
							this.mask.setValue(int7 - int2, int6 - int4, true);
						} else {
							boolean boolean1 = false;
						}

						this.mask.setValue(int7 - int2, int6 - int4, true);
					}
				}

				if (int6 >= int5) {
					break;
				}
			}
		}

		wrappedBuffer.dispose();
		RenderThread.returnContext();
	}

	public Mask(Mask mask) {
		this.width = mask.width;
		this.height = mask.height;
		this.full = mask.full;
		try {
			this.mask = mask.mask.clone();
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			cloneNotSupportedException.printStackTrace(System.err);
		}
	}

	public Object clone() {
		return new Mask(this);
	}

	public void full() {
		this.mask.fill();
		this.full = true;
	}

	public void set(int int1, int int2, boolean boolean1) {
		this.mask.setValue(int1, int2, boolean1);
		if (!boolean1 && this.full) {
			this.full = false;
		}
	}

	public boolean get(int int1, int int2) {
		return this.full ? true : this.mask.getValue(int1, int2);
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		this.width = objectInputStream.readInt();
		this.height = objectInputStream.readInt();
		this.full = objectInputStream.readBoolean();
		if (objectInputStream.readBoolean()) {
			this.mask = (BooleanGrid)objectInputStream.readObject();
		}
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		objectOutputStream.writeInt(this.width);
		objectOutputStream.writeInt(this.height);
		objectOutputStream.writeBoolean(this.full);
		if (this.mask != null) {
			objectOutputStream.writeBoolean(true);
			objectOutputStream.writeObject(this.mask);
		} else {
			objectOutputStream.writeBoolean(false);
		}
	}

	public void save(String string) {
	}
}
