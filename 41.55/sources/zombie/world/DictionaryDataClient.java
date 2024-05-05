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
		ItemInfo itemInfo;
		for (Iterator iterator = this.itemTypeToInfoMap.entrySet().iterator(); iterator.hasNext(); itemInfo.isLoaded = true) {
			Entry entry = (Entry)iterator.next();
			itemInfo = (ItemInfo)entry.getValue();
			if (!itemInfo.removed && itemInfo.scriptItem == null) {
				itemInfo.scriptItem = ScriptManager.instance.getItem(itemInfo.fullType);
			}

			if (itemInfo.scriptItem == null) {
				throw new WorldDictionaryException("Warning client has no script for item " + itemInfo.fullType);
			}

			itemInfo.scriptItem.setRegistry_id(itemInfo.registryID);
			itemInfo.scriptItem.setModID(itemInfo.modID);
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
