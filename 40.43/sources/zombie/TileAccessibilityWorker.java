package zombie;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import zombie.characters.IsoPlayer;
import zombie.core.utils.BooleanGrid;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;


public class TileAccessibilityWorker {
	public static TileAccessibilityWorker instance = new TileAccessibilityWorker();
	public int CurrentWorldXStart = 0;
	public int CurrentWorldYStart = 0;
	public BooleanGrid current = null;
	BooleanGrid working = null;
	public boolean startingNew = true;
	public boolean first = true;
	Queue queuex = new ArrayDeque(512);
	Queue queuey = new ArrayDeque(512);

	public void update() {
		int int1 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
		int int2 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
		if (this.working == null) {
			this.working = new BooleanGrid(int1, int2);
			this.current = new BooleanGrid(int1, int2);
			this.first = true;
		}

		int int3;
		int int4;
		int int5;
		int int6;
		int int7;
		int int8;
		if (this.startingNew) {
			ArrayList arrayList = IsoWorld.instance.getCell().getZoneStack();
			int3 = arrayList.size();
			int4 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldXMinTiles();
			int5 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldYMinTiles();
			int6 = int4 + IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
			int7 = int5 + IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
			int8 = int4 + IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles() / 2;
			int int9 = int5 + IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles() / 2;
			this.queuex.add(int4);
			this.queuey.add(int9);
			this.queuex.add(int6);
			this.queuey.add(int9);
			this.queuex.add(int8);
			this.queuey.add(int5);
			this.queuex.add(int8);
			this.queuey.add(int7);
			this.startingNew = false;
			this.working.clear();
		}

		int int10 = 5000;
		int3 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldXMinTiles();
		int4 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldXMaxTiles();
		int5 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldYMinTiles();
		int6 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldYMaxTiles();
		while (!this.queuex.isEmpty() && (int10 > 0 || this.first)) {
			--int10;
			int7 = (Integer)this.queuex.remove();
			int8 = (Integer)this.queuey.remove();
			IsoChunk chunk = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getChunkForGridSquare(int7, int8);
			if (chunk != null) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int7, int8, 0);
				if (square != null) {
					this.working.setValue(int7 - int3, int8 - int5, true);
					for (int int11 = -1; int11 <= 1; ++int11) {
						for (int int12 = -1; int12 <= 1; ++int12) {
							if ((int11 != 0 || int12 != 0) && (int12 == 0 || int11 == 0)) {
								int int13 = int7 + int11 - int3;
								int int14 = int8 + int12 - int5;
								if (int7 + int11 >= int3 && int7 + int11 < int4 && int8 + int12 >= int5 && int8 + int12 < int6 && !this.working.getValue(int13, int14)) {
									if (!square.testCollideAdjacentAdvanced(int11, int12, 0, false)) {
										this.queuex.add(int7 + int11);
										this.queuey.add(int8 + int12);
										this.working.setValue(int13, int14, true);
									} else {
										IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int7 + int11, int8 + int12, 0);
										if (square2 != null && (square2.getProperties().Is(IsoFlagType.solid) || square2.getProperties().Is(IsoFlagType.solidtrans))) {
											this.working.setValue(int13, int14, true);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (this.queuex.isEmpty()) {
			this.current.copy(this.working);
			this.startingNew = true;
			this.CurrentWorldXStart = int3;
			this.CurrentWorldYStart = int5;
			if (this.first) {
			}

			this.first = false;
		}
	}
}
