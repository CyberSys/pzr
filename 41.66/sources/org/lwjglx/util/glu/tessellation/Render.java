package org.lwjglx.util.glu.tessellation;


class Render {
	private static final boolean USE_OPTIMIZED_CODE_PATH = false;
	private static final Render.RenderFan renderFan = new Render.RenderFan();
	private static final Render.RenderStrip renderStrip = new Render.RenderStrip();
	private static final Render.RenderTriangle renderTriangle = new Render.RenderTriangle();
	private static final int SIGN_INCONSISTENT = 2;

	private Render() {
	}

	public static void __gl_renderMesh(GLUtessellatorImpl gLUtessellatorImpl, GLUmesh gLUmesh) {
		gLUtessellatorImpl.lonelyTriList = null;
		GLUface gLUface;
		for (gLUface = gLUmesh.fHead.next; gLUface != gLUmesh.fHead; gLUface = gLUface.next) {
			gLUface.marked = false;
		}

		for (gLUface = gLUmesh.fHead.next; gLUface != gLUmesh.fHead; gLUface = gLUface.next) {
			if (gLUface.inside && !gLUface.marked) {
				RenderMaximumFaceGroup(gLUtessellatorImpl, gLUface);
				assert gLUface.marked;
			}
		}

		if (gLUtessellatorImpl.lonelyTriList != null) {
			RenderLonelyTriangles(gLUtessellatorImpl, gLUtessellatorImpl.lonelyTriList);
			gLUtessellatorImpl.lonelyTriList = null;
		}
	}

	static void RenderMaximumFaceGroup(GLUtessellatorImpl gLUtessellatorImpl, GLUface gLUface) {
		GLUhalfEdge gLUhalfEdge = gLUface.anEdge;
		Render.FaceCount faceCount = new Render.FaceCount();
		faceCount.size = 1L;
		faceCount.eStart = gLUhalfEdge;
		faceCount.render = renderTriangle;
		if (!gLUtessellatorImpl.flagBoundary) {
			Render.FaceCount faceCount2 = MaximumFan(gLUhalfEdge);
			if (faceCount2.size > faceCount.size) {
				faceCount = faceCount2;
			}

			faceCount2 = MaximumFan(gLUhalfEdge.Lnext);
			if (faceCount2.size > faceCount.size) {
				faceCount = faceCount2;
			}

			faceCount2 = MaximumFan(gLUhalfEdge.Onext.Sym);
			if (faceCount2.size > faceCount.size) {
				faceCount = faceCount2;
			}

			faceCount2 = MaximumStrip(gLUhalfEdge);
			if (faceCount2.size > faceCount.size) {
				faceCount = faceCount2;
			}

			faceCount2 = MaximumStrip(gLUhalfEdge.Lnext);
			if (faceCount2.size > faceCount.size) {
				faceCount = faceCount2;
			}

			faceCount2 = MaximumStrip(gLUhalfEdge.Onext.Sym);
			if (faceCount2.size > faceCount.size) {
				faceCount = faceCount2;
			}
		}

		faceCount.render.render(gLUtessellatorImpl, faceCount.eStart, faceCount.size);
	}

	private static boolean Marked(GLUface gLUface) {
		return !gLUface.inside || gLUface.marked;
	}

	private static GLUface AddToTrail(GLUface gLUface, GLUface gLUface2) {
		gLUface.trail = gLUface2;
		gLUface.marked = true;
		return gLUface;
	}

	private static void FreeTrail(GLUface gLUface) {
		while (gLUface != null) {
			gLUface.marked = false;
			gLUface = gLUface.trail;
		}
	}

	static Render.FaceCount MaximumFan(GLUhalfEdge gLUhalfEdge) {
		Render.FaceCount faceCount = new Render.FaceCount(0L, (GLUhalfEdge)null, renderFan);
		GLUface gLUface = null;
		GLUhalfEdge gLUhalfEdge2;
		for (gLUhalfEdge2 = gLUhalfEdge; !Marked(gLUhalfEdge2.Lface); gLUhalfEdge2 = gLUhalfEdge2.Onext) {
			gLUface = AddToTrail(gLUhalfEdge2.Lface, gLUface);
			++faceCount.size;
		}

		for (gLUhalfEdge2 = gLUhalfEdge; !Marked(gLUhalfEdge2.Sym.Lface); gLUhalfEdge2 = gLUhalfEdge2.Sym.Lnext) {
			gLUface = AddToTrail(gLUhalfEdge2.Sym.Lface, gLUface);
			++faceCount.size;
		}

		faceCount.eStart = gLUhalfEdge2;
		FreeTrail(gLUface);
		return faceCount;
	}

	private static boolean IsEven(long long1) {
		return (long1 & 1L) == 0L;
	}

	static Render.FaceCount MaximumStrip(GLUhalfEdge gLUhalfEdge) {
		Render.FaceCount faceCount = new Render.FaceCount(0L, (GLUhalfEdge)null, renderStrip);
		long long1 = 0L;
		long long2 = 0L;
		GLUface gLUface = null;
		GLUhalfEdge gLUhalfEdge2;
		for (gLUhalfEdge2 = gLUhalfEdge; !Marked(gLUhalfEdge2.Lface); gLUhalfEdge2 = gLUhalfEdge2.Onext) {
			gLUface = AddToTrail(gLUhalfEdge2.Lface, gLUface);
			++long2;
			gLUhalfEdge2 = gLUhalfEdge2.Lnext.Sym;
			if (Marked(gLUhalfEdge2.Lface)) {
				break;
			}

			gLUface = AddToTrail(gLUhalfEdge2.Lface, gLUface);
			++long2;
		}

		GLUhalfEdge gLUhalfEdge3 = gLUhalfEdge2;
		for (gLUhalfEdge2 = gLUhalfEdge; !Marked(gLUhalfEdge2.Sym.Lface); gLUhalfEdge2 = gLUhalfEdge2.Sym.Onext.Sym) {
			gLUface = AddToTrail(gLUhalfEdge2.Sym.Lface, gLUface);
			++long1;
			gLUhalfEdge2 = gLUhalfEdge2.Sym.Lnext;
			if (Marked(gLUhalfEdge2.Sym.Lface)) {
				break;
			}

			gLUface = AddToTrail(gLUhalfEdge2.Sym.Lface, gLUface);
			++long1;
		}

		faceCount.size = long2 + long1;
		if (IsEven(long2)) {
			faceCount.eStart = gLUhalfEdge3.Sym;
		} else if (IsEven(long1)) {
			faceCount.eStart = gLUhalfEdge2;
		} else {
			--faceCount.size;
			faceCount.eStart = gLUhalfEdge2.Onext;
		}

		FreeTrail(gLUface);
		return faceCount;
	}

	static void RenderLonelyTriangles(GLUtessellatorImpl gLUtessellatorImpl, GLUface gLUface) {
		int int1 = -1;
		gLUtessellatorImpl.callBeginOrBeginData(4);
		while (gLUface != null) {
			GLUhalfEdge gLUhalfEdge = gLUface.anEdge;
			do {
				if (gLUtessellatorImpl.flagBoundary) {
					int int2 = !gLUhalfEdge.Sym.Lface.inside ? 1 : 0;
					if (int1 != int2) {
						int1 = int2;
						gLUtessellatorImpl.callEdgeFlagOrEdgeFlagData(int2 != 0);
					}
				}

				gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Org.data);
				gLUhalfEdge = gLUhalfEdge.Lnext;
			}	 while (gLUhalfEdge != gLUface.anEdge);

			gLUface = gLUface.trail;
		}

		gLUtessellatorImpl.callEndOrEndData();
	}

	public static void __gl_renderBoundary(GLUtessellatorImpl gLUtessellatorImpl, GLUmesh gLUmesh) {
		for (GLUface gLUface = gLUmesh.fHead.next; gLUface != gLUmesh.fHead; gLUface = gLUface.next) {
			if (gLUface.inside) {
				gLUtessellatorImpl.callBeginOrBeginData(2);
				GLUhalfEdge gLUhalfEdge = gLUface.anEdge;
				do {
					gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Org.data);
					gLUhalfEdge = gLUhalfEdge.Lnext;
				}		 while (gLUhalfEdge != gLUface.anEdge);

				gLUtessellatorImpl.callEndOrEndData();
			}
		}
	}

	static int ComputeNormal(GLUtessellatorImpl gLUtessellatorImpl, double[] doubleArray, boolean boolean1) {
		CachedVertex[] cachedVertexArray = gLUtessellatorImpl.cache;
		int int1 = gLUtessellatorImpl.cacheCount;
		double[] doubleArray2 = new double[3];
		byte byte1 = 0;
		if (!boolean1) {
			doubleArray[0] = doubleArray[1] = doubleArray[2] = 0.0;
		}

		int int2 = 1;
		double double1 = cachedVertexArray[int2].coords[0] - cachedVertexArray[0].coords[0];
		double double2 = cachedVertexArray[int2].coords[1] - cachedVertexArray[0].coords[1];
		double double3 = cachedVertexArray[int2].coords[2] - cachedVertexArray[0].coords[2];
		while (true) {
			++int2;
			if (int2 >= int1) {
				return byte1;
			}

			double double4 = double1;
			double double5 = double2;
			double double6 = double3;
			double1 = cachedVertexArray[int2].coords[0] - cachedVertexArray[0].coords[0];
			double2 = cachedVertexArray[int2].coords[1] - cachedVertexArray[0].coords[1];
			double3 = cachedVertexArray[int2].coords[2] - cachedVertexArray[0].coords[2];
			doubleArray2[0] = double5 * double3 - double6 * double2;
			doubleArray2[1] = double6 * double1 - double4 * double3;
			doubleArray2[2] = double4 * double2 - double5 * double1;
			double double7 = doubleArray2[0] * doubleArray[0] + doubleArray2[1] * doubleArray[1] + doubleArray2[2] * doubleArray[2];
			if (!boolean1) {
				if (double7 >= 0.0) {
					doubleArray[0] += doubleArray2[0];
					doubleArray[1] += doubleArray2[1];
					doubleArray[2] += doubleArray2[2];
				} else {
					doubleArray[0] -= doubleArray2[0];
					doubleArray[1] -= doubleArray2[1];
					doubleArray[2] -= doubleArray2[2];
				}
			} else if (double7 != 0.0) {
				if (double7 > 0.0) {
					if (byte1 < 0) {
						return 2;
					}

					byte1 = 1;
				} else {
					if (byte1 > 0) {
						return 2;
					}

					byte1 = -1;
				}
			}
		}
	}

	public static boolean __gl_renderCache(GLUtessellatorImpl gLUtessellatorImpl) {
		CachedVertex[] cachedVertexArray = gLUtessellatorImpl.cache;
		int int1 = gLUtessellatorImpl.cacheCount;
		double[] doubleArray = new double[3];
		if (gLUtessellatorImpl.cacheCount < 3) {
			return true;
		} else {
			doubleArray[0] = gLUtessellatorImpl.normal[0];
			doubleArray[1] = gLUtessellatorImpl.normal[1];
			doubleArray[2] = gLUtessellatorImpl.normal[2];
			if (doubleArray[0] == 0.0 && doubleArray[1] == 0.0 && doubleArray[2] == 0.0) {
				ComputeNormal(gLUtessellatorImpl, doubleArray, false);
			}

			int int2 = ComputeNormal(gLUtessellatorImpl, doubleArray, true);
			if (int2 == 2) {
				return false;
			} else {
				return int2 == 0;
			}
		}
	}

	private static class FaceCount {
		long size;
		GLUhalfEdge eStart;
		Render.renderCallBack render;

		private FaceCount() {
		}

		private FaceCount(long long1, GLUhalfEdge gLUhalfEdge, Render.renderCallBack renderCallBack) {
			this.size = long1;
			this.eStart = gLUhalfEdge;
			this.render = renderCallBack;
		}
	}

	private static class RenderTriangle implements Render.renderCallBack {

		public void render(GLUtessellatorImpl gLUtessellatorImpl, GLUhalfEdge gLUhalfEdge, long long1) {
			assert long1 == 1L;
			gLUtessellatorImpl.lonelyTriList = Render.AddToTrail(gLUhalfEdge.Lface, gLUtessellatorImpl.lonelyTriList);
		}
	}

	private interface renderCallBack {

		void render(GLUtessellatorImpl gLUtessellatorImpl, GLUhalfEdge gLUhalfEdge, long long1);
	}

	private static class RenderFan implements Render.renderCallBack {

		public void render(GLUtessellatorImpl gLUtessellatorImpl, GLUhalfEdge gLUhalfEdge, long long1) {
			gLUtessellatorImpl.callBeginOrBeginData(6);
			gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Org.data);
			gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Sym.Org.data);
			while (!Render.Marked(gLUhalfEdge.Lface)) {
				gLUhalfEdge.Lface.marked = true;
				--long1;
				gLUhalfEdge = gLUhalfEdge.Onext;
				gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Sym.Org.data);
			}

			assert long1 == 0L;
			gLUtessellatorImpl.callEndOrEndData();
		}
	}

	private static class RenderStrip implements Render.renderCallBack {

		public void render(GLUtessellatorImpl gLUtessellatorImpl, GLUhalfEdge gLUhalfEdge, long long1) {
			gLUtessellatorImpl.callBeginOrBeginData(5);
			gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Org.data);
			gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Sym.Org.data);
			while (!Render.Marked(gLUhalfEdge.Lface)) {
				gLUhalfEdge.Lface.marked = true;
				--long1;
				gLUhalfEdge = gLUhalfEdge.Lnext.Sym;
				gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Org.data);
				if (Render.Marked(gLUhalfEdge.Lface)) {
					break;
				}

				gLUhalfEdge.Lface.marked = true;
				--long1;
				gLUhalfEdge = gLUhalfEdge.Onext;
				gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Sym.Org.data);
			}

			assert long1 == 0L;
			gLUtessellatorImpl.callEndOrEndData();
		}
	}
}
