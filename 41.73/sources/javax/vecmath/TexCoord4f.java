package javax.vecmath;

import java.io.Serializable;


public class TexCoord4f extends Tuple4f implements Serializable {
	static final long serialVersionUID = -3517736544731446513L;

	public TexCoord4f(float float1, float float2, float float3, float float4) {
		super(float1, float2, float3, float4);
	}

	public TexCoord4f(float[] floatArray) {
		super(floatArray);
	}

	public TexCoord4f(TexCoord4f texCoord4f) {
		super((Tuple4f)texCoord4f);
	}

	public TexCoord4f(Tuple4f tuple4f) {
		super(tuple4f);
	}

	public TexCoord4f(Tuple4d tuple4d) {
		super(tuple4d);
	}

	public TexCoord4f() {
	}
}
