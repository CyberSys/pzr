package javax.vecmath;

import java.io.Serializable;


public class TexCoord3f extends Tuple3f implements Serializable {
	static final long serialVersionUID = -3517736544731446513L;

	public TexCoord3f(float float1, float float2, float float3) {
		super(float1, float2, float3);
	}

	public TexCoord3f(float[] floatArray) {
		super(floatArray);
	}

	public TexCoord3f(TexCoord3f texCoord3f) {
		super((Tuple3f)texCoord3f);
	}

	public TexCoord3f(Tuple3f tuple3f) {
		super(tuple3f);
	}

	public TexCoord3f(Tuple3d tuple3d) {
		super(tuple3d);
	}

	public TexCoord3f() {
	}
}
