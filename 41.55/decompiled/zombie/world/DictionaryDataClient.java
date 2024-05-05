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

   protected void parseItemLoadList(Map var1) throws WorldDictionaryException {
   }

   protected void parseCurrentItemSet() throws WorldDictionaryException {
      ItemInfo var3;
      for(Iterator var1 = this.itemTypeToInfoMap.entrySet().iterator(); var1.hasNext(); var3.isLoaded = true) {
         Entry var2 = (Entry)var1.next();
         var3 = (ItemInfo)var2.getValue();
         if (!var3.removed && var3.scriptItem == null) {
            var3.scriptItem = ScriptManager.instance.getItem(var3.fullType);
         }

         if (var3.scriptItem == null) {
            throw new WorldDictionaryException("Warning client has no script for item " + var3.fullType);
         }

         var3.scriptItem.setRegistry_id(var3.registryID);
         var3.scriptItem.setModID(var3.modID);
      }

   }

   protected void parseObjectNameLoadList(List var1) throws WorldDictionaryException {
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

   protected void saveToByteBuffer(ByteBuffer var1) throws IOException {
   }
}
