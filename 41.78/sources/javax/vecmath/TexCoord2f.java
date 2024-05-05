package javax.vecmath;

import java.io.Serializable;


public class TexCoord2f extends Tuple2f implements Serializable {
	static final long serialVersionUID = 7998248474800032487L;

	public TexCoord2f(float float1, float float2) {
		super(float1, float2);
	}

	public TexCoord2f(float[] floatArray) {
		super(floatArray);
	}

	public TexCoord2f(TexCoord2f texCoord2f) {
		super((Tuple2f)texCoord2f);
	}

	public TexCoord2f(Tuple2f tuple2f) {
		super(tuple2f);
	}

	public TexCoord2f() {
	}
}
