package zombie.world;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import zombie.scripting.ScriptManager;


public class DictionaryDataClient extends DictionaryData {

	protected boolean isClient() {
		return true;
	}

	protected void parseItemLoadList(Map map) throws WorldDictionaryException {
	}

	protected void parseCurrentItemSet() throws WorldDictionaryException {
		Iterator iterator = this.itemTypeToInfoMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			ItemInfo itemInfo = (ItemInfo)entry.getValue();
			if (!itemInfo.removed && itemInfo.scriptItem == null) {
				itemInfo.scriptItem = ScriptManager.instance.getSpecificItem(itemInfo.fullType);
			}

			if (itemInfo.scriptItem != null) {
				itemInfo.scriptItem.setRegistry_id(itemInfo.registryID);
				itemInfo.scriptItem.setModID(itemInfo.modID);
				itemInfo.isLoaded = true;
			} else if (!itemInfo.removed) {
				throw new WorldDictionaryException("Warning client has no script for item " + itemInfo.fullType);
			}
		}
	}

	protected void parseObjectNameLoadList(List list) throws WorldDictionaryException {
	}

	protected void backupCurrentDataSet() throws IOException {
	}

	protected void deleteBackupCurrentDataSet() throws IOException {
	}

	protected void createErrorBackups() {
	}

	protected void load() throws IOException, WorldDictionaryException {
	}

	protected void save() throws IOException, WorldDictionaryException {
	}

	protected void saveToByteBuffer(ByteBuffer byteBuffer) throws IOException {
	}
}
