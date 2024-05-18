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

   protected DataCell(int var1, int var2, int var3) {
      this.hashId = var3;
      this.cellX = var1;
      this.cellY = var2;
   }

   protected DataChunk getChunk(int var1) {
      return (DataChunk)this.dataChunks.get(var1);
   }

   protected DataChunk addChunk(int var1, int var2, int var3) {
      DataChunk var4 = new DataChunk(var1, var2, this, var3);
      this.dataChunks.put(var3, var4);
      return var4;
   }

   protected void setChunk(DataChunk var1) {
      this.dataChunks.put(var1.getHashId(), var1);
   }

   protected void getAllChunks(List var1) {
      Iterator var2 = this.dataChunks.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.add(var3.getValue());
      }

   }
}
