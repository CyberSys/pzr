package org.lwjglx.util.glu.tessellation;

import org.lwjglx.util.glu.GLUtessellator;
import org.lwjglx.util.glu.GLUtessellatorCallback;
import org.lwjglx.util.glu.GLUtessellatorCallbackAdapter;


public class GLUtessellatorImpl implements GLUtessellator {
	public static final int TESS_MAX_CACHE = 100;
	private int state = 0;
	private GLUhalfEdge lastEdge;
	GLUmesh mesh;
	double[] normal = new double[3];
	double[] sUnit = new double[3];
	double[] tUnit = new double[3];
	private double relTolerance;
	int windingRule;
	boolean fatalError;
	Dict dict;
	PriorityQ pq;
	GLUvertex event;
	boolean flagBoundary;
	boolean boundaryOnly;
	GLUface lonelyTriList;
	private boolean flushCacheOnNextVertex;
	int cacheCount;
	CachedVertex[] cache = new CachedVertex[100];
	private Object polygonData;
	private GLUtessellatorCallback callBegin;
	private GLUtessellatorCallback callEdgeFlag;
	private GLUtessellatorCallback callVertex;
	private GLUtessellatorCallback callEnd;
	private GLUtessellatorCallback callError;
	private GLUtessellatorCallback callCombine;
	private GLUtessellatorCallback callBeginData;
	private GLUtessellatorCallback callEdgeFlagData;
	private GLUtessellatorCallback callVertexData;
	private GLUtessellatorCallback callEndData;
	private GLUtessellatorCallback callErrorData;
	private GLUtessellatorCallback callCombineData;
	private static final double GLU_TESS_DEFAULT_TOLERANCE = 0.0;
	private static GLUtessellatorCallback NULL_CB = new GLUtessellatorCallbackAdapter();

	public GLUtessellatorImpl() {
		this.normal[0] = 0.0;
		this.normal[1] = 0.0;
		this.normal[2] = 0.0;
		this.relTolerance = 0.0;
		this.windingRule = 100130;
		this.flagBoundary = false;
		this.boundaryOnly = false;
		this.callBegin = NULL_CB;
		this.callEdgeFlag = NULL_CB;
		this.callVertex = NULL_CB;
		this.callEnd = NULL_CB;
		this.callError = NULL_CB;
		this.callCombine = NULL_CB;
		this.callBeginData = NULL_CB;
		this.callEdgeFlagData = NULL_CB;
		this.callVertexData = NULL_CB;
		this.callEndData = NULL_CB;
		this.callErrorData = NULL_CB;
		this.callCombineData = NULL_CB;
		this.polygonData = null;
		for (int int1 = 0; int1 < this.cache.length; ++int1) {
			this.cache[int1] = new CachedVertex();
		}
	}

	public static GLUtessellator gluNewTess() {
		return new GLUtessellatorImpl();
	}

	private void makeDormant() {
		if (this.mesh != null) {
			Mesh.__gl_meshDeleteMesh(this.mesh);
		}

		this.state = 0;
		this.lastEdge = null;
		this.mesh = null;
	}

	private void requireState(int int1) {
		if (this.state != int1) {
			this.gotoState(int1);
		}
	}

	private void gotoState(int int1) {
		while (this.state != int1) {
			if (this.state < int1) {
				if (this.state == 0) {
					this.callErrorOrErrorData(100151);
					this.gluTessBeginPolygon((Object)null);
				} else if (this.state == 1) {
					this.callErrorOrErrorData(100152);
					this.gluTessBeginContour();
				}
			} else if (this.state == 2) {
				this.callErrorOrErrorData(100154);
				this.gluTessEndContour();
			} else if (this.state == 1) {
				this.callErrorOrErrorData(100153);
				this.makeDormant();
			}
		}
	}

	public void gluDeleteTess() {
		this.requireState(0);
	}

	public void gluTessProperty(int int1, double double1) {
		label31: {
			switch (int1) {
			case 100140: 
				int int2 = (int)double1;
				if ((double)int2 == double1) {
					switch (int2) {
					case 100130: 
					
					case 100131: 
					
					case 100132: 
					
					case 100133: 
					
					case 100134: 
						this.windingRule = int2;
						return;
					
					default: 
						break label31;
					
					}
				}

				break;
			
			case 100141: 
				break label31;
			
			case 100142: 
				if (!(double1 < 0.0) && !(double1 > 1.0)) {
					this.relTolerance = double1;
					return;
				}

				break;
			
			default: 
				this.callErrorOrErrorData(100900);
				return;
			
			}

			this.callErrorOrErrorData(100901);
			return;
		}
		this.boundaryOnly = double1 != 0.0;
	}

	public void gluGetTessProperty(int int1, double[] doubleArray, int int2) {
		switch (int1) {
		case 100140: 
			assert this.windingRule == 100130 || this.windingRule == 100131 || this.windingRule == 100132 || this.windingRule == 100133 || this.windingRule == 100134;
			doubleArray[int2] = (double)this.windingRule;
			break;
		
		case 100141: 
			assert this.boundaryOnly || !this.boundaryOnly;
			doubleArray[int2] = this.boundaryOnly ? 1.0 : 0.0;
			break;
		
		case 100142: 
			assert 0.0 <= this.relTolerance && this.relTolerance <= 1.0;
			doubleArray[int2] = this.relTolerance;
			break;
		
		default: 
			doubleArray[int2] = 0.0;
			this.callErrorOrErrorData(100900);
		
		}
	}

	public void gluTessNormal(double double1, double double2, double double3) {
		this.normal[0] = double1;
		this.normal[1] = double2;
		this.normal[2] = double3;
	}

	public void gluTessCallback(int int1, GLUtessellatorCallback gLUtessellatorCallback) {
		switch (int1) {
		case 100100: 
			this.callBegin = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
			return;
		
		case 100101: 
			this.callVertex = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
			return;
		
		case 100102: 
			this.callEnd = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
			return;
		
		case 100103: 
			this.callError = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
			return;
		
		case 100104: 
			this.callEdgeFlag = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
			this.flagBoundary = gLUtessellatorCallback != null;
			return;
		
		case 100105: 
			this.callCombine = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
			return;
		
		case 100106: 
			this.callBeginData = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
			return;
		
		case 100107: 
			this.callVertexData = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
			return;
		
		case 100108: 
			this.callEndData = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
			return;
		
		case 100109: 
			this.callErrorData = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
			return;
		
		case 100110: 
			this.callEdgeFlagData = this.callBegin = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
			this.flagBoundary = gLUtessellatorCallback != null;
			return;
		
		case 100111: 
			this.callCombineData = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
			return;
		
		default: 
			this.callErrorOrErrorData(100900);
		
		}
	}

	private boolean addVertex(double[] doubleArray, Object object) {
		GLUhalfEdge gLUhalfEdge = this.lastEdge;
		if (gLUhalfEdge == null) {
			gLUhalfEdge = Mesh.__gl_meshMakeEdge(this.mesh);
			if (gLUhalfEdge == null) {
				return false;
			}

			if (!Mesh.__gl_meshSplice(gLUhalfEdge, gLUhalfEdge.Sym)) {
				return false;
			}
		} else {
			if (Mesh.__gl_meshSplitEdge(gLUhalfEdge) == null) {
				return false;
			}

			gLUhalfEdge = gLUhalfEdge.Lnext;
		}

		gLUhalfEdge.Org.data = object;
		gLUhalfEdge.Org.coords[0] = doubleArray[0];
		gLUhalfEdge.Org.coords[1] = doubleArray[1];
		gLUhalfEdge.Org.coords[2] = doubleArray[2];
		gLUhalfEdge.winding = 1;
		gLUhalfEdge.Sym.winding = -1;
		this.lastEdge = gLUhalfEdge;
		return true;
	}

	private void cacheVertex(double[] doubleArray, Object object) {
		if (this.cache[this.cacheCount] == null) {
			this.cache[this.cacheCount] = new CachedVertex();
		}

		CachedVertex cachedVertex = this.cache[this.cacheCount];
		cachedVertex.data = object;
		cachedVertex.coords[0] = doubleArray[0];
		cachedVertex.coords[1] = doubleArray[1];
		cachedVertex.coords[2] = doubleArray[2];
		++this.cacheCount;
	}

	private boolean flushCache() {
		CachedVertex[] cachedVertexArray = this.cache;
		this.mesh = Mesh.__gl_meshNewMesh();
		if (this.mesh == null) {
			return false;
		} else {
			for (int int1 = 0; int1 < this.cacheCount; ++int1) {
				CachedVertex cachedVertex = cachedVertexArray[int1];
				if (!this.addVertex(cachedVertex.coords, cachedVertex.data)) {
					return false;
				}
			}

			this.cacheCount = 0;
			this.flushCacheOnNextVertex = false;
			return true;
		}
	}

	public void gluTessVertex(double[] doubleArray, int int1, Object object) {
		boolean boolean1 = false;
		double[] doubleArray2 = new double[3];
		this.requireState(2);
		if (this.flushCacheOnNextVertex) {
			if (!this.flushCache()) {
				this.callErrorOrErrorData(100902);
				return;
			}

			this.lastEdge = null;
		}

		for (int int2 = 0; int2 < 3; ++int2) {
			double double1 = doubleArray[int2 + int1];
			if (double1 < -1.0E150) {
				double1 = -1.0E150;
				boolean1 = true;
			}

			if (double1 > 1.0E150) {
				double1 = 1.0E150;
				boolean1 = true;
			}

			doubleArray2[int2] = double1;
		}

		if (boolean1) {
			this.callErrorOrErrorData(100155);
		}

		if (this.mesh == null) {
			if (this.cacheCount < 100) {
				this.cacheVertex(doubleArray2, object);
				return;
			}

			if (!this.flushCache()) {
				this.callErrorOrErrorData(100902);
				return;
			}
		}

		if (!this.addVertex(doubleArray2, object)) {
			this.callErrorOrErrorData(100902);
		}
	}

	public void gluTessBeginPolygon(Object object) {
		this.requireState(0);
		this.state = 1;
		this.cacheCount = 0;
		this.flushCacheOnNextVertex = false;
		this.mesh = null;
		this.polygonData = object;
	}

	public void gluTessBeginContour() {
		this.requireState(1);
		this.state = 2;
		this.lastEdge = null;
		if (this.cacheCount > 0) {
			this.flushCacheOnNextVertex = true;
		}
	}

	public void gluTessEndContour() {
		this.requireState(2);
		this.state = 1;
	}

	public void gluTessEndPolygon() {
		try {
			this.requireState(1);
			this.state = 0;
			if (this.mesh == null) {
				if (!this.flagBoundary && Render.__gl_renderCache(this)) {
					this.polygonData = null;
					return;
				}

				if (!this.flushCache()) {
					throw new RuntimeException();
				}
			}

			Normal.__gl_projectPolygon(this);
			if (!Sweep.__gl_computeInterior(this)) {
				throw new RuntimeException();
			}

			GLUmesh gLUmesh = this.mesh;
			if (!this.fatalError) {
				boolean boolean1 = true;
				if (this.boundaryOnly) {
					boolean1 = TessMono.__gl_meshSetWindingNumber(gLUmesh, 1, true);
				} else {
					boolean1 = TessMono.__gl_meshTessellateInterior(gLUmesh);
				}

				if (!boolean1) {
					throw new RuntimeException();
				}

				Mesh.__gl_meshCheckMesh(gLUmesh);
				if (this.callBegin != NULL_CB || this.callEnd != NULL_CB || this.callVertex != NULL_CB || this.callEdgeFlag != NULL_CB || this.callBeginData != NULL_CB || this.callEndData != NULL_CB || this.callVertexData != NULL_CB || this.callEdgeFlagData != NULL_CB) {
					if (this.boundaryOnly) {
						Render.__gl_renderBoundary(this, gLUmesh);
					} else {
						Render.__gl_renderMesh(this, gLUmesh);
					}
				}
			}

			Mesh.__gl_meshDeleteMesh(gLUmesh);
			this.polygonData = null;
			gLUmesh = null;
		} catch (Exception exception) {
			exception.printStackTrace();
			this.callErrorOrErrorData(100902);
		}
	}

	public void gluBeginPolygon() {
		this.gluTessBeginPolygon((Object)null);
		this.gluTessBeginContour();
	}

	public void gluNextContour(int int1) {
		this.gluTessEndContour();
		this.gluTessBeginContour();
	}

	public void gluEndPolygon() {
		this.gluTessEndContour();
		this.gluTessEndPolygon();
	}

	void callBeginOrBeginData(int int1) {
		if (this.callBeginData != NULL_CB) {
			this.callBeginData.beginData(int1, this.polygonData);
		} else {
			this.callBegin.begin(int1);
		}
	}

	void callVertexOrVertexData(Object object) {
		if (this.callVertexData != NULL_CB) {
			this.callVertexData.vertexData(object, this.polygonData);
		} else {
			this.callVertex.vertex(object);
		}
	}

	void callEdgeFlagOrEdgeFlagData(boolean boolean1) {
		if (this.callEdgeFlagData != NULL_CB) {
			this.callEdgeFlagData.edgeFlagData(boolean1, this.polygonData);
		} else {
			this.callEdgeFlag.edgeFlag(boolean1);
		}
	}

	void callEndOrEndData() {
		if (this.callEndData != NULL_CB) {
			this.callEndData.endData(this.polygonData);
		} else {
			this.callEnd.end();
		}
	}

	void callCombineOrCombineData(double[] doubleArray, Object[] objectArray, float[] floatArray, Object[] objectArray2) {
		if (this.callCombineData != NULL_CB) {
			this.callCombineData.combineData(doubleArray, objectArray, floatArray, objectArray2, this.polygonData);
		} else {
			this.callCombine.combine(doubleArray, objectArray, floatArray, objectArray2);
		}
	}

	void callErrorOrErrorData(int int1) {
		if (this.callErrorData != NULL_CB) {
			this.callErrorData.errorData(int1, this.polygonData);
		} else {
			this.callError.error(int1);
		}
	}
}
