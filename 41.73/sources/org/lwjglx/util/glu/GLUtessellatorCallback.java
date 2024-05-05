package org.lwjglx.util.glu;


public interface GLUtessellatorCallback {

	void begin(int int1);

	void beginData(int int1, Object object);

	void edgeFlag(boolean boolean1);

	void edgeFlagData(boolean boolean1, Object object);

	void vertex(Object object);

	void vertexData(Object object, Object object2);

	void end();

	void endData(Object object);

	void combine(double[] doubleArray, Object[] objectArray, float[] floatArray, Object[] objectArray2);

	void combineData(double[] doubleArray, Object[] objectArray, float[] floatArray, Object[] objectArray2, Object object);

	void error(int int1);

	void errorData(int int1, Object object);
}
