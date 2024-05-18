package zombie.core;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class Clipboard {
   public static String getClipboard() {
      Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object)null);

      try {
         if (var0 != null && var0.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String var1 = (String)var0.getTransferData(DataFlavor.stringFlavor);
            return var1;
         }
      } catch (UnsupportedFlavorException var2) {
      } catch (IOException var3) {
      }

      return null;
   }

   public static void setClipboard(String var0) {
      StringSelection var1 = new StringSelection(var0);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var1, (ClipboardOwner)null);
   }
}
