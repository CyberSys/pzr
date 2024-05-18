package zombie.iso;

import java.nio.ByteBuffer;
import zombie.core.utils.ObjectGrid;


public class SliceY {
	IsoCell cell;
	public ObjectGrid Squares;
	int tall;
	int width;
	int y;
	boolean bSaveDirty = true;
	public static ByteBuffer SliceBuffer;
	public static ByteBuffer SliceBuffer2;

	public SliceY(IsoCell cell, int int1, int int2, int int3) {
		this.y = int3;
		this.cell = cell;
		this.width = int1;
		this.tall = IsoCell.getMaxHeight();
		this.Squares = new ObjectGrid(int1, int2);
	}
}
