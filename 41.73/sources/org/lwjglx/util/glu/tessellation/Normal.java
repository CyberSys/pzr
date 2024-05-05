package org.lwjglx.util.glu.tessellation;


class Normal {
	static boolean SLANTED_SWEEP;
	static double S_UNIT_X;
	static double S_UNIT_Y;
	private static final boolean TRUE_PROJECT = false;

	private Normal() {
	}

	private static double Dot(double[] doubleArray, double[] doubleArray2) {
		return doubleArray[0] * doubleArray2[0] + doubleArray[1] * doubleArray2[1] + doubleArray[2] * doubleArray2[2];
	}

	static void Normalize(double[] doubleArray) {
		double double1 = doubleArray[0] * doubleArray[0] + doubleArray[1] * doubleArray[1] + doubleArray[2] * doubleArray[2];
		assert double1 > 0.0;
		double1 = Math.sqrt(double1);
		doubleArray[0] /= double1;
		doubleArray[1] /= double1;
		doubleArray[2] /= double1;
	}

	static int LongAxis(double[] doubleArray) {
		byte byte1 = 0;
		if (Math.abs(doubleArray[1]) > Math.abs(doubleArray[0])) {
			byte1 = 1;
		}

		if (Math.abs(doubleArray[2]) > Math.abs(doubleArray[byte1])) {
			byte1 = 2;
		}

		return byte1;
	}

	static void ComputeNormal(GLUtessellatorImpl gLUtessellatorImpl, double[] doubleArray) {
		GLUvertex gLUvertex = gLUtessellatorImpl.mesh.vHead;
		double[] doubleArray2 = new double[3];
		double[] doubleArray3 = new double[3];
		GLUvertex[] gLUvertexArray = new GLUvertex[3];
		GLUvertex[] gLUvertexArray2 = new GLUvertex[3];
		double[] doubleArray4 = new double[3];
		double[] doubleArray5 = new double[3];
		double[] doubleArray6 = new double[3];
		doubleArray2[0] = doubleArray2[1] = doubleArray2[2] = -2.0E150;
		doubleArray3[0] = doubleArray3[1] = doubleArray3[2] = 2.0E150;
		GLUvertex gLUvertex2;
		for (gLUvertex2 = gLUvertex.next; gLUvertex2 != gLUvertex; gLUvertex2 = gLUvertex2.next) {
			for (int int1 = 0; int1 < 3; ++int1) {
				double double1 = gLUvertex2.coords[int1];
				if (double1 < doubleArray3[int1]) {
					doubleArray3[int1] = double1;
					gLUvertexArray[int1] = gLUvertex2;
				}

				if (double1 > doubleArray2[int1]) {
					doubleArray2[int1] = double1;
					gLUvertexArray2[int1] = gLUvertex2;
				}
			}
		}

		byte byte1 = 0;
		if (doubleArray2[1] - doubleArray3[1] > doubleArray2[0] - doubleArray3[0]) {
			byte1 = 1;
		}

		if (doubleArray2[2] - doubleArray3[2] > doubleArray2[byte1] - doubleArray3[byte1]) {
			byte1 = 2;
		}

		if (doubleArray3[byte1] >= doubleArray2[byte1]) {
			doubleArray[0] = 0.0;
			doubleArray[1] = 0.0;
			doubleArray[2] = 1.0;
		} else {
			double double2 = 0.0;
			GLUvertex gLUvertex3 = gLUvertexArray[byte1];
			GLUvertex gLUvertex4 = gLUvertexArray2[byte1];
			doubleArray4[0] = gLUvertex3.coords[0] - gLUvertex4.coords[0];
			doubleArray4[1] = gLUvertex3.coords[1] - gLUvertex4.coords[1];
			doubleArray4[2] = gLUvertex3.coords[2] - gLUvertex4.coords[2];
			for (gLUvertex2 = gLUvertex.next; gLUvertex2 != gLUvertex; gLUvertex2 = gLUvertex2.next) {
				doubleArray5[0] = gLUvertex2.coords[0] - gLUvertex4.coords[0];
				doubleArray5[1] = gLUvertex2.coords[1] - gLUvertex4.coords[1];
				doubleArray5[2] = gLUvertex2.coords[2] - gLUvertex4.coords[2];
				doubleArray6[0] = doubleArray4[1] * doubleArray5[2] - doubleArray4[2] * doubleArray5[1];
				doubleArray6[1] = doubleArray4[2] * doubleArray5[0] - doubleArray4[0] * doubleArray5[2];
				doubleArray6[2] = doubleArray4[0] * doubleArray5[1] - doubleArray4[1] * doubleArray5[0];
				double double3 = doubleArray6[0] * doubleArray6[0] + doubleArray6[1] * doubleArray6[1] + doubleArray6[2] * doubleArray6[2];
				if (double3 > double2) {
					double2 = double3;
					doubleArray[0] = doubleArray6[0];
					doubleArray[1] = doubleArray6[1];
					doubleArray[2] = doubleArray6[2];
				}
			}

			if (double2 <= 0.0) {
				doubleArray[0] = doubleArray[1] = doubleArray[2] = 0.0;
				doubleArray[LongAxis(doubleArray4)] = 1.0;
			}
		}
	}

	static void CheckOrientation(GLUtessellatorImpl gLUtessellatorImpl) {
		GLUface gLUface = gLUtessellatorImpl.mesh.fHead;
		GLUvertex gLUvertex = gLUtessellatorImpl.mesh.vHead;
		double double1 = 0.0;
		for (GLUface gLUface2 = gLUface.next; gLUface2 != gLUface; gLUface2 = gLUface2.next) {
			GLUhalfEdge gLUhalfEdge = gLUface2.anEdge;
			if (gLUhalfEdge.winding > 0) {
				do {
					double1 += (gLUhalfEdge.Org.s - gLUhalfEdge.Sym.Org.s) * (gLUhalfEdge.Org.t + gLUhalfEdge.Sym.Org.t);
					gLUhalfEdge = gLUhalfEdge.Lnext;
				}		 while (gLUhalfEdge != gLUface2.anEdge);
			}
		}

		if (double1 < 0.0) {
			for (GLUvertex gLUvertex2 = gLUvertex.next; gLUvertex2 != gLUvertex; gLUvertex2 = gLUvertex2.next) {
				gLUvertex2.t = -gLUvertex2.t;
			}

			gLUtessellatorImpl.tUnit[0] = -gLUtessellatorImpl.tUnit[0];
			gLUtessellatorImpl.tUnit[1] = -gLUtessellatorImpl.tUnit[1];
			gLUtessellatorImpl.tUnit[2] = -gLUtessellatorImpl.tUnit[2];
		}
	}

	public static void __gl_projectPolygon(GLUtessellatorImpl gLUtessellatorImpl) {
		GLUvertex gLUvertex = gLUtessellatorImpl.mesh.vHead;
		double[] doubleArray = new double[3];
		boolean boolean1 = false;
		doubleArray[0] = gLUtessellatorImpl.normal[0];
		doubleArray[1] = gLUtessellatorImpl.normal[1];
		doubleArray[2] = gLUtessellatorImpl.normal[2];
		if (doubleArray[0] == 0.0 && doubleArray[1] == 0.0 && doubleArray[2] == 0.0) {
			ComputeNormal(gLUtessellatorImpl, doubleArray);
			boolean1 = true;
		}

		double[] doubleArray2 = gLUtessellatorImpl.sUnit;
		double[] doubleArray3 = gLUtessellatorImpl.tUnit;
		int int1 = LongAxis(doubleArray);
		doubleArray2[int1] = 0.0;
		doubleArray2[(int1 + 1) % 3] = S_UNIT_X;
		doubleArray2[(int1 + 2) % 3] = S_UNIT_Y;
		doubleArray3[int1] = 0.0;
		doubleArray3[(int1 + 1) % 3] = doubleArray[int1] > 0.0 ? -S_UNIT_Y : S_UNIT_Y;
		doubleArray3[(int1 + 2) % 3] = doubleArray[int1] > 0.0 ? S_UNIT_X : -S_UNIT_X;
		for (GLUvertex gLUvertex2 = gLUvertex.next; gLUvertex2 != gLUvertex; gLUvertex2 = gLUvertex2.next) {
			gLUvertex2.s = Dot(gLUvertex2.coords, doubleArray2);
			gLUvertex2.t = Dot(gLUvertex2.coords, doubleArray3);
		}

		if (boolean1) {
			CheckOrientation(gLUtessellatorImpl);
		}
	}

	static  {
	if (SLANTED_SWEEP) {
		S_UNIT_X = 0.5094153956495538;
		S_UNIT_Y = 0.8605207462201063;
	} else {
		S_UNIT_X = 1.0;
		S_UNIT_Y = 0.0;
	}
	}
}
