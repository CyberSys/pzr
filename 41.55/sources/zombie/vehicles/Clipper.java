package zombie.vehicles;

import java.nio.ByteBuffer;
import zombie.debug.DebugLog;


public class Clipper {
	private long address;
	final ByteBuffer bb = ByteBuffer.allocateDirect(64);

	public static void init() {
		String string = "";
		if ("1".equals(System.getProperty("zomboid.debuglibs.clipper"))) {
			DebugLog.log("***** Loading debug version of PZClipper");
			string = "d";
		}

		if (System.getProperty("os.name").contains("OS X")) {
			System.loadLibrary("PZClipper");
		} else if (System.getProperty("sun.arch.data.model").equals("64")) {
			System.loadLibrary("PZClipper64" + string);
		} else {
			System.loadLibrary("PZClipper32" + string);
		}

		n_init();
	}

	public Clipper() {
		this.newInstance();
	}

	private native void newInstance();

	public native void clear();

	public native void addPath(int int1, ByteBuffer byteBuffer, boolean boolean1);

	public native void addLine(float float1, float float2, float float3, float float4);

	public native void addAABB(float float1, float float2, float float3, float float4);

	public void addAABBBevel(float float1, float float2, float float3, float float4, float float5) {
		this.bb.clear();
		this.bb.putFloat(float1 + float5);
		this.bb.putFloat(float2);
		this.bb.putFloat(float3 - float5);
		this.bb.putFloat(float2);
		this.bb.putFloat(float3);
		this.bb.putFloat(float2 + float5);
		this.bb.putFloat(float3);
		this.bb.putFloat(float4 - float5);
		this.bb.putFloat(float3 - float5);
		this.bb.putFloat(float4);
		this.bb.putFloat(float1 + float5);
		this.bb.putFloat(float4);
		this.bb.putFloat(float1);
		this.bb.putFloat(float4 - float5);
		this.bb.putFloat(float1);
		this.bb.putFloat(float2 + float5);
		this.addPath(this.bb.position() / 4 / 2, this.bb, false);
	}

	public native void addPolygon(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8);

	public native void clipAABB(float float1, float float2, float float3, float float4);

	public native int generatePolygons();

	public native int getPolygon(int int1, ByteBuffer byteBuffer);

	public native int generateTriangulatePolygons(int int1, int int2);

	public native int triangulate(int int1, ByteBuffer byteBuffer);

	public static native void n_init();

	private static void writeToStdErr(String string) {
		System.err.println(string);
	}
}
