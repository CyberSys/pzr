package zombie.vehicles;

import java.nio.ByteBuffer;


public final class ClipperOffset {
	private final long address = this.newInstance();

	private native long newInstance();

	public native void clear();

	public native void addPath(int int1, ByteBuffer byteBuffer, int int2, int int3);

	public native void execute(double double1);

	public native int getPolygonCount();

	public native int getPolygon(int int1, ByteBuffer byteBuffer);

	public static enum EndType {

		etClosedPolygon,
		etClosedLine,
		etOpenButt,
		etOpenSquare,
		etOpenRound;

		private static ClipperOffset.EndType[] $values() {
			return new ClipperOffset.EndType[]{etClosedPolygon, etClosedLine, etOpenButt, etOpenSquare, etOpenRound};
		}
	}
	public static enum JoinType {

		jtSquare,
		jtRound,
		jtMiter;

		private static ClipperOffset.JoinType[] $values() {
			return new ClipperOffset.JoinType[]{jtSquare, jtRound, jtMiter};
		}
	}
}
