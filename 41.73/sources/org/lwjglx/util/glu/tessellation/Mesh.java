package org.lwjglx.util.glu.tessellation;


class Mesh {

	private Mesh() {
	}

	static GLUhalfEdge MakeEdge(GLUhalfEdge gLUhalfEdge) {
		GLUhalfEdge gLUhalfEdge2 = new GLUhalfEdge(true);
		GLUhalfEdge gLUhalfEdge3 = new GLUhalfEdge(false);
		if (!gLUhalfEdge.first) {
			gLUhalfEdge = gLUhalfEdge.Sym;
		}

		GLUhalfEdge gLUhalfEdge4 = gLUhalfEdge.Sym.next;
		gLUhalfEdge3.next = gLUhalfEdge4;
		gLUhalfEdge4.Sym.next = gLUhalfEdge2;
		gLUhalfEdge2.next = gLUhalfEdge;
		gLUhalfEdge.Sym.next = gLUhalfEdge3;
		gLUhalfEdge2.Sym = gLUhalfEdge3;
		gLUhalfEdge2.Onext = gLUhalfEdge2;
		gLUhalfEdge2.Lnext = gLUhalfEdge3;
		gLUhalfEdge2.Org = null;
		gLUhalfEdge2.Lface = null;
		gLUhalfEdge2.winding = 0;
		gLUhalfEdge2.activeRegion = null;
		gLUhalfEdge3.Sym = gLUhalfEdge2;
		gLUhalfEdge3.Onext = gLUhalfEdge3;
		gLUhalfEdge3.Lnext = gLUhalfEdge2;
		gLUhalfEdge3.Org = null;
		gLUhalfEdge3.Lface = null;
		gLUhalfEdge3.winding = 0;
		gLUhalfEdge3.activeRegion = null;
		return gLUhalfEdge2;
	}

	static void Splice(GLUhalfEdge gLUhalfEdge, GLUhalfEdge gLUhalfEdge2) {
		GLUhalfEdge gLUhalfEdge3 = gLUhalfEdge.Onext;
		GLUhalfEdge gLUhalfEdge4 = gLUhalfEdge2.Onext;
		gLUhalfEdge3.Sym.Lnext = gLUhalfEdge2;
		gLUhalfEdge4.Sym.Lnext = gLUhalfEdge;
		gLUhalfEdge.Onext = gLUhalfEdge4;
		gLUhalfEdge2.Onext = gLUhalfEdge3;
	}

	static void MakeVertex(GLUvertex gLUvertex, GLUhalfEdge gLUhalfEdge, GLUvertex gLUvertex2) {
		GLUvertex gLUvertex3 = gLUvertex;
		assert gLUvertex != null;
		GLUvertex gLUvertex4 = gLUvertex2.prev;
		gLUvertex.prev = gLUvertex4;
		gLUvertex4.next = gLUvertex;
		gLUvertex.next = gLUvertex2;
		gLUvertex2.prev = gLUvertex;
		gLUvertex.anEdge = gLUhalfEdge;
		gLUvertex.data = null;
		GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge;
		do {
			gLUhalfEdge2.Org = gLUvertex3;
			gLUhalfEdge2 = gLUhalfEdge2.Onext;
		} while (gLUhalfEdge2 != gLUhalfEdge);
	}

	static void MakeFace(GLUface gLUface, GLUhalfEdge gLUhalfEdge, GLUface gLUface2) {
		GLUface gLUface3 = gLUface;
		assert gLUface != null;
		GLUface gLUface4 = gLUface2.prev;
		gLUface.prev = gLUface4;
		gLUface4.next = gLUface;
		gLUface.next = gLUface2;
		gLUface2.prev = gLUface;
		gLUface.anEdge = gLUhalfEdge;
		gLUface.data = null;
		gLUface.trail = null;
		gLUface.marked = false;
		gLUface.inside = gLUface2.inside;
		GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge;
		do {
			gLUhalfEdge2.Lface = gLUface3;
			gLUhalfEdge2 = gLUhalfEdge2.Lnext;
		} while (gLUhalfEdge2 != gLUhalfEdge);
	}

	static void KillEdge(GLUhalfEdge gLUhalfEdge) {
		if (!gLUhalfEdge.first) {
			gLUhalfEdge = gLUhalfEdge.Sym;
		}

		GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge.next;
		GLUhalfEdge gLUhalfEdge3 = gLUhalfEdge.Sym.next;
		gLUhalfEdge2.Sym.next = gLUhalfEdge3;
		gLUhalfEdge3.Sym.next = gLUhalfEdge2;
	}

	static void KillVertex(GLUvertex gLUvertex, GLUvertex gLUvertex2) {
		GLUhalfEdge gLUhalfEdge = gLUvertex.anEdge;
		GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge;
		do {
			gLUhalfEdge2.Org = gLUvertex2;
			gLUhalfEdge2 = gLUhalfEdge2.Onext;
		} while (gLUhalfEdge2 != gLUhalfEdge);

		GLUvertex gLUvertex3 = gLUvertex.prev;
		GLUvertex gLUvertex4 = gLUvertex.next;
		gLUvertex4.prev = gLUvertex3;
		gLUvertex3.next = gLUvertex4;
	}

	static void KillFace(GLUface gLUface, GLUface gLUface2) {
		GLUhalfEdge gLUhalfEdge = gLUface.anEdge;
		GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge;
		do {
			gLUhalfEdge2.Lface = gLUface2;
			gLUhalfEdge2 = gLUhalfEdge2.Lnext;
		} while (gLUhalfEdge2 != gLUhalfEdge);

		GLUface gLUface3 = gLUface.prev;
		GLUface gLUface4 = gLUface.next;
		gLUface4.prev = gLUface3;
		gLUface3.next = gLUface4;
	}

	public static GLUhalfEdge __gl_meshMakeEdge(GLUmesh gLUmesh) {
		GLUvertex gLUvertex = new GLUvertex();
		GLUvertex gLUvertex2 = new GLUvertex();
		GLUface gLUface = new GLUface();
		GLUhalfEdge gLUhalfEdge = MakeEdge(gLUmesh.eHead);
		if (gLUhalfEdge == null) {
			return null;
		} else {
			MakeVertex(gLUvertex, gLUhalfEdge, gLUmesh.vHead);
			MakeVertex(gLUvertex2, gLUhalfEdge.Sym, gLUmesh.vHead);
			MakeFace(gLUface, gLUhalfEdge, gLUmesh.fHead);
			return gLUhalfEdge;
		}
	}

	public static boolean __gl_meshSplice(GLUhalfEdge gLUhalfEdge, GLUhalfEdge gLUhalfEdge2) {
		boolean boolean1 = false;
		boolean boolean2 = false;
		if (gLUhalfEdge == gLUhalfEdge2) {
			return true;
		} else {
			if (gLUhalfEdge2.Org != gLUhalfEdge.Org) {
				boolean2 = true;
				KillVertex(gLUhalfEdge2.Org, gLUhalfEdge.Org);
			}

			if (gLUhalfEdge2.Lface != gLUhalfEdge.Lface) {
				boolean1 = true;
				KillFace(gLUhalfEdge2.Lface, gLUhalfEdge.Lface);
			}

			Splice(gLUhalfEdge2, gLUhalfEdge);
			if (!boolean2) {
				GLUvertex gLUvertex = new GLUvertex();
				MakeVertex(gLUvertex, gLUhalfEdge2, gLUhalfEdge.Org);
				gLUhalfEdge.Org.anEdge = gLUhalfEdge;
			}

			if (!boolean1) {
				GLUface gLUface = new GLUface();
				MakeFace(gLUface, gLUhalfEdge2, gLUhalfEdge.Lface);
				gLUhalfEdge.Lface.anEdge = gLUhalfEdge;
			}

			return true;
		}
	}

	static boolean __gl_meshDelete(GLUhalfEdge gLUhalfEdge) {
		GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge.Sym;
		boolean boolean1 = false;
		if (gLUhalfEdge.Lface != gLUhalfEdge.Sym.Lface) {
			boolean1 = true;
			KillFace(gLUhalfEdge.Lface, gLUhalfEdge.Sym.Lface);
		}

		if (gLUhalfEdge.Onext == gLUhalfEdge) {
			KillVertex(gLUhalfEdge.Org, (GLUvertex)null);
		} else {
			gLUhalfEdge.Sym.Lface.anEdge = gLUhalfEdge.Sym.Lnext;
			gLUhalfEdge.Org.anEdge = gLUhalfEdge.Onext;
			Splice(gLUhalfEdge, gLUhalfEdge.Sym.Lnext);
			if (!boolean1) {
				GLUface gLUface = new GLUface();
				MakeFace(gLUface, gLUhalfEdge, gLUhalfEdge.Lface);
			}
		}

		if (gLUhalfEdge2.Onext == gLUhalfEdge2) {
			KillVertex(gLUhalfEdge2.Org, (GLUvertex)null);
			KillFace(gLUhalfEdge2.Lface, (GLUface)null);
		} else {
			gLUhalfEdge.Lface.anEdge = gLUhalfEdge2.Sym.Lnext;
			gLUhalfEdge2.Org.anEdge = gLUhalfEdge2.Onext;
			Splice(gLUhalfEdge2, gLUhalfEdge2.Sym.Lnext);
		}

		KillEdge(gLUhalfEdge);
		return true;
	}

	static GLUhalfEdge __gl_meshAddEdgeVertex(GLUhalfEdge gLUhalfEdge) {
		GLUhalfEdge gLUhalfEdge2 = MakeEdge(gLUhalfEdge);
		GLUhalfEdge gLUhalfEdge3 = gLUhalfEdge2.Sym;
		Splice(gLUhalfEdge2, gLUhalfEdge.Lnext);
		gLUhalfEdge2.Org = gLUhalfEdge.Sym.Org;
		GLUvertex gLUvertex = new GLUvertex();
		MakeVertex(gLUvertex, gLUhalfEdge3, gLUhalfEdge2.Org);
		gLUhalfEdge2.Lface = gLUhalfEdge3.Lface = gLUhalfEdge.Lface;
		return gLUhalfEdge2;
	}

	public static GLUhalfEdge __gl_meshSplitEdge(GLUhalfEdge gLUhalfEdge) {
		GLUhalfEdge gLUhalfEdge2 = __gl_meshAddEdgeVertex(gLUhalfEdge);
		GLUhalfEdge gLUhalfEdge3 = gLUhalfEdge2.Sym;
		Splice(gLUhalfEdge.Sym, gLUhalfEdge.Sym.Sym.Lnext);
		Splice(gLUhalfEdge.Sym, gLUhalfEdge3);
		gLUhalfEdge.Sym.Org = gLUhalfEdge3.Org;
		gLUhalfEdge3.Sym.Org.anEdge = gLUhalfEdge3.Sym;
		gLUhalfEdge3.Sym.Lface = gLUhalfEdge.Sym.Lface;
		gLUhalfEdge3.winding = gLUhalfEdge.winding;
		gLUhalfEdge3.Sym.winding = gLUhalfEdge.Sym.winding;
		return gLUhalfEdge3;
	}

	static GLUhalfEdge __gl_meshConnect(GLUhalfEdge gLUhalfEdge, GLUhalfEdge gLUhalfEdge2) {
		boolean boolean1 = false;
		GLUhalfEdge gLUhalfEdge3 = MakeEdge(gLUhalfEdge);
		GLUhalfEdge gLUhalfEdge4 = gLUhalfEdge3.Sym;
		if (gLUhalfEdge2.Lface != gLUhalfEdge.Lface) {
			boolean1 = true;
			KillFace(gLUhalfEdge2.Lface, gLUhalfEdge.Lface);
		}

		Splice(gLUhalfEdge3, gLUhalfEdge.Lnext);
		Splice(gLUhalfEdge4, gLUhalfEdge2);
		gLUhalfEdge3.Org = gLUhalfEdge.Sym.Org;
		gLUhalfEdge4.Org = gLUhalfEdge2.Org;
		gLUhalfEdge3.Lface = gLUhalfEdge4.Lface = gLUhalfEdge.Lface;
		gLUhalfEdge.Lface.anEdge = gLUhalfEdge4;
		if (!boolean1) {
			GLUface gLUface = new GLUface();
			MakeFace(gLUface, gLUhalfEdge3, gLUhalfEdge.Lface);
		}

		return gLUhalfEdge3;
	}

	static void __gl_meshZapFace(GLUface gLUface) {
		GLUhalfEdge gLUhalfEdge = gLUface.anEdge;
		GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge.Lnext;
		GLUhalfEdge gLUhalfEdge3;
		do {
			gLUhalfEdge3 = gLUhalfEdge2;
			gLUhalfEdge2 = gLUhalfEdge2.Lnext;
			gLUhalfEdge3.Lface = null;
			if (gLUhalfEdge3.Sym.Lface == null) {
				if (gLUhalfEdge3.Onext == gLUhalfEdge3) {
					KillVertex(gLUhalfEdge3.Org, (GLUvertex)null);
				} else {
					gLUhalfEdge3.Org.anEdge = gLUhalfEdge3.Onext;
					Splice(gLUhalfEdge3, gLUhalfEdge3.Sym.Lnext);
				}

				GLUhalfEdge gLUhalfEdge4 = gLUhalfEdge3.Sym;
				if (gLUhalfEdge4.Onext == gLUhalfEdge4) {
					KillVertex(gLUhalfEdge4.Org, (GLUvertex)null);
				} else {
					gLUhalfEdge4.Org.anEdge = gLUhalfEdge4.Onext;
					Splice(gLUhalfEdge4, gLUhalfEdge4.Sym.Lnext);
				}

				KillEdge(gLUhalfEdge3);
			}
		} while (gLUhalfEdge3 != gLUhalfEdge);

		GLUface gLUface2 = gLUface.prev;
		GLUface gLUface3 = gLUface.next;
		gLUface3.prev = gLUface2;
		gLUface2.next = gLUface3;
	}

	public static GLUmesh __gl_meshNewMesh() {
		GLUmesh gLUmesh = new GLUmesh();
		GLUvertex gLUvertex = gLUmesh.vHead;
		GLUface gLUface = gLUmesh.fHead;
		GLUhalfEdge gLUhalfEdge = gLUmesh.eHead;
		GLUhalfEdge gLUhalfEdge2 = gLUmesh.eHeadSym;
		gLUvertex.next = gLUvertex.prev = gLUvertex;
		gLUvertex.anEdge = null;
		gLUvertex.data = null;
		gLUface.next = gLUface.prev = gLUface;
		gLUface.anEdge = null;
		gLUface.data = null;
		gLUface.trail = null;
		gLUface.marked = false;
		gLUface.inside = false;
		gLUhalfEdge.next = gLUhalfEdge;
		gLUhalfEdge.Sym = gLUhalfEdge2;
		gLUhalfEdge.Onext = null;
		gLUhalfEdge.Lnext = null;
		gLUhalfEdge.Org = null;
		gLUhalfEdge.Lface = null;
		gLUhalfEdge.winding = 0;
		gLUhalfEdge.activeRegion = null;
		gLUhalfEdge2.next = gLUhalfEdge2;
		gLUhalfEdge2.Sym = gLUhalfEdge;
		gLUhalfEdge2.Onext = null;
		gLUhalfEdge2.Lnext = null;
		gLUhalfEdge2.Org = null;
		gLUhalfEdge2.Lface = null;
		gLUhalfEdge2.winding = 0;
		gLUhalfEdge2.activeRegion = null;
		return gLUmesh;
	}

	static GLUmesh __gl_meshUnion(GLUmesh gLUmesh, GLUmesh gLUmesh2) {
		GLUface gLUface = gLUmesh.fHead;
		GLUvertex gLUvertex = gLUmesh.vHead;
		GLUhalfEdge gLUhalfEdge = gLUmesh.eHead;
		GLUface gLUface2 = gLUmesh2.fHead;
		GLUvertex gLUvertex2 = gLUmesh2.vHead;
		GLUhalfEdge gLUhalfEdge2 = gLUmesh2.eHead;
		if (gLUface2.next != gLUface2) {
			gLUface.prev.next = gLUface2.next;
			gLUface2.next.prev = gLUface.prev;
			gLUface2.prev.next = gLUface;
			gLUface.prev = gLUface2.prev;
		}

		if (gLUvertex2.next != gLUvertex2) {
			gLUvertex.prev.next = gLUvertex2.next;
			gLUvertex2.next.prev = gLUvertex.prev;
			gLUvertex2.prev.next = gLUvertex;
			gLUvertex.prev = gLUvertex2.prev;
		}

		if (gLUhalfEdge2.next != gLUhalfEdge2) {
			gLUhalfEdge.Sym.next.Sym.next = gLUhalfEdge2.next;
			gLUhalfEdge2.next.Sym.next = gLUhalfEdge.Sym.next;
			gLUhalfEdge2.Sym.next.Sym.next = gLUhalfEdge;
			gLUhalfEdge.Sym.next = gLUhalfEdge2.Sym.next;
		}

		return gLUmesh;
	}

	static void __gl_meshDeleteMeshZap(GLUmesh gLUmesh) {
		GLUface gLUface = gLUmesh.fHead;
		while (gLUface.next != gLUface) {
			__gl_meshZapFace(gLUface.next);
		}

		assert gLUmesh.vHead.next == gLUmesh.vHead;
	}

	public static void __gl_meshDeleteMesh(GLUmesh gLUmesh) {
		GLUface gLUface;
		for (GLUface gLUface2 = gLUmesh.fHead.next; gLUface2 != gLUmesh.fHead; gLUface2 = gLUface) {
			gLUface = gLUface2.next;
		}

		GLUvertex gLUvertex;
		for (GLUvertex gLUvertex2 = gLUmesh.vHead.next; gLUvertex2 != gLUmesh.vHead; gLUvertex2 = gLUvertex) {
			gLUvertex = gLUvertex2.next;
		}

		GLUhalfEdge gLUhalfEdge;
		for (GLUhalfEdge gLUhalfEdge2 = gLUmesh.eHead.next; gLUhalfEdge2 != gLUmesh.eHead; gLUhalfEdge2 = gLUhalfEdge) {
			gLUhalfEdge = gLUhalfEdge2.next;
		}
	}

	public static void __gl_meshCheckMesh(GLUmesh gLUmesh) {
		GLUface gLUface = gLUmesh.fHead;
		GLUvertex gLUvertex = gLUmesh.vHead;
		GLUhalfEdge gLUhalfEdge = gLUmesh.eHead;
		GLUface gLUface2;
		GLUface gLUface3;
		GLUhalfEdge gLUhalfEdge2;
		for (gLUface3 = gLUface; (gLUface2 = gLUface3.next) != gLUface; gLUface3 = gLUface2) {
			assert gLUface2.prev == gLUface3;
			gLUhalfEdge2 = gLUface2.anEdge;
			do {
				assert gLUhalfEdge2.Sym != gLUhalfEdge2;
				assert gLUhalfEdge2.Sym.Sym == gLUhalfEdge2;
				assert gLUhalfEdge2.Lnext.Onext.Sym == gLUhalfEdge2;
				assert gLUhalfEdge2.Onext.Sym.Lnext == gLUhalfEdge2;
				assert gLUhalfEdge2.Lface == gLUface2;
				gLUhalfEdge2 = gLUhalfEdge2.Lnext;
			}	 while (gLUhalfEdge2 != gLUface2.anEdge);
		}

		assert gLUface2.prev == gLUface3 && gLUface2.anEdge == null && gLUface2.data == null;
		GLUvertex gLUvertex2;
		GLUvertex gLUvertex3;
		for (gLUvertex3 = gLUvertex; (gLUvertex2 = gLUvertex3.next) != gLUvertex; gLUvertex3 = gLUvertex2) {
			assert gLUvertex2.prev == gLUvertex3;
			gLUhalfEdge2 = gLUvertex2.anEdge;
			do {
				assert gLUhalfEdge2.Sym != gLUhalfEdge2;
				assert gLUhalfEdge2.Sym.Sym == gLUhalfEdge2;
				assert gLUhalfEdge2.Lnext.Onext.Sym == gLUhalfEdge2;
				assert gLUhalfEdge2.Onext.Sym.Lnext == gLUhalfEdge2;
				assert gLUhalfEdge2.Org == gLUvertex2;
				gLUhalfEdge2 = gLUhalfEdge2.Onext;
			}	 while (gLUhalfEdge2 != gLUvertex2.anEdge);
		}

		assert gLUvertex2.prev == gLUvertex3 && gLUvertex2.anEdge == null && gLUvertex2.data == null;
		GLUhalfEdge gLUhalfEdge3;
		for (gLUhalfEdge3 = gLUhalfEdge; (gLUhalfEdge2 = gLUhalfEdge3.next) != gLUhalfEdge; gLUhalfEdge3 = gLUhalfEdge2) {
			assert gLUhalfEdge2.Sym.next == gLUhalfEdge3.Sym;
			assert gLUhalfEdge2.Sym != gLUhalfEdge2;
			assert gLUhalfEdge2.Sym.Sym == gLUhalfEdge2;
			assert gLUhalfEdge2.Org != null;
			assert gLUhalfEdge2.Sym.Org != null;
			assert gLUhalfEdge2.Lnext.Onext.Sym == gLUhalfEdge2;
			assert gLUhalfEdge2.Onext.Sym.Lnext == gLUhalfEdge2;
		}

		assert gLUhalfEdge2.Sym.next == gLUhalfEdge3.Sym && gLUhalfEdge2.Sym == gLUmesh.eHeadSym && gLUhalfEdge2.Sym.Sym == gLUhalfEdge2 && gLUhalfEdge2.Org == null && gLUhalfEdge2.Sym.Org == null && gLUhalfEdge2.Lface == null && gLUhalfEdge2.Sym.Lface == null;
	}
}
