package org.lwjglx.util.glu;


public interface GLUtessellator {

	void gluDeleteTess();

	void gluTessProperty(int int1, double double1);

	void gluGetTessProperty(int int1, double[] doubleArray, int int2);

	void gluTessNormal(double double1, double double2, double double3);

	void gluTessCallback(int int1, GLUtessellatorCallback gLUtessellatorCallback);

	void gluTessVertex(double[] doubleArray, int int1, Object object);

	void gluTessBeginPolygon(Object object);

	void gluTessBeginContour();

	void gluTessEndContour();

	void gluTessEndPolygon();

	void gluBeginPolygon();

	void gluNextContour(int int1);

	void gluEndPolygon();
}
