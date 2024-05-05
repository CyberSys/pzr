package zombie.iso.areas.isoregion;

import java.nio.ByteBuffer;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;


public class ChunkUpdate {

	public static void writeIsoChunkIntoBuffer(IsoChunk chunk, ByteBuffer byteBuffer) {
		if (chunk != null) {
			int int1 = byteBuffer.position();
			byteBuffer.putInt(0);
			byteBuffer.putInt(chunk.maxLevel);
			int int2 = (chunk.maxLevel + 1) * 100;
			byteBuffer.putInt(int2);
			int int3;
			for (int3 = 0; int3 <= chunk.maxLevel; ++int3) {
				for (int int4 = 0; int4 < chunk.squares[0].length; ++int4) {
					IsoGridSquare square = chunk.squares[int3][int4];
					byte byte1 = IsoRegions.calculateSquareFlags(square);
					byteBuffer.put(byte1);
				}
			}

			int3 = byteBuffer.position();
			byteBuffer.position(int1);
			byteBuffer.putInt(int3 - int1);
			byteBuffer.position(int3);
		} else {
			byteBuffer.putInt(-1);
		}
	}
}
