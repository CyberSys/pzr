package zombie.iso.areas.isoregion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class DataCell {
	private int hashId;
	private int cellX;
	private int cellY;
	protected final Map dataChunks = new HashMap();

	protected DataCell(int int1, int int2, int int3) {
		this.hashId = int3;
		this.cellX = int1;
		this.cellY = int2;
	}

	protected DataChunk getChunk(int int1) {
		return (DataChunk)this.dataChunks.get(int1);
	}

	protected DataChunk addChunk(int int1, int int2, int int3) {
		DataChunk dataChunk = new DataChunk(int1, int2, this, int3);
		this.dataChunks.put(int3, dataChunk);
		return dataChunk;
	}

	protected void setChunk(DataChunk dataChunk) {
		this.dataChunks.put(dataChunk.getHashId(), dataChunk);
	}

	protected void getAllChunks(List list) {
		Iterator iterator = this.dataChunks.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			list.add(entry.getValue());
		}
	}
}
