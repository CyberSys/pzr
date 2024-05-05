package org.lwjglx.util.glu.tessellation;


class Geom {

	private Geom() {
	}

	static double EdgeEval(GLUvertex gLUvertex, GLUvertex gLUvertex2, GLUvertex gLUvertex3) {
		assert VertLeq(gLUvertex, gLUvertex2) && VertLeq(gLUvertex2, gLUvertex3);
		double double1 = gLUvertex2.s - gLUvertex.s;
		double double2 = gLUvertex3.s - gLUvertex2.s;
		if (double1 + double2 > 0.0) {
			return double1 < double2 ? gLUvertex2.t - gLUvertex.t + (gLUvertex.t - gLUvertex3.t) * (double1 / (double1 + double2)) : gLUvertex2.t - gLUvertex3.t + (gLUvertex3.t - gLUvertex.t) * (double2 / (double1 + double2));
		} else {
			return 0.0;
		}
	}

	static double EdgeSign(GLUvertex gLUvertex, GLUvertex gLUvertex2, GLUvertex gLUvertex3) {
		assert VertLeq(gLUvertex, gLUvertex2) && VertLeq(gLUvertex2, gLUvertex3);
		double double1 = gLUvertex2.s - gLUvertex.s;
		double double2 = gLUvertex3.s - gLUvertex2.s;
		return double1 + double2 > 0.0 ? (gLUvertex2.t - gLUvertex3.t) * double1 + (gLUvertex2.t - gLUvertex.t) * double2 : 0.0;
	}

	static double TransEval(GLUvertex gLUvertex, GLUvertex gLUvertex2, GLUvertex gLUvertex3) {
		assert TransLeq(gLUvertex, gLUvertex2) && TransLeq(gLUvertex2, gLUvertex3);
		double double1 = gLUvertex2.t - gLUvertex.t;
		double double2 = gLUvertex3.t - gLUvertex2.t;
		if (double1 + double2 > 0.0) {
			return double1 < double2 ? gLUvertex2.s - gLUvertex.s + (gLUvertex.s - gLUvertex3.s) * (double1 / (double1 + double2)) : gLUvertex2.s - gLUvertex3.s + (gLUvertex3.s - gLUvertex.s) * (double2 / (double1 + double2));
		} else {
			return 0.0;
		}
	}

	static double TransSign(GLUvertex gLUvertex, GLUvertex gLUvertex2, GLUvertex gLUvertex3) {
		assert TransLeq(gLUvertex, gLUvertex2) && TransLeq(gLUvertex2, gLUvertex3);
		double double1 = gLUvertex2.t - gLUvertex.t;
		double double2 = gLUvertex3.t - gLUvertex2.t;
		return double1 + double2 > 0.0 ? (gLUvertex2.s - gLUvertex3.s) * double1 + (gLUvertex2.s - gLUvertex.s) * double2 : 0.0;
	}

	static boolean VertCCW(GLUvertex gLUvertex, GLUvertex gLUvertex2, GLUvertex gLUvertex3) {
		return gLUvertex.s * (gLUvertex2.t - gLUvertex3.t) + gLUvertex2.s * (gLUvertex3.t - gLUvertex.t) + gLUvertex3.s * (gLUvertex.t - gLUvertex2.t) >= 0.0;
	}

	static double Interpolate(double double1, double double2, double double3, double double4) {
		double1 = double1 < 0.0 ? 0.0 : double1;
		double3 = double3 < 0.0 ? 0.0 : double3;
		if (double1 <= double3) {
			return double3 == 0.0 ? (double2 + double4) / 2.0 : double2 + (double4 - double2) * (double1 / (double1 + double3));
		} else {
			return double4 + (double2 - double4) * (double3 / (double1 + double3));
		}
	}

	static void EdgeIntersect(GLUvertex gLUvertex, GLUvertex gLUvertex2, GLUvertex gLUvertex3, GLUvertex gLUvertex4, GLUvertex gLUvertex5) {
		GLUvertex gLUvertex6;
		if (!VertLeq(gLUvertex, gLUvertex2)) {
			gLUvertex6 = gLUvertex;
			gLUvertex = gLUvertex2;
			gLUvertex2 = gLUvertex6;
		}

		if (!VertLeq(gLUvertex3, gLUvertex4)) {
			gLUvertex6 = gLUvertex3;
			gLUvertex3 = gLUvertex4;
			gLUvertex4 = gLUvertex6;
		}

		if (!VertLeq(gLUvertex, gLUvertex3)) {
			gLUvertex6 = gLUvertex;
			gLUvertex = gLUvertex3;
			gLUvertex3 = gLUvertex6;
			gLUvertex6 = gLUvertex2;
			gLUvertex2 = gLUvertex4;
			gLUvertex4 = gLUvertex6;
		}

		double double1;
		double double2;
		if (!VertLeq(gLUvertex3, gLUvertex2)) {
			gLUvertex5.s = (gLUvertex3.s + gLUvertex2.s) / 2.0;
		} else if (VertLeq(gLUvertex2, gLUvertex4)) {
			double1 = EdgeEval(gLUvertex, gLUvertex3, gLUvertex2);
			double2 = EdgeEval(gLUvertex3, gLUvertex2, gLUvertex4);
			if (double1 + double2 < 0.0) {
				double1 = -double1;
				double2 = -double2;
			}

			gLUvertex5.s = Interpolate(double1, gLUvertex3.s, double2, gLUvertex2.s);
		} else {
			double1 = EdgeSign(gLUvertex, gLUvertex3, gLUvertex2);
			double2 = -EdgeSign(gLUvertex, gLUvertex4, gLUvertex2);
			if (double1 + double2 < 0.0) {
				double1 = -double1;
				double2 = -double2;
			}

			gLUvertex5.s = Interpolate(double1, gLUvertex3.s, double2, gLUvertex4.s);
		}

		if (!TransLeq(gLUvertex, gLUvertex2)) {
			gLUvertex6 = gLUvertex;
			gLUvertex = gLUvertex2;
			gLUvertex2 = gLUvertex6;
		}

		if (!TransLeq(gLUvertex3, gLUvertex4)) {
			gLUvertex6 = gLUvertex3;
			gLUvertex3 = gLUvertex4;
			gLUvertex4 = gLUvertex6;
		}

		if (!TransLeq(gLUvertex, gLUvertex3)) {
			gLUvertex6 = gLUvertex3;
			gLUvertex3 = gLUvertex;
			gLUvertex = gLUvertex6;
			gLUvertex6 = gLUvertex4;
			gLUvertex4 = gLUvertex2;
			gLUvertex2 = gLUvertex6;
		}

		if (!TransLeq(gLUvertex3, gLUvertex2)) {
			gLUvertex5.t = (gLUvertex3.t + gLUvertex2.t) / 2.0;
		} else if (TransLeq(gLUvertex2, gLUvertex4)) {
			double1 = TransEval(gLUvertex, gLUvertex3, gLUvertex2);
			double2 = TransEval(gLUvertex3, gLUvertex2, gLUvertex4);
			if (double1 + double2 < 0.0) {
				double1 = -double1;
				double2 = -double2;
			}

			gLUvertex5.t = Interpolate(double1, gLUvertex3.t, double2, gLUvertex2.t);
		} else {
			double1 = TransSign(gLUvertex, gLUvertex3, gLUvertex2);
			double2 = -TransSign(gLUvertex, gLUvertex4, gLUvertex2);
			if (double1 + double2 < 0.0) {
				double1 = -double1;
				double2 = -double2;
			}

			gLUvertex5.t = Interpolate(double1, gLUvertex3.t, double2, gLUvertex4.t);
		}
	}

	static boolean VertEq(GLUvertex gLUvertex, GLUvertex gLUvertex2) {
		return gLUvertex.s == gLUvertex2.s && gLUvertex.t == gLUvertex2.t;
	}

	static boolean VertLeq(GLUvertex gLUvertex, GLUvertex gLUvertex2) {
		return gLUvertex.s < gLUvertex2.s || gLUvertex.s == gLUvertex2.s && gLUvertex.t <= gLUvertex2.t;
	}

	static boolean TransLeq(GLUvertex gLUvertex, GLUvertex gLUvertex2) {
		return gLUvertex.t < gLUvertex2.t || gLUvertex.t == gLUvertex2.t && gLUvertex.s <= gLUvertex2.s;
	}

	static boolean EdgeGoesLeft(GLUhalfEdge gLUhalfEdge) {
		return VertLeq(gLUhalfEdge.Sym.Org, gLUhalfEdge.Org);
	}

	static boolean EdgeGoesRight(GLUhalfEdge gLUhalfEdge) {
		return VertLeq(gLUhalfEdge.Org, gLUhalfEdge.Sym.Org);
	}

	static double VertL1dist(GLUvertex gLUvertex, GLUvertex gLUvertex2) {
		return Math.abs(gLUvertex.s - gLUvertex2.s) + Math.abs(gLUvertex.t - gLUvertex2.t);
	}
}
