package zombie.iso.SpriteDetails;

import java.util.HashMap;



public enum IsoFlagType {

	collideW,
	collideN,
	solidfloor,
	noStart,
	windowW,
	windowN,
	hidewalls,
	exterior,
	NoWallLighting,
	doorW,
	doorN,
	transparentW,
	transparentN,
	WallOverlay,
	FloorOverlay,
	vegitation,
	burning,
	burntOut,
	unflamable,
	cutW,
	cutN,
	tableN,
	tableNW,
	tableW,
	tableSW,
	tableS,
	tableSE,
	tableE,
	tableNE,
	halfheight,
	HasRainSplashes,
	HasRaindrop,
	solid,
	trans,
	pushable,
	solidtrans,
	invisible,
	floorS,
	floorE,
	shelfS,
	shelfE,
	alwaysDraw,
	ontable,
	transparentFloor,
	climbSheetW,
	climbSheetN,
	climbSheetTopN,
	climbSheetTopW,
	attachtostairs,
	sheetCurtains,
	waterPiped,
	HoppableN,
	HoppableW,
	bed,
	blueprint,
	canPathW,
	canPathN,
	blocksight,
	climbSheetE,
	climbSheetS,
	climbSheetTopE,
	climbSheetTopS,
	makeWindowInvincible,
	water,
	canBeCut,
	canBeRemoved,
	taintedWater,
	smoke,
	attachedN,
	attachedS,
	attachedE,
	attachedW,
	attachedFloor,
	attachedSurface,
	attachedCeiling,
	attachedNW,
	ForceAmbient,
	WallSE,
	WindowN,
	WindowW,
	FloorHeightOneThird,
	FloorHeightTwoThirds,
	CantClimb,
	diamondFloor,
	attachedSE,
	TallHoppableW,
	WallWTrans,
	TallHoppableN,
	WallNTrans,
	container,
	DoorWallW,
	DoorWallN,
	WallW,
	WallN,
	WallNW,
	SpearOnlyAttackThrough,
	MAX,
	index,
	EnumConstants,
	fromStringMap;

	static  {
	IsoFlagType[] var0 = values();
	int var1 = var0.length;
	for (int var2 = 0; var2 < var1; ++var2) {
		IsoFlagType var3 = var0[var2];
		if (var3 == MAX) {
			break;
		}

		fromStringMap.put(var3.name(), var3);
	}
	}


	private IsoFlagType(int int1) {
		this.index = int1;
	}
	public int index() {
		return this.index;
	}
	public static IsoFlagType fromIndex(int int1) {
		return EnumConstants[int1];
	}
	public static IsoFlagType FromString(String string) {
		IsoFlagType flagType = (IsoFlagType)fromStringMap.get(string);
		return flagType == null ? MAX : flagType;
	}
	private static IsoFlagType[] $values() {
		return new IsoFlagType[]{collideW, collideN, solidfloor, noStart, windowW, windowN, hidewalls, exterior, NoWallLighting, doorW, doorN, transparentW, transparentN, WallOverlay, FloorOverlay, vegitation, burning, burntOut, unflamable, cutW, cutN, tableN, tableNW, tableW, tableSW, tableS, tableSE, tableE, tableNE, halfheight, HasRainSplashes, HasRaindrop, solid, trans, pushable, solidtrans, invisible, floorS, floorE, shelfS, shelfE, alwaysDraw, ontable, transparentFloor, climbSheetW, climbSheetN, climbSheetTopN, climbSheetTopW, attachtostairs, sheetCurtains, waterPiped, HoppableN, HoppableW, bed, blueprint, canPathW, canPathN, blocksight, climbSheetE, climbSheetS, climbSheetTopE, climbSheetTopS, makeWindowInvincible, water, canBeCut, canBeRemoved, taintedWater, smoke, attachedN, attachedS, attachedE, attachedW, attachedFloor, attachedSurface, attachedCeiling, attachedNW, ForceAmbient, WallSE, WindowN, WindowW, FloorHeightOneThird, FloorHeightTwoThirds, CantClimb, diamondFloor, attachedSE, TallHoppableW, WallWTrans, TallHoppableN, WallNTrans, container, DoorWallW, DoorWallN, WallW, WallN, WallNW, SpearOnlyAttackThrough, MAX};
	}
}
