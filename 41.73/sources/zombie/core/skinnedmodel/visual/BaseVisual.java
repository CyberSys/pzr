package zombie.core.skinnedmodel.visual;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.skinnedmodel.model.Model;
import zombie.scripting.objects.ModelScript;


public abstract class BaseVisual {

	public abstract void save(ByteBuffer byteBuffer) throws IOException;

	public abstract void load(ByteBuffer byteBuffer, int int1) throws IOException;

	public abstract Model getModel();

	public abstract ModelScript getModelScript();

	public abstract void clear();

	public abstract void copyFrom(BaseVisual baseVisual);

	public abstract void dressInNamedOutfit(String string, ItemVisuals itemVisuals);
}
