package zombie.core.Styles;

import java.nio.FloatBuffer;
import org.lwjgl.util.ReadableColor;



public enum AlphaOp {

	PREMULTIPLY,
	KEEP,
	ZERO,
	PREMULT_ALPHA;

	private AlphaOp() {
	}
	public final void op(ReadableColor readableColor, int int1, FloatBuffer floatBuffer) {
		floatBuffer.put(Float.intBitsToFloat(this.calc(readableColor, int1)));
	}
	public final void op(int int1, int int2, FloatBuffer floatBuffer) {
		floatBuffer.put(Float.intBitsToFloat(int1));
	}
	protected abstract int calc(ReadableColor readableColor, int int1);
	AlphaOp(Object object) {
		this();
	}
}
