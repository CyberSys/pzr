package zombie.interfaces;

import java.nio.ByteBuffer;
import zombie.core.textures.Mask;
import zombie.core.utils.WrappedBuffer;


public interface ITexture extends IDestroyable,IMaskerable {

	void bind();

	void bind(int int1);

	WrappedBuffer getData();

	int getHeight();

	int getHeightHW();

	int getID();

	int getWidth();

	int getWidthHW();

	float getXEnd();

	float getXStart();

	float getYEnd();

	float getYStart();

	boolean isSolid();

	void makeTransp(int int1, int int2, int int3);

	void setAlphaForeach(int int1, int int2, int int3, int int4);

	void setData(ByteBuffer byteBuffer);

	void setMask(Mask mask);

	void setRegion(int int1, int int2, int int3, int int4);
}
