package zombie.iso.areas.isoregion.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public final class DataCell {
	public final DataRoot dataRoot;
	protected final Map dataChunks = new HashMap();

	protected DataCell(DataRoot dataRoot, int int1, int int2, int int3) {
		this.dataRoot = dataRoot;
	}

	protected DataRoot getDataRoot() {
		return this.dataRoot;
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
			list.add((DataChunk)entry.getValue());
		}
	}
}
