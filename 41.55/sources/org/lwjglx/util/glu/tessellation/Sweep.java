package org.lwjglx.util.glu.tessellation;


class Sweep {
	private static final boolean TOLERANCE_NONZERO = false;
	private static final double SENTINEL_COORD = 4.0E150;

	private Sweep() {
	}

	private static void DebugEvent(GLUtessellatorImpl gLUtessellatorImpl) {
	}

	private static void AddWinding(GLUhalfEdge gLUhalfEdge, GLUhalfEdge gLUhalfEdge2) {
		gLUhalfEdge.winding += gLUhalfEdge2.winding;
		GLUhalfEdge gLUhalfEdge3 = gLUhalfEdge.Sym;
		gLUhalfEdge3.winding += gLUhalfEdge2.Sym.winding;
	}

	private static ActiveRegion RegionBelow(ActiveRegion activeRegion) {
		return (ActiveRegion)Dict.dictKey(Dict.dictPred(activeRegion.nodeUp));
	}

	private static ActiveRegion RegionAbove(ActiveRegion activeRegion) {
		return (ActiveRegion)Dict.dictKey(Dict.dictSucc(activeRegion.nodeUp));
	}

	static boolean EdgeLeq(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion, ActiveRegion activeRegion2) {
		GLUvertex gLUvertex = gLUtessellatorImpl.event;
		GLUhalfEdge gLUhalfEdge = activeRegion.eUp;
		GLUhalfEdge gLUhalfEdge2 = activeRegion2.eUp;
		if (gLUhalfEdge.Sym.Org == gLUvertex) {
			if (gLUhalfEdge2.Sym.Org == gLUvertex) {
				if (Geom.VertLeq(gLUhalfEdge.Org, gLUhalfEdge2.Org)) {
					return Geom.EdgeSign(gLUhalfEdge2.Sym.Org, gLUhalfEdge.Org, gLUhalfEdge2.Org) <= 0.0;
				} else {
					return Geom.EdgeSign(gLUhalfEdge.Sym.Org, gLUhalfEdge2.Org, gLUhalfEdge.Org) >= 0.0;
				}
			} else {
				return Geom.EdgeSign(gLUhalfEdge2.Sym.Org, gLUvertex, gLUhalfEdge2.Org) <= 0.0;
			}
		} else if (gLUhalfEdge2.Sym.Org == gLUvertex) {
			return Geom.EdgeSign(gLUhalfEdge.Sym.Org, gLUvertex, gLUhalfEdge.Org) >= 0.0;
		} else {
			double double1 = Geom.EdgeEval(gLUhalfEdge.Sym.Org, gLUvertex, gLUhalfEdge.Org);
			double double2 = Geom.EdgeEval(gLUhalfEdge2.Sym.Org, gLUvertex, gLUhalfEdge2.Org);
			return double1 >= double2;
		}
	}

	static void DeleteRegion(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion) {
		assert !activeRegion.fixUpperEdge || activeRegion.eUp.winding == 0;
		activeRegion.eUp.activeRegion = null;
		Dict.dictDelete(gLUtessellatorImpl.dict, activeRegion.nodeUp);
	}

	static boolean FixUpperEdge(ActiveRegion activeRegion, GLUhalfEdge gLUhalfEdge) {
		assert activeRegion.fixUpperEdge;
		if (!Mesh.__gl_meshDelete(activeRegion.eUp)) {
			return false;
		} else {
			activeRegion.fixUpperEdge = false;
			activeRegion.eUp = gLUhalfEdge;
			gLUhalfEdge.activeRegion = activeRegion;
			return true;
		}
	}

	static ActiveRegion TopLeftRegion(ActiveRegion activeRegion) {
		GLUvertex gLUvertex = activeRegion.eUp.Org;
		do {
			activeRegion = RegionAbove(activeRegion);
		} while (activeRegion.eUp.Org == gLUvertex);

		if (activeRegion.fixUpperEdge) {
			GLUhalfEdge gLUhalfEdge = Mesh.__gl_meshConnect(RegionBelow(activeRegion).eUp.Sym, activeRegion.eUp.Lnext);
			if (gLUhalfEdge == null) {
				return null;
			}

			if (!FixUpperEdge(activeRegion, gLUhalfEdge)) {
				return null;
			}

			activeRegion = RegionAbove(activeRegion);
		}

		return activeRegion;
	}

	static ActiveRegion TopRightRegion(ActiveRegion activeRegion) {
		GLUvertex gLUvertex = activeRegion.eUp.Sym.Org;
		do {
			activeRegion = RegionAbove(activeRegion);
		} while (activeRegion.eUp.Sym.Org == gLUvertex);

		return activeRegion;
	}

	static ActiveRegion AddRegionBelow(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion, GLUhalfEdge gLUhalfEdge) {
		ActiveRegion activeRegion2 = new ActiveRegion();
		activeRegion2.eUp = gLUhalfEdge;
		activeRegion2.nodeUp = Dict.dictInsertBefore(gLUtessellatorImpl.dict, activeRegion.nodeUp, activeRegion2);
		if (activeRegion2.nodeUp == null) {
			throw new RuntimeException();
		} else {
			activeRegion2.fixUpperEdge = false;
			activeRegion2.sentinel = false;
			activeRegion2.dirty = false;
			gLUhalfEdge.activeRegion = activeRegion2;
			return activeRegion2;
		}
	}

	static boolean IsWindingInside(GLUtessellatorImpl gLUtessellatorImpl, int int1) {
		switch (gLUtessellatorImpl.windingRule) {
		case 100130: 
			return (int1 & 1) != 0;
		
		case 100131: 
			return int1 != 0;
		
		case 100132: 
			return int1 > 0;
		
		case 100133: 
			return int1 < 0;
		
		case 100134: 
			return int1 >= 2 || int1 <= -2;
		
		default: 
			throw new InternalError();
		
		}
	}

	static void ComputeWinding(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion) {
		activeRegion.windingNumber = RegionAbove(activeRegion).windingNumber + activeRegion.eUp.winding;
		activeRegion.inside = IsWindingInside(gLUtessellatorImpl, activeRegion.windingNumber);
	}

	static void FinishRegion(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion) {
		GLUhalfEdge gLUhalfEdge = activeRegion.eUp;
		GLUface gLUface = gLUhalfEdge.Lface;
		gLUface.inside = activeRegion.inside;
		gLUface.anEdge = gLUhalfEdge;
		DeleteRegion(gLUtessellatorImpl, activeRegion);
	}

	static GLUhalfEdge FinishLeftRegions(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion, ActiveRegion activeRegion2) {
		ActiveRegion activeRegion3 = activeRegion;
		ActiveRegion activeRegion4;
		GLUhalfEdge gLUhalfEdge;
		for (gLUhalfEdge = activeRegion.eUp; activeRegion3 != activeRegion2; activeRegion3 = activeRegion4) {
			activeRegion3.fixUpperEdge = false;
			activeRegion4 = RegionBelow(activeRegion3);
			GLUhalfEdge gLUhalfEdge2 = activeRegion4.eUp;
			if (gLUhalfEdge2.Org != gLUhalfEdge.Org) {
				if (!activeRegion4.fixUpperEdge) {
					FinishRegion(gLUtessellatorImpl, activeRegion3);
					break;
				}

				gLUhalfEdge2 = Mesh.__gl_meshConnect(gLUhalfEdge.Onext.Sym, gLUhalfEdge2.Sym);
				if (gLUhalfEdge2 == null) {
					throw new RuntimeException();
				}

				if (!FixUpperEdge(activeRegion4, gLUhalfEdge2)) {
					throw new RuntimeException();
				}
			}

			if (gLUhalfEdge.Onext != gLUhalfEdge2) {
				if (!Mesh.__gl_meshSplice(gLUhalfEdge2.Sym.Lnext, gLUhalfEdge2)) {
					throw new RuntimeException();
				}

				if (!Mesh.__gl_meshSplice(gLUhalfEdge, gLUhalfEdge2)) {
					throw new RuntimeException();
				}
			}

			FinishRegion(gLUtessellatorImpl, activeRegion3);
			gLUhalfEdge = activeRegion4.eUp;
		}

		return gLUhalfEdge;
	}

	static void AddRightEdges(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion, GLUhalfEdge gLUhalfEdge, GLUhalfEdge gLUhalfEdge2, GLUhalfEdge gLUhalfEdge3, boolean boolean1) {
		boolean boolean2 = true;
		GLUhalfEdge gLUhalfEdge4 = gLUhalfEdge;
		do {
			assert Geom.VertLeq(gLUhalfEdge4.Org, gLUhalfEdge4.Sym.Org);
			AddRegionBelow(gLUtessellatorImpl, activeRegion, gLUhalfEdge4.Sym);
			gLUhalfEdge4 = gLUhalfEdge4.Onext;
		} while (gLUhalfEdge4 != gLUhalfEdge2);

		if (gLUhalfEdge3 == null) {
			gLUhalfEdge3 = RegionBelow(activeRegion).eUp.Sym.Onext;
		}

		ActiveRegion activeRegion2 = activeRegion;
		GLUhalfEdge gLUhalfEdge5 = gLUhalfEdge3;
		while (true) {
			ActiveRegion activeRegion3 = RegionBelow(activeRegion2);
			gLUhalfEdge4 = activeRegion3.eUp.Sym;
			if (gLUhalfEdge4.Org != gLUhalfEdge5.Org) {
				activeRegion2.dirty = true;
				assert activeRegion2.windingNumber - gLUhalfEdge4.winding == activeRegion3.windingNumber;
				if (boolean1) {
					WalkDirtyRegions(gLUtessellatorImpl, activeRegion2);
				}

				return;
			}

			if (gLUhalfEdge4.Onext != gLUhalfEdge5) {
				if (!Mesh.__gl_meshSplice(gLUhalfEdge4.Sym.Lnext, gLUhalfEdge4)) {
					throw new RuntimeException();
				}

				if (!Mesh.__gl_meshSplice(gLUhalfEdge5.Sym.Lnext, gLUhalfEdge4)) {
					throw new RuntimeException();
				}
			}

			activeRegion3.windingNumber = activeRegion2.windingNumber - gLUhalfEdge4.winding;
			activeRegion3.inside = IsWindingInside(gLUtessellatorImpl, activeRegion3.windingNumber);
			activeRegion2.dirty = true;
			if (!boolean2 && CheckForRightSplice(gLUtessellatorImpl, activeRegion2)) {
				AddWinding(gLUhalfEdge4, gLUhalfEdge5);
				DeleteRegion(gLUtessellatorImpl, activeRegion2);
				if (!Mesh.__gl_meshDelete(gLUhalfEdge5)) {
					throw new RuntimeException();
				}
			}

			boolean2 = false;
			activeRegion2 = activeRegion3;
			gLUhalfEdge5 = gLUhalfEdge4;
		}
	}

	static void CallCombine(GLUtessellatorImpl gLUtessellatorImpl, GLUvertex gLUvertex, Object[] objectArray, float[] floatArray, boolean boolean1) {
		double[] doubleArray = new double[]{gLUvertex.coords[0], gLUvertex.coords[1], gLUvertex.coords[2]};
		Object[] objectArray2 = new Object[1];
		gLUtessellatorImpl.callCombineOrCombineData(doubleArray, objectArray, floatArray, objectArray2);
		gLUvertex.data = objectArray2[0];
		if (gLUvertex.data == null) {
			if (!boolean1) {
				gLUvertex.data = objectArray[0];
			} else if (!gLUtessellatorImpl.fatalError) {
				gLUtessellatorImpl.callErrorOrErrorData(100156);
				gLUtessellatorImpl.fatalError = true;
			}
		}
	}

	static void SpliceMergeVertices(GLUtessellatorImpl gLUtessellatorImpl, GLUhalfEdge gLUhalfEdge, GLUhalfEdge gLUhalfEdge2) {
		Object[] objectArray = new Object[4];
		float[] floatArray = new float[]{0.5F, 0.5F, 0.0F, 0.0F};
		objectArray[0] = gLUhalfEdge.Org.data;
		objectArray[1] = gLUhalfEdge2.Org.data;
		CallCombine(gLUtessellatorImpl, gLUhalfEdge.Org, objectArray, floatArray, false);
		if (!Mesh.__gl_meshSplice(gLUhalfEdge, gLUhalfEdge2)) {
			throw new RuntimeException();
		}
	}

	static void VertexWeights(GLUvertex gLUvertex, GLUvertex gLUvertex2, GLUvertex gLUvertex3, float[] floatArray) {
		double double1 = Geom.VertL1dist(gLUvertex2, gLUvertex);
		double double2 = Geom.VertL1dist(gLUvertex3, gLUvertex);
		floatArray[0] = (float)(0.5 * double2 / (double1 + double2));
		floatArray[1] = (float)(0.5 * double1 / (double1 + double2));
		double[] doubleArray = gLUvertex.coords;
		doubleArray[0] += (double)floatArray[0] * gLUvertex2.coords[0] + (double)floatArray[1] * gLUvertex3.coords[0];
		doubleArray = gLUvertex.coords;
		doubleArray[1] += (double)floatArray[0] * gLUvertex2.coords[1] + (double)floatArray[1] * gLUvertex3.coords[1];
		doubleArray = gLUvertex.coords;
		doubleArray[2] += (double)floatArray[0] * gLUvertex2.coords[2] + (double)floatArray[1] * gLUvertex3.coords[2];
	}

	static void GetIntersectData(GLUtessellatorImpl gLUtessellatorImpl, GLUvertex gLUvertex, GLUvertex gLUvertex2, GLUvertex gLUvertex3, GLUvertex gLUvertex4, GLUvertex gLUvertex5) {
		Object[] objectArray = new Object[4];
		float[] floatArray = new float[4];
		float[] floatArray2 = new float[2];
		float[] floatArray3 = new float[2];
		objectArray[0] = gLUvertex2.data;
		objectArray[1] = gLUvertex3.data;
		objectArray[2] = gLUvertex4.data;
		objectArray[3] = gLUvertex5.data;
		gLUvertex.coords[0] = gLUvertex.coords[1] = gLUvertex.coords[2] = 0.0;
		VertexWeights(gLUvertex, gLUvertex2, gLUvertex3, floatArray2);
		VertexWeights(gLUvertex, gLUvertex4, gLUvertex5, floatArray3);
		System.arraycopy(floatArray2, 0, floatArray, 0, 2);
		System.arraycopy(floatArray3, 0, floatArray, 2, 2);
		CallCombine(gLUtessellatorImpl, gLUvertex, objectArray, floatArray, true);
	}

	static boolean CheckForRightSplice(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion) {
		ActiveRegion activeRegion2 = RegionBelow(activeRegion);
		GLUhalfEdge gLUhalfEdge = activeRegion.eUp;
		GLUhalfEdge gLUhalfEdge2 = activeRegion2.eUp;
		if (Geom.VertLeq(gLUhalfEdge.Org, gLUhalfEdge2.Org)) {
			if (Geom.EdgeSign(gLUhalfEdge2.Sym.Org, gLUhalfEdge.Org, gLUhalfEdge2.Org) > 0.0) {
				return false;
			}

			if (!Geom.VertEq(gLUhalfEdge.Org, gLUhalfEdge2.Org)) {
				if (Mesh.__gl_meshSplitEdge(gLUhalfEdge2.Sym) == null) {
					throw new RuntimeException();
				}

				if (!Mesh.__gl_meshSplice(gLUhalfEdge, gLUhalfEdge2.Sym.Lnext)) {
					throw new RuntimeException();
				}

				activeRegion.dirty = activeRegion2.dirty = true;
			} else if (gLUhalfEdge.Org != gLUhalfEdge2.Org) {
				gLUtessellatorImpl.pq.pqDelete(gLUhalfEdge.Org.pqHandle);
				SpliceMergeVertices(gLUtessellatorImpl, gLUhalfEdge2.Sym.Lnext, gLUhalfEdge);
			}
		} else {
			if (Geom.EdgeSign(gLUhalfEdge.Sym.Org, gLUhalfEdge2.Org, gLUhalfEdge.Org) < 0.0) {
				return false;
			}

			RegionAbove(activeRegion).dirty = activeRegion.dirty = true;
			if (Mesh.__gl_meshSplitEdge(gLUhalfEdge.Sym) == null) {
				throw new RuntimeException();
			}

			if (!Mesh.__gl_meshSplice(gLUhalfEdge2.Sym.Lnext, gLUhalfEdge)) {
				throw new RuntimeException();
			}
		}

		return true;
	}

	static boolean CheckForLeftSplice(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion) {
		ActiveRegion activeRegion2 = RegionBelow(activeRegion);
		GLUhalfEdge gLUhalfEdge = activeRegion.eUp;
		GLUhalfEdge gLUhalfEdge2 = activeRegion2.eUp;
		assert !Geom.VertEq(gLUhalfEdge.Sym.Org, gLUhalfEdge2.Sym.Org);
		GLUhalfEdge gLUhalfEdge3;
		if (Geom.VertLeq(gLUhalfEdge.Sym.Org, gLUhalfEdge2.Sym.Org)) {
			if (Geom.EdgeSign(gLUhalfEdge.Sym.Org, gLUhalfEdge2.Sym.Org, gLUhalfEdge.Org) < 0.0) {
				return false;
			}

			RegionAbove(activeRegion).dirty = activeRegion.dirty = true;
			gLUhalfEdge3 = Mesh.__gl_meshSplitEdge(gLUhalfEdge);
			if (gLUhalfEdge3 == null) {
				throw new RuntimeException();
			}

			if (!Mesh.__gl_meshSplice(gLUhalfEdge2.Sym, gLUhalfEdge3)) {
				throw new RuntimeException();
			}

			gLUhalfEdge3.Lface.inside = activeRegion.inside;
		} else {
			if (Geom.EdgeSign(gLUhalfEdge2.Sym.Org, gLUhalfEdge.Sym.Org, gLUhalfEdge2.Org) > 0.0) {
				return false;
			}

			activeRegion.dirty = activeRegion2.dirty = true;
			gLUhalfEdge3 = Mesh.__gl_meshSplitEdge(gLUhalfEdge2);
			if (gLUhalfEdge3 == null) {
				throw new RuntimeException();
			}

			if (!Mesh.__gl_meshSplice(gLUhalfEdge.Lnext, gLUhalfEdge2.Sym)) {
				throw new RuntimeException();
			}

			gLUhalfEdge3.Sym.Lface.inside = activeRegion.inside;
		}

		return true;
	}

	static boolean CheckForIntersect(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion) {
		ActiveRegion activeRegion2 = RegionBelow(activeRegion);
		GLUhalfEdge gLUhalfEdge = activeRegion.eUp;
		GLUhalfEdge gLUhalfEdge2 = activeRegion2.eUp;
		GLUvertex gLUvertex = gLUhalfEdge.Org;
		GLUvertex gLUvertex2 = gLUhalfEdge2.Org;
		GLUvertex gLUvertex3 = gLUhalfEdge.Sym.Org;
		GLUvertex gLUvertex4 = gLUhalfEdge2.Sym.Org;
		GLUvertex gLUvertex5 = new GLUvertex();
		assert !Geom.VertEq(gLUvertex4, gLUvertex3);
		assert Geom.EdgeSign(gLUvertex3, gLUtessellatorImpl.event, gLUvertex) <= 0.0;
		assert Geom.EdgeSign(gLUvertex4, gLUtessellatorImpl.event, gLUvertex2) >= 0.0;
		assert gLUvertex != gLUtessellatorImpl.event && gLUvertex2 != gLUtessellatorImpl.event;
		assert !activeRegion.fixUpperEdge && !activeRegion2.fixUpperEdge;
		if (gLUvertex == gLUvertex2) {
			return false;
		} else {
			double double1 = Math.min(gLUvertex.t, gLUvertex3.t);
			double double2 = Math.max(gLUvertex2.t, gLUvertex4.t);
			if (double1 > double2) {
				return false;
			} else {
				if (Geom.VertLeq(gLUvertex, gLUvertex2)) {
					if (Geom.EdgeSign(gLUvertex4, gLUvertex, gLUvertex2) > 0.0) {
						return false;
					}
				} else if (Geom.EdgeSign(gLUvertex3, gLUvertex2, gLUvertex) < 0.0) {
					return false;
				}

				DebugEvent(gLUtessellatorImpl);
				Geom.EdgeIntersect(gLUvertex3, gLUvertex, gLUvertex4, gLUvertex2, gLUvertex5);
				assert Math.min(gLUvertex.t, gLUvertex3.t) <= gLUvertex5.t;
				assert gLUvertex5.t <= Math.max(gLUvertex2.t, gLUvertex4.t);
				assert Math.min(gLUvertex4.s, gLUvertex3.s) <= gLUvertex5.s;
				assert gLUvertex5.s <= Math.max(gLUvertex2.s, gLUvertex.s);
				if (Geom.VertLeq(gLUvertex5, gLUtessellatorImpl.event)) {
					gLUvertex5.s = gLUtessellatorImpl.event.s;
					gLUvertex5.t = gLUtessellatorImpl.event.t;
				}

				GLUvertex gLUvertex6 = Geom.VertLeq(gLUvertex, gLUvertex2) ? gLUvertex : gLUvertex2;
				if (Geom.VertLeq(gLUvertex6, gLUvertex5)) {
					gLUvertex5.s = gLUvertex6.s;
					gLUvertex5.t = gLUvertex6.t;
				}

				if (!Geom.VertEq(gLUvertex5, gLUvertex) && !Geom.VertEq(gLUvertex5, gLUvertex2)) {
					if (!Geom.VertEq(gLUvertex3, gLUtessellatorImpl.event) && Geom.EdgeSign(gLUvertex3, gLUtessellatorImpl.event, gLUvertex5) >= 0.0 || !Geom.VertEq(gLUvertex4, gLUtessellatorImpl.event) && Geom.EdgeSign(gLUvertex4, gLUtessellatorImpl.event, gLUvertex5) <= 0.0) {
						if (gLUvertex4 == gLUtessellatorImpl.event) {
							if (Mesh.__gl_meshSplitEdge(gLUhalfEdge.Sym) == null) {
								throw new RuntimeException();
							} else if (!Mesh.__gl_meshSplice(gLUhalfEdge2.Sym, gLUhalfEdge)) {
								throw new RuntimeException();
							} else {
								activeRegion = TopLeftRegion(activeRegion);
								if (activeRegion == null) {
									throw new RuntimeException();
								} else {
									gLUhalfEdge = RegionBelow(activeRegion).eUp;
									FinishLeftRegions(gLUtessellatorImpl, RegionBelow(activeRegion), activeRegion2);
									AddRightEdges(gLUtessellatorImpl, activeRegion, gLUhalfEdge.Sym.Lnext, gLUhalfEdge, gLUhalfEdge, true);
									return true;
								}
							}
						} else if (gLUvertex3 == gLUtessellatorImpl.event) {
							if (Mesh.__gl_meshSplitEdge(gLUhalfEdge2.Sym) == null) {
								throw new RuntimeException();
							} else if (!Mesh.__gl_meshSplice(gLUhalfEdge.Lnext, gLUhalfEdge2.Sym.Lnext)) {
								throw new RuntimeException();
							} else {
								activeRegion2 = activeRegion;
								activeRegion = TopRightRegion(activeRegion);
								GLUhalfEdge gLUhalfEdge3 = RegionBelow(activeRegion).eUp.Sym.Onext;
								activeRegion2.eUp = gLUhalfEdge2.Sym.Lnext;
								gLUhalfEdge2 = FinishLeftRegions(gLUtessellatorImpl, activeRegion2, (ActiveRegion)null);
								AddRightEdges(gLUtessellatorImpl, activeRegion, gLUhalfEdge2.Onext, gLUhalfEdge.Sym.Onext, gLUhalfEdge3, true);
								return true;
							}
						} else {
							if (Geom.EdgeSign(gLUvertex3, gLUtessellatorImpl.event, gLUvertex5) >= 0.0) {
								RegionAbove(activeRegion).dirty = activeRegion.dirty = true;
								if (Mesh.__gl_meshSplitEdge(gLUhalfEdge.Sym) == null) {
									throw new RuntimeException();
								}

								gLUhalfEdge.Org.s = gLUtessellatorImpl.event.s;
								gLUhalfEdge.Org.t = gLUtessellatorImpl.event.t;
							}

							if (Geom.EdgeSign(gLUvertex4, gLUtessellatorImpl.event, gLUvertex5) <= 0.0) {
								activeRegion.dirty = activeRegion2.dirty = true;
								if (Mesh.__gl_meshSplitEdge(gLUhalfEdge2.Sym) == null) {
									throw new RuntimeException();
								}

								gLUhalfEdge2.Org.s = gLUtessellatorImpl.event.s;
								gLUhalfEdge2.Org.t = gLUtessellatorImpl.event.t;
							}

							return false;
						}
					} else if (Mesh.__gl_meshSplitEdge(gLUhalfEdge.Sym) == null) {
						throw new RuntimeException();
					} else if (Mesh.__gl_meshSplitEdge(gLUhalfEdge2.Sym) == null) {
						throw new RuntimeException();
					} else if (!Mesh.__gl_meshSplice(gLUhalfEdge2.Sym.Lnext, gLUhalfEdge)) {
						throw new RuntimeException();
					} else {
						gLUhalfEdge.Org.s = gLUvertex5.s;
						gLUhalfEdge.Org.t = gLUvertex5.t;
						gLUhalfEdge.Org.pqHandle = gLUtessellatorImpl.pq.pqInsert(gLUhalfEdge.Org);
						if ((long)gLUhalfEdge.Org.pqHandle == Long.MAX_VALUE) {
							gLUtessellatorImpl.pq.pqDeletePriorityQ();
							gLUtessellatorImpl.pq = null;
							throw new RuntimeException();
						} else {
							GetIntersectData(gLUtessellatorImpl, gLUhalfEdge.Org, gLUvertex, gLUvertex3, gLUvertex2, gLUvertex4);
							RegionAbove(activeRegion).dirty = activeRegion.dirty = activeRegion2.dirty = true;
							return false;
						}
					}
				} else {
					CheckForRightSplice(gLUtessellatorImpl, activeRegion);
					return false;
				}
			}
		}
	}

	static void WalkDirtyRegions(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion) {
		ActiveRegion activeRegion2 = RegionBelow(activeRegion);
		while (true) {
			while (activeRegion2.dirty) {
				activeRegion = activeRegion2;
				activeRegion2 = RegionBelow(activeRegion2);
			}

			if (!activeRegion.dirty) {
				activeRegion2 = activeRegion;
				activeRegion = RegionAbove(activeRegion);
				if (activeRegion == null || !activeRegion.dirty) {
					return;
				}
			}

			activeRegion.dirty = false;
			GLUhalfEdge gLUhalfEdge = activeRegion.eUp;
			GLUhalfEdge gLUhalfEdge2 = activeRegion2.eUp;
			if (gLUhalfEdge.Sym.Org != gLUhalfEdge2.Sym.Org && CheckForLeftSplice(gLUtessellatorImpl, activeRegion)) {
				if (activeRegion2.fixUpperEdge) {
					DeleteRegion(gLUtessellatorImpl, activeRegion2);
					if (!Mesh.__gl_meshDelete(gLUhalfEdge2)) {
						throw new RuntimeException();
					}

					activeRegion2 = RegionBelow(activeRegion);
					gLUhalfEdge2 = activeRegion2.eUp;
				} else if (activeRegion.fixUpperEdge) {
					DeleteRegion(gLUtessellatorImpl, activeRegion);
					if (!Mesh.__gl_meshDelete(gLUhalfEdge)) {
						throw new RuntimeException();
					}

					activeRegion = RegionAbove(activeRegion2);
					gLUhalfEdge = activeRegion.eUp;
				}
			}

			if (gLUhalfEdge.Org != gLUhalfEdge2.Org) {
				if (gLUhalfEdge.Sym.Org == gLUhalfEdge2.Sym.Org || activeRegion.fixUpperEdge || activeRegion2.fixUpperEdge || gLUhalfEdge.Sym.Org != gLUtessellatorImpl.event && gLUhalfEdge2.Sym.Org != gLUtessellatorImpl.event) {
					CheckForRightSplice(gLUtessellatorImpl, activeRegion);
				} else if (CheckForIntersect(gLUtessellatorImpl, activeRegion)) {
					return;
				}
			}

			if (gLUhalfEdge.Org == gLUhalfEdge2.Org && gLUhalfEdge.Sym.Org == gLUhalfEdge2.Sym.Org) {
				AddWinding(gLUhalfEdge2, gLUhalfEdge);
				DeleteRegion(gLUtessellatorImpl, activeRegion);
				if (!Mesh.__gl_meshDelete(gLUhalfEdge)) {
					throw new RuntimeException();
				}

				activeRegion = RegionAbove(activeRegion2);
			}
		}
	}

	static void ConnectRightVertex(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion, GLUhalfEdge gLUhalfEdge) {
		GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge.Onext;
		ActiveRegion activeRegion2 = RegionBelow(activeRegion);
		GLUhalfEdge gLUhalfEdge3 = activeRegion.eUp;
		GLUhalfEdge gLUhalfEdge4 = activeRegion2.eUp;
		boolean boolean1 = false;
		if (gLUhalfEdge3.Sym.Org != gLUhalfEdge4.Sym.Org) {
			CheckForIntersect(gLUtessellatorImpl, activeRegion);
		}

		if (Geom.VertEq(gLUhalfEdge3.Org, gLUtessellatorImpl.event)) {
			if (!Mesh.__gl_meshSplice(gLUhalfEdge2.Sym.Lnext, gLUhalfEdge3)) {
				throw new RuntimeException();
			}

			activeRegion = TopLeftRegion(activeRegion);
			if (activeRegion == null) {
				throw new RuntimeException();
			}

			gLUhalfEdge2 = RegionBelow(activeRegion).eUp;
			FinishLeftRegions(gLUtessellatorImpl, RegionBelow(activeRegion), activeRegion2);
			boolean1 = true;
		}

		if (Geom.VertEq(gLUhalfEdge4.Org, gLUtessellatorImpl.event)) {
			if (!Mesh.__gl_meshSplice(gLUhalfEdge, gLUhalfEdge4.Sym.Lnext)) {
				throw new RuntimeException();
			}

			gLUhalfEdge = FinishLeftRegions(gLUtessellatorImpl, activeRegion2, (ActiveRegion)null);
			boolean1 = true;
		}

		if (boolean1) {
			AddRightEdges(gLUtessellatorImpl, activeRegion, gLUhalfEdge.Onext, gLUhalfEdge2, gLUhalfEdge2, true);
		} else {
			GLUhalfEdge gLUhalfEdge5;
			if (Geom.VertLeq(gLUhalfEdge4.Org, gLUhalfEdge3.Org)) {
				gLUhalfEdge5 = gLUhalfEdge4.Sym.Lnext;
			} else {
				gLUhalfEdge5 = gLUhalfEdge3;
			}

			gLUhalfEdge5 = Mesh.__gl_meshConnect(gLUhalfEdge.Onext.Sym, gLUhalfEdge5);
			if (gLUhalfEdge5 == null) {
				throw new RuntimeException();
			} else {
				AddRightEdges(gLUtessellatorImpl, activeRegion, gLUhalfEdge5, gLUhalfEdge5.Onext, gLUhalfEdge5.Onext, false);
				gLUhalfEdge5.Sym.activeRegion.fixUpperEdge = true;
				WalkDirtyRegions(gLUtessellatorImpl, activeRegion);
			}
		}
	}

	static void ConnectLeftDegenerate(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion, GLUvertex gLUvertex) {
		GLUhalfEdge gLUhalfEdge = activeRegion.eUp;
		if (Geom.VertEq(gLUhalfEdge.Org, gLUvertex)) {
			assert false;
			SpliceMergeVertices(gLUtessellatorImpl, gLUhalfEdge, gLUvertex.anEdge);
		} else if (!Geom.VertEq(gLUhalfEdge.Sym.Org, gLUvertex)) {
			if (Mesh.__gl_meshSplitEdge(gLUhalfEdge.Sym) == null) {
				throw new RuntimeException();
			} else {
				if (activeRegion.fixUpperEdge) {
					if (!Mesh.__gl_meshDelete(gLUhalfEdge.Onext)) {
						throw new RuntimeException();
					}

					activeRegion.fixUpperEdge = false;
				}

				if (!Mesh.__gl_meshSplice(gLUvertex.anEdge, gLUhalfEdge)) {
					throw new RuntimeException();
				} else {
					SweepEvent(gLUtessellatorImpl, gLUvertex);
				}
			}
		} else {
			assert false;
			activeRegion = TopRightRegion(activeRegion);
			ActiveRegion activeRegion2 = RegionBelow(activeRegion);
			GLUhalfEdge gLUhalfEdge2 = activeRegion2.eUp.Sym;
			GLUhalfEdge gLUhalfEdge3;
			GLUhalfEdge gLUhalfEdge4 = gLUhalfEdge3 = gLUhalfEdge2.Onext;
			if (activeRegion2.fixUpperEdge) {
				assert gLUhalfEdge4 != gLUhalfEdge2;
				DeleteRegion(gLUtessellatorImpl, activeRegion2);
				if (!Mesh.__gl_meshDelete(gLUhalfEdge2)) {
					throw new RuntimeException();
				}

				gLUhalfEdge2 = gLUhalfEdge4.Sym.Lnext;
			}

			if (!Mesh.__gl_meshSplice(gLUvertex.anEdge, gLUhalfEdge2)) {
				throw new RuntimeException();
			} else {
				if (!Geom.EdgeGoesLeft(gLUhalfEdge4)) {
					gLUhalfEdge4 = null;
				}

				AddRightEdges(gLUtessellatorImpl, activeRegion, gLUhalfEdge2.Onext, gLUhalfEdge3, gLUhalfEdge4, true);
			}
		}
	}

	static void ConnectLeftVertex(GLUtessellatorImpl gLUtessellatorImpl, GLUvertex gLUvertex) {
		ActiveRegion activeRegion = new ActiveRegion();
		activeRegion.eUp = gLUvertex.anEdge.Sym;
		ActiveRegion activeRegion2 = (ActiveRegion)Dict.dictKey(Dict.dictSearch(gLUtessellatorImpl.dict, activeRegion));
		ActiveRegion activeRegion3 = RegionBelow(activeRegion2);
		GLUhalfEdge gLUhalfEdge = activeRegion2.eUp;
		GLUhalfEdge gLUhalfEdge2 = activeRegion3.eUp;
		if (Geom.EdgeSign(gLUhalfEdge.Sym.Org, gLUvertex, gLUhalfEdge.Org) == 0.0) {
			ConnectLeftDegenerate(gLUtessellatorImpl, activeRegion2, gLUvertex);
		} else {
			ActiveRegion activeRegion4 = Geom.VertLeq(gLUhalfEdge2.Sym.Org, gLUhalfEdge.Sym.Org) ? activeRegion2 : activeRegion3;
			if (!activeRegion2.inside && !activeRegion4.fixUpperEdge) {
				AddRightEdges(gLUtessellatorImpl, activeRegion2, gLUvertex.anEdge, gLUvertex.anEdge, (GLUhalfEdge)null, true);
			} else {
				GLUhalfEdge gLUhalfEdge3;
				if (activeRegion4 == activeRegion2) {
					gLUhalfEdge3 = Mesh.__gl_meshConnect(gLUvertex.anEdge.Sym, gLUhalfEdge.Lnext);
					if (gLUhalfEdge3 == null) {
						throw new RuntimeException();
					}
				} else {
					GLUhalfEdge gLUhalfEdge4 = Mesh.__gl_meshConnect(gLUhalfEdge2.Sym.Onext.Sym, gLUvertex.anEdge);
					if (gLUhalfEdge4 == null) {
						throw new RuntimeException();
					}

					gLUhalfEdge3 = gLUhalfEdge4.Sym;
				}

				if (activeRegion4.fixUpperEdge) {
					if (!FixUpperEdge(activeRegion4, gLUhalfEdge3)) {
						throw new RuntimeException();
					}
				} else {
					ComputeWinding(gLUtessellatorImpl, AddRegionBelow(gLUtessellatorImpl, activeRegion2, gLUhalfEdge3));
				}

				SweepEvent(gLUtessellatorImpl, gLUvertex);
			}
		}
	}

	static void SweepEvent(GLUtessellatorImpl gLUtessellatorImpl, GLUvertex gLUvertex) {
		gLUtessellatorImpl.event = gLUvertex;
		DebugEvent(gLUtessellatorImpl);
		GLUhalfEdge gLUhalfEdge = gLUvertex.anEdge;
		do {
			if (gLUhalfEdge.activeRegion != null) {
				ActiveRegion activeRegion = TopLeftRegion(gLUhalfEdge.activeRegion);
				if (activeRegion == null) {
					throw new RuntimeException();
				}

				ActiveRegion activeRegion2 = RegionBelow(activeRegion);
				GLUhalfEdge gLUhalfEdge2 = activeRegion2.eUp;
				GLUhalfEdge gLUhalfEdge3 = FinishLeftRegions(gLUtessellatorImpl, activeRegion2, (ActiveRegion)null);
				if (gLUhalfEdge3.Onext == gLUhalfEdge2) {
					ConnectRightVertex(gLUtessellatorImpl, activeRegion, gLUhalfEdge3);
				} else {
					AddRightEdges(gLUtessellatorImpl, activeRegion, gLUhalfEdge3.Onext, gLUhalfEdge2, gLUhalfEdge2, true);
				}

				return;
			}

			gLUhalfEdge = gLUhalfEdge.Onext;
		} while (gLUhalfEdge != gLUvertex.anEdge);

		ConnectLeftVertex(gLUtessellatorImpl, gLUvertex);
	}

	static void AddSentinel(GLUtessellatorImpl gLUtessellatorImpl, double double1) {
		ActiveRegion activeRegion = new ActiveRegion();
		GLUhalfEdge gLUhalfEdge = Mesh.__gl_meshMakeEdge(gLUtessellatorImpl.mesh);
		if (gLUhalfEdge == null) {
			throw new RuntimeException();
		} else {
			gLUhalfEdge.Org.s = 4.0E150;
			gLUhalfEdge.Org.t = double1;
			gLUhalfEdge.Sym.Org.s = -4.0E150;
			gLUhalfEdge.Sym.Org.t = double1;
			gLUtessellatorImpl.event = gLUhalfEdge.Sym.Org;
			activeRegion.eUp = gLUhalfEdge;
			activeRegion.windingNumber = 0;
			activeRegion.inside = false;
			activeRegion.fixUpperEdge = false;
			activeRegion.sentinel = true;
			activeRegion.dirty = false;
			activeRegion.nodeUp = Dict.dictInsert(gLUtessellatorImpl.dict, activeRegion);
			if (activeRegion.nodeUp == null) {
				throw new RuntimeException();
			}
		}
	}

	static void InitEdgeDict(GLUtessellatorImpl gLUtessellatorImpl) {
		gLUtessellatorImpl.dict = Dict.dictNewDict(gLUtessellatorImpl, new Dict.DictLeq(){
			
			public boolean leq(Object var1, Object var2, Object var3) {
				return Sweep.EdgeLeq(gLUtessellatorImpl, (ActiveRegion)var2, (ActiveRegion)var3);
			}
		});
		if (gLUtessellatorImpl.dict == null) {
			throw new RuntimeException();
		} else {
			AddSentinel(gLUtessellatorImpl, -4.0E150);
			AddSentinel(gLUtessellatorImpl, 4.0E150);
		}
	}

	static void DoneEdgeDict(GLUtessellatorImpl gLUtessellatorImpl) {
		int int1 = 0;
		ActiveRegion activeRegion;
		while ((activeRegion = (ActiveRegion)Dict.dictKey(Dict.dictMin(gLUtessellatorImpl.dict))) != null) {
			if (!activeRegion.sentinel) {
				assert activeRegion.fixUpperEdge;
				if (!$assertionsDisabled) {
					++int1;
					if (int1 != 1) {
						throw new AssertionError();
					}
				}
			}

			assert activeRegion.windingNumber == 0;
			DeleteRegion(gLUtessellatorImpl, activeRegion);
		}

		Dict.dictDeleteDict(gLUtessellatorImpl.dict);
	}

	static void RemoveDegenerateEdges(GLUtessellatorImpl gLUtessellatorImpl) {
		GLUhalfEdge gLUhalfEdge = gLUtessellatorImpl.mesh.eHead;
		GLUhalfEdge gLUhalfEdge2;
		for (GLUhalfEdge gLUhalfEdge3 = gLUhalfEdge.next; gLUhalfEdge3 != gLUhalfEdge; gLUhalfEdge3 = gLUhalfEdge2) {
			gLUhalfEdge2 = gLUhalfEdge3.next;
			GLUhalfEdge gLUhalfEdge4 = gLUhalfEdge3.Lnext;
			if (Geom.VertEq(gLUhalfEdge3.Org, gLUhalfEdge3.Sym.Org) && gLUhalfEdge3.Lnext.Lnext != gLUhalfEdge3) {
				SpliceMergeVertices(gLUtessellatorImpl, gLUhalfEdge4, gLUhalfEdge3);
				if (!Mesh.__gl_meshDelete(gLUhalfEdge3)) {
					throw new RuntimeException();
				}

				gLUhalfEdge3 = gLUhalfEdge4;
				gLUhalfEdge4 = gLUhalfEdge4.Lnext;
			}

			if (gLUhalfEdge4.Lnext == gLUhalfEdge3) {
				if (gLUhalfEdge4 != gLUhalfEdge3) {
					if (gLUhalfEdge4 == gLUhalfEdge2 || gLUhalfEdge4 == gLUhalfEdge2.Sym) {
						gLUhalfEdge2 = gLUhalfEdge2.next;
					}

					if (!Mesh.__gl_meshDelete(gLUhalfEdge4)) {
						throw new RuntimeException();
					}
				}

				if (gLUhalfEdge3 == gLUhalfEdge2 || gLUhalfEdge3 == gLUhalfEdge2.Sym) {
					gLUhalfEdge2 = gLUhalfEdge2.next;
				}

				if (!Mesh.__gl_meshDelete(gLUhalfEdge3)) {
					throw new RuntimeException();
				}
			}
		}
	}

	static boolean InitPriorityQ(GLUtessellatorImpl gLUtessellatorImpl) {
		PriorityQ priorityQ = gLUtessellatorImpl.pq = PriorityQ.pqNewPriorityQ(new PriorityQ.Leq(){
    
    public boolean leq(Object priorityQ, Object gLUvertex2) {
        return Geom.VertLeq((GLUvertex)priorityQ, (GLUvertex)gLUvertex2);
    }
});
		if (priorityQ == null) {
			return false;
		} else {
			GLUvertex gLUvertex = gLUtessellatorImpl.mesh.vHead;
			GLUvertex gLUvertex2;
			for (gLUvertex2 = gLUvertex.next; gLUvertex2 != gLUvertex; gLUvertex2 = gLUvertex2.next) {
				gLUvertex2.pqHandle = priorityQ.pqInsert(gLUvertex2);
				if ((long)gLUvertex2.pqHandle == Long.MAX_VALUE) {
					break;
				}
			}

			if (gLUvertex2 == gLUvertex && priorityQ.pqInit()) {
				return true;
			} else {
				gLUtessellatorImpl.pq.pqDeletePriorityQ();
				gLUtessellatorImpl.pq = null;
				return false;
			}
		}
	}

	static void DonePriorityQ(GLUtessellatorImpl gLUtessellatorImpl) {
		gLUtessellatorImpl.pq.pqDeletePriorityQ();
	}

	static boolean RemoveDegenerateFaces(GLUmesh gLUmesh) {
		GLUface gLUface;
		for (GLUface gLUface2 = gLUmesh.fHead.next; gLUface2 != gLUmesh.fHead; gLUface2 = gLUface) {
			gLUface = gLUface2.next;
			GLUhalfEdge gLUhalfEdge = gLUface2.anEdge;
			assert gLUhalfEdge.Lnext != gLUhalfEdge;
			if (gLUhalfEdge.Lnext.Lnext == gLUhalfEdge) {
				AddWinding(gLUhalfEdge.Onext, gLUhalfEdge);
				if (!Mesh.__gl_meshDelete(gLUhalfEdge)) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean __gl_computeInterior(GLUtessellatorImpl gLUtessellatorImpl) {
		gLUtessellatorImpl.fatalError = false;
		RemoveDegenerateEdges(gLUtessellatorImpl);
		if (!InitPriorityQ(gLUtessellatorImpl)) {
			return false;
		} else {
			InitEdgeDict(gLUtessellatorImpl);
			GLUvertex gLUvertex;
			for (; (gLUvertex = (GLUvertex)gLUtessellatorImpl.pq.pqExtractMin()) != null; SweepEvent(gLUtessellatorImpl, gLUvertex)) {
				while (true) {
					GLUvertex gLUvertex2 = (GLUvertex)gLUtessellatorImpl.pq.pqMinimum();
					if (gLUvertex2 == null || !Geom.VertEq(gLUvertex2, gLUvertex)) {
						break;
					}

					gLUvertex2 = (GLUvertex)gLUtessellatorImpl.pq.pqExtractMin();
					SpliceMergeVertices(gLUtessellatorImpl, gLUvertex.anEdge, gLUvertex2.anEdge);
				}
			}

			gLUtessellatorImpl.event = ((ActiveRegion)Dict.dictKey(Dict.dictMin(gLUtessellatorImpl.dict))).eUp.Org;
			DebugEvent(gLUtessellatorImpl);
			DoneEdgeDict(gLUtessellatorImpl);
			DonePriorityQ(gLUtessellatorImpl);
			if (!RemoveDegenerateFaces(gLUtessellatorImpl.mesh)) {
				return false;
			} else {
				Mesh.__gl_meshCheckMesh(gLUtessellatorImpl.mesh);
				return true;
			}
		}
	}
}
