package zombie.core.skinnedmodel.visual;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.skinnedmodel.model.ModelInstance;


public abstract class BaseVisual {

	public abstract void save(ByteBuffer byteBuffer) throws IOException;

	public abstract void load(ByteBuffer byteBuffer, int int1) throws IOException;

	public abstract ModelInstance createModelInstance();
}
