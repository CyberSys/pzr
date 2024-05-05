package org.lwjglx.util.glu.tessellation;


class TessMono {

	static boolean __gl_meshTessellateMonoRegion(GLUface gLUface) {
		GLUhalfEdge gLUhalfEdge = gLUface.anEdge;
		assert gLUhalfEdge.Lnext != gLUhalfEdge && gLUhalfEdge.Lnext.Lnext != gLUhalfEdge;
		while (Geom.VertLeq(gLUhalfEdge.Sym.Org, gLUhalfEdge.Org)) {
			gLUhalfEdge = gLUhalfEdge.Onext.Sym;
		}

		while (Geom.VertLeq(gLUhalfEdge.Org, gLUhalfEdge.Sym.Org)) {
			gLUhalfEdge = gLUhalfEdge.Lnext;
		}

		GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge.Onext.Sym;
		while (true) {
			GLUhalfEdge gLUhalfEdge3;
			while (gLUhalfEdge.Lnext != gLUhalfEdge2) {
				if (Geom.VertLeq(gLUhalfEdge.Sym.Org, gLUhalfEdge2.Org)) {
					while (gLUhalfEdge2.Lnext != gLUhalfEdge && (Geom.EdgeGoesLeft(gLUhalfEdge2.Lnext) || Geom.EdgeSign(gLUhalfEdge2.Org, gLUhalfEdge2.Sym.Org, gLUhalfEdge2.Lnext.Sym.Org) <= 0.0)) {
						gLUhalfEdge3 = Mesh.__gl_meshConnect(gLUhalfEdge2.Lnext, gLUhalfEdge2);
						if (gLUhalfEdge3 == null) {
							return false;
						}

						gLUhalfEdge2 = gLUhalfEdge3.Sym;
					}

					gLUhalfEdge2 = gLUhalfEdge2.Onext.Sym;
				} else {
					while (gLUhalfEdge2.Lnext != gLUhalfEdge && (Geom.EdgeGoesRight(gLUhalfEdge.Onext.Sym) || Geom.EdgeSign(gLUhalfEdge.Sym.Org, gLUhalfEdge.Org, gLUhalfEdge.Onext.Sym.Org) >= 0.0)) {
						gLUhalfEdge3 = Mesh.__gl_meshConnect(gLUhalfEdge, gLUhalfEdge.Onext.Sym);
						if (gLUhalfEdge3 == null) {
							return false;
						}

						gLUhalfEdge = gLUhalfEdge3.Sym;
					}

					gLUhalfEdge = gLUhalfEdge.Lnext;
				}
			}

			assert gLUhalfEdge2.Lnext != gLUhalfEdge;
			while (gLUhalfEdge2.Lnext.Lnext != gLUhalfEdge) {
				gLUhalfEdge3 = Mesh.__gl_meshConnect(gLUhalfEdge2.Lnext, gLUhalfEdge2);
				if (gLUhalfEdge3 == null) {
					return false;
				}

				gLUhalfEdge2 = gLUhalfEdge3.Sym;
			}

			return true;
		}
	}

	public static boolean __gl_meshTessellateInterior(GLUmesh gLUmesh) {
		GLUface gLUface;
		for (GLUface gLUface2 = gLUmesh.fHead.next; gLUface2 != gLUmesh.fHead; gLUface2 = gLUface) {
			gLUface = gLUface2.next;
			if (gLUface2.inside && !__gl_meshTessellateMonoRegion(gLUface2)) {
				return false;
			}
		}

		return true;
	}

	public static void __gl_meshDiscardExterior(GLUmesh gLUmesh) {
		GLUface gLUface;
		for (GLUface gLUface2 = gLUmesh.fHead.next; gLUface2 != gLUmesh.fHead; gLUface2 = gLUface) {
			gLUface = gLUface2.next;
			if (!gLUface2.inside) {
				Mesh.__gl_meshZapFace(gLUface2);
			}
		}
	}

	public static boolean __gl_meshSetWindingNumber(GLUmesh gLUmesh, int int1, boolean boolean1) {
		GLUhalfEdge gLUhalfEdge;
		for (GLUhalfEdge gLUhalfEdge2 = gLUmesh.eHead.next; gLUhalfEdge2 != gLUmesh.eHead; gLUhalfEdge2 = gLUhalfEdge) {
			gLUhalfEdge = gLUhalfEdge2.next;
			if (gLUhalfEdge2.Sym.Lface.inside != gLUhalfEdge2.Lface.inside) {
				gLUhalfEdge2.winding = gLUhalfEdge2.Lface.inside ? int1 : -int1;
			} else if (!boolean1) {
				gLUhalfEdge2.winding = 0;
			} else if (!Mesh.__gl_meshDelete(gLUhalfEdge2)) {
				return false;
			}
		}

		return true;
	}
}
