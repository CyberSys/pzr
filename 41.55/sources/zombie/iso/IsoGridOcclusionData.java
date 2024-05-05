package zombie.iso;

import gnu.trove.set.hash.THashSet;
import java.util.ArrayList;
import zombie.iso.areas.IsoBuilding;


public class IsoGridOcclusionData {
	public static final int MAXBUILDINGOCCLUDERS = 3;
	private static final THashSet _leftBuildings = new THashSet(3);
	private static final THashSet _rightBuildings = new THashSet(3);
	private static final THashSet _allBuildings = new THashSet(3);
	private static int _ObjectEpoch = 0;
	private final ArrayList _leftBuildingsArray = new ArrayList(3);
	private final ArrayList _rightBuildingsArray = new ArrayList(3);
	private final ArrayList _allBuildingsArray = new ArrayList(3);
	private IsoGridSquare _ownerSquare = null;
	private boolean _bSoftInitialized = false;
	private boolean _bLeftOccludedByOrphanStructures = false;
	private boolean _bRightOccludedByOrphanStructures = false;
	private int _objectEpoch = -1;

	public IsoGridOcclusionData(IsoGridSquare square) {
		this._ownerSquare = square;
	}

	public static void SquareChanged() {
		++_ObjectEpoch;
		if (_ObjectEpoch < 0) {
			_ObjectEpoch = 0;
		}
	}

	public void Reset() {
		this._bSoftInitialized = false;
		this._bLeftOccludedByOrphanStructures = false;
		this._bRightOccludedByOrphanStructures = false;
		this._allBuildingsArray.clear();
		this._leftBuildingsArray.clear();
		this._rightBuildingsArray.clear();
		this._objectEpoch = -1;
	}

	public boolean getCouldBeOccludedByOrphanStructures(IsoGridOcclusionData.OcclusionFilter occlusionFilter) {
		if (this._objectEpoch != _ObjectEpoch) {
			if (this._bSoftInitialized) {
				this.Reset();
			}

			this._objectEpoch = _ObjectEpoch;
		}

		if (!this._bSoftInitialized) {
			this.LazyInitializeSoftOccluders();
		}

		if (occlusionFilter == IsoGridOcclusionData.OcclusionFilter.Left) {
			return this._bLeftOccludedByOrphanStructures;
		} else if (occlusionFilter == IsoGridOcclusionData.OcclusionFilter.Right) {
			return this._bRightOccludedByOrphanStructures;
		} else {
			return this._bLeftOccludedByOrphanStructures || this._bRightOccludedByOrphanStructures;
		}
	}

	public ArrayList getBuildingsCouldBeOccluders(IsoGridOcclusionData.OcclusionFilter occlusionFilter) {
		if (this._objectEpoch != _ObjectEpoch) {
			if (this._bSoftInitialized) {
				this.Reset();
			}

			this._objectEpoch = _ObjectEpoch;
		}

		if (!this._bSoftInitialized) {
			this.LazyInitializeSoftOccluders();
		}

		if (occlusionFilter == IsoGridOcclusionData.OcclusionFilter.Left) {
			return this._leftBuildingsArray;
		} else {
			return occlusionFilter == IsoGridOcclusionData.OcclusionFilter.Right ? this._rightBuildingsArray : this._allBuildingsArray;
		}
	}

	private void LazyInitializeSoftOccluders() {
		boolean boolean1 = false;
		int int1 = this._ownerSquare.getX();
		int int2 = this._ownerSquare.getY();
		int int3 = this._ownerSquare.getZ();
		_allBuildings.clear();
		_leftBuildings.clear();
		_rightBuildings.clear();
		boolean1 |= this.GetBuildingFloorsProjectedOnSquare(_allBuildings, int1, int2, int3);
		boolean1 |= this.GetBuildingFloorsProjectedOnSquare(_allBuildings, int1 + 1, int2 + 1, int3);
		boolean1 |= this.GetBuildingFloorsProjectedOnSquare(_allBuildings, int1 + 2, int2 + 2, int3);
		boolean1 |= this.GetBuildingFloorsProjectedOnSquare(_allBuildings, int1 + 3, int2 + 3, int3);
		this._bLeftOccludedByOrphanStructures |= this.GetBuildingFloorsProjectedOnSquare(_leftBuildings, int1, int2 + 1, int3);
		this._bLeftOccludedByOrphanStructures |= this.GetBuildingFloorsProjectedOnSquare(_leftBuildings, int1 + 1, int2 + 2, int3);
		this._bLeftOccludedByOrphanStructures |= this.GetBuildingFloorsProjectedOnSquare(_leftBuildings, int1 + 2, int2 + 3, int3);
		this._bRightOccludedByOrphanStructures |= this.GetBuildingFloorsProjectedOnSquare(_rightBuildings, int1 + 1, int2, int3);
		this._bRightOccludedByOrphanStructures |= this.GetBuildingFloorsProjectedOnSquare(_rightBuildings, int1 + 2, int2 + 1, int3);
		this._bRightOccludedByOrphanStructures |= this.GetBuildingFloorsProjectedOnSquare(_rightBuildings, int1 + 3, int2 + 2, int3);
		this._bLeftOccludedByOrphanStructures |= boolean1;
		_leftBuildings.addAll(_allBuildings);
		this._bRightOccludedByOrphanStructures |= boolean1;
		_rightBuildings.addAll(_allBuildings);
		_allBuildings.clear();
		_allBuildings.addAll(_leftBuildings);
		_allBuildings.addAll(_rightBuildings);
		this._leftBuildingsArray.addAll(_leftBuildings);
		this._rightBuildingsArray.addAll(_rightBuildings);
		this._allBuildingsArray.addAll(_allBuildings);
		this._bSoftInitialized = true;
	}

	private boolean GetBuildingFloorsProjectedOnSquare(THashSet tHashSet, int int1, int int2, int int3) {
		boolean boolean1 = false;
		int int4 = int1;
		int int5 = int2;
		for (int int6 = int3; int6 < IsoCell.MaxHeight; int5 += 3) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int6);
			if (square != null) {
				IsoBuilding building = square.getBuilding();
				if (building == null) {
					building = square.roofHideBuilding;
				}

				if (building != null) {
					tHashSet.add(building);
				}

				for (int int7 = int6 - 1; int7 >= 0 && building == null; --int7) {
					IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int7);
					if (square2 != null) {
						building = square2.getBuilding();
						if (building == null) {
							building = square2.roofHideBuilding;
						}

						if (building != null) {
							tHashSet.add(building);
						}
					}
				}

				if (building == null && !boolean1 && square.getZ() != 0 && square.getPlayerBuiltFloor() != null) {
					boolean1 = true;
				}
			}

			++int6;
			int4 += 3;
		}

		return boolean1;
	}

	public static enum OcclusionFilter {

		Left,
		Right,
		All;

		private static IsoGridOcclusionData.OcclusionFilter[] $values() {
			return new IsoGridOcclusionData.OcclusionFilter[]{Left, Right, All};
		}
	}
	public static enum OccluderType {

		Unknown,
		NotFull,
		Full;

		private static IsoGridOcclusionData.OccluderType[] $values() {
			return new IsoGridOcclusionData.OccluderType[]{Unknown, NotFull, Full};
		}
	}
}
